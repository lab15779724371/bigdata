package com.tfit.BdBiProcSrvShEduOmc.model.ro;

import lombok.Data;

/**
 * @Descritpion：团餐公司基础信息 redis 模型
 * @author: tianfang_infotech
 * @date: 2019/1/14 16:23
 */
@Data
public class GroupMealCompanyBasicRO {

    /**
     * 团餐公司编号（表主键）
     */
    private String id;

    /**
     * 团餐公司名称
     */
    private String supplierName;

    /**
     * 区号
     */
    private Integer area;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 手机号
     */
    private String contactWay;

    /**
     * 质量负责人
     */
    private String qaPerson;

    /**
     * 联系电话
     */
    private String qaWay;

    /**
     * 注册资金
     */
    private String regCapital;

    /**
     * 法人
     */
    private String corporation;

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

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactWay() {
		return contactWay;
	}

	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
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

	public String getCorporation() {
		return corporation;
	}

	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}
}
