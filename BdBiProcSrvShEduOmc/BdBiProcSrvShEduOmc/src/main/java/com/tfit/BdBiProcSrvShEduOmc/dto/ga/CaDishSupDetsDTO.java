package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class CaDishSupDetsDTO {
	String time;
	PageInfo pageInfo;
	List<CaDishSupDets> caDishSupDets;
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
	public List<CaDishSupDets> getCaDishSupDets() {
		return caDishSupDets;
	}
	public void setCaDishSupDets(List<CaDishSupDets> caDishSupDets) {
		this.caDishSupDets = caDishSupDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
