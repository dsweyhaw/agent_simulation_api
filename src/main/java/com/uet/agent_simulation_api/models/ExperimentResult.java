package com.uet.agent_simulation_api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;

@Table(name = "experiment_results")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ExperimentResult extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private BigInteger id;

    @Column(nullable = false)
    private Integer finalStep;

    @Column(columnDefinition = "VARCHAR(300)")
    private String location;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer status;

    @Column(name = "run_command_pid", columnDefinition = "BIGINT")
    private Long runCommandPid;

    @Column(columnDefinition = "BIGINT")
    private BigInteger number;

    @Column(columnDefinition = "BIGINT", name = "simulation_run_id")
    private BigInteger simulationRunId;

    @Column(name = "experiment_id", nullable = false, insertable = false, updatable = false, columnDefinition = "BIGINT")
    private BigInteger experimentId;

    @Column(name = "node_id", nullable = false, insertable = false, updatable = false)
    private Integer nodeId;

    private Long runTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id", referencedColumnName = "id")
    @JsonIgnore
    private Experiment experiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", referencedColumnName = "id")
    @JsonIgnore
    private Node node;
}
