package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class SubLevelCodesDTO {
	String time;
	List<NameCode> subLevelCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getSubLevelCodes() {
		return subLevelCodes;
	}
	public void setSubLevelCodes(List<NameCode> subLevelCodes) {
		this.subLevelCodes = subLevelCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
