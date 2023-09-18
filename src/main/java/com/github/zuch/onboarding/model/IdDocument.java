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
public class IdDocument {

    private IdType type;
    private int idNumber;
    private String countryCode;
    private LocalDate issueDate;
    private LocalDate expiryDate;
}
