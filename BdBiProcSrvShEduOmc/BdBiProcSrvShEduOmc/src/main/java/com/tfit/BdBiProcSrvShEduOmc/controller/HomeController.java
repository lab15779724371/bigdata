package com.tfit.BdBiProcSrvShEduOmc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.home.*;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.AnnouncesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.DishMatRateDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.DishRsRateDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.GoodsOptRateDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.HomeInfoStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.LicWarnProcRateDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.SchSitInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.SchTypeStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.TodoListStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Descritpion：大数据运营管理后台-首页api
 * @author: tianfang_infotech
 * @date: 2019/1/2 13:30
 */
@RestController
@RequestMapping(value = "/biOptAnl")
public class HomeController {

    private static final Logger logger = LogManager.getLogger(HomeController.class.getName());

    @Autowired
    ObjectMapper objectMapper;

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
     * hive数据库服务（预警）
     */
    @Autowired
    DbHiveWarnService dbHiveWarnService;
    
    /**
     * hive数据库服务(排菜)
     */
    @Autowired
    DbHiveDishService dbHiveDishService;
    
    /**
     * hive数据库服务(用料)
     */
    @Autowired
    DbHiveMatService dbHiveMatService;

    /**
     * 待办事项统计应用模型
     */
    TodoListStatAppMod tlsAppMod = new TodoListStatAppMod();

    /**
     * 首页信息统计应用模型
     */
    HomeInfoStatAppMod hisAppMod = new HomeInfoStatAppMod();

    /**
     * 通告列表应用模型
     */
    AnnouncesAppMod asAppMod = new AnnouncesAppMod();
    
    //学校情况信息应用模型
    SchSitInfoAppMod ssiAppMod = new SchSitInfoAppMod();

    /**
     * 排菜率与用料确认率趋势应用模型
     */
    DishMatRateAppMod dmrAppMod = new DishMatRateAppMod();

    /**
     * 配货单操作率趋势应用模型
     */
    GoodsOptRateAppMod gorAppMod = new GoodsOptRateAppMod();
    
    //菜品留样率应用模型
    DishRsRateAppMod drrAppMod = new DishRsRateAppMod();
    
    //证照预警处理率应用模型
    LicWarnProcRateAppMod lwprAppMod = new LicWarnProcRateAppMod();

    /**
     * 各类型学校数量统计应用模型
     */
    SchTypeStatOamAppMod stsoAppMod = new SchTypeStatOamAppMod();


    /**
     * 待办事项统计
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/todoListStat",method = RequestMethod.GET)
    public String v1_todoListStat(HttpServletRequest request)
    {
        //初始化响应数据
        String strResp = null;
        TodoListStatDTO tlsDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if(distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if(prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //时间类型
        String timeType = request.getParameter("timeType");
        if(timeType == null) {
        	timeType = request.getParameter("timetype");
        }
        //日期
        String date = request.getParameter("date");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", timeType = " + timeType + ", date = " + date);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //待办事项统计应用模型函数
		if(isAuth)
			tlsDto = tlsAppMod.appModFunc(token, distName, prefCity, province, timeType, date, db1Service, db2Service,dbHiveWarnService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if(tlsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(tlsDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            //获取异常响应
        	code = codes[0];
			logger.info("错误码：code = " + code);
			IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 通告列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/announces",method = RequestMethod.GET)
    public String v1_announcs(HttpServletRequest request)
    {
        //初始化响应数据
        String strResp = null;
        AnnouncesDTO asDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if(distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if(prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //时间类型
        String strTimeType = request.getParameter("timeType");
        if(strTimeType == null) {
            strTimeType = request.getParameter("timetype");
        }
        int timeType = -1;
        if(strTimeType != null) {
            timeType = Integer.parseInt(strTimeType);
        }
        //日期
        String date = request.getParameter("date");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", timeType = " + timeType + ", date = " + date);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //通告列表应用模型函数
		if(isAuth)
			asDto = asAppMod.appModFunc(distName, prefCity, province, timeType, date, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if(asDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(asDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            //获取异常响应
        	code = codes[0];
			logger.info("错误码：code = " + code);
			IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }
    
    /**
     * 学校情况信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/schSitInfo",method = RequestMethod.GET)
    public String v1_schSitInfo(HttpServletRequest request)
    {
        //初始化响应数据
        String strResp = null;
        SchSitInfoDTO ssiDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if(distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if(prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //时间类型
        String strTimeType = request.getParameter("timeType");
        if(strTimeType == null) {
            strTimeType = request.getParameter("timetype");
        }
        int timeType = -1;
        if(strTimeType != null) {
            timeType = Integer.parseInt(strTimeType);
        }
        //日期
        String date = request.getParameter("date");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", timeType = " + timeType + ", date = " + date);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //学校情况信息模型函数
		if(isAuth)
			ssiDto = ssiAppMod.appModFunc(token, distName, prefCity, province, timeType, date, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if(ssiDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ssiDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            //获取异常响应
        	code = codes[0];
			logger.info("错误码：code = " + code);
			IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 首页信息统计【首页暂时未用到，未做权限修改】
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/homeInfoStat",method = RequestMethod.GET)
    public String v1_homeInfoStat(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        HomeInfoStatDTO hisDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if(distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if(prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //日期
        String date = request.getParameter("date");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", date = " + date);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //首页信息统计应用模型函数
		if(isAuth)
			hisDto = hisAppMod.appModFunc(token, distName, prefCity, province, date, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if(hisDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(hisDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            //获取异常响应
        	code = codes[0];
			logger.info("错误码：code = " + code);
			IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 排菜率与用料确认率趋势
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishMatRate",method = RequestMethod.GET)
    public String v1_dishMatRate(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DishMatRateDTO dmrDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if(distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if(prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //时间类型
        String strTimeType = request.getParameter("timeType");
        if(strTimeType == null) {
            strTimeType = request.getParameter("timetype");
        }
        int timeType = 3;
        if(strTimeType != null) {
            timeType = Integer.parseInt(strTimeType);
        }
        //日期
        String date = request.getParameter("date");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", timeType = " + timeType + ", date = " + date);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //排菜率与用料确认率趋势应用模型函数
		if(isAuth)
			dmrDto = dmrAppMod.appModFunc(token, distName, prefCity, province, timeType, date, db1Service, db2Service,dbHiveDishService,dbHiveMatService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if(dmrDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(dmrDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            //获取异常响应
        	code = codes[0];
			logger.info("错误码：code = " + code);
			IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 配货单操作率趋势
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/goodsOptRate",method = RequestMethod.GET)
    public String v1_goodsOptRate(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        GoodsOptRateDTO gorDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if(distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if(prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //时间类型
        String strTimeType = request.getParameter("timeType");
        if(strTimeType == null) {
            strTimeType = request.getParameter("timetype");
        }
        int timeType = 3;
        if(strTimeType != null) {
            timeType = Integer.parseInt(strTimeType);
        }
        //日期
        String date = request.getParameter("date");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", timeType = " + timeType + ", date = " + date);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //配货单操作率趋势应用模型函数
		if(isAuth)
			gorDto = gorAppMod.appModFunc(token, distName, prefCity, province, timeType, date, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if(gorDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(gorDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            //获取异常响应
        	code = codes[0];
			logger.info("错误码：code = " + code);
			IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }
    
    //菜品留样率
    @RequestMapping(value = "/v1/dishRsRate",method = RequestMethod.GET)
    public String v1_dishRsRate(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DishRsRateDTO drrDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if(distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if(prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //时间类型
        String strTimeType = request.getParameter("timeType");
        if(strTimeType == null) {
            strTimeType = request.getParameter("timetype");
        }
        int timeType = 3;
        if(strTimeType != null) {
            timeType = Integer.parseInt(strTimeType);
        }
        //日期
        String date = request.getParameter("date");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", timeType = " + timeType + ", date = " + date);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //菜品留样率应用模型函数
		if(isAuth)
			drrDto = drrAppMod.appModFunc(token, distName, prefCity, province, timeType, date, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if(drrDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(drrDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            //获取异常响应
        	code = codes[0];
			logger.info("错误码：code = " + code);
			IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }
    
    //证照预警处理率
    @RequestMapping(value = "/v1/licWarnProcRate",method = RequestMethod.GET)
    public String v1_licWarnProcRate(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        LicWarnProcRateDTO lwprDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if(distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if(prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //时间类型
        String strTimeType = request.getParameter("timeType");
        if(strTimeType == null) {
            strTimeType = request.getParameter("timetype");
        }
        int timeType = 3;
        if(strTimeType != null) {
            timeType = Integer.parseInt(strTimeType);
        }
        //日期
        String date = request.getParameter("date");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", timeType = " + timeType + ", date = " + date);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //证照预警处理率应用模型函数
		if(isAuth)
			lwprDto = lwprAppMod.appModFunc(token, distName, prefCity, province, timeType, date, db1Service, db2Service,dbHiveWarnService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if(lwprDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(lwprDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            //获取异常响应
        	code = codes[0];
			logger.info("错误码：code = " + code);
			IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }

    /**
     * 各类型学校数量统计
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/schTypeStat",method = RequestMethod.GET)
    public String v1_schTypeStat(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SchTypeStatDTO stsDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if(distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if(prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //时间类型
        String strTimeType = request.getParameter("timeType");
        if(strTimeType == null) {
            strTimeType = request.getParameter("timetype");
        }
        int timeType = -1;
        if(strTimeType != null) {
            timeType = Integer.parseInt(strTimeType);
        }
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", timeType = " + timeType);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //学校类型统计应用模型函数
		if(isAuth)
			stsDto = stsoAppMod.appModFunc(token, distName, prefCity, province, timeType, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if(stsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(stsDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            //获取异常响应
        	code = codes[0];
			logger.info("错误码：code = " + code);
			IOTHttpRspVO excepResp = AppModConfig.getExcepResp(code);
            try {
                strResp = objectMapper.writeValueAsString(excepResp);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return strResp;
    }
}
