package com.tfit.BdBiProcSrvShEduOmc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ga.*;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.*;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveBaseService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Descritpion：运营管理-综合分析
 * @author: tianfang_infotech
 * @date: 2019/1/2 16:47
 */
@RestController
@RequestMapping(value = "/biOptAnl")
public class GlobalAnalysisController {
    private static final Logger logger = LogManager.getLogger(HomeController.class.getName());

    /**
     * mysql主数据库服务
     */
    @Autowired
    SaasService saasService;

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
     * hive数据库服务
     */
    @Autowired
    DbHiveService dbHiveService;
    
    /**
     * hive数据库服务
     */
    @Autowired
    DbHiveBaseService dbHiveBaseService;
    
    /**
     * 学校名称编码列表应用模型
     */
    SchNameCodesAppMod sncAppMod = new SchNameCodesAppMod();

    /**
     * 物料分类编码列表应用模型
     */
    MatClassifyCodesAppMod mccAppMod = new MatClassifyCodesAppMod();

    /**
     * 原料类别编码列表应用模型
     */
    MatCategoryCodesAppMod mcacAppMod = new MatCategoryCodesAppMod();

    /**
     * 综合分析原料供应统计列表应用模型
     */
    CaMatSupStatsAppMod cmssAppMod = new CaMatSupStatsAppMod();

    /**
     * 导出综合分析原料供应统计列表应用模型
     */
    ExpCaMatSupStatsAppMod ecmssAppMod = new ExpCaMatSupStatsAppMod();

    /**
     * 物料名称编码列表应用模型
     */
    MatNameCodesAppMod matncAppMod = new MatNameCodesAppMod();

    /**
     * 供应商名称编码列表应用模型
     */
    SupNameCodesAppMod supncAppMod = new SupNameCodesAppMod();

    /**
     * 综合分析原料供应明细列表应用模型
     */
    CaMatSupDetsAppMod cmsdAppMod = new CaMatSupDetsAppMod();

    /**
     * 导出综合分析原料供应明细列表应用模型
     */
    ExpCaMatSupDetsAppMod ecmsdAppMod = new ExpCaMatSupDetsAppMod();

    /**
     * 菜品类别编码列表应用模型
     */
    DishTypeCodesAppMod ditcAppMod = new DishTypeCodesAppMod();

    /**
     * 综合分析菜品供应统计列表应用模型
     */
    CaDishSupStatsAppMod cdssAppMod = new CaDishSupStatsAppMod();

    /**
     * 导出综合分析菜品供应统计列表应用模型
     */
    ExpCaDishSupStatsAppMod ecdssAppMod = new ExpCaDishSupStatsAppMod();

    /**
     * 菜品名称编码列表应用模型
     */
    DishNameCodesAppMod dincAppMod = new DishNameCodesAppMod();

    /**
     * 综合分析菜品供应明细列表应用模型
     */
    CaDishSupDetsAppMod cdsdAppMod = new CaDishSupDetsAppMod();

    /**
     * 导出综合分析菜品供应明细列表应用模型
     */
    ExpCaDishSupDetsAppMod ecdsdAppMod = new ExpCaDishSupDetsAppMod();
    
    /**
     * 3.9.1 - 学校名称编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/schNameCodes", method = RequestMethod.GET)
    public String v1_schNameCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SchNameCodesDTO sncDto = null;
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
        //用户名
        String userName = request.getParameter("userName");
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null){
            pageSize = String.valueOf(AppModConfig.maxPageSize);}
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", userName = " + userName + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //学校名称编码列表应用模型函数
		if(isAuth)
			 sncDto = sncAppMod.appModFunc(token, distName, prefCity, province, userName, page, pageSize, db1Service, db2Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");       
        //设置响应数据
        if (sncDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(sncDto);
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
     * 3.9.2 - 物料分类编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/matClassifyCodes", method = RequestMethod.GET)
    public String v1_matClassifyCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        MatClassifyCodesDTO mccDto = null;
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
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //物料分类编码列表应用模型函数
		if(isAuth)
			 mccDto = mccAppMod.appModFunc(distName, prefCity, province, db1Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");       
        //设置响应数据
        if (mccDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(mccDto);
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
     * 3.9.3 - 原料类别编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/matCategoryCodes", method = RequestMethod.GET)
    public String v1_matCategoryCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        MatCategoryCodesDTO mcacDto = null;
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
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //原料类别编码列表应用模型函数
		if(isAuth)
			 mcacDto = mcacAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");       
        //设置响应数据
        if (mcacDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(mcacDto);
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
     * 3.9.4 - 综合分析原料供应统计列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/caMatSupStats", method = RequestMethod.GET)
    public String v1_caMatSupStats(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CaMatSupStatsDTO cmssDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始用料日期，格式：xxxx-xx-xx
        String startUseDate = request.getParameter("startSubDate");
        //结束用料日期，格式：xxxx-xx-xx
        String endUseDate = request.getParameter("endSubDate");
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
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //学校名称
        String schName = request.getParameter("schName");
        //分类（物料）
        String matClassify = request.getParameter("matClassify");
        //原料类别，0:主料，1:辅料
        String matCategory = request.getParameter("matCategory");
        //原料标准名称
        String matStdName = request.getParameter("matStdName");
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
        logger.info("输入参数：" + "token = " + token + ", startUseDate = " + startUseDate + ", endUseDate = " + endUseDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", schType = " + schType + ", schName = " + schName + ", matClassify = " + matClassify + ", matCategory = " + matCategory + ", matStdName = " + matStdName + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //综合分析原料供应统计列表应用模型函数
		if(isAuth)
			cmssDto = cmssAppMod.appModFunc(token, startUseDate, endUseDate, distName, prefCity, province, schType, 
					schName, matClassify, matCategory, matStdName, page, pageSize, 
					db1Service, db2Service, saasService,dbHiveService);
        else
			logger.info("授权码：" + token + "，验证失败！");

        //设置响应数据
        if (cmssDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(cmssDto);
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
     * 3.9.5 - 导出综合分析原料供应统计列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expCaMatSupStats", method = RequestMethod.GET)
    public String v1_expCaMatSupStats(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpCaMatSupStatsDTO ecmssDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始用料日期，格式：xxxx-xx-xx
        String startUseDate = request.getParameter("startSubDate");
        //结束用料日期，格式：xxxx-xx-xx
        String endUseDate = request.getParameter("endSubDate");
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
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，
        //10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //学校名称
        String schName = request.getParameter("schName");
        //分类（物料）
        String matClassify = request.getParameter("matClassify");
        //原料类别，0:主料，1:辅料
        String matCategory = request.getParameter("matCategory");
        //原料标准名称
        String matStdName = request.getParameter("matStdName");
        logger.info("输入参数：" + "token = " + token + ", startUseDate = " + startUseDate + ", endUseDate = " + endUseDate 
        		+ ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province
        		+ ", schType = " + schType + ", schName = " + schName + ", matClassify = " + matClassify 
        		+ ", matCategory = " + matCategory + ", matStdName = " + matStdName);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出综合分析原料供应统计列表应用模型函数
		if(isAuth)
			ecmssDto = ecmssAppMod.appModFunc(token, startUseDate, endUseDate, distName, prefCity, province, 
					schType, schName, matClassify, matCategory, matStdName, 
					db1Service, db2Service, saasService,dbHiveService);
        else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ecmssDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ecmssDto);
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
     * 3.9.6 - 物料名称编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/matNameCodes", method = RequestMethod.GET)
    public String v1_matNameCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        MatNameCodesDTO mncDto = null;
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
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null){
            pageSize = String.valueOf(AppModConfig.maxPageSize);}
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //物料名称编码列表应用模型函数
		if(isAuth)
			mncDto = matncAppMod.appModFunc(distName, prefCity, province, page, pageSize, db1Service, dbHiveBaseService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (mncDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(mncDto);
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
     * 3.9.7 - 供应商名称编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/supNameCodes", method = RequestMethod.GET)
    public String v1_supNameCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SupNameCodesDTO sncDto = null;
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
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null){
            pageSize = String.valueOf(AppModConfig.maxPageSize);}
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //供应商名称编码列表应用模型函数
		if(isAuth)
			sncDto = supncAppMod.appModFunc(distName, prefCity, province, page, pageSize, db1Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (sncDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(sncDto);
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
     * 3.9.8 - 综合分析原料供应明细列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/caMatSupDets", method = RequestMethod.GET)
    public String v1_caMatSupDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CaMatSupDetsDTO cmsdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始用料日期，格式：xxxx-xx-xx
        String startUseDate = request.getParameter("startSubDate");
        //结束用料日期，格式：xxxx-xx-xx
        String endUseDate = request.getParameter("endSubDate");
        //学校名称
        String schName = request.getParameter("schName");
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
        //物料名称
        String matName = request.getParameter("matName");
        //物料名称
        String stdMatName = request.getParameter("stdMatName");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //供应商名称
        String supplierName = request.getParameter("supplierName");
        //配货批次号
        String distrBatNumber = request.getParameter("distrBatNumber");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //验收状态，0:待验收，1:已验收
        String acceptStatus = request.getParameter("acceptStatus");
        //经营模式（供餐模式，1:外包-现场加工，2:外包-快餐配送
        String optMode = request.getParameter("optMode");
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
        logger.info("输入参数：" + "token = " + token + ", startUseDate = " + startUseDate + ", endUseDate = " + endUseDate
        		+ ", schName = " + schName + ", distName = " + distName + ", prefCity = " + prefCity
        		+ ", province = " + province + ", matName = " + matName + ", rmcName = " + rmcName 
        		+ ", supplierName = " + supplierName + ", distrBatNumber = " + distrBatNumber 
        		+ ", schType = " + schType + ", acceptStatus = " + acceptStatus
        		+ ", optMode = " + optMode + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //综合分析原料供应统计列表应用模型函数
		if(isAuth)
			cmsdDto = cmsdAppMod.appModFunc(token, startUseDate, endUseDate, schName, distName, prefCity,
					province, matName,stdMatName,rmcName, supplierName, distrBatNumber, schType, acceptStatus, optMode, 
					page, pageSize, db1Service, db2Service, saasService,dbHiveService);
        else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (cmsdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(cmsdDto);
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
     * 3.9.9 - 导出综合分析原料供应明细列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expCaMatSupDets", method = RequestMethod.GET)
    public String v1_expCaMatSupDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpCaMatSupDetsDTO ecmsdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始用料日期，格式：xxxx-xx-xx
        String startUseDate = request.getParameter("startSubDate");
        //结束用料日期，格式：xxxx-xx-xx
        String endUseDate = request.getParameter("endSubDate");
        //学校名称
        String schName = request.getParameter("schName");
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
        //物料名称
        String matName = request.getParameter("matName");
        //原料名称
        String stdMatName = request.getParameter("stdMatName");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //供应商名称
        String supplierName = request.getParameter("supplierName");
        //配货批次号
        String distrBatNumber = request.getParameter("distrBatNumber");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，
        //11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //验收状态，0:待验收，1:已验收
        String acceptStatus = request.getParameter("acceptStatus");
        //经营模式（供餐模式，1:外包-现场加工，2:外包-快餐配送
        String optMode = request.getParameter("optMode");
        logger.info("输入参数：" + "token = " + token + ", startUseDate = " + startUseDate 
        		+ ", endUseDate = " + endUseDate + ", schName = " + schName + ", distName = " + distName
                + ", prefCity = " + prefCity + ", province = " + province + ", matName = " + matName + ", rmcName = " + rmcName 
                + ", supplierName = " + supplierName + ", distrBatNumber = " + distrBatNumber + ", schType = " + schType 
                + ", acceptStatus = " + acceptStatus + ", optMode = " + optMode);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出综合分析原料供应明细列表应用模型函数
		if(isAuth)
			ecmsdDto = ecmsdAppMod.appModFunc(token, startUseDate, endUseDate, schName, distName, 
					prefCity, province, matName,stdMatName, rmcName, supplierName, distrBatNumber, schType,
					acceptStatus, optMode, db1Service, db2Service, saasService,dbHiveService);
        else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ecmsdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ecmsdDto);
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
     * 3.9.10 - 菜品类别编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishTypeCodes", method = RequestMethod.GET)
    public String v1_dishTypeCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DishTypeCodesDTO dtcDto = null;
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
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //菜品类别编码列表应用模型函数
		if(isAuth)
			 dtcDto = ditcAppMod.appModFunc(distName, prefCity, province, db1Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");       
        //设置响应数据
        if (dtcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(dtcDto);
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
     * 3.9.11 - 综合分析菜品供应统计列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/caDishSupStats", method = RequestMethod.GET)
    public String v1_caDishSupStats(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CaDishSupStatsDTO cdssdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //就餐开始日期，格式：xxxx-xx-xx
        String repastStartDate = request.getParameter("startSubDate");
        //就餐结束日期，格式：xxxx-xx-xx
        String repasEndDate = request.getParameter("endSubDate");
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
        //学校名称
        String schName = request.getParameter("schName");
        //菜品类别
        String dishType = request.getParameter("dishType");
        //餐别，0:早餐，1:午餐，2:晚餐，3:午点，4:早点
        String caterType = request.getParameter("caterType");
        //菜品名称
        String dishName = request.getParameter("dishName");        
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
        logger.info("输入参数：" + "token = " + token + ", repastStartDate = " + repastStartDate + ", repasEndDate = " + repasEndDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", schName = " + schName + ", dishType = " + dishType + ", caterType = " + caterType + ", dishName = " + dishName + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //综合分析菜品供应统计列表应用模型函数
		if(isAuth)
			cdssdDto = cdssAppMod.appModFunc(token, repastStartDate, repasEndDate, distName, prefCity, 
					province, schName, dishType, caterType, dishName, page, pageSize, 
					db1Service, db2Service, saasService,dbHiveService);
        else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (cdssdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(cdssdDto);
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
     * 3.9.12 - 导出综合分析菜品供应统计列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expCaDishSupStats", method = RequestMethod.GET)
    public String v1_expCaDishSupStats(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpCaDishSupStatsDTO ecdssDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //就餐开始日期，格式：xxxx-xx-xx
        String repastStartDate = request.getParameter("startSubDate");
        //就餐结束日期，格式：xxxx-xx-xx
        String repasEndDate = request.getParameter("endSubDate");
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
        //学校名称
        String schName = request.getParameter("schName");
        //菜品类别
        String dishType = request.getParameter("dishType");
        //餐别，0:早餐，1:午餐，2:晚餐，3:午点，4:早点
        String caterType = request.getParameter("caterType");
        //菜品名称
        String dishName = request.getParameter("dishName");
        logger.info("输入参数：" + "token = " + token + ", repastStartDate = " + repastStartDate + 
        		", repasEndDate = " + repasEndDate + ", distName = " + distName + 
        		", prefCity = " + prefCity + ", province = " + province + ", schName = " + schName + 
        		", dishType = " + dishType + ", caterType = " + caterType + ", dishName = " + dishName);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出综合分析菜品供应统计列表应用模型函数
		if(isAuth)
			ecdssDto = ecdssAppMod.appModFunc(token, repastStartDate, repasEndDate, distName,
					prefCity, province, schName, dishType, caterType, dishName, 
					db1Service, db2Service, saasService,dbHiveService);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ecdssDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ecdssDto);
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
     * 3.9.13 - 菜品名称编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/dishNameCodes", method = RequestMethod.GET)
    public String v1_dishNameCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        DishNameCodesDTO dncDto = null;
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
        //页号
        String page = request.getParameter("page");
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = request.getParameter("pageSize");
        if (pageSize == null){
            pageSize = String.valueOf(AppModConfig.maxPageSize);}
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //菜品名称编码列表应用模型函数
		if(isAuth)
			dncDto = dincAppMod.appModFunc(distName, prefCity, province, page, pageSize, db1Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (dncDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(dncDto);
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
     * 3.9.14 - 综合分析菜品供应明细列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/caDishSupDets", method = RequestMethod.GET)
    public String v1_caDishSupDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CaDishSupDetsDTO cdsdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //就餐开始日期，格式：xxxx-xx-xx
        String repastStartDate = request.getParameter("startSubDate");
        //就餐结束日期，格式：xxxx-xx-xx
        String repasEndDate = request.getParameter("endSubDate");
        //学校名称
        String schName = request.getParameter("schName");
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
        //菜品名称
        String dishName = request.getParameter("dishName");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //餐别，0:早餐，1:午餐，2:晚餐，3:午点，4:早点
        String caterType = request.getParameter("caterType");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //经营模式（供餐模式，1:外包-现场加工，2:外包-快餐配送
        String optMode = request.getParameter("optMode");
        //菜单名称
        String menuName = request.getParameter("menuName");
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
        logger.info("输入参数：" + "token = " + token + ", repastStartDate = " + repastStartDate + ", repasEndDate = " + repasEndDate + ", schName = " + schName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", dishName = " + dishName + ", rmcName = " + rmcName + ", caterType = " + caterType + ", schType = " + schType + ", schProp = " + schProp + ", optMode = " + optMode + ", menuName = " + menuName + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //综合分析菜品供应明细列表应用模型函数
		if(isAuth)
			cdsdDto = cdsdAppMod.appModFunc(token, repastStartDate, repasEndDate, schName, distName, 
					prefCity, province, dishName, rmcName, caterType, schType, schProp, optMode, 
					menuName, page, pageSize, db1Service, db2Service, saasService,dbHiveService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (cdsdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(cdsdDto);
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
     * 3.9.15 - 导出综合分析菜品供应明细列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expCaDishSupDets", method = RequestMethod.GET)
    public String v1_expCaDishSupDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpCaDishSupDetsDTO ecdsdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //就餐开始日期，格式：xxxx-xx-xx
        String repastStartDate = request.getParameter("startSubDate");
        //就餐结束日期，格式：xxxx-xx-xx
        String repasEndDate = request.getParameter("endSubDate");
        //学校名称
        String schName = request.getParameter("schName");
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
        //菜品名称
        String dishName = request.getParameter("dishName");
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //餐别，0:早餐，1:午餐，2:晚餐，3:午点，4:早点
        String caterType = request.getParameter("caterType");
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //经营模式（供餐模式，1:外包-现场加工，2:外包-快餐配送
        String optMode = request.getParameter("optMode");
        //菜单名称
        String menuName = request.getParameter("menuName");
        logger.info("输入参数：" + "token = " + token + ", repastStartDate = " + repastStartDate + ", repasEndDate = " + repasEndDate + ", schName = " + schName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", dishName = " + dishName + ", rmcName = " + rmcName + ", caterType = " + caterType + ", schType = " + schType + ", schProp = " + schProp + ", optMode = " + optMode + ", menuName = " + menuName);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出综合分析菜品供应明细列表应用模型函数
		if(isAuth)
			ecdsdDto = ecdsdAppMod.appModFunc(token, repastStartDate, repasEndDate, 
					schName, distName, prefCity, province, dishName, rmcName, caterType, 
					schType, schProp, optMode, menuName, 
					db1Service, db2Service, saasService,dbHiveService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (ecdsdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ecdsdDto);
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
}
