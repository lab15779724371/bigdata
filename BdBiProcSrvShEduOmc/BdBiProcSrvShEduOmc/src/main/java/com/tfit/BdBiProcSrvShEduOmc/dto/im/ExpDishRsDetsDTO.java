package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpDishRsDetsDTO {
	String time;
	ExpDishRsDets expDishRsDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpDishRsDets getExpDishRsDets() {
		return expDishRsDets;
	}
	public void setExpDishRsDets(ExpDishRsDets expDishRsDets) {
		this.expDishRsDets = expDishRsDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
