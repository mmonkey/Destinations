package com.github.mmonkey.Destinations.Parse;

public class Match {
	
	private String match;
	private Filter filter;
	private int start;
	private int end;
	
	public String getMatch() {
		return this.match;
	}
	
	public void setMatch(String match) {
		this.match = match;
	}
	
	public Filter getFilter()
	{
		return this.filter;
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
	public int getStart() {
		return this.start;
	}
	
	public void setBegin(int start) {
		this.start = start;
	}
	
	public int getEnd() {
		return this.end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public Match(String match, Filter filter, int start, int end) {
		this.match = match;
		this.filter = filter;
		this.start = start;
		this.end = end;
	}
	
	public Match(){
	}
	
}
