package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class DishRsDetsDTO {
	String time;
	PageInfo pageInfo;
	List<DishRsDets> dishRsDets;
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
	public List<DishRsDets> getDishRsDets() {
		return dishRsDets;
	}
	public void setDishRsDets(List<DishRsDets> dishRsDets) {
		this.dishRsDets = dishRsDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
