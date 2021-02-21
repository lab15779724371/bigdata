package com.tfit.BdBiProcSrvShEduOmc.dto.home;

import java.util.List;

public class LicWarnProcRateDTO {
	String time;
	float curProcRate;
	List<LicWarnProcRate> licWarnProcRate;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public float getCurProcRate() {
		return curProcRate;
	}
	public void setCurProcRate(float curProcRate) {
		this.curProcRate = curProcRate;
	}
	public List<LicWarnProcRate> getLicWarnProcRate() {
		return licWarnProcRate;
	}
	public void setLicWarnProcRate(List<LicWarnProcRate> licWarnProcRate) {
		this.licWarnProcRate = licWarnProcRate;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
