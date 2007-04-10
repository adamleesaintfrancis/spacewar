package edu.ou.spacewar.objects.shadows;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.Base;
import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jfager
 * Date: Apr 7, 2006
 * Time: 2:00:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseShadow extends Shadow2D {
    private final Base base;

    public BaseShadow(Base b) {
        super((int)(Base.BASE_RADIUS * 2), (int)(Base.BASE_RADIUS * 2));
        this.base = b;
    }

    public Vector2D getRealPosition() {
        return base.getPosition();
    }

    public boolean drawMe() {
        return base.isAlive();
    }

    public void draw(Graphics2D g) {
        float radius = Base.BASE_RADIUS;
        float diameter = Base.BASE_RADIUS * 2;

        Ellipse2D.Float shape = new Ellipse2D.Float(drawposition.getX() - radius,
                drawposition.getY() - radius, diameter, diameter);

        String team = base.getTeam();
        if(ShipShadow.teamcolors.containsKey(team)) {
        	Color c = ShipShadow.teamcolors.get(team);
        	g.setColor(c);
            g.fill(shape);
        }
    }

    public void cleanUp() {
        //do nothing...
    }
}
