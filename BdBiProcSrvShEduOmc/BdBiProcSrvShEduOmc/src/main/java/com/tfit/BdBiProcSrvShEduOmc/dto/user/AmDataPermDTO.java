package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.util.Node;

public class AmDataPermDTO {
	String time;
	List<Node> amDataPerm;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<Node> getAmDataPerm() {
		return amDataPerm;
	}
	public void setAmDataPerm(List<Node> amDataPerm) {
		this.amDataPerm = amDataPerm;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
