package com.github.zuch.onboarding.model.response;

import com.github.zuch.onboarding.model.Validation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogOnResponse {

    private String token;
    private Validation validation;
}
