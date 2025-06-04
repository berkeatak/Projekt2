package Projekt_Quiz;

import javax.swing.*;
import java.awt.*;

public class ModusAuswahlDialog extends JDialog {
    public int gewaehlterModus = -1;

    public ModusAuswahlDialog(JFrame parent) {
        super(parent, "Spielmodus", true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        EscapeRoomStyleUtil.stylePanel(content);
        content.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel title = new JLabel("Wähle den Spielmodus:");
        EscapeRoomStyleUtil.styleLabel(title, 20, true, EscapeRoomStyleUtil.ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 22)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 22, 0));
        EscapeRoomStyleUtil.stylePanel(buttonPanel);

        JButton einzel = new JButton("Einzelspieler");
        EscapeRoomStyleUtil.styleButton(einzel);
        einzel.addActionListener(e -> { gewaehlterModus = 0; dispose(); });
        buttonPanel.add(einzel);

        JButton multi = new JButton("Mehrspieler");
        EscapeRoomStyleUtil.styleButton(multi);
        multi.addActionListener(e -> { gewaehlterModus = 1; dispose(); });
        buttonPanel.add(multi);

        content.add(buttonPanel);

        setContentPane(content);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        EscapeRoomStyleUtil.styleFrame(this);
    }
}