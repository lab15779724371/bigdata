package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

public class ExpWarnSchLicDetsDTO {
	String time;
	ExpWarnSchLicDets expWarnSchLicDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpWarnSchLicDets getExpWarnSchLicDets() {
		return expWarnSchLicDets;
	}
	public void setExpWarnSchLicDets(ExpWarnSchLicDets expWarnSchLicDets) {
		this.expWarnSchLicDets = expWarnSchLicDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
