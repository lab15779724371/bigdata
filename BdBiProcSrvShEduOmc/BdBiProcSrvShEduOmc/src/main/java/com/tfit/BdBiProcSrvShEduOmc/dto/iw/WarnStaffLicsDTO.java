package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WarnStaffLicsDTO {
	String time;
	PageInfo pageInfo;
	List<WarnStaffLics> warnStaffLics;
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
	public List<WarnStaffLics> getWarnStaffLics() {
		return warnStaffLics;
	}
	public void setWarnStaffLics(List<WarnStaffLics> warnStaffLics) {
		this.warnStaffLics = warnStaffLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
