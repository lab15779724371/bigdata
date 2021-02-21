package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmUserEnableDTO {
	String userName;                  //用户名称
	Integer userEnable;               //用户使能，0:禁用，1:启用
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getUserEnable() {
		return userEnable;
	}
	public void setUserEnable(Integer userEnable) {
		this.userEnable = userEnable;
	}
}
