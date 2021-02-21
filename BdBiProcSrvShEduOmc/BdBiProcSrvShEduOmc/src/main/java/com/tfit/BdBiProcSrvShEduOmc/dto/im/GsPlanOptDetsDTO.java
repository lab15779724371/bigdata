package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class GsPlanOptDetsDTO {
	String time;
	PageInfo pageInfo;
	List<GsPlanOptDets> gsPlanOptDets;
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
	public List<GsPlanOptDets> getGsPlanOptDets() {
		return gsPlanOptDets;
	}
	public void setGsPlanOptDets(List<GsPlanOptDets> gsPlanOptDets) {
		this.gsPlanOptDets = gsPlanOptDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
