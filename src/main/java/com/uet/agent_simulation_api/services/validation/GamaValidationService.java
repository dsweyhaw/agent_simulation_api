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

/**
 * Service for validating GAML files using GAMA headless
 */
@Service
@Slf4j
public class GamaValidationService implements IGamaValidationService {
    
    @Value("${gama.path.shell}")
    private String GAMA_SHELL_PATH;
    
    @Override
    public ValidationResult validateGamlFile(Path gamlFilePath) {
        try {
            log.info("Starting GAMA validation for file: {}", gamlFilePath);
            
            // Build GAMA validation command
            List<String> command = buildValidationCommand(gamlFilePath);
            
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
            
            // Wait for process to complete
            int exitCode = process.waitFor();
            
            String outputStr = output.toString();
            String errorStr = errorOutput.toString();
            
            log.info("GAMA validation completed with exit code: {}", exitCode);
            log.debug("GAMA output: {}", outputStr);
            
            if (exitCode == 0 && !containsValidationErrors(outputStr, errorStr)) {
                return ValidationResult.success();
            } else {
                String errorMessage = extractErrorMessage(outputStr, errorStr);
                return ValidationResult.error("GAML file validation failed", errorMessage);
            }
            
        } catch (IOException | InterruptedException e) {
            log.error("Error during GAMA validation", e);
            return ValidationResult.error("GAML file is not right format", "Failed to execute GAMA validation: " + e.getMessage());
        }
    }
    
    private List<String> buildValidationCommand(Path gamlFilePath) {
        List<String> command = new ArrayList<>();
        command.add(GAMA_SHELL_PATH);
        command.add("-validate");
        command.add(gamlFilePath.toString());
        
        log.info("GAMA validation command: {}", String.join(" ", command));
        return command;
    }
    
    private boolean containsValidationErrors(String output, String errorOutput) {
        String combinedOutput = (output + "\n" + errorOutput).toLowerCase();
        return combinedOutput.contains("error") || 
               combinedOutput.contains("exception") || 
               combinedOutput.contains("failed") ||
               combinedOutput.contains("syntax error") ||
               combinedOutput.contains("compilation error");
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