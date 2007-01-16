package edu.ou.spacewar.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Mar 15, 2006
 * Time: 11:04:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class IllegalClassException extends Exception {
	private static final long serialVersionUID = 1L;

	public IllegalClassException(String s) {
        super(s);
    }
}
