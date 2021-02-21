package com.tfit.BdBiProcSrvShEduOmc.dto.bks;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class BriKitStovePbVidsDTO {
	String time;
	PageInfo pageInfo;
	List<BriKitStovePbVids> briKitStovePbVids;
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
	public List<BriKitStovePbVids> getBriKitStovePbVids() {
		return briKitStovePbVids;
	}
	public void setBriKitStovePbVids(List<BriKitStovePbVids> briKitStovePbVids) {
		this.briKitStovePbVids = briKitStovePbVids;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
