package com.tfit.BdBiProcSrvShEduOmc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.AnnReadCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.AnnTypeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.RbAnnReadAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.RbAnnounceAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.RbBulletinDetAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.RbBulletinsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.RbReceiversAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.RbUlAttachmentAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.SignInBulletinAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.AnnReadCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.AnnTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbAnnounceDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbBulletinDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbBulletinsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbReceiversDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbUlAttachmentDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.SignInBulletinDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

/**
 * @Descritpion：运营管理-发布通知
 * @author: tianfang_infotech
 * @date: 2019/1/2 16:47
 */
@RestController
@RequestMapping(value = "/biOptAnl")
public class PublishNoticeController {
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
    
    //通知阅读编码列表应用模型
    AnnReadCodesAppMod arcAppMod = new AnnReadCodesAppMod();
    
    //通知类型编码列表应用模型
    AnnTypeCodesAppMod atcAppMod = new AnnTypeCodesAppMod();

    /**
     * 通知列表
     */
    RbBulletinsAppMod rbbAppMod = new RbBulletinsAppMod();

    /**
     * 通知详情应用模型
     */
    RbBulletinDetAppMod rbdAppMod = new RbBulletinDetAppMod();
    
    //通知接收人列表应用模型
    RbReceiversAppMod rrsAppMod = new RbReceiversAppMod();
    
    //通知发布应用模型
    RbAnnounceAppMod racAppMod = new RbAnnounceAppMod();
    
    //签到通知应用模型
    SignInBulletinAppMod sibAppMod = new SignInBulletinAppMod();
    
    //通知阅读应用模型
    RbAnnReadAppMod rarAppMod = new RbAnnReadAppMod();
    
    //通知附件上传应用模型
    RbUlAttachmentAppMod ruaAppMod = new RbUlAttachmentAppMod();
    
    /**
     * 3.8.1 - 通知阅读编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/annReadCodes", method = RequestMethod.GET)
    public String v1_annReadCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        AnnReadCodesDTO arcDto = null;
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
        //通知阅读编码列表应用模型函数
		if(isAuth)
			arcDto = arcAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (arcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(arcDto);
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
     * 3.8.2 -	通知类型编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/annTypeCodes", method = RequestMethod.GET)
    public String v1_annTypeCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        AnnTypeCodesDTO atcDto = null;
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
        //通知类型编码列表应用模型函数
		if(isAuth)
			atcDto = atcAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (atcDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(atcDto);
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
     * 3.8.1 - 通知列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/rbBulletins", method = RequestMethod.GET)
    public String v1_rbBulletins(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        RbBulletinsDTO rbbDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //用户名
        String userName = request.getParameter("userName");
        //开始发布日期，格式：xxxx-xx-xx
        String startPubDate = request.getParameter("startSubDate");
        //结束发布日期，格式：xxxx-xx-xx
        String endPubDate = request.getParameter("endSubDate");
        //标题，模糊查询
        String title = request.getParameter("title");
        //阅读标识，0:未读，1:已读
        String readFlag = request.getParameter("readFlag");        
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
        logger.info("输入参数：" + "token = " + token + ", userName = " + userName + ", startPubDate = " + startPubDate + ", endPubDate = " + endPubDate + ", title = " + title + ", readFlag = " + readFlag + ", page = " + page + ", pageSize = " + pageSize);
        //验证授权        
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //通知列表应用模型函数
		if(isAuth)
			rbbDto = rbbAppMod.appModFunc(token, userName, startPubDate, endPubDate, title, readFlag, page, pageSize, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (rbbDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(rbbDto);
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
     * 3.8.2 - 通知详情
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/rbBulletinDet", method = RequestMethod.GET)
    public String v1_rbBulletinDet(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        RbBulletinDetDTO rbdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //通告ID
        String bulletinId = request.getParameter("bulletinId");
        //用户名，非空表示发布通知详情
        String userName = request.getParameter("userName");
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
        logger.info("输入参数：" + "token = " + token + ", bulletinId = " + bulletinId + ", userName = " + userName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //通知详情应用模型函数
		if(isAuth)
			rbdDto = rbdAppMod.appModFunc(token, bulletinId, userName, distName, prefCity, province, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (rbdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(rbdDto);
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
     * 3.8.4 - 通知接收人列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/rbReceivers", method = RequestMethod.GET)
    public String v1_rbReceivers(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        RbReceiversDTO rrsDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //用户名称
        String userName = request.getParameter("userName");
        //角色类型，1:监管部门，2:学校，默认为1
        String roleType = request.getParameter("roleType");
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
        logger.info("输入参数：" + "token = " + token + ", userName = " + userName + ", roleType = " + roleType + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
        //验证授权        
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //通知详情应用模型函数
		if(isAuth)
			rrsDto = rrsAppMod.appModFunc(token, userName, roleType, distName, prefCity, province, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (rrsDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(rrsDto);
                strResp = strResp.replaceAll("\"list\"", "\"children\"");
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
     * 3.8.2 - 通知发布
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/rbAnnounce", method = RequestMethod.POST)
    public String v1_rbAnnounce(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        RbAnnounceDTO racDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");  		
  		//Body传输内容，格式为application/json
  		String strBodyCont = GetBodyJsonReq(request, false);
  		logger.info("输入参数：" + "token = " + token + ", strBodyCont = " + strBodyCont);
  		//验证授权        
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //通知发布应用模型函数
		if(isAuth)
			racDto = racAppMod.appModFunc(token, strBodyCont, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (racDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(racDto);
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
     * 3.8.4 - 签到通知
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/signInBulletin", method = RequestMethod.POST)
    public String v1_signInBulletin(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        SignInBulletinDTO sibDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");  		
  		//Body传输内容，格式为application/json
  		String strBodyCont = GetBodyJsonReq(request, false);
  		logger.info("输入参数：" + "token = " + token + ", strBodyCont = " + strBodyCont);
  		//验证授权        
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //签到通知应用模型函数
		if(isAuth)
			sibDto = sibAppMod.appModFunc(token, strBodyCont, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (sibDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(sibDto);
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
     * 3.8.2 - 通知附件上传
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/rbUlAttachment", method = RequestMethod.POST)
    public String v1_rbUlAttachment(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        RbUlAttachmentDTO ruaDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//文件名
  		String fileName = request.getParameter("fileName");  		
  		logger.info("输入参数：" + "token = " + token + ", fileName = " + fileName);
  		//验证授权        
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //通知详情应用模型函数
		if(isAuth)
			ruaDto = ruaAppMod.appModFunc(fileName, request);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (ruaDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ruaDto);
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
     * 3.8.6 - 通知阅读
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/rbAnnRead", method = RequestMethod.POST)
    public String v1_rbAnnRead(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        IOTHttpRspVO normResp = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");  		
  		//Body传输内容，格式为application/json
  		String strBodyCont = GetBodyJsonReq(request, false);
  		logger.info("输入参数：" + "token = " + token + ", strBodyCont = " + strBodyCont);
  		//验证授权        
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //通知详情应用模型函数
		if(isAuth)
			normResp = rarAppMod.appModFunc(token, strBodyCont, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (normResp != null) {
            try {
                strResp = objectMapper.writeValueAsString(normResp);
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
    
  	//获取body数据
  	private String GetBodyJsonReq(HttpServletRequest request, boolean isUtf8Code) {
  		BufferedReader br;
  		StringBuilder sb = null;
  		String reqBody = null;
  		try {
  			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
  			String line = null;
  			sb = new StringBuilder();
  			while ((line = br.readLine()) != null) {
  				sb.append(line);
  			}
  			if(isUtf8Code) {
  				reqBody = URLDecoder.decode(sb.toString(), "UTF-8");
  			}
  			else
  				reqBody = sb.toString();
  			logger.info("接收Body内容：" + reqBody);
  			return reqBody;
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			return null;
  		}
  	}
}
