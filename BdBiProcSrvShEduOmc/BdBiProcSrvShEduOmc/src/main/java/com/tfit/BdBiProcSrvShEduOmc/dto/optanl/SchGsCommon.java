package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class SchGsCommon {
	//配货日期
	String actionDate;
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
	//状态:-2 信息不完整 -1 未指派 0 已指派（未配送） 1配送中 2 待验收（已配送）3已验收 -4已取消
	Integer haulStatus;
	
	String disSealStatus;
	//管理部门编号
	String departmentId;
}
