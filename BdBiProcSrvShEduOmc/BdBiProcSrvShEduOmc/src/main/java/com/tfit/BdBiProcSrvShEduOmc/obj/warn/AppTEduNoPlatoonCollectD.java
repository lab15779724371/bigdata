package com.tfit.BdBiProcSrvShEduOmc.obj.warn;

import com.tfit.BdBiProcSrvShEduOmc.dto.ParentObj;

import lombok.Data;

@Data
public class AppTEduNoPlatoonCollectD extends ParentObj  {
	//预警时间
    private String warnDate;
    //管理部门
    private String departmentId;
    //学校id
    private String schoolId;
    //学校名字
    private String schoolName;
    //提示预警
    private Integer warnPrompt;
    //提醒预警
    private Integer warnRemind;
    //预警
    private Integer warnEarly;
    //督办预警
    private Integer warnSupervise;
    //追责预警
    private Integer warnAccountability;
    
	//预警开始时间
    private String warnStartDate;
	//预警结束时间
    private String warnEndDate;
	//管理部门模式：0：全市（排除市属中职校） 20：市属中职校
    private String departmentMode;
    
    
    //学校区
    private String area;

}