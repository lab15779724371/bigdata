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
import com.tfit.BdBiProcSrvShEduOmc.dto.home.GoodsOptRate;
import com.tfit.BdBiProcSrvShEduOmc.dto.home.GoodsOptRateDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//配货单操作率趋势应用模型
public class GoodsOptRateAppMod {
	private static final Logger logger = LogManager.getLogger(GoodsOptRateAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//时间坐标个数
	final int timeCoordNum = 7;
	
	//变量数据初始化
	float curAssignRate = (float) 78.02;
	float curDispRate = (float) 57.11;
	float curAcceptRate = (float) 46.35;
	//数组数据初始化
	String[] timeCoord_Array = {"9/21", "9/22", "9/23", "9/24", "9/25", "9/26", "9/27"};
	float[] assignRate_Array = {(float) 100.00, (float) 100.00, (float) 98.93, (float) 99.14, (float) 99.22, (float) 99.28, (float) 99.41};
	float[] dispRate_Array = {(float) 86.96, (float) 90.91, (float) 26.54, (float) 12.98, (float) 10.49, (float) 16.68, (float) 13.84};
	float[] acceptRate_Array = {(float) 13.04, (float) 9.09, (float) 66.13, (float) 80.68, (float) 83.91, (float) 77.49, (float) 81.49};
	
	//模拟数据函数
	private GoodsOptRateDTO SimuDataFunc() {
		GoodsOptRateDTO gorDto = new GoodsOptRateDTO();
		//时戳
		gorDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//当前指派率
		BigDecimal bd = new BigDecimal(curAssignRate);
		curAssignRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		gorDto.setCurAssignRate(curAssignRate);
		//当前派送率
		bd = new BigDecimal(curDispRate);
		curDispRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		gorDto.setCurDispRate(curDispRate);
		//当前验收率
		bd = new BigDecimal(curAcceptRate);
		curAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		gorDto.setCurAcceptRate(curAcceptRate);
		//配货单操作率趋势模拟数据
		List<GoodsOptRate> goodsOptRate = new ArrayList<>();
		DateTime dt = new DateTime();
		//赋值
		for (int i = 0; i < timeCoord_Array.length; i++) {
			GoodsOptRate gor = new GoodsOptRate();
			DateTime curDt = dt.minusDays(timeCoord_Array.length-i-1);
			timeCoord_Array[i] = BCDTimeUtil.convertMonthDayForm(curDt);
			gor.setTimeCoord(timeCoord_Array[i]);
			bd = new BigDecimal(assignRate_Array[i]);
			assignRate_Array[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			gor.setAssignRate(assignRate_Array[i]);;
			bd = new BigDecimal(dispRate_Array[i]);
			dispRate_Array[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			gor.setDispRate(dispRate_Array[i]);		
			bd = new BigDecimal(acceptRate_Array[i]);
			acceptRate_Array[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			gor.setAcceptRate(acceptRate_Array[i]);
			goodsOptRate.add(gor);
		}
		//设置数据
		gorDto.setGoodsOptRate(goodsOptRate);
		//消息ID
		gorDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gorDto;
	}
	
	// 配货单操作率趋势函数
	GoodsOptRateDTO goodsOptRate(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList) {
		GoodsOptRateDTO gorDto = new GoodsOptRateDTO();
		List<GoodsOptRate> goodsOptRate = new ArrayList<>();
		String key = "", keyVal = "", field = "";
		Map<String, String> distributionTotalMap = null;
		int i, j, k, distCount = tddList.size(), dateCount = dates.length;
		//总配送单
		int[][] totalGsPlanNums = new int[dateCount][distCount]; 
		//已指派配送单
		int[][] assignGsPlanNums = new int[dateCount][distCount]; 
		//已派送配送单
		int[][] dispGsPlanNums = new int[dateCount][distCount]; 
		//已验收配送单
		int[][] acceptGsPlanNums = new int[dateCount][distCount];
		//配送单指派率
		float[] assignRates = new float[dateCount];
		//配送单派送率
		float[] dispRates = new float[dateCount];
		//配送单验收率
		float[] acceptRates = new float[dateCount];
		
		
		//总配送单
		int[][] schTotalGsPlanNums = new int[dateCount][distCount]; 
		//已指派配送单
		int[][] schAssignGsPlanNums = new int[dateCount][distCount]; 
		//已派送配送单
		int[][] schDispGsPlanNums = new int[dateCount][distCount]; 
		//已验收配送单
		int[][] schAcceptGsPlanNums = new int[dateCount][distCount];
		//学校指派率
		float[] schAssignRates = new float[dateCount];
		//学校派送率
		float[] schDispRates = new float[dateCount];
		//学校验收率
		float[] schAcceptRates = new float[dateCount];
		
		// 最近7天每天配货计划总数、已指派数、已配送数和已验收数
		for(k = 0; k < dateCount; k++) {
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionTotalMap != null) {				
				for (i = 0; i < tddList.size(); i++) {
					TEduDistrictDo curTdd = tddList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distIdorSCName != null) {
						if(!curDistId.equalsIgnoreCase(distIdorSCName))
							continue ;
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
					// 已指派数
					assignGsPlanNums[k][i] = 0;
					for(j = 0; j < 4; j++) {
						field = "area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int curAssignGsPlanNum = Integer.parseInt(keyVal);
							if(curAssignGsPlanNum < 0)
								curAssignGsPlanNum = 0;
							assignGsPlanNums[k][i] += curAssignGsPlanNum;
						}
					}
					// 已配送数
					dispGsPlanNums[k][i] = 0;
					for(j = 2; j < 4; j++) {
						field = "area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int dispGsPlanNum = Integer.parseInt(keyVal);
							if(dispGsPlanNum < 0)
								dispGsPlanNum = 0;
							dispGsPlanNums[k][i] += dispGsPlanNum;
						}
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
					
					/**
					 * 学校维度配送单的统计
					 */
					// 区域配货计划总数
					schTotalGsPlanNums[k][i] = 0;
					for(j = -2; j < 4; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int curSchTotalGsPlanNum = Integer.parseInt(keyVal);
							if(curSchTotalGsPlanNum < 0)
								curSchTotalGsPlanNum = 0;
							schTotalGsPlanNums[k][i] += curSchTotalGsPlanNum;
						}
					}
					
					// 已指派数
					schAssignGsPlanNums[k][i] = 0;
					for(j = 0; j < 4; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int curAssignGsPlanNum = Integer.parseInt(keyVal);
							if(curAssignGsPlanNum < 0)
								curAssignGsPlanNum = 0;
							schAssignGsPlanNums[k][i] += curAssignGsPlanNum;
						}
					}
					// 已配送数
					schDispGsPlanNums[k][i] = 0;
					for(j = 2; j < 4; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int dispGsPlanNum = Integer.parseInt(keyVal);
							if(dispGsPlanNum < 0)
								dispGsPlanNum = 0;
							schDispGsPlanNums[k][i] += dispGsPlanNum;
						}
					}
					// 已验收数
					field = "school-area" + "_" + curDistId + "_" + "status" + "_3";
					schAcceptGsPlanNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						schAcceptGsPlanNums[k][i] = Integer.parseInt(keyVal);
						if(schAcceptGsPlanNums[k][i] < 0)
							schAcceptGsPlanNums[k][i] = 0;
					}
				}
			}
		}
		// 最近7天每天已指派率、已配送率和已验收率
		for(k = 0; k < dateCount; k++) {
			GoodsOptRate gor = new GoodsOptRate();
			assignRates[k] = 0;
			dispRates[k] = 0;
			acceptRates[k] = 0;
			DateTime curDt = BCDTimeUtil.convertDateStrToDate(dates[k]);
			String timeCoord = curDt.toString("M/d");
			int totalGsPlanNum = 0, assignGsPlanNum = 0, dispGsPlanNum = 0, acceptGsPlanNum = 0;
			for(i = 0; i < distCount; i++) {
				totalGsPlanNum += totalGsPlanNums[k][i];
				assignGsPlanNum += assignGsPlanNums[k][i];
				dispGsPlanNum += dispGsPlanNums[k][i];
				acceptGsPlanNum += acceptGsPlanNums[k][i];
			}
			// 区域配货计划
			if (totalGsPlanNum != 0) {
				//指派率
				assignRates[k] = 100 * ((float) assignGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(assignRates[k]);
				assignRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (assignRates[k] > 100) {
					assignRates[k] = 100;
				}
				//配送率
				dispRates[k] = 100 * ((float) dispGsPlanNum / (float) totalGsPlanNum);
				bd = new BigDecimal(dispRates[k]);
				dispRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (dispRates[k] > 100) {
					dispRates[k] = 100;
				}
				//验收率
				acceptRates[k] = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				bd = new BigDecimal(acceptRates[k]);
				acceptRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRates[k] > 100) {
					acceptRates[k] = 100;
				}*/
			}
			logger.info("日期：" + dates[k] + "，配货计划总数：" + totalGsPlanNums[k] + "，已指派数：" + assignGsPlanNums[k] + "，指派率：" + assignRates[k] + "，配送数：" + dispGsPlanNums[k] + "，配送率：" + dispRates[k] + "，验收数：" + acceptGsPlanNums[k] + "，验收率：" + acceptRates[k]);
			gor.setAssignRate(assignRates[k]);
			gor.setDispRate(dispRates[k]);
			gor.setAcceptRate(acceptRates[k]);
			
			
			/**
			 * 学校配送单维度数据处理
			 */
			schAssignRates[k] = 0;
			schDispRates[k] = 0;
			schAcceptRates[k] = 0;
			int schTotalGsPlanNum = 0, schAssignGsPlanNum = 0, schDispGsPlanNum = 0, schAcceptGsPlanNum = 0;
			for(i = 0; i < distCount; i++) {
				schTotalGsPlanNum += schTotalGsPlanNums[k][i];
				schAssignGsPlanNum += schAssignGsPlanNums[k][i];
				schDispGsPlanNum += schDispGsPlanNums[k][i];
				schAcceptGsPlanNum += schAcceptGsPlanNums[k][i];
			}
			// 区域配货计划
			if (schTotalGsPlanNum != 0) {
				//指派率
				schAssignRates[k] = 100 * ((float) schAssignGsPlanNum / (float) schTotalGsPlanNum);
				BigDecimal bd = new BigDecimal(schAssignRates[k]);
				schAssignRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAssignRates[k] > 100) {
					schAssignRates[k] = 100;
				}
				//配送率
				schDispRates[k] = 100 * ((float) schDispGsPlanNum / (float) schTotalGsPlanNum);
				bd = new BigDecimal(schDispRates[k]);
				schDispRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schDispRates[k] > 100) {
					schDispRates[k] = 100;
				}
				//验收率
				schAcceptRates[k] = 100 * ((float) schAcceptGsPlanNum / (float) schTotalGsPlanNum);
				bd = new BigDecimal(schAcceptRates[k]);
				schAcceptRates[k] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRates[k] > 100) {
					schAcceptRates[k] = 100;
				}
			}
			logger.info("日期：" + dates[k] + "，学校配货计划总数：" + schTotalGsPlanNums[k] + 
					"，学校已指派数：" + schAssignGsPlanNums[k] + "，学校指派率：" + schAssignRates[k] + 
					"，学校配送数：" + schDispGsPlanNums[k] + "，学校配送率：" + schDispRates[k] + 
					"，学校验收数：" + schAcceptGsPlanNums[k] + "，学校验收率：" + schAcceptRates[k]);
			gor.setSchAssignRate(schAssignRates[k]);
			gor.setSchDispRate(schDispRates[k]);
			gor.setSchAcceptRate(schAcceptRates[k]);
			
			gor.setTimeCoord(timeCoord);
			goodsOptRate.add(gor);
			//今日指派率、配送率和验收率
			if(k == dateCount-1) {
				gorDto.setCurAssignRate(assignRates[k]);
				gorDto.setCurDispRate(dispRates[k]);
				gorDto.setCurAcceptRate(acceptRates[k]);
				
				//学校配送大维度统计
				gorDto.setCurSchAssignRate(schAssignRates[k]);
				gorDto.setCurSchDispRate(schDispRates[k]);
				gorDto.setCurSchAcceptRate(schAcceptRates[k]);
			}
		}
		// 设置返回数据
		gorDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//设置数据
		gorDto.setGoodsOptRate(goodsOptRate);
		//消息ID
		gorDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gorDto;
	}
	
	// 配货单操作率趋势模型函数
	public GoodsOptRateDTO appModFunc(String token, String distName, String prefCity, String province, int timeType, String date, Db1Service db1Service, Db2Service db2Service) {
		GoodsOptRateDTO gorDto = null;
		if(isRealData) {         //真实数据
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
					// 配货单操作率趋势函数
					gorDto = goodsOptRate(departmentId,distIdorSCName, dates, tddList);
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
					// 配货单操作率趋势函数
					gorDto = goodsOptRate(departmentId,distIdorSCName, dates, tddList);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			gorDto = SimuDataFunc();
		}		

		return gorDto;
	}
}
