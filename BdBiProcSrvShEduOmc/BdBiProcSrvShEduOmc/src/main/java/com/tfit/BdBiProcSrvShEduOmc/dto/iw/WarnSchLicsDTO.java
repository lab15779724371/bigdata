package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WarnSchLicsDTO {
	String time;
	PageInfo pageInfo;
	List<WarnSchLics> warnSchLics;
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
	public List<WarnSchLics> getWarnSchLics() {
		return warnSchLics;
	}
	public void setWarnSchLics(List<WarnSchLics> warnSchLics) {
		this.warnSchLics = warnSchLics;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
