package com.wastely.utils;

import java.awt.Font;

/**
 * Centralized typography definitions for the WASTELY application.
 */
public class Typography {
    // Base font family
    public static final String FONT_FAMILY = "Segoe UI";
    public static final String FONT_FAMILY_MONO = "Consolas";
    
    // Font sizes
    public static final int SIZE_TINY = 10;      // 10px
    public static final int SIZE_SMALL = 12;     // 12px
    public static final int SIZE_BASE = 14;      // 14px
    public static final int SIZE_NORMAL = 16;    // 16px
    public static final int SIZE_LARGE = 18;     // 18px
    public static final int SIZE_XLARGE = 20;    // 20px
    public static final int SIZE_2XLARGE = 24;   // 24px
    
    // Font styles
    public static final Font BODY_NORMAL = new Font(FONT_FAMILY, Font.PLAIN, SIZE_NORMAL);
    public static final Font BODY_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, SIZE_NORMAL);
    
    public static final Font HEADING_H1 = new Font(FONT_FAMILY, Font.BOLD, SIZE_2XLARGE);
    public static final Font HEADING_H2 = new Font(FONT_FAMILY, Font.BOLD, SIZE_XLARGE);
    public static final Font HEADING_H3 = new Font(FONT_FAMILY, Font.BOLD, SIZE_LARGE);
    public static final Font HEADING_H4 = new Font(FONT_FAMILY, Font.BOLD, SIZE_NORMAL);
    
    public static final Font LABEL = new Font(FONT_FAMILY, Font.PLAIN, SIZE_BASE);
    public static final Font LABEL_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, SIZE_BASE);
    
    public static final Font BUTTON = new Font(FONT_FAMILY, Font.BOLD, SIZE_NORMAL);
    public static final Font BUTTON_SMALL = new Font(FONT_FAMILY, Font.BOLD, SIZE_BASE);
    
    public static final Font TABLE_HEADER = new Font(FONT_FAMILY, Font.BOLD, SIZE_BASE);
    public static final Font TABLE_CELL = new Font(FONT_FAMILY, Font.PLAIN, SIZE_NORMAL);
    
    public static final Font SMALL_TEXT = new Font(FONT_FAMILY, Font.PLAIN, SIZE_SMALL);
    public static final Font SMALL_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, SIZE_SMALL);
}
