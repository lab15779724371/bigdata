package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AddWarnBody;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

public class PushPersonMod {
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
		LinkedHashMap<String, Object> data =new LinkedHashMap<String, Object>();
		// 业务操作
		try {
	  		//授权码
	  		String token =request.getHeader("Authorization");
	  		//验证授权
			boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service,new int[2]);
			if (verTokenFlag) {
				// 以下业务逻辑层修改
				
				TEduBdUserDo userINfo=db2Service.getBdUserInfoByToken(token);
				if(userINfo.getIsAdmin()==1) {
					sourceDao=db2Service.getoperatorList(userINfo.getOrgName());
					for(int i=0;i<sourceDao.size();i++) {
						dataList.add(sourceDao.get(i).getCommonMap());
					}
					
					appCommonExternalModulesDto.setData(dataList);
				}else {
					data.put("userAccount", userINfo.getUserAccount());
					data.put("name", userINfo.getName());
					dataList.add(data);
					appCommonExternalModulesDto.setData(dataList);
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
