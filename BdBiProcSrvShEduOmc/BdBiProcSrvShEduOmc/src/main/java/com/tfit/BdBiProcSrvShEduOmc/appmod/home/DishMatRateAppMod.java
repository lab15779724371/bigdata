package com.tfit.BdBiProcSrvShEduOmc.appmod.home;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.DishMatRate;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.DishMatRateDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchMatCommon;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//排菜率与用料确认率趋势应用模型
public class DishMatRateAppMod {
	private static final Logger logger = LogManager.getLogger(DishMatRateAppMod.class.getName());
	
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
	String[] timeCoord_Array = {"9/21", "9/22", "9/23", "9/24", "9/25", "9/26", "9/27"};
	float[] dishRate_Array = {(float) 0.51, (float) 0.51, (float) 0.55, (float) 85.96, (float) 87.08, (float) 87.08, (float) 85.05};
	float[] matConRate_Array = {(float) 66.67, (float) 75.00, (float) 71.00, (float) 98.67, (float) 100.00, (float) 100.00, (float) 91.26};
	
	//模拟数据函数
	private DishMatRateDTO SimuDataFunc() {
		DishMatRateDTO dmrDto = new DishMatRateDTO();
		//时戳
		dmrDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//当前排菜率
		float curDishRate = (float) 81.09, curMatConRate = (float) 71.22;
		BigDecimal bd = new BigDecimal(curDishRate);
		curDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		dmrDto.setCurDishRate(curDishRate);
		//当前用料确认率
		bd = new BigDecimal(curMatConRate);
		curMatConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		dmrDto.setCurMatConRate(curMatConRate);
		//排菜率与用料确认率趋势模拟数据
		List<DishMatRate> dishMatRate = new ArrayList<>();
		DateTime dt = new DateTime();
		//赋值
		for (int i = 0; i < timeCoord_Array.length; i++) {
			DishMatRate dmr = new DishMatRate();
			DateTime curDt = dt.minusDays(timeCoord_Array.length-i-1);
			timeCoord_Array[i] = BCDTimeUtil.convertMonthDayForm(curDt);
			dmr.setTimeCoord(timeCoord_Array[i]);
			bd = new BigDecimal(dishRate_Array[i]);
			dishRate_Array[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			dmr.setDishRate(dishRate_Array[i]);
			bd = new BigDecimal(matConRate_Array[i]);
			matConRate_Array[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			dmr.setMatConRate(matConRate_Array[i]);
			dishMatRate.add(dmr);
		}
		//设置数据
		dmrDto.setDishMatRate(dishMatRate);
		//消息ID
		dmrDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return dmrDto;
	}
	
	// 排菜率与用料确认率趋势函数
	private DishMatRateDTO dishMatRate(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList) {
		DishMatRateDTO dmrDto = new DishMatRateDTO();
		List<DishMatRate> dishMatRate = new ArrayList<>();
		DishMatRate dmr = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> platoonFeedTotalMap = null, useMaterialPlanMap = null;
		int distCount = tddList.size(), dateCount = dates.length;
		int[][] totalMealSchNums = new int[dateCount][distCount], distDishSchNums = new int[dateCount][distCount];
		int[][] totalMatPlanNums = new int[dateCount][distCount], matPlanConfirmNums = new int[dateCount][distCount];
		float[] dateDishRates = new float[dateCount], dateMatRates = new float[dateCount];
		// 时间段内各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k] + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(platoonFeedTotalMap == null)
				continue;
			for(String curKey : platoonFeedTotalMap.keySet()) {
				for (int i = 0; i < tddList.size(); i++) {
					TEduDistrictDo curTdd = tddList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equals(distIdorSCName))
							continue ;
					}
					// 区域排菜学校供餐数
					fieldPrefix = curDistId + "_";
					int mealSchNum = 0, dishSchNum = 0;
					if (curKey.indexOf(fieldPrefix) == 0) {
						String[] curKeys = curKey.split("_");
						if(curKeys.length >= 3)
						{
							if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("已排菜")) {
								keyVal = platoonFeedTotalMap.get(curKey);
								if(keyVal != null) {
									mealSchNum = Integer.parseInt(keyVal);
									dishSchNum = mealSchNum;
								}
							}
							else if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("未排菜")) {
								keyVal = platoonFeedTotalMap.get(curKey);
								if(keyVal != null) {
									mealSchNum = Integer.parseInt(keyVal);
								}
							}
						}
					}
					totalMealSchNums[k][i] += mealSchNum;
					distDishSchNums[k][i] += dishSchNum;
				}
			}
		}
		//时间段内用料计划数量
		for (int k = 0; k < dates.length; k++) {
			//用料计划数量
			key = dates[k] + DataKeyConfig.useMaterialPlanTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentUseMaterialPlanTotal+departmentId;
			}
			useMaterialPlanMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (useMaterialPlanMap != null) {
				for(int i = 0; i < distCount; i++) {
					TEduDistrictDo curTdd = tddList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equalsIgnoreCase(distIdorSCName))
							continue ;
					}
					fieldPrefix = "area" + "_" + curDistId;
					// 用料计划总数
					int totalMatPlanNum = 0;
					for(int j = 0; j < 3; j++) {
						field = fieldPrefix + "_" + "status" + "_" + j;
						keyVal = useMaterialPlanMap.get(field);
						if (keyVal != null)
							totalMatPlanNum += Integer.parseInt(keyVal);
					}
					totalMatPlanNums[k][i] = totalMatPlanNum;
					// 用料计划确认数
					field = fieldPrefix + "_" + "status" + "_" + 2;
					keyVal = useMaterialPlanMap.get(field);
					if (keyVal != null)
						matPlanConfirmNums[k][i] = Integer.parseInt(keyVal);
				}
			}
		}
		//计算日期排菜率和用料计划确认率
		for (int k = 0; k < dates.length; k++) {
			int totalMealSchNum = 0, dateDishSchNum = 0, totalMatPlanNum = 0, matPlanConfirmNum = 0;
			dateDishRates[k] = 0;
			dateMatRates[k] = 0;
			for(int i = 0; i < distCount; i++) {
				totalMealSchNum += totalMealSchNums[k][i];
				dateDishSchNum += distDishSchNums[k][i];
				totalMatPlanNum += totalMatPlanNums[k][i];
				matPlanConfirmNum += matPlanConfirmNums[k][i];
			}
			if(totalMealSchNum > 0 && totalMealSchNum > minMealSchNumThre) {    //排菜率
				dateDishRates[k] = 100 * ((float) dateDishSchNum / (float) totalMealSchNum);
				BigDecimal bd = new BigDecimal(dateDishRates[k]);
				dateDishRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (dateDishRates[k] > 100) {
					dateDishRates[k] = 100;
				}
			}
			if(totalMatPlanNum > 0) {    //用料计划确认率
				dateMatRates[k] = 100 * ((float) matPlanConfirmNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(dateMatRates[k]);
				dateMatRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (dateMatRates[k] > 100) {
					dateMatRates[k] = 100;
				}
			}
		}
		//设置排菜和用料确认数据
		for(int k = 0; k < dates.length; k++) {
			dmr = new DishMatRate();
			DateTime curDt = BCDTimeUtil.convertDateStrToDate(dates[k]);
			String timeCoord = curDt.toString("M/d");
			dmr.setDishRate(dateDishRates[k]);
			dmr.setMatConRate(dateMatRates[k]);
			dmr.setTimeCoord(timeCoord);
			dishMatRate.add(dmr);
			//今日排菜率和确认率
			if(k == dateCount-1) {
				dmrDto.setCurDishRate(dateDishRates[k]);
				dmrDto.setCurMatConRate(dateMatRates[k]);
			}
			logger.info("日期：" + dates[k] + "，排菜率：" + dateDishRates[k]);
		}
		// 设置返回数据
		dmrDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//设置数据
		dmrDto.setDishMatRate(dishMatRate);
		//消息ID
		dmrDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return dmrDto;
	}
	
	// 排菜率与用料确认率趋势函数
	private DishMatRateDTO dishMatRateFromHive(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList,
			DbHiveDishService dbHiveDishService,DbHiveMatService dbHiveMatService) {
		DishMatRateDTO dmrDto = new DishMatRateDTO();
		List<DishMatRate> dishMatRate = new ArrayList<>();
		DishMatRate dmr = null;
		// 当天排菜学校总数
		int dateCount = dates.length;
		float[] dateDishRates = new float[dateCount], dateMatRates = new float[dateCount];
		// 时间段内各区排菜学校数量
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
		//学校总数
		Map<String,Integer> totalSchNumMap = new HashMap<>();
		//应排菜学校
		Map<String,Integer> totalMealSchNumMap = new HashMap<>();
		//已排菜学校
		Map<String,Integer> distDishSchNumMap = new HashMap<>();
		//未排菜学校
		Map<String,Integer> distNoDishSchNumMap = new HashMap<>();
		
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, distIdorSCName, null, 
				-1, -1, null, null,departmentId,null, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				String dataKey =schDishCommon.getDishDate();
				
				if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
					if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNumMap.put(dataKey, 
									(totalSchNumMap.get(dataKey)==null?0:totalSchNumMap.get(dataKey)) 
									+ schDishCommon.getTotal());
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
				}else{
				}
			}
		}
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		//用料计划总数
		Map<String,Integer> totalMatPlanNumMap = new HashMap<>();
		//已确认用料计划数
		Map<String,Integer> conMatPlanNumMap = new HashMap<>();
		//未确认用料计划数
		Map<String,Integer> noConMatPlanNumMap = new HashMap<>();
		//未确认用料计划学校数
		Map<String,Integer> conMatSchNumMap = new HashMap<>();
		//未确认用料计划学校数
		Map<String,Integer> noConMatSchNumMap = new HashMap<>();
		
		List<SchMatCommon> matList = new ArrayList<>();
		matList = dbHiveMatService.getMatList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, distIdorSCName, null, 
				-1, -1, null, null,departmentId,null,0);
		if(matList !=null && matList.size() > 0) {
			for(SchMatCommon schDishCommon: matList) {
				String dataKey =schDishCommon.getMatDate();
				
				//区域为空，代表全市数据，此处去除
				if(CommonUtil.isEmpty(schDishCommon.getDistId())) {
					continue;
				}
				
				if(schDishCommon.getStatus() ==null) {
					totalMatPlanNumMap.put(dataKey, 
							(totalMatPlanNumMap.get(dataKey)==null?0:totalMatPlanNumMap.get(dataKey)) 
							+ schDishCommon.getTotal());
				}else if (schDishCommon.getStatus() == 0 || schDishCommon.getStatus() == 1) {
					//信息不完整和待确认都属于未确认状态
					
					noConMatSchNumMap.put(dataKey, 
							(noConMatSchNumMap.get(dataKey)==null?0:noConMatSchNumMap.get(dataKey)) 
							+ schDishCommon.getSchoolTotal());
					
					noConMatPlanNumMap.put(dataKey, 
							(noConMatPlanNumMap.get(dataKey)==null?0:noConMatPlanNumMap.get(dataKey)) 
							+ schDishCommon.getTotal());
					
				}else if (schDishCommon.getStatus() == 2) {
					
					conMatSchNumMap.put(dataKey, 
							(conMatSchNumMap.get(dataKey)==null?0:conMatSchNumMap.get(dataKey)) 
							+ schDishCommon.getSchoolTotal());
					
					conMatPlanNumMap.put(dataKey, 
							(conMatPlanNumMap.get(dataKey)==null?0:conMatPlanNumMap.get(dataKey)) 
							+ schDishCommon.getTotal());
				}
			}
		}
		
		//计算日期排菜率和用料计划确认率
		for (int k = 0; k < dates.length; k++) {
			int totalMealSchNum = 0, dateDishSchNum = 0, totalMatPlanNum = 0, matPlanConfirmNum = 0;
			dateDishRates[k] = 0;
			dateMatRates[k] = 0;
			
			String dataKey = dates[k].replaceAll("-", "/");
			totalMealSchNum = totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey);
			dateDishSchNum = distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey);
			
			totalMatPlanNum = totalMatPlanNumMap.get(dataKey)==null?0:totalMatPlanNumMap.get(dataKey);
			matPlanConfirmNum = conMatPlanNumMap.get(dataKey)==null?0:conMatPlanNumMap.get(dataKey);
			 
			
			if(totalMealSchNum > 0 && totalMealSchNum > minMealSchNumThre) {    //排菜率
				dateDishRates[k] = 100 * ((float) dateDishSchNum / (float) totalMealSchNum);
				BigDecimal bd = new BigDecimal(dateDishRates[k]);
				dateDishRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (dateDishRates[k] > 100) {
					dateDishRates[k] = 100;
				}
			}
			if(totalMatPlanNum > 0) {    //用料计划确认率
				dateMatRates[k] = 100 * ((float) matPlanConfirmNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(dateMatRates[k]);
				dateMatRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (dateMatRates[k] > 100) {
					dateMatRates[k] = 100;
				}
			}
		}
		//设置排菜和用料确认数据
		for(int k = 0; k < dates.length; k++) {
			dmr = new DishMatRate();
			DateTime curDt = BCDTimeUtil.convertDateStrToDate(dates[k]);
			String timeCoord = curDt.toString("M/d");
			dmr.setDishRate(dateDishRates[k]);
			dmr.setMatConRate(dateMatRates[k]);
			dmr.setTimeCoord(timeCoord);
			dishMatRate.add(dmr);
			//今日排菜率和确认率
			if(k == dateCount-1) {
				dmrDto.setCurDishRate(dateDishRates[k]);
				dmrDto.setCurMatConRate(dateMatRates[k]);
			}
			logger.info("日期：" + dates[k] + "，排菜率：" + dateDishRates[k]);
		}
		// 设置返回数据
		dmrDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//设置数据
		dmrDto.setDishMatRate(dishMatRate);
		//消息ID
		dmrDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return dmrDto;
	}
	
	// 排菜率与用料确认率趋势模型函数
	public DishMatRateDTO appModFunc(String token, String distName, String prefCity, String province,
			int timeType, String date, 
			Db1Service db1Service, Db2Service db2Service, DbHiveDishService dbHiveDishService,DbHiveMatService dbHiveMatService) {
		DishMatRateDTO dmrDto = null;
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
					// 排菜率与用料确认率趋势函数
					if(methodIndex == 1) {
						dmrDto = dishMatRate(departmentId,distIdorSCName, dates, tddList);
					}else if (methodIndex == 2) {
						dmrDto = dishMatRateFromHive(departmentId,distIdorSCName, dates, tddList, dbHiveDishService,dbHiveMatService);
					}else if (methodIndex == 3) {
						//如果包含当天数据，则从redis中获取，否则从hive库中获取
						SimpleDateFormat fromat = new SimpleDateFormat("yyyy-MM-dd");
						if(dates[dates.length - 1].equals(fromat.format(new Date()))) {
							dmrDto = dishMatRate(departmentId,distIdorSCName, dates, tddList);
						}else {
							dmrDto = dishMatRateFromHive(departmentId,distIdorSCName, dates, tddList, dbHiveDishService,dbHiveMatService);
						}
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
					// 排菜率与用料确认率趋势函数
					if(methodIndex == 1) {
						dmrDto = dishMatRate(departmentId,distIdorSCName, dates, tddList);
					}else if (methodIndex == 2) {
						dmrDto = dishMatRateFromHive(departmentId,distIdorSCName, dates, tddList, dbHiveDishService,dbHiveMatService);
					}else if (methodIndex == 3) {
						//如果包含当天数据，则从redis中获取，否则从hive库中获取
						SimpleDateFormat fromat = new SimpleDateFormat("yyyy-MM-dd");
						if(dates[dates.length - 1].equals(fromat.format(new Date()))) {
							dmrDto = dishMatRate(departmentId,distIdorSCName, dates, tddList);
						}else {
							dmrDto = dishMatRateFromHive(departmentId,distIdorSCName, dates, tddList, dbHiveDishService,dbHiveMatService);
						}
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
			dmrDto = SimuDataFunc();
		}		

		return dmrDto;
	}
}
