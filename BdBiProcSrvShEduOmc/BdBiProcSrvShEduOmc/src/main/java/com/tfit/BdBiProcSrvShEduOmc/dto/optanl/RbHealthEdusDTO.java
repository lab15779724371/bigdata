package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class RbHealthEdusDTO {
	String time;
	PageInfo pageInfo;
	List<RbHealthEdus> rbHealthEdus;
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
	public List<RbHealthEdus> getRbHealthEdus() {
		return rbHealthEdus;
	}
	public void setRbHealthEdus(List<RbHealthEdus> rbHealthEdus) {
		this.rbHealthEdus = rbHealthEdus;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
