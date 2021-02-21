package com.tfit.BdBiProcSrvShEduOmc.dto.ev;

public class OeSubAnsDTO {
	String time;
	OeSubAns oeSubAns;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public OeSubAns getOeSubAns() {
		return oeSubAns;
	}
	public void setOeSubAns(OeSubAns oeSubAns) {
		this.oeSubAns = oeSubAns;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
