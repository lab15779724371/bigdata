package com.tfit.BdBiProcSrvShEduOmc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.EtVidAttachmentAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.EtVidDetAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.EtVidLibPubAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.EtVidThumbAttAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.OeExamPaperDetAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.OeExamPapersAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.OeSubAnsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.EduTrainClassifyCodesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.EtVidAdminVideosAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.EtVidLibAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.EtVidLibDelAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ev.EtVidLibEditAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidAttachmentDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibPubDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidThumbAttDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeExamPaperDetDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeExamPapersDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.OeSubAnsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EduTrainClassifyCodesDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidAdminVideosDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.ev.EtVidLibEditDTO;
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
 * @Descritpion：运营管理-教育培训视频
 * @author: tianfang_infotech
 * @date: 2019/1/2 16:47
 */
@RestController
@RequestMapping(value = "/biOptAnl")
public class EduVideoController {
    private static final Logger logger = LogManager.getLogger(EduVideoController.class.getName());

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

    @Autowired
    ObjectMapper objectMapper;
    
    //教育培训分类编码列表应用模型
    EduTrainClassifyCodesAppMod etccAppMod = new EduTrainClassifyCodesAppMod();

    /**
     * 教育培训视频库应用模型
     */
    EtVidLibAppMod evlAppMod = new EtVidLibAppMod();

    /**
     * 教育培训视频管理视频列表应用模型
     */
    EtVidAdminVideosAppMod evavAppMod = new EtVidAdminVideosAppMod();
    
    //视频缩略图附件上传应用模型
    EtVidThumbAttAppMod evtaAppMod = new EtVidThumbAttAppMod();
    
    //视频附件上传应用模型
    EtVidAttachmentAppMod evaAppMod = new EtVidAttachmentAppMod();
    
    //教育培训视频发布应用模型
    EtVidLibPubAppMod evlpAppMod = new EtVidLibPubAppMod();
    
    //教育培训视频编辑应用模型
    EtVidLibEditAppMod evleAppMod = new EtVidLibEditAppMod();
    
    //教育培训视频详情应用模型
    EtVidDetAppMod evdAppMod = new EtVidDetAppMod();
    
    //在线考试试卷列表应用模型
    OeExamPapersAppMod oepAppMod = new OeExamPapersAppMod();
    
    //在线考试试卷详情应用模型
    OeExamPaperDetAppMod oepdAppMod = new OeExamPaperDetAppMod();
    
    //在线考试提交答案应用模型
    OeSubAnsAppMod osaAppMod = new OeSubAnsAppMod();
    
    //教育培训视频删除应用模型
    EtVidLibDelAppMod evldAppMod = new EtVidLibDelAppMod();
    
    /**
     * 3.10.1 - 教育培训分类编码列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/eduTrainClassifyCodes", method = RequestMethod.GET)
    public String v1_eduTrainClassifyCodes(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        EduTrainClassifyCodesDTO etccDto = null;
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
        //承办人编码列表应用模型函数
		if(isAuth)
			etccDto = etccAppMod.appModFunc(distName, prefCity, province, db1Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (etccDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(etccDto);
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
     * 3.10.1 - 教育培训视频库
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/etVidLib", method = RequestMethod.GET)
    public String v1_etVidLib(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        EtVidLibDTO evlDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //视频名称，模糊查询
        String vidName = request.getParameter("vidName");
        //视频分类，默认为0，0:全部，1:系统操作，2:食品安全，3:政策法规
        String vidCategory = request.getParameter("vidCategory");
        //排序类型，默认为0，0:按播放次数降序，1:按上传时间降序，2:按好评率降序
        String sortType = request.getParameter("sortType");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
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
        logger.info("输入参数：" + "token = " + token + ", vidName = " + vidName + ", vidCategory = " + vidCategory + ", sortType = " + sortType + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //教育培训视频库应用模型函数
		if(isAuth)
			evlDto = evlAppMod.appModFunc(token, vidName, vidCategory, sortType, distName, prefCity, province, page, pageSize, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (evlDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(evlDto);
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
     * 3.10.3 - 教育培训视频管理视频列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/etVidAdminVideos", method = RequestMethod.GET)
    public String v1_etVidAdminVideos(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        EtVidAdminVideosDTO evavDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
        //日期类型，0:上传日期，1:审核日期
        String dateType = request.getParameter("dateType");
        //开始日期，格式：xxxx-xx-xx
        String startDate = request.getParameter("startSubDate");
        //结束日期，格式：xxxx-xx-xx
        String endDate = request.getParameter("endSubDate");
        //视频名称，模糊查询
        String vidName = request.getParameter("vidName");
        //视频分类，1:系统操作，2:食品安全，3:政策法规
        String vidCategory = request.getParameter("vidCategory");
        //视频状态，0:待审核，1:已审核，2:已下架，3:已驳回
        String vidStatus = request.getParameter("vidStatus");
        //区域名称
        String distName = request.getParameter("distName");
        if (distName == null) {
            distName = request.getParameter("distname");
        }
        //授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
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
        logger.info("输入参数：" + "token = " + token + ", dateType = " + dateType + ", startDate = " + startDate + ", endDate = " + endDate + ", vidName = " + vidName + ", vidCategory = " + vidCategory + ", vidStatus = " + vidStatus + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //教育培训视频管理视频列表应用模型函数
		if(isAuth)
			evavDto = evavAppMod.appModFunc(token, dateType, startDate, endDate, vidName, vidCategory, vidStatus, distName, prefCity, province, page, pageSize, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (evavDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(evavDto);
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
     * 3.10.2 - 视频缩略图附件上传
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/etVidThumbAtt", method = RequestMethod.POST)
    public String v1_etVidThumbAtt(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        EtVidThumbAttDTO evtaDto = null;
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
			evtaDto = evtaAppMod.appModFunc(fileName, request);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (evtaDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(evtaDto);
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
     * 3.10.3 - 视频附件上传
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/etVidAttachment", method = RequestMethod.POST)
    public String v1_etVidAttachment(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        EtVidAttachmentDTO evaDto = null;
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
			evaDto = evaAppMod.appModFunc(fileName, request);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (evaDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(evaDto);
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
     * 3.10.4 - 教育培训视频发布
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/etVidLibPub", method = RequestMethod.POST)
    public String v1_etVidLibPub(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        EtVidLibPubDTO evlpDto = null;
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
			evlpDto = evlpAppMod.appModFunc(token, strBodyCont, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (evlpDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(evlpDto);
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
     * 3.10.11 - 教育培训视频编辑
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/etVidLibEdit", method = RequestMethod.POST)
    public String v1_etVidLibEdit(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        EtVidLibEditDTO evlepDto = null;
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
			evlepDto = evleAppMod.appModFunc(token, strBodyCont, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (evlepDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(evlepDto);
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
     * 3.10.12 - 教育培训视频删除
     * @param request
     * @return
     */
  	@RequestMapping(value = "/v1/etVidLibDel",method = RequestMethod.POST)
  	public String v1_etVidLibDel(HttpServletRequest request)
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
  		String strBodyCont = AppModConfig.GetBodyJsonReq(request, false);
  		logger.info("输入参数：" + "token = " + token + ", strBodyCont = " + strBodyCont);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
  		//添加账号应用模型函数
		if(isAuth)
			normResp = evldAppMod.appModFunc(strBodyCont, db2Service, codes);
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
    
    /**
     * 3.10.6 - 教育培训视频详情
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/etVidDet", method = RequestMethod.GET)
    public String v1_etVidDet(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        EtVidDetDTO evdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //视频ID，视频记录唯一标识
        String vidId = request.getParameter("vidId");
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
        logger.info("输入参数：" + "token = " + token + ", vidId = " + vidId + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //在线考试试卷详情应用模型函数
		if(isAuth)
			evdDto = evdAppMod.appModFunc(token, vidId, distName, prefCity, province, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (evdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(evdDto);
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
     * 3.10.5 - 在线考试试卷列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/oeExamPapers", method = RequestMethod.GET)
    public String v1_oeExamPapers(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        OeExamPapersDTO oepDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //试卷分类，1:系统操作，2:食品安全，3:政策法规
        String epCategory = request.getParameter("epCategory");
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
        logger.info("输入参数：" + "token = " + token + ", epCategory = " + epCategory + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province + ", page = " + page + ", pageSize = " + pageSize);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //在线考试试卷列表应用模型函数
		if(isAuth)
			oepDto = oepAppMod.appModFunc(token, epCategory, distName, prefCity, province, page, pageSize, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (oepDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(oepDto);
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
     * 3.10.6 - 在线考试试卷详情
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/oeExamPaperDet", method = RequestMethod.GET)
    public String v1_oeExamPaperDet(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        OeExamPaperDetDTO oepdDto = null;
        boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		//授权码
        String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
        //试卷ID，试卷唯一标识
        String epId = request.getParameter("epId");        
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
        logger.info("输入参数：" + "token = " + token + ", epId = " + epId + ", distName = " + distName + ", prefCity = " + prefCity + ", province = " + province);
  		//验证授权
  		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
        //在线考试试卷详情应用模型函数
		if(isAuth)
			oepdDto = oepdAppMod.appModFunc(token, epId, distName, prefCity, province, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");        
        //设置响应数据
        if (oepdDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(oepdDto);
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
     * 3.10.7 - 在线考试提交答案
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/oeSubAns", method = RequestMethod.POST)
    public String v1_oeSubAns(HttpServletRequest request) {
        //初始化响应数据
        String strResp = null;
        OeSubAnsDTO osaDto = null;
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
			osaDto = osaAppMod.appModFunc(token, strBodyCont, db1Service, db2Service);
		else
			logger.info("授权码：" + token + "，验证失败！");
        //设置响应数据
        if (osaDto != null) {
            try {
                strResp = objectMapper.writeValueAsString(osaDto);
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
