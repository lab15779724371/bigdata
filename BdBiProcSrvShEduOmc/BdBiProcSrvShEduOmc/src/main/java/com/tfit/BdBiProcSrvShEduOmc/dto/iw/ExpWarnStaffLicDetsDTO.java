package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

public class ExpWarnStaffLicDetsDTO {
	String time;
	ExpWarnStaffLicDets expWarnStaffLicDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpWarnStaffLicDets getExpWarnStaffLicDets() {
		return expWarnStaffLicDets;
	}
	public void setExpWarnStaffLicDets(ExpWarnStaffLicDets expWarnStaffLicDets) {
		this.expWarnStaffLicDets = expWarnStaffLicDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
