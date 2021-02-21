package com.tfit.BdBiProcSrvShEduOmc.obj.base;

import lombok.Data;

@Data
public class TEduBdUserLableRelationObj {
    private String userId;

    private Integer lableId;

    private String creator;

    private String createTime;

    private String updater;

    private String lastUpdateTime;

}