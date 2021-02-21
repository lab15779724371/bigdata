package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpDishRetSamplesDTO {
	String time;
	ExpDishRetSamples expDishRetSamples;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpDishRetSamples getExpDishRetSamples() {
		return expDishRetSamples;
	}
	public void setExpDishRetSamples(ExpDishRetSamples expDishRetSamples) {
		this.expDishRetSamples = expDishRetSamples;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
