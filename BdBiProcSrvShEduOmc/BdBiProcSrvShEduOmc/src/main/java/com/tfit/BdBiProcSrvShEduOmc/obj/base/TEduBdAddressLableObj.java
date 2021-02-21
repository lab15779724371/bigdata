package com.tfit.BdBiProcSrvShEduOmc.obj.base;

import java.util.List;

import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSimpleObj;

import lombok.Data;

@Data
public class TEduBdAddressLableObj {
    private Integer id;

    //标签名称
    private String lableName;

    private Integer stat = 1;

    private String remark;

    private String creator;

    private String createTime;

    private String updater;

    private String lastUpdateTime;
    
    //人员数量
    private String userCount;
    //人员列表
    private List<UserSimpleObj> userList;

    //人员列表
    private List<String> userIdList;

}