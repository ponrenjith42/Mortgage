package com.company.mortgage.exceptionhandler;

import com.company.mortgage.error.ErrorMessageResponse;
import com.company.mortgage.service.exception.DuplicateInterestRateException;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InterestRateNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleInterestRateNotFound(InterestRateNotFoundException ex) {
        String traceId = UUID.randomUUID().toString();
        log.warn("InterestRateNotFoundException: {} (traceId={})", ex.getMessage(), traceId);
        ErrorMessageResponse errorResponse = new ErrorMessageResponse(
                "INTEREST_RATE_NOT_FOUND",
                List.of(ex.getMessage()),
                traceId,
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String traceId = UUID.randomUUID().toString();
        log.warn("Unhandled exception: {} (traceId={})", ex.getMessage(), traceId, ex);

        List<String> messages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        ErrorMessageResponse errorResponse = new ErrorMessageResponse("VALIDATION_ERROR",
                messages,
                traceId,
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateInterestRateException.class)
    public ResponseEntity<ErrorMessageResponse> handleDuplicateRate(DuplicateInterestRateException ex) {
        String traceId = UUID.randomUUID().toString();
        log.warn("DuplicateInterestRateException: {} (traceId={})", ex.getMessage(), traceId);
        ErrorMessageResponse errorResponse = new ErrorMessageResponse(
                "DUPLICATE_INTEREST_RATE",
                List.of(ex.getMessage()),
                traceId,
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
