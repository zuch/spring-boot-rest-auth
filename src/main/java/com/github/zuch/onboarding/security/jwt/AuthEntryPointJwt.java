package com.github.zuch.onboarding.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zuch.onboarding.model.Validation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    @Override
    public void commence(
            final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException {
        final String authExceptionMessage = authException.getMessage();

        log.error("Unauthorized error: [{}]", authExceptionMessage);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Validation validation = Validation.builder()
                .valid(false)
                .validationMessages(Collections.singleton(authExceptionMessage))
                .build();

        final Map<String, Object> body = new HashMap<>();
        body.put("validation", validation);

        mapper.writeValue(response.getOutputStream(), body);
    }
}