package com.github.zuch.onboarding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zuch.onboarding.mapper.CustomerMapper;
import com.github.zuch.onboarding.model.request.Registration;
import com.github.zuch.onboarding.model.response.RegistrationResponse;
import com.github.zuch.onboarding.persistence.AccountRepository;
import com.github.zuch.onboarding.persistence.entity.AccountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private AccountRepository accountRepository;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        accountRepository.deleteAll();
    }

    @Test
    void given_validRegistration_when_PostedToRegisterEndpoint_then_201Persisted() throws Exception {
        // given
        File file = resourceLoader.getResource("classpath:json/registration_valid.json").getFile();
        Registration registration = om.readValue(file, Registration.class);

        // when
        RegistrationResponse response = om.readValue(
                mockMvc.perform(post("/register")
                                .contentType("application/json")
                                .content(om.writeValueAsString(registration)))
                        .andDo(print())
                        .andExpect(jsonPath("$.iban", notNullValue()))
                        .andExpect(jsonPath("$.password", notNullValue()))
                        .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), RegistrationResponse.class);

        // then
        assertTrue(response.getValidation().isValid());
    }

    @Test
    void given_invalidRegistration_when_PostedToRegisterAndUserAlreadyPersisted_then_400ValidationError() throws Exception {
        // given
        File file = resourceLoader.getResource("classpath:json/registration_valid.json").getFile();
        Registration registration = om.readValue(file, Registration.class);

        // persist same customer already
        AccountEntity accountEntity = customerMapper.mapToAccountEntity(registration);
        accountRepository.save(accountEntity);

        // when
        RegistrationResponse response = om.readValue(
                mockMvc.perform(post("/register")
                                .contentType("application/json")
                                .content(om.writeValueAsString(registration)))
                        .andDo(print())
                        .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString(), RegistrationResponse.class);

        // then
        assertFalse(response.getValidation().isValid());
        assertFalse(response.getValidation().getValidationMessages().isEmpty());
        assertEquals("username [theone] already exists", response.getValidation().getValidationMessages().stream().findFirst().get());
    }
}
