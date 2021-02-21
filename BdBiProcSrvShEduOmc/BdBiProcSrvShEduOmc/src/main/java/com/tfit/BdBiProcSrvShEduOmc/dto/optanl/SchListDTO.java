package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class SchListDTO {
	String time;
	PageInfo pageInfo;
	List<SchList> schList;
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
	public List<SchList> getSchList() {
		return schList;
	}
	public void setSchList(List<SchList> schList) {
		this.schList = schList;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
