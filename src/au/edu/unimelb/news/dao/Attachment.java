/* Generated by DaoGen version 0.1
 *
 * DO NOT EDIT THIS FILE. This file was automatically
 * generated, any changes made to this fill will be
 * lost if the file is re-generated.
 */
package au.edu.unimelb.news.dao;

import java.lang.Comparable;

public class Attachment implements Comparable<Attachment> {

	private long id = 0;
	private long articleId;
	private String name = "";
	private long size;
	private String diskName = "";

	public long getId() {
		return id;
	}

	public long getArticleId() {
		return articleId;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	public String getDiskName() {
		return diskName;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setArticleId(long articleId) {
		 this.articleId = articleId;
	}

	public void setName(String name) {
		 this.name = name;
	}

	public void setSize(long size) {
		 this.size = size;
	}

	public void setDiskName(String diskName) {
		 this.diskName = diskName;
	}

	public int compareTo(Attachment o) {
		if(articleId!=o.getArticleId()) {
			if(this.articleId < o.getArticleId()) return -1; else return 1;
		}
		if(name.compareTo(o.getName())!=0) {
			return name.compareTo(o.getName());
		}
		if(size!=o.getSize()) {
			if(this.size < o.getSize()) return -1; else return 1;
		}
		if(diskName.compareTo(o.getDiskName())!=0) {
			return diskName.compareTo(o.getDiskName());
		}
		return 0;
	}

}
