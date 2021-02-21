package com.tfit.BdBiProcSrvShEduOmc.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.annotation.CheckUserToken;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.ExpDishSumInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.AbnCondSchListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.AbnOdNoProcWarnsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.BusiDetDataInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.DishRsSumInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.DishSumInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.ExpPpDishUseDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.GsAcceptSumInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.MatSumInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.PpDishUseDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.SchConMatSitStatAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.SchDishRsSitStatAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.SchDishSitStatAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.SchGsAcceptSitStatAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.optanl.SumDataInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpCommonDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpDishSumInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.AbnCondSchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.AbnOdNoProcWarnsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.BusiDetDataInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.DishRsSumInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.DishSumInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.GsAcceptSumInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.MatSumInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.PpDishDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.PpDishUseDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchConMatSitStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishRsSitStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishSitStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsAcceptSitStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SumDataInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;

/**
 * @Descritpion：业务监管
 * @author: fengyang_xie
 * @date: 2019/1/24
 */
@CheckUserToken
@RestController
@RequestMapping(value = "/biOptAnl")
public class BusinessSupervisionController {
    private static final Logger logger = LogManager.getLogger(HomeController.class.getName());

    @Autowired
    ObjectMapper objectMapper;

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
     * hive数据库服务2
     */
    @Autowired
    DbHiveWarnService dbHiveWarnService;
    
    /**
     * hive数据库-餐厨垃圾、废弃油脂
     */
    @Autowired
    DbHiveRecyclerWasteService dbHiveRecyclerWasteService;
    
    /**
     * hive数据库-排菜
     */
    @Autowired
    DbHiveDishService dbHiveDishService;
    
    /**
     * hive数据库-用料
     */
    @Autowired
    DbHiveMatService dbHiveMatService;
    
    /**
     * hive数据库-配货
     */
    @Autowired
    DbHiveGsService dbHiveGsService;
    
    /**
     * 教委系统学校信息服务
     */
    @Autowired
    private EduSchoolService eduSchoolService;

    /**
     * 汇总数据信息应用模型
     */
    SumDataInfoAppMod sumDataInfoAppMod = new SumDataInfoAppMod();

    /**
     * 业务明细数据信息应用模型
     */
    BusiDetDataInfoAppMod busiDetDataInfoAppMod = new BusiDetDataInfoAppMod();

    /**
     * 异常情况学校列表应用模型
     */
    AbnCondSchListAppMod abnCondSchListAppMod = new AbnCondSchListAppMod();
    
    /**
     * 逾期未处理预警列表应用模型
     */
    AbnOdNoProcWarnsAppMod abnOdNoProcWarns = new AbnOdNoProcWarnsAppMod();

    /**
     * 3.2.9.	排菜汇总信息应用模型
     */
    DishSumInfoAppMod dishSumInfoAppMod = new DishSumInfoAppMod();
    
    /**
     * 3.2.10.	学校排菜情况统计应用模型
     */
    SchDishSitStatAppMod schDishSitStatAppMod = new SchDishSitStatAppMod();
    
    /**
     * 3.2.25.	用料汇总信息应用模型
     */
    MatSumInfoAppMod matSumInfoAppMod = new MatSumInfoAppMod();
    
    /**
     * 3.2.26.	学校确认用料计划情况统计应用模型
     */
    SchConMatSitStatAppMod schConMatSitStatAppMod = new SchConMatSitStatAppMod();
    
    /**
     * 3.2.32.	配货验收汇总信息应用模型
     */
    GsAcceptSumInfoAppMod gsAcceptSumInfoAppMod = new GsAcceptSumInfoAppMod();
    
    /**
     * 3.2.33.	学校配货验收情况统计应用模型
     */
    SchGsAcceptSitStatAppMod schGsAcceptSitStatAppMod = new SchGsAcceptSitStatAppMod();
    
    /**
     * 3.2.43.	菜品留样汇总信息应用模型
     */
    DishRsSumInfoAppMod dishRsSumInfoAppMod = new DishRsSumInfoAppMod();
    
    /**
     * 3.2.44.	学校菜品留样情况统计应用模型
     */
    SchDishRsSitStatAppMod schDishRsSitStatAppMod = new SchDishRsSitStatAppMod();
    
    /**
     * 排菜数据汇总报表
     */
    ExpDishSumInfoAppMod expDishSumInfoAppMod = new ExpDishSumInfoAppMod();
    
    /**
     * 项目点排菜使用情况应用模型
     */
    PpDishUseDetsAppMod ppDishUseDetsAppMod = new PpDishUseDetsAppMod();
    
    /**
     * 项目点排菜使用情况应用模型
     */
    ExpPpDishUseDetsAppMod expPpDishUseDetsAppMod = new ExpPpDishUseDetsAppMod();
    
    
    /**
     * 3.2.1.	汇总数据信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/sumDataInfo", method = RequestMethod.GET)
    public String sumDataInfo(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SumDataInfoDTO sumDataInfoDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报列表应用模型函数
		if(isAuth) {
			sumDataInfoDTO = sumDataInfoAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate,
					db1Service,db2Service,saasService,dbHiveWarnService,dbHiveRecyclerWasteService,dbHiveDishService,dbHiveGsService);
		}else {
			logger.info("授权码：" + token + "，验证失败！");  
		}
        //设置响应数据
        if (sumDataInfoDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(sumDataInfoDTO);
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
     * 3.2.2.	业务明细数据信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/busiDetDataInfo", method = RequestMethod.GET)
    public String busiDetDataInfo(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        BusiDetDataInfoDTO busiDetDataInfoDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        
        String subLevel=request.getParameter("subLevel");;//所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效【预留】
		String compDep=request.getParameter("compDep");;//主管部门【预留】
        
        
        //统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
        String strStatMode = request.getParameter("statMode");
        int statMode = 0;
        if (strStatMode != null) {
        	statMode = Integer.parseInt(strStatMode);
        }
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报详情列表应用模型函数
		if(isAuth) {
			busiDetDataInfoDTO = busiDetDataInfoAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate,statMode, subLevel,compDep,
					db1Service,db2Service,saasService,eduSchoolService,dbHiveWarnService,dbHiveRecyclerWasteService,dbHiveDishService,
					dbHiveGsService);
		}else {
			logger.info("授权码：" + token + "，验证失败！");       
		}
        //设置响应数据
        if (busiDetDataInfoDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(busiDetDataInfoDTO);
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
     * 3.2.3.	异常情况学校列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/abnCondSchList", method = RequestMethod.GET)
    public String abnCondSchList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        AbnCondSchListDTO abnCondSchListDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //举报投诉ID
        String crId = request.getParameter("crId");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//投诉举报详情列表应用模型函数
  		//String subDate, String distName, String prefCity, String province, String schName, String contractor, int procStatus, Db1Service db1Service
		if(isAuth) {
			abnCondSchListDTO = abnCondSchListAppMod.appModFunc(distName, prefCity, province,startDate, endDate, db1Service);
		}else {
			logger.info("授权码：" + token + "，验证失败！");       
		}
        //设置响应数据
        if (abnCondSchListDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(abnCondSchListDTO);
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
     * 3.2.4.	逾期未处理预警列表
     * @param request
     * @return
     */
    //@RequestMapping(value = "/v1/abnOdNoProcWarns", method = RequestMethod.GET)
    public String abnOdNoProcWarns(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        AbnOdNoProcWarnsDTO abnOdNoProcWarnsDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//投诉举报详情列表应用模型函数
		if(isAuth) {
			abnOdNoProcWarnsDTO = abnOdNoProcWarns.appModFunc(distName, prefCity, province, startDate, endDate,  db1Service);
		}else {
			logger.info("授权码：" + token + "，验证失败！");        
		}
        //设置响应数据
        if (abnOdNoProcWarnsDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(abnOdNoProcWarnsDTO);
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
     * 3.2.9.	排菜汇总信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishSumInfo", method = RequestMethod.GET)
    public String dishSumInfo(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DishSumInfoDTO dishSumInfoDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报列表应用模型函数
		if(isAuth) {
			dishSumInfoDTO = dishSumInfoAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate, db1Service,db2Service,saasService,dbHiveDishService);
		}else {
			logger.info("授权码：" + token + "，验证失败！");  
		}
        //设置响应数据
        if (dishSumInfoDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(dishSumInfoDTO);
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
     * 3.2.10.	学校排菜情况统计
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/schDishSitStat", method = RequestMethod.GET)
    public String schDishSitStat(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SchDishSitStatDTO schDishSitStatDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        
        //统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
        String strStatMode = request.getParameter("statMode");
        int statMode = 0;
        if (strStatMode != null) {
        	statMode = Integer.parseInt(strStatMode);
        }
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报列表应用模型函数
		if(isAuth) {
			schDishSitStatDTO = schDishSitStatAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate,statMode, 
					db1Service,db2Service,saasService,eduSchoolService,dbHiveDishService);
		}else {
			logger.info("授权码：" + token + "，验证失败！");  
		}
        //设置响应数据
        if (schDishSitStatDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(schDishSitStatDTO);
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
     * 3.2.25.	用料汇总信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/matSumInfo", method = RequestMethod.GET)
    public String matSumInfo(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        MatSumInfoDTO matSumInfoDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate
        		+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报列表应用模型函数
		if(isAuth) {
			matSumInfoDTO = matSumInfoAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate, 
					db1Service,db2Service,saasService,dbHiveMatService);
		}else {
			logger.info("授权码：" + token + "，验证失败！");  
		}
        //设置响应数据
        if (matSumInfoDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(matSumInfoDTO);
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
     * 3.2.26.	学校确认用料计划情况统计
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/schConMatSitStat", method = RequestMethod.GET)
    public String schConMatSitStat(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SchConMatSitStatDTO schConMatSitStatDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        
        //统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
        String strStatMode = request.getParameter("statMode");
        int statMode = 0;
        if (strStatMode != null) {
        	statMode = Integer.parseInt(strStatMode);
        }
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);

		if(isAuth) {
			schConMatSitStatDTO = schConMatSitStatAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate,statMode,
					db1Service,db2Service,saasService,eduSchoolService,dbHiveMatService);
		}else {
			logger.info("授权码：" + token + "，验证失败！");  
		}
        //设置响应数据
        if (schConMatSitStatDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(schConMatSitStatDTO);
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
     * 3.2.32.	配货验收汇总信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/gsAcceptSumInfo", method = RequestMethod.GET)
    public String gsAcceptSumInfo(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        GsAcceptSumInfoDTO gsAcceptSumInfoDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报列表应用模型函数
		if(isAuth) {
			gsAcceptSumInfoDTO = gsAcceptSumInfoAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate,
					db1Service,db2Service,saasService,dbHiveGsService);
		}else {
			logger.info("授权码：" + token + "，验证失败！");  
		}
        //设置响应数据
        if (gsAcceptSumInfoDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(gsAcceptSumInfoDTO);
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
     * 3.2.33.	学校配货验收情况统计
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/schGsAcceptSitStat", method = RequestMethod.GET)
    public String schGsAcceptSitStat(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SchGsAcceptSitStatDTO schGsAcceptSitStatDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        
        //统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
        String strStatMode = request.getParameter("statMode");
        int statMode = 0;
        if (strStatMode != null) {
        	statMode = Integer.parseInt(strStatMode);
        }
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报列表应用模型函数
		if(isAuth) {
			schGsAcceptSitStatDTO = schGsAcceptSitStatAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate,statMode, 
					db1Service,db2Service,saasService,eduSchoolService,dbHiveGsService);
		}else {
			logger.info("授权码：" + token + "，验证失败！");  
		}
        //设置响应数据
        if (schGsAcceptSitStatDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(schGsAcceptSitStatDTO);
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
     * 3.2.43.	菜品留样汇总信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishRsSumInfo", method = RequestMethod.GET)
    public String dishRsSumInfo(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DishRsSumInfoDTO dishRsSumInfoDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报列表应用模型函数
		if(isAuth) {
			dishRsSumInfoDTO = dishRsSumInfoAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate, db1Service,db2Service,saasService);
		}else {
			logger.info("授权码：" + token + "，验证失败！");  
		}
        //设置响应数据
        if (dishRsSumInfoDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(dishRsSumInfoDTO);
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
     * 3.2.44.	学校菜品留样情况统计
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/schDishRsSitStat", method = RequestMethod.GET)
    public String schDishRsSitStat(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SchDishRsSitStatDTO schDishRsSitStatDTO = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        //开始提交日期
        String startDate = request.getParameter("startDate");
        //结束提交日期
        String endDate = request.getParameter("endDate");
        
        //统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
        String strStatMode = request.getParameter("statMode");
        int statMode = 0;
        if (strStatMode != null) {
        	statMode = Integer.parseInt(strStatMode);
        }
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报列表应用模型函数
		if(isAuth) {
			schDishRsSitStatDTO = schDishRsSitStatAppMod.appModFunc(token,distName, prefCity, province, startDate, endDate,statMode, db1Service,db2Service,saasService,eduSchoolService);
		}else {
			logger.info("授权码：" + token + "，验证失败！");  
		}
        //设置响应数据
        if (schDishRsSitStatDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(schDishRsSitStatDTO);
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
     * 排菜数据统计报表 导出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expDishSumInfo", method = RequestMethod.GET)
    public String v1_expDishRetSamples(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpDishSumInfoDTO expSumDataInfoDTO = null;
        boolean isAuth = false;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //就餐开始日期
        String startDate = request.getParameter("startDate");
        //就餐结束日期
        String endDate = request.getParameter("endDate");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //地级城市
        String prefCity = request.getParameter("prefCity");
        if (prefCity == null) {
            prefCity = request.getParameter("prefcity");
        }
        //省或直辖市
        String province = request.getParameter("province");
        logger.info("输入参数：" + "token = " + token + ", repastStartDate = " + startDate + 
        		", repasEndDate = " + endDate +
        		", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
        isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //导出菜品留样列表应用模型函数
        if (isAuth)
        	expSumDataInfoDTO = expDishSumInfoAppMod.appModFunc(token, startDate, endDate, distName, prefCity, province,
        			db1Service, db2Service, saasService,dbHiveDishService);
        else
            logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (expSumDataInfoDTO != null) {
            try {
                strResp = objectMapper.writeValueAsString(expSumDataInfoDTO);
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
     * 3.2.4 - 项目点排菜使用详情列表（包括排菜、用料、配货、留样）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ppDishUseDets", method = RequestMethod.GET)
    public String v1_ppDishUseDets(HttpServletRequest request,PpDishDets ppDishDets) {
        //初始化响应数据
        String strResp = null;
        PpDishUseDetsDTO pddDto = null;
        int code = 0;
        int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        logger.info("输入参数：" + ppDishDets.toString());
        //验证授权
        //项目点排菜详情列表应用模型函数
        pddDto = ppDishUseDetsAppMod.appModFunc(token,ppDishDets,db1Service, db2Service, saasService,dbHiveDishService);
        //设置响应数据
        if (pddDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(pddDto);
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
     * 3.2.4 - 项目点排菜使用详情列表（包括排菜、用料、配货、留样）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expPpDishUseDets", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> v1_expPpDishUseDets(HttpServletRequest request,PpDishDets ppDishDets) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        logger.info("输入参数：" + ppDishDets.toString());
        //项目点排菜详情列表应用模型函数
        return expPpDishUseDetsAppMod.appModFunc(token,ppDishDets,db1Service, db2Service, saasService,dbHiveDishService);
    }
}
