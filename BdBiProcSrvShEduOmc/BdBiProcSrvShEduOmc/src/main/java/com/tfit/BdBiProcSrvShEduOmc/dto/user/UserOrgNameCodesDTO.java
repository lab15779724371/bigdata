package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class UserOrgNameCodesDTO {
	String time;
	List<NameCode> userOrgNameCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getUserOrgNameCodes() {
		return userOrgNameCodes;
	}
	public void setUserOrgNameCodes(List<NameCode> userOrgNameCodes) {
		this.userOrgNameCodes = userOrgNameCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
