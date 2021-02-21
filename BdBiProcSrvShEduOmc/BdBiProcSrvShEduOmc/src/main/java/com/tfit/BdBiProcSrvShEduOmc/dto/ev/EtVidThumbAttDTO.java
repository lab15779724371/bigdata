package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class EtVidThumbAttDTO {
	String time;
	EtVidThumbAtt etVidThumbAtt;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public EtVidThumbAtt getEtVidThumbAtt() {
		return etVidThumbAtt;
	}
	public void setEtVidThumbAtt(EtVidThumbAtt etVidThumbAtt) {
		this.etVidThumbAtt = etVidThumbAtt;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
