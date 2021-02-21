package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpSumDataDetsDTO {
	String time;
	ExpSumDataDets expSumDataDets;
	long msgId;
	 
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpSumDataDets getExpSumDataDets() {
		return expSumDataDets;
	}
	public void setExpSumDataDets(ExpSumDataDets expSumDataDets) {
		this.expSumDataDets = expSumDataDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
