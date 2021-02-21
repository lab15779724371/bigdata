package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class GsPlanOptCommonDets {
	String distrDate;
	//批次号
	String distrBatNumber;
	String ppName;	
	String schGenBraFlag;
	int braCampusNum;
	String relGenSchName;
	String subLevel;
	String compDep;
	String subDistName;
	String fblMb;
	String schProp;	
	String schType;
	String distName;
	String rmcName;
	//配送类型，0:原料，1:成品菜
	String dispType;
	//配送方式，0:统配，1:直配
	String dispMode;
	Integer haulStatus;
	int sendFlag;
	
	//验收时间
	String acceptTime;
	//验收操作规则规则
	String disDealStatus;
	//管理部门编号
	String departmentId;
	
}
