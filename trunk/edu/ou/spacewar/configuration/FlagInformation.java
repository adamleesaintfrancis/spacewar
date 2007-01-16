package edu.ou.spacewar.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: Apr 9, 2006
 * Time: 10:18:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlagInformation {
    public String name;
    public int team;
    public float[][] startpos;

    public FlagInformation(String name, int team, float[][] startpos) {
        this.name = name;
        this.team = team;
        this.startpos = startpos;
    }
}
