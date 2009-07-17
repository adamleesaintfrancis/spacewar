package edu.ou.mlfw;

public abstract class AbstractClient implements Client
{
	private String displayName = "Unset Display Name";
	
	public void setDisplayName(String name) {
		this.displayName = name;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
}
