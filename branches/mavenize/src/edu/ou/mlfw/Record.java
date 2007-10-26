package edu.ou.mlfw;

public abstract class Record implements Comparable<Record> {
	protected static int sortMethod = 0;
	protected String displayName;
	protected int rank;
	protected float percentageWins;
	protected int totalWins;
	protected int totalGames;
	
	public Record(String displayName){
		super();
		this.displayName = displayName;
		this.rank = 0;
		this.totalWins = 0;
		this.percentageWins = 0f;
		this.totalGames = 0;
	}
	
	public String getDisplayName(){
		return displayName;
	}
	
	public void setDisplayName(String s){
		this.displayName = s;
	}

	public int getTotalGames(){
		return totalGames;
	}
	
	public int getRank(){
		return rank;
	}
	
	public void setRank(int rank){
		this.rank = rank;
	}
	
	public int getWins(){
		return totalWins;
	}
	
	public void setWinner(){
		this.totalWins++;
		if(this.totalGames != 0){
			this.percentageWins = this.totalWins / this.totalGames;
		}
	}
	
	public boolean equals(Object a){
		if(a == null){
			return false;
		}
		else if(a instanceof Record){
			Record b = (Record) a;
			if(b.displayName==null){
				return false;
			}
			return this.displayName.equals(b.displayName);
		}
		else{
			return false;
		}
	}
	
	public static void setSortMethod(int sMethod){
		sortMethod = sMethod;
	}
	
	public abstract String toString();
	public abstract String toCSV();
	public abstract String getCSVHeader();
	public abstract String toHTML();
	public abstract String getHTMLHeader();
	public abstract String getHTMLFooter();
	public abstract void addRecord(Record rhs);
	public abstract void reset();
}
