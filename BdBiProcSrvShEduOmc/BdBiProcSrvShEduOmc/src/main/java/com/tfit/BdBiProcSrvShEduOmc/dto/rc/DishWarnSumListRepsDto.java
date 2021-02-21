package com.tfit.BdBiProcSrvShEduOmc.dto.rc;

import lombok.Data;

@Data
public class DishWarnSumListRepsDto {
	//序号
	private Integer sortNo;
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
    //备注
    private String remark = "";
    
}
