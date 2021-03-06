package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
public class TaskListMod {
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
				// 筛选参数
				TEduBdUserDo userINfo=db2Service.getBdUserInfoByToken(token);
				
				LinkedHashMap<String, Object> filterParamMap=new LinkedHashMap<String, Object>();
				filterParamMap.put("operation_unit",userINfo.getOrgName());
				filterParamMap.put("operator",userINfo.getUserAccount());
				
				filterParamMap.put("startDate",request.getParameter("startDate"));
				filterParamMap.put("endDate",request.getParameter("endDate"));
				filterParamMap.put("release_unit",request.getParameter("releaseUnit"));
				filterParamMap.put("status",request.getParameter("status"));
				
				Integer startNum = (curPageNum - 1) * pageSize;
				
				if(filterParamMap.get("operation_unit") !=null && filterParamMap.get("operator") !=null){
					sourceDao = db2Service.getTaskList(filterParamMap);
					
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
					appCommonExternalModulesDto.setResMsg("承办单位或者承办人不能为空");
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
