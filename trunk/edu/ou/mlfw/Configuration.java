package edu.ou.mlfw;

public interface Configuration<T> 
{
	public T initialize();
	
	public void load(String configFileLocation)
		throws IllegalConfigurationException;
		
	public void save(String configFileLocation)
		throws IllegalConfigurationException;
}
