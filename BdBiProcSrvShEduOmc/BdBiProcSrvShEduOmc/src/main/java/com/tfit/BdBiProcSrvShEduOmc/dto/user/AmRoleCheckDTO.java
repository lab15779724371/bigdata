package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmRoleCheckDTO {
	Integer roleType;               //角色类型，1:监管部门，2:学校
	String roleName;                //角色名称

	public Integer getRoleType() {
		return roleType;
	}

	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}