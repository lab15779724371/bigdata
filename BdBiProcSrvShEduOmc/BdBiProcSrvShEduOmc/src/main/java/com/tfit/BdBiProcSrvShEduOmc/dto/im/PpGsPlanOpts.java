package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;
@Data
public class PpGsPlanOpts {
	//配货日期，格式：xxxx/xx/xx
	String distrDate;
	//区域名称
	String distName;
	//学校类型（学制）
	String schType;
	//项目点名称
	String ppName;
	//详细地址
	String detailAddr;
	//项目联系人
	String projContact;
	//手机
	String pcMobilePhone;
	//配货计划数量
	Integer distrPlanNum;
	//验收状态，0:待验收，1:已验收
	Integer acceptStatus;
	//已验收数量
	Integer acceptPlanNum;
	//未验收数量
	Integer noAcceptPlanNum;
	//指派状态，0:未指派，1：已指派
	Integer assignStatus;
	//已指派数量 
	Integer assignPlanNum;
	//未指派数量
	Integer noAssignPlanNum;
	//配送状态，0:未派送，1: 已配送
	Integer dispStatus;
	//已配送数量
	Integer dispPlanNum;
	//未配送数量
	Integer noDispPlanNum;
	
	//操作状态 1 表示规范录入 2 表示补录 3 表示逾期补录 4 表示无数据
	String plaStatus;
	//管理部门编号
	String departmentId;
	
}
