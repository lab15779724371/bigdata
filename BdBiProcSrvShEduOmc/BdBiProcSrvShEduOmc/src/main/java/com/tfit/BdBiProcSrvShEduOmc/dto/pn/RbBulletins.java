package  com.tfit.BdBiProcSrvShEduOmc.dto.pn;

public class RbBulletins {
	String bulletinId;
	int announceType;
	String pubOrg;
	String publisher;
	String title;
	String rcvOrgName;
	String pubDate;
	int amFlag;
	int readFlag;
	
	public int getAnnounceType() {
		return announceType;
	}
	public void setAnnounceType(int announceType) {
		this.announceType = announceType;
	}
	public String getBulletinId() {
		return bulletinId;
	}
	public void setBulletinId(String bulletinId) {
		this.bulletinId = bulletinId;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRcvOrgName() {
		return rcvOrgName;
	}
	public void setRcvOrgName(String rcvOrgName) {
		this.rcvOrgName = rcvOrgName;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public int getAmFlag() {
		return amFlag;
	}
	public void setAmFlag(int amFlag) {
		this.amFlag = amFlag;
	}
	public int getReadFlag() {
		return readFlag;
	}
	public void setReadFlag(int readFlag) {
		this.readFlag = readFlag;
	}
}
