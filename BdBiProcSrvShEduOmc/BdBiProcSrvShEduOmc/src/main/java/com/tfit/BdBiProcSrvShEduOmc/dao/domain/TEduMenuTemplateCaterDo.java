package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class TEduMenuTemplateCaterDo {
	String id;                        //
	String tempId;                    //t_pro_menu_template 表主键
	String caterTypeId;               //餐别ID
	String caterTypeName;             //餐别名
	Integer mealType;                 //排餐类型 1:固定套餐 2:有限选择 3:自由点餐
	Integer packageNum	;             //套餐数	
	String supplierId;                //供应商id	
	String creator;                   //创建者
	Timestamp createTime;             //
	String updater;                   //更新人
	Timestamp lastUpdateTime;         //
	Integer stat;                     //
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTempId() {
		return tempId;
	}
	public void setTempId(String tempId) {
		this.tempId = tempId;
	}
	public String getCaterTypeId() {
		return caterTypeId;
	}
	public void setCaterTypeId(String caterTypeId) {
		this.caterTypeId = caterTypeId;
	}
	public String getCaterTypeName() {
		return caterTypeName;
	}
	public void setCaterTypeName(String caterTypeName) {
		this.caterTypeName = caterTypeName;
	}
	public Integer getMealType() {
		return mealType;
	}
	public void setMealType(Integer mealType) {
		this.mealType = mealType;
	}
	public Integer getPackageNum() {
		return packageNum;
	}
	public void setPackageNum(Integer packageNum) {
		this.packageNum = packageNum;
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
