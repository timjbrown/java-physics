package engine;

import java.awt.Color;

public class ColorTools {
    public static Color randomColor() {
        return new Color((float) Math.random(), (float) Math.random(),
                (float) Math.random());
    }
}
