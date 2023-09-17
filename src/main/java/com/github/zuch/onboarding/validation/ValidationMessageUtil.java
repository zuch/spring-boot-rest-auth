package com.github.zuch.onboarding.validation;

import com.github.zuch.onboarding.model.Validation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ValidationMessageUtil {

    public Validation createValidationFailed(final String message) {
        final Set<String> messages = new HashSet<>(Collections.singletonList(message));
        return Validation.builder()
                .valid(false)
                .validationMessages(messages)
                .build();
    }

    public Validation createValidationFailed(final List<String> validationMessages) {
        final Set<String> messages = new HashSet<>(validationMessages);
        return Validation.builder()
                .valid(false)
                .validationMessages(messages)
                .build();
    }
}
