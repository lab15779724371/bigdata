package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

public class ExpBdSchListDTO {
	String time;
	ExpBdSchList expBdSchList;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpBdSchList getExpBdSchList() {
		return expBdSchList;
	}
	public void setExpBdSchList(ExpBdSchList expBdSchList) {
		this.expBdSchList = expBdSchList;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
