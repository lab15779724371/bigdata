package com.tfit.BdBiProcSrvShEduOmc.dto.cp;

public class CrDet {
	String crId;
	String ppName;
	String title;
	String subDate;
	String crContent;
	String complainant;
	String telNumber;
	String contractor;
	int procStatus;
	String procFeedBack;
	String handleDate;
	
	public String getCrId() {
		return crId;
	}
	public void setCrId(String crId) {
		this.crId = crId;
	}
	public String getPpName() {
		return ppName;
	}
	public void setPpName(String ppName) {
		this.ppName = ppName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubDate() {
		return subDate;
	}
	public void setSubDate(String subDate) {
		this.subDate = subDate;
	}
	public String getCrContent() {
		return crContent;
	}
	public void setCrContent(String crContent) {
		this.crContent = crContent;
	}
	public String getComplainant() {
		return complainant;
	}
	public void setComplainant(String complainant) {
		this.complainant = complainant;
	}
	public String getTelNumber() {
		return telNumber;
	}
	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}
	public String getContractor() {
		return contractor;
	}
	public void setContractor(String contractor) {
		this.contractor = contractor;
	}
	public int getProcStatus() {
		return procStatus;
	}
	public void setProcStatus(int procStatus) {
		this.procStatus = procStatus;
	}
	public String getProcFeedBack() {
		return procFeedBack;
	}
	public void setProcFeedBack(String procFeedBack) {
		this.procFeedBack = procFeedBack;
	}
	public String getHandleDate() {
		return handleDate;
	}
	public void setHandleDate(String handleDate) {
		this.handleDate = handleDate;
	}
}
