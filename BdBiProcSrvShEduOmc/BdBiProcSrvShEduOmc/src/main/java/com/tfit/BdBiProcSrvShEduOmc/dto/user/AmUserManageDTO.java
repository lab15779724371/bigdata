package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class AmUserManageDTO {
	String time;
	PageInfo pageInfo;	
	List<AmUserManage> amUserManage;
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
	public List<AmUserManage> getAmUserManage() {
		return amUserManage;
	}
	public void setAmUserManage(List<AmUserManage> amUserManage) {
		this.amUserManage = amUserManage;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
