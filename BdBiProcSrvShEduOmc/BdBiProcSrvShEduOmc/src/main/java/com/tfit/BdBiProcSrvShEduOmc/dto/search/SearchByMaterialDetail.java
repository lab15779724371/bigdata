package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import java.util.List;

import lombok.Data;

@Data
public class SearchByMaterialDetail {
	//学校名称
	String schName;
	//20:员工健康证
	Integer healthLicenseCount;
	//22:A1证  
	Integer aoneLicenseCount;
	//23:B证  
	Integer blicenseCount;
	//24:C证 
	Integer clicenseCount;
	//25:A2证
	Integer atwoLicenseCount;
	//2.学校列表
	List<SearchSch> schList;
	//3.证照信息列表
	List<SearchLicense> licenseList;
	//4.团餐公司信息列表
	List<SearchSupplier> rmcList;
	//5.供应商信息列表
	List<SearchSupplier> supplierList;
	//6.配送单明细列表
	List<SearchSupplyMatSup> supplyMatSupList;
}
