package com.wastely.views.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.wastely.assets.Loader;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SearchBar extends JPanel {

    private final CustomTextField searchField;
    private final CustomComboBox<String> filterCombo;
    private final JButton filterButton;

    private Runnable onSearchChange;
    private SearchDialog searchDialog;
    private boolean enableShortcut = true;

    private AWTEventListener globalListener;

    public SearchBar(String placeholder) {

        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ================= SEARCH FIELD =================
        searchField = new CustomTextField(
                "search.png",
                placeholder,
                new Dimension(600, 45)
        );
        searchField.setCornerRadius(35);

        gbc.gridx = 0;
        gbc.weightx = 1;
        add(searchField, gbc);

        // ================= FILTER BUTTON =================
        filterButton = new JButton(Loader.loadIcon("filter.png", 38));
        filterButton.setFocusPainted(false);
        filterButton.setContentAreaFilled(false);
        filterButton.setBorder(null);
        filterButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ================= CUSTOM COMBO =================
        filterCombo = new CustomComboBox<>(new String[]{
                "All Status",
                "Scheduled",
                "Pending",
                "Completed",
                "Cancelled"
        });

        filterCombo.setPreferredSize(new Dimension(160, 38));
        filterCombo.setLightWeightPopupEnabled(false);

        JPanel filterGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterGroup.setOpaque(false);
        filterGroup.add(filterButton);
        filterGroup.add(filterCombo);

        gbc.gridx = 1;
        gbc.weightx = 0;
        add(filterGroup, gbc);

        initEvents();
        installGlobalShortcut();
        installSafeClickOutside(this);
    }

    // ================= EVENTS =================
    private void initEvents() {

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { triggerSearch(); }
            public void removeUpdate(DocumentEvent e) { triggerSearch(); }
            public void changedUpdate(DocumentEvent e) { triggerSearch(); }
        });

        searchField.addActionListener(e -> {
            triggerSearch();
            if (searchDialog != null) {
                searchDialog.openFromEnter(getText());
            }
        });

        // ✔ FIXED: combo now works properly
        filterCombo.addActionListener(e -> triggerSearch());
    }

    // ================= SHORTCUT =================
    private void installGlobalShortcut() {
        if (!enableShortcut) return;

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {

                    if (e.getID() != KeyEvent.KEY_PRESSED) return false;

                    if (e.getKeyChar() == '/' && !searchField.hasFocus()) {
                        SwingUtilities.invokeLater(() -> {
                            searchField.requestFocusInWindow();
                            searchField.setText("");
                        });
                        return true;
                    }
                    return false;
                });
    }

    // ================= SAFE CLICK OUTSIDE =================
    private void installSafeClickOutside(JComponent root) {

        globalListener = event -> {

            if (!(event instanceof MouseEvent)) return;

            MouseEvent me = (MouseEvent) event;

            if (me.getID() != MouseEvent.MOUSE_PRESSED) return;
            if (!root.isShowing()) return;

            // ✔ FIX: Check if any combobox popup is showing
            if (filterCombo.isPopupVisible()) return;

            // ✔ FIX: Check if the component at click location is within root
            Point clickPoint = new Point(me.getX(), me.getY());
            SwingUtilities.convertPointFromScreen(clickPoint, root);
            
            if (clickPoint.x < 0 || clickPoint.y < 0 || 
                clickPoint.x > root.getWidth() || 
                clickPoint.y > root.getHeight()) {
                return; // Click is outside root bounds
            }

            Component source = SwingUtilities.getDeepestComponentAt(
                    root,
                    clickPoint.x,
                    clickPoint.y
            );

            // ✔ DO NOT interfere with combo box or popup
            if (isComboRelated(source)) return;

            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                    .clearGlobalFocusOwner();
        };

        Toolkit.getDefaultToolkit()
                .addAWTEventListener(globalListener, AWTEvent.MOUSE_EVENT_MASK);
    }

    private boolean isComboRelated(Component c) {

        if (c == null) return false;

        if (c instanceof JComboBox) return true;
        
        if (c instanceof JButton && c.getParent() instanceof JComboBox) return true;

        if (SwingUtilities.getAncestorOfClass(JComboBox.class, c) != null) return true;

        String name = c.getClass().getName();

        return name.contains("ComboPopup") ||
               name.contains("BasicComboPopup") ||
               name.contains("MetalComboBoxButton");
    }

    // ================= CORE =================
    private void triggerSearch() {

        if (onSearchChange != null) {
            onSearchChange.run();
        }

        if (searchDialog != null) {
            searchDialog.updateQuery(getText());
        }
    }

    // ================= API =================
    public String getText() {
        return searchField.getText().trim();
    }

    public String getSelectedFilter() {
        return filterCombo.getSelectedItem() != null
                ? filterCombo.getSelectedItem().toString()
                : "";
    }

    public void setFilters(List<String> filters) {
        filterCombo.removeAllItems();
        for (String f : filters) {
            filterCombo.addItem(f);
        }
    }

    public void setOnSearchChange(Runnable onSearchChange) {
        this.onSearchChange = onSearchChange;
    }

    public CustomTextField getField() {
        return searchField;
    }

    public void attachSearchDialog(SearchDialog searchDialog) {
        this.searchDialog = searchDialog;
    }

    // ================= PAINT =================
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);

        g2.setColor(new Color(225, 225, 225));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 35, 35);

        g2.dispose();
        super.paintComponent(g);
    }
}