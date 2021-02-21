package com.tfit.BdBiProcSrvShEduOmc.model.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.FileExport;
import lombok.Data;

import java.util.Date;

/**
 * @Descritpion：团餐公司回收废弃油脂导出模型
 * @author: tianfang_infotech
 * @date: 2019/1/17 15:11
 */
@Data
public class RmcRecoveryWasteOilDetailsExport extends FileExport {
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
