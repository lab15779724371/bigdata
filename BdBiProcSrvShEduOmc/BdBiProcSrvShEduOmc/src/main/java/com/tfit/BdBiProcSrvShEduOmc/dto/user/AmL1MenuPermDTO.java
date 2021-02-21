package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.IdLabel;

public class AmL1MenuPermDTO {
	String time;
	List<IdLabel> amL1MenuPerm;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<IdLabel> getAmL1MenuPerm() {
		return amL1MenuPerm;
	}
	public void setAmL1MenuPerm(List<IdLabel> amL1MenuPerm) {
		this.amL1MenuPerm = amL1MenuPerm;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
