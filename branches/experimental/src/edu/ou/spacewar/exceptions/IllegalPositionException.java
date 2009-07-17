package edu.ou.spacewar.exceptions;

import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Feb 11, 2006
 * Time: 4:15:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class IllegalPositionException extends Exception {
	private static final long serialVersionUID = 5277444298406447809L;
	private final Vector2D pos;

    public IllegalPositionException(Vector2D position) {
        super();
        this.pos = position;
    }

    public void printStackTrace() {
        System.err.println("Illegal position: " + this.pos);
        super.printStackTrace();
    }
}
