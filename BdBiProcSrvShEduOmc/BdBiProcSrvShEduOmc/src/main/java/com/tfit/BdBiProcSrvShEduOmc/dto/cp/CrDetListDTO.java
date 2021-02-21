package com.tfit.BdBiProcSrvShEduOmc.dto.cp;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class CrDetListDTO {
	String time;
	PageInfo pageInfo;
	List<CrDetList> crDetList;
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
	public List<CrDetList> getCrDetList() {
		return crDetList;
	}
	public void setCrDetList(List<CrDetList> crDetList) {
		this.crDetList = crDetList;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
