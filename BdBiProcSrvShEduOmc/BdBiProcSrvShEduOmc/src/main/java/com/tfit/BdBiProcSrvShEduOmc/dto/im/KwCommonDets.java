package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class KwCommonDets {
	//回收日期，格式：xxxx/xx/xx
	String recDate;
	//区域名称
	String distName;
	//项目点名称
	String ppName;
	//总分校标识
	String schGenBraFlag;
	//分校数量
	int braCampusNum;
	//关联总校
	String relGenSchName;
	//所属
	String subLevel;
	//主管部门
	String compDep;
	//所属区域名称
	String subDistName;
	//学校类型（学制）
	String schType;
	//学校性质
	String schProp;
	//团餐公司名称
	String rmcName;
	//回收数量，单位：桶 修改原因：hive库中可能会有带加号和带单位的值
	String recNum;
	//回收单位
	String recComany;
	//回收人
	String recPerson;
	//回收单据数
	Integer recBillNum;
	//1为教委端 2为团餐端
	int platformType;
	//1餐厨垃圾，2废弃油脂
	int type;
	//废弃油脂种类，0:废油，1:含油废水
   String woType;
   
   //学校对应的团餐公司编号
   String schSupplierId;
	
}
