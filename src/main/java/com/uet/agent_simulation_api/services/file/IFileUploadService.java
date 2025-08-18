package com.uet.agent_simulation_api.services.file;

import com.uet.agent_simulation_api.requests.model.UploadGamlRequest;
import com.uet.agent_simulation_api.responses.model.UploadGamlResponse;

/**
 * Service interface for file upload operations
 */
public interface IFileUploadService {
    /**
     * Uploads and validates a GAML file
     *
     * @param request Upload request containing file and metadata
     * @return Upload response with validation results
     */
    UploadGamlResponse uploadAndValidateGamlFile(UploadGamlRequest request);
}