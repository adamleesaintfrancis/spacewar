package client0;

import java.awt.event.*;
import java.io.File;

import edu.ou.mlfw.*;
import edu.ou.spacewar.ImmutableSpacewarState;
import edu.ou.spacewar.controllables.ControllableShip;
import edu.ou.spacewar.objects.ShipCommand;
import edu.ou.spacewar.objects.immutables.ImmutableShip;
import edu.ou.spacewar.mlfw.clients.*;

import org.apache.log4j.*;

public class MyHumanClient extends HumanClient
{
	public static final Logger logger = Logger.getLogger(MyHumanClient.class); 
	
	private int actionCounter;
	public MyHumanClient() {
		super();
		this.actionCounter = 0;
	}
	
	public ShipCommand startAction( final ImmutableSpacewarState state, 
								    final ImmutableShip controllable ) 
	{
		actionCounter++;
		if(actionCounter % 10 == 0) {
			logger.info("MyHumanClient taking action " + actionCounter);
		}
		return super.startAction(state, controllable);
	}
}
