package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class AccountTypeCodesDTO {
	String time;
	List<NameCode> accountTypeCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getAccountTypeCodes() {
		return accountTypeCodes;
	}
	public void setAccountTypeCodes(List<NameCode> accountTypeCodes) {
		this.accountTypeCodes = accountTypeCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
