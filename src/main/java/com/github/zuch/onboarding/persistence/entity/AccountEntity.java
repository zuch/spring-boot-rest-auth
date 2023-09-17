package com.github.zuch.onboarding.persistence.entity;

import com.github.zuch.onboarding.model.AccountType;
import com.github.zuch.onboarding.model.Address;
import com.github.zuch.onboarding.model.IdDocument;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DAO object for an account of a customer
 */
@Getter
@Setter
@Builder
@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @Column(unique = true)
    private String iban;

    private String name;
    private String surName;

    @Embedded
    private Address address;
    private LocalDate dateOfBirth;

    @Embedded
    private IdDocument idDocument;
    private String username;
    private BigDecimal accountBalance;

    @Enumerated(EnumType.STRING)
    private AccountType typeOfAccount;
}
