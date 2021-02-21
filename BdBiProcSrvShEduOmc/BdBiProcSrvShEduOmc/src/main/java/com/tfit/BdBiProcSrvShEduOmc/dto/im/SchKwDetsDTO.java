package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class SchKwDetsDTO {
	String time;
	PageInfo pageInfo;
	List<SchKwDets> schKwDets;
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
	public List<SchKwDets> getSchKwDets() {
		return schKwDets;
	}
	public void setSchKwDets(List<SchKwDets> schKwDets) {
		this.schKwDets = schKwDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
