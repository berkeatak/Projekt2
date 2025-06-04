package Projekt_Quiz;

import javax.swing.*;
import java.awt.*;

public class MehrspielerAuswahlDialog extends JDialog {
    public int auswahl = -1;

    public MehrspielerAuswahlDialog(JFrame parent) {
        super(parent, "Mehrspieler-Modus", true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        EscapeRoomStyleUtil.stylePanel(content);
        content.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel title = new JLabel("Möchtest du einen Raum erstellen oder einem Raum beitreten?");
        EscapeRoomStyleUtil.styleLabel(title, 18, true, EscapeRoomStyleUtil.ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 0));
        EscapeRoomStyleUtil.stylePanel(buttonPanel);

        String[] optionen = {"Raum erstellen", "Raum beitreten", "Zurück"};
        for (int i = 0; i < optionen.length; i++) {
            JButton btn = new JButton(optionen[i]);
            EscapeRoomStyleUtil.styleButton(btn);
            final int idx = i;
            btn.addActionListener(e -> { auswahl = idx; dispose(); });
            buttonPanel.add(btn);
        }

        content.add(buttonPanel);

        setContentPane(content);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        EscapeRoomStyleUtil.styleFrame(this);
    }
}