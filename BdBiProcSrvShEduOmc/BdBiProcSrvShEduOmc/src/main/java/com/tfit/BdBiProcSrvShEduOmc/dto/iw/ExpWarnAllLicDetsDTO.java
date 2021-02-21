package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

public class ExpWarnAllLicDetsDTO {
	String time;
	ExpWarnAllLicDets expWarnAllLicDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpWarnAllLicDets getExpWarnAllLicDets() {
		return expWarnAllLicDets;
	}
	public void setExpWarnAllLicDets(ExpWarnAllLicDets expWarnAllLicDets) {
		this.expWarnAllLicDets = expWarnAllLicDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
