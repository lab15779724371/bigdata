package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

public class ExpBdSupListDTO {
	String time;
	ExpBdSupList expBdSupList;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpBdSupList getExpBdSupList() {
		return expBdSupList;
	}
	public void setExpBdSupList(ExpBdSupList expBdSupList) {
		this.expBdSupList = expBdSupList;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
