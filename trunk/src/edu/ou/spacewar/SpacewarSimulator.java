package edu.ou.spacewar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.Simulator;
import edu.ou.mlfw.SimulatorState;
import edu.ou.spacewar.configuration.SpacewarConfiguration;
import edu.ou.spacewar.exceptions.ClassBufferBoundsException;
import edu.ou.spacewar.exceptions.IdCollisionException;
import edu.ou.spacewar.exceptions.IllegalPositionException;
import edu.ou.spacewar.exceptions.IllegalVelocityException;
import edu.ou.spacewar.exceptions.NoClassBufferException;
import edu.ou.spacewar.exceptions.NoOpenPositionException;
import edu.ou.spacewar.objects.Ship;

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
		} catch (IdCollisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalPositionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalVelocityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoClassBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassBufferBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoOpenPositionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean isRunning() {
		// TODO Auto-generated method stub
    	return false;
	}
	
    public void advance() {
		assert(this.game != null);
		this.game.advanceTime(100.0f);
	}
	
	public SimulatorState getState() {
//		 TODO Auto-generated method stub
		return null;
	}
	
	public Collection<Controllable> getControllables() {
		//for now, only ships are controllable
		Ship[] ships = this.game.getLive(Ship.class);
		Collection<Controllable> out = new ArrayList<Controllable>(ships.length);		
		for(Ship s : ships) {
			if(s.isControllable()) {
				out.add(s.getControllableShip());
			}
		}
		return out;
	}

	public void shutdown(OutputStream config) {
		// TODO Auto-generated method stub
		
	}
}
