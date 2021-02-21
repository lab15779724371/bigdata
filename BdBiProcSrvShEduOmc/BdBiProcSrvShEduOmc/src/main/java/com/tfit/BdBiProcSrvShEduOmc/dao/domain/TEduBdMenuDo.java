package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

public class TEduBdMenuDo {
	String id;                     //主键
	String menuName;              //菜单标题
	Integer level;                 //菜单级别，1:一级菜单，2:二级菜单，3:三级菜单
	String parentId;              //父菜单ID	
	String parentName;            //父菜单名称
	Integer menuType;             //菜单类型，0:普通菜单，1:按钮
	String descript;               //描述
	Integer stat;                  //是否有效：1表示有效；0表示无效
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public Integer getMenuType() {
		return menuType;
	}
	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
}
