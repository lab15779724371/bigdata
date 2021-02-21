package com.tfit.BdBiProcSrvShEduOmc.constant;

/**
 * @Descritpion：Rediskey常量
 * @author: tianfang_infotech
 * @date: 2019/1/7 10:32
 */
public interface RedisKeyConstant {

    /**
     * 学校废弃油脂汇总Key
     */
    String SCHOOL_RECOVERY_WASTE_OIL_TOTAL = "schooloiltotal";

    /**
     * 学校废弃油脂详情Key
     */
    String SCHOOL_RECOVERY_WASTE_OIL_DETAIL = "schooloil";

    /**
     * 学校基础信息
     */
    String SCHOOL_BASE_INFO = "schoolDetail";


    /**
     * 团餐公司废弃油脂汇总Key
     */
    String RMC_RECOVERY_WASTE_OIL_TOTAL = "supplieroiltotal";

    /**
     * 团餐公司废弃油脂详情Key
     */
    String RMC_RECOVERY_WASTE_OIL_DETAIL = "supplieroil";

    /**
     * 团餐公司基础信息
     */
    String RMC_BASE_INFO = "group-supplier-detail";
}
