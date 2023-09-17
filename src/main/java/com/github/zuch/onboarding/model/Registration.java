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
public class Registration {

    private String name;
    private String surName;
    private Address address;
    private LocalDate dateOfBirth;
    private IdDocument idDocument;
    private String username;
}
