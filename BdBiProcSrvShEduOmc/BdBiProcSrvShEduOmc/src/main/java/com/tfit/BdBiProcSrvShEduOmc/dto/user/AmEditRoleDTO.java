package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmEditRoleDTO {
	String roleId;                      //角色ID
	Integer roleType;                   //角色类型，1:监管部门，2:学校
	String roleName;                    //角色名称
	String remark;                      //备注
	
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
