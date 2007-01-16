package edu.ou.spacewar.configuration;

/**
 * Created by IntelliJ IDEA.
 * User: jfager
 * Date: Apr 12, 2006
 * Time: 11:17:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class TeamInformation {
    public String name;
    public int number;
    public String agentClass;
    public boolean agentRecord;

    public TeamInformation(String name, int number, String agentClass, boolean agentRecord) {
        this.name = name;
        this.number = number;
        this.agentClass = agentClass;
        this.agentRecord = agentRecord;
    }
}
