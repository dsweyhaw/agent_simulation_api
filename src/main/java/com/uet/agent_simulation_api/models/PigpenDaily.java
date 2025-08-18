package com.uet.agent_simulation_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Table(name = "pigpen_daily")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PigpenDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private BigInteger id;

    @Column(name = "run_id", nullable = false, columnDefinition = "BIGINT")
    private BigInteger runId;

    @Column(name = "pigpen_id", nullable = false)
    private Integer pigpenId;

    @Column(nullable = false)
    private Integer day;

    @Column(name = "total_pigs", nullable = false)
    private Integer totalPigs;

    @Column(name = "unexposed_count", nullable = false)
    private Integer unexposedCount;

    @Column(name = "exposed_count", nullable = false)
    private Integer exposedCount;

    @Column(name = "infected_count", nullable = false)
    private Integer infectedCount;

    @Column(name = "recovered_count", nullable = false)
    private Integer recoveredCount;
}
