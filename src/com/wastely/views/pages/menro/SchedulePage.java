package com.wastely.views.pages.menro;

import java.awt.*;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import com.wastely.models.PopupItem;
import com.wastely.model.Schedule;
import com.wastely.service.ScheduleService;
import com.wastely.store.DataChangeBus;
import com.wastely.store.DataTopics;
import com.wastely.utils.UINotificationHelper;
import com.wastely.views.components.CustomButton;
import com.wastely.views.components.Header;
import com.wastely.views.components.ScrollableTable;
import com.wastely.views.components.SearchBar;
import com.wastely.views.pages.menro.forms.ScheduleFormDialog;

/**
 * Schedule Management Page for MENRO Dashboard
 * 
 * Automatically refreshes when schedule data changes via DataChangeBus.
 * Subscribes to SCHEDULES, BARANGAYS, COLLECTION_INFO, and DASHBOARD topics.
 */
public class SchedulePage extends JPanel {

    private ScrollableTable table;
    private final ScheduleService scheduleService;
    private SearchBar searchBar;
    
    // Subscription management
    private DataChangeBus.Subscription scheduleSubscription;
    private DataChangeBus.Subscription dashboardSubscription;

    public SchedulePage() {
        this.scheduleService = new ScheduleService();
        setupUI();
        loadTableData("", "All Status");
        subscribeToDataChanges();
    }

    /**
     * Subscribe to data changes so the page auto-refreshes when data is modified.
     */
    private void subscribeToDataChanges() {
        // Subscribe to schedule changes
        scheduleSubscription = DataChangeBus.subscribe(DataTopics.SCHEDULES, () -> {
            SwingUtilities.invokeLater(() -> {
                if (isVisible()) {
                    loadTableData(searchBar.getText(), searchBar.getSelectedFilter());
                }
            });
        });

        // Also subscribe to dashboard changes (affects schedule data)
        dashboardSubscription = DataChangeBus.subscribe(DataTopics.DASHBOARD, () -> {
            SwingUtilities.invokeLater(() -> {
                if (isVisible()) {
                    loadTableData(searchBar.getText(), searchBar.getSelectedFilter());
                }
            });
        });
    }

    /**
     * Unsubscribe from data changes when the page is removed.
     * This prevents memory leaks from old subscriptions.
     */
    private void unsubscribeFromDataChanges() {
        if (scheduleSubscription != null) {
            scheduleSubscription.unsubscribe();
        }
        if (dashboardSubscription != null) {
            dashboardSubscription.unsubscribe();
        }
    }

    @Override
    public void removeNotify() {
        unsubscribeFromDataChanges();
        super.removeNotify();
    }

    /**
     * UI initialization
     */
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 249, 245));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 249, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        
        CustomButton addBtn = new CustomButton("+ Add Schedule", CustomButton.ButtonStyle.PRIMARY);
        addBtn.addActionListener(e -> addSchedule());
        
        headerPanel.add(new Header("Schedule Management", false), BorderLayout.WEST);
        headerPanel.add(addBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(new Color(240, 249, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));

        // ================= TABLE =================
        table = new ScrollableTable(
                "ID",
                "Barangay",
                "Admin",
                "Team",
                "Truck",
                "Day",
                "Time",
                "Status",
                ""
        ).setTitle("Schedule Table");

        // ================= SEARCH BAR =================
        searchBar = new SearchBar("Search schedules by barangay, admin, or team...");

        searchBar.setFilters(List.of(
                "All Status",
                "Scheduled",
                "Pending",
                "Completed",
                "Cancelled"
        ));

        searchBar.setOnSearchChange(() -> {
            String keyword = searchBar.getText();
            String filter = searchBar.getSelectedFilter();

            loadTableData(keyword, filter);
        });

        // ================= ADD TO UI =================
        contentPanel.add(searchBar, BorderLayout.NORTH);
        contentPanel.add(table, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Load table data with filtering support
     */
    private void loadTableData(String keyword, String statusFilter) {
        try {
            table.clear();

            List<Schedule> schedules = scheduleService.getAllSchedules();
            if (schedules == null) schedules = new ArrayList<>();

            for (Schedule s : schedules) {

                // ================= FILTER LOGIC =================
                if (!matchesSearch(s, keyword, statusFilter)) {
                    continue;
                }

                List<PopupItem> actions = new ArrayList<>();
                actions.add(new PopupItem(
                        "Edit",
                        "Edit this schedule",
                        () -> openEditDialog(s)
                ));

                actions.add(new PopupItem(
                        "Delete",
                        "Remove this schedule",
                        () -> deleteSchedule(s)
                ));


                table.addRowWithAction(
                        s.getId(),
                        s.getBarangayName(),
                        s.getBarangayAdmin(),
                        s.getCollectorTeam(),
                        s.getTruckPlateNumber(),
                        s.getDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        s.getTime(),
                        s.getStatus(),
                        actions
                );
            }

            revalidate();
            repaint();
        } catch (Exception e) {
            System.err.println("Error loading schedule data: " + e.getMessage());
            UINotificationHelper.showError(this, "Error loading schedules: " + e.getMessage());
        }
    }

    /**
     * Filter logic (UI-level filtering before DAO optimization)
     */
    private boolean matchesSearch(Schedule s, String keyword, String statusFilter) {

        boolean matchesKeyword = keyword == null || keyword.isEmpty()
                || s.getBarangayName().toLowerCase().contains(keyword.toLowerCase())
                || s.getBarangayAdmin().toLowerCase().contains(keyword.toLowerCase())
                || s.getCollectorTeam().toLowerCase().contains(keyword.toLowerCase());

        boolean matchesStatus = statusFilter == null
                || statusFilter.equals("All Status")
                || s.getStatus().equalsIgnoreCase(statusFilter);

        return matchesKeyword && matchesStatus;
    }

    /**
     * Add schedule
     */
    private void addSchedule() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        ScheduleFormDialog dialog = new ScheduleFormDialog(parent, null);
        dialog.setVisible(true);
        
        // Dialog will trigger refresh via DataChangeBus when saved
        // No need for manual reload here anymore
    }

    /**
     * Edit schedule
     */
    private void openEditDialog(Schedule s) {
        Schedule schedule = scheduleService.getById(s.getId());
        if (schedule == null) {
            UINotificationHelper.showError(this, "Schedule not found");
            return;
        }

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        ScheduleFormDialog dialog = new ScheduleFormDialog(parent, schedule);
        dialog.setVisible(true);
        
        // Dialog will trigger refresh via DataChangeBus when saved
        // No need for manual reload here anymore
    }

    /**
     * Delete schedule
     */
    private void deleteSchedule(Schedule s) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this schedule?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = scheduleService.deleteSchedule(s.getId());

                if (success) {
                    // Success feedback will be shown by DataChangeBus listener
                    UINotificationHelper.showSuccess(this, "Schedule deleted successfully");
                } else {
                    UINotificationHelper.showError(this, "Failed to delete schedule");
                }
            } catch (Exception e) {
                UINotificationHelper.showError(this, "Error: " + e.getMessage());
            }
        }
    }
}