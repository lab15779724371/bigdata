package com.tfit.BdBiProcSrvShEduOmc.dto.cp;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class CrListDTO {
	String time;
	PageInfo pageInfo;
	List<CrList> crList;
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
	public List<CrList> getCrList() {
		return crList;
	}
	public void setCrList(List<CrList> crList) {
		this.crList = crList;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
