package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

public class ExpCaMatSupDetsDTO {
	String time;
	ExpCaMatSupDets expCaMatSupDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpCaMatSupDets getExpCaMatSupDets() {
		return expCaMatSupDets;
	}
	public void setExpCaMatSupDets(ExpCaMatSupDets expCaMatSupDets) {
		this.expCaMatSupDets = expCaMatSupDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
