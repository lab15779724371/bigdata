package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

public class ExpWarnMaterialOverProDTO {
	String time;
	ExpWarnAllLics expWarnMaterialOverPro;
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
	public ExpWarnAllLics getExpWarnMaterialOverPro() {
		return expWarnMaterialOverPro;
	}
	public void setExpWarnMaterialOverPro(ExpWarnAllLics expWarnMaterialOverPro) {
		this.expWarnMaterialOverPro = expWarnMaterialOverPro;
	}
	
	
}
