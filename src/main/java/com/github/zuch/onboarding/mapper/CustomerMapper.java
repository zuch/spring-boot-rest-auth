package com.github.zuch.onboarding.mapper;

import com.github.zuch.onboarding.extractor.RegistrationExtractor;
import com.github.zuch.onboarding.model.AccountType;
import com.github.zuch.onboarding.model.Address;
import com.github.zuch.onboarding.model.Currency;
import com.github.zuch.onboarding.model.IdDocument;
import com.github.zuch.onboarding.model.request.RegistrationRequest;
import com.github.zuch.onboarding.model.response.LogOnResponse;
import com.github.zuch.onboarding.model.response.OverviewResponse;
import com.github.zuch.onboarding.model.response.RegistrationResponse;
import com.github.zuch.onboarding.model.Validation;
import com.github.zuch.onboarding.persistence.entity.Account;
import com.github.zuch.onboarding.persistence.entity.Role;
import com.github.zuch.onboarding.persistence.entity.Roles;
import com.github.zuch.onboarding.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

    //------------------------- User -----------------------------
    public User mapToUser(final RegistrationRequest registrationRequest, final Role role, final Account account, final String password) {
        return User.builder()
                .username(RegistrationExtractor.username(registrationRequest))
                .password(password)
                .name(RegistrationExtractor.name(registrationRequest))
                .surname(RegistrationExtractor.surname(registrationRequest))
                .addressStreet(RegistrationExtractor.addressStreet(registrationRequest))
                .addressHouseNumber(RegistrationExtractor.addressHouseNumber(registrationRequest))
                .addressPostCode(RegistrationExtractor.addressPostCode(registrationRequest))
                .addressCity(RegistrationExtractor.addressCity(registrationRequest))
                .dateOfBirth(RegistrationExtractor.dateOfBirth(registrationRequest))
                .idDocumentType(RegistrationExtractor.idType(registrationRequest))
                .idDocumentNumber(RegistrationExtractor.idNumber(registrationRequest))
                .idDocumentCountryCode(RegistrationExtractor.idCountryCode(registrationRequest))
                .idDocumentIssueDate(RegistrationExtractor.issueDate(registrationRequest))
                .idDocumentExpiryDate(RegistrationExtractor.idExpiryDate(registrationRequest))
                .roles(Collections.singleton(role))
                .account(account)
                .build();
    }

    public Role mapToRole(final RegistrationRequest registrationRequest) {
        return Role.builder()
                .username(RegistrationExtractor.username(registrationRequest))
                .name(Roles.CUSTOMER)
                .build();
    }

    public Account mapToAccount(final RegistrationRequest registrationRequest) {
        return Account.builder()
                .iban(generateIBAN(registrationRequest))
                .username(RegistrationExtractor.username(registrationRequest))
                .accountBalance(generateAccountBalance())
                .typeOfAccount(generateTypeOfAccount())
                .currency(Currency.EUR)
                .openingDate(LocalDateTime.now())
                .build();
    }

    public RegistrationResponse mapToRegResponse(final User user, final Validation validation, final String password) {
        return RegistrationResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .address(
                        Address.builder()
                                .street(user.getAddressStreet())
                                .houseNumber(user.getAddressHouseNumber())
                                .postCode(user.getAddressPostCode())
                                .city(user.getAddressCity())
                                .build()
                )
                .dateOfBirth(user.getDateOfBirth())
                .idDocument(
                        IdDocument.builder()
                                .type(user.getIdDocumentType())
                                .idNumber(user.getIdDocumentNumber())
                                .countryCode(user.getIdDocumentCountryCode())
                                .issueDate(user.getIdDocumentIssueDate())
                                .expiryDate(user.getIdDocumentExpiryDate())
                                .build()
                )
                .validation(validation)
                .username(user.getUsername())
                .password(password)
                .build();
    }

    public RegistrationResponse mapToRegResponseException(final RegistrationRequest registrationRequest, final Validation validation) {
        return RegistrationResponse.builder()
                .name(RegistrationExtractor.name(registrationRequest))
                .username(RegistrationExtractor.username(registrationRequest))
                .address(
                        Address.builder()
                                .street(RegistrationExtractor.addressStreet(registrationRequest))
                                .houseNumber(RegistrationExtractor.addressHouseNumber(registrationRequest))
                                .postCode(RegistrationExtractor.addressPostCode(registrationRequest))
                                .city(RegistrationExtractor.addressCity(registrationRequest))
                                .build()
                )
                .dateOfBirth(RegistrationExtractor.dateOfBirth(registrationRequest))
                .idDocument(
                        IdDocument.builder()
                                .type(RegistrationExtractor.idType(registrationRequest))
                                .idNumber(RegistrationExtractor.idNumber(registrationRequest))
                                .countryCode(RegistrationExtractor.idCountryCode(registrationRequest))
                                .issueDate(RegistrationExtractor.issueDate(registrationRequest))
                                .expiryDate(RegistrationExtractor.idExpiryDate(registrationRequest))
                                .build()
                )
                .validation(validation)
                .username(RegistrationExtractor.username(registrationRequest))
                .build();
    }

    //------------------------- LogOn -----------------------------
    public LogOnResponse mapToLogOnResponse(
            final String jwt, final String username, final List<String> roles, final Validation validation) {
        return LogOnResponse.builder()
                .token(jwt)
                .username(username)
                .roles(roles)
                .validation(validation)
                .build();
    }

    public LogOnResponse mapToLogOnResponseException(final Validation validation) {
        return LogOnResponse.builder()
                .validation(validation)
                .build();
    }

    //------------------------- Overview -----------------------------

    public OverviewResponse mapToOverviewResponse(final Account account) {
        return OverviewResponse.builder()
                .iban(account.getIban())
                .accountBalance(account.getAccountBalance())
                .typeOfAccount(account.getTypeOfAccount())
                .currency(account.getCurrency())
                .openingDate(account.getOpeningDate())
                .validation(Validation.builder()
                        .valid(true)
                        .build())
                .build();
    }

    public OverviewResponse mapToOverviewResponseException(final Validation validation) {
        return OverviewResponse.builder()
                .validation(validation)
                .build();
    }


    //------------------------- generators -----------------------------
    private String generateIBAN(final RegistrationRequest registrationRequest) {
        final String countryCode = RegistrationExtractor.idCountryCode(registrationRequest);
        final Iban iban = switch (countryCode) {
            case "BE" -> Iban.random(CountryCode.BE);
            case "DE" -> Iban.random(CountryCode.DE);
            default -> Iban.random(CountryCode.NL);
        };
        return iban.toFormattedString();
    }

    private BigDecimal generateAccountBalance() {
        return generateRandomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(1000000.00));
    }

    public static BigDecimal generateRandomBigDecimal(BigDecimal min, BigDecimal max) {
        BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomBigDecimal.setScale(2, RoundingMode.HALF_UP);
    }

    private AccountType generateTypeOfAccount() {
        if ((int) Math.round(Math.random()) % 2 == 0) {
            return AccountType.CURRENT;
        } else {
            return AccountType.SAVINGS;
        }
    }

    public String generatePassword() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        final Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
