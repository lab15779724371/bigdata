package com.tfit.BdBiProcSrvShEduOmc.model.enums;

/**
 * @Descritpion：身份结构类型(0：总校; 1：分校)
 * @author: tianfang_infotech
 * @date: 2019/1/8 17:03
 */
public enum SchoolStructType {
    /**
     * 总校
     */
    GENERAL_SCHOOL(0, "总校"),

    /**
     * 分校
     */
    BRANCH_SCHOOL(1, "分校"),
    ;

    SchoolStructType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private int code;

    private String name;
}
