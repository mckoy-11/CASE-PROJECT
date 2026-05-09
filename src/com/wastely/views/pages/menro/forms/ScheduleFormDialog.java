package com.wastely.views.pages.menro.forms;

import com.wastely.model.*;
import com.wastely.service.*;

import com.wastely.utils.UINotificationHelper;
import com.wastely.views.components.*;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.List;
import java.util.Objects;

public class ScheduleFormDialog extends JDialog {
        private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
        private static final Dimension FIELD_SIZE = new Dimension(420, 46);
        private static final Dimension HALF_FIELD_SIZE = new Dimension(200, 46);

        private final ScheduleService scheduleService = new ScheduleService();
        private final BarangayService barangayService = new BarangayService();
        private final TeamService teamService = new TeamService();
        private final TruckService truckService = new TruckService();

        private final CustomComboBox<Barangay> barangayCombo = createBarangayComboBox();
        private final CustomComboBox<Team> teamCombo = createTeamComboBox();
        private final CustomComboBox<Truck> truckCombo = createTruckComboBox();
        private final CustomComboBox<String> statusCombo = createStatusComboBox();

        private final CustomTextField adminField = createTextField("Enter admin name");
        private final CustomTextField contactField = createTextField("0912-345-6789");
        private final CustomTextField dateField = createHalfTextField("yyyy-MM-dd");
        private final CustomTextField timeField = createHalfTextField("HH:mm");

        private final Schedule schedule;
        private final boolean editMode;

        public ScheduleFormDialog(JFrame parent,  Schedule schedule) {
                super(parent, true);

                this.schedule = schedule;
                this.editMode = schedule != null;

                setTitle(editMode ? "Edit Schedule" : "Add Schedule");
                setSize(560, 760);
                setLocationRelativeTo(parent);
                setResizable(false);

                initUI();
                loadBarangays();
                loadTeams();
                loadTrucks();
                initializeDefaults();

                if (editMode) {populateData();}
        }

        private void initUI() {
                JPanel mainPanel =new JPanel(new BorderLayout());
                mainPanel.setBackground(new Color(245, 245, 245));

                JPanel formPanel = new JPanel(new GridBagLayout());
                formPanel.setOpaque(false);
                formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                GridBagConstraints gbc =new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(8, 0, 8, 0);
                addField(formPanel, gbc, "Barangay", barangayCombo);

                addField(
                        formPanel,
                        gbc,
                        "Admin Name",
                        adminField
                );

                addField(
                        formPanel,
                        gbc,
                        "Contact Number",
                        contactField
                );

                addField(
                        formPanel,
                        gbc,
                        "Team",
                        teamCombo
                );

                addField(
                        formPanel,
                        gbc,
                        "Truck",
                        truckCombo
                );

                JPanel dateTimePanel =
                        new JPanel(
                                new GridLayout(
                                        1,
                                        2,
                                        12,
                                        0
                                )
                        );

                dateTimePanel.setOpaque(false);

                JPanel dateWrapper =
                        createFieldWrapper(
                                "Date",
                                dateField
                        );

                JPanel timeWrapper =
                        createFieldWrapper(
                                "Time",
                                timeField
                        );

                dateTimePanel.add(dateWrapper);
                dateTimePanel.add(timeWrapper);

                gbc.gridy++;

                formPanel.add(dateTimePanel, gbc);

                addField(
                        formPanel,
                        gbc,
                        "Status",
                        statusCombo
                );

                CustomButton saveButton =
                        new CustomButton(
                                editMode
                                        ? "Update Schedule"
                                        : "Save Schedule"
                        );

                saveButton.addActionListener(
                        event -> saveSchedule()
                );

                JPanel bottomPanel =
                        new JPanel(
                                new FlowLayout(
                                        FlowLayout.RIGHT
                                )
                        );

                bottomPanel.setOpaque(false);

                bottomPanel.setBorder(
                        BorderFactory.createEmptyBorder(
                                10,
                                20,
                                20,
                                20
                        )
                );

                bottomPanel.add(saveButton);

                mainPanel.add(
                        formPanel,
                        BorderLayout.CENTER
                );

                mainPanel.add(
                        bottomPanel,
                        BorderLayout.SOUTH
                );

                setContentPane(mainPanel);
        }

        private void addField(
                JPanel panel,
                GridBagConstraints gbc,
                String label,
                JComponent component
        ) {

                gbc.gridy++;

                panel.add(
                        createFieldWrapper(
                                label,
                                component
                        ),
                        gbc
                );
        }

        private JPanel createFieldWrapper(
                String label,
                JComponent component
        ) {

                JPanel wrapper =
                        new JPanel(
                                new BorderLayout(0, 6)
                        );

                wrapper.setOpaque(false);

                JLabel lbl =
                        new JLabel(label);

                lbl.setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                13
                        )
                );

                lbl.setPreferredSize(
                        new Dimension(100, 20)
                );

                wrapper.add(
                        lbl,
                        BorderLayout.NORTH
                );

                wrapper.add(
                        component,
                        BorderLayout.CENTER
                );

                return wrapper;
        }

        private CustomComboBox<Barangay>
        createBarangayComboBox() {

                CustomComboBox<Barangay> comboBox =
                        new CustomComboBox<>(
                                new Barangay[]{}
                        );

                applyFieldSize(
                        comboBox,
                        FIELD_SIZE
                );

                return comboBox;
        }

        private CustomComboBox<Team>
        createTeamComboBox() {

                CustomComboBox<Team> comboBox =
                        new CustomComboBox<>(
                                new Team[]{}
                        );

                applyFieldSize(
                        comboBox,
                        FIELD_SIZE
                );

                return comboBox;
        }

        private CustomComboBox<Truck>
        createTruckComboBox() {

                CustomComboBox<Truck> comboBox =
                        new CustomComboBox<>(
                                new Truck[]{}
                        );

                applyFieldSize(
                        comboBox,
                        FIELD_SIZE
                );

                return comboBox;
        }

        private CustomComboBox<String>
        createStatusComboBox() {

                CustomComboBox<String> comboBox =
                        new CustomComboBox<>(
                                new String[]{
                                        "SCHEDULED",
                                        "PENDING",
                                        "COMPLETED",
                                        "CANCELLED"
                                }
                        );

                applyFieldSize(
                        comboBox,
                        FIELD_SIZE
                );

                return comboBox;
        }

        private CustomTextField createTextField(
                String placeholder
        ) {

                CustomTextField field =
                        new CustomTextField(
                                null,
                                placeholder,
                                FIELD_SIZE
                        );

                applyFieldSize(
                        field,
                        FIELD_SIZE
                );

                return field;
        }

        private CustomTextField createHalfTextField(
                String placeholder
        ) {

                CustomTextField field =
                        new CustomTextField(
                                null,
                                placeholder,
                                HALF_FIELD_SIZE
                        );

                applyFieldSize(
                        field,
                        HALF_FIELD_SIZE
                );

                return field;
        }

        private void applyFieldSize(
                JComponent component,
                Dimension size
        ) {

                component.setPreferredSize(size);

                component.setMinimumSize(size);

                component.setMaximumSize(
                        new Dimension(
                                Integer.MAX_VALUE,
                                size.height
                        )
                );
        }

        private void initializeDefaults() {

                dateField.setText(
                        LocalDate.now()
                                .format(DATE_FORMAT)
                );

                timeField.setText("08:00");

                statusCombo.setSelectedItem(
                        "SCHEDULED"
                );

                barangayCombo.addActionListener(event -> {

                        Barangay barangay = (Barangay) barangayCombo.getSelectedItem();

                        if (barangay == null) {
                                return;
                        }

                        if (!editMode) {
                                adminField.setText(safe(barangayService.getAdminNameByBarangay(barangay)));
                                contactField.setText(safe(barangay.getContact()));
                        }
                });
        }

        private void loadBarangays() {
                barangayCombo.removeAllItems();

                List<Barangay> barangays = barangayService.getAllBarangays();
                for (Barangay barangay : barangays) {
                        barangayCombo.addItem(barangay);
                }
        }

        private void loadTeams() {

                teamCombo.removeAllItems();

                List<Team> teams =
                        teamService.getAllTeams();

                for (Team team : teams) {

                teamCombo.addItem(team);
                }
        }

        private void loadTrucks() {

                truckCombo.removeAllItems();

                List<Truck> trucks = truckService.getAllTrucks();

                for (Truck truck : trucks) {

                truckCombo.addItem(truck);
                }
        }

        private void populateData() {

                adminField.setText(
                        safe(
                                schedule.getBarangayAdmin()
                        )
                );

                contactField.setText(
                        safe(
                                schedule.getContactNumber()
                        )
                );

                if (schedule.getDate() != null) {

                dateField.setText(
                        schedule.getDate()
                                .format(DATE_FORMAT)
                );
                }

                if (schedule.getTime() != null) {

                timeField.setText(
                        schedule.getTime()
                                .format(TIME_FORMAT)
                );
                }

                statusCombo.setSelectedItem(
                        safe(schedule.getStatus())
                );

                selectBarangay();
                selectTeam();
                selectTruck();
        }

        private void selectBarangay() {

                for (
                        int i = 0;
                        i < barangayCombo.getItemCount();
                        i++
                ) {

                Barangay barangay =
                        barangayCombo.getItemAt(i);

                if (barangay == null) {
                        continue;
                }

                if (
                        Objects.equals(
                                barangay.getBarangayName(),
                                schedule.getBarangayName()
                        )
                ) {

                        barangayCombo.setSelectedIndex(i);

                        return;
                }
                }
        }

        private void selectTeam() {

                for (
                        int i = 0;
                        i < teamCombo.getItemCount();
                        i++
                ) {

                Team team =
                        teamCombo.getItemAt(i);

                if (team == null) {
                        continue;
                }

                if (
                        Objects.equals(
                                team.getTeamName(),
                                schedule.getCollectorTeam()
                        )
                ) {

                        teamCombo.setSelectedIndex(i);

                        return;
                }
                }
        }

        private void selectTruck() {

                for (
                        int i = 0;
                        i < truckCombo.getItemCount();
                        i++
                ) {

                Truck truck =
                        truckCombo.getItemAt(i);

                if (truck == null) {
                        continue;
                }

                if (
                        Objects.equals(
                                truck.getPlateNumber(),
                                schedule.getTruckPlateNumber()
                        )
                ) {

                        truckCombo.setSelectedIndex(i);

                        return;
                }
                }
        }

        private void saveSchedule() {

                try {

                String validation =
                        validateForm();

                if (validation != null) {

                        UINotificationHelper.showError(
                                this,
                                "Validation Error",
                                validation
                        );

                        return;
                }

                Schedule s =
                        editMode
                                ? schedule
                                : new Schedule();

                Barangay barangay =
                        (Barangay)
                                barangayCombo
                                        .getSelectedItem();

                Team team =
                        (Team)
                                teamCombo
                                        .getSelectedItem();

                Truck truck =
                        (Truck)
                                truckCombo
                                        .getSelectedItem();

                LocalDate date =
                        LocalDate.parse(
                                dateField.getText().trim(),
                                DATE_FORMAT
                        );

                LocalTime time =
                        LocalTime.parse(
                                timeField.getText().trim(),
                                TIME_FORMAT
                        );

                s.setBarangayName(
                        safe(
                                barangay
                                        .getBarangayName()
                        )
                );

                s.setBarangayAdmin(
                        safe(
                                adminField.getText()
                        )
                );

                s.setContactNumber(
                        safe(
                                contactField.getText()
                        )
                );

                s.setCollectorTeam(
                        safe(
                                team.getTeamName()
                        )
                );

                s.setTruckPlateNumber(
                        safe(
                                truck.getPlateNumber()
                        )
                );

                s.setDate(date);

                s.setTime(time);

                s.setStatus(
                        String.valueOf(
                                statusCombo.getSelectedItem()
                        )
                );

                boolean success;

                if (editMode) {

                        success =
                                scheduleService
                                        .updateSchedule(s);

                } else {

                        success =
                                scheduleService
                                        .saveSchedule(s);
                }

                if (success) {

                        UINotificationHelper.showSuccess(
                                this,
                                editMode
                                        ? "Schedule updated successfully"
                                        : "Schedule added successfully"
                        );

                        SwingUtilities.invokeLater(
                                this::dispose
                        );

                } else {

                        UINotificationHelper.showError(
                                this,
                                "Failed to save schedule. Please try again."
                        );
                }

                } catch (DateTimeParseException exception) {

                UINotificationHelper.showError(
                        this,
                        "Validation Error",
                        "Invalid date/time format.\nUse yyyy-MM-dd and HH:mm"
                );

                } catch (Exception exception) {

                UINotificationHelper.showError(
                        this,
                        "Error",
                        "An unexpected error occurred: "
                                + exception.getMessage()
                );
                }
        }

        private String validateForm() {
                if (barangayCombo.getSelectedItem() == null) return "Please select barangay.";
                if (teamCombo.getSelectedItem() == null)     return "Please select team.";
                if (truckCombo.getSelectedItem() == null)    return "Please select truck.";
                if (safe(dateField.getText()).isEmpty())     return "Date is required.";
                if (safe(timeField.getText()).isEmpty())     return "Time is required.";
                return null;
        }

        private String safe(String value) {return value == null ? "" : value.trim();}
}