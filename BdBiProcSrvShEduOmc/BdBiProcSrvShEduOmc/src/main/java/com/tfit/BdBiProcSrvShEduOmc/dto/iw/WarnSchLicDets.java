package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import lombok.Data;

@Data
public class WarnSchLicDets {
	String warnDate;
	String distName;
	String schType;
	String schProp;
	String schName;
	String licName;
	String licNo;
	//有效日期，格式：xxxx-xx-xx
	String validDate;
	//状态
	String licStatus;
	//审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
	int licAuditStatus;
	//消除日期，格式：xxxx/xx/xx
	String elimDate;
	
	//管理部门
	String departmentId;
	//地址
	String address;
	//联系人
	String foodSafetyPersion;
	//手机号码
	String foodSafetyMobilephone;
	//证件主体
	String fullName;
	//触发预警单位
	String trigWarnUnit;
	
}
