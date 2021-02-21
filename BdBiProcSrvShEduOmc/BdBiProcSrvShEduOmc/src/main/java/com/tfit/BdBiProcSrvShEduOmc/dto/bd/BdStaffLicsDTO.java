package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class BdStaffLicsDTO {
	String time;
	PageInfo pageInfo;
	List<BdStaffLics> bdStaffLics;
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
	public List<BdStaffLics> getBdStaffLics() {
		return bdStaffLics;
	}
	public void setBdStaffLics(List<BdStaffLics> bdStaffLics) {
		this.bdStaffLics = bdStaffLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
