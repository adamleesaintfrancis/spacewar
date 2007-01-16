package edu.ou.spacewar.exceptions;

public class SingletonException extends Exception {
	private static final long serialVersionUID = 849671710689664903L;

	public SingletonException() {
        super();
    }

    public SingletonException(String s) {
        super(s);
    }
}
