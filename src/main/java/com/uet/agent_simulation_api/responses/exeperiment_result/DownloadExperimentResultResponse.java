package com.uet.agent_simulation_api.responses.exeperiment_result;

import org.springframework.core.io.InputStreamResource;

public record DownloadExperimentResultResponse(
    InputStreamResource resource,
    String filename,
    Long fileSize
) {}
