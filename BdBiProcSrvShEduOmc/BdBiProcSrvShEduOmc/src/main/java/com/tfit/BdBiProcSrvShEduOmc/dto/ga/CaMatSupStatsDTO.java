package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class CaMatSupStatsDTO {
	String time;
	PageInfo pageInfo;
	List<CaMatSupStats> caMatSupStats;
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
	public List<CaMatSupStats> getCaMatSupStats() {
		return caMatSupStats;
	}
	public void setCaMatSupStats(List<CaMatSupStats> caMatSupStats) {
		this.caMatSupStats = caMatSupStats;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
