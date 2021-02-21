package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd;

import lombok.Data;

import java.util.Date;

/**
 * @Descritpion：大数据系统用户信息
 * @author: tianfang_infotech
 * @date: 2019/1/23 10:58
 */
@Data
public class EduBdUserDo {

    /**
     * 主键id
     */
    private String id;

    /**
     * 登录账户名
     */
    private String userAccount;

    /**
     * 密码
     */
    private String password;

    /**
     * 安全等级，0:弱，1:中，2:高
     */
    private Integer safeGrade;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 固定电话
     */
    private String fixPhone;

    /**
     * 手机
     */
    private String mobilePhone;

    /**
     * 姓名
     */
    private String name;

    /**
     * 用户图片地址
     */
    private String userPicUrl;

    /**
     * 是否为管理员
     */
    private Integer isAdmin;

    /**
     * 角色编号(t_edu_bd_role表id)
     */
    private String roleId;

    /**
     * 父账户，空表示超级管理员
     */
    private String parentId;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 更新人时间
     */
    private String lastUpdateTime;

    /**
     * '是否禁用0禁用 1启用'
     */
    private Integer forbid;

    /**
     * 用户授权码
     */
    private String token;

    /**
     * 是否有效 1 有效 0 无效
     */
    private Integer stat;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 单位ID
     */
    private String orgId;

    /**
     * 单位名称
     */
    private String orgName;

    /**
     * 传真
     */
    private String fax;

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

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
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
