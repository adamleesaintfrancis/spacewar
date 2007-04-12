package edu.ou.spacewar.objects;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import edu.ou.mlfw.Record;

public class TeamRecord extends Record {
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

	public TeamRecord(String displayName){
		super(displayName);
		this.totalGames = 1;
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

	public TeamRecord(String displayName,
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

	public TeamRecord(String displayName, TeamRecord rhs){
		this(displayName, rhs.totalGames, rhs.totalBeacons, rhs.totalKills, 
				rhs.totalDeaths, rhs.totalHits, rhs.totalFlags, rhs.totalShotsFired, 
				rhs.totalCPUTime);
	}

	public TeamRecord(TeamRecord rhs){
		this(rhs.displayName, rhs);
	}

	@Override
	public void addRecord(Record rhs) {
		TeamRecord a = (TeamRecord) rhs;
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
		String out = null;
		switch(sortMethod){
		case 1:
		{
			out = new String("Display Name,Average Kills,Total Kills,Average Deaths,Total Deaths,Average Hits,Total Hits,Average Beacons,Total Beacons,Average Flags,Total Flags");
			break;
		}
		default:
		{
			out = new String("Display Name,Average Beacons,Total Beacons,Average Kills,Total Kills,Average Hits,Total Hits,Average Deaths,Total Deaths,Average Flags,Total Flags");
		}
		}
		return out;
	}

	public static String getHTMLHeader_s(){
		String out = null;
		switch(sortMethod){
		case 1:
		{
			out = new String("<title> Spacewar Ladder </title>\n"+
					"<body>\n" +
					"<h1> Spacewar Ladder </h1>\n" +
					"<table border=2>\n" +
					"<tr>" +
					"<th>Display Name</th>" +
					"<th>Rank</th>" +
					"<th>Number of Games</th>" +
					"<th>Average Kills</th>" +
					"<th>Total Kills</th>" +
					"<th>Average Deaths</th>" +
					"<th>Total Deaths</th>" +
					"<th>Average Beacons</th>" +
					"<th>Total Beacons</th>" +
					"<th>Average Hits</th>" +
					"<th>Total Hits</th>" +
					"<th>Average Shots Fired</th>" +
					"<th>Total Shots Fired</th>" +
					"<th>Average Flags</th>" +
					"<th>Total Flags</th>" +
					"<th>Average CPU Time</th>" +
					"<th>Total CPU Time</th>" +
			"</tr>");
			break;
		}
		default:
		{
			out = new String("<title> Spacewar Ladder </title>\n"+
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
		}
		return out;
	}

	@Override
	public String getHTMLHeader() {
		return getHTMLHeader_s();
	}

	@Override
	public String getHTMLFooter(){
		GregorianCalendar calendar = new GregorianCalendar();
		return new String("</table>" + "\n" +
				"<p>Ladder updated as of " +
				Integer.toString(calendar.get(Calendar.MONTH)+1) + "/" + 
				Integer.toString(calendar.get(Calendar.DATE)) + "/" + 
				Integer.toString(calendar.get(Calendar.YEAR)) +
				" at "+
				Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + 
				Integer.toString(calendar.get(Calendar.MINUTE)) + ":" + 
				Integer.toString(calendar.get(Calendar.SECOND)) + " " + 
				TimeZone.getDefault().getDisplayName() + "</p>" +
		"</body>\n");
	}

	@Override
	public String toCSV() {
		String out = null;
		switch(sortMethod){
		case 1:
		{
			out = new String(displayName + "," +
					Integer.toString(rank) + "," +
					Integer.toString(totalGames) + "," +
					Float.toString(avgKills)+ "," +
					Integer.toString(totalKills) + "," +
					Float.toString(avgDeaths)+ "," +
					Integer.toString(totalDeaths) + "," +
					Float.toString(avgBeacons)+ "," +
					Integer.toString(totalBeacons) + "," +
					Float.toString(avgHits)+ "," +
					Integer.toString(totalHits) + "," +
					Float.toString(avgShotsFired)+ "," +
					Integer.toString(totalShotsFired) + "," +
					Float.toString(avgFlags)+ "," +
					Integer.toString(totalFlags) + "," +
					Float.toString(avgCPUTime)+ "," +
					Long.toString(totalCPUTime) + ",");
			break;
		}
		default:
		{
			out = new String(displayName + "," +
					Integer.toString(rank) + "," +
					Integer.toString(totalGames) + "," +
					Float.toString(avgBeacons)+ "," +
					Integer.toString(totalBeacons) + "," +
					Float.toString(avgDeaths)+ "," +
					Integer.toString(totalDeaths) + "," +
					Float.toString(avgKills)+ "," +
					Integer.toString(totalKills) + "," +
					Float.toString(avgHits)+ "," +
					Integer.toString(totalHits) + "," +
					Float.toString(avgShotsFired)+ "," +
					Integer.toString(totalShotsFired) + "," +
					Float.toString(avgFlags)+ "," +
					Integer.toString(totalFlags) + "," +
					Float.toString(avgCPUTime)+ "," +
					Long.toString(totalCPUTime) + ",");
		}
		}
		return out;
	}

	@Override
	public String toHTML() {
		String out = null;
		switch(sortMethod){
		case 1:
		{
			out = new String("<tr>" +
					"<td>" + displayName + "</td>" +
					"<td>" + Integer.toString(rank) + "</td>" +
					"<td>" + Integer.toString(totalGames) + "</td>" +
					"<td>" + Float.toString(avgKills)+ "</td>" +
					"<td>" + Integer.toString(totalKills) + "</td>" +
					"<td>" + Float.toString(avgDeaths)+ "</td>" +
					"<td>" + Integer.toString(totalDeaths) + "</td>" +
					"<td>" + Float.toString(avgBeacons)+ "</td>" +
					"<td>" + Integer.toString(totalBeacons) + "</td>" +
					"<td>" + Float.toString(avgHits)+ "</td>" +
					"<td>" + Integer.toString(totalHits) + "</td>" +
					"<td>" + Float.toString(avgShotsFired)+ "</td>" +
					"<td>" + Integer.toString(totalShotsFired) + "</td>" +
					"<td>" + Float.toString(avgFlags)+ "</td>" +
					"<td>" + Integer.toString(totalFlags) + "</td>" +
					"<td>" + Float.toString(avgCPUTime)+ "</td>" +
					"<td>" + Long.toString(totalCPUTime) + "</td>" +
			"</tr>");
			break;
		}
		default:
		{
			out = new String("<tr>" +
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
		}
		return out;
	}

	@Override
	public String toString() {
		String out = null;
		switch(sortMethod){
		case 1:
		{
			out = new String(displayName + "," +
					Integer.toString(rank) + "," +
					Integer.toString(totalGames) + "," +
					Integer.toString(totalKills) + "," +
					Float.toString(avgHits)+ "," +
					Float.toString(avgDeaths)+ "," +
					Integer.toString(totalDeaths) + "," +
					Float.toString(avgBeacons)+ "," +
					Integer.toString(totalBeacons) + "," +
					Float.toString(avgKills)+ "," +
					Integer.toString(totalHits) + "," +
					Float.toString(avgShotsFired)+ "," +
					Integer.toString(totalShotsFired) + "," +
					Float.toString(avgFlags)+ "," +
					Integer.toString(totalFlags) + "," +
					Float.toString(avgCPUTime)+ "," +
					Long.toString(totalCPUTime) + ",");
		}
		default:
		{
			out = new String(displayName + "," +
					Integer.toString(rank) + "," +
					Integer.toString(totalGames) + "," +
					Float.toString(avgBeacons)+ "," +
					Integer.toString(totalBeacons) + "," +
					Float.toString(avgDeaths)+ "," +
					Integer.toString(totalDeaths) + "," +
					Float.toString(avgKills)+ "," +
					Integer.toString(totalKills) + "," +
					Float.toString(avgHits)+ "," +
					Integer.toString(totalHits) + "," +
					Float.toString(avgShotsFired)+ "," +
					Integer.toString(totalShotsFired) + "," +
					Float.toString(avgFlags)+ "," +
					Integer.toString(totalFlags) + "," +
					Float.toString(avgCPUTime)+ "," +
					Long.toString(totalCPUTime) + ",");
		}
		}
		return out;
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

	public int compareTo(Record o){		
		if(o instanceof TeamRecord){
			TeamRecord t = (TeamRecord) o;
			switch(sortMethod){
			case 1:
			{
				return killsThenDeathSortMethod(t);
			}
			default:
			{
				return beaconThenDeathSortMethod(t);
			}
			}
		}
		else{
			throw new ClassCastException("Bad cast");
		}
	}

	private int beaconThenDeathSortMethod(TeamRecord s){
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

	private int killsThenDeathSortMethod(TeamRecord s){
		if(this.avgKills < s.avgKills){
			return 1;
		}
		else if(this.avgKills > s.avgKills){
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

	public int hashCode() {
		return displayName.hashCode();
	}
	
	public void addShip(ShipRecord s){
		this.totalBeacons += s.getTotalBeacons();
		this.totalDeaths += s.getTotalDeaths();
		this.totalFlags += s.getTotalFlags();
		this.totalHits += s.getTotalHits();
		this.totalKills += s.getTotalKills();
		this.totalShotsFired += s.getTotalShotsFired();
		
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
}
