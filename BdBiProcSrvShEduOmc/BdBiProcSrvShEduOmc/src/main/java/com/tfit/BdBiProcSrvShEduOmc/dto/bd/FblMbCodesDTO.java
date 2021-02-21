package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class FblMbCodesDTO {
	String time;
	List<NameCode> fblMbCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getFblMbCodes() {
		return fblMbCodes;
	}
	public void setFblMbCodes(List<NameCode> fblMbCodes) {
		this.fblMbCodes = fblMbCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
