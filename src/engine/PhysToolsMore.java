package engine;

import java.util.ArrayList;

import controller.Settings;
import models.Pair;
import models.Rectangle;
import models.Vector2;
import shapes.Circle;
import shapes.Pill;
import shapes.Polygon;
import shapes.Shape;
import tools.PhysTools;

public class PhysToolsMore {

    public static boolean colliding(Vector2 point, Pill pill) {
        Circle fake = new Circle(0);
        fake.setPos(point);
        return colliding(fake, pill) != null;
    }

    public static Circle colliding(Circle circle, Pill pill) {
        Vector2 begToEnd = pill.end().getPos().sub(pill.beg().getPos());
        Vector2 begToBall = circle.getPos().sub(pill.beg().getPos());

        double len = begToEnd.getX() * begToEnd.getX()
                + begToEnd.getY() * begToEnd.getY();
        double t = Math.max(0, Math.min(len, begToEnd.dot(begToBall))) / len;

        Vector2 fakePos = pill.beg().getPos().add(begToEnd.mul(t));
        Vector2 fakeVel = pill.getVel();
        if (!pill.isMovable()) {
            fakeVel = circle.getVel().mul(-1);
        }
        Circle fake = new Circle(pill.getRadius());
        fake.setParent(pill);
        fake.setDisplaceable(pill.isDisplaceable());
        fake.setMovable(pill.isMovable());
        fake.setPos(fakePos);
        fake.setVel(fakeVel);
        if (PhysTools.colliding(circle, fake)) {
            return fake;
        }
        return null;
    }

    public static Pair<Circle, Circle> colliding(Pill p1, Pill p2) {
        Circle fake = colliding(p1.beg(), p2);
        if (fake != null)
            return new Pair<Circle, Circle>(p1.beg(), fake);

        fake = colliding(p1.end(), p2);
        if (fake != null)
            return new Pair<Circle, Circle>(p1.end(), fake);

        fake = colliding(p2.beg(), p1);
        if (fake != null)
            return new Pair<Circle, Circle>(p2.beg(), fake);

        fake = colliding(p2.end(), p1);
        if (fake != null)
            return new Pair<Circle, Circle>(p2.end(), fake);

        return null;
    }

    public static boolean colliding(Vector2 point, Polygon poly) {
        Circle fake = new Circle(0);
        fake.setPos(point);
        return !colliding(fake, poly).isEmpty();
    }

    public static ArrayList<Circle> colliding(Circle circle, Polygon poly) {
        ArrayList<Circle> fakes = new ArrayList<Circle>();
        for (Shape shape : poly.getChildren()) {
            Pill pill = (Pill) shape;
            Circle fake = colliding(circle, pill);
            if (fake != null) {
                fakes.add(fake);
                // commenting this out causes double collisions with overlapping pills
                // This causes polys to float in mid air 
                // BUT leaving this in allows balls to drop through polys
                 return fakes;
            }
        }
        return fakes;
    }

    public static Pair<Circle, Circle> colliding(Pill pill, Polygon poly) {
        for (Shape shape : poly.getChildren()) {
            Pill polypill = (Pill) shape;
            Pair<Circle, Circle> pair = colliding(pill, polypill);
            if (pair != null)
                return pair;
        }
        return null;
    }

    public static Pair<Circle, Circle> colliding(Polygon p1, Polygon p2) {
        for (Shape shape : p1.getChildren()) {
            Pill pill1 = (Pill) shape;
            Pair<Circle, Circle> pair = colliding(pill1, p2);
            if (pair != null)
                return pair;
        }
        for (Shape shape : p2.getChildren()) {
            Pill pill2 = (Pill) shape;
            Pair<Circle, Circle> pair = colliding(pill2, p1);
            if (pair != null)
                return pair;
        }
        return null;
    }

    public static void collide(Shape c1, Shape c2, double restitution) {
        changePositions(c1, c2);
        changeVelocities(c1, c2, restitution);
    }

    private static void changePositions(Shape c1, Shape c2) {
        double distance = PhysTools.distance(c1, c2);
        double overlap = distance - c1.getRadius() - c2.getRadius();
        Vector2 c1_to_c2_dir = c2.getPos().sub(c1.getPos()).unit();
        Vector2 c2_to_c1_dir = c1.getPos().sub(c2.getPos()).unit();

        if (c1.getRoot().isDisplaceable() && c2.getRoot().isDisplaceable()) {
            c1.getRoot().addPos(c1_to_c2_dir.mul(overlap * .5));
            c2.getRoot().addPos(c2_to_c1_dir.mul(overlap * .5));
        } else if (c1.getRoot().isDisplaceable()) {
            c1.getRoot().addPos(c1_to_c2_dir.mul(overlap));
        } else if (c2.getRoot().isDisplaceable()) {
            c2.getRoot().addPos(c2_to_c1_dir.mul(overlap));
        }
    }

    private static void changeVelocities(Shape c1, Shape c2,
            double restitution) {
        double mass1 = c1.getRoot().getMass();
        double mass2 = c2.getRoot().getMass();
        if (!c1.isMovable())
            mass1 = mass2;
        if (!c2.isMovable())
            mass2 = mass1;

        double distance = PhysTools.distance(c1, c2);
        double nx = (c2.getPos().getX() - c1.getPos().getX()) / distance;
        double ny = (c2.getPos().getY() - c1.getPos().getY()) / distance;
        double tx = -ny;
        double ty = nx;
        double kx = c1.getVel().getX() - c2.getVel().getX();
        double ky = c1.getVel().getY() - c2.getVel().getY();
        double p = 2.0 * (nx * kx + ny * ky) / (mass1 + mass2);
        Vector2 c1Change = new Vector2(p * mass2 * nx, p * mass2 * ny);
        Vector2 c2Change = new Vector2(p * mass1 * nx, p * mass1 * ny);
        if (c1.isMovable()) {
            c1.getRoot().addVel(c1Change.neg());
            c1.getRoot().setVel(c1.getRoot().getVel().mul(restitution));
        }
        if (c2.isMovable()) {
            c2.getRoot().addVel(c2Change);
            c2.getRoot().setVel(c2.getRoot().getVel().mul(restitution));
        }
    }

    public static Shape bounceOffWalls(Rectangle rect, Shape c,
            double restitution) {
        if (!c.isCollidable())
            return null;

        Shape left = bounceOffLeft(rect, c);
        Shape right = bounceOffRight(rect, c);
        Shape top = bounceOffTop(rect, c);
        Shape bottom = bounceOffBottom(rect, c);

        if (left != null) { // left
            double diff = c.getPos().getX() - left.getPos().getX();
            c.setVel(new Vector2(-c.getVel().getX(), c.getVel().getY()));
            c.setPos(new Vector2(rect.getTopLeft().getX() + left.getRadius() + diff,
                    c.getPos().getY()));
            c.setVel(c.getVel().mul(restitution));
            return left;
        }
        if (top != null) { // top
            double diff = c.getPos().getY() - top.getPos().getY();
            c.setVel(new Vector2(c.getVel().getX(), -c.getVel().getY()));
            c.setPos(new Vector2(c.getPos().getX(),
                    rect.getTopLeft().getY() + top.getRadius() + diff));
            c.setVel(c.getVel().mul(restitution));
            return top;
        }
        if (right != null) { // right
            double diff = c.getPos().getX() - right.getPos().getX();
            c.setVel(new Vector2(-c.getVel().getX(), c.getVel().getY()));
            c.setPos(new Vector2(rect.getBotRight().getX() - right.getRadius() + diff,
                    c.getPos().getY()));
            c.setVel(c.getVel().mul(restitution));
            return right;
        }
        if (bottom != null && Settings.walls.isSouthWall()) { // bottom
            double diff = c.getPos().getY() - bottom.getPos().getY();
            c.setVel(new Vector2(c.getVel().getX(), -c.getVel().getY()));
            c.setPos(new Vector2(c.getPos().getX(),
                    rect.getBotRight().getY() - bottom.getRadius() + diff));
            c.setVel(c.getVel().mul(restitution));
            return bottom;
        } else if (!Settings.walls.isSouthWall() && teleportBottom(rect, c)) {
            c.setPos(new Vector2(c.getPos().getX(), 0));
            return null;
        }
        return null;
    }

    public static Shape bounceOffLeft(Rectangle rect, Shape s) {
        for (Shape c : s.getChildren()) {
            if (c instanceof Circle) {
                if (PhysTools.bounceOffLeft(rect, c)) {
                    return c;
                }
            } else {
                Shape result = bounceOffLeft(rect, c);
                if (result != null)
                    return result;
            }
        }
        if (s.getPos().getX() - s.getRadius() < rect.getTopLeft().getX())
            return s;
        return null;
    }

    public static Shape bounceOffRight(Rectangle rect, Shape s) {
        for (Shape c : s.getChildren()) {
            if (c instanceof Circle) {
                if (PhysTools.bounceOffRight(rect, c)) {
                    return c;
                }
            } else {
                Shape result = bounceOffRight(rect, c);
                if (result != null)
                    return result;
            }
        }
        if (s.getPos().getX() + s.getRadius() > rect.getBotRight().getX())
            return s;
        return null;
    }

    public static Shape bounceOffTop(Rectangle rect, Shape s) {
        for (Shape c : s.getChildren()) {
            if (c instanceof Circle) {
                if (PhysTools.bounceOffTop(rect, c)) {
                    return c;
                }
            } else {
                Shape result = bounceOffTop(rect, c);
                if (result != null)
                    return result;
            }
        }
        if (s.getPos().getY() - s.getRadius() < rect.getTopLeft().getY())
            return s;
        return null;
    }

    public static Shape bounceOffBottom(Rectangle rect, Shape s) {
        for (Shape c : s.getChildren()) {
            if (c instanceof Circle) {
                if (PhysTools.bounceOffBottom(rect, c)) {
                    return c;
                }
            } else {
                Shape result = bounceOffBottom(rect, c);
                if (result != null)
                    return result;
            }
        }
        if (s.getPos().getY() + s.getRadius() > rect.getBotRight().getY())
            return s;
        return null;
    }

    public static boolean teleportBottom(Rectangle rect, Shape s) {
        for (Shape c : s.getChildren()) {
            if (c instanceof Circle) {
                if (!PhysTools.bounceOffBottom(rect, c)) {
                    return false;
                }
            } else {
                Shape result = bounceOffBottom(rect, c);
                if (result == null)
                    return false;
            }
        }
        return s.getPos().getY() + s.getRadius() > rect.getBotRight().getY();
    }
}
