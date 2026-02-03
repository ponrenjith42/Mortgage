package com.company.mortgage.repository;

import com.company.mortgage.repository.model.InterestRateEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("!in-memory")
public interface InterestRateRepository extends JpaRepository<InterestRateEntity, Integer> {
    Optional<InterestRateEntity> findByMaturityPeriod(int maturityPeriod);
}
