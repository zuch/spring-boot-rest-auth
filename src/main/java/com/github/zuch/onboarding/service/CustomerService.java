package com.github.zuch.onboarding.service;

import com.github.zuch.onboarding.model.Registration;
import com.github.zuch.onboarding.model.RegistrationResponse;
import com.github.zuch.onboarding.model.Validation;
import com.github.zuch.onboarding.util.ValidationMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final ValidationMessageUtil validationMessageUtil;
    private final CustomerValidationService customerValidationService;

    public RegistrationResponse register(final Registration registration) {
        RegistrationResponse response = new RegistrationResponse();
        try {
            // validation
            final Validation validation = customerValidationService.validateRegistration(registration);
            response.setValidation(validation);
        } catch (final Exception e) {
            log.error("Error while processing Customer Registration", e);

            final Validation validation = validationMessageUtil.createValidationFailed(e.getMessage());
            response.setValidation(validation);
        }
        return response;
    }
}
