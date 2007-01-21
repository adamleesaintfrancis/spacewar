package edu.ou.spacewar.gui;

import java.awt.*;

import edu.ou.utils.Vector2D;

/**
 * Shadow2D provides a way for objects, agents, or whatever else to draw on the JSpacewarComponent.  The main
 * consideration here is that a Space object represents a toroidal surface, so for some drawing cases, the image
 * will have to be drawn multiple times when they overlap the edges of the screen.  The Shadow class, in conjunction
 * with the JspacewarComponent class, standardizes how this happens.
 * <p/>
 * For images that need to wrap, the width and height passed into a Shadow2D constructor form a ounding box around the
 * graphic.  In conjunction with the getRealPosition() method, JspacewarComponent uses this bounding box information
 * to determine when to draw the image so that it shows up on both side of the screen.
 * <p/>
 * For images that don't need to wrap, the width and height are unnecessary, and can be specified as 0.
 * <p/>
 * At draw time, JSpacewarComponent hands each shadow a Graphics2D object by calling the draw(Graphics2D) method.
 * This is where any drawing operations should be handled.
 * <p/>
 * For examples of how to use Shadows, refer to AIClass.ai.agents.DrawingAgent
 */
public abstract class Shadow2D {
    protected Vector2D drawposition;
    protected Vector2D realposition;
    private int width, halfwidth, height, halfheight;

    /**
     * Initialize the shadow with a bounding box, calculate the halfwidth and halfheight
     *
     * @param width The width of the bounding box.
     * @param height The height of the bounding box.
     */
    public Shadow2D(int width, int height) {
        this.width = width;
        this.halfwidth = width / 2;
        this.height = height;
        this.halfheight = height / 2;
    }


    /**
     * @return The width of the shadow's bounding box.
     */
    public final int getWidth() {
        return width;
    }

    /**
     * @return The halfwidth of the shadow's bounding box.
     */
    public final int getHalfWidth() {
        return halfwidth;
    }

    /**
     * @return The height of the shadow's bounding box.
     */
    public final int getHeight() {
        return height;
    }

    /**
     * @return The halfheight of the shadow's bounding box
     */
    public final int getHalfHeight() {
        return halfheight;
    }

    /**
     * Centers the bounding box around a particular point for drawing.  This should only need to be used by
     * JSpacewarComponent, but is available if you want it for something else.  The draw position gets set back to
     * the real position after the drw routine has finished.
     *
     * @param pos The new drawing position.
     */
    public final void setDrawPosition(Vector2D pos) {
        this.drawposition = pos;
    }

    /**
     * @return The current drawing position.
     */
    public final Vector2D getDrawPosition() {
        return this.drawposition;
    }
    
    /**
     * Reset the drawing position to the real position.
     */
    public final void resetDrawPosition() {
    	this.drawposition = getRealPosition();
    }

    /**
     * JSpacewarComponent uses this method in conjunction with the bounding box 
     * information to determine if the graphic needs to be redrawn to account 
     * for wrapping.  Real position is the position that resetDrawPosition sets
     * the draw position to.   
     *
     * @return The current "real" center position of the bounding box.
     */
    public abstract Vector2D getRealPosition();

    /**
     * Tell the JSpacewarComponent to draw or not draw this shadow.  This does 
     * not remove the shadow, it just keeps it from being drawn.
     *
     * @return Whether the shadow should be drawn or not.
     */
    public abstract boolean drawMe();


    /**
     * The actual drawing routine.  For an example, see
     * @param g
     */
    public abstract void draw(Graphics2D g);


    /**
     * A convenience method that gets called after all the drawings are 
     * complete and the draw position is reset to the real position.
     */
    public abstract void cleanUp();

}
