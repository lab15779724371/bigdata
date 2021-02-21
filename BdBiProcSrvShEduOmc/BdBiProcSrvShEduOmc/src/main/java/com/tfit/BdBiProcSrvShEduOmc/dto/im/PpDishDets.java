package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class PpDishDets {
	//项目UUID
	String schoolId;
	String dishDate;
	String ppName;	
	String schGenBraFlag;
	int braCampusNum;
	String relGenSchName;
	String subLevel;
	String compDep;
	String subDistName;
	String fblMb;
	String distName;
	String schType;
	String schProp;
	int mealFlag;
	String optMode;
	String rmcName;
	int dishFlag;
	//详细地址
	String detailAddr;
	//项目联系人
	String projContact;
	//手机
	String pcMobilePhone;
	//操作时间
	String createtime;
	
	//不供餐原因
	String reason;
	//操作状态 1 表示规范录入 2 表示补录 3 表示逾期补录 4 表示无数据
	String plaStatus;
	//管理部门编号
	String departmentId;
	
}
