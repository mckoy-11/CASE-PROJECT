package com.wastely.assets;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Loader {
    public Loader() {}
    public static ImageIcon loadIcon(String path, int size) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        ImageIcon icon = new ImageIcon("src/com/wastely/assets/icons/" + path);
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            return null;
        }

        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}