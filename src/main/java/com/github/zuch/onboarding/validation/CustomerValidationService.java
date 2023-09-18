package com.github.zuch.onboarding.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zuch.onboarding.extractor.RegistrationExtractor;
import com.github.zuch.onboarding.model.request.Registration;
import com.github.zuch.onboarding.model.Validation;
import com.github.zuch.onboarding.model.config.AppConfigProperties;
import com.github.zuch.onboarding.persistence.AccountRepository;
import com.github.zuch.onboarding.persistence.entity.AccountEntity;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerValidationService {

    private static final String REGISTRATION_VALIDATION_SCHEMA_PATH = "validation/RegistrationSchema.json";

    private final ObjectMapper objectMapper;
    private final AccountRepository accountRepository;
    private final AppConfigProperties appConfigProperties;
    private final ValidationMessageUtil validationMessageUtil;

    private final Map<String, JsonSchema> schemaCache;

    /**
     * Validate a customer registration payload with JSON Schema validation first then check custom validation rules
     *
     * @param registration payload
     * @return a Validation object
     * - set to valid
     * - or not valid with validation error messages
     * @throws JsonProcessingException exception reading path to Schema Rules json file
     */
    public Validation validateReg(final Registration registration) throws JsonProcessingException {
        // JSON Schema Validation
        final Set<ValidationMessage> validationResult = validateRegistrationJsonStructure(registration);
        if (!validationResult.isEmpty()) { //JSON Schema Validation
            final List<String> validationMessages = validationResult.stream().map(ValidationMessage::getMessage).toList();
            return validationMessageUtil.createValidationFailed(validationMessages);
        } else { //check Custom validation rules
            final List<String> customValidationMessages = new ArrayList<>();

            isValidCountyCode(registration, customValidationMessages);
            isValidDateOfBirth(registration, customValidationMessages);
            isValidUserName(registration, customValidationMessages);

            if (!customValidationMessages.isEmpty()) { //custom validation error found
                return validationMessageUtil.createValidationFailed(customValidationMessages);
            } else { // valid - no validation errors found
                return Validation.builder().valid(true).build();
            }
        }
    }

    // ----------------- json schema validation -----------------

    /**
     * Returns a set of JSON Schema Validation messages found by checking a Registration against a Schema Rules json file
     *
     * @param registration payload
     * @return set of JSON Schema Validation messages
     * @throws JsonProcessingException exception reading path to Schema Rules json file
     */
    private Set<ValidationMessage> validateRegistrationJsonStructure(final Registration registration) throws JsonProcessingException {
        // get JsonSchema from schemaPath
        final JsonSchema schema = getJsonSchema(REGISTRATION_VALIDATION_SCHEMA_PATH);
        // parse json payload to String
        final String registrationString = objectMapper.writeValueAsString(registration);
        // Do actual validation
        final JsonNode json = objectMapper.readTree(registrationString);
        return schema.validate(json);
    }

    /**
     * Returns a JsonSchema object for a given path to a Schema Rules json file
     *
     * @param schemaPath path to Schema Rules json file
     * @return JsonSchema object
     */
    private JsonSchema getJsonSchema(final String schemaPath) {
        return schemaCache.computeIfAbsent(schemaPath, path -> {
            final ClassPathResource res = new ClassPathResource(schemaPath);
            final JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
            try (InputStream schemaStream = res.getInputStream()) {
                return schemaFactory.getSchema(schemaStream);
            } catch (final Exception e) {
                throw new RuntimeException("An error occurred while loading JSON Schema, path: " + path, e);
            }
        });
    }

    // ----------------- custom validation rules -----------------

    /**
     * Any customer outside The Netherlands, Belgium and Germany must not be able to register and create an account.
     * Custom validation will check countyCode passed in Registration payload against a configured allowed county list
     *
     * @param registration             payload
     * @param customValidationMessages
     */
    public void isValidCountyCode(final Registration registration, final List<String> customValidationMessages) {
        final List<String> countryAllowedList = appConfigProperties.getCountryAllowedList();
        final String countryCode = RegistrationExtractor.idCountryCode(registration);
        if (countryCode.isEmpty() || countryAllowedList.stream().noneMatch(countryCode::contains)) {
            final String validationMessage = String.format("CountyCode [%s] is not valid or one of the allowed countries", countryCode);
            customValidationMessages.add(validationMessage);
        }
    }

    /**
     * Customer must provide a unique username. If a username already exists, they should receive an error.
     *
     * @param registration             payload
     * @param customValidationMessages
     */
    public void isValidDateOfBirth(final Registration registration, final List<String> customValidationMessages) {
        final LocalDate dateOfBirth = RegistrationExtractor.dateOfBirth(registration);
        final LocalDate eighteenYearsInPast = LocalDate.now().minusYears(18);
        if (dateOfBirth.isAfter(eighteenYearsInPast)) { // < 18 years
            final String validationMessage = "Customer must be older than 18 years old to register for an account";
            customValidationMessages.add(validationMessage);
        }
    }

    /**
     * Customers above 18 years old are only allowed to register and create an account
     *
     * @param registration             payload
     * @param customValidationMessages
     */
    public void isValidUserName(final Registration registration, final List<String> customValidationMessages) {
        final String username = RegistrationExtractor.username(registration);
        final AccountEntity account = accountRepository.findByUsername(username);
        if (username.isEmpty() || account != null) {
            final String validationMessage = String.format("username [%s] already exists", username);
            customValidationMessages.add(validationMessage);
        }
    }
}
