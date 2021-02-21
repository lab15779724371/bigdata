package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class WarnMaterialOverProDTO {
	String time;
	PageInfo pageInfo;
	List<WarnMaterialOverPro> warnMaterialOverPro;
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
	public List<WarnMaterialOverPro> getWarnMaterialOverPro() {
		return warnMaterialOverPro;
	}
	public void setWarnMaterialOverPro(List<WarnMaterialOverPro> warnMaterialOverPro) {
		this.warnMaterialOverPro = warnMaterialOverPro;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
