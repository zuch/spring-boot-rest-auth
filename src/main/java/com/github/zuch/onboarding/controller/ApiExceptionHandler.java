package com.github.zuch.onboarding.controller;

import com.github.zuch.onboarding.model.Validation;
import com.github.zuch.onboarding.model.response.ApiErrorResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(final Exception e) {
        if (e instanceof HttpMessageNotReadableException) { //Payload cant be parsed
            final String errorMessage = e.getMessage();
            log.warn("Error while parsing payload: {}", errorMessage);
            final ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                    .validation(
                            Validation.builder()
                                    .valid(false)
                                    .validationMessages(Collections.singleton(errorMessage))
                                    .build()
                    )
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
        } else {
            log.error("Error while processing request", e);
            final String errorMessage = e.getMessage();
            final ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                    .validation(
                            Validation.builder()
                                    .valid(false)
                                    .validationMessages(Collections.singleton(errorMessage))
                                    .build()
                    )
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
        }
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<Object> handleRequestNotPermitted(final RequestNotPermitted e, final HttpServletRequest request) {
        final String errorMessage = e.getMessage();
        log.warn("Request to path [{}] is blocked due to rate-limiting: [{}]", request.getRequestURI(), errorMessage);
        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .validation(
                        Validation.builder()
                                .valid(false)
                                .validationMessages(Collections.singleton(errorMessage))
                                .build()
                )
                .build();

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).contentType(MediaType.APPLICATION_JSON).body(apiErrorResponse);
    }
}