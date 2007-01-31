package edu.ou.mlfw.gui;

import java.awt.*;

import edu.ou.utils.Vector2D;

public class LineShadow extends Shadow2D {
	private Vector2D realposition;
	private boolean slopesUp;
	private Color myColor = Color.RED;
    private boolean drawme;
    
    /**
     * start should be the starting point of the line, and linesegment should 
     * be a vector pointing from the starting point to the desired end point of 
     * the line segment. We do this instead of just passing in the start and 
     * end points to avoid ambiguity when there is more than one way to draw a 
     * line between two points.
     * 
     * @param line
     */
    public LineShadow(Vector2D linesegment, Vector2D start) {
    	super((int)Math.abs(linesegment.getX()), 
    			(int)Math.abs(linesegment.getY()));
    	
    	realposition = start.add(linesegment.divide(2.0f));
    	float angle = linesegment.getAngle();
		slopesUp = (angle >= 0.0f && angle <= Vector2D.HALFPI) ||
				   (angle >= Math.PI && angle <= Vector2D.THREEHALFPI);
    }
	
	public void setColor(Color newColor) {
		myColor = newColor;
	}
	
	public void cleanUp() {
		// do nothing
	}

	public void draw(Graphics2D g) {
		g.setColor(myColor);
        g.setStroke(new BasicStroke(2f));

        if(slopesUp) {
        	g.drawLine((int) drawposition.getX() - getHalfWidth(), 
        			(int) drawposition.getY() - getHalfHeight(),
        			(int) drawposition.getX() + getHalfWidth(), 
        			(int) drawposition.getY() + getHalfHeight());
        } else {
        	g.drawLine((int) drawposition.getX() - getHalfWidth(), 
        			(int) drawposition.getY() + getHalfHeight(),
        			(int) drawposition.getX() + getHalfWidth(), 
        			(int) drawposition.getY() - getHalfHeight());
        }
 	}

	public boolean drawMe() {
		return drawme;
	}
	
	public void setDrawMe(boolean b) {
        this.drawme = b;
    }
	

	public Vector2D getRealPosition() {
		return realposition;
	}
	
	
	
}

