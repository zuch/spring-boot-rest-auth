package com.github.zuch.onboarding.controller;

import com.github.zuch.onboarding.model.request.LogOnRequest;
import com.github.zuch.onboarding.model.response.LogOnResponse;
import com.github.zuch.onboarding.model.request.RegistrationRequest;
import com.github.zuch.onboarding.model.response.OverviewResponse;
import com.github.zuch.onboarding.model.response.RegistrationResponse;
import com.github.zuch.onboarding.service.CustomerService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Customer REST Controller")
@RestController
@RequiredArgsConstructor
public class CustomerRestController {

    private final CustomerService customerService;

    @Operation(summary = "endpoint used to register a new customer")
    @RateLimiter(name = "rateLimiter")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegistrationResponse> register(@RequestBody final RegistrationRequest registrationRequest) {
        final RegistrationResponse response = customerService.register(registrationRequest);
        if (response.getValidation().isValid()) {
            response.setValidation(null);//remove validation object
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "endpoint used to logon an existing customer")
    @RateLimiter(name = "rateLimiter")
    @PostMapping(value = "/logon", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<LogOnResponse> logon(@RequestBody final LogOnRequest logOnRequest) {
        final LogOnResponse response = customerService.logon(logOnRequest);
        if (response.getValidation().isValid()) {
            response.setValidation(null);//remove validation object
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "endpoint used to get account overview for an existing customer using JWT Token")
    @RateLimiter(name = "rateLimiter")
    @GetMapping(value = "/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<OverviewResponse> overview(@RequestHeader Map<String, String> headers) {
        final OverviewResponse response = customerService.overview(headers);
        if (response.getValidation().isValid()) {
            response.setValidation(null);//remove validation object
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}