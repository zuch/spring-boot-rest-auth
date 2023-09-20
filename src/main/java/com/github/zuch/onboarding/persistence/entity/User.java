package com.github.zuch.onboarding.persistence.entity;

import com.github.zuch.onboarding.model.IdType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

import java.time.LocalDate;
import java.util.Set;

/**
 * DAO object representing a customer
 */
@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_username_idx", columnNames = "username")
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_gen")
    @SequenceGenerator(name = "user_gen", sequenceName = "user_seq")
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 50)
    private String surname;

    @NotBlank
    @Size(max = 50)
    private String addressStreet;

    @NotBlank
    @Size(max = 10)
    private String addressHouseNumber;

    @NotBlank
    @Size(max = 10)
    private String addressPostCode;

    @NotBlank
    @Size(max = 50)
    private String addressCity;

    @NotBlank
    private LocalDate dateOfBirth;

    @NotBlank
    @Size(max = 15)
    @Enumerated(EnumType.STRING)
    private IdType idDocumentType;

    @NotBlank
    private int idDocumentNumber;

    @NotBlank
    @Size(max = 50)
    private String idDocumentCountryCode;

    @NotBlank
    private LocalDate idDocumentIssueDate;

    @NotBlank
    private LocalDate idDocumentExpiryDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
}
