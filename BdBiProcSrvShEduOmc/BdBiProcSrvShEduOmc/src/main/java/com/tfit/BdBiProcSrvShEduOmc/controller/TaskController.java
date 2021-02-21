package com.tfit.BdBiProcSrvShEduOmc.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.annotation.CheckUserToken;
import com.tfit.BdBiProcSrvShEduOmc.appmod.bd.BdTemplateAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.pn.RbUlAttachmentAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.AddAffairMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.AddTaskMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.AffairDetailMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.AffairEduListMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.AffairListMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.CompleteAffairMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.CompleteTaskMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.DataAnomalyWarningMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.DeleteMyaffairMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.DeleteTaskMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.LevelWarningMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.LwUnscheduledAlertMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.MyApplyListMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.MyReleaseListMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.NoReadInfoNumMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.NotSampleWarningMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.ScheduleTaskMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.SendDataWarningMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.SysInfoDisplayMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.TaskDetailMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.TaskListMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.TodayUnscheduledAlertMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.UnacceptedWarningMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.UpdateMyaffairMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.UpdateTaskMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.eduListMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.task.operatorListMod;
import com.tfit.BdBiProcSrvShEduOmc.common.ApiResponse;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.WarnLevelBody;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTHttpRspVO;
import com.tfit.BdBiProcSrvShEduOmc.dto.pn.RbUlAttachmentDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdTemplate;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

//任务中心
@RestController
@RequestMapping(value = "/biOptAnl")
public class TaskController {
	private static final Logger logger = LogManager.getLogger(TaskController.class.getName());
	/**
    * mysql主数据库服务
    */
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
    * hive数据库服务
    */
   @Autowired
   DbHiveWarnService dbHiveWarnService;
   
   /**
    * hive数据库服务
    */
   @Autowired
   DbHiveRecyclerWasteService dbHiveRecyclerWasteService;
   
   /**
    * hive数据库服务
    */
   @Autowired
   DbHiveDishService dbHiveDishService;
   
   /**
    * hive数据库服务
    */
   @Autowired
   DbHiveMatService dbHiveMatService;
   
   /**
    * hive数据库服务
    */
   @Autowired
   DbHiveGsService dbHiveGsService;
   
   /**
    * hive数据库服务
    */
   @Autowired
   DbHiveService dbHiveService;
   
   /**
    * 教委系统学校信息服务
    */
   @Autowired
   private EduSchoolService eduSchoolService;

	@Autowired
	ObjectMapper objectMapper;

	/** 任务中心 **/
	// 1.1 添加任务
	@RequestMapping(value = "/v1/addTask", method = RequestMethod.POST)
	public String v1_addTask(HttpServletRequest request) {
		return new AddTaskMod().appModFunc(request, db1Service, db2Service);
	}

	// 1.2 任务列表
	@RequestMapping(value = "/v1/taskList", method = RequestMethod.GET)
	public String v1_taskList(HttpServletRequest request) {
		return new TaskListMod().appModFunc(request, db1Service, db2Service);
	}

	// 1.2.1 任务办结
	@RequestMapping(value = "/v1/completeTask", method = RequestMethod.POST)
	public String v1_completeTask(HttpServletRequest request) {
		return new CompleteTaskMod().appModFunc(request, db1Service, db2Service);
	}

	//1.2.2 任务详情
	@RequestMapping(value = "/v1/taskDetail", method = RequestMethod.GET)
	public String v1_taskDetail(HttpServletRequest request) {
		return new TaskDetailMod().appModFunc(request, db1Service, db2Service);
	}
	
	// 1.3 我的发布
	@RequestMapping(value = "/v1/myReleaseList", method = RequestMethod.GET)
	public String v1_myReleaseList(HttpServletRequest request) {
		return new MyReleaseListMod().appModFunc(request, db1Service, db2Service);
	}

	// 1.3.1 修改我发布的任务
	@RequestMapping(value = "/v1/updateTask", method = RequestMethod.POST)
	public String v1_updateTask(HttpServletRequest request) {
		return new UpdateTaskMod().appModFunc(request, db1Service, db2Service);
	}

	// 1.3.2 删除我发布的任务
	@RequestMapping(value = "/v1/deleteTask", method = RequestMethod.GET)
	public String v1_deleteTask(HttpServletRequest request) {
		return new DeleteTaskMod().appModFunc(request, db1Service, db2Service);
	}
	
	//1.4  市/区教育局下拉列表
	@RequestMapping(value = "/v1/eduList", method = RequestMethod.GET)
	public String v1_eduList(HttpServletRequest request) {
		return new eduListMod().appModFunc(request, db1Service, db2Service);
	}
	
	//1.4.1 根据市区,获取承办人列表
	@RequestMapping(value = "/v1/operatorList", method = RequestMethod.GET)
	public String v1_operatorList(HttpServletRequest request) {
		return new operatorListMod().appModFunc(request, db1Service, db2Service);
	}
	

	/** 事务申办 **/
	
	// 2.1 申办列表
	@RequestMapping(value = "/v1/affairList", method = RequestMethod.GET)
	public String v1_affairList(HttpServletRequest request) {
		return new AffairListMod().appModFunc(request, db1Service, db2Service);
	}
	
	// 2.2 事务申办
	@RequestMapping(value = "/v1/addAffair", method = RequestMethod.POST)
	public String v1_addAffair(HttpServletRequest request) {
		return new AddAffairMod().appModFunc(request, db1Service, db2Service);
	}

	// 2.2.1 事务附件文件上传
	@RequestMapping(value = "/v1/affairRbUlAttachment", method = RequestMethod.POST)
	public String v1_rbUlAttachment(HttpServletRequest request) {
		// 初始化响应数据
		String strResp = null;
		RbUlAttachmentDTO ruaDto = null;
		boolean isAuth = false;
		int code = 0;
		int[] codes = new int[1];
		// 授权码
		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
		// 文件名
		String fileName = request.getParameter("fileName");
		logger.info("输入参数：" + "token = " + token + ", fileName = " + fileName);
		// 验证授权
		isAuth = AppModConfig.verifyAuthCode2(token, db2Service, codes);
		// 通知详情应用模型函数
		if (isAuth)
			ruaDto = new RbUlAttachmentAppMod().appModFunc(fileName, request);
		else
			logger.info("授权码：" + token + "，验证失败！");
		// 设置响应数据
		if (ruaDto != null) {
			try {
				strResp = objectMapper.writeValueAsString(ruaDto);
				logger.info(strResp);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// 获取异常响应
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

	//2.3 申办详情
	@RequestMapping(value = "/v1/affairDetail", method = RequestMethod.GET)
	public String v1_affairDetail(HttpServletRequest request) {
		return new AffairDetailMod().appModFunc(request, db1Service, db2Service);
	}
	
	//2.4 事务办结
	@RequestMapping(value = "/v1/completeAffair", method = RequestMethod.POST)
	public String v1_completeAffair(HttpServletRequest request) {
		return new CompleteAffairMod().appModFunc(request, db1Service, db2Service);
	}
	
	// 2.5 我的申办
	@RequestMapping(value = "/v1/myApplyList", method = RequestMethod.GET)
	public String v1_myApplyList(HttpServletRequest request) {
		return new MyApplyListMod().appModFunc(request, db1Service, db2Service);
	}
	
	//2.6 修改我的申办
	@RequestMapping(value = "/v1/updateMyaffair", method = RequestMethod.POST)
	public String v1_updateMyaffair(HttpServletRequest request) {
		return new UpdateMyaffairMod().appModFunc(request, db1Service, db2Service);
	}
	
	//2.7 删除我的申办
	@RequestMapping(value = "/v1/deleteMyaffair", method = RequestMethod.GET)
	public String v1_deleteMyaffair(HttpServletRequest request) {
		return new DeleteMyaffairMod().appModFunc(request, db1Service, db2Service);
	}
	
	//2.8  市/区教育局下拉列表
	@RequestMapping(value = "/v1/affairEduList", method = RequestMethod.GET)
	public String v1_affairEduList(HttpServletRequest request) {
		return new AffairEduListMod().appModFunc(request, db1Service, db2Service);
	}
	
	//定时任务
    //@Scheduled(cron = "1 * * * * *")
    public void timedTask(){
    	logger.info("springtask 定时任务！"+ new ToolUtil().currentTime());
    	new ScheduleTaskMod().appModFunc(db1Service, db2Service, saasService,dbHiveDishService,eduSchoolService,dbHiveService);
    }

	/** 预警信息设置 **/
	//3.1 下周未排菜预警
	@RequestMapping(value = "/v1/lwUnscheduledAlert", method = RequestMethod.POST)
	public String v1_lwUnscheduledAlert(HttpServletRequest request) {
		return new LwUnscheduledAlertMod().appModFunc(request, db1Service, db2Service);
	}
	//3.2 当日未排菜预警
	@RequestMapping(value = "/v1/todayUnscheduledAlert", method = RequestMethod.POST)
	public String v1_todayUnscheduledAlert(HttpServletRequest request) {
		return new TodayUnscheduledAlertMod().appModFunc(request, db1Service, db2Service);
	}
	//3.3 未验收预警
	@RequestMapping(value = "/v1/unacceptedWarning", method = RequestMethod.POST)
	public String v1_unacceptedWarning(HttpServletRequest request) {
		return new UnacceptedWarningMod().appModFunc(request, db1Service, db2Service);
	}
	//3.4 未留样预警
	@RequestMapping(value = "/v1/notSampleWarning", method = RequestMethod.POST)
	public String v1_notSampleWarning(HttpServletRequest request) {
		return new NotSampleWarningMod().appModFunc(request, db1Service, db2Service);
	}
	//3.5 数据异常预警
	@RequestMapping(value = "/v1/dataAnomalyWarning", method = RequestMethod.POST)
	public String v1_dataAnomalyWarning(HttpServletRequest request) {
		return new DataAnomalyWarningMod().appModFunc(request, db1Service, db2Service);
	}
	
	/** 消息展示接口 **/
	//4.0 消息展示接口
	@RequestMapping(value = "/v1/sysInfoDisplay", method = RequestMethod.GET)
	public String v1_sysInfoDisplay(HttpServletRequest request) {
		return new SysInfoDisplayMod().appModFunc(request, db1Service, db2Service);
	}
	
	//4.0.1 未读信息数
	@RequestMapping(value = "/v1/noReadInfoNum", method = RequestMethod.GET)
	public String v1_noReadInfoNum(HttpServletRequest request) {
		return new NoReadInfoNumMod().appModFunc(request, db1Service, db2Service);
	}
	
	/** 发送信息**/
//	//4.1 发送下周未排菜预警数据
//	@RequestMapping(value = "/v1/sendLwUnscheduledAlert", method = RequestMethod.GET)
//	public String v1_sendLwUnscheduledAlert(HttpServletRequest request) {
//		return new SendLwUnscheduledAlertMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService);
//	}
//	
//	//4.2 发送当日未排菜预警
//	@RequestMapping(value = "/v1/sendTodayUnscheduledAlert", method = RequestMethod.GET)
//	public String v1_sendTodayUnscheduledAlert(HttpServletRequest request) {
//		return new SendtodayUnscheduledAlertMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService);
//	}
//	
//	//4.3 发送当日未验收预警
//	@RequestMapping(value = "/v1/sendUnacceptedWarning", method = RequestMethod.GET)
//	public String v1_sendUnacceptedWarning(HttpServletRequest request) {
//		return new SendUnacceptedWarningMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService,eduSchoolService);
//	}
//	
//	//4.4 发送当日未留样预警
//	@RequestMapping(value = "/v1/sendNotSampleWarning", method = RequestMethod.GET)
//	public String v1_sendNotSampleWarning(HttpServletRequest request) {
//		return new SendNotSampleWarningMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService,eduSchoolService);
//	}
//	
//	//4.5 发送当日排菜异常预警
//	@RequestMapping(value = "/v1/sendSteakDataAnomalyWarning", method = RequestMethod.GET)
//	public String v1_sendSteakDataAnomalyWarning(HttpServletRequest request) {
//		return new SendSteakDataAnomalyWarningMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService,eduSchoolService);
//	}
//	
//	//4.6 发送当日验收异常预警
//	@RequestMapping(value = "/v1/sendAcceptanceDataAnomalyWarning", method = RequestMethod.GET)
//	public String v1_sendAcceptanceDataAnomalyWarning(HttpServletRequest request) {
//		return new SendAcceptanceDataAnomalyWarningMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService,eduSchoolService);
//	}
	//4.7 发送数据
	@RequestMapping(value = "/v1/sendDataWarning", method = RequestMethod.GET)
	public String v1_sendDataWarning(HttpServletRequest request) {
		return new SendDataWarningMod().appModFunc(request, db1Service, db2Service, saasService,dbHiveDishService,eduSchoolService,dbHiveService);
	}

	/**
	 * @Description: 设置预警规则
	 * @Param: [request]
	 * @return: java.lang.String
	 * @Author: jianghy
	 * @Date: 2020/1/13
	 * @Time: 21:29
	 */
	@RequestMapping(value = "/v1/setWarnRuleByLevel", method = RequestMethod.POST)
	public String setWarnRuleByLevel(HttpServletRequest request,@RequestBody WarnLevelBody wlb) {
		return new LevelWarningMod().appModFunc(request,wlb,db1Service, db2Service);
	}

	/**
	 * @Description: 获取预警规则
	 * @Param: [request]
	 * @return: java.lang.String
	 * @Author: jianghy
	 * @Date: 2020/1/13
	 * @Time: 21:29
	 */
	@RequestMapping(value = "/v1/getWarnRuleSetting", method = RequestMethod.POST)
	public String getWarnRuleSetting(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
		return new LevelWarningMod().getWarnRuleSetting(request,jsonObject,db1Service, db2Service);
	}

	/**
	 * 获取预警模板列表
	 * @param request
	 * @return
	 */
	@CheckUserToken
	@RequestMapping(value = "/v1/listTemplate", method = RequestMethod.GET)
	public ApiResponse<List<TEduBdTemplate>> listTemplate(HttpServletRequest request) {
		return new BdTemplateAppMod().listTemplate(saasService);
	}
	
	/**
	 * 获取预警模板列表
	 * @param request
	 * @return
	 */
	@CheckUserToken
	@RequestMapping(value = "/v1/getTemplate", method = RequestMethod.GET)
	public ApiResponse<TEduBdTemplate> getTemplate(HttpServletRequest request,@RequestParam String id) {
		return new BdTemplateAppMod().getTemplate(id,saasService);
	}
	
	/**
	 * 保存预警模板
	 * @param request
	 * @return
	 */
	@CheckUserToken
	@RequestMapping(value = "/v1/saveTemplate", method = RequestMethod.POST)
	public ApiResponse<T> saveTemplate(HttpServletRequest request,@RequestBody TEduBdTemplate record) {
		String token = AppModConfig.GetHeadJsonReq(request, "Authorization");
		return new BdTemplateAppMod().saveTemplate(token,record, saasService,db2Service);
	}
	
	/**
	 * 保存预警模板
	 * @param request
	 * @return
	 */
	@CheckUserToken
	@RequestMapping(value = "/v1/updateTemplate", method = RequestMethod.POST)
	public ApiResponse<T> updateTemplate(HttpServletRequest request,@RequestBody TEduBdTemplate record) {
		return new BdTemplateAppMod().updateTemplate(record, saasService);
	}
	
	/**
	 * 保存预警模板
	 * @param request
	 * @return
	 */
	@CheckUserToken
	@RequestMapping(value = "/v1/deleteTemplate", method = RequestMethod.POST)
	public ApiResponse<T> deleteTemplate(HttpServletRequest request,@RequestParam String id) {
		return new BdTemplateAppMod().deleteTemplate(id, saasService);
	}

}
