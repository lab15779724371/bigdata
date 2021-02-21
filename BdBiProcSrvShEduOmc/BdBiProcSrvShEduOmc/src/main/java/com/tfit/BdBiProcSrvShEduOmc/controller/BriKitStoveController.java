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
import com.tfit.BdBiProcSrvShEduOmc.appmod.bks.BriKitStovePbVidsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bks.BriKitStoveRtVidsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bks.BriKitStoveSchsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bks.EcBriKitStoveAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.BriKitStovePbVidsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.BriKitStoveRtVidsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.BriKitStoveSchsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bks.EcBriKitStoveDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Custom1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;

//明厨亮灶BI应用服务
@RestController
@RequestMapping(value = "/biOptAnl")
public class BriKitStoveController {
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
	
	//Redis服务
	@Autowired
	RedisService redisService;
			
	@Autowired
	ObjectMapper objectMapper;
	
    /**
     * 应急指挥明厨亮灶应用模型
     */
    EcBriKitStoveAppMod ebksAppMod = new EcBriKitStoveAppMod();
    
    //3.7.1 - 明厨亮灶实时视频列表应用模型
    BriKitStoveRtVidsAppMod bksrvAppMod = new BriKitStoveRtVidsAppMod();
    
    //3.7.2 - 明厨亮灶视频回放列表应用模型
    BriKitStovePbVidsAppMod bkspvAppMod = new BriKitStovePbVidsAppMod();
    
    //3.7.3 - 明厨亮灶学校列表应用模型
    BriKitStoveSchsAppMod bkssAppMod = new BriKitStoveSchsAppMod();

    /**
     * 3.7.1 - 应急指挥明厨亮灶
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/ecBriKitStove", method = RequestMethod.GET)
    public String v1_ecBriKitStove(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        EcBriKitStoveDTO ebksDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //项目点名称
        String ppName = request.getParameter("ppName");
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
        //应急指挥明厨亮灶应用模型函数
		if(isAuth)
			 ebksDto = ebksAppMod.appModFunc(ppName, distName, prefCity, province, db1Service);
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
     * 3.7.1 - 明厨亮灶实时视频列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/briKitStoveRtVids", method = RequestMethod.GET)
    public String v1_briKitStoveRtVids(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        BriKitStoveRtVidsDTO bksrvDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //项目点名称
        String ppName = request.getParameter("ppName");
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
            pageSize = "8";
        }
        logger.info("输入参数：" + "token = " + token + ", ppName = " + ppName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //应急指挥明厨亮灶应用模型函数
		if(isAuth)
			bksrvDto = bksrvAppMod.appModFunc(token, ppName, distName, prefCity, province, page, pageSize, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");       
        //设置响应数据
        if (bksrvDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(bksrvDto);
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
     * 3.7.2 - 明厨亮灶视频回放列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/briKitStovePbVids", method = RequestMethod.GET)
    public String v1_briKitStovePbVids(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        BriKitStovePbVidsDTO bkspvDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //项目点名称
        String ppName = request.getParameter("ppName");
        //开始回放时间，格式：xxxx-xx-xx xx:xx:xx
        String startSubTime = request.getParameter("startSubTime");
        //结束回放时间，格式：xxxx-xx-xx xx:xx:xx
        String endSubTime = request.getParameter("endSubTime");       
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
        logger.info("输入参数：" + "token = " + token + ", ppName = " + ppName + ", startSubTime = " + startSubTime + ", endSubTime = " + endSubTime + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //应急指挥明厨亮灶应用模型函数
		if(isAuth)
			bkspvDto = bkspvAppMod.appModFunc(ppName, startSubTime, endSubTime, distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");       
        //设置响应数据
        if (bkspvDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(bkspvDto);
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
     * 3.7.3 - 明厨亮灶学校列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/briKitStoveSchs", method = RequestMethod.GET)
    public String v1_briKitStoveSchs(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        BriKitStoveSchsDTO bkssDto = null;
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
        //应急指挥明厨亮灶应用模型函数
		if(isAuth)
			bkssDto = bkssAppMod.appModFunc(token, distName, prefCity, province, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");       
        //设置响应数据
        if (bkssDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(bkssDto);
                strResp = strResp.replaceAll("\"list\"", "\"children\"");
                //logger.info(strResp);
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
