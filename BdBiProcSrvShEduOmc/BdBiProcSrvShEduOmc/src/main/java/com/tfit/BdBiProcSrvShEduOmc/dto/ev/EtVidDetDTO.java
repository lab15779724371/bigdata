package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class EtVidDetDTO {
	String time;
	EtVidDet etVidDet;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public EtVidDet getEtVidDet() {
		return etVidDet;
	}
	public void setEtVidDet(EtVidDet etVidDet) {
		this.etVidDet = etVidDet;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
