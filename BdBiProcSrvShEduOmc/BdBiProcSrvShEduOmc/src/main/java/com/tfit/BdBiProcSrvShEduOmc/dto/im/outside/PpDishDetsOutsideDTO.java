package com.tfit.BdBiProcSrvShEduOmc.dto.im.outside;

import java.util.List;

public class PpDishDetsOutsideDTO {
	String time;
	List<PpDishDetsOutside> ppDishDetsOutside;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<PpDishDetsOutside> getPpDishDets() {
		return ppDishDetsOutside;
	}
	public void setPpDishDets(List<PpDishDetsOutside> ppDishDetsOutside) {
		this.ppDishDetsOutside = ppDishDetsOutside;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
