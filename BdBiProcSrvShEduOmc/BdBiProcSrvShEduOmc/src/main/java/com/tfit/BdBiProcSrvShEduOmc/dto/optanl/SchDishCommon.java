package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class SchDishCommon {
	//排菜日期
	String dishDate;
	//区
	String distId;
	//数量
	private Integer total;
	//学校学制
	String levelName;
	//学校性质:0 公办   2 民办 3 "外籍人员子女学校"， -1 表示空数据
	String schoolNatureName;
	//部门主管ID
	String departmentMasterId;
	//所属部/区/市级ID
	String departmentSlaveIdName;
	//是否供餐	0 不供餐  1供餐
	Integer haveClass;
	//是否排菜	0 未排菜  1排菜
	Integer havePlatoon;
	//1 表示规范录入 2 表示补录 3 表示逾期补录 4 表示无数据
	Integer platoonDealStatus;
	
	//主管部门
	String departmentId;
}
