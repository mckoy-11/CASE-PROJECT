package com.wastely.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Centralized UI notification helper for displaying success, error, warning, and info dialogs.
 * Ensures consistent feedback across the entire application.
 * Thread-safe: All notifications are dispatched to the EDT.
 */
public final class UINotificationHelper {

    private UINotificationHelper() {
    }

    /**
     * Show a success notification dialog.
     *
     * @param parent Parent component or window (can be null)
     * @param title Dialog title
     * @param message Success message to display
     */
    public static void showSuccess(Component parent, String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    parent,
                    message,
                    title != null ? title : "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    /**
     * Show a success notification with a default title.
     *
     * @param parent Parent component or window (can be null)
     * @param message Success message to display
     */
    public static void showSuccess(Component parent, String message) {
        showSuccess(parent, "Success", message);
    }

    /**
     * Show an error notification dialog.
     *
     * @param parent Parent component or window (can be null)
     * @param title Dialog title
     * @param message Error message to display
     */
    public static void showError(Component parent, String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    parent,
                    message,
                    title != null ? title : "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        });
    }

    /**
     * Show an error notification with a default title.
     *
     * @param parent Parent component or window (can be null)
     * @param message Error message to display
     */
    public static void showError(Component parent, String message) {
        showError(parent, "Error", message);
    }

    /**
     * Show a warning notification dialog.
     *
     * @param parent Parent component or window (can be null)
     * @param title Dialog title
     * @param message Warning message to display
     */
    public static void showWarning(Component parent, String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    parent,
                    message,
                    title != null ? title : "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        });
    }

    /**
     * Show a warning notification with a default title.
     *
     * @param parent Parent component or window (can be null)
     * @param message Warning message to display
     */
    public static void showWarning(Component parent, String message) {
        showWarning(parent, "Warning", message);
    }

    /**
     * Show an info notification dialog.
     *
     * @param parent Parent component or window (can be null)
     * @param title Dialog title
     * @param message Info message to display
     */
    public static void showInfo(Component parent, String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    parent,
                    message,
                    title != null ? title : "Information",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    /**
     * Show an info notification with a default title.
     *
     * @param parent Parent component or window (can be null)
     * @param message Info message to display
     */
    public static void showInfo(Component parent, String message) {
        showInfo(parent, "Information", message);
    }

    /**
     * Show a confirmation dialog (Yes/No).
     *
     * @param parent Parent component or window (can be null)
     * @param title Dialog title
     * @param message Question to ask the user
     * @return true if user clicked Yes, false if clicked No
     */
    public static boolean showConfirmation(Component parent, String title, String message) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                title != null ? title : "Confirm",
                JOptionPane.YES_NO_OPTION
        );
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Show a confirmation dialog with a default title.
     *
     * @param parent Parent component or window (can be null)
     * @param message Question to ask the user
     * @return true if user clicked Yes, false if clicked No
     */
    public static boolean showConfirmation(Component parent, String message) {
        return showConfirmation(parent, "Confirm", message);
    }

    /**
     * Show a delete confirmation dialog.
     *
     * @param parent Parent component or window (can be null)
     * @param itemName Name of the item being deleted
     * @return true if user confirmed delete, false if cancelled
     */
    public static boolean showDeleteConfirmation(Component parent, String itemName) {
        String message = "Are you sure you want to delete this " + 
                (itemName != null ? itemName : "item") + "? This action cannot be undone.";
        return showConfirmation(parent, "Confirm Delete", message);
    }
}
