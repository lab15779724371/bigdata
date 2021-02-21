package com.tfit.BdBiProcSrvShEduOmc.dto.home;

public class HomeInfoStatDTO {
	String time;
	HomeInfoStat homeInfoStat;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public HomeInfoStat getHomeInfoStat() {
		return homeInfoStat;
	}
	public void setHomeInfoStat(HomeInfoStat homeInfoStat) {
		this.homeInfoStat = homeInfoStat;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
