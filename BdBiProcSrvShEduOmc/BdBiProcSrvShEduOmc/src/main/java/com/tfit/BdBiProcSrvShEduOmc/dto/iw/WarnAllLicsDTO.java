package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WarnAllLicsDTO {
	String time;
	PageInfo pageInfo;
	List<WarnAllLics> warnAllLics;
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
	public List<WarnAllLics> getWarnAllLics() {
		return warnAllLics;
	}
	public void setWarnAllLics(List<WarnAllLics> warnAllLics) {
		this.warnAllLics = warnAllLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
