package com.tfit.BdBiProcSrvShEduOmc.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Descritpion：团餐公司回收废弃油脂详细列表
 * @author: tianfang_infotech
 * @date: 2019/1/7 16:11
 */
@Data
public class RmcRecoveryWasteOilDetailVO {

    /**
     * 回收日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = "GMT+8")
    private Date recDate;

    /**
     * 区域名称
     */
    private String distName;

    /**
     * 团餐公司名称
     */
    private String rmcName;

    /**
     * 废弃油脂种类，1:废油，2:含油废水
     */
    private String woType;

    /**
     * 回收数量(改为String类型原因：数据库中存在带单位的数量)
     */
    private String recNum;

    /**
     * 回收单位
     */
    private String recCompany;

    /**
     * 回收人
     */
    private String recPerson;

    /**
     * 回收单据数
     */
    private Integer recBillNum;

}
