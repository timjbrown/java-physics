package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Stack;

import models.Pair;
import tools.PhysTools;
import engine.PhysToolsMore;
import models.Vector2;
import model.World;
import shapes.Circle;
import shapes.Pill;
import shapes.Polygon;
import shapes.Shape;

public class MyMouseListener extends MouseAdapter {

    private Vector2 mousePressedPos;
    private Stack<Pair<Vector2, Long>> mousePositions;
    private World world;

    public MyMouseListener(World world) {
        this.world = world;
        mousePositions = new Stack<Pair<Vector2, Long>>();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressedPos = new Vector2(e.getPoint());
        for (Circle c : world.circles) {
            if (PhysTools.colliding(mousePressedPos, c)) {
                world.selectedShape = c;
                world.selectedShape.setSelected(true);
                return;
            }
        }

        for (Pill p : world.pills) {
            if (PhysTools.colliding(mousePressedPos, p.beg())) {
                world.selectedShape = p.beg();
                world.selectedShape.setSelected(true);
                return;
            } else if (PhysTools.colliding(mousePressedPos, p.end())) {
                world.selectedShape = p.end();
                world.selectedShape.setSelected(true);
                return;
            } else if (PhysToolsMore.colliding(mousePressedPos, p)) {
                world.selectedShape = p;
                world.selectedShape.setSelected(true);
                return;
            }
        }

        for (Polygon poly : world.polys) {
            if (PhysToolsMore.colliding(mousePressedPos, poly)) {
                world.selectedShape = poly;
                world.selectedShape.setSelected(true);
                return;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Shape s = world.selectedShape;
        if (s != null) {
            if (mousePositions.size() > 1) {
                Pair<Vector2, Long> next = new Pair<Vector2, Long>(
                        new Vector2(e.getPoint()), System.currentTimeMillis());
                Pair<Vector2, Long> curr = mousePositions.pop();
                Pair<Vector2, Long> prev = mousePositions.pop();
                mousePositions.clear();

                Pair<Vector2, Long> now = next;
                Pair<Vector2, Long> old = prev;
                double duration = (now.o2 - old.o2) / 1000.0;
                s.setVel(now.o1.sub(old.o1).mul(1 / duration));
            }
            s.setDisplaceable(s.getSnapshot().isDisplaceable());
            s.setMovable(s.getSnapshot().isMovable());
            s.setSelected(false);
        }

        mousePressedPos = null;
        world.selectedShape = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMove(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseMove(e);
    }

    private void mouseMove(MouseEvent e) {
        Vector2 mouseCurrPos = new Vector2(e.getPoint());
        if (world.selectedShape != null) {
            mousePositions.push(new Pair<Vector2, Long>(mouseCurrPos,
                    System.currentTimeMillis()));

            Vector2 pressedToMouse = mouseCurrPos.sub(mousePressedPos);
            world.selectedShape.setPos(
                    world.selectedShape.getSnapshot().getPos().add(
                            pressedToMouse));
            world.selectedShape.setVel(Vector2.zero());

            if (world.selectedShape.getRoot() instanceof Pill)
                ((Pill) world.selectedShape.getRoot()).updateCenter(true);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (world.selectedShape != null) {
            world.selectedShape.addRadius(-e.getWheelRotation());
        }
    }
}