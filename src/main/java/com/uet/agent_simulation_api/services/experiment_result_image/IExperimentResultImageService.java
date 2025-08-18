package com.uet.agent_simulation_api.services.experiment_result_image;

import com.uet.agent_simulation_api.models.ExperimentResultImage;
import com.uet.agent_simulation_api.responses.Pagination;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.responses.experiment_result_image.ExperimentResultImageDetailResponse;
import com.uet.agent_simulation_api.responses.experiment_result_image.ExperimentResultImageListResponse;
import reactor.core.publisher.Flux;

import java.math.BigInteger;
import java.util.List;

public interface IExperimentResultImageService {
    /**
     * This method is used to get all experiment result images.
     *
     * @param experimentResultId Experiment result id
     * @param experimentId       Experiment id
     * @param modelId            Model id
     * @param projectId          Project id
     * @param experimentResultCategoryId Experiment result category id
     * @param step               Step
     * @param page               Page
     * @param pageSize           Page size
     * @param orderBy            Order by
     * @param orderDirection     Order direction
     * @return Pagination of ExperimentResultImage
     */
    Pagination<List<ExperimentResultImage>> get(
        BigInteger experimentResultId,
        BigInteger experimentId,
        BigInteger modelId,
        BigInteger projectId,
        BigInteger experimentResultCategoryId,
        Integer step,
        Integer page,
        Integer pageSize,
        String orderBy,
        String orderDirection
    );

    /**
     * This method is used to get an experiment result image data.
     *
     * @param id Experiment result image id
     * @return ExperimentResultImageDetailResponse
     */
    ExperimentResultImageDetailResponse getImageData(BigInteger id);

    /**
     * This method is used to get an experiment result image data encoded.
     *
     * @param id Experiment result image id
     * @return Experiment result image data encoded
     */
    String getImageDataEncoded(BigInteger id);

    ExperimentResultImageListResponse getByRange(BigInteger experimentResultId, Integer startStep, Integer endStep);

    /**
     * This method is used to get animated images.
     *
     * @param experimentResultId Experiment result id
     * @param startStep          Start step
     * @param endStep            End step
     * @param duration           Duration
     * @return Flux of ExperimentResultImageListResponse
     */
    Flux<ExperimentResultImageListResponse> getAnimatedImages(
        BigInteger experimentResultId,
        Integer startStep,
        Integer endStep,
        long duration
    );

    /**
     * This method is used to get animated images.
     *
     * @param experimentResultIds Experiment result ids
     * @param startStep           Start step
     * @param endStep             End step
     * @param duration            Duration
     * @return Flux of ExperimentResultImageListResponse
     */
    Flux<ExperimentResultImageListResponse> getMultiExperimentAnimatedImages(
        String experimentResultIds,
        Integer startStep,
        Integer endStep,
        long duration,
        String categoryIds
    );
}
