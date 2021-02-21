package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.IdLabel;

public class AmL2MenuPermDTO {
	String time;
	List<IdLabel> amL2MenuPerm;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<IdLabel> getAmL2MenuPerm() {
		return amL2MenuPerm;
	}
	public void setAmL2MenuPerm(List<IdLabel> amL2MenuPerm) {
		this.amL2MenuPerm = amL2MenuPerm;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
