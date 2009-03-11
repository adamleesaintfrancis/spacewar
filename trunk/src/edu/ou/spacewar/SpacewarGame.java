package edu.ou.spacewar;

import java.lang.reflect.Array;
import java.util.*;

import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.exceptions.*;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.simulator.*;
import edu.ou.utils.Vector2D;

/**
 * This is the main class for storing the information relevant to a game of
 * Spacewar.  Instances of this class are responsible for keeping track of the
 * ships, obstacles, beacons, bullets, flags, and bases associated with a given
 * game.
 * <p/>
 * The most useful public features of this class are the methods it provides
 * for setting up a game scenario.  After a SpacewarGame is instantiated,
 * Ships, Obstacles, and Beacons can be added at specified positions, or the
 * Game can find open positions for these objects automatically.
 */
public final class SpacewarGame extends Space {
	public static final float BUFFER_DIST = 10.0f;  //minimum distance between objects.
	public static final int ATTEMPTS = 10;          //number of times to try inserting an object

	private final long seed;
	private final Random rand;
	private final float timeLimit;
	private final Collection<Team> teams;

	private final Map<Class<? extends Object2D>,
					  List<? extends Object2D>> objectlookup;

	private final PriorityQueue<QWrapper> respawnQ;

	private boolean initialized = false;

	static class QWrapper implements Comparable<QWrapper> {
		final float t; final Object2D obj;

		public QWrapper(final Object2D obj, final float timestamp) {
			this.obj = obj; t = timestamp;
		}

		public int compareTo(final QWrapper other) {
			return Float.compare(t, other.t);
		}
	}

	/**
	 * @param width The width of the space.
	 * @param height The height of the space.
	 * @param bufferinfo A map from classes to the number of instances of those
	 *                   classed that will be used in the game.
	 * @param buffertotal The total number of objects (sum of bufferinfo)
	 * @param timeLimit How long the game will run for.
	 */
	public SpacewarGame(final long seed, final float width,	final float height,
						final float timeLimit, final float timeStep)
	{
		super(width, height, timeStep);

		this.seed = seed;
		rand = new Random(seed);
		this.timeLimit = timeLimit;
		objectlookup = new HashMap<Class<? extends Object2D>,
		                                List<? extends Object2D>>();
		teams = new ArrayList<Team>();
		respawnQ = new PriorityQueue<QWrapper>();
	}

	/**
	 * Return the Random object for this game
	 */
	public Random getRandom() {
		return rand;
	}

	public Collection<Team> getTeams() {
		return teams;
	}

	public void addTeam(final Team team) {
		teams.add(team);
	}

	/**
	 * Get all instances of the specified class from the game.  This method
	 * returns the objects copied from the section of the objects array
	 * reserved for the given class, so if any slots have not yet been filled,
	 * then those slots will be null.
	 *
	 * @param klass The class of the objects to be retrieved.
	 * @return An array of objects of the requested class.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object2D> T[] getAll(final Class<T> klass)
	{
		final List<T> all = (List<T>)objectlookup.get(klass);
		if(all == null) {
			return (T[])Array.newInstance(klass, 0);
		}

		final T[] out = (T[])Array.newInstance(klass, all.size());
		return all.toArray(out);
	}

	/**
	 * Get all instances of the specified class for which isAlive() returns
	 * true.
	 *
	 * @param klass The class of the objects to be retrieved.
	 * @return An array of objects of the requested class.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object2D> T[] getLive(final Class<T> klass)
	{
		final List<T> all = (List<T>)objectlookup.get(klass);
		if(all == null) {
			return (T[])Array.newInstance(klass, 0);
		}

		final List<T> live = new ArrayList<T>(all.size());
		for(final T obj : all) {
			if(obj.isAlive()) {
				live.add(klass.cast(obj));
			}
		}

		final T[] out = (T[])Array.newInstance(klass, live.size());
		return live.toArray(out);
	}

	public <T extends Object2D> void autoAdd(final T obj)
		throws IllegalPositionException, NoOpenPositionException
	{
		obj.setPosition(findOpenPosition(obj.getRadius(), BUFFER_DIST, rand, ATTEMPTS));  //throws exception
		obj.setVelocity(Vector2D.ZERO_VECTOR);
		obj.setOrientation(Vector2D.getRandom(rand, 1));
		this.add(obj);
	}

	@SuppressWarnings("unchecked")
	public <T extends Object2D> void add(final T obj)
		throws IllegalPositionException
	{
		if (!isOpenAtPosition(obj.getPosition(), obj.getRadius(), BUFFER_DIST)) {
			throw new IllegalPositionException(obj.getPosition());
		}
		forceAdd(obj);
	}

	@SuppressWarnings("unchecked")
	public <T extends Object2D> void forceAdd(final T obj)
	{
		List<T> objlist = (List<T>)objectlookup.get(obj.getClass());
		if(objlist == null) {
			objlist = new ArrayList<T>();
			objectlookup.put(obj.getClass(), objlist);
		}
		objlist.add(obj);
		addObject(obj);

		if(obj instanceof Ship) {
			final Ship ship = (Ship)obj;
			List<Bullet> bulletlist
				= (List<Bullet>)objectlookup.get(Bullet.class);
			if(bulletlist == null) {
				bulletlist = new ArrayList<Bullet>();
				objectlookup.put(Bullet.class, bulletlist);
			}
			for(int i=0; i<Ship.MAX_BULLETS; i++) {
				final Bullet bullet = ship.getBullet(i);
				bulletlist.add(bullet);
				addObject(bullet);
			}

			List<Mine> minelist
				= (List<Mine>)objectlookup.get(Mine.class);
			if(minelist == null) {
				minelist = new ArrayList<Mine>();
				objectlookup.put(Mine.class, minelist);
			}
			for(int i=0; i<Ship.MAX_MINES; i++) {
				final Mine mine = ship.getMine(i);
				minelist.add(mine);
				addObject(mine);
			}

			List<Laser> laserlist
			= (List<Laser>)objectlookup.get(Laser.class);
			if(laserlist == null) {
				laserlist = new ArrayList<Laser>();
				objectlookup.put(Laser.class, laserlist);
			}
			for(int i=0; i<Ship.MAX_LASERS; i++) {
				final Laser laser = ship.getLaser(i);
				laserlist.add(laser);
				addObject(laser);
			}
}
	}

	public Iterable<Shadow2D> getShadowIterable() {
		return new Iterable<Shadow2D>() {
			public Iterator<Shadow2D> iterator() {
				return new Iterator<Shadow2D>() {
					private final Iterator<Object2D> baseiter
						= SpacewarGame.this.iterator();
					private Shadow2D nextshadow = null;
					public boolean hasNext() {
						if(nextshadow != null) {
							return true;
						}
						while(baseiter.hasNext() && (nextshadow == null)) {
							nextshadow = baseiter.next().getShadow();
						}
						return nextshadow != null;
					}
					public Shadow2D next() {
						if(hasNext()) {
							final Shadow2D out = nextshadow;
							nextshadow = null;
							return out;
						}
						throw new NoSuchElementException();
					}
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	//TODO: are the initialize and reset methods necessary?
	public void initialize() {
		assert(!initialized);
		rand.setSeed(seed);
		for(final Object2D obj : this) {
			if(obj != null) {
				obj.initialize();
			}
		}
		initialized = true;
	}

	public void reset() {
		this.reset(seed);
	}

	public void reset(final long seed) {
		assert(initialized);
		rand.setSeed(seed);
		for(final Object2D obj : this) {
			if(obj != null) {
				obj.reset();
			}
		}
	}

	public void resetStats() {
		for (final Object2D obj : this) {
			if (obj != null) {
				obj.resetStats();
			}
		}
	}

	public void queueForRespawn(final Object2D obj, final float delay) {
		respawnQ.offer(new QWrapper(obj, getTimestamp() + delay));
	}

	public final boolean isRunning() {
		return getTimestamp() <= timeLimit;
	}

	/**
	 * Makes sure the game is initialized before calling super.advanceTime(timestep).
	 * @param timestep
	 */
	@Override
	public final void advanceTime() {
		if(!initialized) {
			initialize();
		}
		if( isRunning() ) {
			while(!respawnQ.isEmpty() && (respawnQ.peek().t <= getTimestamp())) {
				final Object2D respawn = respawnQ.poll().obj;
				respawn.reset();
			}
			//do teams first
			for(final Team t: teams) {
				t.advanceTime(timeStep);
			}
			super.advanceTime();
		}
	}
}
