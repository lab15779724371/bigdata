package com.tfit.BdBiProcSrvShEduOmc.dto.pn;

import java.util.List;

public class RbAnnounceBody {
	String receiver;
	String title;
	Integer announceType;
	String annCont;
	List<RbUlAttachment> amInfos;
	Integer isSynMailNotice;
	
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getAnnounceType() {
		return announceType;
	}
	public void setAnnounceType(Integer announceType) {
		this.announceType = announceType;
	}
	public String getAnnCont() {
		return annCont;
	}
	public void setAnnCont(String annCont) {
		this.annCont = annCont;
	}
	public List<RbUlAttachment> getAmInfos() {
		return amInfos;
	}
	public void setAmInfos(List<RbUlAttachment> amInfos) {
		this.amInfos = amInfos;
	}
	public Integer getIsSynMailNotice() {
		return isSynMailNotice;
	}
	public void setIsSynMailNotice(Integer isSynMailNotice) {
		this.isSynMailNotice = isSynMailNotice;
	}
}
