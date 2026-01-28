package com.company.mortgage.service.mapper;

import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.response.MortgageRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MortgageRateMapper {

    @Mapping(source = "lastUpdatedAt", target = "lastUpdate")
    MortgageRateResponse toMortgageRateResponse(MortgageRate entity);

    List<MortgageRateResponse> toMortgageRateResponseList(List<MortgageRate> entities);
}
