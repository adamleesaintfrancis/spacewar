package edu.ou.spacewar;

import java.io.*;
import java.util.*;

import javax.swing.JComponent;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.*;
import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.configuration.SpacewarConfiguration;
import edu.ou.spacewar.gui.*;
import edu.ou.spacewar.objects.Ship;

/**
 * SpacewarSimulator implements the Simulator interface to allow the use 
 * of spacewar as a mlfw simulator.  
 *  
 * @author Jason
 */
public class SpacewarSimulator implements Simulator {
	private SpacewarGame game;
	private JSpacewarComponent gui = null;

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
	
    public void advance(float secs) {
		assert(this.game != null);
		this.game.advanceTime(secs);  
	}
	
	public State getState() {
		return new ImmutableSpacewarState(this.game);
	}
	
	public Collection<Controllable> getControllables() {
		//only ships are controllable
		Ship[] ships = this.game.getAll(Ship.class);
		Collection<Controllable> out 
			= new ArrayList<Controllable>(ships.length);		
		for(Ship s : ships) {
			if(s.isControllable()) {
				out.add(s.getControllableShip());
			}
		}
		return out;
	}

	public Collection<Controllable> getAllControllables() {
		//only ships are controllable
		Ship[] ships = this.game.getAll(Ship.class);
		Collection<Controllable> out 
			= new ArrayList<Controllable>(ships.length);		
		for(Ship s : ships) {
			if(s.isControllable()) {
				out.add(s.getControllableShip());
			}
		}
		return out;
	}
	
	public void shutdown() {
		// TODO: game stats get collected here?
	}

	public JComponent getGUI() {
		if(this.gui == null) {
			int width = (int)this.game.getWidth();
			int height = (int)this.game.getHeight();
			Collection<Shadow2D> shadows = game.getShadows(); 
			this.gui = new JSpacewarComponent();
			this.gui.initialize(width, height, shadows);
		} 
		return this.gui;
	}
}
