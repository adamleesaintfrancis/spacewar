package edu.ou.spacewar.objects.shadows;

import java.awt.*;
import edu.ou.mlfw.gui.LineShadow;
import edu.ou.utils.Vector2D;
import edu.ou.spacewar.objects.Laser;

public class LaserShadow extends LineShadow {
	public static final Color LASER_COLOR = Color.MAGENTA;
    public static final Color LASER_LINE_COLOR = Color.MAGENTA;

    final Laser laser;

    public LaserShadow(final Laser laser, Vector2D lineVector, Vector2D position) {
        super(lineVector, position);
        this.laser = laser;
        setColor(LASER_COLOR);
    }

	public boolean drawMe() {
        return laser.isAlive();
    }

}
