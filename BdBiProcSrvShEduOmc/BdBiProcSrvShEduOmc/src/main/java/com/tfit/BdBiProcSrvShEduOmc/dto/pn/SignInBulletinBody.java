package com.tfit.BdBiProcSrvShEduOmc.dto.pn;

public class SignInBulletinBody {
	String bulletinId;
	String sendUserName;
	String rcvUserName;
	String distName;
	String prefCity;
	String province;
	
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
	public String getDistName() {
		return distName;
	}
	public void setDistName(String distName) {
		this.distName = distName;
	}
	public String getPrefCity() {
		return prefCity;
	}
	public void setPrefCity(String prefCity) {
		this.prefCity = prefCity;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
}
