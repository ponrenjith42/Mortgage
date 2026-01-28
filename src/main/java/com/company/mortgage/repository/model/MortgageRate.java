package com.company.mortgage.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor @NoArgsConstructor @Getter @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Table(name = "mortgage_rate")
public class MortgageRate {

    @Id
    @EqualsAndHashCode.Include
    private int maturityPeriod;
    private BigDecimal interestRate;
    @Builder.Default
    private LocalDateTime lastUpdatedAt = LocalDateTime.now();
}