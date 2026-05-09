package com.wastely.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Utility helper class for common UI refresh and update operations.
 * Ensures consistent refresh behavior and EDT safety across the application.
 */
public final class UIHelper {

    private UIHelper() {
    }

    /**
     * Safely refresh a JTable by clearing and revalidating on the EDT.
     * Use this to refresh table displays after data changes.
     *
     * @param table The JTable to refresh
     */
    public static void refreshTable(JTable table) {
        if (table == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            table.revalidate();
            table.repaint();
        });
    }

    /**
     * Safely clear a JTable model and refresh on the EDT.
     *
     * @param table The JTable to clear
     */
    public static void clearTable(JTable table) {
        if (table == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            table.revalidate();
            table.repaint();
        });
    }

    /**
     * Safely refresh a JComboBox on the EDT.
     *
     * @param comboBox The JComboBox to refresh
     */
    public static void refreshComboBox(JComboBox<?> comboBox) {
        if (comboBox == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            comboBox.revalidate();
            comboBox.repaint();
        });
    }

    /**
     * Safely refresh a JPanel on the EDT.
     *
     * @param panel The JPanel to refresh
     */
    public static void refreshPanel(JPanel panel) {
        if (panel == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            panel.revalidate();
            panel.repaint();
        });
    }

    /**
     * Safely refresh any JComponent on the EDT.
     *
     * @param component The component to refresh
     */
    public static void refreshComponent(JComponent component) {
        if (component == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            component.revalidate();
            component.repaint();
        });
    }

    /**
     * Execute a runnable on the EDT if not already on the EDT.
     * Safe to call from any thread.
     *
     * @param runnable The code to execute on EDT
     */
    public static void runOnEDT(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Enable or disable a component safely on the EDT.
     *
     * @param component The component to enable/disable
     * @param enabled   true to enable, false to disable
     */
    public static void setEnabled(JComponent component, boolean enabled) {
        if (component == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> component.setEnabled(enabled));
    }

    /**
     * Set the visibility of a component safely on the EDT.
     *
     * @param component The component to show/hide
     * @param visible   true to show, false to hide
     */
    public static void setVisible(JComponent component, boolean visible) {
        if (component == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> component.setVisible(visible));
    }
}
