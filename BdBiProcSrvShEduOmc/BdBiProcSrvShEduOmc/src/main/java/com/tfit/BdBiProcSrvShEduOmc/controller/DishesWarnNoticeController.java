package com.tfit.BdBiProcSrvShEduOmc.controller;

import com.tfit.BdBiProcSrvShEduOmc.appmod.dwn.DishesWarnNoticeMod;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishesWarnNoticeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 未排菜和未验收预警通知
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-15
 */


@RestController
@RequestMapping(value = "/biOptAnl")
public class DishesWarnNoticeController {
    private static final Logger logger = LogManager.getLogger(AssessAndEvaluationController.class.getName());
    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveDishesWarnNoticeService dbHiveDishesWarnNoticeService;
    /**
     * mysql数据库服务1
     */
    @Autowired
    Db1Service db1Service;
    /**
     * mysql数据库服务2
     */
    @Autowired
    Db2Service db2Service;


    /**
     *  1-  未排菜预警提醒
     *
     * @param request
     * @return
     */

    @RequestMapping(value = "/v1/dishesWarnNoticeList", method = RequestMethod.GET)
    public String queryDishesWarnNoticeList(HttpServletRequest request) {
        return new DishesWarnNoticeMod().queryDishesWarnNoticeFunc(request, dbHiveDishesWarnNoticeService, db2Service,db1Service);
    }

    /**
     *  2-  未验收预警提醒
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/checkWarnNoticeList", method = RequestMethod.GET)
    public String queryCheckWarnNoticeList(HttpServletRequest request) {
        return new DishesWarnNoticeMod().queryCheckWarnNoticeFunc(request, dbHiveDishesWarnNoticeService, db2Service,db1Service);
    }
}
