package edu.ou.spacewar.objects.shadows;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.ou.spacewar.gui.JSpacewarComponent;
import edu.ou.spacewar.gui.Shadow2D;
import edu.ou.spacewar.objects.Beacon;
import edu.ou.utils.Vector2D;

/**
 * The default shadow class for a Beacon.
 */
public class BeaconShadow extends Shadow2D {
    public static final Color BEACON_COLOR = Color.GREEN;
    public static final Color BEACON_LINE_COLOR = new Color(55, 200, 55);

    private final Beacon beacon;

    public BeaconShadow(Beacon b) {
        super((int)(Beacon.BEACON_RADIUS * 2), (int)(Beacon.BEACON_RADIUS * 2));
        this.beacon = b;
    }

    public Vector2D getRealPosition() {
        return beacon.getPosition();
    }

    public boolean drawMe() {
        return beacon.isAlive();
    }

    public void draw(Graphics2D g) {
        float radius = Beacon.BEACON_RADIUS;
        float diameter = Beacon.BEACON_RADIUS * 2;

        Ellipse2D.Float shape = new Ellipse2D.Float(drawposition.getX() - radius,
                drawposition.getY() - radius, diameter, diameter);

        g.setColor(BEACON_COLOR);
        g.fill(shape);

        g.setStroke(JSpacewarComponent.THICK_STROKE);
        g.setColor(BEACON_LINE_COLOR);
        g.draw(shape);
    }

    public void cleanUp() {
        //do nothing...
    }
}
