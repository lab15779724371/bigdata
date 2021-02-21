package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOpts;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsCommon;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//配货计划操作列表应用模型
public class GsPlanOptsAppMod {
	private static final Logger logger = LogManager.getLogger(GsPlanOptsAppMod.class.getName());
	
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
	String[] distrDate_Array = {"2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04"}; //, "合计"};
	String[] distName_Array = {"徐汇区", "黄浦区", "闵行区", "静安区", "松江区", "长宁区", "浦东新区", "宝山区", "嘉定区", "青浦区", "奉贤区", "杨浦区", "金山区", "普陀区", "虹口区", "崇明区"}; //, "全市"};
	int[] totalGsPlanNum_Array = {2231, 3251, 4658, 5661, 6502, 7502, 7802, 6505, 4860, 6512, 6601, 3610, 6502, 4702, 6581, 3658}; //, 165850};
	int[] assignGsPlanNum_Array = {1502, 2880, 3820, 5066, 6021, 6582, 7025, 5102, 4035, 5768, 6021, 3326, 6105, 4220, 6022, 3105}; //, 150150};
	float[] assignRate_Array = {(float) 67.32, (float) 88.59, (float) 82.01, (float) 89.49, (float) 92.60, (float) 87.74, (float) 90.04, (float) 78.43, (float) 83.02, (float) 88.57, (float) 91.21, (float) 92.13, (float) 93.89, (float) 89.75, (float) 91.51, (float) 84.88}; //, (float) 90.50};
	int[] dispGsPlanNum_Array = {729, 371, 838, 595, 481, 920, 777, 1403, 825, 744, 580, 284, 397, 482, 559, 553}; //, 145600};
	float[] dispRate_Array = {(float) 67.32, (float) 88.59, (float) 82.01, (float) 89.49, (float) 92.60, (float) 87.74, (float) 90.04, (float) 78.43, (float) 83.02, (float) 88.57, (float) 91.21, (float) 92.13, (float) 93.89, (float) 89.75, (float) 91.51, (float) 84.88}; //, (float) 87.79};
	int[] acceptGsPlanNum_Array = {1178, 2556, 3496, 4742, 5697, 6258, 6701, 4778, 3711, 5444, 5697, 3002, 5781, 3896, 5698, 2781}; //, 140250};
	float[] acceptRate_Array = {(float) 52.80, (float) 78.62, (float) 75.05, (float) 83.77, (float) 87.62, (float) 83.42, (float) 85.89, (float) 73.45, (float) 76.36, (float) 83.60, (float) 86.31, (float) 83.16, (float) 88.91, (float) 82.86, (float) 86.58, (float) 76.03}; //, (float) 84.56};
	
	//模拟数据函数
	private GsPlanOptsDTO SimuDataFunc() {
		GsPlanOptsDTO gpoDto = new GsPlanOptsDTO();
		//时戳
		gpoDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//配货计划操作列表模拟数据
		List<GsPlanOpts> gsPlanOpts = new ArrayList<>();
		//赋值
		for (int i = 0; i < distrDate_Array.length; i++) {
			GsPlanOpts gpo = new GsPlanOpts();
			gpo.setDistrDate(distrDate_Array[i]);
			gpo.setDistName(distName_Array[i]);
			gpo.setTotalGsPlanNum(totalGsPlanNum_Array[i]);
			gpo.setAssignGsPlanNum(assignGsPlanNum_Array[i]);
			gpo.setAssignRate(assignRate_Array[i]);
			gpo.setDispGsPlanNum(dispGsPlanNum_Array[i]);
			gpo.setDispRate(dispRate_Array[i]);
			gpo.setAcceptGsPlanNum(acceptGsPlanNum_Array[i]);
			gpo.setAcceptRate(acceptRate_Array[i]);
			gsPlanOpts.add(gpo);
		}
		//设置数据
		gpoDto.setGsPlanOpts(gsPlanOpts);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = distrDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		gpoDto.setPageInfo(pageInfo);
		//消息ID
		gpoDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gpoDto;
	}
	
	// 配货计划操作列表函数 按主管部门
	private GsPlanOptsDTO gsPlanOptsByCompDep(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, int subLevel, 
			int compDep, String subDistName,String subLevels,String compDeps) {
		GsPlanOptsDTO gpoDto = new GsPlanOptsDTO();
		List<GsPlanOpts> gsPlanOpts = new ArrayList<>();
		GsPlanOpts gpo = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> distributionTotalMap = null, platoonFeedTotalMap = null;
		int i, j, k, l, 
		subLevelCount = 4, 
		compDepCount = 0, 
		maxCompDepCount = AppModConfig.compDepIdToNameMap3.size(),
		dateCount = dates.length;
		int[][][] distDishSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				conSchNums = new int[dateCount][subLevelCount][maxCompDepCount],
				acceptSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				noAcceptSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				totalGsPlanNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				assignGsPlanNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				dispGsPlanNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				acceptGsPlanNums = new int[dateCount][subLevelCount][maxCompDepCount];
		 //应验收学校
		 int[][][] totalAcceptSchNums = new int[dateCount][subLevelCount][maxCompDepCount];
		 float[][] assignRates = new float[subLevelCount][maxCompDepCount], 
				dispRates = new float[subLevelCount][maxCompDepCount], 
				acceptRates = new float[subLevelCount][maxCompDepCount];
		 
		int[][][] standardNums = new int[dateCount][subLevelCount][maxCompDepCount]; 
		int[][][] supplementNums  = new int[dateCount][subLevelCount][maxCompDepCount]; 
		int[][][] beOverdueNums = new int[dateCount][subLevelCount][maxCompDepCount]; 
		int[][][] noDataNums = new int[dateCount][subLevelCount][maxCompDepCount];
			
		List<Object> subLevelList=CommonUtil.changeStringToList(subLevels);
		List<Object> compDepList=CommonUtil.changeStringToList(compDeps);
			
		// 当天各区配货计划总数量
		for(k = 0; k < dates.length; k++) {
			//供餐学校数量
			//注释原因：目前界面没有展示排菜学校，且30天之后的数据会迁移到hive库中
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
			//配货计划数量
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionTotalMap != null) {
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
						//已确认学校
						conSchNums[k][i][j] = 0;
						for(l = 0; l < 4; l++) {
							field = "school-" + fieldPrefix + "_" + "status" + "_" + l;
							keyVal = distributionTotalMap.get(field);
							if(keyVal != null) {
								int curConSchNum = Integer.parseInt(keyVal);
								if(curConSchNum < 0)
									curConSchNum = 0;
								conSchNums[k][i][j] += curConSchNum;
							}
						}
						
						//应验收学校总数
						totalAcceptSchNums[k][i][j] = 0;
						for(l = -2; l < 4; l++) {
							field = "school-" + fieldPrefix + "_" + "status" + "_" + l;
							keyVal = distributionTotalMap.get(field);
							if(keyVal != null) {
								int curConSchNum = Integer.parseInt(keyVal);
								if(curConSchNum < 0)
									curConSchNum = 0;
								totalAcceptSchNums[k][i][j] += curConSchNum;
							}
						}
						
						//已验收学校
						field = "school-" + fieldPrefix + "_" + "status" + "_3";
						acceptSchNums[k][i][j] = 0;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							acceptSchNums[k][i][j] = Integer.parseInt(keyVal);
							if(acceptSchNums[k][i][j] < 0)
								acceptSchNums[k][i][j] = 0;
						}
						//未验收学校
						noAcceptSchNums[k][i][j] = totalAcceptSchNums[k][i][j] - acceptSchNums[k][i][j];
						
						// 区域配货计划总数
						/*field = fieldPrefix;
						totalGsPlanNums[k][i][j] = 0;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							totalGsPlanNums[k][i][j] = Integer.parseInt(keyVal);
							if(totalGsPlanNums[k][i][j] < 0)
								totalGsPlanNums[k][i][j] = 0;
						}*/
						for(l = -2; l < 4; l++) {
							field = fieldPrefix + "_" + "status" + "_" + l;
							keyVal = distributionTotalMap.get(field);
							if(keyVal != null) {
								int curConSchNum = Integer.parseInt(keyVal);
								if(curConSchNum < 0)
									curConSchNum = 0;
								totalGsPlanNums[k][i][j] += curConSchNum;
							}
						}
						
						// 已指派数
						assignGsPlanNums[k][i][j] = 0;
						for(l = 0; l < 4; l++) {
							field = fieldPrefix + "_" + "status" + "_" + l;
							keyVal = distributionTotalMap.get(field);
							if(keyVal != null) {
								int curAssignGsPlanNum = Integer.parseInt(keyVal);
								if(curAssignGsPlanNum < 0)
									curAssignGsPlanNum = 0;
								assignGsPlanNums[k][i][j] += curAssignGsPlanNum;
							}
						}
						// 已配送数
						dispGsPlanNums[k][i][j] = 0;
						for(l = 2; l < 4; l++) {
							field = fieldPrefix + "_" + "status" + "_" + l;
							keyVal = distributionTotalMap.get(field);
							if(keyVal != null) {
								int dispGsPlanNum = Integer.parseInt(keyVal);
								if(dispGsPlanNum < 0)
									dispGsPlanNum = 0;
								dispGsPlanNums[k][i][j] += dispGsPlanNum;
							}
						}
						// 已验收数
						field = fieldPrefix + "_" + "status" + "_3";
						acceptGsPlanNums[k][i][j] = 0;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							acceptGsPlanNums[k][i][j] = Integer.parseInt(keyVal);
							if(acceptGsPlanNums[k][i][j] < 0)
								acceptGsPlanNums[k][i][j] = 0;
						}
						
						//操作状态对应的数量
						field = "dis-school-"+fieldPrefix + "_disstatus_"+"1";
						standardNums[k][i][j] = 0;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							standardNums[k][i][j] = Integer.parseInt(keyVal);
							if(standardNums[k][i][j] < 0)
								standardNums[k][i][j] = 0;
						}
						
						
						field = "dis-school-"+fieldPrefix + "_disstatus_"+"2";
						supplementNums[k][i][j] = 0;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							supplementNums[k][i][j] = Integer.parseInt(keyVal);
							if(supplementNums[k][i][j] < 0)
								supplementNums[k][i][j] = 0;
						}
						
						field = "dis-school-"+fieldPrefix + "_disstatus_"+"3";
						beOverdueNums[k][i][j] = 0;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							beOverdueNums[k][i][j] = Integer.parseInt(keyVal);
							if(beOverdueNums[k][i][j] < 0)
								beOverdueNums[k][i][j] = 0;
						}
						
						field = "dis-school-"+fieldPrefix + "_disstatus_"+"4";
						noDataNums[k][i][j] = 0;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							noDataNums[k][i][j] = Integer.parseInt(keyVal);
							if(noDataNums[k][i][j] < 0)
								noDataNums[k][i][j] = 0;
						}
						
					}
				}
			}
			// 该日期各区配货计划
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
					// 区域配货计划
					if (totalGsPlanNums[k][i][j] != 0) {
						//指派率
						assignRates[i][j] = 100 * ((float) assignGsPlanNums[k][i][j] / (float) totalGsPlanNums[k][i][j]);
						BigDecimal bd = new BigDecimal(assignRates[i][j]);
						assignRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (assignRates[i][j] > 100) {
							assignRates[i][j] = 100;
						}
						//配送率
						dispRates[i][j] = 100 * ((float) dispGsPlanNums[k][i][j] / (float) totalGsPlanNums[k][i][j]);
						bd = new BigDecimal(dispRates[i][j]);
						dispRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (dispRates[i][j] > 100) {
							dispRates[i][j] = 100;
						}
						//验收率
						acceptRates[i][j] = 100 * ((float) acceptGsPlanNums[k][i][j] / (float) totalGsPlanNums[k][i][j]);
						bd = new BigDecimal(acceptRates[i][j]);
						acceptRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (acceptRates[i][j] > 100) {
							acceptRates[i][j] = 100;
						}
					}
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + compDepName + "，配货计划总数：" + totalGsPlanNums[k][i][j] + "，已指派数：" + assignGsPlanNums[k][i][j] + "，指派率：" + assignRates[i][j] + "，配送数：" + dispGsPlanNums[k][i][j] + "，配送率：" + dispRates[i][j] + "，验收数：" + acceptGsPlanNums[k][i][j] + "，验收率：" + acceptRates[i][j]);
				}
			}
		}
		float schAcceptRate = 0 ;

		int standardNum = 0;
		int supplementNum = 0;
		int beOverdueNum = 0;
		int noDataNum = 0;
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
					gpo = new GsPlanOpts();
					gpo.setDistrDate(dates[k].replaceAll("-", "/"));
					gpo.setSubLevel(String.valueOf(i) + "," + AppModConfig.subLevelIdToNameMap.get(i));
					gpo.setCompDep(compDepId + "," + compDepName);
					int totalDistDishSchNum = 0, totalConSchNum = 0, totalNoAcceptSchNum = 0, totalAcceptSchNum, totalGsPlanNum = 0, assignGsPlanNum = 0, dispGsPlanNum = 0, acceptGsPlanNum = 0;
					
					totalDistDishSchNum = distDishSchNums[k][i][j];
					totalConSchNum = conSchNums[k][i][j];
					totalNoAcceptSchNum = noAcceptSchNums[k][i][j];
					totalAcceptSchNum = acceptSchNums[k][i][j];	
					totalGsPlanNum = totalGsPlanNums[k][i][j];
					assignGsPlanNum = assignGsPlanNums[k][i][j];
					dispGsPlanNum = dispGsPlanNums[k][i][j];
					acceptGsPlanNum = acceptGsPlanNums[k][i][j];
					
					standardNum = standardNums[k][i][j];
					supplementNum = supplementNums[k][i][j];
					beOverdueNum = beOverdueNums[k][i][j];
					noDataNum = noDataNums[k][i][j];
					
					//已排菜学校
					gpo.setDishSchNum(totalDistDishSchNum);
					//已确认学校
					gpo.setConSchNum(totalConSchNum);
					
					//应验收学校
					gpo.setShouldAcceptSchNum(totalNoAcceptSchNum + totalAcceptSchNum);
					//未验收学校
					gpo.setNoAcceptSchNum(totalNoAcceptSchNum);
					//已验收学校
					gpo.setAcceptSchNum(totalAcceptSchNum);
					
					//学校验收率
					schAcceptRate = 0;
					if(gpo.getShouldAcceptSchNum() > 0) {
						schAcceptRate = 100 * ((float) totalAcceptSchNum / (float) gpo.getShouldAcceptSchNum());
						BigDecimal bd = new BigDecimal(schAcceptRate);
						schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (schAcceptRate > 100)
							schAcceptRate = 100;
					}
					gpo.setSchAcceptRate(schAcceptRate);
					
					//配送计划总数
					gpo.setTotalGsPlanNum(totalGsPlanNum);
					//指派数量及指派率
					gpo.setAssignGsPlanNum(assignGsPlanNum);
					assignRates[i][j] = 0;
					if(totalGsPlanNum > 0) {
						assignRates[i][j] = 100 * ((float) assignGsPlanNum / (float) totalGsPlanNum);
						BigDecimal bd = new BigDecimal(assignRates[i][j]);
						assignRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (assignRates[i][j] > 100)
							assignRates[i][j] = 100;
					}
					gpo.setAssignRate(assignRates[i][j]);
					//配送数量及配送率
					gpo.setDispGsPlanNum(dispGsPlanNum);
					dispRates[i][j] = 0;
					if(totalGsPlanNum > 0) {
						dispRates[i][j] = 100 * ((float) dispGsPlanNum / (float) totalGsPlanNum);
						BigDecimal bd = new BigDecimal(dispRates[i][j]);
						dispRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (dispRates[i][j] > 100)
							dispRates[i][j] = 100;
					}
					gpo.setDispRate(dispRates[i][j]);
					//验收数量及验收率
					gpo.setAcceptGsPlanNum(acceptGsPlanNum);
					acceptRates[i][j] = 0;
					if(totalGsPlanNum > 0) {
						acceptRates[i][j] = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
						BigDecimal bd = new BigDecimal(acceptRates[i][j]);
						acceptRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (acceptRates[i][j] > 100)
							acceptRates[i][j] = 100;
					}
					gpo.setAcceptRate(acceptRates[i][j]);
					//未验收学校
					gpo.setNoAcceptGsPlanNum(gpo.getTotalGsPlanNum() - gpo.getAcceptGsPlanNum());
					
					//1 表示规范录入
					gpo.setSchStandardNum(standardNum);
					//2 表示补录
					gpo.setSchSupplementNum(supplementNum);
					//3 表示逾期补录
					gpo.setSchBeOverdueNum(beOverdueNum);
					//4 表示无数据
					gpo.setSchNoDataNum(noDataNum);
					
					//标准验收率
					schStandardRate = 0;
					if(gpo.getShouldAcceptSchNum() > 0) {
						schStandardRate = 100 * ((float) gpo.getSchStandardNum() / (float) gpo.getShouldAcceptSchNum());
						BigDecimal bd = new BigDecimal(schStandardRate);
						schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (schStandardRate > 100)
							schStandardRate = 100;
					}
					gpo.setSchStandardRate(schStandardRate);
					
					gsPlanOpts.add(gpo);
				}
			}
		}
		//排序
		SortList<GsPlanOpts> sortList = new SortList<GsPlanOpts>();
		sortList.Sort(gsPlanOpts, methods0, sorts0, dataTypes0);
		// 设置返回数据
		gpoDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<GsPlanOpts> pageBean = new PageBean<GsPlanOpts>(gsPlanOpts, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		gpoDto.setPageInfo(pageInfo);
		//设置数据
		gpoDto.setGsPlanOpts(pageBean.getCurPageData());
		//消息ID
		gpoDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gpoDto;
	}
	
	// 配货计划操作列表函数 按主管部门
	private GsPlanOptsDTO gsPlanOptsByCompDepFromHive(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, int subLevel, 
			int compDep, String subDistName,String subLevels,String compDeps,DbHiveGsService dbHiveGsService) {
		GsPlanOptsDTO gpoDto = new GsPlanOptsDTO();
		List<GsPlanOpts> gsPlanOpts = new ArrayList<>();
		GsPlanOpts gpo = null;
		int i, j, k, 
		subLevelCount = 4, 
		compDepCount = 0, 
		maxCompDepCount = AppModConfig.compDepIdToNameMap3.size();
		 float[][] assignRates = new float[subLevelCount][maxCompDepCount], 
				dispRates = new float[subLevelCount][maxCompDepCount], 
				acceptRates = new float[subLevelCount][maxCompDepCount];
		 
		List<Object> subLevelList=CommonUtil.changeStringToList(subLevels);
		List<Object> compDepList=CommonUtil.changeStringToList(compDeps);
			
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
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
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
				-1, -1, null, null,departmentId,null, 3);
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
				
				if(schDishCommon.getDisSealStatus() !=null && !"-1".equals(schDishCommon.getDisSealStatus())) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if("1".equals(schDishCommon.getDisSealStatus())) {
						standardNumMap.put(key, 
								(standardNumMap.get(key)==null?0:standardNumMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
					}else if("2".equals(schDishCommon.getDisSealStatus())) {
						supplementNumMap.put(key, 
								(supplementNumMap.get(key)==null?0:supplementNumMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
					}else if("3".equals(schDishCommon.getDisSealStatus())) {
						beOverdueNumMap.put(key, 
								(beOverdueNumMap.get(key)==null?0:beOverdueNumMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
					}else if("4".equals(schDishCommon.getDisSealStatus())) {
						noDataNumMap.put(key, 
								(noDataNumMap.get(key)==null?0:noDataNumMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
					}
				}
			}
		}
		
		//标准验收率
		float schStandardRate = 0;
		float schAcceptRate = 0 ;
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
					gpo = new GsPlanOpts();
					gpo.setDistrDate(dates[k].replaceAll("-", "/"));
					gpo.setSubLevel(String.valueOf(i) + "," + AppModConfig.subLevelIdToNameMap.get(i));
					gpo.setCompDep(compDepId + "," + compDepName);
					int totalDistDishSchNum = 0, totalConSchNum = 0;
					
					//转换，hive库中 区属的子项是数字，slaveMap中为中文
					String dataKey = gpo.getDistrDate()+"_"+i+"_" + compDepId;
					int totalNoAcceptSchNum = noAcceptSchNumsMap.get(dataKey)==null?0:noAcceptSchNumsMap.get(dataKey);
					int totalAcceptSchNum = acceptSchNumsMap.get(dataKey)==null?0:acceptSchNumsMap.get(dataKey);
					int totalGsPlanNum = totalGsPlanNumMap.get(dataKey)==null?0:totalGsPlanNumMap.get(dataKey);
					int acceptGsPlanNum = acceptGsPlanNumMap.get(dataKey)==null?0:acceptGsPlanNumMap.get(dataKey);
					int  noAcceptGsPlanNum= noAcceptGsPlanNumMap.get(dataKey)==null?0:noAcceptGsPlanNumMap.get(dataKey);
					int  assignGsPlanNum= assignGsPlanNumsMap.get(dataKey)==null?0:assignGsPlanNumsMap.get(dataKey);
					int  dispGsPlanNum= assignGsPlanNumsMap.get(dataKey)==null?0:assignGsPlanNumsMap.get(dataKey);
					
					
					//已排菜学校
					gpo.setDishSchNum(totalDistDishSchNum);
					//已确认学校
					gpo.setConSchNum(totalConSchNum);
					
					//应验收学校
					gpo.setShouldAcceptSchNum(totalNoAcceptSchNum + totalAcceptSchNum);
					//未验收学校
					gpo.setNoAcceptSchNum(totalNoAcceptSchNum);
					//已验收学校
					gpo.setAcceptSchNum(totalAcceptSchNum);
					
					//学校验收率
					schAcceptRate = 0;
					if(gpo.getShouldAcceptSchNum() > 0) {
						schAcceptRate = 100 * ((float) totalAcceptSchNum / (float) gpo.getShouldAcceptSchNum());
						BigDecimal bd = new BigDecimal(schAcceptRate);
						schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (schAcceptRate > 100)
							schAcceptRate = 100;
					}
					gpo.setSchAcceptRate(schAcceptRate);
					
					//配送计划总数
					gpo.setTotalGsPlanNum(totalGsPlanNum);
					//指派数量及指派率
					gpo.setAssignGsPlanNum(assignGsPlanNum);
					assignRates[i][j] = 0;
					if(totalGsPlanNum > 0) {
						assignRates[i][j] = 100 * ((float) assignGsPlanNum / (float) totalGsPlanNum);
						BigDecimal bd = new BigDecimal(assignRates[i][j]);
						assignRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (assignRates[i][j] > 100)
							assignRates[i][j] = 100;
					}
					gpo.setAssignRate(assignRates[i][j]);
					//配送数量及配送率
					gpo.setDispGsPlanNum(dispGsPlanNum);
					dispRates[i][j] = 0;
					if(totalGsPlanNum > 0) {
						dispRates[i][j] = 100 * ((float) dispGsPlanNum / (float) totalGsPlanNum);
						BigDecimal bd = new BigDecimal(dispRates[i][j]);
						dispRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (dispRates[i][j] > 100)
							dispRates[i][j] = 100;
					}
					gpo.setDispRate(dispRates[i][j]);
					//验收数量及验收率
					gpo.setAcceptGsPlanNum(acceptGsPlanNum);
					acceptRates[i][j] = 0;
					if(totalGsPlanNum > 0) {
						acceptRates[i][j] = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
						BigDecimal bd = new BigDecimal(acceptRates[i][j]);
						acceptRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (acceptRates[i][j] > 100)
							acceptRates[i][j] = 100;
					}
					gpo.setAcceptRate(acceptRates[i][j]);
					//未验收学校
					gpo.setNoAcceptGsPlanNum(gpo.getTotalGsPlanNum() - gpo.getAcceptGsPlanNum());
					

					//1 表示规范录入
					gpo.setSchStandardNum(standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey));
					//2 表示补录
					gpo.setSchSupplementNum(supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey));
					//3 表示逾期补录
					gpo.setSchBeOverdueNum(beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey));
					//4 表示无数据
					gpo.setSchNoDataNum(noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey));
					
					//标准验收率
					schStandardRate = 0;
					if(gpo.getShouldAcceptSchNum() > 0) {
						schStandardRate = 100 * ((float) gpo.getSchStandardNum() / (float) gpo.getShouldAcceptSchNum());
						BigDecimal bd = new BigDecimal(schStandardRate);
						schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (schStandardRate > 100)
							schStandardRate = 100;
					}
					gpo.setSchStandardRate(schStandardRate);
					
					
					gsPlanOpts.add(gpo);
				}
			}
		}
		//排序
		SortList<GsPlanOpts> sortList = new SortList<GsPlanOpts>();
		sortList.Sort(gsPlanOpts, methods0, sorts0, dataTypes0);
		// 设置返回数据
		gpoDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<GsPlanOpts> pageBean = new PageBean<GsPlanOpts>(gsPlanOpts, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		gpoDto.setPageInfo(pageInfo);
		//设置数据
		gpoDto.setGsPlanOpts(pageBean.getCurPageData());
		//消息ID
		gpoDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gpoDto;
	}
	
	// 配货计划操作列表函数 按所在地
	private GsPlanOptsDTO gsPlanOptsByLocalityFromHive(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList,String distNames,
			DbHiveGsService dbHiveGsService) {
		GsPlanOptsDTO gpoDto = new GsPlanOptsDTO();
		List<GsPlanOpts> gsPlanOpts = new ArrayList<>();
		GsPlanOpts gpo = null;
		// 当天排菜学校总数
		int distCount = tddList.size();
		float[] assignRates = new float[distCount], dispRates = new float[distCount], acceptRates = new float[distCount];
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		
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
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
		List<SchGsCommon> dishList = new ArrayList<>();
		dishList = dbHiveGsService.getGsList(DataKeyConfig.talbeLedgerMasterTotalD,listYearMonth, startDate, endDateAddOne, distIdorSCName, null, 
				-1, -1, null, null,departmentId,null, 0);
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
				if(schDishCommon.getDisSealStatus() !=null && !"-1".equals(schDishCommon.getDisSealStatus())) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if("1".equals(schDishCommon.getDisSealStatus())) {
						standardNumMap.put(dataKey, 
								(standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
					}else if("2".equals(schDishCommon.getDisSealStatus())) {
						supplementNumMap.put(dataKey, 
								(supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
					}else if("3".equals(schDishCommon.getDisSealStatus())) {
						beOverdueNumMap.put(dataKey, 
								(beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
					}else if("4".equals(schDishCommon.getDisSealStatus())) {
						noDataNumMap.put(dataKey, 
								(noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
					}
				}
			}
		}
		float schAcceptRate = 0;
		//标准验收率
		float schStandardRate = 0;
		for(int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tddList.get(i);
			String curDistId = curTdd.getId();
			//判断是否按区域获取配货计划数据（distIdorSCName为空表示按省或直辖市级别获取数据）
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
			for(int k = 0; k < dates.length; k++) {
				gpo = new GsPlanOpts();
				gpo.setDistrDate(dates[k].replaceAll("-", "/"));
				gpo.setDistName(curTdd.getId());
				int totalDistDishSchNum = 0, 
						totalConMatSchNum = 0;
				String dataKey = gpo.getDistrDate() + "_" + curDistId;
				
				int totalNoAcceptSchNum = noAcceptSchNumsMap.get(dataKey)==null?0:noAcceptSchNumsMap.get(dataKey);
				int totalAcceptSchNum = acceptSchNumsMap.get(dataKey)==null?0:acceptSchNumsMap.get(dataKey);
				int totalGsPlanNum = totalGsPlanNumMap.get(dataKey)==null?0:totalGsPlanNumMap.get(dataKey);
				int acceptGsPlanNum = acceptGsPlanNumMap.get(dataKey)==null?0:acceptGsPlanNumMap.get(dataKey);
				int  noAcceptGsPlanNum= noAcceptGsPlanNumMap.get(dataKey)==null?0:noAcceptGsPlanNumMap.get(dataKey);
				int  assignGsPlanNum= assignGsPlanNumsMap.get(dataKey)==null?0:assignGsPlanNumsMap.get(dataKey);
				int  dispGsPlanNum= assignGsPlanNumsMap.get(dataKey)==null?0:assignGsPlanNumsMap.get(dataKey);
				
				//已排菜学校
				gpo.setDishSchNum(totalDistDishSchNum);
				//已确认学校
				gpo.setConSchNum(totalConMatSchNum);
				//应验收学校
				gpo.setShouldAcceptSchNum(totalNoAcceptSchNum + totalAcceptSchNum);
				//未验收学校
				gpo.setNoAcceptSchNum(totalNoAcceptSchNum);
				//已验收学校
				gpo.setAcceptSchNum(totalAcceptSchNum);
				//学校验收率
				schAcceptRate = 0;
				if(gpo.getShouldAcceptSchNum() > 0) {
					schAcceptRate = 100 * ((float) totalAcceptSchNum / (float) gpo.getShouldAcceptSchNum());
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100)
						schAcceptRate = 100;
				}
				gpo.setSchAcceptRate(schAcceptRate);
				
				//配送计划总数
				gpo.setTotalGsPlanNum(totalGsPlanNum);
				//指派数量及指派率
				gpo.setAssignGsPlanNum(assignGsPlanNum);
				assignRates[i] = 0;
				if(totalGsPlanNum > 0) {
					assignRates[i] = 100 * ((float) assignGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(assignRates[i]);
					assignRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (assignRates[i] > 100)
						assignRates[i] = 100;
				}
				gpo.setAssignRate(assignRates[i]);
				//配送数量及配送率
				gpo.setDispGsPlanNum(dispGsPlanNum);
				dispRates[i] = 0;
				if(totalGsPlanNum > 0) {
					dispRates[i] = 100 * ((float) dispGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(dispRates[i]);
					dispRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (dispRates[i] > 100)
						dispRates[i] = 100;
				}
				gpo.setDispRate(dispRates[i]);
				//未验收数
				gpo.setNoAcceptGsPlanNum(totalGsPlanNum-acceptGsPlanNum);
				//验收数量及验收率
				gpo.setAcceptGsPlanNum(acceptGsPlanNum);
				acceptRates[i] = 0;
				if(totalGsPlanNum > 0) {
					acceptRates[i] = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRates[i]);
					acceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (acceptRates[i] > 100)
						acceptRates[i] = 100;
				}
				gpo.setAcceptRate(acceptRates[i]);
				
				//1 表示规范录入
				gpo.setSchStandardNum(standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey));
				//2 表示补录
				gpo.setSchSupplementNum(supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey));
				//3 表示逾期补录
				gpo.setSchBeOverdueNum(beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey));
				//4 表示无数据
				gpo.setSchNoDataNum(noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey));
				
				//标准验收率
				schStandardRate = 0;
				if(gpo.getShouldAcceptSchNum() > 0) {
					schStandardRate = 100 * ((float) gpo.getSchStandardNum() / (float) gpo.getShouldAcceptSchNum());
					BigDecimal bd = new BigDecimal(schStandardRate);
					schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schStandardRate > 100)
						schStandardRate = 100;
				}
				gpo.setSchStandardRate(schStandardRate);
				
				gsPlanOpts.add(gpo);
			}
		}
		//排序
		SortList<GsPlanOpts> sortList = new SortList<GsPlanOpts>();
		sortList.Sort(gsPlanOpts, methods1, sorts1, dataTypes1);
		// 设置返回数据
		gpoDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<GsPlanOpts> pageBean = new PageBean<GsPlanOpts>(gsPlanOpts, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		gpoDto.setPageInfo(pageInfo);
		//设置数据
		gpoDto.setGsPlanOpts(pageBean.getCurPageData());
		//消息ID
		gpoDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return gpoDto;
	}
	
	// 配货计划操作列表函数 按所在地
	private GsPlanOptsDTO gsPlanOptsByLocality(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList,String distNames) {
		GsPlanOptsDTO gpoDto = new GsPlanOptsDTO();
		List<GsPlanOpts> gsPlanOpts = new ArrayList<>();
		GsPlanOpts gpo = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> distributionTotalMap = null, platoonFeedTotalMap = null, useMaterialPlanMap = null;
		int distCount = tddList.size(), dateCount = dates.length;
		int[][] distDishSchNums = new int[dateCount][distCount], conSchNums = new int[dateCount][distCount],
				noAcceptSchNums = new int[dateCount][distCount], acceptSchNums = new int[dateCount][distCount],
				totalGsPlanNums = new int[dateCount][distCount], assignGsPlanNums = new int[dateCount][distCount], 
				dispGsPlanNums = new int[dateCount][distCount], acceptGsPlanNums = new int[dateCount][distCount],
				conMatSchNums = new int[dateCount][distCount];
		int[][] totalAcceptSchNums = new int[dateCount][distCount];
		float[] assignRates = new float[distCount], dispRates = new float[distCount], acceptRates = new float[distCount];
		
		int[][] standardNums = new int[dateCount][distCount]; 
		int[][] supplementNums  = new int[dateCount][distCount]; 
		int[][] beOverdueNums = new int[dateCount][distCount]; 
		int[][] noDataNums = new int[dateCount][distCount];
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		
		// 当天各区配货计划总数量
		for(int k = 0; k < dates.length; k++) {
			//供餐学校数量
			//注释原因：目前界面没有展示排菜学校，且30天之后的数据会迁移到hive库中
			/*key = dates[k] + "_platoonfeed-total";
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(platoonFeedTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				platoonFeedTotalMap = AppModConfig.getHdfsDataKey(dates[k], key);
			}
			if(platoonFeedTotalMap != null) {
				for(String curKey : platoonFeedTotalMap.keySet()) {
					for (int i = 0; i < tddList.size(); i++) {
						TEduDistrictDo curTdd = tddList.get(i);
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
			// 用料计划数量
			//注释原因：目前界面没有展示排菜学校，且30天之后的数据会迁移到hive库中
			/*key = dates[k] + "_useMaterialPlanTotal";
			useMaterialPlanMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (useMaterialPlanMap != null) {
				for(int i = 0; i < distCount; i++) {
					TEduDistrictDo curTdd = tddList.get(i);
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
					fieldPrefix = "school-area" + "_" + curDistId;
					// 已确认用料学校
					field = fieldPrefix + "_" + "status" + "_" + 2;
					keyVal = useMaterialPlanMap.get(field);
					if (keyVal != null)
						conMatSchNums[k][i] = Integer.parseInt(keyVal);
				}
			}*/
			//配货计划数量
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			
			
			if (distributionTotalMap != null) {
				for (int i = 0; i < tddList.size(); i++) {
					TEduDistrictDo curTdd = tddList.get(i);
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
					//已确认学校
					conSchNums[k][i] = 0;
					for(int j = 2; j < 4; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							conSchNums[k][i] += curConSchNum;
						}
					}
					
					//应验收学校总数
					totalAcceptSchNums[k][i] = 0;
					for(int j = -2; j < 4; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							totalAcceptSchNums[k][i] += curConSchNum;
						}
					}
					
					//已验收学校
					field = "school-area" + "_" + curDistId + "_" + "status" + "_3";
					acceptSchNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						acceptSchNums[k][i] = Integer.parseInt(keyVal);
						if(acceptSchNums[k][i] < 0)
							acceptSchNums[k][i] = 0;
					}
					//未验收学校
					noAcceptSchNums[k][i] = totalAcceptSchNums[k][i] - acceptSchNums[k][i];
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
					for(int j = 0; j < 4; j++) {
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
					for(int j = 2; j < 4; j++) {
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
					
					//操作状态对应的数量
					field = "dis-school-area" + "_" + curDistId + "_disstatus_"+"1";
					standardNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						standardNums[k][i] = Integer.parseInt(keyVal);
						if(standardNums[k][i] < 0)
							standardNums[k][i] = 0;
					}
					
					
					field = "dis-school-area" + "_" + curDistId + "_disstatus_"+"2";
					supplementNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						supplementNums[k][i] = Integer.parseInt(keyVal);
						if(supplementNums[k][i] < 0)
							supplementNums[k][i] = 0;
					}
					
					field = "dis-school-area" + "_" + curDistId + "_disstatus_"+"3";
					beOverdueNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						beOverdueNums[k][i] = Integer.parseInt(keyVal);
						if(beOverdueNums[k][i] < 0)
							beOverdueNums[k][i] = 0;
					}
					
					field = "dis-school-area" + "_" + curDistId + "_disstatus_"+"4";
					noDataNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						noDataNums[k][i] = Integer.parseInt(keyVal);
						if(noDataNums[k][i] < 0)
							noDataNums[k][i] = 0;
					}
				}
			}
			// 该日期各区配货计划
			for (int i = 0; i < distCount; i++) {
				TEduDistrictDo curTdd = tddList.get(i);
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
				// 区域配货计划
				if (totalGsPlanNums[k][i] != 0) {
					//指派率
					assignRates[i] = 100 * ((float) assignGsPlanNums[k][i] / (float) totalGsPlanNums[k][i]);
					BigDecimal bd = new BigDecimal(assignRates[i]);
					assignRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (assignRates[i] > 100) {
						assignRates[i] = 100;
					}
					//配送率
					dispRates[i] = 100 * ((float) dispGsPlanNums[k][i] / (float) totalGsPlanNums[k][i]);
					bd = new BigDecimal(dispRates[i]);
					dispRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (dispRates[i] > 100) {
						dispRates[i] = 100;
					}
					//验收率
					acceptRates[i] = 100 * ((float) acceptGsPlanNums[k][i] / (float) totalGsPlanNums[k][i]);
					bd = new BigDecimal(acceptRates[i]);
					acceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (acceptRates[i] > 100) {
						acceptRates[i] = 100;
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，配货计划总数：" + totalGsPlanNums[k][i] + "，已指派数：" + assignGsPlanNums[k][i] + "，指派率：" + assignRates[i] + "，配送数：" + dispGsPlanNums[k][i] + "，配送率：" + dispRates[i] + "，验收数：" + acceptGsPlanNums[k][i] + "，验收率：" + acceptRates[i]);
			}
		}
		float schAcceptRate = 0;
		
		int standardNum = 0;
		int supplementNum = 0;
		int beOverdueNum = 0;
		int noDataNum = 0;
		float schStandardRate = 0;
		for(int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tddList.get(i);
			String curDistId = curTdd.getId();
			//判断是否按区域获取配货计划数据（distIdorSCName为空表示按省或直辖市级别获取数据）
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
			for(int k = 0; k < dates.length; k++) {
				gpo = new GsPlanOpts();
				gpo.setDistrDate(dates[k].replaceAll("-", "/"));
				gpo.setDistName(curTdd.getId());
				int totalDistDishSchNum = 0, totalConMatSchNum = 0, totalConSchNum = 0, totalNoAcceptSchNum = 0, totalAcceptSchNum = 0, totalGsPlanNum = 0, assignGsPlanNum = 0, dispGsPlanNum = 0, acceptGsPlanNum = 0;
				standardNum = 0;
				supplementNum = 0;
				beOverdueNum = 0;
				noDataNum = 0;
				totalDistDishSchNum = distDishSchNums[k][i];
				totalConMatSchNum = conMatSchNums[k][i];
				totalConSchNum = conSchNums[k][i];
				totalNoAcceptSchNum = noAcceptSchNums[k][i];
				totalAcceptSchNum = acceptSchNums[k][i];				
				totalGsPlanNum = totalGsPlanNums[k][i];
				assignGsPlanNum = assignGsPlanNums[k][i];
				dispGsPlanNum = dispGsPlanNums[k][i];
				acceptGsPlanNum = acceptGsPlanNums[k][i];
				
				standardNum += standardNums[k][i];
				supplementNum += supplementNums[k][i];
				beOverdueNum += beOverdueNums[k][i];
				noDataNum += noDataNums[k][i];
				
				//已排菜学校
				gpo.setDishSchNum(totalDistDishSchNum);
				//已确认学校
				gpo.setConSchNum(totalConMatSchNum);
				//应验收学校
				gpo.setShouldAcceptSchNum(totalNoAcceptSchNum + totalAcceptSchNum);
				//未验收学校
				gpo.setNoAcceptSchNum(totalNoAcceptSchNum);
				//已验收学校
				gpo.setAcceptSchNum(totalAcceptSchNum);
				//学校验收率
				schAcceptRate = 0;
				if(gpo.getShouldAcceptSchNum() > 0) {
					schAcceptRate = 100 * ((float) totalAcceptSchNum / (float) gpo.getShouldAcceptSchNum());
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100)
						schAcceptRate = 100;
				}
				gpo.setSchAcceptRate(schAcceptRate);
				
				//配送计划总数
				gpo.setTotalGsPlanNum(totalGsPlanNum);
				//指派数量及指派率
				gpo.setAssignGsPlanNum(assignGsPlanNum);
				assignRates[i] = 0;
				if(totalGsPlanNum > 0) {
					assignRates[i] = 100 * ((float) assignGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(assignRates[i]);
					assignRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (assignRates[i] > 100)
						assignRates[i] = 100;
				}
				gpo.setAssignRate(assignRates[i]);
				//配送数量及配送率
				gpo.setDispGsPlanNum(dispGsPlanNum);
				dispRates[i] = 0;
				if(totalGsPlanNum > 0) {
					dispRates[i] = 100 * ((float) dispGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(dispRates[i]);
					dispRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (dispRates[i] > 100)
						dispRates[i] = 100;
				}
				gpo.setDispRate(dispRates[i]);
				//未验收数
				gpo.setNoAcceptGsPlanNum(totalGsPlanNum-acceptGsPlanNum);
				//验收数量及验收率
				gpo.setAcceptGsPlanNum(acceptGsPlanNum);
				acceptRates[i] = 0;
				if(totalGsPlanNum > 0) {
					acceptRates[i] = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRates[i]);
					acceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (acceptRates[i] > 100)
						acceptRates[i] = 100;
				}
				gpo.setAcceptRate(acceptRates[i]);
				

				//1 表示规范录入
				gpo.setSchStandardNum(standardNum);
				//2 表示补录
				gpo.setSchSupplementNum(supplementNum);
				//3 表示逾期补录
				gpo.setSchBeOverdueNum(beOverdueNum);
				//4 表示无数据
				gpo.setSchNoDataNum(noDataNum);
				
				//标准验收率
				schStandardRate = 0;
				if(gpo.getShouldAcceptSchNum() > 0) {
					schStandardRate = 100 * ((float) gpo.getSchStandardNum() / (float) gpo.getShouldAcceptSchNum());
					BigDecimal bd = new BigDecimal(schStandardRate);
					schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schStandardRate > 100)
						schStandardRate = 100;
				}
				gpo.setSchStandardRate(schStandardRate);
				
				gsPlanOpts.add(gpo);
			}
		}
		//排序
		SortList<GsPlanOpts> sortList = new SortList<GsPlanOpts>();
		sortList.Sort(gsPlanOpts, methods1, sorts1, dataTypes1);
		// 设置返回数据
		gpoDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<GsPlanOpts> pageBean = new PageBean<GsPlanOpts>(gsPlanOpts, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		gpoDto.setPageInfo(pageInfo);
		//设置数据
		gpoDto.setGsPlanOpts(pageBean.getCurPageData());
		//消息ID
		gpoDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return gpoDto;
	}
	
	// 配货计划操作列表函数 按所在地
	private GsPlanOptsDTO gsPlanOptsByDepartment(String departmentId,String[] dates,List<DepartmentObj> deparmentList) {
		GsPlanOptsDTO gpoDto = new GsPlanOptsDTO();
		List<GsPlanOpts> gsPlanOpts = new ArrayList<>();
		GsPlanOpts gpo = null;
		String key = "", keyVal = "", field = "";
		// 当天排菜学校总数
		Map<String, String> distributionTotalMap = null, platoonFeedTotalMap = null, useMaterialPlanMap = null;
		int distCount = deparmentList.size(), dateCount = dates.length;
		int[][] distDishSchNums = new int[dateCount][distCount], conSchNums = new int[dateCount][distCount],
				noAcceptSchNums = new int[dateCount][distCount], acceptSchNums = new int[dateCount][distCount],
				totalGsPlanNums = new int[dateCount][distCount], assignGsPlanNums = new int[dateCount][distCount], 
				dispGsPlanNums = new int[dateCount][distCount], acceptGsPlanNums = new int[dateCount][distCount],
				conMatSchNums = new int[dateCount][distCount];
		int[][] totalAcceptSchNums = new int[dateCount][distCount];
		float[] assignRates = new float[distCount], dispRates = new float[distCount], acceptRates = new float[distCount];
		
		int[][] standardNums = new int[dateCount][distCount]; 
		int[][] supplementNums  = new int[dateCount][distCount]; 
		int[][] beOverdueNums = new int[dateCount][distCount]; 
		int[][] noDataNums = new int[dateCount][distCount];
		
		// 当天各区配货计划总数量
		for(int k = 0; k < dates.length; k++) {
			//供餐学校数量
			//注释原因：目前界面没有展示排菜学校，且30天之后的数据会迁移到hive库中
			/*key = dates[k] + "_platoonfeed-total";
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(platoonFeedTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				platoonFeedTotalMap = AppModConfig.getHdfsDataKey(dates[k], key);
			}
			if(platoonFeedTotalMap != null) {
				for(String curKey : platoonFeedTotalMap.keySet()) {
					for (int i = 0; i < tddList.size(); i++) {
						TEduDistrictDo curTdd = tddList.get(i);
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
			// 用料计划数量
			//注释原因：目前界面没有展示排菜学校，且30天之后的数据会迁移到hive库中
			/*key = dates[k] + "_useMaterialPlanTotal";
			useMaterialPlanMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (useMaterialPlanMap != null) {
				for(int i = 0; i < distCount; i++) {
					TEduDistrictDo curTdd = tddList.get(i);
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
					fieldPrefix = "school-area" + "_" + curDistId;
					// 已确认用料学校
					field = fieldPrefix + "_" + "status" + "_" + 2;
					keyVal = useMaterialPlanMap.get(field);
					if (keyVal != null)
						conMatSchNums[k][i] = Integer.parseInt(keyVal);
				}
			}*/
			//配货计划数量
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			
			
			if (distributionTotalMap != null) {
				for (int i = 0; i < deparmentList.size(); i++) {
					DepartmentObj departmentObj = deparmentList.get(i);
					String curDepartmentId = departmentObj.getDepartmentId();
					//已确认学校
					conSchNums[k][i] = 0;
					for(int j = 2; j < 4; j++) {
						field = "school-department" + "_" + curDepartmentId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							conSchNums[k][i] += curConSchNum;
						}
					}
					
					//应验收学校总数
					totalAcceptSchNums[k][i] = 0;
					for(int j = -2; j < 4; j++) {
						field = "school-department" + "_" + curDepartmentId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							totalAcceptSchNums[k][i] += curConSchNum;
						}
					}
					
					//已验收学校
					field = "school-department" + "_" + curDepartmentId + "_" + "status" + "_3";
					acceptSchNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						acceptSchNums[k][i] = Integer.parseInt(keyVal);
						if(acceptSchNums[k][i] < 0)
							acceptSchNums[k][i] = 0;
					}
					//未验收学校
					noAcceptSchNums[k][i] = totalAcceptSchNums[k][i] - acceptSchNums[k][i];
					// 区域配货计划总数
					/*field = "department" + "_" + curDepartmentId;
					totalGsPlanNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						totalGsPlanNums[k][i] = Integer.parseInt(keyVal);
						if(totalGsPlanNums[k][i] < 0)
							totalGsPlanNums[k][i] = 0;
					}*/
					
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
					
					// 已指派数
					assignGsPlanNums[k][i] = 0;
					for(int j = 0; j < 4; j++) {
						field = "department" + "_" + curDepartmentId + "_" + "status" + "_" + j;
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
					for(int j = 2; j < 4; j++) {
						field = "department" + "_" + curDepartmentId + "_" + "status" + "_" + j;
						keyVal = distributionTotalMap.get(field);
						if(keyVal != null) {
							int dispGsPlanNum = Integer.parseInt(keyVal);
							if(dispGsPlanNum < 0)
								dispGsPlanNum = 0;
							dispGsPlanNums[k][i] += dispGsPlanNum;
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
					
					//操作状态对应的数量
					field = "dis-school-department" + "_" + curDepartmentId + "_disstatus_"+"1";
					standardNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						standardNums[k][i] = Integer.parseInt(keyVal);
						if(standardNums[k][i] < 0)
							standardNums[k][i] = 0;
					}
					
					
					field = "dis-school-department" + "_" + curDepartmentId + "_disstatus_"+"2";
					supplementNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						supplementNums[k][i] = Integer.parseInt(keyVal);
						if(supplementNums[k][i] < 0)
							supplementNums[k][i] = 0;
					}
					
					field = "dis-school-department" + "_" + curDepartmentId + "_disstatus_"+"3";
					beOverdueNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						beOverdueNums[k][i] = Integer.parseInt(keyVal);
						if(beOverdueNums[k][i] < 0)
							beOverdueNums[k][i] = 0;
					}
					
					field = "dis-school-department" + "_" + curDepartmentId + "_disstatus_"+"4";
					noDataNums[k][i] = 0;
					keyVal = distributionTotalMap.get(field);
					if(keyVal != null) {
						noDataNums[k][i] = Integer.parseInt(keyVal);
						if(noDataNums[k][i] < 0)
							noDataNums[k][i] = 0;
					}
				}
			}
			// 该日期各区配货计划
			for (int i = 0; i < distCount; i++) {
				DepartmentObj departmentObj = deparmentList.get(i);
				// 区域配货计划
				if (totalGsPlanNums[k][i] != 0) {
					//指派率
					assignRates[i] = 100 * ((float) assignGsPlanNums[k][i] / (float) totalGsPlanNums[k][i]);
					BigDecimal bd = new BigDecimal(assignRates[i]);
					assignRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (assignRates[i] > 100) {
						assignRates[i] = 100;
					}
					//配送率
					dispRates[i] = 100 * ((float) dispGsPlanNums[k][i] / (float) totalGsPlanNums[k][i]);
					bd = new BigDecimal(dispRates[i]);
					dispRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (dispRates[i] > 100) {
						dispRates[i] = 100;
					}
					//验收率
					acceptRates[i] = 100 * ((float) acceptGsPlanNums[k][i] / (float) totalGsPlanNums[k][i]);
					bd = new BigDecimal(acceptRates[i]);
					acceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (acceptRates[i] > 100) {
						acceptRates[i] = 100;
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + departmentObj.getDepartmentName() + "，配货计划总数：" + totalGsPlanNums[k][i] + "，已指派数：" + assignGsPlanNums[k][i] + "，指派率：" + assignRates[i] + "，配送数：" + dispGsPlanNums[k][i] + "，配送率：" + dispRates[i] + "，验收数：" + acceptGsPlanNums[k][i] + "，验收率：" + acceptRates[i]);
			}
		}
		float schAcceptRate = 0;
		int standardNum = 0;
		int supplementNum = 0;
		int beOverdueNum = 0;
		int noDataNum = 0;
		float schStandardRate = 0;
		for(int i = 0; i < distCount; i++) {
			DepartmentObj departmentObj = deparmentList.get(i);
			for(int k = 0; k < dates.length; k++) {
				gpo = new GsPlanOpts();
				gpo.setDistrDate(dates[k].replaceAll("-", "/"));
				gpo.setDepartmentId(departmentObj.getDepartmentId());
				int totalDistDishSchNum = 0, totalConMatSchNum = 0, totalConSchNum = 0, totalNoAcceptSchNum = 0, totalAcceptSchNum = 0, totalGsPlanNum = 0, assignGsPlanNum = 0, dispGsPlanNum = 0, acceptGsPlanNum = 0;
				totalDistDishSchNum = distDishSchNums[k][i];
				totalConMatSchNum = conMatSchNums[k][i];
				totalConSchNum = conSchNums[k][i];
				totalNoAcceptSchNum = noAcceptSchNums[k][i];
				totalAcceptSchNum = acceptSchNums[k][i];				
				totalGsPlanNum = totalGsPlanNums[k][i];
				assignGsPlanNum = assignGsPlanNums[k][i];
				dispGsPlanNum = dispGsPlanNums[k][i];
				acceptGsPlanNum = acceptGsPlanNums[k][i];

				standardNum = standardNums[k][i];
				supplementNum = supplementNums[k][i];
				beOverdueNum = beOverdueNums[k][i];
				noDataNum = noDataNums[k][i];
				
				//已排菜学校
				gpo.setDishSchNum(totalDistDishSchNum);
				//已确认学校
				gpo.setConSchNum(totalConMatSchNum);
				//应验收学校
				gpo.setShouldAcceptSchNum(totalNoAcceptSchNum + totalAcceptSchNum);
				//未验收学校
				gpo.setNoAcceptSchNum(totalNoAcceptSchNum);
				//已验收学校
				gpo.setAcceptSchNum(totalAcceptSchNum);
				//学校验收率
				schAcceptRate = 0;
				if(gpo.getShouldAcceptSchNum() > 0) {
					schAcceptRate = 100 * ((float) totalAcceptSchNum / (float) gpo.getShouldAcceptSchNum());
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100)
						schAcceptRate = 100;
				}
				gpo.setSchAcceptRate(schAcceptRate);
				
				//配送计划总数
				gpo.setTotalGsPlanNum(totalGsPlanNum);
				//指派数量及指派率
				gpo.setAssignGsPlanNum(assignGsPlanNum);
				assignRates[i] = 0;
				if(totalGsPlanNum > 0) {
					assignRates[i] = 100 * ((float) assignGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(assignRates[i]);
					assignRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (assignRates[i] > 100)
						assignRates[i] = 100;
				}
				gpo.setAssignRate(assignRates[i]);
				//配送数量及配送率
				gpo.setDispGsPlanNum(dispGsPlanNum);
				dispRates[i] = 0;
				if(totalGsPlanNum > 0) {
					dispRates[i] = 100 * ((float) dispGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(dispRates[i]);
					dispRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (dispRates[i] > 100)
						dispRates[i] = 100;
				}
				gpo.setDispRate(dispRates[i]);
				//未验收数
				gpo.setNoAcceptGsPlanNum(totalGsPlanNum-acceptGsPlanNum);
				//验收数量及验收率
				gpo.setAcceptGsPlanNum(acceptGsPlanNum);
				acceptRates[i] = 0;
				if(totalGsPlanNum > 0) {
					acceptRates[i] = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRates[i]);
					acceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (acceptRates[i] > 100)
						acceptRates[i] = 100;
				}
				gpo.setAcceptRate(acceptRates[i]);
				
				//1 表示规范录入
				gpo.setSchStandardNum(standardNum);
				//2 表示补录
				gpo.setSchSupplementNum(supplementNum);
				//3 表示逾期补录
				gpo.setSchBeOverdueNum(beOverdueNum);
				//4 表示无数据
				gpo.setSchNoDataNum(noDataNum);
				
				//标准验收率
				schStandardRate = 0;
				if(gpo.getShouldAcceptSchNum() > 0) {
					schStandardRate = 100 * ((float) gpo.getSchStandardNum() / (float) gpo.getShouldAcceptSchNum());
					BigDecimal bd = new BigDecimal(schStandardRate);
					schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schStandardRate > 100)
						schStandardRate = 100;
				}
				gpo.setSchStandardRate(schStandardRate);
				
				gsPlanOpts.add(gpo);
			}
		}
		//排序
		//SortList<GsPlanOpts> sortList = new SortList<GsPlanOpts>();
		//sortList.Sort(gsPlanOpts, methods1, sorts1, dataTypes1);
		// 设置返回数据
		gpoDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<GsPlanOpts> pageBean = new PageBean<GsPlanOpts>(gsPlanOpts, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		gpoDto.setPageInfo(pageInfo);
		//设置数据
		gpoDto.setGsPlanOpts(pageBean.getCurPageData());
		//消息ID
		gpoDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return gpoDto;
	}
	
	// 配货计划操作列表函数 按所在地
	private GsPlanOptsDTO gsPlanOptsByDepartmentFromHive(String departmentId,String distIdorSCName, String[] dates, List<DepartmentObj> departmentList,String departmentIds,
			DbHiveGsService dbHiveGsService) {
		GsPlanOptsDTO gpoDto = new GsPlanOptsDTO();
		List<GsPlanOpts> gsPlanOpts = new ArrayList<>();
		GsPlanOpts gpo = null;
		// 当天排菜学校总数
		int distCount = departmentList.size();
		float[] assignRates = new float[distCount], dispRates = new float[distCount], acceptRates = new float[distCount];
		
		List<Object> distNamesList=CommonUtil.changeStringToList(departmentIds);
		List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
		
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
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
		List<SchGsCommon> dishList = new ArrayList<>();
		dishList = dbHiveGsService.getGsList(DataKeyConfig.talbeLedgerMasterTotalD,listYearMonth, startDate, endDateAddOne, distIdorSCName, null, 
				-1, -1, null, null,departmentId,departmentIdList, 4);
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
				if(schDishCommon.getDisSealStatus() !=null && !"-1".equals(schDishCommon.getDisSealStatus())) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if("1".equals(schDishCommon.getDisSealStatus())) {
						standardNumMap.put(dataKey, 
								(standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
					}else if("2".equals(schDishCommon.getDisSealStatus())) {
						supplementNumMap.put(dataKey, 
								(supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
					}else if("3".equals(schDishCommon.getDisSealStatus())) {
						beOverdueNumMap.put(dataKey, 
								(beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
					}else if("4".equals(schDishCommon.getDisSealStatus())) {
						noDataNumMap.put(dataKey, 
								(noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey)) 
								+ schDishCommon.getSchoolTotal());
					}
				}
			}
		}
		float schAcceptRate = 0;
		//标准验收率
		float schStandardRate = 0;
		for(int i = 0; i < distCount; i++) {
			DepartmentObj departmentObj = departmentList.get(i);
			String curDepartmentId = departmentObj.getDepartmentId();
			//判断是否按区域获取配货计划数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if(distIdorSCName != null) {
				if(!curDepartmentId.equalsIgnoreCase(distIdorSCName))
					continue ;
			}else if(distNamesList!=null && distNamesList.size() >0) {
				if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
					continue;
				}
				if(!distNamesList.contains(curDepartmentId)) {
					continue ;
				}
			}
			for(int k = 0; k < dates.length; k++) {
				gpo = new GsPlanOpts();
				gpo.setDistrDate(dates[k].replaceAll("-", "/"));
				gpo.setDepartmentId(curDepartmentId);
				int totalDistDishSchNum = 0, 
						totalConMatSchNum = 0;
				String dataKey = gpo.getDistrDate() + "_" + curDepartmentId;
				
				int totalNoAcceptSchNum = noAcceptSchNumsMap.get(dataKey)==null?0:noAcceptSchNumsMap.get(dataKey);
				int totalAcceptSchNum = acceptSchNumsMap.get(dataKey)==null?0:acceptSchNumsMap.get(dataKey);
				int totalGsPlanNum = totalGsPlanNumMap.get(dataKey)==null?0:totalGsPlanNumMap.get(dataKey);
				int acceptGsPlanNum = acceptGsPlanNumMap.get(dataKey)==null?0:acceptGsPlanNumMap.get(dataKey);
				int  noAcceptGsPlanNum= noAcceptGsPlanNumMap.get(dataKey)==null?0:noAcceptGsPlanNumMap.get(dataKey);
				int  assignGsPlanNum= assignGsPlanNumsMap.get(dataKey)==null?0:assignGsPlanNumsMap.get(dataKey);
				int  dispGsPlanNum= assignGsPlanNumsMap.get(dataKey)==null?0:assignGsPlanNumsMap.get(dataKey);
				
				//已排菜学校
				gpo.setDishSchNum(totalDistDishSchNum);
				//已确认学校
				gpo.setConSchNum(totalConMatSchNum);
				//应验收学校
				gpo.setShouldAcceptSchNum(totalNoAcceptSchNum + totalAcceptSchNum);
				//未验收学校
				gpo.setNoAcceptSchNum(totalNoAcceptSchNum);
				//已验收学校
				gpo.setAcceptSchNum(totalAcceptSchNum);
				//学校验收率
				schAcceptRate = 0;
				if(gpo.getShouldAcceptSchNum() > 0) {
					schAcceptRate = 100 * ((float) totalAcceptSchNum / (float) gpo.getShouldAcceptSchNum());
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100)
						schAcceptRate = 100;
				}
				gpo.setSchAcceptRate(schAcceptRate);
				
				//配送计划总数
				gpo.setTotalGsPlanNum(totalGsPlanNum);
				//指派数量及指派率
				gpo.setAssignGsPlanNum(assignGsPlanNum);
				assignRates[i] = 0;
				if(totalGsPlanNum > 0) {
					assignRates[i] = 100 * ((float) assignGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(assignRates[i]);
					assignRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (assignRates[i] > 100)
						assignRates[i] = 100;
				}
				gpo.setAssignRate(assignRates[i]);
				//配送数量及配送率
				gpo.setDispGsPlanNum(dispGsPlanNum);
				dispRates[i] = 0;
				if(totalGsPlanNum > 0) {
					dispRates[i] = 100 * ((float) dispGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(dispRates[i]);
					dispRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (dispRates[i] > 100)
						dispRates[i] = 100;
				}
				gpo.setDispRate(dispRates[i]);
				//未验收数
				gpo.setNoAcceptGsPlanNum(totalGsPlanNum-acceptGsPlanNum);
				//验收数量及验收率
				gpo.setAcceptGsPlanNum(acceptGsPlanNum);
				acceptRates[i] = 0;
				if(totalGsPlanNum > 0) {
					acceptRates[i] = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRates[i]);
					acceptRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (acceptRates[i] > 100)
						acceptRates[i] = 100;
				}
				gpo.setAcceptRate(acceptRates[i]);
				
				//1 表示规范录入
				gpo.setSchStandardNum(standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey));
				//2 表示补录
				gpo.setSchSupplementNum(supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey));
				//3 表示逾期补录
				gpo.setSchBeOverdueNum(beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey));
				//4 表示无数据
				gpo.setSchNoDataNum(noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey));
				
				//标准验收率
				schStandardRate = 0;
				if(gpo.getShouldAcceptSchNum() > 0) {
					schStandardRate = 100 * ((float) gpo.getSchStandardNum() / (float) gpo.getShouldAcceptSchNum());
					BigDecimal bd = new BigDecimal(schStandardRate);
					schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schStandardRate > 100)
						schStandardRate = 100;
				}
				gpo.setSchStandardRate(schStandardRate);
				
				gsPlanOpts.add(gpo);
			}
		}
		//排序
		//SortList<GsPlanOpts> sortList = new SortList<GsPlanOpts>();
		//sortList.Sort(gsPlanOpts, methods1, sorts1, dataTypes1);
		// 设置返回数据
		gpoDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<GsPlanOpts> pageBean = new PageBean<GsPlanOpts>(gsPlanOpts, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		gpoDto.setPageInfo(pageInfo);
		//设置数据
		gpoDto.setGsPlanOpts(pageBean.getCurPageData());
		//消息ID
		gpoDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return gpoDto;
	}
	
	// 配货计划操作列表函数
	private GsPlanOptsDTO gsPlanOpts(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, int schSelMode, int subLevel, 
			int compDep, String subDistName,String subLevels,String compDeps,String distNames,String departmentIds,DbHiveGsService dbHiveGsService,Db1Service db1Service) {
		GsPlanOptsDTO pdlDto = new GsPlanOptsDTO();
		//筛选学校模式
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		if(days >= 2) {
			if(schSelMode == 0) {    //按主管部门
				pdlDto = gsPlanOptsByCompDepFromHive(departmentId,distIdorSCName, dates, tedList, subLevel, compDep,subDistName,subLevels,compDeps,dbHiveGsService);
			}
			else if(schSelMode == 1) {  //按所在地
				pdlDto = gsPlanOptsByLocalityFromHive(departmentId,distIdorSCName, dates, tedList,distNames,dbHiveGsService);			
			}else if(schSelMode == 2) {  //按管理部门
				List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
				DepartmentObj departmentObj = new DepartmentObj();
				if(CommonUtil.isNotEmpty(departmentId)) {
					departmentObj.setDepartmentId(departmentId);
				}
				List<DepartmentObj> departmentList =  db1Service.getDepartmentObjList(departmentObj,departmentIdsList, -1, -1);	
				pdlDto = gsPlanOptsByDepartmentFromHive(departmentId, distIdorSCName, dates, departmentList, departmentIds, dbHiveGsService);
			}     	
		}else {
			if(schSelMode == 0) {    //按主管部门
				pdlDto = gsPlanOptsByCompDep(departmentId,distIdorSCName, dates, tedList, subLevel, compDep,subDistName,subLevels,compDeps);
			}
			else if(schSelMode == 1) {  //按所在地
				pdlDto = gsPlanOptsByLocality(departmentId,distIdorSCName, dates, tedList,distNames);			
			}else if(schSelMode == 2) {  //按管理部门
				List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
				DepartmentObj departmentObj = new DepartmentObj();
				if(CommonUtil.isNotEmpty(departmentId)) {
					departmentObj.setDepartmentId(departmentId);
				}
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,departmentIdsList, -1, -1);	
				pdlDto = gsPlanOptsByDepartment(departmentId, dates, deparmentList);
			}   
		}
		return pdlDto;
	}
	
	// 配货计划操作列表模型函数
	public GsPlanOptsDTO appModFunc(String token, String startDate, String endDate, String schSelMode, String subLevel, 
			String compDep, String subDistName, String distName, String prefCity, String province, String subLevels,String compDeps,String distNames,
			String departmentIds,
			String page, String pageSize, Db1Service db1Service, Db2Service db2Service,DbHiveGsService dbHiveGsService) {
		GsPlanOptsDTO gpoDto = null;
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
			if (province == null)
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
					//获取用户数据权限信息
				  	/*UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);
				  	if(curSubLevel == -1)
				  		curSubLevel = udpiDto.getSubLevelId();
				  	if(curCompDep == -1)
				  		curCompDep = udpiDto.getCompDepId();*/
					// 配货计划操作列表函数
					gpoDto = gsPlanOpts(departmentId,distIdorSCName, dates, tddList, curSchSelMode, curSubLevel, curCompDep, subDistName,
							subLevels,compDeps,distNames,departmentIds,dbHiveGsService,db1Service);
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
					//获取用户数据权限信息
				  	/*UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);
				  	if(curSubLevel == -1)
				  		curSubLevel = udpiDto.getSubLevelId();
				  	if(curCompDep == -1)
				  		curCompDep = udpiDto.getCompDepId();*/
					// 配货计划操作列表函数
					gpoDto = gsPlanOpts(departmentId,distIdorSCName, dates, tddList, curSchSelMode, curSubLevel, curCompDep, subDistName, 
							subLevels,compDeps,distNames,departmentIds,dbHiveGsService,db1Service);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			gpoDto = SimuDataFunc();
		}		

		return gpoDto;
	}
}