package edu.ou.mlfw.gui;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.utils.Vector2D;

/**
 * The default shadow class for a Beacon.
 */
public class CircleShadow extends Shadow2D {
    private static final int radius = 5;
    private Vector2D location;
    private Color color = Color.RED;
    boolean drawMe;
    
    public CircleShadow(Vector2D location) {
        super(radius * 2, radius * 2);
        this.location = location;
    }

    public Vector2D getRealPosition() {
        return location;
    }

    public boolean drawMe() {
        return drawMe;
    }
    
    public void setDrawMe(boolean b) {
    	drawMe = b;
    }

    public void draw(Graphics2D g) {
        Ellipse2D.Float shape = new Ellipse2D.Float(drawposition.getX() - radius,
                drawposition.getY() - radius, 2 * radius, 2 * radius);

        g.setColor(color);
        g.fill(shape);
        g.draw(shape);
    }
    
    public void setColor(Color newColor) {
    	color = newColor;
    }

    public void cleanUp() {
        //do nothing...
    }
}
