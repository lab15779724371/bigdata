package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class PpDishDetsDTO {
	String time;
	PageInfo pageInfo;
	List<PpDishDets> ppDishDets;
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
	public List<PpDishDets> getPpDishDets() {
		return ppDishDets;
	}
	public void setPpDishDets(List<PpDishDets> ppDishDets) {
		this.ppDishDets = ppDishDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
