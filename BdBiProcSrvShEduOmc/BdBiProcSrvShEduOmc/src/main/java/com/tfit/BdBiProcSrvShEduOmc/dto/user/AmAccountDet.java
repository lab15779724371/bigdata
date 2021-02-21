package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

public class AmAccountDet {
	int roleType;
	String userOrgId;
    int isAdmin;
	String userName;
	String fullName;
	String roleName;
	String accountType;
	String userOrg;
	String mobPhone;
	String email;
	String userSrc;
	String userStatus;
	String creator;
	String createTime;
	String lastLoginTime;
	String password;
	String fixPhone;
	String fax;
	List<String> amDataPerm;               //数据权限,元素为学校ID
	List<String> amL1MenuPerm;             //一级菜单权限ID集合
	List<String> amL2MenuPerm;             //二级菜单权限ID集合
	List<String> amL3MenuPerm;             //三级菜单权限ID（按钮）集合
	
	public int getRoleType() {
		return roleType;
	}
	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}
	public String getUserOrgId() {
		return userOrgId;
	}
	public void setUserOrgId(String userOrgId) {
		this.userOrgId = userOrgId;
	}
	public int getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getUserOrg() {
		return userOrg;
	}
	public void setUserOrg(String userOrg) {
		this.userOrg = userOrg;
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
	public String getUserSrc() {
		return userSrc;
	}
	public void setUserSrc(String userSrc) {
		this.userSrc = userSrc;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
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
	public String getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
}
