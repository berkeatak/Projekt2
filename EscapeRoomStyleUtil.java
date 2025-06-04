package Projekt_Quiz;

import javax.swing.*;
import java.awt.*;

public class EscapeRoomStyleUtil {
    public static final Color PRIMARY_BG = new Color(33, 37, 43);
    public static final Color PANEL_BG = new Color(44, 47, 51);
    public static final Color PRIMARY_FG = new Color(255, 255, 255);
    public static final Color ACCENT = new Color(0x5CDB95);
    public static final Color BUTTON_BG = new Color(44, 62, 80);
    public static final Color BUTTON_FG = new Color(236, 240, 241);

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(PRIMARY_BG);
        frame.setLocationRelativeTo(null);
    }

    public static void styleFrame(JDialog dialog) {
        dialog.getContentPane().setBackground(PRIMARY_BG);
        dialog.setLocationRelativeTo(null);
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(PANEL_BG);
    }

    public static void styleLabel(JLabel label, int fontSize, boolean bold, Color color) {
        label.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(color);
    }

    public static void styleButton(JButton button) {
        button.setBackground(BUTTON_BG);
        button.setForeground(BUTTON_FG);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
    }

    public static void styleTextField(JTextField field) {
        field.setBackground(new Color(36, 40, 50));
        field.setForeground(BUTTON_FG);
        field.setFont(new Font("Arial", Font.PLAIN, 18));
        field.setCaretColor(ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }
}