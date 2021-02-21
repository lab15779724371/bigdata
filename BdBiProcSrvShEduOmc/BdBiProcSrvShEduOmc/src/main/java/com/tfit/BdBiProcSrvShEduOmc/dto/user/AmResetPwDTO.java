package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmResetPwDTO {
	String userName;                     //用户名
	String oldPassword;                  //密码，经过SHA1算法生成的字符串。
	String newPassword;                  //新密码，经过SHA1算法生成的字符串。
	Integer safeGrade;                   //安全等级，0:弱，1:中，2:强
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public Integer getSafeGrade() {
		return safeGrade;
	}
	public void setSafeGrade(Integer safeGrade) {
		this.safeGrade = safeGrade;
	}
}
