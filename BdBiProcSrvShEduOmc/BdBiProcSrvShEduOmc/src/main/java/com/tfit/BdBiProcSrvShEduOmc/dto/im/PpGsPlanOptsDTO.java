package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class PpGsPlanOptsDTO {
	String time;
	PageInfo pageInfo;
	List<PpGsPlanOpts> ppGsPlanOpts;
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
	
	public List<PpGsPlanOpts> getPpGsPlanOpts() {
		return ppGsPlanOpts;
	}
	public void setPpGsPlanOpts(List<PpGsPlanOpts> ppGsPlanOpts) {
		this.ppGsPlanOpts = ppGsPlanOpts;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
