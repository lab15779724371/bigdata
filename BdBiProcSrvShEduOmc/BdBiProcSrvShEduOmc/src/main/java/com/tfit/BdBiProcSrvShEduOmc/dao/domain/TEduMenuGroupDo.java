package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class TEduMenuGroupDo {
	String id;
	String menuGroupName;                    //菜单分组名	
	String note;                             //备注
	String supplierId;                       //供应商id
	Integer globalSortNum;                   //预制排序
	Integer privateSortNum;                  //私有排序
	String applicableProjectType;            //适用项目点类型 1:学校 2:社会	
	Integer menuLabel;                       //菜单标签(1学生餐、2教工餐)	
	Integer enable;                          //2停用/1启动
	String creator;                          //创建者
	Timestamp createTime;	
	String update;                           //更新人
	Timestamp lastUpdateTime;
	Integer stat;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMenuGroupName() {
		return menuGroupName;
	}
	public void setMenuGroupName(String menuGroupName) {
		this.menuGroupName = menuGroupName;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public Integer getGlobalSortNum() {
		return globalSortNum;
	}
	public void setGlobalSortNum(Integer globalSortNum) {
		this.globalSortNum = globalSortNum;
	}
	public Integer getPrivateSortNum() {
		return privateSortNum;
	}
	public void setPrivateSortNum(Integer privateSortNum) {
		this.privateSortNum = privateSortNum;
	}
	public String getApplicableProjectType() {
		return applicableProjectType;
	}
	public void setApplicableProjectType(String applicableProjectType) {
		this.applicableProjectType = applicableProjectType;
	}
	public Integer getMenuLabel() {
		return menuLabel;
	}
	public void setMenuLabel(Integer menuLabel) {
		this.menuLabel = menuLabel;
	}
	public Integer getEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getUpdate() {
		return update;
	}
	public void setUpdate(String update) {
		this.update = update;
	}
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Integer getStat() {
		return stat;
	}
	public void setStat(Integer stat) {
		this.stat = stat;
	}
}
