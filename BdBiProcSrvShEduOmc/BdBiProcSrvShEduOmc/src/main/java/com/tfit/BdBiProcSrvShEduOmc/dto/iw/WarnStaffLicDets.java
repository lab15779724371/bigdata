package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import lombok.Data;

@Data
public class WarnStaffLicDets {
	String warnDate;
	String distName;
	String rmcName;
	String relSchName;
	String licName;
	String fullName;
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
	//触发预警单位
	String trigWarnUnit;
    //学校名称
	String schName;
}
