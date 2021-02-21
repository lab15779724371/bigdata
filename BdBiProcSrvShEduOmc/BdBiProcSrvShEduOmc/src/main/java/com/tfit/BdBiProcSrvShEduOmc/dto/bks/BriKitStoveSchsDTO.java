package com.tfit.BdBiProcSrvShEduOmc.dto.bks;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.util.Node;

public class BriKitStoveSchsDTO {
	String time;
	List<Node> briKitStoveSchs;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<Node> getBriKitStoveSchs() {
		return briKitStoveSchs;
	}
	public void setBriKitStoveSchs(List<Node> briKitStoveSchs) {
		this.briKitStoveSchs = briKitStoveSchs;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
