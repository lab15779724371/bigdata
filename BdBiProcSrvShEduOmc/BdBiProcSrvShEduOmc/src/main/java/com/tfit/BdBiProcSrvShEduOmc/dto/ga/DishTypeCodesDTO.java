package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class DishTypeCodesDTO {
	String time;
	List<NameCode> dishTypeCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getDishTypeCodes() {
		return dishTypeCodes;
	}
	public void setDishTypeCodes(List<NameCode> dishTypeCodes) {
		this.dishTypeCodes = dishTypeCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
