package edu.ou.spacewar;

import java.io.*;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import jargs.gnu.CmdLineParser;
import com.thoughtworks.xstream.XStream;

/**
 * SpacewarSim initializes a SpacewarGame and all of the agents for a particular run, and
 * launches the GUI.  You can adjust the swconfig.xml file to suit your needs!
 */
public class SpacewarSim {
    public static final int DEFGAMESTEPS = 3000; //5 minutes @ 10fps
    public static final int DEFCOMPTIME = 300;   //5 minutes
    public static final int DEFGAMES = 1000;
    public static final int DEFFREQ = 100;
    public static final String DEFCONFIG = "swconfig.xml";
    public static final String DEFOUTDIR = ".";
    public static final SWCentComAgent[] emptycentcom = new SWCentComAgent[0];
    public static final SWAgentRecord[] emptyagentrec = new SWAgentRecord[0];

    /**
     * Starts up the simulator!
     *
     * @param args Can have one class name of an agent.
     */
    public static void main(String[] args) {
        
        SpacewarConfiguration swconfig;
        try {
            String infile = (String) parser.getOptionValue(config, DEFCONFIG);
            FileReader fr = new FileReader(infile);
            swconfig = (SpacewarConfiguration) test.fromXML(fr);
            fr.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find configuration file!");
            e.printStackTrace();
            exit();
        } 
    }

    public static void runGame(BasicSpacewarWorld world,
                               SpacewarAgent[] shipagents,
                               SWAgentRecord[] shiprcrds,
                               int maxSteps,
                               long maxAgentTime) {
        runGame(world, shipagents, shiprcrds, emptycentcom, emptyagentrec, maxSteps, maxAgentTime);
    }

    /**
     * Runs a SpacewarGame, keeping track of statistics...
     *
     * @param world        The world to run.
     * @param shipagents   The agents controlling the ships in the world.
     * @param shiprcrds    The record keeping objects for each ship agent.
     * @param teamagents   The agents controlling the teams in the world.
     * @param teamrcrds    The record keeping objects for each team agent.
     * @param maxSteps     The maximum number of timesteps the simulator will run.
     * @param maxAgentTime The maximum amount of computation time an agent can use.
     */
    public static void runGame(BasicSpacewarWorld world,
                               SpacewarAgent[] shipagents,
                               SWAgentRecord[] shiprcrds,
                               SWCentComAgent[] teamagents,
                               SWAgentRecord[] teamrcrds,
                               int maxSteps,
                               long maxAgentTime) {
        long[] teamtimes = new long[teamagents.length];

        Ship[] ships = world.getGame().getAllShips();
        long[] shiptimes = new long[shipagents.length];

        //start up the simulation...
        int step = 0;
        boolean[] active = new boolean[shipagents.length];
        Arrays.fill(active, true);
        while (step < maxSteps) {
            //get an action from each teamagent, keeping track of the time that agent takes to make a decision
            for (int i = 0; i < teamagents.length; i++) {
                long start = System.currentTimeMillis();
                teamagents[i].takeAction();
                teamtimes[i] += System.currentTimeMillis() - start;
            }

            //get an action from each shipagent, keeping track of the time that agent takes to make a decision
            for (int i = 0; i < shipagents.length; i++) {
                if (active[i]) {
                    long start = System.currentTimeMillis();
                    shipagents[i].takeAction();
                    shiptimes[i] += System.currentTimeMillis() - start;
                }
            }

            //advance the physics engine 1 timestep
            world.advanceTime();
            step++;

            //end the player actions and clear away dead players
            for (int i = 0; i < shipagents.length; i++) {
                if (active[i]) {
                    long start = System.currentTimeMillis();
                    shipagents[i].endAction();
                    shiptimes[i] += System.currentTimeMillis() - start;

                    //check for dead or time expired agents
                    if (!shipagents[i].isRunning() || shiptimes[i] > maxAgentTime) {
                        start = System.currentTimeMillis();
                        shipagents[i].finish();
                        shiptimes[i] += System.currentTimeMillis() - start;

                        active[i] = false;
                    }
                }

                //check for respawned agents that haven't time expired
                if(shipagents[i].isRunning() && shiptimes[i] < maxAgentTime) {
                    active[i] = true;
                }
            }

            //end the team actions
            for (int i = 0; i < teamagents.length; i++) {
                long start = System.currentTimeMillis();
                teamagents[i].endAction();
                teamtimes[i] += System.currentTimeMillis() - start;
            }
        }

        //the game is over, so call finish for all the agents
        for (int i = 0; i < shipagents.length; i++) {
            long start = System.currentTimeMillis();
            shipagents[i].finish();
            shiptimes[i] += System.currentTimeMillis() - start;
        }

        for(int i = 0; i<teamagents.length;i++) {
            long start = System.currentTimeMillis();
            teamagents[i].finish();
            teamtimes[i] += System.currentTimeMillis() - start;
        }

        //find the winning team
        int winningTeam = getWinningTeam(ships);

        //record the team statistics
        for(int i = 0; i<teamrcrds.length; i++) {
            if(teamrcrds[i] != null) {
                int b = 0, k = 0, h = 0, f = 0, fa= 0, d = 0;
                for(Ship s : ships) {
                    if(s.getTeam() == teamrcrds[i].getTeam()) {
                        b += s.getBeacons();
                        k += s.getKills();
                        h += s.getHits();
                        f += s.getFlags();
                        d += s.getDeaths();
                    } else {
                        fa += s.getFlags();
                    }
                }
                teamrcrds[i].recordGame(teamrcrds[i].getTeam() == winningTeam, b,k,h,f,fa,d,teamtimes[i]);
            }
        }

        //record the ship statistics
        for (int i = 0; i < shiprcrds.length; i++) {
            if (shiprcrds[i] != null) {
                Ship s = ships[i];
                //records beacons, kills, hits, flags, deaths, and computation time
                shiprcrds[i].recordGame(s.getTeam() == winningTeam, s.getBeacons(), s.getKills(),
                        s.getHits(), s.getFlags(), 0, s.getDeaths(), shiptimes[i]);
            }
        }
    }

    private static int getWinningTeam(Ship[] ships) {
        Integer[] out = new Integer[1];

        //win determined by flags, then kills, then deaths
        HashMap<Integer, double[]>  stats = new HashMap<Integer, double[]>();
        for(Ship ship : ships) {
            int team = ship.getTeam();
            if(stats.containsKey(team)) {
                double[] teamstats = stats.get(team);
                teamstats[0] += ship.getFlags();
                teamstats[1] += ship.getKills();
                teamstats[2] += ship.getDeaths();
            } else {
                double[] teamstats = new double[3];
                teamstats[0] = ship.getFlags();
                teamstats[1] = ship.getKills();
                teamstats[2] = ship.getDeaths();
                stats.put(team, teamstats);
            }
        }

        HashSet<Integer> winners = new HashSet<Integer>();
        double best = Double.NEGATIVE_INFINITY;

        //check flags from full set:
        for(Integer team : stats.keySet()) {
            double flags = stats.get(team)[0];
            if(flags > best) {
                best = flags;
                winners.clear();
                winners.add(team);
            } else if(flags == best) {
                winners.add(team);
            }
        }

        if(winners.size() == 1) {
            winners.toArray(out);
            return out[0];
        }

        //check kills from winner set:
        Integer[] tmp = new Integer[winners.size()];
        winners.toArray(tmp);
        winners.clear();
        best = Double.NEGATIVE_INFINITY;
        for(Integer team : tmp) {
            double kills = stats.get(team)[1];
            if(kills > best) {
                best = kills;
                winners.clear();
                winners.add(team);
            } else if(kills == best) {
                winners.add(team);
            }
        }

        if(winners.size() == 1) {
            winners.toArray(out);
            return out[0];
        }

        //check deaths from winner set:
        tmp = new Integer[winners.size()];
        winners.toArray(tmp);
        winners.clear();
        best = Double.POSITIVE_INFINITY;
        for(Integer team : tmp) {
            double deaths = stats.get(team)[2];
            if(deaths < best) {
                best = deaths;
                winners.clear();
                winners.add(team);
            } else if(deaths == best) {
                winners.add(team);
            }
        }

        if(winners.size() == 1) {
            winners.toArray(out);
            return out[0];
        }

        return -1;
    }

    private static LinkedList<Integer> findMaxSet(final LinkedList<Integer> currset, final int[] criteria) {
        final LinkedList<Integer> out = new LinkedList<Integer>();
        int max = Integer.MIN_VALUE;
        for (final int i : currset) {
            if (criteria[i] > max) {
                out.clear();
                out.add(i);
                max = criteria[i];
            } else if (criteria[i] == max) {
                out.add(i);
            }
        }
        return out;
    }

    private static void writeOutput(SWAgentRecord[] teamrcrds, SWCentComAgent[] teamagents,
                                    SWAgentRecord[] shiprcrds, SpacewarAgent[] shipagents, String outdir) {
        for (int i = 0; i < shiprcrds.length; i++) {
            SWAgentRecord srcrd = shiprcrds[i];
            if (srcrd != null) {
                try {
                    FileWriter out =
                            new FileWriter(outdir + File.separator + srcrd.getName() + "Results.csv", true);
                    srcrd.toCSV(out);
                    out.flush();
                    out.close();
                    srcrd.clearGameRecords();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }

                try {
                    FileWriter out =
                            new FileWriter(outdir + File.separator + srcrd.getName() + "Knowledge.xml", false);
                    shipagents[i].saveKnowledge(out);
                    out.flush();
                    out.close();
                    srcrd.clearGameRecords();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }

        for (int i = 0; i < teamrcrds.length; i++) {
            SWAgentRecord trcrd = teamrcrds[i];
            if (trcrd != null) {
                try {
                    FileWriter out =
                            new FileWriter(outdir + File.separator + trcrd.getName() + "Results.csv", true);
                    trcrd.toCSV(out);
                    out.flush();
                    out.close();
                    trcrd.clearGameRecords();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }

                try {
                    FileWriter out =
                            new FileWriter(outdir + File.separator + trcrd.getName() + "Knowledge.xml", false);
                    teamagents[i].saveKnowledge(out);
                    out.flush();
                    out.close();
                    trcrd.clearGameRecords();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }

    /**
     * Exits the program with usage instructions.
     */
    private static void exit() {
        System.out.println(
                "Usage: SpacewarSim [-h] [-g[H]] [-n games] [-s maxSteps] [-c maxComp] [-f outFreq] " +
                        "[-p /path/to/configfile] [-o /path/to/outputdir]\n\n" +

                        "-h display this help screen and exit.\n\n" +

                        "-g indicates the gui should be shown.  If this flag\n" +
                        "   is not set, the program will run in experiment mode.\n\n" +

                        "-H indicates that the final ship added should be human\n" +
                        "   controllable.  This flag is ignored if -g is not set.\n\n" +

                        "-n indicates the maximum number of games that will be run. \n" +
                        "   If this flag is not set, " + DEFGAMES + " games will be played by default.\n" +
                        "   This flag is ignored if -g is set.\n\n" +

                        "-s indicates the maximum number of timesteps in a single game.\n" +
                        "   Updates occur at 1/10th second intervals, so a 5 minute game is\n" +
                        "   given by 5 * 60 * 10 = 3000 timesteps.  If this flag is not set,\n" +
                        "   the game will be " + DEFGAMESTEPS + " timesteps by default.  This flag is \n" +
                        "   ignored if -g is set.\n\n" +

                        "-c indicates the maximum computation time allowed to a single agent \n" +
                        "   in seconds.  A five minute computation time is given by 5 * 60 = 300s. \n" +
                        "   If this flag is not set, the game will be " + DEFCOMPTIME + "s by default.  This flag \n" +
                        "   is ignored if -g is set.\n\n" +

                        "-f indicates the output frequency, that is, the number of games between subsequent\n" +
                        "   output sessions.  An ouput session flushes the results buffer for each agent and \n" +
                        "   saves the agent's accumulated knowledge. If this flag is not set, the output \n" +
                        "   frequency will be every " + DEFFREQ + " games by default.  This flag is ignored if -g is set.\n\n" +

                        "-p indicates the path to the game configuration file.\n" +
                        "   If -p is not set, the program will attempt to find\n" +
                        "   and load \"" + DEFCONFIG + "\".\n\n" +

                        "-o indicates the path to the output directory for saving agent data.\n" +
                        "   If -o is not set, the program will use \"" + DEFOUTDIR + "\" as the base directory.\n" +
                        "   The files that are saved in this directory include the agent's knowledge\n" +
                        "   files, adnd the result files.  All filenames are a concatenation of the name of\n" +
                        "   the agent with a descriptive suffix and a 3 character file extension.\n" +
                        "   If -g is set, this flag indicates where an agent's knowledge will be loaded from;\n" +
                        "   no files will be saved, however.\n"
        );
        System.exit(-1);
    }
}
