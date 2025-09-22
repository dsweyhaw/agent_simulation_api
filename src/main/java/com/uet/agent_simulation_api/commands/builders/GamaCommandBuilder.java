package com.uet.agent_simulation_api.commands.builders;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Service
@Qualifier("gamaCommandBuilder")
@RequiredArgsConstructor
public class GamaCommandBuilder implements IGamaCommandBuilder {
    private final Environment env;

    @Override
    public String build(String pathToShell, Map<String, String> options, List<String> args) {
        final var joiner = new StringJoiner(" ");
        joiner.add(pathToShell);

        if (options != null && !options.isEmpty()) {
            options.forEach((key, value) -> joiner.add(key + " " + value));
        }

        if (args != null && !args.isEmpty()) {
            args.forEach(joiner::add);
        }

        return joiner.toString();
    }

    @Override
    public String createXmlFile(String experimentName, String pathToGamlFile, String pathToOutputXml) {
        return this.build(
                env.getProperty("gama.path.shell"),
                null,
                List.of("-xml", experimentName, pathToGamlFile, pathToOutputXml)
        );
    }

    @Override
    public String buildLegacy(Map<String, String> options, String pathToXmlFile, String pathToOutputDir) {
        pathToOutputDir = pathToOutputDir.endsWith("/") ? pathToOutputDir : pathToOutputDir + "/";

        return this.build(
                env.getProperty("gama.path.shell"),
                options,
                List.of(pathToXmlFile, pathToOutputDir)
        );
    }

    @Override
    public String buildLegacyWithQuality(Map<String, String> options, String pathToXmlFile, String pathToOutputDir) {
        pathToOutputDir = pathToOutputDir.endsWith("/") ? pathToOutputDir : pathToOutputDir + "/";
        
        // Add quality enhancement options for GAMA headless
        Map<String, String> enhancedOptions = new java.util.HashMap<>(options != null ? options : new java.util.HashMap<>());
        
        // Add rendering quality parameters
        if (!enhancedOptions.containsKey("-Djava.awt.headless")) {
            enhancedOptions.put("-Djava.awt.headless", "false"); // Enable AWT for better rendering
        }
        if (!enhancedOptions.containsKey("-Xmx")) {
            enhancedOptions.put("-Xmx", "4g"); // Increase memory for better rendering
        }
        if (!enhancedOptions.containsKey("-Dfile.encoding")) {
            enhancedOptions.put("-Dfile.encoding", "UTF-8"); // Ensure proper encoding
        }
        
        return this.build(
                env.getProperty("gama.path.shell"),
                enhancedOptions,
                List.of(pathToXmlFile, pathToOutputDir)
        );
    }

    @Override
    public String buildBatch(Map<String, String> options, String experimentName, String pathToGamlFile) {
        return this.build(
                env.getProperty("gama.path.shell"),
                options,
                List.of("-batch", experimentName, pathToGamlFile)
        );
    }
}
