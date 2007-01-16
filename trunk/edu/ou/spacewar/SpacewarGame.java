package edu.ou.spacewar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Map.Entry;

import edu.ou.spacewar.exceptions.*;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.simulator.Object2D;
import edu.ou.spacewar.simulator.Space;
import edu.ou.spacewar.simulator.SpacewarCollisionHandler;
import edu.ou.utils.Vector2D;

/**
 * This is the main class for storing the information relevant to a game of Spacewar.  Instances of this class
 * are responsible for keeping track of the ships, obstacles, beacons, bullets, flags, and
 * bases associated with a given game.
 * <p/>
 * The most useful public features of this class are the methods it provides for setting up a game scenario.  After a
 * SpacewarGame is instantiated, Ships, Obstacles, and Beacons can be added at specified positions, or the Game can
 * find open positions for these objects automatically.
 *
 */
public final class SpacewarGame extends Space {
    public static final float BUFFER_DIST = 10.0f;  //minimum distance between objects.
    public static final int ATTEMPTS = 10;          //number of times to try inserting an object

    private final long seed;
    private final Random rand;
    private final float maxInitialSpeed;

    class Span 
    {
    	public final int start, length;
    	public Span(int start, int size) {
    		this.start = start; this.length = size;
    	}
    }
    private final Map<Class<?>, Span> buffers;

    private final PriorityQueue<QWrapper> respawnQ;

    private boolean initialized = false;

    private class QWrapper implements Comparable<QWrapper> {
        int t;
        Object2D obj;

        public QWrapper(Beacon b) {
            this.obj = b;
            this.t = timestamp + 30; //3 sec @ 10fps
        }

        public QWrapper(Ship s) {
            this.obj = s;
            this.t = timestamp + (s.getDeaths() < 5 ? s.getDeaths() * 20 : 100);
        }

        public int compareTo(QWrapper other) {
            return new Integer(this.t).compareTo(other.t);
        }
    }

    /**
     * @param width The width of the space.
     * @param height The height of the space.
     * @param ships The total number of ships in the game.
     * @param obstacles The total number of obstacles in the game.
     * @param beacons The total number of beacons in the game.
     * @param maxInitialSpeed The maximum magnitude of a starting velocity vector.
     */
    public SpacewarGame(long seed,
                        float width,
                        float height,
                        Map<Class<? extends Object2D>, Integer> bufferinfo,
                        int buffertotal, //needed for call to super()
                        float maxInitialSpeed) {
        super(width, height, new SpacewarCollisionHandler(), buffertotal);

        this.seed = seed;
        this.rand = new Random(seed);
        this.maxInitialSpeed = maxInitialSpeed;
        
        this.buffers = new HashMap<Class<?>, Span>();
        
        int i = 0;
        for(Entry<Class<? extends Object2D>, Integer> e: bufferinfo.entrySet()) {
        	Span s = new Span(i, e.getValue());
        	buffers.put(e.getKey(), s);
        	i += e.getValue();
        }

        this.respawnQ = new PriorityQueue<QWrapper>();
    }

    /**
     * Return the Random object for this game
     */
    public Random getRandom() {
        return rand;
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
	public <T extends Object2D> T[] getAll(Class<T> klass)
    {
    	T[] out;
    	if(buffers.containsKey(klass)) {
    		Span span = buffers.get(klass);
    		out = (T[])Array.newInstance(klass, span.length);
    		System.arraycopy(objects, span.start, out, 0, span.length);
    	}
    	else {
    		out = (T[])Array.newInstance(klass, 0);
    	}
    	return out;
    }
    
    /**
     * Get all instances of the specified class for which isAlive() returns
     * true.
     * 
     * @param klass The class of the objects to be retrieved.
     * @return An array of objects of the requested class.
     */
    @SuppressWarnings("unchecked")
	public <T extends Object2D> T[] getLive(Class<T> klass)
    {
    	T[] out;
    	if(buffers.containsKey(klass)) {
    		Span span = buffers.get(klass);
        	ArrayList<T> liveobjs = new ArrayList<T>(span.length);
            for(int i=span.start;i<span.start+span.length;i++) {
                if(objects[i] != null && objects[i].isAlive()) {
                    liveobjs.add((T)objects[i]);
                }
            }
            out = (T[])liveobjs.toArray();
    	}
    	else {
    		out = (T[])Array.newInstance(klass, 0);
    	}
    	return out;
    }

    /**
     * Prints the current game state.
     */
    public void print() {
        System.out.println("-----------------------------------------");
        System.out.println("Space Dimensions: " + Float.toString(getWidth())
                + "x" + Float.toString(getHeight()));
        System.out.println("Player List:");
        for (Ship ship : getAll(Ship.class)) {
            if(ship != null) {
            System.out.println(ship.getId() + ": " + ship.getName());
            System.out.println("\t Alive: " + ship.isAlive());
            System.out.println("\t Energy: " + ship.getEnergy());
            System.out.println("\t Ammo: " + ship.getAmmo());

            System.out.print("\t Command: (");
            Command command = ship.getActiveCommand();
            System.out
                    .print((command.left || command.right) ? (command.left) ? "L"
                            : "R"
                            : "0");
            System.out.print(",");
            System.out.print(command.fire ? "F" : "0");
            System.out.print(",");
            System.out.print(command.thrust ? "T" : "0");
            System.out.println(")");

            System.out.println("\t Position: ("
                    + ship.getPosition().getX() + ", "
                    + ship.getPosition().getY() + ")");
            System.out.println("\t Velocity: ("
                    + ship.getVelocity().getX() + ", "
                    + ship.getVelocity().getY() + ")");
            }
        }

        System.out.println("Bullet List:");
        for (Bullet bullet : getAll(Bullet.class)) {
            if(bullet != null) {
            System.out.println(bullet.getId() + ": belongs to " + bullet.getShip().getId());
            System.out.println("\t Alive: " + bullet.isAlive());
            System.out.println("\t Lifetime: " + bullet.getLifetime());
            System.out.println("\t Position: ("
                    + bullet.getPosition().getX() + ", "
                    + bullet.getPosition().getY() + ")");
            System.out.println("\t Velocity: ("
                    + bullet.getVelocity().getX() + ", "
                    + bullet.getVelocity().getY() + ")");
            }
        }

        System.out.println("Obstacle List:");
        for (Obstacle obst : getAll(Obstacle.class)) {
            if(obst != null) {
            System.out.println(obst.getId() + ": ");
            System.out.println("\t Alive: " + obst.isAlive());
            System.out.println("\t Position: ("
                    + obst.getPosition().getX() + ", "
                    + obst.getPosition().getY() + ")");
            System.out.println("\t Velocity: ("
                    + obst.getVelocity().getX() + ", "
                    + obst.getVelocity().getY() + ")");
            }
        }

        System.out.println("Beacon List:");
        for (Beacon bcon : getAll(Beacon.class)) {
            if(bcon != null) {
            System.out.println(bcon.getId() + ": ");
            System.out.println("\t Alive: " + bcon.isAlive());
            System.out.println("\t Position: ("
                    + bcon.getPosition().getX() + ", "
                    + bcon.getPosition().getY() + ")");
            System.out.println("\t Velocity: ("
                    + bcon.getVelocity().getX() + ", "
                    + bcon.getVelocity().getY() + ")");
            }
        }
    }

    public void autoAdd(Object2D obj)
    	throws IdCollisionException, NoOpenPositionException, 
    			NoClassBufferException, ClassBufferBoundsException  
    {
    	int objindx = findBufferIndex(obj);
    	    	
    	//default distance between objects of 10.0, 10 attempts
    	obj.setPosition(findOpenPosition(obj.getRadius(), BUFFER_DIST, rand, ATTEMPTS));  //throws exception
        obj.setVelocity(Vector2D.ZERO_VECTOR);
        obj.setOrientation(Vector2D.getRandom(rand, 1));

        objects[objindx] = obj;
        
        handleSpecialAdd(obj);
    }
    
    public void add(Object2D obj)
    	throws IdCollisionException, IllegalPositionException, 
    		IllegalVelocityException, NoClassBufferException, 
    		ClassBufferBoundsException, NoOpenPositionException
    {
    	int objindx = findBufferIndex(obj);
        if (!isOpenAtPosition(obj.getPosition(), obj.getRadius(), BUFFER_DIST)) {
            throw new IllegalPositionException(obj.getPosition());
        }
        if (obj.getVelocity().getMagnitude() > maxInitialSpeed) {
            throw new IllegalVelocityException();
        }
        objects[objindx] = obj;	
        
        handleSpecialAdd(obj);
    }
    
    private int findBufferIndex(Object2D obj) 
    	throws IdCollisionException, NoClassBufferException, 
    			ClassBufferBoundsException
    {
    	if(!buffers.containsKey(obj.getClass())) {
    		throw new NoClassBufferException();
    	}
    	Span span = buffers.get(obj.getClass());
    	int objindx = span.start + obj.getId();
    	if (objindx > span.start + span.length) {
    		throw new ClassBufferBoundsException();
    	}
        if (objects[objindx] != null) {
            throw new IdCollisionException();
        }
        return objindx;
    }
    
    private void handleSpecialAdd(Object2D obj) 
    	throws IdCollisionException, NoOpenPositionException, 
    			NoClassBufferException, ClassBufferBoundsException 
    {
    	if(obj instanceof Ship) {
    		//add the ship's bullets
    		Span span = buffers.get(Bullet.class);
            int blltindx = span.start + obj.getId() * Ship.MAX_AMMO;
            for(int i=0; i<Ship.MAX_AMMO; i++, blltindx++) {
                if(objects[blltindx] != null) {
                    throw new IdCollisionException();
                }
                objects[blltindx] = ((Ship)obj).getBullet(i);
            }
    	}
    }
    
    private void handleSpecialRemove(Object2D obj) 
    {
    	if(obj instanceof Ship) {
    		//remove the ship's bullets
    		Span span = buffers.get(Bullet.class);
    		int blltindx = span.start + obj.getId() * Ship.MAX_AMMO;
    		for(int i=0; i<Ship.MAX_AMMO; i++, blltindx++) {
    			objects[blltindx] = null;
    		}
    	}
    }
    

    /**
     * Removes the ship at the specified ship index.
     * @param index
     * @return Returns the ship that was removed.
     */
    @SuppressWarnings("unchecked")
	public <T extends Object2D> T remove(Class<T> klass, int index) 
    	throws NoClassBufferException, ClassBufferBoundsException
    {
    	if(!buffers.containsKey(klass)) {
    		throw new NoClassBufferException();
    	}
    	Span span = buffers.get(klass);
    	int objindx = span.start + index;
    	if (objindx > span.start + span.length) {
    		throw new ClassBufferBoundsException();
    	}
        T out = (T)objects[objindx];
        objects[objindx] = null;

        handleSpecialRemove(out);
        return out;
    }

    public void initialize() {
        assert(!initialized);
        timestamp = 0;
        rand.setSeed(seed);
        for(Object2D obj : objects) {
            if(obj != null) {
                obj.initialize();
            }
        }
        this.initialized = true;
    }

    public void reset() {
        reset(this.seed);
    }

    public void reset(long seed) {
        assert(initialized);
        this.timestamp = 0;
        this.rand.setSeed(seed);
        for(Object2D obj : objects) {
            if(obj != null) {
                obj.reset();
            }
        }
    }

    public void resetStats() {
        for (Object2D obj : objects) {
            if (obj != null) {
                obj.resetStats();
            }
        }
    }

    public void queue(Ship s) {
        respawnQ.offer(new QWrapper(s));
    }

    public void queue(Beacon b) {
        respawnQ.offer(new QWrapper(b));
    }

    /**
     * Makes sure the game is initialized before calling super.advanceTime(timestep).
     * @param timestep
     */
    public final void advanceTime(float timestep) {
        if(!initialized) {
            initialize();
        }

        while(!respawnQ.isEmpty() && respawnQ.peek().t <= timestamp) {
            respawnQ.poll().obj.reset();
        }
        super.advanceTime(timestep);
    }
}
