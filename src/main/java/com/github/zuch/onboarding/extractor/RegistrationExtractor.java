package com.github.zuch.onboarding.extractor;

import com.github.zuch.onboarding.model.Address;
import com.github.zuch.onboarding.model.IdDocument;
import com.github.zuch.onboarding.model.IdType;
import com.github.zuch.onboarding.model.request.RegistrationRequest;

import java.time.LocalDate;
import java.util.Optional;

public class RegistrationExtractor {

    private RegistrationExtractor() {
    }

    // ----------------- extractors -----------------

    public static String name(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getName).orElse("");
    }

    public static String surname(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getSurname).orElse("");
    }

    public static String addressStreet(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getAddress).map(Address::getStreet).orElse("");
    }

    public static String addressHouseNumber(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getAddress).map(Address::getHouseNumber).orElse("");
    }

    public static String addressPostCode(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getAddress).map(Address::getPostCode).orElse("");
    }

    public static String addressCity(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getAddress).map(Address::getCity).orElse("");
    }

    public static LocalDate dateOfBirth(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getDateOfBirth).orElseThrow();
    }

    public static IdType idType(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getIdDocument).map(IdDocument::getType).orElseThrow();
    }

    public static Integer idNumber(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getIdDocument).map(IdDocument::getIdNumber).orElse(0);
    }

    public static String idCountryCode(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getIdDocument).map(IdDocument::getCountryCode).orElse("");
    }

    public static LocalDate issueDate(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getIdDocument).map(IdDocument::getIssueDate).orElseThrow();
    }

    public static LocalDate idExpiryDate(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getIdDocument).map(IdDocument::getExpiryDate).orElseThrow();
    }

    public static String username(final RegistrationRequest registrationRequest) {
        return Optional.of(registrationRequest).map(RegistrationRequest::getUsername).orElse("");
    }
}
