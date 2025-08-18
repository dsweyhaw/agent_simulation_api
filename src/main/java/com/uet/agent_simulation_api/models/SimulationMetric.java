package com.uet.agent_simulation_api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;

@Table(name = "simulation_metrics")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class SimulationMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private BigInteger id;

    @Column(columnDefinition = "BIGINT", name = "simulation_run_id")
    private BigInteger simulationRunId;

    @Column(name = "node_id", nullable = false)
    private Integer nodeId;

    @Column(name = "node_name")
    private String nodeName;

    @Column(name = "cpu_usage", nullable = false)
    private Double cpuUsage;

    @Column(name = "ram_usage", nullable = false)
    private Double ramUsage;

    @Column(name = "duration", nullable = false)
    private Integer duration;
}
