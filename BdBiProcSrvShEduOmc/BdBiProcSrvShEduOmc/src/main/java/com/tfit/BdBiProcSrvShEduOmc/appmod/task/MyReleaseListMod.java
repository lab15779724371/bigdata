package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

public class MyReleaseListMod {
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;

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
			boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service,new int[2]);
			if (verTokenFlag) {
				// 以下业务逻辑层修改

				if (request.getParameter("page") != null && !request.getParameter("page").toString().isEmpty()) {
					this.curPageNum = Integer.parseInt(request.getParameter("page").toString());
				}
				if (request.getParameter("pageSize") != null && !request.getParameter("pageSize").toString().isEmpty()) {
					this.pageSize = Integer.parseInt(request.getParameter("pageSize").toString());
				}
				TEduBdUserDo userINfo=db2Service.getBdUserInfoByToken(token);
				// 筛选参数
				LinkedHashMap<String, Object> filterParamMap=new LinkedHashMap<String, Object>();
				filterParamMap.put("release_unit", userINfo.getOrgName());
				filterParamMap.put("releaser", userINfo.getUserAccount());
				
				//可选参数
				filterParamMap.put("startDate",request.getParameter("startDate"));
				filterParamMap.put("endDate",request.getParameter("endDate"));
				filterParamMap.put("operation_unit",request.getParameter("operationUnit"));
				filterParamMap.put("status",request.getParameter("status"));
				
				Integer startNum = (curPageNum - 1) * pageSize;
				
				if(filterParamMap.get("release_unit") !=null && filterParamMap.get("releaser") !=null) {
					sourceDao = db2Service.getMyReleaseList(filterParamMap);
					
					List<AppCommonDao> resultDao=sourceDao.subList(startNum, sourceDao.size() >(startNum + pageSize)?(startNum + pageSize):sourceDao.size());
					// 获取列表数据
					for (int i = 0; i < resultDao.size(); i++) {
						LinkedHashMap<String, Object> commonMap = resultDao.get(i).getCommonMap();
						dataList.add(commonMap);
					}
					LinkedHashMap<String, Object> data=new LinkedHashMap<String, Object>();
					data.put("curPageNum", curPageNum);
					data.put("pageTotal", sourceDao.size());
					
					appCommonData.setData(data);
					appCommonData.setDataList(dataList);
					
					appCommonExternalModulesDto.setData(appCommonData);
				}else {
					appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
					appCommonExternalModulesDto.setResMsg("发布单位和发布人不能为空");
				}

				// 以上业务逻辑层修改
				// 固定返回
			} else {
				appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
				appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
			}
		} catch (Exception e) {
			appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
			appCommonExternalModulesDto.setResMsg(e.getMessage());
		}

		ObjectMapper objectMapper = new ObjectMapper();
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
