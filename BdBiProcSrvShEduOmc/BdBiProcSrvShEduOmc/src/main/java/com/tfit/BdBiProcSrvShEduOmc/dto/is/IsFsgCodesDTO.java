package com.tfit.BdBiProcSrvShEduOmc.dto.is;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class IsFsgCodesDTO {
	String time;
	List<NameCode> isFsgCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getIsFsgCodes() {
		return isFsgCodes;
	}
	public void setIsFsgCodes(List<NameCode> isFsgCodes) {
		this.isFsgCodes = isFsgCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
