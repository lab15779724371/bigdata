package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class BdRmcListDTO {
	String time;
	PageInfo pageInfo;
	List<BdRmcList> bdRmcList;
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
	public List<BdRmcList> getBdRmcList() {
		return bdRmcList;
	}
	public void setBdRmcList(List<BdRmcList> bdRmcList) {
		this.bdRmcList = bdRmcList;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
