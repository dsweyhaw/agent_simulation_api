package com.uet.agent_simulation_api.requests.simulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RunMultiSimulationRequest {
    private Integer finalStep;
    private Integer numberPigpen;
    private String numberPigs;
    private String initDiseaseAppearPigpenIds;
    private String initDiseaseAppearDays;
}
