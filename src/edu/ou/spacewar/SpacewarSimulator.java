package edu.ou.spacewar;

import java.io.*;
import java.util.*;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.*;
import edu.ou.spacewar.configuration.SpacewarConfig;
import edu.ou.spacewar.controllables.SWControllable;
import edu.ou.spacewar.gui.JSpacewarComponent;
import edu.ou.spacewar.objects.Team;
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


	public void initialize(final File configfile) {
		final XStream xstream = SpacewarConfig.getXStream();
        SpacewarConfig swconfig;
        try {
            final FileReader fr = new FileReader(configfile);
            swconfig = (SpacewarConfig) xstream.fromXML(fr);
            fr.close();
            this.initialize(swconfig);
        } catch (final FileNotFoundException e) {
            logger.error("Could not find Spacewar configuration file!");
            logger.error("Was looking for: " + configfile);
            e.printStackTrace();
        } catch (final IOException e) {
			e.printStackTrace();
		}
	}

    public void initialize(final SpacewarConfig swconfig)
    {
    	try {
			game = swconfig.newGame();
			controllables = extractControllables( game );
		} catch (final Exception e) {
			logger.error("Error initializing from spacewar config.", e);
		}
    }

    public static Collection<Controllable> extractControllables( final SpacewarGame game ) {
    	final Collection<Controllable> out = new LinkedList<Controllable>();
    	for(final Object2D obj : game) {
    		if (obj instanceof SWControllable) {
    			final SWControllable swc = (SWControllable)obj;
    			if( swc.isControllable() ) {
    				out.add(swc.getControllable());
    			}
    		}
    	}
    	//Teams aren't Object2Ds, so need to handle separately
    	for(final Team t : game.getTeams()) {
    		if( t.isControllable() ) {
    			out.add( t.getControllable() );
    		}
    	}
    	return out;
    }

    public boolean isRunning() {
    	return game.isRunning();
	}

    public void advance() {
		assert(game != null);
		game.advanceTime();
	}

	public State getState() {
		return new ImmutableSpacewarState(game);
	}

	public Collection<Controllable> getControllables() {
		return controllables;
	}

	public Collection<Controllable> getAllControllables() {
		return controllables;
	}

	public void shutdown() {
//		 TODO: game stats get collected here?
		controllables = extractControllables( game );
	}

	public JComponent getGUI() {
		if(gui == null) {
			final int width = (int)game.getWidth();
			final int height = (int)game.getHeight();
			gui = new JSpacewarComponent(width, height);
			gui.setSimulatorShadowSource(game.getShadowIterable());
		}
		return gui;
	}
}
