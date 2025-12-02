package com.medicconnect.utils;

import java.util.regex.Pattern;
import com.medicconnect.exceptions.BadRequestException;

public final class ValidationUtils {

    private ValidationUtils() {} // Prevent instantiation

    // ---------------- EMAIL ----------------
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email cannot be empty");
        }
        if (!isValidEmail(email)) {
            throw new BadRequestException("Invalid email format: " + email);
        }
    }

    // ---------------- UTILITY ----------------
    // Add other generic utilities here if needed
}
