package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import lombok.Data;

@Data
public class WarnMaterialOverProDets {
	String warnDate;
	String distName;
	String schName;
	String departmentId;
	//触发预警单位
	String trigWarnUnit;
	//配送批次
	String batchNo; 
	//司机
	String driverName;
	//车辆
	String carCode;
	//配送日期
	String batchDate;
	//状态:0：警示中 1：已消除
	String status;
	//消除日期，格式：xxxx/xx/xx
	String elimDate;
	//物料名称
	String materialName;
	//生产日期
	String productionDate;
	//保质期至
	String expirationDate;
	
}
