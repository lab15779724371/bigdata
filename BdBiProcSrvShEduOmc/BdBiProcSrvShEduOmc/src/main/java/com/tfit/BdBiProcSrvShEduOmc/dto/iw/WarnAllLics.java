package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import lombok.Data;

@Data
public class WarnAllLics {
	
	public WarnAllLics() {
		this.totalWarnNum = 0;
		this.totalWarnNum= 0;
		this.noProcWarnNum= 0;
		this.rejectWarnNum= 0;
		this.auditWarnNum= 0;
		this.elimWarnNum= 0;
		this.warnProcRate= 0;
	}
	
	String warnPeriod;
	String distName;
	String departmentId;
	String departmentName;
	int totalWarnNum;
	int noProcWarnNum;
	int rejectWarnNum;
	int auditWarnNum;
	int elimWarnNum;
	float warnProcRate;
}
