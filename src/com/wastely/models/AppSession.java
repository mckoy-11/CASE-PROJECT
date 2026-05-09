package com.wastely.models;

/**
 * Singleton class managing the application's user session.
 * Handles user login/logout and session state.
 */
public class AppSession {
    private static AppSession instance;
    private User currentUser;
    
    private AppSession() {
    }
    
    public static synchronized AppSession getInstance() {
        if (instance == null) {
            instance = new AppSession();
        }
        return instance;
    }
    
    public void login(User user) {
        this.currentUser = user;
    }
    
    public void logout() {
        this.currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean isMenroAdmin() {
        return isLoggedIn() && currentUser.isMenroAdmin();
    }
    
    public boolean isBarangayAdmin() {
        return isLoggedIn() && currentUser.isBarangayAdmin();
    }
}
