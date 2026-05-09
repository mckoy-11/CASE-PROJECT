package com.wastely.utils;

import java.awt.Color;

/**
 * Centralized color definitions for the WASTELY application.
 * All colors matched from the React Tailwind theme.
 */
public class Colors {
    // Primary Colors
    public static final Color PRIMARY_GREEN = new Color(22, 163, 74);      // #16a34a
    public static final Color PRIMARY_DARK_GREEN = new Color(21, 128, 61); // #15803d
    public static final Color PRIMARY_LIGHT_GREEN = new Color(220, 252, 231); // #dcfce7
    
    // Secondary/Accent Colors
    public static final Color SECONDARY_GREEN = Color.decode("#287C27");     
    public static final Color SECONDARY_LIGHT_GREEN = Color.decode("#81DB7A");
    
    // Neutral Colors
    public static final Color TEXT_PRIMARY = new Color(3, 2, 19);          // #030213
    public static final Color TEXT_SECONDARY = new Color(113, 113, 130);   // #717182
    public static final Color BACKGROUND_WHITE = new Color(255, 255, 255); // #ffffff
    public static final Color BACKGROUND_GRAY = new Color(249, 250, 251);  // #f8fafc
    public static final Color BORDER_GRAY = new Color(209, 213, 219);      // #d1d5db
    
    // Status Colors
    public static final Color STATUS_PENDING = new Color(234, 179, 8);     // #eab308 (yellow)
    public static final Color STATUS_PENDING_BG = new Color(254, 243, 199); // #fef3c7
    public static final Color STATUS_PENDING_TEXT = new Color(161, 98, 7); // #a16207
    
    public static final Color STATUS_IN_PROGRESS = new Color(59, 130, 246); // #3b82f6 (blue)
    public static final Color STATUS_IN_PROGRESS_BG = new Color(219, 234, 254); // #dbeafe
    public static final Color STATUS_IN_PROGRESS_TEXT = new Color(30, 64, 175); // #1e40af
    
    public static final Color STATUS_COMPLETED = new Color(34, 197, 94);   // #22c55e (green)
    public static final Color STATUS_COMPLETED_BG = new Color(220, 252, 231); // #dcfce7
    public static final Color STATUS_COMPLETED_TEXT = new Color(21, 128, 61); // #15803d
    
    public static final Color STATUS_RESOLVED = new Color(34, 197, 94);    // #22c55e (green)
    public static final Color STATUS_RESOLVED_BG = new Color(220, 252, 231); // #dcfce7
    public static final Color STATUS_RESOLVED_TEXT = new Color(21, 128, 61); // #15803d
    
    public static final Color STATUS_CANCELLED = new Color(239, 68, 68);   // #ef4444 (red)
    public static final Color STATUS_CANCELLED_BG = new Color(254, 226, 226); // #fee2e2
    public static final Color STATUS_CANCELLED_TEXT = new Color(220, 38, 38); // #dc2626
    
    // Button Colors
    public static final Color BUTTON_DANGER = new Color(239, 68, 68);      // #ef4444
    public static final Color BUTTON_DANGER_HOVER = new Color(220, 38, 38); // #dc2626
    
    // Shadow Color (with alpha for transparency effects)
    public static final Color SHADOW = new Color(0, 0, 0, 30);
    
    // Disable/Inactive Color
    public static final Color DISABLED_TEXT = new Color(156, 163, 175);    // #9ca3af
}
