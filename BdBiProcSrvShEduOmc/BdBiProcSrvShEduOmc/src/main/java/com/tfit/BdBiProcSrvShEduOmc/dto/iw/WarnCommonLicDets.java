package com.tfit.BdBiProcSrvShEduOmc.dto.iw;

import lombok.Data;

@Data
public class WarnCommonLicDets {
	String warnDate;
	String distName;
	String schType;
	String schProp;
	String schName;
	//证照类型
	String licName;
	String licNo;
	//有效日期，格式：xxxx-xx-xx
	String validDate;
	//状态
	String licStatus;
	//审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
	int licAuditStatus;
	//消除日期，格式：xxxx/xx/xx
	String elimDate;
	
	//------团餐公司证照类别特有属性
	//团餐公司名称
	String rmcName;
	
	//------人员证照列表特有属性
	//关联学校
	String relSchName;
	//人员名称（证件主体）
	String fullName;
	
	//----全部证照列表特殊属性
	String trigWarnUnit;
	
	String departmentId;
	//配送批次
	String batchNo; 
	//司机
	String driverName;
	//车辆
	String carCode;
	//配送日期
	String batchDate;
	//物料名称
	String materialName;
	//生产日期
	String productionDate;
	//保质期至
	String expirationDate;
	
	
	//新增
	//地址
	String address;
	//联系人
	String foodSafetyPersion;
	//手机号码
	String foodSafetyMobilephone;
	
	//排菜日期
	String dinnerDate;
	//验收日期
	String deliveryDate;
	//学制编号
	Integer schTypeId;
	
	//五级预警类型名
	String warnLevelName;
	
}
