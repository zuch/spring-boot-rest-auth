package com.github.zuch.onboarding.model.response;

import com.github.zuch.onboarding.model.Address;
import com.github.zuch.onboarding.model.IdDocument;
import com.github.zuch.onboarding.model.Validation;
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
    private Validation validation;
    private String username;
    private String password;
}
