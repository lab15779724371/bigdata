package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class TProReserveSampleDo {
	String id;                         //主键	
	String packageId;                  //t_saas_package表ID
	String caterTypeId;                //餐别ID
	String caterTypeName;              //餐别名
	String menuGroupId;                //菜单分组ID
	String menuGroupName;              //菜单分组名称
	Timestamp supplyDate;              //就餐时间
	Integer reserveHour;               //留样时间-时
	Integer reserveMinute;             //留样时间-分
	String remark;                     //留样说明
	String schoolId;                   //项目点Id
	String schoolName;                 //项目点名称
	String districtId;                 //区县Id
	String districtName;               //区县名称
	String supplierId;                 //团餐公司Id
	String supplierName;               //团餐公司名称
	String creator;                    //创建者
	Timestamp createTime;
	String updater;                    //更新人
	Timestamp lastUpdateTime;
	Integer stat;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
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
	public String getMenuGroupId() {
		return menuGroupId;
	}
	public void setMenuGroupId(String menuGroupId) {
		this.menuGroupId = menuGroupId;
	}
	public String getMenuGroupName() {
		return menuGroupName;
	}
	public void setMenuGroupName(String menuGroupName) {
		this.menuGroupName = menuGroupName;
	}
	public Timestamp getSupplyDate() {
		return supplyDate;
	}
	public void setSupplyDate(Timestamp supplyDate) {
		this.supplyDate = supplyDate;
	}
	public Integer getReserveHour() {
		return reserveHour;
	}
	public void setReserveHour(Integer reserveHour) {
		this.reserveHour = reserveHour;
	}
	public Integer getReserveMinute() {
		return reserveMinute;
	}
	public void setReserveMinute(Integer reserveMinute) {
		this.reserveMinute = reserveMinute;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getDistrictId() {
		return districtId;
	}
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
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
