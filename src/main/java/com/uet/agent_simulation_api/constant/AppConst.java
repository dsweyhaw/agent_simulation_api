package com.uet.agent_simulation_api.constant;

import java.util.List;

/**
 * This class is used to store app constants.
 */
public final class AppConst {
    /**
     * API success status.
     */
    public static final String SUCCESS = "success";

    /**
     * API fail status.
     */
    public static final String FAIL = "fail";

    /**
     * API error status.
     */
    public static final String ERROR = "error";

    /**
     * List of image extensions.
     */
    public static final List<String> IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    public static final String QUERY_TYPE_NATIVE = "native";
    public static final String QUERY_TYPE_JPQL = "jpql";
    public static final List<String> ORDER_DIRECTIONS = List.of("asc", "desc", "ASC", "DESC");

    public static final List<String> EXPERIMENT_RESULT_IMAGE_SORT_FIELDS_JPQL = List.of("id", "experiment_result_id",
            "name", "experiment_id", "model_id", "project_id", "experiment_result_category_id", "step");
    public static final List<String> EXPERIMENT_RESULT_IMAGE_SORT_FIELDS_NATIVE = List.of("id", "experimentResultId",
            "name", "experimentId", "modelId", "projectId", "experimentResultCategoryId", "step");

    public static final int PAGE_MAX_SIZE = 200;
}
