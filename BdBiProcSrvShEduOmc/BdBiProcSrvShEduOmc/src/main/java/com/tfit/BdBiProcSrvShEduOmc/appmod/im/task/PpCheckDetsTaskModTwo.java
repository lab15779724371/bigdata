package com.tfit.BdBiProcSrvShEduOmc.appmod.im.task;

import com.tfit.BdBiProcSrvShEduOmc.appmod.common.SendMailCommon;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.PushReceiverInfo;
import com.tfit.BdBiProcSrvShEduOmc.dao.WarnLevelBody;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.DishEmailDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLicDets;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.*;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/** 
 * @Description: 验收模型
 * @Author: jianghy 
 * @Date: 2020/1/14
 * @Time: 15:23       
 */
public class PpCheckDetsTaskModTwo {
	private static final Logger logger = LogManager.getLogger(PpCheckDetsTaskModTwo.class.getName());

	/**
	 * @Description: 验收推送操作
	 * @Param: [db1Service, db2Service, saasService, dbHiveCheckWarnService, dbHiveWarnService, dbHiveService, type]
	 * @return: void
	 * @Author: jianghy
	 * @Date: 2020/1/14
	 * @Time: 16:00
	 */
	public void appModFunc(Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveCheckWarnService dbHiveCheckWarnService,DbHiveWarnService dbHiveWarnService,DbHiveService dbHiveService,Integer type) {
		// 业务操作
		try {
			//根据类型查询预警配置信息
			List<AppCommonDao> checkWarnSetting = null;
			if (type == 1){
				checkWarnSetting = db2Service.getCheckWarnSetting(2, 1);
			}else if (type == 2){
				checkWarnSetting = db2Service.getCheckWarnSetting(2, 2);
			}else if (type == 3){
				checkWarnSetting = db2Service.getCheckWarnSetting(2, 3);
			}else if (type == 4){
				checkWarnSetting = db2Service.getCheckWarnSetting(2, 4);
			}else if (type == 5){
				checkWarnSetting = db2Service.getCheckWarnSetting(2, 5);
			}
			doPushData(checkWarnSetting,dbHiveWarnService,db1Service,db2Service,type,0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * @Description: 执行推送操作
	 * @Param: [checkWarnSetting, dbHiveWarnService]
	 * @return: void
	 * @Author: jianghy
	 * @Date: 2020/1/14
	 * @Time: 15:54
	 */
	public void doPushData(List<AppCommonDao> checkWarnSetting,DbHiveWarnService dbHiveWarnService,Db1Service db1Service,Db2Service db2Service,Integer type,Integer pollTimes){
		WarnLevelBody warnLevelBody = getWarnRuleInfo(checkWarnSetting);
		if (null != warnLevelBody){
			//如果定时器开启
			if (warnLevelBody.getScheduledStatus() == 1){
				//获取数据时间
				String warnDataTime = warnLevelBody.getWarnDataTime();
				//获取日期集合
				Map<String, Object> dateMap = getDateMap(warnDataTime, type);
				List<String> listYearMonth = (List<String>)dateMap.get("listYearMonth");
				String startDate = String.valueOf(dateMap.get("startDate"));
				String endDateAddOne = String.valueOf(dateMap.get("endDateAddOne"));
				String dishDate = String.valueOf(dateMap.get("dishDate"));
				String searchEndDate = String.valueOf(dateMap.get("searchEndDate"));
				//今天日期（年月日）
				String todayDate2 = String.valueOf(dateMap.get("todayDate2"));

				//获取数据
				List<WarnCommonLicDets> warnLicDetsList = dbHiveWarnService.getWarnLicDetsList(6, null, listYearMonth, startDate,
						endDateAddOne, null, null, -1, -1, -1,
						-1, null, null, null, null,
						null, -1, null, null, null, null,
						null, null, null, null, "2",null,
						-1, -1, null);
                List<DishEmailDto> dataList = new ArrayList<>();
                //获取管理部门
                List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(null,null, -1, -1);
                Map<String,String> deparmentMap = deparmentList.stream().collect(Collectors.toMap(DepartmentObj::getDepartmentId,DepartmentObj::getDepartmentName));
                if(deparmentMap == null) {
                    logger.error("管理部门数据为空");
                    return;
                }
                Map<String,List<DishEmailDto>> dataMap = new HashMap<>();
                int allWarnAccount=0;
                if(!CollectionUtils.isEmpty(warnLicDetsList)) {
                    String departmentName = "";
                    for(WarnCommonLicDets warnCommon : warnLicDetsList) {
                        //"徐汇区教育局（6所）","幼儿园","盛大幼儿园","9月9日","2019年09月09日 17:00"
                        departmentName = CommonUtil.isEmpty(warnCommon.getDepartmentId())?"":deparmentMap.get(warnCommon.getDepartmentId());
						DishEmailDto pdd = new DishEmailDto(departmentName,warnCommon.getSchTypeId(),warnCommon.getSchType(),warnCommon.getSchName(),warnCommon.getDeliveryDate(),searchEndDate);
						dataList = dataMap.get(departmentName);
                        if(dataList==null) {
                            dataList = new ArrayList<>();
                        }
                        dataList.add(pdd);
                        dataMap.put(departmentName, dataList);
                        allWarnAccount ++;
                    }
                }else {
                    //等待1分钟(防止太频繁)
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
					logger.info("验收"+AppModConfig.warnLevelIdToNameMap.get(type)+"预警 "+"轮询第"+pollTimes+"次================================================");
					//轮询10次，仍然未取到数据，停止服务
					if(pollTimes == 1) {
						logger.info("验收"+AppModConfig.warnLevelIdToNameMap.get(type)+"预警 "+"轮询第"+pollTimes+"次,停止轮询================================================");
						return;
					}
					pollTimes = pollTimes+1;
                    doPushData(checkWarnSetting,dbHiveWarnService,db1Service,db2Service,type,pollTimes);
                }

                //是否发送邮件1：发送 2：不发送
                Integer emailStatus = warnLevelBody.getEmailStatus();
                if (emailStatus == 1){
                    //接收人信息
                    List<PushReceiverInfo> pushReceiverMsg = warnLevelBody.getPushReceiverMsg();
                    if (!CollectionUtils.isEmpty(pushReceiverMsg)){
                        String mailTitle = todayDate2+"验收信息未上报"+ AppModConfig.warnLevelIdToNameMap.get(type)+"名单";
                        for(PushReceiverInfo pushReceiverInfo : pushReceiverMsg) {
							int warnAccount = 0;
							String newMailTitle = mailTitle;
                            //根据账号信息查询个人信息
                            TEduBdUserDo tebuDo = db2Service.getBdUserInfoByUserName(pushReceiverInfo.getUserAccount());
                            if(tebuDo != null && CommonUtil.isNotEmpty(tebuDo.getEmail())) {
                                String departmentName = "全市";
                                if(tebuDo.getOrgName() !=null && !"市教委".equals(tebuDo.getOrgName())) {
                                    departmentName = tebuDo.getOrgName();
									dataList = dataMap.get(tebuDo.getOrgName());
									dataMap= new HashMap<>();
									dataMap.put(departmentName, dataList);
									newMailTitle += "("+departmentName+")";
									if(dataList!=null) {
										warnAccount = dataList.size();
									}
                                }else {
									newMailTitle += "(上海市)";
									warnAccount = allWarnAccount;
								}
                                boolean flag = SendMailCommon.sendNoAcceptMail(db2Service, newMailTitle,tebuDo.getEmail(),searchEndDate,dishDate,departmentName,dataMap,warnAccount);
                                if(!flag) {
                                    logger.error("发送到"+tebuDo.getEmail()+"失败！");
                                }
                            }
                        }
                    }
                }
			}
		}
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


	/**
	 * @Description: 获取需要的日期map
	 * @Param: [pushTime, type]
	 * @return: java.util.Map<java.lang.String,java.lang.Object>
	 * @Author: jianghy
	 * @Date: 2020/1/14
	 * @Time: 16:09
	 */
	public Map<String,Object> getDateMap(String pushTime,Integer type){
		Map<String,Object> map = new HashedMap();
		List<String> listYearMonth;
		//今天时间（yyyy-MM-dd）
        String todayDate = BCDTimeUtil.convertNormalDate(null);
        //昨日时间（yyyy-MM-dd）
        String yestdayDate = BCDTimeUtil.getAgoDayDate(1, "yyyy-MM-dd");
        //查询开始时间
        String startDateTime = "";
        //供餐日
        String dishDate = "";
        String [] yearMonths = new String[4];
        if (type == 1){
            startDateTime = todayDate +" 00:00:00";
            dishDate = BCDTimeUtil.getAgoDayDate(1, "yyyy/MM/dd");
            //根据开始日期、结束日期，获取开始日期和结束日期的年、月
            yearMonths = CommonUtil.getYearMonthByDate(todayDate, todayDate);
        }else if (type == 2){
            startDateTime = todayDate +" 00:00:00";
            dishDate = BCDTimeUtil.getAgoDayDate(1, "yyyy/MM/dd");
            yearMonths = CommonUtil.getYearMonthByDate(todayDate, todayDate);
        }else if (type == 3){
            startDateTime = todayDate +" 00:00:00";
            dishDate = BCDTimeUtil.getAgoDayDate(1, "yyyy/MM/dd");
            yearMonths = CommonUtil.getYearMonthByDate(todayDate, todayDate);
        }else if (type == 4){
            startDateTime = yestdayDate +" 00:00:00";
            dishDate = BCDTimeUtil.getAgoDayDate(2, "yyyy/MM/dd");
            yearMonths = CommonUtil.getYearMonthByDate(yestdayDate, todayDate);
        }else if (type == 5){
            startDateTime = yestdayDate +" 00:00:00";
            dishDate = BCDTimeUtil.getAgoDayDate(2, "yyyy/MM/dd");
            yearMonths = CommonUtil.getYearMonthByDate(yestdayDate, todayDate);
        }
        //当前时间（年月日）
		String todayDate2 = BCDTimeUtil.getAgoDayDate(0, "yyyy年MM月dd日");
		//截至日期
		String searchEndDate = todayDate2+" "+pushTime;

        //查询结束时间
        String endDateTime = todayDate +" "+ pushTime + ":01";
		String startYear = yearMonths[0];
		String startMonth = yearMonths[1];
		String endYear = yearMonths[2];
		String endMonth = yearMonths[3];
		//获取开始日期、结束日期的年月集合
		listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);
		map.put("listYearMonth",listYearMonth);
		map.put("startDate",startDateTime);
		map.put("endDateAddOne",endDateTime);
		map.put("dishDate",dishDate);
		map.put("searchEndDate",searchEndDate);
		map.put("todayDate2",todayDate2);
		return map;
	}
}