package com.uet.agent_simulation_api.responses.experiment_result_image;

import java.math.BigInteger;

public record ExperimentResultImageCategoryResponse(
    BigInteger experimentResultId,
    BigInteger categoryId,
    String encodedImage
) {}
