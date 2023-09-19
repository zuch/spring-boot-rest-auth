package com.github.zuch.onboarding.model.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfigProperties {

    private String jwtSecret;
    private long jwtExpirationMs;
    private List<String> countryAllowedList = new ArrayList<>();
}
