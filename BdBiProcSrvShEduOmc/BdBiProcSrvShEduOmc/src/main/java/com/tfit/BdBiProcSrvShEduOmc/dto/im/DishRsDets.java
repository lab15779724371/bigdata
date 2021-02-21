package com.tfit.BdBiProcSrvShEduOmc.dto.im;

import lombok.Data;

@Data
public class DishRsDets {
	String repastDate;       //就餐日期，格式：xxxx/xx/xx
	String ppName;           //项目点名称
	String schGenBraFlag;    //总分校标识
	int braCampusNum;        //分校数量
	String relGenSchName;     //关联总校
	String subLevel;         //所属
	String compDep;          //主管部门
	String subDistName;      //所属区域名称
	String fblMb;            //证件主体，0:学校，1:外包
	String distName;         //区域名称或ID
	String schType;          //学校类型（学制）
	String schProp;          //学校性质
	String optMode;          //经营模式（供餐类型）
	String rmcName;          //团餐公司名称
	String caterType;        //餐别
	String menuName;         //菜单名称
	String dishName;         //菜品名称
	int dishNum;             //菜品份数
	int rsFlag;              //是否留样标识，0:未留样，1:已留样
	String rsNum;            //留样数量
	String rsTime;           //留样时间，格式：yyyy/MM/dd HH:mm
	String rsExplain;        //留样说明
	String rsUnit;           //留样单位
	String rsPerson;         //留样人
	String createtime;         //留样操作时间
	
	//操作状态 1 表示规范录入 2 表示补录 3 表示逾期补录 4 表示无数据
	String reserveStatus;
	//管理部门编号
	String departmentId;
	
}
