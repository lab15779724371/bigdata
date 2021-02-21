package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WarnAllLicDetsDTO {
	String time;
	PageInfo pageInfo;
	List<WarnAllLicDets> warnAllLicDets;
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
	public List<WarnAllLicDets> getWarnAllLicDets() {
		return warnAllLicDets;
	}
	public void setWarnAllLicDets(List<WarnAllLicDets> warnAllLicDets) {
		this.warnAllLicDets = warnAllLicDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
