package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import lombok.Data;

@Data
public class WarnAllLicDets {
	String warnDate;
	String distName;
	String schName;
	String trigWarnUnit;
	String licName;
	String licNo;
	String validDate;
	String licStatus;
	int licAuditStatus;
	String elimDate;
	
	//学制
	String schType;
	//学校性质
	String schProp;
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
}
