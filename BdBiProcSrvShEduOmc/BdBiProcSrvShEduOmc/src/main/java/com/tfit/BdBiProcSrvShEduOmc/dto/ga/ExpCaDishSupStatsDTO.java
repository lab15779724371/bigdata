package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

public class ExpCaDishSupStatsDTO {
	String time;
	ExpCaDishSupStats expCaDishSupStats;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpCaDishSupStats getExpCaDishSupStats() {
		return expCaDishSupStats;
	}
	public void setExpCaDishSupStats(ExpCaDishSupStats expCaDishSupStats) {
		this.expCaDishSupStats = expCaDishSupStats;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
