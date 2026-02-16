package com.delimce.aibroker.domain.entities;

import com.delimce.aibroker.domain.enums.ModelType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "tbl_model")
public class Model extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModelType type;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,6)")
    @Builder.Default
    private float costTokenIn = 0; // usd

    @Column(nullable = false, columnDefinition = "DECIMAL(10,6)")
    @Builder.Default
    private float costTokenOut = 0; // usd

    @Column(nullable = true)
    private String costTokenUnit; // ex: 1M -> million

}
