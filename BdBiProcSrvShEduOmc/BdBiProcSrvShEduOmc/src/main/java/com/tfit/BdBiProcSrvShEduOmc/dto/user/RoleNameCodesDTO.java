package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class RoleNameCodesDTO {
	String time;
	List<NameCode> roleNameCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getRoleNameCodes() {
		return roleNameCodes;
	}
	public void setRoleNameCodes(List<NameCode> roleNameCodes) {
		this.roleNameCodes = roleNameCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
