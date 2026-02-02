package com.company.mortgage.exceptionhandler;

import com.company.mortgage.error.ErrorMessageResponse;
import com.company.mortgage.exception.DuplicateInterestRateException;
import com.company.mortgage.exception.InterestRateNotFoundException;
import com.company.mortgage.exception.MortgageNotFeasibleException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String TRACE_ID = "traceId";

    @ExceptionHandler(InterestRateNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleInterestRateNotFound(InterestRateNotFoundException ex) {
        log.warn("InterestRateNotFoundException: {} (traceId={})", ex.getMessage(), MDC.get(TRACE_ID));
        ErrorMessageResponse errorResponse = new ErrorMessageResponse(
                "INTEREST_RATE_NOT_FOUND",
                List.of(ex.getMessage()),
                MDC.get(TRACE_ID),
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Unhandled exception: {} (traceId={})", ex.getMessage(), MDC.get(TRACE_ID), ex);

        List<String> messages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        ErrorMessageResponse errorResponse = new ErrorMessageResponse("VALIDATION_ERROR",
                messages,
                MDC.get(TRACE_ID),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DuplicateInterestRateException.class)
    public ResponseEntity<ErrorMessageResponse> handleDuplicateRate(DuplicateInterestRateException ex) {
        log.warn("DuplicateInterestRateException: {} (traceId={})", ex.getMessage(), MDC.get(TRACE_ID));
        ErrorMessageResponse errorResponse = new ErrorMessageResponse(
                "DUPLICATE_INTEREST_RATE",
                List.of(ex.getMessage()),
                MDC.get(TRACE_ID),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MortgageNotFeasibleException.class)
    public ResponseEntity<ErrorMessageResponse> handleMortgageNotFeasible(
            MortgageNotFeasibleException ex) {
        log.warn("MortgageNotFeasibleException: {} (traceId={})", ex.getMessage(), MDC.get(TRACE_ID));
        ErrorMessageResponse response = new ErrorMessageResponse(
                "MORTGAGE_NOT_FEASIBLE",
                List.of(ex.getMessage()),
                MDC.get(TRACE_ID),
                HttpStatus.UNPROCESSABLE_ENTITY.value()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessageResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        log.warn("HttpRequestMethodNotSupportedException: {} (traceId={})", ex.getMessage(), MDC.get(TRACE_ID));

        ErrorMessageResponse response = new ErrorMessageResponse(
                "METHOD_NOT_ALLOWED",
                List.of(ex.getMessage()),
                MDC.get(TRACE_ID),
                HttpStatus.METHOD_NOT_ALLOWED.value()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorMessageResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex) {
        ErrorMessageResponse response = new ErrorMessageResponse(
                "UNSUPPORTED_MEDIA_TYPE",
                List.of(ex.getMessage()),
                MDC.get(TRACE_ID),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleInternalServerError(Exception ex) {
        log.error("Unhandled exception: {} (traceId={})", ex.getMessage(), MDC.get(TRACE_ID), ex);

        ErrorMessageResponse response = new ErrorMessageResponse(
                "INTERNAL_SERVER_ERROR",
                List.of("An unexpected error occurred. Please contact support."),
                MDC.get(TRACE_ID),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

