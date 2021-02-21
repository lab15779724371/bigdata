package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;



public class PpDishUseDetsDTO {
	String time;
	PageInfo pageInfo;
	List<PpDishUseDets> ppDishDets;
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
	public List<PpDishUseDets> getPpDishDets() {
		return ppDishDets;
	}
	public void setPpDishDets(List<PpDishUseDets> ppDishDets) {
		this.ppDishDets = ppDishDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
