package com.github.zuch.onboarding.mapper;

import com.github.zuch.onboarding.extractor.RegistrationExtractor;
import com.github.zuch.onboarding.model.AccountType;
import com.github.zuch.onboarding.model.Address;
import com.github.zuch.onboarding.model.IdDocument;
import com.github.zuch.onboarding.model.Registration;
import com.github.zuch.onboarding.persistence.entity.AccountEntity;
import com.mifmif.common.regex.Generex;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CustomerMapper {

    private static final String IBAN_NL = "NL[0-9]{2}[A-Z]{4}[0-9]{10}";
    private static final String IBAN_BE = "BE[0-9]{2}[0-9]{3}[0-9]{7}[0-9]{2}";
    private static final String IBAN_DE = "DE[0-9]{2}[0-9]{8}[0-9]{10}";

    public AccountEntity mapToAccountEntity(final Registration registration) {
        return AccountEntity.builder()
                .iban(generateIBAN(registration))
                .name(RegistrationExtractor.extractName(registration))
                .surName(RegistrationExtractor.extractSurName(registration))
                .address(
                        Address.builder()
                                .street(RegistrationExtractor.extractAddressStreet(registration))
                                .houseNumber(RegistrationExtractor.extractAddressHouseNumber(registration))
                                .postCode(RegistrationExtractor.extractAddressPostCode(registration))
                                .city(RegistrationExtractor.extractAddressCity(registration))
                                .build()
                )
                .dateOfBirth(RegistrationExtractor.extractDateOfBirth(registration))
                .idDocument(
                        IdDocument.builder()
                                .type(RegistrationExtractor.extractIdType(registration))
                                .countryCode(RegistrationExtractor.extractIdCountryCode(registration))
                                .issueDate(RegistrationExtractor.extractIdIssueDate(registration))
                                .expiryDate(RegistrationExtractor.extractIdExpiryDate(registration))
                                .build()
                )
                .username(RegistrationExtractor.extractUsername(registration))
                .accountBalance(generateAccountBalance())
                .typeOfAccount(generateTypeOfAccount())
                .build();
    }

    private String generateIBAN(final Registration registration) {
        final String countryCode = RegistrationExtractor.extractIdCountryCode(registration);
        final Generex generex = switch (countryCode) {
            case "NL" -> new Generex(IBAN_NL);
            case "BE" -> new Generex(IBAN_BE);
            case "DE" -> new Generex(IBAN_DE);
            default -> new Generex(IBAN_NL);
        };
        return generex.random();
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

}
