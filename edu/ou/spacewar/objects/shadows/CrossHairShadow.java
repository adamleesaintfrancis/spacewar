package edu.ou.spacewar.objects.shadows;

import java.awt.*;

import edu.ou.spacewar.gui.Shadow2D;
import edu.ou.spacewar.objects.Ship;
import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Mar 7, 2006
 * Time: 8:56:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrossHairShadow extends Shadow2D {
    private static final float FADEMAX = 1.0f;
    private static final float FADERATE = 0.9f;
    private Vector2D realposition = Vector2D.ZERO_VECTOR;
    private boolean drawme;
    private float fade = FADEMAX;

    public CrossHairShadow() {
        super((int) Ship.SHIP_RADIUS * 3, (int) Ship.SHIP_RADIUS * 3);
    }

    public Vector2D getRealPosition() {
        return realposition;
    }

    public void setRealPosition(Vector2D pos) {
        this.realposition = pos;
        fade = FADEMAX;
    }

    public void setDrawMe(boolean b) {
        this.drawme = b;
    }

    public boolean drawMe() {
        return drawme;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2f));

        int width = (int)(getWidth() * fade);
        int halfwidth = width / 2;

        int height = (int)(getHeight() * fade);
        int halfheight = height / 2;

        g.drawOval((int) drawposition.getX() - halfwidth,
                (int) drawposition.getY() - halfheight,
                width, height);

        g.drawLine((int) drawposition.getX() - halfwidth, (int) drawposition.getY(),
                (int) drawposition.getX() + halfwidth, (int) drawposition.getY());

        g.drawLine((int) drawposition.getX(), (int) drawposition.getY() - halfheight,
                (int) drawposition.getX(), (int) drawposition.getY() + halfheight);

        fade *= FADERATE;

    }

    public void cleanUp() {
        //do nothing
    }
}
