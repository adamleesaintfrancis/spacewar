package edu.ou.spacewar.mlfw.agents;

import AIClass.ai.environments.SWShipBasicEnvironment;
import AIClass.spacewar.Command;

import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Mar 5, 2006
 * Time: 1:57:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWRandomAgent extends SpacewarAgent {
    public SWRandomAgent(SWShipBasicEnvironment env, String label, Integer team) {
        super(env, label, team);
    }

    public Command findAction() {
        Command[] actions = getActions();
        if (actions.length == 0)
            return null;

        int randomIndex = getRandom().nextInt(actions.length);
        return actions[randomIndex];
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
