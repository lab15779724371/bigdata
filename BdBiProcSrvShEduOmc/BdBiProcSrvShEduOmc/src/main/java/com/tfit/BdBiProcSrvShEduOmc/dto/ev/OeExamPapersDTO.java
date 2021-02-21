package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;

public class OeExamPapersDTO {
	String time;
	PageInfo pageInfo;
	List<OeExamPapers>oeExamPapers;
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
	public List<OeExamPapers> getOeExamPapers() {
		return oeExamPapers;
	}
	public void setOeExamPapers(List<OeExamPapers> oeExamPapers) {
		this.oeExamPapers = oeExamPapers;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
