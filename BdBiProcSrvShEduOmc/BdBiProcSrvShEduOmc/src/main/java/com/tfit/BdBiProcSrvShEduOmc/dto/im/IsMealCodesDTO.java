package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class IsMealCodesDTO {
	String time;
	List<NameCode> isMealCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getIsMealCodes() {
		return isMealCodes;
	}
	public void setIsMealCodes(List<NameCode> isMealCodes) {
		this.isMealCodes = isMealCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
