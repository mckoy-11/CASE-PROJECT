package com.wastely.views.components;

import javax.swing.*;
import java.awt.*;

/**
 * Container for multiple Summary Cards.
 */
public class SummaryCards extends JPanel {

    private String[] titles;
    private int[] values;
    private String[] details;
    private String[] iconPaths;
    private Color[] iconBgColors;
    private String[] subLabels;

    public SummaryCards(String[] titles, int[] values, String[] details,
                        String[] iconPaths, Color[] iconBgColors) {
        this(titles, values, details, iconPaths, iconBgColors, null);
    }

    public SummaryCards(String[] titles, int[] values, String[] details,
                        String[] iconPaths, Color[] iconBgColors,
                        String[] subLabels) {

        this.titles = titles;
        this.values = values;
        this.details = details;
        this.iconPaths = iconPaths;
        this.iconBgColors = iconBgColors;
        this.subLabels = subLabels;

        setOpaque(false);
        setLayout(new GridLayout(1, titles.length, 15, 0));

        buildCards();
    }

    /**
     * Builds all cards.
     */
    private void buildCards() {
        removeAll();

        for (int i = 0; i < titles.length; i++) {
            Card card = createCard(i);
            add(card);
        }

        revalidate();
        repaint();
    }

    /**
     * Creates and configures a Card instance using setters.
     */
    private Card createCard(int index) {
        Card card = new Card();

        String unit = (subLabels != null && index < subLabels.length)
                ? subLabels[index]
                : null;

        card.setTitle(titles[index]);
        card.setValue(values[index]);
        card.setUnit(unit);
        card.setTrend(details[index]);
        card.setIcon(iconPaths[index]);

        card.setIconBackground(iconBgColors[index]);

        return card;
    }

    /**
     * Updates card values dynamically.
     * @param newValues
     */
    public void updateValues(int[] newValues) {
        if (newValues == null || newValues.length != values.length) return;

        this.values = newValues;
        buildCards();
    }
}