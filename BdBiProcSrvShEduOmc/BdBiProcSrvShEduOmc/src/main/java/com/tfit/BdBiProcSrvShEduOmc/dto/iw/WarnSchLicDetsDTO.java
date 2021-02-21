package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WarnSchLicDetsDTO {
	String time;
	PageInfo pageInfo;
	List<WarnSchLicDets> warnSchLicDets;
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
	public List<WarnSchLicDets> getWarnSchLicDets() {
		return warnSchLicDets;
	}
	public void setWarnSchLicDets(List<WarnSchLicDets> warnSchLicDets) {
		this.warnSchLicDets = warnSchLicDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
