package com.wastely.views.pages.menro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import com.wastely.model.*;
import com.wastely.service.*;
import com.wastely.store.DataTopics;
import com.wastely.store.DataChangeBus;
import com.wastely.views.components.*;
import com.wastely.utils.*;

import static com.wastely.utils.Colors.*;
import static com.wastely.utils.Typography.*;

public class ServicePage extends JPanel {

    private static final String REPORT = "Report";
    private static final String COMPLAINT = "Complaint";
    private static final String REQUEST = "Request";

    private final ReportService reportService = new ReportService();
    private final ComplaintService complaintService = new ComplaintService();
    private final RequestService requestService = new RequestService();

    private final CardLayout layout = new CardLayout();
    private final JPanel content = ComponentStyle.createTransparentPanel();
    private final JPanel summary = ComponentStyle.createTransparentPanel(new BorderLayout());

    private String view = REPORT;

    public ServicePage() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Subscribe to data changes via DataChangeBus
        DataChangeBus.subscribe(DataTopics.REPORTS, this::refresh);
        DataChangeBus.subscribe(DataTopics.COMPLAINTS, this::refresh);
        DataChangeBus.subscribe(DataTopics.REQUESTS, this::refresh);

        add(new Header("Management Panel"), BorderLayout.NORTH);
        add(buildRoot(), BorderLayout.CENTER);
    }

    private JPanel buildRoot() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(new Color(240, 249, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));
        
        summary.add(summaryView(), BorderLayout.CENTER);
        content.setLayout(layout);
        buildViews();

        contentPanel.add(summary, BorderLayout.NORTH);
        contentPanel.add(content, BorderLayout.CENTER);

        return contentPanel;
    }

    private void buildViews() {
        content.removeAll();
        content.add(reportView(), REPORT);
        content.add(complaintView(), COMPLAINT);
        content.add(requestView(), REQUEST);
        layout.show(content, view);
    }

    private JPanel reportView() {
        return viewPanel("Report Management", reportService.getAllReports(), this::reportCard);
    }

    private JPanel complaintView() {
        return viewPanel("Complaint Management", complaintService.getAllComplaints(), this::complaintCard);
    }

    private JPanel requestView() {
        return viewPanel("Request Management", requestService.getAllRequests(), this::requestCard);
    }

    private <T> JPanel viewPanel(String title, List<T> data, java.util.function.Function<T, JPanel> mapper) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        panel.add(header(title), BorderLayout.NORTH);

        JPanel grid = grid();
        data.forEach(item -> grid.add(mapper.apply(item)));

        panel.add(new CustomScrollPane(grid), BorderLayout.CENTER);
        return panel;
    }

    private JPanel grid() {
        JPanel g = new JPanel(new GridLayout(0, 2, 15, 15));
        g.setOpaque(false);
        g.setBorder(new EmptyBorder(10, 10, 10, 10));
        return g;
    }

    private JPanel baseCard(String title, String status) {
        JPanel card = Card.createCard();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setBackground(BACKGROUND_WHITE);

        JLabel t = new JLabel(title);
        t.setFont(HEADING_H3);

        String statusText = status == null || status.isEmpty() ? "Pending" : status;
        Color statusBg = getStatusColor(statusText);
        JLabel s = ComponentStyle.createCapsuleLabel(
                statusText,
                statusBg,
                PRIMARY_GREEN
        );

        JPanel top = ComponentStyle.createTransparentPanel(new BorderLayout());
        top.add(t, BorderLayout.WEST);
        top.add(s, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        return card;
    }

    private JPanel reportCard(Report r) {
        JPanel c = baseCard("Report #" + r.getReportId(), r.getStatus());
        JPanel b = vertical();
        b.add(new JLabel("Date: " + r.getCreatedAt()));
        b.add(new JLabel("Message: " + safe(r.getMessage())));
        c.add(b, BorderLayout.CENTER);
        return c;
    }

    private JPanel complaintCard(Complaint c) {
        JPanel card = baseCard("Complaint #" + c.getComplaintId(), c.getStatus());
        JPanel b = vertical();
        b.add(new JLabel("Location: " + safe(c.getLocation())));
        b.add(new JLabel("Message: " + safe(c.getMessage())));
        card.add(b, BorderLayout.CENTER);
        return card;
    }

    private JPanel requestCard(Request r) {
        JPanel card = baseCard("Request #" + r.getRequestId(), r.getStatus());
        JPanel b = vertical();
        b.add(new JLabel("Type: " + safe(r.getType())));
        b.add(new JLabel("Location: " + safe(r.getLocation())));
        card.add(b, BorderLayout.CENTER);
        return card;
    }

    private JPanel header(String titleText) {
        JPanel h = Card.createCard();
        h.setLayout(new BorderLayout());
        h.setBorder(new EmptyBorder(10, 15, 10, 15));
        h.setBackground(PRIMARY_GREEN);

        JLabel title = new JLabel(titleText);
        title.setForeground(BACKGROUND_WHITE);
        title.setFont(HEADING_H3);

        JPanel nav = ComponentStyle.createTransparentPanel(new GridLayout(1, 3, 5, 0));
        nav.setOpaque(false);

        for (String v : new String[]{REPORT, COMPLAINT, REQUEST}) {
            CustomButton b = new CustomButton(v);
            b.addActionListener(e -> switchView(v));
            nav.add(b);
        }

        JPanel right = ComponentStyle.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(nav);

        h.add(title, BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    private JPanel summaryView() {
        if (REPORT.equals(view)) return summary(reportService.getAllReports(), "Resolved", "Pending");
        if (COMPLAINT.equals(view)) return summary(complaintService.getAllComplaints(), "Resolved", "Pending");
        return summary(requestService.getAllRequests(), "Approved", "Pending");
    }

    private <T> JPanel summary(
            List<T> list,
            String successStatus,
            String pendingStatus
    ) {

        int total = list.size();

        int success = (int) list.stream()
                .filter(item ->
                        successStatus.equalsIgnoreCase(((Report) item).getStatus())
                )
                .count();

        int pending = (int) list.stream()
                .filter(item ->
                        pendingStatus.equalsIgnoreCase(((Report) item).getStatus())
                )
                .count();

        return new SummaryCards(
                new String[]{
                        "Total",
                        successStatus,
                        pendingStatus
                },
                new int[]{
                        total,
                        success,
                        pending
                },
                new String[]{
                        "All",
                        successStatus,
                        pendingStatus
                },
                icons(),
                colors()
        );
    }

    private void switchView(String v) {
        view = v;
        refresh();
    }

    private void refresh() {
        UIHelper.runOnEDT(() -> {
            summary.removeAll();
            summary.add(summaryView(), BorderLayout.CENTER);
            buildViews();
            UIHelper.refreshComponent(this);
        });
    }

    private JPanel vertical() {
        JPanel p = ComponentStyle.createTransparentPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    private String safe(String v) {
        return v == null || v.trim().isEmpty() ? "" : v;
    }

    private Color getStatusColor(String status) {
        if (status == null) return STATUS_COMPLETED_BG;
        switch (status.toLowerCase()) {
            case "resolved":
            case "approved":
            case "completed":
                return STATUS_COMPLETED_BG;
            case "rejected":
            case "closed":
            case "cancelled":
                return STATUS_CANCELLED_BG;
            case "pending":
                return STATUS_PENDING_BG;
            case "in_progress":
            case "in progress":
                return STATUS_IN_PROGRESS_BG;
            default:
                return STATUS_COMPLETED_BG;
        }
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
}