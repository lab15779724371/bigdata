package com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu;

import lombok.Data;

/**
 * @Descritpion：学校信息
 * @author: tianfang_infotech
 * @date: 2019/1/9 10:56
 */
@Data
public class EduSchool {

    /**
     * 编号
     */
    private String id;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 地址
     */
    private String address;

    /**
     * 区
     */
    private String area;

    /**
     * 等级 0:幼儿园，1:小学，2:初中，3:高中，5:企业，6:中职校 7:托儿所  9:其他
     */
    private String LEVEL;

    /**
     * 学校性质 0:国内公办 1:国际公办 2:国内民办 3:国际民办 , 20180810 改:0公办和2私办
     */
    private String schoolNature;

    /**
     * 食堂经营模式 0:食堂自营管理，1:食堂外包管理，2:混合
     */
    private String canteenMode;

    /**
     *配送类型,0快餐配送 1原料配送
     */
    private String ledgerType;

    /**
     * 学校学制
     */
    private Integer level2;

    /**
     * 关联的总校
     */
    private String parentId;

    /**
     *食品许可证件主体的类型(1学校|2外包)
     */
    private String licenseMainType;

    /**
     * 供餐模式:20181024新增:1自行加工,2食品加工商,3快餐配送,4现场加工
     */
    private Integer licenseMainChild;

    /**
     * 所属区ID
     */
    private String schoolAreaId;
    
    
    /**
     * 部门主管ID
     */
    private String departmentMasterId;
    
    
    /**
     * 部门主管
     */
    //private String departmentMaster;
    
    
    /**
     * 所属部/区/市级ID	
     */
    private String departmentSlaveId;
    
    
    /**
     * 所属部/区/市级
     */
    //private String departmentSlave;
    
    /**
     * 部门负责人姓名(分管后勤总务处)
     */
    private String depHeadName;
    
    /**
     * 部门负责人手机号
     */
    private String dhnMobilePhone;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLEVEL() {
		return LEVEL;
	}

	public void setLEVEL(String lEVEL) {
		LEVEL = lEVEL;
	}

	public String getSchoolNature() {
		return schoolNature;
	}

	public void setSchoolNature(String schoolNature) {
		this.schoolNature = schoolNature;
	}

	public String getCanteenMode() {
		return canteenMode;
	}

	public void setCanteenMode(String canteenMode) {
		this.canteenMode = canteenMode;
	}

	public String getLedgerType() {
		return ledgerType;
	}

	public void setLedgerType(String ledgerType) {
		this.ledgerType = ledgerType;
	}

	public Integer getLevel2() {
		return level2;
	}

	public void setLevel2(Integer level2) {
		this.level2 = level2;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getLicenseMainType() {
		return licenseMainType;
	}

	public void setLicenseMainType(String licenseMainType) {
		this.licenseMainType = licenseMainType;
	}

	public Integer getLicenseMainChild() {
		return licenseMainChild;
	}

	public void setLicenseMainChild(Integer licenseMainChild) {
		this.licenseMainChild = licenseMainChild;
	}

	public String getSchoolAreaId() {
		return schoolAreaId;
	}

	public void setSchoolAreaId(String schoolAreaId) {
		this.schoolAreaId = schoolAreaId;
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

	

	public String getDepHeadName() {
		return depHeadName;
	}

	public void setDepHeadName(String depHeadName) {
		this.depHeadName = depHeadName;
	}

	public String getDhnMobilePhone() {
		return dhnMobilePhone;
	}

	public void setDhnMobilePhone(String dhnMobilePhone) {
		this.dhnMobilePhone = dhnMobilePhone;
	}
}
