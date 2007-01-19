package edu.ou.spacewar.mlfw.agents;

import AIClass.spacewar.Command;
import AIClass.ai.environments.SWShipBasicEnvironment;

import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Mar 15, 2006
 * Time: 10:34:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWDoNothingAgent extends SpacewarAgent {
    public SWDoNothingAgent(SWShipBasicEnvironment env, String label, Integer team) {
        super(env, label, team);
    }

    public SWDoNothingAgent(String label, int team) {
        super(label, team);
    }

    public ShipCommand findAction() {
        return ShipCommand.DoNothing; 
    }

    public void endAction() {
        //do nothing
    }

    public void finish() {
        //do nothing
    }

    public void loadKnowledge(FileReader fr) {
        //do nothing
    }

    public void saveKnowledge(FileWriter fw) {
        //do nothing
    }
}
