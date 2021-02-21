package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class PpDishCommonDets {
	//项目UUID
	String schoolId;
	String dishDate;
	String ppName;	
	//总校、分校
	String schGenBraFlag;
	//分校个数
	int braCampusNum;
	//关联学校
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
	
	//配货计划数量
	Integer distrPlanNum;
	//已验收数量
	Integer acceptPlanNum;
	//已指派数量
	Integer assignPlanNum;
	//已配送数量
	Integer dispPlanNum;
    //配送状态
	Integer haulStatus;
	
	//留样总数
	Integer reserveTotal;
	//未留样数量
	Integer noreserveTotal;
	//已留样数量
	Integer haveReserveTotal;
	//是否留样
	Integer haveReserve;
	//供餐备注
	String reason;
	//
	Integer materialStatus;
	
	//操作状态
	String plaStatus;
	//管理部门
	String departmentId;
	//验收操作规则规则
	String disDealStatus;
	
	
}
