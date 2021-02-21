package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

public class TEduBdUserPermDo {
	Long id;                                     //主键id
	String userId;                               //用户id，表t_edu_bd_user的主键id
	String permId;                               //权限id，数据权限则为表t_edu_bd_data_perm的主键id，菜单权限则为表t_edu_bd_menu的主键id
	Integer permType;                            //权限类型，1:数据权限，2:菜单权限
	String createTime;                           //创建时间
	String updateTime;                           //更新时间
	Integer stat;                                //0:无效，1:有效
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPermId() {
		return permId;
	}
	public void setPermId(String permId) {
		this.permId = permId;
	}
	public Integer getPermType() {
		return permType;
	}
	public void setPermType(Integer permType) {
		this.permType = permType;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
}
