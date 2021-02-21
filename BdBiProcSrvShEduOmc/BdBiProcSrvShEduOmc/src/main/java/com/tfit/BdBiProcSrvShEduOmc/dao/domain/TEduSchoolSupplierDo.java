package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import java.sql.Timestamp;

public class TEduSchoolSupplierDo {
	//主键id
	String id;
	//学校id
	String schoolId;
	//供应商id
	String supplierId;
	//学校名称
	String schoolName;
	//学校地址
	String address;
	//校方联系人
	String contact;
	//校方联系方式
	String mobileNo;
	//项目点名称
	String projectName;
	//项目经理Id
	String managerId;
	//项目经理
	String managerName;
	//项目经理联系方式
	String managerNo;
	//备注
	String remark;
	//车辆Id
	String carId;
	//车牌号
	String carCode;
	//司机Id
	String dirverId;
	//司机姓名
	String driverName;
	//投票(0 否 1 是)
	Integer vote;
	//项目点编号
	String pjNo;
	//资料完整度：0 不完整 1完整
	Integer managerStatus;
	//项目点类型 1：学校项目点 2：社会项目点
	Integer projType;
	//0 幼儿园，1 小学，2 初中，3 高中，4 大学 ,5 企业，6 职高
	String level;
	//排餐类型 1:固定套餐 2:有限选择 3:自由点餐
	Integer mealType;
	//套餐数	
	Integer packageNum;
	//0统配 1直配 2混合配
	Integer deliveryWay;
	//教工人数
	Integer staffCount;
	//食堂从业人数
	Integer workerCount;
	//学生人数
	Integer studentCount;
	//创建者
	String creator;
	//创建时间
	Timestamp createTime;
	//更新者
	String updater;
	//更新时间
	Timestamp lastUpdateTime;
	//是否有效
	Integer stat;
	//0 未关联 1 已关联
	Integer relation;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getManagerId() {
		return managerId;
	}
	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getManagerNo() {
		return managerNo;
	}
	public void setManagerNo(String managerNo) {
		this.managerNo = managerNo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCarId() {
		return carId;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}
	public String getCarCode() {
		return carCode;
	}
	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}
	public String getDirverId() {
		return dirverId;
	}
	public void setDirverId(String dirverId) {
		this.dirverId = dirverId;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public Integer getVote() {
		return vote;
	}
	public void setVote(Integer vote) {
		this.vote = vote;
	}
	public String getPjNo() {
		return pjNo;
	}
	public void setPjNo(String pjNo) {
		this.pjNo = pjNo;
	}
	public Integer getManagerStatus() {
		return managerStatus;
	}
	public void setManagerStatus(Integer managerStatus) {
		this.managerStatus = managerStatus;
	}
	public Integer getProjType() {
		return projType;
	}
	public void setProjType(Integer projType) {
		this.projType = projType;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
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
	public Integer getDeliveryWay() {
		return deliveryWay;
	}
	public void setDeliveryWay(Integer deliveryWay) {
		this.deliveryWay = deliveryWay;
	}
	public Integer getStaffCount() {
		return staffCount;
	}
	public void setStaffCount(Integer staffCount) {
		this.staffCount = staffCount;
	}
	public Integer getWorkerCount() {
		return workerCount;
	}
	public void setWorkerCount(Integer workerCount) {
		this.workerCount = workerCount;
	}
	public Integer getStudentCount() {
		return studentCount;
	}
	public void setStudentCount(Integer studentCount) {
		this.studentCount = studentCount;
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
	public Integer getRelation() {
		return relation;
	}
	public void setRelation(Integer relation) {
		this.relation = relation;
	}
}
