package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class MatConfirmDetsDTO {
	String time;
	PageInfo pageInfo;
	List<MatConfirmDets> matConfirmDets;
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
	public List<MatConfirmDets> getMatConfirmDets() {
		return matConfirmDets;
	}
	public void setMatConfirmDets(List<MatConfirmDets> matConfirmDets) {
		this.matConfirmDets = matConfirmDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
