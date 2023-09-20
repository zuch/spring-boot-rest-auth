package com.github.zuch.onboarding.persistence.entity;

import com.github.zuch.onboarding.model.AccountType;
import com.github.zuch.onboarding.model.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DAO object for an account of a customer
 */
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(name= "acc_iban_idx", columnNames = "iban"),
                @UniqueConstraint(name= "acc_username_idx", columnNames = "username")
        })
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acc_gen")
    @SequenceGenerator(name = "acc_gen", sequenceName = "acc_seq")
    private Long id;

    @NotBlank
    @Size(max = 30)
    private String iban;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    private BigDecimal accountBalance;

    @NotBlank
    @Size(max = 10)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotBlank
    @Size(max = 3)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @NotBlank
    private LocalDateTime openingDate;

    @OneToOne(mappedBy = "account")
    private User user;
}
