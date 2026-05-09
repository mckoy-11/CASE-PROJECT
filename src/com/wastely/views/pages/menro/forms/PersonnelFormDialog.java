package com.wastely.views.pages.menro.forms;

import com.wastely.model.Personnel;
import com.wastely.model.Team;
import com.wastely.service.PersonnelService;
import com.wastely.service.TeamService;
import com.wastely.store.DataChangeBus;
import com.wastely.store.DataTopics;
import com.wastely.utils.UINotificationHelper;
import com.wastely.views.components.CustomButton;
import com.wastely.views.components.CustomComboBox;
import com.wastely.views.components.CustomTextField;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PersonnelFormDialog extends JDialog {

    private static final Dimension FIELD_SIZE = new Dimension(420, 46);
    private static final Dimension HALF_FIELD_SIZE = new Dimension(200, 46);

    private final PersonnelService personnelService = new PersonnelService();
    private final TeamService teamService = new TeamService();

    private final Personnel existingPersonnel;
    private final boolean editMode;

    private final CustomTextField fullNameField = createTextField("Enter full name");
    private final CustomTextField ageField = createHalfTextField("25");
    private final CustomComboBox<String> genderComboBox = createGenderComboBox();
    private final CustomTextField addressField = createTextField("Enter address");
    private final CustomTextField phoneNumberField = createTextField("0912-345-6789");
    private final CustomComboBox<String> roleComboBox = createRoleComboBox();
    private final CustomComboBox<Team> teamComboBox = createTeamComboBox();
    private final CustomComboBox<String> statusComboBox = createStatusComboBox();

    public PersonnelFormDialog(Frame parent, Personnel personnel) {
        super(parent, true);

        this.existingPersonnel = personnel;
        this.editMode = personnel != null;

        setTitle(editMode ? "Edit Personnel" : "Add Personnel");
        setSize(560, 720);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        loadTeams();
        initializeDefaults();
        populateData();
    }

    private void initUI() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        addField(formPanel, gbc, "Full Name", fullNameField);
        addRow(formPanel, gbc, "Age", ageField, "Gender", genderComboBox);
        addField(formPanel, gbc, "Phone Number", phoneNumberField);
        addField(formPanel, gbc, "Address", addressField);
        addField(formPanel, gbc, "Role", roleComboBox);
        addField(formPanel, gbc, "Team", teamComboBox);
        addField(formPanel, gbc, "Status", statusComboBox);

        CustomButton saveButton = new CustomButton(editMode ? "Update Personnel" : "Save Personnel");
        saveButton.addActionListener(e -> savePersonnel());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottom.add(saveButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(bottom, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent comp) {
        panel.add(wrap(label, comp), gbc);
        gbc.gridy++;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String l1, JComponent c1, String l2, JComponent c2) {

        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(false);

        row.add(wrap(l1, c1));
        row.add(wrap(l2, c2));

        panel.add(row, gbc);
        gbc.gridy++;
    }

    private JPanel wrap(String label, JComponent comp) {

        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));

        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);

        return p;
    }

    private CustomTextField createTextField(String ph) {
        CustomTextField f = new CustomTextField(null, ph, FIELD_SIZE);
        apply(f, FIELD_SIZE);
        return f;
    }

    private CustomTextField createHalfTextField(String ph) {
        CustomTextField f = new CustomTextField(null, ph, HALF_FIELD_SIZE);
        apply(f, HALF_FIELD_SIZE);
        return f;
    }

    private CustomComboBox<String> createGenderComboBox() {
        CustomComboBox<String> c = new CustomComboBox<>(new String[]{"Male", "Female", "Other"});
        apply(c, FIELD_SIZE);
        return c;
    }

    private CustomComboBox<String> createRoleComboBox() {
        CustomComboBox<String> c = new CustomComboBox<>(new String[]{"Driver", "Collector", "Lead"});
        apply(c, FIELD_SIZE);
        return c;
    }

    private CustomComboBox<Team> createTeamComboBox() {
        CustomComboBox<Team> c = new CustomComboBox<>(new Team[]{});
        apply(c, FIELD_SIZE);
        return c;
    }

    private CustomComboBox<String> createStatusComboBox() {
        CustomComboBox<String> c = new CustomComboBox<>(new String[]{"Active", "Inactive"});
        apply(c, FIELD_SIZE);
        return c;
    }

    private void apply(JComponent c, Dimension d) {
        c.setPreferredSize(d);
        c.setMinimumSize(d);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, d.height));
    }

    private void initializeDefaults() {
        genderComboBox.setSelectedItem("Male");
        roleComboBox.setSelectedItem("Collector");
        statusComboBox.setSelectedItem("Active");
    }

    private void loadTeams() {
        teamComboBox.removeAllItems();
        for (Team t : teamService.getAllTeams()) {
            teamComboBox.addItem(t);
        }
    }

    private void populateData() {

        if (!editMode || existingPersonnel == null) return;

        fullNameField.setText(existingPersonnel.getFullName());
        ageField.setText(String.valueOf(existingPersonnel.getAge()));
        phoneNumberField.setText(existingPersonnel.getPhoneNumber());
        addressField.setText(existingPersonnel.getAddress());

        genderComboBox.setSelectedItem(existingPersonnel.getGender());
        roleComboBox.setSelectedItem(existingPersonnel.getRole());
        statusComboBox.setSelectedItem(existingPersonnel.getStatus());

        for (int i = 0; i < teamComboBox.getItemCount(); i++) {
            Team t = teamComboBox.getItemAt(i);
            if (t != null && Objects.equals(t.getTeamName(), existingPersonnel.getTeam())) {
                teamComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void savePersonnel() {

        try {

            Personnel p = editMode ? existingPersonnel : new Personnel();

            p.setFullName(fullNameField.getText().trim());
            p.setPhoneNumber(phoneNumberField.getText().trim());
            p.setAddress(addressField.getText().trim());

            p.setAge(Integer.parseInt(ageField.getText().trim()));

            p.setGender(String.valueOf(genderComboBox.getSelectedItem()));
            p.setRole(String.valueOf(roleComboBox.getSelectedItem()));
            p.setStatus(String.valueOf(statusComboBox.getSelectedItem()));

            Team t = (Team) teamComboBox.getSelectedItem();
            p.setTeam(t != null ? t.getTeamName() : "");

            boolean ok = editMode
                    ? personnelService.updatePersonnel(p)
                    : personnelService.addPersonnel(p);

            if (!ok) {
                UINotificationHelper.showError(this, "Save Failed", "Unable to save personnel");
                return;
            }

            DataChangeBus.publish(DataTopics.PERSONNEL);
            UINotificationHelper.showSuccess(this, "Saved successfully");

            dispose();

        } catch (Exception e) {
            UINotificationHelper.showError(this, "Error", e.getMessage());
        }
    }
}