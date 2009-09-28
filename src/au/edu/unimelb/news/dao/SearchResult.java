/* Generated by DaoGen version 0.1
 *
 * DO NOT EDIT THIS FILE. This file was automatically
 * generated, any changes made to this fill will be
 * lost if the file is re-generated.
 */
package au.edu.unimelb.news.dao;

import java.lang.Comparable;

public class SearchResult implements Comparable<SearchResult> {

	private long id;
	private String name = "";
	private String status = "";
	private String introduction = "";
	private long rank;
	private boolean published;

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	public String getIntroduction() {
		return introduction;
	}

	public long getRank() {
		return rank;
	}

	public boolean isPublished() {
		return published;
	}

	public void setId(long id) {
		 this.id = id;
	}

	public void setName(String name) {
		 this.name = name;
	}

	public void setStatus(String status) {
		 this.status = status;
	}

	public void setIntroduction(String introduction) {
		 this.introduction = introduction;
	}

	public void setRank(long rank) {
		 this.rank = rank;
	}

	public void setPublished(boolean published) {
		 this.published = published;
	}

	public int compareTo(SearchResult o) {
		if(id!=o.getId()) {
			if(this.id < o.getId()) return -1; else return 1;
		}
		if(name.compareTo(o.getName())!=0) {
			return name.compareTo(o.getName());
		}
		if(status.compareTo(o.getStatus())!=0) {
			return status.compareTo(o.getStatus());
		}
		if(introduction.compareTo(o.getIntroduction())!=0) {
			return introduction.compareTo(o.getIntroduction());
		}
		if(rank!=o.getRank()) {
			if(this.rank < o.getRank()) return -1; else return 1;
		}
		if(published!=o.published) {
			if(this.published) return -1; else return 1;
		}
		return 0;
	}

}
