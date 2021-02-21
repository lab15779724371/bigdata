package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

public class ExpWarnAllLicsDTO {
	String time;
	ExpWarnAllLics expWarnAllLics;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpWarnAllLics getExpWarnAllLics() {
		return expWarnAllLics;
	}
	public void setExpWarnAllLics(ExpWarnAllLics expWarnAllLics) {
		this.expWarnAllLics = expWarnAllLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
