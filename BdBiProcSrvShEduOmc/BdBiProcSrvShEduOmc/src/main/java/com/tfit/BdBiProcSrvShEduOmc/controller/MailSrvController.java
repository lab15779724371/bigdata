package com.tfit.BdBiProcSrvShEduOmc.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ms.MailSrvDetAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ms.MailSrvPortCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ms.SaveMailSrvInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ms.SendTestMailAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ms.MailSrvDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ms.MailSrvPortCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ms.SaveMailSrvInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Custom1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;

@RestController
@RequestMapping(value = "/biOptAnl")
public class MailSrvController {
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
	
	//邮件服务器端口编码列表应用模型
	MailSrvPortCodesAppMod mspcAppMod = new MailSrvPortCodesAppMod();
	
	//邮件服务器详情应用模型
	MailSrvDetAppMod msdAppMod = new MailSrvDetAppMod();
	
	//保存邮件服务器设置应用模型
	SaveMailSrvInfoAppMod smsiAppMod = new SaveMailSrvInfoAppMod();
	
	//发送测试邮件应用模型
	SendTestMailAppMod stmAppMod = new SendTestMailAppMod();
	
  	//邮件服务器端口编码列表
  	@RequestMapping(value = "/v1/mailSrvPortCodes",method = RequestMethod.GET)
  	public String v1_mailSrvPortCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		MailSrvPortCodesDTO mspcDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//区域名称
  		String distName = request.getParameter("distName");
  		if(distName == null)
  			distName = request.getParameter("distname");
  		//地级城市
  		String prefCity = request.getParameter("prefCity");
  		if(prefCity == null)
  			prefCity = request.getParameter("prefcity");
  		//省或直辖市
  		String province = request.getParameter("province");
  		logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//邮件服务器端口编码列表应用模型函数
		if(isAuth)
			mspcDto = mspcAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(mspcDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(mspcDto);
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
  	
  	//邮件服务器详情
  	@RequestMapping(value = "/v1/mailSrvDet",method = RequestMethod.GET)
  	public String v1_mailSrvDet(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		MailSrvDetDTO msdDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");		
  		logger.info("输入参数：" + "token = " + token);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//邮件服务器详情应用模型函数
		if(isAuth)
			msdDto = msdAppMod.appModFunc(token, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(msdDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(msdDto);
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
  	
  	//保存邮件服务器设置
  	@RequestMapping(value = "/v1/saveMailSrvInfo",method = RequestMethod.POST)
  	public String v1_saveMailSrvInfo(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		SaveMailSrvInfoDTO asuiDto = null;
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
  		//保存个人资料应用模型函数
		if(isAuth)
			asuiDto = smsiAppMod.appModFunc(token, strBodyCont, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(asuiDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(asuiDto);
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
  	
  	//发送测试邮件
  	@RequestMapping(value = "/v1/sendTestMail",method = RequestMethod.POST)
  	public String v1_sendTestMail(HttpServletRequest request)
  	{
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
  		//保存个人资料应用模型函数
		if(isAuth)
			normResp = stmAppMod.appModFunc(token, strBodyCont, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(normResp != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(normResp);
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
