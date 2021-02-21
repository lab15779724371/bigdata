package com.tfit.BdBiProcSrvShEduOmc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.is.IsFsgCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.is.IsFsgDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.is.IsFsgListAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.is.IsFsgCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.is.IsFsgDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.is.IsFsgListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Descritpion：业务数据-信息共享
 * @author: tianfang_infotech
 * @date: 2019/1/2 16:47
 */
@RestController
@RequestMapping(value = "/biOptAnl")
public class InfoShareController {
    private static final Logger logger = LogManager.getLogger(HomeController.class.getName());

    @Autowired
    ObjectMapper objectMapper;
    
    //mysql主数据库服务
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
     * 信息共享食品安全等级列表应用模型
     */
    IsFsgListAppMod iflAppMod = new IsFsgListAppMod();
    
    //食品安全等级编码列表应用模型
    IsFsgCodesAppMod ifcAppMod = new IsFsgCodesAppMod();

    /**
     * 信息共享食品安全等级详情列表应用模型
     */
    IsFsgDetsAppMod ifdAppMod = new IsFsgDetsAppMod();

    /**
     * 3.6.1 - 信息共享食品安全等级列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/isFsgList", method = RequestMethod.GET)
    public String v1_isFsgList(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        IsFsgListDTO iflDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //开始日期
        String startDate = request.getParameter("startSubDate");
        //结束日期
        String endDate = request.getParameter("endSubDate");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
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
        
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");
        
        logger.info("输入参数：" + "token = " + token + ", startDate = " + startDate + ", endDate = " + endDate 
        		+ ", schSelMode = " + schSelMode + ", subLevel = " + subLevel + ", compDep = " + compDep 
        		+ ", subDistName = " + subDistName + ", distName = " + distName + ", prefCity = " + prefCity 
        		+ ", province = " + province + ", page = " + page + ", pageSize = " + pageSize
        		+", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //信息共享食品安全等级列表应用模型函数
		if(isAuth)
			iflDto = iflAppMod.appModFunc(token, startDate, endDate, schSelMode, subLevel, compDep, subDistName, distName, prefCity, province, subLevels, compDeps, distNames, page, pageSize, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (iflDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(iflDto);
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
    
  	//食品安全等级编码列表
  	@RequestMapping(value = "/v1/isFsgCodes",method = RequestMethod.GET)
  	public String v1_isFsgCodes(HttpServletRequest request)
  	{
  		//初始化响应数据
  		String strResp = null;
  		IsFsgCodesDTO ifcDto = null;
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
  		//食品安全等级编码列表应用模型函数
		if(isAuth)
			ifcDto = ifcAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");  		
  		//设置响应数据
  		if(ifcDto != null) {
  			try {
  				strResp = objectMapper.writeValueAsString(ifcDto);
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

    /**
     * 3.6.2 - 信息共享食品安全等级详情列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/isFsgDets", method = RequestMethod.GET)
    public String v1_isFsgDets(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        IsFsgDetsDTO ifdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //学校筛选方式，0:按主管部门，1:按所在地
        String schSelMode = request.getParameter("schSelMode");
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
        String subLevel = request.getParameter("subLevel");
        //主管部门，按主管部门有效
        String compDep = request.getParameter("compDep");
        //所属区域名称，按主管部门有效
        String subDistName = request.getParameter("subDistName");
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
        //项目点名称
        String ppName = request.getParameter("ppName");
        //许可证
  		String licNo = request.getParameter("licNo");
        //等级，0:良好，1:一般，2:较差
        String grade = request.getParameter("grade");
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
        //所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效(多个[1,3])
        String subLevels = request.getParameter("subLevels");
        //主管部门，按主管部门有效(多个[1_1,3_1])
        String compDeps = request.getParameter("compDeps");
        //区域名称(多个[1,3])
        String distNames = request.getParameter("distNames");
        logger.info("输入参数：" + "token = " + token + ", schSelMode = " + schSelMode + ", subLevel = " + subLevel + ", compDep = " + compDep + ", subDistName = " + subDistName + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", ppName = " + ppName + ", licNo = " + licNo + ", grade = " + grade + ", page = " + page + ", pageSize = " + pageSize+", subLevels = " + subLevels + ", compDeps = " + compDeps + ", distNames = " + distNames);
        //验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);        
        //信息共享食品安全等级详情列表应用模型函数
		if(isAuth)
			ifdDto = ifdAppMod.appModFunc(token, schSelMode, subLevel, compDep, subDistName, distName, prefCity, province, ppName, licNo, grade, subLevels, compDeps, distNames, page, pageSize, db1Service, db2Service, saasService);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (ifdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(ifdDto);
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
