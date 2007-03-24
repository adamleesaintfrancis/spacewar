package edu.ou.spacewar.simulator;

import edu.ou.spacewar.exceptions.NoOpenPositionException;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

import edu.ou.spacewar.simulator.Space.DistanceCache.StaleCacheException;
import edu.ou.utils.ArrayIterator;
import edu.ou.utils.Vector2D;

/**
 * This class is responsible for handling the physics of the 2D toroidal space
 * and the objects within it. This includes collision detection, the size of the
 * space, placing Object2Ds in the space, etc.
 */
public abstract class Space implements CollisionHandler, Iterable<Object2D> {
	private final float width, halfWidth, height, halfHeight;

	private final CollisionHandler collisionHandler;

	// distances, tempDistances, and distancesSaved are used for caching
	private final DistanceCache distanceCache;

	// The main object storage array. This is marked protected because we want
	// to farm out to a subclass the responsibility for handling the details of
	// what kinds of objects are stored where and how those objects are accessed
	protected final Object2D[] objects;

	// Timestamp keeps track of the elapsed simulation time in seconds,
	// while stepcount keeps track of how many times advanceTime() has been
	// called.
	private float timestamp = 0.0f;

	private int stepcount = 0;

	public Space(float width, float height, CollisionHandler collisionHandler,
			int numberOfObjects) {
		this.width = width;
		this.height = height;
		if (collisionHandler != null)
			this.collisionHandler = collisionHandler;
		else
			this.collisionHandler = this;
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;

		this.objects = new Object2D[numberOfObjects];
		this.distanceCache = new DistanceCache(this);
	}

	public Iterator<Object2D> iterator() {
		return new ArrayIterator<Object2D>(this.objects, 0, this.objects.length);
	}

	public float getTimestamp() {
		return this.timestamp;
	}

	public int getStepCount() {
		return this.stepcount;
	}

	public final float getWidth() {
		return width;
	}

	public final float getHeight() {
		return height;
	}

	public final Object2D[] getLiveObjects() {
		ArrayList<Object2D> liveobjs = new ArrayList<Object2D>(objects.length);
		for (Object2D obj : objects) {
			if (obj != null && obj.isAlive()) {
				liveobjs.add(obj);
			}
		}
		Object2D[] out = new Object2D[liveobjs.size()];
		return liveobjs.toArray(out);
	}

	public Object2D[] getObjects() {
		return objects;
	}

	public CollisionHandler getCollisionHandler() {
		return collisionHandler;
	}

	/**
	 * Finds the shortest distance between points a and b taking into account
	 * the fact that space is a torus.
	 * 
	 * @param a
	 *            the start point
	 * @param b
	 *            the end point
	 * @return the shortest vector that starts at point a and ends at point b
	 */
	public Vector2D findShortestDistance(Vector2D a, Vector2D b) {
		return findShortestDistance(a, b, this.width, this.height,
				this.halfWidth, this.halfHeight);
	}

	public Vector2D findShortestDistance(Object2D a, Object2D b) {
		try {
			return distanceCache.getDistance(this.timestamp, a, b);
		} catch (StaleCacheException e) {
			return findShortestDistance(a.getPosition(), b.getPosition());
		}
	}

	public Vector2D findOpenPosition(final float radius, final float buffer,
										final Random rand, final int attempts) 
		throws NoOpenPositionException 
	{
		for (int i = 0; i < attempts; i++) {
			// initialize the iteration with a new candidate position...
			float x = rand.nextFloat() * width;
			float y = rand.nextFloat() * height;
			Vector2D position = new Vector2D(x, y);

			if (isOpenAtPosition(position, radius, buffer)) {
				return position;
			}
		}
		throw new NoOpenPositionException();
	}

	public boolean isOpenAtPosition(Vector2D position, float radius,
			float buffer) {
		for (Object2D o : objects) {
			if (o != null && o.isAlive()) {
				Vector2D dist = findShortestDistance(position, o.position);
				if (dist.getMagnitude() < radius + buffer + o.getRadius()) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * advanceTime is the main driver of the physics engine. The specified
	 * increment is how many simulated seconds the simulator will advance before
	 * this method returns. If any collisions occur in this interval, they will
	 * be handled appropriately (with some caveats, keep reading).
	 * 
	 * You would prefer one increment to another based on three factors: update
	 * opportunities, accuracy, and pacing.
	 * 
	 * First, you may need to update objects to change how they are handled by 
	 * the physics engine.  This engine is intended for a single-threaded 
	 * environment, and as such, provides zero guarantee that changes made to 
	 * objects while this method is executing will be handled properly. 
	 * 
	 * Second, the physics engine is pretty basic, and can miss some collisions
	 * if the interval is set too high.  If two objects would completely pass
	 * through one another over the course of the given interval (that is, they
	 * do not intersect before and after the interval, but would have at some
	 * point within the interval), then this engine will miss the collision.
	 * 
	 * Third, if you are displaying the simulation, then you want to make sure
	 * that the simulator is advancing at the same pace that you are drawing to
	 * the display (that is, you want the simulated time to appear to be in 
	 * sync with real time).
	 * 
	 * If you are not displaying the simulation, you should pass in the largest
	 * increment that gives sufficient opportunity for updates and minimizes 
	 * the risk of missed collisions, as larger increments make for much faster
	 * simulations.
	 * 
	 * TODO: Fix to remove missed collision possibility?  Maybe not worth effort.
	 * 
	 * @param incrementInSeconds
	 */
	public void advanceTime(float incrementInSeconds) {
		this.timestamp += incrementInSeconds;
		this.stepcount++;
		
		// let all the objects update themselves
		for (Object2D o : objects) {
			if (o != null && o.alive) {
				//we hope that nobody updates themselves badly (i.e. setting
				//their own position so they intersect with another object)
				o.advanceTime(incrementInSeconds);
			}
		}
	
		// find and update with collisions
		while (incrementInSeconds >= 0.0f) {
			this.distanceCache.forceUpdate(this.timestamp);
			float advanceTo = incrementInSeconds;  
			Object2D object1 = null;
			Object2D object2 = null;

			for (int i = 0; i < objects.length; i++) {
				Object2D o1 = this.objects[i];
				if (o1 == null || !o1.alive)
					continue;

				for (int j = i + 1; j < objects.length; j++) {
					Object2D o2 = this.objects[j];
					if (o2 == null || !o2.alive)
						continue;

					Vector2D distance = this.distanceCache.fastGetDistance(i, j);
					Vector2D velocity = o1.velocity.subtract(o2.velocity);

					float ddotv = distance.dot(velocity);

					if (ddotv < 0)
						continue;

					float vdotv = velocity.dot(velocity);
					float determinate = o1.radius + o2.radius;
					determinate = distance.dot(distance) - determinate
							* determinate;
					determinate = ddotv * ddotv - vdotv * determinate;

					if (determinate <= 0)
						continue;

					float time = (ddotv - (float) Math.sqrt(determinate))
							/ vdotv;

					if (time <= advanceTo && time >= 0) {
						advanceTo = time;
						object1 = o1;
						object2 = o2;
						// object1i = i;
						// object2i = j;

						if (advanceTo == 0)
							break;
					}
				}

				if (advanceTo == 0 && object1 != null)
					break;
			}

			for (int i = 0; i < objects.length; i++) {
				Object2D o1 = this.objects[i];

				if (o1 == null || !o1.alive) {
					continue;
				}

				o1.position = o1.position.add(o1.velocity.multiply(advanceTo));

				// fix positions after potential wrap around
				while (o1.position.getX() < 0)
					o1.position = new Vector2D(o1.position.getX() + width,
							o1.position.getY());
				while (o1.position.getY() < 0)
					o1.position = new Vector2D(o1.position.getX(), o1.position
							.getY()
							+ height);
				while (o1.position.getX() >= width)
					o1.position = new Vector2D(o1.position.getX() - width,
							o1.position.getY());
				while (o1.position.getY() >= height)
					o1.position = new Vector2D(o1.position.getX(), o1.position
							.getY()
							- height);
			}

			if (object1 != null) {
				Vector2D distance = this.findShortestDistance(object1.getPosition(),
						object2.getPosition());
				this.collisionHandler.handleCollision(distance.unit(), object1,
						object2);
			}

			incrementInSeconds -= advanceTo;

			if (incrementInSeconds == 0 && object1 == null)
				incrementInSeconds = -1;
		}
	}

	/**
	 * Finds the nearest object of the given type.
	 * 
	 * @param <T>
	 *            The type to look for.
	 * @param ship
	 *            The ship to find obstacles near.
	 * @param objectClass
	 *            The class of object to find.
	 * @return The closest object of the given type.
	 */
	public <T extends Object2D> T findNearestObject(Object2D obj, 
													Class<T> objectClass) 
	{
		this.distanceCache.update(this.timestamp);

		int index = obj.spaceIndex;

		int minIndex = -1;
		float minSquaredDistance = Float.MAX_VALUE;
		float squaredDistance;

		for (int i = 0; i < this.objects.length; i++) {
			if (index == i)
				continue;

			Object2D object = this.objects[i];

			if (!object.alive || !objectClass.isInstance(object))
				continue;

			final Vector2D dist = this.distanceCache.fastGetDistance(index, i);
			squaredDistance = dist.dot(dist);
			if (squaredDistance < minSquaredDistance) {
				minIndex = i;
				minSquaredDistance = squaredDistance;
			}
		}

		if (minIndex == -1)
			return null;

		return objectClass.cast(this.objects[minIndex]);
	}

	/**
	 * Finds the nearest objects of the given type. Objects are returned in 
	 * sorted order from closest to farthest.
	 * 
	 * @param <T>
	 *            The type to look for.
	 * @param ship
	 *            The ship to find objects near.
	 * @param count
	 *            The number of objects to find.
	 * @param objectClass
	 *            The class of object to find.
	 * @return The n closest object of the given type. If there are not enough
	 *         objects, then the array will be filled with trailing nulls.
	 */
	public <T extends Object2D> T[] findNearestObjects(Object2D obj, 
														int count, 
														Class<T> objectClass) 
	{
		this.distanceCache.update(this.timestamp);
		final int index = obj.spaceIndex;
		final T[] array = createArray(objectClass, count);
		final float[] tempDistances = new float[this.objects.length];

		for (int i = 0; i < this.objects.length; i++) {
			if (index == i) {
				tempDistances[i] = 0;
				continue;
			}

			Object2D object = this.objects[i];

			if (!object.alive || !objectClass.isInstance(object)) {
				tempDistances[i] = 0;
				continue;
			}

			final Vector2D dist = this.distanceCache.fastGetDistance(index, i); 
			tempDistances[i] = dist.dot(dist);
		}

		for (int i = 0; i < count; i++) {
			int minIndex = -1;
			float minDistance = Float.MAX_VALUE;

			for (int j = 0; j < this.objects.length; j++) {
				if (tempDistances[j] != 0 
						&& tempDistances[j] < minDistance) {
					minIndex = j;
					minDistance = tempDistances[j];
				}
			}

			if (minIndex != -1) {
				tempDistances[minIndex] = 0;
				array[i] = objectClass.cast(this.objects[minIndex]);
			} else {
				for (; i < count; i++)
					array[i] = null;
			}
		}

		return array;
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] createArray(Class<T> objectClass, int length) {
		return (T[]) Array.newInstance(objectClass, length);
	}

	public void handleCollision(Vector2D normal, Object2D object1,
			Object2D object2) {
		Space.collide(1, normal, object1, object2);
	}

	public static void collide(float coefficientOfRestitution, Vector2D normal,
			Object2D object1, Object2D object2) {
		float o1v = normal.dot(object1.velocity);
		float o2v = normal.dot(object2.velocity);

		Vector2D o1vx = normal.multiply(o1v);
		Vector2D o2vx = normal.multiply(o2v);

		Vector2D o1vxf = o2vx.multiply(
				(1 + coefficientOfRestitution) * object2.mass).add(
				o1vx.multiply(object1.mass - coefficientOfRestitution
						* object2.mass)).divide(object1.mass + object2.mass);
		Vector2D o2vxf = o1vxf.add(o1vx.multiply(coefficientOfRestitution))
				.subtract(o2vx.multiply(coefficientOfRestitution));

		Vector2D o1vy = object1.velocity.subtract(o1vx);
		Vector2D o2vy = object2.velocity.subtract(o2vx);

		Vector2D o1vyf = o1vy.multiply(object1.mass).add(
				o2vy.multiply((1 - coefficientOfRestitution) * object2.mass))
				.divide(
						object1.mass + (1 - coefficientOfRestitution)
								* object2.mass);
		Vector2D o2vyf = o2vy.multiply(object2.mass).add(
				o1vy.multiply((1 - coefficientOfRestitution) * object1.mass))
				.divide(
						object2.mass + (1 - coefficientOfRestitution)
								* object1.mass);

		object1.velocity = o1vyf.add(o1vxf);
		object2.velocity = o2vyf.add(o2vxf);
	}

	/**
	 * A convenience method for finding a vector pointing from position A to
	 * position B along the shortest path in a toroidal environment with the
	 * given dimensions. This assumes that the input positions are vectors
	 * pointing from the origin of the toroidal space in question.
	 * 
	 * @param a
	 *            The start position. Should not be null.
	 * @param b
	 *            The end position. Should not be null.
	 * @param width
	 *            The 'width' of the toroidal space, should be > 0.
	 * @param height
	 *            The 'height' of the toroidal space, should be > 0.
	 * @return A vector pointing from position A to position B along the
	 *         shortest path in a toroidal environment with the given
	 *         dimensions.
	 */
	public static Vector2D findShortestDistance(Vector2D a, Vector2D b,
			float width, float height) {
		return findShortestDistance(a, b, width, height, width / 2.0f,
				height / 2.0f);
	}

	public static Vector2D findShortestDistance(final Vector2D a,
			final Vector2D b, final float width, final float height,
			final float halfWidth, final float halfHeight) {
		final float x = b.getX() - a.getX();
		final float y = b.getY() - a.getY();
		if (x > halfWidth) {
			if (y > halfHeight)
				return new Vector2D(x - width, y - height);
			else if (y < -halfHeight)
				return new Vector2D(x - width, y + height);
			else
				return new Vector2D(x - width, y);
		} else if (x < -halfWidth) {
			if (y > halfHeight)
				return new Vector2D(x + width, y - height);
			else if (y < -halfHeight)
				return new Vector2D(x + width, y + height);
			else
				return new Vector2D(x + width, y);
		} else if (y > halfHeight)
			return new Vector2D(x, y - height);
		else if (y < -halfHeight)
			return new Vector2D(x, y + height);
		else
			return new Vector2D(x, y);
	}

	static class DistanceCache {
		static class StaleCacheException extends Exception {
			private static final long serialVersionUID = 1L;
		}

		private final Space space;
		private final Vector2D[][] distances;

		private float timestamp;

		DistanceCache(final Space space) {
			final int size = space.objects.length;
			this.distances = new Vector2D[size][size];
			this.space = space;
		}

		final Vector2D getDistance(final float timestamp,
				final Object2D a, final Object2D b) throws StaleCacheException {
			if (timestamp != this.timestamp) {
				throw new StaleCacheException();
			}
			return distances[a.spaceIndex][b.spaceIndex];
		}

		final Vector2D fastGetDistance(final int a, final int b) {
			return distances[a][b];
		}

		final void update(final float newtimestamp) {
			if(this.timestamp != newtimestamp) {
				forceUpdate(newtimestamp);
			}
		}
		
		final void forceUpdate(final float newtimestamp) {
			Object2D[] objs = space.getObjects();
			assert (objs.length == this.distances.length);
			for (int i = 0; i < objs.length; i++) {
				final Object2D o1 = objs[i];
				if (o1 == null || !o1.alive)
					continue;

				for (int j = i + 1; j < objs.length; j++) {
					final Object2D o2 = objs[j];
					if (o2 == null || !o2.alive)
						continue;

					final Vector2D distance = space.findShortestDistance(
							o1.position, o2.position);
					o1.spaceIndex = i; o2.spaceIndex =j;
					this.distances[i][j] = distance;
					this.distances[j][i] = distance.negate();
				}
				this.distances[i][i] = Vector2D.ZERO_VECTOR;
			}
			this.timestamp = newtimestamp;
		}
	}
}