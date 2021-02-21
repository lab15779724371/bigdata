package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class LicTypeCodesDTO {
	String time;
	List<NameCode> licTypeCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getLicTypeCodes() {
		return licTypeCodes;
	}
	public void setLicTypeCodes(List<NameCode> licTypeCodes) {
		this.licTypeCodes = licTypeCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
