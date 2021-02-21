package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class SchMatCommon {
	//排菜日期
	String matDate;
	//区
	String distId;
	//数量
	private Integer total;
	//学校数量
	private Integer schoolTotal;
	//学校学制
	String levelName;
	//学校性质:0 公办   2 民办 3 "外籍人员子女学校"， -1 表示空数据
	String schoolNatureName;
	//部门主管ID
	String departmentMasterId;
	//所属部/区/市级ID
	String departmentSlaveIdName;
	//用料状态	0 信息不完整 1待确认  2 已确认'
	Integer status;
}
