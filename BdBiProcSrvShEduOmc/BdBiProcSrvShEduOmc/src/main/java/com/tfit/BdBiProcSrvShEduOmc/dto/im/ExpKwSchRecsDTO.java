package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpKwSchRecsDTO {
	String time;
	ExpKwSchRecs expKwSchRecs;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpKwSchRecs getExpKwSchRecs() {
		return expKwSchRecs;
	}
	public void setExpKwSchRecs(ExpKwSchRecs expKwSchRecs) {
		this.expKwSchRecs = expKwSchRecs;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
