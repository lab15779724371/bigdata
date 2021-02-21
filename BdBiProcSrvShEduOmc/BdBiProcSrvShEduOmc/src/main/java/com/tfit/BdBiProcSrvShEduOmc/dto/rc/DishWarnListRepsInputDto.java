package com.tfit.BdBiProcSrvShEduOmc.dto.rc;

import com.tfit.BdBiProcSrvShEduOmc.dto.ParentObj;

import lombok.Data;

@Data
public class DishWarnListRepsInputDto extends ParentObj {
	//开始排菜日期
    private String startWarnDate;
    //结束排菜日期
    private String endWarnDate;
    //管理部门
    private String departmentId;
    //预警类型：  1, "提示" 2, "提醒" 3, "预警" 4, "督办" 5, "追责"
    private String warnLevel;
    //学校区
    private String area;
}