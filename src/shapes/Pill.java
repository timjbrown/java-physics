package shapes;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import models.Vector2;

public class Pill extends Shape {

    public Pill(double radius, Vector2 p1, Vector2 p2) {
        super(radius);
        // needed instead of updateCenter() so beg & end don't change twice
        setPos(p1.add(p2.sub(p1).mul(.5)));
        children.add(new Circle(radius));
        children.add(new Circle(radius));
        setBeg(p1);
        setEnd(p2);
        changeMass();
    }

    public Circle beg() {
        return (Circle) children.get(0);
    }

    private void setBeg(Vector2 pos) {
        beg().setParent(this);
        beg().setColor(getColor());
        beg().setDisplaceable(isDisplaceable());
        beg().setMovable(isMovable());
        beg().setPos(pos);
        beg().setVel(getVel());
        beg().setAcc(getAcc());
    }

    public Circle end() {
        return (Circle) children.get(1);
    }

    private void setEnd(Vector2 pos) {
        end().setParent(this);
        end().setColor(getColor());
        end().setDisplaceable(isDisplaceable());
        end().setMovable(isMovable());
        end().setPos(pos);
        end().setVel(getVel());
        end().setAcc(getAcc());
    }

    public void updateCenter(boolean ignoreChildren) {
        Vector2 p1 = beg().getPos();
        Vector2 p2 = end().getPos();
        Vector2 newPos = p1.add(p2.sub(p1).mul(.5));
        
        if (ignoreChildren)
            setPosIgnoreChildren(newPos);
        else
            setPos(newPos);
        
        changeMass();
    }
    
    private void changeMass() {
        setMass(Math.pow(beg().getPos().dist(getPos()), 3));
    }

    public Vector2 normal() {
        return new Vector2(-(end().getPos().getY() - beg().getPos().getY()),
                end().getPos().getX() - beg()
                                             .getPos()
                                             .getX());
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(color);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Vector2 normal = normal().unit();
        Vector2 l1p1 = beg().getPos().add(normal.mul(getRadius()));
        Vector2 l1p2 = end().getPos().add(normal.mul(getRadius()));
        Vector2 l2p1 = beg().getPos().add(normal.mul(-getRadius()));
        Vector2 l2p2 = end().getPos().add(normal.mul(-getRadius()));
        int[] xpoints = { l1p1.intX(), l1p2.intX(), l2p2.intX(), l2p1.intX() };
        int[] ypoints = { l1p1.intY(), l1p2.intY(), l2p2.intY(), l2p1.intY() };
        g.fillPolygon(new Polygon(xpoints, ypoints, 4));

        // g.drawLine(l1p1.intX(), l1p1.intY(), l1p2.intX(), l1p2.intY());
        // g.drawLine(l2p1.intX(), l2p1.intY(), l2p2.intX(), l2p2.intY());
        // System.out.println(
        // beg().getPos() + " " + getPos() + " " + end().getPos());
        // System.out.println(l1p1 + " " + getPos() + " " + l1p2);
    }
}
