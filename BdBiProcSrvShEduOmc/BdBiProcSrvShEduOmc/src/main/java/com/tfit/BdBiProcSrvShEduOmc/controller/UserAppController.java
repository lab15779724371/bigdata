package com.tfit.BdBiProcSrvShEduOmc.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.annotation.CheckUserToken;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AccountTypeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AddUserInterfaceColumsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmAccountDelAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmAccountDetAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmAccountSecAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmAddAccountAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmAddRoleAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmAdminUserCheckAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmDataPermAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmDelRoleAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmEditAccountAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmEditRoleAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmL1MenuPermAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmL2MenuPermAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmL3MenuPermAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmMenuPermAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmPwCheckAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmResetPwAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmRoleCheckAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmRoleDetAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmRoleManageAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmSaveUserInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmUserCheckAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmUserEnableAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmUserInfoAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.AmUserManageAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.ExpAmRoleManageAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.ExpAmUserManageAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.GetUserInterfaceColumsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.GetUserTokenAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.LoginAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.RoleNameCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.RoleTypeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.UserLableAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.UserOrgNameCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.UserSrcCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.user.UserStatusCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.common.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AccountTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AddUserInterfaceColums;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AddUserInterfaceColumsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmAccountDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmAccountSecDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmDataPermDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmL1MenuPermDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmL2MenuPermDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmL3MenuPermDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmMenuPermDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmRoleDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmRoleManageDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmSaveUserInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmUserInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.AmUserManageDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.ExpAmRoleManageDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.ExpAmUserManageDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.RoleNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.RoleTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserOrgNameCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSimpleObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserSrcCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.user.UserStatusCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdAddressLableObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edubd.EduBdService;

//用户管理系统BI应用服务
@RestController
@RequestMapping(value = "/biUser")
public class UserAppController {
	private static final Logger logger = LogManager.getLogger(UserAppController.class.getName());
	
	public static UserAppController userAppController;
	
	//mysql数据库服务
	@Autowired
	SaasService saasService;
	
	//mysql数据库服务1
	@Autowired
	Db1Service db1Service;
		
	//mysql数据库服务2
	@Autowired
	Db2Service db2Service;
	
	//mysql数据库服务2
	@Autowired
	EduBdService eduBdService;
		
	//Redis服务
	@Autowired
	RedisService redisService;
		
	@Autowired
	ObjectMapper objectMapper;
	
	//用户登录
	LoginAppMod lAppMod = new LoginAppMod();
	
	//个人资料应用模型
	AmUserInfoAppMod auiAppMod = new AmUserInfoAppMod();
	
	//保存个人资料应用模型
	AmSaveUserInfoAppMod asuiAppMod = new AmSaveUserInfoAppMod();
	
	//账号安全应用模型
	AmAccountSecAppMod aasAppMod = new AmAccountSecAppMod();
	
	//密码验证应用模型
	AmPwCheckAppMod apcAppMod = new AmPwCheckAppMod();
	
	//修改密码应用模型
	AmResetPwAppMod arpAppMod = new AmResetPwAppMod();
	
	//角色名称编码列表应用模型
	RoleNameCodesAppMod rncAppMod = new RoleNameCodesAppMod();
	
	//用户单位名称编码列表应用模型
	UserOrgNameCodesAppMod uoncAppMod = new UserOrgNameCodesAppMod();
	
	//账号类型编码列表应用模型
	AccountTypeCodesAppMod atcAppMod = new AccountTypeCodesAppMod();
	
	//账号来源编码列表应用模型
	UserSrcCodesAppMod uscAppMod = new UserSrcCodesAppMod();
	
	//账号状态编码列表应用模型
	UserStatusCodesAppMod ustcAppMod = new UserStatusCodesAppMod();
	
	//用户管理应用模型
	AmUserManageAppMod aumAppMod = new AmUserManageAppMod();
	
	//导出用户管理应用模型
	ExpAmUserManageAppMod eaumAppMod = new ExpAmUserManageAppMod();
	
	//账号详情应用模型
	AmAccountDetAppMod aadAppMod = new AmAccountDetAppMod();
	
	//数据权限应用模型
	AmDataPermAppMod adpAppMod = new AmDataPermAppMod();
	
	//一级菜单权限应用模型
	AmL1MenuPermAppMod al1mpAppMod = new AmL1MenuPermAppMod();
	
	//二级菜单权限应用模型
	AmL2MenuPermAppMod al2mpAppMod = new AmL2MenuPermAppMod();
	
	//三级菜单权限（按钮权限）应用模型
	AmL3MenuPermAppMod al3mpAppMod = new AmL3MenuPermAppMod();
	
	//用户名检测应用模型
	AmUserCheckAppMod aucAppMod = new AmUserCheckAppMod();
	
	//管理员管理员用户名检测应用模型
	AmAdminUserCheckAppMod aaucAppMod = new AmAdminUserCheckAppMod();
	
	//添加账号应用模型
	AmAddAccountAppMod aaaAppMod = new AmAddAccountAppMod();
	
	//编辑账号应用模型
	AmEditAccountAppMod aeaAppMod = new AmEditAccountAppMod();
	
	//账号启禁用应用模型
	AmUserEnableAppMod aueAppMod = new AmUserEnableAppMod();
	
	//账号删除应用模型
	AmAccountDelAppMod aadeAppMod = new AmAccountDelAppMod();
	
	//角色类型编码列表应用模型
	RoleTypeCodesAppMod rtcAppMod = new RoleTypeCodesAppMod();
	
	//角色管理应用模型
	AmRoleManageAppMod armAppMod = new AmRoleManageAppMod();
	
	//导出角色管理应用模型
	ExpAmRoleManageAppMod earmAppMod = new ExpAmRoleManageAppMod();
	
	//角色详情应用模型
	AmRoleDetAppMod ardAppMod = new AmRoleDetAppMod();
	
	//添加角色应用模型
	AmAddRoleAppMod aarAppMod = new AmAddRoleAppMod();
	
	//编辑角色应用模型
	AmEditRoleAppMod aerAppMod = new AmEditRoleAppMod();
	
	//角色名检测应用模型
	AmRoleCheckAppMod arcAppMod = new AmRoleCheckAppMod();
	
	//删除角色应用模型
	AmDelRoleAppMod adrAppMod = new AmDelRoleAppMod();
	
	//添加用户列表字段应用模型
	AddUserInterfaceColumsAppMod columsAppMod = new AddUserInterfaceColumsAppMod();
	
	//添加用户列表字段应用模型
	GetUserInterfaceColumsAppMod getColumsAppMod = new GetUserInterfaceColumsAppMod();
	
	//菜单权限应用模型
	AmMenuPermAppMod ampAppMod = new AmMenuPermAppMod();
	

	//通讯录
	UserLableAppMod userLableAppMod = new UserLableAppMod();
	
	//用户登录
  	@RequestMapping(value = "/v1/login",method = RequestMethod.POST)
  	public String v1_login(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		IOTHttpRspVO normResp = null;
  		int code = 0;
		int[] codes = new int[1];
  		//用户名称
  		String userName = request.getParameter("userName");
  		if(userName == null)
  			userName = request.getParameter("username");
  		//用户密码（SHA1算法生成字符串）
  		String password = request.getParameter("password");
  		logger.info("输入参数：" + "userName = " + userName + ", password = " + password);
  		//用户登录应用模型函数
  		normResp = lAppMod.appModFunc(userName, password, db1Service, db2Service, codes);
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
  	
	//根据用户名称获取用户是否存在，不存在给与提示，存在返回token
  	@RequestMapping(value = "/v1/getUserToken",method = RequestMethod.GET)
  	public String v1_getUserToken(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		IOTHttpRspVO normResp = null;
  		int code = 0;
		int[] codes = new int[1];
  		//用户名称
  		String userName = request.getParameter("userName");
  		if(userName == null)
  			userName = request.getParameter("username");
  		logger.info("输入参数：" + "userName = " + userName);
  		//用户登录应用模型函数
  		normResp = new  GetUserTokenAppMod().appModFunc(userName, db1Service, db2Service, codes);
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
  	
  	//个人资料
  	@RequestMapping(value = "/v1/amUserInfo",method = RequestMethod.GET)
  	public String v1_amUserInfo(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmUserInfoDTO auiDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");		
  		logger.info("输入参数：" + "token = " + token);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//个人资料应用模型函数
		if(isAuth)
			auiDto = auiAppMod.appModFunc(token, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(auiDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(auiDto);
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
  	
  	//保存个人资料
  	@RequestMapping(value = "/v1/amSaveUserInfo",method = RequestMethod.POST)
  	public String v1_amSaveUserInfo(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmSaveUserInfoDTO asuiDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//Body传输内容，格式为application/json
  		String strBodyCont = GetBodyJsonReq(request, false);
  		//logger.info("输入参数：" + "token = " + token + ", strBodyCont = " + strBodyCont);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//保存个人资料应用模型函数
		if(isAuth)
			asuiDto = asuiAppMod.appModFunc(token, strBodyCont, db2Service);
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
  	
  	//账号安全
  	@RequestMapping(value = "/v1/amAccountSec",method = RequestMethod.GET)
  	public String v1_amAccountSec(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmAccountSecDTO aasDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");		
  		logger.info("输入参数：" + "token = " + token);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//个人资料应用模型函数
		if(isAuth)
			aasDto = aasAppMod.appModFunc(token, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(aasDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(aasDto);
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
  			//logger.info("接收Body内容：" + reqBody);
  			return reqBody;
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			return null;
  		}
  	}
  	
  	//密码验证
  	@RequestMapping(value = "/v1/amPwCheck",method = RequestMethod.GET)
  	public String v1_amPwCheck(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		IOTHttpRspVO normResp = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//输入密码，经过SHA1算法生成的字符串。
  		String inPassword = request.getParameter("inPassword");	
  		logger.info("输入参数：" + "token = " + token + ", inPassword = " + inPassword);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//保存个人资料应用模型函数
		if(isAuth)
			normResp = apcAppMod.appModFunc(token, inPassword, db2Service, codes);
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
  	
  	//修改密码
  	@RequestMapping(value = "/v1/amResetPw",method = RequestMethod.POST)
  	public String v1_amResetPw(HttpServletRequest request)
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
  		//修改密码应用模型函数
		if(isAuth)
			normResp = arpAppMod.appModFunc(token, strBodyCont, db2Service, codes);
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
  	
  	//角色名称编码列表
  	@RequestMapping(value = "/v1/roleNameCodes",method = RequestMethod.GET)
  	public String v1_roleNameCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		RoleNameCodesDTO rncDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//角色类型，1:监管部门，2:学校
  		String roleType = request.getParameter("roleType");
  		logger.info("输入参数：" + "token = " + token + ", roleType = " + roleType);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//角色名称编码列表应用模型函数
		if(isAuth)
			rncDto = rncAppMod.appModFunc(roleType, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(rncDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(rncDto);
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
  	
  	//用户单位名称编码列表
  	@RequestMapping(value = "/v1/userOrgNameCodes",method = RequestMethod.GET)
  	public String v1_userOrgNameCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		UserOrgNameCodesDTO uoncDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		logger.info("输入参数：" + "token = " + token);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//用户单位名称编码列表应用模型函数
		if(isAuth)
			uoncDto = uoncAppMod.appModFunc(token, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(uoncDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(uoncDto);
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
  	
  	//账号类型编码列表
  	@RequestMapping(value = "/v1/accountTypeCodes",method = RequestMethod.GET)
  	public String v1_accountTypeCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AccountTypeCodesDTO atcDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		logger.info("输入参数：" + "token = " + token);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//用户单位名称编码列表应用模型函数
		if(isAuth)
			atcDto = atcAppMod.appModFunc(db1Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(atcDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(atcDto);
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
  	
  	//账号来源编码列表
  	@RequestMapping(value = "/v1/userSrcCodes",method = RequestMethod.GET)
  	public String v1_userSrcCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		UserSrcCodesDTO uscDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		logger.info("输入参数：" + "token = " + token);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//用户单位名称编码列表应用模型函数
		if(isAuth)
			uscDto = uscAppMod.appModFunc(db1Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(uscDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(uscDto);
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
  	
  	//账号状态编码列表
  	@RequestMapping(value = "/v1/userStatusCodes",method = RequestMethod.GET)
  	public String v1_userStatusCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		UserStatusCodesDTO uscDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		logger.info("输入参数：" + "token = " + token);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//用户单位名称编码列表应用模型函数
		if(isAuth)
			uscDto = ustcAppMod.appModFunc(db1Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(uscDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(uscDto);
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
  	
  	//用户管理
  	@RequestMapping(value = "/v1/amUserManage",method = RequestMethod.GET)
  	public String v1_amUserManage(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmUserManageDTO aumDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//用户名
  		String userName = request.getParameter("userName");
  		//姓名
  		String fullName = request.getParameter("fullName");
  		//角色
  		String roleName = request.getParameter("roleName");
  		//单位
  		String userOrg = request.getParameter("userOrg");
  		//账号类型，0:普通账号，1:管理员账号
  		String accountType = request.getParameter("accountType");
  		//用户状态，0:禁用，1:启用
  		String userStatus = request.getParameter("userStatus");
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";  		
  		logger.info("输入参数：" + "token = " + token + ", userName = " + userName + ", fullName = " + fullName + ", roleName = " + roleName + ", userOrg = " + userOrg + ", accountType = " + accountType + ", userStatus = " + userStatus + ", page = " + page + ", pageSize = " + pageSize);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//用户管理应用模型函数
		if(isAuth)
			aumDto = aumAppMod.appModFunc(token, userName, fullName, roleName, userOrg, accountType, userStatus, page, pageSize, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(aumDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(aumDto);
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
  	
  	//导出用户管理
  	@RequestMapping(value = "/v1/expAmUserManage",method = RequestMethod.GET)
  	public String v1_expAmUserManage(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		ExpAmUserManageDTO eaumDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//用户名
  		String userName = request.getParameter("userName");
  		//姓名
  		String fullName = request.getParameter("fullName");  		
  		//角色
  		String roleName = request.getParameter("roleName");
  		//单位
  		String userOrg = request.getParameter("userOrg");
  		//账号类型，0:普通账号，1:管理员账号
  		String accountType = request.getParameter("accountType");
  		//用户状态，0:禁用，1:启用
  		String userStatus = request.getParameter("userStatus");  		
  		logger.info("输入参数：" + "token = " + token + ", userName = " + userName + ", fullName = " + fullName + ", roleName = " + roleName + ", userOrg = " + userOrg + ", accountType = " + accountType + ", userStatus = " + userStatus);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//用户管理应用模型函数
		if(isAuth)
			eaumDto = eaumAppMod.appModFunc(token, userName, fullName, roleName, userOrg, accountType, userStatus,db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(eaumDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(eaumDto);
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
  	
  	//账号详情
  	@RequestMapping(value = "/v1/amAccountDet",method = RequestMethod.GET)
  	public String v1_amAccountDet(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmAccountDetDTO aadDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//用户名
  		String userName = request.getParameter("userName");  		
  		logger.info("输入参数：" + "token = " + token + ", userName = " + userName);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//账号详情应用模型函数
		if(isAuth)
			aadDto = aadAppMod.appModFunc(userName, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(aadDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(aadDto);
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
  	
  	//数据权限
  	@RequestMapping(value = "/v1/amDataPerm",method = RequestMethod.GET)
  	public String v1_amDataPerm(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmDataPermDTO adpDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//用户名
  		String userName = request.getParameter("userName");  		
  		logger.info("输入参数：" + "token = " + token + ", userName = " + userName);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//数据权限应用模型函数
		if(isAuth)
			adpDto = adpAppMod.appModFunc(token, userName, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(adpDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(adpDto);
  				strResp = strResp.replaceAll("\"list\"", "\"children\"");
  				//logger.info(strResp);
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
  	
  	//一级菜单权限
  	@RequestMapping(value = "/v1/amL1MenuPerm",method = RequestMethod.GET)
  	public String v1_amL1MenuPerm(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmL1MenuPermDTO al1mpDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//用户名
  		String userName = request.getParameter("userName");  		
  		logger.info("输入参数：" + "token = " + token + ", userName = " + userName);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//一级菜单权限应用模型函数
		if(isAuth)
			al1mpDto = al1mpAppMod.appModFunc(token, userName, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(al1mpDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(al1mpDto);
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
  	
  	//二级菜单权限
  	@RequestMapping(value = "/v1/amL2MenuPerm",method = RequestMethod.GET)
  	public String v1_amL2MenuPerm(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmL2MenuPermDTO al2mpDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//一级菜单ID
  		String l1MenuId	= request.getParameter("menuId");
  		//用户名
  		String userName = request.getParameter("userName");  		
  		logger.info("输入参数：" + "token = " + token + ", l1MenuId = " + l1MenuId + ", userName = " + userName);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//账号详情应用模型函数
		if(isAuth)
			al2mpDto = al2mpAppMod.appModFunc(token, l1MenuId, userName, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(al2mpDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(al2mpDto);
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
  	
  	//三级菜单权限（按钮权限）
  	@RequestMapping(value = "/v1/amL3MenuPerm",method = RequestMethod.GET)
  	public String v1_amL3MenuPerm(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmL3MenuPermDTO al3mpDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//二级菜单ID
  		String l2MenuId = request.getParameter("menuId");
  		//用户名
  		String userName = request.getParameter("userName");  		
  		logger.info("输入参数：" + "token = " + token + ", userName = " + userName);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//账号详情应用模型函数
		if(isAuth)
			al3mpDto = al3mpAppMod.appModFunc(token, l2MenuId, userName, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(al3mpDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(al3mpDto);
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
  	
  	//菜单权限
  	@RequestMapping(value = "/v1/amMenuPerm",method = RequestMethod.GET)
  	public String v1_amMenuPerm(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmMenuPermDTO ampDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//用户名
  		String userName = request.getParameter("userName");  		
  		logger.info("输入参数：" + "token = " + token + ", userName = " + userName);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//一级菜单权限应用模型函数
		if(isAuth)
			ampDto = ampAppMod.appModFunc(token, userName, db1Service, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(ampDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(ampDto);
  				strResp = strResp.replaceAll("\"list\"", "\"children\"");
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
  	
  	//用户名检测
  	@RequestMapping(value = "/v1/amUserCheck",method = RequestMethod.POST)
  	public String v1_amUserCheck(HttpServletRequest request)
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
  		//用户名检测应用模型函数
		if(isAuth)
			normResp = aucAppMod.appModFunc(strBodyCont, db2Service, codes);
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
  	
  	//管理员用户名检测
  	@RequestMapping(value = "/v1/amAdminUserCheck",method = RequestMethod.POST)
  	public String v1_amAdminUserCheck(HttpServletRequest request)
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
  		//管理员用户名检测应用模型函数
		if(isAuth)
			normResp = aaucAppMod.appModFunc(token,strBodyCont, db2Service, codes);
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
  	
	//添加账号
  	@RequestMapping(value = "/v1/amAddAccount",method = RequestMethod.POST)
  	public String v1_amAddAccount(HttpServletRequest request)
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
  		//添加账号应用模型函数
		if(isAuth)
			normResp = aaaAppMod.appModFunc(token, strBodyCont, db1Service, db2Service, codes);
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
  	
  	//编辑账号
  	@RequestMapping(value = "/v1/amEditAccount",method = RequestMethod.POST)
  	public String v1_amEditAccount(HttpServletRequest request)
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
  		//添加账号应用模型函数
		if(isAuth)
			normResp = aeaAppMod.appModFunc(token, strBodyCont, db1Service, db2Service, codes);
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
  	
  	//账号启禁用
  	@RequestMapping(value = "/v1/amUserEnable",method = RequestMethod.POST)
  	public String v1_amUserEnable(HttpServletRequest request)
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
  		//账号启禁用应用模型函数
		if(isAuth)
			normResp = aueAppMod.appModFunc(strBodyCont, db2Service, codes);
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
  	
  	//账号删除
  	@RequestMapping(value = "/v1/amAccountDel",method = RequestMethod.POST)
  	public String v1_amAccountDel(HttpServletRequest request)
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
  		//添加账号应用模型函数
		if(isAuth)
			normResp = aadeAppMod.appModFunc(strBodyCont, db2Service, codes);
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
  	
  	//角色类型编码列表
  	@RequestMapping(value = "/v1/roleTypeCodes",method = RequestMethod.GET)
  	public String v1_roleTypeCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		RoleTypeCodesDTO rtcDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		logger.info("输入参数：" + "token = " + token);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//角色类型编码列表应用模型函数
		if(isAuth)
			rtcDto = rtcAppMod.appModFunc(db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(rtcDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(rtcDto);
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
  	
  	//角色管理
  	@RequestMapping(value = "/v1/amRoleManage",method = RequestMethod.GET)
  	public String v1_amRoleManage(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmRoleManageDTO armDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//角色名称
  		String roleName = request.getParameter("roleName");
  		//角色类型，1:监管部门，2:学校
  		String roleType = request.getParameter("roleType");  		
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";  		
  		logger.info("输入参数：" + "token = " + token + ", roleName = " + roleName + ", roleType = " + roleType + ", page = " + page + ", pageSize = " + pageSize);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//用户管理应用模型函数
		if(isAuth)
			armDto = armAppMod.appModFunc(roleName, roleType, page, pageSize, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(armDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(armDto);
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
  	
  	//导出角色管理
  	@RequestMapping(value = "/v1/expAmRoleManage",method = RequestMethod.GET)
  	public String v1_expAmRoleManage(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		ExpAmRoleManageDTO earmDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//角色名称
  		String roleName = request.getParameter("roleName");
  		//角色类型，1:监管部门，2:学校
  		String roleType = request.getParameter("roleType");  		
  		logger.info("输入参数：" + "token = " + token + ", roleName = " + roleName + ", roleType = " + roleType);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//用户管理应用模型函数
		if(isAuth)
			earmDto = earmAppMod.appModFunc(token, roleName, roleType, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(earmDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(earmDto);
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
  	
  	//角色详情
  	@RequestMapping(value = "/v1/amRoleDet",method = RequestMethod.GET)
  	public String v1_amRoleDet(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AmRoleDetDTO ardDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//角色ID
  		String roleId = request.getParameter("roleId");  		
  		logger.info("输入参数：" + "token = " + token + ", roleId = " + roleId);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//角色详情应用模型函数
		if(isAuth)
			ardDto = ardAppMod.appModFunc(roleId, db2Service, codes);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(ardDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(ardDto);
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
  	
  	//角色名检测
  	@RequestMapping(value = "/v1/amRoleCheck",method = RequestMethod.POST)
  	public String v1_amRoleCheck(HttpServletRequest request)
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
  		//角色名检测应用模型函数
		if(isAuth)
			normResp = arcAppMod.appModFunc(strBodyCont, db2Service, eduBdService, codes);
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
  	
  	//添加角色
  	@RequestMapping(value = "/v1/amAddRole",method = RequestMethod.POST)
  	public String v1_amAddRole(HttpServletRequest request)
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
  		//添加账号应用模型函数
		if(isAuth)
			normResp = aarAppMod.appModFunc(token, strBodyCont, db2Service, codes);
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
  	
  	//编辑角色
  	@RequestMapping(value = "/v1/amEditRole",method = RequestMethod.POST)
  	public String v1_amEditRole(HttpServletRequest request)
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
  		//添加账号应用模型函数
		if(isAuth)
			normResp = aerAppMod.appModFunc(token, strBodyCont, db2Service, codes);
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
  	
  	//删除角色
  	@RequestMapping(value = "/v1/amDelRole",method = RequestMethod.POST)
  	public String v1_amDelRole(HttpServletRequest request)
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
  		//添加账号应用模型函数
		if(isAuth)
			normResp = adrAppMod.appModFunc(strBodyCont, db2Service, codes);
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

  	//保存当前用户指定接口设置的显示列
  	@RequestMapping(value = "/v1/addUserInterfaceColums",method = RequestMethod.POST)
  	public String v1_addUserInterfaceColums(HttpServletRequest request,@RequestBody AddUserInterfaceColums columsInput)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AddUserInterfaceColumsDTO userColumsDTO = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//Body传输内容，格式为application/json
  		logger.info("输入参数：" + "token = " + token + ", interfaceName = " + columsInput.getInterfaceName()
  		+ ", columns = " + columsInput.getColumns());
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//保存个人资料应用模型函数
		if(isAuth)
			userColumsDTO = columsAppMod.appModFunc(token, columsInput, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(userColumsDTO != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(userColumsDTO);
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
  	
  	//获取当前用户指定接口设置的显示列
  	@RequestMapping(value = "/v1/getUserInterfaceColums",method = RequestMethod.GET)
  	public String v1_getUserInterfaceColums(HttpServletRequest request,@RequestParam String interfaceName)
  	{
  		//初始化响应数据
  		String strResp = null;
  		AddUserInterfaceColumsDTO auiDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");		
  		logger.info("输入参数：" + "token = " + token);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//个人资料应用模型函数
		if(isAuth)
			auiDto = getColumsAppMod.appModFunc(token, interfaceName, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(auiDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(auiDto);
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


  	//通讯录相关
 	//获取当前用户指定接口设置的显示列
  	@CheckUserToken
  	@RequestMapping(value = "/v1/getAddressLableList",method = RequestMethod.GET)
  	public ApiResponse<PageInfo<List<TEduBdAddressLableObj>>>  getAddressLableList(HttpServletRequest request,@RequestParam Map<String,String> inputMap)
  	{
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");	
  		return userLableAppMod.getAddressLableList(token, inputMap, db2Service);
  	}
  	
 	//获取当前用户指定接口设置的显示列
  	@CheckUserToken
  	@RequestMapping(value = "/v1/getAddressLableInfo",method = RequestMethod.GET)
  	public ApiResponse<TEduBdAddressLableObj>  getAddressLableInfo(HttpServletRequest request,@RequestParam Integer id )
  	{
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");	
  		return userLableAppMod.getAddressLableInfo(token, id, db2Service);
  	}
  	
 	//获取当前用户指定接口设置的显示列
  	@CheckUserToken
  	@RequestMapping(value = "/v1/saveAddressLable",method = RequestMethod.POST)
  	public ApiResponse<T>  saveAddressLableList(HttpServletRequest request,@RequestBody TEduBdAddressLableObj addressLable )
  	{
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");	
  		return userLableAppMod.addAddressLableInfo(token, addressLable, db2Service);
  	}
  	
 	//获取当前用户指定接口设置的显示列
  	@CheckUserToken
  	@RequestMapping(value = "/v1/deleteAddressLable",method = RequestMethod.GET)
  	public ApiResponse<T>  deleteAddressLableList(HttpServletRequest request,@RequestParam Integer id )
  	{
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");	
  		return userLableAppMod.deleteAddressLableInfo(token, id, db2Service);
  	}
  	
  	@CheckUserToken
  	@RequestMapping(value = "/v1/getUserListByLable",method = RequestMethod.GET)
  	public ApiResponse<List<UserSimpleObj>>  getUserListByLable(HttpServletRequest request,@RequestParam Integer lableId)
  	{
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");	
  		return userLableAppMod.getUserListByLable(token, lableId, db2Service);
  	}
  	
  	@CheckUserToken
  	@RequestMapping(value = "/v1/deleteUserLableRelation",method = RequestMethod.GET)
  	public ApiResponse<T>  deleteUserLableRelation(HttpServletRequest request,@RequestParam String userId,@RequestParam Integer lableId  )
  	{
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");	
  		return userLableAppMod.deleteUserLableRelation(token,userId,lableId,db2Service);
  	}
}
