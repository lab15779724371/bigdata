package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class BdSchLicsDTO {
	String time;
	PageInfo pageInfo;
	List<BdSchLics> bdSchLics;
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
	public List<BdSchLics> getBdSchLics() {
		return bdSchLics;
	}
	public void setBdSchLics(List<BdSchLics> bdSchLics) {
		this.bdSchLics = bdSchLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
