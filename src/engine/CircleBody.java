package engine;

public class CircleBody {

    private double radius;
    private double mass;
    private Vector2 pos; // pixels
    private Vector2 vel; // pixels/second
    private Vector2 acc; // pixels/second/second

    private double friction; // pixels/second/second
    private double terminalVel; // pixels/second

    private boolean movable;
    private boolean collidable;
    private boolean displaceable;

    public CircleBody() {
        radius = 1;
        mass = 1;
        pos = Vector2.zero();
        vel = Vector2.zero();
        acc = Vector2.zero();
        friction = 0;
        terminalVel = Double.MAX_VALUE;
        movable = true;
        collidable = true;
        displaceable = true;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public void addPos(Vector2 pos) {
        this.pos = this.pos.add(pos);
    }

    public Vector2 getVel() {
        return vel;
    }

    public void setVel(Vector2 vel) {
        this.vel = vel;
    }

    public void addVel(Vector2 vel) {
        this.vel = this.vel.add(vel);
    }

    public Vector2 getAcc() {
        return acc;
    }

    public void setAcc(Vector2 acc) {
        this.acc = acc;
    }

    public void addAcc(Vector2 acc) {
        this.acc = this.acc.add(acc);
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    public boolean isDisplaceable() {
        return displaceable;
    }

    public void setDisplaceable(boolean displaceable) {
        this.displaceable = displaceable;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public double getTerminalVel() {
        return terminalVel;
    }

    public void setTerminalVel(double terminalVel) {
        this.terminalVel = terminalVel;
    }

    public void update(double elapsedTime, Vector2 forces) {
        if (!movable)
            return;

        if (vel.length() > terminalVel)
            setVel(vel.unit().mul(terminalVel));
        addVel(acc.mul(elapsedTime));
        addVel(forces.mul(elapsedTime));
        Vector2 frictionv = vel.unit().mul(-friction * elapsedTime);
        if (frictionv.length() > vel.length())
            setVel(Vector2.zero());
        else
            addVel(frictionv);
        addPos(vel.mul(elapsedTime));

        if (vel.length() < .01) {
            setVel(Vector2.zero());
        }
    }
}
