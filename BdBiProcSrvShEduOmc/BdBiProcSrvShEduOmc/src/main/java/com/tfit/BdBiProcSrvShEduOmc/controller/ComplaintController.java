package com.tfit.BdBiProcSrvShEduOmc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.cp.ContractorCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.cp.CrDetAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.cp.CrDetListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.cp.CrListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.cp.CrOperationAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.cp.CrProcStatusCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.cp.CrPublishAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.ContractorCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrDetListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrOperationDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrProcStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.cp.CrPublishDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Descritpion：业务数据-投诉举报
 * @author: tianfang_infotech
 * @date: 2019/1/2 16:47
 */
@RestController
@RequestMapping(value = "/biOptAnl")
public class ComplaintController {
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
    
    //承办人编码列表应用模型
    ContractorCodesAppMod ctsAppMod = new ContractorCodesAppMod();
    
    //投诉举报处理状态编码列表应用模型
    CrProcStatusCodesAppMod cpsAppMod = new CrProcStatusCodesAppMod();

    /**
     * 投诉举报列表应用模型
     */
    CrListAppMod crlAppMod = new CrListAppMod();

    /**
     * 投诉举报详情列表应用模型
     */
    CrDetListAppMod cdlAppMod = new CrDetListAppMod();

    /**
     * 投诉举报详情应用模型
     */
    CrDetAppMod crdAppMod = new CrDetAppMod();
    
    //投诉举报发布应用模型
    CrPublishAppMod cplAppMod = new CrPublishAppMod();
    
    //投诉举报操作应用模型
    CrOperationAppMod copAppMod = new CrOperationAppMod();
    
    /**
     * 3.4.1 - 承办人编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/contractorCodes", method = RequestMethod.GET)
    public String v1_contractorCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        ContractorCodesDTO ctsDto = null;
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
        String startSubDate = request.getParameter("startSubDate");
        //结束提交日期
        String endSubDate = request.getParameter("endSubDate");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", startSubDate = " + startSubDate + ", endSubDate = " + endSubDate);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //承办人编码列表应用模型函数
		if(isAuth)
			ctsDto = ctsAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (ctsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ctsDto);
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
     * 3.4.2 - 投诉举报处理状态编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/crProcStatusCodes", method = RequestMethod.GET)
    public String v1_crProcStatusCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CrProcStatusCodesDTO cpsDto = null;
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
        String startSubDate = request.getParameter("startSubDate");
        //结束提交日期
        String endSubDate = request.getParameter("endSubDate");
        logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", startSubDate = " + startSubDate + ", endSubDate = " + endSubDate);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报列表应用模型函数
		if(isAuth)
			cpsDto = cpsAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (cpsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(cpsDto);
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
     * 3.4.1 - 投诉举报列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/crList", method = RequestMethod.GET)
    public String v1_crList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CrListDTO crlDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始提交日期
        String startSubDate = request.getParameter("startSubDate");
        //结束提交日期
        String endSubDate = request.getParameter("endSubDate");
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
        logger.info("输入参数：" + "token = " + token + ", startSubDate = " + startSubDate + ", endSubDate = " + endSubDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报列表应用模型函数
		if(isAuth)
			crlDto = crlAppMod.appModFunc(token, startSubDate, endSubDate, distName, prefCity, province, page, pageSize, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (crlDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(crlDto);
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
     * 3.4.2 - 投诉举报详情列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/crDetList", method = RequestMethod.GET)
    public String v1_crDetList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CrDetListDTO cdlDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始提交日期，格式：xxxx-xx-xx
        String subDate = request.getParameter("subDate");        
        //投诉举报主题
        String title = request.getParameter("title");        
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
        //承办人名称
        String contractor = request.getParameter("contractor");
        //处理状态，0:待处理，1:已指派，2:已办结
        String procStatus = request.getParameter("procStatus");
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
        logger.info("输入参数：" + "token = " + token + ", subDate = " + subDate + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", schName = " + schName + ", contractor = " + contractor + ", procStatus = " + procStatus + ", page = " + page + ", pageSize = " + pageSize);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报详情列表应用模型函数
		if(isAuth)
			cdlDto = cdlAppMod.appModFunc(token, subDate, title, distName, prefCity, province, schName, contractor, procStatus, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");       
        //设置响应数据
        if (cdlDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(cdlDto);
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
     * 3.4.3 - 投诉举报详情
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/crDet", method = RequestMethod.GET)
    public String v1_crDet(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CrDetDTO crdDto = null;
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
        logger.info("输入参数：" + "token = " + token + ", crId = " + crId + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//投诉举报详情列表应用模型函数
		if(isAuth)
			crdDto = crdAppMod.appModFunc(token, crId, distName, prefCity, province, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (crdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(crdDto);
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
     * 3.4.4 - 投诉举报发布
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/crPublish", method = RequestMethod.POST)
    public String v1_crPublish(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CrPublishDTO cplDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");  		
  		//Body传输内容，格式为application/json
  		String strBodyCont = AppModConfig.GetBodyJsonReq(request, false);
  		logger.info("输入参数：" + "token = " + token + ", strBodyCont = " + strBodyCont);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //通知发布应用模型函数
		if(isAuth)
			cplDto = cplAppMod.appModFunc(token, strBodyCont, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (cplDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(cplDto);
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
     * 3.4.7 - 投诉举报操作
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/crOperation", method = RequestMethod.POST)
    public String v1_crOperation(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        CrOperationDTO copDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");  		
  		//Body传输内容，格式为application/json
  		String strBodyCont = AppModConfig.GetBodyJsonReq(request, false);
  		logger.info("输入参数：" + "token = " + token + ", strBodyCont = " + strBodyCont);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //投诉举报操作应用模型函数
		if(isAuth)
			copDto = copAppMod.appModFunc(token, strBodyCont, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (copDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(copDto);
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
