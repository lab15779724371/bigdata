package  com.tfit.BdBiProcSrvShEduOmc.dto.pn;

import java.util.List;

public class RbBulletinDet {
	String bulletinId;
	int announceType;
	String title;
	String pubOrg;
	String publisher;
	String pubDate;
	String content;	
	int amFlag;
	String readUsers;
	String unreadUsers;
	List<RbUlAttachment> amInfos;
	String lastBulletinId;
	String lastTitle;
	String nextBulletinId;
	String nextTitle;
	SignInInfo signInInfo;
	
	public String getBulletinId() {
		return bulletinId;
	}
	public void setBulletinId(String bulletinId) {
		this.bulletinId = bulletinId;
	}	
	public int getAnnounceType() {
		return announceType;
	}
	public void setAnnounceType(int announceType) {
		this.announceType = announceType;
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
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getAmFlag() {
		return amFlag;
	}
	public void setAmFlag(int amFlag) {
		this.amFlag = amFlag;
	}
	public String getReadUsers() {
		return readUsers;
	}
	public void setReadUsers(String readUsers) {
		this.readUsers = readUsers;
	}
	public String getUnreadUsers() {
		return unreadUsers;
	}
	public void setUnreadUsers(String unreadUsers) {
		this.unreadUsers = unreadUsers;
	}
	public List<RbUlAttachment> getAmInfos() {
		return amInfos;
	}
	public void setAmInfos(List<RbUlAttachment> amInfos) {
		this.amInfos = amInfos;
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
	public SignInInfo getSignInInfo() {
		return signInInfo;
	}
	public void setSignInInfo(SignInInfo signInInfo) {
		this.signInInfo = signInInfo;
	}
}
