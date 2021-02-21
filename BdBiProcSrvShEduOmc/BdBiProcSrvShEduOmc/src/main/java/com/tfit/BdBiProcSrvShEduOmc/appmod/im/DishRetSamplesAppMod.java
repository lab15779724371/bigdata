package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

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

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRetSamples;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRetSamplesDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//菜品留样列表应用模型
public class DishRetSamplesAppMod {
	private static final Logger logger = LogManager.getLogger(DishRetSamplesAppMod.class.getName());
	
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
	String[] repastDate_Array = {"2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04"};
	String[] distName_Array = {"徐汇区", "黄浦区", "闵行区"};
	int[] menuNum_Array = {2201, 3255, 4525};
	int[] rsMenuNum_Array = {1502, 2880, 3820};
	int[] noRsMenuNum_Array = {699, 375, 705};
	float[] rsRate_Array = {(float) 68.24, (float) 88.48, (float) 84.42};	
	
	//模拟数据函数
	private DishRetSamplesDTO SimuDataFunc() {
		DishRetSamplesDTO drsDto = new DishRetSamplesDTO();
		//时戳
		drsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//菜品留样列表模拟数据
		List<DishRetSamples> dishRetSamples = new ArrayList<>();
		//赋值
		for (int i = 0; i < repastDate_Array.length; i++) {
			DishRetSamples drs = new DishRetSamples();
			drs.setRepastDate(repastDate_Array[i]);
			drs.setDistName(distName_Array[i]);
			drs.setMenuNum(menuNum_Array[i]);
			drs.setRsMenuNum(rsMenuNum_Array[i]);
			drs.setNoRsMenuNum(noRsMenuNum_Array[i]);
			drs.setRsRate(rsRate_Array[i]);
			dishRetSamples.add(drs);
		}
		//设置数据
		drsDto.setDishRetSamples(dishRetSamples);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = repastDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		drsDto.setPageInfo(pageInfo);
		//消息ID
		drsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return drsDto;
	}
	
	// 菜品留样列表函数按主管部门
	private DishRetSamplesDTO dishRetSamplesByCompDep(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, int subLevel, int compDep, 
        String subDistName,String subLevels,String compDeps) {
		DishRetSamplesDTO drsDto = new DishRetSamplesDTO();
		List<DishRetSamples> dishRetSamples = new ArrayList<>();
		DishRetSamples drs = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> gcRetentiondishtotalMap = null, 
				platoonFeedTotalMap = null;
		int i, j, k, subLevelCount = 4, 
				compDepCount = 0, 
				maxCompDepCount = AppModConfig.compDepIdToNameMap3.size(), 
				dateCount = dates.length;
		int[][][] distDishSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				distRsSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				distNoRsSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				totalDishNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				distRsDishNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				distNoRsDishNums = new int[dateCount][subLevelCount][maxCompDepCount];
		float[][] distRsRates = new float[subLevelCount][maxCompDepCount];
		
		int[][][] standardNums = new int[dateCount][subLevelCount][maxCompDepCount]; 
		int[][][] supplementNums  = new int[dateCount][subLevelCount][maxCompDepCount]; 
		int[][][] beOverdueNums = new int[dateCount][subLevelCount][maxCompDepCount]; 
		int[][][] noDataNums = new int[dateCount][subLevelCount][maxCompDepCount];
		
		List<Object> subLevelList=CommonUtil.changeStringToList(subLevels);
		
		List<Object> compDepList=CommonUtil.changeStringToList(compDeps);
		
		// 当天各区菜品留样数量
		for (k = 0; k < dates.length; k++) {
			//注释原因：目前界面没有展示排菜学校，且30天之后的数据会迁移到hive库中
			//供餐学校数量
			/*key = dates[k] + "_platoonfeed-total";
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
							compDepCount = AppModConfig.compDepIdToNameMap3.size();
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
							int dishSchNum = 0;
							if (curKey.indexOf(fieldPrefix) == 0) {
								String[] curKeys = curKey.split("_");
								if(curKeys.length >= 6)
								{
									if(curKeys[4].equalsIgnoreCase("供餐") && curKeys[5].equalsIgnoreCase("已排菜")) {
										keyVal = platoonFeedTotalMap.get(curKey);
										if(keyVal != null) {
											dishSchNum = Integer.parseInt(keyVal);
										}
									}
								}
							}
							distDishSchNums[k][i][j] += dishSchNum;
						}
					}
				}
			}*/
			//菜品留样数量
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
			logger.info("redisService:"+redisService);
			gcRetentiondishtotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(gcRetentiondishtotalMap != null) {
				for(String curKey : gcRetentiondishtotalMap.keySet()) {
					
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
							// 区域留样和未留样学校数
							field = "school-" + fieldPrefix;
							if (curKey.indexOf(field) == 0) {
								if(curKeys.length >= 4)
								{
									if(curKeys[4].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
										keyVal = gcRetentiondishtotalMap.get(curKey);
										if(keyVal != null) {
											distRsSchNums[k][i][j] = Integer.parseInt(keyVal);
										}
									}
									else if(curKeys[4].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
										keyVal = gcRetentiondishtotalMap.get(curKey);
										if(keyVal != null) {
											distNoRsSchNums[k][i][j] = Integer.parseInt(keyVal);
										}
									}
								}
							}
							// 区域菜品留样和未留样数
							if (curKey.indexOf(fieldPrefix) == 0) {
								if(curKeys.length >= 2)
								{
									if(curKeys[4].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
										keyVal = gcRetentiondishtotalMap.get(curKey);
										if(keyVal != null) {
											distRsDishNums[k][i][j] = Integer.parseInt(keyVal);
											totalDishNums[k][i][j] += distRsDishNums[k][i][j];
										}
									}
									else if(curKeys[4].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
										keyVal = gcRetentiondishtotalMap.get(curKey);
										if(keyVal != null) {
											distNoRsDishNums[k][i][j] = Integer.parseInt(keyVal);
											totalDishNums[k][i][j] += distNoRsDishNums[k][i][j];
										}
									}
								}
							}
							
							//操作状态对应的数量
							field = "re-school-" + fieldPrefix + "_reservestatus_";
							if (curKey.indexOf(field) == 0) {
								if("1".equals(curKeys[5])) {
									standardNums[k][i][j] = 0;
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										standardNums[k][i][j] = Integer.parseInt(keyVal);
										if(standardNums[k][i][j] < 0)
											standardNums[k][i][j] = 0;
									}

								}else if ("2".equals(curKeys[5])) {
									supplementNums[k][i][j] = 0;
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										supplementNums[k][i][j] = Integer.parseInt(keyVal);
										if(supplementNums[k][i][j] < 0)
											supplementNums[k][i][j] = 0;
									}
								}else if ("3".equals(curKeys[5])) {
									beOverdueNums[k][i][j] = 0;
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										beOverdueNums[k][i][j] = Integer.parseInt(keyVal);
										if(beOverdueNums[k][i][j] < 0)
											beOverdueNums[k][i][j] = 0;
									}
								}else if ("4".equals(curKeys[5])) {
									noDataNums[k][i][j] = 0;
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										noDataNums[k][i][j] = Integer.parseInt(keyVal);
										if(noDataNums[k][i][j] < 0)
											noDataNums[k][i][j] = 0;
									}
								}
							}
							
						}
					}
				}
			}
			// 该日期各区留样率
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
					// 区域留样率
					if (totalDishNums[k][i][j] != 0) {
						distRsRates[i][j] = 100 * ((float) distRsDishNums[k][i][j] / (float) totalDishNums[k][i][j]);
						BigDecimal bd = new BigDecimal(distRsRates[i][j]);
						distRsRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (distRsRates[i][j] > 100) {
							distRsRates[i][j] = 100;
							distRsDishNums[k][i] = totalDishNums[k][i];
						}
					}
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + "，菜品数量：" + totalDishNums[k][i][j]
							+ "，已留样菜品数：" + distRsDishNums[k][i][j] + "，未留样菜品数：" + distNoRsDishNums[k][i][j] + "，排菜率：" + distRsRates[i][j] + "，field = "
							+ field);
				}
			}
		}
		
		int shouldRsSchNum=0;
		float schRsRate = 0;
		float schStandardRate = 0 ;
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
				for (k = 0; k < dates.length; k++) {
					drs = new DishRetSamples();
					drs.setRepastDate(dates[k].replaceAll("-", "/"));
					drs.setSubLevel(String.valueOf(i) + "," + AppModConfig.subLevelIdToNameMap.get(i));
					drs.setCompDep(compDepId + "," + compDepName);
					int totalDistDishSchNum = 0, totalDistRsSchNum = 0, totalDistNoRsSchNum = 0, totalDishNum = 0, distRsDishNum = 0, distNoRsDishNum = 0;
					totalDistDishSchNum = distDishSchNums[k][i][j];
					totalDistRsSchNum = distRsSchNums[k][i][j];
					totalDistNoRsSchNum = distNoRsSchNums[k][i][j];					
					totalDishNum = totalDishNums[k][i][j];
					distRsDishNum = distRsDishNums[k][i][j];
					distNoRsDishNum = distNoRsDishNums[k][i][j];
					
					drs.setDishSchNum(totalDistDishSchNum);
					drs.setRsSchNum(totalDistRsSchNum);
					drs.setNoRsSchNum(totalDistNoRsSchNum);
					drs.setMenuNum(totalDishNum);
					drs.setRsMenuNum(distRsDishNum);
					drs.setNoRsMenuNum(distNoRsDishNum);
					distRsRates[i][j] = 0;
					if(totalDishNum > 0) {
						distRsRates[i][j] = 100 * ((float) distRsDishNum / (float) totalDishNum);
						BigDecimal bd = new BigDecimal(distRsRates[i][j]);
						distRsRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (distRsRates[i][j] > 100)
							distRsRates[i][j] = 100;
					}
					drs.setRsRate(distRsRates[i][j]);
					
					shouldRsSchNum=0;
					schRsRate = 0;
					//应留样学校数 = 已留样学校 + 未留样学校
					shouldRsSchNum = drs.getRsSchNum() + drs.getNoRsSchNum();
					drs.setShouldRsSchNum(shouldRsSchNum);
					//学校留样率
					if(shouldRsSchNum > 0) {
						schRsRate = 100 * ((float) drs.getRsSchNum() / (float) shouldRsSchNum);
						BigDecimal bd = new BigDecimal(schRsRate);
						schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (schRsRate > 100)
							schRsRate = 100;
					}
					drs.setSchRsRate(schRsRate);
					
					//1 表示规范录入
					drs.setSchStandardNum(standardNums[k][i][j]);
					//2 表示补录
					drs.setSchSupplementNum(supplementNums[k][i][j]);
					//3 表示逾期补录
					drs.setSchBeOverdueNum(beOverdueNums[k][i][j]);
					//4 表示无数据
					drs.setSchNoDataNum(noDataNums[k][i][j]);
					
					//标准验收率
					schStandardRate = 0;
					if(shouldRsSchNum > 0) {
						schStandardRate = 100 * ((float) drs.getSchStandardNum() / shouldRsSchNum);
						BigDecimal bd = new BigDecimal(schStandardRate);
						schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (schStandardRate > 100)
							schStandardRate = 100;
					}
					drs.setSchStandardRate(schStandardRate);
					
					dishRetSamples.add(drs);
				}
			}
		}
		//排序
		SortList<DishRetSamples> sortList = new SortList<DishRetSamples>();
		sortList.Sort(dishRetSamples, methods0, sorts0, dataTypes0);
		// 设置返回数据
		drsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<DishRetSamples> pageBean = new PageBean<DishRetSamples>(dishRetSamples, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		drsDto.setPageInfo(pageInfo);
		// 设置数据
		drsDto.setDishRetSamples(pageBean.getCurPageData());
		// 消息ID
		drsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return drsDto;
	}
	
	// 菜品留样列表函数按所在地
	private DishRetSamplesDTO dishRetSamplesByLocality(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,String distNames) {
		DishRetSamplesDTO drsDto = new DishRetSamplesDTO();
		List<DishRetSamples> dishRetSamples = new ArrayList<>();
		DishRetSamples drs = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> gcRetentiondishtotalMap = null, platoonFeedTotalMap = null;
		int i, k,distCount = tedList.size(), dateCount = dates.length;
		int[][] distDishSchNums = new int[dateCount][distCount], distRsSchNums = new int[dateCount][distCount], distNoRsSchNums = new int[dateCount][distCount], totalDishNums = new int[dateCount][distCount], distRsDishNums = new int[dateCount][distCount], distNoRsDishNums = new int[dateCount][distCount];
		float[] distRsRates = new float[distCount];
		
		int[][] standardNums = new int[dateCount][distCount]; 
		int[][] supplementNums  = new int[dateCount][distCount]; 
		int[][] beOverdueNums = new int[dateCount][distCount]; 
		int[][] noDataNums = new int[dateCount][distCount];
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		// 当天各区菜品留样数量
		for (k = 0; k < dates.length; k++) {
			//供餐学校数量
			//注释原因：目前界面没有展示排菜学校，且30天之后的数据会迁移到hive库中
			/*key = dates[k] + "_platoonfeed-total";
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(platoonFeedTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				platoonFeedTotalMap = AppModConfig.getHdfsDataKey(dates[k], key);
			}
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
						fieldPrefix = curDistId + "_";
						int dishSchNum = 0;
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 3)
							{
								if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("已排菜")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										dishSchNum = Integer.parseInt(keyVal);
									}
								}
							}
						}
						distDishSchNums[k][i] += dishSchNum;
					}
				}
			}			*/
			//留样菜品数量
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
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
						
						String[] curKeys = curKey.split("_");
						// 区域学校留样和未留样数
						fieldPrefix = "school-area" + "_" + curDistId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							if(curKeys.length >= 3)
							{
								if(curKeys[2].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distRsSchNums[k][i] = Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[2].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsSchNums[k][i] = Integer.parseInt(keyVal);
									}
								}
							}
						}
						// 区域菜品留样和未留样数
						fieldPrefix = curDistId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							if(curKeys.length >= 2)
							{
								if(curKeys[1].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distRsDishNums[k][i] = Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[1].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsDishNums[k][i] = Integer.parseInt(keyVal);
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
						
						//操作状态对应的数量
						fieldPrefix = "re-school-area" + "_" + curDistId + "_reservestatus_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							if("1".equals(curKeys[3])) {
								standardNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									standardNums[k][i] = Integer.parseInt(keyVal);
									if(standardNums[k][i] < 0)
										standardNums[k][i] = 0;
								}

							}else if ("2".equals(curKeys[3])) {
								supplementNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									supplementNums[k][i] = Integer.parseInt(keyVal);
									if(supplementNums[k][i] < 0)
										supplementNums[k][i] = 0;
								}
							}else if ("3".equals(curKeys[3])) {
								beOverdueNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									beOverdueNums[k][i] = Integer.parseInt(keyVal);
									if(beOverdueNums[k][i] < 0)
										beOverdueNums[k][i] = 0;
								}
							}else if ("4".equals(curKeys[3])) {
								noDataNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									noDataNums[k][i] = Integer.parseInt(keyVal);
									if(noDataNums[k][i] < 0)
										noDataNums[k][i] = 0;
								}
							}
						}
					}
				}
			}
			// 该日期各区留样率
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
				// 区域留样率
				if (totalDishNums[k][i] != 0) {
					distRsRates[i] = 100 * ((float) distRsDishNums[k][i] / (float) totalDishNums[k][i]);
					BigDecimal bd = new BigDecimal(distRsRates[i]);
					distRsRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRates[i] > 100) {
						distRsRates[i] = 100;
						distRsDishNums[k][i] = totalDishNums[k][i];
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，菜品数量：" + totalDishNums[k][i]
						+ "，已留样菜品数：" + distRsDishNums[k][i] + "，未留样菜品数：" + distNoRsDishNums[k][i] + "，排菜率：" + distRsRates[i] + "，field = "
						+ field);
			}
		}
		
		int shouldRsSchNum=0;
		float schRsRate = 0;
		
		float schStandardRate = 0;
		
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
				drs = new DishRetSamples();
				drs.setRepastDate(dates[k].replaceAll("-", "/"));
				drs.setDistName(curTdd.getId());
				int totalDistDishSchNum = 0, totalDistRsSchNum = 0, totalDistNoRsSchNum = 0, totalDishNum = 0, distRsDishNum = 0, distNoRsDishNum = 0;	
				totalDistDishSchNum = distDishSchNums[k][i];
				totalDistRsSchNum = distRsSchNums[k][i];
				totalDistNoRsSchNum = distNoRsSchNums[k][i];				
				totalDishNum = totalDishNums[k][i];
				distRsDishNum = distRsDishNums[k][i];
				distNoRsDishNum = distNoRsDishNums[k][i];				
				drs.setDishSchNum(totalDistDishSchNum);
				drs.setRsSchNum(totalDistRsSchNum);
				drs.setNoRsSchNum(totalDistNoRsSchNum);				
				drs.setMenuNum(totalDishNum);
				drs.setRsMenuNum(distRsDishNum);
				drs.setNoRsMenuNum(distNoRsDishNum);
				distRsRates[i] = 0;
				if(totalDishNum > 0) {
					distRsRates[i] = 100 * ((float) distRsDishNum / (float) totalDishNum);
					BigDecimal bd = new BigDecimal(distRsRates[i]);
					distRsRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRates[i] > 100)
						distRsRates[i] = 100;
				}
				drs.setRsRate(distRsRates[i]);
				
				shouldRsSchNum=0;
				schRsRate = 0;
				//应留样学校数 = 已留样学校 + 未留样学校
				shouldRsSchNum = drs.getRsSchNum() + drs.getNoRsSchNum();
				drs.setShouldRsSchNum(shouldRsSchNum);
				//学校留样率
				if(shouldRsSchNum > 0) {
					schRsRate = 100 * ((float) drs.getRsSchNum() / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schRsRate);
					schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schRsRate > 100)
						schRsRate = 100;
				}
				drs.setSchRsRate(schRsRate);
				
				//1 表示规范录入
				drs.setSchStandardNum(standardNums[k][i]);
				//2 表示补录
				drs.setSchSupplementNum(supplementNums[k][i]);
				//3 表示逾期补录
				drs.setSchBeOverdueNum(beOverdueNums[k][i]);
				//4 表示无数据
				drs.setSchNoDataNum(noDataNums[k][i]);
				
				//标准验收率
				schStandardRate = 0;
				if(shouldRsSchNum > 0) {
					schStandardRate = 100 * ((float) drs.getSchStandardNum() / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schStandardRate);
					schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schStandardRate > 100)
						schStandardRate = 100;
				}
				drs.setSchStandardRate(schStandardRate);
				
				dishRetSamples.add(drs);
			}
		}
		// 设置返回数据
		drsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<DishRetSamples> pageBean = new PageBean<DishRetSamples>(dishRetSamples, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		drsDto.setPageInfo(pageInfo);
		// 设置数据
		drsDto.setDishRetSamples(pageBean.getCurPageData());
		// 消息ID
		drsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return drsDto;
	}
	
	// 菜品留样列表函数按所在地
	private DishRetSamplesDTO dishRetSamplesByDepartment(String departmentId,String[] dates, List<DepartmentObj> departmentList,String departmentIds) {
		DishRetSamplesDTO drsDto = new DishRetSamplesDTO();
		List<DishRetSamples> dishRetSamples = new ArrayList<>();
		DishRetSamples drs = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> gcRetentiondishtotalMap = null, platoonFeedTotalMap = null;
		int i, k,distCount = departmentList.size(), dateCount = dates.length;
		int[][] distDishSchNums = new int[dateCount][distCount], distRsSchNums = new int[dateCount][distCount], distNoRsSchNums = new int[dateCount][distCount], totalDishNums = new int[dateCount][distCount], distRsDishNums = new int[dateCount][distCount], distNoRsDishNums = new int[dateCount][distCount];
		float[] distRsRates = new float[distCount];
		
		int[][] standardNums = new int[dateCount][distCount]; 
		int[][] supplementNums  = new int[dateCount][distCount]; 
		int[][] beOverdueNums = new int[dateCount][distCount]; 
		int[][] noDataNums = new int[dateCount][distCount];
		
		List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
		// 当天各区菜品留样数量
		for (k = 0; k < dates.length; k++) {
			//留样菜品数量
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
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
						
						String[] curKeys = curKey.split("_");
						// 区域学校留样和未留样数
						fieldPrefix = "school-department" + "_" + curDepartmentId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							if(curKeys.length >= 3)
							{
								if(curKeys[2].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distRsSchNums[k][i] = Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[2].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsSchNums[k][i] = Integer.parseInt(keyVal);
									}
								}
							}
						}
						// 区域菜品留样和未留样数
						fieldPrefix = "department_"+curDepartmentId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							if(curKeys.length >= 2)
							{
								if(curKeys[2].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distRsDishNums[k][i] = Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[2].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsDishNums[k][i] = Integer.parseInt(keyVal);
									}
								}
								
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									totalDishNums[k][i] += Integer.parseInt(keyVal);
								}
							}
							
							
						}
						
						//操作状态对应的数量
						fieldPrefix = "re-school-department" + "_" + curDepartmentId + "_reservestatus_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							if("1".equals(curKeys[3])) {
								standardNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									standardNums[k][i] = Integer.parseInt(keyVal);
									if(standardNums[k][i] < 0)
										standardNums[k][i] = 0;
								}

							}else if ("2".equals(curKeys[3])) {
								supplementNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									supplementNums[k][i] = Integer.parseInt(keyVal);
									if(supplementNums[k][i] < 0)
										supplementNums[k][i] = 0;
								}
							}else if ("3".equals(curKeys[3])) {
								beOverdueNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									beOverdueNums[k][i] = Integer.parseInt(keyVal);
									if(beOverdueNums[k][i] < 0)
										beOverdueNums[k][i] = 0;
								}
							}else if ("4".equals(curKeys[3])) {
								noDataNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									noDataNums[k][i] = Integer.parseInt(keyVal);
									if(noDataNums[k][i] < 0)
										noDataNums[k][i] = 0;
								}
							}
						}
					}
				}
			}
			// 该日期各区留样率
			for (i = 0; i < distCount; i++) {
				DepartmentObj departmentObj = departmentList.get(i);
				String curDepartmentId = departmentObj.getDepartmentId();
				field = "department" + "_" + curDepartmentId;
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (departmentId != null) {
					if (!curDepartmentId.equals(departmentId))
						continue;
				}
				if(departmentIdList!=null && departmentIdList.size() >0) {
					if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
						continue;
					}
					if(!departmentIdList.contains(curDepartmentId)) {
						continue ;
					}
				}
				// 区域留样率
				if (totalDishNums[k][i] != 0) {
					distRsRates[i] = 100 * ((float) distRsDishNums[k][i] / (float) totalDishNums[k][i]);
					BigDecimal bd = new BigDecimal(distRsRates[i]);
					distRsRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRates[i] > 100) {
						distRsRates[i] = 100;
						distRsDishNums[k][i] = totalDishNums[k][i];
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + departmentObj.getDepartmentName() + "，菜品数量：" + totalDishNums[k][i]
						+ "，已留样菜品数：" + distRsDishNums[k][i] + "，未留样菜品数：" + distNoRsDishNums[k][i] + "，排菜率：" + distRsRates[i] + "，field = "
						+ field);
			}
		}
		
		int shouldRsSchNum=0;
		float schRsRate = 0;
		
		float schStandardRate = 0;
		
		for (i = 0; i < distCount; i++) {
			DepartmentObj departmentObj = departmentList.get(i);
			String curDepartmentId = departmentObj.getDepartmentId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (departmentId != null) {
				if (!curDepartmentId.equals(departmentId))
					continue;
			}
			if(departmentIdList!=null && departmentIdList.size() >0) {
				if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
					continue;
				}
				if(!departmentIdList.contains(curDepartmentId)) {
					continue ;
				}
			}
			for (k = 0; k < dates.length; k++) {
				drs = new DishRetSamples();
				drs.setRepastDate(dates[k].replaceAll("-", "/"));
				drs.setDepartmentId(departmentObj.getDepartmentId());
				drs.setDepartmentName(departmentObj.getDepartmentName());
				int totalDistDishSchNum = 0, totalDistRsSchNum = 0, totalDistNoRsSchNum = 0, totalDishNum = 0, distRsDishNum = 0, distNoRsDishNum = 0;	
				totalDistDishSchNum = distDishSchNums[k][i];
				totalDistRsSchNum = distRsSchNums[k][i];
				totalDistNoRsSchNum = distNoRsSchNums[k][i];				
				totalDishNum = totalDishNums[k][i];
				distRsDishNum = distRsDishNums[k][i];
				distNoRsDishNum = distNoRsDishNums[k][i];				
				drs.setDishSchNum(totalDistDishSchNum);
				drs.setRsSchNum(totalDistRsSchNum);
				drs.setNoRsSchNum(totalDistNoRsSchNum);				
				drs.setMenuNum(totalDishNum);
				drs.setRsMenuNum(distRsDishNum);
				drs.setNoRsMenuNum(distNoRsDishNum);
				distRsRates[i] = 0;
				if(totalDishNum > 0) {
					distRsRates[i] = 100 * ((float) distRsDishNum / (float) totalDishNum);
					BigDecimal bd = new BigDecimal(distRsRates[i]);
					distRsRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRates[i] > 100)
						distRsRates[i] = 100;
				}
				drs.setRsRate(distRsRates[i]);
				
				shouldRsSchNum=0;
				schRsRate = 0;
				//应留样学校数 = 已留样学校 + 未留样学校
				shouldRsSchNum = drs.getRsSchNum() + drs.getNoRsSchNum();
				drs.setShouldRsSchNum(shouldRsSchNum);
				//学校留样率
				if(shouldRsSchNum > 0) {
					schRsRate = 100 * ((float) drs.getRsSchNum() / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schRsRate);
					schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schRsRate > 100)
						schRsRate = 100;
				}
				drs.setSchRsRate(schRsRate);
				
				//1 表示规范录入
				drs.setSchStandardNum(standardNums[k][i]);
				//2 表示补录
				drs.setSchSupplementNum(supplementNums[k][i]);
				//3 表示逾期补录
				drs.setSchBeOverdueNum(beOverdueNums[k][i]);
				//4 表示无数据
				drs.setSchNoDataNum(noDataNums[k][i]);
				
				//标准验收率
				schStandardRate = 0;
				if(shouldRsSchNum > 0) {
					schStandardRate = 100 * ((float) drs.getSchStandardNum() / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schStandardRate);
					schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schStandardRate > 100)
						schStandardRate = 100;
				}
				drs.setSchStandardRate(schStandardRate);
				
				dishRetSamples.add(drs);
			}
		}
		// 设置返回数据
		drsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<DishRetSamples> pageBean = new PageBean<DishRetSamples>(dishRetSamples, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		drsDto.setPageInfo(pageInfo);
		// 设置数据
		drsDto.setDishRetSamples(pageBean.getCurPageData());
		// 消息ID
		drsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return drsDto;
	}
	
	// 菜品留样列表函数
	private DishRetSamplesDTO dishRetSamples(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, int schSelMode, int subLevel, 
			int compDep, String subDistName,String subLevels,String compDeps,String distNames,String departmentIds,Db1Service db1Service) {
		DishRetSamplesDTO drsDto = new DishRetSamplesDTO();
		//筛选学校模式
		if(schSelMode == 0) {    //按主管部门
			drsDto = dishRetSamplesByCompDep(departmentId,distIdorSCName, dates, tedList, subLevel, compDep, subDistName
					,subLevels,compDeps);
		}
		else if(schSelMode == 1) {  //按所在地
			drsDto = dishRetSamplesByLocality(departmentId,distIdorSCName, dates, tedList,distNames);
		}else if (schSelMode == 2) {//按管理部门
			List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
			DepartmentObj departmentObj = new DepartmentObj();
			if(CommonUtil.isNotEmpty(departmentId)) {
				departmentObj.setDepartmentId(departmentId);
			}
			List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,departmentIdsList, -1, -1);	
			drsDto = dishRetSamplesByDepartment(departmentId, dates, deparmentList, departmentIds);
		}    	

		return drsDto;
	}
	
	// 菜品留样列表模型函数
	public DishRetSamplesDTO appModFunc(String token, String startDate, String endDate, String schSelMode, String subLevel, String compDep, 
			String subDistName, String distName, String prefCity, String province,String subLevels,String compDeps,String distNames,
			String departmentIds,
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service) {
		
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		
		DishRetSamplesDTO drsDto = null;
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
			String departmentId = null;
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
					// 菜品留样列表函数
					drsDto = dishRetSamples(departmentId,distIdorSCName, dates, tedList, curSchSelMode, curSubLevel, curCompDep, subDistName,
							subLevels,compDeps,distNames,departmentIds,db1Service);
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
					// 菜品留样列表函数
					drsDto = dishRetSamples(departmentId,distIdorSCName, dates, tedList, curSchSelMode, curSubLevel, curCompDep, subDistName,
							subLevels,compDeps,distNames,departmentIds,db1Service);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}			
		}
		else {    //模拟数据
			//模拟数据函数
			drsDto = SimuDataFunc();
		}		

		return drsDto;
	}
}