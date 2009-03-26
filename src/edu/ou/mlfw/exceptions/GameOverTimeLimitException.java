/**
 * 
 */
package edu.ou.mlfw.exceptions;

/**
 * @author Patrick Yost
 *
 */
public class GameOverTimeLimitException extends Exception {
	
	/**
	 * This is used to label other exceptions as this.  It circumvents the beautiful care
	 * taken by java to guard against mistaken exceptions being passed... but it does fix the problem. 
	 */
//	public static final String SHOULD_BE_OVERTIME_EXCEPTION = 
//		"This should be a GameOverTimeLimitException!!!";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -605196164479744307L;

	/**
	 * 
	 */
	public GameOverTimeLimitException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 */
	public GameOverTimeLimitException(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

}
