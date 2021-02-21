package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

public class ExpWarnRmcLicsDTO {
	String time;
	ExpWarnRmcLics expWarnRmcLics;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpWarnRmcLics getExpWarnRmcLics() {
		return expWarnRmcLics;
	}
	public void setExpWarnRmcLics(ExpWarnRmcLics expWarnRmcLics) {
		this.expWarnRmcLics = expWarnRmcLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
