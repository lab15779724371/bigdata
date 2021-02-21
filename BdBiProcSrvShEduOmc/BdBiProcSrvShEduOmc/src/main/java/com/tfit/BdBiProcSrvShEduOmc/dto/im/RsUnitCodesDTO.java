package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class RsUnitCodesDTO {
	String time;
	PageInfo pageInfo;
	List<NameCode> rsUnitCodes;
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
	public List<NameCode> getRsUnitCodes() {
		return rsUnitCodes;
	}
	public void setRsUnitCodes(List<NameCode> rsUnitCodes) {
		this.rsUnitCodes = rsUnitCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
