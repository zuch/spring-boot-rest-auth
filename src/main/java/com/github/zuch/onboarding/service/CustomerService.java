package com.github.zuch.onboarding.service;

import com.github.zuch.onboarding.mapper.CustomerMapper;
import com.github.zuch.onboarding.model.Validation;
import com.github.zuch.onboarding.model.request.LogOnRequest;
import com.github.zuch.onboarding.model.request.RegistrationRequest;
import com.github.zuch.onboarding.model.response.LogOnResponse;
import com.github.zuch.onboarding.model.response.OverviewResponse;
import com.github.zuch.onboarding.model.response.RegistrationResponse;
import com.github.zuch.onboarding.persistence.AccountRepository;
import com.github.zuch.onboarding.persistence.RoleRepository;
import com.github.zuch.onboarding.persistence.UserRepository;
import com.github.zuch.onboarding.persistence.entity.Account;
import com.github.zuch.onboarding.persistence.entity.Role;
import com.github.zuch.onboarding.persistence.entity.User;
import com.github.zuch.onboarding.security.jwt.JwtUtils;
import com.github.zuch.onboarding.validation.CustomerValidationService;
import com.github.zuch.onboarding.validation.ValidationMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final String AUTHORIZATION = "authorization";

    private final JwtUtils jwtUtils;
    private final CustomerMapper mapper;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final ValidationMessageUtil validationMessageUtil;
    private final CustomerValidationService customerValidationService;

    public RegistrationResponse register(final RegistrationRequest registrationRequest) {
        try {
            // validation
            final Validation validation = customerValidationService.validateReg(registrationRequest);

            if (validation.isValid()) { //valid
                final String password = mapper.generatePassword();

                // persist role
                Role role = mapper.mapToRole(registrationRequest);
                roleRepository.save(role);

                // persist account
                Account account = mapper.mapToAccount(registrationRequest);
                accountRepository.save(account);

                // persist user
                final User user = mapper.mapToUser(registrationRequest, role, account, encoder.encode(password));
                final User saved = userRepository.save(user);

                return mapper.mapToRegResponse(saved, validation, password);
            } else { //validation error
                return mapper.mapToRegResponseException(validation);
            }
        } catch (final Exception e) {
            log.error("Error while processing Customer Registration request", e);
            final Validation validation = validationMessageUtil.createValidationFailed(e.getMessage());
            return mapper.mapToRegResponseException(validation);
        }
    }

    public LogOnResponse logon(final LogOnRequest logOnRequest) {
        try {
            // validation
            final Validation validation = customerValidationService.validateLogon(logOnRequest);

            if (validation.isValid()) { //valid
                final Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(logOnRequest.getUsername(), logOnRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                final String jwt = jwtUtils.generateJwtToken(authentication);

                return mapper.mapToLogOnResponse(jwt, validation);
            } else {
                return mapper.mapToLogOnResponseException(validation);
            }
        } catch (final Exception e) {
            log.error("Error while processing Logon request", e);
            final Validation validation = validationMessageUtil.createValidationFailed(e.getMessage());
            return mapper.mapToLogOnResponseException(validation);
        }
    }

    public OverviewResponse overview(final Map<String, String> headers) {
        try {
            final String authorizationHeader = headers.get(AUTHORIZATION);
            final String username = jwtUtils.getUserNameFromJwtToken(jwtUtils.parseAuthHeader(authorizationHeader));
            log.info("Overview endpoint JWT token username [{}]", username);

            final Optional<Account> account = accountRepository.findByUsername(username);// find account with username parsed from JWT
            if (account.isPresent()) {
                return mapper.mapToOverviewResponse(account.get());
            } else {
                final Validation validation = validationMessageUtil.createValidationFailed(String.format("account with username [%s] not found", username));
                return mapper.mapToOverviewResponseException(validation);
            }
        } catch (final Exception e) {
            log.error("Error while processing Overview request", e);
            final Validation validation = validationMessageUtil.createValidationFailed(e.getMessage());
            return mapper.mapToOverviewResponseException(validation);
        }
    }
}
