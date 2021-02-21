package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

public class RbNotices {
	String bulletinId;
	String title;
	String sendee;
	String pubOrg;
	String pubDate;
	
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
	public String getSendee() {
		return sendee;
	}
	public void setSendee(String sendee) {
		this.sendee = sendee;
	}
	public String getPubOrg() {
		return pubOrg;
	}
	public void setPubOrg(String pubOrg) {
		this.pubOrg = pubOrg;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
}
