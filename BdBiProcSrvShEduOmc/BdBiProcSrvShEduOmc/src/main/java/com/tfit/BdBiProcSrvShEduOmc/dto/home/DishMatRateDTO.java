package com.tfit.BdBiProcSrvShEduOmc.dto.home;

import java.util.List;

public class DishMatRateDTO {
	String time;
	float curDishRate;
	float curMatConRate;
	List<DishMatRate> dishMatRate;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public float getCurDishRate() {
		return curDishRate;
	}
	public void setCurDishRate(float curDishRate) {
		this.curDishRate = curDishRate;
	}
	public float getCurMatConRate() {
		return curMatConRate;
	}
	public void setCurMatConRate(float curMatConRate) {
		this.curMatConRate = curMatConRate;
	}
	public List<DishMatRate> getDishMatRate() {
		return dishMatRate;
	}
	public void setDishMatRate(List<DishMatRate> dishMatRate) {
		this.dishMatRate = dishMatRate;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
