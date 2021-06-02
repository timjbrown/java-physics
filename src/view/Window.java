package view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import controller.Settings;

public class Window extends JFrame {

    private DrawPanel drawPanel;

    public Window() {
        setTitle("Circles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(Settings.drawRect.intWidth() + 5,
                Settings.drawRect.intHeight() + 30));
        setResizable(false);
        setLayout(new BorderLayout());

        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
    }

    public DrawPanel getDrawPanel() {
        return drawPanel;
    }
}
