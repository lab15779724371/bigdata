package com.tfit.BdBiProcSrvShEduOmc.dto.pn;

public class SignInBulletinDTO {
	String time;
	SignInBulletin signInBulletin;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public SignInBulletin getSignInBulletin() {
		return signInBulletin;
	}
	public void setSignInBulletin(SignInBulletin signInBulletin) {
		this.signInBulletin = signInBulletin;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
