package com.github.zuch.onboarding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private String name;
    private String address;
    private LocalDate dateOfBirth;
    private String idDocument;
    private String username;
    private String password;
}
