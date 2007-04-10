package edu.ou.spacewar.configuration;

public class FlagInformation {
    public String name;
    public String team;
    public float[][] startpos;

    public FlagInformation(String name, String team, float[][] startpos) {
        this.name = name;
        this.team = team;
        this.startpos = startpos;
    }
}
