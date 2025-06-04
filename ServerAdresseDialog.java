package Projekt_Quiz;

import javax.swing.*;
import java.awt.*;

public class ServerAdresseDialog extends JDialog {
    public String serverAdresse = null;

    public ServerAdresseDialog(JFrame parent) {
        super(parent, "Server-IP eingeben", true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        EscapeRoomStyleUtil.stylePanel(content);
        content.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel title = new JLabel("<html>Gib die Server-IP-Adresse ein:<br>(z.B. 192.168.1.100)<br>NICHT 'localhost' verwenden bei verschiedenen PCs!</html>");
        EscapeRoomStyleUtil.styleLabel(title, 16, true, EscapeRoomStyleUtil.ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        JTextField addressField = new JTextField();
        EscapeRoomStyleUtil.styleTextField(addressField);
        addressField.setMaximumSize(new Dimension(220, 36));
        content.add(addressField);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 0));
        EscapeRoomStyleUtil.stylePanel(buttonPanel);

        JButton ok = new JButton("OK");
        EscapeRoomStyleUtil.styleButton(ok);
        ok.addActionListener(e -> {
            serverAdresse = addressField.getText().trim();
            if (!serverAdresse.isEmpty()) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Bitte gib eine Adresse ein!", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton abbrechen = new JButton("Abbrechen");
        EscapeRoomStyleUtil.styleButton(abbrechen);
        abbrechen.addActionListener(e -> {
            serverAdresse = null;
            dispose();
        });

        buttonPanel.add(ok);
        buttonPanel.add(abbrechen);
        content.add(buttonPanel);

        setContentPane(content);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        EscapeRoomStyleUtil.styleFrame(this);
    }
}