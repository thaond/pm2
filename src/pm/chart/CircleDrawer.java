package pm.chart;

import org.jfree.ui.Drawable;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class CircleDrawer implements Drawable {

    private Color color;

    public CircleDrawer(Color color) {
        this.color = color;
    }

    public void draw(Graphics2D graphics2d, Rectangle2D rectangle2d) {
        java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(
                rectangle2d.getX(), rectangle2d.getY(), rectangle2d.getWidth(),
                rectangle2d.getHeight());
        graphics2d.setPaint(color);
        graphics2d.fill(double1);
    }

}
