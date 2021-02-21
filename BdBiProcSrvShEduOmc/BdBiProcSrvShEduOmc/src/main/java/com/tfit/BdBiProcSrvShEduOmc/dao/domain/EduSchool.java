package com.tfit.BdBiProcSrvShEduOmc.dao.domain;

import lombok.Data;

/**
 * @Descritpion：学校信息
 * @author: tianfang_infotech
 * @date: 2019/1/9 10:56
 */
@Data
public class EduSchool {

    /**
     * 编号
     */
    private String id;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 地址
     */
    private String address;

    /**
     * 区
     */
    private String area;

    /**
     * 等级 0:幼儿园，1:小学，2:初中，3:高中，5:企业，6:中职校 7:托儿所  9:其他
     */
    private String LEVEL;

    /**
     * 学校性质 0:国内公办 1:国际公办 2:国内民办 3:国际民办 , 20180810 改:0公办和2私办
     */
    private String schoolNature;

    /**
     * 食堂经营模式 0:食堂自营管理，1:食堂外包管理，2:混合
     */
    private String canteenMode;

    /**
     *配送类型,0快餐配送 1原料配送
     */
    private String ledgerType;

    /**
     * 学校学制
     */
    private Integer level2;

    /**
     * 关联的总校
     */
    private String parentId;

    /**
     *食品许可证件主体的类型(1学校|2外包)
     */
    private String licenseMainType;

    /**
     * 供餐模式:20181024新增:1自行加工,2食品加工商,3快餐配送,4现场加工
     */
    private Integer licenseMainChild;

    /**
     * 所属区ID
     */
    private String schoolAreaId;
}
