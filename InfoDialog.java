package Projekt_Quiz;

import javax.swing.*;
import java.awt.*;

public class InfoDialog extends JDialog {
    public InfoDialog(JFrame parent, String titel, String nachricht) {
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

        JButton okBtn = new JButton("OK");
        EscapeRoomStyleUtil.styleButton(okBtn);
        okBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        okBtn.addActionListener(e -> dispose());
        content.add(okBtn);

        setContentPane(content);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        EscapeRoomStyleUtil.styleFrame(this);
    }
}
