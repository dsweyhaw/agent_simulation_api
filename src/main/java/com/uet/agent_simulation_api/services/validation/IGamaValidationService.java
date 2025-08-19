package com.uet.agent_simulation_api.services.validation;

import java.nio.file.Path;

/**
 * Service interface for GAMA validation operations
 */
public interface IGamaValidationService {
    /**
     * Validates a GAML file using GAMA headless batch execution
     *
     * @param gamlFilePath Path to the GAML file to validate
     * @param experimentName Name of the experiment to validate against
     * @return ValidationResult containing validation status and details
     */
    ValidationResult validateGamlFile(Path gamlFilePath, String experimentName);
    
    /**
     * Result of GAMA validation
     */
    record ValidationResult(
        boolean isValid,
        String message,
        String details
    ) {
        public static ValidationResult success() {
            return new ValidationResult(true, "GAML file is valid", "");
        }
        
        public static ValidationResult error(String message, String details) {
            return new ValidationResult(false, message, details);
        }
    }
}