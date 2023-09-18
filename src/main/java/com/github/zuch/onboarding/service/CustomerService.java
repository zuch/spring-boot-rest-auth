package com.github.zuch.onboarding.service;

import com.github.zuch.onboarding.mapper.CustomerMapper;
import com.github.zuch.onboarding.model.request.LogOn;
import com.github.zuch.onboarding.model.response.LogOnResponse;
import com.github.zuch.onboarding.model.request.Registration;
import com.github.zuch.onboarding.model.response.RegistrationResponse;
import com.github.zuch.onboarding.model.Validation;
import com.github.zuch.onboarding.persistence.AccountRepository;
import com.github.zuch.onboarding.persistence.entity.AccountEntity;
import com.github.zuch.onboarding.validation.CustomerValidationService;
import com.github.zuch.onboarding.validation.ValidationMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerMapper mapper;
    private final AccountRepository accountRepository;
    private final ValidationMessageUtil validationMessageUtil;
    private final CustomerValidationService customerValidationService;

    public RegistrationResponse register(final Registration registration) {
        try {
            // validation
            final Validation validation = customerValidationService.validateReg(registration);

            if (validation.isValid()) { //valid
                final AccountEntity accountEntity = mapper.mapToAccountEntity(registration);
                accountEntity.setPassword(mapper.generatePassword());// set random password

                final AccountEntity saved = accountRepository.save(accountEntity);// persist registration
                return mapper.mapToRegResponse(saved, validation);
            } else { //validation error
                return mapper.mapToRegResponseException(registration, validation);
            }
        } catch (final Exception e) {
            log.error("Error while processing Customer Registration", e);

            final Validation validation = validationMessageUtil.createValidationFailed(e.getMessage());
            return mapper.mapToRegResponseException(registration, validation);
        }
    }

    public LogOnResponse logon(final LogOn logOn) {
        try {

        } catch (final Exception e) {
            log.error("Error while processing Logon", e);
        }

        return LogOnResponse.builder().build();
    }

}
