package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class UserStatusCodesDTO {
	String time;
	List<NameCode> userStatusCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getUserStatusCodes() {
		return userStatusCodes;
	}
	public void setUserStatusCodes(List<NameCode> userStatusCodes) {
		this.userStatusCodes = userStatusCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
