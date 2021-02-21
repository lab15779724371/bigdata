package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

public class SendDataWarningMod {

	private static final Logger logger = LogManager.getLogger(SendNotSampleWarningMod.class.getName());
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	ObjectMapper objectMapper = new ObjectMapper();
	// 资源路径
	String fileResPath = "/amSaveUserInfo/";

	public String appModFunc(HttpServletRequest request, Db1Service db1Service, Db2Service db2Service,
			SaasService saasService, DbHiveDishService dbHiveDishService, EduSchoolService eduSchoolService,DbHiveService dbHiveService) {
		String strResp ="";
		String task_category=request.getParameter("taskCategory");
		if("1".equals(task_category)) {
			strResp=new SendLwUnscheduledAlertMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService);
		}else if("2".equals(task_category)) {
			strResp=new SendtodayUnscheduledAlertMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService);
		}else if("3".equals(task_category)) {
			strResp=new SendUnacceptedWarningMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService,eduSchoolService);
		}else if("4".equals(task_category)) {
			strResp=new SendNotSampleWarningMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService,eduSchoolService);
		}else if("5".equals(task_category)) {
			strResp=new SendSteakDataAnomalyWarningMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService,eduSchoolService);
		}else if("6".equals(task_category)) {
			strResp=new SendAcceptanceDataAnomalyWarningMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService,eduSchoolService,dbHiveService);
		}
		return strResp;
	}


}
