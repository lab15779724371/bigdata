package com.tfit.BdBiProcSrvShEduOmc.dto.ms;

public class MailSrvDetDTO {
	String time;
	MailSrvDet mailSrvDet;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public MailSrvDet getMailSrvDet() {
		return mailSrvDet;
	}
	public void setMailSrvDet(MailSrvDet mailSrvDet) {
		this.mailSrvDet = mailSrvDet;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
