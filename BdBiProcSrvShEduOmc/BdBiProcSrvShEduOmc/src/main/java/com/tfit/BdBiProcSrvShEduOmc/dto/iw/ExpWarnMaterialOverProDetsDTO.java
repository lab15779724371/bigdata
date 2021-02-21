package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

public class ExpWarnMaterialOverProDetsDTO {
	String time;
	ExpWarnAllLicDets expWarnMaterialOverProDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
	public ExpWarnAllLicDets getExpWarnMaterialOverProDets() {
		return expWarnMaterialOverProDets;
	}
	public void setExpWarnMaterialOverProDets(ExpWarnAllLicDets expWarnMaterialOverProDets) {
		this.expWarnMaterialOverProDets = expWarnMaterialOverProDets;
	}
	
	
	
}
