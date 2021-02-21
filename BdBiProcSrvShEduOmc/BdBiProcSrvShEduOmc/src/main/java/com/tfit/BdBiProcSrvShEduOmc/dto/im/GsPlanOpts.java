package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class GsPlanOpts {
	String distrDate;
	String subLevel;
	String compDep;	
	String distName;	
	int dishSchNum;
	int conSchNum;
	//应验收学校
	int shouldAcceptSchNum;	
	int noAcceptSchNum;
	int acceptSchNum;	
	//学校验收率	
	float schAcceptRate;
	int totalGsPlanNum;
	int assignGsPlanNum;
	float assignRate;
	int dispGsPlanNum;
	float dispRate;
	int noAcceptGsPlanNum;
	int acceptGsPlanNum;
	float acceptRate;
	
	String departmentId;
	
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
