package com.company.mortgage.repository;

import com.company.mortgage.repository.model.MortgageRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRateRepository extends JpaRepository<MortgageRate, Integer> {}
