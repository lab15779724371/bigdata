package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WarnRmcLicDetsDTO {
	String time;
	PageInfo pageInfo;
	List<WarnRmcLicDets> warnRmcLicDets;
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
	public List<WarnRmcLicDets> getWarnRmcLicDets() {
		return warnRmcLicDets;
	}
	public void setWarnRmcLicDets(List<WarnRmcLicDets> warnRmcLicDets) {
		this.warnRmcLicDets = warnRmcLicDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
