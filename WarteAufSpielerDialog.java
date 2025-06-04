package Projekt_Quiz;

import javax.swing.*;
import java.awt.*;

public class WarteAufSpielerDialog extends JDialog {
    private JLabel infoLabel;

    public WarteAufSpielerDialog(JFrame parent) {
        super(parent, "Warte auf Spieler", true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        EscapeRoomStyleUtil.stylePanel(content);
        content.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        infoLabel = new JLabel("Warte auf weitere Spieler...\n");
        EscapeRoomStyleUtil.styleLabel(infoLabel, 20, true, EscapeRoomStyleUtil.ACCENT);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(infoLabel);
        content.add(Box.createRigidArea(new Dimension(0, 24)));

        JLabel tipp = new JLabel("(Das Spiel startet automatisch sobald alle verbunden sind.)");
        EscapeRoomStyleUtil.styleLabel(tipp, 14, false, EscapeRoomStyleUtil.PRIMARY_FG);
        tipp.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(tipp);

        setContentPane(content);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        pack();
        EscapeRoomStyleUtil.styleFrame(this);
    }

    public void setWarteText(String text) {
        infoLabel.setText(text);
    }
}