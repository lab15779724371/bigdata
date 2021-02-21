package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.IdLabel;

public class AmL3MenuPermDTO {
	String time;
	List<IdLabel> amL3MenuPerm;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<IdLabel> getAmL3MenuPerm() {
		return amL3MenuPerm;
	}
	public void setAmL3MenuPerm(List<IdLabel> amL3MenuPerm) {
		this.amL3MenuPerm = amL3MenuPerm;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
