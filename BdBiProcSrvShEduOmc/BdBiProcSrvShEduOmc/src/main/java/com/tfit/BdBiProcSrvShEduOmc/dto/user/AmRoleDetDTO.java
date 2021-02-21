package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmRoleDetDTO {
	String time;
	AmRoleDet amRoleDet;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public AmRoleDet getAmRoleDet() {
		return amRoleDet;
	}
	public void setAmRoleDet(AmRoleDet amRoleDet) {
		this.amRoleDet = amRoleDet;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
