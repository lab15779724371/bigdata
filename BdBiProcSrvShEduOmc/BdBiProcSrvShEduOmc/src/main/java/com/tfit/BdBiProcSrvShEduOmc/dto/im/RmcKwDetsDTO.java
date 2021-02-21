package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class RmcKwDetsDTO {
	String time;
	PageInfo pageInfo;
	List<RmcKwDets> rmcKwDets;
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
	public List<RmcKwDets> getRmcKwDets() {
		return rmcKwDets;
	}
	public void setRmcKwDets(List<RmcKwDets> rmcKwDets) {
		this.rmcKwDets = rmcKwDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
