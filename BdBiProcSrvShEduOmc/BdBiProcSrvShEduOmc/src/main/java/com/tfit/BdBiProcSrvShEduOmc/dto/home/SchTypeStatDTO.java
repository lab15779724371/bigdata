package com.tfit.BdBiProcSrvShEduOmc.dto.home;

import java.util.List;

public class SchTypeStatDTO {
	String time;
	List<SchTypeNumDTO> schTypeStat;
	long msgId;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<SchTypeNumDTO> getSchTypeStat() {
		return schTypeStat;
	}
	public void setSchTypeStat(List<SchTypeNumDTO> schTypeStat) {
		this.schTypeStat = schTypeStat;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
