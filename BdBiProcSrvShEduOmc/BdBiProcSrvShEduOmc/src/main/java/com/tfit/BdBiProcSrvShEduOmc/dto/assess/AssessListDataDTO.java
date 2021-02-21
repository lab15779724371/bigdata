package com.tfit.BdBiProcSrvShEduOmc.dto.assess;

import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import lombok.Data;

import java.util.List;

/**
 * @Description: 考核评价 导出模型
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-14
 */
@Data
public class AssessListDataDTO {
    String time;
    String resCode;
    String resMsg;
    PageInfo pageInfo;
    List<AssessData> assessListData;
    long msgId;
}
