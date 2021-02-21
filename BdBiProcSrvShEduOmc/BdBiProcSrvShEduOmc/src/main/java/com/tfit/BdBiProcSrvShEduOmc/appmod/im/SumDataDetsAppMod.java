package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonRecs;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SumDataDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.SumDataDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsCommon;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//汇总数据详情列表应用模型
public class SumDataDetsAppMod {
	private static final Logger logger = LogManager.getLogger(SumDataDetsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//二级排序条件
	final String[] methods0 = {"getSubLevel", "getCompDep"};
	final String[] sorts0 = {"asc", "desc"};
	final String[] dataTypes0 = {"String", "String"};
		
	final String[] methods1 = {"getDistName", "getDishDate"};
	final String[] sorts1 = {"asc", "asc"};
	final String[] dataTypes1 = {"String", "String"};
	
	//时间坐标个数
	final int timeCoordNum = 7;
	
	//最小供餐学校数量域值
	final int minMealSchNumThre = 0;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 2;
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
		
	//数组数据初始化
	String[] dishDate_Array = {"2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04"};
	String[] subLevel_Array = {"区属", "区属", "区属"};
	String[] compDep_Array = {"黄浦区教育局", "徐汇区教育局", "闵行区教育局"};
	String[] distName_Array = {"黄浦区", "徐汇区", "闵行区"};
	int[] mealSchNum_Array = {150, 150, 150};
	int[] dishSchNum_Array = {60, 60, 60};
	int[] noDishSchNum_Array = {90, 90, 90};
	float[] dishRate_Array = {(float) 60.00, (float) 60.00, (float) 60.00};
	int[] totalGsPlanNum_Array = {150, 150, 150};
	int[] noAcceptGsPlanNum_Array = {60, 60, 60};
	int[] acceptGsPlanNum_Array = {90, 90, 90};
	float[] acceptRate_Array = {(float) 60.00, (float) 60.00, (float) 60.00};
	int[] totalDishNum_Array = {150, 150, 150};
	int[] noRsDishNum_Array = {60, 60, 60};
	int[] rsDishNum_Array = {90, 90, 90};
	float[] rsRate_Array = {(float) 60.00, (float) 60.00, (float) 60.00};
	int[] totalWarnNum_Array = {150, 150, 150};
	int[] noProcWarnNum_Array = {60, 60, 60};
	int[] elimWarnNum_Array = {90, 90, 90};
	float[] warnProcRate_Array = {(float) 60.00, (float) 60.00, (float) 60.00};	
	int[] totalKwRecNum_Array = {150, 150, 150};
	int[] kwSchRecNum_Array = {60, 60, 60};
	int[] kwRmcRecNum_Array = {90, 90, 90};
	int[] totalWoRecNum_Array = {150, 150, 150};
	int[] woSchRecNum_Array = {60, 60, 60};
	int[] woRmcRecNum_Array = {90, 90, 90};
	
	//模拟数据函数
	private SumDataDetsDTO SimuDataFunc() {
		SumDataDetsDTO sddDto = new SumDataDetsDTO();
		//时戳
		sddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//汇总数据详情列表模拟数据
		List<SumDataDets> sumDataDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < dishDate_Array.length; i++) {
			SumDataDets sdd = new SumDataDets();
			sdd.setDishDate(dishDate_Array[i]);
			sdd.setSubLevel(subLevel_Array[i]);
			sdd.setCompDep(compDep_Array[i]);
			sdd.setDistName(distName_Array[i]);
			sdd.setMealSchNum(mealSchNum_Array[i]);
			sdd.setDishSchNum(dishSchNum_Array[i]);
			sdd.setNoDishSchNum(noDishSchNum_Array[i]);
			sdd.setDishRate(dishRate_Array[i]);
			sdd.setTotalGsPlanNum(totalGsPlanNum_Array[i]);
			sdd.setNoAcceptGsPlanNum(noAcceptGsPlanNum_Array[i]);
			sdd.setAcceptGsPlanNum(acceptGsPlanNum_Array[i]);
			sdd.setAcceptRate(acceptRate_Array[i]);
			sdd.setTotalDishNum(totalDishNum_Array[i]);
			sdd.setNoRsDishNum(noRsDishNum_Array[i]);
			sdd.setRsDishNum(rsDishNum_Array[i]);
			sdd.setRsRate(rsRate_Array[i]);
			sdd.setTotalWarnNum(totalWarnNum_Array[i]);
			sdd.setNoProcWarnNum(noProcWarnNum_Array[i]);
			sdd.setElimWarnNum(elimWarnNum_Array[i]);
			sdd.setWarnProcRate(warnProcRate_Array[i]);
			sdd.setTotalKwRecNum(Float.valueOf(totalKwRecNum_Array[i]));
			sdd.setKwSchRecNum(Float.valueOf(kwSchRecNum_Array[i]));
			sdd.setKwRmcRecNum(Float.valueOf(kwRmcRecNum_Array[i]));
			sdd.setTotalWoRecNum(Float.valueOf(totalWoRecNum_Array[i]));
			sdd.setWoSchRecNum(Float.valueOf(woSchRecNum_Array[i]));
			sdd.setWoRmcRecNum(Float.valueOf(woRmcRecNum_Array[i]));
			sumDataDets.add(sdd);
		}
		//设置数据
		sddDto.setSumDataDets(sumDataDets);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = dishDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		sddDto.setPageInfo(pageInfo);
		//消息ID
		sddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return sddDto;
	}
	
	//汇总数据详情列表函数按主管部门
	private SumDataDetsDTO sumDataDetsByCompDep(String departmentId,String distIdorSCName, String[] dates,Integer target, List<TEduDistrictDo> tedList, 
			int subLevel, int compDep, String subDistName,
			String subLevels,String compDeps,DbHiveWarnService dbHiveWarnService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService,
			DbHiveDishService dbHiveDishService,DbHiveGsService dbHiveGsService) {
		SumDataDetsDTO sddDto = new SumDataDetsDTO();
		List<SumDataDets> sumDataDets = new ArrayList<>();
		SumDataDets sdd = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		int i, j, k, subLevelCount = 4, compDepCount = 0, maxCompDepCount = AppModConfig.compDepIdToNameMap3.size();
		//排菜数据
		int distCount = tedList.size(), dateCount = dates.length;
		int[][][] totalMealSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				distDishSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				distNoDishSchNums = new int[dateCount][subLevelCount][maxCompDepCount];
		int[][][] standardNums = new int[dateCount][subLevelCount][maxCompDepCount];
		int[][][] supplementNums = new int[dateCount][subLevelCount][maxCompDepCount];
		int[][][] beOverdueNums = new int[dateCount][subLevelCount][maxCompDepCount];
		int[][][] noDataNums = new int[dateCount][subLevelCount][maxCompDepCount];
		
		float[][] distDishRates = new float[subLevelCount][maxCompDepCount];
		//验收数据
		Map<String, String> distributionTotalMap = null;
		int[][][] totalGsPlanNums = new int[dateCount][subLevelCount][maxCompDepCount];
		int[][][] noAcceptGsPlanNums = new int[dateCount][subLevelCount][maxCompDepCount]; 
		int[][][] acceptGsPlanNums = new int[dateCount][subLevelCount][maxCompDepCount];
		float[][] acceptRates = new float[subLevelCount][maxCompDepCount];
		//学校验收信息
		int[][][] shouldAcceptSchNums = new int[dateCount][subLevelCount][maxCompDepCount];
		int[][][] acceptSchNums = new int[dateCount][subLevelCount][maxCompDepCount]; 
		int[][][] noAcceptSchNums = new int[dateCount][subLevelCount][maxCompDepCount];
		float[][] schAcceptRates = new float[subLevelCount][maxCompDepCount];
		//菜品留样
		Map<String, String> gcRetentiondishtotalMap = null;
		int[][][] totalDishNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				dishRsDishNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				dishNoRsDishNums = new int[dateCount][subLevelCount][maxCompDepCount];
		float[][] dishRsRates = new float[subLevelCount][maxCompDepCount];
		
		//学校留样
		int[][][] rsSchNums = new int[dateCount][subLevelCount][maxCompDepCount];
		int[][][] noRsSchNums = new int[dateCount][subLevelCount][maxCompDepCount];
		float[][] schRsRates = new float[subLevelCount][maxCompDepCount];
		
		//证照逾期处理
		int[][][] totalWarnNums = new int[dateCount][subLevelCount][maxCompDepCount],
				noProcWarnNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				elimWarnNums = new int[dateCount][subLevelCount][maxCompDepCount];
		float[][] warnProcRates = new float[subLevelCount][maxCompDepCount];			
		//区域ID到索引映射
		Map<String, Integer> distIdToIdxMap = new HashMap<>();
		for(i = 0; i < distCount; i++) {
			distIdToIdxMap.put(tedList.get(i).getId(), i);
		}
		distIdToIdxMap.put("-", i);
		//餐厨垃圾回收
		float[][][] kwSchRcNums = new float[dateCount][subLevelCount][maxCompDepCount];			
		//废弃油脂回收
		float[][][] woSchRcNums = new float[dateCount][subLevelCount][maxCompDepCount];			
		
		List<Object> subLevelList=CommonUtil.changeStringToList(subLevels);
		List<Object> compDepList=CommonUtil.changeStringToList(compDeps);
		
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		
		// 各天各区排菜学校数量
		for (k = 0; k < dates.length; k++) {
			
			if(days >= 2) {
				//1天之前数据从hive库中获取，在时间循环外部统一获取
			}else {
				//排菜学校
				getDishByCompDep(dates,departmentId, subLevel, compDep, k, subLevelCount, totalMealSchNums, distDishSchNums,
						distNoDishSchNums, subLevelList, compDepList);
				
				//验收数据
				getDistributionByCompDepFromRedis(dates,departmentId, subLevel, compDep, k, subLevelCount, totalGsPlanNums,
						noAcceptGsPlanNums, acceptGsPlanNums, shouldAcceptSchNums, acceptSchNums, noAcceptSchNums,
						subLevelList, compDepList);
			}
			//菜品留样
			getGcRetentiondishByCompDepFromRedis(departmentId,dates, subLevel, compDep, k, subLevelCount, totalDishNums,
					dishRsDishNums, dishNoRsDishNums, rsSchNums, noRsSchNums, subLevelList, compDepList);
			
			//证照逾期处理
			//2019.06.06注释，注释原因:预警信息改为从hive库中获取,再循环外部调用，调用方法：setWarnDataByCompDepTwo
			//fieldPrefix = setWarnDataByCompDep(dates, subLevel, compDep, fieldPrefix, k, subLevelCount,
			//		totalWarnNums, noProcWarnNums, elimWarnNums, subLevelList, compDepList);		
			
			//30天之前数据从hive中获取，30天之内及之后从redis中获取
			if(days >= 2) {
				//从hive库中获取，在日期循环后获取
			}else {
				//餐厨垃圾回收
				//学校回收桶数
				fieldPrefix = getRecyclerWasteByCompDepFromRedis(dates, subLevel, compDep, fieldPrefix, k, subLevelCount,
						kwSchRcNums, woSchRcNums, subLevelList, compDepList);
			}
			
			//预警
			setWarnDataByCompDepTwo(departmentId,dates[0], dates[dates.length-1], target, subLevel, maxCompDepCount, subLevelCount, 
					totalWarnNums, noProcWarnNums, elimWarnNums, subLevelList, compDepList, dbHiveWarnService);
			// 该日期段各区排菜数据、验收数据、菜品留样、证照逾期处理、餐厨垃圾回收和废弃油脂回收
			for (i = 0; i < subLevelCount; i++) {
				if(i == 0)
					compDepCount = 1;
				else if(i == 1)
					compDepCount = 2;
				else if(i == 2)
					compDepCount = 8;
				else if(i == 3)
					compDepCount = 16;
				else
					compDepCount = 0;
				for(j = 0; j < compDepCount; j++) {
					//判断是否按主管部门获取数据
					if(subLevel != -1) {
						if(i != subLevel)
							continue ;
						else {
							if(compDep != -1) {
								if(compDep != j)
									continue ;
							}
						}
					}else if(subLevelList!=null && subLevelList.size()>0) {
						if(!subLevelList.contains(String.valueOf(i))) {
							continue;
						}else {
							if(compDepList!=null && compDepList.size()>0) {
								if(!compDepList.contains(i+"_"+(j))) {
									continue ;
								}
							}
						}
					}
					// 设置前置域名
					String compDepId = "", compDepName = "其他";
					if(i < 3) {   //其他、部属、市属
						compDepId = String.valueOf(j);
						fieldPrefix = "masterid_" + i + "_slave_" + j;
						if(i == 1) {
							compDepName = AppModConfig.compDepIdToNameMap1.get(compDepId);
						}
						else if(i == 2) {
							compDepName = AppModConfig.compDepIdToNameMap2.get(compDepId);
						}
					}
					else if(i == 3) {  //区属
						compDepId = String.valueOf(j);								
						compDepName = AppModConfig.compDepIdToNameMap3.get(compDepId);
						fieldPrefix = "masterid_" + i + "_slave_" + compDepName;
					}
					// 区域学校排菜率
					if (totalMealSchNums[k][i][j] != 0) {
						distDishRates[i][j] = 100 * ((float) distDishSchNums[k][i][j] / (float) totalMealSchNums[k][i][j]);
						BigDecimal bd = new BigDecimal(distDishRates[i][j]);
						distDishRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (distDishRates[i][j] > 100) {
							distDishRates[i][j] = 100;
							distDishSchNums[k][i][j] = totalMealSchNums[k][i][j];
						}
					}
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + compDepName 
							+ "，排菜学校数量：" + distDishSchNums[k][i] 	+ "，供餐学校总数：" + totalMealSchNums[k][i] 
									+ "，排菜率：" + distDishRates[i] + "，field = " + field);				
					//配货单验收率
					if (totalGsPlanNums[k][i][j] != 0) {
						//验收率
						acceptRates[i][j] = 100 * ((float) acceptGsPlanNums[k][i][j] / (float) totalGsPlanNums[k][i][j]);
						BigDecimal bd = new BigDecimal(acceptRates[i][j]);
						acceptRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (acceptRates[i][j] > 100) {
							acceptRates[i][j] = 100;
						}
					}
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + compDepName 
							+ "，配货计划总数：" + totalGsPlanNums[k][i] + "，验收数：" + acceptGsPlanNums[k][i] + "，验收率：" + acceptRates[i]);
					// 区域留样率
					if (totalDishNums[k][i][j] != 0) {
						dishRsRates[i][j] = 100 * ((float) dishRsDishNums[k][i][j] / (float) totalDishNums[k][i][j]);
						BigDecimal bd = new BigDecimal(dishRsRates[i][j]);
						dishRsRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (dishRsRates[i][j] > 100) {
							dishRsRates[i][j] = 100;
							dishRsDishNums[k][i][j] = totalDishNums[k][i][j];
						}
					}
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + compDepName + "，菜品数量：" + totalDishNums[k][i]
							+ "，已留样菜品数：" + dishRsDishNums[k][i] + "，未留样菜品数：" + dishNoRsDishNums[k][i] + "，留样率：" + dishRsRates[i] + "，field = "
							+ field);
					//证照逾期处理
					int totalWarnNum = totalWarnNums[k][i][j], elimWarnNum = elimWarnNums[k][i][j];
					warnProcRates[i][j] = 0;
					if(totalWarnNum > 0) {
						warnProcRates[i][j] = 100 * ((float) elimWarnNum / (float) totalWarnNum);
						BigDecimal bd = new BigDecimal(warnProcRates[i][j]);
						warnProcRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (warnProcRates[i][j] > 100)
							warnProcRates[i][j] = 100;
					}
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) 
					+ "，主管部门：" + compDepName + "，预警数量：" + totalWarnNums[k][i]
							+ "，已处理预警数：" + elimWarnNums[k][i] + "，未处理预警数：" + noProcWarnNums[k][i] 
									+ "，处理率：" + warnProcRates[i] + "，field = "
							+ field);					
					//餐厨垃圾回收
					//餐厨垃圾学校回收
					BigDecimal bd = new BigDecimal(kwSchRcNums[k][i][j]);
					kwSchRcNums[k][i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + compDepName 
							+ "，餐厨垃圾学校回收数量：" + kwSchRcNums[k][i][j] + " 桶" + "，field = " + field);				
					//废弃油脂回收
					//废弃油脂学校回收
					bd = new BigDecimal(woSchRcNums[k][i][j]);
					woSchRcNums[k][i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + compDepName 
							+ "，废弃油脂学校回收数量：" + woSchRcNums[k][i] + " 桶" + "，field = " + field);
				}
			}
		}
		
		//排菜学校
		if(days >= 2) {
			//1天之前数据从hive库中获取，在时间循环外部统一获取
			getDishByCompDepFromHive(departmentId,dates, subLevel, maxCompDepCount, subLevelCount, totalMealSchNums, distDishSchNums, 
					distNoDishSchNums,
					standardNums,supplementNums,beOverdueNums,noDataNums,
					subLevelList, compDepList, dbHiveDishService);
			
			//配货
			getDistributionByCompDepFromHive(distIdorSCName, dates, subLevel, maxCompDepCount, subLevelCount,
					totalGsPlanNums, noAcceptGsPlanNums, acceptGsPlanNums, shouldAcceptSchNums, acceptSchNums, 
					noAcceptSchNums, subLevelList, compDepList, departmentId,null,dbHiveGsService);
		}
		
		//从hive库中获取
		if(days >= 2) {
			getRecyclerWasteByCompFromHive(distIdorSCName, dates, subLevel, compDep, dbHiveRecyclerWasteService, field,
					subLevelCount, kwSchRcNums, woSchRcNums, subLevelList, compDepList, days);
		}
		
		for (i = 3; i >=0; i--) {
			if(i == 0)
				compDepCount = 0;
			else if(i == 1)
				compDepCount = 1;
			else if(i == 2)
				compDepCount = 7;
			else if(i == 3)
				compDepCount = 16;
			else
				compDepCount = 0;
			for(j = compDepCount; j >=0; j--) {
				//判断是否按主管部门获取数据
				if(subLevel != -1) {
					if(i != subLevel)
						continue ;
					else {
						if(compDep != -1) {
							if(compDep != j)
								continue ;
						}
					}
				}else if(subLevelList!=null && subLevelList.size()>0) {
					if(!subLevelList.contains(String.valueOf(i))) {
						continue;
					}else {
						if(compDepList!=null && compDepList.size()>0) {
							if(!compDepList.contains(i+"_"+(j))) {
								continue ;
							}
						}
					}
				}
				// 设置前置域名
				String compDepId = "", compDepName = "其他";
				if(i < 3) {   //其他、部属、市属
					compDepId = String.valueOf(j);
					fieldPrefix = "masterid_" + i + "_slave_" + j;
					if(i == 1) {
						compDepName = AppModConfig.compDepIdToNameMap1.get(compDepId);
					}
					else if(i == 2) {
						compDepName = AppModConfig.compDepIdToNameMap2.get(compDepId);
					}
				}
				else if(i == 3) {  //区属
					compDepId = String.valueOf(j);								
					compDepName = AppModConfig.compDepIdToNameMap3.get(compDepId);
					fieldPrefix = "masterid_" + i + "_slave_" + compDepName;
				}
				for (k = 0; k < dates.length; k++) {
					sdd = new SumDataDets();
					//日期
					sdd.setDishDate(dates[k].replaceAll("-", "/"));
					//所属
					sdd.setSubLevel(AppModConfig.subLevelIdToNameMap.get(i));
					//主管部门
					sdd.setCompDep(compDepName);
					//应排菜数数、已排菜数、未排菜数、排菜率
					int totalDistSchNum = 0, distDishSchNum = 0, distNoDishSchNum = 0;					
					totalDistSchNum = totalMealSchNums[k][i][j];
					distDishSchNum = distDishSchNums[k][i][j];
					distNoDishSchNum = distNoDishSchNums[k][i][j];
					sdd.setMealSchNum(totalDistSchNum);
					sdd.setDishSchNum(distDishSchNum);
					sdd.setNoDishSchNum(distNoDishSchNum);
					distDishRates[i][j] = 0;
					if(totalDistSchNum > 0) {
						distDishRates[i][j] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
						BigDecimal bd = new BigDecimal(distDishRates[i][j]);
						distDishRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (distDishRates[i][j] > 100)
							distDishRates[i][j] = 100;
					}
					sdd.setDishRate(distDishRates[i][j]);	
					//1 表示规范录入
					sdd.setStandardNum(standardNums[k][i][j]);
					//2 表示补录
					sdd.setSupplementNum(supplementNums[k][i][j]);
					//3 表示逾期补录
					sdd.setBeOverdueNum(beOverdueNums[k][i][j]);
					//4 表示无数据
					sdd.setNoDataNum(noDataNums[k][i][j]);
					
					
					//验收数据：配货计划总数、待验收数、	已验收数、验收率
					int totalGsPlanNum = 0, noAcceptGsPlanNum = 0, acceptGsPlanNum = 0;
					totalGsPlanNum = totalGsPlanNums[k][i][j];
					noAcceptGsPlanNum = noAcceptGsPlanNums[k][i][j];
					acceptGsPlanNum = acceptGsPlanNums[k][i][j];
					//验收数量及验收率
					acceptRates[i][j] = 0;
					if(totalGsPlanNum > 0) {
						acceptRates[i][j] = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
						BigDecimal bd = new BigDecimal(acceptRates[i][j]);
						acceptRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (acceptRates[i][j] > 100)
							acceptRates[i][j] = 100;
					}
					sdd.setTotalGsPlanNum(totalGsPlanNum);
					sdd.setNoAcceptGsPlanNum(noAcceptGsPlanNum);
					sdd.setAcceptGsPlanNum(acceptGsPlanNum);
					sdd.setAcceptRate(acceptRates[i][j]);
					//学校验收信息以及验收率
					int shouldAcceptSchNum = 0;
					int acceptSchNum = 0;
					int noAcceptSchNum = 0;
					shouldAcceptSchNum = shouldAcceptSchNums[k][i][j];
					acceptSchNum = acceptSchNums[k][i][j];
					noAcceptSchNum = noAcceptSchNums[k][i][j];
					sdd.setShouldAcceptSchNum(shouldAcceptSchNum);
					sdd.setAcceptSchNum(acceptSchNum);
					sdd.setNoAcceptSchNum(noAcceptSchNum);
					
					schAcceptRates[i][j] = 0;
					if(shouldAcceptSchNum > 0) {
						schAcceptRates[i][j] = 100 * ((float) acceptSchNum / (float) shouldAcceptSchNum);
						BigDecimal bd = new BigDecimal(schAcceptRates[i][j]);
						schAcceptRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (schAcceptRates[i][j] > 100)
							schAcceptRates[i][j] = 100;
					}
					sdd.setSchAcceptRate(schAcceptRates[i][j]);
					
					//菜品总数、未留样数、已留样数、留样率
					int totalDishNum = 0, dishRsDishNum = 0, dishNoRsDishNum = 0;					
					totalDishNum = totalDishNums[k][i][j];
					dishRsDishNum = dishRsDishNums[k][i][j];
					dishNoRsDishNum = dishNoRsDishNums[k][i][j];
					sdd.setTotalDishNum(totalDishNum);
					sdd.setRsDishNum(dishRsDishNum);
					sdd.setNoRsDishNum(dishNoRsDishNum);
					dishRsRates[i][j] = 0;
					if(totalDishNum > 0) {
						dishRsRates[i][j] = 100 * ((float) dishRsDishNum / (float) totalDishNum);
						BigDecimal bd = new BigDecimal(dishRsRates[i][j]);
						dishRsRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (dishRsRates[i][j] > 100)
							dishRsRates[i][j] = 100;
					}
					sdd.setRsRate(dishRsRates[i][j]);
					
					//学校留样相关信息
					int shouldRsSchNum = 0;
					int rsSchNum = 0;
					int noRsSchNum = 0;
					
					rsSchNum = rsSchNums[k][i][j];
					noRsSchNum = noRsSchNums[k][i][j];
					shouldRsSchNum = rsSchNum + noRsSchNum;
					sdd.setShouldRsSchNum(shouldRsSchNum);
					sdd.setRsSchNum(rsSchNum);
					sdd.setNoRsSchNum(noRsSchNum);
					schRsRates[i][j] = 0;
					if(shouldRsSchNum > 0) {
						schRsRates[i][j] = 100 * ((float) rsSchNum / (float) shouldRsSchNum);
						BigDecimal bd = new BigDecimal(schRsRates[i][j]);
						schRsRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (schRsRates[i][j] > 100)
							schRsRates[i][j] = 100;
					}
					sdd.setSchRsRate(schRsRates[i][j]);
					
					
					//预警总数、待处理预警数、已消除预警数、预警处理率
					int totalWarnNum = 0, noProcWarnNum = 0, elimWarnNum = 0;					
					totalWarnNum = totalWarnNums[k][i][j];
					noProcWarnNum = noProcWarnNums[k][i][j];
					elimWarnNum = elimWarnNums[k][i][j];
					sdd.setTotalWarnNum(totalWarnNum);
					sdd.setNoProcWarnNum(totalWarnNum - elimWarnNum);
					sdd.setElimWarnNum(elimWarnNum);
					warnProcRates[i][j] = 0;
					if(totalWarnNum > 0) {
						warnProcRates[i][j] = 100 * ((float) elimWarnNum / (float) totalWarnNum);
						BigDecimal bd = new BigDecimal(warnProcRates[i][j]);
						warnProcRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (warnProcRates[i][j] > 100)
							warnProcRates[i][j] = 100;
					}
					sdd.setWarnProcRate(warnProcRates[i][j]);
					//餐厨垃圾学校回收数量
					float kwSchRcNum = 0, kwRmcRcNum = 0, kwTotalRcNum = 0;
					kwSchRcNum = kwSchRcNums[k][i][j];
					sdd.setTotalKwRecNum( new BigDecimal(kwTotalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
					sdd.setKwSchRecNum( new BigDecimal(kwSchRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
					sdd.setKwRmcRecNum( new BigDecimal(kwRmcRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
					//废弃油脂学校回收数量
					float woSchRcNum = 0, woRmcRcNum = 0, woTotalRcNum = 0;					
					woSchRcNum = woSchRcNums[k][i][j];
					sdd.setTotalWoRecNum( new BigDecimal(woTotalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
					sdd.setWoSchRecNum( new BigDecimal(woSchRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
					sdd.setWoRmcRecNum( new BigDecimal(woRmcRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
					
					sumDataDets.add(sdd);
				}
			}
		}
		//排序
    	/*SortList<SumDataDets> sortList = new SortList<SumDataDets>();  
    	sortList.Sort(sumDataDets, methods0, sorts0, dataTypes0);*/
		//时戳
		sddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<SumDataDets> pageBean = new PageBean<SumDataDets>(sumDataDets, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		sddDto.setPageInfo(pageInfo);
		// 设置数据
		sddDto.setSumDataDets(pageBean.getCurPageData());
		// 消息ID
		sddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return sddDto;
	}

	private void getGcRetentiondishByCompDepFromRedis(String departmentId,String[] dates, int subLevel, int compDep, int k,
			int subLevelCount, int[][][] totalDishNums, int[][][] dishRsDishNums, int[][][] dishNoRsDishNums,
			int[][][] rsSchNums, int[][][] noRsSchNums, List<Object> subLevelList, List<Object> compDepList) {
		String key;
		int i;
		int j;
		int compDepCount;
		Map<String, String> gcRetentiondishtotalMap;
		key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
		}
		gcRetentiondishtotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(gcRetentiondishtotalMap != null) {
			String fieldPrefix = "";
			String keyVal = "";
			for(String curKey : gcRetentiondishtotalMap.keySet()) {
				for (i = 0; i < subLevelCount; i++) {
					if(i == 0)
						compDepCount = 1;
					else if(i == 1)
						compDepCount = 2;
					else if(i == 2)
						compDepCount = 8;
					else if(i == 3)
						compDepCount = 17;
					else
						compDepCount = 0;
					for(j = 0; j < compDepCount; j++) {
						//判断是否按主管部门获取数据
						if(subLevel != -1) {
							if(i != subLevel)
								continue ;
							else {
								if(compDep != -1) {
									if(compDep != j)
										continue ;
								}
							}
						}else if(subLevelList!=null && subLevelList.size()>0) {
							if(!subLevelList.contains(String.valueOf(i))) {
								continue;
							}else {
								if(compDepList!=null && compDepList.size()>0) {
									if(!compDepList.contains(i+"_"+(j))) {
										continue ;
									}
								}
							}
						}
						/**
						 * 菜品留样相关
						 */
						// 设置前置域名
						if(i < 3)
							fieldPrefix = "masterid_" + i + "_slave_" + j;
						else if(i == 3) {
							String compDepId = String.valueOf(j);								
							fieldPrefix = "masterid_" + i + "_slave_" + AppModConfig.compDepIdToNameMap3.get(compDepId);
						}
						// 区域菜品留样和未留样数
						if (curKey.indexOf(fieldPrefix) != -1) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 5)	{
								if(curKeys[4].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										dishRsDishNums[k][i][j] = Integer.parseInt(keyVal);
										totalDishNums[k][i][j] += dishRsDishNums[k][i][j];
									}
								}
								else if(curKeys[4].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										dishNoRsDishNums[k][i][j] = Integer.parseInt(keyVal);
										totalDishNums[k][i][j] += dishNoRsDishNums[k][i][j];
									}
								}
							}
						}
						
						/**
						 * 学校留样相关
						 */
						// 设置前置域名
						if(i < 3)
							fieldPrefix = "school-masterid_" + i + "_slave_" + j;
						else if(i == 3) {
							String compDepId = String.valueOf(j);								
							fieldPrefix = "school-masterid_" + i + "_slave_" + AppModConfig.compDepIdToNameMap3.get(compDepId);
						}
						// 区域菜品留样和未留样数
						if (curKey.indexOf(fieldPrefix) != -1) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 5)	{
								if(curKeys[4].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										rsSchNums[k][i][j] = Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[4].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										noRsSchNums[k][i][j] = Integer.parseInt(keyVal);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void getDistributionByCompDepFromRedis(String[] dates,String departmentId,int subLevel, int compDep, int k, int subLevelCount,
			int[][][] totalGsPlanNums, int[][][] noAcceptGsPlanNums, int[][][] acceptGsPlanNums,
			int[][][] shouldAcceptSchNums, int[][][] acceptSchNums, int[][][] noAcceptSchNums,
			List<Object> subLevelList, List<Object> compDepList) {
		int i;
		int j;
		int compDepCount;
		Map<String, String> distributionTotalMap;
		String key = dates[k]   + DataKeyConfig.distributionTotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
		}
		distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if (distributionTotalMap != null) {
			String field = "";
			String fieldPrefix = "";
			String keyVal = "";
			for (i = 0; i < subLevelCount; i++) {
				if(i == 0)
					compDepCount = 1;
				else if(i == 1)
					compDepCount = 2;
				else if(i == 2)
					compDepCount = 8;
				else if(i == 3)
					compDepCount = 17;
				else
					compDepCount = 0;
				for(j = 0; j < compDepCount; j++) {
					//判断是否按主管部门获取数据
					if(subLevel != -1) {
						if(i != subLevel)
							continue ;
						else {
							if(compDep != -1) {
								if(compDep != j)
									continue ;
							}
						}
					}else if(subLevelList!=null && subLevelList.size()>0) {
						if(!subLevelList.contains(String.valueOf(i))) {
							continue;
						}else {
							if(compDepList!=null && compDepList.size()>0) {
								if(!compDepList.contains(i+"_"+(j))) {
									continue ;
								}
							}
						}
					}
					// 设置前置域名
					if(i < 3)
						fieldPrefix = "masterid_" + i + "_slave_" + j;
					else if(i == 3) {
						String compDepId = String.valueOf(j);								
						fieldPrefix = "masterid_" + i + "_slave_" + AppModConfig.compDepIdToNameMap3.get(compDepId);
					}
					
					/**
					 * 配货计划验收信息：应验收配货计划、已验收配货计划、未验收配货计划、配货计划验收率
					 */
					// 区域配货计划总数
					for(int l = -2; l < 4; l++) {
						field = fieldPrefix + "_" + "status" + "_" + l;
						if(l == 3) { // 已验收数
							acceptGsPlanNums[k][i][j] = 0;
							keyVal = distributionTotalMap.get(field);
							if(keyVal != null) {
								acceptGsPlanNums[k][i][j] = Integer.parseInt(keyVal);
								if(acceptGsPlanNums[k][i][j] < 0)
									acceptGsPlanNums[k][i][j] = 0;
							}
							totalGsPlanNums[k][i][j] += acceptGsPlanNums[k][i][j];
						}
						else {   //未验收数
							keyVal = distributionTotalMap.get(field);
							int curGsPlanNum = 0;
							if(keyVal != null) {						
								curGsPlanNum = Integer.parseInt(keyVal);
								if(curGsPlanNum < 0)
									curGsPlanNum = 0;
							}
							// 未验收数
							noAcceptGsPlanNums[k][i][j] += curGsPlanNum;
							totalGsPlanNums[k][i][j] += curGsPlanNum;
						}
					}
					
					/**
					 * 学校验收信息：应验收学校、已验收学校、未验收学校、学校验收率
					 */
					// 设置前置域名 
					if(i < 3)
						fieldPrefix = "school-masterid_" + i + "_slave_" + j;
					else if(i == 3) {
						String compDepId = String.valueOf(j);								
						fieldPrefix = "school-masterid_" + i + "_slave_" + AppModConfig.compDepIdToNameMap3.get(compDepId);
					}
					// 区域配货计划总数
					for(int l = -2; l < 4; l++) {
						field = fieldPrefix + "_" + "status" + "_" + l;
						if(l == 3) { // 已验收数
							acceptSchNums[k][i][j] = 0;
							keyVal = distributionTotalMap.get(field);
							if(keyVal != null && !"".equals(keyVal) && !"null".equals(keyVal)) {
								acceptSchNums[k][i][j] = Integer.parseInt(keyVal);
								if(acceptSchNums[k][i][j] < 0)
									acceptSchNums[k][i][j] = 0;
							}
							shouldAcceptSchNums[k][i][j] += acceptSchNums[k][i][j];
						}
						else {   //未验收数
							keyVal = distributionTotalMap.get(field);
							int curGsPlanNum = 0;
							if(keyVal != null && !"".equals(keyVal) && !"null".equals(keyVal)) {						
								curGsPlanNum = Integer.parseInt(keyVal);
								if(curGsPlanNum < 0)
									curGsPlanNum = 0;
							}
							// 未验收数
							noAcceptSchNums[k][i][j] += curGsPlanNum;
							shouldAcceptSchNums[k][i][j] += curGsPlanNum;
						}
					}
				}
			}
		}
	}

	private void getDistributionByCompDepFromHive(String distIdorSCName,String[] dates, int subLevel, int compDep,int subLevelCount,
			int[][][] totalGsPlanNums, int[][][] noAcceptGsPlanNums, int[][][] acceptGsPlanNums,
			int[][][] shouldAcceptSchNums, int[][][] acceptSchNums, int[][][] noAcceptSchNums,
			List<Object> subLevelList, List<Object> compDepList,
			String departmentId,List<Object> departmentIdsList,
			DbHiveGsService dbHiveGsService) {
		int compDepCount;
		// 当天各区配货计划总数量
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();//配送计划数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();//已验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();//未验收数量
		//已指派
		Map<String,Integer> assignGsPlanNumsMap = new HashMap<String,Integer>();
		//已配送
		Map<String,Integer> dispGsPlanNumsMap = new HashMap<String,Integer>();
		
		// 时间段内各区配货计划详情
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
		dishList = dbHiveGsService.getGsList(DataKeyConfig.talbeLedgerMasterTotalD,listYearMonth, startDate, endDateAddOne, distIdorSCName, null, 
				-1, -1, null, null,departmentId,departmentIdsList, 3);
		if(dishList !=null && dishList.size() > 0) {
			for(SchGsCommon schDishCommon: dishList) {
				String key = schDishCommon.getActionDate() + "_" +schDishCommon.getDepartmentMasterId() + "_" +schDishCommon.getDepartmentSlaveIdName();
				
				if(schDishCommon.getDisSealStatus() ==null || "-1".equals(schDishCommon.getDisSealStatus())) {
					if(schDishCommon.getHaulStatus() ==null) {
						totalGsPlanNumMap.put(key, 
								(totalGsPlanNumMap.get(key)==null?0:totalGsPlanNumMap.get(key)) 
								+ schDishCommon.getTotal());
						
					}else if(schDishCommon.getHaulStatus() == 3) {
						//已验收
						acceptSchNumsMap.put(key, 
								(acceptSchNumsMap.get(key)==null?0:acceptSchNumsMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
						
						acceptGsPlanNumMap.put(key, 
								(acceptGsPlanNumMap.get(key)==null?0:acceptGsPlanNumMap.get(key)) 
								+ schDishCommon.getTotal());
					}else {
						//未验收：haul_status = -4 or haul_status = -2 or haul_status = -1 or haul_status = 0 or haul_status = 1 or haul_status = 2
						noAcceptSchNumsMap.put(key, 
								(noAcceptSchNumsMap.get(key)==null?0:noAcceptSchNumsMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
						
						noAcceptGsPlanNumMap.put(key, 
								(noAcceptGsPlanNumMap.get(key)==null?0:noAcceptGsPlanNumMap.get(key)) 
								+ schDishCommon.getTotal());
					}
				}
				if(schDishCommon.getHaulStatus() !=null) {
					//已指派:0 已指派（未配送） 1配送中 2 待验收（已配送）3已验收
					if(schDishCommon.getHaulStatus() == 0 || schDishCommon.getHaulStatus() == 1 || schDishCommon.getHaulStatus() == 2
							|| schDishCommon.getHaulStatus() == 3) {
						dispGsPlanNumsMap.put(key, 
								(dispGsPlanNumsMap.get(key)==null?0:dispGsPlanNumsMap.get(key)) 
								+ schDishCommon.getTotal());
						
					}
					
					//已配送: 1配送中 2 待验收（已配送）3已验收
					if(schDishCommon.getHaulStatus() == 1 || schDishCommon.getHaulStatus() == 2
							|| schDishCommon.getHaulStatus() == 3) {
						assignGsPlanNumsMap.put(key, 
								(assignGsPlanNumsMap.get(key)==null?0:assignGsPlanNumsMap.get(key)) 
								+ schDishCommon.getTotal());
						
					}
				}
			}
		}
		
		for (int k = 0; k < dates.length; k++) {
			for (int i = 0; i < subLevelCount; i++) {
				if(i == 0)
					compDepCount = 1;
				else if(i == 1)
					compDepCount = 2;
				else if(i == 2)
					compDepCount = 8;
				else if(i == 3)
					compDepCount = 17;
				else
					compDepCount = 0;
				for(int j = 0; j < compDepCount; j++) {
					//判断是否按主管部门获取数据
					if(subLevel != -1) {
						if(i != subLevel)
							continue ;
						else {
							if(compDep != -1) {
								if(compDep != j)
									continue ;
							}
						}
					}else if(subLevelList!=null && subLevelList.size()>0) {
						if(!subLevelList.contains(String.valueOf(i))) {
							continue;
						}else {
							if(compDepList!=null && compDepList.size()>0) {
								if(!compDepList.contains(i+"_"+(j))) {
									continue ;
								}
							}
						}
					}
				
					String dataKey = dates[0].replaceAll("-", "/")+"_"+i+"_" + j;
					totalGsPlanNums[k][i][j] =  totalGsPlanNumMap.get(dataKey)==null?0:totalGsPlanNumMap.get(dataKey);
					noAcceptGsPlanNums[k][i][j] =  noAcceptGsPlanNumMap.get(dataKey)==null?0:noAcceptGsPlanNumMap.get(dataKey); 
					acceptGsPlanNums[k][i][j] =  acceptGsPlanNumMap.get(dataKey)==null?0:acceptGsPlanNumMap.get(dataKey); 
					acceptSchNums[k][i][j] =  acceptSchNumsMap.get(dataKey)==null?0:acceptSchNumsMap.get(dataKey);
					noAcceptSchNums[k][i][j] =  noAcceptSchNumsMap.get(dataKey)==null?0:noAcceptSchNumsMap.get(dataKey);
					
					shouldAcceptSchNums[k][i][j] =  acceptSchNums[k][i][j] + noAcceptSchNums[k][i][j];
				}
			}
		}
	}

	private void getDishByCompDep(String[] dates,String departmentId, int subLevel, int compDep, int k, int subLevelCount,
			int[][][] totalMealSchNums, int[][][] distDishSchNums, int[][][] distNoDishSchNums,
			List<Object> subLevelList, List<Object> compDepList) {
		int i;
		int j;
		int compDepCount;
		Map<String, String> platoonFeedTotalMap;
		String dishKeyVal = "";
		String dishFieldPrefix = "";
		String dishKey = dates[k]   + DataKeyConfig.platoonfeedTotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			dishKey = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
		}
		platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, dishKey);
		if(platoonFeedTotalMap != null) {
			for(String curKey : platoonFeedTotalMap.keySet()) {
				for (i = 0; i < subLevelCount; i++) {
					if(i == 0)
						compDepCount = 1;
					else if(i == 1)
						compDepCount = 2;
					else if(i == 2)
						compDepCount = 8;
					else if(i == 3)
						compDepCount = 17;
					else
						compDepCount = 0;
					for(j = 0; j < compDepCount; j++) {
						//判断是否按主管部门获取数据
						if(subLevel != -1) {
							if(i != subLevel)
								continue ;
							else {
								if(compDep != -1) {
									if(compDep != j)
										continue ;
								}
							}
						}else if(subLevelList!=null && subLevelList.size()>0) {
							if(!subLevelList.contains(String.valueOf(i))) {
								continue;
							}else {
								if(compDepList!=null && compDepList.size()>0) {
									if(!compDepList.contains(i+"_"+(j))) {
										continue ;
									}
								}
							}
						}
						// 设置前置域名
						if(i < 3)
							dishFieldPrefix = "masterid_" + i + "_slave_" + j;
						else if(i == 3) {
							String compDepId = String.valueOf(j);								
							dishFieldPrefix = "masterid_" + i + "_slave_" + AppModConfig.compDepIdToNameMap3.get(compDepId);
						}
						// 区域排菜学校供餐数
						int mealSchNum = 0, dishSchNum = 0, noDishSchNum = 0;
						if (curKey.indexOf(dishFieldPrefix) != -1) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 6)
							{
								if(curKeys[4].equalsIgnoreCase("供餐") && curKeys[5].equalsIgnoreCase("已排菜")) {
									dishKeyVal = platoonFeedTotalMap.get(curKey);
									if(dishKeyVal != null) {
										mealSchNum = Integer.parseInt(dishKeyVal);
										dishSchNum = mealSchNum;
									}
								}
								else if(curKeys[4].equalsIgnoreCase("供餐") && curKeys[5].equalsIgnoreCase("未排菜")) {
									dishKeyVal = platoonFeedTotalMap.get(curKey);
									if(dishKeyVal != null) {
										mealSchNum = Integer.parseInt(dishKeyVal);
										noDishSchNum = mealSchNum;
									}
								}
							}
						}
						totalMealSchNums[k][i][j] += mealSchNum;
						distDishSchNums[k][i][j] += dishSchNum;
						distNoDishSchNums[k][i][j] += noDishSchNum;
					}
				}
			}
		}
	}

	private void getDishByCompDepFromHive(String departmentId,String[] dates, int subLevel, int compDep, int subLevelCount,
			int[][][] totalMealSchNums, int[][][] distDishSchNums, int[][][] distNoDishSchNums,
			int[][][] standardNums,int[][][] supplementNums,int[][][] beOverdueNums,int[][][] noDataNums,
			List<Object> subLevelList, List<Object> compDepList,
			DbHiveDishService dbHiveDishService) {
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
		//学校总数
		Map<String,Integer> totalSchNumMap = new HashMap<>();
		//应排菜学校
		Map<String,Integer> totalMealSchNumMap = new HashMap<>();
		//已排菜学校
		Map<String,Integer> distDishSchNumMap = new HashMap<>();
		//未排菜学校
		Map<String,Integer> distNoDishSchNumMap = new HashMap<>();
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, null, null, 
				-1,-1, subLevelList, compDepList, departmentId,null,3);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				String dataKey = schDishCommon.getDishDate() + "_" + schDishCommon.getDepartmentMasterId() + "_" +schDishCommon.getDepartmentSlaveIdName();
				if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNumMap.put(dataKey, 
									(totalSchNumMap.get(dataKey )==null?0:totalSchNumMap.get(dataKey)) 
									+ schDishCommon.getTotal());
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
							totalMealSchNumMap.put(dataKey, 
									(totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey)) 
									+ schDishCommon.getTotal());
						}else{
							if(schDishCommon.getHavePlatoon() == 1) {
								//已排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =1
								distDishSchNumMap.put(dataKey, 
										(distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey)) 
										+ schDishCommon.getTotal());
							}else if(schDishCommon.getHavePlatoon() == 0) {
								//未排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =0
								distNoDishSchNumMap.put(dataKey, 
										(distNoDishSchNumMap.get(dataKey)==null?0:distNoDishSchNumMap.get(dataKey)) 
										+ schDishCommon.getTotal());
							}
						}
					}else{
						
					}
				}
			}
		}
		int compDepCount;
		for (int k = 0; k < dates.length; k++) {
			for (int i = 0; i < subLevelCount; i++) {
				if(i == 0)
					compDepCount = 1;
				else if(i == 1)
					compDepCount = 2;
				else if(i == 2)
					compDepCount = 8;
				else if(i == 3)
					compDepCount = 17;
				else
					compDepCount = 0;
				for(int j = 0; j < compDepCount; j++) {
					//判断是否按主管部门获取数据
					if(subLevel != -1) {
						if(i != subLevel)
							continue ;
						else {
							if(compDep != -1) {
								if(compDep != j)
									continue ;
							}
						}
					}else if(subLevelList!=null && subLevelList.size()>0) {
						if(!subLevelList.contains(String.valueOf(i))) {
							continue;
						}else {
							if(compDepList!=null && compDepList.size()>0) {
								if(!compDepList.contains(i+"_"+(j))) {
									continue ;
								}
							}
						}
					}
				
					String dataKey = dates[0].replaceAll("-", "/")+"_"+i+"_" + j;
					int totalSchNum = totalSchNumMap.get(dataKey)==null?0:totalSchNumMap.get(dataKey);
					int totalDistSchNum = totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey);
					int distDishSchNum = distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey);
					int distNoDishSchNum = distNoDishSchNumMap.get(dataKey)==null?0:distNoDishSchNumMap.get(dataKey);
					
					//1 表示规范录入
					int standardNum = standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey);
					//2 表示补录
					int supplementNum = supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey);
					//3 表示逾期补录
					int beOverdueNum = beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey);
					//4 表示无数据
					int noDataNum = noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey);
					
					totalMealSchNums[k][i][j] = totalDistSchNum; 
					distDishSchNums[k][i][j] = distDishSchNum; 
					distNoDishSchNums[k][i][j] = distNoDishSchNum;
					
					standardNums[k][i][j] = standardNum;
					supplementNums[k][i][j] = supplementNum;
					beOverdueNums[k][i][j] = beOverdueNum;
					noDataNums[k][i][j] = noDataNum;
				}
			}
		}
	}
	
	private void getRecyclerWasteByCompFromHive(String distIdorSCName, String[] dates, int subLevel, int compDep,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService, String field, int subLevelCount,
			float[][][] kwSchRcNums, float[][][] woSchRcNums, List<Object> subLevelList, List<Object> compDepList,
			int days) {
		int i;
		int j;
		int k;
		int compDepCount;
		//垃圾回收、废弃油脂
		// 时间段内各区餐厨垃圾学校回收总数
		//学校餐厨垃圾集合 key:区号 value ：回收数量
		Map<String,Float> totalSchRcNumMap = new HashMap<String,Float>();
		//学校餐废弃油脂集合 key:区号 value ：回收数量
		Map<String,Float> totalSchOilRcNumMap = new HashMap<String,Float>();
		
		// 时间段内各区餐厨垃圾学校回收总数
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
		List<KwCommonRecs> warnCommonLicList = new ArrayList<>();
		warnCommonLicList = dbHiveRecyclerWasteService.getRecyclerWasteList(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
				null, null, null,-1,-1,subLevelList, compDepList, null, null,3);
		float totalRcNum = 0;
		String totalRcNumMapKey="";
		for(KwCommonRecs warnCommonLics : warnCommonLicList) {
			//PlatformType:1为教委端 2为团餐端 type:1餐厨垃圾，2废弃油脂
			if(warnCommonLics.getPlatformType() == 1) {
				totalRcNumMapKey = warnCommonLics.getRecDate().replaceAll("/", "-") + "_" +warnCommonLics.getDepartmentMasterId() +"_"+ warnCommonLics.getDepartmentSlaveIdName();
				//学校
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalSchRcNumMap.get(totalRcNumMapKey)==null?0:totalSchRcNumMap.get(totalRcNumMapKey);
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalSchRcNumMap.put(totalRcNumMapKey, totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalSchOilRcNumMap.get(totalRcNumMapKey)==null?0:totalSchOilRcNumMap.get(totalRcNumMapKey);
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalSchOilRcNumMap.put(totalRcNumMapKey, totalRcNum);
				}
			}
		}
		for (k = 0; k < dates.length; k++) {
			for (i = 0; i < subLevelCount; i++) {
				if(i == 0)
					compDepCount = 1;
				else if(i == 1)
					compDepCount = 2;
				else if(i == 2)
					compDepCount = 8;
				else if(i == 3)
					compDepCount = 17;
				else
					compDepCount = 0;
				for(j = 0; j < compDepCount; j++) {
					//判断是否按主管部门获取数据
					if(subLevel != -1) {
						if(i != subLevel)
							continue ;
						else {
							if(compDep != -1) {
								if(compDep != j)
									continue ;
							}
						}
					}else if(subLevelList!=null && subLevelList.size()>0) {
						if(!subLevelList.contains(String.valueOf(i))) {
							continue;
						}else {
							if(compDepList!=null && compDepList.size()>0) {
								if(!compDepList.contains(i+"_"+(j))) {
									continue ;
								}
							}
						}
					}
				
					totalRcNumMapKey = dates[k]+"_"+i+"_"+j;
					totalRcNum = totalSchRcNumMap.get(totalRcNumMapKey)==null?0:totalSchRcNumMap.get(totalRcNumMapKey);
					float totalSchOilRcNum = totalSchOilRcNumMap.get(totalRcNumMapKey)==null?0:totalSchOilRcNumMap.get(totalRcNumMapKey);
					
					//学校餐厨垃圾
					kwSchRcNums[k][i][j] += totalRcNum;
					//学校废弃油脂
					woSchRcNums[k][i][j] += totalSchOilRcNum;
					
					//餐厨垃圾回收
					//餐厨垃圾学校回收
					BigDecimal bd = new BigDecimal(kwSchRcNums[k][i][j]);
					kwSchRcNums[k][i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + j 
							+ "，餐厨垃圾学校回收数量：" + kwSchRcNums[k][i][j] + " 桶");				
					//废弃油脂回收
					//废弃油脂学校回收
					bd = new BigDecimal(woSchRcNums[k][i][j]);
					woSchRcNums[k][i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + j 
							+ "，废弃油脂学校回收数量：" + woSchRcNums[k][i][j] + " 桶");
				}
			}
		}
	}

	/**
	 * 获取餐厨垃圾废弃油脂（redis）（根据主管部门）
	 * @param dates
	 * @param subLevel
	 * @param compDep
	 * @param fieldPrefix
	 * @param k
	 * @param subLevelCount
	 * @param kwSchRcNums
	 * @param woSchRcNums
	 * @param subLevelList
	 * @param compDepList
	 * @return
	 */
	private String getRecyclerWasteByCompDepFromRedis(String[] dates, int subLevel, int compDep, String fieldPrefix, int k,
			int subLevelCount, float[][][] kwSchRcNums, float[][][] woSchRcNums, List<Object> subLevelList,
			List<Object> compDepList) {
		String key;
		String keyVal;
		int i;
		int j;
		int compDepCount;
		Map<String, String> schoolwastetotalMap;
		Map<String, String> schooloiltotalMap;
		key = dates[k] + "_schoolwastetotal";
		schoolwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(schoolwastetotalMap != null) {
			for(String curKey : schoolwastetotalMap.keySet()) {
				for (i = 0; i < subLevelCount; i++) {
					if(i == 0)
						compDepCount = 1;
					else if(i == 1)
						compDepCount = 2;
					else if(i == 2)
						compDepCount = 8;
					else if(i == 3)
						compDepCount = 16;
					else
						compDepCount = 0;
					for(j = 0; j < compDepCount; j++) {
						//判断是否按主管部门获取数据
						if(subLevel != -1) {
							if(i != subLevel)
								continue ;
							else {
								if(compDep != -1) {
									if(compDep != j)
										continue ;
								}
							}
						}else if(subLevelList!=null && subLevelList.size()>0) {
							if(!subLevelList.contains(String.valueOf(i))) {
								continue;
							}else {
								if(compDepList!=null && compDepList.size()>0) {
									if(!compDepList.contains(i+"_"+(j))) {
										continue ;
									}
								}
							}
						}
						// 设置前置域名
						if(i < 3)
							fieldPrefix = "masterid_" + i + "_slave_" + j;
						else if(i == 3) {
							String compDepId = String.valueOf(j);								
							fieldPrefix = "masterid_" + i + "_slave_" + AppModConfig.compDepIdToNameMap3.get(compDepId);
						}
						// 学校回收垃圾桶数
						fieldPrefix += "_total";
						if (curKey.equalsIgnoreCase(fieldPrefix)) {
							keyVal = schoolwastetotalMap.get(curKey);
							if(keyVal != null) {
								kwSchRcNums[k][i][j] = Float.parseFloat(keyVal);
							}
						}
					}
				}
			}
		}
		//废弃油脂回收
		//学校回收桶数
		key = dates[k] + "_schooloiltotal";
		schooloiltotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(schooloiltotalMap != null) {
			for(String curKey : schooloiltotalMap.keySet()) {
				for (i = 0; i < subLevelCount; i++) {
					if(i == 0)
						compDepCount = 1;
					else if(i == 1)
						compDepCount = 2;
					else if(i == 2)
						compDepCount = 8;
					else if(i == 3)
						compDepCount = 17;
					else
						compDepCount = 0;
					for(j = 0; j < compDepCount; j++) {
						//判断是否按主管部门获取数据
						if(subLevel != -1) {
							if(i != subLevel)
								continue ;
							else {
								if(compDep != -1) {
									if(compDep != j)
										continue ;
								}
							}
						}else if(subLevelList!=null && subLevelList.size()>0) {
							if(!subLevelList.contains(String.valueOf(i))) {
								continue;
							}else {
								if(compDepList!=null && compDepList.size()>0) {
									if(!compDepList.contains(i+"_"+(j))) {
										continue ;
									}
								}
							}
						}
						// 设置前置域名
						if(i < 3)
							fieldPrefix = "masterid_" + i + "_slave_" + j;
						else if(i == 3) {
							String compDepId = String.valueOf(j);								
							fieldPrefix = "masterid_" + i + "_slave_" + AppModConfig.compDepIdToNameMap3.get(compDepId);
						}
						//区域回收垃圾桶数
						fieldPrefix += "_total";
						if (curKey.equalsIgnoreCase(fieldPrefix)) {
							keyVal = schooloiltotalMap.get(curKey);
							if(keyVal != null) {
								woSchRcNums[k][i][j] = Float.parseFloat(keyVal);
							}
						}
					}
				}
			}
		}
		return fieldPrefix;
	}

	/**
	 * 按照主管部门，获取预警数据
	 * @param dates
	 * @param subLevel
	 * @param compDep
	 * @param fieldPrefix
	 * @param k
	 * @param subLevelCount
	 * @param totalWarnNums
	 * @param noProcWarnNums
	 * @param elimWarnNums
	 * @param subLevelList
	 * @param compDepList
	 * @return
	 */
	private String setWarnDataByCompDep(String[] dates, int subLevel, int compDep, String fieldPrefix, int k,
			int subLevelCount, int[][][] totalWarnNums, int[][][] noProcWarnNums, int[][][] elimWarnNums,
			List<Object> subLevelList, List<Object> compDepList) {
		String key;
		String keyVal;
		int i;
		int j;
		int compDepCount;
		Map<String, String> warnTotalMap;
		int curWarnNum;
		key = dates[k] + "_warn-total";
		warnTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(warnTotalMap != null) {
			for(String curKey : warnTotalMap.keySet()) {
				String[] curKeys = curKey.split("_");
				for (i = 0; i < subLevelCount; i++) {
					if(i == 0)
						compDepCount = 1;
					else if(i == 1)
						compDepCount = 2;
					else if(i == 2)
						compDepCount = 8;
					else if(i == 3)
						compDepCount = 16;
					else
						compDepCount = 0;
					for(j = 0; j < compDepCount; j++) {
						//判断是否按主管部门获取数据
						if(subLevel != -1) {
							if(i != subLevel)
								continue ;
							else {
								if(compDep != -1) {
									if(compDep != j)
										continue ;
								}
							}
						}else if(subLevelList!=null && subLevelList.size()>0) {
							if(!subLevelList.contains(String.valueOf(i))) {
								continue;
							}else {
								if(compDepList!=null && compDepList.size()>0) {
									if(!compDepList.contains(i+"_"+(j))) {
										continue ;
									}
								}
							}
						}
						// 设置前置域名
						if(i < 3)
							fieldPrefix = "masterid_" + i + "_slave_" + j;
						else if(i == 3) {
							String compDepId = String.valueOf(j);								
							fieldPrefix = "masterid_" + i + "_slave_" + AppModConfig.compDepIdToNameMap3.get(compDepId);
						}
						if (curKey.indexOf(fieldPrefix) != -1) {
							keyVal = warnTotalMap.get(curKey);
							curWarnNum = 0;
							if(keyVal != null)
								curWarnNum = Integer.parseInt(keyVal);
							if(curWarnNum < 0)
								curWarnNum = 0;
							int l = AppModConfig.getVarValIndex(curKeys, "status");
							if(l != -1) {
								if(curKeys[l].equalsIgnoreCase("1")) {         //未处理预警数
									noProcWarnNums[k][i][j] += curWarnNum;
									totalWarnNums[k][i][j] += curWarnNum;
								}
								else if(curKeys[l].equalsIgnoreCase("2")) {    //审核中预警数
									totalWarnNums[k][i][j] += curWarnNum;
								}
								else if(curKeys[l].equalsIgnoreCase("3")) {    //已驳回预警数
									totalWarnNums[k][i][j] += curWarnNum;
								}
								else if(curKeys[l].equalsIgnoreCase("4")) {    //已消除预警数
									elimWarnNums[k][i][j] += curWarnNum;
									totalWarnNums[k][i][j] += curWarnNum;
								}
							}
						}
					}
				}
			}
		}
		return fieldPrefix;
	}
	
	/**
	 * 按照主管部门，获取预警数据
	 * @param dates
	 * @param subLevel
	 * @param compDep
	 * @param fieldPrefix
	 * @param k
	 * @param subLevelCount
	 * @param totalWarnNums
	 * @param noProcWarnNums
	 * @param elimWarnNums
	 * @param subLevelList
	 * @param compDepList
	 * @return
	 */
	private void setWarnDataByCompDepTwo(String departmentId,String startDate,String endDate,Integer target,int subLevel, int compDep,
			int subLevelCount, int[][][] totalWarnNums, int[][][] noProcWarnNums, int[][][] elimWarnNums,
			List<Object> subLevelList, List<Object> compDepList,DbHiveWarnService dbHiveWarnService) {
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
				null, null, -1, -1, null, null, departmentId, null, 3);
		
		if(warnCommonList != null && warnCommonList.size()>0) {
			for(WarnCommon warnCommon : warnCommonList) {
				if(warnCommon !=null) {
					if(StringUtils.isNotEmpty(warnCommon.getDepartmentSlaveIdName()) && CommonUtil.isInteger(warnCommon.getDepartmentSlaveIdName())) {
						if(CommonUtil.isEmpty(warnCommon.getDepartmentMasterId()) ) {
							continue;
						}
						
						int idx = Integer.parseInt(warnCommon.getDepartmentSlaveIdName())-1;
						if(idx < 0) {
							continue;
						}
						
						int masterIdIdx = Integer.parseInt(warnCommon.getDepartmentMasterId());
						if(masterIdIdx < 0) {
							continue;
						}
						
						//未处理预警数
						noProcWarnNums[0][masterIdIdx][idx] += warnCommon.getNoProcWarnNum();
						totalWarnNums[0][masterIdIdx][idx] += warnCommon.getNoProcWarnNum();
						//审核中预警数
						totalWarnNums[0][masterIdIdx][idx] += warnCommon.getAuditWarnNum();
						//已驳回预警数
						totalWarnNums[0][masterIdIdx][idx] += warnCommon.getRejectWarnNum();
						//已消除预警数
						elimWarnNums[0][masterIdIdx][idx] += warnCommon.getElimWarnNum();
						totalWarnNums[0][masterIdIdx][idx] += warnCommon.getElimWarnNum();
					}
				}
			}
		}
	
	}
	
	//汇总数据详情列表函数按所在区
	private SumDataDetsDTO sumDataDetsByLocality(String departmentId,String distIdorSCName, String[] dates,Integer target, List<TEduDistrictDo> tedList,
			String distNames,DbHiveWarnService dbHiveWarnService,DbHiveRecyclerWasteService dbHiveRecyclerWasteService,
			DbHiveDishService dbHiveDishService,DbHiveGsService dbHiveGsService) {
		SumDataDetsDTO sddDto = new SumDataDetsDTO();
		List<SumDataDets> sumDataDets = new ArrayList<>();
		SumDataDets sdd = null;
		String field = "", fieldPrefix = "";
		int i, k;
		//排菜数据
		int distCount = tedList.size(), dateCount = dates.length;
		int[][] totalMealSchNums = new int[dateCount][distCount], 
				distDishSchNums = new int[dateCount][distCount], 
				distNoDishSchNums = new int[dateCount][distCount];
		int[][] standardNums = new int[dateCount][distCount];
		int[][] supplementNums = new int[dateCount][distCount];
		int[][] beOverdueNums = new int[dateCount][distCount];
		int[][] noDataNums = new int[dateCount][distCount];
		float[] distDishRates = new float[distCount];
		//验收数据
		int[][] totalGsPlanNums = new int[dateCount][distCount], 
				noAcceptGsPlanNums = new int[dateCount][distCount],
				acceptGsPlanNums = new int[dateCount][distCount];
		float[] acceptRates = new float[distCount];
		
		//学校验收信息
		int[][] acceptSchNums = new int[dateCount][distCount]; 
		int[][] noAcceptSchNums = new int[dateCount][distCount];
		float[] schAcceptRates = new float[distCount];
		
		//菜品留样
		int[][] totalDishNums = new int[dateCount][distCount];
		int[][] dishRsDishNums = new int[dateCount][distCount];
		int[][] dishNoRsDishNums = new int[dateCount][distCount];
		float[] dishRsRates = new float[distCount];
		
		//学校留样
		int[][] rsSchNums = new int[dateCount][distCount];
		int[][] noRsSchNums = new int[dateCount][distCount];
		float[] schRsRates = new float[distCount];
		
		//证照逾期处理
		int[][] totalWarnNums = new int[dateCount][distCount], 
				noProcWarnNums = new int[dateCount][distCount], 
				elimWarnNums = new int[dateCount][distCount];
		float[] warnProcRates = new float[distCount];			
		//区域ID到索引映射
		Map<String, Integer> distIdToIdxMap = new HashMap<>();
		for(i = 0; i < distCount; i++) {
			distIdToIdxMap.put(tedList.get(i).getId(), i);
		}
		distIdToIdxMap.put("-", i);
		//餐厨垃圾回收
		int[][] kwSchRcFreqs = new int[dateCount][distCount], 
				kwRmcRcFreqs = new int[dateCount][distCount];
		float[][] kwSchRcNums = new float[dateCount][distCount], 
				kwRmcRcNums = new float[dateCount][distCount], 
				kwTotalRcNums = new float[dateCount][distCount];			
		//废弃油脂回收
		int[][] woSchRcFreqs = new int[dateCount][distCount], 
				woRmcRcFreqs = new int[dateCount][distCount];
		float[][] woSchRcNums = new float[dateCount][distCount], 
				woRmcRcNums = new float[dateCount][distCount], 
				woTotalRcNums = new float[dateCount][distCount];	
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		
		// 各天各区排菜学校数量
		for (k = 0; k < dates.length; k++) {
			
			if(days >= 2) {
				//在时间循环之后统一获取
			}else {
				
				//排菜学校
				getDishDataByLocalityFromRedis(departmentId,distIdorSCName, dates, tedList, k, totalMealSchNums, distDishSchNums,
					distNoDishSchNums, distNamesList);
				
				//配货
				getDistributionByLocalityFromRedis(departmentId,distIdorSCName, dates, tedList, k, totalGsPlanNums, noAcceptGsPlanNums,
						acceptGsPlanNums, acceptSchNums, noAcceptSchNums, distNamesList);
			}
			
			
			
			//2019.0606注释，改为从hive中获取，再时间循环外面调用，新方法：setWarnDataTwo
			//证照逾期处理
			//setWarnData(dates,keyVal,k,totalWarnNums, noProcWarnNums, elimWarnNums, distIdToIdxMap);	
			//菜品留样
			getGcRetentionDishByLocalityFromRedis(departmentId,distIdorSCName, dates, tedList, k, totalDishNums,
					dishRsDishNums, dishNoRsDishNums, rsSchNums, noRsSchNums, distNamesList);	
			
			//30天之前数据从hive中获取，30天之内及之后从redis中获取
			if(days >= 2) {
				//从hive库中获取，在日期循环后获取
			}else {
				getRecyclerWasteFromRedis(distIdorSCName, dates, tedList, fieldPrefix, k, kwSchRcFreqs,
						kwRmcRcFreqs, kwSchRcNums, kwRmcRcNums, woSchRcFreqs, woRmcRcFreqs, woSchRcNums, woRmcRcNums,
						distNamesList);
			}
			//证照逾期处理
			setWarnDataByLocalityTwo(departmentId, dates[0], dates[dates.length-1], target, totalWarnNums, noProcWarnNums, elimWarnNums, distIdToIdxMap, dbHiveWarnService);
			
			// 该日期段各区排菜数据、验收数据、菜品留样、证照逾期处理、餐厨垃圾回收和废弃油脂回收
			for (i = 0; i < distCount; i++) {
				TEduDistrictDo curTdd = tedList.get(i);
				String curDistId = curTdd.getId();
				field = "area" + "_" + curDistId;
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (distIdorSCName != null) {
					if (!curDistId.equals(distIdorSCName))
						continue;
				}else if(distNamesList!=null && distNamesList.size() >0) {
					if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
						continue;
					}
					if(!distNamesList.contains(curDistId)) {
						continue ;
					}
				}
				// 区域学校排菜率
				if (totalMealSchNums[k][i] != 0) {
					distDishRates[i] = 100 * ((float) distDishSchNums[k][i] / (float) totalMealSchNums[k][i]);
					BigDecimal bd = new BigDecimal(distDishRates[i]);
					distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distDishRates[i] > 100) {
						distDishRates[i] = 100;
						distDishSchNums[k][i] = totalMealSchNums[k][i];
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，排菜学校数量：" + distDishSchNums[k][i] 	
						+ "，供餐学校总数：" + totalMealSchNums[k][i] + "，排菜率：" + distDishRates[i] + "，field = " + field);				
				//验收数据
				if (totalGsPlanNums[k][i] != 0) {
					//验收率
					acceptRates[i] = 100 * ((float) acceptGsPlanNums[k][i] / (float) totalGsPlanNums[k][i]);
					BigDecimal bd = new BigDecimal(acceptRates[i]);
					acceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (acceptRates[i] > 100) {
						acceptRates[i] = 100;
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，配货计划总数：" + totalGsPlanNums[k][i] 
						+ "，验收数：" + acceptGsPlanNums[k][i] + "，验收率：" + acceptRates[i]);
				// 区域留样率
				if (totalDishNums[k][i] != 0) {
					dishRsRates[i] = 100 * ((float) dishRsDishNums[k][i] / (float) totalDishNums[k][i]);
					BigDecimal bd = new BigDecimal(dishRsRates[i]);
					dishRsRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (dishRsRates[i] > 100) {
						dishRsRates[i] = 100;
						dishRsDishNums[k][i] = totalDishNums[k][i];
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，菜品数量：" + totalDishNums[k][i]
						+ "，已留样菜品数：" + dishRsDishNums[k][i] + "，未留样菜品数：" + dishNoRsDishNums[k][i] + "，留样率：" + dishRsRates[i] + "，field = "
						+ field);
				//证照逾期处理
				int totalWarnNum = totalWarnNums[k][i], elimWarnNum = elimWarnNums[k][i];
				warnProcRates[i] = 0;
				if(totalWarnNum > 0) {
					warnProcRates[i] = 100 * ((float) elimWarnNum / (float) totalWarnNum);
					BigDecimal bd = new BigDecimal(warnProcRates[i]);
					warnProcRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (warnProcRates[i] > 100)
						warnProcRates[i] = 100;
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，预警数量：" + totalWarnNums[k][i]
						+ "，已处理预警数：" + elimWarnNums[k][i] + "，未处理预警数：" + noProcWarnNums[k][i] + "，处理率：" + warnProcRates[i] + "，field = "
						+ field);					
				//餐厨垃圾回收
				//餐厨垃圾学校回收
				BigDecimal bd = new BigDecimal(kwSchRcNums[k][i]);
				kwSchRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，餐厨垃圾学校回收次数：" + kwSchRcFreqs[k][i]
						+ "，餐厨垃圾学校回收数量：" + kwSchRcNums[k][i] + " 桶" + "，field = " + field);
				//餐厨垃圾团餐公司回收
				bd = new BigDecimal(kwRmcRcNums[k][i]);
				kwRmcRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，餐厨垃圾团餐公司回收次数：" + kwRmcRcFreqs[k][i]
						+ "，餐厨垃圾团餐公司回收数量：" + kwRmcRcNums[k][i] + " 桶" + "，field = " + field);
				kwTotalRcNums[k][i] = kwSchRcNums[k][i] + kwRmcRcNums[k][i];					
				//废弃油脂回收
				//废弃油脂学校回收
				bd = new BigDecimal(woSchRcNums[k][i]);
				woSchRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，废弃油脂学校回收次数：" + woSchRcFreqs[k][i]
						+ "，废弃油脂学校回收数量：" + woSchRcNums[k][i] + " 桶" + "，field = " + field);
				//废弃油脂团餐公司回收
				bd = new BigDecimal(woRmcRcNums[k][i]);
				woRmcRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，/废弃油脂团餐公司回收次数：" + woRmcRcFreqs[k][i]
						+ "，/废弃油脂团餐公司回收数量：" + woRmcRcNums[k][i] + " 桶" + "，field = " + field);
				woTotalRcNums[k][i] = woSchRcNums[k][i] + woRmcRcNums[k][i];
			}
		}
		
		
		if(days >= 2) {
			//排菜学校
			getDishDataByLocalityFromHive(departmentId,distIdorSCName, dates, tedList, totalMealSchNums, distDishSchNums, distNoDishSchNums,
					standardNums,supplementNums,beOverdueNums,noDataNums,
					distNamesList, dbHiveDishService);
			
			//配货
			getDistributionByLocalityFromHive(distIdorSCName, dates, tedList, totalGsPlanNums, noAcceptGsPlanNums, 
					acceptGsPlanNums, acceptSchNums, noAcceptSchNums, distNamesList,departmentId,null, dbHiveGsService);
		}
		
		//30天之前数据从hive中获取，30天之内及之后从redis中获取
		if(days >= 2) {
			getRecyclerWasteByLocalityFromHive(distIdorSCName, dates, tedList, dbHiveRecyclerWasteService, field, kwSchRcFreqs,
					kwRmcRcFreqs, kwSchRcNums, kwRmcRcNums, kwTotalRcNums, woSchRcFreqs, woRmcRcFreqs, woSchRcNums,
					woRmcRcNums, woTotalRcNums, distNamesList, days);
		}
		
		for (i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distIdorSCName != null) {
				if (!curDistId.equals(distIdorSCName))
					continue;
			}else if(distNamesList!=null && distNamesList.size() >0) {
				if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
					continue;
				}
				if(!distNamesList.contains(curDistId)) {
					continue ;
				}
			}
			for (k = 0; k < dates.length; k++) {
				sdd = new SumDataDets();
				//日期
				sdd.setDishDate(dates[k].replaceAll("-", "/"));
				//所在区域
				sdd.setDistName(curTdd.getName());
				//应排菜数数、已排菜数、未排菜数、排菜率
				int totalDistSchNum = 0, distDishSchNum = 0, distNoDishSchNum = 0;			
				totalDistSchNum = totalMealSchNums[k][i];
				distDishSchNum = distDishSchNums[k][i];
				distNoDishSchNum = distNoDishSchNums[k][i];
				sdd.setMealSchNum(totalDistSchNum);
				sdd.setDishSchNum(distDishSchNum);
				sdd.setNoDishSchNum(distNoDishSchNum);
				distDishRates[i] = 0;
				if(totalDistSchNum > 0) {
					distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
					BigDecimal bd = new BigDecimal(distDishRates[i]);
					distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distDishRates[i] > 100)
						distDishRates[i] = 100;
				}
				sdd.setDishRate(distDishRates[i]);
				
				//1 表示规范录入
				sdd.setStandardNum(standardNums[k][i]);
				//2 表示补录
				sdd.setSupplementNum(supplementNums[k][i]);
				//3 表示逾期补录
				sdd.setBeOverdueNum(beOverdueNums[k][i]);
				//4 表示无数据
				sdd.setNoDataNum(noDataNums[k][i]);
				
				//验收数据：配货计划总数、待验收数、	已验收数、验收率
				int totalGsPlanNum = 0, noAcceptGsPlanNum = 0, acceptGsPlanNum = 0;				
				totalGsPlanNum = totalGsPlanNums[k][i];
				noAcceptGsPlanNum = noAcceptGsPlanNums[k][i];
				acceptGsPlanNum = acceptGsPlanNums[k][i];
				//验收数量及验收率
				acceptRates[i] = 0;
				if(totalGsPlanNum > 0) {
					acceptRates[i] = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRates[i]);
					acceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (acceptRates[i] > 100)
						acceptRates[i] = 100;
				}
				sdd.setTotalGsPlanNum(totalGsPlanNum);
				sdd.setNoAcceptGsPlanNum(noAcceptGsPlanNum);
				sdd.setAcceptGsPlanNum(acceptGsPlanNum);
				sdd.setAcceptRate(acceptRates[i]);
				
				//学校验收信息以及验收率
				int shouldAcceptSchNum = 0;
				int acceptSchNum = 0;
				int noAcceptSchNum = 0;
				acceptSchNum = acceptSchNums[k][i];
				noAcceptSchNum = noAcceptSchNums[k][i];
				
				shouldAcceptSchNum = acceptSchNum + noAcceptSchNum;
				
				sdd.setShouldAcceptSchNum(shouldAcceptSchNum);
				sdd.setAcceptSchNum(acceptSchNum);
				sdd.setNoAcceptSchNum(noAcceptSchNum);
				
				schAcceptRates[i] = 0;
				if(shouldAcceptSchNum > 0) {
					schAcceptRates[i] = 100 * ((float) acceptSchNum / (float) shouldAcceptSchNum);
					BigDecimal bd = new BigDecimal(schAcceptRates[i]);
					schAcceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRates[i] > 100)
						schAcceptRates[i] = 100;
				}
				sdd.setSchAcceptRate(schAcceptRates[i]);
				
				//菜品总数、未留样数、已留样数、留样率
				int totalDishNum = 0, dishRsDishNum = 0, dishNoRsDishNum = 0;
				totalDishNum = totalDishNums[k][i];
				dishRsDishNum = dishRsDishNums[k][i];
				dishNoRsDishNum = dishNoRsDishNums[k][i];
				sdd.setTotalDishNum(totalDishNum);
				sdd.setRsDishNum(dishRsDishNum);
				sdd.setNoRsDishNum(dishNoRsDishNum);
				dishRsRates[i] = 0;
				if(totalDishNum > 0) {
					dishRsRates[i] = 100 * ((float) dishRsDishNum / (float) totalDishNum);
					BigDecimal bd = new BigDecimal(dishRsRates[i]);
					dishRsRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (dishRsRates[i] > 100)
						dishRsRates[i] = 100;
				}
				sdd.setRsRate(dishRsRates[i]);
				
				//学校留样相关信息
				int shouldRsSchNum = 0;
				int rsSchNum = 0;
				int noRsSchNum = 0;
				
				rsSchNum = rsSchNums[k][i];
				noRsSchNum = noRsSchNums[k][i];
				shouldRsSchNum = rsSchNum + noRsSchNum;
				sdd.setShouldRsSchNum(shouldRsSchNum);
				sdd.setRsSchNum(rsSchNum);
				sdd.setNoRsSchNum(noRsSchNum);
				schRsRates[i] = 0;
				if(shouldRsSchNum > 0) {
					schRsRates[i] = 100 * ((float) rsSchNum / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schRsRates[i]);
					schRsRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schRsRates[i] > 100)
						schRsRates[i] = 100;
				}
				sdd.setSchRsRate(schRsRates[i]);
				
				//预警总数、待处理预警数、已消除预警数、预警处理率
				int totalWarnNum = 0, noProcWarnNum = 0, elimWarnNum = 0;				
				totalWarnNum = totalWarnNums[k][i];
				noProcWarnNum = noProcWarnNums[k][i];
				elimWarnNum = elimWarnNums[k][i];
				sdd.setTotalWarnNum(totalWarnNum);
				sdd.setNoProcWarnNum(totalWarnNum - elimWarnNum);
				sdd.setElimWarnNum(elimWarnNum);
				warnProcRates[i] = 0;
				if(totalWarnNum > 0) {
					warnProcRates[i] = 100 * ((float) elimWarnNum / (float) totalWarnNum);
					BigDecimal bd = new BigDecimal(warnProcRates[i]);
					warnProcRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (warnProcRates[i] > 100)
						warnProcRates[i] = 100;
				}
				sdd.setWarnProcRate(warnProcRates[i]);	
				//餐厨垃圾回收合计，按所在地有效、餐厨垃圾学校回收数量、餐厨垃圾团餐公司回收数量，按所在地有效
				float kwSchRcNum = 0, kwRmcRcNum = 0, kwTotalRcNum = 0;				
				kwSchRcNum = kwSchRcNums[k][i];
				kwRmcRcNum = kwRmcRcNums[k][i];
				kwTotalRcNum = kwTotalRcNums[k][i];
				sdd.setTotalKwRecNum( new BigDecimal(kwTotalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				sdd.setKwSchRecNum( new BigDecimal(kwSchRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				sdd.setKwRmcRecNum( new BigDecimal(kwRmcRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());	
				//废弃油脂回收合计，按所在地有效、废弃油脂学校回收数量、废弃油脂团餐公司回收数量，按所在地有效
				float woSchRcNum = 0, woRmcRcNum = 0, woTotalRcNum = 0;
				woSchRcNum = woSchRcNums[k][i];
				woRmcRcNum = woRmcRcNums[k][i];
				woTotalRcNum = woTotalRcNums[k][i];
				sdd.setTotalWoRecNum( new BigDecimal(woTotalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				sdd.setWoSchRecNum( new BigDecimal(woSchRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				sdd.setWoRmcRecNum( new BigDecimal(woRmcRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				sumDataDets.add(sdd);
			}
		}
		//排序
    	SortList<SumDataDets> sortList = new SortList<SumDataDets>();  
    	sortList.Sort(sumDataDets, methods1, sorts1, dataTypes1);
		//时戳
		sddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<SumDataDets> pageBean = new PageBean<SumDataDets>(sumDataDets, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		sddDto.setPageInfo(pageInfo);
		// 设置数据
		sddDto.setSumDataDets(pageBean.getCurPageData());
		// 消息ID
		sddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return sddDto;
	}
	
	//汇总数据详情列表函数按所在区
	private SumDataDetsDTO sumDataDetsByDepartment(String departmentId, String[] dates,Integer target,
			String departmentIds,Map<Integer, String> schoolDepartmentMap,int [] departmentIdArr,
			DbHiveWarnService dbHiveWarnService,DbHiveRecyclerWasteService dbHiveRecyclerWasteService,
			DbHiveDishService dbHiveDishService,DbHiveGsService dbHiveGsService,Db1Service db1Service) {
		SumDataDetsDTO sddDto = new SumDataDetsDTO();
		List<SumDataDets> sumDataDets = new ArrayList<>();
		SumDataDets sdd = null;
		String field = "", fieldPrefix = "";
		int i, k;
		//排菜数据
		int departmentCount = departmentIdArr.length, dateCount = dates.length;
		int[][] totalMealSchNums = new int[dateCount][departmentCount], 
				distDishSchNums = new int[dateCount][departmentCount], 
				distNoDishSchNums = new int[dateCount][departmentCount];
		float[] distDishRates = new float[departmentCount];
		int[][] standardNums = new int[dateCount][departmentCount];
		int[][] supplementNums = new int[dateCount][departmentCount];
		int[][] beOverdueNums = new int[dateCount][departmentCount];
		int[][] noDataNums = new int[dateCount][departmentCount];
		//验收数据
		int[][] totalGsPlanNums = new int[dateCount][departmentCount], 
				noAcceptGsPlanNums = new int[dateCount][departmentCount],
				acceptGsPlanNums = new int[dateCount][departmentCount];
		float[] acceptRates = new float[departmentCount];
		
		//学校验收信息
		int[][] acceptSchNums = new int[dateCount][departmentCount]; 
		int[][] noAcceptSchNums = new int[dateCount][departmentCount];
		float[] schAcceptRates = new float[departmentCount];
		
		//菜品留样
		int[][] totalDishNums = new int[dateCount][departmentCount];
		int[][] dishRsDishNums = new int[dateCount][departmentCount];
		int[][] dishNoRsDishNums = new int[dateCount][departmentCount];
		float[] dishRsRates = new float[departmentCount];
		
		//学校留样
		int[][] rsSchNums = new int[dateCount][departmentCount];
		int[][] noRsSchNums = new int[dateCount][departmentCount];
		float[] schRsRates = new float[departmentCount];
		
		//证照逾期处理
		int[][] totalWarnNums = new int[dateCount][departmentCount], 
				noProcWarnNums = new int[dateCount][departmentCount], 
				elimWarnNums = new int[dateCount][departmentCount];
		float[] warnProcRates = new float[departmentCount];			
		//区域ID到索引映射
		Map<String, Integer> distIdToIdxMap = new HashMap<>();
		for(i = 0; i < departmentCount; i++) {
			distIdToIdxMap.put(String.valueOf(departmentIdArr[i]), i);
		}
		distIdToIdxMap.put("-", i);
		//餐厨垃圾回收
		int[][] kwSchRcFreqs = new int[dateCount][departmentCount], 
				kwRmcRcFreqs = new int[dateCount][departmentCount];
		float[][] kwSchRcNums = new float[dateCount][departmentCount], 
				kwRmcRcNums = new float[dateCount][departmentCount], 
				kwTotalRcNums = new float[dateCount][departmentCount];			
		//废弃油脂回收
		int[][] woSchRcFreqs = new int[dateCount][departmentCount], 
				woRmcRcFreqs = new int[dateCount][departmentCount];
		float[][] woSchRcNums = new float[dateCount][departmentCount], 
				woRmcRcNums = new float[dateCount][departmentCount], 
				woTotalRcNums = new float[dateCount][departmentCount];	
		
		List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		
		
		Map<Integer, String> departmentMap = new LinkedHashMap<Integer,String>();
		DepartmentObj departmentObj = new DepartmentObj();
		departmentObj.setDepartmentId(departmentId);
		List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,departmentIdList, -1, -1);	
		
		int [] schOwnTypes = new int [deparmentList.size()];
		if(CommonUtil.isNotEmpty(departmentId)) {
		     schOwnTypes = new int [1];
		}
		
		if(departmentIdList !=null && departmentIdList.size()>0) {
		     schOwnTypes = new int [departmentIdList.size()];
		}
		
		int index = 0;
		if(deparmentList != null) {
			for(DepartmentObj department : deparmentList) {
				if(CommonUtil.isNotEmpty(departmentId) && !departmentId.equals(department.getDepartmentId())) {
					continue;
				}
				departmentMap.put(Integer.valueOf(department.getDepartmentId()), department.getDepartmentName());
				schOwnTypes[index++] = Integer.valueOf(department.getDepartmentId());
			}
		}
		
		
		// 各天各区排菜学校数量
		for (k = 0; k < dates.length; k++) {
			
			if(days >= 2) {
				//在时间循环之后统一获取
			}else {
				
				//排菜学校
				getDishDataByDepartmentFromRedis(departmentId, dates, departmentMap, schOwnTypes, k, 
						totalMealSchNums, distDishSchNums, distNoDishSchNums, departmentIdList);
				//配货
				getDistributionByDepartmentFromRedis(departmentId, dates, deparmentList, k, 
						totalGsPlanNums, noAcceptGsPlanNums, acceptGsPlanNums, acceptSchNums, noAcceptSchNums, departmentIdList);
			}
			
			
			
			//2019.0606注释，改为从hive中获取，再时间循环外面调用，新方法：setWarnDataTwo
			//证照逾期处理
			//setWarnData(dates,keyVal,k,totalWarnNums, noProcWarnNums, elimWarnNums, distIdToIdxMap);	
			//菜品留样
			getGcRetentionDishByDepartmentFromRedis(departmentId, dates, deparmentList, k, 
					totalDishNums, dishRsDishNums, dishNoRsDishNums, rsSchNums, noRsSchNums, departmentIdList);
			
			//30天之前数据从hive中获取，30天之内及之后从redis中获取
			/*if(days >= 2) {
				//从hive库中获取，在日期循环后获取
			}else {
				getRecyclerWasteFromRedis(distIdorSCName, dates, tedList, fieldPrefix, k, kwSchRcFreqs,
						kwRmcRcFreqs, kwSchRcNums, kwRmcRcNums, woSchRcFreqs, woRmcRcFreqs, woSchRcNums, woRmcRcNums,
						distNamesList);
			}*/
			//证照逾期处理
			//setWarnDataByLocalityTwo(distIdorSCName, dates[0], dates[dates.length-1], target, totalWarnNums, noProcWarnNums, elimWarnNums, distIdToIdxMap, dbHiveWarnService);
			
			// 该日期段各区排菜数据、验收数据、菜品留样、证照逾期处理、餐厨垃圾回收和废弃油脂回收
			for (i = 0; i < departmentCount; i++) {
				String curDistId = String.valueOf(departmentIdArr[i]);
				field = "department" + "_" + curDistId;
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (departmentId != null) {
					if (!curDistId.equals(departmentId))
						continue;
				}
				if(departmentIdList!=null && departmentIdList.size() >0) {
					if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
						continue;
					}
					if(!departmentIdList.contains(curDistId)) {
						continue ;
					}
				}
				// 区域学校排菜率
				if (totalMealSchNums[k][i] != 0) {
					distDishRates[i] = 100 * ((float) distDishSchNums[k][i] / (float) totalMealSchNums[k][i]);
					BigDecimal bd = new BigDecimal(distDishRates[i]);
					distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distDishRates[i] > 100) {
						distDishRates[i] = 100;
						distDishSchNums[k][i] = totalMealSchNums[k][i];
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curDistId + "，排菜学校数量：" + distDishSchNums[k][i] 	
						+ "，供餐学校总数：" + totalMealSchNums[k][i] + "，排菜率：" + distDishRates[i] + "，field = " + field);				
				//验收数据
				if (totalGsPlanNums[k][i] != 0) {
					//验收率
					acceptRates[i] = 100 * ((float) acceptGsPlanNums[k][i] / (float) totalGsPlanNums[k][i]);
					BigDecimal bd = new BigDecimal(acceptRates[i]);
					acceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (acceptRates[i] > 100) {
						acceptRates[i] = 100;
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curDistId + "，配货计划总数：" + totalGsPlanNums[k][i] 
						+ "，验收数：" + acceptGsPlanNums[k][i] + "，验收率：" + acceptRates[i]);
				// 区域留样率
				if (totalDishNums[k][i] != 0) {
					dishRsRates[i] = 100 * ((float) dishRsDishNums[k][i] / (float) totalDishNums[k][i]);
					BigDecimal bd = new BigDecimal(dishRsRates[i]);
					dishRsRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (dishRsRates[i] > 100) {
						dishRsRates[i] = 100;
						dishRsDishNums[k][i] = totalDishNums[k][i];
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curDistId + "，菜品数量：" + totalDishNums[k][i]
						+ "，已留样菜品数：" + dishRsDishNums[k][i] + "，未留样菜品数：" + dishNoRsDishNums[k][i] + "，留样率：" + dishRsRates[i] + "，field = "
						+ field);
				//证照逾期处理
				int totalWarnNum = totalWarnNums[k][i], elimWarnNum = elimWarnNums[k][i];
				warnProcRates[i] = 0;
				if(totalWarnNum > 0) {
					warnProcRates[i] = 100 * ((float) elimWarnNum / (float) totalWarnNum);
					BigDecimal bd = new BigDecimal(warnProcRates[i]);
					warnProcRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (warnProcRates[i] > 100)
						warnProcRates[i] = 100;
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curDistId + "，预警数量：" + totalWarnNums[k][i]
						+ "，已处理预警数：" + elimWarnNums[k][i] + "，未处理预警数：" + noProcWarnNums[k][i] + "，处理率：" + warnProcRates[i] + "，field = "
						+ field);					
				//餐厨垃圾回收
				//餐厨垃圾学校回收
				BigDecimal bd = new BigDecimal(kwSchRcNums[k][i]);
				kwSchRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curDistId + "，餐厨垃圾学校回收次数：" + kwSchRcFreqs[k][i]
						+ "，餐厨垃圾学校回收数量：" + kwSchRcNums[k][i] + " 桶" + "，field = " + field);
				//餐厨垃圾团餐公司回收
				bd = new BigDecimal(kwRmcRcNums[k][i]);
				kwRmcRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curDistId + "，餐厨垃圾团餐公司回收次数：" + kwRmcRcFreqs[k][i]
						+ "，餐厨垃圾团餐公司回收数量：" + kwRmcRcNums[k][i] + " 桶" + "，field = " + field);
				kwTotalRcNums[k][i] = kwSchRcNums[k][i] + kwRmcRcNums[k][i];					
				//废弃油脂回收
				//废弃油脂学校回收
				bd = new BigDecimal(woSchRcNums[k][i]);
				woSchRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curDistId + "，废弃油脂学校回收次数：" + woSchRcFreqs[k][i]
						+ "，废弃油脂学校回收数量：" + woSchRcNums[k][i] + " 桶" + "，field = " + field);
				//废弃油脂团餐公司回收
				bd = new BigDecimal(woRmcRcNums[k][i]);
				woRmcRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curDistId + "，/废弃油脂团餐公司回收次数：" + woRmcRcFreqs[k][i]
						+ "，/废弃油脂团餐公司回收数量：" + woRmcRcNums[k][i] + " 桶" + "，field = " + field);
				woTotalRcNums[k][i] = woSchRcNums[k][i] + woRmcRcNums[k][i];
			}
		}
		
		
		if(days >= 2) {
			//排菜学校
			getDishDataByDepartmentFromHive(departmentId, dates, deparmentList, totalMealSchNums, distDishSchNums, distNoDishSchNums, standardNums, 
					supplementNums, beOverdueNums, noDataNums, departmentIdList, dbHiveDishService);
			
			//配货
			getDistributionByDepartmentFromHive(dates, deparmentList, 
					totalGsPlanNums, noAcceptGsPlanNums, acceptGsPlanNums, 
					acceptSchNums, noAcceptSchNums, departmentId,
					departmentIdList, dbHiveGsService);
		}
		
		//预警
		Map<String, Integer> deparmentIdToIdxMap = new HashMap<>();
		for(i = 0; i < deparmentList.size(); i++) {
			deparmentIdToIdxMap.put(deparmentList.get(i).getDepartmentId(), i);
		}
		setWarnDataByDepartment(departmentId, dates[0], dates[dates.length-1], target, totalWarnNums, noProcWarnNums, elimWarnNums, deparmentIdToIdxMap, dbHiveWarnService);
		
		
		//30天之前数据从hive中获取，30天之内及之后从redis中获取
		if(days >= 2) {
			//getRecyclerWasteByLocalityFromHive(departmentId, dates, tedList, dbHiveRecyclerWasteService, field, kwSchRcFreqs,
			//		kwRmcRcFreqs, kwSchRcNums, kwRmcRcNums, kwTotalRcNums, woSchRcFreqs, woRmcRcFreqs, woSchRcNums,
			//		woRmcRcNums, woTotalRcNums, departmentIdList, days);
		}
		
		for (i = 0; i < departmentCount; i++) {
			String curDistId = String.valueOf(departmentIdArr[i]);
			// 判断是否按区域获取排菜数据（departmentId为空表示按省或直辖市级别获取数据）
			if (departmentId != null) {
				if (!curDistId.equals(departmentId))
					continue;
			}
			if(departmentIdList!=null && departmentIdList.size() >0) {
				if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
					continue;
				}
				if(!departmentIdList.contains(curDistId)) {
					continue ;
				}
			}
			for (k = 0; k < dates.length; k++) {
				sdd = new SumDataDets();
				//日期
				sdd.setDishDate(dates[k].replaceAll("-", "/"));
				//所在区域
				sdd.setDistName(departmentMap.get(departmentIdArr[i]));
				//应排菜数数、已排菜数、未排菜数、排菜率
				int totalDistSchNum = 0, distDishSchNum = 0, distNoDishSchNum = 0;			
				totalDistSchNum = totalMealSchNums[k][i];
				distDishSchNum = distDishSchNums[k][i];
				distNoDishSchNum = distNoDishSchNums[k][i];
				sdd.setMealSchNum(totalDistSchNum);
				sdd.setDishSchNum(distDishSchNum);
				sdd.setNoDishSchNum(distNoDishSchNum);
				distDishRates[i] = 0;
				if(totalDistSchNum > 0) {
					distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
					BigDecimal bd = new BigDecimal(distDishRates[i]);
					distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distDishRates[i] > 100)
						distDishRates[i] = 100;
				}
				sdd.setDishRate(distDishRates[i]);
				//验收数据：配货计划总数、待验收数、	已验收数、验收率
				int totalGsPlanNum = 0, noAcceptGsPlanNum = 0, acceptGsPlanNum = 0;				
				totalGsPlanNum = totalGsPlanNums[k][i];
				noAcceptGsPlanNum = noAcceptGsPlanNums[k][i];
				acceptGsPlanNum = acceptGsPlanNums[k][i];
				//验收数量及验收率
				acceptRates[i] = 0;
				if(totalGsPlanNum > 0) {
					acceptRates[i] = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRates[i]);
					acceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (acceptRates[i] > 100)
						acceptRates[i] = 100;
				}
				sdd.setTotalGsPlanNum(totalGsPlanNum);
				sdd.setNoAcceptGsPlanNum(noAcceptGsPlanNum);
				sdd.setAcceptGsPlanNum(acceptGsPlanNum);
				sdd.setAcceptRate(acceptRates[i]);
				
				//学校验收信息以及验收率
				int shouldAcceptSchNum = 0;
				int acceptSchNum = 0;
				int noAcceptSchNum = 0;
				acceptSchNum = acceptSchNums[k][i];
				noAcceptSchNum = noAcceptSchNums[k][i];
				
				shouldAcceptSchNum = acceptSchNum + noAcceptSchNum;
				
				sdd.setShouldAcceptSchNum(shouldAcceptSchNum);
				sdd.setAcceptSchNum(acceptSchNum);
				sdd.setNoAcceptSchNum(noAcceptSchNum);
				
				schAcceptRates[i] = 0;
				if(shouldAcceptSchNum > 0) {
					schAcceptRates[i] = 100 * ((float) acceptSchNum / (float) shouldAcceptSchNum);
					BigDecimal bd = new BigDecimal(schAcceptRates[i]);
					schAcceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRates[i] > 100)
						schAcceptRates[i] = 100;
				}
				sdd.setSchAcceptRate(schAcceptRates[i]);
				
				//菜品总数、未留样数、已留样数、留样率
				int totalDishNum = 0, dishRsDishNum = 0, dishNoRsDishNum = 0;
				totalDishNum = totalDishNums[k][i];
				dishRsDishNum = dishRsDishNums[k][i];
				dishNoRsDishNum = dishNoRsDishNums[k][i];
				sdd.setTotalDishNum(totalDishNum);
				sdd.setRsDishNum(dishRsDishNum);
				sdd.setNoRsDishNum(dishNoRsDishNum);
				dishRsRates[i] = 0;
				if(totalDishNum > 0) {
					dishRsRates[i] = 100 * ((float) dishRsDishNum / (float) totalDishNum);
					BigDecimal bd = new BigDecimal(dishRsRates[i]);
					dishRsRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (dishRsRates[i] > 100)
						dishRsRates[i] = 100;
				}
				sdd.setRsRate(dishRsRates[i]);
				
				//学校留样相关信息
				int shouldRsSchNum = 0;
				int rsSchNum = 0;
				int noRsSchNum = 0;
				
				rsSchNum = rsSchNums[k][i];
				noRsSchNum = noRsSchNums[k][i];
				shouldRsSchNum = rsSchNum + noRsSchNum;
				sdd.setShouldRsSchNum(shouldRsSchNum);
				sdd.setRsSchNum(rsSchNum);
				sdd.setNoRsSchNum(noRsSchNum);
				schRsRates[i] = 0;
				if(shouldRsSchNum > 0) {
					schRsRates[i] = 100 * ((float) rsSchNum / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schRsRates[i]);
					schRsRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schRsRates[i] > 100)
						schRsRates[i] = 100;
				}
				sdd.setSchRsRate(schRsRates[i]);
				
				//预警总数、待处理预警数、已消除预警数、预警处理率
				int totalWarnNum = 0, noProcWarnNum = 0, elimWarnNum = 0;				
				totalWarnNum = totalWarnNums[k][i];
				noProcWarnNum = noProcWarnNums[k][i];
				elimWarnNum = elimWarnNums[k][i];
				sdd.setTotalWarnNum(totalWarnNum);
				sdd.setNoProcWarnNum(totalWarnNum - elimWarnNum);
				sdd.setElimWarnNum(elimWarnNum);
				warnProcRates[i] = 0;
				if(totalWarnNum > 0) {
					warnProcRates[i] = 100 * ((float) elimWarnNum / (float) totalWarnNum);
					BigDecimal bd = new BigDecimal(warnProcRates[i]);
					warnProcRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (warnProcRates[i] > 100)
						warnProcRates[i] = 100;
				}
				sdd.setWarnProcRate(warnProcRates[i]);	
				//餐厨垃圾回收合计，按所在地有效、餐厨垃圾学校回收数量、餐厨垃圾团餐公司回收数量，按所在地有效
				float kwSchRcNum = 0, kwRmcRcNum = 0, kwTotalRcNum = 0;				
				kwSchRcNum = kwSchRcNums[k][i];
				kwRmcRcNum = kwRmcRcNums[k][i];
				kwTotalRcNum = kwTotalRcNums[k][i];
				sdd.setTotalKwRecNum( new BigDecimal(kwTotalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				sdd.setKwSchRecNum( new BigDecimal(kwSchRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				sdd.setKwRmcRecNum( new BigDecimal(kwRmcRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());	
				//废弃油脂回收合计，按所在地有效、废弃油脂学校回收数量、废弃油脂团餐公司回收数量，按所在地有效
				float woSchRcNum = 0, woRmcRcNum = 0, woTotalRcNum = 0;
				woSchRcNum = woSchRcNums[k][i];
				woRmcRcNum = woRmcRcNums[k][i];
				woTotalRcNum = woTotalRcNums[k][i];
				sdd.setTotalWoRecNum( new BigDecimal(woTotalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				sdd.setWoSchRecNum( new BigDecimal(woSchRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				sdd.setWoRmcRecNum( new BigDecimal(woRmcRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				sumDataDets.add(sdd);
			}
		}
		//排序
    	//SortList<SumDataDets> sortList = new SortList<SumDataDets>();  
    	//sortList.Sort(sumDataDets, methods1, sorts1, dataTypes1);
		//时戳
		sddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<SumDataDets> pageBean = new PageBean<SumDataDets>(sumDataDets, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		sddDto.setPageInfo(pageInfo);
		// 设置数据
		sddDto.setSumDataDets(pageBean.getCurPageData());
		// 消息ID
		sddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return sddDto;
	}

	private String getGcRetentionDishByLocalityFromRedis(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, int k,
			int[][] totalDishNums, int[][] dishRsDishNums, int[][] dishNoRsDishNums, int[][] rsSchNums,
			int[][] noRsSchNums, List<Object> distNamesList) {
		int i;
		Map<String, String> gcRetentiondishtotalMap;
		String key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
		}
		String fieldPrefix = "";
		String keyVal = "";
		gcRetentiondishtotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(gcRetentiondishtotalMap != null) {
			for(String curKey : gcRetentiondishtotalMap.keySet()) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}else if(distNamesList!=null && distNamesList.size() >0) {
						if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
							continue;
						}
						if(!distNamesList.contains(curDistId)) {
							continue ;
						}
					}
					// 区域菜品留样和未留样数
					fieldPrefix = curDistId + "_";
					if (curKey.indexOf(fieldPrefix) == 0) {
						String[] curKeys = curKey.split("_");
						if(curKeys.length >= 2)
						{
							if(curKeys[1].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									dishRsDishNums[k][i] = Integer.parseInt(keyVal);
								}
							}
							else if(curKeys[1].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									dishNoRsDishNums[k][i] = Integer.parseInt(keyVal);
								}
							}
						}
					}
					if(curKey.equalsIgnoreCase(curDistId)) {      //区域菜品总数
						keyVal = gcRetentiondishtotalMap.get(curKey);
						if(keyVal != null) {
							totalDishNums[k][i] = Integer.parseInt(keyVal);
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
									rsSchNums[k][i] = Integer.parseInt(keyVal);
								}
							}
							else if(curKeys[2].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									noRsSchNums[k][i] = Integer.parseInt(keyVal);
								}
							}
						}
					}
				}
			}
		}
		return fieldPrefix;
	}

	private String getGcRetentionDishByDepartmentFromRedis(String departmentId,String[] dates, List<DepartmentObj> departmentList, int k,
			int[][] totalDishNums, int[][] dishRsDishNums, int[][] dishNoRsDishNums, int[][] rsSchNums,
			int[][] noRsSchNums, List<Object> departmentIdList) {
		int i;
		Map<String, String> gcRetentiondishtotalMap;
		String key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
		}
		String fieldPrefix = "";
		String keyVal = "";
		gcRetentiondishtotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(gcRetentiondishtotalMap != null) {
			for(String curKey : gcRetentiondishtotalMap.keySet()) {
				for (i = 0; i < departmentList.size(); i++) {
					DepartmentObj departmentObj = departmentList.get(i);
					String curDepartmentId = departmentObj.getDepartmentId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(departmentId != null) {
						if(!curDepartmentId.equals(departmentId))
							continue ;
					}
					if(departmentIdList!=null && departmentIdList.size() >0) {
						if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
							continue;
						}
						if(!departmentIdList.contains(curDepartmentId)) {
							continue ;
						}
					}
					// 区域菜品留样和未留样数
					fieldPrefix = "department_"+curDepartmentId + "_";
					if (curKey.indexOf(fieldPrefix) == 0) {
						String[] curKeys = curKey.split("_");
						if(curKeys.length >= 2)
						{
							if(curKeys[2].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									dishRsDishNums[k][i] = Integer.parseInt(keyVal);
								}
							}
							else if(curKeys[2].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									dishNoRsDishNums[k][i] = Integer.parseInt(keyVal);
								}
							}
						}
					}
					if(curKey.equalsIgnoreCase(curDepartmentId)) {      //区域菜品总数
						keyVal = gcRetentiondishtotalMap.get(curKey);
						if(keyVal != null) {
							totalDishNums[k][i] = Integer.parseInt(keyVal);
						}
					}
					
					// 区域学校留样和未留样数
					fieldPrefix = "school-department" + "_" + curDepartmentId + "_";
					if (curKey.indexOf(fieldPrefix) == 0) {
						String[] curKeys = curKey.split("_");
						if(curKeys.length >= 3)
						{
							if(curKeys[2].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									rsSchNums[k][i] = Integer.parseInt(keyVal);
								}
							}
							else if(curKeys[2].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									noRsSchNums[k][i] = Integer.parseInt(keyVal);
								}
							}
						}
					}
				}
			}
		}
		return fieldPrefix;
	}
	
	private void getDistributionByLocalityFromRedis(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, int k,
			int[][] totalGsPlanNums, int[][] noAcceptGsPlanNums, int[][] acceptGsPlanNums, int[][] acceptSchNums,
			int[][] noAcceptSchNums, List<Object> distNamesList) {
		int i;
		Map<String, String> distributionTotalMap;
		//验收数据
		String key = dates[k]   + DataKeyConfig.distributionTotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
		}
		String field = "";
		String keyVal = "";
		distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if (distributionTotalMap != null) {
			for (i = 0; i < tedList.size(); i++) {
				TEduDistrictDo curTdd = tedList.get(i);
				String curDistId = curTdd.getId();
				//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if(distIdorSCName != null) {
					if(!curDistId.equalsIgnoreCase(distIdorSCName))
						continue ;
				}else if(distNamesList!=null && distNamesList.size() >0) {
					if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
						continue;
					}
					if(!distNamesList.contains(curDistId)) {
						continue ;
					}
				}
				// 区域配货计划总数
				field = "area" + "_" + curDistId;
				totalGsPlanNums[k][i] = 0;
				keyVal = distributionTotalMap.get(field);
				if(keyVal != null) {
					totalGsPlanNums[k][i] = Integer.parseInt(keyVal);
					if(totalGsPlanNums[k][i] < 0)
						totalGsPlanNums[k][i] = 0;
				}
				// 已验收数
				field = "area" + "_" + curDistId + "_" + "status" + "_3";
				acceptGsPlanNums[k][i] = 0;
				keyVal = distributionTotalMap.get(field);
				if(keyVal != null) {
					acceptGsPlanNums[k][i] = Integer.parseInt(keyVal);
					if(acceptGsPlanNums[k][i] < 0)
						acceptGsPlanNums[k][i] = 0;
				}
				// 未验收数
				noAcceptGsPlanNums[k][i] = totalGsPlanNums[k][i] - acceptGsPlanNums[k][i];
				
				/**
				 * 学校验收信息
				 */
				// 已验收数
				acceptSchNums[k][i]=0;
				field = "school-area" + "_" + curDistId + "_" + "status" + "_3";
				acceptSchNums[k][i] = 0;
				keyVal = distributionTotalMap.get(field);
				if(keyVal != null) {
					acceptSchNums[k][i] = Integer.parseInt(keyVal);
					if(acceptSchNums[k][i] < 0)
						acceptSchNums[k][i] = 0;
				}
				// 未验收学校
				noAcceptSchNums[k][i] = 0;
				for(int m = -2; m <= 2; m++) {
					field = "school-area" + "_" + curDistId + "_" + "status" + "_" + m;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						int noAcceptSchNum = Integer.parseInt(keyVal);
						if(noAcceptSchNum < 0) {
							noAcceptSchNum = 0;
						}
						noAcceptSchNums[k][i] += noAcceptSchNum;
					}
				}
				
			}
		}
	}
	
	private void getDistributionByDepartmentFromRedis(String departmentId,String[] dates, List<DepartmentObj> departmentList, int k,
			int[][] totalGsPlanNums, int[][] noAcceptGsPlanNums, int[][] acceptGsPlanNums, int[][] acceptSchNums,
			int[][] noAcceptSchNums, List<Object> departmentidsList) {
		int i;
		Map<String, String> distributionTotalMap;
		//验收数据
		String key = dates[k]   + DataKeyConfig.distributionTotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
		}
		String field = "";
		String keyVal = "";
		distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if (distributionTotalMap != null) {
			for (i = 0; i < departmentList.size(); i++) {
				DepartmentObj departmentObj = departmentList.get(i);
				String curDepartmentId = departmentObj.getDepartmentId();
				//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if(departmentId != null) {
					if(!curDepartmentId.equalsIgnoreCase(departmentId))
						continue ;
				}
				if(departmentidsList!=null && departmentidsList.size() >0) {
					if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
						continue;
					}
					if(!departmentidsList.contains(curDepartmentId)) {
						continue ;
					}
				}
				// 区域配货计划总数
				for(int l = -2; l < 4; l++) {
					field = "department" + "_" + curDepartmentId + "_" + "status" + "_" + l;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						int curConSchNum = Integer.parseInt(keyVal);
						if(curConSchNum < 0)
							curConSchNum = 0;
						totalGsPlanNums[k][i] += curConSchNum;
					}
				}
				
				// 已验收数
				field = "department" + "_" + curDepartmentId + "_" + "status" + "_3";
				acceptGsPlanNums[k][i] = 0;
				keyVal = distributionTotalMap.get(field);
				if(keyVal != null) {
					acceptGsPlanNums[k][i] = Integer.parseInt(keyVal);
					if(acceptGsPlanNums[k][i] < 0)
						acceptGsPlanNums[k][i] = 0;
				}
				// 未验收数
				noAcceptGsPlanNums[k][i] = totalGsPlanNums[k][i] - acceptGsPlanNums[k][i];
				
				/**
				 * 学校验收信息
				 */
				// 已验收数
				acceptSchNums[k][i]=0;
				field = "school-department" + "_" + curDepartmentId + "_" + "status" + "_3";
				acceptSchNums[k][i] = 0;
				keyVal = distributionTotalMap.get(field);
				if(keyVal != null) {
					acceptSchNums[k][i] = Integer.parseInt(keyVal);
					if(acceptSchNums[k][i] < 0)
						acceptSchNums[k][i] = 0;
				}
				// 未验收学校
				noAcceptSchNums[k][i] = 0;
				for(int m = -2; m <= 2; m++) {
					field = "school-department" + "_" + curDepartmentId + "_" + "status" + "_" + m;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						int noAcceptSchNum = Integer.parseInt(keyVal);
						if(noAcceptSchNum < 0) {
							noAcceptSchNum = 0;
						}
						noAcceptSchNums[k][i] += noAcceptSchNum;
					}
				}
				
			}
		}
	}
	
	private void getDistributionByLocalityFromHive(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			int[][] totalGsPlanNums, int[][] noAcceptGsPlanNums, int[][] acceptGsPlanNums, int[][] acceptSchNums,
			int[][] noAcceptSchNums, List<Object> distNamesList,
			String departmentId,List<Object> departmentIdsList,
			DbHiveGsService dbHiveGsService) {
		
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
		//配送计划数量
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();
		//已验收数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		//已指派
		Map<String,Integer> assignGsPlanNumsMap = new HashMap<String,Integer>();
		//已配送
		Map<String,Integer> dispGsPlanNumsMap = new HashMap<String,Integer>();
		
		List<SchGsCommon> dishList = new ArrayList<>();
		dishList = dbHiveGsService.getGsList(DataKeyConfig.talbeLedgerMasterTotalD,listYearMonth, startDate, endDateAddOne, distIdorSCName, null, 
				-1, -1, null, null,departmentId,departmentIdsList, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchGsCommon schDishCommon: dishList) {
				
				//区域为空，代表全市数据，此处去除
				if(CommonUtil.isEmpty(schDishCommon.getDistId())) {
					continue;
				}
				
				String dataKey =schDishCommon.getActionDate() + "_" + schDishCommon.getDistId();
				
				if(schDishCommon.getDisSealStatus() ==null || "-1".equals(schDishCommon.getDisSealStatus())) {
					if(schDishCommon.getHaulStatus() ==null) {
						totalGsPlanNumMap.put(dataKey, 
								(totalGsPlanNumMap.get(dataKey)==null?0:totalGsPlanNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
						
					}else if(schDishCommon.getHaulStatus() == 3) {
						//已验收
						acceptSchNumsMap.put(dataKey, 
								(acceptSchNumsMap.get(dataKey)==null?0:acceptSchNumsMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
						
						acceptGsPlanNumMap.put(dataKey, 
								(acceptGsPlanNumMap.get(dataKey)==null?0:acceptGsPlanNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
					}else {
						//未验收：haul_status = -4 or haul_status = -2 or haul_status = -1 or haul_status = 0 or haul_status = 1 or haul_status = 2
						noAcceptSchNumsMap.put(dataKey, 
								(noAcceptSchNumsMap.get(dataKey)==null?0:noAcceptSchNumsMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
						
						noAcceptGsPlanNumMap.put(dataKey, 
								(noAcceptGsPlanNumMap.get(dataKey)==null?0:noAcceptGsPlanNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
						
					}
				}
				
				if(schDishCommon.getHaulStatus() !=null) {
					//已指派:0 已指派（未配送） 1配送中 2 待验收（已配送）3已验收
					if(schDishCommon.getHaulStatus() == 0 || schDishCommon.getHaulStatus() == 1 || schDishCommon.getHaulStatus() == 2
							|| schDishCommon.getHaulStatus() == 3) {
						dispGsPlanNumsMap.put(dataKey, 
								(dispGsPlanNumsMap.get(dataKey)==null?0:dispGsPlanNumsMap.get(dataKey)) 
								+ schDishCommon.getTotal());
						
					}
					
					//已配送: 1配送中 2 待验收（已配送）3已验收
					if(schDishCommon.getHaulStatus() == 1 || schDishCommon.getHaulStatus() == 2
							|| schDishCommon.getHaulStatus() == 3) {
						assignGsPlanNumsMap.put(dataKey, 
								(assignGsPlanNumsMap.get(dataKey)==null?0:assignGsPlanNumsMap.get(dataKey)) 
								+ schDishCommon.getTotal());
						
					}
				}
			}
			
			//Map转换成数组，方便统一处理
			for (int k = 0; k < dates.length; k++) {
				for (int i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}else if(distNamesList!=null && distNamesList.size() >0) {
						if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
							continue;
						}
						if(!distNamesList.contains(curDistId)) {
							continue ;
						}
					}
					
					String dataKey = dates[0].replaceAll("-", "/") + "_" + curDistId;
					totalGsPlanNums[k][i] =  totalGsPlanNumMap.get(dataKey)==null?0:totalGsPlanNumMap.get(dataKey);
					noAcceptGsPlanNums[k][i] =  noAcceptGsPlanNumMap.get(dataKey)==null?0:noAcceptGsPlanNumMap.get(dataKey); 
					acceptGsPlanNums[k][i] =  acceptGsPlanNumMap.get(dataKey)==null?0:acceptGsPlanNumMap.get(dataKey); 
					acceptSchNums[k][i] =  acceptSchNumsMap.get(dataKey)==null?0:acceptSchNumsMap.get(dataKey);
					noAcceptSchNums[k][i] =  noAcceptSchNumsMap.get(dataKey)==null?0:noAcceptSchNumsMap.get(dataKey);
				}
			}
		}
	}
	
	private void getDistributionByDepartmentFromHive(String[] dates, List<DepartmentObj> departmentList,
			int[][] totalGsPlanNums, int[][] noAcceptGsPlanNums, int[][] acceptGsPlanNums, int[][] acceptSchNums,
			int[][] noAcceptSchNums,
			String departmentId,List<Object> departmentIdsList,
			DbHiveGsService dbHiveGsService) {
		
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
		//配送计划数量
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();
		//已验收数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		//已指派
		Map<String,Integer> assignGsPlanNumsMap = new HashMap<String,Integer>();
		//已配送
		Map<String,Integer> dispGsPlanNumsMap = new HashMap<String,Integer>();
		
		List<SchGsCommon> dishList = new ArrayList<>();
		dishList = dbHiveGsService.getGsList(DataKeyConfig.talbeLedgerMasterTotalD,listYearMonth, startDate, endDateAddOne, null, null, 
				-1, -1, null, null,departmentId,departmentIdsList, 4);
		if(dishList !=null && dishList.size() > 0) {
			for(SchGsCommon schDishCommon: dishList) {
				
				//区域为空，代表全市数据，此处去除
				if(CommonUtil.isEmpty(schDishCommon.getDepartmentId())) {
					continue;
				}
				
				String dataKey =schDishCommon.getActionDate() + "_" + schDishCommon.getDepartmentId();
				
				if(schDishCommon.getDisSealStatus() ==null || "-1".equals(schDishCommon.getDisSealStatus())) {
					if(schDishCommon.getHaulStatus() ==null) {
						totalGsPlanNumMap.put(dataKey, 
								(totalGsPlanNumMap.get(dataKey)==null?0:totalGsPlanNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
						
					}else if(schDishCommon.getHaulStatus() == 3) {
						//已验收
						acceptSchNumsMap.put(dataKey, 
								(acceptSchNumsMap.get(dataKey)==null?0:acceptSchNumsMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
						
						acceptGsPlanNumMap.put(dataKey, 
								(acceptGsPlanNumMap.get(dataKey)==null?0:acceptGsPlanNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
					}else {
						//未验收：haul_status = -4 or haul_status = -2 or haul_status = -1 or haul_status = 0 or haul_status = 1 or haul_status = 2
						noAcceptSchNumsMap.put(dataKey, 
								(noAcceptSchNumsMap.get(dataKey)==null?0:noAcceptSchNumsMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
						
						noAcceptGsPlanNumMap.put(dataKey, 
								(noAcceptGsPlanNumMap.get(dataKey)==null?0:noAcceptGsPlanNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
						
					}
				
					if(schDishCommon.getHaulStatus() !=null) {
						//已指派:0 已指派（未配送） 1配送中 2 待验收（已配送）3已验收
						if(schDishCommon.getHaulStatus() == 0 || schDishCommon.getHaulStatus() == 1 || schDishCommon.getHaulStatus() == 2
								|| schDishCommon.getHaulStatus() == 3) {
							dispGsPlanNumsMap.put(dataKey, 
									(dispGsPlanNumsMap.get(dataKey)==null?0:dispGsPlanNumsMap.get(dataKey)) 
									+ schDishCommon.getTotal());
							
						}
						
						//已配送: 1配送中 2 待验收（已配送）3已验收
						if(schDishCommon.getHaulStatus() == 1 || schDishCommon.getHaulStatus() == 2
								|| schDishCommon.getHaulStatus() == 3) {
							assignGsPlanNumsMap.put(dataKey, 
									(assignGsPlanNumsMap.get(dataKey)==null?0:assignGsPlanNumsMap.get(dataKey)) 
									+ schDishCommon.getTotal());
							
						}
					}
				}
			}
			
			//Map转换成数组，方便统一处理
			for (int k = 0; k < dates.length; k++) {
				for (int i = 0; i < departmentList.size(); i++) {
					DepartmentObj departmentObj = departmentList.get(i);
					String curDepartmentId = departmentObj.getDepartmentId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(departmentId != null) {
						if(!curDepartmentId.equals(departmentId))
							continue ;
					}
					
					if(departmentIdsList!=null && departmentIdsList.size() >0) {
						if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
							continue;
						}
						if(!departmentIdsList.contains(curDepartmentId)) {
							continue ;
						}
					}
					
					String dataKey = dates[0].replaceAll("-", "/") + "_" + curDepartmentId;
					totalGsPlanNums[k][i] =  totalGsPlanNumMap.get(dataKey)==null?0:totalGsPlanNumMap.get(dataKey);
					noAcceptGsPlanNums[k][i] =  noAcceptGsPlanNumMap.get(dataKey)==null?0:noAcceptGsPlanNumMap.get(dataKey); 
					acceptGsPlanNums[k][i] =  acceptGsPlanNumMap.get(dataKey)==null?0:acceptGsPlanNumMap.get(dataKey); 
					acceptSchNums[k][i] =  acceptSchNumsMap.get(dataKey)==null?0:acceptSchNumsMap.get(dataKey);
					noAcceptSchNums[k][i] =  noAcceptSchNumsMap.get(dataKey)==null?0:noAcceptSchNumsMap.get(dataKey);
				}
			}
		}
	}

	/**
	 * 获取排菜信息（从redis中获取）
	 * @param distIdorSCName
	 * @param dates
	 * @param tedList
	 * @param k
	 * @param totalMealSchNums
	 * @param distDishSchNums
	 * @param distNoDishSchNums
	 * @param distNamesList
	 */
	private void getDishDataByLocalityFromRedis(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, int k,
			int[][] totalMealSchNums, int[][] distDishSchNums, int[][] distNoDishSchNums, List<Object> distNamesList) {
		int i;
		Map<String, String> platoonFeedTotalMap;
		String disKeyVal = "";
		String disFieldPrefix = "";
		String dishKey = dates[k]   + DataKeyConfig.platoonfeedTotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			dishKey = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
		}
		platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, dishKey);
		if(platoonFeedTotalMap != null) {
			for(String curKey : platoonFeedTotalMap.keySet()) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}else if(distNamesList!=null && distNamesList.size() >0) {
						if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
							continue;
						}
						if(!distNamesList.contains(curDistId)) {
							continue ;
						}
					}
					// 区域排菜学校供餐数
					disFieldPrefix = curDistId + "_";
					int mealSchNum = 0, dishSchNum = 0, noDishSchNum = 0;
					if (curKey.indexOf(disFieldPrefix) == 0) {
						String[] curKeys = curKey.split("_");
						if(curKeys.length >= 3)
						{
							if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("已排菜")) {
								disKeyVal = platoonFeedTotalMap.get(curKey);
								if(disKeyVal != null) {
									mealSchNum = Integer.parseInt(disKeyVal);
									dishSchNum = mealSchNum;
								}
							}
							else if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("未排菜")) {
								disKeyVal = platoonFeedTotalMap.get(curKey);
								if(disKeyVal != null) {
									mealSchNum = Integer.parseInt(disKeyVal);
									noDishSchNum = mealSchNum;
								}
							}
						}
					}
					totalMealSchNums[k][i] += mealSchNum;
					distDishSchNums[k][i] += dishSchNum;
					distNoDishSchNums[k][i] += noDishSchNum;
				}
			}
		}
	}
	
	/**
	 * 获取排菜信息（从redis中获取）
	 * @param distIdorSCName
	 * @param dates
	 * @param tedList
	 * @param k
	 * @param totalMealSchNums
	 * @param distDishSchNums
	 * @param distNoDishSchNums
	 * @param distNamesList
	 */
	private void getDishDataByDepartmentFromRedis(String departmentId,String[] dates,Map<Integer, String> schoolDepartmentMap,int [] schOwnTypes, int k,
			int[][] totalMealSchNums, int[][] distDishSchNums, int[][] distNoDishSchNums, List<Object> departmentIdsList) {
		int i;
		Map<String, String> platoonFeedTotalMap;
		String disKeyVal = "";
		String disFieldPrefix = "";
		String dishKey = dates[k]   + DataKeyConfig.platoonfeedTotal;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			dishKey = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
		}
		platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, dishKey);
		if(platoonFeedTotalMap != null) {
			for(String curKey : platoonFeedTotalMap.keySet()) {
				for (i = 0; i < schOwnTypes.length; i++) {
					String curDepartmentId = String.valueOf(schOwnTypes[i]);
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(departmentId != null) {
						if(!curDepartmentId.equals(departmentId))
							continue ;
					}else if(departmentIdsList!=null && departmentIdsList.size() >0) {
						if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
							continue;
						}
						if(!departmentIdsList.contains(curDepartmentId)) {
							continue ;
						}
					}
					// 区域排菜学校供餐数
					disFieldPrefix = "department_"+curDepartmentId + "_";
					int mealSchNum = 0, dishSchNum = 0, noDishSchNum = 0;
					if (curKey.indexOf(disFieldPrefix) == 0) {
						String[] curKeys = curKey.split("_");
						if(curKeys.length >= 3)
						{
							if(curKeys[2].equalsIgnoreCase("供餐") && curKeys[3].equalsIgnoreCase("已排菜")) {
								disKeyVal = platoonFeedTotalMap.get(curKey);
								if(disKeyVal != null) {
									mealSchNum = Integer.parseInt(disKeyVal);
									dishSchNum = mealSchNum;
								}
							}
							else if(curKeys[2].equalsIgnoreCase("供餐") && curKeys[3].equalsIgnoreCase("未排菜")) {
								disKeyVal = platoonFeedTotalMap.get(curKey);
								if(disKeyVal != null) {
									mealSchNum = Integer.parseInt(disKeyVal);
									noDishSchNum = mealSchNum;
								}
							}
						}
					}
					totalMealSchNums[k][i] += mealSchNum;
					distDishSchNums[k][i] += dishSchNum;
					distNoDishSchNums[k][i] += noDishSchNum;
				}
			}
		}
	}
	/**
	 * 获取排菜信息（从Hive中获取）
	 * @param distIdorSCName
	 * @param dates
	 * @param tedList
	 * @param k
	 * @param totalMealSchNums
	 * @param distDishSchNums
	 * @param distNoDishSchNums
	 * @param distNamesList
	 */
	private void getDishDataByLocalityFromHive(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			int[][] totalMealSchNums, int[][] distDishSchNums, int[][] distNoDishSchNums,
			int[][] standardNums,int[][] supplementNums,int[][] beOverdueNums,int[][] noDataNums,
			List<Object> distNamesList,
			DbHiveDishService dbHiveDishService) {
		
		// 时间段内各区餐厨垃圾学校回收总数
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
		//学校总数
		Map<String,Integer> totalSchNumMap = new HashMap<>();
		//应排菜学校
		Map<String,Integer> totalMealSchNumMap = new HashMap<>();
		//已排菜学校
		Map<String,Integer> distDishSchNumMap = new HashMap<>();
		//未排菜学校
		Map<String,Integer> distNoDishSchNumMap = new HashMap<>();
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, distIdorSCName, distNamesList, 
				-1, -1, null, null,departmentId, null,0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				String dataKey =schDishCommon.getDishDate() + "_" + schDishCommon.getDistId();
				if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNumMap.put(dataKey, 
									(totalSchNumMap.get(dataKey)==null?0:totalSchNumMap.get(dataKey)) 
									+ schDishCommon.getTotal());
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
							totalMealSchNumMap.put(dataKey, 
									(totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey)) 
									+ schDishCommon.getTotal());
						}else{
							if(schDishCommon.getHavePlatoon() == 1) {
								//已排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =1
								distDishSchNumMap.put(dataKey, 
										(distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey)) 
										+ schDishCommon.getTotal());
							}else if(schDishCommon.getHavePlatoon() == 0) {
								//未排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =0
								distNoDishSchNumMap.put(dataKey, 
										(distNoDishSchNumMap.get(dataKey)==null?0:distNoDishSchNumMap.get(dataKey)) 
										+ schDishCommon.getTotal());
							}
						}
					}
				}
				
				if(schDishCommon.getPlatoonDealStatus() !=null && schDishCommon.getPlatoonDealStatus() != -1 ) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if(schDishCommon.getPlatoonDealStatus() == 1) {
						standardNumMap.put(dataKey, 
								(standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 2) {
						supplementNumMap.put(dataKey, 
								(supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 3) {
						beOverdueNumMap.put(dataKey, 
								(beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 4) {
						noDataNumMap.put(dataKey, 
								(noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
					}
				}
			}
			
			for (int k = 0; k < dates.length; k++) {
				for (int i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}else if(distNamesList!=null && distNamesList.size() >0) {
						if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
							continue;
						}
						if(!distNamesList.contains(curDistId)) {
							continue ;
						}
					}
					
					String dataKey = dates[0].replaceAll("-", "/") + "_" + curDistId;
					int totalDistSchNum = totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey);
					int distDishSchNum = distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey);
					int distNoDishSchNum = distNoDishSchNumMap.get(dataKey)==null?0:distNoDishSchNumMap.get(dataKey);
					
					//1 表示规范录入
					int standardNum = standardNumMap.get(String.valueOf(curDistId))==null?0:standardNumMap.get(String.valueOf(curDistId));
					//2 表示补录
					int supplementNum = supplementNumMap.get(String.valueOf(curDistId))==null?0:supplementNumMap.get(String.valueOf(curDistId));
					//3 表示逾期补录
					int beOverdueNum = beOverdueNumMap.get(String.valueOf(curDistId))==null?0:beOverdueNumMap.get(String.valueOf(curDistId));
					//4 表示无数据
					int noDataNum = noDataNumMap.get(String.valueOf(curDistId))==null?0:noDataNumMap.get(String.valueOf(curDistId));
					
					totalMealSchNums[k][i] = totalDistSchNum; 
					distDishSchNums[k][i] = distDishSchNum; 
					distNoDishSchNums[k][i] = distNoDishSchNum;
					
					standardNums[k][i] = standardNum;
					supplementNums[k][i] = supplementNum;
					beOverdueNums[k][i] = beOverdueNum;
					noDataNums[k][i] = noDataNum;
					
				}
			}
					
		}
	}
	
	
	/**
	 * 获取排菜信息（从Hive中获取）
	 * @param distIdorSCName
	 * @param dates
	 * @param tedList
	 * @param k
	 * @param totalMealSchNums
	 * @param distDishSchNums
	 * @param distNoDishSchNums
	 * @param distNamesList
	 */
	private void getDishDataByDepartmentFromHive(String departmentId, String[] dates, List<DepartmentObj> deparmentList,
			int[][] totalMealSchNums, int[][] distDishSchNums, int[][] distNoDishSchNums,
			int[][] standardNums,int[][] supplementNums,int[][] beOverdueNums,int[][] noDataNums,
			List<Object> departmentIdList,
			DbHiveDishService dbHiveDishService) {
		
		// 时间段内各区餐厨垃圾学校回收总数
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
		//学校总数
		Map<String,Integer> totalSchNumMap = new HashMap<>();
		//应排菜学校
		Map<String,Integer> totalMealSchNumMap = new HashMap<>();
		//已排菜学校
		Map<String,Integer> distDishSchNumMap = new HashMap<>();
		//未排菜学校
		Map<String,Integer> distNoDishSchNumMap = new HashMap<>();
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, null, null, 
				-1, -1, null, null,departmentId, departmentIdList,4);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				String dataKey =schDishCommon.getDishDate() + "_" + schDishCommon.getDepartmentId();
				if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNumMap.put(dataKey, 
									(totalSchNumMap.get(dataKey)==null?0:totalSchNumMap.get(dataKey)) 
									+ schDishCommon.getTotal());
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
							totalMealSchNumMap.put(dataKey, 
									(totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey)) 
									+ schDishCommon.getTotal());
						}else{
							if(schDishCommon.getHavePlatoon() == 1) {
								//已排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =1
								distDishSchNumMap.put(dataKey, 
										(distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey)) 
										+ schDishCommon.getTotal());
							}else if(schDishCommon.getHavePlatoon() == 0) {
								//未排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =0
								distNoDishSchNumMap.put(dataKey, 
										(distNoDishSchNumMap.get(dataKey)==null?0:distNoDishSchNumMap.get(dataKey)) 
										+ schDishCommon.getTotal());
							}
						}
					}
				}
				
				if(schDishCommon.getPlatoonDealStatus() !=null && schDishCommon.getPlatoonDealStatus() != -1 ) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if(schDishCommon.getPlatoonDealStatus() == 1) {
						standardNumMap.put(dataKey, 
								(standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 2) {
						supplementNumMap.put(dataKey, 
								(supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 3) {
						beOverdueNumMap.put(dataKey, 
								(beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 4) {
						noDataNumMap.put(dataKey, 
								(noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey)) 
								+ schDishCommon.getTotal());
					}
				}
			}
			
			for (int k = 0; k < dates.length; k++) {
				for (int i = 0; i < deparmentList.size(); i++) {
					DepartmentObj departmentObj = deparmentList.get(i);
					String curDepartmentId = departmentObj.getDepartmentId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(departmentId != null) {
						if(!curDepartmentId.equals(departmentId))
							continue ;
					}
					
					if(departmentIdList!=null && departmentIdList.size() >0) {
						if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
							continue;
						}
						if(!departmentIdList.contains(curDepartmentId)) {
							continue ;
						}
					}
					
					String dataKey = dates[0].replaceAll("-", "/") + "_" + curDepartmentId;
					int totalDistSchNum = totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey);
					int distDishSchNum = distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey);
					int distNoDishSchNum = distNoDishSchNumMap.get(dataKey)==null?0:distNoDishSchNumMap.get(dataKey);
					
					//1 表示规范录入
					int standardNum = standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey);
					//2 表示补录
					int supplementNum = supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey);
					//3 表示逾期补录
					int beOverdueNum = beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey);
					//4 表示无数据
					int noDataNum = noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey);
					
					totalMealSchNums[k][i] = totalDistSchNum; 
					distDishSchNums[k][i] = distDishSchNum; 
					distNoDishSchNums[k][i] = distNoDishSchNum;
					
					standardNums[k][i] = standardNum;
					supplementNums[k][i] = supplementNum;
					beOverdueNums[k][i] = beOverdueNum;
					noDataNums[k][i] = noDataNum;
					
				}
			}
					
		}
	}

	/**
	 * 获取餐厨垃圾、废弃油脂（从redis库中获取） 按所在区
	 * @param distIdorSCName
	 * @param dates
	 * @param tedList
	 * @param dbHiveRecyclerWasteService
	 * @param field
	 * @param kwSchRcFreqs
	 * @param kwRmcRcFreqs
	 * @param kwSchRcNums
	 * @param kwRmcRcNums
	 * @param kwTotalRcNums
	 * @param woSchRcFreqs
	 * @param woRmcRcFreqs
	 * @param woSchRcNums
	 * @param woRmcRcNums
	 * @param woTotalRcNums
	 * @param distNamesList
	 * @param days
	 */
	private void getRecyclerWasteByLocalityFromHive(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService, String field, int[][] kwSchRcFreqs,
			int[][] kwRmcRcFreqs, float[][] kwSchRcNums, float[][] kwRmcRcNums, float[][] kwTotalRcNums,
			int[][] woSchRcFreqs, int[][] woRmcRcFreqs, float[][] woSchRcNums, float[][] woRmcRcNums,
			float[][] woTotalRcNums, List<Object> distNamesList, int days) {
		int i;
		int k;
		// 时间段内各区餐厨垃圾学校回收总数
		//学校餐厨垃圾集合 key:区号 value ：回收数量
		Map<String,Float> totalSchRcNumMap = new HashMap<String,Float>();
		//团餐公司餐厨垃圾集合 key:区号 value ：回收数量
		Map<String,Float> totalRmCRcNumMap = new HashMap<String,Float>();
		//学校餐废弃油脂集合 key:区号 value ：回收数量
		Map<String,Float> totalSchOilRcNumMap = new HashMap<String,Float>();
		//团餐公司废弃油脂集合 key:区号 value ：回收数量
		Map<String,Float> totalRmCOilRcNumMap = new HashMap<String,Float>();
		
		// 时间段内各区餐厨垃圾学校回收总数
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
		List<KwCommonRecs> warnCommonLicList = new ArrayList<>();
		warnCommonLicList = dbHiveRecyclerWasteService.getRecyclerWasteList(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
				distNamesList, null, null,-1,-1,null,null, null, null,0);
		float totalRcNum = 0;
		String totalRcNumMapKey="";
		for(KwCommonRecs warnCommonLics : warnCommonLicList) {
			if(CommonUtil.isEmpty(warnCommonLics.getDistName())) {
				continue;
			}
			//PlatformType:1为教委端 2为团餐端 type:1餐厨垃圾，2废弃油脂
			if(warnCommonLics.getPlatformType() == 1) {
				if(StringUtils.isEmpty(warnCommonLics.getDistName())) {
					continue;
				}
				totalRcNumMapKey = warnCommonLics.getRecDate().replace("/", "-") + "_" +warnCommonLics.getDistName();
				//学校
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalSchRcNumMap.get(totalRcNumMapKey)==null?0:totalSchRcNumMap.get(totalRcNumMapKey);
					totalRcNum = totalRcNum+ warnCommonLics.getRecyclerSum();
					totalSchRcNumMap.put(totalRcNumMapKey, totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalSchOilRcNumMap.get(totalRcNumMapKey)==null?0:totalSchOilRcNumMap.get(totalRcNumMapKey);
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalSchOilRcNumMap.put(totalRcNumMapKey, totalRcNum);
				}
			}else if(warnCommonLics.getPlatformType() == 2) {
				//团餐公司
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalRmCRcNumMap.get(totalRcNumMapKey)==null?0:totalRmCRcNumMap.get(totalRcNumMapKey);
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalRmCRcNumMap.put(totalRcNumMapKey, totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalRmCOilRcNumMap.get(totalRcNumMapKey)==null?0:totalRmCOilRcNumMap.get(totalRcNumMapKey);
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalRmCOilRcNumMap.put(totalRcNumMapKey, totalRcNum);
				}
			}
		}
		for (k = 0; k < dates.length; k++) {
			for (i = 0; i < tedList.size(); i++) {
				TEduDistrictDo curTdd = tedList.get(i);
				String curDistId = curTdd.getId();
				//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if(distIdorSCName != null) {
					if(!curDistId.equals(distIdorSCName))
						continue ;
				}else if(distNamesList!=null && distNamesList.size() >0) {
					if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
						continue;
					}
					if(!distNamesList.contains(curDistId)) {
						continue ;
					}
				}
				
				totalRcNumMapKey = dates[k]+"_"+curDistId;
				
				totalRcNum = totalSchRcNumMap.get(totalRcNumMapKey)==null?0:totalSchRcNumMap.get(totalRcNumMapKey);
				float totalRmCRcNum = totalRmCRcNumMap.get(totalRcNumMapKey)==null?0:totalRmCRcNumMap.get(totalRcNumMapKey);
				float totalSchOilRcNum = totalSchOilRcNumMap.get(totalRcNumMapKey)==null?0:totalSchOilRcNumMap.get(totalRcNumMapKey);
				float totalRmCOilRcNum = totalRmCOilRcNumMap.get(totalRcNumMapKey)==null?0:totalRmCOilRcNumMap.get(totalRcNumMapKey);
				
				//学校餐厨垃圾
				kwSchRcNums[k][i] = totalRcNum;
				//团餐公司餐厨垃圾
				kwRmcRcNums[k][i] = totalRmCRcNum;
				//学校餐厨垃圾+团餐公司餐厨垃圾
				kwTotalRcNums[k][i] = totalRcNum + totalRmCRcNum;

				//学校废弃油脂
				woSchRcNums[k][i] = totalSchOilRcNum;
				//团餐公司废弃油脂
				woRmcRcNums[k][i] = totalRmCOilRcNum;
				//学校废弃油脂 + 团餐公司废弃油脂
				woTotalRcNums[k][i] = totalSchOilRcNum + totalRmCOilRcNum;
				
				//餐厨垃圾回收
				//餐厨垃圾学校回收
				BigDecimal bd = new BigDecimal(kwSchRcNums[k][i]);
				kwSchRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，餐厨垃圾学校回收次数：" + kwSchRcFreqs[k][i]
						+ "，餐厨垃圾学校回收数量：" + kwSchRcNums[k][i] + " 桶" + "，field = " + field);
				//餐厨垃圾团餐公司回收
				bd = new BigDecimal(kwRmcRcNums[k][i]);
				kwRmcRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，餐厨垃圾团餐公司回收次数：" + kwRmcRcFreqs[k][i]
						+ "，餐厨垃圾团餐公司回收数量：" + kwRmcRcNums[k][i] + " 桶" + "，field = " + field);
				kwTotalRcNums[k][i] = kwSchRcNums[k][i] + kwRmcRcNums[k][i];					
				//废弃油脂回收
				//废弃油脂学校回收
				bd = new BigDecimal(woSchRcNums[k][i]);
				woSchRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，废弃油脂学校回收次数：" + woSchRcFreqs[k][i]
						+ "，废弃油脂学校回收数量：" + woSchRcNums[k][i] + " 桶" + "，field = " + field);
				//废弃油脂团餐公司回收
				bd = new BigDecimal(woRmcRcNums[k][i]);
				woRmcRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，/废弃油脂团餐公司回收次数：" + woRmcRcFreqs[k][i]
						+ "，/废弃油脂团餐公司回收数量：" + woRmcRcNums[k][i] + " 桶" + "，field = " + field);
				woTotalRcNums[k][i] = woSchRcNums[k][i] + woRmcRcNums[k][i];
				
			}
		}
	}

	/**
	 * 获取餐厨垃圾、废弃油脂（从redis库中获取）
	 * @param distIdorSCName
	 * @param dates
	 * @param tedList
	 * @param fieldPrefix
	 * @param k
	 * @param kwSchRcFreqs
	 * @param kwRmcRcFreqs
	 * @param kwSchRcNums
	 * @param kwRmcRcNums
	 * @param woSchRcFreqs
	 * @param woRmcRcFreqs
	 * @param woSchRcNums
	 * @param woRmcRcNums
	 * @param distNamesList
	 */
	private void getRecyclerWasteFromRedis(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			String fieldPrefix, int k, int[][] kwSchRcFreqs, int[][] kwRmcRcFreqs, float[][] kwSchRcNums,
			float[][] kwRmcRcNums, int[][] woSchRcFreqs, int[][] woRmcRcFreqs, float[][] woSchRcNums,
			float[][] woRmcRcNums, List<Object> distNamesList) {
		int i;
		Map<String, String> schoolwastetotalMap;
		Map<String, String> supplierwastetotalMap;
		Map<String, String> schooloiltotalMap;
		Map<String, String> supplieroiltotalMap;
		String keyVal = "";
		//餐厨垃圾回收
		//学校回收桶数
		String key = dates[k] + "_schoolwastetotal";
		schoolwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		
		if(schoolwastetotalMap != null) {
			for(String curKey : schoolwastetotalMap.keySet()) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}else if(distNamesList!=null && distNamesList.size() >0) {
						if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
							continue;
						}
						if(!distNamesList.contains(curDistId)) {
							continue ;
						}
					}
					//区域回收次数
					fieldPrefix = curDistId + "_total";
					if (curKey.equalsIgnoreCase(fieldPrefix)) {
						keyVal = schoolwastetotalMap.get(curKey);
						if(keyVal != null) {
							kwSchRcFreqs[k][i] = Integer.parseInt(keyVal);
						}
					}
					// 区域回收垃圾桶数
					fieldPrefix = curDistId;
					if (curKey.equalsIgnoreCase(fieldPrefix)) {
						keyVal = schoolwastetotalMap.get(curKey);
						if(keyVal != null) {
							kwSchRcNums[k][i] = Float.parseFloat(keyVal);
						}
					}
				}
			}
		}
		//团餐公司回收桶数
		key = dates[k] + "_supplierwastetotal";
		supplierwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(supplierwastetotalMap != null) {
			for(String curKey : supplierwastetotalMap.keySet()) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}else if(distNamesList!=null && distNamesList.size() >0) {
						if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
							continue;
						}
						if(!distNamesList.contains(curDistId)) {
							continue ;
						}
					}
					//区域回收次数
					fieldPrefix = curDistId + "_total";
					if (curKey.equalsIgnoreCase(fieldPrefix)) {
						keyVal = supplierwastetotalMap.get(curKey);
						if(keyVal != null) {
							kwRmcRcFreqs[k][i] = Integer.parseInt(keyVal);
						}
					}
					// 区域回收垃圾桶数
					fieldPrefix = curDistId;
					if (curKey.equalsIgnoreCase(fieldPrefix)) {
						keyVal = supplierwastetotalMap.get(curKey);
						if(keyVal != null) {
							kwRmcRcNums[k][i] = Float.parseFloat(keyVal);
						}
					}
					
				}
			}
		}				
		//废弃油脂回收
		//学校回收桶数
		key = dates[k] + "_schooloiltotal";
		schooloiltotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(schooloiltotalMap != null) {
			for(String curKey : schooloiltotalMap.keySet()) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}else if(distNamesList!=null && distNamesList.size() >0) {
						if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
							continue;
						}
						if(!distNamesList.contains(curDistId)) {
							continue ;
						}
					}
					//区域回收次数
					fieldPrefix = curDistId + "_total";
					if (curKey.equalsIgnoreCase(fieldPrefix)) {
						keyVal = schooloiltotalMap.get(curKey);
						if(keyVal != null) {
							woSchRcFreqs[k][i] = Integer.parseInt(keyVal);
						}
					}
					//区域回收垃圾桶数
					fieldPrefix = curDistId;
					if (curKey.equalsIgnoreCase(fieldPrefix)) {
						keyVal = schooloiltotalMap.get(curKey);
						if(keyVal != null) {
							woSchRcNums[k][i] = Float.parseFloat(keyVal);
						}
					}
				}
			}
		}
		//团餐公司回收桶数
		key = dates[k] + "_supplieroiltotal";
		supplieroiltotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(supplieroiltotalMap != null) {
			for(String curKey : supplieroiltotalMap.keySet()) {
				for (i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}else if(distNamesList!=null && distNamesList.size() >0) {
						if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
							continue;
						}
						if(!distNamesList.contains(curDistId)) {
							continue ;
						}
					}
					//区域回收次数
					fieldPrefix = curDistId + "_total";
					if (curKey.equalsIgnoreCase(fieldPrefix)) {
						keyVal = supplieroiltotalMap.get(curKey);
						if(keyVal != null) {
							woRmcRcFreqs[k][i] = Integer.parseInt(keyVal);
						}
					}
					//区域回收垃圾桶数
					fieldPrefix = curDistId;
					if (curKey.equalsIgnoreCase(fieldPrefix)) {
						keyVal = supplieroiltotalMap.get(curKey);
						if(keyVal != null) {
							woRmcRcNums[k][i] = Float.parseFloat(keyVal);
						}
					}
				}
			}
		}
	}

	/**
	 * 证照预期处理
	 * @param dates
	 * @param keyVal
	 * @param k
	 * @param totalWarnNums
	 * @param noProcWarnNums
	 * @param elimWarnNums
	 * @param distIdToIdxMap
	 * @return
	 */
	private String setWarnDataByLocality(String[] dates, String keyVal, int k, int[][] totalWarnNums, int[][] noProcWarnNums,
			int[][] elimWarnNums, Map<String, Integer> distIdToIdxMap) {
		String key;
		int i;
		int j;
		Map<String, String> warnTotalMap;
		int curWarnNum;
		key = dates[k] + "_warn-total";
		warnTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(warnTotalMap != null) {
			for(String curKey : warnTotalMap.keySet()) {
				String[] curKeys = curKey.split("_");
				if(curKeys.length == 4) {
					i = AppModConfig.getVarValIndex(curKeys, "area");
					if(i != -1) {
						if(!curKeys[i].equalsIgnoreCase("null")) {
							if(distIdToIdxMap.containsKey(curKeys[i])) {
								int idx = distIdToIdxMap.get(curKeys[i]);
								keyVal = warnTotalMap.get(curKey);
								curWarnNum = 0;
								if(keyVal != null)
									curWarnNum = Integer.parseInt(keyVal);
								if(curWarnNum < 0)
									curWarnNum = 0;
								j = AppModConfig.getVarValIndex(curKeys, "status");
								if(j != -1) {
									if(curKeys[j].equalsIgnoreCase("1")) {         //未处理预警数
										noProcWarnNums[k][idx] += curWarnNum;
										totalWarnNums[k][idx] += curWarnNum;
									}
									else if(curKeys[j].equalsIgnoreCase("2")) {    //审核中预警数
										totalWarnNums[k][idx] += curWarnNum;
									}
									else if(curKeys[j].equalsIgnoreCase("3")) {    //已驳回预警数
										totalWarnNums[k][idx] += curWarnNum;
									}
									else if(curKeys[j].equalsIgnoreCase("4")) {    //已消除预警数
										elimWarnNums[k][idx] += curWarnNum;
										totalWarnNums[k][idx] += curWarnNum;
									}
								}
							}
						}
					}
				}
			}
		}
		return keyVal;
	}
	
	/**
	 * 从hive库中获取
	 * @param distId
	 * @param startDate
	 * @param endDate
	 * @param target
	 * @param totalWarnNums
	 * @param noProcWarnNums
	 * @param elimWarnNums
	 * @param distIdToIdxMap
	 * @param dbHiveWarnService
	 */
	private void setWarnDataByLocalityTwo(String departmentId,String startDate,String endDate,Integer target, int[][] totalWarnNums, int[][] noProcWarnNums,
			int[][] elimWarnNums, Map<String, Integer> distIdToIdxMap,DbHiveWarnService dbHiveWarnService) {
		
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
				null, null, -1, -1, null, null, departmentId, null, 0);
		
		if(warnCommonList != null && warnCommonList.size()>0) {
			for(WarnCommon warnCommon : warnCommonList) {
				if(warnCommon !=null) {
					if(warnCommon.getDistName() !=null) {
						if(distIdToIdxMap.containsKey(warnCommon.getDistName())) {
							int idx = distIdToIdxMap.get(warnCommon.getDistName());
							//未处理预警数
							noProcWarnNums[0][idx] += warnCommon.getNoProcWarnNum();
							totalWarnNums[0][idx] += warnCommon.getNoProcWarnNum();
							//审核中预警数
							totalWarnNums[0][idx] += warnCommon.getAuditWarnNum();
							//已驳回预警数
							totalWarnNums[0][idx] += warnCommon.getRejectWarnNum();
							//已消除预警数
							elimWarnNums[0][idx] += warnCommon.getElimWarnNum();
							totalWarnNums[0][idx] += warnCommon.getElimWarnNum();
						}
					}
				}
			}
		}
	}
	/**
	 * 从hive库中获取
	 * @param distId
	 * @param startDate
	 * @param endDate
	 * @param target
	 * @param totalWarnNums
	 * @param noProcWarnNums
	 * @param elimWarnNums
	 * @param distIdToIdxMap
	 * @param dbHiveWarnService
	 */
	private void setWarnDataByDepartment(String departmentId,String startDate,String endDate,Integer target, int[][] totalWarnNums, int[][] noProcWarnNums,
			int[][] elimWarnNums, Map<String, Integer> distIdToIdxMap,DbHiveWarnService dbHiveWarnService) {
		
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
				null, null, -1, -1, null, null, departmentId, null, 4);
		
		if(warnCommonList != null && warnCommonList.size()>0) {
			for(WarnCommon warnCommon : warnCommonList) {
				if(warnCommon !=null) {
					if(warnCommon.getDepartmentId() !=null) {
						if(distIdToIdxMap.containsKey(warnCommon.getDepartmentId())) {
							int idx = distIdToIdxMap.get(warnCommon.getDepartmentId());
							//未处理预警数
							noProcWarnNums[0][idx] += warnCommon.getNoProcWarnNum();
							totalWarnNums[0][idx] += warnCommon.getNoProcWarnNum();
							//审核中预警数
							totalWarnNums[0][idx] += warnCommon.getAuditWarnNum();
							//已驳回预警数
							totalWarnNums[0][idx] += warnCommon.getRejectWarnNum();
							//已消除预警数
							elimWarnNums[0][idx] += warnCommon.getElimWarnNum();
							totalWarnNums[0][idx] += warnCommon.getElimWarnNum();
						}
					}
				}
			}
		}
	}
	
	// 汇总数据详情列表函数
	private SumDataDetsDTO sumDataDets(String departmentId,String distIdorSCName, String[] dates,
			List<TEduDistrictDo> tedList, 
			int schSelMode, int subLevel, int compDep, String subDistName,
			String subLevels,String compDeps,String distNames,String departmentIds,
			Integer target,
			DbHiveWarnService dbHiveWarnService,DbHiveRecyclerWasteService dbHiveRecyclerWasteService,
			DbHiveDishService dbHiveDishService,DbHiveGsService dbHiveGsService,Db1Service db1Service) {
		SumDataDetsDTO sddDto = new SumDataDetsDTO();
		
		//筛选学校模式
		if(schSelMode == 0) {    //按主管部门
			sddDto = sumDataDetsByCompDep(departmentId,distIdorSCName, dates,target, tedList, subLevel, compDep, subDistName,
					subLevels,compDeps,dbHiveWarnService,dbHiveRecyclerWasteService,dbHiveDishService,dbHiveGsService);
		}
		else if(schSelMode == 1) {  //按所在地
			sddDto = sumDataDetsByLocality(departmentId,distIdorSCName, dates,target, tedList,distNames,dbHiveWarnService,
					dbHiveRecyclerWasteService,dbHiveDishService,dbHiveGsService);
		}else if(schSelMode == 2) {  //按管理部门
			
			Map<Integer, String> departmentMap = new LinkedHashMap<Integer,String>();
			List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
			List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),departmentIdsList, -1, -1);	
			
			 int [] schOwnTypes = new int [deparmentList.size()];
			if(CommonUtil.isNotEmpty(departmentId)) {
			     schOwnTypes = new int [1];
			}
			int index = 0;
			if(deparmentList != null) {
				for(DepartmentObj department : deparmentList) {
					if(CommonUtil.isNotEmpty(departmentId) && !departmentId.equals(department.getDepartmentId())) {
						continue;
					}
					departmentMap.put(Integer.valueOf(department.getDepartmentId()), department.getDepartmentName());
					schOwnTypes[index++] = Integer.valueOf(department.getDepartmentId());
				}
			}
			sddDto = sumDataDetsByDepartment(departmentId, dates, target, departmentIds, departmentMap, 
					schOwnTypes, dbHiveWarnService, dbHiveRecyclerWasteService,
					dbHiveDishService, dbHiveGsService, db1Service);
		}

		return sddDto;
	}
	
	// 汇总数据详情列表模型函数
	public SumDataDetsDTO appModFunc(String token, String startDate, String endDate, String schSelMode, 
			String subLevel, String compDep, String subDistName, String distName,String departmentId, String prefCity, 
			String province, 
			String subLevels,String compDeps,String distNames,String departmentIds,
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service,DbHiveWarnService dbHiveWarnService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService,
			DbHiveDishService dbHiveDishService,DbHiveGsService dbHiveGsService) {
		SumDataDetsDTO sddDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 日期
			String[] dates = null;
			if (startDate == null || endDate == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			} else { // 按照开始日期和结束日期获取数据
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
			if(province == null)
				province = "上海市";
			//学校筛选方式，0:按主管部门，1:按所在地
			int curSchSelMode = 1;
			if(schSelMode != null)
				curSchSelMode = Integer.parseInt(schSelMode);
			//所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
			int curSubLevel = -1;
			if(subLevel != null)
				curSubLevel = Integer.parseInt(subLevel);
			//主管部门，按主管部门有效
			int curCompDep = -1;
			if(compDep != null)
				curCompDep = Integer.parseInt(compDep);			
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			// 餐厨垃圾学校回收列表函数
			Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
			
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) { // 按区域，省或直辖市处理
				List<TEduDistrictDo> tedList = db1Service.getListByDs1IdName();
				if(tedList != null) {
					// 查找是否存在该区域和省市
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						if (curTdd.getId().compareTo(distName) == 0) {
							bfind = true;
							distIdorSCName = curTdd.getId();
							break;
						}
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					//获取用户数据权限信息
				  	/*UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);
				  	if(curSubLevel == -1)
				  		curSubLevel = udpiDto.getSubLevelId();
				  	if(curCompDep == -1)
				  		curCompDep = udpiDto.getCompDepId();*/
					// 汇总数据详情列表函数
					sddDto = sumDataDets(departmentId,distIdorSCName, dates, tedList, curSchSelMode, curSubLevel,
							curCompDep, subDistName,
							subLevels,compDeps,distNames,departmentIds,target,dbHiveWarnService,dbHiveRecyclerWasteService,
							dbHiveDishService,dbHiveGsService,db1Service);
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				List<TEduDistrictDo> tedList = null;
				if (province.compareTo("上海市") == 0) {
					tedList = db1Service.getListByDs1IdName();
					if(tedList != null)
						bfind = true;
					distIdorSCName = null;
				}
				if (bfind) {
					if(departmentId == null)
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					//获取用户数据权限信息
				  	/*UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);
				  	if(curSubLevel == -1)
				  		curSubLevel = udpiDto.getSubLevelId();
				  	if(curCompDep == -1)
				  		curCompDep = udpiDto.getCompDepId();*/
					// 汇总数据详情列表函数
					sddDto = sumDataDets(departmentId,distIdorSCName, dates, tedList, curSchSelMode, curSubLevel, 
							curCompDep, subDistName,
							subLevels,compDeps,distNames,departmentIds,target,dbHiveWarnService,dbHiveRecyclerWasteService,
							dbHiveDishService,dbHiveGsService,db1Service);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			sddDto = SimuDataFunc();
		}		

		return sddDto;
	}
}
