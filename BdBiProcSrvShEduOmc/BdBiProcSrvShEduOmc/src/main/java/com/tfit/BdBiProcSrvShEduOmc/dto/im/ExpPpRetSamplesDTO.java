package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpPpRetSamplesDTO {
	String time;
	ExpPpRetSamples expPpRetSamples;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
	public ExpPpRetSamples getExpPpRetSamples() {
		return expPpRetSamples;
	}
	public void setExpPpRetSamples(ExpPpRetSamples expPpRetSamples) {
		this.expPpRetSamples = expPpRetSamples;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
