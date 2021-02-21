package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class TBaseMaterialSupplierDo {
	String id;                            //
	String materialId;                    //原料基础表t_base_material 的 Id
	String ssupplierId;                   //供应商Id
	Integer materialType;                 //原料类型Id
	String materialName;                  //原料名称
	String materialAliasName;             //原料别名
	Integer shelfLife;                    //保质期 以天为单位
	String amountUnit;                    //数量单位 默认为公斤
	String creator;                       //创建者
	Timestamp createTime;                 //
	String updater;                       //更新人
	Timestamp lastUpdateTime;             //
	Integer stat;                         //
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMaterialId() {
		return materialId;
	}
	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}
	public String getSsupplierId() {
		return ssupplierId;
	}
	public void setSsupplierId(String ssupplierId) {
		this.ssupplierId = ssupplierId;
	}
	public Integer getMaterialType() {
		return materialType;
	}
	public void setMaterialType(Integer materialType) {
		this.materialType = materialType;
	}
	public String getMaterialName() {
		return materialName;
	}
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
	public String getMaterialAliasName() {
		return materialAliasName;
	}
	public void setMaterialAliasName(String materialAliasName) {
		this.materialAliasName = materialAliasName;
	}
	public Integer getShelfLife() {
		return shelfLife;
	}
	public void setShelfLife(Integer shelfLife) {
		this.shelfLife = shelfLife;
	}
	public String getAmountUnit() {
		return amountUnit;
	}
	public void setAmountUnit(String amountUnit) {
		this.amountUnit = amountUnit;
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
