package com.wastely.views.pages.menro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.time.LocalDate;

import com.wastely.service.ComplaintService;
import com.wastely.service.PersonnelService;
import com.wastely.service.RequestService;
import com.wastely.service.ScheduleService;
import com.wastely.service.TeamService;
import com.wastely.service.TruckService;
import com.wastely.model.Schedule;
import com.wastely.utils.Colors;
import com.wastely.utils.ComponentStyle;
import com.wastely.utils.Typography;
import com.wastely.views.components.Card;
import com.wastely.views.components.CustomScrollPane;
import com.wastely.views.components.ScrollableTable;

/**
 * MENRO Home Dashboard Page
 * Displays KPI statistics and recent data tables.
 */
public class MenroHomePage extends JPanel {
    private PersonnelService personnelService = new PersonnelService();
    private TeamService teamService = new TeamService();
    private RequestService requestService = new RequestService();
    private ComplaintService complaintService = new ComplaintService();
    private TruckService truckService = new TruckService();
    private ScheduleService scheduleService = new ScheduleService();
    
    public MenroHomePage() {
        setupPage();
    }

    private void setupPage() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 249, 245));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 249, 245));
        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(30, 20, 30, 20)
        );

        /*
         * HEADER 
         */
        
        JPanel header = ComponentStyle.createTransparentPanel(new BorderLayout(0, 10));
        JLabel headerLabel = new JLabel("Dashboard");
        headerLabel.setFont(Typography.HEADING_H1);
        headerLabel.setForeground(Colors.TEXT_PRIMARY);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
                "Welcome back! Here's your waste management overview."
        );
        subtitleLabel.setFont(Typography.LABEL);
        subtitleLabel.setForeground(Colors.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(headerLabel, BorderLayout.CENTER);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        contentPanel.add(header);
        contentPanel.add(Box.createVerticalStrut(30));

        /*
         * WRAPPER (NO BORDERLAYOUT)
         */
        JPanel wrap = ComponentStyle.createTransparentPanel(new BorderLayout(0, 15));
        JLabel text = new JLabel("Key Metrics");
        text.setFont(Typography.HEADING_H3);
        text.setForeground(Colors.TEXT_PRIMARY);
        text.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statsGrid = createStatsGrid();
        
        wrap.add(text, BorderLayout.NORTH);
        wrap.add(statsGrid, BorderLayout.CENTER);

        contentPanel.add(wrap);
        contentPanel.add(Box.createVerticalStrut(30));
        
        JPanel wrap2 = ComponentStyle.createTransparentPanel(new BorderLayout(0, 15));

        JLabel act = new JLabel("Daily Collection");
        act.setFont(Typography.HEADING_H3);
        act.setForeground(Colors.TEXT_PRIMARY);
        act.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel dailyCards = createDailySchedBoard();
        
        wrap2.add(act, BorderLayout.NORTH);
        wrap2.add(dailyCards, BorderLayout.CENTER);
        contentPanel.add(wrap2);
        contentPanel.add(Box.createVerticalStrut(30));


        contentPanel.add(createTableSection("Today's Scheduled Collections"));
        contentPanel.add(Box.createVerticalStrut(30));

        CustomScrollPane scrollPane = new CustomScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatsGrid() {

        JPanel grid = ComponentStyle.createTransparentPanel(new GridLayout(2, 4, 15, 15));

        // row 1
        grid.add(Card.createStatCard("Total Requests", requestService.getTotalRequestCount(), "requests", null, "mail-question-mark.png"));
        grid.add(Card.createStatCard("Total Complaints", complaintService.getTotalComplaintCount(), "complaints", null, "mail-warning.png"));
        grid.add(Card.createStatCard("Active Teams", teamService.getActiveTeamCount(), "teams", null, "team-white.png"));
        grid.add(Card.createStatCard("Completed Today", scheduleService.countByStatus("COMPLETE"), "barangay", null, "calendar-white.png"));

        // row 2
        grid.add(Card.createStatCard("Pending Requests", requestService.getUnreadRequestCount(), "items", null, "mail-question-mark.png"));
        grid.add(Card.createStatCard("In Progress", complaintService.getUnreadComplaintCount() , "complaints", null, "mail-warning.png"));
        grid.add(Card.createStatCard("Total Personnel", personnelService.getTotalPersonnelCount(), "staff", null, "personnel-white.png"));
        grid.add(Card.createStatCard("Available Trucks", truckService.getTotalTruckCount(), "vehicles", null, "truck-white.png"));

        return grid;
    }

    // daily card
    private JPanel createDailySchedBoard() {
        JPanel panel = new JPanel(new GridLayout(1, 7, 5, 0));
        panel.setOpaque(false);

        String[] days = {
            "Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"
        };

        LocalDate today = LocalDate.now();

        for (String day : days) {
            panel.add(createDayCard(day, today));
        }

        return panel;
    }
    
    private JPanel createDayCard(String dayLabel, LocalDate today) {

        JPanel card = Card.createCard();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(148, 200));
        card.setBorder(new EmptyBorder(5, 5, 5, 5));

        String todayName = today.getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        boolean isToday = dayLabel.equalsIgnoreCase(todayName);

        JLabel header = new JLabel(dayLabel + (isToday ? " (Today)" : ""), SwingConstants.CENTER);
        header.setFont(Typography.HEADING_H3);
        if (isToday) {
            header.setForeground(Color.WHITE);
        } else {
            header.setForeground(Colors.TEXT_PRIMARY);
        }

        JPanel content = createDayScheduleInfo(card, dayLabel, today);

        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel createDayScheduleInfo(JPanel card, String dayLabel, LocalDate today) {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        ScheduleService service = new ScheduleService();
        
        // Get all barangays scheduled for this day of week
        java.util.List<String> barangays = service.getBarangaysByCollectionDay(dayLabel);

        String todayName = today.getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);

        boolean isToday = dayLabel.equalsIgnoreCase(todayName);

        if (isToday) {
            card.setBackground(Colors.SECONDARY_GREEN);
        } else {
            card.setBackground(Color.WHITE);
        }

        // Display barangays for this day
        if (barangays != null && !barangays.isEmpty()) {
            barangays.forEach((b) -> {
                if (isToday) {
                    panel.add(createWhiteLabel(b));
                } else {
                    panel.add(createGreenLabel(b));
                }
            });
        } else {
            panel.add(createCenteredLabel("No schedule"));
        }

        return panel;
    }
        
    private JLabel createCenteredLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel createGreenLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Colors.PRIMARY_GREEN);
        return label;
    }

    private JLabel createWhiteLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JPanel createTableSection(String title) {

        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        ScrollableTable table = new ScrollableTable(
                "Barangay",
                "Admin",
                "Team",
                "Truck",
                "Date",
                "Status"
        ).setTitle(title);

        ScheduleService scheduleService = new ScheduleService();

        // Load all schedules
        for (Schedule schedule : scheduleService.getAllSchedules()) {
            table.addRow(
                    schedule.getBarangayName(),
                    schedule.getBarangayAdmin(),
                    schedule.getCollectorTeam(),
                    schedule.getTruckPlateNumber(),
                    schedule.getDate(),
                    schedule.getStatus() != null ? schedule.getStatus() : "Pending"
            );
        }
        section.add(table, BorderLayout.CENTER);
        return section;
    }
}