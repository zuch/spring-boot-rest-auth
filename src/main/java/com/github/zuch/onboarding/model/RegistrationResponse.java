package com.github.zuch.onboarding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {

    private String name;
    private Address address;
    private LocalDate dateOfBirth;
    private IdDocument idDocument;
    private String username;
    private Validation validation;
    private String password;
}
