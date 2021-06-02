package engine;

public class PhysTools {

    public static double distance(CircleBody c1, CircleBody c2) {
        return c1.getPos().dist(c2.getPos());
    }

    public static boolean colliding(Vector2 point, CircleBody circle) {
        return point.dist(circle.getPos()) < circle.getRadius();
    }

    public static boolean colliding(CircleBody c1, CircleBody c2) {
        return distance(c1, c2) < c1.getRadius() + c2.getRadius();
    }
    
    public static boolean colliding(CircleBody circle, Rectangle rect) {
        double cx = circle.getPos().getX();
        double cy = circle.getPos().getY();
        double r1x = rect.getP1().getX();
        double r1y = rect.getP1().getY();
        double r2x = rect.getP2().getX();
        double r2y = rect.getP2().getY();
        double nearX = Math.max(r1x, Math.min(cx, r2x));
        double nearY = Math.max(r1y, Math.min(cy, r2y));
        Vector2 nearest = new Vector2(nearX, nearY);
        Vector2 circleToNearest = nearest.sub(circle.getPos());
        double overlap = circle.getRadius() - circleToNearest.length();

        return overlap > 0;
    }

    public static void collide(CircleBody c1, CircleBody c2,
            double restitution) {
        changePositions(c1, c2);
        changeVelocities(c1, c2, restitution);
    }

    private static void changePositions(CircleBody c1, CircleBody c2) {
        double distance = distance(c1, c2);
        double overlap = distance - c1.getRadius() - c2.getRadius();
        Vector2 c1_to_c2_dir = c2.getPos().sub(c1.getPos()).unit();
        Vector2 c2_to_c1_dir = c1.getPos().sub(c2.getPos()).unit();

        if (c1.isDisplaceable() && c2.isDisplaceable()) {
            c1.addPos(c1_to_c2_dir.mul(overlap * .5));
            c2.addPos(c2_to_c1_dir.mul(overlap * .5));
        } else if (c1.isDisplaceable()) {
            c1.addPos(c1_to_c2_dir.mul(overlap));
        } else if (c2.isDisplaceable()) {
            c2.addPos(c2_to_c1_dir.mul(overlap));
        }
    }

    private static void changeVelocities(CircleBody c1, CircleBody c2,
            double restitution) {
        double mass1 = c1.getMass();
        double mass2 = c2.getMass();
        if (!c1.isMovable())
            mass1 = mass2;
        if (!c2.isMovable())
            mass2 = mass1;

        double distance = distance(c1, c2);
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
            c1.addVel(c1Change.neg());
            c1.setVel(c1.getVel().mul(restitution));
        }
        if (c2.isMovable()) {
            c2.addVel(c2Change);
            c2.setVel(c2.getVel().mul(restitution));
        }
    }

    public static boolean bounceOffWalls(Rectangle rect, CircleBody c,
            double restitution) {
        if (!c.isCollidable())
            return false;

        boolean left = bounceOffLeft(rect, c);
        boolean right = bounceOffRight(rect, c);
        boolean top = bounceOffTop(rect, c);
        boolean bottom = bounceOffBottom(rect, c);

        boolean bounced = false;
        if (left) { // left
            c.setVel(new Vector2(-c.getVel().getX(), c.getVel().getY()));
            c.setPos(new Vector2(rect.getP1().getX() + c.getRadius(),
                    c.getPos().getY()));
            bounced = true;
        }
        if (top) { // top
            c.setVel(new Vector2(c.getVel().getX(), -c.getVel().getY()));
            c.setPos(new Vector2(c.getPos().getX(),
                    rect.getP1().getY() + c.getRadius()));
            bounced = true;
        }
        if (right) { // right
            c.setVel(new Vector2(-c.getVel().getX(), c.getVel().getY()));
            c.setPos(new Vector2(rect.getP2().getX() - c.getRadius(),
                    c.getPos().getY()));
            bounced = true;
        }
        if (bottom) { // bottom
            c.setVel(new Vector2(c.getVel().getX(), -c.getVel().getY()));
            c.setPos(new Vector2(c.getPos().getX(),
                    rect.getP2().getY() - c.getRadius()));
            bounced = true;
        }

        if (bounced)
            c.setVel(c.getVel().mul(restitution));
        return bounced;
    }

    public static boolean bounceOffLeft(Rectangle rect, CircleBody c) {
        return c.getPos().getX() - c.getRadius() < rect.getP1().getX();
    }

    public static boolean bounceOffRight(Rectangle rect, CircleBody c) {
        return c.getPos().getX() + c.getRadius() > rect.getP2().getX();
    }

    public static boolean bounceOffTop(Rectangle rect, CircleBody c) {
        return c.getPos().getY() - c.getRadius() < rect.getP1().getY();
    }

    public static boolean bounceOffBottom(Rectangle rect, CircleBody c) {
        return c.getPos().getY() + c.getRadius() > rect.getP2().getY();
    }
}
