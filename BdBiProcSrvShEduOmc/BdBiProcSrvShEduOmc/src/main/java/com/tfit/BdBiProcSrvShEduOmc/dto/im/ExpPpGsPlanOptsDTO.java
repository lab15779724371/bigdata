package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpPpGsPlanOptsDTO {
	String time;
	ExpPpGsPlanOpts expPpGsPlanOpts;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public ExpPpGsPlanOpts getExpPpGsPlanOpts() {
		return expPpGsPlanOpts;
	}
	public void setExpPpGsPlanOpts(ExpPpGsPlanOpts expPpGsPlanOpts) {
		this.expPpGsPlanOpts = expPpGsPlanOpts;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
