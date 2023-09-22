package com.github.zuch.onboarding.mapper;

import com.github.zuch.onboarding.TestUtil;
import com.github.zuch.onboarding.model.Validation;
import com.github.zuch.onboarding.model.request.RegistrationRequest;
import com.github.zuch.onboarding.model.response.LogOnResponse;
import com.github.zuch.onboarding.model.response.OverviewResponse;
import com.github.zuch.onboarding.model.response.RegistrationResponse;
import com.github.zuch.onboarding.persistence.entity.Account;
import com.github.zuch.onboarding.persistence.entity.Role;
import com.github.zuch.onboarding.persistence.entity.Roles;
import com.github.zuch.onboarding.persistence.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerMapperTest {

    public static final String BE_COUNTRY_CODE = "BE";
    public static final String DE_COUNTRY_CODE = "DE";

    private CustomerMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new CustomerMapper();
    }

    //------------------------- User -----------------------------

    @Test
    void when_RegistrationRequest_when_RoleAndAccountMapped_then_mapToUser() throws IOException {
        // given
        final RegistrationRequest registrationRequest = TestUtil.loadFileToObject("json/registration_valid.json", RegistrationRequest.class);
        final Role role = mapper.mapToRole(registrationRequest);
        final Account account = mapper.mapToAccount(registrationRequest);

        // when
        final User user = mapper.mapToUser(registrationRequest, role, account, "AY3QbioL2k");

        // then
        assertEquals(Roles.CUSTOMER, role.getName());
        assertEquals(registrationRequest.getUsername(), role.getUsername());

        assertEquals(registrationRequest.getUsername(), account.getUsername());

        assertEquals(registrationRequest.getName(), user.getName());
        assertEquals(registrationRequest.getSurname(), user.getSurname());
        assertEquals(registrationRequest.getAddress().getStreet(), user.getAddressStreet());
        assertEquals(registrationRequest.getAddress().getHouseNumber(), user.getAddressHouseNumber());
        assertEquals(registrationRequest.getAddress().getPostCode(), user.getAddressPostCode());
        assertEquals(registrationRequest.getAddress().getCity(), user.getAddressCity());
        assertEquals(registrationRequest.getDateOfBirth(), user.getDateOfBirth());
        assertEquals(registrationRequest.getIdDocument().getType(), user.getIdDocumentType());
        assertEquals(registrationRequest.getIdDocument().getIdNumber(), user.getIdDocumentNumber());
        assertEquals(registrationRequest.getIdDocument().getCountryCode(), user.getIdDocumentCountryCode());
        assertEquals(registrationRequest.getIdDocument().getIssueDate(), user.getIdDocumentIssueDate());
        assertEquals(registrationRequest.getIdDocument().getExpiryDate(), user.getIdDocumentExpiryDate());
        assertEquals(registrationRequest.getUsername(), user.getUsername());
    }

    @Test
    void when_RegistrationRequest_whenValidationTrue_then_mapToRegistrationResponse() throws IOException {
        // given
        final RegistrationRequest registrationRequest = TestUtil.loadFileToObject("json/registration_valid.json", RegistrationRequest.class);
        final Role role = mapper.mapToRole(registrationRequest);
        final Account account = mapper.mapToAccount(registrationRequest);
        final String password = "AY3QbioL2k";
        final User user = mapper.mapToUser(registrationRequest, role, account, password);
        final Validation validation = Validation.builder().valid(true).build();

        // when
        RegistrationResponse regResponse = mapper.mapToRegResponse(user, validation, password);

        // then
        assertTrue(regResponse.getValidation().isValid());
        assertEquals(registrationRequest.getUsername(), regResponse.getUsername());
    }

    @Test
    void when_RegistrationRequest_whenValidationFalse_then_mapToRegistrationResponseException() {
        // given
        final Validation validation = Validation.builder().valid(false).validationMessages(Collections.singleton("Error")).build();

        // when
        RegistrationResponse regResponse = mapper.mapToRegResponseException(validation);

        // then
        assertFalse(regResponse.getValidation().isValid());
        assertEquals("Error", regResponse.getValidation().getValidationMessages().stream().findFirst().get());
    }

    //------------------------- LogOn -----------------------------

    @Test
    void when_LogOnRequest_whenWithValidJwt_then_mapToLogOnResponse() {
        // given
        final Validation validation = Validation.builder().valid(true).build();
        final String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGVvbmUiLCJpYXQiOjE2OTUyMzM4NzMsImV4cCI6MTY5NTI0NDY3M30.F84rQJIKp6uwjOpFkEK-rD9GzOaajmCRBJqTpFjMKnU";

        // when
        LogOnResponse logOnResponse = mapper.mapToLogOnResponse(jwt, validation);

        // then
        assertTrue(logOnResponse.getValidation().isValid());
    }

    @Test
    void when_LogOnRequest_whenWithInvalidJwt_then_mapToLogOnResponseException() {
        // given
        final Validation validation = Validation.builder().valid(false).validationMessages(Collections.singleton("Error")).build();

        // when
        LogOnResponse logOnResponse = mapper.mapToLogOnResponseException(validation);

        // then
        assertFalse(logOnResponse.getValidation().isValid());
        assertEquals("Error", logOnResponse.getValidation().getValidationMessages().stream().findFirst().get());
    }

    //------------------------- Overview -----------------------------

    @Test
    void when_OverviewRequest_whenWithValidJwtAndAccountFound_then_mapToOverviewResponse() throws IOException {
        // given
        final RegistrationRequest registrationRequest = TestUtil.loadFileToObject("json/registration_valid.json", RegistrationRequest.class);
        final Account account = mapper.mapToAccount(registrationRequest);

        // when
        OverviewResponse overviewResponse = mapper.mapToOverviewResponse(account);

        // then
        assertTrue(overviewResponse.getValidation().isValid());
    }

    @Test
    void when_OverviewRequest_whenWithInvalidJwt_then_mapToOverviewResponseException() throws IOException {
        // given
        final Validation validation = Validation.builder().valid(false).validationMessages(Collections.singleton("Error")).build();

        // when
        OverviewResponse overviewResponse = mapper.mapToOverviewResponseException(validation);

        // then
        assertFalse(overviewResponse.getValidation().isValid());
        assertEquals("Error", overviewResponse.getValidation().getValidationMessages().stream().findFirst().get());
    }

    //------------------------- generators -----------------------------

    @Test
    void when_RegistrationRequest_when_BECountry_then_mapToAccountIBANWithBE() throws IOException {
        // given
        final RegistrationRequest registrationRequest = TestUtil.loadFileToObject("json/registration_valid.json", RegistrationRequest.class);
        registrationRequest.getIdDocument().setCountryCode(BE_COUNTRY_CODE);

        // when
        Account account = mapper.mapToAccount(registrationRequest);

        // then
        assertTrue(account.getIban().startsWith(BE_COUNTRY_CODE));
    }

    @Test
    void when_RegistrationRequest_when_DECountry_then_mapToAccountIBANWithDE() throws IOException {
        // given
        final RegistrationRequest registrationRequest = TestUtil.loadFileToObject("json/registration_valid.json", RegistrationRequest.class);
        registrationRequest.getIdDocument().setCountryCode(DE_COUNTRY_CODE);

        // when
        Account account = mapper.mapToAccount(registrationRequest);

        // then
        assertTrue(account.getIban().startsWith(DE_COUNTRY_CODE));
    }
}
