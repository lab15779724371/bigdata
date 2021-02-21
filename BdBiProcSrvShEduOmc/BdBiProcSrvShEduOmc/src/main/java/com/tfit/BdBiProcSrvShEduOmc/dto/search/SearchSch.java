package com.tfit.BdBiProcSrvShEduOmc.dto.search;

import lombok.Data;

@Data
public class SearchSch {
	//学校编号
	String schoolId;
	//学校名称
	String schName;
	//区域名称
	String distName;
	//详细地址
	String detailAddr;
	//统一社会信用代码证
	String uscc;
	//学制
	String schType;
	//性质
	String schProp;
	//主管部门
	String subLevel;
	String compDep;
    //管理部门(暂时不取)
	String managerDep;
	//食品经营许可证主体
	String fblMb;
	//供餐模式
	String optMode;
	//法人代表
	String legalRep;
	//联系人
	String projContact;
	//联系电话
	String pcMobilePhone;
	//食品经营许可证
	String licNo;
}
