package com.uet.agent_simulation_api.services.simulation;

import com.uet.agent_simulation_api.models.PigDataDaily;
import com.uet.agent_simulation_api.repositories.ExperimentResultRepository;
import com.uet.agent_simulation_api.repositories.PigDataDailyRepository;
import com.uet.agent_simulation_api.repositories.PigpenDailyRepository;
import com.uet.agent_simulation_api.responses.simulation.PigDailyResponse;
import com.uet.agent_simulation_api.responses.simulation.PigDataResponse;
import com.uet.agent_simulation_api.responses.simulation.PigpenDataResponse;
import com.uet.agent_simulation_api.responses.simulation.SimulationStatisticResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationStatisticService implements ISimulationStatisticService{
    private final PigpenDailyRepository pigpenDailyRepository;
    private final PigDataDailyRepository pigDataDailyRepository;
    private final ExperimentResultRepository experimentResultRepository;

    @Override
    public SimulationStatisticResponse getSimulationStatistic(
        String experimentResultIds,
        String pigpenIds,
        String pigIds
    ) {
        if (experimentResultIds == null) {
            return null;
        }
        
        // Check if this is a Tsunami simulation by checking XML output
        final var experimentResultIdList = Arrays.stream(experimentResultIds.split(","))
                .map(Integer::parseInt)
                .toList();
        final var firstResultId = experimentResultIdList.getFirst();
        
        final var simulationRunId = experimentResultRepository.getSimulationRunId(firstResultId);

        // Parse filter params
        Set<Integer> pigpenIdSet = pigpenIds != null ?
                Arrays.stream(pigpenIds.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet()) :
                null;

        Set<Integer> pigIdSet = pigIds != null ?
                Arrays.stream(pigIds.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet()) :
                null;

        // Get all pig data from pig_data_daily
        final var pigDataList = pigDataDailyRepository.findAllByRunId(BigInteger.valueOf(simulationRunId));

        // Filter by pigpen_ids and pig_ids if provided
        var filteredPigDataList = pigDataList.stream()
                .filter(data -> pigpenIdSet == null || pigpenIdSet.contains(data.getPigpenId()))
                .filter(data -> pigIdSet == null || pigIdSet.contains(data.getPigId()))
                .toList();

        // Group by pigpen_id
        Map<Integer, List<PigDataDaily>> pigpenGroups = filteredPigDataList.stream()
                .collect(Collectors.groupingBy(PigDataDaily::getPigpenId));

        List<PigpenDataResponse> pigpenResponses = new ArrayList<>();

        // Process each pigpen
        pigpenGroups.forEach((pigpenId, pigpenData) -> {
            // Group by pig_id within each pigpen
            Map<Integer, List<PigDataDaily>> pigGroups = pigpenData.stream()
                    .collect(Collectors.groupingBy(PigDataDaily::getPigId));

            List<PigDataResponse> pigDataResponses = new ArrayList<>();

            // Process each pig's data
            pigGroups.forEach((pigId, pigData) -> {
                List<PigDailyResponse> dailyResponses = pigData.stream()
                        .map(daily -> new PigDailyResponse(
                                daily.getDay(),
                                daily.getWeight(),
                                daily.getDfi(),
                                daily.getCfi(),
                                daily.getTargetCfi(),
                                daily.getTargetDfi(),
                                daily.getEatCount(),
                                daily.getExcreteCount(),
                                daily.getSeir()
                        ))
                        .sorted(Comparator.comparing(PigDailyResponse::day))
                        .collect(Collectors.toList());

                pigDataResponses.add(new PigDataResponse(pigId, dailyResponses));
            });

            pigpenResponses.add(new PigpenDataResponse(pigpenId, pigDataResponses));
        });

        return new SimulationStatisticResponse(pigpenResponses);
    }
    
    @Override
    public Object getTsunamiStatisticsFromXml(String experimentResultIds) {
        log.info("🌊 Getting Tsunami statistics from XML for IDs: {}", experimentResultIds);
        
        try {
            final var experimentResultIdList = Arrays.stream(experimentResultIds.split(","))
                    .map(Integer::parseInt)
                    .toList();
            Integer firstResultId = experimentResultIdList.get(0);
            
            // Get experiment result details to find XML file location
            var experimentResult = experimentResultRepository.findById(BigInteger.valueOf(firstResultId));
            if (experimentResult.isEmpty()) {
                log.warn("🚫 Experiment result not found for ID: {}", firstResultId);
                return null;
            }
            
            // Get experiment ID (not result ID) for XML filename
            BigInteger experimentId = experimentResult.get().getExperimentId();
            log.info("🔍 Result ID: {}, Experiment ID: {}", firstResultId, experimentId);
            
            String location = experimentResult.get().getLocation();
            log.info("📂 Experiment result location: {}", location);
            
            if (location == null) {
                log.warn("🚫 No location found for experiment result: {}", firstResultId);
                return null;
            }
            
            // Multiple possible XML file paths to try (use experiment ID, not result ID)
            String[] possibleXmlFiles = {
                "simulation-outputs" + experimentId + ".xml", // Use experiment ID (229)
                "simulation-outputs" + firstResultId + ".xml", // Fallback: use result ID
            };
            
            File xmlFile = null;
            
            // Try each possible XML file location
            for (String xmlFileName : possibleXmlFiles) {
                File candidateFile = new File(location, xmlFileName);
                log.info("🔍 Trying XML file: {}", candidateFile.getAbsolutePath());
                
                if (candidateFile.exists()) {
                    xmlFile = candidateFile;
                    log.info("✅ Found XML file: {}", xmlFile.getAbsolutePath());
                    break;
                }
            }
            
            // If not found in location, try xmls directory
            if (xmlFile == null || !xmlFile.exists()) {
                log.info("🔍 Trying xmls directory fallback...");
                String xmlsPath = "/app/storage/xmls";
                File xmlsDir = new File(xmlsPath);
                
                if (xmlsDir.exists()) {
                    String xmlsFileName = "node-1_user-1_project-19_model-340_experiment-229_result-" + firstResultId + "_Tsunami-tsunami_simulation.xml";
                    File xmlsFile = new File(xmlsDir, xmlsFileName);
                    log.info("🔍 Trying xmls file: {}", xmlsFile.getAbsolutePath());
                    
                    if (xmlsFile.exists()) {
                        // Read XML content from xmls file but look for simulation output in location
                        xmlFile = xmlsFile;
                        log.info("✅ Found XML file in xmls: {}", xmlFile.getAbsolutePath());
                        
                        // But we need the actual simulation output XML, not the config XML
                        String outputXmlFileName = "simulation-outputs" + firstResultId + ".xml";
                        File outputXmlFile = new File(location, outputXmlFileName);
                        if (outputXmlFile.exists()) {
                            xmlFile = outputXmlFile;
                            log.info("✅ Using simulation output XML: {}", xmlFile.getAbsolutePath());
                        }
                    }
                }
            }
            
            if (xmlFile == null || !xmlFile.exists()) {
                log.warn("🚫 No XML file found for result ID: {}", firstResultId);
                return null;
            }
            
            // Parse XML and extract Tsunami data
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            
            // Create a simplified data structure for frontend consumption
            Map<String, Object> tsunamiData = new java.util.HashMap<>();
            List<Map<String, Object>> steps = new ArrayList<>();
            
            NodeList stepNodes = doc.getElementsByTagName("Step");
            log.info("📊 Found {} steps in XML", stepNodes.getLength());
            
            for (int i = 0; i < stepNodes.getLength(); i++) {
                Node stepNode = stepNodes.item(i);
                if (stepNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element stepElement = (Element) stepNode;
                    String stepId = stepElement.getAttribute("id");
                    
                    Map<String, Object> stepData = new java.util.HashMap<>();
                    stepData.put("id", stepId);
                    
                    Map<String, Object> variables = new java.util.HashMap<>();
                    
                    NodeList variableNodes = stepElement.getElementsByTagName("Variable");
                    for (int j = 0; j < variableNodes.getLength(); j++) {
                        Node varNode = variableNodes.item(j);
                        if (varNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element varElement = (Element) varNode;
                            String name = varElement.getAttribute("name");
                            String type = varElement.getAttribute("type");
                            String value = varElement.getTextContent();
                            
                            Map<String, String> variableData = new java.util.HashMap<>();
                            variableData.put("name", name);
                            variableData.put("type", type);
                            variableData.put("value", value);
                            
                            variables.put(name, variableData);
                        }
                    }
                    
                    stepData.put("variables", variables);
                    steps.add(stepData);
                }
            }
            
            tsunamiData.put("steps", steps);
            
            log.info("✅ Successfully parsed Tsunami XML data with {} steps", steps.size());
            return tsunamiData;
            
        } catch (Exception e) {
            log.error("❌ Error getting Tsunami statistics: {}", e.getMessage(), e);
            return null;
        }
    }
}
