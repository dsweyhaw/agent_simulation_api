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

@Entity
@Table(name = "pig_data_daily")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PigDataDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private BigInteger id;

    @Column(name = "run_id", nullable = false, columnDefinition = "BIGINT")
    private BigInteger runId;

    @Column(name = "pig_id", nullable = false)
    private Integer pigId;

    @Column(name = "pigpen_id", nullable = false)
    private Integer pigpenId;

    @Column(nullable = false)
    private Integer day;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Double dfi;

    @Column(nullable = false)
    private Double cfi;

    @Column(name = "target_cfi", nullable = false)
    private Double targetCfi;

    @Column(name = "target_dfi", nullable = false)
    private Double targetDfi;

    private Integer eatCount;

    private Integer excreteCount;

    @Column(columnDefinition = "TINYINT")
    private Integer seir;
}
