package com.github.zuch.onboarding.extractor;

import com.github.zuch.onboarding.model.Address;
import com.github.zuch.onboarding.model.IdDocument;
import com.github.zuch.onboarding.model.IdType;
import com.github.zuch.onboarding.model.Registration;

import java.time.LocalDate;
import java.util.Optional;

public class RegistrationExtractor {

    private RegistrationExtractor() {
    }

    // ----------------- extractors -----------------

    public static String extractName(final Registration registration) {
        return Optional.of(registration).map(Registration::getName).orElse("");
    }

    public static String extractSurName(final Registration registration) {
        return Optional.of(registration).map(Registration::getSurName).orElse("");
    }

    public static String extractAddressStreet(final Registration registration) {
        return Optional.of(registration).map(Registration::getAddress).map(Address::getStreet).orElse("");
    }

    public static String extractAddressHouseNumber(final Registration registration) {
        return Optional.of(registration).map(Registration::getAddress).map(Address::getHouseNumber).orElse("");
    }

    public static String extractAddressPostCode(final Registration registration) {
        return Optional.of(registration).map(Registration::getAddress).map(Address::getPostCode).orElse("");
    }

    public static String extractAddressCity(final Registration registration) {
        return Optional.of(registration).map(Registration::getAddress).map(Address::getCity).orElse("");
    }

    public static LocalDate extractDateOfBirth(final Registration registration) {
        return Optional.of(registration).map(Registration::getDateOfBirth).orElseThrow();
    }

    public static IdType extractIdType(final Registration registration) {
        return Optional.of(registration).map(Registration::getIdDocument).map(IdDocument::getType).orElseThrow();
    }

    public static LocalDate extractIdIssueDate(final Registration registration) {
        return Optional.of(registration).map(Registration::getIdDocument).map(IdDocument::getIssueDate).orElseThrow();
    }

    public static LocalDate extractIdExpiryDate(final Registration registration) {
        return Optional.of(registration).map(Registration::getIdDocument).map(IdDocument::getExpiryDate).orElseThrow();
    }

    public static String extractIdCountryCode(final Registration registration) {
        return Optional.of(registration).map(Registration::getIdDocument).map(IdDocument::getCountryCode).orElse("");
    }

    public static String extractUsername(final Registration registration) {
        return Optional.of(registration).map(Registration::getUsername).orElse("");
    }
}
