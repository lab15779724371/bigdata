package com.tfit.BdBiProcSrvShEduOmc.dto.cp;

public class CrOperationDTO {
	String time;
	CrOperation crOperation;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public CrOperation getCrOperation() {
		return crOperation;
	}
	public void setCrOperation(CrOperation crOperation) {
		this.crOperation = crOperation;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
