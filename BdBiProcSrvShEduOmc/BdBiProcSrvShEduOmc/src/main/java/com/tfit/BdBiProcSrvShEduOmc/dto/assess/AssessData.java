package com.tfit.BdBiProcSrvShEduOmc.dto.assess;

import lombok.Data;

/**
 * @Description: 考核评价导出数据
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-14
 */
@Data
public class AssessData {
    String evaluatePeriod;
    String schoolName;
    String area;
    String managementDepartment;
    String comprehensiveEvaluationResults;

    //String page;
    //String pageSize;
}
