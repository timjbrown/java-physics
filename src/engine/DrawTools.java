package engine;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class DrawTools {
    public static void drawCenterString(Graphics g, String text, double centerx,
            double centery) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int x = (int) (centerx - metrics.stringWidth(text) / 2);
        int y = (int) (centery - metrics.getHeight() / 2 + metrics.getAscent());
        g.drawString(text, x, y);
    }

    public static void drawCenteredCircle(Graphics g, double radius,
            double centerx, double centery) {
        int d = (int) (radius * 2);
        int x = (int) (centerx - radius);
        int y = (int) (centery - radius);
        g.drawOval(x, y, d, d);
    }

    public static void fillCenteredCircle(Graphics g, double radius,
            double centerx, double centery) {
        int d = (int) (radius * 2);
        int x = (int) (centerx - radius);
        int y = (int) (centery - radius);
        g.fillOval(x, y, d, d);
    }
}
