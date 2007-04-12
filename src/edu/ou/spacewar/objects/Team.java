package edu.ou.spacewar.objects;

import java.util.*;

import org.apache.log4j.Logger;

import edu.ou.mlfw.*;
import edu.ou.spacewar.controllables.*;

/**
 * Teams aren't really objects, but are instead collections of ships, bases,
 * and flags. 
 */
public class Team implements SWControllable {
	private static final Logger logger = Logger.getLogger(Team.class);
	private final Collection<Ship> ships;
	private final String name;
	private final boolean isControllable;
		
	private Base base;
	private Flag flag;
	
	private ControllableTeam controllable;

	public Team(String name, boolean isControllable) {
		this.ships = new ArrayList<Ship>();
		this.name = name;
		this.isControllable = isControllable;
	}
	
	public String getName() {
		return this.name;
	}

	public void addShip( Ship s ) {
		this.ships.add(s);
	}
	
	public Base getBase() {
		return this.base;
	}
	
	public void setBase( Base b ) {
		this.base = b;
	}
	
	public Flag getFlag() {
		return this.flag;
	}
	
	public void setFlag( Flag f ) {
		this.flag = f;
	}
	
	public void advanceTime(float timestep) {
		logger.debug(this.getName());
    	if(this.controllable != null) {
    		Action a = controllable.getAction();
    		if(a instanceof TeamAction) {
    			TeamAction ta = (TeamAction) a;
    			TeamState ts = (TeamState) this.controllable.getState();
    			for(Map.Entry<String, ControllableShip> e: ts.getShips().entrySet()) {
    				e.getValue().setAction(ta.getCommand(e.getKey()));
    			}
    		}
    	}
	}
	
	public Controllable getControllable() {
		Collection<ControllableShip> cs 
			= new ArrayList<ControllableShip>(this.ships.size()); 		
		for(Ship s: this.ships) {
			cs.add((ControllableShip)(s.getControllable()));
		}
		
		TeamRecord stats = new TeamRecord(this.getName());
		for(ControllableShip s: cs){
			logger.debug(s.getRecord().toString());
			stats.addShip((ShipRecord)s.getRecord());
		}
		
		this.controllable = new ControllableTeam(this.getName(), 
												 new TeamState(cs), 
												 stats);
		return this.controllable;
	}

	public boolean isControllable() {
		return this.isControllable;
	}
	
}
