package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class MatCategoryCodesDTO {
	String time;
	List<NameCode> matCategoryCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getMatCategoryCodes() {
		return matCategoryCodes;
	}
	public void setMatCategoryCodes(List<NameCode> matCategoryCodes) {
		this.matCategoryCodes = matCategoryCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
