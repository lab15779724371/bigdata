package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmAccountSecDTO {
	String time;
	AmAccountSec amAccountSec;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public AmAccountSec getAmAccountSec() {
		return amAccountSec;
	}
	public void setAmAccountSec(AmAccountSec amAccountSec) {
		this.amAccountSec = amAccountSec;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
