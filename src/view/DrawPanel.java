package view;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import controller.Settings;
import model.World;

@SuppressWarnings("serial")
public class DrawPanel extends JPanel {

    private World world;

    public DrawPanel() {
        this.setPreferredSize(new Dimension(Settings.drawRect.intWidth(),
                Settings.drawRect.intHeight()));
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        world.draw(g, Settings.drawRect.intWidth(),
                Settings.drawRect.intHeight());
    }
}