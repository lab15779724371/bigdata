package com.tfit.BdBiProcSrvShEduOmc.dto.search;

public class ExpSearchBySchoolDTO {
	String time;
	ExpSearchBySchool expSearchBySchool;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public ExpSearchBySchool getExpSearchBySchool() {
		return expSearchBySchool;
	}
	public void setExpSearchBySchool(ExpSearchBySchool expSearchBySchool) {
		this.expSearchBySchool = expSearchBySchool;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
