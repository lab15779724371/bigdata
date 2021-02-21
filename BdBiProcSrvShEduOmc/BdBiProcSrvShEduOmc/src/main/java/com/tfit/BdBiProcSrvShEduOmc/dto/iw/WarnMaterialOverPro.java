package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import lombok.Data;

@Data
public class WarnMaterialOverPro {
	
	public WarnMaterialOverPro() {
		this.totalWarnNum = 0;
		this.totalWarnNum= 0;
		this.noProcWarnNum= 0;
		this.elimWarnNum= 0;
		this.warnProcRate= 0;
		
	}
	String warnPeriod;//日期
	String departmentId;//管理部门编号
	String departmentName;//管理部门名称
	int totalWarnNum;//预警总数
	int noProcWarnNum;//预警中
	int elimWarnNum;//已消除预警数
	float warnProcRate;//处理率
}
