package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.DishSumInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.DishSumInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 3.2.9.	排菜汇总信息应用模型
 * @author fengyang_xie
 *
 */
public class DishSumInfoAppMod {
	private static final Logger logger = LogManager.getLogger(DishSumInfoAppMod.class.getName());
	
	/**
	 * Redis服务
	 */
	@Autowired
	RedisService redisService = new RedisService();
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 1;
	/**
	 * 是否为真实数据标识
	 */
	private static boolean isRealData = true;
	
	/**
	 * 数组数据初始化
	 */
	String tempData ="{\r\n" + 
			"   \"time\": \"2016-07-14 09:51:35\",\r\n" + 
			"   \"dishSumInfo\": \r\n" + 
			"{\r\n" + 
			"  \"totalSchNum\":3567,\r\n" + 
			"\"mealSchNum\":567,\r\n" + 
			"\"dishSchNum\":500,\r\n" + 
			"\"noDishSchNum\":67,\r\n" + 
			"\"dishRate\":84.11\r\n" + 
			" },\r\n" + 
			"   \"msgId\":1\r\n" + 
			"}\r\n" + 
			"";
	
	/**
	 * 汇总数据
	 * @return
	 */
	private DishSumInfoDTO dishSumInfoFunc(String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService ) {
		
		DishSumInfoDTO dishSumInfoDTO = new DishSumInfoDTO();
		
		DishSumInfo dishSumInfo = new DishSumInfo();
		dishSumInfoDTO.setDishSumInfo(dishSumInfo);
		
		JSONObject jsStr = JSONObject.parseObject(tempData); 
		dishSumInfoDTO = (DishSumInfoDTO) JSONObject.toJavaObject(jsStr,DishSumInfoDTO.class);
		
		
		
		
		//时戳
		dishSumInfoDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		dishSumInfoDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return dishSumInfoDTO;
	}
	
	/**
	 * 汇总数据
	 * @return
	 */
	private DishSumInfoDTO dishSumInfoFuncOne(String departmentId,String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService,DbHiveDishService dbHiveDishService ) {
		
		DishSumInfoDTO dishSumInfoDTO = new DishSumInfoDTO();
		DishSumInfo dishSumInfo = new DishSumInfo();
		
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		if(days >= 2) {
			//学校数量、供餐学校数量、已排菜学校数量、未排菜学校数量、排菜率
			getDishInfoFromHive(dishSumInfo,departmentId, distId, dates, tedList, dbHiveDishService);
		}else {
			/**
			 * 监管学校数量 totalSchNum	必选	INT	学校数量
			 */
			Integer supSchNum = getSupSchNum(departmentId,distId);
			dishSumInfo.setTotalSchNum(supSchNum);
			/**
			 * 排菜汇总
			 * mealSchNum	必选	INT	供餐学校数量
			 * dishSchNum	必选	INT	已排菜学校数量
			 * noDishSchNum	必选	INT	未排菜学校数量
			 * dishRate	必选	FLOAT	排菜率，保留小数点有效数字两位
			 */
			getDishInfo(dishSumInfo,departmentId,distId, dates, tedList);
		
		}
		dishSumInfoDTO.setDishSumInfo(dishSumInfo);
		//时戳
		dishSumInfoDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		dishSumInfoDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return dishSumInfoDTO;
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
	 * 获取排菜数据
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoFromHive(DishSumInfo dishSumInfo,String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			DbHiveDishService dbHiveDishService) {
		// 当天排菜学校总数
		int totalSchNum = 0;
		//应排菜
		int totalDistSchNum = 0;
		//已排菜
		int distDishSchNum = 0;
		//未排菜
		int distNoDishSchNum = 0;
		float distDishRate = 0;
		
		
		//1 表示规范录入
		int standardNum = 0;
		//2 表示补录
		int supplementNum = 0;
		//3 表示逾期补录
		int beOverdueNum = 0;
		//4 表示无数据
		int noDataNum = 0;
		
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
					//不供餐排除
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
		
		/**
		 * 学校数量
		 */
		dishSumInfo.setTotalSchNum(totalSchNum);
			
		/**
		 * 供餐学校数量
		 */
		dishSumInfo.setMealSchNum(totalDistSchNum);
		
		/**
		 * 已排菜学校数量
		 */
		dishSumInfo.setDishSchNum(distDishSchNum);
		
		/**
		 * 未排菜学校数量
		 */
		dishSumInfo.setNoDishSchNum(distNoDishSchNum);
		
		/**
		 * 排菜率，保留小数点有效数字两位。
		 */
		dishSumInfo.setDishRate(distDishRate);
	}
	
	/**
	 * 获取排菜数据
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfo(DishSumInfo dishSumInfo,String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList) {
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
			key = dates[k]   + DataKeyConfig.platoonfeedTotal;
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
									if(keyVal != null) {
										mealSchNum = Integer.parseInt(keyVal);
										dishSchNum = mealSchNum;
									}
								}
								else if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("未排菜")) {
									if(keyVal != null) {
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
			
		/**
		 * 供餐学校数量
		 */
		dishSumInfo.setMealSchNum(totalDistSchNum);
		
		/**
		 * 已排菜学校数量
		 */
		dishSumInfo.setDishSchNum(distDishSchNum);
		
		/**
		 * 未排菜学校数量
		 */
		dishSumInfo.setNoDishSchNum(distNoDishSchNum);
		
		/**
		 * 排菜率，保留小数点有效数字两位。
		 */
		dishSumInfo.setDishRate(distDishRate);
	}
	
	/**
	 * 投诉举报详情模型函数
	 * @param crId
	 * @param distName
	 * @param prefCity
	 * @param province
	 * @param startDate
	 * @param endDate
	 * @param db1Service
	 * @return
	 */
	public DishSumInfoDTO appModFunc(String token, String distName, String prefCity, String province,String startDate, String endDate, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveDishService dbHiveDishService ) {
		
		DishSumInfoDTO dishSumInfoDTO = null;
		//真实数据
		if(isRealData) {       
			// 日期
			String[] dates = null;
			// 按照当天日期获取数据
			if (startDate == null || endDate == null) { 
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
			if(province == null) {
				province = "上海市";
			}
			// 参数查找标识
			boolean bfind = false;
			String distId = null;
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
					// 餐厨垃圾学校回收列表函数
					if(methodIndex==0) {
						dishSumInfoDTO = dishSumInfoFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						dishSumInfoDTO = dishSumInfoFuncOne(departmentId,distId, dates, tedList, db1Service, saasService,dbHiveDishService);
					}
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
					if(methodIndex==0) {
						dishSumInfoDTO = dishSumInfoFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						dishSumInfoDTO = dishSumInfoFuncOne(departmentId,distId, dates, tedList, db1Service, saasService,dbHiveDishService);
					}
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
			dishSumInfoDTO = new DishSumInfoDTO();
		}		

		
		return dishSumInfoDTO;
	}
}
