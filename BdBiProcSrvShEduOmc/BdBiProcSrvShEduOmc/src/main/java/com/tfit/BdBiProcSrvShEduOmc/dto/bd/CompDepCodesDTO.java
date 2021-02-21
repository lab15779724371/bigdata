package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class CompDepCodesDTO {
	String time;
	List<NameCode> compDepCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getCompDepCodes() {
		return compDepCodes;
	}
	public void setCompDepCodes(List<NameCode> compDepCodes) {
		this.compDepCodes = compDepCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
