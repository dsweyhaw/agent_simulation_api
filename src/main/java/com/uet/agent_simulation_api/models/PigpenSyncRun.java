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

import java.math.BigInteger;

@Entity
@Table(name = "pigpen_sync_runs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PigpenSyncRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private BigInteger id;

    @Column(name = "run_id", nullable = false, columnDefinition = "BIGINT")
    private BigInteger runId;

    @Column(name = "pigpen_id", nullable = false)
    private Integer pigpenId;

    @Column(nullable = false)
    private Integer cycle;

    @Column(name = "is_complete", columnDefinition = "BOOL DEFAULT FALSE")
    private Boolean isComplete;
}
