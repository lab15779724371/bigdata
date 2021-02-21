package com.tfit.BdBiProcSrvShEduOmc.dto.assess;

import lombok.Data;

/**
 * @Description: 考核评价 导出
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-14
 */
@Data
public class ExpAssessDTO {
    String startDate;
    String endDate;
    String area;
    String managementDepartment;
    String schoolName;
    Integer sendFlag;
    String expFileUrl;
}
