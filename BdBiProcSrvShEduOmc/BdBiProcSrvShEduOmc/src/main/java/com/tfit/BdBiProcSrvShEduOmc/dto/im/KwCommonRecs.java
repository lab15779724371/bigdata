package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class KwCommonRecs {
	String recDate;
	String distName;
	//回收次数
	int recyclerTotal;
	//回收数量
	Float recyclerSum;
	//1为教委端 2为团餐端
	int platformType;
	//1餐厨垃圾，2废弃油脂
	int type;
	//学校学制
	String levelName;
	//学校性质:0 公办   2 民办 3 "外籍人员子女学校"， -1 表示空数据
	String schoolNatureName;
	//部门主管ID
	String departmentMasterId;
	//所属部/区/市级ID
	String departmentSlaveIdName;

}
