package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class DishRetSamplesDTO {
	String time;
	PageInfo pageInfo;
	List<DishRetSamples> dishRetSamples;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public List<DishRetSamples> getDishRetSamples() {
		return dishRetSamples;
	}
	public void setDishRetSamples(List<DishRetSamples> dishRetSamples) {
		this.dishRetSamples = dishRetSamples;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
