package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.PpDishDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AddWarnBody;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.PushRecipientInfo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.impl.Db1ServiceImpl;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

public class SendtodayUnscheduledAlertMod {
	private static final Logger logger = LogManager.getLogger(SendtodayUnscheduledAlertMod.class.getName());
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	ObjectMapper objectMapper = new ObjectMapper();
	// 资源路径
	String fileResPath = "/amSaveUserInfo/";

	public String appModFunc(HttpServletRequest request, Db1Service db1Service, Db2Service db2Service,
			SaasService saasService, DbHiveDishService dbHiveDishService) {
		// 固定Dto层
		AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
		AppCommonData appCommonData = new AppCommonData();
		List<AppCommonDao> sourceDao = null;
		AppCommonDao pageTotal = null;
		List<LinkedHashMap<String, Object>> dataList = new ArrayList();
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
		// 业务操作
		try {
			// 授权码
			String token = request.getHeader("Authorization");
			// 验证授权
			boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service, new int[2]);
			if (verTokenFlag) {
				// 以下业务逻辑层修改

				TEduBdUserDo userINfo=db2Service.getBdUserInfoByToken(token);
				// 任务时间
				String task_time = request.getParameter("taskTime");

				//开始日期
//				String startDate = BCDTimeUtil.convertNormalDate(null);
		        String startDate = task_time.substring(0, 10);
		        //结束日期
//				String endDate = "2019-12-12";
		        String endDate = task_time.substring(0, 10);
				//分页
				if (request.getParameter("page") != null && !request.getParameter("page").toString().isEmpty()) {
					this.curPageNum = Integer.parseInt(request.getParameter("page").toString());
				}
				if (request.getParameter("pageSize") != null && !request.getParameter("pageSize").toString().isEmpty()) {
					this.pageSize = Integer.parseInt(request.getParameter("pageSize").toString());
				}
				Integer startNum = (curPageNum - 1) * pageSize;
				List<AppCommonDao> resultDao=null;
				try {
					sourceDao=db2Service.getTodayUnscheduledAlert(task_time,userINfo.getUserAccount());
					resultDao=sourceDao.subList(startNum, sourceDao.size() >(startNum + pageSize)?(startNum + pageSize):sourceDao.size());
				}catch(Exception e) {
					
				}
				int sortId =startNum;
				for (int i = 0; i < resultDao.size(); i++) {
					sortId +=1;
					LinkedHashMap<String, Object> commonMap = resultDao.get(i).getCommonMap();
					commonMap.put("sortId",sortId);
					dataList.add(commonMap);
				}
				
	            data.put("deadlineTime", task_time.substring(0,16));
	            data.put("periodTime", startDate.replace("-", "/")+"-"+endDate.replace("-", "/"));
	            data.put("department", userINfo.getOrgName());
	            data.put("schoolNum", sourceDao==null?0:sourceDao.size());
	            data.put("pageTotal", sourceDao==null?0:sourceDao.size());
	            data.put("curPageNum", curPageNum);
				
	            appCommonData.setData(data);
	            appCommonData.setDataList(dataList);
				appCommonExternalModulesDto.setData(appCommonData);
				
	            //更新状态为已读
	            db2Service.getUpdateSysInfo("2",userINfo.getUserAccount(),task_time);
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

		String strResp = null;
		try {
			strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
			strResp = new ToolUtil().rmExternalStructure(strResp, "sendMsgList");
		} catch (Exception e) {
			strResp = new ToolUtil().getInitJson();
		}
		return strResp;
	}
}
