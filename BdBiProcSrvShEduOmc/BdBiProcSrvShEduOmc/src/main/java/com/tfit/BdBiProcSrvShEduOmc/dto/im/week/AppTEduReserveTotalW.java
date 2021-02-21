package com.tfit.BdBiProcSrvShEduOmc.dto.im.week;

import com.tfit.BdBiProcSrvShEduOmc.dto.ParentObj;

import lombok.Data;

@Data
public class AppTEduReserveTotalW extends ParentObj{
	//就餐开始时间
    private String startUseDate;
    //就餐结束时间
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
    //应留样天数
    private Integer reserveDayTotal;
    //已留样天数
    private Integer haveReserveDayTotal;
    //未留样天数
    private Integer haveNoReserveDayTotal;
    //留样规范录入总数
    private Integer guifanReserveTotal;
    //留样补录总数
    private Integer buluReserveTotal;
    //留样逾期补录总数
    private Integer yuqiReserveTotal;
    //留样无数据总数
    private Integer noReserveTotal;
    //学校地址
    private String address;
    //项目联系人姓名
    private String foodSafetyPersion;
    //手机
    private String foodSafetyMobilephone;
    //是否分校
    private Integer isBranchSchool;
    //关联的总校id
    private String parentId;
    //关联的总校名
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