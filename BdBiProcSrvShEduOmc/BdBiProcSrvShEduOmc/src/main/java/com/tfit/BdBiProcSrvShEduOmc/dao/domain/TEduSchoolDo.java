package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class TEduSchoolDo {
	//主键ID
	String id;
	//学校ID
	Integer schoolId;
	//所属教委ID
	String committeeId;
	//学校名称
	String schoolName;
	//学校缩略图
	String schoolThum;
	//法人代表
	String corporation;
	//法人代表电话
	String corporationWay;
	//移动电话
	String mobileNo;
	//联系人
	String contacts;
	//学校地址
	String address;
	//经度
	String longitude;
	//纬度
	String latitude;
	//0 幼儿园，1 小学，2 初中，3 高中，4 大学 ,5 企业，6中职校 7托儿所  9其他
	String level;
	//学校食堂在供应商表的id
	String supplierId;
	//是否审核通过 0：未审核，1：审核通过，2：审核不通过
	short reviewed;
	//是否支持线上付款 0：不支持， 1：支持
	short onlinePayment;
	//项目点编号(4位数字)
	String pjNo;
	//食堂经营模式 0 食堂自营管理，1 食堂外包管理, 2 混合
	short canteenMode;
	//食堂采购模式 0 自主采购配送，1 外包采购配送	
	short purchaseMode;
	//学校性质 0国内公办 1国际公办   2国内民办 3 国际民办
	String schoolNature;
	//配送类型,0快餐配送 1原料配送
	String ledgerType;
	//学校图片
	String schoolPic;
	//学校LOGO
	String schoolLogo;
	//省
	String provinces;
	//市
	String city;
	//区
	String area;
	//学校代码
	String schoolNum;
	//邮箱
	String email;
	//学校性质：0国内公办  1 国内民办  2国际公办  3国际民办
	Integer property;
	//创建者
	String creator;
	//创建时间
	Timestamp createTime;
	//更新者
	String updater;
	//最后更新时间
	Timestamp lastUpdateTime;
	//是否失效
	Integer stat;
	//座机
	String corporationTelephone;
	//是否分校
	Integer isBranchSchool;
	//统一社会信用代码
	String socialCreditCode;
	//关联的总校
	String parentId;
	//部门主管ID
	String departmentMasterId;
	//所属部/区/市级ID	
	String departmentSlaveId;	
	//所属区ID
	String schoolAreaId;
	//所属区	
	String schoolArea;
	//备注说明
	String remark;
	//食品许可证件主体的类型(1学校|2外包)
	String licenseMainType;
	//供餐模式:20181024新增:1自行加工,2食品加工商3快餐配送,4现场加工
	Short licenseMainChild;	
	//民办性质小类
	String schoolNatureSub;
	//部门负责人姓名(分管后勤总务处)
	String departmentHead;
	//手机
	String departmentMobilephone;
	//座机
	String departmentTelephone;
	//传真
	String departmentFax;
	//电子邮件
	String departmentEmail;
	//项目联系人姓名(负责食品安全管理人员)	
	String foodSafetyPersion;
	//手机
	String foodSafetyMobilephone;
	//座机
	String foodSafetyTelephone;
	//学校学制
	Integer level2;
	//学生人数
	Integer studentsAmount;
	//教职工人数
	Integer staffAmount;
	
	//管理部门编号
	String departmentId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}
	public String getCommitteeId() {
		return committeeId;
	}
	public void setCommitteeId(String committeeId) {
		this.committeeId = committeeId;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getSchoolThum() {
		return schoolThum;
	}
	public void setSchoolThum(String schoolThum) {
		this.schoolThum = schoolThum;
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
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getContacts() {
		return contacts;
	}
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public short getReviewed() {
		return reviewed;
	}
	public void setReviewed(short reviewed) {
		this.reviewed = reviewed;
	}
	public short getOnlinePayment() {
		return onlinePayment;
	}
	public void setOnlinePayment(short onlinePayment) {
		this.onlinePayment = onlinePayment;
	}
	public String getPjNo() {
		return pjNo;
	}
	public void setPjNo(String pjNo) {
		this.pjNo = pjNo;
	}
	public short getCanteenMode() {
		return canteenMode;
	}
	public void setCanteenMode(short canteenMode) {
		this.canteenMode = canteenMode;
	}
	public short getPurchaseMode() {
		return purchaseMode;
	}
	public void setPurchaseMode(short purchaseMode) {
		this.purchaseMode = purchaseMode;
	}
	public String getSchoolNature() {
		return schoolNature;
	}
	public void setSchoolNature(String schoolNature) {
		this.schoolNature = schoolNature;
	}
	public String getLedgerType() {
		return ledgerType;
	}
	public void setLedgerType(String ledgerType) {
		this.ledgerType = ledgerType;
	}
	public String getSchoolPic() {
		return schoolPic;
	}
	public void setSchoolPic(String schoolPic) {
		this.schoolPic = schoolPic;
	}
	public String getSchoolLogo() {
		return schoolLogo;
	}
	public void setSchoolLogo(String schoolLogo) {
		this.schoolLogo = schoolLogo;
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
	public String getSchoolNum() {
		return schoolNum;
	}
	public void setSchoolNum(String schoolNum) {
		this.schoolNum = schoolNum;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getProperty() {
		return property;
	}
	public void setProperty(Integer property) {
		this.property = property;
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
	public String getCorporationTelephone() {
		return corporationTelephone;
	}
	public void setCorporationTelephone(String corporationTelephone) {
		this.corporationTelephone = corporationTelephone;
	}
	public Integer getIsBranchSchool() {
		return isBranchSchool;
	}
	public void setIsBranchSchool(Integer isBranchSchool) {
		this.isBranchSchool = isBranchSchool;
	}
	public String getSocialCreditCode() {
		return socialCreditCode;
	}
	public void setSocialCreditCode(String socialCreditCode) {
		this.socialCreditCode = socialCreditCode;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getDepartmentMasterId() {
		return departmentMasterId;
	}
	public void setDepartmentMasterId(String departmentMasterId) {
		this.departmentMasterId = departmentMasterId;
	}
	public String getDepartmentSlaveId() {
		return departmentSlaveId;
	}
	public void setDepartmentSlaveId(String departmentSlaveId) {
		this.departmentSlaveId = departmentSlaveId;
	}
	public String getSchoolAreaId() {
		return schoolAreaId;
	}
	public void setSchoolAreaId(String schoolAreaId) {
		this.schoolAreaId = schoolAreaId;
	}
	public String getSchoolArea() {
		return schoolArea;
	}
	public void setSchoolArea(String schoolArea) {
		this.schoolArea = schoolArea;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getLicenseMainType() {
		return licenseMainType;
	}
	public void setLicenseMainType(String licenseMainType) {
		this.licenseMainType = licenseMainType;
	}
	public Short getLicenseMainChild() {
		return licenseMainChild;
	}
	public void setLicenseMainChild(Short licenseMainChild) {
		this.licenseMainChild = licenseMainChild;
	}
	public String getSchoolNatureSub() {
		return schoolNatureSub;
	}
	public void setSchoolNatureSub(String schoolNatureSub) {
		this.schoolNatureSub = schoolNatureSub;
	}
	public String getDepartmentHead() {
		return departmentHead;
	}
	public void setDepartmentHead(String departmentHead) {
		this.departmentHead = departmentHead;
	}
	public String getDepartmentMobilephone() {
		return departmentMobilephone;
	}
	public void setDepartmentMobilephone(String departmentMobilephone) {
		this.departmentMobilephone = departmentMobilephone;
	}
	public String getDepartmentTelephone() {
		return departmentTelephone;
	}
	public void setDepartmentTelephone(String departmentTelephone) {
		this.departmentTelephone = departmentTelephone;
	}
	public String getDepartmentFax() {
		return departmentFax;
	}
	public void setDepartmentFax(String departmentFax) {
		this.departmentFax = departmentFax;
	}
	public String getDepartmentEmail() {
		return departmentEmail;
	}
	public void setDepartmentEmail(String departmentEmail) {
		this.departmentEmail = departmentEmail;
	}
	public String getFoodSafetyPersion() {
		return foodSafetyPersion;
	}
	public void setFoodSafetyPersion(String foodSafetyPersion) {
		this.foodSafetyPersion = foodSafetyPersion;
	}
	public String getFoodSafetyMobilephone() {
		return foodSafetyMobilephone;
	}
	public void setFoodSafetyMobilephone(String foodSafetyMobilephone) {
		this.foodSafetyMobilephone = foodSafetyMobilephone;
	}
	public String getFoodSafetyTelephone() {
		return foodSafetyTelephone;
	}
	public void setFoodSafetyTelephone(String foodSafetyTelephone) {
		this.foodSafetyTelephone = foodSafetyTelephone;
	}
	public Integer getLevel2() {
		return level2;
	}
	public void setLevel2(Integer level2) {
		this.level2 = level2;
	}
	public Integer getStudentsAmount() {
		return studentsAmount;
	}
	public void setStudentsAmount(Integer studentsAmount) {
		this.studentsAmount = studentsAmount;
	}
	public Integer getStaffAmount() {
		return staffAmount;
	}
	public void setStaffAmount(Integer staffAmount) {
		this.staffAmount = staffAmount;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
}
