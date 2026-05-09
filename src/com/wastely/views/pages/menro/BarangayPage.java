package com.wastely.views.pages.menro;

import javax.swing.*;

import java.awt.*;
import com.wastely.model.Barangay;
import com.wastely.service.BarangayService;
import com.wastely.utils.Colors;
import com.wastely.utils.ComponentStyle;
import com.wastely.utils.Typography;
import com.wastely.utils.UINotificationHelper;
import com.wastely.views.components.Card;
import com.wastely.views.components.CustomScrollPane;
import com.wastely.views.components.ScrollableTable;
import com.wastely.views.components.SearchBar;

/**
 * Barangay Management Page for MENRO Dashboard
 */
public class BarangayPage extends JPanel {

    private ScrollableTable table;
    private BarangayService barangayService = new BarangayService();

    public BarangayPage() {
        setupUI();
        loadTableData("", "All");
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 249, 245));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 249, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        
        JLabel titleLabel = new JLabel("Barangay Management");
        titleLabel.setFont(Typography.HEADING_H1);
        titleLabel.setForeground(Colors.TEXT_PRIMARY);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(new Color(240, 249, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));
        contentPanel.add(createStatsGrid(), BorderLayout.NORTH);
        contentPanel.add(createTable(), BorderLayout.CENTER);

        CustomScrollPane scrollPane = new CustomScrollPane(contentPanel);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatsGrid() {
        JPanel grid = ComponentStyle.createTransparentPanel(new GridLayout(2, 4, 15, 15));
        // row 1
        grid.add(Card.createStatCard("Total Barangay", barangayService.getTotalBarangayCount(), "Barangay", null, "pin-house-white.png"));
        grid.add(Card.createStatCard("Total Household", barangayService.getTotalHousehold(), "Household", null, "team-white.png"));
        grid.add(Card.createStatCard("Scheduled Barangay", barangayService.getTotalScheduleBarangay(), " /" + barangayService.getTotalBarangayCount(), null, "calendar-white.png"));
        grid.add(Card.createStatCard("Completed Today", 0, "Barangay", null, "circle-check.png"));
        return grid;
    }

    private JPanel createTable() {
        JPanel  wrap = ComponentStyle.createTransparentPanel(new BorderLayout(0, 20));

        // Create reusable scrollable table
        table = new ScrollableTable(
                "ID",
                "Barangay Name",
                "No. Puroks", 
                "Population",
                "Collection Day",
                "Contact",
                "Status"
        ).setTitle("All Barangays");

        SearchBar searchBar = new SearchBar("Search barangay, contact, or collection day...");
        searchBar.setFilters(java.util.List.of(
                "All",
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
        ));

        searchBar.setOnSearchChange(() -> {
            String keyword = searchBar.getText();
            String dayFilter = searchBar.getSelectedFilter();

            loadTableData(keyword, dayFilter);
        });

        wrap.add(searchBar, BorderLayout.NORTH);
        wrap.add(table, BorderLayout.CENTER);
        return wrap;
    }

    /**
     * Load table data with filtering support
     */
    private void loadTableData(String keyword, String dayFilter) {
        try {
            table.clear();

            for (Barangay b : barangayService.getAllBarangays()) {

                // ================= FILTER LOGIC =================
                if (!matchesSearch(b, keyword, dayFilter)) {
                    continue;
                }

                table.addRow(
                    b.getBarangayId(),
                    b.getBarangayName(),
                    b.getPurokCount(),
                    b.getPopulation(),
                    b.getCollectionDay(),
                    b.getContact(),
                    b.getStatus()
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
    private boolean matchesSearch(Barangay b, String keyword, String dayFilter) {

        boolean matchesKeyword = keyword == null || keyword.isEmpty()
                || b.getBarangayName().toLowerCase().contains(keyword.toLowerCase())
                || b.getCollectionDay().toLowerCase().contains(keyword.toLowerCase())
                || (b.getContact() != null && b.getContact().toLowerCase().contains(keyword.toLowerCase()));

        boolean matchesDay = dayFilter == null
                || dayFilter.equals("All")
                || b.getCollectionDay().equalsIgnoreCase(dayFilter);

        return matchesKeyword && matchesDay;
    }
}