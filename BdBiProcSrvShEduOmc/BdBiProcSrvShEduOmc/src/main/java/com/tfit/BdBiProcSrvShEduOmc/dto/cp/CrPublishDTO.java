package com.tfit.BdBiProcSrvShEduOmc.dto.cp;

public class CrPublishDTO {
	String time;
	CrPublish crPublish;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public CrPublish getCrPublish() {
		return crPublish;
	}
	public void setCrPublish(CrPublish crPublish) {
		this.crPublish = crPublish;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
