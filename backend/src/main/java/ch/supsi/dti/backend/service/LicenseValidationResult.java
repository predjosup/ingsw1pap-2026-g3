package ch.supsi.dti.backend.service;

public record LicenseValidationResult(boolean valid, String messageKey, String detail) {
}
