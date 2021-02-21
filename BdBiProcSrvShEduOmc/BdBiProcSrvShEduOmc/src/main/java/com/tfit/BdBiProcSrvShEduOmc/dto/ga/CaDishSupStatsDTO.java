package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class CaDishSupStatsDTO {
	String time;
	PageInfo pageInfo;
	List<CaDishSupStats> caDishSupStats;
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
	public List<CaDishSupStats> getCaDishSupStats() {
		return caDishSupStats;
	}
	public void setCaDishSupStats(List<CaDishSupStats> caDishSupStats) {
		this.caDishSupStats = caDishSupStats;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
