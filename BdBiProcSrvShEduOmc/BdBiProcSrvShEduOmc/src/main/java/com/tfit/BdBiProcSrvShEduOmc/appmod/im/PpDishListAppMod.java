package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishList;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishListDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//项目点排菜列表应用模型
public class PpDishListAppMod {
	private static final Logger logger = LogManager.getLogger(PpDishListAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//二级排序条件
	final String[] methods0 = {"getSubLevel", "getCompDep"};
	final String[] sorts0 = {"desc", "desc"};
	final String[] dataTypes0 = {"String", "String"};
	
	final String[] methods1 = {"getDistName", "getDishDate"};
	final String[] sorts1 = {"asc", "asc"};
	final String[] dataTypes1 = {"String", "String"};
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] dishDate_Array = {"2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03", "2018-09-03"};
	String[] subLevel_Array = {"区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属"};
	String[] compDep_Array = {"黄浦区教育局", "嘉定区教育局", "宝山区教育局", "浦东新区教育局", "松江区教育局", "金山区教育局", "青浦区教育局", "奉贤区教育局", "崇明区教育局", "静安区教育局", "徐汇区教育局", "长宁区教育局", "普陀区教育局", "虹口区教育局", "杨浦区教育局", "闵行区教育局"};
	String[] distName_Array = {"黄浦区", "嘉定区", "宝山区", "浦东新区", "松江区", "金山区", "青浦区", "奉贤区", "崇明区", "静安区 ", "徐汇区", "长宁区", "普陀区", "虹口区", "杨浦区", "闵行区"};
	int[] regSchNum_Array = {161, 222, 375, 974, 292, 150, 190, 210, 146, 253, 278, 150, 247, 162, 248, 475};
	int[] mealSchNum_Array = {151, 212, 365, 964, 282, 140, 180, 200, 136, 243, 268, 140, 237, 152, 238, 465};
	int[] dishSchNum_Array = {130, 166, 321, 615, 226, 119, 128, 159, 98, 217, 234, 134, 197, 139, 218, 358};
	int[] noDishSchNum_Array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};   
	
	//模拟数据函数
	private PpDishListDTO SimuDataFunc() {
		PpDishListDTO pdlDto = new PpDishListDTO();
		// 设置返回数据
		pdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		List<PpDishList> ppDishList = new ArrayList<>();
		//赋值
		for (int i = 0; i < distName_Array.length; i++) {
			PpDishList pdl = new PpDishList();
			pdl.setDishDate(dishDate_Array[i]);
			pdl.setSubLevel(subLevel_Array[i]);
			pdl.setCompDep(compDep_Array[i]);
			pdl.setDistName(distName_Array[i]);
			pdl.setRegSchNum(regSchNum_Array[i]);
			pdl.setMealSchNum(mealSchNum_Array[i]);
			pdl.setDishSchNum(dishSchNum_Array[i]);
			noDishSchNum_Array[i] = mealSchNum_Array[i]-dishSchNum_Array[i];
			pdl.setNoDishSchNum(noDishSchNum_Array[i]);
			float dishRate = 100*((float)(dishSchNum_Array[i])/(float)(mealSchNum_Array[i]));
			BigDecimal bd = new BigDecimal(dishRate); 
			dishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if(dishRate > 100)
				dishRate = 100;
			pdl.setDishRate(dishRate);
			ppDishList.add(pdl);
		}
		//设置模拟数据
		pdlDto.setPpDishList(ppDishList);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = distName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		pdlDto.setPageInfo(pageInfo);
		//消息ID
		pdlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return pdlDto;
	}
	
	// 项目点排菜列表函数按主管部门
	private PpDishListDTO ppDishListByCompDep(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, 
			int subLevel, int compDep, String subDistName,String subLevels,String compDeps) {
		PpDishListDTO pdlDto = new PpDishListDTO();
		List<PpDishList> ppDishList = new ArrayList<>();
		PpDishList pdl = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> platoonFeedTotalMap = null;
		int i, j, k, subLevelCount = 4, compDepCount = 0, 
				maxCompDepCount = AppModConfig.compDepIdToNameMap3.size(),
				dateCount = dates.length;
		int[][][] totalMealSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				distDishSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				distNoDishSchNums = new int[dateCount][subLevelCount][maxCompDepCount];
		float[][] distDishRates = new float[subLevelCount][maxCompDepCount];
		
		int[][][] standardNums = new int[dateCount][subLevelCount][maxCompDepCount];
		int[][][] supplementNums = new int[dateCount][subLevelCount][maxCompDepCount];
		int[][][] beOverdueNums = new int[dateCount][subLevelCount][maxCompDepCount];
		int[][][] noDataNums = new int[dateCount][subLevelCount][maxCompDepCount];
		
		List<Object> subLevelList=CommonUtil.changeStringToList(subLevels);
		List<Object> compDepList=CommonUtil.changeStringToList(compDeps);
		
		//各区学校数量
		key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		
		Map<String, String> schoolDataMap = null;
		int[][] regSchNums = new int[subLevelCount][maxCompDepCount];
		schoolDataMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(schoolDataMap != null) {
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
					field = fieldPrefix;
					if(schoolDataMap.containsKey(field)) {
						keyVal = schoolDataMap.get(field);
						regSchNums[i][j] = Integer.parseInt(keyVal);
					}
				}
			}
		}
		// 当天各区排菜学校数量
		for (k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]  + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(platoonFeedTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				platoonFeedTotalMap = AppModConfig.getHdfsDataKey(dates[k], key);
			}
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
								fieldPrefix = "masterid_" + i + "_slave_" + j;
							else if(i == 3) {
								String compDepId = String.valueOf(j);								
								fieldPrefix = "masterid_" + i + "_slave_" + AppModConfig.compDepIdToNameMap3.get(compDepId);
							}
							// 区域排菜学校供餐数
							int mealSchNum = 0, dishSchNum = 0, noDishSchNum = 0;
							if (curKey.indexOf(fieldPrefix) == 0) {
								String[] curKeys = curKey.split("_");
								if(curKeys.length >= 6)
								{
									if(curKeys[4].equalsIgnoreCase("供餐") && curKeys[5].equalsIgnoreCase("已排菜")) {
										keyVal = platoonFeedTotalMap.get(curKey);
										if(keyVal != null) {
											mealSchNum = Integer.parseInt(keyVal);
											dishSchNum = mealSchNum;
										}
									}
									else if(curKeys[4].equalsIgnoreCase("供餐") && curKeys[5].equalsIgnoreCase("未排菜")) {
										keyVal = platoonFeedTotalMap.get(curKey);
										if(keyVal != null) {
											mealSchNum = Integer.parseInt(keyVal);
											noDishSchNum = mealSchNum;
										}
									}
								}
							}
							totalMealSchNums[k][i][j] += mealSchNum;
							distDishSchNums[k][i][j] += dishSchNum;
							distNoDishSchNums[k][i][j] += noDishSchNum;
							
							//操作状态对应的数量
							fieldPrefix = fieldPrefix + "_plastatus_";
							int standardNum = 0, supplementNum = 0, beOverdueNum = 0, noDataNum = 0;
							if (curKey.indexOf(fieldPrefix) == 0) {
								String[] curKeys = curKey.split("_");
								
								if(curKeys.length >= 4)
								{
									if(curKeys[5].equalsIgnoreCase("1")) {
										keyVal = platoonFeedTotalMap.get(curKey);
										if(keyVal != null) {
											standardNum = Integer.parseInt(keyVal);
										}
									}else if(curKeys[5].equalsIgnoreCase("2")) {
										keyVal = platoonFeedTotalMap.get(curKey);
										if(keyVal != null) {
											supplementNum = Integer.parseInt(keyVal);
										}
									}else if(curKeys[5].equalsIgnoreCase("3")) {
										keyVal = platoonFeedTotalMap.get(curKey);
										if(keyVal != null) {
											beOverdueNum = Integer.parseInt(keyVal);
										}
									}else if(curKeys[5].equalsIgnoreCase("4")) {
										keyVal = platoonFeedTotalMap.get(curKey);
										if(keyVal != null) {
											noDataNum = Integer.parseInt(keyVal);
										}
									}
								}
							}
							
							standardNums[k][i][j] += standardNum;
							supplementNums[k][i][j] += supplementNum;
							beOverdueNums[k][i][j] += beOverdueNum;
							noDataNums[k][i][j] += noDataNum;
						}
					}
				}
			}
			// 该日期各区学校排菜率
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
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + compDepName + "，排菜学校数量：" + distDishSchNums[k][i] 	+ "，供餐学校总数：" + totalMealSchNums[k][i] + "，排菜率：" + distDishRates[i] + "，field = " + field);				
				}
			}
		}
		float standardRate = 0;
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
				//监管学校为0,不展示
				if(regSchNums[i][j] <=0) {
				   continue;
				}
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
					pdl = new PpDishList();
					pdl.setDishDate(dates[k].replaceAll("-", "/"));
					pdl.setSubLevel(String.valueOf(i) + "," + AppModConfig.subLevelIdToNameMap.get(i));
					pdl.setCompDep(compDepId + "," + compDepName);
					int totalDistSchNum = 0, distDishSchNum = 0, distNoDishSchNum = 0;
					totalDistSchNum = totalMealSchNums[k][i][j];
					distDishSchNum = distDishSchNums[k][i][j];
					distNoDishSchNum = distNoDishSchNums[k][i][j];		
					pdl.setRegSchNum(regSchNums[i][j]);
					pdl.setMealSchNum(totalDistSchNum);
					pdl.setDishSchNum(distDishSchNum);
					pdl.setNoDishSchNum(distNoDishSchNum);
					distDishRates[i][j] = 0;
					if(totalDistSchNum > 0) {
						distDishRates[i][j] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
						BigDecimal bd = new BigDecimal(distDishRates[i][j]);
						distDishRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (distDishRates[i][j] > 100)
							distDishRates[i][j] = 100;
					}
					pdl.setDishRate(distDishRates[i][j]);
					

					//1 表示规范录入
					pdl.setStandardNum(standardNums[k][i][j]);
					//2 表示补录
					pdl.setSupplementNum(supplementNums[k][i][j]);
					//3 表示逾期补录
					pdl.setBeOverdueNum(beOverdueNums[k][i][j]);
					//4 表示无数据
					pdl.setNoDataNum(noDataNums[k][i][j]);
					
					standardRate = 0;
					if(totalDistSchNum > 0) {
						standardRate = 100 * ((float) pdl.getStandardNum() / (float) totalDistSchNum);
						BigDecimal bd = new BigDecimal(standardRate);
						standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (standardRate > 100)
							standardRate = 100;
					}
					pdl.setStandardRate(standardRate);
					
					
					ppDishList.add(pdl);
				}
			}
		}
		//排序
		SortList<PpDishList> sortList = new SortList<PpDishList>();
		sortList.Sort(ppDishList, methods0, sorts0, dataTypes0);
		//时戳
    	pdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
    	// 分页
    	PageBean<PpDishList> pageBean = new PageBean<PpDishList>(ppDishList, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	pdlDto.setPageInfo(pageInfo);
    	// 设置数据
    	pdlDto.setPpDishList(pageBean.getCurPageData());
    	// 消息ID
    	pdlDto.setMsgId(AppModConfig.msgId);
    	AppModConfig.msgId++;
    	// 消息id小于0判断
    	AppModConfig.msgIdLessThan0Judge();
		
		return pdlDto;
	}
	
	// 项目点排菜列表函数按主管部门
	private PpDishListDTO ppDishListByCompDepFromHive(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, 
			int subLevel, int compDep, String subDistName,String subLevels,String compDeps,DbHiveDishService dbHiveDishService) {
		PpDishListDTO pdlDto = new PpDishListDTO();
		List<PpDishList> ppDishList = new ArrayList<>();
		PpDishList pdl = null;
		// 当天排菜学校总数
		int i, j, k, subLevelCount = 4, compDepCount = 0, 
				maxCompDepCount = AppModConfig.compDepIdToNameMap3.size();
		float[][] distDishRates = new float[subLevelCount][maxCompDepCount];
		
		List<Object> subLevelList=CommonUtil.changeStringToList(subLevels);
		List<Object> compDepList=CommonUtil.changeStringToList(compDeps);
		
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
				subLevel,compDep, subLevelList, compDepList,departmentId,null, 3);
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
		}
		
		float standardRate = 0;	
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
				String compDepId = "", compDepName = "其他";
				if(i < 3) {   //其他、部属、市属
					compDepId = String.valueOf(j);
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
				}
				for (k = 0; k < dates.length; k++) {
					pdl = new PpDishList();
					pdl.setDishDate(dates[k].replaceAll("-", "/"));
					pdl.setSubLevel(String.valueOf(i) + "," + AppModConfig.subLevelIdToNameMap.get(i));
					pdl.setCompDep(compDepId + "," + compDepName);
					
					String dataKey = pdl.getDishDate()+"_"+i+"_" + compDepId;
					
					int totalSchNum = totalSchNumMap.get(dataKey)==null?0:totalSchNumMap.get(dataKey);
					int totalDistSchNum = totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey);
					int distDishSchNum = distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey);
					int distNoDishSchNum = distNoDishSchNumMap.get(dataKey)==null?0:distNoDishSchNumMap.get(dataKey);
					
					pdl.setRegSchNum(totalSchNum);
					pdl.setMealSchNum(totalDistSchNum);
					pdl.setDishSchNum(distDishSchNum);
					pdl.setNoDishSchNum(distNoDishSchNum);
					distDishRates[i][j] = 0;
					if(totalDistSchNum > 0) {
						distDishRates[i][j] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
						BigDecimal bd = new BigDecimal(distDishRates[i][j]);
						distDishRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (distDishRates[i][j] > 100)
							distDishRates[i][j] = 100;
					}
					pdl.setDishRate(distDishRates[i][j]);
					
					//1 表示规范录入
					int standardNum = standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey);
					//2 表示补录
					int supplementNum = supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey);
					//3 表示逾期补录
					int beOverdueNum = beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey);
					//4 表示无数据
					int noDataNum = noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey);
					//1 表示规范录入
					pdl.setStandardNum(standardNum);
					//2 表示补录
					pdl.setSupplementNum(supplementNum);
					//3 表示逾期补录
					pdl.setBeOverdueNum(beOverdueNum);
					//4 表示无数据
					pdl.setNoDataNum(noDataNum);
					standardRate = 0;
					if(totalDistSchNum > 0) {
						standardRate = 100 * ((float) pdl.getStandardNum() / (float) totalDistSchNum);
						BigDecimal bd = new BigDecimal(standardRate);
						standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (standardRate > 100)
							standardRate = 100;
					}
					pdl.setStandardRate(standardRate);
					
					ppDishList.add(pdl);
				}
			}
		}
		//排序
		SortList<PpDishList> sortList = new SortList<PpDishList>();
		sortList.Sort(ppDishList, methods0, sorts0, dataTypes0);
		//时戳
    	pdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
    	// 分页
    	PageBean<PpDishList> pageBean = new PageBean<PpDishList>(ppDishList, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	pdlDto.setPageInfo(pageInfo);
    	// 设置数据
    	pdlDto.setPpDishList(pageBean.getCurPageData());
    	// 消息ID
    	pdlDto.setMsgId(AppModConfig.msgId);
    	AppModConfig.msgId++;
    	// 消息id小于0判断
    	AppModConfig.msgIdLessThan0Judge();
		
		return pdlDto;
	}
	
	// 项目点排菜列表函数按所在区
	private PpDishListDTO ppDishListByLocality(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			String distNames) {
		PpDishListDTO pdlDto = new PpDishListDTO();
		List<PpDishList> ppDishList = new ArrayList<>();
		PpDishList pdl = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> platoonFeedTotalMap = null;
		int distCount = tedList.size(), dateCount = dates.length;
		int[][] totalMealSchNums = new int[dateCount][distCount], 
				distDishSchNums = new int[dateCount][distCount], 
				distNoDishSchNums = new int[dateCount][distCount];
		int[][] standardNums = new int[dateCount][distCount];
		int[][] supplementNums = new int[dateCount][distCount];
		int[][] beOverdueNums = new int[dateCount][distCount];
		int[][] noDataNums = new int[dateCount][distCount];
		
		float[] distDishRates = new float[distCount];
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		
		//各区学校数量
		key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		Map<String, String> schoolDataMap = null;
		int[] regSchNums = new int[distCount];
		schoolDataMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
		if(schoolDataMap != null) {
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
				field = "area_" + curDistId;
				if(schoolDataMap.containsKey(field)) {
					keyVal = schoolDataMap.get(field);
					regSchNums[i] = Integer.parseInt(keyVal);
				}
			}
		}
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]  + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(platoonFeedTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				platoonFeedTotalMap = AppModConfig.getHdfsDataKey(dates[k], key);
			}
			if(platoonFeedTotalMap != null) {
				for(String curKey : platoonFeedTotalMap.keySet()) {
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
						// 区域排菜学校供餐数
						fieldPrefix = curDistId + "_";
						int mealSchNum = 0, dishSchNum = 0, noDishSchNum = 0;
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
										noDishSchNum = mealSchNum;
									}
								}
							}
						}
						
						//操作状态对应的数量
						fieldPrefix = "area_"+curDistId + "_plastatus_";
						int standardNum = 0, supplementNum = 0, beOverdueNum = 0, noDataNum = 0;
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							
							if(curKeys.length >= 4)
							{
								if(curKeys[3].equalsIgnoreCase("1")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										standardNum = Integer.parseInt(keyVal);
									}
								}else if(curKeys[3].equalsIgnoreCase("2")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										supplementNum = Integer.parseInt(keyVal);
									}
								}else if(curKeys[3].equalsIgnoreCase("3")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										beOverdueNum = Integer.parseInt(keyVal);
									}
								}else if(curKeys[3].equalsIgnoreCase("4")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										noDataNum = Integer.parseInt(keyVal);
									}
								}
							}
						}
						
						standardNums[k][i] += standardNum;
						supplementNums[k][i] += supplementNum;
						beOverdueNums[k][i] += beOverdueNum;
						noDataNums[k][i] += noDataNum;
						
						totalMealSchNums[k][i] += mealSchNum;
						distDishSchNums[k][i] += dishSchNum;
						distNoDishSchNums[k][i] += noDishSchNum;
					}
				}
			}
			// 该日期各区学校排菜率
			for (int i = 0; i < distCount; i++) {
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
						+ "，供餐学校总数：" + totalMealSchNums[k][i] + "，排菜率：" + distDishRates[i] + "，field = "
						+ field);
			}
		}
		
		float standardRate = 0;	
		for (int i = 0; i < distCount; i++) {
			if(regSchNums[i] <=0) {
				continue;
			}
			for (int k = 0; k < dates.length; k++) {
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
				pdl = new PpDishList();
				pdl.setDishDate(dates[k].replaceAll("-", "/"));
				pdl.setDistName(curTdd.getId());
				int totalDistSchNum = 0, distDishSchNum = 0, distNoDishSchNum = 0;
				totalDistSchNum = totalMealSchNums[k][i];
				distDishSchNum = distDishSchNums[k][i];
				distNoDishSchNum = distNoDishSchNums[k][i];		
				pdl.setRegSchNum(regSchNums[i]);
				pdl.setMealSchNum(totalDistSchNum);
				pdl.setDishSchNum(distDishSchNum);
				pdl.setNoDishSchNum(distNoDishSchNum);
				distDishRates[i] = 0;
				if(totalDistSchNum > 0) {
					distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
					BigDecimal bd = new BigDecimal(distDishRates[i]);
					distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distDishRates[i] > 100)
						distDishRates[i] = 100;
				}
				pdl.setDishRate(distDishRates[i]);
				
				//1 表示规范录入
				pdl.setStandardNum(standardNums[k][i]);
				//2 表示补录
				pdl.setSupplementNum(supplementNums[k][i]);
				//3 表示逾期补录
				pdl.setBeOverdueNum(beOverdueNums[k][i]);
				//4 表示无数据
				pdl.setNoDataNum(noDataNums[k][i]);
				
				standardRate = 0;
				if(totalDistSchNum > 0) {
					standardRate = 100 * ((float) pdl.getStandardNum() / (float) totalDistSchNum);
					BigDecimal bd = new BigDecimal(standardRate);
					standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (standardRate > 100)
						standardRate = 100;
				}
				pdl.setStandardRate(standardRate);
				
				ppDishList.add(pdl);
			}
		}
		//排序
    	SortList<PpDishList> sortList = new SortList<PpDishList>();  
    	sortList.Sort(ppDishList, methods1, sorts1, dataTypes1);
		//时戳
    	pdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
    	// 分页
    	PageBean<PpDishList> pageBean = new PageBean<PpDishList>(ppDishList, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	pdlDto.setPageInfo(pageInfo);
    	// 设置数据
    	pdlDto.setPpDishList(pageBean.getCurPageData());
    	// 消息ID
    	pdlDto.setMsgId(AppModConfig.msgId);
    	AppModConfig.msgId++;
    	// 消息id小于0判断
    	AppModConfig.msgIdLessThan0Judge();

		return pdlDto;
	}
	
	// 项目点排菜列表函数按所在区
	private PpDishListDTO ppDishListByLocalityFromHive(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			String distNames,
			DbHiveDishService dbHiveDishService) {
		PpDishListDTO pdlDto = new PpDishListDTO();
		List<PpDishList> ppDishList = new ArrayList<>();
		PpDishList pdl = null;
		// 当天排菜学校总数
		int distCount = tedList.size();
		float[] distDishRates = new float[distCount];
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		
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
				-1, -1, null, null,departmentId,null, 0);
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
		}
		float standardRate = 0;		
		for (int i = 0; i < distCount; i++) {
			for (int k = 0; k < dates.length; k++) {
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
				
				pdl = new PpDishList();
				pdl.setDishDate(dates[k].replaceAll("-", "/"));
				pdl.setDistName(curTdd.getId());
				
				String dataKey = pdl.getDishDate() + "_" + curDistId;
				int totalSchNum = totalSchNumMap.get(dataKey)==null?0:totalSchNumMap.get(dataKey);
				int totalDistSchNum = totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey);
				int distDishSchNum = distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey);
				int distNoDishSchNum = distNoDishSchNumMap.get(dataKey)==null?0:distNoDishSchNumMap.get(dataKey);
				
				pdl.setRegSchNum(totalSchNum);
				pdl.setMealSchNum(totalDistSchNum);
				pdl.setDishSchNum(distDishSchNum);
				pdl.setNoDishSchNum(distNoDishSchNum);
				distDishRates[i] = 0;
				if(totalDistSchNum > 0) {
					distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
					BigDecimal bd = new BigDecimal(distDishRates[i]);
					distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distDishRates[i] > 100)
						distDishRates[i] = 100;
				}
				pdl.setDishRate(distDishRates[i]);
				
				//1 表示规范录入
				int standardNum = standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey);
				//2 表示补录
				int supplementNum = supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey);
				//3 表示逾期补录
				int beOverdueNum = beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey);
				//4 表示无数据
				int noDataNum = noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey);
				//1 表示规范录入
				pdl.setStandardNum(standardNum);
				//2 表示补录
				pdl.setSupplementNum(supplementNum);
				//3 表示逾期补录
				pdl.setBeOverdueNum(beOverdueNum);
				//4 表示无数据
				pdl.setNoDataNum(noDataNum);
				
				standardRate = 0;
				if(totalDistSchNum > 0) {
					standardRate = 100 * ((float) pdl.getStandardNum() / (float) totalDistSchNum);
					BigDecimal bd = new BigDecimal(standardRate);
					standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (standardRate > 100)
						standardRate = 100;
				}
				pdl.setStandardRate(standardRate);
				
				ppDishList.add(pdl);
			}
		}
		//排序
    	SortList<PpDishList> sortList = new SortList<PpDishList>();  
    	sortList.Sort(ppDishList, methods1, sorts1, dataTypes1);
		//时戳
    	pdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
    	// 分页
    	PageBean<PpDishList> pageBean = new PageBean<PpDishList>(ppDishList, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	pdlDto.setPageInfo(pageInfo);
    	// 设置数据
    	pdlDto.setPpDishList(pageBean.getCurPageData());
    	// 消息ID
    	pdlDto.setMsgId(AppModConfig.msgId);
    	AppModConfig.msgId++;
    	// 消息id小于0判断
    	AppModConfig.msgIdLessThan0Judge();

		return pdlDto;
	}
	
	// 项目点排菜列表函数按所在区
	private PpDishListDTO ppDishListByDepartmentFromHive(String departmentId, String[] dates, List<DepartmentObj> deparmentList,
			String departmentIds,
			DbHiveDishService dbHiveDishService) {
		PpDishListDTO pdlDto = new PpDishListDTO();
		List<PpDishList> ppDishList = new ArrayList<>();
		PpDishList pdl = null;
		// 当天排菜学校总数
		int distCount = deparmentList.size();
		float[] distDishRates = new float[distCount];
		
		List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
		
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
				-1, -1, null, null,departmentId,departmentIdList, 4);
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

				if(schDishCommon.getPlatoonDealStatus() !=null && schDishCommon.getPlatoonDealStatus() != -1){
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
		}
		float standardRate = 0;			
		for (int i = 0; i < distCount; i++) {
			for (int k = 0; k < dates.length; k++) {
				DepartmentObj departmentObj = deparmentList.get(i);
				String departmentIdTemp = departmentObj.getDepartmentId();
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (departmentId != null) {
					if (!departmentIdTemp.equals(departmentId))
						continue;
				}
				
				if(departmentIdList!=null && departmentIdList.size() >0) {
					if(StringUtils.isEmpty(departmentIdTemp) || !StringUtils.isNumeric(departmentIdTemp)) {
						continue;
					}
					if(!departmentIdList.contains(departmentIdTemp)) {
						continue ;
					}
				}
				
				pdl = new PpDishList();
				pdl.setDishDate(dates[k].replaceAll("-", "/"));
				pdl.setDepartmentId(departmentIdTemp);
				
				String dataKey = pdl.getDishDate() + "_" + departmentIdTemp;
				int totalSchNum = totalSchNumMap.get(dataKey)==null?0:totalSchNumMap.get(dataKey);
				int totalDistSchNum = totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey);
				int distDishSchNum = distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey);
				int distNoDishSchNum = distNoDishSchNumMap.get(dataKey)==null?0:distNoDishSchNumMap.get(dataKey);
				
				pdl.setRegSchNum(totalSchNum);
				pdl.setMealSchNum(totalDistSchNum);
				pdl.setDishSchNum(distDishSchNum);
				pdl.setNoDishSchNum(distNoDishSchNum);
				distDishRates[i] = 0;
				if(totalDistSchNum > 0) {
					distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
					BigDecimal bd = new BigDecimal(distDishRates[i]);
					distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distDishRates[i] > 100)
						distDishRates[i] = 100;
				}
				pdl.setDishRate(distDishRates[i]);
				
				//1 表示规范录入
				int standardNum = standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey);
				//2 表示补录
				int supplementNum = supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey);
				//3 表示逾期补录
				int beOverdueNum = beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey);
				//4 表示无数据
				int noDataNum = noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey);
				//1 表示规范录入
				pdl.setStandardNum(standardNum);
				//2 表示补录
				pdl.setSupplementNum(supplementNum);
				//3 表示逾期补录
				pdl.setBeOverdueNum(beOverdueNum);
				//4 表示无数据
				pdl.setNoDataNum(noDataNum);
				
				standardRate = 0;
				if(totalDistSchNum > 0) {
					standardRate = 100 * ((float) pdl.getStandardNum() / (float) totalDistSchNum);
					BigDecimal bd = new BigDecimal(standardRate);
					standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (standardRate > 100)
						standardRate = 100;
				}
				pdl.setStandardRate(standardRate);
				
				ppDishList.add(pdl);
			}
		}
		//排序
    	//SortList<PpDishList> sortList = new SortList<PpDishList>();  
    	//sortList.Sort(ppDishList, methods1, sorts1, dataTypes1);
		//时戳
    	pdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
    	// 分页
    	PageBean<PpDishList> pageBean = new PageBean<PpDishList>(ppDishList, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	pdlDto.setPageInfo(pageInfo);
    	// 设置数据
    	pdlDto.setPpDishList(pageBean.getCurPageData());
    	// 消息ID
    	pdlDto.setMsgId(AppModConfig.msgId);
    	AppModConfig.msgId++;
    	// 消息id小于0判断
    	AppModConfig.msgIdLessThan0Judge();

		return pdlDto;
	}
	
	// 项目点排菜列表函数按管理部门
	private PpDishListDTO ppDishListByDepartment(String departmentId, String[] dates, Map<Integer, String> schoolDepartmentMap,int [] schOwnTypes,
			String departmentIds) {
		PpDishListDTO pdlDto = new PpDishListDTO();
		List<PpDishList> ppDishList = new LinkedList<>();
		PpDishList pdl = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> platoonFeedTotalMap = null;
		int departmentCount = schoolDepartmentMap.size(), dateCount = dates.length;
		int[][] totalMealSchNums = new int[dateCount][departmentCount], 
				distDishSchNums = new int[dateCount][departmentCount], 
				distNoDishSchNums = new int[dateCount][departmentCount];
		float[] distDishRates = new float[departmentCount];
		
		int[][] standardNums = new int[dateCount][departmentCount];
		int[][] supplementNums = new int[dateCount][departmentCount];
		int[][] beOverdueNums = new int[dateCount][departmentCount];
		int[][] noDataNums = new int[dateCount][departmentCount];
		
		List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
		
		//各区学校数量
		key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		int[] regSchNums = new int[departmentCount];
		for (int i = 0; i < schOwnTypes.length; i++) {
			key = DataKeyConfig.areaSchoolData+schOwnTypes[i];
		    keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, "shanghai");
		    
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
			//field = "department_" + curDepartmentId;
			//if(schoolDataMap.containsKey(field)) {
			//	keyVal = schoolDataMap.get(field);
			regSchNums[i] = 0;
			if(CommonUtil.isNotEmpty(keyVal)) {
				regSchNums[i] = Integer.parseInt(keyVal);
			}
			//}
		}
		
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]  + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(platoonFeedTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				platoonFeedTotalMap = AppModConfig.getHdfsDataKey(dates[k], key);
			}
			if(platoonFeedTotalMap != null) {
				for(String curKey : platoonFeedTotalMap.keySet()) {
					for (int i = 0; i < schOwnTypes.length; i++) {
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
						fieldPrefix = "department_"+curDepartmentId + "_";
						int mealSchNum = 0, dishSchNum = 0, noDishSchNum = 0;
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 3)
							{
								if(curKeys[2].equalsIgnoreCase("供餐") && curKeys[3].equalsIgnoreCase("已排菜")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										mealSchNum = Integer.parseInt(keyVal);
										dishSchNum = mealSchNum;
									}
								}
								else if(curKeys[2].equalsIgnoreCase("供餐") && curKeys[3].equalsIgnoreCase("未排菜")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										mealSchNum = Integer.parseInt(keyVal);
										noDishSchNum = mealSchNum;
									}
								}
							}
						}
						totalMealSchNums[k][i] += mealSchNum;
						distDishSchNums[k][i] += dishSchNum;
						distNoDishSchNums[k][i] += noDishSchNum;
						
						//操作状态对应的数量
						fieldPrefix = fieldPrefix + "plastatus_";
						int standardNum = 0, supplementNum = 0, beOverdueNum = 0, noDataNum = 0;
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							
							if(curKeys.length >= 4)
							{
								if(curKeys[3].equalsIgnoreCase("1")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										standardNum = Integer.parseInt(keyVal);
									}
								}else if(curKeys[3].equalsIgnoreCase("2")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										supplementNum = Integer.parseInt(keyVal);
									}
								}else if(curKeys[3].equalsIgnoreCase("3")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										beOverdueNum = Integer.parseInt(keyVal);
									}
								}else if(curKeys[3].equalsIgnoreCase("4")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										noDataNum = Integer.parseInt(keyVal);
									}
								}
							}
						}
						
						standardNums[k][i] += standardNum;
						supplementNums[k][i] += supplementNum;
						beOverdueNums[k][i] += beOverdueNum;
						noDataNums[k][i] += noDataNum;
					}
				}
			}
			// 该日期各区学校排菜率
			for (int i = 0; i < departmentCount; i++) {
				String curDepartmentId = String.valueOf(schOwnTypes[i]);
				field = "department" + "_" + curDepartmentId;
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (departmentId != null) {
					if (!curDepartmentId.equals(departmentId))
						continue;
				}else if(departmentIdsList!=null && departmentIdsList.size() >0) {
					if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
						continue;
					}
					if(!departmentIdsList.contains(curDepartmentId)) {
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
				logger.info("日期：" + dates[k] + "，辖区名称：" + curDepartmentId + "，排菜学校数量：" + distDishSchNums[k][i]
						+ "，供餐学校总数：" + totalMealSchNums[k][i] + "，排菜率：" + distDishRates[i] + "，field = "
						+ field);
			}
		}
		float standardRate=0;
		for (int i = 0; i < departmentCount; i++) {
			if(regSchNums[i] <=0) {
				continue;
			}
			for (int k = 0; k < dates.length; k++) {
				String curDepartmentId = String.valueOf(schOwnTypes[i]);
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (departmentId != null) {
					if (!curDepartmentId.equals(departmentId))
						continue;
				}else if(departmentIdsList!=null && departmentIdsList.size() >0) {
					if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
						continue;
					}
					if(!departmentIdsList.contains(curDepartmentId)) {
						continue ;
					}
				}
				pdl = new PpDishList();
				pdl.setDishDate(dates[k].replaceAll("-", "/"));
				pdl.setDepartmentId(curDepartmentId);
				int totalDistSchNum = 0, distDishSchNum = 0, distNoDishSchNum = 0;
				totalDistSchNum = totalMealSchNums[k][i];
				distDishSchNum = distDishSchNums[k][i];
				distNoDishSchNum = distNoDishSchNums[k][i];		
				pdl.setRegSchNum(regSchNums[i]);
				pdl.setMealSchNum(totalDistSchNum);
				pdl.setDishSchNum(distDishSchNum);
				pdl.setNoDishSchNum(distNoDishSchNum);
				distDishRates[i] = 0;
				if(totalDistSchNum > 0) {
					distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
					BigDecimal bd = new BigDecimal(distDishRates[i]);
					distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distDishRates[i] > 100)
						distDishRates[i] = 100;
				}
				pdl.setDishRate(distDishRates[i]);
				

				//1 表示规范录入
				pdl.setStandardNum(standardNums[k][i]);
				//2 表示补录
				pdl.setSupplementNum(supplementNums[k][i]);
				//3 表示逾期补录
				pdl.setBeOverdueNum(beOverdueNums[k][i]);
				//4 表示无数据
				pdl.setNoDataNum(noDataNums[k][i]);
				
				standardRate = 0;
				if(totalDistSchNum > 0) {
					standardRate = 100 * ((float) pdl.getStandardNum() / (float) totalDistSchNum);
					BigDecimal bd = new BigDecimal(standardRate);
					standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (standardRate > 100)
						standardRate = 100;
				}
				pdl.setStandardRate(standardRate);
				
				ppDishList.add(pdl);
			}
		}
		//排序
    	//SortList<PpDishList> sortList = new SortList<PpDishList>();  
    	//sortList.Sort(ppDishList, methods1, sorts1, dataTypes1);
		//时戳
    	pdlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
    	// 分页
    	PageBean<PpDishList> pageBean = new PageBean<PpDishList>(ppDishList, curPageNum, pageSize);
    	PageInfo pageInfo = new PageInfo();
    	pageInfo.setPageTotal(pageBean.getTotalCount());
    	pageInfo.setCurPageNum(curPageNum);
    	pdlDto.setPageInfo(pageInfo);
    	// 设置数据
    	pdlDto.setPpDishList(pageBean.getCurPageData());
    	// 消息ID
    	pdlDto.setMsgId(AppModConfig.msgId);
    	AppModConfig.msgId++;
    	// 消息id小于0判断
    	AppModConfig.msgIdLessThan0Judge();

		return pdlDto;
	}
	
	// 项目点排菜列表函数
	private PpDishListDTO ppDishList(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, 
			int schSelMode, int subLevel, int compDep, String subDistName,
			String subLevels,String compDeps,String distNames,String departmentIds,DbHiveDishService dbHiveDishService,Db1Service db1Service) {
		PpDishListDTO pdlDto = new PpDishListDTO();
		
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();

		//今天之前的数据从hive库中获取
		if(days >= 2) {
			//筛选学校模式
			if(schSelMode == 0) {    //按主管部门
				pdlDto = ppDishListByCompDepFromHive(departmentId,distIdorSCName, dates, tedList, subLevel, compDep, subDistName,subLevels,compDeps,dbHiveDishService);
			}
			else if(schSelMode == 1) {  //按所在地
				pdlDto = ppDishListByLocalityFromHive(departmentId,distIdorSCName, dates, tedList, distNames, dbHiveDishService);		
			}else if(schSelMode == 2) {  //按管理部门
				List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),departmentIdsList, -1, -1);	
				pdlDto = ppDishListByDepartmentFromHive(departmentId, dates, deparmentList, departmentIds, dbHiveDishService);
			}    	
		}else {
			//筛选学校模式
			if(schSelMode == 0) {    //按主管部门
				pdlDto = ppDishListByCompDep(departmentId,distIdorSCName, dates, tedList, subLevel, compDep, subDistName,subLevels,compDeps);
			}
			else if(schSelMode == 1) {  //按所在地
				pdlDto = ppDishListByLocality(departmentId,distIdorSCName, dates, tedList,distNames);			
			}else if (schSelMode == 2) {//管理部门
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
				pdlDto = ppDishListByDepartment(departmentId, dates, departmentMap, schOwnTypes, departmentIds);
			}    	
		}
		return pdlDto;
	}

	//项目点排菜列表模型函数
	public PpDishListDTO appModFunc(String token, String startDate, String endDate, String schSelMode, 
			String subLevel, String compDep, String subDistName, String distName,String departmentId,
			String prefCity, String province, 
			String subLevels,String compDeps,String distNames,String departmentIds,
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service,DbHiveDishService dbHiveDishService) {
		PpDishListDTO pdlDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if (isRealData) { // 真实数据
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
				  	/*//UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);
				  	if(curSubLevel == -1)
				  		//curSubLevel = udpiDto.getSubLevelId();
				  	if(curCompDep == -1)
				  		//curCompDep = udpiDto.getCompDepId();
*/					// 项目点排菜列表函数
					pdlDto = ppDishList(departmentId,distIdorSCName, dates, tedList, curSchSelMode, curSubLevel, 
							curCompDep, subDistName,subLevels,compDeps,distNames,departmentIds,dbHiveDishService,db1Service);
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
					// 项目点排菜列表函数
					pdlDto = ppDishList(departmentId,distIdorSCName, dates, tedList, curSchSelMode, curSubLevel, curCompDep, 
							subDistName,subLevels,compDeps,distNames,departmentIds,dbHiveDishService,db1Service);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		} else { // 模拟数据
			//模拟数据函数
			pdlDto = SimuDataFunc();
		}

		return pdlDto;
	}
}
