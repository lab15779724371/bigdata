package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WasteOilDetsDTO {
	String time;
	PageInfo pageInfo;
	List<WasteOilDets> wasteOilDets;
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
	public List<WasteOilDets> getWasteOilDets() {
		return wasteOilDets;
	}
	public void setWasteOilDets(List<WasteOilDets> wasteOilDets) {
		this.wasteOilDets = wasteOilDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
