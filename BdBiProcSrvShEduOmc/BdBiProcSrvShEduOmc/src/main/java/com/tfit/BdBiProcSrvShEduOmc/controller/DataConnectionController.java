package com.tfit.BdBiProcSrvShEduOmc.controller;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduLedgerMasterTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduMaterialTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduPlatoonTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.obj.search.AppTEduMaterialDishD;
import com.tfit.BdBiProcSrvShEduOmc.obj.warn.AppTEduNoPlatoonCollectD;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishesWarnNoticeService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

//check程序
@RestController
@RequestMapping(value = "/BdBiProcSrvShEduOmc")
public class DataConnectionController {
	
	private static final Logger logger = LogManager.getLogger(TaskController.class.getName());
	
	/**
     * mysql主数据库服务
     */
    @Autowired
    SaasService saasService;

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
     * hive数据库服务
     */
    @Autowired
    DbHiveWarnService dbHiveWarnService;
    
    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveRecyclerWasteService dbHiveRecyclerWasteService;
    
    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveDishService dbHiveDishService;
    
    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveMatService dbHiveMatService;
    
    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveGsService dbHiveGsService;
    
    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveService dbHiveService;
    
    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveDishesWarnNoticeService dbHiveDishesWarnNoticeService;
    
  	
    /**
     * @Description:唤醒数据库连接
     * @Param: []
     * @return: void
     * @Author: jianghy
     * @Date: 2020/1/14
     * @Time: 11:06
     */
    @Scheduled(cron = "0 */4 * * * *")
    @RequestMapping(value = "/getDbServiceImpl",method = RequestMethod.GET)
    public void getDb1ServiceImpl() {
        logger.info("CheckTipsTask 定时任务！" + new ToolUtil().currentTime());
        
        try {
        	db1Service.getDepartmentObjList(null,null, -1, -1);
        	logger.info("**************db1Service Succee");
        }catch(Exception ex) {
        	logger.info("**************db1Service Exption:"+ex.getMessage());
        }
        try {
        	db2Service.getBdUserInfoByUserName("admin");
        	logger.info("**************db2Service Succee");
        }catch(Exception ex) {
        	logger.info("**************db2Service Exption:"+ex.getMessage());
        }
        
        String startDate = BCDTimeUtil.convertNormalDate(null);
		String endDate = BCDTimeUtil.convertNormalDate(null);
    	String [] yearMonths = new String [4];
    	//根据开始日期、结束日期，获取开始日期和结束日期的年、月
    	yearMonths = CommonUtil.getYearMonthByDate(startDate, endDate);
    	String startYear = yearMonths[0];
    	String startMonth = yearMonths[1];
    	String endYear = yearMonths[2];
    	String endMonth = yearMonths[3];
    	//结束日期+1天，方便查询处理
		String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
		//获取开始日期、结束日期的年月集合
		List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);
		 try {
			Integer iRet = dbHiveWarnService.getAppTEduNoPlatoonCollectDListCount(listYearMonth, startDate, endDateAddOne, new AppTEduNoPlatoonCollectD());
			logger.info("**************dbHiveWarnService Succee"+iRet);
		 }catch(Exception ex) {
	        logger.info("**************dbHiveWarnService Exption:"+ex.getMessage());
	     }
		 try {
			 Integer iRet  = dbHiveDishService.getAppTEduPlatoonTotalWObjListCount(listYearMonth, startDate, endDateAddOne, new AppTEduPlatoonTotalWObj());
			 logger.info("**************dbHiveDishService Succee"+iRet);
		 }catch(Exception ex) {
		        logger.info("**************dbHiveDishService Exption:"+ex.getMessage());
		     }
		try {
			Integer iRet =  dbHiveMatService.getAppTEduMaterialTotalWObjListCount(listYearMonth, startDate, endDateAddOne, new AppTEduMaterialTotalWObj());
			logger.info("**************dbHiveMatService Succee"+iRet);
		}catch(Exception ex) {
	        logger.info("**************dbHiveMatService Exption:"+ex.getMessage());
	     }
		try {
			Integer iRet =  dbHiveGsService.getAppTEduLedgerMasterTotalWObjListCount(listYearMonth, startDate, endDateAddOne, new AppTEduLedgerMasterTotalWObj());
			logger.info("**************dbHiveGsService Succee"+iRet);
		}catch(Exception ex) {
	        logger.info("**************dbHiveGsService Exption:"+ex.getMessage());
	     }
		try {
			Integer iRet =  dbHiveService.getAppTEduMaterialDishDListCount(listYearMonth, startDate, endDateAddOne, new AppTEduMaterialDishD());
			logger.info("**************dbHiveService Succee"+iRet);
		}catch(Exception ex) {
	        logger.info("**************dbHiveService Exption:"+ex.getMessage());
	     }
		
		try {
			LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();
			filterParamMap.put("departmentId", "100");
			List<LinkedHashMap<String, Object>> list  =  dbHiveDishesWarnNoticeService.getAreaCheckWarnNoticeList(filterParamMap);
			logger.info("**************dbHiveDishesWarnNoticeService Succee"+list);
		}catch(Exception ex) {
	        logger.info("**************dbHiveDishesWarnNoticeService Exption:"+ex.getMessage());
	     }
    }
}