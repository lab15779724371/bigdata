package com.tfit.BdBiProcSrvShEduOmc.dto.pn;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class AnnTypeCodesDTO {
	String time;
	List<NameCode> annTypeCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getAnnTypeCodes() {
		return annTypeCodes;
	}
	public void setAnnTypeCodes(List<NameCode> annTypeCodes) {
		this.annTypeCodes = annTypeCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
