package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmAccountDetDTO {
	String time;
	AmAccountDet amAccountDet;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public AmAccountDet getAmAccountDet() {
		return amAccountDet;
	}
	public void setAmAccountDet(AmAccountDet amAccountDet) {
		this.amAccountDet = amAccountDet;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
