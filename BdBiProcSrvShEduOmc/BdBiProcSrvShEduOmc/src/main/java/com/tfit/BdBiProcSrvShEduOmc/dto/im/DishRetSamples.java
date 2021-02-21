package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class DishRetSamples {
	String repastDate;
	String subLevel;
	String compDep;
	String distName;
	int dishSchNum;
	//应留样学校
	int shouldRsSchNum;
	int noRsSchNum;
	int rsSchNum;
	//学校留样率
	float schRsRate;
	int menuNum;
	int rsMenuNum;
	int noRsMenuNum;
	float rsRate;
	
	String departmentId;
	String departmentName;
	//1 表示规范录入
	int schStandardNum;
	//2 表示补录
	int schSupplementNum;
	//3 表示逾期补录
	int schBeOverdueNum;
	//4 表示无数据
	int schNoDataNum;
	//5 标准规范学校验收率
	float schStandardRate;
	
}
