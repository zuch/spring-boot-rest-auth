package com.github.zuch.onboarding.extractor;

import com.github.zuch.onboarding.model.Address;
import com.github.zuch.onboarding.model.IdDocument;
import com.github.zuch.onboarding.model.IdType;
import com.github.zuch.onboarding.model.request.Registration;

import java.time.LocalDate;
import java.util.Optional;

public class RegistrationExtractor {

    private RegistrationExtractor() {
    }

    // ----------------- extractors -----------------

    public static String name(final Registration registration) {
        return Optional.of(registration).map(Registration::getName).orElse("");
    }

    public static String surname(final Registration registration) {
        return Optional.of(registration).map(Registration::getSurname).orElse("");
    }

    public static String addressStreet(final Registration registration) {
        return Optional.of(registration).map(Registration::getAddress).map(Address::getStreet).orElse("");
    }

    public static String addressHouseNumber(final Registration registration) {
        return Optional.of(registration).map(Registration::getAddress).map(Address::getHouseNumber).orElse("");
    }

    public static String addressPostCode(final Registration registration) {
        return Optional.of(registration).map(Registration::getAddress).map(Address::getPostCode).orElse("");
    }

    public static String addressCity(final Registration registration) {
        return Optional.of(registration).map(Registration::getAddress).map(Address::getCity).orElse("");
    }

    public static LocalDate dateOfBirth(final Registration registration) {
        return Optional.of(registration).map(Registration::getDateOfBirth).orElseThrow();
    }

    public static IdType idType(final Registration registration) {
        return Optional.of(registration).map(Registration::getIdDocument).map(IdDocument::getType).orElseThrow();
    }

    public static Integer idNumber(final Registration registration) {
        return Optional.of(registration).map(Registration::getIdDocument).map(IdDocument::getIdNumber).orElse(0);
    }

    public static String idCountryCode(final Registration registration) {
        return Optional.of(registration).map(Registration::getIdDocument).map(IdDocument::getCountryCode).orElse("");
    }

    public static LocalDate issueDate(final Registration registration) {
        return Optional.of(registration).map(Registration::getIdDocument).map(IdDocument::getIssueDate).orElseThrow();
    }

    public static LocalDate idExpiryDate(final Registration registration) {
        return Optional.of(registration).map(Registration::getIdDocument).map(IdDocument::getExpiryDate).orElseThrow();
    }

    public static String username(final Registration registration) {
        return Optional.of(registration).map(Registration::getUsername).orElse("");
    }
}
