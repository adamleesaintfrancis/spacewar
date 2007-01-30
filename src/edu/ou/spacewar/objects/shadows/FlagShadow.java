package edu.ou.spacewar.objects.shadows;

import java.awt.*;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.objects.Flag;
import edu.ou.spacewar.objects.Ship;
import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jfager
 * Date: Apr 7, 2006
 * Time: 1:59:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlagShadow extends Shadow2D {
    private final Flag flag;

    public FlagShadow(Flag f) {
        super((int)(f.getRadius() * 2), (int)(f.getRadius() * 2));
        this.flag = f;
    }

    public Vector2D getRealPosition() {
        return flag.getPosition();
    }

    public boolean drawMe() {
        return flag.isAlive();
    }

    public void draw(Graphics2D g) {
        float radius = flag.getRadius();

        int[] trianglexs = new int[3];
        int[] triangleys = new int[3];

        trianglexs[0] = (int)(drawposition.getX() - radius);
        triangleys[0] = (int)(drawposition.getY() - radius);

        trianglexs[1] = trianglexs[0];
        triangleys[1] = (int)(drawposition.getY() + radius);

        trianglexs[2] = (int)(drawposition.getX() + radius);
        triangleys[2] = (triangleys[0] + triangleys[1]) / 2;

        if(flag.getTeam() == Ship.BLUE_TEAM) {
            g.setColor(Color.BLUE);
        } else if(flag.getTeam() == Ship.RED_TEAM) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.WHITE);
        }

        g.fillPolygon(trianglexs, triangleys, 3);
    }

    public void cleanUp() {
        //do nothing...
    }

}
