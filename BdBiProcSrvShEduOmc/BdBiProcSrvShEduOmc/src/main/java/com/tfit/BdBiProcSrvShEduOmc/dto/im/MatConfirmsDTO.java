package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class MatConfirmsDTO {
	String time;
	PageInfo pageInfo;
	List<MatConfirms> matConfirms;
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
	public List<MatConfirms> getMatConfirms() {
		return matConfirms;
	}
	public void setMatConfirms(List<MatConfirms> matConfirms) {
		this.matConfirms = matConfirms;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
