package Projekt_Quiz;

import javax.swing.*;
import java.awt.*;

public class SchwierigkeitsgradDialog extends JDialog {
    public int schwierigkeitsgrad = -1;

    public SchwierigkeitsgradDialog(JFrame parent) {
        super(parent, "Schwierigkeitsgrad wählen", true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        EscapeRoomStyleUtil.stylePanel(content);
        content.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Wähle den Schwierigkeitsgrad");
        EscapeRoomStyleUtil.styleLabel(title, 22, true, EscapeRoomStyleUtil.ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 26)));

        JButton leicht = new JButton("Leicht");
        EscapeRoomStyleUtil.styleButton(leicht);
        leicht.addActionListener(e -> { schwierigkeitsgrad = 0; dispose(); });
        content.add(leicht);
        content.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton mittel = new JButton("Mittel");
        EscapeRoomStyleUtil.styleButton(mittel);
        mittel.addActionListener(e -> { schwierigkeitsgrad = 1; dispose(); });
        content.add(mittel);
        content.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton schwer = new JButton("Schwer");
        EscapeRoomStyleUtil.styleButton(schwer);
        schwer.addActionListener(e -> { schwierigkeitsgrad = 2; dispose(); });
        content.add(schwer);

        setContentPane(content);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        EscapeRoomStyleUtil.styleFrame(this);
    }
}