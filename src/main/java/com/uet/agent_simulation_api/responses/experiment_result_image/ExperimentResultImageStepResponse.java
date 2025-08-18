package com.uet.agent_simulation_api.responses.experiment_result_image;

import java.util.List;

public record ExperimentResultImageStepResponse(
    Integer step,
    List<ExperimentResultImageCategoryResponse> categories
) {}
