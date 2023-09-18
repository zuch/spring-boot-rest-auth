package com.github.zuch.onboarding.controller;

import com.github.zuch.onboarding.model.request.LogOn;
import com.github.zuch.onboarding.model.response.LogOnResponse;
import com.github.zuch.onboarding.model.request.Registration;
import com.github.zuch.onboarding.model.response.RegistrationResponse;
import com.github.zuch.onboarding.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerRestController {

    private final CustomerService customerService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegistrationResponse> register(@RequestBody final Registration registration) {
        final RegistrationResponse response = customerService.register(registration);
        if (response.getValidation().isValid()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/logon", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<LogOnResponse> logon(@RequestBody final LogOn logOn) {
        final LogOnResponse response = customerService.logon(logOn);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> overview() {
        return new ResponseEntity<>("Im Alive!", HttpStatus.OK);
    }


}