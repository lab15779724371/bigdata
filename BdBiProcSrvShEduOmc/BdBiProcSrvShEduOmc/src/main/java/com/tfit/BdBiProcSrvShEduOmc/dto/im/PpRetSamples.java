package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class PpRetSamples {
	//就餐日期，格式：xxxx/xx/xx
	String repastDate;
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
	//是否留样标识，0:未留样，1:已留样
	int rsFlag;
	//菜品数量
	int dishNum;
	//已留样菜品数
	int rsDishNum;
	//未留样菜品数
	int noRsDishNum;
	
	//操作状态 1 表示规范录入 2 表示补录 3 表示逾期补录 4 表示无数据
	String reserveStatus;
	//管理部门编号
	String departmentId;
}
