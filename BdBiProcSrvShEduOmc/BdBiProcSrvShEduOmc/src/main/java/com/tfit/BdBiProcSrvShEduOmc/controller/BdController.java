package com.tfit.BdBiProcSrvShEduOmc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.annotation.CheckUserToken;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdDishListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdMatListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdRmcLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdRmcListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdSchLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdSchListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdStaffLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdSupLicsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdSupListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.CompDepCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.DepartmentListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.EditSchoolAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.ExpBdRmcListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.ExpBdSchListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.ExpBdSupListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.FblMbCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.GenBraSchCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.GenSchCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.SemSetCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.SubLevelCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.SupTypeCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdDishListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdMatListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdRmcLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdRmcListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchList;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdStaffLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSupLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSupListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.CompDepCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.DepartmentListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.ExpBdRmcListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.ExpBdSchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.ExpBdSupListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.FblMbCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.GenBraSchCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.GenSchCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.SemSetCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.SubLevelCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.SupTypeCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Custom1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;

//基础数据BI应用服务
@RestController
@RequestMapping(value = "/biOptAnl")
public class BdController {
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
	
	//学期设置编码列表应用模型
	SemSetCodesAppMod sscAppMod = new SemSetCodesAppMod();
	
	//总分校编码列表应用模型
	GenBraSchCodesAppMod gbscAppMod = new GenBraSchCodesAppMod();
	
	//总校编码列表应用模型
	GenSchCodesAppMod gscAppMod = new GenSchCodesAppMod();
	
	//所属级别编码列表应用模型
	SubLevelCodesAppMod slcAppMod = new SubLevelCodesAppMod();
	
	//主管部门编码列表应用模型
	CompDepCodesAppMod cdcAppMod = new CompDepCodesAppMod();
	
	//证件主体编码列表应用模型
	FblMbCodesAppMod fmcAppMod = new FblMbCodesAppMod();
	
	//基础数据学校列表应用模型
	BdSchListAppMod bslAppMod = new BdSchListAppMod();
	
	//导出基础数据学校列表应用模型
	ExpBdSchListAppMod ebslAppMod = new ExpBdSchListAppMod();
		
	//基础数据团餐公司列表应用模型
	BdRmcListAppMod brlAppMod = new BdRmcListAppMod();
	
	//导出基础数据团餐公司列表应用模型
	ExpBdRmcListAppMod ebrlAppMod = new ExpBdRmcListAppMod();
	
	//供应商类型编码列表应用模型
	SupTypeCodesAppMod stcAppMod = new SupTypeCodesAppMod();
		
	//基础数据供应商列表应用模型
	BdSupListAppMod bsulAppMod = new BdSupListAppMod();
	
	//导出基础数据供应商列表应用模型
	ExpBdSupListAppMod ebsulAppMod = new ExpBdSupListAppMod();
		
	//基础数据原料列表应用模型
	BdMatListAppMod bmlAppMod = new BdMatListAppMod();
		
	//基础数据菜品列表应用模型
	BdDishListAppMod bdlAppMod = new BdDishListAppMod();
		
	//基础数据学校证照列表应用模型
	BdSchLicsAppMod bsliAppMod = new BdSchLicsAppMod();
		
	//基础数据团餐公司证照列表应用模型
	BdRmcLicsAppMod brmliAppMod = new BdRmcLicsAppMod();
		
	//基础数据供应商证照列表应用模型
	BdSupLicsAppMod bsuliAppMod = new BdSupLicsAppMod();
		
	//基础数据员工证照列表应用模型
	BdStaffLicsAppMod bstlAppMod = new BdStaffLicsAppMod();
	
	//主管部门列表应用模型
	DepartmentListAppMod departmentListAppMod = new DepartmentListAppMod();
	
	//学校编辑应用模型
	EditSchoolAppMod editSchoolAppMod = new EditSchoolAppMod();
  	
  	//学期设置编码列表
  	@RequestMapping(value = "/v1/semSetCodes",method = RequestMethod.GET)
  	public String v1_semSetCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		SemSetCodesDTO sscDto = null;
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
  		//学期设置编码列表应用模型函数
		if(isAuth)
			sscDto = sscAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(sscDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(sscDto);
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
  	
  	//总分校编码列表
  	@RequestMapping(value = "/v1/genBraSchCodes",method = RequestMethod.GET)
  	public String v1_genBraSchCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		GenBraSchCodesDTO gbscDto = null;
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
  		//总分校编码列表应用模型函数
		if(isAuth)
			gbscDto = gbscAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(gbscDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(gbscDto);
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
  	
  	//总校编码列表
  	@RequestMapping(value = "/v1/genSchCodes",method = RequestMethod.GET)
  	public String v1_genSchCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		GenSchCodesDTO gscDto = null;
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
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = String.valueOf(AppModConfig.maxPageSize);
  		logger.info("输入参数：" + "token = " + token + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//总校编码列表应用模型函数
		if(isAuth)
			gscDto = gscAppMod.appModFunc(token, distName, prefCity, province, page, pageSize, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(gscDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(gscDto);
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
  	
  	//所属级别编码列表
  	@RequestMapping(value = "/v1/subLevelCodes",method = RequestMethod.GET)
  	public String v1_subLevelCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		SubLevelCodesDTO slcDto = null;
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
  		//所属级别编码列表应用模型函数
		if(isAuth)
			slcDto = slcAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(slcDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(slcDto);
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
  	
  	//主管部门编码列表
  	@RequestMapping(value = "/v1/compDepCodes",method = RequestMethod.GET)
  	public String v1_compDepCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		CompDepCodesDTO cdcDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//所属，0:其他, 1:部级, 2:市级, 3:区级
  		String subLevel = request.getParameter("subLevel");
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
  		
  		//所属，0:其他, 1:部级, 2:市级, 3:区级(可以一次传入多个)
  		String subLevels = request.getParameter("subLevels");
  		
  		logger.info("输入参数：" + "token = " + token + ", subLevel = " + subLevel + ", distName = " + distName 
  				+ ", prefCity = " + prefCity + ", province = " + province+", subLevels = " + subLevels);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//主管部门编码列表应用模型函数
		if(isAuth)
			cdcDto = cdcAppMod.appModFunc(distName, prefCity, province, subLevel,subLevels, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(cdcDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(cdcDto);
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
  	
  	//证件主体编码列表
  	@RequestMapping(value = "/v1/fblMbCodes",method = RequestMethod.GET)
  	public String v1_fblMbCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		FblMbCodesDTO fmcDto = null;
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
  		//证件主体编码列表应用模型函数
		if(isAuth)
			fmcDto = fmcAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(fmcDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(fmcDto);
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
  	
  	//基础数据学校列表
  	@RequestMapping(value = "/v1/bdSchList",method = RequestMethod.GET)
  	public String v1_bdSchList(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		BdSchListDTO bslDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//学校名称
  		String schName = request.getParameter("schName");
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
  		//学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
  		String schType = request.getParameter("schType");
  		//学校性质，0:公办，1:民办，2:其他
  		String schProp = request.getParameter("schProp");
  		//学期设置，0:未设置，1:已设置
  		String isSetsem = request.getParameter("isSetsem");
  		//经营模式（供餐模式），0:自行加工，1:现场加工，2:快餐配送，3:食品加工商
  		String optMode = request.getParameter("optMode");
  		//关联单位
  		String relCompName = request.getParameter("relCompName");
  		//总分校标识，0:无，1:总校，2:分校
  		String schGenBraFlag = request.getParameter("schGenBraFlag");
  		//关联总校
  		String relGenSchName = request.getParameter("relGenSchName");
  		//所属，0:区级，1:市级，2:部级，21:其他
  		String subLevel = request.getParameter("subLevel");
  		//主管部门，0:市教委，1:商委，2:教育部
  		String compDep = request.getParameter("compDep");
  		//证件主体，0:学校，1:外包
  		String fblMb = request.getParameter("fblMb");
  		//统一社会信用代码
  		String uscc = request.getParameter("uscc");
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";
  		
  		String departmentId = request.getParameter("departmentId");
  		logger.info("输入参数：" + "token = " + token + ", schName = " + schName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", schType = " + schType + ", schProp = " + schProp + ", isSetsem = " + isSetsem + ", optMode = " + optMode + ", relCompName = " + relCompName + ", schGenBraFlag = " + schGenBraFlag + ", relGenSchName = " + relGenSchName + ", subLevel = " + subLevel + ", compDep = " + compDep + ", fblMb = " + fblMb + ", uscc = " + uscc + ", page = " + page + ", pageSize = " + pageSize);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//基础数据学校列表应用模型函数
		if(isAuth)
			bslDto = bslAppMod.appModFunc(token, schName, distName, prefCity, province, 
					schType, schProp, isSetsem, optMode, relCompName, schGenBraFlag, 
					relGenSchName, subLevel, compDep, fblMb, uscc, departmentId,
					page, pageSize, 
					db1Service, db2Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(bslDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(bslDto);
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
  	
  	//导出基础数据学校列表
  	@RequestMapping(value = "/v1/expBdSchList",method = RequestMethod.GET)
  	public String v1_expBdSchList(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		ExpBdSchListDTO ebslDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//学校名称
  		String schName = request.getParameter("schName");
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
  		//学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
  		String schType = request.getParameter("schType");
  		//学校性质，0:公办，1:民办，2:其他
  		String schProp = request.getParameter("schProp");
  		//学期设置，0:未设置，1:已设置
  		String isSetsem = request.getParameter("isSetsem");
  		//经营模式（供餐模式），0:自行加工，1:现场加工，2:快餐配送，3:食品加工商
  		String optMode = request.getParameter("optMode");
  		//关联单位
  		String relCompName = request.getParameter("relCompName");
  		//总分校标识，0:无，1:总校，2:分校
  		String schGenBraFlag = request.getParameter("schGenBraFlag");
  		//关联总校
  		String relGenSchName = request.getParameter("relGenSchName");
  		//所属，0:区级，1:市级，2:部级，21:其他
  		String subLevel = request.getParameter("subLevel");
  		//主管部门，0:市教委，1:商委，2:教育部
  		String compDep = request.getParameter("compDep");
  		//证件主体，0:学校，1:外包
  		String fblMb = request.getParameter("fblMb");
  		//统一社会信用代码
  		String uscc = request.getParameter("uscc");
  		String departmentId = request.getParameter("departmentId");
  		logger.info("输入参数：" + "token = " + token + ", schName = " + schName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", schType = " + schType + ", schProp = " + schProp + ", isSetsem = " + isSetsem + ", optMode = " + optMode + ", relCompName = " + relCompName + ", schGenBraFlag = " + schGenBraFlag + ", relGenSchName = " + relGenSchName + ", subLevel = " + subLevel + ", compDep = " + compDep + ", fblMb = " + fblMb + ", uscc = " + uscc);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//导出基础数据学校列表应用模型函数
		if(isAuth)
			ebslDto = ebslAppMod.appModFunc(token, schName, distName, prefCity, province, schType, schProp,
					isSetsem, optMode, relCompName, schGenBraFlag, relGenSchName, subLevel, compDep, fblMb, uscc,departmentId, db1Service, db2Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(ebslDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(ebslDto);
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
  	
  	//编辑学校
  	@CheckUserToken
  	@RequestMapping(value = "/v1/editSchool",method = RequestMethod.POST)
  	public IOTHttpRspVO v1_editSchool(HttpServletRequest request,@RequestBody BdSchList bdSchList)
  	{
  		//初始化响应数据
  		IOTHttpRspVO normResp = null;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		logger.info("输入参数：" + "token = " + token + ", strBodyCont = " + bdSchList.toString());
		normResp = editSchoolAppMod.appModFunc(token, bdSchList, db1Service, db2Service,codes);
  			
  		return normResp;
  	}
  	
  	//基础数据团餐公司列表
  	@RequestMapping(value = "/v1/bdRmcList",method = RequestMethod.GET)
  	public String v1_bdRmcList(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		BdRmcListDTO brlDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//企业名称
  		String compName = request.getParameter("compName");
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
  		//联系人
  		String contact = request.getParameter("contact");
  		//食品经营许可证
  		String fblNo = request.getParameter("fblNo");
  		//统一社会信用代码
  		String uscc = request.getParameter("uscc");
  		//注册资本，单位：万元
  		String regCapital = request.getParameter("regCapital");
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";
  		logger.info("输入参数：" + "token = " + token + ", compName = " + compName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", contact = " + contact + ", fblNo = " + fblNo + ", uscc = " + uscc + ", regCapital = " + regCapital + ", page = " + page + ", pageSize = " + pageSize);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//基础数据团餐公司列表应用模型函数
		if(isAuth)
			brlDto = brlAppMod.appModFunc(token, compName, distName, prefCity, province, contact, fblNo, uscc, regCapital, page, pageSize, db1Service, db2Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(brlDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(brlDto);
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
  	
  	//导出基础数据团餐公司列表
  	@RequestMapping(value = "/v1/expBdRmcList",method = RequestMethod.GET)
  	public String v1_expBdRmcList(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		ExpBdRmcListDTO ebrlDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//企业名称
  		String compName = request.getParameter("compName");
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
  		//联系人
  		String contact = request.getParameter("contact");
  		//食品经营许可证
  		String fblNo = request.getParameter("fblNo");
  		//统一社会信用代码
  		String uscc = request.getParameter("uscc");
  		//注册资本，单位：万元
  		String regCapital = request.getParameter("regCapital");  		
  		logger.info("输入参数：" + "token = " + token + ", compName = " + compName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", contact = " + contact + ", fblNo = " + fblNo + ", uscc = " + uscc + ", regCapital = " + regCapital);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//导出基础数据团餐公司列表
		if(isAuth)
			ebrlDto = ebrlAppMod.appModFunc(token, compName, distName, prefCity, province, contact, fblNo, uscc, regCapital, db1Service, db2Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(ebrlDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(ebrlDto);
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
  	
  	//3.5.13.	供应商类型编码列表
  	@RequestMapping(value = "/v1/supTypeCodes",method = RequestMethod.GET)
  	public String v1_supTypeCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		SupTypeCodesDTO stcDto = null;
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
  		//供应商类型编码列表应用模型函数
		if(isAuth)
			stcDto = stcAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(stcDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(stcDto);
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
  	
  	//基础数据供应商列表
  	@RequestMapping(value = "/v1/bdSupList",method = RequestMethod.GET)
  	public String v1_bdSupList(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		BdSupListDTO bsulDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//供应商名称
  		String supplierName = request.getParameter("supplierName");
  		//供应商类型，0:生产类，1:经销类
  		String supplierType = request.getParameter("supplierType");
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
  		//营业执照
  		String blNo = request.getParameter("blNo");
  		//注册资本，单位：万元
  		String regCapital = request.getParameter("regCapital");
  		//食品经营许可证
  		String fblNo = request.getParameter("fblNo");
  		//食品流通许可证
  		String fcpNo = request.getParameter("fcpNo");
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";
  		logger.info("输入参数：" + "token = " + token + ", supplierName = " + supplierName + ", supplierType = " + supplierType + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", blNo = " + blNo + ", regCapital = " + regCapital + ", fblNo = " + fblNo + ", fcpNo = " + fcpNo + ", page = " + page + ", pageSize = " + pageSize);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//基础数据供应商列表应用模型函数
		if(isAuth)
			bsulDto = bsulAppMod.appModFunc(token, supplierName, supplierType,
					distName, prefCity, province, blNo, regCapital,
					fblNo, fcpNo, page, pageSize, db1Service, db2Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");
  		//设置响应数据
  		if(bsulDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(bsulDto);
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
  	
  	//导出基础数据供应商列表
  	@RequestMapping(value = "/v1/expBdSupList",method = RequestMethod.GET)
  	public String v1_expBdSupList(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		ExpBdSupListDTO ebslDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//供应商名称
  		String supplierName = request.getParameter("supplierName");
  		//供应商类型，0:生产型，1:经销类
  		String supplierType = request.getParameter("supplierType");  		
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
  		//营业执照
  		String blNo = request.getParameter("blNo");
  		//注册资本，单位：万元
  		String regCapital = request.getParameter("regCapital");
  		//食品经营许可证
  		String fblNo = request.getParameter("fblNo");
  		//食品流通许可证
  		String fcpNo = request.getParameter("fcpNo");  		
  		logger.info("输入参数：" + "token = " + token + ", supplierName = " + supplierName + ", supplierType = " + supplierType + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", blNo = " + blNo + ", regCapital = " + regCapital + ", fblNo = " + fblNo + ", fcpNo = " + fcpNo);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//导出基础数据团餐公司列表
		if(isAuth)
			ebslDto = ebsulAppMod.appModFunc(token, supplierName, supplierType, distName, prefCity, province, blNo, regCapital, fblNo, fcpNo, db1Service, db2Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(ebslDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(ebslDto);
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
  	
  	//基础数据原料列表
  	@RequestMapping(value = "/v1/bdMatList",method = RequestMethod.GET)
  	public String v1_bdMatList(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		BdMatListDTO bmlDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//团餐公司名称
  		String rmcName = request.getParameter("rmcName");
  		//物料名称
  		String matName = request.getParameter("matName");
  		//标准名称
  		String standardName = request.getParameter("standardName");
  		//原料类别
  		String matCategory = request.getParameter("matCategory");
  		//物料分类，0:冻品，1:非冻品
  		String strMatClassify = request.getParameter("matClassify");
  		int matClassify = 0;
  		if(strMatClassify != null)
  			matClassify = Integer.parseInt(strMatClassify);
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
  		logger.info("输入参数：" + "token = " + token + ", rmcName = " + rmcName + ", matName = " + matName + ", standardName = " + standardName + ", matCategory = " + matCategory + ", matClassify = " + matClassify + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//基础数据原料列表应用模型函数
		if(isAuth)
			bmlDto = bmlAppMod.appModFunc(rmcName, matName, standardName, matCategory, matClassify, distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(bmlDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(bmlDto);
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
  	
  	//基础数据菜品列表
  	@RequestMapping(value = "/v1/bdDishList",method = RequestMethod.GET)
  	public String v1_bdDishList(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		BdDishListDTO bdlDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//团餐公司名称
  		String rmcName = request.getParameter("rmcName");
  		//菜品名称
  		String dishName = request.getParameter("dishName");
  		//颜色
  		String dishColor = request.getParameter("dishColor");
  		//形状
  		String dishShape = request.getParameter("dishShape");
  		//工艺
  		String dishTech = request.getParameter("dishTech");
  		//口味
  		String dishTaste = request.getParameter("dishTaste");
  		//菜系
  		String dishStyle = request.getParameter("dishStyle");
  		//类别
  		String dishCategory = request.getParameter("dishCategory");  		
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
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";
  		logger.info("输入参数：" + "token = " + token + ", rmcName = " + rmcName + ", dishName = " + dishName + ", dishColor = " + dishColor + ", dishShape = " + dishShape + ", dishTech = " + dishTech + ", dishTaste = " + dishTaste + ", dishStyle = " + dishStyle + ", dishCategory = " + dishCategory + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//基础数据菜品列表应用模型函数
		if(isAuth)
			bdlDto = bdlAppMod.appModFunc(rmcName, dishName, dishColor, dishShape, dishTech, dishTaste, dishStyle, dishCategory, distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(bdlDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(bdlDto);
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
  	
  	//基础数据学校证照列表
  	@RequestMapping(value = "/v1/bdSchLics",method = RequestMethod.GET)
  	public String v1_bdSchLics(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		BdSchLicsDTO bsliDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//企业名称
  		String compName = request.getParameter("compName"); 		
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
  		//联系人
  		String contact = request.getParameter("contact");
  		//手机号码
  		String mobilePhone = request.getParameter("mobilePhone");
  		//食品经营许可证
  		String fblNo = request.getParameter("fblNo");
  		//营业执照
  		String blNo = request.getParameter("blNo");
  		//注册资本，单位：万元
  		String regCapital = request.getParameter("regCapital");
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";  		
  		logger.info("输入参数：" + "token = " + token + ", compName = " + compName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", contact = " + contact + ", mobilePhone = " + mobilePhone + ", fblNo = " + fblNo + ", blNo = " + blNo + ", regCapital = " + regCapital + ", page = " + page + ", pageSize = " + pageSize);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//基础数据学校证照列表应用模型函数
		if(isAuth)
			bsliDto = bsliAppMod.appModFunc(compName, distName, prefCity, province, contact, mobilePhone, fblNo, blNo, regCapital, page, pageSize, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(bsliDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(bsliDto);
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
  	
  	//基础数据团餐公司证照列表
  	@RequestMapping(value = "/v1/bdRmcLics",method = RequestMethod.GET)
  	public String v1_bdRmcLics(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		BdRmcLicsDTO brmliDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//企业名称
  		String compName = request.getParameter("compName"); 		
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
  		//联系人
  		String contact = request.getParameter("contact");
  		//手机号码
  		String mobilePhone = request.getParameter("mobilePhone");
  		//食品经营许可证
  		String fblNo = request.getParameter("fblNo");
  		//营业执照
  		String blNo = request.getParameter("blNo");
  		//注册资本，单位：万元
  		String regCapital = request.getParameter("regCapital");  		
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";
  		logger.info("输入参数：" + "token = " + token + ", compName = " + compName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", contact = " + contact + ", mobilePhone = " + mobilePhone + ", fblNo = " + fblNo + ", blNo = " + blNo + ", regCapital = " + regCapital + ", page = " + page + ", pageSize = " + pageSize);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//基础数据团餐公司证照列表应用模型函数
		if(isAuth)
			brmliDto = brmliAppMod.appModFunc(compName, distName, prefCity, province, contact, mobilePhone, fblNo, blNo, regCapital, page, pageSize, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(brmliDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(brmliDto);
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
  	
  	//基础数据供应商证照列表
  	@RequestMapping(value = "/v1/bdSupLics",method = RequestMethod.GET)
  	public String v1_bdSupLics(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		BdSupLicsDTO bsulDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		//供应商名称
  		String supplierName = request.getParameter("supplierName"); 		
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
  		//联系人
  		String contact = request.getParameter("contact");
  		//手机号码
  		String mobilePhone = request.getParameter("mobilePhone");
  		//食品经营许可证
  		String fblNo = request.getParameter("fblNo");
  		//营业执照
  		String blNo = request.getParameter("blNo");
  		//注册资本，单位：万元
  		String regCapital = request.getParameter("regCapital");  		
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";  		
  		logger.info("输入参数：" + "token = " + token + ", supplierName = " + supplierName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", contact = " + contact + ", mobilePhone = " + mobilePhone + ", fblNo = " + fblNo + ", blNo = " + blNo + ", regCapital = " + regCapital + ", page = " + page + ", pageSize = " + pageSize);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//基础数据供应商证照列表应用模型函数
		if(isAuth)
			bsulDto = bsuliAppMod.appModFunc(supplierName, distName, prefCity, province, contact, mobilePhone, fblNo, blNo, regCapital, page, pageSize, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(bsulDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(bsulDto);
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
  	  	
  	//基础数据员工证照列表
  	@RequestMapping(value = "/v1/bdStaffLics",method = RequestMethod.GET)
  	public String v1_bdStaffLics(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		BdStaffLicsDTO bstlDto = null;
  		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
  		//授权码
  		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");  		
  		//团餐公司名称
  		String rmcName = request.getParameter("rmcName"); 	  		
  		//员工姓名
  		String staffName = request.getParameter("staffName");
  		//项目点名称
  		String ppName = request.getParameter("ppName");
  		//证件类型，0:食品经营许可证，1:营业执照，2:健康证，3:餐饮服务许可证，4:A1证，5:A2证，6:B证，7:C证
  		String licType = request.getParameter("licType");
  		//证件编号
  		String licNo = request.getParameter("licNo");
  		//发证日期，格式：xxxx-xx-xx
  		String licStartDate = request.getParameter("licStartDate");
  		//证照到期日，格式：xxxx-xx-xx
  		String licExpireDate = request.getParameter("licExpireDate");
  		//证照状态，-1:信息不完整，0:过期，1:有效
  		String licStatus = request.getParameter("licStatus");  		
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
  		//页号
  		String page = request.getParameter("page");
  		if(page == null)
  			page = "1";
  		//分页大小
  		String pageSize = request.getParameter("pageSize");
  		if(pageSize == null)
  			pageSize = "20";  		
  		logger.info("输入参数：" + "token = " + token + ", rmcName = " + rmcName + ", staffName = " + staffName + ", ppName = " + ppName + ", licType = " + licType + ", licNo = " + licNo + ", licStartDate = " + licStartDate + ", licExpireDate = " + licExpireDate + ", licStatus = " + licStatus + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
  	    //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//基础数据员工证照列表应用模型函数
		if(isAuth)
			bstlDto = bstlAppMod.appModFunc(rmcName, staffName, ppName, licType, licNo, licStartDate, licExpireDate, licStatus, distName, prefCity, province, page, pageSize, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(bstlDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(bstlDto);
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

  	//基础数据学校列表
  	@CheckUserToken
  	@RequestMapping(value = "/v1/departmentList",method = RequestMethod.GET)
  	public DepartmentListDTO v1_departmentList(HttpServletRequest request,@RequestParam Map<String,String> inputParm)
  	{
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
  		logger.info("输入参数：" + inputParm.toString());
		return departmentListAppMod.appModFunc(token, inputParm, db1Service, db2Service,saasService);
  	}
}
