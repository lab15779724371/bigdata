package com.tfit.BdBiProcSrvShEduOmc.service;

import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dto.assess.AssessData;


import java.util.LinkedHashMap;
import java.util.List;

public interface DbHiveAssessService {
    /**
     * 考核评价查询列表 教委账号
     * 从数据库app_saas_v1的数据表t_edu_school_assessment_details_w中根据条件查询数据列表
     * @return
     */

    List<AppCommonDao> getAssessList(LinkedHashMap<String, Object> filterParamMap);

    /**
     * 考核评价查询列表 区账号
     * 从数据库app_saas_v1的数据表t_edu_school_assessment_details_w中根据条件查询数据列表
     * @return
     */

    List<AppCommonDao> getAssessAreaList(LinkedHashMap<String, Object> filterParamMap);

    /**
     *  考核评价查询列表  传入参数 时间周期 为null 时
     *  从数据库app_saas_v1的数据表t_edu_school_assessment_details_w中根据条件查询数据列表
     * @param filterParamMap
     * @return
     */
    List<AppCommonDao> getAssessTimeIsNullAreaList(LinkedHashMap<String, Object> filterParamMap);

    /**
     * 考核评价详情列表
     * 从数据库app_saas_v1的数据表t_edu_school_assessment_details_w中根据条件查询数据列表
     * @return
     */
    List<AppCommonDao> getAssessDetailList(LinkedHashMap<String, Object> filterParamMap);

    /**
     * 导出考核评价 从数据库app_saas_v1的数据表t_edu_school_assessment_details_w中把数据 封装
     * @param filterParamMap
     * @return
     */
    List<AssessData> getExpAssessList(LinkedHashMap<String, Object> filterParamMap);


}
