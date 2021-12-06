package model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import controller.Settings;
import engine.PhysToolsMore;
import models.Pair;
import models.Vector2;
import shapes.Circle;
import shapes.Pill;
import shapes.Polygon;
import shapes.Shape;
import tools.ColorTools;
import tools.PhysTools;

public class World {

    double rowu = (double) Settings.drawRect.getWidth()
            / (Settings.rowsPolys + 1);
    double colu = (double) Settings.drawRect.getHeight()
            / (Settings.colsPolys + 1);
    public double polyRadius = Math.min(rowu / 4, colu / 4);

    public ArrayList<Circle> circles = new ArrayList<Circle>();
    public ArrayList<Pill> pills = new ArrayList<Pill>();
    public ArrayList<Polygon> polys = new ArrayList<Polygon>();
    public Shape selectedShape;
    public Vector2 forces = Vector2.zero();

    public World() {
        forces = forces.add(Settings.gravity); // add gravity
        addCircles();
        addPills();
        addPolys();
        if (Settings.groundPill)
            addGroundPill();
    }

    public void addCircles() {
        double w = Settings.drawRect.getWidth();
        double h = Settings.drawRect.getHeight();
        for (int i = 0; i < Settings.numBalls; i++) {
            Circle c = new Circle(Settings.ballRadius);
            c.setDisplaceable(true);
            c.setColor(ColorTools.randomColor());
            c.setPos(new Vector2(0, w, 0, h));
            c.setVel(new Vector2(0, w / 2, 0, h / 2));
            circles.add(c);
        }
    }

    public void addPills() {
        double w = Settings.drawRect.getWidth();
        double h = Settings.drawRect.getHeight();
        double u = w / (Settings.numPills + 1);

        for (int i = 0; i < Settings.numPills; i++) {
            Vector2 beg = new Vector2(u * i + u, h * .5);
            Vector2 end = new Vector2(u * i + u + 10, h * .6);
            Pill pill = new Pill(Settings.pillRadius, beg, end);
            pill.setColor(Color.black);
            pill.setDisplaceable(!Settings.fixedPills);
            pill.setMovable(!Settings.fixedPills);
            pills.add(pill);
        }
    }

    public void addGroundPill() {
        double w = Settings.drawRect.getWidth();
        double h = Settings.drawRect.getHeight();

        Vector2 beg = new Vector2(w * .05, h * .98);
        Vector2 end = new Vector2(w * .4, h * .98);
        Pill pill = new Pill(Settings.pillRadius, beg, end);
        pill.setColor(Color.black);
        pill.setDisplaceable(!Settings.fixedPills);
        pill.setMovable(!Settings.fixedPills);
        pills.add(pill);
        
        beg = new Vector2(w * .6, h * .98);
        end = new Vector2(w * .95, h * .98);
        pill = new Pill(Settings.pillRadius, beg, end);
        pill.setColor(Color.black);
        pill.setDisplaceable(!Settings.fixedPills);
        pill.setMovable(!Settings.fixedPills);
        pills.add(pill);
    }

    public void addPolys() {
        double theta = 2 * Math.PI / Settings.rowsPolys;

        for (int row = 0; row < Settings.rowsPolys; row++) {
            for (int col = 0; col < Settings.colsPolys; col++) {
                Vector2 center = new Vector2(colu * col + colu,
                        row * rowu + rowu);
                Polygon poly = new Polygon(polyRadius, Settings.pillRadius);
                poly.setColor(Color.black);
                poly.setDisplaceable(!Settings.fixedPolys);
                poly.setMovable(!Settings.fixedPolys);
                poly.addPolyPills(col + 3, polyRadius, center, theta * row);
                polys.add(poly);
            }
        }
    }

    public void update(double elapsedTime) {
        elapsedTime = elapsedTime / Settings.subframes;

        for (int f = 0; f < Settings.subframes; f++) {
            move(elapsedTime, forces);
            ArrayList<Pair<Circle, Circle>> pairs = detectCollisions();
            resolveCollisions(pairs);
            bounceOffWalls();
        }
    }

    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        for (Circle c : circles) {
            c.draw(g);
        }
        for (Pill p : pills) {
            p.draw(g);
        }
        for (Polygon poly : polys) {
            poly.draw(g);
        }
    }

    private void move(double elapsedTime, Vector2 forces) {
        for (Circle c : circles) {
            c.update(elapsedTime, forces);
        }
        for (Pill p : pills) {
            p.update(elapsedTime, forces);
        }
        for (Polygon poly : polys) {
            poly.update(elapsedTime, forces);
        }
    }

    private ArrayList<Pair<Circle, Circle>> detectCollisions() {
        ArrayList<Pair<Circle, Circle>> pairs = new ArrayList<Pair<Circle, Circle>>();
        for (int i = 0; i < circles.size(); i++) {
            Circle c = circles.get(i);

            // circle vs circle
            for (int j = i + 1; j < circles.size(); j++) {
                Circle other = circles.get(j);
                if (PhysTools.colliding(c, other))
                    pairs.add(new Pair<Circle, Circle>(c, other));
            }

            // circle vs pill
            for (int j = 0; j < pills.size(); j++) {
                Pill other = pills.get(j);
                Circle fake = PhysToolsMore.colliding(c, other);
                if (fake != null)
                    pairs.add(new Pair<Circle, Circle>(c, fake));
            }

            // circle vs poly
            for (int j = 0; j < polys.size(); j++) {
                Polygon other = polys.get(j);
                ArrayList<Circle> fakes = PhysToolsMore.colliding(c, other);
                if (!fakes.isEmpty()) {
                    for (Circle fake : fakes) {
                        pairs.add(new Pair<Circle, Circle>(c, fake));
                    }
                }
            }
        }

        for (int i = 0; i < pills.size(); i++) {
            Pill p = pills.get(i);

            // pill vs pill
            for (int j = i + 1; j < pills.size(); j++) {
                Pill other = pills.get(j);
                Pair<Circle, Circle> pair = PhysToolsMore.colliding(p, other);
                if (pair != null)
                    pairs.add(pair);
            }

            // pill vs poly
            for (int j = 0; j < polys.size(); j++) {
                Polygon other = polys.get(j);
                Pair<Circle, Circle> pair = PhysToolsMore.colliding(p, other);
                if (pair != null)
                    pairs.add(pair);
            }
        }

        for (int i = 0; i < polys.size(); i++) {
            Polygon p = polys.get(i);

            // poly vs poly
            for (int j = i + 1; j < polys.size(); j++) {
                Polygon other = polys.get(j);
                Pair<Circle, Circle> pair = PhysToolsMore.colliding(p, other);
                if (pair != null)
                    pairs.add(pair);
            }
        }
        return pairs;
    }

    private void resolveCollisions(ArrayList<Pair<Circle, Circle>> pairs) {
        for (Pair<Circle, Circle> pair : pairs) {
            Circle c1 = pair.o1;
            Circle c2 = pair.o2;
            PhysToolsMore.collide(c1, c2, Settings.ballRestitution);
        }
    }

    private void bounceOffWalls() {
        for (Circle c : circles) {
            PhysToolsMore.bounceOffWalls(Settings.drawRect, c,
                    Settings.wallRestitution);
        }
        for (Pill c : pills) {
            PhysToolsMore.bounceOffWalls(Settings.drawRect, c.getRoot(),
                    Settings.wallRestitution);
        }
        for (Polygon c : polys) {
            PhysToolsMore.bounceOffWalls(Settings.drawRect, c.getRoot(),
                    Settings.wallRestitution);
        }
    }
}