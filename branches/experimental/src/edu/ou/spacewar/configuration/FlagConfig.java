package edu.ou.spacewar.configuration;

public class FlagConfig {
    public String name;
    public String team;
    public float[][] startpos;

    public FlagConfig(String name, String team, float[][] startpos) {
        this.name = name;
        this.team = team;
        this.startpos = startpos;
    }
}
