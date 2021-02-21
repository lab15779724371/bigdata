package com.tfit.BdBiProcSrvShEduOmc.model.ro;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Descritpion：学校基础信息 redis 模型
 * @author: tianfang_infotech
 * @date: 2019/1/8 10:43
 */
@Data
public class SchoolBasicRO {

    /**
     * 学校编号（表主键）
     */
    private String id;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 是否分校(0：总校; 1：分校)
     */
    private Integer isBranchSchool;

    /**
     * 关联总校Id
     */
    private String parentId;

    /**
     * 区号
     */
    private Integer area;

    /**
     * 地址
     */
    private String address;

    /**
     * 统一社会信用代码
     */
    private String socialCreditCode;

    /**
     * 学校学制
     */
    private Integer level;

    /**
     * 学校性质
     */
    private String schoolNature;

    /**
     * 学校性质小类
     */
    private Integer natureSub;

    /**
     * 所属部门(3:区级 2:市级 1:部级 0 其它;)
     */
    private Integer departmentMasterId;

    /**
     * 主管部门
     */
    private String departmentSlaveId;

    /**
     * 食堂经营模式
     */
    private Integer canteenMode;

    /**
     * 配送类型
     */
    private Integer ledgerType;

    /**
     * 学生数量
     */
    private Integer studentsAmount;

    /**
     * 教职工人数
     */
    private Integer staffAmount;

    /**
     * 法定代表人
     */
    private String corporation;

    /**
     * 电话
     */
    private String corporationWay;

    /**
     * 座机
     */
    private String corporationTelephone;

    /**
     * 部门负责人
     */
    private String departmentHead;

    /**
     * 手机
     */
    private String departmentMobilePhone;

    /**
     * 座机
     */
    private String departmentTelephone;

    /**
     *传真
     */
    private String departmentFax;

    /**
     *电子邮件
     */
    private String departmentEmail;

    /**
     *项目联系人
     */
    private String foodSafetyPerson;

    /**
     * 手机
     */
    private String foodSafetyMobilePhone;

    /**
     *座机
     */
    private String foodSafetyTelephone;

    /**
     * 供餐(0:不供餐;1:供餐)
     */
    private Integer gongCan;

    /**
     *证件类型 1 食品经营许可证
     */
    private Integer slicType;

    /**
     *图片
     */
    private String slicPic;

    /**
     *经营单位
     */
    private String slicJob;

    /**
     *许可证号
     */
    private String slicNo;

    /**
     *发证机关
     */
    private String soperation;

    /**
     *发证时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date slicDate;

    /**
     *有效日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date sendDate;

    /**
     * 0 餐饮服务许可证
     */
    private Integer clicType;

    /**
     * 图片
     */
    private String clicPic;

    /**
     *经营单位
     */
    private String clicJob;

    /**
     * 许可证号
     */
    private String clicNo;

    /**
     *发证机关
     */
    private String coperation;

    /**
     * 发证时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date clicDate;

    /**
     *有效日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date cendDate;

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

	public Integer getIsBranchSchool() {
		return isBranchSchool;
	}

	public void setIsBranchSchool(Integer isBranchSchool) {
		this.isBranchSchool = isBranchSchool;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getArea() {
		return area;
	}

	public void setArea(Integer area) {
		this.area = area;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSocialCreditCode() {
		return socialCreditCode;
	}

	public void setSocialCreditCode(String socialCreditCode) {
		this.socialCreditCode = socialCreditCode;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getSchoolNature() {
		return schoolNature;
	}

	public void setSchoolNature(String schoolNature) {
		this.schoolNature = schoolNature;
	}

	public Integer getNatureSub() {
		return natureSub;
	}

	public void setNatureSub(Integer natureSub) {
		this.natureSub = natureSub;
	}

	public Integer getDepartmentMasterId() {
		return departmentMasterId;
	}

	public void setDepartmentMasterId(Integer departmentMasterId) {
		this.departmentMasterId = departmentMasterId;
	}

	public String getDepartmentSlaveId() {
		return departmentSlaveId;
	}

	public void setDepartmentSlaveId(String departmentSlaveId) {
		this.departmentSlaveId = departmentSlaveId;
	}

	public Integer getCanteenMode() {
		return canteenMode;
	}

	public void setCanteenMode(Integer canteenMode) {
		this.canteenMode = canteenMode;
	}

	public Integer getLedgerType() {
		return ledgerType;
	}

	public void setLedgerType(Integer ledgerType) {
		this.ledgerType = ledgerType;
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

	public String getCorporationTelephone() {
		return corporationTelephone;
	}

	public void setCorporationTelephone(String corporationTelephone) {
		this.corporationTelephone = corporationTelephone;
	}

	public String getDepartmentHead() {
		return departmentHead;
	}

	public void setDepartmentHead(String departmentHead) {
		this.departmentHead = departmentHead;
	}

	public String getDepartmentMobilePhone() {
		return departmentMobilePhone;
	}

	public void setDepartmentMobilePhone(String departmentMobilePhone) {
		this.departmentMobilePhone = departmentMobilePhone;
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

	public String getFoodSafetyPerson() {
		return foodSafetyPerson;
	}

	public void setFoodSafetyPerson(String foodSafetyPerson) {
		this.foodSafetyPerson = foodSafetyPerson;
	}

	public String getFoodSafetyMobilePhone() {
		return foodSafetyMobilePhone;
	}

	public void setFoodSafetyMobilePhone(String foodSafetyMobilePhone) {
		this.foodSafetyMobilePhone = foodSafetyMobilePhone;
	}

	public String getFoodSafetyTelephone() {
		return foodSafetyTelephone;
	}

	public void setFoodSafetyTelephone(String foodSafetyTelephone) {
		this.foodSafetyTelephone = foodSafetyTelephone;
	}

	public Integer getGongCan() {
		return gongCan;
	}

	public void setGongCan(Integer gongCan) {
		this.gongCan = gongCan;
	}

	public Integer getSlicType() {
		return slicType;
	}

	public void setSlicType(Integer slicType) {
		this.slicType = slicType;
	}

	public String getSlicPic() {
		return slicPic;
	}

	public void setSlicPic(String slicPic) {
		this.slicPic = slicPic;
	}

	public String getSlicJob() {
		return slicJob;
	}

	public void setSlicJob(String slicJob) {
		this.slicJob = slicJob;
	}

	public String getSlicNo() {
		return slicNo;
	}

	public void setSlicNo(String slicNo) {
		this.slicNo = slicNo;
	}

	public String getSoperation() {
		return soperation;
	}

	public void setSoperation(String soperation) {
		this.soperation = soperation;
	}

	public Date getSlicDate() {
		return slicDate;
	}

	public void setSlicDate(Date slicDate) {
		this.slicDate = slicDate;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Integer getClicType() {
		return clicType;
	}

	public void setClicType(Integer clicType) {
		this.clicType = clicType;
	}

	public String getClicPic() {
		return clicPic;
	}

	public void setClicPic(String clicPic) {
		this.clicPic = clicPic;
	}

	public String getClicJob() {
		return clicJob;
	}

	public void setClicJob(String clicJob) {
		this.clicJob = clicJob;
	}

	public String getClicNo() {
		return clicNo;
	}

	public void setClicNo(String clicNo) {
		this.clicNo = clicNo;
	}

	public String getCoperation() {
		return coperation;
	}

	public void setCoperation(String coperation) {
		this.coperation = coperation;
	}

	public Date getClicDate() {
		return clicDate;
	}

	public void setClicDate(Date clicDate) {
		this.clicDate = clicDate;
	}

	public Date getCendDate() {
		return cendDate;
	}

	public void setCendDate(Date cendDate) {
		this.cendDate = cendDate;
	}
}
