package com.tfit.BdBiProcSrvShEduOmc.dto.cp;

public class CrOperationBody {
	String crId;
	Integer cpStatus;
	String contractor;
	String procFeedBack;
	String handleDate;
	
	public String getCrId() {
		return crId;
	}
	public void setCrId(String crId) {
		this.crId = crId;
	}
	public Integer getCpStatus() {
		return cpStatus;
	}
	public void setCpStatus(Integer cpStatus) {
		this.cpStatus = cpStatus;
	}
	public String getContractor() {
		return contractor;
	}
	public void setContractor(String contractor) {
		this.contractor = contractor;
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
