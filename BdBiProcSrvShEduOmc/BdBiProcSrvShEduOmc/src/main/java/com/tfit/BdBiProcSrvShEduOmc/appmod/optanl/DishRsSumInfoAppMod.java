package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.DishRsSumInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.DishRsSumInfoDTO;

/**
 * 3.2.43.	菜品留样汇总信息应用模型
 * @author fengyang_xie
 *
 */
public class DishRsSumInfoAppMod {
	private static final Logger logger = LogManager.getLogger(DishRsSumInfoAppMod.class.getName());
	
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
			"   \"dishRsSumInfo\": \r\n" + 
			"{\r\n" + 
			"  \"dishSchNum\":500,\r\n" + 
			"\"rsSchNum\":400,\r\n" + 
			"\"noRsSchNum\":100,\r\n" + 
			"\"totalDishNum\":3567,\r\n" + 
			"\"rsDishNum\":3000,\r\n" + 
			"\"noRsDishNum\":567,\r\n" + 
			"\"rsRate\":84.11\r\n" + 
			"},\r\n" + 
			"   \"msgId\":1\r\n" + 
			"}\r\n" + 
			"";
	
	/**
	 * 汇总数据
	 * @return
	 */
	private DishRsSumInfoDTO dishRsSumInfoFuncOne(String departmentId,String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService ) {
		
		DishRsSumInfoDTO dishRsSumInfoDTO = new DishRsSumInfoDTO();
		
		DishRsSumInfo dishRsSumInfo = new DishRsSumInfo();
		//已排菜学校
		Integer  distDishSchNum =MatSumInfoAppMod.getDishInfo(departmentId,distId, dates, tedList,redisService);
		dishRsSumInfo.setDishSchNum(distDishSchNum);
		

		//		totalDishNum	必选	INT	菜品总数
		//		rsDishNum	必选	INT	已留样菜品数
		//		noRsDishNum	必选	INT	未留样菜品数
		//		rsRate	必选	FLOAT	留样率，保留小数点有效数字两位
		getRsInfoRsRate(departmentId,distId, dates, tedList, dishRsSumInfo);
		
		////2019.03.28 注释原因：建模规则修改由detail改为total中获取
		/**
		 * rsSchNum	必选	INT	已留样学校
		 * noRsSchNum	必选	INT	未留样学校
		 */
		 //getRsInfoNoRsSchNum(dishRsSumInfo,distId, dates, db1Service);

		
		dishRsSumInfoDTO.setDishRsSumInfo(dishRsSumInfo);
		//时戳
		dishRsSumInfoDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		dishRsSumInfoDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return dishRsSumInfoDTO;
	}
	
	/**
	 * 获取留样汇总：留样率、未留样菜品
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param rsInfo
	 */
	private void getRsInfoRsRate(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList, DishRsSumInfo dishRsSumInfo) {
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
		float distRsRate = 0;
		float schRsRate = 0;
		
		
		int[] distRsSchNums = new int[dateCount];
		int[] distNoRsSchNums = new int[dateCount];
		
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
		int totalDistRsSchNum = 0;
		int totalDistNoRsSchNum = 0;
		for (int k = 0; k < dates.length; k++) {
			totalDishNum += totalDishNums[k];
			distRsDishNum += distRsDishNums[k];
			distNoRsDishNum += distNoRsDishNums[k];
			totalDistRsSchNum += distRsSchNums[k];
			totalDistNoRsSchNum += distNoRsSchNums[k];
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
		
		//学校留样率
		schRsRate = 0;
		int shouldRsSchNum = totalDistRsSchNum + totalDistNoRsSchNum;
		if(shouldRsSchNum > 0) {
			schRsRate = 100 * ((float) totalDistRsSchNum / (float) shouldRsSchNum);
			BigDecimal bd = new BigDecimal(schRsRate);
			schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (schRsRate > 100) {
				schRsRate = 100;
			}
		}
	   
		/**
		 * 菜品总数
		 */
	   dishRsSumInfo.setTotalDishNum(totalDishNum);
		
		/**
		 * 已留样菜品数
		 */
	   dishRsSumInfo.setRsDishNum(distRsDishNum);
		
		/**
		 * 未留样菜品数
		 */
	   dishRsSumInfo.setNoRsDishNum(distNoRsDishNum);
		
		/**
		 * 留样率，保留小数点有效数字两位
		 */
	   dishRsSumInfo.setRsRate(distRsRate);
	   
		/**
		 * 应留样学校
		 */
	   dishRsSumInfo.setShouldRsSchNum(shouldRsSchNum);
	   
		/**
		 * 已留样学校
		 */
		dishRsSumInfo.setRsSchNum(totalDistRsSchNum);
			
		/**
		 * 未留样学校
		 */
		dishRsSumInfo.setNoRsSchNum(totalDistNoRsSchNum);
		 
		/**
		 * 学校留样率，保留小数点有效数字两位
		 */
		dishRsSumInfo.setSchRsRate(schRsRate);
	}
	
	
	/**
	 * 获取留样汇总：未留样学校个数、已留样学校个数
	 * @param distId
	 * @param dates
	 * @param db1Service
	 * @return
	 */
	private void getRsInfoNoRsSchNum(DishRsSumInfo dishRsSumInfo,String distId, String[] dates, Db1Service db1Service) {
		
		//未留样学校
		Set<String> noRsSchNumSet = new HashSet<String>();
		//已留样学校
		Set<String> rsSchNumSet = new HashSet<String>();
		
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
						
						if("未留样".equals(keyVals[14])) {
							noRsSchNumSet.add(keyVals[5]);
						}else if("已留样".equals(keyVals[14])){
							rsSchNumSet.add(keyVals[5]);
						}
						
					}
					else {
						logger.info("菜品留样："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
			/**
			 * 已留样学校
			 */
		 dishRsSumInfo.setRsSchNum(rsSchNumSet.size());
			
			/**
			 * 未留样学校
			 */
		 dishRsSumInfo.setNoRsSchNum(noRsSchNumSet.size());
	}
	
	/**
	 * 汇总数据
	 * @return
	 */
	private DishRsSumInfoDTO dishRsSumInfoFunc(String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService ) {
		
		DishRsSumInfoDTO dishRsSumInfoDTO = new DishRsSumInfoDTO();
		
		DishRsSumInfo dishRsSumInfo = new DishRsSumInfo();
		dishRsSumInfoDTO.setDishRsSumInfo(dishRsSumInfo);
		
		JSONObject jsStr = JSONObject.parseObject(tempData); 
		dishRsSumInfoDTO = (DishRsSumInfoDTO) JSONObject.toJavaObject(jsStr,DishRsSumInfoDTO.class);
		
		
		
		
		//时戳
		dishRsSumInfoDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		dishRsSumInfoDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return dishRsSumInfoDTO;
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
	public DishRsSumInfoDTO appModFunc(String token, String distName, String prefCity, String province,String startDate, String endDate, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService ) {
		
		DishRsSumInfoDTO dishRsSumInfoDTO = null;
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
						// 餐厨垃圾学校回收列表函数
						dishRsSumInfoDTO = dishRsSumInfoFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						// 餐厨垃圾学校回收列表函数
						dishRsSumInfoDTO = dishRsSumInfoFuncOne(departmentId,distId, dates, tedList, db1Service, saasService);
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
						// 餐厨垃圾学校回收列表函数
						dishRsSumInfoDTO = dishRsSumInfoFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						// 餐厨垃圾学校回收列表函数
						dishRsSumInfoDTO = dishRsSumInfoFuncOne(departmentId,distId, dates, tedList, db1Service, saasService);
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
			dishRsSumInfoDTO = new DishRsSumInfoDTO();
		}		

		
		return dishRsSumInfoDTO;
	}
}
