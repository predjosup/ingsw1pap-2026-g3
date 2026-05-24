package ch.supsi.dti.backend.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class LicenseService {

    private static final String VALID_TOKEN = "LICENZA_VALIDA";
    private static final String INVALID_TOKEN = "LICENZA_NON_VALIDA";

    public String readLicenseCode(Path licenseFile) throws IOException {
        if (licenseFile == null || !Files.exists(licenseFile)) {
            throw new IOException("license file not found");
        }

        String content = Files.readString(licenseFile, StandardCharsets.UTF_8);
        for (String line : content.split("\\R")) {
            String normalized = line.trim();
            if (!normalized.isEmpty()) {
                return normalized;
            }
        }
        return "";
    }

    public LicenseValidationResult validate(Path validatorExecutable, Path licenseFile, Duration timeout) {
        try {
            String licenseCode = readLicenseCode(licenseFile);
            return validateCode(validatorExecutable, licenseCode, timeout);
        } catch (IOException e) {
            return new LicenseValidationResult(false, "license.status.fileReadError", licenseFile == null ? "" : licenseFile.toString());
        }
    }

    public LicenseValidationResult validateCode(Path validatorExecutable, String licenseCode, Duration timeout) {
        if (validatorExecutable == null || !Files.exists(validatorExecutable)) {
            return new LicenseValidationResult(false, "license.status.validatorMissing", validatorExecutable == null ? "" : validatorExecutable.toString());
        }

        String normalizedCode = licenseCode == null ? "" : licenseCode.trim();
        if (normalizedCode.isEmpty()) {
            return new LicenseValidationResult(false, "license.status.codeRequired", null);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                validatorExecutable.toAbsolutePath().toString(),
                normalizedCode
        );
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            boolean completed = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!completed) {
                process.destroyForcibly();
                return new LicenseValidationResult(false, "license.status.timeout", null);
            }

            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            int exitCode = process.exitValue();
            if (exitCode == 0 && output.startsWith(VALID_TOKEN)) {
                return new LicenseValidationResult(true, "license.status.valid", null);
            }
            if (output.startsWith(INVALID_TOKEN)) {
                return new LicenseValidationResult(false, "license.status.invalid", null);
            }
            return new LicenseValidationResult(false, "license.status.error", null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new LicenseValidationResult(false, "license.status.error", null);
        } catch (IOException e) {
            return new LicenseValidationResult(false, "license.status.error", null);
        }
    }
}
