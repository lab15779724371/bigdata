package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class DishExeListDTO {
	String time;
	List<NameCode> dishExeList;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getDishExeList() {
		return dishExeList;
	}
	public void setDishExeList(List<NameCode> dishExeList) {
		this.dishExeList = dishExeList;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
