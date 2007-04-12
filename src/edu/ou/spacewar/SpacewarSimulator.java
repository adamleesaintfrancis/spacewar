package edu.ou.spacewar;

import java.io.*;
import java.util.*;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.*;
import edu.ou.mlfw.gui.Shadow2D;
import edu.ou.spacewar.configuration.SpacewarConfiguration;
import edu.ou.spacewar.controllables.SWControllable;
import edu.ou.spacewar.gui.*;
import edu.ou.spacewar.objects.*;
import edu.ou.spacewar.simulator.Object2D;

/**
 * SpacewarSimulator implements the Simulator interface to allow the use 
 * of spacewar as a mlfw simulator.  
 *  
 * @author Jason
 */
public class SpacewarSimulator implements Simulator {
	private static final Logger logger = Logger.getLogger(SpacewarSimulator.class);
	private SpacewarGame game;
	private Collection<Controllable> controllables;
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
            logger.error("Could not find Spacewar configuration file!");
            logger.error("Was looking for: " + configfile);
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void initialize(SpacewarConfiguration swconfig)
    {
    	try {
			this.game = swconfig.newGame();
			this.controllables = extractControllables( this.game );
		} catch (Exception e) {
			//TODO: replace stack trace printouts with logging
			e.printStackTrace();
		}
    }
    
    public static Collection<Controllable> extractControllables( SpacewarGame game ) {
    	Collection<Controllable> out = new LinkedList<Controllable>();
    	for(Object2D obj : game.getObjects()) {
    		if (obj instanceof SWControllable) {
    			SWControllable swc = (SWControllable)obj;
    			if( swc.isControllable() ) {
    				out.add(swc.getControllable());
    			}
    		}
    	}
    	//Teams aren't Object2Ds, so need to handle separately
    	for(Team t : game.getTeams()) {
    		if( t.isControllable() ) {
    			out.add( t.getControllable() );
    		}
    	}
    	return out;
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
		return this.controllables;
	}

	public Collection<Controllable> getAllControllables() {
		return this.controllables;
	}
	
	public void shutdown() {
//		 TODO: game stats get collected here?
		this.controllables = extractControllables( this.game );		
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
