package com.github.mmonkey.Destinations.Parse;

public class Match {
	
	private String title;
	private String content;
	private Filter filter;
	private int start;
	private int end;
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		this.content = content;
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
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getEnd() {
		return this.end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public Match(String content, Filter filter, int start, int end) {
		this.content = content;
		this.filter = filter;
		this.start = start;
		this.end = end;
	}
	
	public Match(){
	}
	
}
