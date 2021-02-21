package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class EtVidAdminVideosDTO {
	String time;
	PageInfo pageInfo;
	List<EtVidAdminVideos> etVidAdminVideos;
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
	public List<EtVidAdminVideos> getEtVidAdminVideos() {
		return etVidAdminVideos;
	}
	public void setEtVidAdminVideos(List<EtVidAdminVideos> etVidAdminVideos) {
		this.etVidAdminVideos = etVidAdminVideos;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
