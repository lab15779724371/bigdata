package com.tfit.BdBiProcSrvShEduOmc.dto.bd;

import lombok.Data;

@Data
public class DepartmentList {
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
    //修改人编号
    private String updater;
    //最后修改时间
    private String lastUpdateTime;
    
    //修改人姓名
    private String updaterName;
}
