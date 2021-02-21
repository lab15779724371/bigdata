package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class EtVidAdminVideos {
	String vidId;
	String uploadDate;
	String vidName;
	int vidCategory;
	String pubPerson;
	String auditPerson;
	String auditDate;
	int vidStatus;
	
	public String getVidId() {
		return vidId;
	}
	public void setVidId(String vidId) {
		this.vidId = vidId;
	}
	public String getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}
	public String getVidName() {
		return vidName;
	}
	public void setVidName(String vidName) {
		this.vidName = vidName;
	}
	public int getVidCategory() {
		return vidCategory;
	}
	public void setVidCategory(int vidCategory) {
		this.vidCategory = vidCategory;
	}
	public String getPubPerson() {
		return pubPerson;
	}
	public void setPubPerson(String pubPerson) {
		this.pubPerson = pubPerson;
	}
	public String getAuditPerson() {
		return auditPerson;
	}
	public void setAuditPerson(String auditPerson) {
		this.auditPerson = auditPerson;
	}
	public String getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(String auditDate) {
		this.auditDate = auditDate;
	}
	public int getVidStatus() {
		return vidStatus;
	}
	public void setVidStatus(int vidStatus) {
		this.vidStatus = vidStatus;
	}
}
