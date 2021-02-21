package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TSaasPackageDo {
	String id;                               //主键
	String menuId;                           //菜谱ID(一个项目点一天的排菜)
	String tempId;                           //t_pro_menu_template 表主键
	String tempName;                         //模板名称
	String menuGroupId;                      //菜单分组ID
	String menuGroupName;                    //菜单分组名
	String caterTypeId;                      //餐别ID	
	String caterTypeName;                    //餐别名
	Integer ledgerType;                      //配送类型: 1 原料 2 成品菜
	Integer mealType;                        //排餐类型 1:固定套餐 2:有限选择 3:自由点餐
	String caterPackageId;                   //供餐套餐id
	String packageName;                      //套餐名称
	String supplierId;                       //供应商Id
	Timestamp supplyDate;	                 //供应时间
	String projGroupId;                      //项目点组ID
	String projGroupName;                    //项目点组名称
	String schoolSupplierId;                 //项目点关系Id
	String schoolId;                         //项目点Id
	String schoolName;                       //项目点名称
	Integer menuType;                        //排菜方式 1 周菜谱 2 月菜谱
	BigDecimal cost;                         //参考成本
	Integer isPublish;                       //发布 0:未发布 1:已发布
	Integer reserved;                        //是否留样 0未留样，1已留样
	String creator;                          //创建者
	Timestamp createTime;                    //
	String updater;                          //更新人
	Timestamp lastUpdateTime;                //
	Integer stat;                            //
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getTempId() {
		return tempId;
	}
	public void setTempId(String tempId) {
		this.tempId = tempId;
	}
	public String getTempName() {
		return tempName;
	}
	public void setTempName(String tempName) {
		this.tempName = tempName;
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
	public Integer getLedgerType() {
		return ledgerType;
	}
	public void setLedgerType(Integer ledgerType) {
		this.ledgerType = ledgerType;
	}
	public Integer getMealType() {
		return mealType;
	}
	public void setMealType(Integer mealType) {
		this.mealType = mealType;
	}
	public String getCaterPackageId() {
		return caterPackageId;
	}
	public void setCaterPackageId(String caterPackageId) {
		this.caterPackageId = caterPackageId;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public Timestamp getSupplyDate() {
		return supplyDate;
	}
	public void setSupplyDate(Timestamp supplyDate) {
		this.supplyDate = supplyDate;
	}
	public String getProjGroupId() {
		return projGroupId;
	}
	public void setProjGroupId(String projGroupId) {
		this.projGroupId = projGroupId;
	}
	public String getProjGroupName() {
		return projGroupName;
	}
	public void setProjGroupName(String projGroupName) {
		this.projGroupName = projGroupName;
	}
	public String getSchoolSupplierId() {
		return schoolSupplierId;
	}
	public void setSchoolSupplierId(String schoolSupplierId) {
		this.schoolSupplierId = schoolSupplierId;
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
	public Integer getMenuType() {
		return menuType;
	}
	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	public Integer getIsPublish() {
		return isPublish;
	}
	public void setIsPublish(Integer isPublish) {
		this.isPublish = isPublish;
	}
	public Integer getReserved() {
		return reserved;
	}
	public void setReserved(Integer reserved) {
		this.reserved = reserved;
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
