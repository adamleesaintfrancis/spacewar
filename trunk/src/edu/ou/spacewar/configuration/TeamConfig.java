package edu.ou.spacewar.configuration;

public class TeamConfig {
    public String name;
    public boolean isControllable;        // is the team controllable? 

    public TeamConfig(String name, boolean isControllable )
    {
        this.name = name;
        this.isControllable = isControllable;
    }
}
