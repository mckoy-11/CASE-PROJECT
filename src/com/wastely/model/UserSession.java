package com.wastely.model;

public final class UserSession {

    private static int accountId;
    private static String email = "";
    private static String role = "";
    private static String displayName = "";

    private UserSession() {}

    public static void startSession(int accountId, String email, String role, String displayName) {
        UserSession.accountId = accountId;
        UserSession.email = email != null ? email : "";
        UserSession.role = role != null ? role : "";
        UserSession.displayName = displayName != null ? displayName : UserSession.email;
        // Session data updated (placeholder for event system)
    }

    public static void logout() {
        accountId = 0;
        email = "";
        role = "";
        displayName = "";
        // Session data updated (placeholder for event system)
    }

    public static boolean isActive() {
        return accountId > 0;
    }

    public static int getAccountId() {
        return accountId;
    }

    public static String getEmail() {
        return email;
    }

    public static String getRole() {
        return role;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public static void setDisplayName(String value) {
        displayName = value != null ? value : displayName;
        // Session data updated (placeholder for event system)
    }
}
