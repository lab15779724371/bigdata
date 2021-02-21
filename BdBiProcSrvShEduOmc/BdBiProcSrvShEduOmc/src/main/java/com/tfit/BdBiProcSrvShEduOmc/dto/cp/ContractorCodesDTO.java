package com.tfit.BdBiProcSrvShEduOmc.dto.cp;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class ContractorCodesDTO {
	String time;
	List<NameCode> contractorCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getContractorCodes() {
		return contractorCodes;
	}
	public void setContractorCodes(List<NameCode> contractorCodes) {
		this.contractorCodes = contractorCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}