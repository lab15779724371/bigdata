package com.tfit.BdBiProcSrvShEduOmc.dto.ms;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.NameCode;

public class MailSrvPortCodesDTO {
	String time;
	List<NameCode> mailSrvPortCodes;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public List<NameCode> getMailSrvPortCodes() {
		return mailSrvPortCodes;
	}
	public void setMailSrvPortCodes(List<NameCode> mailSrvPortCodes) {
		this.mailSrvPortCodes = mailSrvPortCodes;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}