package com.tfit.BdBiProcSrvShEduOmc.dto.im.week;

import com.tfit.BdBiProcSrvShEduOmc.dto.ParentObj;

import lombok.Data;

@Data
public class AppTEduLedgerMasterTotalWObj extends ParentObj {
	//开始排菜日期
    private String startActionDate;
    //结束排菜日期
    private String endActionDate;
    //学校id
    private String schoolId;
    //学校名字
    private String schoolName;
    //管理部门
    private String departmentId;
    //学校区
    private String area;
    //学校学制
    private String levelName;
    //学校性质
    private String schoolNatureName;
    //民办性质小类
    private String schoolNatureSubName;
    //总校关联的分校数量
    private int isBranchSchool;
    //总校、分校
    private Integer branchTotal;
    //部门主管ID
    private String departmentMasterId;
    //所属部/区/市级ID
    private String departmentSlaveIdName;
    //所属区ID
    private String schoolAreaId;
    //食品许可证件主体的类型
    private String licenseMainType;
    //供餐模式
    private Integer licenseMainChild;
    //应验收天数
    private Integer ledgerDayTotal;
    //已验收天数
    private Integer haveLedgerDayTotal;
    //未验收天数
    private Integer haveNoLedgerDayTotal;
    //验收规范录入总数
    private Integer guifanLedgerTotal;
    //验收补录总数
    private Integer buluLedgerTotal;
    //验收逾期补录总数
    private Integer yuqiLedgerTotal;
    //验收无数据总数
    private Integer noLedgerTotal;
    //学校地址
    private String address;
    //项目联系人姓名
    private String foodSafetyPersion;
    //手机
    private String foodSafetyMobilephone;
    //关联总校编号
    private String parentId;
    //关联总校名称
    private String parentName;
    
    /**
     * 查询条件
     */
    //学校性质（多选）
    private String schProps;
    //学制（多选）
    private String schTypes;
    //主管部门（多选）
    private String subLevels;
    //所属（多选）
    private String compDeps;
	//学校区（多选）
    private String subDistNamesList;
    //管理部门（多选）
    private String departmentIdList;
    //区（多选）
    private String distNames;
    //经营模式（多选）
    private String optModesList;
    //学制编号
    private String levelId;
    //学校性质编号
    private String schoolNatureId;
    //主管部门编号
    private String departmentSlaveId;
    //性质子类编号
    private String schoolNatureSubId;
    //总/分校
    private Integer  schGenBraFlag;

}