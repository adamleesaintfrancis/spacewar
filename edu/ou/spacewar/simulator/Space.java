package edu.ou.spacewar.simulator;

import edu.ou.spacewar.exceptions.NoOpenPositionException;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

import edu.ou.spacewar.objects.Bullet;
import edu.ou.spacewar.objects.Ship;
import edu.ou.utils.ArrayIterator;
import edu.ou.utils.Vector2D;

/**
 * This class is responsible for handling the physics of the 2D toroidal space and the
 * objects within it.  This includes collision detection, the size of the space, placing
 * Object2Ds in the space, etc.
 *
 */
public abstract class Space implements CollisionHandler, Iterable<Object2D> {
	private final float width, halfWidth, height, halfHeight;
	private final CollisionHandler collisionHandler;

	protected int timestamp = 0;

	protected final Object2D[] objects;
	protected final Vector2D[][] distances;
	protected final float[] tempDistances;

	public Space(float width,
			float height,
			CollisionHandler collisionHandler,
			int numberOfObjects)  
	{
		this.width = width;
		this.height = height;
		if (collisionHandler != null)
			this.collisionHandler = collisionHandler;
		else
			this.collisionHandler = this;
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;

		this.objects = new Object2D[numberOfObjects];
		this.distances = new Vector2D[numberOfObjects][numberOfObjects];
		this.tempDistances = new float[numberOfObjects];
	}

	public Iterator<Object2D> iterator() {
		return new ArrayIterator<Object2D>(this.objects, 0, this.objects.length);
	}

	public int getTimestamp() {
		return timestamp;
	}

	public final float getWidth() {
		return width;
	}

	public final float getHeight() {
		return height;
	}

	public final Object2D[] getLiveObjects() {
		ArrayList<Object2D> liveobjs = new ArrayList<Object2D>(objects.length);
		for(Object2D obj : objects) {
			if(obj != null && obj.isAlive()) {
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
	 * @param a the start point
	 * @param b the end point
	 * @return the shortest vector that starts at point a and ends at point b
	 */
	 public Vector2D findShortestDistance(Vector2D a, Vector2D b) {
		 return findShortestDistance(a, b, this.width, this.height, this.halfWidth, this.halfHeight);
	 }

	 public Vector2D findShortestDistance(Object2D a, Object2D b) {
//		 if(distancesSaved) {
//		 if(a.index < b.index) {
//		 return distances[a.index][b.index];
//		 } else {
//		 return distances[a.index][b.index].negate();
//		 }
//		 } else {
		 return findShortestDistance(a.getPosition(), b.getPosition());
//		 }
	 }




	 public Vector2D findOpenPosition(float radius, float buffer,
			 Random rand, int attempts) throws NoOpenPositionException {
		 for (int i = 0; i < attempts; i++) {
			 //initialize the iteration with a new candidate position...
			 float x = rand.nextFloat() * width;
			 float y = rand.nextFloat() * height;
			 Vector2D position = new Vector2D(x, y);

			 if (isOpenAtPosition(position, radius, buffer)) {
				 return position;
			 }
		 }
		 throw new NoOpenPositionException();
	 }

	 public boolean isOpenAtPosition(Vector2D position, float radius, float buffer) {
		 for (Object2D o : objects) {
			 if(o != null && o.isAlive()) {
				 Vector2D dist = findShortestDistance(position, o.position);
				 if (dist.getMagnitude() < radius + buffer + o.getRadius()) {
					 return false;
				 }
			 }
		 }

		 return true;
	 }

	 public void advanceTime(float increment) {
//		 for (int i = 0; i < this.objectCount; i++) {
//		 Object2D object = this.objects[i];
//		 if (!object.alive) {
//		 removeObject(i);
//		 i--;
//		 }
//		 }

		 Vector2D distance;
		 Vector2D velocity;
		 float advanceTo;
		 Object2D o1;
		 Object2D o2;
		 Object2D object1 = null;
		 Object2D object2 = null;
//		 int object1i = -1;
//		 int object2i = -1;

		 //let all the objects update themselves
		 for (Object2D o : objects) {
			 if(o != null && o.alive) {
				 o.advanceTime(increment);
			 }

		 }

//		 if (!this.distancesSaved) {
//		 findDistances();
//		 this.distancesSaved = true;
//		 }



		 //find and update with collisions
		 while (increment >= 0) {
			 findDistances();
			 advanceTo = increment;
			 object1 = null;

			 for (int i = 0; i < objects.length; i++) {
				 o1 = this.objects[i];
				 if (o1 == null || !o1.alive)
					 continue;

				 for (int j = i + 1; j < objects.length; j++) {
					 o2 = this.objects[j];
					 if (o2 == null || !o2.alive)
						 continue;

					 distance = this.distances[i][j];
					 velocity = o1.velocity.subtract(o2.velocity);

					 float ddotv = distance.dot(velocity);

					 if (ddotv < 0)
						 continue;

					 float vdotv = velocity.dot(velocity);
					 float determinate = o1.radius + o2.radius;
					 determinate = distance.dot(distance) - determinate * determinate;
					 determinate = ddotv * ddotv - vdotv * determinate;

					 if (determinate <= 0)
						 continue;

					 float time = (ddotv - (float) Math.sqrt(determinate)) / vdotv;

					 if (time <= advanceTo && time >= 0) {
						 advanceTo = time;
						 object1 = o1;
						 object2 = o2;
//						 object1i = i;
//						 object2i = j;

						 if (advanceTo == 0)
							 break;
					 }
				 }

				 if (advanceTo == 0 && object1 != null)
					 break;
			 }

			 for (int i = 0; i < objects.length; i++) {
				 o1 = this.objects[i];

				 if(o1 == null || !o1.alive) {
					 continue;
				 }

				 o1.position = o1.position.add(o1.velocity.multiply(advanceTo));

				 // fix positions after potential wrap around
				 while (o1.position.getX() < 0)
					 o1.position = new Vector2D(o1.position.getX() + width, o1.position.getY());
				 while (o1.position.getY() < 0)
					 o1.position = new Vector2D(o1.position.getX(), o1.position.getY() + height);
				 while (o1.position.getX() >= width)
					 o1.position = new Vector2D(o1.position.getX() - width, o1.position.getY());
				 while (o1.position.getY() >= height)
					 o1.position = new Vector2D(o1.position.getX(), o1.position.getY() - height);
			 }

			 if (object1 != null) {
				 distance = this.findShortestDistance(object1.getPosition(), object2.getPosition());
				 this.collisionHandler.handleCollision(distance.unit(), object1, object2);
			 }

			 increment -= advanceTo;

			 if (increment == 0 && object1 == null)
				 increment = -1;
		 }

		 timestamp++;
	 }

	 private void findDistances() {
		 for (int i = 0; i < objects.length; i++) {
			 Object2D o1 = this.objects[i];
			 if (o1 == null || !o1.alive)
				 continue;

			 for (int j = i + 1; j < objects.length; j++) {
				 Object2D o2 = this.objects[j];
				 if (o2 == null || !o2.alive)
					 continue;

				 Vector2D distance = findShortestDistance(o1.position, o2.position);
				 this.distances[i][j] = distance;
				 this.distances[j][i] = distance;
			 }
		 }
	 }

	 /**
	  * Finds the nearest object of the given type.  Bullets fired from the
	  * given ship are not considered.
	  *
	  * @param <T> The type to look for.
	  * @param ship The ship to find obstacles near.
	  * @param objectClass The class of object to find.
	  * @return The closest object of the given type.
	  */
	 public <T> T findNearestObject(Ship ship, Class<T> objectClass)
	 {
//		 if (!this.distancesSaved) {
//		 findDistances();
//		 this.distancesSaved = true;
//		 }
		 findDistances();

		 int index = ship.index;

		 int minIndex = -1;
		 float minSquaredDistance = Float.MAX_VALUE;
		 float squaredDistance;

		 for (int i = 0; i < this.objects.length; i++) {
			 if (index == i)
				 continue;

			 Object2D object = this.objects[i];

			 if (!object.alive || !objectClass.isInstance(object))
				 continue;

			 if (object instanceof Bullet) {
				 if (((Bullet) object).getShip() == ship)
					 continue;
			 }

			 squaredDistance = this.distances[index][i].dot(this.distances[index][i]);
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
	  * Finds the nearest objects of the given type.  Bullets fired from the
	  * given ship are not considered.  Objects are returned in sorted order
	  * from closest to farthest.
	  *
	  * @param <T> The type to look for.
	  * @param ship The ship to find objects near.
	  * @param count The number of objects to find.
	  * @param objectClass The class of object to find.
	  * @return The n closest object of the given type.  If there are not enough
	  *         objects, then the array will be filled with trailing nulls.
	  */
	 public <T> T[] findNearestObjects(Ship ship, int count, Class<T> objectClass)
	 {
//		 if (!this.distancesSaved) {
//		 findDistances();
//		 this.distancesSaved = true;
//		 }
		 findDistances();

		 int index = ship.index;

		 T[] array = createArray(objectClass, count);

		 for (int i = 0; i < this.objects.length; i++) {
			 if (index == i) {
				 this.tempDistances[i] = 0;
				 continue;
			 }

			 Object2D object = this.objects[i];

			 if (!object.alive || !objectClass.isInstance(object)) {
				 this.tempDistances[i] = 0;
				 continue;
			 }

			 if (object instanceof Bullet) {
				 if (((Bullet) object).getShip() == ship) {
					 this.tempDistances[i] = 0;
					 continue;
				 }
			 }

			 this.tempDistances[i] = this.distances[index][i].dot(this.distances[index][i]);
		 }

		 int minIndex;
		 float minDistance;

		 for (int i = 0; i < count; i++) {
			 minIndex = -1;
			 minDistance = Float.MAX_VALUE;

			 for (int j = 0; j < this.objects.length; j++) {
				 if (this.tempDistances[j] != 0 && this.tempDistances[j] < minDistance) {
					 minIndex = j;
					 minDistance = this.tempDistances[j];
				 }
			 }

			 if (minIndex != -1) {
				 this.tempDistances[minIndex] = 0;
				 array[i] = objectClass.cast(this.objects[minIndex]);
			 }
			 else {
				 for (; i < count; i++)
					 array[i] = null;
			 }
		 }

		 return array;
	 }

	 @SuppressWarnings("unchecked")
	 private static <T> T[] createArray(Class<T> objectClass, int length) {
		 return (T[])Array.newInstance(objectClass, length);
	 }

	 public void handleCollision(Vector2D normal, Object2D object1, Object2D object2) {
		 Space.collide(1, normal, object1, object2);
	 }

	 public static void collide(float coefficientOfRestitution, Vector2D normal, Object2D object1, Object2D object2) {
		 float o1v = normal.dot(object1.velocity);
		 float o2v = normal.dot(object2.velocity);

		 Vector2D o1vx = normal.multiply(o1v);
		 Vector2D o2vx = normal.multiply(o2v);

		 Vector2D o1vxf = o2vx.multiply((1 + coefficientOfRestitution) * object2.mass).add(
				 o1vx.multiply(object1.mass - coefficientOfRestitution * object2.mass)).divide(
						 object1.mass + object2.mass);
		 Vector2D o2vxf = o1vxf.add(o1vx.multiply(coefficientOfRestitution)).subtract(
				 o2vx.multiply(coefficientOfRestitution));

		 Vector2D o1vy = object1.velocity.subtract(o1vx);
		 Vector2D o2vy = object2.velocity.subtract(o2vx);

		 Vector2D o1vyf = o1vy.multiply(object1.mass).add(o2vy.multiply((1 - coefficientOfRestitution) * object2.mass))
		 .divide(object1.mass + (1 - coefficientOfRestitution) * object2.mass);
		 Vector2D o2vyf = o2vy.multiply(object2.mass).add(o1vy.multiply((1 - coefficientOfRestitution) * object1.mass))
		 .divide(object2.mass + (1 - coefficientOfRestitution) * object1.mass);

		 object1.velocity = o1vyf.add(o1vxf);
		 object2.velocity = o2vyf.add(o2vxf);
	 }

	 /**
	  * A convenience method for finding a vector pointing from position A to position B
	  * along the shortest path in a toroidal environment with the given dimensions.  This
	  * assumes that the input positions are vectors pointing from the origin of the
	  * toroidal space in question.
	  *
	  * @param a      The start position.  Should not be null.
	  * @param b      The end position.  Should not be null.
	  * @param width  The 'width' of the toroidal space, should be > 0.
	  * @param height The 'height' of the toroidal space, should be > 0.
	  * @return A vector pointing from position A to position B along the shortest path
	  *         in a toroidal environment with the given dimensions.
	  */
	 public static Vector2D findShortestDistance(Vector2D a, Vector2D b, float width, float height) {
		 return findShortestDistance(a, b, width, height, width/2.0f, height/2.0f);
	 }
	 public static Vector2D findShortestDistance(final Vector2D a, final Vector2D b, 
			 final float width, final float height,
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


}