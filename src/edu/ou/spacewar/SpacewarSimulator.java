package edu.ou.spacewar;

import java.io.*;
import java.util.*;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.*;
import edu.ou.spacewar.configuration.SpacewarConfiguration;
import edu.ou.spacewar.objects.Ship;

/**
 * SpacewarSimulator implements the Simulator interface to allow the use 
 * of spacewar as a mlfw simulator.  
 *  
 * @author Jason
 */
public class SpacewarSimulator implements Simulator {
	private SpacewarGame game;

	public void initialize(File configfile) {
		XStream xstream = SpacewarConfiguration.getXStream();
        SpacewarConfiguration swconfig;
        try {
            FileReader fr = new FileReader(configfile);
            swconfig = (SpacewarConfiguration) xstream.fromXML(fr);
            fr.close();
            initialize(swconfig);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find Spacewar configuration file!");
            System.out.println("Was looking for: " + configfile);
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void initialize(SpacewarConfiguration swconfig)
    {
    	try {
			this.game = swconfig.newGame();
		} catch (Exception e) {
			//TODO: replace stack trace printouts with logging
			e.printStackTrace();
		}
    }
    
    public boolean isRunning() {
    	return this.game.isRunning();
	}
	
    public void advance() {
		assert(this.game != null);
		this.game.advanceTime(0.1f);  //TODO: find right advanceTime value
	}
	
	public State getState() {
		return new ImmutableSpacewarState(this.game);
	}
	
	public Collection<Controllable> getControllables() {
		//only ships are controllable
		Ship[] ships = this.game.getLive(Ship.class);
		Collection<Controllable> out 
			= new ArrayList<Controllable>(ships.length);		
		for(Ship s : ships) {
			if(s.isControllable()) {
				out.add(s.getControllableShip());
			}
		}
		return out;
	}

	public void shutdown(OutputStream config) {
		// TODO: game stats get collected here?
		// TODO: rethink this interface: why would shutdown need to write back to config? 
		
	}
}
