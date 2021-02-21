package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class EtVidAttachmentDTO {
	String time;
	EtVidAttachment etVidAttachment;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public EtVidAttachment getEtVidAttachment() {
		return etVidAttachment;
	}
	public void setEtVidAttachment(EtVidAttachment etVidAttachment) {
		this.etVidAttachment = etVidAttachment;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
