package com.tfit.BdBiProcSrvShEduOmc.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.task.PpCheckDetsTaskMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.task.PpDishDetsTaskMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.task.WarnPushTaskMod;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveCheckWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

/**
 * @Description: 预警定时任务
 * @Author: jianghy
 * @Date: 2020/1/14
 * @Time: 10:37
 */
@RestController
@RequestMapping(value = "/biOptAnl")
public class WarnTaskController {
    private static final Logger logger = LogManager.getLogger(WarnTaskController.class.getName());
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
    DbHiveCheckWarnService dbHiveCheckWarnService;

    /**
     * 预警相关hive库的查询
     */
    @Autowired
    DbHiveWarnService dbHiveWarnService;

    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveService dbHiveService;



    /**
     * @Description:当日14点验收定时任务
     * @Param: []
     * @return: void
     * @Author: jianghy
     * @Date: 2020/1/14
     * @Time: 11:06
     */
    //@RequestMapping(value = "/warnone",method = RequestMethod.GET)
    //@Scheduled(cron = "0 15 14 * * *")
    public void checkTipsTask() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			logger.info("=========================定时任务ip address："+address);
			if(address != null) {
				logger.info("=========================定时任务ip："+address.getHostAddress());
			}
			
			if(address != null && "172.18.14.40".equals(address.getHostAddress())) {
				logger.info("=========================172.18.14.40不执行任务，保证只有41执行任务");
				return;
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        logger.info("CheckTipsTask 定时任务！" + new ToolUtil().currentTime());
        //未排菜预警
        boolean dishFlag = new PpDishDetsTaskMod().appModFunc(1, db1Service, db2Service, dbHiveWarnService);
        boolean accpetFlag = new PpCheckDetsTaskMod().appModFunc(1, db1Service, db2Service, dbHiveWarnService);
        for(int i=1;i<=5;i++) {
        	//等待2分钟(防止太频繁)
			try {
				TimeUnit.MINUTES.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
	        if(!dishFlag) {
	        	dishFlag = new PpDishDetsTaskMod().appModFunc(1, db1Service, db2Service, dbHiveWarnService);
	        }
	        
	        if(!accpetFlag) {
	        	accpetFlag = new PpCheckDetsTaskMod().appModFunc(1, db1Service, db2Service, dbHiveWarnService);
	        }
	        
	        //如果排菜和验收均成功，则跳出循环
	        if(dishFlag && accpetFlag) {
	        	logger.info("========================跳出定时任务循环");
	        	break;
	        }
        }
    }

    /**
     * @Description:当日16点验收定时任务
     * @Param: []
     * @return: void
     * @Author: jianghy
     * @Date: 2020/1/14
     * @Time: 11:06
     */
    //@RequestMapping(value = "/warntwo",method = RequestMethod.GET)
   // @Scheduled(cron = "0 15 16 * * *")
    public void checkRemindTask() {
    	try {
			InetAddress address = InetAddress.getLocalHost();
			logger.info("=========================定时任务ip address："+address);
			if(address != null) {
				logger.info("=========================定时任务ip："+address.getHostAddress());
			}
			
			if(address != null && "172.18.14.40".equals(address.getHostAddress())) {
				logger.info("=========================172.18.14.40不执行任务，保证只有41执行任务");
				return;
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        logger.info("CheckTipsTask 定时任务！" + new ToolUtil().currentTime());
        //未排菜预警
        boolean dishFlag = new PpDishDetsTaskMod().appModFunc(2, db1Service, db2Service, dbHiveWarnService);
        boolean accpetFlag = new PpCheckDetsTaskMod().appModFunc(2, db1Service, db2Service, dbHiveWarnService);
        for(int i=1;i<=5;i++) {
        	//等待2分钟(防止太频繁)
			try {
				TimeUnit.MINUTES.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
	        if(!dishFlag) {
	        	dishFlag = new PpDishDetsTaskMod().appModFunc(2, db1Service, db2Service, dbHiveWarnService);
	        }
	        
	        if(!accpetFlag) {
	        	accpetFlag = new PpCheckDetsTaskMod().appModFunc(2, db1Service, db2Service, dbHiveWarnService);
	        }
	        
	      //如果排菜和验收均成功，则跳出循环
	        if(dishFlag && accpetFlag) {
	        	logger.info("========================跳出定时任务循环");
	        	break;
	        }
        }
    }

    /**
     * @Description:昨日17点验收定时任务
     * @Param: []
     * @return: void
     * @Author: jianghy
     * @Date: 2020/1/14
     * @Time: 11:06
     */
    //@RequestMapping(value = "/warnthree",method = RequestMethod.GET)
    //@Scheduled(cron = "0 15 17 * * *")
    public void checkWarnTask() {
    	try {
			InetAddress address = InetAddress.getLocalHost();
			logger.info("=========================定时任务ip address："+address);
			if(address != null) {
				logger.info("=========================定时任务ip："+address.getHostAddress());
			}
			
			if(address != null && "172.18.14.40".equals(address.getHostAddress())) {
				logger.info("=========================172.18.14.40不执行任务，保证只有41执行任务");
				return;
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        logger.info("CheckTipsTask 定时任务！" + new ToolUtil().currentTime());
        //未排菜预警
        boolean dishFlag = new PpDishDetsTaskMod().appModFunc(3, db1Service, db2Service, dbHiveWarnService);
        boolean accpetFlag = new PpCheckDetsTaskMod().appModFunc(3, db1Service, db2Service, dbHiveWarnService);
        for(int i=1;i<=5;i++) {
        	//等待2分钟(防止太频繁)
			try {
				TimeUnit.MINUTES.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
	        if(!dishFlag) {
	        	dishFlag = new PpDishDetsTaskMod().appModFunc(3, db1Service, db2Service, dbHiveWarnService);
	        }
	        
	        if(!accpetFlag) {
	        	accpetFlag = new PpCheckDetsTaskMod().appModFunc(3, db1Service, db2Service, dbHiveWarnService);
	        }
	        
	      //如果排菜和验收均成功，则跳出循环
	        if(dishFlag && accpetFlag) {
	        	logger.info("========================跳出定时任务循环");
	        	break;
	        }
        }
    }

    /**
     * @Description:当日9点验收定时任务（9点发送的是5点的未排菜数据，所以可以按时发送）
     * @Param: []
     * @return: void
     * @Author: jianghy
     * @Date: 2020/1/14
     * @Time: 11:06
     */
    //@Scheduled(cron = "0 0 9 * * *")
    // @RequestMapping(value = "/warnfour",method = RequestMethod.GET)
    public void checkSuperviseTask() {
    	try {
    		InetAddress address = InetAddress.getLocalHost();
    		logger.info("=========================定时任务ip address："+address);
			if(address != null) {
				logger.info("=========================定时任务ip："+address.getHostAddress());
			}
			
			if(address != null && "172.18.14.40".equals(address.getHostAddress())) {
				logger.info("=========================172.18.14.40不执行任务，保证只有41执行任务");
				return;
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        logger.info("CheckTipsTask 定时任务！" + new ToolUtil().currentTime());
        //未排菜预警
        boolean dishFlag = new PpDishDetsTaskMod().appModFunc(4, db1Service, db2Service, dbHiveWarnService);
        boolean accpetFlag = new PpCheckDetsTaskMod().appModFunc(4, db1Service, db2Service, dbHiveWarnService);
        for(int i=1;i<=5;i++) {
        	//等待2分钟(防止太频繁)
			try {
				TimeUnit.MINUTES.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
	        if(!dishFlag) {
	        	dishFlag = new PpDishDetsTaskMod().appModFunc(4, db1Service, db2Service, dbHiveWarnService);
	        }
	        
	        if(!accpetFlag) {
	        	accpetFlag = new PpCheckDetsTaskMod().appModFunc(4, db1Service, db2Service, dbHiveWarnService);
	        }
	        
	      //如果排菜和验收均成功，则跳出循环
	        if(dishFlag && accpetFlag) {
	        	logger.info("========================跳出定时任务循环");
	        	break;
	        }
        }
    }

    /**
     * @Description:昨日11点验收定时任务
     * @Param: []
     * @return: void
     * @Author: jianghy
     * @Date: 2020/1/14
     * @Time: 11:06
     */
    //@RequestMapping(value = "/warnfive",method = RequestMethod.GET)
    //@Scheduled(cron = "0 15 11 * * *")
    public void checkTipsAccountabilityTask() {
    	try {
			InetAddress address = InetAddress.getLocalHost();
			logger.info("=========================定时任务ip address："+address);
			if(address != null) {
				logger.info("=========================定时任务ip："+address.getHostAddress());
			}
			if(address != null && "172.18.14.40".equals(address.getHostAddress())) {
				logger.info("=========================172.18.14.40不执行任务，保证只有41执行任务");
				return;
			}
			
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        logger.info("CheckTipsTask 定时任务！" + new ToolUtil().currentTime());
        //未排菜预警
        boolean dishFlag = new PpDishDetsTaskMod().appModFunc(5, db1Service, db2Service, dbHiveWarnService);
        boolean accpetFlag = new PpCheckDetsTaskMod().appModFunc(5, db1Service, db2Service, dbHiveWarnService);
        for(int i=1;i<=5;i++) {
        	//等待2分钟(防止太频繁)
			try {
				TimeUnit.MINUTES.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
	        if(!dishFlag) {
	        	dishFlag = new PpDishDetsTaskMod().appModFunc(5, db1Service, db2Service, dbHiveWarnService);
	        }
	        
	        if(!accpetFlag) {
	        	accpetFlag = new PpCheckDetsTaskMod().appModFunc(5, db1Service, db2Service, dbHiveWarnService);
	        }
	        
	      //如果排菜和验收均成功，则跳出循环
	        if(dishFlag && accpetFlag) {
	        	logger.info("========================跳出定时任务循环");
	        	break;
	        }
        }
    }
    
    //每十分钟跑一次
    //@Scheduled(cron="0 0/5 *  * * ? ") 
    public void test() {
        logger.info("test 定时任务,没10分钟执行一次！" + new ToolUtil().currentTime());
        //未排菜预警
        for(int i = 1;i<6;i++) {
        	new PpDishDetsTaskMod().appModFunc(i, db1Service, db2Service, dbHiveWarnService);
        }

    }


    /**
     * @Description: 修改用户状态
     * @Param: []
     * @return: void
     * @Author: jianghy
     * @Date: 2020/1/18
     * @Time: 16:38
     */
    @RequestMapping(value = "/v1/updateWarnReadSatus", method = RequestMethod.POST)
    public String updateWarnReadSatus(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        String titleId = jsonObject.getString("titleId");
        return new WarnPushTaskMod().updateWarnReadSatus(request,db2Service,titleId);
    }


}
