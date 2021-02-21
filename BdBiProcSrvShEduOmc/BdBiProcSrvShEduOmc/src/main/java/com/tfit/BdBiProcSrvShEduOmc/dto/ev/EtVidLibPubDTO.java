package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class EtVidLibPubDTO {
	String time;  
	EtVidLibPub etVidLibPub;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public EtVidLibPub getEtVidLibPub() {
		return etVidLibPub;
	}
	public void setEtVidLibPub(EtVidLibPub etVidLibPub) {
		this.etVidLibPub = etVidLibPub;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
