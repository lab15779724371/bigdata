package com.tfit.BdBiProcSrvShEduOmc.model.enums;

/**
 * @Descritpion：废弃油脂类型
 * @author: tianfang_infotech
 * @date: 2019/1/7 15:26
 */
public enum WasteOilType {

    /**
     * 废弃油脂
     */
    UN_KNOW(0, "-"),

    /**
     * 废弃油脂
     */
    WASTE_OIL(1, "废油"),

    /**
     * 含油废水
     */
    OILY_WASTE_WATER(2, "含油废水"),
    ;

    WasteOilType(int code, String name) {
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
