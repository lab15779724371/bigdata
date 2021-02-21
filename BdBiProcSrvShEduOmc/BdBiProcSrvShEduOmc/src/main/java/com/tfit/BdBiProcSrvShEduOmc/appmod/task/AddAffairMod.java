package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.controller.TaskController;
import com.tfit.BdBiProcSrvShEduOmc.dao.AddAffairBody;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.RbSchoolAttachment;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbUlAttachment;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

public class AddAffairMod {
	private static final Logger logger = LogManager.getLogger(AddAffairMod.class.getName());
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	ObjectMapper objectMapper = new ObjectMapper();
	// 资源路径
	String fileResPath = "/amSaveUserInfo/";

	public String appModFunc(HttpServletRequest request, Db1Service db1Service, Db2Service db2Service) {
		// 固定Dto层
		AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
		AppCommonData appCommonData = new AppCommonData();
		List<AppCommonDao> sourceDao = null;
		AppCommonDao pageTotal = null;
		List<LinkedHashMap<String, Object>> dataList = new ArrayList();

		// 业务操作
		try {
	  		//授权码
	  		String token =request.getHeader("Authorization");
	  		//验证授权
			boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service,new int[1]);
			if (verTokenFlag) {

				// Body传输内容，格式为 application/json
				AddAffairBody asuib = new AddAffairBody();
				String strBodyCont = new ToolUtil().GetBodyJsonReq(request, false);
				if (strBodyCont != null)
					asuib = objectMapper.readValue(strBodyCont, AddAffairBody.class);
				if (asuib != null) {
					//筛选参数
					String id=asuib.getId();
					TEduBdUserDo userINfo=db2Service.getBdUserInfoByToken(token);
					// 添加参数
					LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();
					filterParamMap.put("time", BCDTimeUtil.convertNormalFrom(null));
					filterParamMap.put("type", asuib.getType());
					filterParamMap.put("title", asuib.getTitle());
					filterParamMap.put("content", asuib.getContent());
					
					filterParamMap.put("release_unit", userINfo.getOrgName());
					filterParamMap.put("releaser", userINfo.getUserAccount());
					filterParamMap.put("releaser_name", userINfo.getName());
					
					filterParamMap.put("operation_unit", asuib.getOperationUnit());
					filterParamMap.put("operator", asuib.getOperator());
					filterParamMap.put("operator_name", asuib.getOperatorName());
					
	  				//学校信息
	  				if(asuib.getSchoolInfos() != null) {
	  					List<RbSchoolAttachment> schoolInfos = asuib.getSchoolInfos();
	  					if(schoolInfos.size() > 0) {
	  						String school_info = "";
	  						for(int i = 0; i < schoolInfos.size(); i++) {
	  							String amRes = " ";
	  							int idx = -1;
	  							logger.info("当前附件URL：" + schoolInfos.get(i).getSchoolName() );
	  								school_info +="(";
	  								school_info += schoolInfos.get(i).getSchoolName()==null?"":schoolInfos.get(i).getSchoolName();
	  								school_info += ",";
	  								school_info += schoolInfos.get(i).getSchoolAddress()==null?"":schoolInfos.get(i).getSchoolAddress();
	  								school_info += ",";
	  								school_info += schoolInfos.get(i).getSocialCreditCode()==null?"":schoolInfos.get(i).getSocialCreditCode();
	  								school_info += ",";
	  								school_info += schoolInfos.get(i).getEduSystem()==null?"":schoolInfos.get(i).getEduSystem();
	  								school_info +=")";
	  								school_info += "|";
	  						}
	  						filterParamMap.put("school_info",school_info.substring(0,school_info.length()-1));
	  					}else {
//	  						filterParamMap.put("school_info",null);
	  					}
	  				}else {
//	  					filterParamMap.put("school_info",null);
	  				}
					
					
	  				//附件信息
	  				if(asuib.getAmInfos() != null) {
	  					List<RbUlAttachment> amInfos = asuib.getAmInfos();
	  					if(amInfos.size() > 0) {
	  						String amInfo = "";
	  						for(int i = 0; i < amInfos.size(); i++) {
	  							String amRes = " ";
	  							int idx = -1;
	  							logger.info("当前附件URL：" + amInfos.get(i).getAmUrl() + "，文件服务域名：" + SpringConfig.repfile_srvdn);
	  								amInfo +="(";
	  								amInfo += amInfos.get(i).getAmName()==null?"":amInfos.get(i).getAmName();
	  								amInfo += ",";
	  								amInfo += amInfos.get(i).getAmUrl()==null?"":amInfos.get(i).getAmUrl();
	  								amInfo +=")";
	  								amInfo += "|";
	  						}
	  						filterParamMap.put("enclosure_link",amInfo.substring(0,amInfo.length()-1));
//	  						tebmnDo.setAmInfo(amInfo);
//	  						tebmnDo.setAmFlag(1);
	  					}else {
//	  						tebmnDo.setAmFlag(0);
	  					}
	  				}else {
//	  					tebmnDo.setAmFlag(0);
	  				}
					
					boolean resultFlag=db2Service.getAddAffair(filterParamMap);
					if(resultFlag) {
						appCommonExternalModulesDto.setResMsg("事务添加成功");
					}else {
						appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
						appCommonExternalModulesDto.setResMsg("事务添加失败");
					}
				}
				
				// 以上业务逻辑层修改
				// 固定返回
			} else {
				appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
				appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
			}
		} catch (Exception e) {
			appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
			appCommonExternalModulesDto.setResMsg(IOTRspType.System_ERR.getMsg().toString());
		}

		String strResp = null;
		try {
			strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
			strResp = new ToolUtil().rmExternalStructure(strResp);
		} catch (Exception e) {
			strResp = new ToolUtil().getInitJson();
		}
		return strResp;
	}
}
