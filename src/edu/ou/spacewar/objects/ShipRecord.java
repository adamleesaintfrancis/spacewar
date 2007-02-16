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
	private long totalCPUTime;
	
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
					long totalCPUTime){
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

	public static String getHTMLHeader_s(){
		return new String("<title> Spacewar Ladder </title>\n"+
				"<body>\n" +
				"<h1> Spacewar Ladder </h1>\n" +
				"<table border=2>\n" +
				"<tr>" +
				"<th>Display Name</th>" +
				"<th>Rank</th>" +
				"<th>Number of Games</th>" +
				"<th>Average Beacons</th>" +
				"<th>Total Beacons</th>" +
				"<th>Average Deaths</th>" +
				"<th>Total Deaths</th>" +
				"<th>Average Kills</th>" +
				"<th>Total Kills</th>" +
				"<th>Average Hits</th>" +
				"<th>Total Hits</th>" +
				"<th>Average Shots Fired</th>" +
				"<th>Total Shots Fired</th>" +
				"<th>Average Flags</th>" +
				"<th>Total Flags</th>" +
				"<th>Average CPU Time</th>" +
				"<th>Total CPU Time</th>" +
				"</tr>");
	}
	
	@Override
	public String getHTMLHeader() {
		return getHTMLHeader_s();
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
		return new String("<tr>" +
				"<td>" + displayName + "</td>" +
				"<td>" + Integer.toString(rank) + "</td>" +
				"<td>" + Integer.toString(totalGames) + "</td>" +
				"<td>" + Float.toString(avgBeacons)+ "</td>" +
				"<td>" + Integer.toString(totalBeacons) + "</td>" +
				"<td>" + Float.toString(avgDeaths)+ "</td>" +
				"<td>" + Integer.toString(totalDeaths) + "</td>" +
				"<td>" + Float.toString(avgKills)+ "</td>" +
				"<td>" + Integer.toString(totalKills) + "</td>" +
				"<td>" + Float.toString(avgHits)+ "</td>" +
				"<td>" + Integer.toString(totalHits) + "</td>" +
				"<td>" + Float.toString(avgShotsFired)+ "</td>" +
				"<td>" + Integer.toString(totalShotsFired) + "</td>" +
				"<td>" + Float.toString(avgFlags)+ "</td>" +
				"<td>" + Integer.toString(totalFlags) + "</td>" +
				"<td>" + Float.toString(avgCPUTime)+ "</td>" +
				"<td>" + Long.toString(totalCPUTime) + "</td>" +
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
				if(this.avgDeaths > s.avgDeaths){
					return 1;
				}
				else if(this.avgDeaths < s.avgDeaths){
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
	
	public long getTotalCPUTime(){
		return totalCPUTime;
	}
}
