package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpDishSumInfoDTO {
	String time;
	ExpDishSumInfo expDishSumInfo;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public ExpDishSumInfo getExpDishSumInfo() {
		return expDishSumInfo;
	}
	public void setExpDishSumInfo(ExpDishSumInfo expDishSumInfo) {
		this.expDishSumInfo = expDishSumInfo;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
