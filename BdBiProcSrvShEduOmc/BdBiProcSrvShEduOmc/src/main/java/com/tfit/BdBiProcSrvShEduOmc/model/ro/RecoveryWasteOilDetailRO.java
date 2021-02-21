package com.tfit.BdBiProcSrvShEduOmc.model.ro;

import lombok.Data;

import java.util.Date;

/**
 * @Descritpion：回收废弃油脂Redis模型
 * @author: tianfang_infotech
 * @date: 2019/1/7 17:59
 */
@Data
public class RecoveryWasteOilDetailRO {

    /**
     * 回收日期
     */
    private Date recDate;

    /**
     * 区/县code
     */
    private Integer area;

    /**
     * 学校编号/团餐公司编号
     */
    private String schoolId;

    /**
     * 回收数量
     */
    private String number;

    /**
     * 回收单位
     */
    private String receiverName;

    /**
     * 回收人
     */
    private String contact;

    /**
     * 回收单据数量
     */
    private Integer documents;

    /**
     * 废弃油脂类型
     */
    private Integer secondType;

	public Date getRecDate() {
		return recDate;
	}

	public void setRecDate(Date recDate) {
		this.recDate = recDate;
	}

	public Integer getArea() {
		return area;
	}

	public void setArea(Integer area) {
		this.area = area;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public Integer getDocuments() {
		return documents;
	}

	public void setDocuments(Integer documents) {
		this.documents = documents;
	}

	public Integer getSecondType() {
		return secondType;
	}

	public void setSecondType(Integer secondType) {
		this.secondType = secondType;
	}
}
