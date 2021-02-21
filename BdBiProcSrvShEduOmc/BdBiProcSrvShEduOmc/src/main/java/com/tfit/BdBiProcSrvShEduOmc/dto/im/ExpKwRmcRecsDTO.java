package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpKwRmcRecsDTO {
	String time;
	ExpKwRmcRecs expKwRmcRecs;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpKwRmcRecs getExpKwRmcRecs() {
		return expKwRmcRecs;
	}
	public void setExpKwRmcRecs(ExpKwRmcRecs expKwRmcRecs) {
		this.expKwRmcRecs = expKwRmcRecs;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
