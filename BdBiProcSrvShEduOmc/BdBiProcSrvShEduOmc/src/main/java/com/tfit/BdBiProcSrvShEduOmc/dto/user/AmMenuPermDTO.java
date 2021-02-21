package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.util.Node;

public class AmMenuPermDTO {
	String time;
	List<Node> amMenuPerm;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<Node> getAmMenuPerm() {
		return amMenuPerm;
	}
	public void setAmMenuPerm(List<Node> amMenuPerm) {
		this.amMenuPerm = amMenuPerm;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
