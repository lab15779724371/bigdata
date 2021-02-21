package com.tfit.BdBiProcSrvShEduOmc.model.enums;

/**
 * @Descritpion：身份类型
 * @author: tianfang_infotech
 * @date: 2019/1/4 17:53
 */
public enum OwnerType {
    /**
     * 学校
     */
    SCHOOL(1, "school"),

    /**
     * 团餐公司
     */
    RMC(2, "rmc"),
    ;

    OwnerType(int code, String name) {
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
