package com.company.mortgage.exceptionhandler;

import com.company.mortgage.error.ErrorMessageResponse;
import com.company.mortgage.service.exception.DuplicateInterestRateException;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import com.company.mortgage.service.exception.MortgageNotFeasibleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Test
    void testHandleInterestRateNotFound() {
        InterestRateNotFoundException ex = new InterestRateNotFoundException(10);

        ResponseEntity<ErrorMessageResponse> response =
                handler.handleInterestRateNotFound(ex);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().code()).isEqualTo("INTEREST_RATE_NOT_FOUND");
    }

    @Test
    void testHandleDuplicateRate() {
        DuplicateInterestRateException ex = new DuplicateInterestRateException(20);

        ResponseEntity<ErrorMessageResponse> response =
                handler.handleDuplicateRate(ex);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().code()).isEqualTo("DUPLICATE_INTEREST_RATE");
    }

    @Test
    void testHandleValidationExceptions() {
        FieldError fieldError1 = new FieldError("request", "loanValue", "must not be null");
        FieldError fieldError2 = new FieldError("request", "income", "must be positive");

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError1, fieldError2));
        when(methodArgumentNotValidException.getBindingResult())
                .thenReturn(bindingResult);

        ResponseEntity<ErrorMessageResponse> response =
                handler.handleValidationExceptions(methodArgumentNotValidException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().messages())
                .containsExactlyInAnyOrder(
                        "loanValue: must not be null",
                        "income: must be positive"
                );
    }

    @Test
    void testHandleMortgageNotFeasible() {
        MortgageNotFeasibleException ex =
                new MortgageNotFeasibleException("Loan value exceeds 4 times the income");

        ResponseEntity<ErrorMessageResponse> response =
                handler.handleMortgageNotFeasible(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("MORTGAGE_NOT_FEASIBLE");
        assertThat(response.getBody().messages())
                .containsExactly("Loan value exceeds 4 times the income");
        assertThat(response.getBody().status()).isEqualTo(422);
        assertThat(response.getBody().traceId()).isNotNull();
    }

    @Test
    void testHandleInternalServerError() {
        Exception ex = new RuntimeException("Something went wrong");

        ResponseEntity<ErrorMessageResponse> response =
                handler.handleInternalServerError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().messages())
                .containsExactly("An unexpected error occurred. Please contact support.");
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().traceId()).isNotNull();
    }
}