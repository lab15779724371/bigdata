package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class RbNoticesDTO {
	String time;
	PageInfo pageInfo;
	List<RbNotices> rbNotices;
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
	public List<RbNotices> getRbNotices() {
		return rbNotices;
	}
	public void setRbNotices(List<RbNotices> rbNotices) {
		this.rbNotices = rbNotices;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
