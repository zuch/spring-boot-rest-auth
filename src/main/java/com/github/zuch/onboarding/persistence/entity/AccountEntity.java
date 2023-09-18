package com.github.zuch.onboarding.persistence.entity;

import com.github.zuch.onboarding.model.AccountType;
import com.github.zuch.onboarding.model.IdType;
import jakarta.persistence.Column;
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
    //@Column(unique = true)
    private String iban;

    private String name;
    private String surname;


    @Column(name = "address_street")
    private String addressStreet;
    @Column(name = "address_house_number")
    private String addressHouseNumber;
    @Column(name = "address_post_code")
    private String addressPostCode;

    @Column(name = "address_city")
    private String addressCity;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "id_document_type")
    @Enumerated(EnumType.STRING)
    private IdType idDocumentType;
    @Column(name = "id_document_number")
    private int idDocumentNumber;
    @Column(name = "id_document_country_code")
    private String idDocumentCountryCode;
    @Column(name = "id_document_issue_date")
    private LocalDate idDocumentIssueDate;
    @Column(name = "id_document_expiry_date")
    private LocalDate idDocumentExpiryDate;

    private String username;
    private String password;

    @Column(name = "account_balance")
    private BigDecimal accountBalance;

    @Column(name = "type_of_account")
    @Enumerated(EnumType.STRING)
    private AccountType typeOfAccount;
}
