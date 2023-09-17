package com.github.zuch.onboarding.model.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfigProperties {

    private final List<String> countryAllowedList = new ArrayList<>();
}
