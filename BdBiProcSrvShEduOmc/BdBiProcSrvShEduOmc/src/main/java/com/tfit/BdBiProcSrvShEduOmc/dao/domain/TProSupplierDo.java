package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Date;
import java.sql.Timestamp;

public class TProSupplierDo {
	//主键ID
	String id;
	//供应商名称
	String supplierName;
	//公司性质 1针对幼儿园
	Integer companyType;
	//经营范围Id
	String scopeId;
	//经营范围名称
	String scopeName;
	//供应商地址
	String address;
	//省
	String provinces;
	//市
	String city;
	//区
	String area;
	//区县ID
	String districtId;
	//供应商分类  0 不区分 1生产类 2 经销类
	Integer supplierClassify;
	//供应商类型 0为不区分，1为成品菜供应商，2为原料供应商
	Integer supplierType;
	//工商执照号
	String businessLicense;
	//组织机构代码
	String organizationCode;
	//餐饮服务证号
	String foodServiceCode;
	//餐饮服务证号失效日期
	Date foodServiceCodeDate;
	//食品经营许可证号
	String foodBusinessCode;
	//食品经营许可证号失效日期
	Date foodBusinessCodeDate;
	//食品流通证号
	String foodCirculationCode;
	//食品流通证号失效日期
	Date foodCirculationCodeDate;
	//食品生产证号
	String foodProduceCode;
	//食品生产证号失效日期
	Date foodProduceCodeDate;
	//联系人 修改成 法人字段
	String corporation;
	//法人联系电话
	String corporationWay;
	//联系人
	String contacts;
	//联系方式
	String contactWay;
	//公司注册时间
	String regTime;
	//注册地址
	String regAddress;
	//法人代表身份证号码
	String idCard;
	//证件类型 0身份证、1护照、2港澳通行证、3台胞证
	String idType;
	//经度
	String longitude;
	//纬度
	String latitude;
	//是否通过审核 0：未审核 1 审核通过 2 拒绝
	short reviewed;
	//拒绝理由
	String refuseReason;
	//质量负责人
	String qaPerson;
	//质量负责人联系电话
	String qaWay;
	//注册资金
	String regCapital;
	//年销售额
	String annualSales;
	//公司图片
	String companyImage;
	//备注
	String note;
	//创建者
	String creator;
	//创建时间
	Timestamp createTime;
	//更新者
	String updater;
	//最后更新时间
	Timestamp lastUpdateTime;
	//是否有效：1有效，0无效
	Integer stat;
	//伊甸代码
	String yidianCode;
	//0为老数据  1为新数据
	Integer isNew;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public Integer getCompanyType() {
		return companyType;
	}
	public void setCompanyType(Integer companyType) {
		this.companyType = companyType;
	}
	public String getScopeId() {
		return scopeId;
	}
	public void setScopeId(String scopeId) {
		this.scopeId = scopeId;
	}
	public String getScopeName() {
		return scopeName;
	}
	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProvinces() {
		return provinces;
	}
	public void setProvinces(String provinces) {
		this.provinces = provinces;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getDistrictId() {
		return districtId;
	}
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	public Integer getSupplierClassify() {
		return supplierClassify;
	}
	public void setSupplierClassify(Integer supplierClassify) {
		this.supplierClassify = supplierClassify;
	}
	public Integer getSupplierType() {
		return supplierType;
	}
	public void setSupplierType(Integer supplierType) {
		this.supplierType = supplierType;
	}
	public String getBusinessLicense() {
		return businessLicense;
	}
	public void setBusinessLicense(String businessLicense) {
		this.businessLicense = businessLicense;
	}
	public String getOrganizationCode() {
		return organizationCode;
	}
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}
	public String getFoodServiceCode() {
		return foodServiceCode;
	}
	public void setFoodServiceCode(String foodServiceCode) {
		this.foodServiceCode = foodServiceCode;
	}
	public Date getFoodServiceCodeDate() {
		return foodServiceCodeDate;
	}
	public void setFoodServiceCodeDate(Date foodServiceCodeDate) {
		this.foodServiceCodeDate = foodServiceCodeDate;
	}
	public String getFoodBusinessCode() {
		return foodBusinessCode;
	}
	public void setFoodBusinessCode(String foodBusinessCode) {
		this.foodBusinessCode = foodBusinessCode;
	}
	public Date getFoodBusinessCodeDate() {
		return foodBusinessCodeDate;
	}
	public void setFoodBusinessCodeDate(Date foodBusinessCodeDate) {
		this.foodBusinessCodeDate = foodBusinessCodeDate;
	}
	public String getFoodCirculationCode() {
		return foodCirculationCode;
	}
	public void setFoodCirculationCode(String foodCirculationCode) {
		this.foodCirculationCode = foodCirculationCode;
	}
	public Date getFoodCirculationCodeDate() {
		return foodCirculationCodeDate;
	}
	public void setFoodCirculationCodeDate(Date foodCirculationCodeDate) {
		this.foodCirculationCodeDate = foodCirculationCodeDate;
	}
	public String getFoodProduceCode() {
		return foodProduceCode;
	}
	public void setFoodProduceCode(String foodProduceCode) {
		this.foodProduceCode = foodProduceCode;
	}
	public Date getFoodProduceCodeDate() {
		return foodProduceCodeDate;
	}
	public void setFoodProduceCodeDate(Date foodProduceCodeDate) {
		this.foodProduceCodeDate = foodProduceCodeDate;
	}
	public String getCorporation() {
		return corporation;
	}
	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}
	public String getCorporationWay() {
		return corporationWay;
	}
	public void setCorporationWay(String corporationWay) {
		this.corporationWay = corporationWay;
	}
	public String getContacts() {
		return contacts;
	}
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
	public String getContactWay() {
		return contactWay;
	}
	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
	}
	public String getRegTime() {
		return regTime;
	}
	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}
	public String getRegAddress() {
		return regAddress;
	}
	public void setRegAddress(String regAddress) {
		this.regAddress = regAddress;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public short getReviewed() {
		return reviewed;
	}
	public void setReviewed(short reviewed) {
		this.reviewed = reviewed;
	}
	public String getRefuseReason() {
		return refuseReason;
	}
	public void setRefuseReason(String refuseReason) {
		this.refuseReason = refuseReason;
	}
	public String getQaPerson() {
		return qaPerson;
	}
	public void setQaPerson(String qaPerson) {
		this.qaPerson = qaPerson;
	}
	public String getQaWay() {
		return qaWay;
	}
	public void setQaWay(String qaWay) {
		this.qaWay = qaWay;
	}
	public String getRegCapital() {
		return regCapital;
	}
	public void setRegCapital(String regCapital) {
		this.regCapital = regCapital;
	}
	public String getAnnualSales() {
		return annualSales;
	}
	public void setAnnualSales(String annualSales) {
		this.annualSales = annualSales;
	}
	public String getCompanyImage() {
		return companyImage;
	}
	public void setCompanyImage(String companyImage) {
		this.companyImage = companyImage;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
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
	public String getYidianCode() {
		return yidianCode;
	}
	public void setYidianCode(String yidianCode) {
		this.yidianCode = yidianCode;
	}
	public Integer getIsNew() {
		return isNew;
	}
	public void setIsNew(Integer isNew) {
		this.isNew = isNew;
	}
}
