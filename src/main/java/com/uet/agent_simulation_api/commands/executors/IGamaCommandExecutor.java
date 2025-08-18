package com.uet.agent_simulation_api.commands.executors;

import com.uet.agent_simulation_api.models.ExperimentResult;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IGamaCommandExecutor extends ICommandExecutor {
    /**
     * This method is used to execute the legacy mode gama headless.
     *
     * @param createXmlCommand - command to create xml file
     * @param runLegacyCommand - command to run legacy mode
     * @param pathToXmlFile - path to xml file
     * @param experimentId - id of the experiment
     * @param experimentName - name of the experiment
     * @param finalStep - final step of the experiment
     * @return CompletableFuture<Void>
     */
    CompletableFuture<Void> executeLegacy(String createXmlCommand, String runLegacyCommand, String pathToXmlFile,
        BigInteger experimentId, String experimentName, long finalStep);

    /**
     * This method is used to execute the legacy mode gama headless.
     *
     * @param createXmlCommand - command to create xml file
     * @param runLegacyCommand - command to run legacy mode
     * @param pathToXmlFile - path to xml file
     * @param experimentId - id of the experiment
     * @param experimentName - name of the experiment
     * @param finalStep - final step of the experiment
     * @param experimentResult - experiment result
     *
     * @return CompletableFuture<Void>
     */
    CompletableFuture<Void> executeLegacy(String createXmlCommand, String runLegacyCommand, String pathToXmlFile,
        BigInteger experimentId, String experimentName, long finalStep, ExperimentResult experimentResult);

    /**
     * This method is used to execute the legacy mode gama headless.
     *
     * @param createXmlCommand - command to create xml file
     * @param runLegacyCommand - command to run legacy mode
     * @param pathToXmlFile - path to xml file
     * @param experimentId - id of the experiment
     * @param experimentName - name of the experiment
     * @param finalStep - final step of the experiment
     * @param experimentResult - experiment result
     * @param params - params
     *
     * @return CompletableFuture<Void>
     */
    CompletableFuture<Void> executeLegacy(
        String createXmlCommand,
        String runLegacyCommand,
        String pathToXmlFile,
        BigInteger experimentId,
        String experimentName,
        long finalStep,
        ExperimentResult experimentResult,
        Map<String, String> params
    );
}
