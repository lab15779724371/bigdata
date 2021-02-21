package com.tfit.BdBiProcSrvShEduOmc.dto.cp;

public class CrDetDTO {
	String time;
	CrDet crDet;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public CrDet getCrDet() {
		return crDet;
	}
	public void setCrDet(CrDet crDet) {
		this.crDet = crDet;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
