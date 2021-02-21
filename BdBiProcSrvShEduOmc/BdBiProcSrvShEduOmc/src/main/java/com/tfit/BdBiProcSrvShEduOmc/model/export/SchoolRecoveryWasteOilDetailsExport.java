package com.tfit.BdBiProcSrvShEduOmc.model.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tfit.BdBiProcSrvShEduOmc.dto.base.PagedDTO;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.FileExport;
import lombok.Data;

import java.util.Date;

/**
 * @Descritpion：学校回收废弃油脂详情导出模型
 * @author: tianfang_infotech
 * @date: 2019/1/17 15:11
 */
@Data
public class SchoolRecoveryWasteOilDetailsExport extends FileExport {
    /**
     * 回收开始日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startSubDate;

    /**
     * 回收结束日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endSubDate;

    /**
     * 废弃油脂种类(0:未知,1:废水;2:含油废水)
     */
    private Integer woType;

    /**
     * 项目点名称(学校)
     */
    private String ppName;

    /**
     * 区域名称
     */
    private Integer distName;

    /**
     * 地级城市
     */
    private String prefCity;

    /**
     * 省或直辖市
     */
    private String province;

    /**
     * 学校类型（学制）：0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，
     *     10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
     */
    private Integer schType;

    /**
     * 团餐公司Id
     */
    private String rmcName;

    /**
     * 回收单位
     */
    private String recCompany;

    /**
     * 回收人
     */
    private String recPerson;

}
