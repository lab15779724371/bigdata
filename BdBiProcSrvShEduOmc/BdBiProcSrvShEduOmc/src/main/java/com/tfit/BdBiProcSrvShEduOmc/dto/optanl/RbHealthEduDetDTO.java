package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

public class RbHealthEduDetDTO {
	String time;
	RbHealthEduDet rbHealthEduDet;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public RbHealthEduDet getRbHealthEduDet() {
		return rbHealthEduDet;
	}
	public void setRbHealthEduDet(RbHealthEduDet rbHealthEduDet) {
		this.rbHealthEduDet = rbHealthEduDet;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
