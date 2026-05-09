package com.wastely.views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;

/**
 * Reusable password input field with styling and placeholder support.
 */
public class CustomPasswordField extends JPasswordField {

    private String placeholder;
    private boolean showingPlaceholder;

    public CustomPasswordField(String placeholder) {
        this.placeholder = placeholder;
        this.showingPlaceholder = true;
        setupPasswordField();
    }

    public CustomPasswordField() {
        this("");
    }

    /**
     * Initializes password field configuration and behavior.
     */
    private void setupPasswordField() {
        setFont(Typography.BODY_NORMAL);
        setForeground(Colors.DISABLED_TEXT);
        setOpaque(false);
        setCaretColor(Colors.PRIMARY_GREEN);

        setMargin(new Insets(10, 15, 10, 15));
        setBorder(new CustomTextField.RoundedBorder(Colors.BORDER_GRAY, 10, 2));

        // show placeholder initially (visual only)
        showPlaceholder();

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    setText("");
                    showingPlaceholder = false;
                }
                setEchoChar('•');
                updateBorder(true);
                setForeground(Colors.TEXT_PRIMARY);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getPassword().length == 0) {
                    showPlaceholder();
                }
                updateBorder(false);
            }
        });
    }

    /**
     * Displays placeholder without storing it as real password data.
     */
    private void showPlaceholder() {
        setText(placeholder);
        setForeground(Colors.DISABLED_TEXT);
        setEchoChar((char) 0); // show text instead of masking
        showingPlaceholder = true;
    }

    /**
     * Updates border based on focus state.
     */
    private void updateBorder(boolean focused) {
        if (focused) {
            setBorder(new CustomTextField.RoundedBorder(Colors.PRIMARY_GREEN, 10, 2));
        } else {
            setBorder(new CustomTextField.RoundedBorder(Colors.BORDER_GRAY, 10, 2));
        }
    }

    /**
     * Returns actual password input (empty if placeholder is active).
     */
    public String getActualPassword() {
        if (showingPlaceholder) return "";
        return new String(getPassword());
    }
}