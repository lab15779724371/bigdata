package com.tfit.BdBiProcSrvShEduOmc.dto.bks;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class BriKitStoveRtVidsDTO {
	String time;
	PageInfo pageInfo;
	List<BriKitStoveRtVids> briKitStoveRtVids;
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
	public List<BriKitStoveRtVids> getBriKitStoveRtVids() {
		return briKitStoveRtVids;
	}
	public void setBriKitStoveRtVids(List<BriKitStoveRtVids> briKitStoveRtVids) {
		this.briKitStoveRtVids = briKitStoveRtVids;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
