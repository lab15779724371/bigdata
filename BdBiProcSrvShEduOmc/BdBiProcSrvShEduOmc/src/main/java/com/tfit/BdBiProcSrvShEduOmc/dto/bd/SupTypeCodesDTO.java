package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class SupTypeCodesDTO {
	String time;
	List<NameCode> supTypeCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getSupTypeCodes() {
		return supTypeCodes;
	}
	public void setSupTypeCodes(List<NameCode> supTypeCodes) {
		this.supTypeCodes = supTypeCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
