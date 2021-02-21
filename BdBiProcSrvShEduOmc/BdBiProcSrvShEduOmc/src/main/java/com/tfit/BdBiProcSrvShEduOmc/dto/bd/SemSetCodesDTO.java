package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class SemSetCodesDTO {
	String time;
	List<NameCode> semSetCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getSemSetCodes() {
		return semSetCodes;
	}
	public void setSemSetCodes(List<NameCode> semSetCodes) {
		this.semSetCodes = semSetCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
