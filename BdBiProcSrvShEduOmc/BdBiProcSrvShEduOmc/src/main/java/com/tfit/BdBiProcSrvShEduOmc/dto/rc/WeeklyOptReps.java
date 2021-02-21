package com.tfit.BdBiProcSrvShEduOmc.dto.rc;

import lombok.Data;

@Data
public class WeeklyOptReps {
	String repId;
	//报表日期区间
	String repPeriod;
	//报名名称
	String repName;
	//报表URL
	String repUrl;
	//报表生成时间
	String creatTime;
	
	//年份
	String year;
	//月份
	String month;
	//报告类型
	String departmentName;
	
	//日期
	String date;
}
