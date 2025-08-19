package com.uet.agent_simulation_api.services.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service for validating GAML files using GAMA headless
 */
@Service
@Slf4j
public class GamaValidationService implements IGamaValidationService {
    
    @Value("${gama.path.shell:}")
    private String GAMA_SHELL_PATH;
    
    @Override
    public ValidationResult validateGamlFile(Path gamlFilePath, String experimentName) {
        try {
            // Check if GAMA path is configured
            if (GAMA_SHELL_PATH == null || GAMA_SHELL_PATH.trim().isEmpty()) {
                log.warn("GAMA shell path not configured. Skipping validation.");
                return ValidationResult.success(); // Allow upload without validation if GAMA not configured
            }
            
            log.info("Starting GAMA batch validation for file: {} with experiment: {}", gamlFilePath, experimentName);
            
            // Build GAMA batch validation command
            List<String> command = buildBatchValidationCommand(gamlFilePath, experimentName);
            log.info("Executing GAMA command: {}", String.join(" ", command));
            
            // Execute validation command
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            
            // Read output
            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
            }
            
            // Wait for process to complete with timeout (2 minutes max)
            boolean finished = process.waitFor(2, TimeUnit.MINUTES);
            int exitCode = finished ? process.exitValue() : -1;
            
            // Kill process if it's still running (likely means validation passed and simulation started)
            if (!finished) {
                log.info("GAMA validation timed out after 2 minutes - assuming validation passed, killing process");
                process.destroyForcibly();
                return ValidationResult.success();
            }
            
            String outputStr = output.toString();
            String errorStr = errorOutput.toString();
            
            log.info("GAMA validation completed with exit code: {}", exitCode);
            log.debug("GAMA output: {}", outputStr);
            if (!errorStr.isEmpty()) {
                log.debug("GAMA error output: {}", errorStr);
            }
            
            // Check for compilation errors in output
            if (containsCompilationErrors(outputStr, errorStr)) {
                String errorMessage = extractErrorMessage(outputStr, errorStr);
                log.warn("GAMA compilation failed: {}", errorMessage);
                return ValidationResult.error("GAML file compilation failed", errorMessage);
            } else if (exitCode == 0) {
                log.info("GAMA validation successful for file: {}", gamlFilePath.getFileName());
                return ValidationResult.success();
            } else {
                String errorMessage = extractErrorMessage(outputStr, errorStr);
                log.warn("GAMA validation failed with exit code {}: {}", exitCode, errorMessage);
                return ValidationResult.error("GAML file validation failed", errorMessage);
            }
            
        } catch (IOException | InterruptedException e) {
            log.error("Error during GAMA validation", e);
            return ValidationResult.error("GAML file is not right format", "Failed to execute GAMA validation: " + e.getMessage());
        }
    }
    
    private List<String> buildBatchValidationCommand(Path gamlFilePath, String experimentName) {
        List<String> command = new ArrayList<>();
        command.add(GAMA_SHELL_PATH);
        command.add("-batch");
        command.add(experimentName);
        command.add(gamlFilePath.toString());
        return command;
    }
    
    private boolean containsCompilationErrors(String output, String errorOutput) {
        String combinedOutput = (output + "\n" + errorOutput).toLowerCase();
        // Check for specific GAMA compilation error indicators
        return combinedOutput.contains("gama couldn't compile your input file") ||
               combinedOutput.contains("compilation error") ||
               combinedOutput.contains("syntax error") ||
               combinedOutput.contains("error in you command") ||
               combinedOutput.contains("gaml parsing error") ||
               combinedOutput.contains("exception");
    }
    
    private String extractErrorMessage(String output, String errorOutput) {
        String combinedOutput = output + "\n" + errorOutput;
        
        // Try to extract meaningful error messages
        String[] lines = combinedOutput.split("\n");
        StringBuilder errorMessage = new StringBuilder();
        
        for (String line : lines) {
            if (line.toLowerCase().contains("error") || 
                line.toLowerCase().contains("exception") ||
                line.toLowerCase().contains("failed")) {
                errorMessage.append(line.trim()).append("\n");
            }
        }
        
        if (errorMessage.length() > 0) {
            return errorMessage.toString().trim();
        }
        
        return "Unknown validation error";
    }
}