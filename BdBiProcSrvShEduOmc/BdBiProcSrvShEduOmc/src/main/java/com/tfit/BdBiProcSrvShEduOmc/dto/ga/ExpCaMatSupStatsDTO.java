package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

public class ExpCaMatSupStatsDTO {
	String time;
	ExpCaMatSupStats expCaMatSupStats;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpCaMatSupStats getExpCaMatSupStats() {
		return expCaMatSupStats;
	}
	public void setExpCaMatSupStats(ExpCaMatSupStats expCaMatSupStats) {
		this.expCaMatSupStats = expCaMatSupStats;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
