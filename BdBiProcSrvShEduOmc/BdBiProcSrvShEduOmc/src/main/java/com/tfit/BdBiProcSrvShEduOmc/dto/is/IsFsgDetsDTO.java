package com.tfit.BdBiProcSrvShEduOmc.dto.is;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class IsFsgDetsDTO {
	String time;
	PageInfo pageInfo;
	List<IsFsgDets> isFsgDets;
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
	public List<IsFsgDets> getIsFsgDets() {
		return isFsgDets;
	}
	public void setIsFsgDets(List<IsFsgDets> isFsgDets) {
		this.isFsgDets = isFsgDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
