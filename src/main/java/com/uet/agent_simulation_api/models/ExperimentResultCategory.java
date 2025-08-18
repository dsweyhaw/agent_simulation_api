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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;

@Table(name = "experiment_result_categories")
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentResultCategory extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "erc_sequence_generator")
    @SequenceGenerator(name = "erc_sequence_generator", sequenceName = "experiment_result_category_seq", allocationSize = 10)
    @Column(columnDefinition = "BIGINT")
    private BigInteger id;

    @Column(nullable = false)
    private String name;

    @Column(name = "experiment_result_id", nullable = false, insertable = false, updatable = false, columnDefinition = "BIGINT")
    private BigInteger experimentResultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_result_id", referencedColumnName = "id")
    @JsonIgnore
    private ExperimentResult experimentResult;
}
