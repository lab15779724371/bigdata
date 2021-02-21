package com.tfit.BdBiProcSrvShEduOmc.dto.ms;

public class SaveMailSrvInfoDTO {
	String time;
	SaveMailSrvInfo saveMailSrvInfo;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public SaveMailSrvInfo getSaveMailSrvInfo() {
		return saveMailSrvInfo;
	}
	public void setSaveMailSrvInfo(SaveMailSrvInfo saveMailSrvInfo) {
		this.saveMailSrvInfo = saveMailSrvInfo;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
