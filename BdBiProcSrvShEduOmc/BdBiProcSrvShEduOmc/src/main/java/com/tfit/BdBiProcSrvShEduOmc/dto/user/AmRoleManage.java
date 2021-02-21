package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmRoleManage {
	String roleId;
	String roleName;
	String roleType;
	String roleDiscrip;
	String creator;
	String createTime;
	int isRelAccount;
	
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getRoleDiscrip() {
		return roleDiscrip;
	}
	public void setRoleDiscrip(String roleDiscrip) {
		this.roleDiscrip = roleDiscrip;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getIsRelAccount() {
		return isRelAccount;
	}
	public void setIsRelAccount(int isRelAccount) {
		this.isRelAccount = isRelAccount;
	}
}
