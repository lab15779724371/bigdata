package com.tfit.BdBiProcSrvShEduOmc.obj.opt;

import lombok.Data;

@Data
public class AppTEduReserveTotalD {
    private String useDate;

    private String area;

    private Integer total;

    private Integer schoolTotal;

    private Integer haveReserve;

    private String levelName;

    private String schoolNatureName;

    private String departmentMasterId;

    private String departmentSlaveIdName;

    private String departmentId;

    private String reserveDealStatus;

}