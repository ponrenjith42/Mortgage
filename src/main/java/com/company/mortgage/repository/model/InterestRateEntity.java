package com.company.mortgage.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "interest_rate_table",
        indexes = {
                @Index(name = "idx_maturity_period", columnList = "maturity_period")
        }
)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@ToString
public class InterestRateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "maturity_period",nullable = false,unique = true)
    @EqualsAndHashCode.Include
    private int maturityPeriod;

    @Column(name = "interest_rate",nullable = false)
    private BigDecimal interestRate;

    @Column(name = "last_update",nullable = false)
    private LocalDateTime lastUpdate;

    @Builder
    private InterestRateEntity(int maturityPeriod, BigDecimal interestRate, LocalDateTime lastUpdate) {
        this.maturityPeriod = maturityPeriod;
        this.interestRate = interestRate;
        this.lastUpdate = lastUpdate;
    }
}