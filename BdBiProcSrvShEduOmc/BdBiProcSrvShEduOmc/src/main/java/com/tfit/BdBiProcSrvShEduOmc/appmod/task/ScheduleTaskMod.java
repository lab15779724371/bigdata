package com.tfit.BdBiProcSrvShEduOmc.appmod.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.ga.CaMatSupDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.PpDishDetsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.PpGsPlanOptsAppMod;
import com.tfit.BdBiProcSrvShEduOmc.appmod.im.PpRetSamplesAppMod;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AddWarnBody;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.PushRecipientInfo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpGsPlanOpts;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpGsPlanOptsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpRetSamples;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpRetSamplesDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.service.impl.Db1ServiceImpl;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

public class ScheduleTaskMod {
	private static final Logger logger = LogManager.getLogger(ScheduleTaskMod.class.getName());
	// 是否为真实数据标识
	private static boolean isRealData = true;

	// 页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	ObjectMapper objectMapper = new ObjectMapper();
	// 资源路径
	String fileResPath = "/amSaveUserInfo/";
	
	public void appModFunc(Db1Service db1Service, Db2Service db2Service,
			SaasService saasService, DbHiveDishService dbHiveDishService, EduSchoolService eduSchoolService,DbHiveService dbHiveService) {
		// 固定Dto层
		AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
		AppCommonData appCommonData = new AppCommonData();
		List<AppCommonDao> sourceDao = null;
		AppCommonDao pageTotal = null;
//		List<LinkedHashMap<String, Object>> dataList = new ArrayList();
		LinkedHashMap<String, Object> data =new LinkedHashMap<String, Object>();
		// 业务操作
		try {
			
			String nowTime=DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
			sourceDao=db2Service.getScheduleTask();
			//站内消息推送
			for(int i=0;i<sourceDao.size();i++) {
				String adminUser=(String) sourceDao.get(i).getCommonMap().get("userAccount");
				String category=(String) sourceDao.get(i).getCommonMap().get("category");
				String unscheduled=(String) sourceDao.get(i).getCommonMap().get("unscheduled");
				String week=(String) sourceDao.get(i).getCommonMap().get("week");
				String time=(String) sourceDao.get(i).getCommonMap().get("time");
				String frequency=(String) sourceDao.get(i).getCommonMap().get("frequency");
				String interval=(String) sourceDao.get(i).getCommonMap().get("interval");
				String pushRecipient=(String) sourceDao.get(i).getCommonMap().get("pushRecipient");
				String emailRecipient=(String) sourceDao.get(i).getCommonMap().get("emailRecipient");
				TEduBdUserDo teduBdUserDo=db2Service.getBdUserInfoByUserAccount(adminUser);
				String orgName=teduBdUserDo.getOrgName();
				
				String fullTime=nowTime.substring(0,10)+" "+time;
				List<String> fullTimeArr=new ToolUtil().fullTimeList(fullTime, frequency, interval);
				String dishesWarn=(String) sourceDao.get(i).getCommonMap().get("dishesWarn");
				String deliveryWarn=(String) sourceDao.get(i).getCommonMap().get("deliveryWarn");
				int nowWeek=((Calendar.getInstance().get(Calendar.DAY_OF_WEEK))-1)==0?7:((Calendar.getInstance().get(Calendar.DAY_OF_WEEK))-1);
//				String fullTime=nowTime; fullTimeArr.contains(nowTime.substring(0,16));nowTime.substring(0,16).equals(fullTime.substring(0,16))
				if("1".equals(unscheduled) && fullTimeArr.contains(nowTime.substring(0,16)) && pushRecipient !=null && !pushRecipient.trim().isEmpty()) {
					String[] pushRecipientArr=pushRecipient.split("\\|");
					for(int j=0;j<pushRecipientArr.length;j++) {
						String[] midRecipientArr=(pushRecipientArr[j]).replace("(", "").replace(")", "").split(",");
						if("1".equals(midRecipientArr[2])) {
							String pushUserAccount=midRecipientArr[0];
							TEduBdUserDo teduBdUserDo1=db2Service.getBdUserInfoByUserAccount(pushUserAccount);
								String content="";
								if("1".equals(category) && Integer.parseInt(week) == nowWeek) {
									content="[预警信息] "+nowTime.substring(0,10)+" 下周未排菜学校名单";
							        //获取下周7天所有未排菜数据
									PpDishDetsAppMod pddAppMod = new PpDishDetsAppMod();
									String token=teduBdUserDo1.getToken();
									//开始日期
//									String startDate = BCDTimeUtil.convertNormalDate(null);
							        String startDate = (new ToolUtil().getNextWeekMonday(nowTime)).substring(0,10);
							        //结束日期
//									String endDate = "2019-12-12";
							        String endDate = CommonUtil.dateAddDay(startDate, 6);
							        //是否排菜，0:未排菜，1:已排菜
							        String dishFlag = "0";
							        //是否供餐，0:否，1:是
							        String mealFlag = "1";
							        PpDishDetsDTO pddDto=null;
									pddDto = pddAppMod.appModFunc(token, startDate, endDate, null, null, null, 
						            		null, null, null, null, null, null, null, dishFlag, 
						            		null, null, mealFlag, null, null, 
						            		null,null,null,null,null,null,null,
						            		null,null,null,null,null,
						            		"1", "30000000", 
						            		db1Service, db2Service, saasService,dbHiveDishService);
									List<PpDishDets> ppDishDets=pddDto.getPpDishDets();
									List<LinkedHashMap<String, Object>> sevenData = new ArrayList();
						            if(ppDishDets !=null) {
							            for(int n=0;n<ppDishDets.size();n++) {
							            	LinkedHashMap<String, Object> dataMap =new LinkedHashMap<String, Object>();
							            	dataMap.put("taskTime", nowTime);
							            	dataMap.put("pushUserAccount", pushUserAccount);
							            	dataMap.put("dishDate", ppDishDets.get(n).getDishDate());
							            	dataMap.put("ppName", ppDishDets.get(n).getPpName());
							            	dataMap.put("schType", ppDishDets.get(n).getSchType());
							            	dataMap.put("compDep", new ToolUtil().departmentName(ppDishDets.get(n).getDepartmentId()));
							            	dataMap.put("num", 1);
							            	sevenData.add(dataMap);
							            }
							            List<LinkedHashMap<String, Object>> PaginationList=mapGroupBy(sevenData,"ppName","schType","compDep","num");
							            db2Service.getInsertLwUnscheduledAlert(PaginationList);
						            }
						           
								}else if("2".equals(category)) {
									content="[预警信息] "+nowTime.substring(0,10)+" 当日未排菜学校名单";
									// 任务时间
									String task_time = nowTime;

									// 开始日期
									// String startDate = BCDTimeUtil.convertNormalDate(null);
									String startDate = task_time.substring(0, 10);
									// 结束日期
									// String endDate = "2019-12-12";
									String endDate = task_time.substring(0, 10);
									// 是否排菜，0:未排菜，1:已排菜
									String dishFlag = "0";
									// 是否供餐，0:否，1:是
									String mealFlag = "1";
									String token=teduBdUserDo1.getToken();
									PpDishDetsAppMod pddAppMod = new PpDishDetsAppMod();
									PpDishDetsDTO pddDto = null;
									pddDto = pddAppMod.appModFunc(token, startDate, endDate, null, null, null, null, null, null, null, null,
											null, null, dishFlag, null, null, mealFlag, null, null, null, null, null, null, null, null,
											null, null, null, null, null, null, "1", "30000000", db1Service, db2Service, saasService,
											dbHiveDishService);
									List<PpDishDets> ppDishDets = pddDto.getPpDishDets();
									List<LinkedHashMap<String, Object>> dataList = new ArrayList();
									if (ppDishDets != null) {
										for (int n = 0; n < ppDishDets.size(); n++) {
											LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
							            	dataMap.put("taskTime", nowTime);
							            	dataMap.put("pushUserAccount", pushUserAccount);
											dataMap.put("dishDate", ppDishDets.get(n).getDishDate());
											dataMap.put("ppName", ppDishDets.get(n).getPpName());
											dataMap.put("schType", ppDishDets.get(n).getSchType());
											dataMap.put("compDep", new ToolUtil().departmentName(ppDishDets.get(n).getDepartmentId()));
											dataMap.put("num", 1);
											dataList.add(dataMap);
										}
							            db2Service.getInsertTodayUnscheduledAlert(dataList);
									}
								}else if("3".equals(category)) {
									content="[预警信息] "+nowTime.substring(0,10)+" 未验收学校名单";
									String token=teduBdUserDo1.getToken();
									List<LinkedHashMap<String, Object>> dataList = new ArrayList();
									// 任务时间
									String task_time = nowTime;

									// 开始日期
									// String startDate = BCDTimeUtil.convertNormalDate(null);
									String startDate = task_time.substring(0, 10);
									// 结束日期
									// String endDate = "2019-12-12";
									String endDate = task_time.substring(0, 10);
									// 验收状态，0:待验收，1:已验收
									Integer acceptStatus = 0;
									
									PpGsPlanOptsAppMod ppGsPlanOptsAppMod = new PpGsPlanOptsAppMod();
									PpGsPlanOptsDTO drsDto = null;
									drsDto = ppGsPlanOptsAppMod.appModFunc(token, startDate, endDate, null, acceptStatus, null, null, null,
											null, null, null, null, null, null, null, null, null, null, null, "1", "30000000", db1Service,
											eduSchoolService, db2Service, dbHiveDishService);
									List<PpGsPlanOpts> ppGsPlanOpts = drsDto.getPpGsPlanOpts();
									if (ppGsPlanOpts != null) {
										for (int n = 0; n < ppGsPlanOpts.size(); n++) {
											LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
							            	dataMap.put("taskTime", nowTime);
							            	dataMap.put("pushUserAccount", pushUserAccount);
											dataMap.put("dishDate", ppGsPlanOpts.get(n).getDistrDate());
											dataMap.put("ppName", ppGsPlanOpts.get(n).getPpName());
											dataMap.put("schType", ppGsPlanOpts.get(n).getSchType());
											dataMap.put("compDep", new ToolUtil().departmentName(ppGsPlanOpts.get(n).getDepartmentId()));
											dataMap.put("num", 1);
											dataList.add(dataMap);
										}
										db2Service.getInsertUnacceptedWarning(dataList);
									}
								}else if("4".equals(category)) {
									content="[预警信息] "+nowTime.substring(0,10)+" 未留样学校名单";
									List<LinkedHashMap<String, Object>> dataList = new ArrayList();
									String token=teduBdUserDo1.getToken();
									// 任务时间
									String task_time = nowTime;

									// 开始日期
									// String startDate = BCDTimeUtil.convertNormalDate(null);
									String startDate = task_time.substring(0, 10);
									// 结束日期
									// String endDate = "2019-12-12";
									String endDate = task_time.substring(0, 10);
									// 是否留样标识，0:未留样，1:已留样
									Integer rsFlag = 0;
									/**
									 * 3.2.53. 项目点留样列表应用模型
									 */
									PpRetSamplesAppMod ppRetSamplesAppMod = new PpRetSamplesAppMod();
									PpRetSamplesDTO drsDto = null;
									drsDto = ppRetSamplesAppMod.appModFunc(token, startDate, endDate, null, rsFlag, null, null, null, null,
											null, null, null, null, null, null, null, null,  "1", "30000000", db1Service,
											eduSchoolService, db2Service);
									List<PpRetSamples> ppRetSamples = drsDto.getPpRetSamples();
									if (ppRetSamples != null) {
										for (int n = 0; n < ppRetSamples.size(); n++) {
											LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
							            	dataMap.put("taskTime", nowTime);
							            	dataMap.put("pushUserAccount", pushUserAccount);
											dataMap.put("dishDate", ppRetSamples.get(n).getRepastDate());
											dataMap.put("ppName", ppRetSamples.get(n).getPpName());
											dataMap.put("schType", ppRetSamples.get(n).getSchType());
											dataMap.put("compDep", new ToolUtil().departmentName(ppRetSamples.get(n).getDepartmentId()));
											dataMap.put("num", 1);
											dataList.add(dataMap);
										}
										db2Service.getInsertNotSampleWarning(dataList);
									}
								}else if("5".equals(category)) {
									content="[预警信息] "+nowTime.substring(0,10)+" 数据异常学校名单";
									
									String token=teduBdUserDo1.getToken();
									// 任务时间
									String task_time = nowTime;

									// 开始日期
									// String startDate = BCDTimeUtil.convertNormalDate(null);
									String startDate = task_time.substring(0, 10);
									// 结束日期
									// String endDate = "2019-12-12";
									String endDate = task_time.substring(0, 10);
//									String dishesWarn=dishesWarn==null?"20":dishesWarn;
									float  offset=(Float.parseFloat(dishesWarn));
									//区域
							  		String departmentId=new ToolUtil().departmentId(teduBdUserDo1.getOrgName());
							  		List<LinkedHashMap<String, Object>> dataList = new ArrayList();
									sourceDao=dbHiveDishService.getSendSteakDataAnomalyWarning(startDate,endDate,offset/100,endDate,departmentId);
									if (sourceDao != null) {
										for (int n = 0; n < sourceDao.size(); n++) {
											LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
							            	dataMap.put("taskTime", nowTime);
							            	dataMap.put("pushUserAccount", pushUserAccount);
											dataMap.put("supplyDate", sourceDao.get(n).getCommonMap().get("supplyDate"));
											dataMap.put("schoolName", sourceDao.get(n).getCommonMap().get("schoolName"));
											dataMap.put("menuGroupName", sourceDao.get(n).getCommonMap().get("menuGroupName"));
											dataMap.put("caterTypeName", sourceDao.get(n).getCommonMap().get("caterTypeName"));
											dataMap.put("dishesName", sourceDao.get(n).getCommonMap().get("dishesName"));
											dataMap.put("mealsCount", sourceDao.get(n).getCommonMap().get("mealsCount"));
											dataMap.put("dishesNumber", sourceDao.get(n).getCommonMap().get("dishesNumber"));
											dataList.add(dataMap);
										}
										db2Service.getInsertSteakDataAnomalyWarning(dataList);
									}
									
									//验收异常数据
									// 验收状态，0:待验收，1:已验收
//									 String acceptStatus = null;
									CaMatSupDetsAppMod cmsdAppMod = new CaMatSupDetsAppMod();
									CaMatSupDetsDTO cmsdDto = cmsdAppMod.appModFunc(token, startDate, endDate, null, null, null, null, null,
											null, null, null, null, null, null, null, "1", "30000000",db1Service, db2Service, saasService,dbHiveService);
									List<LinkedHashMap<String, Object>> midList = new ArrayList();
									List<CaMatSupDets> caMatSupDets = cmsdDto.getCaMatSupDets();
									try {
										for (int n = 0; n < caMatSupDets.size(); n++) {
											if (caMatSupDets.get(n) != null) {
											float acceptRate = caMatSupDets.get(n).getAcceptRate();
											int acceptStatus = caMatSupDets.get(n).getAcceptStatus();
//											if (acceptStatus == 0) {
//												acceptRate = 0.00f;
//											}

											float offset1 = (Float.parseFloat(deliveryWarn));
											if (acceptRate == 0.00f || acceptRate < (100.00f - offset1) || acceptRate > (100.00f + offset1)) {
													LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
													commonMap.put("taskTime", nowTime);
													commonMap.put("pushUserAccount", pushUserAccount);
													commonMap.put("useDate", caMatSupDets.get(n).getMatUseDate());
													commonMap.put("schoolName", caMatSupDets.get(n).getSchName());
													commonMap.put("wareBatchNo", caMatSupDets.get(n).getDistrBatNumber());
													commonMap.put("name", caMatSupDets.get(n).getStandardName());
													commonMap.put("otherQuantity", caMatSupDets.get(n).getCvtQuantity());
													commonMap.put("deliveryNumber", caMatSupDets.get(n).getAcceptNum());
													commonMap.put("ratio", caMatSupDets.get(n).getAcceptRate());
													midList.add(commonMap);
												}
											}
										}
										db2Service.getInsertAcceptanceDataAnomalyWarning(midList);
									} catch (Exception e) {
										logger.info(e.getMessage());
									}
									
								}
								LinkedHashMap<String, Object> filterParamMap=new LinkedHashMap<String, Object>();
								filterParamMap.put("user_account", pushUserAccount);
								filterParamMap.put("task_category", category);
								filterParamMap.put("is_read", 0);
								filterParamMap.put("content", content);
								filterParamMap.put("task_time", nowTime);
								filterParamMap.put("last_time", nowTime);
								filterParamMap.put("dishesWarn", dishesWarn);
								filterParamMap.put("deliveryWarn", deliveryWarn);
								db2Service.getInsertInfo(filterParamMap);
							}
						
						
					}
//					String[] emailRecipientArr=emailRecipient.split(",");
//					for(int k=0;k<emailRecipientArr.length;k++) {
//						if("1".equals(category) && Integer.parseInt(week) == nowWeek) {
//							
//						}else if("2".equals(category)) {
//						}else if("3".equals(category)) {
//						}else if("4".equals(category)) {
//						}else if("5".equals(category)) {
//						}
//					}
				}
				
			}
			
		} catch (Exception e) {
			appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
			appCommonExternalModulesDto.setResMsg(e.getMessage());
		}
	}
	//以管理部门,学校,学制聚合7天未排菜数据
	//List遍历相同数据合并(map里面某个key相同则合并数据)
	public List<LinkedHashMap<String, Object>> mapGroupBy(List<LinkedHashMap<String, Object>> sortMap,String compareKev,String compareKev1,String compareKev2, String sumKey){
		List<LinkedHashMap<String, Object>> countList = new ArrayList<LinkedHashMap<String, Object>>();//用于存放最后的结果
		
        for (int i = 0; i < sortMap.size(); i++) {
            String groupKey = sortMap.get(i).get(compareKev).toString();
            String groupKey1 = sortMap.get(i).get(compareKev1).toString();
            String groupKey2 = sortMap.get(i).get(compareKev2).toString();
             
            int flag = 0;//0为新增数据，1为增加count
            for (int j = 0; j < countList.size(); j++) {
                String groupKey_ = countList.get(j).get(compareKev).toString();
                String groupKey1_ = countList.get(j).get(compareKev1).toString();
                String groupKey2_ = countList.get(j).get(compareKev2).toString();
                if (groupKey.equals(groupKey_) && groupKey1.equals(groupKey1_) && groupKey2.equals(groupKey2_)) {
                	int sum = ((int) sortMap.get(i).get(sumKey))+((int) countList.get(j).get(sumKey));
                    countList.get(j).put(sumKey, sum);
                    String togetherDate=sortMap.get(i).get("dishDate")+","+countList.get(j).get("dishDate");
                    countList.get(j).put("dishDate",togetherDate);
                    flag = 1;
                    continue;
                }
            }
            if (flag == 0) {
                countList.add(sortMap.get(i));
            }
        }
		return countList;
	}
	
	//异常数据 处理
	
}
