package com.tfit.BdBiProcSrvShEduOmc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.annotation.CheckUserToken;
import com.tfit.BdBiProcSrvShEduOmc.appmod.search.ExpSearchMaterialListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.search.ExpSearchMaterialListPicAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.search.ExpSearchSchListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.search.ExpSearchSchListPicAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.search.SearchByMaterialAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.search.SearchBySchoolAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.search.SearchMaterialListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.search.SearchSchListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.ExpCommonDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.ExpSearchBySchoolDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchByMaterialDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchBySchoolDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchMaterialListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Custom1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;

//一键查询BI应用服务
@CheckUserToken
@RestController
@RequestMapping(value = "/biOptAnl")
public class SearchController {
	private static final Logger logger = LogManager.getLogger(OptAnlController.class.getName());
	
	//mysql主数据库服务
	@Autowired
	SaasService saasService;
	
	//mysql从数据库服务1
	@Autowired
	Custom1Service custom1Service;
	
	//mysql数据库服务1
	@Autowired
	Db1Service db1Service;
			
	//mysql数据库服务2
	@Autowired
	Db2Service db2Service;
	
	//hive数据库服务
	@Autowired
	DbHiveService dbHiveService;
	
			
	@Autowired
	ObjectMapper objectMapper;
	
    /**
     * 应急指挥一键查询-学校信息列表
     */
	SearchSchListAppMod searchSchListAppMod = new SearchSchListAppMod();
    

    /**
     * 应急指挥一键查询-学校详情
     */
	SearchBySchoolAppMod searchBySchoolAppMod = new SearchBySchoolAppMod();
	
    /**
     * 应急指挥一键查询-学校详情(导出)
     */
	ExpSearchSchListAppMod expSearchBySchoolAppMod = new ExpSearchSchListAppMod();
	
    /**
     * 应急指挥一键查询-学校详情(导出图片)
     */
	ExpSearchSchListPicAppMod expSearchSchListPicAppMod = new ExpSearchSchListPicAppMod();
	
    /**
     * 应急指挥一键排菜-学校原料信息列表
     */
	SearchMaterialListAppMod searchMaterialListAppMod = new SearchMaterialListAppMod();
	
    /**
     * 应急指挥一键排菜-学校原料详情列表
     */
	SearchByMaterialAppMod searchByMaterialAppMod = new SearchByMaterialAppMod();
	
    /**
     * 导出应急指挥一键排菜-学校原料详情列表
     */
	ExpSearchMaterialListAppMod expSearchMaterialListAppMod = new ExpSearchMaterialListAppMod();
	
    /**
     * 导出应急指挥一键排菜-学校原料配送单信息
     */
	ExpSearchMaterialListPicAppMod expSearchMaterialListPicAppMod = new ExpSearchMaterialListPicAppMod();
	
    /**
     * 应急指挥一键查询-学校信息列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/searchSchList", method = RequestMethod.GET)
    public String v1_searchSchList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SearchSchListDTO ebksDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //项目点名称
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
        
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";
  		
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
		if(isAuth)
			 ebksDto = searchSchListAppMod.appModFunc(token, schName, distName, prefCity, province, page, pageSize, db1Service, db2Service,
					 dbHiveService);
		else
			logger.info("授权码：" + token + "，验证失败！");       
        //设置响应数据
        if (ebksDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ebksDto);
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
     * 应急指挥一键查询-学校详情（排菜、供应商、证书等）
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/searchBySchool", method = RequestMethod.GET)
    public String v1_searchBySchool(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SearchBySchoolDTO ebksDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //项目点名称
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
        
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";
  		
        //就餐开始日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startDate");
        //就餐结束日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endDate");
  		
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
		if(isAuth)
			 ebksDto = searchBySchoolAppMod.appModFunc(token, schName, distName, prefCity, province, startDate, endDate, 
					 page, pageSize, db1Service, db2Service, dbHiveService);
			logger.info("授权码：" + token + "，验证失败！");       
        //设置响应数据
        if (ebksDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ebksDto);
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
     * 应急指挥一键查询-学校详情（排菜、供应商、证书等） 导出
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expSearchSchList", method = RequestMethod.GET)
    public String v1_expSearchSchList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpSearchBySchoolDTO ecmsdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始用料日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startDate");
        //结束用料日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endDate");
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
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate 
        		+ ", endDate = " + endDate + ", schName = " + schName + ", distName = " + distName);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出综合分析原料供应明细列表应用模型函数
		if(isAuth)
			ecmsdDto = expSearchBySchoolAppMod.appModFunc(token, startDate, endDate, schName, distName, prefCity, province, 
					db1Service, db2Service, saasService, dbHiveService);
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
     * 应急指挥一键查询-学校详情 导出配货单图片
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expSearchSchListPic", method = RequestMethod.GET)
    public String v1_expSearchSchListPic(HttpServletRequest request,HttpServletResponse response) {
        //初始化响应数据
        String strResp = null;
        ExpSearchBySchoolDTO ecmsdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始用料日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startDate");
        //结束用料日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endDate");
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
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate 
        		+ ", endDate = " + endDate + ", schName = " + schName + ", distName = " + distName);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出综合分析原料供应明细列表应用模型函数
		if(isAuth)
			ecmsdDto = expSearchSchListPicAppMod.appModFunc(token, startDate, endDate, schName, distName, prefCity, province, 
					db1Service, db2Service, saasService, dbHiveService,request,response);
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
     * 应急指挥一键排查-学校原料信息列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/searchMaterialList", method = RequestMethod.GET)
    public SearchMaterialListDTO v1_searchMaterialList(HttpServletRequest request,@RequestParam Map<String,String> inputParm) {
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		
  		logger.info("输入参数：" + inputParm.toString());
		return searchMaterialListAppMod.appModFunc(token, inputParm, db1Service, db2Service,
					 dbHiveService);
    }
    
    
    /**
     * 应急指挥一键排查-学校详情（排菜、供应商、证书等）
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/searchByMaterial", method = RequestMethod.GET)
    public SearchByMaterialDTO v1_searchByMaterial(HttpServletRequest request,@RequestParam Map<String,String> inputParm) {
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        logger.info("输入参数：" + inputParm.toString());
		return  searchByMaterialAppMod.appModFunc(token, inputParm, db1Service, db2Service, dbHiveService);
    }

    /**
     * 一键排查导出列表
     *
     * @param request+
     * @return
     */
    @RequestMapping(value = "/v1/expSearchMaterialList", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> v1_expSearchMaterialList(HttpServletRequest request,@RequestParam Map<String,String> inputMap) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        logger.info("输入参数：" + inputMap.toString());
        //项目点排菜详情列表应用模型函数
        return expSearchMaterialListAppMod.appModFunc(token, inputMap, db1Service, db2Service, saasService, dbHiveService);
    }
    
    /**
     * 一键排查导出图片
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expSearchMaterialListPic", method = RequestMethod.GET)
    public ApiResponse<ExpCommonDTO> v1_expSearchMaterialListPic(HttpServletRequest request,HttpServletResponse response,@RequestParam Map<String,String> inputMap) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        logger.info("输入参数：" + inputMap.toString());
        //项目点排菜详情列表应用模型函数
        return expSearchMaterialListPicAppMod.appModFunc(token,inputMap, db1Service, db2Service, saasService, dbHiveService, request, response);
    }
}
