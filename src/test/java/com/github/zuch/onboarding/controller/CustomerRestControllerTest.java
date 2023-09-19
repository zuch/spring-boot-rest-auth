package com.github.zuch.onboarding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zuch.onboarding.mapper.CustomerMapper;
import com.github.zuch.onboarding.model.request.LogOnRequest;
import com.github.zuch.onboarding.model.request.RegistrationRequest;
import com.github.zuch.onboarding.model.response.LogOnResponse;
import com.github.zuch.onboarding.model.response.OverviewResponse;
import com.github.zuch.onboarding.model.response.RegistrationResponse;
import com.github.zuch.onboarding.persistence.RoleRepository;
import com.github.zuch.onboarding.persistence.UserRepository;
import com.github.zuch.onboarding.persistence.entity.Account;
import com.github.zuch.onboarding.persistence.entity.Role;
import com.github.zuch.onboarding.persistence.entity.Roles;
import com.github.zuch.onboarding.persistence.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
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
        assertTrue(response.getValidation().isValid());
    }

    @Test
    void given_invalidRegistration_when_PostedToRegisterAndUserAlreadyPersisted_then_400_ValidationError() throws Exception {
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
        assertTrue(logOnResponse.getValidation().isValid());
        assertFalse(logOnResponse.getToken().isEmpty());
    }

    @Test
    void given_CustomerRegisteredAndValidLogon_when_PostedToOverviewEndpoint_then_200_OverviewResponse() throws Exception {
        // given
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

        // get from /overview with JWT Token in Authorization header
        OverviewResponse overviewResponse = om.readValue(
                mockMvc.perform(get("/overview")
                                .contentType("application/json")
                                .header("authorization", "Bearer " + logOnResponse.getToken())
                        )
                        .andDo(print())
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), OverviewResponse.class);

        // then
        assertFalse(overviewResponse.getIban().isEmpty());
    }
}
