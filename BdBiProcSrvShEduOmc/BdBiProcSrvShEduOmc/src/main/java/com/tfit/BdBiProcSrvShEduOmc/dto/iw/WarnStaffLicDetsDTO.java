package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WarnStaffLicDetsDTO {
	String time;
	PageInfo pageInfo;
	List<WarnStaffLicDets> warnStaffLicDets;
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
	public List<WarnStaffLicDets> getWarnStaffLicDets() {
		return warnStaffLicDets;
	}
	public void setWarnStaffLicDets(List<WarnStaffLicDets> warnStaffLicDets) {
		this.warnStaffLicDets = warnStaffLicDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
