package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

public class AmEditAccountDTO {
	Integer roleType;                      //角色类型，1:监管部门，2:学校
	String userOrgId;                      //用户单位ID
	Integer isAdmin;                       //是否为管理员，0:否，1:是
	String userName;                       //用户名称
	String password;                       //密码
	String confirmPw;                      //确认密码
	String fullName;                       //姓名
	String roleName;                       //角色名称
	String mobPhone;                       //手机号码
	String email;                          //邮箱
	String fixPhone;                       //固定电话
	String fax;                            //传真
	List<String> amDataPerm;               //数据权限,元素为学校ID
	List<String> amL1MenuPerm;             //一级菜单权限ID集合
	List<String> amL2MenuPerm;             //二级菜单权限ID集合
	List<String> amL3MenuPerm;             //三级菜单权限ID（按钮）集合
	
	public Integer getRoleType() {
		return roleType;
	}
	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}
	public String getUserOrgId() {
		return userOrgId;
	}
	public void setUserOrgId(String userOrgId) {
		this.userOrgId = userOrgId;
	}
	public Integer getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPw() {
		return confirmPw;
	}
	public void setConfirmPw(String confirmPw) {
		this.confirmPw = confirmPw;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getMobPhone() {
		return mobPhone;
	}
	public void setMobPhone(String mobPhone) {
		this.mobPhone = mobPhone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}	
	public String getFixPhone() {
		return fixPhone;
	}
	public void setFixPhone(String fixPhone) {
		this.fixPhone = fixPhone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public List<String> getAmDataPerm() {
		return amDataPerm;
	}
	public void setAmDataPerm(List<String> amDataPerm) {
		this.amDataPerm = amDataPerm;
	}
	public List<String> getAmL1MenuPerm() {
		return amL1MenuPerm;
	}
	public void setAmL1MenuPerm(List<String> amL1MenuPerm) {
		this.amL1MenuPerm = amL1MenuPerm;
	}
	public List<String> getAmL2MenuPerm() {
		return amL2MenuPerm;
	}
	public void setAmL2MenuPerm(List<String> amL2MenuPerm) {
		this.amL2MenuPerm = amL2MenuPerm;
	}
	public List<String> getAmL3MenuPerm() {
		return amL3MenuPerm;
	}
	public void setAmL3MenuPerm(List<String> amL3MenuPerm) {
		this.amL3MenuPerm = amL3MenuPerm;
	}
}
