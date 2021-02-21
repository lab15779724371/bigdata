package com.tfit.BdBiProcSrvShEduOmc.dto.cp;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class CrProcStatusCodesDTO {
	String time;
	List<NameCode> crProcStatusCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getCrProcStatusCodes() {
		return crProcStatusCodes;
	}
	public void setCrProcStatusCodes(List<NameCode> crProcStatusCodes) {
		this.crProcStatusCodes = crProcStatusCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}