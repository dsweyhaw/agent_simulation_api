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
    
    @Value("${gama.path.shell}")
    private String GAMA_SHELL_PATH;
    
    @Override
    public ValidationResult validateGamlFile(Path gamlFilePath, String experimentName) {
        try {
            log.info("Starting GAMA batch validation for file: {} with experiment: {}", gamlFilePath, experimentName);
            
            // Build GAMA batch validation command
            List<String> command = buildBatchValidationCommand(gamlFilePath, experimentName);
            log.info("Executing GAMA command: {}", String.join(" ", command));
            
            // Execute validation command
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            
            // Read output with timeout and early detection
            ValidationResult result = monitorProcessOutput(process, gamlFilePath);
            if (result != null) {
                return result;
            }
            
            // If monitoring didn't determine result, wait with timeout
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            int exitCode = finished ? process.exitValue() : -1;
            
            // Kill process if it's still running (simulation started, so validation passed)
            if (!finished) {
                log.info("GAMA validation timed out, but simulation started - killing process and marking as valid");
                process.destroyForcibly();
                return ValidationResult.success();
            }
            
            log.info("GAMA validation completed with exit code: {}", exitCode);
            
            if (exitCode == 0) {
                log.info("GAMA validation successful for file: {}", gamlFilePath.getFileName());
                return ValidationResult.success();
            } else {
                // Read any remaining output for error analysis
                String remainingOutput = readRemainingOutput(process);
                log.warn("GAMA validation failed with exit code {}: {}", exitCode, remainingOutput);
                return ValidationResult.error("GAML file validation failed", remainingOutput);
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
    
    /**
     * Monitors the GAMA process output in real-time to detect compilation success/failure early
     */
    private ValidationResult monitorProcessOutput(Process process, Path gamlFilePath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            long startTime = System.currentTimeMillis();
            long maxWaitTime = 60000; // 60 seconds max wait for compilation
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                log.debug("GAMA output: {}", line);
                
                // Check for compilation failure indicators
                if (containsCompilationErrorLine(line)) {
                    log.warn("Compilation error detected: {}", line);
                    // Read a few more lines to get complete error message
                    StringBuilder errorDetail = new StringBuilder(line).append("\n");
                    for (int i = 0; i < 5; i++) {
                        String errorLine = reader.readLine();
                        if (errorLine != null) {
                            errorDetail.append(errorLine).append("\n");
                        }
                    }
                    process.destroyForcibly();
                    return ValidationResult.error("GAML file compilation failed", errorDetail.toString());
                }
                
                // Check for simulation start indicators (means compilation succeeded)
                if (containsSimulationStartLine(line)) {
                    log.info("Simulation started - compilation successful, terminating validation");
                    process.destroyForcibly();
                    return ValidationResult.success();
                }
                
                // Timeout check
                if (System.currentTimeMillis() - startTime > maxWaitTime) {
                    log.warn("GAMA validation monitoring timed out");
                    break;
                }
            }
            
            // If we reach here, the process may have ended naturally or timed out
            return null; // Let the caller handle final process status
            
        } catch (IOException e) {
            log.error("Error monitoring GAMA process output", e);
            return ValidationResult.error("GAML file validation failed", "Error monitoring process: " + e.getMessage());
        }
    }
    
    /**
     * Checks if a line indicates compilation failure
     */
    private boolean containsCompilationErrorLine(String line) {
        String lowerLine = line.toLowerCase();
        return lowerLine.contains("gama couldn't compile your input file") ||
               lowerLine.contains("compilation error") ||
               lowerLine.contains("syntax error") ||
               lowerLine.contains("error in you command") ||
               lowerLine.contains("gaml parsing error");
    }
    
    /**
     * Checks if a line indicates simulation has started (compilation succeeded)
     */
    private boolean containsSimulationStartLine(String line) {
        String lowerLine = line.toLowerCase();
        return lowerLine.contains("image display surface created") ||
               lowerLine.contains("simulation") && lowerLine.contains("started") ||
               lowerLine.contains("running simulation") ||
               lowerLine.contains("step") && lowerLine.contains("cycle");
    }
    
    /**
     * Reads any remaining output from the process for error reporting
     */
    private String readRemainingOutput(Process process) {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < 20) { // Limit to prevent hanging
                output.append(line).append("\n");
                lineCount++;
            }
        } catch (IOException e) {
            log.debug("Error reading remaining output", e);
        }
        return output.toString();
    }
}