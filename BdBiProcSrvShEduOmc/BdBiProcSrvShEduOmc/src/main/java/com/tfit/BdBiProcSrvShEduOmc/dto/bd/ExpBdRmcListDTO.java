package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

public class ExpBdRmcListDTO {
	String time;
	ExpBdRmcList expBdRmcList;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpBdRmcList getExpBdRmcList() {
		return expBdRmcList;
	}
	public void setExpBdRmcList(ExpBdRmcList expBdRmcList) {
		this.expBdRmcList = expBdRmcList;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
