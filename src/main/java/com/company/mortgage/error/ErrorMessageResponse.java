package com.company.mortgage.error;

import java.util.List;

public record ErrorMessageResponse(
        String code,
        List<String> messages,
        String traceId,
        int status
) {
}
