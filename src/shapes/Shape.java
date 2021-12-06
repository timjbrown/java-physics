package shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import controller.Settings;
import models.CircleBody;
import tools.DrawTools;
import models.Vector2;

public class Shape extends CircleBody {

    protected Shape parent;
    protected ArrayList<Shape> children = new ArrayList<Shape>();
    protected Color color;
    protected boolean selected;
    protected Shape snapshot;

    public Shape(double radius) {
        setRadius(radius);
        setTerminalVel(1200);
        color = Color.black;
    }

    public Shape copy() {
        Shape copy = new Shape(getRadius());
        copy.parent = parent;
        copy.setMass(getMass());
        copy.setPos(getPos().copy());
        copy.setVel(getVel().copy());
        copy.setAcc(getAcc().copy());
        copy.setColor(color);
        copy.setDisplaceable(isDisplaceable());
        copy.setMovable(isMovable());
        copy.setSelected(selected);
        for (Shape c : children) {
            copy.children.add(c.copy());
        }
        return copy;
    }

    public Shape getRoot() {
        Shape current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
    }

    public ArrayList<Shape> getChildren() {
        return children;
    }

    public void draw(Graphics g) {
        if (selected) {
            g.setColor(Settings.selectedColor);
            DrawTools.fillCircle(g, getRadius() + 2, getPos().getX(),
                    getPos().getY());
        }
        g.setColor(getColor());
        DrawTools.fillCircle(g, getRadius(), getPos().getX(),
                getPos().getY());
        for (Shape c : children)
            c.draw(g);
    }

    public void update(double elapsedTime, Vector2 forces) {
        super.update(elapsedTime, forces);
    }

    public Shape parent() {
        return parent;
    }

    public void setParent(Shape parent) {
        this.parent = parent;
    }

    public void setRadius(double radius) {
        super.setRadius(radius);
        for (Shape c : children)
            c.setRadius(radius);
        setMass(Math.pow(radius, 3));
    }

    public void addRadius(double val) {
        setRadius(getRadius() + val);
    }

    public void setPosIgnoreChildren(Vector2 pos) {
        super.setPos(pos);
    }

    public void setPos(Vector2 pos) {
        Vector2 delta = pos.sub(getPos());
        addPos(delta);
        // this.pos = pos;
    }

    public void addPosIgnoreChildren(Vector2 pos) {
        super.addPos(pos);
    }

    public void addPos(Vector2 pos) {
        super.addPos(pos);
        for (Shape c : children)
            c.addPos(pos);
    }

    public void setVel(Vector2 vel) {
        super.setVel(vel);
        for (Shape c : children)
            c.setVel(vel);
    }

    public void addVel(Vector2 vel) {
        super.addVel(vel);
        for (Shape c : children)
            c.addVel(vel);
    }

    public void setAcc(Vector2 acc) {
        super.setAcc(acc);
        for (Shape c : children)
            c.setAcc(acc);
    }

    public void addAcc(Vector2 acc) {
        super.addAcc(acc);
        for (Shape c : children)
            c.addAcc(acc);
    }

    public void setDisplaceable(boolean displaceable) {
        super.setDisplaceable(displaceable);
        for (Shape c : children)
            c.setDisplaceable(displaceable);
    }

    public void setMovable(boolean movable) {
        super.setMovable(movable);
        for (Shape c : children)
            c.setMovable(movable);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        for (Shape c : children)
            c.setColor(color);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            snapshot();
        }
        this.selected = selected;
        for (Shape c : children)
            c.setSelected(selected);
    }

    public void snapshot() {
        snapshot = this.copy();
    }

    public Shape getSnapshot() {
        return snapshot;
    }

    public void setMass(double mass) {
        super.setMass(mass);
        for (Shape c : children)
            c.setMass(mass);
    }
}
