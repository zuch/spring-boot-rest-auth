package com.github.zuch.onboarding.mapper;

import com.github.zuch.onboarding.extractor.RegistrationExtractor;
import com.github.zuch.onboarding.model.AccountType;
import com.github.zuch.onboarding.model.Address;
import com.github.zuch.onboarding.model.IdDocument;
import com.github.zuch.onboarding.model.request.Registration;
import com.github.zuch.onboarding.model.response.RegistrationResponse;
import com.github.zuch.onboarding.model.Validation;
import com.github.zuch.onboarding.persistence.entity.AccountEntity;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Component
public class CustomerMapper {

    public AccountEntity mapToAccountEntity(final Registration registration) {
        return AccountEntity.builder()
                .iban(generateIBAN(registration))
                .name(RegistrationExtractor.name(registration))
                .surname(RegistrationExtractor.surname(registration))
                .addressStreet(RegistrationExtractor.addressStreet(registration))
                .addressHouseNumber(RegistrationExtractor.addressHouseNumber(registration))
                .addressPostCode(RegistrationExtractor.addressPostCode(registration))
                .addressCity(RegistrationExtractor.addressCity(registration))
                .dateOfBirth(RegistrationExtractor.dateOfBirth(registration))
                .idDocumentType(RegistrationExtractor.idType(registration))
                .idDocumentNumber(RegistrationExtractor.idNumber(registration))
                .idDocumentCountryCode(RegistrationExtractor.idCountryCode(registration))
                .idDocumentIssueDate(RegistrationExtractor.issueDate(registration))
                .idDocumentExpiryDate(RegistrationExtractor.idExpiryDate(registration))
                .username(RegistrationExtractor.username(registration))
                .accountBalance(generateAccountBalance())
                .typeOfAccount(generateTypeOfAccount())
                .build();
    }

    public RegistrationResponse mapToRegResponse(final AccountEntity accountEntity, final Validation validation) {
        return RegistrationResponse.builder()
                .name(accountEntity.getName())
                .username(accountEntity.getUsername())
                .address(
                        Address.builder()
                                .street(accountEntity.getAddressStreet())
                                .houseNumber(accountEntity.getAddressHouseNumber())
                                .postCode(accountEntity.getAddressPostCode())
                                .city(accountEntity.getAddressCity())
                                .build()
                )
                .dateOfBirth(accountEntity.getDateOfBirth())
                .idDocument(
                        IdDocument.builder()
                                .type(accountEntity.getIdDocumentType())
                                .idNumber(accountEntity.getIdDocumentNumber())
                                .countryCode(accountEntity.getIdDocumentCountryCode())
                                .issueDate(accountEntity.getIdDocumentIssueDate())
                                .expiryDate(accountEntity.getIdDocumentExpiryDate())
                                .build()
                )
                .validation(validation)
                .username(accountEntity.getUsername())
                .iban(accountEntity.getIban())
                .password(accountEntity.getPassword())
                .build();
    }

    public RegistrationResponse mapToRegResponseException(final Registration registration, final Validation validation) {
        return RegistrationResponse.builder()
                .name(RegistrationExtractor.name(registration))
                .username(RegistrationExtractor.username(registration))
                .address(
                        Address.builder()
                                .street(RegistrationExtractor.addressStreet(registration))
                                .houseNumber(RegistrationExtractor.addressHouseNumber(registration))
                                .postCode(RegistrationExtractor.addressPostCode(registration))
                                .city(RegistrationExtractor.addressCity(registration))
                                .build()
                )
                .dateOfBirth(RegistrationExtractor.dateOfBirth(registration))
                .idDocument(
                        IdDocument.builder()
                                .type(RegistrationExtractor.idType(registration))
                                .idNumber(RegistrationExtractor.idNumber(registration))
                                .countryCode(RegistrationExtractor.idCountryCode(registration))
                                .issueDate(RegistrationExtractor.issueDate(registration))
                                .expiryDate(RegistrationExtractor.idExpiryDate(registration))
                                .build()
                )
                .validation(validation)
                .username(RegistrationExtractor.username(registration))
                .build();
    }

    private String generateIBAN(final Registration registration) {
        final String countryCode = RegistrationExtractor.idCountryCode(registration);
        final Iban iban = switch (countryCode) {
            case "NL" -> Iban.random(CountryCode.NL);
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
