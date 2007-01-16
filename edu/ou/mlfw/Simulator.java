package edu.ou.mlfw;

import java.io.OutputStream;
import java.util.Collection;

public interface Simulator extends Initializable
{
	SimulatorState getState();
	Collection<Controllable> getControllables();
	void shutdown(OutputStream config);
}
