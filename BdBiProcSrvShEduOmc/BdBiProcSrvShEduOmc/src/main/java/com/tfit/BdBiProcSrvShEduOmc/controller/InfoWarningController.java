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
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.ExpWarnAllLicDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.ExpWarnAllLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.ExpWarnMaterialOverProAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.ExpWarnMaterialOverProDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.ExpWarnRmcLicDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.ExpWarnRmcLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.ExpWarnSchLicDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.ExpWarnSchLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.ExpWarnStaffLicDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.ExpWarnStaffLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.LicAudStatusCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.LicStatusCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.LicTypeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.TrigWarnUnitCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.WarnAllLicDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.WarnAllLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.WarnMaterialOverProAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.WarnMaterialOverProDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.WarnRmcLicDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.WarnRmcLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.WarnSchLicDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.WarnSchLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.WarnStaffLicDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.iw.WarnStaffLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnAllLicDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnAllLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnMaterialOverProDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnMaterialOverProDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnRmcLicDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnRmcLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnSchLicDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnSchLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnStaffLicDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.ExpWarnStaffLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.LicAudStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.LicStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.LicTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.TrigWarnUnitCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnAllLicDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnAllLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnMaterialOverProDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnMaterialOverProDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnRmcLicDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnRmcLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnSchLicDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnSchLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnStaffLicDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnStaffLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;

/**
 * @Descritpion：业务数据-信息预警
 * @author: tianfang_infotech
 * @date: 2019/1/2 16:26
 */
@CheckUserToken
@RestController
@RequestMapping(value = "/biOptAnl")
public class InfoWarningController {
    private static final Logger logger = LogManager.getLogger(HomeController.class.getName());

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

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 证照预警全部证件列表应用模型
     */
    WarnAllLicsAppMod walAppMod = new WarnAllLicsAppMod();

    /**
     * 导出证照预警全部证件列表应用模型
     */
    ExpWarnAllLicsAppMod ewalAppMod = new ExpWarnAllLicsAppMod();

    /**
     * 证件类型编码列表应用模型
     */
    LicTypeCodesAppMod ltcAppMod = new LicTypeCodesAppMod();
    
    //证件状况编码列表应用模型
    LicStatusCodesAppMod lscAppMod = new LicStatusCodesAppMod();

    /**
     * 证件审核状态编码列表应用模型
     */
    LicAudStatusCodesAppMod lascAppMod = new LicAudStatusCodesAppMod();

    /**
     * 触发预警单位编码列表应用模型
     */
    TrigWarnUnitCodesAppMod twucAppMod = new TrigWarnUnitCodesAppMod();

    /**
     * 证照预警全部证件详情列表应用模型
     */
    WarnAllLicDetsAppMod waldAppMod = new WarnAllLicDetsAppMod();

    /**
     * 导出证照预警全部证件详情列表应用模型
     */
    ExpWarnAllLicDetsAppMod ewaldAppMod = new ExpWarnAllLicDetsAppMod();

    /**
     * 证照预警学校证件列表应用模型
     */
    WarnSchLicsAppMod wslAppMod = new WarnSchLicsAppMod();

    /**
     * 导出证照预警学校证件列表应用模型
     */
    ExpWarnSchLicsAppMod ewslAppMod = new ExpWarnSchLicsAppMod();

    /**
     * 证照预警学校证件详情列表应用模型
     */
    WarnSchLicDetsAppMod wsldAppMod = new WarnSchLicDetsAppMod();

    /**
     * 导出证照预警学校证件详情列表应用模型
     */
    ExpWarnSchLicDetsAppMod ewsldAppMod = new ExpWarnSchLicDetsAppMod();

    /**
     * 证照预警团餐公司证件列表应用模型
     */
    WarnRmcLicsAppMod wrlAppMod = new WarnRmcLicsAppMod();

    /**
     * 导出证照预警团餐公司证件列表应用模型
     */
    ExpWarnRmcLicsAppMod ewrlAppMod = new ExpWarnRmcLicsAppMod();

    /**
     * 证照预警团餐公司证件详情列表应用模型
     */
    WarnRmcLicDetsAppMod wrldAppMod = new WarnRmcLicDetsAppMod();

    /**
     * 导出证照预警团餐公司证件详情列表应用模型
     */
    ExpWarnRmcLicDetsAppMod ewrldAppMod = new ExpWarnRmcLicDetsAppMod();

    /**
     * 证照预警人员证件列表应用模型
     */
    WarnStaffLicsAppMod wstlAppMod = new WarnStaffLicsAppMod();

    /**
     * 导出证照预警人员证件列表应用模型
     */
    ExpWarnStaffLicsAppMod ewstlAppMod = new ExpWarnStaffLicsAppMod();

    /**
     * 证照预警人员证件详情列表应用模型
     */
    WarnStaffLicDetsAppMod wstldAppMod = new WarnStaffLicDetsAppMod();

    /**
     * 导出证照预警人员证件详情列表应用模型
     */
    ExpWarnStaffLicDetsAppMod ewstldAppMod = new ExpWarnStaffLicDetsAppMod();

    WarnMaterialOverProAppMod warnMaterialOverProAppMod = new WarnMaterialOverProAppMod();
    WarnMaterialOverProDetsAppMod warnMaterialOverProDetsAppMod = new WarnMaterialOverProDetsAppMod();
    ExpWarnMaterialOverProAppMod expWarnMaterialOverProAppMod = new ExpWarnMaterialOverProAppMod();
    ExpWarnMaterialOverProDetsAppMod expWarnMaterialOverProDetsAppMod = new ExpWarnMaterialOverProDetsAppMod();

    /**
     * 3.3.1 - 证照预警全部证件列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/warnAllLics", method = RequestMethod.GET)
    public String v1_warnAllLics(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WarnAllLicsDTO walDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        if (pageSize == null) {
            pageSize = "20";
        }
        
        String distNames = request.getParameter("distNames");
        String departmentIds = request.getParameter("departmentIds");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //证证照预警全部证件列表应用模型函数
		if(isAuth)
			walDto = walAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province,distNames,departmentIds,schSelMode, page, pageSize, db1Service, db2Service,dbHiveWarnService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (walDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(walDto);
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
     * 3.3.2 - 导出证照预警全部证件列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWarnAllLics", method = RequestMethod.GET)
    public String v1_expWarnAllLics(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpWarnAllLicsDTO ewalDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        String distNames = request.getParameter("distNames");
        String departmentIds = request.getParameter("departmentIds");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出证照预警全部证件列表应用模型函数
		if(isAuth)
			ewalDto = ewalAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province,distNames,departmentIds,schSelMode, db1Service, db2Service, saasService,dbHiveWarnService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (ewalDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ewalDto);
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
     * 3.3.3 - 证件类型编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/licTypeCodes", method = RequestMethod.GET)
    public String v1_licTypeCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        LicTypeCodesDTO ltcDto = null;
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
        //证件类型编码列表应用模型函数
		if(isAuth)
			ltcDto = ltcAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (ltcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ltcDto);
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
    
    //证件状况编码列表
    @RequestMapping(value = "/v1/licStatusCodes", method = RequestMethod.GET)
    public String v1_licStatusCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        LicStatusCodesDTO lscDto = null;
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
        //证件类型编码列表应用模型函数
		if(isAuth)
			lscDto = lscAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (lscDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(lscDto);
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
     * 3.3.4 - 证件审核状态编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/licAudStatusCodes", method = RequestMethod.GET)
    public String v1_licAudStatusCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        LicAudStatusCodesDTO lascDto = null;
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
        //证件审核状态编码列表应用模型函数
		if(isAuth)
			lascDto = lascAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (lascDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(lascDto);
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
     * 3.3.5 - 触发预警单位编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/trigWarnUnitCodes", method = RequestMethod.GET)
    public String v1_trigWarnUnitCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        TrigWarnUnitCodesDTO twucDto = null;
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
        if (pageSize == null) {
            pageSize = String.valueOf(AppModConfig.maxPageSize);
        }
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //触发预警单位编码列表应用模型函数
		if(isAuth)
			twucDto = twucAppMod.appModFunc(distName, prefCity, province, page, pageSize, db1Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (twucDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(twucDto);
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
     * 3.3.6 - 证照预警全部证件详情列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/warnAllLicDets", method = RequestMethod.GET)
    public String v1_warnAllLicDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WarnAllLicDetsDTO waldDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        //触发预警单位
        String trigWarnUnit = request.getParameter("trigWarnUnit");
        //审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatus = request.getParameter("licAuditStatus");
        //审核状态集合，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatuss = request.getParameter("licAuditStatuss");
        //开始消除日期，格式：xxxx-xx-xx
        String startElimDate = request.getParameter("startElimDate");
        //结束消除日期，格式：xxxx-xx-xx
        String endElimDate = request.getParameter("endElimDate");
        //开始有效日期，格式：xxxx-xx-xx
        String startValidDate = request.getParameter("startValidDate");
        //结束有效日期，格式：xxxx-xx-xx
        String endValidDate = request.getParameter("endValidDate");
        //证件号码
        String licNo = request.getParameter("licNo");
        
        //管理部门
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        //证件主体
        String fullName = request.getParameter("fullName");
        //学制
        String schType = request.getParameter("schType");
        //学校性质
        String schProp = request.getParameter("schProp");
        //证件类型，0:食品经营许可证，1:营业执照，2:健康证，3:餐饮服务许可证，4:A1证，5:A2证，6:B证，7:C证
        String licType = request.getParameter("licType");
        //证件状况，0:逾期，1:到期
        String licStatus = request.getParameter("licStatus");
        
        
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
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", schName = " + schName + ", trigWarnUnit = " + trigWarnUnit + ", licType = " + licType + ", licStatus = " + licStatus + ", licAuditStatus = " + licAuditStatus + ", startElimDate = " + startElimDate + ", endElimDate = " + endElimDate + ", startValidDate = " + startValidDate + ", endValidDate = " + endValidDate + ", licNo = " + licNo + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //证照预警全部证件详情列表应用模型函数
		if(isAuth)
			waldDto = waldAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province, schName, 
					trigWarnUnit, licType, licStatus, licAuditStatus,licAuditStatuss, startElimDate, endElimDate, startValidDate,
					endValidDate, licNo, 
					departmentId,departmentIds,fullName,schType,schProp,
					page, pageSize, db1Service, db2Service, saasService,dbHiveWarnService);
        else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (waldDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(waldDto);
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
     * 3.3.7 - 导出证照预警全部证件详情列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWarnAllLicDets", method = RequestMethod.GET)
    public String v1_expWarnAllLicDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpWarnAllLicDetsDTO ewaldDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        //触发预警单位
        String trigWarnUnit = request.getParameter("trigWarnUnit");
        //证件类型，0:食品经营许可证，1:营业执照，2:健康证，3:餐饮服务许可证，4:A1证，5:A2证，6:B证，7:C证
        String licType = request.getParameter("licType");
        //证件状况，0:逾期，1:到期
        String licStatus = request.getParameter("licStatus");
        //审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatus = request.getParameter("licAuditStatus");
        //审核状态集合，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatuss = request.getParameter("licAuditStatuss");
        //开始消除日期，格式：xxxx-xx-xx
        String startElimDate = request.getParameter("startElimDate");
        //结束消除日期，格式：xxxx-xx-xx
        String endElimDate = request.getParameter("endElimDate");
        //开始有效日期，格式：xxxx-xx-xx
        String startValidDate = request.getParameter("startValidDate");
        //结束有效日期，格式：xxxx-xx-xx
        String endValidDate = request.getParameter("endValidDate");
        //证件号码
        String licNo = request.getParameter("licNo");
        //管理部门
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        //证件主体
        String fullName = request.getParameter("fullName");
        //学制
        String schType = request.getParameter("schType");
        //学校性质
        String schProp = request.getParameter("schProp");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", schName = " + schName + ", trigWarnUnit = " + trigWarnUnit + ", licType = " + licType + ", licStatus = " + licStatus + ", licAuditStatus = " + licAuditStatus + ", startElimDate = " + startElimDate + ", endElimDate = " + endElimDate + ", startValidDate = " + startValidDate + ", endValidDate = " + endValidDate + ", licNo = " + licNo);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出证照预警全部证件详情列表应用模型函数
		if(isAuth)
			ewaldDto = ewaldAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, 
					province, schName, trigWarnUnit, licType, licStatus, licAuditStatus,licAuditStatuss, startElimDate, 
					endElimDate, startValidDate, endValidDate, licNo, 
					departmentId,departmentIds,fullName,schType,schProp,
					db1Service, db2Service, saasService,dbHiveWarnService);
        else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ewaldDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ewaldDto);
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
     * 3.3.8 - 证照预警学校证件列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/warnSchLics", method = RequestMethod.GET)
    public String v1_warnSchLics(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WarnSchLicsDTO wslDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        if (pageSize == null) {
            pageSize = "20";
        }
        
        String distNames = request.getParameter("distNames");
        String departmentIds = request.getParameter("departmentIds");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //证照预警学校证件列表应用模型函数
		if(isAuth)
			wslDto = wslAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province,distNames,departmentIds,schSelMode, page, pageSize, db1Service, db2Service,dbHiveWarnService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (wslDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(wslDto);
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
     * 3.3.9 - 导出证照预警学校证件列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWarnSchLics", method = RequestMethod.GET)
    public String v1_expWarnSchLics(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpWarnSchLicsDTO ewslDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        
        String distNames = request.getParameter("distNames");
        String departmentIds = request.getParameter("departmentIds");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出证照预警学校证件列表应用模型函数
		if(isAuth)
			ewslDto = ewslAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province,distNames,departmentIds,schSelMode,
					db1Service, db2Service, saasService,dbHiveWarnService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (ewslDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ewslDto);
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
     * 3.3.10 - 证照预警学校证件详情列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/warnSchLicDets", method = RequestMethod.GET)
    public String v1_warnSchLicDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WarnSchLicDetsDTO wsldDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //证件类型，0:食品经营许可证，1:营业执照，2:健康证，3:餐饮服务许可证，4:A1证，5:A2证，6:B证，7:C证
        String licType = request.getParameter("licType");
        //证件状况，0:逾期，1:到期
        String licStatus = request.getParameter("licStatus");
        //审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatus = request.getParameter("licAuditStatus");
        //审核状态集合，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatuss = request.getParameter("licAuditStatuss");
        //开始消除日期，格式：xxxx-xx-xx
        String startElimDate = request.getParameter("startElimDate");
        //结束消除日期，格式：xxxx-xx-xx
        String endElimDate = request.getParameter("endElimDate");
        //开始有效日期，格式：xxxx-xx-xx
        String startValidDate = request.getParameter("startValidDate");
        //结束有效日期，格式：xxxx-xx-xx
        String endValidDate = request.getParameter("endValidDate");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //证件号码
        String licNo = request.getParameter("licNo");
        
        //管理部门
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        //证件主体
        String fullName = request.getParameter("fullName");
        
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
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", schName = " + schName + ", schType = " + schType + ", licType = " + licType + ", licStatus = " + licStatus + ", licAuditStatus = " + licAuditStatus + ", startElimDate = " + startElimDate + ", endElimDate = " + endElimDate + ", startValidDate = " + startValidDate + ", endValidDate = " + endValidDate + ", schProp = " + schProp + ", licNo = " + licNo + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //证照预警学校证件详情列表应用模型函数
		if(isAuth)
			wsldDto = wsldAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province, schName, schType, 
					licType, licStatus, licAuditStatus,licAuditStatuss, startElimDate, endElimDate, startValidDate, endValidDate, schProp, 
					licNo, 
					departmentId,departmentIds,fullName,
					page, pageSize, db1Service, db2Service, saasService,dbHiveWarnService);
        else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (wsldDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(wsldDto);
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
     * 3.3.11 - 导出证照预警学校证件详情列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWarnSchLicDets", method = RequestMethod.GET)
    public String v1_expWarnSchLicDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpWarnSchLicDetsDTO ewsldDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        //学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
        String schType = request.getParameter("schType");
        //证件类型，0:食品经营许可证，1:营业执照，2:健康证，3:餐饮服务许可证，4:A1证，5:A2证，6:B证，7:C证
        String licType = request.getParameter("licType");
        //证件状况，0:逾期，1:到期
        String licStatus = request.getParameter("licStatus");
        //审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatus = request.getParameter("licAuditStatus");
        //审核状态集合，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatuss = request.getParameter("licAuditStatuss");
        //开始消除日期，格式：xxxx-xx-xx
        String startElimDate = request.getParameter("startElimDate");
        //结束消除日期，格式：xxxx-xx-xx
        String endElimDate = request.getParameter("endElimDate");
        //开始有效日期，格式：xxxx-xx-xx
        String startValidDate = request.getParameter("startValidDate");
        //结束有效日期，格式：xxxx-xx-xx
        String endValidDate = request.getParameter("endValidDate");
        //学校性质，0:公办，1:民办，2:其他
        String schProp = request.getParameter("schProp");
        //证件号码
        String licNo = request.getParameter("licNo");
        //管理部门
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        //证件主体
        String fullName = request.getParameter("fullName");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", schName = " + schName + ", schType = " + schType + ", licType = " + licType + ", licStatus = " + licStatus + ", licAuditStatus = " + licAuditStatus + ", startElimDate = " + startElimDate + ", endElimDate = " + endElimDate + ", startValidDate = " + startValidDate + ", endValidDate = " + endValidDate + ", schProp = " + schProp + ", licNo = " + licNo);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出证照预警学校证件详情列表应用模型函数
		if(isAuth)
			ewsldDto = ewsldAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province, 
					schName, schType, licType, licStatus, licAuditStatus,licAuditStatuss, startElimDate, endElimDate, 
					startValidDate, endValidDate, schProp, licNo, 
					departmentId,departmentIds,fullName,
					db1Service, db2Service, saasService,dbHiveWarnService);
        else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (ewsldDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ewsldDto);
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
     * 3.3.12 - 证照预警团餐公司证件列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/warnRmcLics", method = RequestMethod.GET)
    public String v1_warnRmcLics(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WarnRmcLicsDTO wrlDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        if (pageSize == null) {
            pageSize = "20";
        }
        String distNames = request.getParameter("distNames");
        String departmentIds = request.getParameter("departmentIds");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //证照预警团餐公司证件列表应用模型函数
		if(isAuth)
			wrlDto = wrlAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province,distNames,departmentIds,schSelMode, page, pageSize, db1Service, db2Service, saasService,dbHiveWarnService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (wrlDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(wrlDto);
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
     * 3.3.13 - 导出证照预警团餐公司证件列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWarnRmcLics", method = RequestMethod.GET)
    public String v1_expWarnRmcLics(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpWarnRmcLicsDTO ewrlDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        String distNames = request.getParameter("distNames");
        String departmentIds = request.getParameter("departmentIds");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出证照预警团餐公司证件列表应用模型函数
		if(isAuth)
			ewrlDto = ewrlAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province,distNames,departmentIds,schSelMode, db1Service, db2Service, saasService,dbHiveWarnService);
		else
			logger.info("授权码：" + token + "，验证失败！");          
        //设置响应数据
        if (ewrlDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ewrlDto);
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
     * 3.3.14 - 证照预警团餐公司证件详情列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/warnRmcLicDets", method = RequestMethod.GET)
    public String v1_warnRmcLicDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WarnRmcLicDetsDTO wrldDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //证件类型，0:食品经营许可证，1:营业执照，2:健康证，3:餐饮服务许可证，4:A1证，5:A2证，6:B证，7:C证
        String licType = request.getParameter("licType");
        //证件状况，0:逾期，1:到期
        String licStatus = request.getParameter("licStatus");
        //审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatus = request.getParameter("licAuditStatus");
        //审核状态集合，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatuss = request.getParameter("licAuditStatuss");
        //开始消除日期，格式：xxxx-xx-xx
        String startElimDate = request.getParameter("startElimDate");
        //结束消除日期，格式：xxxx-xx-xx
        String endElimDate = request.getParameter("endElimDate");
        //开始有效日期，格式：xxxx-xx-xx
        String startValidDate = request.getParameter("startValidDate");
        //结束有效日期，格式：xxxx-xx-xx
        String endValidDate = request.getParameter("endValidDate");
        //证件号码
        String licNo = request.getParameter("licNo");
        
        //管理部门
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        //证件主体
        String fullName = request.getParameter("fullName");
        //学制
        String schType = request.getParameter("schType");
        //学校性质
        String schProp = request.getParameter("schProp");
        
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
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", rmcName = " + rmcName + ", licType = " + licType + ", licStatus = " + licStatus + ", licAuditStatus = " + licAuditStatus + ", startElimDate = " + startElimDate + ", endElimDate = " + endElimDate + ", startValidDate = " + startValidDate + ", endValidDate = " + endValidDate + ", licNo = " + licNo + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //证照预警团餐公司证件详情列表应用模型函数
		if(isAuth)
			wrldDto = wrldAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province, rmcName, 
					licType, licStatus, licAuditStatus,licAuditStatuss, startElimDate, endElimDate, startValidDate, endValidDate, 
					licNo, 
					departmentId,departmentIds,fullName,schType,schProp,
					page, pageSize, db1Service, db2Service, saasService,dbHiveWarnService);
        else
			logger.info("授权码：" + token + "，验证失败！");  
        //设置响应数据
        if (wrldDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(wrldDto);
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
     * 3.3.15 - 导出证照预警团餐公司证件详情列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWarnRmcLicDets", method = RequestMethod.GET)
    public String v1_expWarnRmcLicDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpWarnRmcLicDetsDTO ewrldDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //证件类型，0:食品经营许可证，1:营业执照，2:健康证，3:餐饮服务许可证，4:A1证，5:A2证，6:B证，7:C证
        String licType = request.getParameter("licType");
        //证件状况，0:逾期，1:到期
        String licStatus = request.getParameter("licStatus");
        //审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatus = request.getParameter("licAuditStatus");
        //审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatuss = request.getParameter("licAuditStatuss");
        //开始消除日期，格式：xxxx-xx-xx
        String startElimDate = request.getParameter("startElimDate");
        //结束消除日期，格式：xxxx-xx-xx
        String endElimDate = request.getParameter("endElimDate");
        //开始有效日期，格式：xxxx-xx-xx
        String startValidDate = request.getParameter("startValidDate");
        //结束有效日期，格式：xxxx-xx-xx
        String endValidDate = request.getParameter("endValidDate");
        //证件号码
        String licNo = request.getParameter("licNo");
        
        //管理部门
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        //证件主体
        String fullName = request.getParameter("fullName");
        //学制
        String schType = request.getParameter("schType");
        //学校性质
        String schProp = request.getParameter("schProp");
        
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", rmcName = " + rmcName + ", licType = " + licType + ", licStatus = " + licStatus + ", licAuditStatus = " + licAuditStatus + ", startElimDate = " + startElimDate + ", endElimDate = " + endElimDate + ", startValidDate = " + startValidDate + ", endValidDate = " + endValidDate + ", licNo = " + licNo);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出证照预警团餐公司证件详情列表应用模型函数
		if(isAuth)
			ewrldDto = ewrldAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province, rmcName, licType, 
					licStatus, licAuditStatus,licAuditStatuss, startElimDate, endElimDate, startValidDate, endValidDate, licNo,
					departmentId,departmentIds,fullName,schType,schProp,
					db1Service, db2Service, saasService,dbHiveWarnService);
        else
			logger.info("授权码：" + token + "，验证失败！");  
        //设置响应数据
        if (ewrldDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ewrldDto);
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
     * 3.3.16 - 证照预警人员证件列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/warnStaffLics", method = RequestMethod.GET)
    public String v1_warnStaffLics(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WarnStaffLicsDTO wstlDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        if (pageSize == null) {
            pageSize = "20";
        }
        String distNames = request.getParameter("distNames");
        String departmentIds = request.getParameter("departmentIds");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //证照预警人员证件列表应用模型函数
		if(isAuth)
			wstlDto = wstlAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province,distNames,departmentIds,schSelMode, page, pageSize, db1Service, db2Service, saasService,dbHiveWarnService);
        else
			logger.info("授权码：" + token + "，验证失败！");  
        //设置响应数据
        if (wstlDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(wstlDto);
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
     * 3.3.17 - 导出证照预警人员证件列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWarnStaffLics", method = RequestMethod.GET)
    public String v1_expWarnStaffLics(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpWarnStaffLicsDTO ewslDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        String distNames = request.getParameter("distNames");
        String departmentIds = request.getParameter("departmentIds");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出证照预警人员证件列表应用模型函数
		if(isAuth)
			ewslDto = ewstlAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province,distNames,departmentIds,schSelMode, db1Service, db2Service, saasService,dbHiveWarnService);
        else
			logger.info("授权码：" + token + "，验证失败！");  
        //设置响应数据
        if (ewslDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ewslDto);
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
     * 3.3.18 - 证照预警人员证件详情列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/warnStaffLicDets", method = RequestMethod.GET)
    public String v1_warnStaffLicDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        WarnStaffLicDetsDTO wstldDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //证件类型，0:食品经营许可证，1:营业执照，2:健康证，3:餐饮服务许可证，4:A1证，5:A2证，6:B证，7:C证
        String licType = request.getParameter("licType");
        //证件状况，0:逾期，1:到期
        String licStatus = request.getParameter("licStatus");
        //审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatus = request.getParameter("licAuditStatus");
        //审核状态集合，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatuss = request.getParameter("licAuditStatuss");
        //姓名
        String fullName = request.getParameter("fullName");
        //开始消除日期，格式：xxxx-xx-xx
        String startElimDate = request.getParameter("startElimDate");
        //结束消除日期，格式：xxxx-xx-xx
        String endElimDate = request.getParameter("endElimDate");
        //开始有效日期，格式：xxxx-xx-xx
        String startValidDate = request.getParameter("startValidDate");
        //结束有效日期，格式：xxxx-xx-xx
        String endValidDate = request.getParameter("endValidDate");
        //证件号码
        String licNo = request.getParameter("licNo");
        //学校名称
        String schName = request.getParameter("schName");
        
        //管理部门
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        //学制
        String schType = request.getParameter("schType");
        //学校性质
        String schProp = request.getParameter("schProp");
        
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
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", rmcName = " + rmcName + ", licType = " + licType + ", licStatus = " + licStatus + ", licAuditStatus = " + licAuditStatus + ", fullName = " + fullName + ", startElimDate = " + startElimDate + ", endElimDate = " + endElimDate + ", startValidDate = " + startValidDate + ", endValidDate = " + endValidDate + ", licNo = " + licNo + ", schName = " + schName + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //证照预警人员证件详情列表应用模型函数
		if(isAuth)
			wstldDto = wstldAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province, rmcName, 
					licType, licStatus, licAuditStatus,licAuditStatuss, fullName, startElimDate, endElimDate, startValidDate, endValidDate, 
					licNo, schName,
					departmentId,departmentIds,schType,schProp,
					page, pageSize, db1Service, db2Service, saasService,dbHiveWarnService);
        else
			logger.info("授权码：" + token + "，验证失败！");  
        //设置响应数据
        if (wstldDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(wstldDto);
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
     * 3.3.19 - 导出证照预警人员证件详情列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWarnStaffLicDets", method = RequestMethod.GET)
    public String v1_expWarnStaffLicDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ExpWarnStaffLicDetsDTO ewstldDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        //团餐公司名称
        String rmcName = request.getParameter("rmcName");
        //证件类型，0:食品经营许可证，1:营业执照，2:健康证，3:餐饮服务许可证，4:A1证，5:A2证，6:B证，7:C证
        String licType = request.getParameter("licType");
        //证件状况，0:逾期，1:到期
        String licStatus = request.getParameter("licStatus");
        //审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatus = request.getParameter("licAuditStatus");
        //审核状态集合，0:未处理，1:审核中，2:已消除，3:已驳回
        String licAuditStatuss = request.getParameter("licAuditStatuss");
        //姓名
        String fullName = request.getParameter("fullName");
        //开始消除日期，格式：xxxx-xx-xx
        String startElimDate = request.getParameter("startElimDate");
        //结束消除日期，格式：xxxx-xx-xx
        String endElimDate = request.getParameter("endElimDate");
        //开始有效日期，格式：xxxx-xx-xx
        String startValidDate = request.getParameter("startValidDate");
        //结束有效日期，格式：xxxx-xx-xx
        String endValidDate = request.getParameter("endValidDate");
        //证件号码
        String licNo = request.getParameter("licNo");
        //学校名称
        String schName = request.getParameter("schName");
        
        //管理部门
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        //学制
        String schType = request.getParameter("schType");
        //学校性质
        String schProp = request.getParameter("schProp");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", rmcName = " + rmcName + ", licType = " + licType + ", licStatus = " + licStatus + ", licAuditStatus = " + licAuditStatus + ", fullName = " + fullName + ", startElimDate = " + startElimDate + ", endElimDate = " + endElimDate + ", startValidDate = " + startValidDate + ", endValidDate = " + endValidDate + ", licNo = " + licNo + ", schName = " + schName);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //导出证照预警人员证件详情列表应用模型函数
		if(isAuth)
			ewstldDto = ewstldAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province, rmcName, 
					licType, licStatus, licAuditStatus,licAuditStatuss, fullName, startElimDate, endElimDate, startValidDate, endValidDate, 
					licNo, schName, 
					departmentId,departmentIds,schType,schProp,
					db1Service, db2Service, saasService,dbHiveWarnService);
        else
			logger.info("授权码：" + token + "，验证失败！");  
        //设置响应数据
        if (ewstldDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ewstldDto);
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
     * 过保预警汇总
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/warnMaterialOverPro", method = RequestMethod.GET)
    public WarnMaterialOverProDTO v1_warnMaterialOverPro(HttpServletRequest request) {
        //初始化响应数据
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        if (pageSize == null) {
            pageSize = "20";
        }
        String departmentIds = request.getParameter("departmentIds");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate 
        		+ ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        return warnMaterialOverProAppMod.appModFunc(token, startWarnDate, endWarnDate, null, prefCity, province, departmentIds,
					page, pageSize, db1Service, db2Service, dbHiveWarnService);

    }
    
    /**
     * 导出过保预警汇总
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWarnMaterialOverPro", method = RequestMethod.GET)
    public ExpWarnMaterialOverProDTO v1_expWarnMaterialOverPro(HttpServletRequest request) {
        //初始化响应数据
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        if (pageSize == null) {
            pageSize = "20";
        }
        String departmentIds = request.getParameter("departmentIds");
        logger.info("输入参数：" + "token = " + token + ", startWarnDate = " + startWarnDate + ", endWarnDate = " + endWarnDate 
        		+ ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        return expWarnMaterialOverProAppMod.appModFunc(token, startWarnDate, endWarnDate,
        		null, prefCity, province, departmentIds, db1Service, db2Service, 
        		saasService, dbHiveWarnService);

    }
    
    /**
     * 过保预警详情
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/warnMaterialOverProDets", method = RequestMethod.GET)
    public WarnMaterialOverProDetsDTO v1_warnMaterialOverProDets(HttpServletRequest request) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        //证件状况，0:警示中，1:已消除
        String status = request.getParameter("status");
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        String trigWarnUnit = request.getParameter("trigWarnUnit");
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
        //证照预警学校证件详情列表应用模型函数
		return  warnMaterialOverProDetsAppMod.appModFunc(token, startWarnDate, endWarnDate, distName, prefCity, province,departmentId,departmentIds, schName, 
				status,trigWarnUnit,page, pageSize, db1Service, db2Service, saasService,dbHiveWarnService);
    }
    
    /**
     * 导出过保预警详情
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/expWarnMaterialOverProDets", method = RequestMethod.GET)
    public ExpWarnMaterialOverProDetsDTO v1_expWarnMaterialOverProDets(HttpServletRequest request) {
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始预警日期，格式：xxxx-xx-xx
        String startWarnDate = request.getParameter("startSubDate");
        //结束预警日期，格式：xxxx-xx-xx
        String endWarnDate = request.getParameter("endSubDate");
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
        //证件状况，0:警示中，1:已消除
        String status = request.getParameter("status");
        String departmentId = request.getParameter("departmentId");
        String departmentIds = request.getParameter("departmentIds");
        String trigWarnUnit = request.getParameter("trigWarnUnit");
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
        //证照预警学校证件详情列表应用模型函数
		return  expWarnMaterialOverProDetsAppMod.appModFunc(token, startWarnDate, endWarnDate, distName,
				prefCity, province, schName, trigWarnUnit, status, departmentId, departmentIds,
				db1Service, db2Service, saasService, dbHiveWarnService);
    }
}
