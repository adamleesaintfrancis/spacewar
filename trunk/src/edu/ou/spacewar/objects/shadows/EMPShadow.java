package edu.ou.spacewar.objects.shadows;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.utils.Vector2D;
import edu.ou.spacewar.objects.EMP;

public class EMPShadow extends Shadow2D {
	public static final Color EMP_COLOR = Color.MAGENTA;

    final EMP laser;

    public EMPShadow(EMP laser) {
        super((int)(EMP.EMP_RADIUS * 2), (int)(EMP.EMP_RADIUS * 2));
        this.laser = laser;
    }

    public Vector2D getRealPosition() {
        return laser.getPosition();
    }

    public boolean drawMe() {
        return laser.isAlive();
    }

    public void draw(Graphics2D g) {
        float radius = laser.getRadius();
        float diameter = radius * 2;

        g.setColor(EMP_COLOR);
        g.fill(new Ellipse2D.Float(drawposition.getX() - radius,
                drawposition.getY() - radius, diameter, diameter));
    }

    public void cleanUp() {
        //do nothing...
    }
}