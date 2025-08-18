package com.uet.agent_simulation_api.requests.simulation;

import com.uet.agent_simulation_api.models.Experiment;
import com.uet.agent_simulation_api.models.ExperimentResult;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Create experiment request class.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExperimentRequest {
    @Min(value = 0, message = "Experiment id must be greater than or equal to 1")
    @NotNull
    private BigInteger id;

    private String gamlFile;

    @NotNull(message = "Model id must not be null")
    private BigInteger modelId;

    private Experiment experiment;

    private BigInteger experimentResultNumber;

    private ExperimentResult experimentResult;

    @Min(value = 1, message = "Final step must be greater than or equal to 1")
    private Long finalStep;
}
