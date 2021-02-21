package com.tfit.BdBiProcSrvShEduOmc.dto.outside;

import java.util.List;

import lombok.Data;

/**
 * 3.2.43.	菜品留样汇总信息模型
 * @author Administrator
 *
 */
@Data
public class GsPlanOptDetsInputDTO {
   //开始日期
    String startSubDate;
    //结束日期
    String endSubDate;
    //配送单批次号集合
    List<String> distrBatNumberList;
}
