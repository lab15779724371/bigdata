package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class ExpAmUserManageDTO {
	String time;
	ExpAmUserManage expAmUserManage;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpAmUserManage getExpAmUserManage() {
		return expAmUserManage;
	}
	public void setExpAmUserManage(ExpAmUserManage expAmUserManage) {
		this.expAmUserManage = expAmUserManage;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
