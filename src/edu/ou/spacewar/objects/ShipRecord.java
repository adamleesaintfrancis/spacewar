package edu.ou.spacewar.objects;

import edu.ou.mlfw.Record;
import java.lang.Comparable;
import java.lang.ClassCastException;

public class ShipRecord extends Record implements Comparable{
	private float avgBeacons;
	private float avgKills;
	private float avgHits;
	private float avgDeaths;
	private float avgFlags;
	private float avgShotsFired;
	private float avgCPUTime;
	private int totalGames;
	private int totalBeacons;
	private int totalKills;
	private int totalDeaths;
	private int totalHits;
	private int totalFlags;
	private int totalShotsFired;
	private int totalCPUTime;
	
	public ShipRecord(String displayName){
		super(displayName);
		this.totalGames = 0;
		this.totalBeacons = 0;
		this.totalKills = 0;
		this.totalDeaths = 0;
		this.totalHits = 0;
		this.totalFlags = 0;
		this.totalShotsFired = 0;
		this.totalCPUTime = 0;
		this.avgBeacons = 0f;
		this.avgKills = 0f;
		this.avgHits = 0f;
		this.avgDeaths = 0f;
		this.avgFlags = 0f;
		this.avgShotsFired = 0f;
		this.avgCPUTime = 0f;
	}
	
	public ShipRecord(String displayName,
					int totalGames,
					int totalBeacons,
					int totalKills,
					int totalDeaths,
					int totalHits,
					int totalFlags,
					int totalShotsFired,
					int totalCPUTime){
		super(displayName);
		this.totalGames = totalGames;
		this.totalBeacons = totalBeacons;
		this.totalKills = totalKills;
		this.totalDeaths = totalDeaths;
		this.totalHits = totalHits;
		this.totalFlags = totalFlags;
		this.totalShotsFired = totalShotsFired;
		this.totalCPUTime = totalCPUTime;
		
		if(this.totalGames != 0){
			this.avgBeacons = (float) this.totalBeacons/this.totalGames;
			this.avgKills = (float) this.totalKills/this.totalGames;
			this.avgHits = (float) this.totalHits/this.totalGames;
			this.avgDeaths = (float) this.totalDeaths/this.totalGames;
			this.avgFlags = (float) this.totalFlags/this.totalGames;
			this.avgShotsFired = (float) this.totalShotsFired/this.totalGames;
			this.avgCPUTime = (float) this.totalCPUTime/this.totalGames;
		}
	}
	
	public ShipRecord(String displayName, ShipRecord rhs){
		this(displayName, rhs.totalGames, rhs.totalBeacons, rhs.totalKills, 
				rhs.totalDeaths, rhs.totalHits, rhs.totalFlags, rhs.totalShotsFired, 
				rhs.totalCPUTime);
	}
	
	public ShipRecord(ShipRecord rhs){
		this(rhs.displayName, rhs);
	}
	
	@Override
	public void addRecord(Record rhs) {
		ShipRecord a = (ShipRecord) rhs;
		this.totalGames += a.totalGames;
		this.totalBeacons += a.totalBeacons;
		this.totalKills += a.totalKills;
		this.totalDeaths += a.totalDeaths;
		this.totalHits += a.totalHits;
		this.totalFlags += a.totalFlags;
		this.totalShotsFired += a.totalShotsFired;
		this.totalCPUTime += a.totalCPUTime;
		
		if(this.totalGames != 0){
			this.avgBeacons = (float) this.totalBeacons/this.totalGames;
			this.avgKills = (float) this.totalKills/this.totalGames;
			this.avgHits = (float) this.totalHits/this.totalGames;
			this.avgDeaths = (float) this.totalDeaths/this.totalGames;
			this.avgFlags = (float) this.totalFlags/this.totalGames;
			this.avgShotsFired = (float) this.totalShotsFired/this.totalGames;
			this.avgCPUTime = (float) this.totalCPUTime/this.totalGames;
		}
	}

	@Override
	public final String getCSVHeader() {
		return new String("Display Name,Average Beacons,Total Beacons,Average Kills,Total Kills,Average Hits,Total Hits,Average Deaths,Total Deaths,Average Flags,Total Flags");
	}

	@Override
	public String getHTMLHeader() {
		return new String("<title> Spacewar Ladder </title>\n"+
				"<body>\n" +
				"<h1> Spacewar Ladder </h1>\n" +
				"<table border=2>\n" +
				"<tr>\n" +
				"<th>Display Name</th>\n" +
				"<th>Rank</th>\n" +
				"<th>Number of Games</th>\n" +
				"<th>Average Beacons</th>\n" +
				"<th>Total Beacons</th>\n" +
				"<th>Average Deaths</th>\n" +
				"<th>Total Deaths</th>\n" +
				"<th>Average Kills</th>\n" +
				"<th>Total Kills</th>\n" +
				"<th>Average Hits</th>\n" +
				"<th>Total Hits</th>\n" +
				"<th>Average Shots Fired</th>\n" +
				"<th>Total Shots Fired</th>\n" +
				"<th>Average Flags</th>\n" +
				"<th>Total Flags</th>\n" +
				"<th>Average CPU Time</th>\n" +
				"<th>Total CPU Time</th>\n" +
				"</tr>");
	}

	@Override
	public String getHTMLFooter(){
		return new String("</table>" + "\n");
	}
	
	@Override
	public String toCSV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toHTML() {
		return new String("<tr>\n" +
				"<td>" + displayName + "</td>\n" +
				"<td>" + Integer.toString(rank) + "</td>\n" +
				"<td>" + Integer.toString(totalGames) + "</td>\n" +
				"<td>" + Float.toString(avgBeacons)+ "</td>\n" +
				"<td>" + Integer.toString(totalBeacons) + "</td>\n" +
				"<td>" + Float.toString(avgDeaths)+ "</td>\n" +
				"<td>" + Integer.toString(totalDeaths) + "</td>\n" +
				"<td>" + Float.toString(avgKills)+ "</td>\n" +
				"<td>" + Integer.toString(totalKills) + "</td>\n" +
				"<td>" + Float.toString(avgHits)+ "</td>\n" +
				"<td>" + Integer.toString(totalHits) + "</td>\n" +
				"<td>" + Float.toString(avgShotsFired)+ "</td>\n" +
				"<td>" + Integer.toString(totalShotsFired) + "</td>\n" +
				"<td>" + Float.toString(avgFlags)+ "</td>\n" +
				"<td>" + Integer.toString(totalFlags) + "</td>\n" +
				"<td>" + Float.toString(avgCPUTime)+ "</td>\n" +
				"<td>" + Integer.toString(totalCPUTime) + "</td>\n" +
				"</tr>");
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		this.totalGames = 0;
		this.totalBeacons = 0;
		this.totalKills = 0;
		this.totalDeaths = 0;
		this.totalHits = 0;
		this.totalFlags = 0;
		this.totalShotsFired = 0;
		this.totalCPUTime = 0;
	}
	
	public int compareTo(Object o){
		
		if(o instanceof ShipRecord){
			ShipRecord s = (ShipRecord) o;
			if(this.avgBeacons < s.avgBeacons){
				return 1;
			}
			else if(this.avgBeacons > s.avgBeacons){
				return -1;
			}
			else{
				if(this.avgDeaths < s.avgDeaths){
					return 1;
				}
				else if(this.avgDeaths > s.avgDeaths){
					return -1;
				}
				else{
					return 0;
				}
			}
		}
		else{
			throw new ClassCastException("Bad cast");
		}
	}
	
	public int getTotalGames(){
		return totalGames;
	}
	
	public int getTotalBeacons(){
		return totalBeacons;
	}
	
	public int getTotalKills(){
		return totalKills;
	}
	
	public int getTotalHits(){
		return totalHits;
	}
	
	public int getTotalDeaths(){
		return totalDeaths;
	}
	
	public int getTotalFlags(){
		return totalFlags;
	}
	
	public int getTotalShotsFired(){
		return totalShotsFired;
	}
	
	public int getTotalCPUTime(){
		return totalCPUTime;
	}
}
