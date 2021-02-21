package com.tfit.BdBiProcSrvShEduOmc.model.vo;

import lombok.Data;

/**
 * @Descritpion：供应商基本信息
 * @author: tianfang_infotech
 * @date: 2019/1/24 12:01
 */
@Data
public class SupplierBasic {

    /**
     * 供应商主键
     */
    private String supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

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
}
