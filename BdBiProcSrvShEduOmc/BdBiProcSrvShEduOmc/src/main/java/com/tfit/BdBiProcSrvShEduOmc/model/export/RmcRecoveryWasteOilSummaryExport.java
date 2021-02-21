package com.tfit.BdBiProcSrvShEduOmc.model.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tfit.BdBiProcSrvShEduOmc.model.vo.base.FileExport;
import lombok.Data;

import java.util.Date;

/**
 * @Descritpion：废弃油脂统计列表导出
 * @author: tianfang_infotech
 * @date: 2019/1/17 14:46
 */
@Data
public class RmcRecoveryWasteOilSummaryExport extends FileExport {
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
     * 区域名称
     */
    private String distName;

    /**
     * 地级城市
     */
    private String prefCity;

    /**
     * 省或直辖市
     */
    private String province;
}
