package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WarnRmcLicsDTO {
	String time;
	PageInfo pageInfo;
	List<WarnRmcLics> warnRmcLics;
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
	public List<WarnRmcLics> getWarnRmcLics() {
		return warnRmcLics;
	}
	public void setWarnRmcLics(List<WarnRmcLics> warnRmcLics) {
		this.warnRmcLics = warnRmcLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
