package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class TEduCaterTypeDo {
	String id;                                 //
	String caterTypeName;                      //餐别名
	Integer sortNum;                           //预制排序
	Integer privateSort;                       //私有排序
	String supplyType;                         //适用项目点类型 1:学校 2:社会	
	String supplierId;                         //供应商id
	String creator;                            //创建者
	Timestamp createTime;                      //
	String updater;                            //更新人
	Timestamp lastUpdateTime;                  //
	Integer stat;                              //
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCaterTypeName() {
		return caterTypeName;
	}
	public void setCaterTypeName(String caterTypeName) {
		this.caterTypeName = caterTypeName;
	}
	public Integer getSortNum() {
		return sortNum;
	}
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
	public Integer getPrivateSort() {
		return privateSort;
	}
	public void setPrivateSort(Integer privateSort) {
		this.privateSort = privateSort;
	}
	public String getSupplyType() {
		return supplyType;
	}
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
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
	public String getUpdater() {
		return updater;
	}
	public void setUpdater(String updater) {
		this.updater = updater;
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
