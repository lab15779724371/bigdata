package com.tfit.BdBiProcSrvShEduOmc.obj.base;

import lombok.Data;

@Data
public class DepartmentObj {
	//主管部门编号
    private String departmentId;
    //主管部门名称
    private String departmentName;
    //备注
    private String remark;
    //创建人
    private String creator;
    //创建时间
    private String createTime;
    //修改人
    private String updater;
    //最后修改时间
    private String lastUpdateTime;

}