package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class DispTypeCodesDTO {
	String time;
	List<NameCode> dispTypeCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getDispTypeCodes() {
		return dispTypeCodes;
	}
	public void setDispTypeCodes(List<NameCode> dispTypeCodes) {
		this.dispTypeCodes = dispTypeCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
