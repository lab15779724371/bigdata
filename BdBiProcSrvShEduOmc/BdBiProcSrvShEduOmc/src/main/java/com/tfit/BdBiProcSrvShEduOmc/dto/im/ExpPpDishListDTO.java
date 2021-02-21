package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpPpDishListDTO {
	String time;
	ExpPpDishList expPpDishList;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpPpDishList getExpPpDishList() {
		return expPpDishList;
	}
	public void setExpPpDishList(ExpPpDishList expPpDishList) {
		this.expPpDishList = expPpDishList;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}