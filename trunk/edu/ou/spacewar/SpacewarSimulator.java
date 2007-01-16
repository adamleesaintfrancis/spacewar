package edu.ou.spacewar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.Collection;

import com.thoughtworks.xstream.XStream;

import edu.ou.mlfw.Controllable;
import edu.ou.mlfw.Simulator;
import edu.ou.spacewar.configuration.BaseInformation;
import edu.ou.spacewar.configuration.BeaconInformation;
import edu.ou.spacewar.configuration.FlagInformation;
import edu.ou.spacewar.configuration.ObstacleInformation;
import edu.ou.spacewar.configuration.ShipInformation;
import edu.ou.spacewar.configuration.SpacewarConfiguration;
import edu.ou.spacewar.configuration.TeamInformation;

public class SpacewarSimulator implements Simulator {
	private final SpacewarGame game;

	public Collection<Controllable> getControllables() {
		// TODO Auto-generated method stub
		return null;
	}

	public void shutdown(OutputStream config) {
		// TODO Auto-generated method stub
		
	}

	public void initialize(File configfile) {
		XStream xstream = new XStream();
        xstream.alias("SpacewarConfiguration", SpacewarConfiguration.class);
        xstream.alias("ShipInformation", ShipInformation.class);
        xstream.alias("ObstacleInformation", ObstacleInformation.class);
        xstream.alias("BeaconInformation", BeaconInformation.class);
        xstream.alias("FlagInformation", FlagInformation.class);
        xstream.alias("BaseInformation", BaseInformation.class);
        xstream.alias("TeamInformation", TeamInformation.class);
        
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
        }
	}

    private void initialize(SpacewarConfiguration swconfig)
    {
    	SpacewarGame swgame = swconfig.newGame();
    }
}
