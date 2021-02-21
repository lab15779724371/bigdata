package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmRoleDet {
	String roleName;
	int roleType;
	String roleDiscrip;
	String creator;
	String createTime;
	
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public int getRoleType() {
		return roleType;
	}
	public void setRoleType(int roleType) {
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
}
