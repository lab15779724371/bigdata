package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import lombok.Data;

@Data
public class BdSchList {
	String schStdName;
	String schGenBraFlag;
	int braCampusNum;
	String relGenSchName;
	String distName;
	String detailAddr;
	String uscc;
	String schType;
	String schProp;
	String subLevel;
	String compDep;
	String subDistName;
	String remark;
	String fblMb;
	String optMode;
	int studentNum;
	int teacherNum;
	String legalRep;
	String lrMobilePhone;
	String lrFixPhone;
	String depHeadName;
	String dhnMobilePhone;
	String dhnFixPhone;
	String dhnFax;
	String dhnEmail;
	String projContact;
	String pcMobilePhone;
	String pcFixPhone;
	int isSetsem;
	String licName;
	String schPic;
	String optUnit;
	String licNo;
	String licIssueAuth;
	String licIssueDate;
	String validDate;
	String relCompName;
	//编号
	String id;
	//管理部门编号
	String departmentId;
	//管理部门名称
	String departmentName;
}
