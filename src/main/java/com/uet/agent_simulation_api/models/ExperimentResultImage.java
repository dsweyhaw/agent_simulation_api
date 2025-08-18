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
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.math.BigInteger;

@Table(name = "experiment_result_images")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@BatchSize(size = 1000)
@ToString
public class ExperimentResultImage extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eri_sequence_generator")
    @SequenceGenerator(name = "eri_sequence_generator", sequenceName = "experiment_result_image_seq", allocationSize = 1000)
    @Column(columnDefinition = "BIGINT")
    private BigInteger id;

    private String name;

    @Column(columnDefinition = "VARCHAR(300)")
    @JsonIgnore
    private String location;

    @Column(columnDefinition = "VARCHAR(20)")
    private String extension;

    private Integer step;

    @Column(name = "experiment_result_id", nullable = false, insertable = false, updatable = false, columnDefinition = "BIGINT")
    private BigInteger experimentResultId;

    @Column(name = "experiment_result_category_id", insertable = false, updatable = false, columnDefinition = "BIGINT")
    private BigInteger experimentResultCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_result_id", referencedColumnName = "id")
    @JsonIgnore
    private ExperimentResult experimentResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_result_category_id", referencedColumnName = "id")
    @JsonIgnore
    private ExperimentResultCategory experimentResultCategory;
}
