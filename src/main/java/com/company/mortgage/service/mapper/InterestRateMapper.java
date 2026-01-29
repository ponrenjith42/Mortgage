package com.company.mortgage.service.mapper;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.response.InterestRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InterestRateMapper {

    @Mapping(source = "lastUpdatedAt", target = "lastUpdate")
    InterestRateResponse toMortgageRateResponse(InterestRateEntity entity);

    List<InterestRateResponse> toMortgageRateResponseList(List<InterestRateEntity> entities);
}
