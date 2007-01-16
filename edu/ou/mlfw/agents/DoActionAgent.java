package edu.ou.machinelearning.agents;

import edu.ou.machinelearning.*;

/**
 * Created by IntelliJ IDEA. User: jason Date: Jan 5, 2006 Time: 1:29:05 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class DoActionAgent<C> extends Agent<C> {
	public DoActionAgent(String label) {
		super(label);
	}

	public abstract void takeAction(C controllable);

	public void endAction(C controllable) {
		// do nothing
	}

	public void finish(C controllable) {
		// do nothing
	}

	public void saveResults(String baseFilename) {
		// do nothing
	}
}
