package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.AddTaskBody;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FileWRCommSys;
import com.tfit.BdBiProcSrvShEduOmc.util.UniqueIdGen;

public class AddTaskMod {
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
			boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service,new int[2]);
			if (verTokenFlag) {

				// Body传输内容，格式为 application/json
				AddTaskBody asuib = new AddTaskBody();
				String strBodyCont = new ToolUtil().GetBodyJsonReq(request, false);
				if (strBodyCont != null)
					asuib = objectMapper.readValue(strBodyCont, AddTaskBody.class);
				if (asuib != null) {
					//筛选参数
					String id=asuib.getId();
					
					// 添加参数
					TEduBdUserDo userINfo=db2Service.getBdUserInfoByToken(token);
					
					LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();
					filterParamMap.put("time", BCDTimeUtil.convertNormalFrom(null));
					filterParamMap.put("title", asuib.getTitle());
					filterParamMap.put("content", asuib.getContent());
					filterParamMap.put("release_unit", userINfo.getOrgName());
					filterParamMap.put("releaser", userINfo.getUserAccount());
					filterParamMap.put("releaser_name", userINfo.getName());
					filterParamMap.put("operation_unit", asuib.getOperationUnit());
					filterParamMap.put("operator", asuib.getOperator());
					filterParamMap.put("operator_name", asuib.getOperatorName());
					
					boolean resultFlag=db2Service.getAddTask(filterParamMap);
					if(resultFlag) {
						appCommonExternalModulesDto.setResMsg("添加成功");
					}else {
						appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
						appCommonExternalModulesDto.setResMsg("添加失败");
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
