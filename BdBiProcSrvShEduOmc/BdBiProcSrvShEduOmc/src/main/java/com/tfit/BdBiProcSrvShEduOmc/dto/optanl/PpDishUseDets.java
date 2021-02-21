package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

@Data
public class PpDishUseDets {
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
	String optMode;
	String rmcName;
	//详细地址
	String detailAddr;
	//项目联系人
	String projContact;
	//手机
	String pcMobilePhone;
	//操作时间
	String createtime;
	//是否排菜，0:未排菜，1:已排菜
	int dishFlag;
	//用料确认情况 0:未确认，1:已确认
	Integer materialStatus;
	//配送状态 0:未配送，1:已配送
	Integer dispPlanStatus;
	//指派状态 0:未派送，1:已派送
	Integer assignPlanStatus;
	//验收情况 0:未验收，1:已验收
	Integer acceptStatus;
	//是否供餐 0：不供餐 1：供餐
	int mealFlag;
	//是否留样 0：否 1：是
	int haveReserve;
	//供餐备注
	String reason;
	
	//管理部门
	String departmentId;
}
