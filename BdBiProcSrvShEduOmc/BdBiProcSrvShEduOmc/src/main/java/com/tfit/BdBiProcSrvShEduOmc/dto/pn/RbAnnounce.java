package com.tfit.BdBiProcSrvShEduOmc.dto.pn;

public class RbAnnounce {
	String bulletinId;
	String title;
	int announceType;
	String publishDate;
	
	public String getBulletinId() {
		return bulletinId;
	}
	public void setBulletinId(String bulletinId) {
		this.bulletinId = bulletinId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getAnnounceType() {
		return announceType;
	}
	public void setAnnounceType(int announceType) {
		this.announceType = announceType;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
}
