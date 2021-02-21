package com.tfit.BdBiProcSrvShEduOmc.appmod.home;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.tfit.BdBiProcSrvShEduOmc.dto.home.DishRsRate;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.DishRsRateDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//菜品留样率应用模型
public class DishRsRateAppMod {
	private static final Logger logger = LogManager.getLogger(DishRsRateAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//时间坐标个数
	final int timeCoordNum = 7;
	
	//最小供餐学校数量域值
	final int minMealSchNumThre = 0;
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//数组数据初始化
	String[] timeCoord_Array = {"8/6", "8/7", "8/8", "8/9", "8/10", "8/11", "8/12"};
	float[] rsRate_Array = {(float) 88.21, (float) 78.34, (float) 69.56, (float) 75.78, (float) 84.22, (float) 82.55, (float) 81.15};
	
	//模拟数据函数
	private DishRsRateDTO SimuDataFunc() {
		DishRsRateDTO drrDto = new DishRsRateDTO();
		//时戳
		drrDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//当前排菜率
		float curRsRate = (float) 71.00;
		BigDecimal bd = new BigDecimal(curRsRate);
		curRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		drrDto.setCurRsRate(curRsRate);
		//菜品留样率模拟数据
		List<DishRsRate> dishRsRate = new ArrayList<>();
		DateTime dt = new DateTime();
		//赋值
		for (int i = 0; i < timeCoord_Array.length; i++) {
			DishRsRate drr = new DishRsRate();
			DateTime curDt = dt.minusDays(timeCoord_Array.length-i-1);
			timeCoord_Array[i] = BCDTimeUtil.convertMonthDayForm(curDt);
			drr.setTimeCoord(timeCoord_Array[i]);
			bd = new BigDecimal(rsRate_Array[i]);
			rsRate_Array[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			drr.setRsRate(rsRate_Array[i]);
			dishRsRate.add(drr);
		}
		//设置数据
		drrDto.setDishRsRate(dishRsRate);
		//消息ID
		drrDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return drrDto;
	}
	
	// 菜品留样率趋势函数
	private DishRsRateDTO dishRsRate(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList) {
		DishRsRateDTO drrDto = new DishRsRateDTO();
		List<DishRsRate> dishRsRate = new ArrayList<>();
		String key = "", keyVal = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> gcRetentiondishtotalMap = null;
		int i, k, dateCount = dates.length;
		int[] totalDishNums = new int[dateCount], distRsDishNums = new int[dateCount];
		float[] distRsRates = new float[dateCount];
		
		//已留样学校
		int[] distRsSchNums = new int[dateCount];
		//未留样学校
		int[] distNoRsSchNums = new int[dateCount];
		// 当天各区菜品留样数量
		for (k = 0; k < dates.length; k++) {
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
			gcRetentiondishtotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(gcRetentiondishtotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				
			}
			if(gcRetentiondishtotalMap != null) {
				for(String curKey : gcRetentiondishtotalMap.keySet()) {
					for (i = 0; i < tddList.size(); i++) {
						TEduDistrictDo curTdd = tddList.get(i);
						String curDistId = curTdd.getId();
						//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						if(distIdorSCName != null) {
							if(!curDistId.equals(distIdorSCName))
								continue ;
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
										distRsDishNums[k] += Integer.parseInt(keyVal);
									}
								}
							}
						}
						if(curKey.equalsIgnoreCase(curDistId)) {      //区域菜品总数
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
		}
		// 最近7天每天留样率
		float curSchRsRate = 0;
		for(k = 0; k < dateCount; k++) {
			DishRsRate drr = new DishRsRate();
			DateTime curDt = BCDTimeUtil.convertDateStrToDate(dates[k]);
			String timeCoord = curDt.toString("M/d");
			// 区域留样数
			if (totalDishNums[k] != 0) {
				//留样率
				distRsRates[k] = 100 * ((float) distRsDishNums[k] / (float) totalDishNums[k]);
				BigDecimal bd = new BigDecimal(distRsRates[k]);
				distRsRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distRsRates[k] > 100) {
					distRsRates[k] = 100;
				}
			}
			
			//3.留样率的计算改为：已留样的学校数量/排菜学校数量*100%。
			curSchRsRate = 0;
			if((distNoRsSchNums[k] + distRsSchNums[k]) > 0) {
				curSchRsRate = 100 * ((float) distRsSchNums[k] / (float) (distNoRsSchNums[k] + distRsSchNums[k]));
				BigDecimal bd = new BigDecimal(curSchRsRate);
				curSchRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (curSchRsRate > 100) {
					curSchRsRate = 100;
				}
			}
			
			logger.info("日期：" + dates[k] + "，菜品总数：" + totalDishNums[k] + "，已留样数：" + distRsDishNums[k] + "，留样率：" + distRsRates[k]);
			drr.setRsRate(distRsRates[k]);
			drr.setSchRsRate(curSchRsRate);
			drr.setTimeCoord(timeCoord);
			dishRsRate.add(drr);
			//今日留样率
			if(k == dateCount-1) {
				drrDto.setCurRsRate(distRsRates[k]);
				//学校留样率
				drrDto.setCurSchRsRate(curSchRsRate);
			}
		}
		// 设置返回数据
		drrDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//设置数据
		drrDto.setDishRsRate(dishRsRate);
		//消息ID
		drrDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return drrDto;
	}
	
	// 菜品留样率模型函数
	public DishRsRateDTO appModFunc(String token, String distName, String prefCity, String province, int timeType, String date, Db1Service db1Service, Db2Service db2Service) {
		DishRsRateDTO drrDto = null;
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
					// 菜品留样率函数
					drrDto = dishRsRate(departmentId,distIdorSCName, dates, tddList);
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
					// 菜品留样率函数
					drrDto = dishRsRate(departmentId,distIdorSCName, dates, tddList);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}			
		}
		else {    //模拟数据
			//模拟数据函数
			drrDto = SimuDataFunc();
		}		

		return drrDto;
	}
}
