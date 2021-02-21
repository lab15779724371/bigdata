package com.tfit.BdBiProcSrvShEduOmc.dao.domain.saas;

import lombok.Data;

/**
 * @Descritpion：回收单位模型
 * @author: tianfang_infotech
 * @date: 2019/1/10 19:02
 */
@Data
public class ProRecyclerSupplier {

    /**
     * 主键
     */
    private String id;

    /**
     * 团餐公司Id或者学校id
     */
    private String sourceId;

    /**
     * 1餐厨垃圾，2废弃油脂
     */
    private Integer type;

    /**
     * 单位名称
     */
    private String supplierName;

    /**
     * 单位地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 联系电话
     */
    private String telephone;
}
