package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

public class ExpWarnStaffLicsDTO {
	String time;
	ExpWarnStaffLics expWarnStaffLics;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpWarnStaffLics getExpWarnStaffLics() {
		return expWarnStaffLics;
	}
	public void setExpWarnStaffLics(ExpWarnStaffLics expWarnStaffLics) {
		this.expWarnStaffLics = expWarnStaffLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
