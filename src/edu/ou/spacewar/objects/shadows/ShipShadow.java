package edu.ou.spacewar.objects.shadows;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.gui.JSpacewarComponent;
import edu.ou.spacewar.objects.Ship;
import edu.ou.utils.Vector2D;

/**
 * The default shadow class for a Ship.
 */
public class ShipShadow extends Shadow2D {
    public static final Color THRUST_COLOR = new Color(255, 242, 23);
    public static final Color THRUST_SPUTTER_COLOR = new Color(193, 72, 8);
    public static final Color SHIELD_COLOR = new Color(190, 40, 140);
    public static final Shape SHIP_SHAPE = new Polygon(new int[]{54, 108, 100, 141, 85, 73, 106, 89, 80, 40, 54, -54,
            -40, -80, -89, -106, -73, -85, -141, -100, -108, -54}, new int[]{-89, 3, 18, 89, 89, 69, 69, 38, 53, 53,
            75, 75, 53, 53, 38, 69, 69, 89, 89, 18, 3, -89}, 22);
    public static final Shape THRUST_SHAPE = new Polygon(new int[]{44, -44, 0}, new int[]{65, 65, 200}, 3);
    public static final Shape THRUST_SPUTTER_SHAPE = new Polygon(new int[]{30, -30, 0}, new int[]{65, 65, 170}, 3);

    private final Ship ship;

    public static final Map<String, Color> teamcolors = new HashMap<String, Color>();

    private static final Iterator<Color> colorwheel
    	= Arrays.asList(new Color[]{Color.RED, Color.BLUE, Color.YELLOW}).iterator();

    private Color shipcolor = new Color(109, 192, 255);

    public ShipShadow(final Ship s) {
        super((int)(Ship.SHIP_RADIUS * 2), (int)(Ship.SHIP_RADIUS * 2));
        ship = s;
    }

    @Override
	public Vector2D getRealPosition() {
        return ship.getPosition();
    }

    @Override
	public boolean drawMe() {
        return ship.isAlive();
    }

    public void setColor(final Color c) {
        shipcolor = c;
    }

    @Override
	public void draw(final Graphics2D g) {
    	g.setStroke(JSpacewarComponent.THIN_STROKE);
        final AffineTransform transform =
                AffineTransform.getTranslateInstance(drawposition.getX(), drawposition.getY());
        transform.rotate(ship.getOrientation().getAngle() + Math.PI / 2);
        transform.scale(.10, .10);

        if (ship.getActiveCommand().thrust) {
            final Shape newThrustShape = transform.createTransformedShape(THRUST_SHAPE);
            g.setPaint(THRUST_COLOR);
            g.fill(newThrustShape);
        } else if (ship.getActiveCommand().thrust) {
            final Shape newThrustShape = transform.createTransformedShape(THRUST_SPUTTER_SHAPE);
            g.setPaint(THRUST_SPUTTER_COLOR);
            g.fill(newThrustShape);
        }

        final Shape newShipShape = transform.createTransformedShape(SHIP_SHAPE);

        //TODO: this is fairly ridiculous, we should probably have a better way
        //to specify colors for teams.
        final String team = ship.getTeam();
        if(team == null) {
        	g.setPaint(shipcolor);
        } else if (teamcolors.containsKey(team)) {
        	g.setPaint(teamcolors.get(team));
        } else if (colorwheel.hasNext()) {
        	final Color c = colorwheel.next();
        	teamcolors.put(team, c);
        	g.setPaint(c);
        } else {
        	g.setPaint(shipcolor);
        }

        g.fill(newShipShape);

        if(ship.hasFlag()) {
            final String fteam = ship.getFlag().getTeam();
            g.setPaint(teamcolors.get(fteam));
            final float x = ship.getPosition().getX();
            final float y = ship.getPosition().getY();
            final float r = ship.getRadius();
            g.drawOval((int)(x-r), (int)(y-r), (int)(r*2), (int)(r*2));
        }

        if(ship.shieldUp()) {
        	g.setPaint(SHIELD_COLOR);
        	final float x = ship.getPosition().getX();
            final float y = ship.getPosition().getY();
            final float r = ship.getRadius();
        	g.drawOval((int)(x-2*r), (int)(y-2*r), (int)(r*4), (int)(r*4));
        }


        final Font font = new Font("Arial", Font.BOLD, 12);
        g.setFont(font);

        String number = Integer.toString(ship.getEnergy());
        g.setPaint(JSpacewarComponent.TEXT_COLOR);
        g.drawString(number, ship.getPosition().getX() + 12, ship.getPosition().getY() + 12);

        number = Integer.toString(ship.getDeaths());
        g.drawString(number, ship.getPosition().getX() + 12, ship.getPosition().getY() + 1);

        if (ship.getName() != null) {
			g.drawString(ship.getName(), ship.getPosition().getX() + 12, ship.getPosition().getY() - 10);
		}

        g.setStroke(JSpacewarComponent.THIN_STROKE);
        g.drawLine(
                (int) ship.getPosition().getX(),
                (int) ship.getPosition().getY(),
                (int) (ship.getPosition().getX() + ship.getVelocity().getX()),
                (int) (ship.getPosition().getY() + ship .getVelocity().getY()));

        number = Integer.toString(ship.getFlags());
        g.setPaint(Color.WHITE);
        g.drawString(number, ship.getPosition().getX() - 24, ship.getPosition().getY() + 12);

        number = Integer.toString(ship.getKills());
        g.setPaint(Color.PINK);
        g.drawString(number, ship.getPosition().getX() - 24, ship.getPosition().getY() - 10);

        number = Integer.toString(ship.getHits());
        g.setPaint(Color.GRAY);
        g.drawString(number, ship.getPosition().getX() - 24, ship.getPosition().getY() + 1);

        number = Integer.toString(ship.getBeacons());
        g.setPaint(Color.GREEN);
        g.drawString(number, ship.getPosition().getX() - 24, ship.getPosition().getY() + 23 );
    }

    @Override
	public void cleanUp() {
        //do nothing...
    }
}