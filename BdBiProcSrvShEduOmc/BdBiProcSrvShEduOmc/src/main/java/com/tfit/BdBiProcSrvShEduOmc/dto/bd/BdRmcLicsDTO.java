package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class BdRmcLicsDTO {
	String time;
	PageInfo pageInfo;
	List<BdRmcLics> bdRmcLics;
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
	public List<BdRmcLics> getBdRmcLics() {
		return bdRmcLics;
	}
	public void setBdRmcLics(List<BdRmcLics> bdRmcLics) {
		this.bdRmcLics = bdRmcLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
