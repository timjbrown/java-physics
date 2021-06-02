package shapes;

import engine.Vector2;

public class Polygon extends Shape {

    private double polyRadius;

    public Polygon(double polyRadius, double pillRadius) {
        super(pillRadius);
        this.polyRadius = polyRadius;
        setRadius(pillRadius);
    }

    public void add(Pill p) {
        p.parent = this;
        children.add(p);
    }

    public void addPolyPills(int sides, double sideLength, Vector2 origin,
            double theta) {
        if (sides < 3)
            sides = 3;
        setPos(origin);
        for (int i = 0; i < sides; i++) {
            double angle1 = 2 * Math.PI * i / sides + theta;
            double angle2 = 2 * Math.PI * (i + 1) / sides + theta;
            Vector2 begPos = new Vector2(sideLength * Math.cos(angle1),
                    sideLength * Math.sin(angle1));
            Vector2 endPos = new Vector2(sideLength * Math.cos(angle2),
                    sideLength * Math.sin(angle2));

            Vector2 beg = origin.add(begPos);
            Vector2 end = origin.add(endPos);
            Pill pill = new Pill(getRadius(), beg, end);
            pill.setColor(color);
            pill.setDisplaceable(isDisplaceable());
            pill.setMovable(isMovable());
            this.add(pill);
        }
    }

    public void setRadius(double radius) {
        super.setRadius(radius);
        setMass(Math.pow(polyRadius, 3));
    }
}
