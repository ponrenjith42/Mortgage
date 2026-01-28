package com.company.mortgage.exceptionhandler;

import com.company.mortgage.error.ErrorMessageResponse;
import com.company.mortgage.service.exception.DuplicateInterestRateException;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
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

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().code()).isEqualTo("INTEREST_RATE_NOT_FOUND");
    }

    @Test
    void testHandleDuplicateRate() {
        DuplicateInterestRateException ex = new DuplicateInterestRateException(20);

        ResponseEntity<ErrorMessageResponse> response =
                handler.handleDuplicateRate(ex);

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
        assertThat(response.getBody().messages())
                .containsExactlyInAnyOrder(
                        "loanValue: must not be null",
                        "income: must be positive"
                );
    }
}