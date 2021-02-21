package com.tfit.BdBiProcSrvShEduOmc.dto.rc;

import lombok.Data;

@Data
public class DishWarnListRepsDto {
	//序号
	private Integer sortNo;
    //学校名字
    private String schName;
    //管理部门
    private String departmentId;
    //截止日期（预警时间）
    private String warnDate;
    //备注
    private String remark = "";
    
	//五级预警类型名
	String warnLevelName;
    
}
