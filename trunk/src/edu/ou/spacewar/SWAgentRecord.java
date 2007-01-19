package edu.ou.spacewar;

import java.io.Serializable;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Amy McGovern
 * Date: Feb 3, 2005
 * Time: 10:11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class SWAgentRecord implements Serializable {
    String name, fullName;
    int rank, team, games, wins;
    double ttldeaths, avgdeaths, maxdeaths, mindeaths;
    double ttlhits, avghits, maxhits, minhits;
    double ttlflags, avgflags, maxflags, minflags;
    double ttlfsallwd, avgfsallwd, maxfsallwd, minfsallwd;
    double ttlbeacons, avgbeacons, maxbeacons, minbeacons;
    double ttlkills, avgkills, maxkills, minkills;
    double ttltime, avgtime, maxtime, mintime;
    ArrayList<SWGameRecord> gameRecords;

    public SWAgentRecord(String name, String fullName, int rank) {
        this(name, -1);
        this.fullName = fullName;
        this.rank = rank;
    }

    public SWAgentRecord(String name, String fullName, int team, int rank) {
        this(name, team);
        this.fullName = fullName;
        this.rank = rank;
    }

    public SWAgentRecord(String name, int team) {
        this.name = name;
        this.team = team;

        games = 0;
        wins = 0;

        ttlbeacons = 0;
        avgbeacons = 0;
        maxbeacons = 0;
        minbeacons = Integer.MAX_VALUE;

        ttlkills = 0;
        avgkills = 0;
        maxkills = 0;
        minkills = Integer.MAX_VALUE;

        ttltime = 0;
        avgtime = 0;
        maxtime = 0;
        mintime = Integer.MAX_VALUE;

        ttlflags = 0;
        avgflags = 0;
        maxflags = 0;
        minflags = Integer.MAX_VALUE;

        ttldeaths = 0;
        avgdeaths = 0;
        maxdeaths = 0;
        mindeaths = Integer.MAX_VALUE;

        ttlhits = 0;
        avghits = 0;
        maxhits = 0;
        minhits = Integer.MAX_VALUE;

        this.gameRecords = new ArrayList<SWGameRecord>();
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getGames() {
        return games;
    }

    public int getWins() {
        return wins;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public double getTtlbeacons() {
        return ttlbeacons;
    }

    public double getAvgbeacons() {
        return avgbeacons;
    }

    public double getMaxbeacons() {
        return maxbeacons;
    }

    public double getMinbeacons() {
        return minbeacons;
    }

    public double getTtlkills() {
        return ttlkills;
    }

    public double getAvgkills() {
        return avgkills;
    }

    public double getMaxkills() {
        return maxkills;
    }

    public double getMinkills() {
        return minkills;
    }

    public double getTtltime() {
        return ttltime;
    }

    public double getAvgtime() {
        return avgtime;
    }

    public double getMaxtime() {
        return maxtime;
    }

    public double getMintime() {
        return mintime;
    }

    public double getTtldeaths() {
        return ttldeaths;
    }

    public double getAvgdeaths() {
        return avgdeaths;
    }

    public double getMaxdeaths() {
        return maxdeaths;
    }

    public double getMindeaths() {
        return mindeaths;
    }

    public double getTtlhits() {
        return ttlhits;
    }

    public double getAvghits() {
        return avghits;
    }

    public double getMaxhits() {
        return maxhits;
    }

    public double getMinhits() {
        return minhits;
    }

    public double getTtlflags() {
        return ttlflags;
    }

    public double getAvgflags() {
        return avgflags;
    }

    public double getMaxflags() {
        return maxflags;
    }

    public double getMinflags() {
        return minflags;
    }

    public double getTtlfsallwd() {
        return ttlfsallwd;
    }

    public double getAvgfsallwd() {
        return avgfsallwd;
    }

    public double getMaxfsallwd() {
        return maxfsallwd;
    }

    public double getMinfsallwd() {
        return minfsallwd;
    }

    public ArrayList<SWGameRecord> getGameRecords() {
        return gameRecords;
    }

    //records beacons, kills, hits, flags, deaths, and computation time
    public void recordGame(boolean win, int beacons, int kills, int hits, int flags, int fsallwd, int deaths, long time) {
        gameRecords.add(new SWGameRecord(win, beacons, kills, hits, flags, fsallwd, deaths, time));

        games++;
        if(win) {
            wins++;
        }

        ttlbeacons += beacons;
        avgbeacons = ttlbeacons / (double)games;
        if(beacons > maxbeacons) {
            maxbeacons = beacons;
        } else if(beacons < minbeacons) {
            minbeacons = beacons;
        }

        ttlkills += kills;
        avgkills = ttlkills / (double)games;
        if(kills > maxkills) {
            maxkills = kills;
        } else if(kills < minkills) {
            minkills = kills;
        }

        ttlhits += hits;
        avghits = ttlhits / (double)games;
        if(hits > maxhits) {
            maxhits = hits;
        } else if(hits < minhits) {
            minhits = hits;
        }

        ttlflags += flags;
        avgflags = ttlflags / (double)games;
        if(flags > maxflags) {
            maxflags = flags;
        } else if(flags < minflags) {
            minflags = flags;
        }

        ttlfsallwd += fsallwd;
        avgfsallwd = ttlfsallwd / (double) games;
        if (fsallwd > maxfsallwd) {
            maxfsallwd = fsallwd;
        } else if (fsallwd < minfsallwd) {
            minfsallwd = fsallwd;
        }

        ttldeaths += deaths;
        avgdeaths = ttldeaths / (double)games;
        if(deaths > maxdeaths) {
            maxdeaths = deaths;
        } else if(deaths < mindeaths) {
            mindeaths = deaths;
        }

        ttltime += time;
        avgtime = ttltime / (double)games;
        if(time > maxtime) {
            maxtime = time;
        } else if(time < mintime) {
            mintime = time;
        }


    }

    public String toString() {
        StringBuilder output = new StringBuilder("name: " + name + " fullname: " + fullName);
        output.append(" rank: ").append(rank);

        output.append(" ttlbeacons: ").append(ttlbeacons);
        output.append(" avgbeacons: ").append(avgbeacons);
        output.append(" maxbeacons: ").append(maxbeacons);
        output.append(" minbeacons: ").append(minbeacons);

        output.append(" ttlkills: ").append(ttlkills);
        output.append(" avgkills: ").append(avgkills);
        output.append(" maxkills: ").append(maxkills);
        output.append(" minkills: ").append(minkills);

        output.append(" ttlhits: ").append(ttlhits);
        output.append(" avghits: ").append(avghits);
        output.append(" maxhits: ").append(maxhits);
        output.append(" minhits: ").append(minhits);

        output.append(" ttlflags: ").append(ttlflags);
        output.append(" avgflags: ").append(avgflags);
        output.append(" maxflags: ").append(maxflags);
        output.append(" minflags: ").append(minflags);

        output.append(" ttldeaths: ").append(ttldeaths);
        output.append(" avgdeaths: ").append(avgdeaths);
        output.append(" maxdeaths: ").append(maxdeaths);
        output.append(" mindeaths: ").append(mindeaths);

        output.append(" ttltime: ").append(ttltime);
        output.append(" avgtime: ").append(avgtime);
        output.append(" maxtime: ").append(maxtime);
        output.append(" mintime: ").append(mintime);
        return output.toString();
    }

    /**
     * Output the history of Games to a comma-separated values file.
     * The format is:
     *      beacons, kills, hits, flags, deaths, time
     *
     * @param fw The FileWriter to be used for output.
     */
    public void toCSV(FileWriter fw) {
        try {
            for(SWGameRecord gr: gameRecords) {
                fw.write(gr.beacons + ", ");
                fw.write(gr.kills + ", ");
                fw.write(gr.hits + ", ");
                fw.write(gr.flags + ", ");
                fw.write(gr.deaths + ", ");
                fw.write(gr.time + ", ");
                fw.write("\n");
            }
        }catch(Exception e) {
            System.out.println("Error writing game record csv file");
            e.printStackTrace();
        }
    }

    /**
     * Clear all the GameRecords for this AgentRecord...
     */
    public void clearGameRecords() {
        this.gameRecords.clear();
    }

    /**
     * Truncates to %d.4 precision for nice html printing
     */
    public void roundNumbersForPrinting() {
        avgbeacons = Math.round(avgbeacons * 10) / 10.0;
        avgkills = Math.round(avgkills * 10) / 10.0;
        avghits = Math.round(avghits * 10) / 10.0;
        avgflags = Math.round(avgflags * 10) / 10.0;
        avgdeaths = Math.round(avgdeaths * 10) / 10.0;
        avgtime = Math.round(avgtime * 10) / 10.0;
    }

    /**
     * Hash on the record's short name...
     *
     * @return The hash value of the agent's label
     */
    public int hashCode() {
        return name.hashCode();
    }
}

