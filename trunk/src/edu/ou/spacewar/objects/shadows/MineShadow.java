package edu.ou.spacewar.objects.shadows;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.gui.JSpacewarComponent;
import edu.ou.spacewar.objects.Mine;
import edu.ou.utils.Vector2D;

public class MineShadow extends Shadow2D {
	public static final Color MINE_COLOR = Color.RED;
    public static final Color MINE_LINE_COLOR = new Color(200, 55, 55);

    final Mine mine;

    public MineShadow(final Mine mine) {
        super((int)(Mine.MINE_RADIUS * 2), (int)(Mine.MINE_RADIUS * 2));
        this.mine = mine;
    }

    @Override
	public Vector2D getRealPosition() {
        return mine.getPosition();
    }

    @Override
	public boolean drawMe() {
        return mine.isAlive();
    }

    @Override
	public void draw(final Graphics2D g) {
        final float radius = Mine.MINE_RADIUS;
        final float diameter = radius * 2;

        final Ellipse2D.Float shape = new Ellipse2D.Float(drawposition.getX() - radius,
                drawposition.getY() - radius, diameter, diameter);

        g.setColor(MINE_COLOR);
        g.fill(shape);

        g.setStroke(JSpacewarComponent.THICK_STROKE);
        g.setColor(MINE_LINE_COLOR);
        g.draw(shape);
    }

    @Override
	public void cleanUp() {
        //do nothing...
    }

}
