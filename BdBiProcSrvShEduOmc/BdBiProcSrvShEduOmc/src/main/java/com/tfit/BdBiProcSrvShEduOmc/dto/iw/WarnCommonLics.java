package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import lombok.Data;

@Data
public class WarnCommonLics {
	
	public WarnCommonLics() {
		this.totalWarnNum = 0;
		this.totalWarnNum= 0;
		this.noProcWarnNum= 0;
		this.rejectWarnNum= 0;
		this.auditWarnNum= 0;
		this.elimWarnNum= 0;
		this.warnProcRate= 0;
		this.noProcUnitNum= 0;
	}
	
	String warnPeriod;
	//区域名称
	String distName;
	//学校性质
	String nature;
	//学制
	String level;
	//预警总数
	int totalWarnNum;
	//未处理预警数
	int noProcWarnNum;
	//已驳回预警数
	int rejectWarnNum;
	//审核中预警数
	int auditWarnNum;
	//已消除预警数
	int elimWarnNum;
	//预警处理率
	float warnProcRate;
	//未处理预警单位
	int noProcUnitNum;
}
