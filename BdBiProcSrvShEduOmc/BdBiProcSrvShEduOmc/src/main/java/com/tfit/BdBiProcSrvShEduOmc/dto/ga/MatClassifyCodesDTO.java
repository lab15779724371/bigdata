package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class MatClassifyCodesDTO {
	String time;
	List<NameCode> matClassifyCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getMatClassifyCodes() {
		return matClassifyCodes;
	}
	public void setMatClassifyCodes(List<NameCode> matClassifyCodes) {
		this.matClassifyCodes = matClassifyCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
