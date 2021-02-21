package com.tfit.BdBiProcSrvShEduOmc.dao.domain.saas;

import lombok.Data;

/**
 * @Descritpion：学校供应商信息
 * @author: tianfang_infotech
 * @date: 2019/1/10 12:04
 */
@Data
public class EduSchoolSupplier {

    /**
     * 学校编号
     */
    private String schoolId;

    /**
     * 供应商编号
     */
    private String supplierId;

    /**
     * 学校名称
     */
    //private String schoolName;

    /**
     * 项目点名称
     */
    //private String projectName;

    /**
     * 团餐公司(供应商)名称
     */
    private String supplierName;

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

	/*public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}*/

	/*public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}*/

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
}
