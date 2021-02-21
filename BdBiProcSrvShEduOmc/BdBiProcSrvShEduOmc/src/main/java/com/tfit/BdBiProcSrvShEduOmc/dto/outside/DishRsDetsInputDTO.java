package com.tfit.BdBiProcSrvShEduOmc.dto.outside;

import java.util.Map;

import lombok.Data;

/**
 * 3.2.43.	菜品留样汇总信息模型
 * @author Administrator
 *
 */
@Data
public class DishRsDetsInputDTO {
	 //就餐开始日期
    String startSubDate;
    //就餐结束日期
    String endSubDate;
    //区域名称
    String distName;
    //地级城市
    String prefCity;
    //省或直辖市
    String province;        
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
    String schProp;        
    //项目点名称
    String ppName;
    //团餐公司名称
    String rmcName;
    //是否留样标识，0:未留样，1:已留样
    String rsFlag;
    //餐别，0:早餐，1:午餐，2:晚餐，3:午点，4:早点
    String caterType;
    //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
    String schType;
    //菜单名称
    String menuName;
    //经营模式（供餐类型），0:自营，1:外包-现场加工，2:外包-快餐配送
    String optMode;
    //留样单位
    String rsUnit;
    //页号
    String page;
    //分页大小
    String pageSize;
    //区域名称 格式：[“1”,”2”……]
    String distNames;
    //所属 格式：[“1”,”2”……]
    String subLevels;
    //主管部门 格式：[“1”,”2”……]
    String compDeps;
    //学校性质 格式：[“1”,”2”……]
    String schProps;
    //餐别 格式：[“1”,”2”……]
    String caterTypes;
    //学校类型 格式：[“1”,”2”……]
    String schTypes;
    //菜单名称 格式：[“1”,”2”……]
    String menuNames;
    //经营模式 格式：[“1”,”2”……]
    String optModes;
    String departmentId;
    String departmentIds;
    String reserveStatus;
    Map<String,String> menuCaterTypeMap;
}
