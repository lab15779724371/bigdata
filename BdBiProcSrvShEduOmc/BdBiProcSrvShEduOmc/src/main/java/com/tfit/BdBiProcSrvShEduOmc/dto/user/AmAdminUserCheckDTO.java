package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmAdminUserCheckDTO {
	Integer roleType;              //角色类型，1:监管部门，2:学校
	String orgId;                  //单位ID
	
	public Integer getRoleType() {
		return roleType;
	}
	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
}
