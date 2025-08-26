package com.uet.agent_simulation_api.services.project;

import com.uet.agent_simulation_api.requests.project.FinalizeProjectRequest;
import com.uet.agent_simulation_api.requests.project.UploadProjectRequest;
import com.uet.agent_simulation_api.responses.project.ProjectFinalizeResponse;
import com.uet.agent_simulation_api.responses.project.ProjectUploadResponse;

/**
 * Service interface for project upload operations
 */
public interface IProjectUploadService {
    /**
     * Uploads and extracts a project ZIP file to temporary directory
     *
     * @param request Upload request containing project info and ZIP file
     * @return Upload response with temporary directory info and GAML files list
     */
    ProjectUploadResponse uploadProjectZip(UploadProjectRequest request);
    
    /**
     * Finalizes project creation with selected GAML files
     *
     * @param request Finalize request with selected files
     * @return Finalize response with created project info
     */
    ProjectFinalizeResponse finalizeProject(FinalizeProjectRequest request);
    
    /**
     * Cleans up temporary directory
     *
     * @param tempDirId Temporary directory ID to clean up
     */
    void cleanupTempDirectory(String tempDirId);
}