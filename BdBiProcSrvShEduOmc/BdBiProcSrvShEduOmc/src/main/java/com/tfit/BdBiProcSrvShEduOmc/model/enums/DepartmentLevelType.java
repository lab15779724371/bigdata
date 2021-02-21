package com.tfit.BdBiProcSrvShEduOmc.model.enums;

/**
 * @Descritpion：部门等级(3:区级 2:市级 1:部级 0 其它;)
 * @author: tianfang_infotech
 * @date: 2019/1/10 20:35
 */
public enum DepartmentLevelType {
    /**
     * 其它
     */
    OTHER_LEVEL(0, "其它"),

    /**
     * 部级
     */
    MINISTRY_LEVEL(1, "部级"),

    /**
     * 市级
     */
    CITY_LEVEL(2, "市级"),

    /**
     * 区级
     */
    DISTRICT_LEVEL(3, "区级"),
    ;

    DepartmentLevelType(int code, String name) {
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
