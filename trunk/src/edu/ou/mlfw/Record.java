package edu.ou.mlfw;

public abstract class Record implements Comparable<Record> {
	protected String displayName;
	protected int rank;
	
	public Record(String displayName){
		super();
		this.displayName = displayName;
		this.rank = 0;
	}
	
	public String getDisplayName(){
		return displayName;
	}
	
	public void setDisplayName(String s){
		this.displayName = s;
	}

	public int getRank(){
		return rank;
	}
	
	public void setRank(int rank){
		this.rank = rank;
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
	
	public abstract String toString();
	public abstract String toCSV();
	public abstract String getCSVHeader();
	public abstract String toHTML();
	public abstract String getHTMLHeader();
	public abstract String getHTMLFooter();
	public abstract void addRecord(Record rhs);
	public abstract void reset();
}
