package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class PpDishList {
	String dishDate;
	String subLevel;
	String compDep;
	String distName;
	int regSchNum;
	int mealSchNum;
	int dishSchNum;
	int noDishSchNum;
	float dishRate;
	
	//管理部门编号
	String departmentId;
	
	//1 表示规范录入
	int standardNum;
	//2 表示补录
	int supplementNum;
	//3 表示逾期补录
	int beOverdueNum;
	//4 表示无数据
	int noDataNum;
	float standardRate;
	
}
