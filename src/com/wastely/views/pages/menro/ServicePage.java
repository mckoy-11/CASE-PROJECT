package com.wastely.views.pages.menro;

import com.wastely.model.Complaint;
import com.wastely.model.Report;
import com.wastely.model.Request;
import com.wastely.service.ComplaintService;
import com.wastely.service.ReportService;
import com.wastely.service.RequestService;
import com.wastely.store.DataChangeBus;
import com.wastely.store.DataTopics;
import com.wastely.utils.ComponentStyle;
import com.wastely.utils.UIHelper;
import com.wastely.views.components.Card;
import com.wastely.views.components.CustomButton;
import com.wastely.views.components.CustomScrollPane;
import com.wastely.views.components.Header;
import com.wastely.views.components.SummaryCards;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

import static com.wastely.utils.Colors.*;
import static com.wastely.utils.Typography.*;

public class ServicePage extends JPanel {

    private static final String REPORT_VIEW = "Report";
    private static final String COMPLAINT_VIEW = "Complaint";
    private static final String REQUEST_VIEW = "Request";

    private final ReportService reportService = new ReportService();
    private final ComplaintService complaintService = new ComplaintService();
    private final RequestService requestService = new RequestService();

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentPanel = ComponentStyle.createTransparentPanel();
    private final JPanel summaryPanel = ComponentStyle.createTransparentPanel(new BorderLayout());

    private String currentView = REPORT_VIEW;

    public ServicePage() {

        setLayout(new BorderLayout());
        setOpaque(false);

        DataChangeBus.subscribe(DataTopics.REPORTS, this::refreshUI);
        DataChangeBus.subscribe(DataTopics.COMPLAINTS, this::refreshUI);
        DataChangeBus.subscribe(DataTopics.REQUESTS, this::refreshUI);

        add(new Header("Management Panel", false), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createMainContent() {

        JPanel root = new JPanel(new BorderLayout(0, 15));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        summaryPanel.setOpaque(false);
        summaryPanel.add(createSummary(currentView), BorderLayout.CENTER);

        contentPanel.setOpaque(false);
        contentPanel.setLayout(contentLayout);

        rebuildViews();

        root.add(summaryPanel, BorderLayout.NORTH);
        root.add(contentPanel, BorderLayout.CENTER);

        return root;
    }

    private void rebuildViews() {

        contentPanel.removeAll();

        contentPanel.add(createReportView(), REPORT_VIEW);
        contentPanel.add(createComplaintView(), COMPLAINT_VIEW);
        contentPanel.add(createRequestView(), REQUEST_VIEW);

        contentLayout.show(contentPanel, currentView);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createReportView() {
        return createCardView(
                "Report Management",
                reportService.getAllReports(),
                this::createReportCard
        );
    }

    private JPanel createComplaintView() {
        return createCardView(
                "Complaint Management",
                complaintService.getAllComplaints(),
                this::createComplaintCard
        );
    }

    private JPanel createRequestView() {
        return createCardView(
                "Request Management",
                requestService.getAllRequests(),
                this::createRequestCard
        );
    }

    private <T> JPanel createCardView(
            String title,
            List<T> list,
            Function<T, JPanel> mapper
    ) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        panel.add(createHeader(title), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);

        if (list != null && !list.isEmpty()) {

            for (T item : list) {

                JPanel card = mapper.apply(item);

                if (card != null) {
                    grid.add(card, gbc);
                    gbc.gridy++;
                }
            }

        } else {

            JPanel empty = ComponentStyle.createTransparentPanel(
                    new GridBagLayout()
            );

            JLabel emptyLabel = new JLabel("No records available");
            emptyLabel.setFont(BODY_NORMAL);
            emptyLabel.setForeground(TEXT_SECONDARY);

            empty.add(emptyLabel);

            gbc.weighty = 1;
            grid.add(empty, gbc);
        }

        gbc.weighty = 1;
        grid.add(Box.createVerticalGlue(), gbc);

        CustomScrollPane scrollPane = new CustomScrollPane(grid);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBaseCard(String title, String status) {

        JPanel card = Card.createCard();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(BACKGROUND_WHITE);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(800, 170));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(HEADING_H3);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel statusLabel = ComponentStyle.createCapsuleLabel(
                safe(status, "Pending"),
                getStatusColor(status),
                PRIMARY_GREEN
        );

        JPanel top = ComponentStyle.createTransparentPanel(
                new BorderLayout()
        );

        top.add(titleLabel, BorderLayout.WEST);
        top.add(statusLabel, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        return card;
    }

    private JPanel createReportCard(Report report) {

        JPanel card = createBaseCard(
                "Report #" + report.getReportId(),
                report.getStatus()
        );

        JPanel body = vertical();

        body.add(createInfoLabel(
                "Barangay: " + safe(report.getBarangayName())
        ));

        body.add(Box.createVerticalStrut(5));

        body.add(createInfoLabel(
                "Type: " + safe(report.getType())
        ));

        body.add(Box.createVerticalStrut(5));

        body.add(createInfoLabel(
                "Date: " + safe(
                        report.getCreatedAt() != null
                                ? report.getCreatedAt().toString()
                                : ""
                )
        ));

        body.add(Box.createVerticalStrut(5));

        body.add(createInfoLabel(
                "Message: " + safe(report.getMessage())
        ));

        card.add(body, BorderLayout.CENTER);
        card.add(createActionPanel(() -> showReportDetailDialog(report)), BorderLayout.SOUTH);

        return card;
    }

    private JPanel createComplaintCard(Complaint complaint) {

        JPanel card = createBaseCard(
                "Complaint #" + complaint.getComplaintId(),
                complaint.getStatus()
        );

        JPanel body = vertical();

        body.add(createInfoLabel(
                "Type: " + safe(complaint.getType())
        ));

        body.add(Box.createVerticalStrut(5));

        body.add(createInfoLabel(
                "Location: " + safe(complaint.getLocation())
        ));

        body.add(Box.createVerticalStrut(5));

        body.add(createInfoLabel(
                "Date: " + safe(
                        complaint.getCreatedAt() != null
                                ? complaint.getCreatedAt().toString()
                                : ""
                )
        ));

        body.add(Box.createVerticalStrut(5));

        body.add(createInfoLabel(
                "Message: " + safe(complaint.getMessage())
        ));

        card.add(body, BorderLayout.CENTER);
        card.add(createActionPanel(() -> showComplaintDetailDialog(complaint)), BorderLayout.SOUTH);

        return card;
    }

    private JPanel createRequestCard(Request request) {

        JPanel card = createBaseCard(
                "Request #" + request.getRequestId(),
                request.getStatus()
        );

        JPanel body = vertical();

        body.add(createInfoLabel(
                "Type: " + safe(request.getType())
        ));

        body.add(Box.createVerticalStrut(5));

        body.add(createInfoLabel(
                "Location: " + safe(request.getLocation())
        ));

        body.add(Box.createVerticalStrut(5));

        body.add(createInfoLabel(
                "Date: " + safe(
                        request.getCreatedAt() != null
                                ? request.getCreatedAt().toString()
                                : ""
                )
        ));

        body.add(Box.createVerticalStrut(5));

        body.add(createInfoLabel(
                "Message: " + safe(request.getMessage())
        ));

        card.add(body, BorderLayout.CENTER);
        card.add(createActionPanel(() -> showRequestDetailDialog(request)), BorderLayout.SOUTH);

        return card;
    }

    private JPanel createActionPanel(Runnable onViewDetails) {

        JPanel panel = ComponentStyle.createTransparentPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0)
        );

        CustomButton button = new CustomButton("View Details");
        button.setPreferredSize(new Dimension(140, 35));
        button.addActionListener(e -> onViewDetails.run());

        panel.add(button);

        return panel;
    }

    private JLabel createInfoLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(BODY_NORMAL);
        label.setForeground(TEXT_SECONDARY);

        return label;
    }

    private JPanel createHeader(String titleText) {

        JPanel header = Card.createCard();
        header.setLayout(new BorderLayout());
        header.setBackground(PRIMARY_GREEN);
        header.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel title = new JLabel(titleText);
        title.setForeground(BACKGROUND_WHITE);
        title.setFont(HEADING_H3);

        JPanel navBar = ComponentStyle.createTransparentPanel(
                new GridLayout(1, 3, 5, 0)
        );

        navBar.setPreferredSize(new Dimension(380, 40));

        for (String view : new String[]{
                REPORT_VIEW,
                COMPLAINT_VIEW,
                REQUEST_VIEW
        }) {

            CustomButton button = new CustomButton(view);

            button.addActionListener(e -> switchView(view));

            navBar.add(button);
        }

        JPanel right = ComponentStyle.createTransparentPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );

        right.add(navBar);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private SummaryCards createSummary(String view) {

        if (REPORT_VIEW.equals(view)) {

            List<Report> list = reportService.getAllReports();

            return new SummaryCards(
                    new String[]{
                            "Total",
                            "Resolved",
                            "Pending"
                    },
                    new int[]{
                            list.size(),
                            (int) list.stream()
                                    .filter(r ->
                                            "Resolved".equalsIgnoreCase(
                                                    r.getStatus()
                                            )
                                    )
                                    .count(),
                            (int) list.stream()
                                    .filter(r ->
                                            "Pending".equalsIgnoreCase(
                                                    r.getStatus()
                                            )
                                    )
                                    .count()
                    },
                    new String[]{
                            "All",
                            "Resolved",
                            "Pending"
                    },
                    icons(),
                    colors()
            );
        }

        if (COMPLAINT_VIEW.equals(view)) {

            List<Complaint> list = complaintService.getAllComplaints();

            return new SummaryCards(
                    new String[]{
                            "Total",
                            "Resolved",
                            "Pending"
                    },
                    new int[]{
                            list.size(),
                            (int) list.stream()
                                    .filter(c ->
                                            "Resolved".equalsIgnoreCase(
                                                    c.getStatus()
                                            )
                                    )
                                    .count(),
                            (int) list.stream()
                                    .filter(c ->
                                            "Pending".equalsIgnoreCase(
                                                    c.getStatus()
                                            )
                                    )
                                    .count()
                    },
                    new String[]{
                            "All",
                            "Resolved",
                            "Pending"
                    },
                    icons(),
                    colors()
            );
        }

        List<Request> list = requestService.getAllRequests();

        return new SummaryCards(
                new String[]{
                        "Total",
                        "Approved",
                        "Pending"
                },
                new int[]{
                        list.size(),
                        (int) list.stream()
                                .filter(r ->
                                        "Approved".equalsIgnoreCase(
                                                r.getStatus()
                                        )
                                )
                                .count(),
                        (int) list.stream()
                                .filter(r ->
                                        "Pending".equalsIgnoreCase(
                                                r.getStatus()
                                        )
                                )
                                .count()
                },
                new String[]{
                        "All",
                        "Approved",
                        "Pending"
                },
                icons(),
                colors()
        );
    }

    private void switchView(String view) {

        currentView = view;

        refreshUI();
    }

    private void refreshUI() {

        UIHelper.runOnEDT(() -> {

            summaryPanel.removeAll();
            summaryPanel.add(
                    createSummary(currentView),
                    BorderLayout.CENTER
            );

            rebuildViews();

            revalidate();
            repaint();
        });
    }

    private JPanel vertical() {

        JPanel panel = ComponentStyle.createTransparentPanel();

        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        return panel;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String safe(String value, String defaultValue) {

        return value == null || value.trim().isEmpty()
                ? defaultValue
                : value;
    }

    private Color getStatusColor(String status) {

        if (status == null) {
            return STATUS_PENDING_BG;
        }

        switch (status.toLowerCase()) {

            case "resolved":
            case "approved":
            case "completed":
                return STATUS_COMPLETED_BG;

            case "rejected":
            case "cancelled":
            case "closed":
                return STATUS_CANCELLED_BG;

            case "in progress":
            case "in_progress":
                return STATUS_IN_PROGRESS_BG;

            default:
                return STATUS_PENDING_BG;
        }
    }

    private String[] icons() {

        return new String[]{
                "calendar.png",
                "circle-check.png",
                "circle-alert.png"
        };
    }

    private Color[] colors() {

        return new Color[]{
                new Color(139, 92, 246, 20),
                new Color(59, 130, 246, 20),
                new Color(232, 114, 82, 20)
        };
    }

    // =========================================================
    // DETAIL DIALOGS AND RESPONSE FORMS
    // =========================================================

    private void showReportDetailDialog(Report report) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Report Details", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(true);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(BACKGROUND_WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel detailsPanel = createDetailsPanel(
                new String[]{"Report ID", "Barangay", "Type", "Status", "Date", "Message", "Response"},
                new String[]{
                        String.valueOf(report.getReportId()),
                        safe(report.getBarangayName()),
                        safe(report.getType()),
                        safe(report.getStatus()),
                        safe(report.getCreatedAt() != null ? report.getCreatedAt().toString() : ""),
                        safe(report.getMessage()),
                        safe(report.getResponseMessage())
                }
        );

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = ComponentStyle.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        CustomButton responseBtn = new CustomButton("Add Response");
        responseBtn.addActionListener(e -> {
            dialog.dispose();
            showResponseForm(report, null, null);
        });
        buttonPanel.add(responseBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private void showComplaintDetailDialog(Complaint complaint) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Complaint Details", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(true);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(BACKGROUND_WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel detailsPanel = createDetailsPanel(
                new String[]{"Complaint ID", "Type", "Status", "Location", "Date", "Message", "Response"},
                new String[]{
                        String.valueOf(complaint.getComplaintId()),
                        safe(complaint.getType()),
                        safe(complaint.getStatus()),
                        safe(complaint.getLocation()),
                        safe(complaint.getCreatedAt() != null ? complaint.getCreatedAt().toString() : ""),
                        safe(complaint.getMessage()),
                        safe(complaint.getResponseMessage())
                }
        );

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = ComponentStyle.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        CustomButton responseBtn = new CustomButton("Add Response");
        responseBtn.addActionListener(e -> {
            dialog.dispose();
            showResponseForm(null, complaint, null);
        });
        buttonPanel.add(responseBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private void showRequestDetailDialog(Request request) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Request Details", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(true);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(BACKGROUND_WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel detailsPanel = createDetailsPanel(
                new String[]{"Request ID", "Type", "Status", "Location", "Date", "Message", "Response"},
                new String[]{
                        String.valueOf(request.getRequestId()),
                        safe(request.getType()),
                        safe(request.getStatus()),
                        safe(request.getLocation()),
                        safe(request.getCreatedAt() != null ? request.getCreatedAt().toString() : ""),
                        safe(request.getMessage()),
                        safe(request.getResponseMessage())
                }
        );

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = ComponentStyle.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        CustomButton responseBtn = new CustomButton("Add Response");
        responseBtn.addActionListener(e -> {
            dialog.dispose();
            showResponseForm(null, null, request);
        });
        buttonPanel.add(responseBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private JPanel createDetailsPanel(String[] labels, String[] values) {
        JPanel panel = ComponentStyle.createTransparentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < labels.length; i++) {
            JPanel row = ComponentStyle.createTransparentPanel(new BorderLayout(10, 0));
            row.setBorder(new EmptyBorder(10, 0, 10, 0));

            JLabel labelComp = new JLabel(labels[i] + ":");
            labelComp.setFont(LABEL_MEDIUM);
            labelComp.setForeground(TEXT_SECONDARY);
            labelComp.setPreferredSize(new Dimension(120, 20));

            JLabel valueComp = new JLabel(safe(values[i]));
            valueComp.setFont(BODY_NORMAL);
            valueComp.setForeground(TEXT_PRIMARY);

            row.add(labelComp, BorderLayout.WEST);
            row.add(valueComp, BorderLayout.CENTER);
            panel.add(row);
        }

        return panel;
    }

    private void showResponseForm(Report report, Complaint complaint, Request request) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Add Response", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(true);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(BACKGROUND_WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Response Form");
        titleLabel.setFont(HEADING_H3);
        titleLabel.setForeground(TEXT_PRIMARY);

        JPanel titlePanel = ComponentStyle.createTransparentPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.WEST);
        contentPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = ComponentStyle.createTransparentPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel messageLabel = new JLabel("Response Message:");
        messageLabel.setFont(LABEL_MEDIUM);
        messageLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(messageLabel);
        formPanel.add(Box.createVerticalStrut(5));

        JTextArea messageArea = new JTextArea(8, 50);
        messageArea.setFont(BODY_NORMAL);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1));

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(null);
        formPanel.add(scrollPane);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        contentPanel.add(formScroll, BorderLayout.CENTER);

        JPanel buttonPanel = ComponentStyle.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        CustomButton cancelBtn = new CustomButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        CustomButton submitBtn = new CustomButton("Submit Response");
        submitBtn.addActionListener(e -> {
            String response = messageArea.getText().trim();
            if (response.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a response message", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (report != null) {
                report.setResponseMessage(response);
                report.setStatus("Pending");
                reportService.updateReport(report);
            } else if (complaint != null) {
                complaint.setResponseMessage(response);
                complaint.setStatus("Pending");
                complaintService.updateComplaint(complaint);
            } else if (request != null) {
                request.setResponseMessage(response);
                request.setStatus("Pending");
                requestService.updateRequest(request);
            }

            JOptionPane.showMessageDialog(dialog, "Response submitted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            refreshUI();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
}