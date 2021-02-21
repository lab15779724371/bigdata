package com.tfit.BdBiProcSrvShEduOmc.dto.home;

import java.util.List;

public class AnnouncesDTO {
	String time;
	List<Announces> announces;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<Announces> getAnnounces() {
		return announces;
	}
	public void setAnnounces(List<Announces> announces) {
		this.announces = announces;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
