package com.wastely.views.pages.menro;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.wastely.model.*;
import com.wastely.models.PopupItem;
import com.wastely.service.*;
import com.wastely.store.DataTopics;
import com.wastely.store.DataChangeBus;
import com.wastely.views.components.*;
import com.wastely.utils.*;

import static com.wastely.utils.Colors.*;
import static com.wastely.utils.Typography.*;
import com.wastely.views.pages.menro.forms.*;

public class ManagementPage extends JPanel {

    private static final String PERSONNEL_VIEW = "Personnel";
    private static final String TEAM_VIEW = "Team";
    private static final String TRUCK_VIEW = "Truck";

    private final PersonnelService personnelService = new PersonnelService();
    private final TeamService teamService = new TeamService();
    private final TruckService truckService = new TruckService();

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentPanel = ComponentStyle.createTransparentPanel();
    private final JPanel summaryPanel = ComponentStyle.createTransparentPanel(new BorderLayout());

    private String currentView = PERSONNEL_VIEW;

    public ManagementPage() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Subscribe to data changes via DataChangeBus
        DataChangeBus.subscribe(DataTopics.PERSONNEL, this::refreshUI);
        DataChangeBus.subscribe(DataTopics.TEAMS, this::refreshUI);
        DataChangeBus.subscribe(DataTopics.TRUCKS, this::refreshUI);

        add(new Header("Management", false), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBackground(new Color(240, 249, 245));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        summaryPanel.add(createSummary(currentView), BorderLayout.CENTER);

        contentPanel.setLayout(contentLayout);
        contentPanel.setBorder(new EmptyBorder(0, 0, 4, 4));
        rebuildContentViews();

        root.add(summaryPanel, BorderLayout.NORTH);
        root.add(contentPanel, BorderLayout.CENTER);
        return root;
    }

    private void rebuildContentViews() {
        contentPanel.removeAll();
        contentPanel.add(createPersonnelView(), PERSONNEL_VIEW);
        contentPanel.add(createTeamView(), TEAM_VIEW);
        contentPanel.add(createTruckView(), TRUCK_VIEW);
        contentLayout.show(contentPanel, currentView);
    }

    private JPanel createPersonnelView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(createHeader("Personnel Management"), BorderLayout.NORTH);

        ScrollableTable table = new ScrollableTable(
                "Name", "Age", "Sex", "Address", "Phone", "Team", "Role", "Status", "Action"
        );

        for (Personnel personnel : personnelService.getAllPersonnel()) {
            List<PopupItem> actions = new ArrayList<>();
            actions.add(new PopupItem("Edit", "Update personnel details", () -> openPersonnelDialog(personnel)));
            actions.add(new PopupItem("Delete", "Remove this personnel", () -> deletePersonnel(personnel)));
            table.addRowWithAction(
                    safe(personnel.getFullName()),
                    personnel.getAge(),
                    safe(personnel.getGender()),
                    safe(personnel.getAddress()),
                    safe(personnel.getPhoneNumber()),
                    safe(personnel.getTeam(), "Unassigned"),
                    safe(personnel.getRole()),
                    safe(personnel.getStatus()),
                    actions
            );
        }

        panel.add(table, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTeamView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(createHeader("Team Management"), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 15)); // 2 columns
        grid.setOpaque(false);

        for (Team team : teamService.getAllTeams()) {
            grid.add(createTeamCard(team));
        }

        CustomScrollPane scrollable = new CustomScrollPane(grid);
        panel.add(scrollable, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createTeamCard(Team team) {
        JPanel card = Card.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(300, 100));
        card.setBackground(BACKGROUND_WHITE);
        
        // LEFT (icon + info)
        JPanel center = ComponentStyle.createTransparentPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(safe(team.getTeamName()));
        name.setFont(HEADING_H3);

        JLabel location = new JLabel(
                safe(team.getTruckPlateNumber(), "No assigned area")
        );
        location.setForeground(TEXT_SECONDARY);

        center.add(name);
        center.add(Box.createVerticalStrut(5));
        center.add(location);

        // RIGHT (status + button)
        JPanel right = ComponentStyle.createTransparentPanel(new BorderLayout());

        String statusText = safe(team.getStatus(), "Active");
        Color statusBg = getStatusColor(statusText);
        JLabel status = ComponentStyle.createCapsuleLabel(statusText, statusBg, PRIMARY_GREEN);
        status.setHorizontalAlignment(SwingConstants.CENTER);

        JButton viewBtn = new CustomButton("View Details");
        viewBtn.addActionListener(e -> editTeam(team));

        right.add(status, BorderLayout.NORTH);
        right.add(viewBtn, BorderLayout.SOUTH);
        
        JLabel icon = new JLabel();
        icon.setBorder(new EmptyBorder(10, 20, 10, 20));
        card.add(icon, BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private JPanel createTruckView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(createHeader("Truck Management"), BorderLayout.NORTH);

        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0); // spacing between cards

        int y = 0;
        for (Truck truck : truckService.getAllTrucks()) {
            gbc.gridy = y++;
            container.add(createTruckCard(truck), gbc);
        }

        CustomScrollPane scrollable = new CustomScrollPane(container);
        panel.add(scrollable, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createTruckCard(Truck truck) {
        JPanel card = Card.createCard();
        card.setLayout(new BorderLayout(15, 10));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setBackground(BACKGROUND_WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel image = new JLabel();
        image.setBorder(new EmptyBorder(0, 5, 0, 10));

        JLabel title = new JLabel("Truck " + safe(truck.getPlateNumber()));
        title.setFont(HEADING_H3);

        String statusText = safe(truck.getStatus(), "Active");
        Color statusBg = getStatusColor(statusText);
        JLabel status = ComponentStyle.createCapsuleLabel(statusText, statusBg, PRIMARY_GREEN);

        JPanel top = ComponentStyle.createTransparentPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.add(title);
        top.add(Box.createHorizontalGlue());
        top.add(status);

        JPanel info = new JPanel(new GridLayout(1, 4, 20, 0));
        info.setOpaque(false);

        info.add(ComponentStyle.createInfoItem("Capacity", safe(truck.getCapacity(), "None")));
        info.add(ComponentStyle.createInfoItem("Assigned To", safe(truck.getAssignedTeam(), "None")));
        info.add(ComponentStyle.createInfoItem("Truck Type", safe(truck.getTruckType(), "None")));
        info.add(ComponentStyle.createInfoItem("Assigned Barangay", safe(truck.getAssignedBarangay(), "None")));

        // Action buttons
        JPanel actionPanel = ComponentStyle.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton editBtn = new CustomButton("Edit");
        editBtn.addActionListener(e -> editTruck(truck));

        JButton deleteBtn = new CustomButton("Delete");
        deleteBtn.addActionListener(e -> deleteTruck(truck));

        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);

        JPanel center = ComponentStyle.createTransparentPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(top);
        center.add(Box.createVerticalStrut(10));
        center.add(info);
        center.add(Box.createVerticalStrut(10));
        center.add(actionPanel);

        card.add(image, BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);

        return card;
    }




    private JPanel createHeader(String titleText) {
        JPanel header = Card.createCard();
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 15, 10, 15));
        header.setBackground(PRIMARY_GREEN);

        JLabel title = new JLabel(titleText);
        title.setForeground(BACKGROUND_WHITE);
        title.setFont(HEADING_H3);

        JPanel navBar = ComponentStyle.createTransparentPanel(new GridLayout(1, 3, 5, 0));
        navBar.setPreferredSize(new Dimension(500, 35));

        for (String view : new String[]{PERSONNEL_VIEW, TEAM_VIEW, TRUCK_VIEW}) {
            CustomButton button = new CustomButton(view);
            button.addActionListener(event -> switchView(view));
            navBar.add(button);
        }

        JPanel right = ComponentStyle.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.add(navBar);

        CustomButton addButton = new CustomButton("Add");
        addButton.setPreferredSize(new Dimension(100, 35));
        addButton.addActionListener(event -> openCreateDialog());
        navBar.add(addButton);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private SummaryCards createSummary(String view) {
        if (TEAM_VIEW.equals(view)) {
            List<Team> teams = teamService.getAllTeams();
            return new SummaryCards(
                    new String[]{"Total Teams", "Active Teams", "Inactive Teams"},
                    new int[]{
                        teams.size(),
                        (int) teams.stream().filter(team -> "Active".equalsIgnoreCase(team.getStatus())).count(),
                        (int) teams.stream().filter(team -> "Inactive".equalsIgnoreCase(team.getStatus())).count()
                    },
                    new String[]{"All teams", "Ready for dispatch", "Needs attention"},
                    icons(),
                    colors()
            );
        }

        if (TRUCK_VIEW.equals(view)) {
            List<Truck> trucks = truckService.getAllTrucks();
            return new SummaryCards(
                    new String[]{"Total Trucks", "Active Trucks", "Maintenance"},
                    new int[]{
                        trucks.size(),
                        (int) trucks.stream().filter(truck -> "Active".equalsIgnoreCase(truck.getStatus())).count(),
                        (int) trucks.stream().filter(truck -> "Maintenance".equalsIgnoreCase(truck.getStatus())).count()
                    },
                    new String[]{"Fleet total", "Available", "Unavailable"},
                    icons(),
                    colors()
            );
        }

        return new SummaryCards(
                new String[]{"Total Personnel", "Active Personnel", "Unassigned"},
                new int[]{
                    personnelService.getTotalPersonnelCount(),
                    personnelService.getActivePersonnelCount(),
                    personnelService.getUnassignedPersonnelCount()
                },
                new String[]{"Team members", "Ready to assign", "Awaiting assignment"},
                icons(),
                colors()
        );
    }

    private String[] icons() {
        return new String[]{"calendar.png", "circle-check.png", "circle-alert.png"};
    }

    private Color[] colors() {
        return new Color[]{
            new Color(139, 92, 246, 20),
            new Color(59, 130, 246, 20),
            new Color(232, 114, 82, 20)
        };
    }

    private Color getStatusColor(String status) {
        if (status == null) return STATUS_COMPLETED_BG;
        switch (status.toLowerCase()) {
            case "active":
            case "resolved":
            case "approved":
                return STATUS_COMPLETED_BG;
            case "inactive":
            case "rejected":
            case "closed":
                return STATUS_CANCELLED_BG;
            case "pending":
            case "maintenance":
                return STATUS_PENDING_BG;
            default:
                return STATUS_COMPLETED_BG;
        }
    }

    private void switchView(String view) {
        currentView = view;
        refreshUI();
    }

    private String safe(String value) {
        return safe(value, "");
    }

    private String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private void refreshUI() {
        UIHelper.runOnEDT(() -> {
            summaryPanel.removeAll();
            summaryPanel.add(createSummary(currentView), BorderLayout.CENTER);
            UIHelper.refreshComponent(summaryPanel);

            rebuildContentViews();
            UIHelper.refreshComponent(contentPanel);
        });
    }

    private void openPersonnelDialog(Personnel personnel) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        PersonnelFormDialog dialog = new PersonnelFormDialog(parent, personnel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private void deletePersonnel(Personnel personnel) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete " + personnel.getFullName() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = personnelService.deletePersonnel(personnel.getId());
            if (success) {
                UINotificationHelper.showSuccess(this, "Personnel deleted successfully");
            } else {
                UINotificationHelper.showError(this, "Failed to delete personnel");
            }
        }
    }

    private void editTeam(Team team) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        TeamFormDialog dialog = new TeamFormDialog(parent, team);
        dialog.setVisible(true);
    }

    private void editTruck(Truck truck) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        TruckFormDialog dialog = new TruckFormDialog(parent, truck);
        dialog.setVisible(true);
    }

    private void deleteTruck(Truck truck) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete truck " + truck.getPlateNumber() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = truckService.deleteTruck(truck.getId());
            if (success) {
                UINotificationHelper.showSuccess(this, "Truck deleted successfully");
            } else {
                UINotificationHelper.showError(this, "Failed to delete truck");
            }
        }
    }

    private void openCreateDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        switch (currentView) {
            case PERSONNEL_VIEW:
                PersonnelFormDialog personnelDialog = new PersonnelFormDialog(parent, null);
                personnelDialog.setVisible(true);
                break;
            case TRUCK_VIEW:
                TruckFormDialog truckDialog = new TruckFormDialog(parent, null);
                truckDialog.setVisible(true);
                break;
            case TEAM_VIEW:
                TeamFormDialog teamDialog = new TeamFormDialog(parent, null);
                teamDialog.setVisible(true);
                break;
        }
    }
}
