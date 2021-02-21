package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class EtVidLibEditDTO {
	String time;
	EtVidLibEdit etVidLibEdit;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public EtVidLibEdit getEtVidLibEdit() {
		return etVidLibEdit;
	}
	public void setEtVidLibEdit(EtVidLibEdit etVidLibEdit) {
		this.etVidLibEdit = etVidLibEdit;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
