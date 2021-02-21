package com.tfit.BdBiProcSrvShEduOmc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.AcceptWarnListRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.AcceptWarnSumListRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.AcceptWarnSumListRepsSumAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.DayOptRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.DishWarnListRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.DishWarnSumListRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.DishWarnSumListRepsSumAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.ExpAcceptWarnListRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.ExpAcceptWarnSumListRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.ExpDishWarnListRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.ExpDishWarnSumListRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.ExpWeeklyOptRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.MonthOptRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.rc.WeeklyOptRepsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpCommonDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnListRepsInputDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnListRepsOutDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnSumListRepsOutDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.DishWarnSumListRepsSumOutDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.ExpDayRepsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.rc.WeeklyOptRepsDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.warn.AppTEduNoLedgerCollectD;
import com.tfit.BdBiProcSrvShEduOmc.obj.warn.AppTEduNoPlatoonCollectD;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;

//报表中心BI应用服务
@RestController
@RequestMapping(value = "/biOptAnl")
public class RepCenterController {
    private static final Logger logger = LogManager.getLogger(HomeController.class.getName());

    @Autowired
    ObjectMapper objectMapper;
    
    //mysql主数据库服务
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
    
    //周运营报告列表应用模型
    WeeklyOptRepsAppMod worAppMod = new WeeklyOptRepsAppMod();
    
    DayOptRepsAppMod dayOptRepsAppMod = new DayOptRepsAppMod();
    MonthOptRepsAppMod monthOptRepsAppMod = new MonthOptRepsAppMod();
    
    
    //周运营报告导出应用模型
    ExpWeeklyOptRepsAppMod expWeeklyOptRepsAppMod = new ExpWeeklyOptRepsAppMod();
    
    //周运营报告导出应用模型
    DishWarnListRepsAppMod dishWarnListRepsAppMod = new DishWarnListRepsAppMod();
    AcceptWarnListRepsAppMod acceptWarnListRepsAppMod = new AcceptWarnListRepsAppMod();
    
    DishWarnSumListRepsAppMod dishWarnSumListRepsAppMod = new DishWarnSumListRepsAppMod();
    AcceptWarnSumListRepsAppMod acceptWarnSumListRepsAppMod = new AcceptWarnSumListRepsAppMod();
    
    DishWarnSumListRepsSumAppMod dishWarnSumListRepsSumAppMod = new DishWarnSumListRepsSumAppMod();
    AcceptWarnSumListRepsSumAppMod acceptWarnSumListRepsSumAppMod = new AcceptWarnSumListRepsSumAppMod();
    
    ExpDishWarnListRepsAppMod expDishWarnListRepsAppMod = new ExpDishWarnListRepsAppMod();
    ExpAcceptWarnListRepsAppMod expAcceptWarnListRepsAppMod = new ExpAcceptWarnListRepsAppMod();
    
    ExpDishWarnSumListRepsAppMod expDishWarnSumListRepsAppMod = new ExpDishWarnSumListRepsAppMod();
    ExpAcceptWarnSumListRepsAppMod expAcceptWarnSumListRepsAppMod = new ExpAcceptWarnSumListRepsAppMod();
    /**
     * 3.6.1 - 周运营报告列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/weeklyOptReps", method = RequestMethod.GET)
    public String v1_weeklyOptReps(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WeeklyOptRepsDTO worDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
       
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }
        
        
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String departmentId = request.getParameter("departmentId");
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate 
        		+ ", prefCity = " + prefCity 
        		+ ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //信息共享食品安全等级列表应用模型函数
		if(isAuth)
			worDto = worAppMod.appModFunc(token, startDate, endDate,year,month,departmentId, page, pageSize, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (worDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(worDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
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
     * 3.6.1 - 日运营报告列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dayOptReps", method = RequestMethod.GET)
    public String dayOptReps(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WeeklyOptRepsDTO worDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
       
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }
        
        
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String departmentId = request.getParameter("departmentId");
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate 
        		+ ", prefCity = " + prefCity 
        		+ ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //信息共享食品安全等级列表应用模型函数
		if(isAuth)
			worDto = dayOptRepsAppMod.appModFunc(token, startDate, endDate,year,month,departmentId, page, pageSize, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (worDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(worDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
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
     * 3.6.1 -月运营报告列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/monthOptReps", method = RequestMethod.GET)
    public String monthOptReps(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WeeklyOptRepsDTO worDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
       
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            pageSize = "20";
        }
        
        
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String departmentId = request.getParameter("departmentId");
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate 
        		+ ", prefCity = " + prefCity 
        		+ ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //信息共享食品安全等级列表应用模型函数
		if(isAuth)
			worDto = monthOptRepsAppMod.appModFunc(token, startDate, endDate,year,month,departmentId, page, pageSize, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (worDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(worDto);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
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
     * 3.6.2 - 周运营报告列表导出
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWeeklyOptReps", method = RequestMethod.GET)
    public String v1_expWeeklyOptReps(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        boolean isAuth = true;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //类型
        String repType = request.getParameter("repType");
        
        String repId = request.getParameter("repId");
        
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        
        
        logger.info("输入参数：" + "token = " + token + ", repType = " + repType 
        		+ ", prefCity = " + prefCity 
        		+ ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //信息共享食品安全等级列表应用模型函数
  		ExpDayRepsDTO expDayRepsDTO= null;
		if(isAuth)
			expDayRepsDTO = expWeeklyOptRepsAppMod.appModFunc(token, repType, repId, prefCity, province);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (isAuth) {
            try {
                strResp = objectMapper.writeValueAsString(expDayRepsDTO);
                logger.info(strResp);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
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


    //----------------------五级预警报表------------------------------------------------------------------------
    /**
     *排菜未上报报表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishWarnListReps", method = RequestMethod.GET)
    public DishWarnListRepsOutDto dishWarnListReps(HttpServletRequest request,@Validated DishWarnListRepsInputDto inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return dishWarnListRepsAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveWarnService);
    }
    
    /**
     * 排菜未上报报表导出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expDishWarnListReps", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> expDishWarnListReps(HttpServletRequest request,HttpServletResponse response,@Validated DishWarnListRepsInputDto inputParm) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        logger.info("输入参数：" + inputParm.toString());
        //项目点排菜详情列表应用模型函数
        return expDishWarnListRepsAppMod.appModFunc(token,inputParm,db1Service, db2Service,dbHiveWarnService,response);
    }
    
    /**
     *验收未上报报表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/acceptWarnListReps", method = RequestMethod.GET)
    public DishWarnListRepsOutDto acceptWarnListReps(HttpServletRequest request,@Validated DishWarnListRepsInputDto inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return acceptWarnListRepsAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveWarnService);
    }
    
    /**
     * 验收未上报报表导出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expAcceptWarnListReps", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> expAcceptWarnListReps(HttpServletRequest request,HttpServletResponse response,@Validated DishWarnListRepsInputDto inputParm) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        logger.info("输入参数：" + inputParm.toString());
        //项目点排菜详情列表应用模型函数
        return expAcceptWarnListRepsAppMod.appModFunc(token,inputParm,db1Service, db2Service,dbHiveWarnService,response);
    }
    
    /**
     *排菜未上报汇总报表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishWarnSumListReps", method = RequestMethod.GET)
    public DishWarnSumListRepsOutDto dishWarnSumListReps(HttpServletRequest request,@Validated AppTEduNoPlatoonCollectD inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return dishWarnSumListRepsAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveWarnService);
    }
    
    /**
     *排菜未上报汇总报表合计
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishWarnSumListRepsSum", method = RequestMethod.GET)
    public DishWarnSumListRepsSumOutDto dishWarnSumListRepsSum(HttpServletRequest request,@Validated AppTEduNoPlatoonCollectD inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return dishWarnSumListRepsSumAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveWarnService);
    }
    
    /**
     * 排菜未上报汇总报表导出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expDishWarnSumListReps", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> expDishWarnSumListReps(HttpServletRequest request,HttpServletResponse response,@Validated AppTEduNoPlatoonCollectD inputParm) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        logger.info("输入参数：" + inputParm.toString());
        //项目点排菜详情列表应用模型函数
        return expDishWarnSumListRepsAppMod.appModFunc(token,inputParm,db1Service, db2Service,dbHiveWarnService,response);
    }
    
    /**
     *验收未上报汇总报表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/acceptWarnSumListReps", method = RequestMethod.GET)
    public DishWarnSumListRepsOutDto acceptWarnSumListReps(HttpServletRequest request,@Validated AppTEduNoLedgerCollectD inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return acceptWarnSumListRepsAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveWarnService);
    }
    
    /**
     * 排菜未上报汇总报表导出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expAcceptWarnSumListReps", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> expAcceptWarnSumListReps(HttpServletRequest request,HttpServletResponse response,@Validated AppTEduNoLedgerCollectD inputParm) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        logger.info("输入参数：" + inputParm.toString());
        //项目点排菜详情列表应用模型函数
        return expAcceptWarnSumListRepsAppMod.appModFunc(token,inputParm,db1Service, db2Service,dbHiveWarnService,response);
    }
    
    /**
     *验收未上报汇总报表合计
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/acceptWarnSumListRepsSum", method = RequestMethod.GET)
    public DishWarnSumListRepsSumOutDto acceptWarnSumListRepsSum(HttpServletRequest request,@Validated AppTEduNoLedgerCollectD inputParm) {
        logger.info("输入参数：" + inputParm.toString());
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        return acceptWarnSumListRepsSumAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveWarnService);
    }
    
}