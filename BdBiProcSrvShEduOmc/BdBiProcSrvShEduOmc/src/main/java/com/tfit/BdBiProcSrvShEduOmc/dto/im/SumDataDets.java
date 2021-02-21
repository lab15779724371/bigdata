package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class SumDataDets {
	String dishDate;
	String subLevel;
	String compDep;
	String distName;
	int mealSchNum;
	int dishSchNum;
	int noDishSchNum;
	float dishRate;
	//1 表示规范录入
	int standardNum;
	//2 表示补录
	int supplementNum;
	//3 表示逾期补录
	int beOverdueNum;
	//4 表示无数据
	int noDataNum;
	
	int totalGsPlanNum;
	int noAcceptGsPlanNum;
	int acceptGsPlanNum;
	float acceptRate;
	//应验收学校数
	private Integer shouldAcceptSchNum;
	//已验收学校数
	private Integer acceptSchNum;
	//未验收学校数
	private Integer noAcceptSchNum;
	//学校验收率
	private Float schAcceptRate;
	int totalDishNum;
	int noRsDishNum;
	int rsDishNum;
	float rsRate;	
	 // 应留样学校数
	private Integer shouldRsSchNum;
	 // 已留样学校数
	private Integer rsSchNum;
	
	 // 未留样学校数
	private Integer noRsSchNum;
	 // 学校留样率
	private Float schRsRate;
	int totalWarnNum;
	int noProcWarnNum;
	int elimWarnNum;
	float warnProcRate;	
	Float totalKwRecNum;
	Float kwSchRecNum;
	Float kwRmcRecNum;
	Float totalWoRecNum;
	Float woSchRecNum;
	Float woRmcRecNum;
	
}
