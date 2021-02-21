package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class GenBraSchCodesDTO {
	String time;
	List<NameCode> genBraSchCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getGenBraSchCodes() {
		return genBraSchCodes;
	}
	public void setGenBraSchCodes(List<NameCode> genBraSchCodes) {
		this.genBraSchCodes = genBraSchCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
