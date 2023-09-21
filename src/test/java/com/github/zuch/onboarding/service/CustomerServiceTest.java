package com.github.zuch.onboarding.service;

import com.github.zuch.onboarding.TestUtil;
import com.github.zuch.onboarding.mapper.CustomerMapper;
import com.github.zuch.onboarding.model.AccountType;
import com.github.zuch.onboarding.model.Currency;
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
import com.github.zuch.onboarding.persistence.entity.Roles;
import com.github.zuch.onboarding.persistence.entity.User;
import com.github.zuch.onboarding.security.jwt.JwtUtils;
import com.github.zuch.onboarding.validation.CustomerValidationService;
import com.github.zuch.onboarding.validation.ValidationMessageUtil;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private CustomerMapper mapper;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private ValidationMessageUtil validationMessageUtil;
    @Mock
    private CustomerValidationService customerValidationService;

    @InjectMocks
    private CustomerService customerService;

    //--------------------------- register --------------------------

    @Test
    void given_validRegistrationRequest_when_register_then_ValidRegistrationResponseReturned() throws IOException {
        // given
        final RegistrationRequest registrationRequest = TestUtil.loadFileToObject("json/registration_valid.json", RegistrationRequest.class);
        final Validation validation = Validation.builder().valid(true).build();

        when(customerValidationService.validateReg(registrationRequest)).thenReturn(validation);
        when(mapper.generatePassword()).thenReturn("AY3QbioL2k");
        when(mapper.mapToRole(registrationRequest)).thenReturn(Role.builder().username("theone").name(Roles.CUSTOMER).build());
        when(mapper.mapToAccount(registrationRequest)).thenReturn(Account.builder().build());
        when(encoder.encode("AY3QbioL2k")).thenReturn("$2a$10$kLDlpQAjgi9/Fs2Ry.QIAO0Am88w/yypVoS2NC34zX.lz7qgEXqH6");
        when(mapper.mapToUser(any(RegistrationRequest.class), any(Role.class), any(Account.class), anyString())).thenReturn(User.builder().build());
        when(userRepository.save(any(User.class))).thenReturn(User.builder().build());
        when(mapper.mapToRegResponse(any(User.class), any(Validation.class), anyString())).thenReturn(RegistrationResponse.builder().validation(validation).build());

        // when
        final RegistrationResponse registrationResponse = customerService.register(registrationRequest);

        // then
        assertTrue(registrationResponse.getValidation().isValid());
    }

    @Test
    void given_validRegistrationRequest_when_registerAndValidationError_then_inValidRegistrationResponseReturned() throws IOException {
        // given
        final RegistrationRequest registrationRequest = TestUtil.loadFileToObject("json/registration_valid.json", RegistrationRequest.class);
        final Validation validation = Validation.builder().valid(false).build();

        when(customerValidationService.validateReg(registrationRequest)).thenReturn(validation);
        when(mapper.mapToRegResponseException(any(Validation.class))).thenReturn(RegistrationResponse.builder().validation(validation).build());

        // when
        final RegistrationResponse registrationResponse = customerService.register(registrationRequest);

        // then
        assertFalse(registrationResponse.getValidation().isValid());
    }

    @Test
    void given_validRegistrationRequest_when_registerAndExceptionThrown_then_inValidRegistrationResponseReturned() throws IOException {
        // given
        final RegistrationRequest registrationRequest = TestUtil.loadFileToObject("json/registration_valid.json", RegistrationRequest.class);
        final Validation validation = Validation.builder().valid(false).build();

        when(customerValidationService.validateReg(registrationRequest)).thenThrow(new RuntimeException("Error"));
        when(validationMessageUtil.createValidationFailed(anyString())).thenReturn(validation);
        when(mapper.mapToRegResponseException(any(Validation.class))).thenReturn(RegistrationResponse.builder().validation(validation).build());

        // when
        final RegistrationResponse registrationResponse = customerService.register(registrationRequest);

        // then
        assertFalse(registrationResponse.getValidation().isValid());
    }

    //--------------------------- logon --------------------------

    @Test
    void given_validLogOnRequest_when_logon_then_ValidLogOnResponseReturned() throws IOException {
        // given
        final LogOnRequest logOnRequest = TestUtil.loadFileToObject("json/logon_valid.json", LogOnRequest.class);
        final Validation validation = Validation.builder().valid(true).build();

        when(customerValidationService.validateLogon(logOnRequest)).thenReturn(validation);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(makeAuth());
        when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGVvbmUiLCJpYXQiOjE2OTUyMzM4NzMsImV4cCI6MTY5NTI0NDY3M30.F84rQJIKp6uwjOpFkEK-rD9GzOaajmCRBJqTpFjMKnU");
        when(mapper.mapToLogOnResponse(anyString(), any(Validation.class))).thenReturn(LogOnResponse.builder().validation(validation).build());

        // when
        final LogOnResponse logOnResponse = customerService.logon(logOnRequest);

        // then
        assertTrue(logOnResponse.getValidation().isValid());
    }

    @Test
    void given_validLogOnRequest_when_logonAndValidationError_then_inValidLogOnResponseReturned() throws IOException {
        // given
        final LogOnRequest logOnRequest = TestUtil.loadFileToObject("json/logon_valid.json", LogOnRequest.class);
        final Validation validation = Validation.builder().valid(false).build();

        when(customerValidationService.validateLogon(logOnRequest)).thenReturn(validation);
        when(mapper.mapToLogOnResponseException(any(Validation.class))).thenReturn(LogOnResponse.builder().validation(validation).build());

        // when
        final LogOnResponse logOnResponse = customerService.logon(logOnRequest);

        // then
        assertFalse(logOnResponse.getValidation().isValid());
    }

    @Test
    void given_validLogOnRequest_when_logonAndExceptionThrown_then_inValidLogOnResponseReturned() throws IOException {
        // given
        final LogOnRequest logOnRequest = TestUtil.loadFileToObject("json/logon_valid.json", LogOnRequest.class);
        final Validation validation = Validation.builder().valid(false).build();

        when(customerValidationService.validateLogon(logOnRequest)).thenThrow(new RuntimeException("Error"));
        when(validationMessageUtil.createValidationFailed(anyString())).thenReturn(validation);
        when(mapper.mapToLogOnResponseException(any(Validation.class))).thenReturn(LogOnResponse.builder().validation(validation).build());

        // when
        final LogOnResponse logOnResponse = customerService.logon(logOnRequest);

        // then
        assertFalse(logOnResponse.getValidation().isValid());
    }

    //--------------------------- overview --------------------------

    @Test
    void given_validOverviewRequest_when_overview_then_ValidOverviewResponseReturned() {
        // given
        final Map<String, String> headers = new HashMap<>();
        final String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGVvbmUiLCJpYXQiOjE2OTUyMzM4NzMsImV4cCI6MTY5NTI0NDY3M30.F84rQJIKp6uwjOpFkEK-rD9GzOaajmCRBJqTpFjMKnU";
        headers.put("authorization", "Bearer " + jwtToken);

        final OverviewResponse validOverResponse = OverviewResponse.builder()
                .iban("NL62VLET1423701633")
                .openingDate(LocalDateTime.now())
                .currency(Currency.EUR)
                .accountBalance(BigDecimal.TEN)
                .accountType(AccountType.SAVINGS).build();

        when(jwtUtils.parseAuthHeader(anyString())).thenReturn(jwtToken);
        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn("theone");
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.of(Account.builder().build()));
        when(mapper.mapToOverviewResponse(any(Account.class))).thenReturn(validOverResponse);

        // when
        final OverviewResponse overviewResponse = customerService.overview(headers);

        // then
        assertEquals("NL62VLET1423701633", overviewResponse.getIban());
        assertEquals(Currency.EUR, overviewResponse.getCurrency());
        assertEquals(AccountType.SAVINGS, overviewResponse.getAccountType());
        assertEquals(BigDecimal.TEN, overviewResponse.getAccountBalance());
    }

    @Test
    void given_invalidOverviewRequest_when_overviewAndExceptionThrown_then_inValidOverviewResponseReturned() {
        // given
        final Map<String, String> headers = new HashMap<>();
        final String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGVvbmUiLCJpYXQiOjE2OTUyMzM4NzMsImV4cCI6MTY5NTI0NDY3M30.F84rQJIKp6uwjOpFkEK-rD9GzOaajmCRBJqTpFjMKnU";
        headers.put("authorization", "Bearer " + jwtToken);

        final Validation validation = Validation.builder().valid(false).build();
        final OverviewResponse invalidOverviewResponse = OverviewResponse.builder()
                .validation(validation)
                .accountType(AccountType.SAVINGS).build();

        when(jwtUtils.parseAuthHeader(anyString())).thenReturn(jwtToken);
        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn("theone");
        when(accountRepository.findByUsername(anyString())).thenReturn(null);
        when(validationMessageUtil.createValidationFailed(anyString())).thenReturn(validation);
        when(mapper.mapToOverviewResponseException(any(Validation.class))).thenReturn(invalidOverviewResponse);

        // when
        final OverviewResponse overviewResponse = customerService.overview(headers);

        // then
        assertFalse(overviewResponse.getValidation().isValid());
    }

    private Authentication makeAuth() {
        return new Authentication() {
            @Override
            public boolean equals(Object another) {
                return false;
            }

            @Override
            public String toString() {
                return null;
            }

            @Override
            public int hashCode() {
                return 0;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        };
    }


}
