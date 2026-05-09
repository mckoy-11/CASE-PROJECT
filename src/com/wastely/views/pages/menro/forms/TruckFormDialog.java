package com.wastely.views.pages.menro.forms;

import com.wastely.model.Barangay;
import com.wastely.model.Truck;
import com.wastely.service.BarangayService;
import com.wastely.service.TruckService;
import com.wastely.store.DataChangeBus;
import com.wastely.store.DataTopics;
import com.wastely.utils.UINotificationHelper;
import com.wastely.views.components.CustomButton;
import com.wastely.views.components.CustomComboBox;
import com.wastely.views.components.CustomTextField;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class TruckFormDialog extends JDialog {

    private static final Dimension FIELD_SIZE =
            new Dimension(420, 46);

    private final TruckService truckService =
            new TruckService();

    private final BarangayService barangayService =
            new BarangayService();

    private final CustomTextField plateNumberField =
            createTextField("Enter plate number");

    private final CustomTextField capacityField =
            createTextField("e.g. 5000 kg");

    private final CustomComboBox<String> truckTypeComboBox =
            createTruckTypeComboBox();

    private final CustomComboBox<Barangay> barangayComboBox =
            createBarangayComboBox();

    private final CustomComboBox<String> statusComboBox =
            createStatusComboBox();

    private final Truck existingTruck;

    private final boolean editMode;

    public TruckFormDialog(
            Frame parent,
            Truck truck
    ) {

        super(parent, true);

        this.existingTruck = truck;
        this.editMode = truck != null;

        initializeDialog(parent);

        initUI();

        loadBarangays();

        initializeDefaults();

        populateData();
    }

    private void initializeDialog(Frame parent) {

        setTitle(
                editMode
                        ? "Edit Truck"
                        : "Add Truck"
        );

        setSize(560, 620);

        setResizable(false);

        setLocationRelativeTo(parent);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        mainPanel.setBackground(
                new Color(245, 245, 245)
        );

        JPanel formPanel =
                new JPanel(new GridBagLayout());

        formPanel.setOpaque(false);

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(8, 0, 8, 0);

        addField(
                formPanel,
                gbc,
                "Plate Number",
                plateNumberField
        );

        addField(
                formPanel,
                gbc,
                "Truck Type",
                truckTypeComboBox
        );

        addField(
                formPanel,
                gbc,
                "Capacity",
                capacityField
        );

        addField(
                formPanel,
                gbc,
                "Assigned Barangay",
                barangayComboBox
        );

        addField(
                formPanel,
                gbc,
                "Status",
                statusComboBox
        );

        JPanel actionPanel =
                new JPanel(
                        new FlowLayout(FlowLayout.RIGHT)
                );

        actionPanel.setOpaque(false);

        actionPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        20,
                        20,
                        20
                )
        );

        CustomButton saveButton =
                new CustomButton(
                        editMode
                                ? "Update Truck"
                                : "Save Truck"
                );

        saveButton.addActionListener(
                event -> saveTruck()
        );

        actionPanel.add(saveButton);

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                actionPanel,
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

        JPanel wrapper =
                new JPanel(
                        new BorderLayout(0, 6)
                );

        wrapper.setOpaque(false);

        JLabel fieldLabel =
                new JLabel(label);

        fieldLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        wrapper.add(
                fieldLabel,
                BorderLayout.NORTH
        );

        wrapper.add(
                component,
                BorderLayout.CENTER
        );

        panel.add(wrapper, gbc);

        gbc.gridy++;
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

        applyFieldSize(field);

        return field;
    }

    private CustomComboBox<String> createTruckTypeComboBox() {

        CustomComboBox<String> comboBox =
                new CustomComboBox<>(
                        new String[]{
                                "Compactor",
                                "Dump Truck",
                                "Garbage Truck",
                                "Container Carrier",
                                "Other"
                        }
                );

        applyFieldSize(comboBox);

        return comboBox;
    }

    private CustomComboBox<Barangay> createBarangayComboBox() {

        CustomComboBox<Barangay> comboBox =
                new CustomComboBox<>(
                        new Barangay[]{}
                );

        applyFieldSize(comboBox);

        return comboBox;
    }

    private CustomComboBox<String> createStatusComboBox() {

        CustomComboBox<String> comboBox =
                new CustomComboBox<>(
                        new String[]{
                                "Active",
                                "Maintenance",
                                "Inactive"
                        }
                );

        applyFieldSize(comboBox);

        return comboBox;
    }

    private void applyFieldSize(
            JComponent component
    ) {

        component.setPreferredSize(FIELD_SIZE);

        component.setMinimumSize(FIELD_SIZE);

        component.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        FIELD_SIZE.height
                )
        );
    }

    private void initializeDefaults() {

        truckTypeComboBox.setSelectedItem(
                "Garbage Truck"
        );

        statusComboBox.setSelectedItem(
                editMode
                        ? safe(
                        existingTruck.getStatus(),
                        "Active"
                )
                        : "Active"
        );
    }

    private void loadBarangays() {

        barangayComboBox.removeAllItems();

        List<Barangay> barangays =
                barangayService.getAllBarangays();

        for (Barangay barangay : barangays) {
            barangayComboBox.addItem(barangay);
        }
    }

    private void populateData() {

        if (!editMode || existingTruck == null) {
            return;
        }

        plateNumberField.setText(
                safe(existingTruck.getPlateNumber())
        );

        capacityField.setText(
                safe(existingTruck.getCapacity())
        );

        truckTypeComboBox.setSelectedItem(
                safe(
                        existingTruck.getTruckType(),
                        "Garbage Truck"
                )
        );

        statusComboBox.setSelectedItem(
                safe(
                        existingTruck.getStatus(),
                        "Active"
                )
        );

        selectBarangay();
    }

    private void selectBarangay() {

        for (
                int i = 0;
                i < barangayComboBox.getItemCount();
                i++
        ) {

            Barangay barangay =
                    barangayComboBox.getItemAt(i);

            if (
                    barangay != null
                            && Objects.equals(
                            safe(barangay.getBarangayName()),
                            safe(existingTruck.getAssignedBarangay())
                    )
            ) {

                barangayComboBox.setSelectedIndex(i);

                return;
            }
        }
    }

    private void saveTruck() {

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

            Truck truck =
                    editMode
                            ? existingTruck
                            : new Truck();

            truck.setPlateNumber(
                    safe(
                            plateNumberField.getText()
                    ).toUpperCase()
            );

            truck.setTruckType(
                    String.valueOf(
                            truckTypeComboBox.getSelectedItem()
                    )
            );

            truck.setCapacity(
                    safe(
                            capacityField.getText()
                    )
            );

            truck.setStatus(
                    String.valueOf(
                            statusComboBox.getSelectedItem()
                    )
            );

            Barangay barangay =
                    (Barangay) barangayComboBox.getSelectedItem();

            if (barangay != null) {

                truck.setAssignedBarangay(
                        safe(
                                barangay.getBarangayName()
                        )
                );
            }

            boolean success =
                    editMode
                            ? truckService.updateTruck(truck)
                            : truckService.addTruck(truck);

            if (!success) {

                UINotificationHelper.showError(
                        this,
                        "Save Failed",
                        "Failed to save truck."
                );

                return;
            }

            UINotificationHelper.showSuccess(
                    this,
                    editMode
                            ? "Truck updated successfully."
                            : "Truck added successfully."
            );

            SwingUtilities.invokeLater(() -> {

                DataChangeBus.publish(
                        DataTopics.TRUCKS
                );

                dispose();
            });

        } catch (Exception exception) {

            exception.printStackTrace();

            UINotificationHelper.showError(
                    this,
                    "System Error",
                    "Error saving truck: "
                            + exception.getMessage()
            );
        }
    }

    private String validateForm() {

        if (
                safe(
                        plateNumberField.getText()
                ).isEmpty()
        ) {
            return "Plate number is required.";
        }

        if (
                safe(
                        capacityField.getText()
                ).isEmpty()
        ) {
            return "Capacity is required.";
        }

        if (
                barangayComboBox.getSelectedItem()
                        == null
        ) {
            return "Please select a barangay.";
        }

        return null;
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value.trim();
    }

    private String safe(
            String value,
            String fallback
    ) {

        return value == null
                || value.trim().isEmpty()
                ? fallback
                : value.trim();
    }
}