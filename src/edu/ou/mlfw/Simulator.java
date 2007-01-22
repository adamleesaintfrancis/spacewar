package edu.ou.mlfw;

import java.io.OutputStream;
import java.util.Collection;

import edu.ou.utils.Initializable;

public interface Simulator extends Initializable
{
	State getState();
	Collection<Controllable> getControllables();
	void shutdown(OutputStream config);
	boolean isRunning();
	void advance();
}
