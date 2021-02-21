package com.tfit.BdBiProcSrvShEduOmc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.assess.AssessListMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.assess.ExpAssessListMod;
import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.dto.assess.AssessData;
import com.tfit.BdBiProcSrvShEduOmc.dto.assess.ExpAssessDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveAssessService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;



/**
 * @Description: 考核评价及详情
 * @Author: weihai_zhao
 * @Date: 2020-01-13
 */

//考核评价
@RestController
@RequestMapping(value = "/biOptAnl")
public class AssessAndEvaluationController {
    private static final Logger logger = LogManager.getLogger(AssessAndEvaluationController.class.getName());
    /**
     * hive数据库服务
     */
    @Autowired
    //Db1Service db1Service;
            DbHiveAssessService dbHiveAssessService;

    @Autowired
    Db1Service db1Service;
    /**
     * hive数据库服务2
     */
    @Autowired
    Db2Service db2Service;


    // @Autowired

    @Autowired
    ObjectMapper objectMapper;


    /**
     * 1- 考核评价列表查询
     *
     * @param request
     * @return
     */

    @RequestMapping(value = "/v1/assessList", method = RequestMethod.GET)
    public String assessEvaluationList(HttpServletRequest request) {
        return new AssessListMod().queryAssessModFunc(request, dbHiveAssessService, db1Service, db2Service);
    }

    /**
     * 2- 导出考核评价（）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expAssessList", method = RequestMethod.GET)
    public ApiResponse<ExpAssessDTO> expAssessList(HttpServletRequest request, AssessData assessData) {
        //授权码
        // String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //logger.info("输入参数：" + assessData.toString());
        //项目点排菜详情列表应用模型函数
        return new ExpAssessListMod().appModFunc(request, assessData, db2Service, dbHiveAssessService);
    }


    /**
     * 3- 考核评价详情
     *
     * @param request
     * @return
     */

    @RequestMapping(value = "/v1/assessDetailsList", method = RequestMethod.GET)
    public String assessDetailsList(HttpServletRequest request) {
        return new AssessListMod().queryAssessDetailsModFunc(request, dbHiveAssessService, db2Service);
    }


}
