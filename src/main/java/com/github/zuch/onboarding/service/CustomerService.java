package com.github.zuch.onboarding.service;

import com.github.zuch.onboarding.mapper.CustomerMapper;
import com.github.zuch.onboarding.model.Registration;
import com.github.zuch.onboarding.model.RegistrationResponse;
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

            // persist registration
            if (validation.isValid()) {
                final AccountEntity accountEntity = mapper.mapToAccountEntity(registration);
                accountEntity.setPassword(mapper.generatePassword());//set random password

                final AccountEntity saved = accountRepository.save(accountEntity);//persist
                return mapper.mapToRegResponse(saved, validation);
            } else {
                return mapper.mapToRegResponseException(registration, validation);
            }
        } catch (final Exception e) {
            log.error("Error while processing Customer Registration", e);

            final Validation validation = validationMessageUtil.createValidationFailed(e.getMessage());
            return mapper.mapToRegResponseException(registration, validation);
        }
    }
}
