package com.tfit.BdBiProcSrvShEduOmc.model.vo;

import lombok.Data;

/**
 * @Descritpion：大数据用户基本信息
 * @author: tianfang_infotech
 * @date: 2019/1/23 11:24
 */
@Data
public class BasicBdUser {
    /**
     * 主键id
     */
    private String id;

    /**
     * 登录账户名
     */
    private String userAccount;

    /**
     * 姓名
     */
    private String name;

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
     * 用户授权码
     */
    private String token;

    /**
     * 单位ID
     */
    private String orgId;

    /**
     * 单位名称
     */
    private String orgName;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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
}
