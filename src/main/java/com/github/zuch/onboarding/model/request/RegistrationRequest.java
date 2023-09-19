package com.github.zuch.onboarding.model.request;

import com.github.zuch.onboarding.model.Address;
import com.github.zuch.onboarding.model.IdDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    private String name;
    private String surname;
    private Address address;
    private LocalDate dateOfBirth;
    private IdDocument idDocument;
    private String username;
}
