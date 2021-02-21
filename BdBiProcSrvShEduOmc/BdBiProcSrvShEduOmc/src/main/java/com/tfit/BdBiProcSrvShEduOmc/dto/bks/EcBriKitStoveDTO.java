package com.tfit.BdBiProcSrvShEduOmc.dto.bks;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class EcBriKitStoveDTO {
	String time;
	PageInfo pageInfo;
	List<EcBriKitStove> ecBriKitStove;
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
	public List<EcBriKitStove> getEcBriKitStove() {
		return ecBriKitStove;
	}
	public void setEcBriKitStove(List<EcBriKitStove> ecBriKitStove) {
		this.ecBriKitStove = ecBriKitStove;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
