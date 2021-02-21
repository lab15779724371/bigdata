package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class SchTypeCodesDTO {
	String time;
	List<NameCode> schTypeCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getSchTypeCodes() {
		return schTypeCodes;
	}
	public void setSchTypeCodes(List<NameCode> schTypeCodes) {
		this.schTypeCodes = schTypeCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
