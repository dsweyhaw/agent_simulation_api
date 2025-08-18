package com.uet.agent_simulation_api.responses.experiment_result_image;

import org.springframework.http.MediaType;

public record ExperimentResultImageDetailResponse (
    MediaType mediaType,
    byte[] data
) {}
