package com.github.zuch.onboarding.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    //TODO consider IBAN as Primary Key
    @Id
    @GeneratedValue
    private String iban;

    private String name;
    private String address;
    private LocalDate dateOfBirth;
    private String idDocument;
    private String username;
    private String accountBalance;
    private String typeOfAccount;
}
