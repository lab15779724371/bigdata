package com.tfit.BdBiProcSrvShEduOmc.dto.pn;

public class SignInBulletin {
	String bulletinId;
	String sendUserName;
	String rcvUserName;
	int signFlag;
	
	public String getBulletinId() {
		return bulletinId;
	}
	public void setBulletinId(String bulletinId) {
		this.bulletinId = bulletinId;
	}
	public String getSendUserName() {
		return sendUserName;
	}
	public void setSendUserName(String sendUserName) {
		this.sendUserName = sendUserName;
	}
	public String getRcvUserName() {
		return rcvUserName;
	}
	public void setRcvUserName(String rcvUserName) {
		this.rcvUserName = rcvUserName;
	}
	public int getSignFlag() {
		return signFlag;
	}
	public void setSignFlag(int signFlag) {
		this.signFlag = signFlag;
	}
}
