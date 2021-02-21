package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

public class ExpWarnRmcLicDetsDTO {
	String time;
	ExpWarnRmcLicDets expWarnRmcLicDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}	
	public ExpWarnRmcLicDets getExpWarnRmcLicDets() {
		return expWarnRmcLicDets;
	}
	public void setExpWarnRmcLicDets(ExpWarnRmcLicDets expWarnRmcLicDets) {
		this.expWarnRmcLicDets = expWarnRmcLicDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
