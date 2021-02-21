package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class LicStatusCodesDTO {
	String time;
	List<NameCode> licStatusCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getLicStatusCodes() {
		return licStatusCodes;
	}
	public void setLicStatusCodes(List<NameCode> licStatusCodes) {
		this.licStatusCodes = licStatusCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
