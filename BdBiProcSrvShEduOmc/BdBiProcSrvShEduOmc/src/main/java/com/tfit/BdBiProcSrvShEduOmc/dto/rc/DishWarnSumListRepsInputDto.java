package com.tfit.BdBiProcSrvShEduOmc.dto.rc;

import com.tfit.BdBiProcSrvShEduOmc.dto.ParentObj;

import lombok.Data;

@Data
public class DishWarnSumListRepsInputDto extends ParentObj {
	//开始排菜日期
    private String startDishDate;
    //结束排菜日期
    private String endDishDate;
    //管理部门
    private String departmentId;
    
    
    //学校区
    private String area;
}