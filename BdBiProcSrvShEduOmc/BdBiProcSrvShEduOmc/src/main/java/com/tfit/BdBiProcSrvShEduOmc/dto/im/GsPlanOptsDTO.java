package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class GsPlanOptsDTO {
	String time;
	PageInfo pageInfo;
	List<GsPlanOpts> gsPlanOpts;
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
	public List<GsPlanOpts> getGsPlanOpts() {
		return gsPlanOpts;
	}
	public void setGsPlanOpts(List<GsPlanOpts> gsPlanOpts) {
		this.gsPlanOpts = gsPlanOpts;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
