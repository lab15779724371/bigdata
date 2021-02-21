package com.tfit.BdBiProcSrvShEduOmc.dto.im.week;

import com.tfit.BdBiProcSrvShEduOmc.dto.ParentObj;

import lombok.Data;

@Data
public class AppTEduPlatoonTotalWObj extends ParentObj {
	//开始排菜日期
    private String startUseDate;
    //结束排菜日期
    private String endUseDate;
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
    //总/分校
    private Integer  schGenBraFlag;
    
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
    
}