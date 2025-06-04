package Projekt_Quiz;

import javax.swing.*;
import java.awt.*;

public class WarteDialog extends JDialog {
    public WarteDialog(JFrame parent, String titel, String nachricht) {
        super(parent, titel, true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        EscapeRoomStyleUtil.stylePanel(content);
        content.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel msgLabel = new JLabel("<html>" + nachricht.replaceAll("\n", "<br>") + "</html>");
        EscapeRoomStyleUtil.styleLabel(msgLabel, 16, false, EscapeRoomStyleUtil.PRIMARY_FG);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(msgLabel);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        // Kein OK-Button, Fenster bleibt offen bis dispose() aufgerufen wird
        setContentPane(content);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        pack();
        setResizable(false);
        EscapeRoomStyleUtil.styleFrame(this);
    }
}