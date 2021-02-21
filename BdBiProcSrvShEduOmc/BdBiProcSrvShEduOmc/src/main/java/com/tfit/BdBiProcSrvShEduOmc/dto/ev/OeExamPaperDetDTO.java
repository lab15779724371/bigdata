package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class OeExamPaperDetDTO {
	String time;
	OeExamPaperDet oeExamPaperDet;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public OeExamPaperDet getOeExamPaperDet() {
		return oeExamPaperDet;
	}
	public void setOeExamPaperDet(OeExamPaperDet oeExamPaperDet) {
		this.oeExamPaperDet = oeExamPaperDet;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
