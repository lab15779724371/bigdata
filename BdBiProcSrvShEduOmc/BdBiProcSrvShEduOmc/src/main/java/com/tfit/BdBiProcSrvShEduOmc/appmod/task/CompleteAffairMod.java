package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AddAffairBody;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonExternalData;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

public class CompleteAffairMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	ObjectMapper objectMapper = new ObjectMapper();

	public String appModFunc(HttpServletRequest request, Db1Service db1Service, Db2Service db2Service) {
		// 固定Dto层
		AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
		AppCommonData appCommonData = new AppCommonData();
		AppCommonExternalData appCommonExternalData = new AppCommonExternalData();
		List<AppCommonDao> sourceDao = null;
		AppCommonDao pageTotal = null;
		List<LinkedHashMap<String, Object>> dataList = new ArrayList();
		List<LinkedHashMap<String, Object>> externalList = new ArrayList();

		// 业务操作
		try {
			// 授权码
			String token = request.getHeader("Authorization");
			// 验证授权
			boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service, new int[1]);
			if (verTokenFlag) {

				// Body传输内容，格式为 application/json
				AddAffairBody asuib = new AddAffairBody();
				String strBodyCont = new ToolUtil().GetBodyJsonReq(request, false);
				if (strBodyCont != null)
					asuib = objectMapper.readValue(strBodyCont, AddAffairBody.class);
				String id = asuib.getId();
				if (asuib != null) {
					TEduBdUserDo userINfo = db2Service.getBdUserInfoByToken(token);
					LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();
					if ("2".equals(asuib.getStatus())) {
						filterParamMap.put("completer", userINfo.getUserAccount());
						filterParamMap.put("completer_name", userINfo.getName());
						filterParamMap.put("completion_feedback", asuib.getCompletionFeedback());
						filterParamMap.put("completion_date", asuib.getCompletionDate());
						filterParamMap.put("status", asuib.getStatus());
					}

					if ("3".equals(asuib.getStatus())) {
						filterParamMap.put("reviewer", userINfo.getUserAccount());
						filterParamMap.put("reviewer_name", userINfo.getName());
						filterParamMap.put("reviewe_time", new ToolUtil().currentTime());
						filterParamMap.put("status", asuib.getStatus());
					}
					db2Service.getCompleteAffair(filterParamMap, id);
				}

				// 返回修改后数据
				if (id != null) {

					AppCommonDao resultDao = db2Service.getAffairDetail(id);
					// 获取列表数据
					LinkedHashMap<String, Object> data = resultDao.getCommonMap();
					Object enclosureLink = resultDao.getCommonMap().get("enclosureLink");

					if (enclosureLink != null && !enclosureLink.toString().trim().isEmpty()) {
						String[] enclosureLinkArr = enclosureLink.toString().split("\\|");
						for (int i = 0; i < enclosureLinkArr.length; i++) {
							LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
							dataMap.put("amName", enclosureLinkArr[i].replaceAll("\\(|\\)", "").split(",").length>=1?enclosureLinkArr[i].replaceAll("\\(|\\)", "").split(",")[0]:null);
							dataMap.put("amUrl", enclosureLinkArr[i].replaceAll("\\(|\\)", "").split(",").length>=2?enclosureLinkArr[i].replaceAll("\\(|\\)", "").split(",")[1]:null);
							dataList.add(dataMap);
						}
					}
					
					Object schoolInfo = resultDao.getCommonMap().get("schoolInfo");
					if (schoolInfo != null && !schoolInfo.toString().trim().isEmpty() ) {
						String[] schoolInfoArr = schoolInfo.toString().split("\\|");
						for (int i = 0; i < schoolInfoArr.length; i++) {
							LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
							dataMap.put("schoolName", schoolInfoArr[i].replaceAll("\\(|\\)", "").split(",").length>=1?schoolInfoArr[i].replaceAll("\\(|\\)", "").split(",")[0]:null);
							dataMap.put("schoolAddress", schoolInfoArr[i].replaceAll("\\(|\\)", "").split(",").length>=2?schoolInfoArr[i].replaceAll("\\(|\\)", "").split(",")[1]:null);
							dataMap.put("socialCreditCode", schoolInfoArr[i].replaceAll("\\(|\\)", "").split(",").length>=3?schoolInfoArr[i].replaceAll("\\(|\\)", "").split(",")[2]:null);
							dataMap.put("eduSystem", schoolInfoArr[i].replaceAll("\\(|\\)", "").split(",").length>=4?schoolInfoArr[i].replaceAll("\\(|\\)", "").split(",")[3]:null);
							externalList.add(dataMap);
						}
					}

					data.remove("enclosureLink");
					data.remove("schoolInfo");
					appCommonExternalData.setData(data);
					appCommonExternalData.setDataList(dataList);
					appCommonExternalData.setExternalList(externalList);
				}
				appCommonExternalModulesDto.setData(appCommonExternalData);

				// 以上业务逻辑层修改
				// 固定返回
			} else {
				appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
				appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
			}
		} catch (

		Exception e) {
			appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
			appCommonExternalModulesDto.setResMsg(IOTRspType.System_ERR.getMsg().toString());
		}

		String strResp = null;
		try {
			strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
			strResp = new ToolUtil().rmExternalStructure(strResp,"amInfos","schoolInfos");
		} catch (Exception e) {
			strResp = new ToolUtil().getInitJson();
		}
		return strResp;
	}
}
