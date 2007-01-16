package edu.ou.spacewar.gui;

import java.awt.*;

import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Mar 6, 2006
 * Time: 1:58:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class TextBoxShadow extends Shadow2D {
    Vector2D realPosition;
    String string;

    /**
     * Initialize the shadow with a bounding box, calculate the halfwidth and halfheight
     *
     * @param width  The width of the bounding box.
     * @param height The height of the bounding box.
     */
    public TextBoxShadow(int width, int height) {
        super(width, height);
    }

    public void setRealPosition(Vector2D rp) {
        this.realPosition = rp;
    }

    public void setString(String str) {
        this.string = str;
    }

    public Vector2D getRealPosition() {
        return realPosition;
    }

    public boolean drawMe() {
        return true;
    }

    public void draw(Graphics2D g) {
        g.drawString(string, drawposition.getX(), drawposition.getY());
    }

    public void cleanUp() {
        //do nothing
    }
}
