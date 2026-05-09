package com.wastely.views.pages.menro.forms;

import com.wastely.model.Personnel;
import com.wastely.model.Team;
import com.wastely.model.Truck;
import com.wastely.service.PersonnelService;
import com.wastely.service.TeamService;
import com.wastely.service.TruckService;
import com.wastely.store.DataChangeBus;
import com.wastely.store.DataTopics;
import com.wastely.utils.UINotificationHelper;
import com.wastely.views.components.CustomButton;
import com.wastely.views.components.CustomComboBox;
import com.wastely.views.components.CustomTextField;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeamFormDialog extends JDialog {

    private static final Dimension FIELD_SIZE = new Dimension(420, 46);
    private static final int MAX_COLLECTORS = 6;

    private final TeamService teamService = new TeamService();
    private final TruckService truckService = new TruckService();
    private final PersonnelService personnelService = new PersonnelService();

    private final CustomTextField teamNameField = createTextField("Enter team name");
    private final CustomComboBox<Personnel> leaderComboBox = createPersonnelComboBox();
    private final CustomComboBox<Personnel> driverComboBox = createPersonnelComboBox();
    private final CustomComboBox<Truck> truckComboBox = createTruckComboBox();
    private final CustomComboBox<String> statusComboBox = createStatusComboBox();

    private final JPanel collectorPanel = new JPanel();
    private final JLabel collectorCountLabel = new JLabel();
    private final Map<Integer, JCheckBox> collectorMap = new LinkedHashMap<>();
    private final List<Personnel> personnelPool = new ArrayList<>();

    private final Team existingTeam;
    private final boolean editMode;

    public TeamFormDialog(Frame parent, Team team) {

        super(parent, true);

        this.existingTeam = team;
        this.editMode = team != null;

        setTitle(editMode ? "Edit Team" : "Add Team");
        setSize(760, 620);
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        loadData();
        initUI();
        buildCollectors();
        initializeDefaults();
        populateData();
        bindEvents();
        refreshCollectorAvailability();
        updateCollectorCount();
    }

    private void initUI() {

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 245, 245));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setOpaque(false);
        content.add(buildCollectorsSection(), BorderLayout.WEST);
        content.add(buildFormSection(), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        CustomButton save = new CustomButton(editMode ? "Update Team" : "Save Team");
        save.addActionListener(e -> saveTeam());
        actions.add(save);

        main.add(content, BorderLayout.CENTER);
        main.add(actions, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private JPanel buildCollectorsSection() {

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(240, 450));

        JLabel title = new JLabel("Collectors (Max " + MAX_COLLECTORS + ")");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));

        collectorCountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(collectorCountLabel, BorderLayout.EAST);

        collectorPanel.setLayout(new BoxLayout(collectorPanel, BoxLayout.Y_AXIS));
        collectorPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(collectorPanel);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

        panel.add(header, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildFormSection() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 12, 0);

        addField(panel, g, "Team Name", teamNameField);
        addField(panel, g, "Assigned Truck", truckComboBox);
        addField(panel, g, "Leader", leaderComboBox);
        addField(panel, g, "Driver", driverComboBox);
        addField(panel, g, "Status", statusComboBox);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints g, String label, JComponent comp) {

        JPanel wrap = new JPanel(new BorderLayout(0, 6));
        wrap.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));

        wrap.add(l, BorderLayout.NORTH);
        wrap.add(comp, BorderLayout.CENTER);

        panel.add(wrap, g);
        g.gridy++;
    }

    private CustomTextField createTextField(String p) {
        CustomTextField f = new CustomTextField(null, p, FIELD_SIZE);
        apply(f);
        return f;
    }

    private CustomComboBox<Personnel> createPersonnelComboBox() {
        CustomComboBox<Personnel> c = new CustomComboBox<>(new Personnel[]{});
        apply(c);
        return c;
    }

    private CustomComboBox<Truck> createTruckComboBox() {
        CustomComboBox<Truck> c = new CustomComboBox<>(new Truck[]{});
        apply(c);
        return c;
    }

    private CustomComboBox<String> createStatusComboBox() {
        CustomComboBox<String> c = new CustomComboBox<>(new String[]{"Active", "Inactive"});
        apply(c);
        return c;
    }

    private void apply(JComponent c) {
        c.setPreferredSize(FIELD_SIZE);
        c.setMinimumSize(FIELD_SIZE);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_SIZE.height));
    }

    private void initializeDefaults() {
        statusComboBox.setSelectedItem("Active");
    }

    private void loadData() {
        loadPersonnel();
        loadTrucks();
    }

    private void loadPersonnel() {

        leaderComboBox.removeAllItems();
        driverComboBox.removeAllItems();
        personnelPool.clear();

        List<Personnel> list = personnelService.getAllUnassignedPersonnel();

        if (editMode && existingTeam != null) {
            includeExisting(list);
        }

        for (Personnel p : list) {
            if (p == null) continue;
            personnelPool.add(p);
            leaderComboBox.addItem(p);
            driverComboBox.addItem(p);
        }
    }

    private void loadTrucks() {

        truckComboBox.removeAllItems();

        List<Truck> trucks = truckService.getAllUnassignedTrucks();

        if (editMode && existingTeam.getTruckId() > 0) {
            Truck t = truckService.getTruckById(existingTeam.getTruckId());
            if (t != null) trucks.add(0, t);
        }

        for (Truck t : trucks) truckComboBox.addItem(t);
    }

    private void buildCollectors() {

        collectorPanel.removeAll();
        collectorMap.clear();

        for (Personnel p : personnelPool) {

            JCheckBox cb = new JCheckBox(p.getFullName());
            cb.setBackground(Color.WHITE);

            collectorMap.put(p.getId(), cb);
            collectorPanel.add(cb);

            cb.addActionListener(e -> {
                enforceLimit(cb);
                updateCollectorCount();
                refreshCollectorAvailability();
            });
        }
    }

    private void populateData() {

        if (!editMode || existingTeam == null) return;

        teamNameField.setText(existingTeam.getTeamName());
        statusComboBox.setSelectedItem(existingTeam.getStatus());

        select(leaderComboBox, existingTeam.getLeaderId());
        select(driverComboBox, existingTeam.getDriverId());
        select(truckComboBox, existingTeam.getTruckId());

        if (existingTeam.getCollectorIds() != null) {
            for (Integer id : existingTeam.getCollectorIds()) {
                JCheckBox cb = collectorMap.get(id);
                if (cb != null) cb.setSelected(true);
            }
        }
    }

    private void bindEvents() {
        leaderComboBox.addActionListener(e -> refreshCollectorAvailability());
        driverComboBox.addActionListener(e -> refreshCollectorAvailability());
    }

    private void refreshCollectorAvailability() {

        Personnel leader = (Personnel) leaderComboBox.getSelectedItem();
        Personnel driver = (Personnel) driverComboBox.getSelectedItem();

        for (Map.Entry<Integer, JCheckBox> e : collectorMap.entrySet()) {

            int id = e.getKey();
            JCheckBox cb = e.getValue();

            boolean lock = (leader != null && leader.getId() == id)
                    || (driver != null && driver.getId() == id);

            cb.setEnabled(!lock);
            if (lock) cb.setSelected(false);
        }
    }

    private void enforceLimit(JCheckBox cb) {
        if (getSelected().size() <= MAX_COLLECTORS) return;
        cb.setSelected(false);
        UINotificationHelper.showError(this, "Max collectors " + MAX_COLLECTORS);
    }

    private void updateCollectorCount() {
        collectorCountLabel.setText(getSelected().size() + "/" + MAX_COLLECTORS);
    }

    private List<Integer> getSelected() {
        List<Integer> ids = new ArrayList<>();
        for (Map.Entry<Integer, JCheckBox> e : collectorMap.entrySet()) {
            if (e.getValue().isSelected()) ids.add(e.getKey());
        }
        return ids;
    }

    private void saveTeam() {

        try {

            Team t = editMode ? existingTeam : new Team();

            Personnel leader = (Personnel) leaderComboBox.getSelectedItem();
            Personnel driver = (Personnel) driverComboBox.getSelectedItem();
            Truck truck = (Truck) truckComboBox.getSelectedItem();

            t.setTeamName(teamNameField.getText().trim());
            t.setStatus(String.valueOf(statusComboBox.getSelectedItem()));

            if (leader != null) {
                t.setLeaderId(leader.getId());
                t.setLeaderName(leader.getFullName());
                personnelService.updatePersonnelRole(leader.getId(), "SUPERVISOR");
            }

            if (driver != null) {
                t.setDriverId(driver.getId());
                t.setDriverName(driver.getFullName());
                personnelService.updatePersonnelRole(driver.getId(), "DRIVER");
            }

            if (truck != null) {
                t.setTruckId(truck.getId());
            }

            t.setCollectorIds(getSelected());

            for (Integer id : getSelected()) {
                personnelService.updatePersonnelRole(id, "COLLECTOR");
            }

            boolean ok = editMode ? teamService.updateTeam(t) : teamService.addTeam(t);

            if (!ok) {
                UINotificationHelper.showError(this, "Failed to save team");
                return;
            }

            DataChangeBus.publish(DataTopics.TEAMS);
            UINotificationHelper.showSuccess(this, "Saved");
            dispose();

        } catch (Exception e) {
            UINotificationHelper.showError(this, e.getMessage());
        }
    }

    private void select(JComboBox<?> box, int id) {
        if (id <= 0) return;

        for (int i = 0; i < box.getItemCount(); i++) {
            Object o = box.getItemAt(i);
            if (o instanceof Personnel p && p.getId() == id) box.setSelectedIndex(i);
            if (o instanceof Truck t && t.getId() == id) box.setSelectedIndex(i);
        }
    }

    private void includeExisting(List<Personnel> list) {
        if (existingTeam == null) return;
        add(list, existingTeam.getLeaderId());
        add(list, existingTeam.getDriverId());
        if (existingTeam.getCollectorIds() != null) {
            for (Integer id : existingTeam.getCollectorIds()) add(list, id);
        }
    }

    private void add(List<Personnel> list, int id) {
        if (id <= 0) return;
        boolean exists = list.stream().anyMatch(p -> p.getId() == id);
        if (!exists) {
            Personnel p = personnelService.getPersonnelById(id);
            if (p != null) list.add(p);
        }
    }
}