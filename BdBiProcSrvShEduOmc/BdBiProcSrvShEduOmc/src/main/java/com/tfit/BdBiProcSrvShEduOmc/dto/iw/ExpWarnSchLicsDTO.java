package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

public class ExpWarnSchLicsDTO {
	String time;
	ExpWarnSchLics expWarnSchLics;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpWarnSchLics getExpWarnSchLics() {
		return expWarnSchLics;
	}
	public void setExpWarnSchLics(ExpWarnSchLics expWarnSchLics) {
		this.expWarnSchLics = expWarnSchLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
