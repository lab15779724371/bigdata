package com.tfit.BdBiProcSrvShEduOmc.dto.im.week;

import lombok.Data;

@Data
public class PpGsPlanWeekDto {
    private String actionDatePeriod;
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
    //总校、分校
    private int isBranchSchool;
    //总校关联的分校数量
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
    
    //学制编号
    private String levelId;
    //学校性质编号
    private String schoolNatureId;
    //主管部门编号
    private String departmentSlaveId;
    //性质子类编号
    private String schoolNatureSubId;
    //供餐模式名称
    private String licenseMainChildName;
    //食品经营许可证主体名称
    private String licenseMainTypeName;
    //所属区域名称
    private String schoolAreaName;
    //主管部门名称
    private String departmentMasterName;
    //总校、分校
    private String isBranchSchoolName;
}
