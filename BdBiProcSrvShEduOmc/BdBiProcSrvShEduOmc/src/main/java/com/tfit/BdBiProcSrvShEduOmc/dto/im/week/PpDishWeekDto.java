package com.tfit.BdBiProcSrvShEduOmc.dto.im.week;

import lombok.Data;

@Data
public class PpDishWeekDto {
    private String UseDatePeriod;
    
    //学校id
    private String schoolId;
    //学校名字
    private String schoolName;
    //管理部门
    private String departmentId;
    //学校区
    private String area;
    //学制编号
    private String levelId;
    //学校学制
    private String levelName;
    //学校性质
    private String schoolNatureId;
    //学校性质
    private String schoolNatureName;
    //民办性质小类
    private String schoolNatureSubName;
    //总校关联的分校数量
    private Integer branchTotal;
    //部门主管ID
    private String departmentMasterId;
    //主管部门名称
    private String departmentMasterName;
    //所属部/区/市级ID
    private String departmentSlaveId;
    //所属名称
    private String departmentSlaveIdName;
    //所属区ID
    private String schoolAreaId;
    //所属区域名称
    private String schoolAreaName;
    //食品许可证件主体的类型
    private String licenseMainType;
    //食品经营许可证主体名称
    private String licenseMainTypeName;
    //供餐模式
    private Integer licenseMainChild;
    //供餐模式名称
    private String licenseMainChildName;
    //应排菜总数
    private Integer haveClassTotal;	
    //已排菜总数
    private Integer havePlatoonTotal;
    //未排菜总数
    private Integer haveNoPlatoonTotal;
    //规范录入总数
    private Integer guifanPlatoonTotal;
    //补录总数
    private Integer buluPlatoonTotal;
    //逾期补录总数
    private Integer yuqiPlatoonTotal;
    //无数据总数
    private Integer noPlatoonTotal;
    //学校地址
    private String address;
    //项目联系人姓名
    private String foodSafetyPersion;	
    //手机
    private String foodSafetyMobilephone;
}
