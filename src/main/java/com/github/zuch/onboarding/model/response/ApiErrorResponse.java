package com.github.zuch.onboarding.model.response;

import com.github.zuch.onboarding.model.Validation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiErrorResponse {
    private Validation validation;
}
