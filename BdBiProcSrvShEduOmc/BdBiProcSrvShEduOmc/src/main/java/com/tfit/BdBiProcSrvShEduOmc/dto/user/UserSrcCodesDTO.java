package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class UserSrcCodesDTO {
	String time;
	List<NameCode> userSrcCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getUserSrcCodes() {
		return userSrcCodes;
	}
	public void setUserSrcCodes(List<NameCode> userSrcCodes) {
		this.userSrcCodes = userSrcCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
