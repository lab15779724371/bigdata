package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpGsPlanOptsDTO {
	String time;
	ExpGsPlanOpts expGsPlanOpts;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpGsPlanOpts getExpGsPlanOpts() {
		return expGsPlanOpts;
	}
	public void setExpGsPlanOpts(ExpGsPlanOpts expGsPlanOpts) {
		this.expGsPlanOpts = expGsPlanOpts;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
