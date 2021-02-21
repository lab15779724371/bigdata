package com.tfit.BdBiProcSrvShEduOmc.appmod.im.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.tfit.BdBiProcSrvShEduOmc.appmod.common.SendMailCommon;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.PushReceiverInfo;
import com.tfit.BdBiProcSrvShEduOmc.dao.WarnLevelBody;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.DishEmailDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLicDets;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/** 
 * @Description: 验收模型
 * @Author: jianghy 
 * @Date: 2020/1/14
 * @Time: 15:23       
 */
public class PpCheckDetsTaskMod {
	private static final Logger logger = LogManager.getLogger(PpCheckDetsTaskMod.class.getName());

	//三级排序条件
	final String[] methods = {"getDistName", "getSchType", "getPpName"};
	final String[] sorts = {"asc", "asc", "asc"};
	final String[] dataTypes = {"String", "String", "String"};
	
	
	// 项目点验收详情列表函数（方案2）
	boolean ppDishDetsFromHive(int warnLevel,Db1Service db1Service,Db2Service db2Service,
			DbHiveWarnService dbHiveWarnService,Integer pollTimes) {
		boolean result = false;
		logger.info("=====================================验收"+AppModConfig.warnLevelIdToNameMap.get(warnLevel)+"预警"+"开始");
		
		//BCDTimeUtil.convertDateStrToDate("2020-01-02")
		String currDate = BCDTimeUtil.convertNormalDate(null);
		//截止日期
		String closingDate = "";
		//验收日期
		String dishDate = "";
		//推送日期
		String pushDate = "";
		if (warnLevel == 1){
			currDate+=" 14:00:00";
			//验收日期=当天日期
			dishDate =BCDTimeUtil.getAgoDayDate(0, "yyyy-MM-dd");
			closingDate = BCDTimeUtil.getAgoDayDate(0, "yyyy年MM月dd日")+" 14:00";
			pushDate = closingDate;
		}else if (warnLevel == 2){
			currDate+=" 16:00:00";
			//验收日期=当天日期
			dishDate =BCDTimeUtil.getAgoDayDate(0, "yyyy-MM-dd");
			closingDate = BCDTimeUtil.getAgoDayDate(0, "yyyy年MM月dd日")+" 16:00";
			pushDate = closingDate;
		}else if (warnLevel == 3){
			currDate+=" 17:00:00";
			//验收日期=当天日期
			dishDate =BCDTimeUtil.getAgoDayDate(0, "yyyy-MM-dd");
			closingDate = BCDTimeUtil.getAgoDayDate(0, "yyyy年MM月dd日")+" 17:00";
			pushDate = closingDate;
		}else if (warnLevel == 4){
			currDate+=" 05:00:00";
			//验收日期=当天日期-1天
			dishDate =BCDTimeUtil.getAgoDayDate(1, "yyyy-MM-dd");
			closingDate = BCDTimeUtil.getAgoDayDate(0, "yyyy年MM月dd日")+" 05:00";
			pushDate = BCDTimeUtil.getAgoDayDate(0, "yyyy年MM月dd日")+" 09:00";
		}else if (warnLevel == 5){
			currDate+=" 11:00:00";
			//验收日期=当天日期-1天
			dishDate =BCDTimeUtil.getAgoDayDate(1, "yyyy-MM-dd");
			closingDate = BCDTimeUtil.getAgoDayDate(0, "yyyy年MM月dd日")+" 11:00";
			pushDate = closingDate;
		}
		
		
		
		// 时间段内各区餐厨垃圾学校回收总数
		String startDate = currDate;
		String endDate = currDate;
		if(startDate==null || startDate.split("-").length < 2) { 
    		startDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	if((endDate==null || endDate.split("-").length < 2)&& startDate!=null) {
    		endDate = startDate;
    	}else if (endDate==null || endDate.split("-").length < 2) {
    		endDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	
    	String [] yearMonths = new String [4];
    	//根据开始日期、结束日期，获取开始日期和结束日期的年、月
    	yearMonths = CommonUtil.getYearMonthByDate(startDate, endDate);
    	String startYear = yearMonths[0];
    	String startMonth = yearMonths[1];
    	String endYear = yearMonths[2];
    	String endMonth = yearMonths[3];
    	
		//结束日期+1天，方便查询处理
		String endDateAddOne = CommonUtil.dateAddSecondByFormat(endDate, 1,"yyyy-MM-dd HH:mm:ss");
		//获取开始日期、结束日期的年月集合
		List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);
		
		List<WarnCommonLicDets> warnCommonLicDets = new ArrayList<>();
		warnCommonLicDets = dbHiveWarnService.getWarnLicDetsList(6,null,listYearMonth, startDate,
				endDateAddOne, null, null, -1, -1, -1, 
				-1,null,null, null, null, 
				null,-1, null, null,null,null,
				null,null,null,null,"2",null,
				null, null, null);
		List<DishEmailDto> dataList = new ArrayList<>();
		Map<String,List<DishEmailDto>> dataMap = new HashMap<String,List<DishEmailDto>>();
		int allWarnAccount=0;
		if(warnCommonLicDets != null && warnCommonLicDets.size()>0) {
			//获取管理部门
			List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(null,null, -1, -1);
			 Map<String,String> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,DepartmentObj::getDepartmentName));
			 if(deparmentMap == null) {
				 logger.error("管理部门数据为空");
				 return false;
			 }
			 
			String departmentName = "";
			for(WarnCommonLicDets warnCommon :warnCommonLicDets) {
				if(warnCommon == null) {
					continue;
				}
				//"徐汇区教育局（6所）","幼儿园","盛大幼儿园","9月9日","2019年09月09日 17:00"
				DishEmailDto pdd = new DishEmailDto(warnCommon.getDepartmentId(),warnCommon.getSchTypeId()==null?1000:warnCommon.getSchTypeId(),warnCommon.getSchType(),warnCommon.getSchName(),warnCommon.getDeliveryDate(),closingDate);
				
				departmentName = CommonUtil.isEmpty(warnCommon.getDepartmentId())?"":deparmentMap.get(warnCommon.getDepartmentId());
				dataList = dataMap.get(departmentName);
				if(dataList==null) {
					dataList = new ArrayList<>();
				}
				dataList.add(pdd);
				dataMap.put(departmentName, dataList);
				
				allWarnAccount ++;
			}
			
			List<AppCommonDao> checkWarnSetting  = db2Service.getCheckWarnSetting(2, warnLevel);
			WarnLevelBody warnLevelBody = PpCheckDetsTaskMod.getWarnRuleInfo(checkWarnSetting);
			if (null != warnLevelBody){
				if (warnLevelBody.getScheduledStatus() !=null && warnLevelBody.getScheduledStatus() == 1){
					//是否发送邮件1：发送 2：不发送
					Integer emailStatus = warnLevelBody.getEmailStatus();
					//接收人
					List<PushReceiverInfo> pushReceiverMsg = warnLevelBody.getPushReceiverMsg();
					//接收人不为空
					if(pushReceiverMsg!=null &&pushReceiverMsg.size()>0) {
						//****年**月**日验收/验收信息未上报提示/提醒/预警/督办/追责名单
					    String mailTitle = closingDate.substring(0,closingDate.indexOf(" "))+"验收信息未上报"+AppModConfig.warnLevelIdToNameMap.get(warnLevel)+"名单";
					    String newMailTitle = "";
					    Map<String,List<DishEmailDto>> newDataMap = new HashMap<String,List<DishEmailDto>>();
						for(PushReceiverInfo pushReceiverInfo : pushReceiverMsg) {
							newDataMap = new HashMap<String,List<DishEmailDto>>();
							int warnAccount = 0;
							if(pushReceiverInfo == null || CommonUtil.isEmpty(pushReceiverInfo.getUserAccount())) {
								continue;
							}
							
							newMailTitle = mailTitle;
							
							/**
							 * 发送邮件
							 */
							TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(pushReceiverInfo.getUserAccount());  		
							//是否发送邮件
							if(tebuDo !=null && CommonUtil.isNotEmpty(tebuDo.getEmail())) {
								departmentName = "全市";
								if(tebuDo.getOrgName() !=null && !"市教委".equals(tebuDo.getOrgName())) {
									departmentName = tebuDo.getOrgName();
									dataList = dataMap.get(tebuDo.getOrgName());
									newDataMap.put(departmentName, dataList);
									newMailTitle += "("+departmentName+")";
									if(dataList!=null) {
										warnAccount = dataList.size();
									}
								}else {
									newMailTitle += "(上海市)";
									warnAccount = allWarnAccount;
									newDataMap = dataMap;
								}
								
								
							}
							
							/**
							 * 发送邮件
							 */
							if(emailStatus ==1 ){
								boolean flag = SendMailCommon.sendNoAcceptMail(db2Service, newMailTitle,tebuDo.getEmail(),closingDate,dishDate,departmentName,newDataMap,warnAccount);
								if(!flag) {
									logger.error("发送到"+tebuDo.getEmail()+"失败！");
								}
							}
							/**
							 * 发送通知
							 */
							if(tebuDo !=null) {
								new WarnPushTaskMod().insertPushData(db2Service,warnAccount ,2, warnLevel, newMailTitle, pushDate, tebuDo.getUserAccount(), closingDate, dishDate, departmentName, newDataMap);
							}
						}

					}
					
				}
			}
			return true;
		}else {
			//递归展示去除，代码经过测试，可用，但是为了解决其中一种预警有数据，另外一种预警等满10分钟+的问题
			/*//等待2分钟(防止太频繁)
			try {
				TimeUnit.MINUTES.sleep(2);
				//TimeUnit.SECONDS.sleep(1);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("验收"+AppModConfig.warnLevelIdToNameMap.get(warnLevel)+"预警 "+"轮询第"+pollTimes+"次================================================");
			//轮询10次，仍然未取到数据，停止服务
			if(pollTimes == 5) {
				logger.info("验收"+AppModConfig.warnLevelIdToNameMap.get(warnLevel)+"预警 "+"轮询第"+pollTimes+"次,停止轮询================================================");
				return false;
			}
			pollTimes = pollTimes+1;
			ppDishDetsFromHive(warnLevel, db1Service, db2Service, dbHiveWarnService,pollTimes);*/
			
		}
		
		logger.info("=====================================验收"+AppModConfig.warnLevelIdToNameMap.get(warnLevel)+"预警"+"结束"+pollTimes);
		return result;
	}	

	//项目点验收详情列表模型函数
	public boolean appModFunc(int warnLevel,
			Db1Service db1Service, 
			Db2Service db2Service,
			DbHiveWarnService dbHiveWarnService) {
			return ppDishDetsFromHive(warnLevel, db1Service,db2Service, dbHiveWarnService,0);		
	}


	/** 
	 * @Description: 获取预警配置信息
	 * @Param: [getWarnRuleInfo]
	 * @return: boolean 
	 * @Author: jianghy 
	 * @Date: 2020/1/14
	 * @Time: 14:29       
	 */
	public static WarnLevelBody getWarnRuleInfo(List<AppCommonDao> checkWarnSetting){
		WarnLevelBody wlb = new WarnLevelBody();
		List<PushReceiverInfo> receiverInfos = new ArrayList<>();
		if (!CollectionUtils.isEmpty(checkWarnSetting)){
			AppCommonDao appCommonDao = checkWarnSetting.get(0);
			LinkedHashMap<String, Object> commonMap = appCommonDao.getCommonMap();
			if (null != commonMap){
				Object id = commonMap.get("id");
				if (id != null){
					wlb.setId(id.toString());
				}
				wlb.setUserAccount(String.valueOf(commonMap.get("userAccount")));
				wlb.setWarnType(Integer.valueOf(commonMap.get("warnType").toString()));
				wlb.setWarnAlertType(Integer.valueOf(commonMap.get("warnAlertType").toString()));
				wlb.setScheduledStatus(Integer.valueOf(commonMap.get("scheduledStatus").toString()));
				wlb.setWarnPushContent(commonMap.get("warnPushContent")==null?"":commonMap.get("warnPushContent").toString());
				wlb.setWarnPushTime(commonMap.get("warnPushTime").toString());
				wlb.setWarnDataTime(commonMap.get("warnDataTime").toString());
				wlb.setEmailStatus(commonMap.get("emailStatus")==null?2:Integer.valueOf(commonMap.get("emailStatus").toString()));
				String pushReceiverMsg = String.valueOf(commonMap.get("pushReceiverMsg"));
				if (pushReceiverMsg != null){
					List<String> msgInfo = Arrays.asList(pushReceiverMsg.split("/"));
					if (!CollectionUtils.isEmpty(msgInfo)){
						for (String s : msgInfo) {
							List<String> msg = Arrays.asList(s.split(","));
							PushReceiverInfo pushReceiverInfo = new PushReceiverInfo();
							pushReceiverInfo.setUserAccount(msg.get(0));
							if (msg.size()>1) {
								pushReceiverInfo.setName(msg.get(1));
							}
							if (msg.size()>2){
								pushReceiverInfo.setMobileNo(msg.get(2));
							}
							if (msg.size()>3) {
								pushReceiverInfo.setEmail(msg.get(3));
							}
							receiverInfos.add(pushReceiverInfo);
						}
					}
				}
			}
		}
		wlb.setPushReceiverMsg(receiverInfos);
		return wlb;
	}


}