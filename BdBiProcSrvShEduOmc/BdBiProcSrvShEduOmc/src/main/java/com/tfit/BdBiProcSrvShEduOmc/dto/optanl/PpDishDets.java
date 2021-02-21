package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

import lombok.Data;

@Data
public class PpDishDets {
    //开始日期
    String startSubDate ;
    //结束日期
    String endSubDate;
    //项目点名称
    String ppName ;
    //区域名称
    String distName;
    //地级城市
    String prefCity;
    //省或直辖市
    String province ;
    //所属，0:其他，1:部属，2:市属，3: 区属
    String subLevel;
    //主管部门，0:市教委，1:商委，2:教育部
    String compDep;
    //总分校标识，0:无，1:总校，2:分校
    String schGenBraFlag;
    //所属区域名称
    String subDistName;
    //证件主体，0:学校，1:外包
    String fblMb;
    //学校性质，0:公办，1:民办，2:其他
    String schProp ;        
    //是否排菜，0:未排菜，1:已排菜
    String dishFlag ;
    //团餐公司编号
    String rmcId ;
    //团餐公司名称
    String rmcName ;
    //学校类型（学制）
    String schType;
    //是否供餐，0:否，1:是
    String mealFlag ;
    //经营模式
    String optMode;
    //发送状态，0:未发送，1:已发送
    String sendFlag ;

    
    //区域名称 格式：[“1”,”2”……]
    String distNames;
    //所属 格式：[“1”,”2”……]
    String subLevels ;
    //主管部门 格式：[“1”,”2”……]
    String compDeps ;
    //学校性质 格式：[“1”,”2”……]
    String schProps;
    //学校类型 格式：[“1”,”2”……]
    String schTypes ;
    //经营模式 格式：[“1”,”2”……]
    String optModes ;
    //所属区(sub) 格式：[“1”,”2”……]
    String subDistNames;
    //学校编号
    String schoolId;
    //用料计划确认情况，0:待确认，1:已确认
    Integer materialStatus;
    //验收情况，0:未验收，1:已验收
    Integer acceptStatus;
    //指派情况，0:未指派，1:已指派
    Integer assignStatus;
    //配送情况，0:未配送，1:已配送
    Integer dispStatus;
	//是否留样 0：否 1：是
    Integer haveReserve;
	//不供餐原因
	String reason;
	//管理部门
	String departmentIds;
    
    String page;
    String pageSize;
}
