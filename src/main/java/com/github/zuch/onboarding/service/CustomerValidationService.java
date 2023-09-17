package com.github.zuch.onboarding.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zuch.onboarding.model.IdDocument;
import com.github.zuch.onboarding.model.Registration;
import com.github.zuch.onboarding.model.RegistrationResponse;
import com.github.zuch.onboarding.model.Validation;
import com.github.zuch.onboarding.model.config.AppConfigProperties;
import com.github.zuch.onboarding.util.ValidationMessageUtil;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerValidationService {
    private static final String REGISTRATION_VALIDATION_SCHEMA_PATH = "validation/RegistrationSchema.json";

    private final ObjectMapper objectMapper;
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
    public Validation validateRegistration(final Registration registration) throws JsonProcessingException {
        //final RegistrationResponse response = new RegistrationResponse();

        // JSON Schema Validation
        final Set<ValidationMessage> validationResult = validateRegistrationJsonStructure(registration);
        if (!validationResult.isEmpty()) { //json schema validation failed
            final List<String> validationMessages = validationResult.stream().map(ValidationMessage::getMessage).toList();
            return validationMessageUtil.createValidationFailed(validationMessages);
        } else { //check custom validation rules
            final List<String> customValidationMessages = new ArrayList<>();

            isValidCountyCode(registration, customValidationMessages);

            if (!customValidationMessages.isEmpty()) { //custom validation error found
                return validationMessageUtil.createValidationFailed(customValidationMessages);
            } else { // valid - no validation errors found
                return Validation.builder().valid(true).build();
            }
        }
    }

    /**
     * Returns a set of JSON Schema Validation messages found by checking a Registration against a Schema Rules json file
     *
     * @param registration payload
     * @return set of JSON Schema Validation messages
     * @throws JsonProcessingException exception reading path to Schema Rules json file
     */
    private Set<ValidationMessage> validateRegistrationJsonStructure(Registration registration) throws JsonProcessingException {
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
    private JsonSchema getJsonSchema(String schemaPath) {
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


    /**
     * Any customer outside The Netherlands, Belgium and Germany must not be able to register and create an account.
     * Custom validation will check countyCode passed in Registration payload against a configured allowed county list
     *
     * @param registration             payload
     * @param customValidationMessages
     */
    public void isValidCountyCode(final Registration registration, List<String> customValidationMessages) {
        final List<String> countryAllowedList = appConfigProperties.getCountryAllowedList();
        final String countryCode = extractCountryCode(registration);
        if (countryCode.isEmpty() || countryAllowedList.stream().noneMatch(countryCode::contains)) {
            final String validationMessage = String.format("CountyCode passed [%s] is not valid or one of the allowed countries", countryCode);
            customValidationMessages.add(validationMessage);
        }
    }

    // ----------------- extractors -----------------

    private String extractCountryCode(final Registration registration) {
        return Optional.of(registration).map(Registration::getIdDocument).map(IdDocument::getCountryCode).orElse("");
    }
}
