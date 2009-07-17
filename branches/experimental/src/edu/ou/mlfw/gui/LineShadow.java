package edu.ou.mlfw.gui;

import java.awt.*;

import edu.ou.utils.Vector2D;

public class LineShadow extends Shadow2D {
	private Vector2D realposition;
	private boolean slopesUp;
	private Color myColor = Color.CYAN;
    private boolean drawme;
    private float width;
    
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
    	//System.out.println("X= " + linesegment.getX() + ", Y= " + linesegment.getY());
    	//System.out.println("Angle= " + angle);
		slopesUp = (angle >= 0.0f && angle <= Vector2D.HALFPI) ||
				   (angle < -Vector2D.HALFPI);
		width=2f;
    }
	
	public void setColor(Color newColor) {
		myColor = newColor;
	}
	
	public void setWidth(float w) {
		width = w;
	}
	
	public void cleanUp() {
		// do nothing
	}

	public void draw(Graphics2D g) {
		g.setColor(myColor);
        g.setStroke(new BasicStroke(width));

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

