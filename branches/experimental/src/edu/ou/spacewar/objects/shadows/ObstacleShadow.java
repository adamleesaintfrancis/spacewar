package edu.ou.spacewar.objects.shadows;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.gui.JSpacewarComponent;
import edu.ou.spacewar.objects.Obstacle;
import edu.ou.utils.Vector2D;

/**
 * The default shadow class for an Obstacle...
 */
public class ObstacleShadow extends Shadow2D {
    public static final Color OBSTACLE_COLOR = new Color(126, 96, 58);
    public static final Color OBSTACLE_LINE_COLOR = new Color(162, 124, 76);

    private final Obstacle obstacle;

    public ObstacleShadow(final Obstacle o) {
        super((int)(o.getRadius() * 2), (int)(o.getRadius() * 2));
        obstacle = o;
    }

    @Override
	public Vector2D getRealPosition() {
        return obstacle.getPosition();
    }

    @Override
	public boolean drawMe() {
        return obstacle.isAlive();
    }

    @Override
	public void draw(final Graphics2D g) {
        final float radius = obstacle.getRadius();
        final float diameter = obstacle.getRadius() * 2;

        final Ellipse2D.Float shape = new Ellipse2D.Float(drawposition.getX() - radius,
                drawposition.getY() - radius, diameter, diameter);

        g.setColor(OBSTACLE_COLOR);
        g.fill(shape);

        g.setStroke(JSpacewarComponent.STROKE);
        g.setColor(OBSTACLE_LINE_COLOR);
        g.draw(shape);
    }

    @Override
	public void cleanUp() {
        //do nothing...
    }
}
