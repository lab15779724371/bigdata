package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

public class TEduBdUserDo {
	String id;                            //主键id
	String userAccount;                   //登录账户名
	String password;                      //密码
	Integer safeGrade;                    //安全等级，0:弱，1:中，2:高
	String email;                         //邮箱
	String fixPhone;                      //固定电话
	String mobilePhone;                   //手机号码
	String name;                          //姓名
	String userPicUrl;                    //用户图片URL
	Integer isAdmin;                      //0是false 1是true
	String roleId;                        //t_edu_bd_role  表id
	String parentId;                      //账户父ID，空表示超级管理员账户，拥有最高权限
	String lastLoginTime;                 //最后登录时间
	String creator;                       //创建者
	String createTime;                    //创建时间
	String updater;                       //更新人
	String lastUpdateTime;                //最近更新时间
	Integer forbid;                       //是否禁用0禁用 1启用
	String token;                         //用户授权码
	Integer stat;                         //是否有效 1 有效 0 无效
	String remarks;                       //备注	
	String orgId;                         //单位ID
	String orgName;                       //单位名称
	String fax;                           //传真
	Integer userType;//用户类型0:正式用户 1：测试用户
	Integer errorPasswordAccount;
	
	
	public Integer getErrorPasswordAccount() {
		return errorPasswordAccount;
	}
	public void setErrorPasswordAccount(Integer errorPasswordAccount) {
		this.errorPasswordAccount = errorPasswordAccount;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}	
	public Integer getSafeGrade() {
		return safeGrade;
	}
	public void setSafeGrade(Integer safeGrade) {
		this.safeGrade = safeGrade;
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
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserPicUrl() {
		return userPicUrl;
	}
	public void setUserPicUrl(String userPicUrl) {
		this.userPicUrl = userPicUrl;
	}
	public Integer getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
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
	public String getUpdater() {
		return updater;
	}
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Integer getForbid() {
		return forbid;
	}
	public void setForbid(Integer forbid) {
		this.forbid = forbid;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
}
