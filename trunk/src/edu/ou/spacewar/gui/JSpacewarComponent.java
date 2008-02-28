package edu.ou.spacewar.gui;

import java.awt.*;

import javax.swing.JComponent;

import edu.ou.mlfw.gui.*;
import edu.ou.utils.Vector2D;

//TODO:  This doesn't have to be spacewar specific...it just draws shadows in
//a wraparound fashion on some win
public class JSpacewarComponent extends JComponent implements Shadow2DCanvas {
    private static final long serialVersionUID = 1L;

    public static final Color TEXT_COLOR = new Color(0, 218, 159);

    public static final BasicStroke THIN_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final BasicStroke STROKE = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final BasicStroke THICK_STROKE = new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private Iterable<Shadow2D> simulatorshadows = Shadow2D.EMPTY_ITER;
    private Iterable<Shadow2D> clientshadows = Shadow2D.EMPTY_ITER;
    private boolean showshadows = true;
    private final int width, height;

    public JSpacewarComponent(final int width, final int height)
    {
    	this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
    }

    public void toggleShadows() {
        showshadows = !showshadows;
    }

    public void setClientShadowSource(final Iterable<Shadow2D> clientshadows) {
    	this.clientshadows = clientshadows;
    }

    public void setSimulatorShadowSource(final Iterable<Shadow2D> simulatorshadows) {
    	this.simulatorshadows = simulatorshadows;
    }

    @Override
	protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        		RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

		synchronized (simulatorshadows) {  // must be used for iterator
	        for (final Shadow2D shadow : simulatorshadows) {
	        	if (!shadow.drawMe()) {
	        		continue;
	        	}
	        	drawShadow(shadow, graphics);
	        }
		}

        if (showshadows) {
        	synchronized (clientshadows) { // must be used for iterator
				for (final Shadow2D shadow : clientshadows) {
	        		if (!shadow.drawMe()) {
	        			continue;
	        		}
	        		drawShadow(shadow, graphics);
	        	}
        	}
        }
    }

    private void drawShadow(final Shadow2D shadow, final Graphics2D graphics) {
        final Vector2D position = shadow.getRealPosition();
        final float x = position.getX();
        final float y = position.getY();

        if (x < shadow.getHalfWidth()) {
            //goes off screen to left
            shadow.setDrawPosition(new Vector2D(x + width, y));
            shadow.draw(graphics);
            if (y < shadow.getHalfHeight()) {
                //also goes off screen to the bottom
                shadow.setDrawPosition(new Vector2D(x, y + height));
                shadow.draw(graphics);
                shadow.setDrawPosition(new Vector2D(x + width, y + height));
                shadow.draw(graphics);
            } else if (y >= height - shadow.getHalfHeight()) {
                //also goes off screen to the top
                shadow.setDrawPosition(new Vector2D(x, y - height));
                shadow.draw(graphics);
                shadow.setDrawPosition(new Vector2D(x + width, y - height));
                shadow.draw(graphics);
            }
        } else if (x >= width - shadow.getHalfWidth()) {
            //goes off screen to right
            shadow.setDrawPosition(new Vector2D(x - width, y));
            shadow.draw(graphics);
            if (y < shadow.getHalfHeight()) {
                //goes off screen to bottom
                shadow.setDrawPosition(new Vector2D(x, y + height));
                shadow.draw(graphics);
                shadow.setDrawPosition(new Vector2D(x - width, y + height));
                shadow.draw(graphics);
            } else if (y >= height - shadow.getHalfHeight()) {
                //also goes off screen to the top
                shadow.setDrawPosition(new Vector2D(x, y - height));
                shadow.draw(graphics);
                shadow.setDrawPosition(new Vector2D(x - width, y - height));
                shadow.draw(graphics);
            }
        } else if (y < shadow.getHalfHeight()) {
            //goes off screen to bottom
            shadow.setDrawPosition(new Vector2D(x, y + height));
            shadow.draw(graphics);
        } else if (y >= height - shadow.getHalfHeight()) {
            //goes off screen to top
            shadow.setDrawPosition(new Vector2D(x, y - height));
            shadow.draw(graphics);
        }

        shadow.setDrawPosition(position);
        shadow.draw(graphics);
        shadow.cleanUp();
    }
}



