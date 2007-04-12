package edu.ou.spacewar.configuration;

public class TeamInformation {
    public String name;
    public boolean isControllable;        // is the team controllable? 

    public TeamInformation(String name, boolean isControllable )
    {
        this.name = name;
        this.isControllable = isControllable;
    }
}
