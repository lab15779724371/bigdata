package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class DispStatusCodesDTO {
	String time;
	List<NameCode> dispStatusCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getDispStatusCodes() {
		return dispStatusCodes;
	}
	public void setDispStatusCodes(List<NameCode> dispStatusCodes) {
		this.dispStatusCodes = dispStatusCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
