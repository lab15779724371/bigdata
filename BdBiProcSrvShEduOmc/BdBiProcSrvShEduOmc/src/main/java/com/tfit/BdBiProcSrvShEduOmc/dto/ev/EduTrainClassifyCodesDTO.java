package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class EduTrainClassifyCodesDTO {
	String time;
	List<NameCode> eduTrainClassifyCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getEduTrainClassifyCodes() {
		return eduTrainClassifyCodes;
	}
	public void setEduTrainClassifyCodes(List<NameCode> eduTrainClassifyCodes) {
		this.eduTrainClassifyCodes = eduTrainClassifyCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
