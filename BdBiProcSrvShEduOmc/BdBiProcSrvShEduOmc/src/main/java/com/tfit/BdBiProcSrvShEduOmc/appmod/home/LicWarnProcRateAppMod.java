package com.tfit.BdBiProcSrvShEduOmc.appmod.home;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.LicWarnProcRate;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.LicWarnProcRateDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLics;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//证照预警处理率应用模型
public class LicWarnProcRateAppMod {
	private static final Logger logger = LogManager.getLogger(LicWarnProcRateAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//时间坐标个数
	final int timeCoordNum = 7;
	
	//最小供餐学校数量域值
	final int minMealSchNumThre = 0;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 3;
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//数组数据初始化
	String[] timeCoord_Array = {"8/6", "8/7", "8/8", "8/9", "8/10", "8/11", "8/12"};
	float[] procRate_Array = {(float) 88.21, (float) 78.34, (float) 69.56, (float) 75.78, (float) 84.22, (float) 82.55, (float) 81.15};
	
	//模拟数据函数
	private LicWarnProcRateDTO SimuDataFunc() {
		LicWarnProcRateDTO lwprDto = new LicWarnProcRateDTO();
		//时戳
		lwprDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//当前排菜率
		float curProcRate = (float) 71.00;
		BigDecimal bd = new BigDecimal(curProcRate);
		curProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		lwprDto.setCurProcRate(curProcRate);
		//证照预警处理率模拟数据
		List<LicWarnProcRate> licWarnProcRate = new ArrayList<>();
		DateTime dt = new DateTime();
		//赋值
		for (int i = 0; i < timeCoord_Array.length; i++) {
			LicWarnProcRate lwpr = new LicWarnProcRate();
			DateTime curDt = dt.minusDays(timeCoord_Array.length-i-1);
			timeCoord_Array[i] = BCDTimeUtil.convertMonthDayForm(curDt);
			lwpr.setTimeCoord(timeCoord_Array[i]);
			bd = new BigDecimal(procRate_Array[i]);
			procRate_Array[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			lwpr.setProcRate(procRate_Array[i]);
			licWarnProcRate.add(lwpr);
		}
		//设置数据
		lwprDto.setLicWarnProcRate(licWarnProcRate);
		//消息ID
		lwprDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return lwprDto;
	}
	
	// 证照预警处理率函数
	private LicWarnProcRateDTO licWarnProcRate(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList) {
		LicWarnProcRateDTO lwprDto = new LicWarnProcRateDTO();
		List<LicWarnProcRate> licWarnProcRate = new ArrayList<>();
		String key = "", keyVal = "";
		// 预警映射信息
		Map<String, String> warnTotalMap = null;
		int i, j, k, distCount = tedList.size(), dateCount = dates.length, curWarnNum = 0;
		int[] totalWarnNums = new int[dateCount], elimWarnNums = new int[dateCount];
		float[] warnProcRates = new float[dateCount];
		//区域ID到索引映射
		Map<String, Integer> distIdToIdxMap = new HashMap<>();
		for(i = 0; i < distCount; i++) {
			distIdToIdxMap.put(tedList.get(i).getId(), i);
		}
		distIdToIdxMap.put("-", i);
		// 当天各区证照预警处理数量
		for (k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k] + "_warn-total";
			warnTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(warnTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				
			}
			if(warnTotalMap != null) {
				for(String curKey : warnTotalMap.keySet()) {
					String[] curKeys = curKey.split("_");
					if(curKeys.length == 4) {
						i = AppModConfig.getVarValIndex(curKeys, "area");
						if(i != -1) {
							if(!curKeys[i].equalsIgnoreCase("null")) {
								if(distIdToIdxMap.containsKey(curKeys[i])) {
									keyVal = warnTotalMap.get(curKey);
									curWarnNum = 0;
									if(keyVal != null)
										curWarnNum = Integer.parseInt(keyVal);
									if(curWarnNum < 0)
										curWarnNum = 0;
									j = AppModConfig.getVarValIndex(curKeys, "status");
									if(j != -1) {
										if(curKeys[j].equalsIgnoreCase("1")) {         //未处理预警数
											totalWarnNums[k] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("2")) {    //审核中预警数
											totalWarnNums[k] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("3")) {    //已驳回预警数
											totalWarnNums[k] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("4")) {    //已消除预警数
											elimWarnNums[k] += curWarnNum;
											totalWarnNums[k] += curWarnNum;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		// 最近7天每天处理率
		for(k = 0; k < dateCount; k++) {
			LicWarnProcRate lwpr = new LicWarnProcRate();
			DateTime curDt = BCDTimeUtil.convertDateStrToDate(dates[k]);
			String timeCoord = curDt.toString("M/d");
			// 区域处理数
			if (totalWarnNums[k] != 0) {
				//处理率
				warnProcRates[k] = 100 * ((float) elimWarnNums[k] / (float) totalWarnNums[k]);
				BigDecimal bd = new BigDecimal(warnProcRates[k]);
				warnProcRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRates[k] > 100) {
					warnProcRates[k] = 100;
				}
			}
			logger.info("日期：" + dates[k] + "，预警总数：" + totalWarnNums[k] + "，已处理数：" + elimWarnNums[k] + "，处理率：" + warnProcRates[k]);
			lwpr.setProcRate(warnProcRates[k]);
			lwpr.setTimeCoord(timeCoord);
			licWarnProcRate.add(lwpr);
			//今日处理率
			if(k == dateCount-1) {
				lwprDto.setCurProcRate(warnProcRates[k]);
			}
		}
		// 设置返回数据
		lwprDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//设置数据
		lwprDto.setLicWarnProcRate(licWarnProcRate);
		//消息ID
		lwprDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return lwprDto;
	}
	
	// 证照预警处理率函数
	private LicWarnProcRateDTO licWarnProcRateTwo(String distIdorSCName,Integer target, String[] dates,DbHiveWarnService dbHiveWarnService) {
		LicWarnProcRateDTO lwprDto = new LicWarnProcRateDTO();
		List<LicWarnProcRate> licWarnProcRate = new ArrayList<>();
		
		
		String startDate = dates[0];
		String endDate = dates[dates.length - 1];
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
		String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
		//获取开始日期、结束日期的年月集合
		List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<WarnCommonLics> warnCommonLicList = new ArrayList<>();
		warnCommonLicList = dbHiveWarnService.getWarnLicList(4, listYearMonth, startDate, endDateAddOne, distIdorSCName,target);
		
		/**
		 * 2.合并数据(按日期合并)
		 */
		int dateCount = dates.length;
		int[] totalWarnNums = new int[dateCount], elimWarnNums = new int[dateCount];
		float[] warnProcRates = new float[dateCount];
		for(WarnCommonLics warnCommonLics : warnCommonLicList) {
			for(int i = 0; i< dates.length ; i ++) {
				if(dates[i].replaceAll("-", "/").equals(warnCommonLics.getWarnPeriod())) {
					totalWarnNums[i] += warnCommonLics.getNoProcWarnNum() + warnCommonLics.getRejectWarnNum() + warnCommonLics.getAuditWarnNum()+warnCommonLics.getElimWarnNum();
					elimWarnNums[i] += warnCommonLics.getElimWarnNum();
					break;
				}
			}
		}
		
		// 最近7天每天处理率
		for(int k = 0; k < dateCount; k++) {
			LicWarnProcRate lwpr = new LicWarnProcRate();
			DateTime curDt = BCDTimeUtil.convertDateStrToDate(dates[k]);
			String timeCoord = curDt.toString("M/d");
			// 区域处理数
			if (totalWarnNums[k] != 0) {
				//处理率
				warnProcRates[k] = 100 * ((float) elimWarnNums[k] / (float) totalWarnNums[k]);
				BigDecimal bd = new BigDecimal(warnProcRates[k]);
				warnProcRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRates[k] > 100) {
					warnProcRates[k] = 100;
				}
			}
			logger.info("日期：" + dates[k] + "，预警总数：" + totalWarnNums[k] + "，已处理数：" + elimWarnNums[k] + "，处理率：" + warnProcRates[k]);
			lwpr.setProcRate(warnProcRates[k]);
			lwpr.setTimeCoord(timeCoord);
			licWarnProcRate.add(lwpr);
			//今日处理率
			if(k == dateCount-1) {
				lwprDto.setCurProcRate(warnProcRates[k]);
			}
		}
		// 设置返回数据
		lwprDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//设置数据
		lwprDto.setLicWarnProcRate(licWarnProcRate);
		//消息ID
		lwprDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return lwprDto;
	}
	
	// 证照预警处理率函数(修改新预警表)
	private LicWarnProcRateDTO licWarnProcRateThree(String departmentId,String distIdorSCName,Integer target, String[] dates,DbHiveWarnService dbHiveWarnService) {
		LicWarnProcRateDTO lwprDto = new LicWarnProcRateDTO();
		List<LicWarnProcRate> licWarnProcRate = new ArrayList<>();
		
		
		String startDate = dates[0];
		String endDate = dates[dates.length - 1];
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
		String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
		//获取开始日期、结束日期的年月集合
		List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(1, 4, target, listYearMonth, startDate, endDateAddOne,
				distIdorSCName, null, -1, -1, null, null, departmentId, null, 0);
		
		/**
		 * 2.合并数据(按日期合并)
		 */
		int dateCount = dates.length;
		int[] totalWarnNums = new int[dateCount], elimWarnNums = new int[dateCount];
		float[] warnProcRates = new float[dateCount];
		for(WarnCommon warnCommonLics : warnCommonList) {
			for(int i = 0; i< dates.length ; i ++) {
				if(dates[i].replaceAll("-", "/").equals(warnCommonLics.getWarnPeriod())) {
					totalWarnNums[i] += warnCommonLics.getNoProcWarnNum() + warnCommonLics.getRejectWarnNum() + warnCommonLics.getAuditWarnNum()+warnCommonLics.getElimWarnNum();
					elimWarnNums[i] += warnCommonLics.getElimWarnNum();
					break;
				}
			}
		}
		
		// 最近7天每天处理率
		for(int k = 0; k < dateCount; k++) {
			LicWarnProcRate lwpr = new LicWarnProcRate();
			DateTime curDt = BCDTimeUtil.convertDateStrToDate(dates[k]);
			String timeCoord = curDt.toString("M/d");
			// 区域处理数
			if (totalWarnNums[k] != 0) {
				//处理率
				warnProcRates[k] = 100 * ((float) elimWarnNums[k] / (float) totalWarnNums[k]);
				BigDecimal bd = new BigDecimal(warnProcRates[k]);
				warnProcRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRates[k] > 100) {
					warnProcRates[k] = 100;
				}
			}
			logger.info("日期：" + dates[k] + "，预警总数：" + totalWarnNums[k] + "，已处理数：" + elimWarnNums[k] + "，处理率：" + warnProcRates[k]);
			lwpr.setProcRate(warnProcRates[k]);
			lwpr.setTimeCoord(timeCoord);
			licWarnProcRate.add(lwpr);
			//今日处理率
			if(k == dateCount-1) {
				lwprDto.setCurProcRate(warnProcRates[k]);
			}
		}
		// 设置返回数据
		lwprDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//设置数据
		lwprDto.setLicWarnProcRate(licWarnProcRate);
		//消息ID
		lwprDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return lwprDto;
	}
	
	// 证照预警处理率模型函数
	public LicWarnProcRateDTO appModFunc(String token, String distName, String prefCity, String province, int timeType, String date, 
			Db1Service db1Service, Db2Service db2Service,DbHiveWarnService dbHiveWarnService) {
		LicWarnProcRateDTO lwprDto = null;
		if(isRealData) {       //真实数据
			//日期
			String[] dates = null;
			if(timeType == 3) {  //按天取日期
				DateTime dt = null;
				dates = new String[timeCoordNum];
				if(date == null)
					dt = new DateTime();
				else
					dt = BCDTimeUtil.convertDateStrToDate(date);
				for(int i = timeCoordNum-1, j = 0; i >= 0; i--, j++)
					dates[i] = dt.minusDays(j).toString("yyyy-MM-dd");
			}
			else if(timeType == 4) {  //按周取日期
				
			}
			for (int i = 0; i < dates.length; i++) {
				logger.info("dates[" + i + "] = " + dates[i]);
			}
			// 省或直辖市
			if (province == null)
				province = "上海市";
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			String departmentId = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) { // 按区域，省或直辖市处理
				List<TEduDistrictDo> tddList = db1Service.getListByDs1IdName();
				// 查找是否存在该区域和省市
				for (int i = 0; i < tddList.size(); i++) {
					TEduDistrictDo curTdd = tddList.get(i);
					if (curTdd.getId().compareTo(distName) == 0) {
						bfind = true;
						distIdorSCName = curTdd.getId();
						break;
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 预警处理率函数
					if(methodIndex == 1) {
						lwprDto = licWarnProcRate(distIdorSCName, dates, tddList);
					}else if (methodIndex == 2) {
						//target :2:区教育局,3:市教委
					  	Integer target = CommonUtil.getTarget(token,db1Service,db2Service);
						lwprDto = licWarnProcRateTwo(distIdorSCName, target, dates, dbHiveWarnService);
					}else if (methodIndex == 3) {
						//target :2:区教育局,3:市教委
					  	Integer target = CommonUtil.getTarget(token,db1Service,db2Service);
						lwprDto = licWarnProcRateThree(departmentId,distIdorSCName, target, dates, dbHiveWarnService);
					}
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				List<TEduDistrictDo> tddList = null;
				if (province.compareTo("上海市") == 0) {
					bfind = true;
					tddList = db1Service.getListByDs1IdName();
					distIdorSCName = null;
				}
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 预警处理率函数
					if(methodIndex == 1) {
						lwprDto = licWarnProcRate(distIdorSCName, dates, tddList);
					}else if (methodIndex == 2) {
						//target :2:区教育局,3:市教委
					  	Integer target = CommonUtil.getTarget(token,db1Service,db2Service);
						lwprDto = licWarnProcRateTwo(distIdorSCName, target, dates, dbHiveWarnService);
					}else if (methodIndex == 3) {
						//target :2:区教育局,3:市教委
					  	Integer target = CommonUtil.getTarget(token,db1Service,db2Service);
						lwprDto = licWarnProcRateThree(departmentId,distIdorSCName, target, dates, dbHiveWarnService);
					}
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}						
		}
		else {    //模拟数据
			//模拟数据函数
			lwprDto = SimuDataFunc();
		}		

		return lwprDto;
	}
}
