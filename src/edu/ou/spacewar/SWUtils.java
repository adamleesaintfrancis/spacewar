package edu.ou.spacewar;

import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableObject2D;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.spacewar.simulator.Space;
import edu.ou.utils.SortUtils;
import edu.ou.utils.Vector2D;

/**
 * Created by IntelliJ IDEA.
 * User: jfager
 * Date: Feb 13, 2006
 * Time: 11:38:10 AM
 * <p/>
 * This class contains static utility methods for common Spacewar operations
 */
public class SWUtils {
	/**
     * This method provides a way to build a fully connected graph out of a
     * set of given positions in a given toroidal space.  The edges of the graph are
     * given as the vector pointing in the shortest direction from point A to
     * point B, for every pair of positions.  This assumes that the input positions
     * are vectors pointing from the origin of the toroidal space in question.
     *
     * @param positions The set of positions to inspect.  None should be null.
     * @param width     The 'width' of the toroidal space, should be > 0.
     * @param height    The 'height' of the toroidal space, should be > 0.
     * @return A 2-D matrix holding the distance vectors.
     */
    public static Vector2D[][] findDistances(Vector2D[] positions, float width, float height) {
        Vector2D[][] out = new Vector2D[positions.length][positions.length];
        for (int i = 0; i < positions.length; i++) {
            for (int j = i + 1; j < positions.length; j++) {
                Vector2D distance = Space.findShortestDistance(positions[i], positions[j], width, height);
                out[i][j] = distance;
                out[j][i] = distance.negate();
            }
        }
        return out;
    }


    /**
     * This method takes an origin position and a set of other position vectors, and sorts those vectors by
     * their distance from the origin position in the given toroidal space.  In addition to sorting the position
     * vectors in place, the method returns a corresponding array of distance vectors.
     *
     * @param origin The origin position.  Should not be null.
     * @param others The set of positions to inspect.  THIS ARRAY WILL BE ALTERED!  None should be null.
     * @param width  The 'width' of the toroidal space, should be > 0.
     * @param height The 'height' of the toroidal space, should be > 0.
     * @return The sorted distance vectors.
     */
    public static Vector2D[] sortByDistance(Vector2D origin, Vector2D[] others, float width, float height) {
        Vector2D[] out = new Vector2D[others.length];
        for (int i = 0; i < others.length; i++) {
            out[i] = Space.findShortestDistance(origin, others[i], width, height);
        }

        //do a symmetric quicksort:
        SortUtils.symmetricSort(out, others);

        return out;
    }


    /**
     * This method takes an start object and a set of other objects, and sorts those objects by
     * their distance from the start object in the given toroidal space.  In addition to sorting the
     * objects in place, the method returns a corresponding array of distance vectors.
     *
     * @param origin The origin position.  Should not be null.
     * @param others The set of positions to inspect.  THIS ARRAY WILL BE ALTERED!  None should be null.
     * @param width  The 'width' of the toroidal space, should be > 0.
     * @param height The 'height' of the toroidal space, should be > 0.
     * @return The sorted distance vectors.
     */
    public static <O1 extends Object2D, O2 extends Object2D> Vector2D[]
            sortByDistance(O1 origin, O2[] others, float width, float height) {

        Vector2D[] out = new Vector2D[others.length];
        for (int i = 0; i < others.length; i++) {
            out[i] = Space.findShortestDistance(origin.getPosition(), others[i].getPosition(), width, height);
        }

        SortUtils.symmetricSort(out, others);

        return out;
    }

    /**
     * This method takes an start object and a set of other objects, and sorts those objects by
     * their distance from the start object in the given toroidal space.  In addition to sorting the
     * objects in place, the method returns a corresponding array of distance vectors.
     *
     * @param origin The origin position.  Should not be null.
     * @param others The set of positions to inspect.  THIS ARRAY WILL BE ALTERED!  None should be null.
     * @param width  The 'width' of the toroidal space, should be > 0.
     * @param height The 'height' of the toroidal space, should be > 0.
     * @return The sorted distance vectors.
     */
    public static <O1 extends ImmutableObject2D, O2 extends ImmutableObject2D> Vector2D[]
            sortImmutablesByDistance(O1 origin, O2[] others, float width, float height) {

        Vector2D[] out = new Vector2D[others.length];
        for (int i = 0; i < others.length; i++) {
            out[i] = Space.findShortestDistance(origin.getPosition(), others[i].getPosition(), width, height);
        }

        SortUtils.symmetricSort(out, others);

        return out;
    }

    /**
     * Returns the command needed for alligning the orientation of
     * the ship with the vector v. Returns null if the orientation
     * of the ship is within the supplied precision.
     *
     * @author Charles DeGranville
     *
     * @param shipOrt   The orientation of the ship.
     * @param v         The vector to allign the orientation of the ship.
     * @param precision The precision.
     * @return The command to be executed.
     */
    public static ShipCommand AllignmentController(Vector2D shipOrt, Vector2D v, float precision) {
        double angle = v.angleBetween(shipOrt);

        if (Math.abs(angle) < precision) {
            return null;
        } else if (angle > 0.0f) {
            return ShipCommand.TurnLeft;
        } else {
            return ShipCommand.TurnRight;
        }
    }
    
}
