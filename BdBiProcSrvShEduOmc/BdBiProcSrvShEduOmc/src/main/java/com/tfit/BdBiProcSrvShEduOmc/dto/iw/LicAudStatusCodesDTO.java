package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class LicAudStatusCodesDTO {
	String time;
	List<NameCode> licAudStatusCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getLicAudStatusCodes() {
		return licAudStatusCodes;
	}
	public void setLicAudStatusCodes(List<NameCode> licAudStatusCodes) {
		this.licAudStatusCodes = licAudStatusCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
