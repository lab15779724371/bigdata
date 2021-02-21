package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class RecUnitCodesDTO {
	String time;
	PageInfo pageInfo;
	List<NameCode> recUnitCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public List<NameCode> getRecUnitCodes() {
		return recUnitCodes;
	}
	public void setRecUnitCodes(List<NameCode> recUnitCodes) {
		this.recUnitCodes = recUnitCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
