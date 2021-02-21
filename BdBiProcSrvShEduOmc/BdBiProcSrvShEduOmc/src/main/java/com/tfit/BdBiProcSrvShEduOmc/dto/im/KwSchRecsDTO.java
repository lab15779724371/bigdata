package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class KwSchRecsDTO {
	String time;
	PageInfo pageInfo;
	List<KwSchRecs> kwSchRecs;
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
	public List<KwSchRecs> getKwSchRecs() {
		return kwSchRecs;
	}
	public void setKwSchRecs(List<KwSchRecs> kwSchRecs) {
		this.kwSchRecs = kwSchRecs;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
