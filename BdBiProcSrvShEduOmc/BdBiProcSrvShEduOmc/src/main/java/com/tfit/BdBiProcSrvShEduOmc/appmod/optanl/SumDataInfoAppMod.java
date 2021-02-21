package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonRecs;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SumDataAcceptInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SumDataDishInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SumDataInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SumDataInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SumDataKwInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SumDataRsInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SumDataWarnInfo;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 3.2.1.	汇总数据信息应用模型
 * @author fengyang_xie
 *
 */
public class SumDataInfoAppMod {
	private static final Logger logger = LogManager.getLogger(SumDataInfoAppMod.class.getName());
	
	/**
	 * Redis服务
	 */
	@Autowired
	RedisService redisService = new RedisService();
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 0;
	/**
	 * 是否为真实数据标识
	 */
	private static boolean isRealData = true;
	
	/**
	 * 汇总数据
	 * @return
	 */
	private SumDataInfoDTO sumDataInfoFunc(String departmentId,String distId,String[] dates,Integer target, List<TEduDistrictDo> tedList,
			Db1Service db1Service,
			SaasService saasService ,DbHiveWarnService dbHiveWarnService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService,
			DbHiveDishService dbHiveDishService,DbHiveGsService dbHiveGsService) {
		
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		
		
		SumDataInfo sumDataInfo = new SumDataInfo();
		/**
		 * 监管学校数量
		 */
		Integer supSchNum = getSupSchNum(departmentId,distId);
		sumDataInfo.setSupSchNum(supSchNum);
		/**
		 * 排菜汇总
		 */
		SumDataDishInfo dishInfo = new SumDataDishInfo();
		if(days >= 2) {
			dishInfo = getDishInfoFromHive(departmentId,distId, dates, tedList, dbHiveDishService);
		}else {
			dishInfo = getDishInfo(departmentId,distId, dates, tedList);
		}
		sumDataInfo.setDishInfo(dishInfo);
		
		/**
		 * 验收汇总
		 */
		SumDataAcceptInfo acceptInfo = new SumDataAcceptInfo();
		//获取配送信息：未验收配送单个数、验收率
		if(days >= 2) {
			getAccDistrInfoFromHive(departmentId,distId, dates, tedList, acceptInfo, dbHiveGsService);
		}else {
			getAccDistrInfo(departmentId,distId, dates, tedList, acceptInfo);
		}
		
		//注释时间：2019-03/26 改为从Total里获取未验收学校
		/*//获取配送信息：未验收学校
		Set<String> schoolSet = getAcceptNoAccSchuNum(distId, dates);
		acceptInfo.setNoAccSchNum(schoolSet.size());*/
		sumDataInfo.setAcceptInfo(acceptInfo);
		
		/**
		 * 留样汇总
		 */
		 SumDataRsInfo rsInfo = new SumDataRsInfo();
		 //获取留样汇总：留样率、未留样菜品个数
		 getRsInfoRsRate(departmentId,distId, dates, tedList, rsInfo,dishInfo.getDishSchNum());
		 
		 ////2019.03.28 注释原因：建模规则修改由detail改为total中获取
		 //获取留样汇总：未留样学校个数
		 //Set<String> dishRsDetSet = getRsInfoNoRsSchNum(distId, dates, db1Service);
		 //rsInfo.setNoRsSchNum(dishRsDetSet.size());
		 
		 sumDataInfo.setRsInfo(rsInfo);
		
		/**
		 * 预警汇总
		 */
		//2019.06.05注释，注释原因：预警改为从hive库中获取
		////获取预警汇总：未处理预警、处理率
		//SumDataWarnInfo warnInfo = getWarnInfoWarnProcRate(distId, dates, tedList);
		////获取预警汇总：未处理单位
		//Set<String> warnUnitSet = getWarnInfoNoProcUnitNum(distId, dates, saasService);
		//warnInfo.setNoProcUnitNum(warnUnitSet.size());
		 
	    //SumDataWarnInfo warnInfo = getWarnInfoWarnProcRateTwo(departmentId, dates[0], dates[0], target, tedList, dbHiveWarnService);
		 
		SumDataWarnInfo warnInfo = getWarnInfoWarnProcRateThree(departmentId, null,dates[0], dates[0], target, tedList, dbHiveWarnService);
		sumDataInfo.setWarnInfo(warnInfo);
		//30天之前的数据获取hive库，30天以内以及今天以后的数据获取redis数据
		if(days >= 2) {
			/**
			 * 餐厨垃圾
			 */
			SumDataKwInfo kwInfo = new SumDataKwInfo();
			Map<String,Float> resultMap = getRecInfoFromHive(departmentId, dates[dates.length -1],dates[0], tedList, dbHiveRecyclerWasteService);
			//获取学校回收垃圾数
			float schRecNum = resultMap.get("schRecNum");
			//获取团餐公司回收垃圾数
			float rmcRecNum = resultMap.get("rmcRecNum");
			
			kwInfo.setSchRecNum( new BigDecimal(schRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			kwInfo.setRmcRecNum( new BigDecimal(rmcRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			kwInfo.setTotalRec( new BigDecimal(schRecNum + rmcRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			sumDataInfo.setKwInfo(kwInfo);
			
			/**
			 * 排废弃油脂
			 */
			SumDataKwInfo woInfo = new SumDataKwInfo();
			//获取学校回收废弃油脂数
		    schRecNum = resultMap.get("schOilRecNum");
			//获取团餐公司废弃油脂数
			rmcRecNum = resultMap.get("rmcOilRecNum");
			
			woInfo.setRmcRecNum( new BigDecimal(rmcRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			woInfo.setSchRecNum( new BigDecimal(schRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			woInfo.setTotalRec( new BigDecimal(schRecNum + rmcRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			sumDataInfo.setWoInfo(woInfo);
		}else {
			/**
			 * 餐厨垃圾
			 */
			SumDataKwInfo kwInfo = new SumDataKwInfo();
			
			//获取学校回收垃圾数
			String keySuffix ="_schoolwastetotal";
			float schRecNum = getRecInfo(departmentId, dates, tedList, keySuffix);
			//获取团餐公司回收垃圾数
			keySuffix ="_supplierwastetotal";
			float rmcRecNum = getRecInfo(departmentId, dates, tedList, keySuffix);
			
			kwInfo.setSchRecNum( new BigDecimal(schRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			kwInfo.setRmcRecNum( new BigDecimal(rmcRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			kwInfo.setTotalRec( new BigDecimal(schRecNum + rmcRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			sumDataInfo.setKwInfo(kwInfo);
			
			/**
			 * 排废弃油脂
			 */
			SumDataKwInfo woInfo = new SumDataKwInfo();
			//获取学校回收废弃油脂数
		    keySuffix ="_schooloiltotal";
		    schRecNum = getRecInfo(departmentId, dates, tedList, keySuffix);
			//获取团餐公司废弃油脂数
			keySuffix ="_supplieroiltotal";
			rmcRecNum = getRecInfo(departmentId, dates, tedList, keySuffix);
			
			woInfo.setRmcRecNum( new BigDecimal(rmcRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			woInfo.setSchRecNum( new BigDecimal(schRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			woInfo.setTotalRec( new BigDecimal(schRecNum + rmcRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			sumDataInfo.setWoInfo(woInfo);
		}
		
		
		SumDataInfoDTO sumDataInfoDTO =new SumDataInfoDTO();
		sumDataInfoDTO.setSumDataInfo(sumDataInfo);
		//时戳
		sumDataInfoDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		sumDataInfoDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return sumDataInfoDTO;
	}

	/**
	 * 获取废弃油脂、餐厨垃圾相关数据
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param keySuffix
	 * @return
	 */
	private float getRecInfo(String distId, String[] dates, List<TEduDistrictDo> tedList, String keySuffix) {
		String key = "";
		String keyVal = "";
		String field = "";
		String fieldPrefix = "";
		// 时间段内各区餐厨垃圾学校回收总数
		Map<String, String> schoolwastetotalMap = null;
		int distCount = tedList.size();
		int dateCount = dates.length;
		int[] totalRcFreqs = new int[dateCount];
		float[] totalRcNums = new float[dateCount];
		// 时间段内各区餐厨垃圾学校回收数量
		for (int k = 0; k < dates.length; k++) {
			totalRcFreqs[k]=0;
			// 回收桶数
			key = dates[k] + keySuffix;
			schoolwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(schoolwastetotalMap == null) {    
				
			}
			if(schoolwastetotalMap != null) {
				for(String curKey : schoolwastetotalMap.keySet()) {
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						String curDistId = curTdd.getId();
						//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						if(distId != null) {
							if(curDistId.compareTo(distId) != 0) {
								continue ;
							}
						}
						//区域回收次数
						fieldPrefix = curDistId + "_total";
						if (curKey.equalsIgnoreCase(fieldPrefix)) {
							keyVal = schoolwastetotalMap.get(curKey);
							if(keyVal != null) {
								totalRcFreqs[k] += Integer.parseInt(keyVal);
							}
						}
						// 区域回收垃圾桶数
						fieldPrefix = curDistId;
						if (curKey.equalsIgnoreCase(fieldPrefix)) {
							keyVal = schoolwastetotalMap.get(curKey);
							if(keyVal != null) {
								totalRcNums[k] += Float.parseFloat(keyVal);
							}
						}
					}
				}
			}
			// 该日期各区餐厨垃圾回收列表
			for (int i = 0; i < distCount; i++) {
				TEduDistrictDo curTdd = tedList.get(i);
				String curDistId = curTdd.getId();
				field = "area" + "_" + curDistId;
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (distId != null) {
					if (curDistId.compareTo(distId) != 0) {
						continue;
					}
				}
				BigDecimal bd = new BigDecimal(totalRcNums[k]);
				totalRcNums[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，回收次数：" + totalRcFreqs[k]
						+ "，回收数量：" + totalRcNums[k] + " 桶" + "，field = " + field);
			}
		}
		
		float totalRcNum = 0;
		for (int k = 0; k < dates.length; k++) {
			totalRcNum += totalRcNums[k];
		}
		return totalRcNum;
	}
	
	/**
	 * 获取废弃油脂、餐厨垃圾相关数据
	 * @param distId
	 * @param startDate
	 * @param tedList
	 * @param endDate
	 * @return
	 */
	private Map<String,Float> getRecInfoFromHive(String distId, String startDate, String endDate,
			List<TEduDistrictDo> tedList,DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		//key:类型 vlaue：回收数量
		Map<String,Float> resultMap = new HashMap<String,Float>();
		
		// 时间段内各区餐厨垃圾学校回收总数
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
		List<KwCommonRecs> warnCommonLicList = new ArrayList<>();
		warnCommonLicList = dbHiveRecyclerWasteService.getRecyclerWasteList(listYearMonth, startDate, endDateAddOne, distId, 
				null, null, null,  -1,-1,null,null,null, null,0);
		
		/**
		 * 2.合并数据
		 */
		/**
		 * 学校餐厨回收桶数
		 */
		Float schRecNum = Float.valueOf("0");
		/**
		 * 团餐公司餐厨回收桶数
		 */
		Float rmcRecNum = Float.valueOf("0");
		/**
		 * 学校废弃油脂回收桶数
		 */
		Float schOilRecNum = Float.valueOf("0");
		/**
		 * 团餐废弃油脂公司回收桶数
		 */
		Float rmcOilRecNum = Float.valueOf("0");
		logger.info("******************* warnCommonLicList:"+warnCommonLicList.size());
		for(KwCommonRecs warnCommonLics : warnCommonLicList) {
			//PlatformType:1为教委端 2为团餐端 type:1餐厨垃圾，2废弃油脂
			if(warnCommonLics.getPlatformType() == 1) {
				//学校
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					schRecNum += new BigDecimal(warnCommonLics.getRecyclerSum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					schOilRecNum +=  new BigDecimal(warnCommonLics.getRecyclerSum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				}
			}else if(warnCommonLics.getPlatformType() == 2) {
				//团餐公司
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					rmcRecNum +=  new BigDecimal(warnCommonLics.getRecyclerSum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					rmcOilRecNum +=  new BigDecimal(warnCommonLics.getRecyclerSum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				}
			}
		}
		
		resultMap.put("schRecNum",  new BigDecimal(schRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		resultMap.put("rmcRecNum",  new BigDecimal(rmcRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		resultMap.put("schOilRecNum",  new BigDecimal(schOilRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		resultMap.put("rmcOilRecNum",  new BigDecimal(rmcOilRecNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		return resultMap;
	}

	/**
	 * 获取预警汇总：未处理单位
	 * @param distId
	 * @param dates
	 * @param saasService
	 * @return
	 */
	private Set<String> getWarnInfoNoProcUnitNum(String distId, String[] dates, SaasService saasService) {
		//所有单位
		Set<String> warnUnitSet = new HashSet<String>();
		//人员证照：key：supplierId vlaue:schoolId
		Map<String,String> peopleWarnMap = new HashMap<String,String>();
		Map<String, String> warnDetailMap = new HashMap<>();
		int i;
		int k;
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		//供应商id和名称
		Map<String, String> supIdToNameMap = new HashMap<>();
    	List<TProSupplierDo> tpsDoList = saasService.getIdSupplierIdName();
    	if(tpsDoList != null) {
    		for(i = 0; i < tpsDoList.size(); i++) {
    			supIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
    		}
    	}
    	// 时间段内各区学校餐厨垃圾详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_warnDetail";
			warnDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (warnDetailMap != null) {
				for (String curKey : warnDetailMap.keySet()) {
					keyVal = warnDetailMap.get(curKey);
					// 证照预警全部证件详情列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 16) {
						//区
						i = AppModConfig.getVarValIndex(keyVals, "area");
						//判断区域（判断索引0）
						if(distId != null) {
							String curDistName = keyVals[i];
							if(!curDistName.equalsIgnoreCase(distId)) {
								continue;
							}
								
						}
						
						//触发预警单位
						String trigWarnUnit = "";
						i = AppModConfig.getVarValIndex(keyVals, "supplierid");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								if(supIdToNameMap.containsKey(keyVals[i])) {
									trigWarnUnit = supIdToNameMap.get(keyVals[i]);
								}
							}
						}
						
						//状态
						i = AppModConfig.getVarValIndex(keyVals, "status");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								if(!keyVals[i].equalsIgnoreCase("4")) {
									if(keyVal.indexOf("people_")==0) {
										//学校名称
										String schoolId="";
										i = AppModConfig.getVarValIndex(keyVals, "schoolid");
										if(i != -1) {
											if(!keyVals[i].equalsIgnoreCase("null")) {
												schoolId = keyVals[i];
											}
										}
										peopleWarnMap.put(trigWarnUnit, schoolId);
									}else {
										warnUnitSet.add(trigWarnUnit);
									}
								}else {
									continue;
								}
							}
						}
						
						   
					}
					else {
						logger.info("菜品供应明细："+ curKey + "，格式错误！");
					}
				}
			}
			//整合学校证书、团餐公司证书、人员证书。学校证书拿团餐公司统计，团餐公司证书拿团餐公司统计，人员证书拿团餐公司和学校
			for(Map.Entry<String, String> entry : peopleWarnMap.entrySet()) {
				//如果人员对应的团餐公司不包含在学校和团餐公司预计中，则将人员预警对应的学习加入统计中
				if(!warnUnitSet.contains(entry.getKey())) {
					warnUnitSet.add(entry.getValue());
				}
			}
		}
		return warnUnitSet;
	}
	
	/**
	 * 获取预警汇总：未处理预警、处理率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private SumDataWarnInfo getWarnInfoWarnProcRate(String distId, String[] dates, List<TEduDistrictDo> tedList) {
		SumDataWarnInfo warnInfo = new SumDataWarnInfo();
		String key = "";
		String keyVal = "";
		int i;
		int k;
		int j;
		int distCount = tedList.size();
		int dateCount = dates.length;
		int curWarnNum = 0;
		int[] totalWarnNums = new int[dateCount];
		int[] noProcWarnNums = new int[dateCount];
		int[] elimWarnNums = new int[dateCount];
		float warnProcRate = 0;
		//区域ID到索引映射
		Map<String, Integer> distIdToIdxMap = new HashMap<>();
		for(i = 0; i < distCount; i++) {
			distIdToIdxMap.put(tedList.get(i).getId(), i);
		}
		distIdToIdxMap.put("-", i);
		// 时间段预警总数
		Map<String, String> warnTotalMap = null;
		// 时间段内各区预警统计
		for (k = 0; k < dates.length; k++) {
			// 供应数量
			key = dates[k] + "_warn-total";
			warnTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(warnTotalMap == null) {    
				
			}
			if(warnTotalMap != null) {
				for(String curKey : warnTotalMap.keySet()) {
					String[] curKeys = curKey.split("_");
					if(curKeys.length == 4) {
						i = AppModConfig.getVarValIndex(curKeys, "area");
						if(i != -1) {
							if(!curKeys[i].equalsIgnoreCase("null")) {
								if(distIdToIdxMap.containsKey(curKeys[i])) {
									
									// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
									if (distId != null) {
										if (!curKeys[i].equalsIgnoreCase(distId)) {
											continue;
										}
									}
									
									keyVal = warnTotalMap.get(curKey);
									curWarnNum = 0;
									if(keyVal != null) {
										curWarnNum = Integer.parseInt(keyVal);
									}
									if(curWarnNum < 0) {
										curWarnNum = 0;
									}
									j = AppModConfig.getVarValIndex(curKeys, "status");
									if(j != -1) {
										
										if(curKeys[j].equalsIgnoreCase("1")) {   
											 //未处理预警数
											noProcWarnNums[k] += curWarnNum;
											totalWarnNums[k] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("2")) {   
											noProcWarnNums[k] += curWarnNum;
											 //审核中预警数
											totalWarnNums[k] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("3")) {   
											noProcWarnNums[k] += curWarnNum;
											 //已驳回预警数
											totalWarnNums[k] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("4")) {    
											//已消除预警数
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
		
		int totalWarnNum = 0;
		int noProcWarnNum = 0;
		int elimWarnNum = 0;
		for (k = 0; k < dates.length; k++) {
			totalWarnNum += totalWarnNums[k];
			noProcWarnNum += noProcWarnNums[k];
			elimWarnNum += elimWarnNums[k];
		}			
		warnProcRate = 0;
		if(totalWarnNum > 0) {
			warnProcRate = 100 * ((float) elimWarnNum / (float) totalWarnNum);
			BigDecimal bd = new BigDecimal(warnProcRate);
			warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (warnProcRate > 100) {
				warnProcRate = 100;
			}
		}
		
		warnInfo.setNoProcNum(noProcWarnNum);
		warnInfo.setProcRate(warnProcRate);
		return warnInfo;
	}
	
	
	/**
	 * 获取预警汇总：未处理单位、未处理预警、处理率（改为从hive库中获取数据）
	 * @return
	 */
	private SumDataWarnInfo getWarnInfoWarnProcRateTwo(String distId,String startDate,String endDate,Integer target,
			List<TEduDistrictDo> tedList,DbHiveWarnService dbHiveWarnService) {
		SumDataWarnInfo warnInfo = new SumDataWarnInfo();
		/**
		 * 1.从hive库中获取汇总数据
		 */
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
		List<WarnCommonLics> warnCommonLicList = dbHiveWarnService.getWarnLicList(4, listYearMonth, startDate, endDateAddOne, distId,target);
		
		int totalWarnNum = 0;
		int noProcWarnNum = 0;
		int elimWarnNum = 0;
		int noProcUnitNum = 0;//未处理预警单位
		for(WarnCommonLics warnCommonLics : warnCommonLicList) {
			noProcWarnNum += warnCommonLics.getNoProcWarnNum();
			elimWarnNum += warnCommonLics.getElimWarnNum();
			noProcUnitNum +=warnCommonLics.getNoProcUnitNum();
			totalWarnNum += (warnCommonLics.getNoProcWarnNum() + warnCommonLics.getRejectWarnNum() + warnCommonLics.getAuditWarnNum() + warnCommonLics.getElimWarnNum());
		}
		
		
		
		
		float warnProcRate = 0;
		if(totalWarnNum > 0) {
			warnProcRate = 100 * ((float) elimWarnNum / (float) totalWarnNum);
			BigDecimal bd = new BigDecimal(warnProcRate);
			warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (warnProcRate > 100) {
				warnProcRate = 100;
			}
		}
		
		warnInfo.setNoProcNum(totalWarnNum - elimWarnNum);
		warnInfo.setProcRate(warnProcRate);
		warnInfo.setNoProcUnitNum(noProcUnitNum);
		return warnInfo;
	}

	/**
	 * 获取预警汇总：未处理单位、未处理预警、处理率（改为从hive库中获取数据）
	 * @param departmentId
	 * @param distId
	 * @param startDate
	 * @param endDate
	 * @param target
	 * @param tedList
	 * @param dbHiveWarnService
	 * @return
	 */
	private SumDataWarnInfo getWarnInfoWarnProcRateThree(String departmentId,String distId,String startDate,String endDate,Integer target,
			List<TEduDistrictDo> tedList,DbHiveWarnService dbHiveWarnService) {
		SumDataWarnInfo warnInfo = new SumDataWarnInfo();
		/**
		 * 1.从hive库中获取汇总数据
		 */
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
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(1, 4, target, listYearMonth, startDate, endDateAddOne,
				distId, null, -1, -1, null, null, departmentId, null, 0);
		
		int totalWarnNum = 0;
		int noProcWarnNum = 0;
		int elimWarnNum = 0;
		for(WarnCommon warnCommonLics : warnCommonList) {
			noProcWarnNum += warnCommonLics.getNoProcWarnNum();
			elimWarnNum += warnCommonLics.getElimWarnNum();
			totalWarnNum += (warnCommonLics.getNoProcWarnNum() + warnCommonLics.getRejectWarnNum() + warnCommonLics.getAuditWarnNum() + warnCommonLics.getElimWarnNum());
		}
		
		
		
		
		float warnProcRate = 0;
		if(totalWarnNum > 0) {
			warnProcRate = 100 * ((float) elimWarnNum / (float) totalWarnNum);
			BigDecimal bd = new BigDecimal(warnProcRate);
			warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (warnProcRate > 100) {
				warnProcRate = 100;
			}
		}
		
		warnInfo.setNoProcNum(totalWarnNum - elimWarnNum);
		warnInfo.setProcRate(warnProcRate);
		warnInfo.setTotalWarnNum(totalWarnNum);
		warnInfo.setElimWarnNum(elimWarnNum);
		return warnInfo;
	}

	/**
	 * 获取留样汇总：未留样学校个数
	 * @param distId
	 * @param dates
	 * @param db1Service
	 * @return
	 */
	private Set<String> getRsInfoNoRsSchNum(String distId, String[] dates, Db1Service db1Service) {
		Set<String> dishRsDetSet = new HashSet<String>();
		Map<String, String> gcRetentiondishMap = new HashMap<String, String>();
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		int j;
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distId,1,1, 1);
		for(int i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		// 时间段内各区菜品留样详情
		for(int k = 0; k < dateCount; k++) {
			key = dates[k] + "_gc-retentiondish";
			gcRetentiondishMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (gcRetentiondishMap != null) {
				for (String curKey : gcRetentiondishMap.keySet()) {
					keyVal = gcRetentiondishMap.get(curKey);
					// 菜品留样列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 23) {
						if(!"未留样".equals(keyVals[14])) {
							continue;
						}
						if(distId != null) {
							if(keyVals[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						//学校信息（项目点）
						TEduSchoolDo tesDo = null;
						if(schIdMap.containsKey(keyVals[5])) {
							j = schIdMap.get(keyVals[5]);
							tesDo = tesDoList.get(j-1);
						}
						if(tesDo == null) {
							continue;
						}
						dishRsDetSet.add(keyVals[5]);
					}
					else {
						logger.info("菜品留样："+ curKey + "，格式错误！");
					}
				}
			}
		}
		return dishRsDetSet;
	}

	/**
	 * 获取留样汇总：留样率、未留样菜品
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param rsInfo
	 */
	private void getRsInfoRsRate(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList, SumDataRsInfo rsInfo,int dishSchNum) {
		String key = "";
		String keyVal = "";
		String field = "";
		String fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> gcRetentiondishtotalMap = null;
		int distCount = tedList.size();
		int dateCount = dates.length;
		int[] totalDishNums = new int[dateCount];
		int[] distRsDishNums = new int[dateCount];
		int[] distNoRsDishNums = new int[dateCount];
		
		//已留样学校
		int[] distRsSchNums = new int[dateCount];
		//未留样学校
		int[] distNoRsSchNums = new int[dateCount];
		
		float distRsRate = 0;
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
			gcRetentiondishtotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(gcRetentiondishtotalMap == null) {    
				
			}
			if(gcRetentiondishtotalMap != null) {
				for(String curKey : gcRetentiondishtotalMap.keySet()) {
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						String curDistId = curTdd.getId();
						//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						if(distId != null) {
							if(curDistId.compareTo(distId) != 0) {
								continue ;
							}
						}
						// 区域菜品留样和未留样数
						fieldPrefix = curDistId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 2)
							{
								if(curKeys[1].equalsIgnoreCase("已留样")) {     
									//区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distRsDishNums[k] += Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[1].equalsIgnoreCase("未留样")) {    
									 //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsDishNums[k] += Integer.parseInt(keyVal);
									}
								}
							}
						}
						if(curKey.equalsIgnoreCase(curDistId)) {      
							//区域菜品总数
							keyVal = gcRetentiondishtotalMap.get(curKey);
							if(keyVal != null) {
								totalDishNums[k] += Integer.parseInt(keyVal);
							}
						}
						
						// 区域学校留样和未留样数
						fieldPrefix = "school-area" + "_" + curDistId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 3)
							{
								if(curKeys[2].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distRsSchNums[k] += Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[2].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsSchNums[k] += Integer.parseInt(keyVal);
									}
								}
							}
						}
					}
				}
			}
			// 该日期各区留样率
			for (int i = 0; i < distCount; i++) {
				TEduDistrictDo curTdd = tedList.get(i);
				String curDistId = curTdd.getId();
				field = "area" + "_" + curDistId;
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (distId != null) {
					if (curDistId.compareTo(distId) != 0) {
						continue;
					}
				}
				// 区域留样率
				if (totalDishNums[k] != 0) {
					distRsRate = 100 * ((float) distRsDishNums[k] / (float) totalDishNums[k]);
					BigDecimal bd = new BigDecimal(distRsRate);
					distRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRate > 100) {
						distRsRate = 100;
						distRsDishNums[k] = totalDishNums[k];
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，菜品数量：" + totalDishNums[k]
						+ "，已留样菜品数：" + distRsDishNums[k] + "，未留样菜品数：" + distNoRsDishNums[k] + "，排菜率：" + distRsRate + "，field = "
						+ field);
			}
		}
		
		int totalDishNum = 0;
		int distRsDishNum = 0;
		int distNoRsDishNum = 0;
		int distNoRsSchNum = 0;
		int distRsSchNum = 0;
		for (int k = 0; k < dates.length; k++) {
			totalDishNum += totalDishNums[k];
			distRsDishNum += distRsDishNums[k];
			distNoRsDishNum += distNoRsDishNums[k];
			distNoRsSchNum += distNoRsSchNums[k];
			distRsSchNum += distRsSchNums[k];
		}
		distRsRate = 0;
		if(totalDishNum > 0) {
			distRsRate = 100 * ((float) distRsDishNum / (float) totalDishNum);
			BigDecimal bd = new BigDecimal(distRsRate);
			distRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distRsRate > 100) {
				distRsRate = 100;
			}
		}
		
	   //3.留样率的计算改为：已留样的学校数量/排菜学校数量*100%。
		float schRsRate = 0;
		if((distNoRsSchNum + distRsSchNum) > 0) {
			schRsRate = 100 * ((float) distRsSchNum / (float) (distNoRsSchNum + distRsSchNum));
			BigDecimal bd = new BigDecimal(schRsRate);
			schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (schRsRate > 100) {
				schRsRate = 100;
			}
		}
		
	   rsInfo.setNoRsDishNum(distNoRsDishNum);
	   rsInfo.setRsRate(distRsRate);
	   
	   //应留样学校
	   rsInfo.setShouldRsSchNum(distNoRsSchNum + distRsSchNum);
	   //未留样学校
	   rsInfo.setNoRsSchNum(distNoRsSchNum);
	   //已留样学校
	   rsInfo.setRsSchNum(distRsSchNum);
	   //学校留样率
	   rsInfo.setSchRsRate(schRsRate);
	}

	/**
	 * 获取留样数据
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private SumDataDishInfo getRsInfoRsRateFromHive(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			DbHiveDishService dbHiveDishService) {
		// 当天排菜学校总数
		int dateCount = dates.length;
		float distDishRate = 0;
		// 当天各区排菜学校数量
		String startDate = dates[dates.length - 1];
		String endDate = dates[0];
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
		
		int totalSchNum = 0;
		int totalDistSchNum = 0;
		int distDishSchNum = 0;
		int distNoDishSchNum = 0;
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbeReserveTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,departmentId,null, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				if(CommonUtil.isEmpty(schDishCommon.getDistId())) {
					continue;
				}
				if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if((schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1)) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNum += schDishCommon.getTotal();
						}
					}
				}else {
					if(schDishCommon.getHaveClass() != 1) {
						continue;
					}
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//应排菜数量的统计数据，
							//判断area,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1
							totalDistSchNum += schDishCommon.getTotal();
						}else{
							if(schDishCommon.getHavePlatoon() == 1) {
								//已排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =1
								distDishSchNum += schDishCommon.getTotal();
							}else if(schDishCommon.getHavePlatoon() == 0) {
								//未排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =0
								distNoDishSchNum += schDishCommon.getTotal();
							}
						}
					}
				}
			}
		}
		
		// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
		distDishRate = 0;
		if(totalDistSchNum > 0) {
			distDishRate = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
			
		
		SumDataDishInfo dishInfo = new SumDataDishInfo();
		//应排菜学校
		dishInfo.setMealSchNum(totalDistSchNum);
		//未排菜校数
		dishInfo.setNoDishSchNum(distNoDishSchNum);
		//已排菜学校数
		dishInfo.setDishSchNum(distDishSchNum);
		//未排菜学校数量
		dishInfo.setNoDishDayNum(distNoDishSchNum);
		//排菜率
		dishInfo.setDishRate(distDishRate);
		return dishInfo;
	}
	
	/**
	 * 获取配送信息：未验收学校
	 * @param distId
	 * @param dates
	 * @return
	 */
	private Set<String> getAcceptNoAccSchuNum(String distId, String[] dates) {
		Set<String> schoolSet = new HashSet<String>();
		Map<String, String> distributionDetailMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		String[] keyVals;
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_Distribution-Detail";
			distributionDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionDetailMap != null) {
				for (String curKey : distributionDetailMap.keySet()) {
					keyVals = distributionDetailMap.get(curKey).split("_");
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKeys.length >= 14) {
						if(distId != null) {
							if(curKeys[7].compareTo(distId) != 0) {
								continue ;
							}
						}
						//如果value值为空或者value第一个值不是数字，则不做统计
						if(keyVals.length < 1) {
							continue;
						}
						int dispPlanStatus = Integer.parseInt(keyVals[0]);
						//去除已验收和已取消的订单
						if(dispPlanStatus!=3 && dispPlanStatus!=4) {
							schoolSet.add(curKeys[5]);
						}
					}
					else {
						logger.info("配货计划："+ curKey + "，格式错误！");
					}
				}
			}
		}
		return schoolSet;
	}

	/**
	 * 获取配送信息：未验收配送单个数、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAccDistrInfo(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			SumDataAcceptInfo acceptInfo) {
		String key = "";
		String keyVal = "";
		String field = "";
		// 当天排菜学校总数
		Map<String, String> schIdToPlatoonMap = new HashMap<>();
		int dateCount = dates.length;
		int[]totalGsPlanNums = new int[dateCount];
		int []acceptGsPlanNums = new int[dateCount];
		
		int []noAcceptSchNums = new int[dateCount];
		int []acceptSchNums = new int[dateCount];
		int []conSchNums = new int[dateCount];
		
		float acceptRate = 0;
		int []noAcceptGsPlanNums = new int[dateCount];
		// 当天各区配货计划总数量
		for(int k = 0; k < dates.length; k++) {
			totalGsPlanNums[k] = 0;
			noAcceptGsPlanNums[k] = 0;
			acceptGsPlanNums[k] = 0;
			conSchNums[k] = 0;
			noAcceptSchNums[k] = 0;
			//_DistributionTotal
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				// date_DistributionTotal_department_departmentId
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			schIdToPlatoonMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (schIdToPlatoonMap != null) {
				for (int i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distId != null) {
						if(!curDistId.equalsIgnoreCase(distId)) {
							continue ;
						}
					}
					// 区域配货计划总数
					field = "area" + "_" + curDistId;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						totalGsPlanNums[k] += Integer.parseInt(keyVal);
						if(totalGsPlanNums[k] < 0) {
							totalGsPlanNums[k] = 0;
						}
					}
					// 未验收数
					for(int j = -2; j <= 2; j++) {
						field = "area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int noAcceptGsPlanNum = Integer.parseInt(keyVal);
							if(noAcceptGsPlanNum < 0) {
								noAcceptGsPlanNum = 0;
							}
							noAcceptGsPlanNums[k] += noAcceptGsPlanNum;
						}
					}
					
					// 已验收数
					field = "area" + "_" + curDistId + "_" + "status" + "_3";
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						acceptGsPlanNums[k] += Integer.parseInt(keyVal);
						if(acceptGsPlanNums[k] < 0) {
							acceptGsPlanNums[k] = 0;
						}
					}
					
					//已确认学校
					for(int j = 2; j < 4; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							conSchNums[k] += curConSchNum;
						}
					}
					
					// 未验收学校数
					for(int j = -2; j <= 2; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int noAcceptSchNum = Integer.parseInt(keyVal);
							if(noAcceptSchNum < 0) {
								noAcceptSchNum = 0;
							}
							noAcceptSchNums[k] += noAcceptSchNum;
						}
					}
					
					//已验收学校
					field = "school-area" + "_" + curDistId + "_" + "status" + "_3";
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						int acceptSchNum = Integer.parseInt(keyVal);
						if(acceptSchNum < 0)
							acceptSchNum = 0;
						acceptSchNums[k] += acceptSchNum;
					}
					
					//未验收学校
					//noAcceptSchNums[k] += (conSchNums[k] - acceptSchNums[k]);
				}
			}
		}
		
		int totalGsPlanNum = 0;
		int acceptGsPlanNum = 0;
		int noAcceptGsPlanNum = 0;
		int noAcceptSchNum = 0;//未验收学校
		int acceptSchNum = 0;//已验收学校
		for(int k = 0; k < dates.length; k++) {
			totalGsPlanNum += totalGsPlanNums[k];
			acceptGsPlanNum += acceptGsPlanNums[k];
			noAcceptGsPlanNum +=noAcceptGsPlanNums[k];
			noAcceptSchNum +=noAcceptSchNums[k];
			acceptSchNum +=acceptSchNums[k];
		}
		//验收数量及验收率
		acceptRate = 0;
		if(totalGsPlanNum > 0) {
			acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
			BigDecimal bd = new BigDecimal(acceptRate);
			acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			/*if (acceptRate > 100) {
				acceptRate = 100;
			}*/
		}
		
		//验收率的计算改为：已验收的学校数量/应验收学校的数量*100%。应验收学校的数量=未验收学校数据+已验收学校的数量
		float schAcceptRate = 0;
		int totalSchNum = acceptSchNum + noAcceptSchNum;
		if(totalGsPlanNum > 0) {
			schAcceptRate = 100 * ((float) acceptSchNum / (float) totalSchNum);
			BigDecimal bd = new BigDecimal(schAcceptRate);
			schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (schAcceptRate > 100) {
				schAcceptRate = 100;
			}
		}
				
		acceptInfo.setNoAccDistrNum(noAcceptGsPlanNum);
		acceptInfo.setAcceptRate(acceptRate);
		
		//应验收学校
		acceptInfo.setShouldAccSchNum(totalSchNum);
		//未验收学校
		acceptInfo.setNoAccSchNum(noAcceptSchNum);
		//已验收学校
		acceptInfo.setAccSchNum(acceptSchNum);
		//学校验收率
		acceptInfo.setSchAcceptRate(schAcceptRate);
	}
	
	/**
	 * 获取配送信息：未验收配送单个数、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAccDistrInfoFromHive(String deparmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			SumDataAcceptInfo acceptInfo,DbHiveGsService dbHiveGsService) {
		//配货单总数
		int totalGsPlanNum = 0;
		//已验收配货单
		int acceptGsPlanNum = 0;
		//未验收配货单
		int noAcceptGsPlanNum = 0;
		//已确认学校
		int acceptSchNum = 0;
		//未确认学校
		int noAcceptSchNum = 0;
		//验收率
		float acceptRate = 0;
		
		// 当天各区配货计划总数量
		String startDate = dates[dates.length - 1];
		String endDate = dates[0];
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
		List<SchGsCommon> dishList = new ArrayList<>();
		dishList = dbHiveGsService.getGsList(DataKeyConfig.talbeLedgerMasterTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,deparmentId,null, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchGsCommon schDishCommon: dishList) {
				//区域为空，代表全市数据，此处去除
				if(CommonUtil.isEmpty(schDishCommon.getDistId())) {
					continue;
				}
				if(schDishCommon.getDisSealStatus() ==null || "-1".equals(schDishCommon.getDisSealStatus())) {
					if(schDishCommon.getHaulStatus() ==null) {
						totalGsPlanNum += schDishCommon.getTotal();
						
					}else if(schDishCommon.getHaulStatus() == 3) {
						//已验收
						acceptSchNum +=schDishCommon.getSchoolTotal();
						acceptGsPlanNum += schDishCommon.getTotal();
					}else {
						//未验收：haul_status = -4 or haul_status = -2 or haul_status = -1 or haul_status = 0 or haul_status = 1 or haul_status = 2
						noAcceptSchNum +=schDishCommon.getSchoolTotal();
						noAcceptGsPlanNum += schDishCommon.getTotal();
					}
				}
			}
		}
		
		//验收数量及验收率
		acceptRate = 0;
		if(totalGsPlanNum > 0) {
			acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
			BigDecimal bd = new BigDecimal(acceptRate);
			acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			/*if (acceptRate > 100) {
				acceptRate = 100;
			}*/
		}
		
		//验收率的计算改为：已验收的学校数量/应验收学校的数量*100%。应验收学校的数量=未验收学校数据+已验收学校的数量
		float schAcceptRate = 0;
		int totalSchNum = acceptSchNum + noAcceptSchNum;
		if(totalGsPlanNum > 0) {
			schAcceptRate = 100 * ((float) acceptSchNum / (float) totalSchNum);
			BigDecimal bd = new BigDecimal(schAcceptRate);
			schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (schAcceptRate > 100) {
				schAcceptRate = 100;
			}
		}
				
		acceptInfo.setNoAccDistrNum(noAcceptGsPlanNum);
		acceptInfo.setAcceptRate(acceptRate);
		
		//应验收学校
		acceptInfo.setShouldAccSchNum(totalSchNum);
		//未验收学校
		acceptInfo.setNoAccSchNum(noAcceptSchNum);
		//已验收学校
		acceptInfo.setAccSchNum(acceptSchNum);
		//学校验收率
		acceptInfo.setSchAcceptRate(schAcceptRate);
	}

	/**
	 * 获取排菜数据
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private SumDataDishInfo getDishInfo(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList) {
		String key = "";
		String keyVal = "";
		String fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> platoonFeedTotalMap = null;
		int dateCount = dates.length;
		int[]totalMealSchNums = new int[dateCount];
		int[]distDishSchNums = new int[dateCount];
		int[]distNoDishSchNums = new int[dateCount];
		int[]distNoServeFoodSchNums = new int[dateCount];
		float distDishRate = 0;
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]  + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(platoonFeedTotalMap == null) {   
			}
			if(platoonFeedTotalMap != null) {
				for(String curKey : platoonFeedTotalMap.keySet()) {
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						String curDistId = curTdd.getId();
						//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						if(distId != null && curDistId.compareTo(distId) != 0) {
							continue ;
						}
						// 区域排菜学校供餐数
						fieldPrefix = curDistId + "_";
						int mealSchNum = 0;
						int dishSchNum = 0;
						int noDishSchNum = 0;
						int distNoServeFoodSchNum = 0;
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 3)
							{
								keyVal = platoonFeedTotalMap.get(curKey);
								if(keyVal == null || "".equals(keyVal) || !StringUtils.isNumeric(keyVal)) {
									continue;
								}
								if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("已排菜")) {
									if(keyVal != null && !"".equals(keyVal)) {
										mealSchNum = Integer.parseInt(keyVal);
										dishSchNum = mealSchNum;
									}
								}
								else if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("未排菜")) {
									if(keyVal != null && !"".equals(keyVal)) {
										mealSchNum = Integer.parseInt(keyVal);
										noDishSchNum = mealSchNum;
									}
								}else if (curKeys[1].equalsIgnoreCase("不供餐")) {
									distNoServeFoodSchNum =Integer.parseInt(keyVal);
								}
							}
						}
						totalMealSchNums[k] += mealSchNum;
						distDishSchNums[k] += dishSchNum;
						distNoDishSchNums[k] += noDishSchNum;
						distNoServeFoodSchNums[k] +=distNoServeFoodSchNum;
					}
				}
			}
		}
		
		// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
		int totalDistSchNum = 0;
		int distDishSchNum = 0;
		int distNoDishSchNum = 0;
		int distNoServeFoodSchNum = 0;
		for (int k = 0; k < dates.length; k++) {
			totalDistSchNum += totalMealSchNums[k];
			distDishSchNum += distDishSchNums[k];
			distNoDishSchNum += distNoDishSchNums[k];
			distNoServeFoodSchNum+=distNoServeFoodSchNums[k];
		}
		distDishRate = 0;
		if(totalDistSchNum > 0) {
			distDishRate = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
			
		
		SumDataDishInfo dishInfo = new SumDataDishInfo();
		//应排菜学校
		dishInfo.setMealSchNum(totalDistSchNum);
		//未排菜校数
		dishInfo.setNoDishSchNum(distNoDishSchNum);
		//已排菜学校数
		dishInfo.setDishSchNum(distDishSchNum);
		//未排菜学校数量
		dishInfo.setNoDishDayNum(distNoDishSchNum);
		//排菜率
		dishInfo.setDishRate(distDishRate);
		return dishInfo;
	}
	
	/**
	 * 获取排菜数据
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private SumDataDishInfo getDishInfoFromHive(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			DbHiveDishService dbHiveDishService) {
		// 当天排菜学校总数
		int dateCount = dates.length;
		float distDishRate = 0;
		// 当天各区排菜学校数量
		String startDate = dates[dates.length - 1];
		String endDate = dates[0];
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
		
		int totalSchNum = 0;
		int totalDistSchNum = 0;
		int distDishSchNum = 0;
		int distNoDishSchNum = 0;
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,departmentId,null, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				if(CommonUtil.isEmpty(schDishCommon.getDistId())) {
					continue;
				}
				if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if((schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1)) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNum += schDishCommon.getTotal();
						}
					}
				}else {
					if(schDishCommon.getHaveClass() != 1) {
						continue;
					}
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//应排菜数量的统计数据，
							//判断area,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1
							totalDistSchNum += schDishCommon.getTotal();
						}else{
							if(schDishCommon.getHavePlatoon() == 1) {
								//已排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =1
								distDishSchNum += schDishCommon.getTotal();
							}else if(schDishCommon.getHavePlatoon() == 0) {
								//未排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =0
								distNoDishSchNum += schDishCommon.getTotal();
							}
						}
					}
				}
			}
		}
		
		// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
		distDishRate = 0;
		if(totalDistSchNum > 0) {
			distDishRate = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
			
		
		SumDataDishInfo dishInfo = new SumDataDishInfo();
		//应排菜学校
		dishInfo.setMealSchNum(totalDistSchNum);
		//未排菜校数
		dishInfo.setNoDishSchNum(distNoDishSchNum);
		//已排菜学校数
		dishInfo.setDishSchNum(distDishSchNum);
		//未排菜学校数量
		dishInfo.setNoDishDayNum(distNoDishSchNum);
		//排菜率
		dishInfo.setDishRate(distDishRate);
		return dishInfo;
	}

	/**
	 * 获取监管学校总数
	 * @param distId
	 * @return
	 */
	private Integer getSupSchNum(String departmentId,String distId) {
		Integer supSchNum = 0;
		String key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		String keyVal = null;
		if(StringUtils.isEmpty(distId)) {
			//如果区域是空，数据则从从schoolData：shanghai中获取，否则从指定区域获取
			keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, "shanghai");
		}else {
			//如果区域不为空，则去除区域学校总数
			keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, "area_"+distId);
		}
		if(keyVal != null) {
			supSchNum = Integer.parseInt(keyVal);
		}
		return supSchNum;
	}
	

	/**
	 * 投诉举报详情模型函数
	 * @param token
	 * @param distName
	 * @param prefCity
	 * @param province
	 * @param startDate
	 * @param endDate
	 * @param db1Service
	 * @param db2Service
	 * @param saasService
	 * @param dbHiveWarnService
	 * @param dbHiveRecyclerWasteService
	 * @param dbHiveDishService
	 * @param dbHiveGsService
	 * @return
	 */
	public SumDataInfoDTO appModFunc(String token, String distName, String prefCity, String province,String startDate, String endDate, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveWarnService dbHiveWarnService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService,DbHiveDishService dbHiveDishService,DbHiveGsService dbHiveGsService ) {
		
		SumDataInfoDTO sumDataInfoDTO = null;
		//真实数据
		if(isRealData) {       
			// 日期
			String[] dates = null;
			
			//开始时间和结束时间有一个为空，一个不为空，则开始时间和结束时间一致
			if((startDate==null || "".equals(startDate)) && (endDate!=null && !"".equals(endDate))) {
				startDate = endDate;
			}else if((endDate==null || "".equals(endDate)) && (startDate!=null && !"".equals(startDate))) {
				endDate = startDate;
			}
			
			// 按照当天日期获取数据
			if (startDate == null || endDate == null) { 
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			}else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(startDate);
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(endDate);
				int days = Days.daysBetween(startDt, endDt).getDays() + 1;
				dates = new String[days];
				for (int i = 0; i < days; i++) {
					dates[i] = endDt.minusDays(i).toString("yyyy-MM-dd");
				}
			}
			for (int i = 0; i < dates.length; i++) {
				logger.info("dates[" + i + "] = " + dates[i]);
			}
			// 省或直辖市
			if(province == null) {
				province = "上海市";
			}
			// 参数查找标识
			boolean bfind = false;
			String distId = null;
			
			// 餐厨垃圾学校回收列表函数
			Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
			
			String departmentId = null;
			
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) { 
				// 按区域，省或直辖市处理
				List<TEduDistrictDo> tedList = db1Service.getListByDs1IdName();
				if(tedList != null) {
					// 查找是否存在该区域和省市
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						if (curTdd.getId().compareTo(distName) == 0) {
							bfind = true;
							distId = curTdd.getId();
							break;
						}
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(departmentId == null) {
						//获取用户权限区域ID
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  
					}
					sumDataInfoDTO = sumDataInfoFunc(departmentId,distId, dates,target, tedList, db1Service, saasService,dbHiveWarnService,
							dbHiveRecyclerWasteService,dbHiveDishService,dbHiveGsService);
				}
			} else if (distName == null && prefCity == null && province != null) { 
				// 按省或直辖市处理
				List<TEduDistrictDo> tedList = null;
				if (province.compareTo("上海市") == 0) {
					tedList = db1Service.getListByDs1IdName();
					if(tedList != null) {
						bfind = true;
					}
					distId = null;
				}
				if (bfind) {
					if(departmentId == null) {
						//获取用户权限区域ID
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  
					}
					// 餐厨垃圾学校回收列表函数
					sumDataInfoDTO = sumDataInfoFunc(departmentId,distId, dates,target, tedList, db1Service, saasService,dbHiveWarnService,
							dbHiveRecyclerWasteService,dbHiveDishService,dbHiveGsService);
				}
			} else if (distName != null && prefCity != null && province != null) { 
				// 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { 
				// 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}			
		}
		else {    //模拟数据
			//模拟数据函数
			sumDataInfoDTO = new SumDataInfoDTO();
		}		

		
		return sumDataInfoDTO;
	}
}
