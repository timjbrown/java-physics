package shapes;

import java.awt.Color;
import java.awt.Graphics;

import models.Vector2;

public class Circle extends Shape {

    public Circle(double radius) {
        super(radius);
    }

    public void draw(Graphics g) {
        super.draw(g);
        // drawDirection(g);
    }

    public void drawDirection(Graphics g) {
        g.setColor(Color.white);
        Vector2 end = getPos().add(getVel().unit().mul(getRadius()));
        g.drawLine(getPos().intX(), getPos().intY(), end.intX(), end.intY());
    }
}