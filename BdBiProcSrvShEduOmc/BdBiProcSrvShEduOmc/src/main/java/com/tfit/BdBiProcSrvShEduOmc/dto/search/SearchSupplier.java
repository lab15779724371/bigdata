package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import lombok.Data;

@Data
public class SearchSupplier {
	//团餐公司（或供应商）编号
	String supplierId;
	///团餐公司（或供应商）名称
	String supplierName;
	//服务起止时间
	String serviceDate;
	//区域名称
	String distName;
	//详细地址
	String detailAddr;
	//统一社会信用代码证
	String uscc;
	//食品经营许可证
	String fblNo;
	//食品经营许可证有效日期
	String fblExpireDate;
	//法人代表
	String legalRep;
	//联系人
	String contact;
	//联系电话
	String mobilePhone;
	
	//-----供应商特有
	//食品生产许可证编号
	String fplNo;
	//食品流通许可证
	String fcpNo;

}
