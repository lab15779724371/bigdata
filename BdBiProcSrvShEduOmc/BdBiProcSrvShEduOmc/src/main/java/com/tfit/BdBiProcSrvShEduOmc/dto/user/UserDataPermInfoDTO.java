package com.tfit.BdBiProcSrvShEduOmc.dto.user;

import java.util.List;

//用户数据权限信息
public class UserDataPermInfoDTO {
	String userName;                    //用户名称
	String distId;                      //所在区域ID
	int subLevelId;                     //所属ID，如3、2、1、0
	int compDepId;                      //主管部门ID，如所属ID为3时，则主管部门ID为1～16，分别对应上海16区教育局
	String subLevel;                    //所属，如区属、市属、部署、其他
	String orgId;                       //组织ID
	String orgName;                     //组织名称
	Integer RoleType;                   //角色类型，1:监管部门，2:学校
	List<String> dataPermList;          //数据权限列表，即学校id列表，为空表示拥有所有学校权限
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDistId() {
		return distId;
	}
	public void setDistId(String distId) {
		this.distId = distId;
	}	
	public int getSubLevelId() {
		return subLevelId;
	}
	public void setSubLevelId(int subLevelId) {
		this.subLevelId = subLevelId;
	}
	public int getCompDepId() {
		return compDepId;
	}
	public void setCompDepId(int compDepId) {
		this.compDepId = compDepId;
	}
	public String getSubLevel() {
		return subLevel;
	}
	public void setSubLevel(String subLevel) {
		this.subLevel = subLevel;
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
	public Integer getRoleType() {
		return RoleType;
	}
	public void setRoleType(Integer roleType) {
		RoleType = roleType;
	}
	public List<String> getDataPermList() {
		return dataPermList;
	}
	public void setDataPermList(List<String> dataPermList) {
		this.dataPermList = dataPermList;
	}
}