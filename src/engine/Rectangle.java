package engine;

public class Rectangle {

    private Vector2 p1; // topleft
    private Vector2 p2; // botright

    public Rectangle(double minX, double minY, double maxX, double maxY) {
        setP1(new Vector2(minX, minY));
        setP2(new Vector2(maxX, maxY));
    }

    public Rectangle(Vector2 p1, Vector2 p2) {
        double minX = Math.min(p1.getX(), p2.getX());
        double minY = Math.min(p1.getY(), p2.getY());
        double maxX = Math.max(p1.getX(), p2.getX());
        double maxY = Math.max(p1.getY(), p2.getY());
        setP1(new Vector2(minX, minY));
        setP2(new Vector2(maxX, maxY));
    }

    public Vector2 getP1() {
        return p1;
    }

    public void setP1(Vector2 p1) {
        this.p1 = p1;
    }

    public Vector2 getP2() {
        return p2;
    }

    public void setP2(Vector2 p2) {
        this.p2 = p2;
    }

    public double getWidth() {
        return p2.getX() - p1.getX();
    }

    public double getHeight() {
        return p2.getY() - p1.getY();
    }

    public int intWidth() {
        return (int) getWidth();
    }

    public int intHeight() {
        return (int) getHeight();
    }
}
