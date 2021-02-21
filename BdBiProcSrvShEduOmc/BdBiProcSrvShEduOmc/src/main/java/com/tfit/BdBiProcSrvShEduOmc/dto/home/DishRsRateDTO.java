package com.tfit.BdBiProcSrvShEduOmc.dto.home;

import java.util.List;

public class DishRsRateDTO {
	String time;
	//菜品留样率
	float curRsRate;
	//学校留样率
	float curSchRsRate;
	List<DishRsRate> dishRsRate;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public float getCurRsRate() {
		return curRsRate;
	}
	public void setCurRsRate(float curRsRate) {
		this.curRsRate = curRsRate;
	}
	public List<DishRsRate> getDishRsRate() {
		return dishRsRate;
	}
	public void setDishRsRate(List<DishRsRate> dishRsRate) {
		this.dishRsRate = dishRsRate;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
	public float getCurSchRsRate() {
		return curSchRsRate;
	}
	public void setCurSchRsRate(float curSchRsRate) {
		this.curSchRsRate = curSchRsRate;
	}
	
}
