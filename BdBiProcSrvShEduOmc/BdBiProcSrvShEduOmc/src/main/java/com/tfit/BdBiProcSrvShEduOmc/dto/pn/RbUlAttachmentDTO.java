package com.tfit.BdBiProcSrvShEduOmc.dto.pn;

public class RbUlAttachmentDTO {
	String time;
	RbUlAttachment rbUlAttachment;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public RbUlAttachment getRbUlAttachment() {
		return rbUlAttachment;
	}
	public void setRbUlAttachment(RbUlAttachment rbUlAttachment) {
		this.rbUlAttachment = rbUlAttachment;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
