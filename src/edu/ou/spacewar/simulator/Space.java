package edu.ou.spacewar.simulator;

import java.lang.reflect.Array;
import java.util.*;

import edu.ou.spacewar.exceptions.NoOpenPositionException;
import edu.ou.spacewar.simulator.Space.DistanceCache.StaleCacheException;
import edu.ou.utils.Vector2D;

/**
 * This class is responsible for handling the physics of the 2D toroidal space
 * and the objects within it. This includes collision detection, the size of
 * the space, placing Object2Ds in the space, etc.
 */
public abstract class Space
	implements CollisionHandler, Iterable<Object2D>
{
	//The width and height of the environment.  halfWidth and
	//halfHeight are stored because they are used very frequently.
	private final float width, halfWidth, height, halfHeight;

	//Callbacks for when collisions are detected.
	private final CollisionHandler collisionHandler;

	//Distances between objects are cached for faster lookup.
	private final DistanceCache distanceCache;

	//The main object storage array. This is marked protected because we want
	//to farm out to a subclass the responsibility for handling the details of
	//storing and accessing different kinds of objects.
	private final List<Object2D> objects;

	//Queue for storing Objects that should be added to the object list.
	//This is used to allow any consumer class to add an object at any time,
	//without having to worry about concurrent modification exceptions thrown
	//based on Space's lifecycle.
	private final Queue<Object2D> objectsToAdd;

	//How many seconds elapse each simulation step.
	protected final float timeStep;

	//Track the elapsed simulation time in seconds.
	private float timestamp = 0.0f;

	//Track how many times advanceTime() has been called.
	private int stepcount = 0;

	public Space(final float width, final float height, final float timeStep)
	{
		this(width, height, timeStep, null);
	}

	/**
	 * Create a new Space object.
	 *
	 * @param width The width of the environment (unitless)
	 * @param height The height of the environment (unitless)
	 * @param timeStep The number of seconds for each step of the simulator.
	 * @param collisionHandler Callbacks for when collisions are detected.
	 * @param numberOfObjects The maximum number of objects in the environment.
	 */
	public Space(final float width,
				 final float height,
				 final float timeStep,
				 final CollisionHandler collisionHandler)
	{
		this.width = width;
		this.height = height;
		this.timeStep = timeStep;
		if (collisionHandler != null) {
			this.collisionHandler = collisionHandler;
		} else {
			this.collisionHandler = this;
		}
		halfWidth = width / 2;
		halfHeight = height / 2;

		objects = new ArrayList<Object2D>();
		objectsToAdd = new LinkedList<Object2D>();
		distanceCache = new DistanceCache();
	}

	public Iterator<Object2D> iterator() {
		consumeAddedObjects();
		return objects.iterator();
	}

	public float getTimestamp() {
		return timestamp;
	}

	public int getStepCount() {
		return stepcount;
	}

	public final float getWidth() {
		return width;
	}

	public final float getHeight() {
		return height;
	}

	private void consumeAddedObjects() {
		objects.addAll(objectsToAdd);
		objectsToAdd.clear();
	}

	public void addObject(final Object2D obj) {
		objectsToAdd.add(obj);
	}

	public final Object2D[] getLiveObjects() {
		final ArrayList<Object2D> liveobjs
			= new ArrayList<Object2D>(objects.size());
		for (final Object2D obj : objects) {
			if ((obj != null) && obj.isAlive()) {
				liveobjs.add(obj);
			}
		}
		final Object2D[] out = new Object2D[liveobjs.size()];
		return liveobjs.toArray(out);
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
	public Vector2D findShortestDistance(final Vector2D a, final Vector2D b) {
		return findShortestDistance(a, b, width, height,
				halfWidth, halfHeight);
	}

	public Vector2D findShortestDistance(final Object2D a, final Object2D b) {
		try {
			return distanceCache.getDistance(timestamp, a, b);
		} catch (final StaleCacheException e) {
			return this.findShortestDistance(a.getPosition(), b.getPosition());
		}
	}

	public Vector2D findOpenPosition(final float radius, final float buffer,
										final Random rand, final int attempts)
		throws NoOpenPositionException
	{
		for (int i = 0; i < attempts; i++) {
			// initialize the iteration with a new candidate position...
			final float x = rand.nextFloat() * width;
			final float y = rand.nextFloat() * height;
			final Vector2D position = new Vector2D(x, y);

			if (isOpenAtPosition(position, radius, buffer)) {
				return position;
			}
		}
		throw new NoOpenPositionException();
	}

	public boolean isOpenAtPosition(final Vector2D position,
									final float radius,
									final float buffer)
	{
		return getObjectAtPosition(position, radius, buffer) == null;
	}

	public Object2D getObjectAtPosition(final Vector2D position,
										final float radius,
										final float buffer)
	{
		for (final Object2D o : objects) {
			if ((o != null) && o.isAlive()) {
				final Vector2D dist
					= this.findShortestDistance(position, o.getPosition());
				if (dist.getMagnitude() < radius + buffer + o.getRadius()) {
					return o;
				}
			}
		}

		return null;
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
	public void advanceTime() {
		timestamp += timeStep;
		stepcount++;

		// let all the objects update themselves
		consumeAddedObjects();
		for (final Object2D o : objects) {
			if ((o != null) && o.alive) {
				//we hope that nobody updates themselves badly (i.e. setting
				//their own position so they intersect with another object)
				o.advanceTime(timeStep);
			}
		}
		consumeAddedObjects();

		float tempTimeStep = timeStep;
		// find and update with collisions
		while (tempTimeStep >= 0.0f) {
			distanceCache.forceUpdate(tempTimeStep);
			float advanceTo = tempTimeStep;
			Object2D object1 = null;
			Object2D object2 = null;

			for (int i = 0; i < objects.size(); i++) {
				final Object2D o1 = objects.get(i);
				if ((o1 == null) || !o1.alive) {
					continue;
				}

				for (int j = i + 1; j < objects.size(); j++) {
					final Object2D o2 = objects.get(j);
					if ((o2 == null) || !o2.alive) {
						continue;
					}

					final Vector2D distance = distanceCache.fastGetDistance(i, j);
					final Vector2D velocity = o1.getVelocity().subtract(o2.getVelocity());

					final float ddotv = distance.dot(velocity);

					if (ddotv < 0) {
						continue;
					}

					final float vdotv = velocity.dot(velocity);
					float determinate = o1.getRadius() + o2.getRadius();
					determinate = distance.dot(distance) - determinate
							* determinate;
					determinate = ddotv * ddotv - vdotv * determinate;

					if (determinate <= 0) {
						continue;
					}

					final float time = (ddotv - (float) Math.sqrt(determinate))
							/ vdotv;

					if ((time <= advanceTo) && (time >= 0)) {
						advanceTo = time;
						object1 = o1;
						object2 = o2;
						// object1i = i;
						// object2i = j;

						if (advanceTo == 0) {
							break;
						}
					}
				}

				if ((advanceTo == 0) && (object1 != null)) {
					break;
				}
			}

			for (int i = 0; i < objects.size(); i++) {
				final Object2D o1 = objects.get(i);

				if ((o1 == null) || !o1.alive) {
					continue;
				}

				o1.setPosition(o1.getPosition().add(o1.getVelocity().multiply(advanceTo)));

				// fix positions after potential wrap around
				while (o1.getPosition().getX() < 0) {
					o1.setPosition( new Vector2D(o1.getPosition().getX() + width,
							o1.getPosition().getY()));
				}
				while (o1.getPosition().getY() < 0) {
					o1.setPosition( new Vector2D(o1.getPosition().getX(), o1.getPosition()
							.getY()
							+ height));
				}
				while (o1.getPosition().getX() >= width) {
					o1.setPosition( new Vector2D(o1.getPosition().getX() - width,
							o1.getPosition().getY()));
				}
				while (o1.getPosition().getY() >= height) {
					o1.setPosition( new Vector2D(o1.getPosition().getX(), o1.getPosition()
							.getY()
							- height));
				}
			}

			if (object1 != null) {
				final Vector2D distance = this.findShortestDistance(object1.getPosition(),
						object2.getPosition());
				collisionHandler.handleCollision(distance.unit(), object1,
						object2);
			}

			tempTimeStep -= advanceTo;

			if ((tempTimeStep == 0) && (object1 == null)) {
				tempTimeStep = -1;
			}
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
	public <T extends Object2D> T findNearestObject(final Object2D obj,
													final Class<T> objectClass)
	{
		distanceCache.update(timestamp);

		final int index = obj.spaceIndex;

		int minIndex = -1;
		float minSquaredDistance = Float.MAX_VALUE;
		float squaredDistance;

		for (int i = 0; i < objects.size(); i++) {
			if (index == i) {
				continue;
			}

			final Object2D object = objects.get(i);

			if (!object.alive || !objectClass.isInstance(object)) {
				continue;
			}

			final Vector2D dist = distanceCache.fastGetDistance(index, i);
			squaredDistance = dist.dot(dist);
			if (squaredDistance < minSquaredDistance) {
				minIndex = i;
				minSquaredDistance = squaredDistance;
			}
		}

		if (minIndex == -1) {
			return null;
		}

		return objectClass.cast(objects.get(minIndex));
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
	public <T extends Object2D> T[] findNearestObjects(final Object2D obj,
														final int count,
														final Class<T> objectClass)
	{
		distanceCache.update(timestamp);
		final int index = obj.spaceIndex;
		final T[] array = createArray(objectClass, count);
		final float[] tempDistances = new float[objects.size()];

		for (int i = 0; i < objects.size(); i++) {
			if (index == i) {
				tempDistances[i] = 0;
				continue;
			}

			final Object2D object = objects.get(i);

			if (!object.alive || !objectClass.isInstance(object)) {
				tempDistances[i] = 0;
				continue;
			}

			final Vector2D dist = distanceCache.fastGetDistance(index, i);
			tempDistances[i] = dist.dot(dist);
		}

		for (int i = 0; i < count; i++) {
			int minIndex = -1;
			float minDistance = Float.MAX_VALUE;

			for (int j = 0; j < objects.size(); j++) {
				if ((tempDistances[j] != 0)
						&& (tempDistances[j] < minDistance)) {
					minIndex = j;
					minDistance = tempDistances[j];
				}
			}

			if (minIndex != -1) {
				tempDistances[minIndex] = 0;
				array[i] = objectClass.cast(objects.get(minIndex));
			} else {
				for (; i < count; i++) {
					array[i] = null;
				}
			}
		}

		return array;
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] createArray(final Class<T> objectClass, final int length) {
		return (T[]) Array.newInstance(objectClass, length);
	}

	public void handleCollision(final Vector2D normal,
								final Object2D object1, final Object2D object2)
	{
		object1.dispatch(normal, object2);
	}

	public static void collide( final float coeffRestitution,
								final Vector2D normal,
								final Object2D obj1,
								final Object2D obj2)
	{
		final float o1v = normal.dot(obj1.getVelocity());
		final float o2v = normal.dot(obj2.getVelocity());

		final Vector2D o1vx = normal.multiply(o1v);
		final Vector2D o2vx = normal.multiply(o2v);

		final Vector2D o1vxf
			= o2vx.multiply((1 + coeffRestitution) * obj2.getMass())
			      .add(o1vx.multiply(  obj1.getMass()
								     - coeffRestitution * obj2.getMass()))
				  .divide(obj1.getMass() + obj2.getMass());
		final Vector2D o2vxf
			= o1vxf.add(o1vx.multiply(coeffRestitution))
				   .subtract(o2vx.multiply(coeffRestitution));

		final Vector2D o1vy = obj1.getVelocity().subtract(o1vx);
		final Vector2D o2vy = obj2.getVelocity().subtract(o2vx);

		final Vector2D o1vyf
			= o1vy.multiply(obj1.getMass())
				  .add(o2vy.multiply((1 - coeffRestitution) * obj2.getMass()))
				  .divide(  obj1.getMass()
						  + (1 - coeffRestitution) * obj2.getMass());
		final Vector2D o2vyf
			= o2vy.multiply(obj2.getMass())
			      .add(o1vy.multiply((1 - coeffRestitution) * obj1.getMass()))
				  .divide(  obj2.getMass()
						  + (1 - coeffRestitution) * obj1.getMass());

		obj1.setVelocity(o1vyf.add(o1vxf));
		obj2.setVelocity(o2vyf.add(o2vxf));
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
	public static Vector2D findShortestDistance(final Vector2D a, final Vector2D b,
			final float width, final float height) {
		return findShortestDistance(a, b, width, height, width / 2.0f,
				height / 2.0f);
	}

	public static Vector2D findShortestDistance(final Vector2D a,
			final Vector2D b, final float width, final float height,
			final float halfWidth, final float halfHeight) {
		final float x = b.getX() - a.getX();
		final float y = b.getY() - a.getY();
		if (x > halfWidth) {
			if (y > halfHeight) {
				return new Vector2D(x - width, y - height);
			} else if (y < -halfHeight) {
				return new Vector2D(x - width, y + height);
			} else {
				return new Vector2D(x - width, y);
			}
		} else if (x < -halfWidth) {
			if (y > halfHeight) {
				return new Vector2D(x + width, y - height);
			} else if (y < -halfHeight) {
				return new Vector2D(x + width, y + height);
			} else {
				return new Vector2D(x + width, y);
			}
		} else if (y > halfHeight) {
			return new Vector2D(x, y - height);
		} else if (y < -halfHeight) {
			return new Vector2D(x, y + height);
		} else {
			return new Vector2D(x, y);
		}
	}

	class DistanceCache {
		class StaleCacheException extends Exception {
			private static final long serialVersionUID = 1L;
		}

		private Vector2D[][] distances = new Vector2D[0][0];
		private float timestamp;

		final Vector2D getDistance(final float timestamp,
				                   final Object2D a, final Object2D b)
			throws StaleCacheException
		{
			if (timestamp != this.timestamp) {
				throw new StaleCacheException();
			}
			return distances[a.spaceIndex][b.spaceIndex];
		}

		final Vector2D fastGetDistance(final int a, final int b) {
			return distances[a][b];
		}

		final void update(final float newtimestamp) {
			if(timestamp != newtimestamp) {
				forceUpdate(newtimestamp);
			}
		}

		final void forceUpdate(final float newtimestamp) {
			final int size = objects.size();
			if(size != distances.length) {
				distances = new Vector2D[size][size];
			}
			for (int i = 0; i < size; i++) {
				final Object2D o1 = objects.get(i);
				if ((o1 == null) || !o1.alive) {
					continue;
				}

				for (int j = i + 1; j < size; j++) {
					final Object2D o2 = objects.get(j);
					if ((o2 == null) || !o2.alive) {
						continue;
					}

					final Vector2D distance
						= Space.this.findShortestDistance(o1.getPosition(), o2.getPosition());
					o1.spaceIndex = i; o2.spaceIndex =j;
					distances[i][j] = distance;
					distances[j][i] = distance.negate();
				}
				distances[i][i] = Vector2D.ZERO_VECTOR;
			}
			timestamp = newtimestamp;
		}
	}
}