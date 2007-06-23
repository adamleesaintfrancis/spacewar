package edu.ou.mlfw;

public abstract class AbstractClient<A extends Action, 
									 S extends State, 
                                     C extends Controllable> 
	implements Client<A,S,C> 
{
	private String displayName = "Unset Display Name";
	
	public void setDisplayName(String name) {
		this.displayName = name;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
}
