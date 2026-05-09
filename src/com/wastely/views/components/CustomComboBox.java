package com.wastely.views.components;

import com.wastely.assets.Loader;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CustomComboBox<E> extends JComboBox<E> {

    private boolean focused;

    public CustomComboBox(E[] items) {
        super(items);
        initialize();
    }

    private void initialize() {

        setFont(Typography.BODY_NORMAL);

        setBackground(Color.WHITE);
        setForeground(Colors.TEXT_PRIMARY);

        setFocusable(true);
        setEditable(false);
        setOpaque(false);

        setMaximumRowCount(8);
        setLightWeightPopupEnabled(false);

        setBorder(
                new CustomTextField.RoundedBorder(
                        Colors.BORDER_GRAY,
                        10,
                        2
                )
        );

        setupRenderer();
        setupUI();
        setupFocusHandling();
    }

    private void setupRenderer() {

        setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {

                JLabel label = (JLabel)
                        super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus
                        );

                label.setFont(Typography.BODY_NORMAL);

                label.setBorder(
                        new EmptyBorder(10, 12, 10, 12)
                );

                if (isSelected) {

                    label.setBackground(
                            new Color(220, 240, 230)
                    );

                    label.setForeground(
                            Colors.TEXT_PRIMARY
                    );

                } else {

                    label.setBackground(Color.WHITE);

                    label.setForeground(
                            Colors.TEXT_PRIMARY
                    );
                }

                return label;
            }
        });
    }

    private void setupUI() {

        setUI(new BasicComboBoxUI() {

            @Override
            protected JButton createArrowButton() {

                JButton button = new JButton(
                        Loader.loadIcon(
                                "chevron-down.png",
                                16
                        )
                );

                button.setBorder(null);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setOpaque(false);
                button.setFocusable(false);

                button.setCursor(
                        Cursor.getPredefinedCursor(
                                Cursor.HAND_CURSOR
                        )
                );

                return button;
            }

            @Override
            protected ComboPopup createPopup() {

                BasicComboPopup popup =
                        new BasicComboPopup(comboBox) {

                            @Override
                            protected MouseListener createListMouseListener() {

                                return new MouseAdapter() {

                                    @Override
                                    public void mousePressed(MouseEvent e) {

                                        if (!comboBox.isEnabled()) {
                                            return;
                                        }

                                        comboBox.repaint();
                                    }

                                    @Override
                                    public void mouseReleased(MouseEvent e) {

                                        JList<?> list = getList();

                                        if (list.getModel()
                                                .getSize() <= 0) {
                                            return;
                                        }

                                        int index =
                                                list.locationToIndex(
                                                        e.getPoint()
                                                );

                                        if (index >= 0) {

                                            Object selectedItem =
                                                    list.getModel()
                                                            .getElementAt(index);

                                            comboBox.setSelectedItem(
                                                    selectedItem
                                            );

                                            hide();
                                        }
                                    }
                                };
                            }
                        };

                popup.setBorder(
                        BorderFactory.createLineBorder(
                                Colors.BORDER_GRAY
                        )
                );

                JList<?> list = popup.getList();

                list.setSelectionMode(
                        ListSelectionModel.SINGLE_SELECTION
                );

                return popup;
            }
        });
    }

    private void setupFocusHandling() {

        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {

                focused = true;

                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {

                focused = false;

                repaint();
            }
        });
    }

    @Override
    protected void paintBorder(Graphics g) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(
                focused
                        ? Colors.PRIMARY_GREEN
                        : Colors.BORDER_GRAY
        );

        g2.setStroke(new BasicStroke(2));

        g2.drawRoundRect(
                0,
                0,
                getWidth() - 1,
                getHeight() - 1,
                10,
                10
        );

        g2.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(Color.WHITE);

        g2.fillRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                10,
                10
        );

        g2.dispose();

        super.paintComponent(g);
    }
}