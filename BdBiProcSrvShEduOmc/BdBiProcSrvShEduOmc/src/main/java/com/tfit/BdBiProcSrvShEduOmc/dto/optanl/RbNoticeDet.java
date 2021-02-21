package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

public class RbNoticeDet {
	String bulletinId;
	String title;
	String pubOrg;
	String pubDate;
	int readNum;
	String content;
	String lastBulletinId;
	String lastTitle;
	String nextBulletinId;
	String nextTitle;
	
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
	public int getReadNum() {
		return readNum;
	}
	public void setReadNum(int readNum) {
		this.readNum = readNum;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLastBulletinId() {
		return lastBulletinId;
	}
	public void setLastBulletinId(String lastBulletinId) {
		this.lastBulletinId = lastBulletinId;
	}
	public String getLastTitle() {
		return lastTitle;
	}
	public void setLastTitle(String lastTitle) {
		this.lastTitle = lastTitle;
	}
	public String getNextBulletinId() {
		return nextBulletinId;
	}
	public void setNextBulletinId(String nextBulletinId) {
		this.nextBulletinId = nextBulletinId;
	}
	public String getNextTitle() {
		return nextTitle;
	}
	public void setNextTitle(String nextTitle) {
		this.nextTitle = nextTitle;
	}
}
