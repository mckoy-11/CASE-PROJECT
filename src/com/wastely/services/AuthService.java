package com.wastely.services;

import com.wastely.database.DatabaseManager;
import com.wastely.model.Account;
import com.wastely.models.User;

/**
 * Service responsible for authentication operations.
 * Bridges UI layer with database authentication services.
 */
public final class AuthService {

    /**
     * Minimum password length.
     */
    private static final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Email validation regex.
     */
    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /**
     * Private constructor to prevent instantiation.
     */
    private AuthService() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated."
        );
    }

    /*
     * =========================================================
     * Authentication
     * =========================================================
     */

    /**
     * Authenticates user credentials.
     *
     * @param email user email
     * @param password user password
     * @return authenticated user or null
     */
    public static User login(
            String email,
            String password
    ) {

        try {

            com.wastely.service.AuthService dbAuthService =
                    getDatabaseAuthService();

            Account account =
                    dbAuthService.login(email, password);

            if (account == null) {
                return null;
            }

            return mapAccountToUser(account);

        } catch (Exception ex) {

            logError(
                    "Error during login",
                    ex
            );

            return null;
        }
    }

    /*
     * =========================================================
     * Registration
     * =========================================================
     */

    /**
     * Registers a new account.
     *
     * @param name user full name
     * @param email user email
     * @param password user password
     * @param barangay barangay assignment
     * @return registration result message
     */
    public static String register(
            String name,
            String email,
            String password,
            String barangay
    ) {

        try {

            return getDatabaseAuthService()
                    .register(
                            name,
                            email,
                            password,
                            barangay
                    );

        } catch (Exception ex) {

            logError(
                    "Error during registration",
                    ex
            );

            return buildErrorMessage(ex);
        }
    }

    /**
     * Registers a new account with extended details.
     *
     * @param name user full name
     * @param email user email
     * @param password user password
     * @param age user age
     * @param gender user gender
     * @param barangay barangay assignment
     * @return registration result message
     */
    public static String register(
            String name,
            String email,
            String password,
            int age,
            String gender,
            String barangay
    ) {

        try {

            return getDatabaseAuthService()
                    .register(
                            name,
                            email,
                            password,
                            age,
                            gender,
                            barangay
                    );

        } catch (Exception ex) {

            logError(
                    "Error during registration",
                    ex
            );

            return buildErrorMessage(ex);
        }
    }

    /*
     * =========================================================
     * Password Reset
     * =========================================================
     */

    /**
     * Requests password reset.
     *
     * @param email user email
     * @return true if request accepted
     */
    public static boolean resetPassword(
            String email
    ) {

        System.out.println(
                "Password reset requested for: "
                        + email
        );

        return true;
    }

    /*
     * =========================================================
     * Validation
     * =========================================================
     */

    /**
     * Validates email format.
     *
     * @param email email value
     * @return true if valid
     */
    public static boolean isValidEmail(
            String email
    ) {

        return email != null
                && email.matches(EMAIL_REGEX);
    }

    /**
     * Validates password strength.
     *
     * @param password password value
     * @return true if valid
     */
    public static boolean isValidPassword(
            String password
    ) {

        return password != null
                && password.length()
                >= MIN_PASSWORD_LENGTH;
    }

    /*
     * =========================================================
     * Database
     * =========================================================
     */

    /**
     * Checks database connection status.
     *
     * @return true if connected
     */
    public static boolean isDatabaseConnected() {

        return DatabaseManager.getInstance()
                .isConnected();
    }

    /**
     * Returns database authentication service.
     *
     * @return database auth service
     */
    private static com.wastely.service.AuthService
    getDatabaseAuthService() {

        return DatabaseManager.getInstance()
                .getAuthService();
    }

    /*
     * =========================================================
     * Mapping
     * =========================================================
     */

    /**
     * Maps account entity to UI user model.
     *
     * @param account database account
     * @return mapped user
     */
    private static User mapAccountToUser(
            Account account
    ) {

        return new User(
                String.valueOf(
                        account.getAccountId()
                ),
                safe(account.getEmail()),
                safe(account.getName()),
                determineUserRole(account),
                safe(account.getBarangay())
        );
    }

    /**
     * Resolves user role.
     *
     * @param account account entity
     * @return normalized role
     */
    private static String determineUserRole(
            Account account
    ) {

        if (account == null) {
            return "USER";
        }

        String role =
                safe(account.getRole());

        switch (role.toUpperCase()) {

            case "MENRO":
                return "MENRO_ADMIN";

            case "BARANGAY":
                return "BARANGAY_ADMIN";

            default:
                return role.isEmpty()
                        ? "USER"
                        : role;
        }
    }

    /*
     * =========================================================
     * Utilities
     * =========================================================
     */

    /**
     * Returns safe trimmed string.
     *
     * @param value source value
     * @return safe string
     */
    private static String safe(
            String value
    ) {

        return value == null
                ? ""
                : value.trim();
    }

    /**
     * Builds formatted error message.
     *
     * @param ex exception
     * @return message
     */
    private static String buildErrorMessage(
            Exception ex
    ) {

        return "Operation failed: "
                + safe(ex.getMessage());
    }

    /**
     * Logs exception details.
     *
     * @param message log message
     * @param ex exception
     */
    private static void logError(
            String message,
            Exception ex
    ) {

        System.err.println(
                message + ": "
                        + ex.getMessage()
        );

        ex.printStackTrace();
    }
}