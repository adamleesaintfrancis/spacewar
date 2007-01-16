package edu.ou.spacewar;

/**
 * Created by IntelliJ IDEA.
 * User: jfager
 * Date: Apr 12, 2006
 * Time: 12:35:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class SWGameRecord {
    final boolean win;
    final double beacons, kills, hits, flags, flagsallowed, deaths, time;

    public SWGameRecord(boolean win,
                        double beacons,
                        double kills,
                        double hits,
                        double flags,
                        double flagsallowed,
                        double deaths,
                        double time) {
        this.win = win;
        this.beacons = beacons;
        this.kills = kills;
        this.hits = hits;
        this.flags = flags;
        this.flagsallowed = flagsallowed;
        this.deaths = deaths;
        this.time = time;
    }

    public boolean isWin() {
        return win;
    }
    
    public double getBeacons() {
        return beacons;
    }

    public double getKills() {
        return kills;
    }

    public double getHits() {
        return hits;
    }

    public double getFlags() {
        return flags;
    }

    public double getFlagsAllowed() {
        return this.flagsallowed;    
    }

    public double getDeaths() {
        return deaths;
    }

    public double getTime() {
        return time;
    }
}

