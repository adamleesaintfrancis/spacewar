package edu.ou.spacewar.actions;

import edu.ou.mlfw.Action;
import edu.ou.spacewar.*;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.*;
import edu.ou.utils.Vector2D;

/**
 * Thanks to Charles DeGranville for the use of his avoidance code...
 */
public class FleeAction implements Action{
	private int n;
	private static final float buffer = 40f;
	private ImmutableObject2D[] objects = null;

    /**
     * Avoids the objects specified in the object array.
     *
     * @param myShip The current representation of my ship.
     * @param dists The vectors pointing at the objects to flee from
     * @return The command needed to be executed to flee from the objects in objs.
     */
    public ShipCommand fleeObjects(final ImmutableShip myShip, final ImmutableSpacewarState state) {
    	final Vector2D[] dists = new Vector2D[objects.length];

    	for(int i = 0; i < objects.length; i++){
    		dists[i] = state.findShortestDistance(myShip.getPosition(), objects[i].getPosition());
    	}

    	Vector2D dest = Vector2D.ZERO_VECTOR;

        for(int i = 0;(i < n) && (i < dists.length); i++) {
            final float mag = dists[i].getMagnitude();
            if(mag == 0) {
                //ignore self
                continue;
            }

            if(mag > buffer) {
                break;
            }
            dest = dest.add(dists[i]);
        }

        dest = dest.negate();

        final ShipCommand cmd = SWUtils.AllignmentController(myShip.getOrientation(), dest, 0.5f);
        return (cmd == null) ? ShipCommand.Thrust : cmd;
    }

    /**
     * This method is called by the client code to set the parameters of the desired
     * objects to flee from.
     *
     * @param obs	This is the collection of objects we would like to flee from
     * @param n		This sets the number of objects in obs to flee from. The method
     * 				will flee from the nearest n objects.
     */
    public void setFleeObjects(final ImmutableObject2D[] obs, final int n){
    	objects = obs;
    	this.n = n;
    }
}
