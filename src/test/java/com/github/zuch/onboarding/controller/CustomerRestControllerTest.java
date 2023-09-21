package com.github.zuch.onboarding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zuch.onboarding.mapper.CustomerMapper;
import com.github.zuch.onboarding.model.request.LogOnRequest;
import com.github.zuch.onboarding.model.request.RegistrationRequest;
import com.github.zuch.onboarding.model.response.ApiErrorResponse;
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
import com.github.zuch.onboarding.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class CustomerRestControllerTest {

    @Autowired
    private ObjectMapper om;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    @SpyBean
    private AccountRepository accountRepository;

    @Autowired
    @SpyBean
    private CustomerService customerService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        roleRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void given_validRegistration_when_PostedToRegisterEndpoint_then_201_Persisted() throws Exception {
        // given
        File file = resourceLoader.getResource("classpath:json/registration_valid.json").getFile();
        RegistrationRequest registrationRequest = om.readValue(file, RegistrationRequest.class);

        // when
        RegistrationResponse response = om.readValue(
                mockMvc.perform(post("/register")
                                .contentType("application/json")
                                .content(om.writeValueAsString(registrationRequest)))
                        .andDo(print())
                        .andExpect(jsonPath("$.password", notNullValue()))
                        .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), RegistrationResponse.class);

        // then
        assertFalse(response.getPassword().isEmpty());
    }

    @Test
    void given_invalidRegistrationUserAlreadyPersisted_when_PostedToRegisterAndUserAlreadyPersisted_then_400_ValidationError() throws Exception {
        // given
        File file = resourceLoader.getResource("classpath:json/registration_valid.json").getFile();
        RegistrationRequest reg = om.readValue(file, RegistrationRequest.class);

        // persist role
        Role role = Role.builder().username(reg.getUsername()).name(Roles.CUSTOMER).build();
        Role savedRole = roleRepository.save(role);

        // persist the same customer already
        Account account = Account.builder().username(reg.getUsername()).build();
        User user = customerMapper.mapToUser(reg, savedRole, account, "w2kfHZriif");
        userRepository.save(user);

        // when
        RegistrationResponse response = om.readValue(
                mockMvc.perform(post("/register")
                                .contentType("application/json")
                                .content(om.writeValueAsString(reg)))
                        .andDo(print())
                        .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString(), RegistrationResponse.class);

        // then
        assertFalse(response.getValidation().isValid());
        assertFalse(response.getValidation().getValidationMessages().isEmpty());
        assertEquals("username [theone] already exists", response.getValidation().getValidationMessages().stream().findFirst().get());
    }

    @Test
    void given_validRegistration_when_PostedToRegisterEndpointAndExceptionOccurs_then_500_ApiErrorResponse() throws Exception {
        // given
        File file = resourceLoader.getResource("classpath:json/registration_valid.json").getFile();
        RegistrationRequest registrationRequest = om.readValue(file, RegistrationRequest.class);

        when(customerService.register(any(RegistrationRequest.class))).thenThrow(new RuntimeException("Error"));

        // when
        ApiErrorResponse response = om.readValue(
                mockMvc.perform(post("/register")
                                .contentType("application/json")
                                .content(om.writeValueAsString(registrationRequest)))
                        .andDo(print())
                        .andExpect(status().isInternalServerError()).andReturn().getResponse().getContentAsString(), ApiErrorResponse.class);

        // then
        assertFalse(response.getValidation().isValid());
        assertEquals("Error", response.getValidation().getValidationMessages().stream().findFirst().get());
    }

    @Test
    void given_invalidRegistrationDateOfBirth_when_PostedToRegisterEndpoint_then_400_ApiErrorResponse() throws Exception {
        // given
        File file = resourceLoader.getResource("classpath:json/registration_valid.json").getFile();
        RegistrationRequest registrationRequest = om.readValue(file, RegistrationRequest.class);
        String payload = om.writeValueAsString(registrationRequest);
        payload = payload.replace("1964-09-02", "02-09-1964");

        // when
        ApiErrorResponse response = om.readValue(
                mockMvc.perform(post("/register")
                                .contentType("application/json")
                                .content(payload))
                        .andDo(print())
                        .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString(), ApiErrorResponse.class);

        // then
        assertFalse(response.getValidation().isValid());
        assertEquals(
                "JSON parse error: Cannot deserialize value of type `java.time.LocalDate` from String \"02-09-1964\": Failed to deserialize java.time.LocalDate: (java.time.format.DateTimeParseException) Text '02-09-1964' could not be parsed at index 0",
                response.getValidation().getValidationMessages().stream().findFirst().get());
    }

    @Test
    void given_validLogonAnd_when_PostedToLogonEndpoint_then_200_AuthorizedWithJwtToken() throws Exception {
        // given
        File regFile = resourceLoader.getResource("classpath:json/registration_valid.json").getFile();
        RegistrationRequest registrationRequest = om.readValue(regFile, RegistrationRequest.class);

        // customer registered
        RegistrationResponse regResponse = om.readValue(
                mockMvc.perform(post("/register")
                                .contentType("application/json")
                                .content(om.writeValueAsString(registrationRequest)))
                        .andDo(print())
                        .andExpect(jsonPath("$.password", notNullValue()))
                        .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), RegistrationResponse.class);

        // logon request with password from registration regResponse
        File logOnFile = resourceLoader.getResource("classpath:json/logon_valid.json").getFile();
        LogOnRequest logOnRequest = om.readValue(logOnFile, LogOnRequest.class);
        logOnRequest.setPassword(regResponse.getPassword());

        // when
        LogOnResponse logOnResponse = om.readValue(
                mockMvc.perform(post("/logon")
                                .contentType("application/json")
                                .content(om.writeValueAsString(logOnRequest)))
                        .andDo(print())
                        .andExpect(jsonPath("$.token", notNullValue()))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), LogOnResponse.class);

        // then
        assertFalse(logOnResponse.getToken().isEmpty());
    }

    @Test
    void given_invalidLogonEmptyPassWord_when_PostedToLogonEndpoint_then_401_UnauthorizedWithValidationError() throws Exception {
        // given
        File logOnFile = resourceLoader.getResource("classpath:json/logon_invalid_empty_password.json").getFile();
        LogOnRequest logOnRequest = om.readValue(logOnFile, LogOnRequest.class);

        // when
        LogOnResponse logOnResponse = om.readValue(
                mockMvc.perform(post("/logon")
                                .contentType("application/json")
                                .content(om.writeValueAsString(logOnRequest)))
                        .andDo(print())
                        .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString(), LogOnResponse.class);

        // then
        assertFalse(logOnResponse.getValidation().isValid());
        assertEquals("$.password: is missing but it is required", logOnResponse.getValidation().getValidationMessages().stream().findFirst().get());
    }

    /**
     * Happy Path
     *
     * @throws Exception
     */
    @Test
    void given_validCustomerRegisteredAndValidLogon_when_PostedToOverviewEndpoint_then_200_OverviewResponse() throws Exception {
        // given
        LogOnResponse logOnResponse = registerAndLogOn();

        // when - get from /overview with JWT Token in Authorization header
        OverviewResponse response = om.readValue(
                mockMvc.perform(get("/overview")
                                .contentType("application/json")
                                .header("authorization", "Bearer " + logOnResponse.getToken())
                        )
                        .andDo(print())
                        .andExpect(jsonPath("$.iban", notNullValue()))
                        .andExpect(jsonPath("$.accountBalance", notNullValue()))
                        .andExpect(jsonPath("$.accountType", notNullValue()))
                        .andExpect(jsonPath("$.currency", notNullValue()))
                        .andExpect(jsonPath("$.openingDate", notNullValue()))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), OverviewResponse.class);

        // then
        assertFalse(response.getIban().isEmpty());
    }

    /**
     * Register & Logon Successfully but no account persisted for username
     *
     * @throws Exception
     */
    @Test
    void given_validCustomerRegisteredAndValidLogon_when_PostedToOverviewEndpointAndNoAccountForUserPersisted_then_401_OverviewResponse() throws Exception {
        // given
        LogOnResponse logOnResponse = registerAndLogOn();
        when(accountRepository.findByUsername(anyString())).thenReturn(null);

        // when - get from /overview with JWT Token in Authorization header
        OverviewResponse response = om.readValue(
                mockMvc.perform(get("/overview")
                                .contentType("application/json")
                                .header("authorization", "Bearer " + logOnResponse.getToken())
                        )
                        .andDo(print())
                        .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString(), OverviewResponse.class);

        // then
        assertFalse(response.getValidation().isValid());
    }

    private LogOnResponse registerAndLogOn() throws Exception {
        File regFile = resourceLoader.getResource("classpath:json/registration_valid.json").getFile();
        RegistrationRequest registrationRequest = om.readValue(regFile, RegistrationRequest.class);

        // post to /register
        RegistrationResponse regResponse = om.readValue(
                mockMvc.perform(post("/register")
                                .contentType("application/json")
                                .content(om.writeValueAsString(registrationRequest)))
                        .andDo(print())
                        .andExpect(jsonPath("$.password", notNullValue()))
                        .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), RegistrationResponse.class);

        // logon request with password from registration regResponse
        File logOnFile = resourceLoader.getResource("classpath:json/logon_valid.json").getFile();
        LogOnRequest logOnRequest = om.readValue(logOnFile, LogOnRequest.class);
        logOnRequest.setPassword(regResponse.getPassword());

        // post to /logon
        LogOnResponse logOnResponse = om.readValue(
                mockMvc.perform(post("/logon")
                                .contentType("application/json")
                                .content(om.writeValueAsString(logOnRequest)))
                        .andDo(print())
                        .andExpect(jsonPath("$.token", notNullValue()))
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), LogOnResponse.class);

        Thread.sleep(1000);// wait 1s for RateLimiter

        return logOnResponse;
    }

    @Test
    void given_invalidOverviewRequest_when_PostedToOverviewEndpoint_then_401_Unauthorized() throws Exception {
        // given
        String invalidJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGVvbmUiLCJpYXQiOjE2OTUyMzM4NzMsImV4cCI6MTY5NTI0NDY3M30.F84rQJIKp6uwjOpFkEK-rD9GzOaajmCRBJqTpFjMKnU";

        // when - get from /overview with JWT Token in Authorization header
        OverviewResponse overviewResponse = om.readValue(
                mockMvc.perform(get("/overview")
                                .contentType("application/json")
                                .header("authorization", "Bearer " + invalidJwtToken)
                        )
                        .andDo(print())
                        .andExpect(jsonPath("$.validation.valid", notNullValue()))
                        .andExpect(jsonPath("$.validation.validationMessages", notNullValue()))
                        .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString(), OverviewResponse.class);

        // then
        assertEquals("Full authentication is required to access this resource", overviewResponse.getValidation().getValidationMessages().stream().findFirst().get());
    }

    /**
     * Test rate limit of 2 times per second by receiving 429 on a fourth request to /register
     *
     * @throws Exception
     */
    @Test
    void given_validRegistration_when_PostedToRegisterEndpointFourTimes_then_FirstCall201_SecondCall400_FourthCall429() throws Exception {
        // given
        File regFile = resourceLoader.getResource("classpath:json/registration_valid.json").getFile();
        RegistrationRequest registrationRequest = om.readValue(regFile, RegistrationRequest.class);

        // call /register 4 times
        for (int i = 0; i < 4; i++) {
            if (i == 2) { // skip assertion of 3rd call
                mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType("application/json")
                        .content(om.writeValueAsString(registrationRequest)));
            } else {
                mockMvc.perform(MockMvcRequestBuilders.post("/register")
                                .contentType("application/json")
                                .content(om.writeValueAsString(registrationRequest)))
                        .andExpect(getStatusMatcher(i));
            }
        }
    }

    private static ResultMatcher getStatusMatcher(int i) {
        return switch (i) {
            case 0 -> status().isCreated();
            case 1 -> status().isBadRequest();
            default -> status().isTooManyRequests();
        };
    }
}
