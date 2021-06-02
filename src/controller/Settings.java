package controller;

import java.awt.Color;

import engine.Rectangle;
import engine.Vector2;
import model.Walls;

public class Settings {
    public static final int FRAME_TIME = 10; // in milliseconds
    public static final Rectangle drawRect = new Rectangle(0, 0, 500, 500);
    public static final Walls walls = new Walls(true, true, false, true);
    // public static Vector2 gravity = new Vector2(0, 0);
    public static final Vector2 gravity = new Vector2(0, 3000);
    public static final int subframes = 5;
    public static final int numBalls = 100;
    public static final int numPills = 0;
    public static final int rowsPolys = 3;
    public static final int colsPolys = 3;
    public static final double ballRadius = 4;
    public static final double pillRadius = 3;
    public static final double wallRestitution = .9;
    public static final double ballRestitution = .9;
    public static final boolean fixedPills = true;
    public static final boolean fixedPolys = true;
    public static final boolean groundPill = true;

    public static final Color selectedColor = Color.green;
}
