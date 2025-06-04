package Projekt_Quiz;

import javax.swing.*;
import java.awt.*;

public class NameEingabeDialog extends JDialog {
    public String spielerName = null;

    public NameEingabeDialog(JFrame parent) {
        super(parent, "Name eingeben", true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        EscapeRoomStyleUtil.stylePanel(content);
        content.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Bitte gib deinen Namen ein");
        EscapeRoomStyleUtil.styleLabel(title, 20, true, EscapeRoomStyleUtil.ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        JTextField nameField = new JTextField();
        EscapeRoomStyleUtil.styleTextField(nameField);
        nameField.setMaximumSize(new Dimension(220, 36));
        content.add(nameField);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton ok = new JButton("OK");
        EscapeRoomStyleUtil.styleButton(ok);
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);
        ok.addActionListener(e -> {
            spielerName = nameField.getText().trim();
            if (!spielerName.isEmpty()) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Bitte gib einen Namen ein!", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });
        content.add(ok);

        setContentPane(content);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        EscapeRoomStyleUtil.styleFrame(this);
    }
}