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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatConfirms;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatConfirmsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchMatCommon;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//用料确认列表应用模型
public class MatConfirmsAppMod {
	private static final Logger logger = LogManager.getLogger(MatConfirmsAppMod.class.getName());
	
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
	String[] matUseDate_Array = {"2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03", "2018/09/03-2018/09/03"};
	String[] subLevel_Array = {"区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属", "区属"};
	String[] compDep_Array = {"黄浦区教育局", "嘉定区教育局", "宝山区教育局", "浦东新区教育局", "松江区教育局", "金山区教育局", "青浦区教育局", "奉贤区教育局", "崇明区教育局", "静安区教育局", "徐汇区教育局", "长宁区教育局", "普陀区教育局", "虹口区教育局", "杨浦区教育局", "闵行区教育局"};
	String[] distName_Array = {"黄浦区", "嘉定区", "宝山区", "浦东新区", "松江区", "金山区", "青浦区", "奉贤区", "崇明区", "静安区", "徐汇区 ", "长宁区", "普陀区", "虹口区", "杨浦区", "闵行区"};
	int[] dishSchNum_Array = {161, 222, 375, 974, 292, 150, 190, 210, 146, 253, 278, 150, 247, 162, 248, 475};
	int[] noConMatSchNum_Array = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
	int[] conMatSchNum_Array = {151, 212, 365, 964, 282, 140, 180, 200, 136, 243, 268, 140, 237, 152, 238, 465};
	int[] totalMatPlanNum_Array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	int[] conMatPlanNum_Array = {125, 164, 321, 572, 231, 114, 129, 150, 76, 220, 235, 138, 204, 132, 218, 341};
	int[] noConMatPlanNum_Array = {0, 0, 1, 47, 0, 4, 0, 9, 26, 0, 0, 0, 0, 2, 3, 23};
	int[] expConMatPlanNum_Array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	float[] matConRate_Array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	
	//模拟数据函数
	private MatConfirmsDTO SimuDataFunc() {
		MatConfirmsDTO mcsDto = new MatConfirmsDTO();
		//时戳
		mcsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//用料确认列表模拟数据
		List<MatConfirms> matConfirms = new ArrayList<>();
		//赋值
		for (int i = 0; i < matUseDate_Array.length; i++) {
			MatConfirms mcs = new MatConfirms();
			totalMatPlanNum_Array[i] = conMatPlanNum_Array[i] + noConMatPlanNum_Array[i];
			matConRate_Array[i] = 100*((float)(conMatPlanNum_Array[i])/(float)(totalMatPlanNum_Array[i]));
			BigDecimal bd = new BigDecimal(matConRate_Array[i]); 
			matConRate_Array[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			mcs.setMatUseDate(matUseDate_Array[i]);
			mcs.setSubLevel(subLevel_Array[i]);
			mcs.setCompDep(compDep_Array[i]);
			mcs.setDistName(distName_Array[i]);
			mcs.setDishSchNum(dishSchNum_Array[i]);
			mcs.setNoConMatSchNum(noConMatSchNum_Array[i]); 
			mcs.setConMatSchNum(conMatSchNum_Array[i]);
			mcs.setTotalMatPlanNum(totalMatPlanNum_Array[i]);
			mcs.setConMatPlanNum(conMatPlanNum_Array[i]);
			mcs.setNoConMatPlanNum(noConMatPlanNum_Array[i]);
			mcs.setExpConMatPlanNum(expConMatPlanNum_Array[0]);
			mcs.setMatConRate(matConRate_Array[i]);
			matConfirms.add(mcs);
		}
		//设置数据
		mcsDto.setMatConfirms(matConfirms);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = matUseDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		mcsDto.setPageInfo(pageInfo);
		//消息ID
		mcsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mcsDto;
	}
	
	//用料确认列表函数按主管部门
	private MatConfirmsDTO matConfirmsByCompDep(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, 
        int subLevel, int compDep, String subDistName,
        String subLevels,String compDeps) {
		MatConfirmsDTO mcsDto = new MatConfirmsDTO();
		List<MatConfirms> matConfirms = new ArrayList<>();
		MatConfirms mcs = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> useMaterialPlanMap = null, platoonFeedTotalMap = null;
		int i, j, k, l, 
		        subLevelCount = 4, 
				compDepCount = 0, 
				maxCompDepCount = AppModConfig.compDepIdToNameMap3.size(), 
				dateCount = dates.length;
		int[][][] distDishSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				noConMatSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				conMatSchNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				totalMatPlanNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				matPlanConfirmNums = new int[dateCount][subLevelCount][maxCompDepCount], 
				matPlanNoConfirmNums = new int[dateCount][subLevelCount][maxCompDepCount];
		float[][] matPlanConfirmRates = new float[subLevelCount][maxCompDepCount];
		List<Object> subLevelList=CommonUtil.changeStringToList(subLevels);
		List<Object> compDepList=CommonUtil.changeStringToList(compDeps);
		//时间段内用料计划数量
		for (k = 0; k < dates.length; k++) {
			//注释原因：目前界面没有展示排菜学校，且30天之后的数据会迁移到hive库中
			//供餐学校数量
//			key = dates[k] + "_platoonfeed-total";
//			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
//			if(platoonFeedTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
//				platoonFeedTotalMap = AppModConfig.getHdfsDataKey(dates[k], key);
//			}
//			if(platoonFeedTotalMap != null) {
//				for(String curKey : platoonFeedTotalMap.keySet()) {
//					for (i = 0; i < subLevelCount; i++) {
//						if(i == 0)
//							compDepCount = 1;
//						else if(i == 1)
//							compDepCount = 2;
//						else if(i == 2)
//							compDepCount = 8;
//						else if(i == 3)
//							compDepCount = 17;
//						else
//							compDepCount = 0;
//						for(j = 0; j < compDepCount; j++) {
//							//判断是否按主管部门获取数据
//							if(subLevel != -1) {
//								if(i != subLevel)
//									continue ;
//								else {
//									if(compDep != -1) {
//										if(compDep != j)
//											continue ;
//									}
//								}
//							}else if(subLevelList!=null && subLevelList.size()>0) {
//								if(!subLevelList.contains(String.valueOf(i))) {
//									continue;
//								}else {
//									if(compDepList!=null && compDepList.size()>0) {
//										if(!compDepList.contains(i+"_"+(j))) {
//											continue ;
//										}
//									}
//								}
//							}
//							// 设置前置域名
//							if(i < 3)
//								fieldPrefix = "masterid_" + i + "_slave_" + j;
//							else if(i == 3) {
//								String compDepId = String.valueOf(j);								
//								fieldPrefix = "masterid_" + i + "_slave_" + AppModConfig.compDepIdToNameMap3.get(compDepId);
//							}
//							// 区域排菜学校供餐数
//							int dishSchNum = 0;
//							if (curKey.indexOf(fieldPrefix) == 0) {
//								String[] curKeys = curKey.split("_");
//								if(curKeys.length >= 6)
//								{
//									if(curKeys[4].equalsIgnoreCase("供餐") && curKeys[5].equalsIgnoreCase("已排菜")) {
//										keyVal = platoonFeedTotalMap.get(curKey);
//										if(keyVal != null) {
//											dishSchNum = Integer.parseInt(keyVal);
//										}
//									}
//								}
//							}
//							distDishSchNums[k][i][j] += dishSchNum;
//						}
//					}
//				}
//			}
			// 用料计划数量
			key = dates[k] + DataKeyConfig.useMaterialPlanTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentUseMaterialPlanTotal+departmentId;
			}
			useMaterialPlanMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value,
					SpringConfig.RedisDBIdx, key);
			if (useMaterialPlanMap != null) {
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
						// 未确认用料学校
						int noConMatSchNum = 0;
						for(l = 0; l < 2; l++) {
							field = "school-" + fieldPrefix + "_" + "status" + "_" + l;
							keyVal = useMaterialPlanMap.get(field);
							if (keyVal != null)
								noConMatSchNum += Integer.parseInt(keyVal);
						}
						noConMatSchNums[k][i][j] = noConMatSchNum;
						// 已确认用料学校
						field = "school-" + fieldPrefix + "_" + "status" + "_" + 2;
						keyVal = useMaterialPlanMap.get(field);
						if (keyVal != null)
							conMatSchNums[k][i][j] = Integer.parseInt(keyVal);	
						// 用料计划总数
						int totalMatPlanNum = 0;
						for(l = 0; l < 3; l++) {
							field = fieldPrefix + "_" + "status" + "_" + l;
							keyVal = useMaterialPlanMap.get(field);
							if (keyVal != null)
								totalMatPlanNum += Integer.parseInt(keyVal);
						}
						totalMatPlanNums[k][i][j] = totalMatPlanNum;
						// 用料计划确认数
						field = fieldPrefix + "_" + "status" + "_" + 2;
						keyVal = useMaterialPlanMap.get(field);
						if (keyVal != null)
							matPlanConfirmNums[k][i][j] = Integer.parseInt(keyVal);
						// 用料计划未确认数
						int matPlanNoConfirmNum = 0;
						for(l = 0; l < 2; l++) {
							field = fieldPrefix + "_" + "status" + "_" + l;
							keyVal = useMaterialPlanMap.get(field);
							if (keyVal != null)
								matPlanNoConfirmNum += Integer.parseInt(keyVal);
						}
						matPlanNoConfirmNums[k][i][j] = matPlanNoConfirmNum;
					}
				}
			}
			// 该日期各区用料计划
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
					// 区域确认率
					if (totalMatPlanNums[k][i][j] != 0) {
						matPlanConfirmRates[i][j] = 100 * ((float) matPlanConfirmNums[k][i][j] / (float) totalMatPlanNums[k][i][j]);
						BigDecimal bd = new BigDecimal(matPlanConfirmRates[i][j]);
						matPlanConfirmRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (matPlanConfirmRates[i][j] > 100) {
							matPlanConfirmRates[i][j] = 100;
							matPlanConfirmNums[k][i][j] = totalMatPlanNums[k][i][j];
						}
					}
					logger.info("日期：" + dates[k] + "，所属：" + AppModConfig.subLevelIdToNameMap.get(i) + "，主管部门：" + compDepName + "，用料计划总数：" + totalMatPlanNums[k][i] + "，已确认用料计划数：" + matPlanConfirmNums[k][i] + "，确认率：" + matPlanConfirmRates[i][j] + "，field = " + field);
				}
			}
		}
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
					mcs = new MatConfirms();
					mcs.setMatUseDate(dates[k].replaceAll("-", "/"));
					mcs.setSubLevel(String.valueOf(i) + "," + AppModConfig.subLevelIdToNameMap.get(i));
					mcs.setCompDep(compDepId + "," + compDepName);
					int totalDistDishSchNum = 0, totalNoConMatSchNum = 0, totalConMatSchNum = 0, totalMatPlanNum = 0, matPlanConfirmNum = 0, matPlanNoConfirmNum = 0;
					totalDistDishSchNum = distDishSchNums[k][i][j];
					totalNoConMatSchNum = noConMatSchNums[k][i][j];
					totalConMatSchNum = conMatSchNums[k][i][j];					
					totalMatPlanNum = totalMatPlanNums[k][i][j];
					matPlanConfirmNum = matPlanConfirmNums[k][i][j];
					matPlanNoConfirmNum = matPlanNoConfirmNums[k][i][j];
					//已排菜学校
					mcs.setDishSchNum(totalDistDishSchNum);
					//未确认用料学校
					mcs.setNoConMatSchNum(totalNoConMatSchNum);
					//已确认用料学校
					mcs.setConMatSchNum(totalConMatSchNum);
					//未确认学校
					mcs.setShouldAccSchNum(mcs.getNoConMatSchNum() + mcs.getConMatSchNum());
					//用料计划总数
					mcs.setTotalMatPlanNum(totalMatPlanNum);
					//用料计划确认数
					mcs.setConMatPlanNum(matPlanConfirmNum);
					//用料计划未确认数
					mcs.setNoConMatPlanNum(matPlanNoConfirmNum);
					//逾期确认数量
					mcs.setExpConMatPlanNum(0);     //待定
					//用料计划确认率
					matPlanConfirmRates[i][j] = 0;
					if(totalMatPlanNum > 0) {
						matPlanConfirmRates[i][j] = 100 * ((float) matPlanConfirmNum / (float) totalMatPlanNum);
						BigDecimal bd = new BigDecimal(matPlanConfirmRates[i][j]);
						matPlanConfirmRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (matPlanConfirmRates[i][j] > 100)
							matPlanConfirmRates[i][j] = 100;
					}
					mcs.setMatConRate(matPlanConfirmRates[i][j]);
					matConfirms.add(mcs);
				}
			}
		}
		//排序
		SortList<MatConfirms> sortList = new SortList<MatConfirms>();
		sortList.Sort(matConfirms, methods0, sorts0, dataTypes0);
		// 设置返回数据
		mcsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<MatConfirms> pageBean = new PageBean<MatConfirms>(matConfirms, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		mcsDto.setPageInfo(pageInfo);
		//设置数据
		mcsDto.setMatConfirms(pageBean.getCurPageData());
		//消息ID
		mcsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mcsDto;
	}
	
	
	//用料确认列表函数按主管部门
	private MatConfirmsDTO matConfirmsByCompDepFromHive(String deaprtmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, 
        int subLevel, int compDep, String subDistName,
        String subLevels,String compDeps,DbHiveMatService dbHiveMatService) {
		MatConfirmsDTO mcsDto = new MatConfirmsDTO();
		List<MatConfirms> matConfirms = new ArrayList<>();
		MatConfirms mcs = null;
		// 当天排菜学校总数
		int i, j, k,
		        subLevelCount = 4, 
				compDepCount = 0, 
				maxCompDepCount = AppModConfig.compDepIdToNameMap3.size();
		float[][] matPlanConfirmRates = new float[subLevelCount][maxCompDepCount];
		List<Object> subLevelList=CommonUtil.changeStringToList(subLevels);
		List<Object> compDepList=CommonUtil.changeStringToList(compDeps);
		//时间段内用料计划数量
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
		//用料计划总数
		Map<String,Integer> totalMatPlanNumMap = new HashMap<String,Integer>();
		//已确认用料计划数
		Map<String,Integer> conMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划数
		Map<String,Integer> noConMatPlanNumMap = new HashMap<String,Integer>();
		//已确认用料计划学校数
		Map<String,Integer> conMatSchNumMap = new HashMap<String,Integer>();
		//未确认用料计划学校数
		Map<String,Integer> noConMatSchNumMap = new HashMap<String,Integer>();
		
		List<SchMatCommon> dishList = new ArrayList<>();
		dishList = dbHiveMatService.getMatList(DataKeyConfig.talbeMaterialTotalD,listYearMonth, startDate, endDateAddOne, null, null, 
				subLevel,compDep, subLevelList, compDepList,deaprtmentId,null, 3);
		if(dishList !=null && dishList.size() > 0) {
			for(SchMatCommon schDishCommon: dishList) {
				String key = schDishCommon.getMatDate() + "_" + schDishCommon.getDepartmentMasterId() + "_" +schDishCommon.getDepartmentSlaveIdName();
				if(schDishCommon.getStatus() ==null) {
					totalMatPlanNumMap.put(key, 
							(totalMatPlanNumMap.get(key)==null?0:totalMatPlanNumMap.get(key))+ schDishCommon.getTotal());
				}else if (schDishCommon.getStatus() == 0 || schDishCommon.getStatus() == 1) {
					//信息不完整和待确认都属于未确认状态
					
					noConMatSchNumMap.put(key, 
							(noConMatSchNumMap.get(key)==null?0:noConMatSchNumMap.get(key))+ schDishCommon.getSchoolTotal());
					
					noConMatPlanNumMap.put(key, 
							(noConMatPlanNumMap.get(key)==null?0:noConMatPlanNumMap.get(key))+ schDishCommon.getTotal());
					
				}else if (schDishCommon.getStatus() == 2) {
					
					conMatSchNumMap.put(key, 
							(conMatSchNumMap.get(key)==null?0:conMatSchNumMap.get(key)) + schDishCommon.getSchoolTotal());
					
					conMatPlanNumMap.put(key, 
							(conMatPlanNumMap.get(key)==null?0:conMatPlanNumMap.get(key)) + schDishCommon.getTotal());
				}
			}
		}
		
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
					
					
					mcs = new MatConfirms();
					mcs.setMatUseDate(dates[k].replaceAll("-", "/"));
					mcs.setSubLevel(String.valueOf(i) + "," + AppModConfig.subLevelIdToNameMap.get(i));
					mcs.setCompDep(compDepId + "," + compDepName);
					int totalDistDishSchNum = 0, totalNoConMatSchNum = 0, totalConMatSchNum = 0, totalMatPlanNum = 0, matPlanConfirmNum = 0, matPlanNoConfirmNum = 0;
					
					String dataKey = mcs.getMatUseDate()+"_"+i+"_" + compDepId;
					totalNoConMatSchNum = noConMatSchNumMap.get(dataKey)==null?0:noConMatSchNumMap.get(dataKey);
					totalConMatSchNum = conMatSchNumMap.get(dataKey)==null?0:conMatSchNumMap.get(dataKey);
					totalMatPlanNum = totalMatPlanNumMap.get(dataKey)==null?0:totalMatPlanNumMap.get(dataKey);
					matPlanConfirmNum = conMatPlanNumMap.get(dataKey)==null?0:conMatPlanNumMap.get(dataKey);
					matPlanNoConfirmNum = noConMatPlanNumMap.get(dataKey)==null?0:noConMatPlanNumMap.get(dataKey);
					
					//目前界面不展示
					totalDistDishSchNum = 0;
					
					//已排菜学校
					mcs.setDishSchNum(totalDistDishSchNum);
					//未确认用料学校
					mcs.setNoConMatSchNum(totalNoConMatSchNum);
					//已确认用料学校
					mcs.setConMatSchNum(totalConMatSchNum);
					//未确认学校
					mcs.setShouldAccSchNum(mcs.getNoConMatSchNum() + mcs.getConMatSchNum());
					//用料计划总数
					mcs.setTotalMatPlanNum(totalMatPlanNum);
					//用料计划确认数
					mcs.setConMatPlanNum(matPlanConfirmNum);
					//用料计划未确认数
					mcs.setNoConMatPlanNum(matPlanNoConfirmNum);
					//逾期确认数量
					mcs.setExpConMatPlanNum(0);     //待定
					//用料计划确认率
					matPlanConfirmRates[i][j] = 0;
					if(totalMatPlanNum > 0) {
						matPlanConfirmRates[i][j] = 100 * ((float) matPlanConfirmNum / (float) totalMatPlanNum);
						BigDecimal bd = new BigDecimal(matPlanConfirmRates[i][j]);
						matPlanConfirmRates[i][j] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						if (matPlanConfirmRates[i][j] > 100)
							matPlanConfirmRates[i][j] = 100;
					}
					mcs.setMatConRate(matPlanConfirmRates[i][j]);
					matConfirms.add(mcs);
				}
			}
		}
		//排序
		SortList<MatConfirms> sortList = new SortList<MatConfirms>();
		sortList.Sort(matConfirms, methods0, sorts0, dataTypes0);
		// 设置返回数据
		mcsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<MatConfirms> pageBean = new PageBean<MatConfirms>(matConfirms, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		mcsDto.setPageInfo(pageInfo);
		//设置数据
		mcsDto.setMatConfirms(pageBean.getCurPageData());
		//消息ID
		mcsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mcsDto;
	}
	
	//用料确认列表函数按所在地
	private MatConfirmsDTO matConfirmsByLocalityFromHive(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList,String distNames,
			DbHiveMatService dbHiveMatService) {
		MatConfirmsDTO mcsDto = new MatConfirmsDTO();
		List<MatConfirms> matConfirms = new ArrayList<>();
		MatConfirms mcs = null;
		// 当天排菜学校总数
		int distCount = tddList.size();
		float[] matPlanConfirmRates = new float[distCount];
		
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
		
		List<SchMatCommon> dishList = new ArrayList<>();
		dishList = dbHiveMatService.getMatList(DataKeyConfig.talbeMaterialTotalD,listYearMonth, startDate, endDateAddOne, distIdorSCName, distNamesList, 
				-1, -1, null, null,departmentId,null, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchMatCommon schDishCommon: dishList) {
				String dataKey =schDishCommon.getMatDate() + "_" + schDishCommon.getDistId();
				
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
		for(int i = 0; i < distCount; i++) {
			for (int k = 0; k < dates.length; k++) {
				TEduDistrictDo curTdd = tddList.get(i);
				String curDistId = curTdd.getId();
				//判断是否按区域获取数据（distIdorSCName为空表示按省或直辖市级别获取数据）
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
				mcs = new MatConfirms();
				mcs.setMatUseDate(dates[k].replaceAll("-", "/"));
				mcs.setDistName(curTdd.getId());
				int totalDistDishSchNum = 0, totalNoConMatSchNum = 0, totalConMatSchNum = 0, totalMatPlanNum = 0, matPlanConfirmNum = 0, matPlanNoConfirmNum = 0;
				
				String dataKey = mcs.getMatUseDate() + "_" + curDistId;
				//目前界面不做展示
				totalDistDishSchNum = 0;
				//未确认用料学校
				totalNoConMatSchNum = noConMatSchNumMap.get(dataKey)==null?0:noConMatSchNumMap.get(dataKey);
				//已确认用料学校
				totalConMatSchNum = conMatSchNumMap.get(dataKey)==null?0:conMatSchNumMap.get(dataKey);
				//用料计划总数
				totalMatPlanNum = totalMatPlanNumMap.get(dataKey)==null?0:totalMatPlanNumMap.get(dataKey);
				//用料计划确认数
				matPlanConfirmNum = conMatPlanNumMap.get(dataKey)==null?0:conMatPlanNumMap.get(dataKey);
				//用料计划未确认数
				matPlanNoConfirmNum = noConMatPlanNumMap.get(dataKey)==null?0:noConMatPlanNumMap.get(dataKey);
				
				
				//已排菜学校
				mcs.setDishSchNum(totalDistDishSchNum);
				//未确认用料学校
				mcs.setNoConMatSchNum(totalNoConMatSchNum);
				//已确认用料学校
				mcs.setConMatSchNum(totalConMatSchNum);
				//应确认学校
				mcs.setShouldAccSchNum(mcs.getNoConMatSchNum() + mcs.getConMatSchNum());
				//用料计划总数
				mcs.setTotalMatPlanNum(totalMatPlanNum);
				//用料计划确认数
				mcs.setConMatPlanNum(matPlanConfirmNum);
				//用料计划未确认数
				mcs.setNoConMatPlanNum(matPlanNoConfirmNum);
				//逾期确认数量
				mcs.setExpConMatPlanNum(0);     //待定
				//用料计划确认率
				matPlanConfirmRates[i] = 0;
				if(totalMatPlanNum > 0) {
					matPlanConfirmRates[i] = 100 * ((float) matPlanConfirmNum / (float) totalMatPlanNum);
					BigDecimal bd = new BigDecimal(matPlanConfirmRates[i]);
					matPlanConfirmRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (matPlanConfirmRates[i] > 100)
						matPlanConfirmRates[i] = 100;
				}
				mcs.setMatConRate(matPlanConfirmRates[i]);
				matConfirms.add(mcs);
			}
		}
		//排序
		SortList<MatConfirms> sortList = new SortList<MatConfirms>();
		sortList.Sort(matConfirms, methods1, sorts1, dataTypes1);
		// 设置返回数据
		mcsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<MatConfirms> pageBean = new PageBean<MatConfirms>(matConfirms, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		mcsDto.setPageInfo(pageInfo);
		//设置数据
		mcsDto.setMatConfirms(pageBean.getCurPageData());
		//消息ID
		mcsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return mcsDto;
	}
	
	//用料确认列表函数管理部门
	private MatConfirmsDTO matConfirmsByDepartmentFromHive(String departmentId, String[] dates, List<DepartmentObj> deparmentList,String departmentIds,
			DbHiveMatService dbHiveMatService) {
		MatConfirmsDTO mcsDto = new MatConfirmsDTO();
		List<MatConfirms> matConfirms = new ArrayList<>();
		MatConfirms mcs = null;
		// 当天排菜学校总数
		int distCount = deparmentList.size();
		float[] matPlanConfirmRates = new float[distCount];
		
		List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
		
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
		
		List<SchMatCommon> dishList = new ArrayList<>();
		dishList = dbHiveMatService.getMatList(DataKeyConfig.talbeMaterialTotalD,listYearMonth, startDate, endDateAddOne, null, null, 
				-1, -1, null, null,departmentId,departmentIdsList, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchMatCommon schDishCommon: dishList) {
				String dataKey =schDishCommon.getMatDate() + "_" + schDishCommon.getDistId();
				
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
		for(int i = 0; i < distCount; i++) {
			for (int k = 0; k < dates.length; k++) {
				DepartmentObj departmentObj = deparmentList.get(i);
				String curDepartmentId = departmentObj.getDepartmentId();
				//判断是否按区域获取数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if(departmentId != null) {
					if(!curDepartmentId.equalsIgnoreCase(departmentId))
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
				mcs = new MatConfirms();
				mcs.setMatUseDate(dates[k].replaceAll("-", "/"));
				mcs.setDepartmentId(curDepartmentId);
				int totalDistDishSchNum = 0, totalNoConMatSchNum = 0, totalConMatSchNum = 0, totalMatPlanNum = 0, matPlanConfirmNum = 0, matPlanNoConfirmNum = 0;
				
				String dataKey = mcs.getMatUseDate() + "_" + curDepartmentId;
				//目前界面不做展示
				totalDistDishSchNum = 0;
				//未确认用料学校
				totalNoConMatSchNum = noConMatSchNumMap.get(dataKey)==null?0:noConMatSchNumMap.get(dataKey);
				//已确认用料学校
				totalConMatSchNum = conMatSchNumMap.get(dataKey)==null?0:conMatSchNumMap.get(dataKey);
				//用料计划总数
				totalMatPlanNum = totalMatPlanNumMap.get(dataKey)==null?0:totalMatPlanNumMap.get(dataKey);
				//用料计划确认数
				matPlanConfirmNum = conMatPlanNumMap.get(dataKey)==null?0:conMatPlanNumMap.get(dataKey);
				//用料计划未确认数
				matPlanNoConfirmNum = noConMatPlanNumMap.get(dataKey)==null?0:noConMatPlanNumMap.get(dataKey);
				
				
				//已排菜学校
				mcs.setDishSchNum(totalDistDishSchNum);
				//未确认用料学校
				mcs.setNoConMatSchNum(totalNoConMatSchNum);
				//已确认用料学校
				mcs.setConMatSchNum(totalConMatSchNum);
				//应确认学校
				mcs.setShouldAccSchNum(mcs.getNoConMatSchNum() + mcs.getConMatSchNum());
				//用料计划总数
				mcs.setTotalMatPlanNum(totalMatPlanNum);
				//用料计划确认数
				mcs.setConMatPlanNum(matPlanConfirmNum);
				//用料计划未确认数
				mcs.setNoConMatPlanNum(matPlanNoConfirmNum);
				//逾期确认数量
				mcs.setExpConMatPlanNum(0);     //待定
				//用料计划确认率
				matPlanConfirmRates[i] = 0;
				if(totalMatPlanNum > 0) {
					matPlanConfirmRates[i] = 100 * ((float) matPlanConfirmNum / (float) totalMatPlanNum);
					BigDecimal bd = new BigDecimal(matPlanConfirmRates[i]);
					matPlanConfirmRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (matPlanConfirmRates[i] > 100)
						matPlanConfirmRates[i] = 100;
				}
				mcs.setMatConRate(matPlanConfirmRates[i]);
				matConfirms.add(mcs);
			}
		}
		//排序
		//SortList<MatConfirms> sortList = new SortList<MatConfirms>();
		//sortList.Sort(matConfirms, methods1, sorts1, dataTypes1);
		// 设置返回数据
		mcsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<MatConfirms> pageBean = new PageBean<MatConfirms>(matConfirms, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		mcsDto.setPageInfo(pageInfo);
		//设置数据
		mcsDto.setMatConfirms(pageBean.getCurPageData());
		//消息ID
		mcsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return mcsDto;
	}
	
	//用料确认列表函数按所在地
	private MatConfirmsDTO matConfirmsByLocality(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList,String distNames) {
		MatConfirmsDTO mcsDto = new MatConfirmsDTO();
		List<MatConfirms> matConfirms = new ArrayList<>();
		MatConfirms mcs = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> useMaterialPlanMap = null;
		int distCount = tddList.size(), dateCount = dates.length;
		int[][] distDishSchNums = new int[dateCount][distCount], 
				noConMatSchNums = new int[dateCount][distCount], 
				conMatSchNums = new int[dateCount][distCount], 
				totalMatPlanNums = new int[dateCount][distCount], 
				matPlanConfirmNums = new int[dateCount][distCount], 
				matPlanNoConfirmNums = new int[dateCount][distCount];
		float[] matPlanConfirmRates = new float[distCount];
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		
		//时间段内用料计划数量
		for (int k = 0; k < dates.length; k++) {	
			//注释原因：目前界面没有展示排菜学校，且30天之后的数据会迁移到hive库中
			//供餐学校数量
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
							if(!CommonUtil.isInteger(curDistId)) {
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
			key = dates[k] + DataKeyConfig.useMaterialPlanTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentUseMaterialPlanTotal+departmentId;
			}
			useMaterialPlanMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value,
					SpringConfig.RedisDBIdx, key);
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
					// 未确认用料学校
					int noConMatSchNum = 0;
					for(int j = 0; j < 2; j++) {
						field = fieldPrefix + "_" + "status" + "_" + j;
						keyVal = useMaterialPlanMap.get(field);
						if (keyVal != null)
							noConMatSchNum += Integer.parseInt(keyVal);
					}
					noConMatSchNums[k][i] = noConMatSchNum;
					// 已确认用料学校
					field = fieldPrefix + "_" + "status" + "_" + 2;
					keyVal = useMaterialPlanMap.get(field);
					if (keyVal != null)
						conMatSchNums[k][i] = Integer.parseInt(keyVal);					
					// 用料计划总数
					fieldPrefix = "area" + "_" + curDistId;
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
					// 用料计划未确认数
					int matPlanNoConfirmNum = 0;
					for(int j = 0; j < 2; j++) {
						field = fieldPrefix + "_" + "status" + "_" + j;
						keyVal = useMaterialPlanMap.get(field);
						if (keyVal != null)
							matPlanNoConfirmNum += Integer.parseInt(keyVal);
					}
					matPlanNoConfirmNums[k][i] = matPlanNoConfirmNum;
				}
			}
		}
		for(int i = 0; i < distCount; i++) {
			for (int k = 0; k < dates.length; k++) {
				TEduDistrictDo curTdd = tddList.get(i);
				String curDistId = curTdd.getId();
				//判断是否按区域获取数据（distIdorSCName为空表示按省或直辖市级别获取数据）
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
				mcs = new MatConfirms();
				mcs.setMatUseDate(dates[k].replaceAll("-", "/"));
				mcs.setDistName(curTdd.getId());
				int totalDistDishSchNum = 0, totalNoConMatSchNum = 0, totalConMatSchNum = 0, totalMatPlanNum = 0, matPlanConfirmNum = 0, matPlanNoConfirmNum = 0;
				totalDistDishSchNum = distDishSchNums[k][i];
				totalNoConMatSchNum = noConMatSchNums[k][i];
				totalConMatSchNum = conMatSchNums[k][i];
				totalMatPlanNum = totalMatPlanNums[k][i];
				matPlanConfirmNum = matPlanConfirmNums[k][i];
				matPlanNoConfirmNum = matPlanNoConfirmNums[k][i];				
				//已排菜学校
				mcs.setDishSchNum(totalDistDishSchNum);
				//未确认用料学校
				mcs.setNoConMatSchNum(totalNoConMatSchNum);
				//已确认用料学校
				mcs.setConMatSchNum(totalConMatSchNum);
				//应确认学校
				mcs.setShouldAccSchNum(mcs.getNoConMatSchNum() + mcs.getConMatSchNum());
				//用料计划总数
				mcs.setTotalMatPlanNum(totalMatPlanNum);
				//用料计划确认数
				mcs.setConMatPlanNum(matPlanConfirmNum);
				//用料计划未确认数
				mcs.setNoConMatPlanNum(matPlanNoConfirmNum);
				//逾期确认数量
				mcs.setExpConMatPlanNum(0);     //待定
				//用料计划确认率
				matPlanConfirmRates[i] = 0;
				if(totalMatPlanNum > 0) {
					matPlanConfirmRates[i] = 100 * ((float) matPlanConfirmNum / (float) totalMatPlanNum);
					BigDecimal bd = new BigDecimal(matPlanConfirmRates[i]);
					matPlanConfirmRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (matPlanConfirmRates[i] > 100)
						matPlanConfirmRates[i] = 100;
				}
				mcs.setMatConRate(matPlanConfirmRates[i]);
				matConfirms.add(mcs);
			}
		}
		//排序
		SortList<MatConfirms> sortList = new SortList<MatConfirms>();
		sortList.Sort(matConfirms, methods1, sorts1, dataTypes1);
		// 设置返回数据
		mcsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<MatConfirms> pageBean = new PageBean<MatConfirms>(matConfirms, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		mcsDto.setPageInfo(pageInfo);
		//设置数据
		mcsDto.setMatConfirms(pageBean.getCurPageData());
		//消息ID
		mcsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return mcsDto;
	}
	
	//用料确认列表函数按管理部门
	private MatConfirmsDTO matConfirmsByDepartment(String departmentId,String[] dates, List<DepartmentObj> deparmentList,String departmentIds) {
		MatConfirmsDTO mcsDto = new MatConfirmsDTO();
		List<MatConfirms> matConfirms = new ArrayList<>();
		MatConfirms mcs = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> useMaterialPlanMap = null;
		int distCount = deparmentList.size(), dateCount = dates.length;
		int[][] distDishSchNums = new int[dateCount][distCount], 
				noConMatSchNums = new int[dateCount][distCount], 
				conMatSchNums = new int[dateCount][distCount], 
				totalMatPlanNums = new int[dateCount][distCount], 
				matPlanConfirmNums = new int[dateCount][distCount], 
				matPlanNoConfirmNums = new int[dateCount][distCount];
		float[] matPlanConfirmRates = new float[distCount];
		
		List<Object> deparmentIdList=CommonUtil.changeStringToList(departmentIds);
		
		//时间段内用料计划数量
		for (int k = 0; k < dates.length; k++) {	
			// 用料计划数量
			key = dates[k] + DataKeyConfig.useMaterialPlanTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentUseMaterialPlanTotal+departmentId;
			}
			useMaterialPlanMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value,
					SpringConfig.RedisDBIdx, key);
			if (useMaterialPlanMap != null) {
				for(int i = 0; i < distCount; i++) {
					DepartmentObj departmentObj = deparmentList.get(i);
					String curDepartmentId = departmentObj.getDepartmentId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(departmentId != null) {
						if(!curDepartmentId.equalsIgnoreCase(departmentId))
							continue ;
					}
					
					if(deparmentIdList!=null && deparmentIdList.size() >0) {
						if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
							continue;
						}
						if(!deparmentIdList.contains(curDepartmentId)) {
							continue ;
						}
					}
					fieldPrefix = "school-department" + "_" + curDepartmentId;
					// 未确认用料学校
					int noConMatSchNum = 0;
					for(int j = 0; j < 2; j++) {
						field = fieldPrefix + "_" + "status" + "_" + j;
						keyVal = useMaterialPlanMap.get(field);
						if (keyVal != null)
							noConMatSchNum += Integer.parseInt(keyVal);
					}
					noConMatSchNums[k][i] = noConMatSchNum;
					// 已确认用料学校
					field = fieldPrefix + "_" + "status" + "_" + 2;
					keyVal = useMaterialPlanMap.get(field);
					if (keyVal != null)
						conMatSchNums[k][i] = Integer.parseInt(keyVal);					
					// 用料计划总数
					fieldPrefix = "department" + "_" + curDepartmentId;
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
					// 用料计划未确认数
					int matPlanNoConfirmNum = 0;
					for(int j = 0; j < 2; j++) {
						field = fieldPrefix + "_" + "status" + "_" + j;
						keyVal = useMaterialPlanMap.get(field);
						if (keyVal != null)
							matPlanNoConfirmNum += Integer.parseInt(keyVal);
					}
					matPlanNoConfirmNums[k][i] = matPlanNoConfirmNum;
				}
			}
		}
		for(int i = 0; i < distCount; i++) {
			for (int k = 0; k < dates.length; k++) {
				DepartmentObj departmentObj = deparmentList.get(i);
				String curDepartmentId = departmentObj.getDepartmentId();
				//判断是否按区域获取数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if(departmentId != null) {
					if(!curDepartmentId.equalsIgnoreCase(departmentId))
						continue ;
				}
				if(deparmentIdList!=null && deparmentIdList.size() >0) {
					if(StringUtils.isEmpty(curDepartmentId) || !StringUtils.isNumeric(curDepartmentId)) {
						continue;
					}
					if(!deparmentIdList.contains(curDepartmentId)) {
						continue ;
					}
				}
				mcs = new MatConfirms();
				mcs.setMatUseDate(dates[k].replaceAll("-", "/"));
				mcs.setDepartmentId(curDepartmentId);
				int totalDistDishSchNum = 0, totalNoConMatSchNum = 0, totalConMatSchNum = 0, totalMatPlanNum = 0, matPlanConfirmNum = 0, matPlanNoConfirmNum = 0;
				totalDistDishSchNum = distDishSchNums[k][i];
				totalNoConMatSchNum = noConMatSchNums[k][i];
				totalConMatSchNum = conMatSchNums[k][i];
				totalMatPlanNum = totalMatPlanNums[k][i];
				matPlanConfirmNum = matPlanConfirmNums[k][i];
				matPlanNoConfirmNum = matPlanNoConfirmNums[k][i];				
				//已排菜学校
				mcs.setDishSchNum(totalDistDishSchNum);
				//未确认用料学校
				mcs.setNoConMatSchNum(totalNoConMatSchNum);
				//已确认用料学校
				mcs.setConMatSchNum(totalConMatSchNum);
				//应确认学校
				mcs.setShouldAccSchNum(mcs.getNoConMatSchNum() + mcs.getConMatSchNum());
				//用料计划总数
				mcs.setTotalMatPlanNum(totalMatPlanNum);
				//用料计划确认数
				mcs.setConMatPlanNum(matPlanConfirmNum);
				//用料计划未确认数
				mcs.setNoConMatPlanNum(matPlanNoConfirmNum);
				//逾期确认数量
				mcs.setExpConMatPlanNum(0);     //待定
				//用料计划确认率
				matPlanConfirmRates[i] = 0;
				if(totalMatPlanNum > 0) {
					matPlanConfirmRates[i] = 100 * ((float) matPlanConfirmNum / (float) totalMatPlanNum);
					BigDecimal bd = new BigDecimal(matPlanConfirmRates[i]);
					matPlanConfirmRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (matPlanConfirmRates[i] > 100)
						matPlanConfirmRates[i] = 100;
				}
				mcs.setMatConRate(matPlanConfirmRates[i]);
				matConfirms.add(mcs);
			}
		}
		//排序
		//SortList<MatConfirms> sortList = new SortList<MatConfirms>();
		//sortList.Sort(matConfirms, methods1, sorts1, dataTypes1);
		// 设置返回数据
		mcsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<MatConfirms> pageBean = new PageBean<MatConfirms>(matConfirms, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		mcsDto.setPageInfo(pageInfo);
		//设置数据
		mcsDto.setMatConfirms(pageBean.getCurPageData());
		//消息ID
		mcsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return mcsDto;
	}
	
	// 用料确认列表函数
	private MatConfirmsDTO matConfirms(String departmentId,String distIdorSCName, String[] dates,
			List<TEduDistrictDo> tedList, int schSelMode, int subLevel, int compDep, 
			String subDistName,String subLevels,String compDeps,String distNames,
			String departmentIds,
			DbHiveMatService dbHiveMatService,Db1Service db1Service) {
		MatConfirmsDTO pdlDto = new MatConfirmsDTO();
		
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		
		//筛选学校模式
		if(days >= 2) {
			if(schSelMode == 0) {    //按主管部门
				pdlDto = matConfirmsByCompDepFromHive(departmentId,distIdorSCName, dates, tedList, subLevel, compDep, subDistName, subLevels, compDeps, dbHiveMatService);
			}
			else if(schSelMode == 1) {  //按所在地
				pdlDto = matConfirmsByLocalityFromHive(departmentId,distIdorSCName, dates, tedList, distNames, dbHiveMatService);			
			}else if(schSelMode == 2) {  //按管理部门
				List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
				DepartmentObj departmentObj = new DepartmentObj();
				if(CommonUtil.isNotEmpty(departmentId)) {
					departmentObj.setDepartmentId(departmentId);
				}
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,departmentIdsList, -1, -1);	
				pdlDto = matConfirmsByDepartmentFromHive(departmentId, dates, deparmentList, departmentIds, dbHiveMatService);
			}  	   
		}else {
			if(schSelMode == 0) {    //按主管部门
				pdlDto = matConfirmsByCompDep(departmentId,distIdorSCName, dates, tedList, subLevel, compDep, 
						subDistName,subLevels,compDeps);
			}else if(schSelMode == 1) {  //按所在地
				pdlDto = matConfirmsByLocality(departmentId,distIdorSCName, dates, tedList,distNames);			
			}else if(schSelMode == 2) {  //按管理部门
				List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
				DepartmentObj departmentObj = new DepartmentObj();
				if(CommonUtil.isNotEmpty(departmentId)) {
					departmentObj.setDepartmentId(departmentId);
				}
				List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,departmentIdsList, -1, -1);	
				pdlDto = matConfirmsByDepartment(departmentId, dates, deparmentList, departmentIds);
			}  	
		}
		return pdlDto;
	}
	
	// 用料确认列表模型函数
	public MatConfirmsDTO appModFunc(String token, String startDate, String endDate, String schSelMode, 
			String subLevel, String compDep, String subDistName, String distName, String prefCity, 
			String province, 
			String subLevels,String compDeps,String distNames,String departmentIds,
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service,DbHiveMatService dbHiveMatService) {
		MatConfirmsDTO mcsDto = null;
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
					// 用料确认列表函数
					mcsDto = matConfirms(departmentId,distIdorSCName, dates, tddList, curSchSelMode, curSubLevel, curCompDep, subDistName,
							subLevels,compDeps,distNames,departmentIds,dbHiveMatService,db1Service);
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
					// 用料确认列表函数
					mcsDto = matConfirms(departmentId,distIdorSCName, dates, tddList, curSchSelMode, curSubLevel, curCompDep, subDistName,
							subLevels,compDeps,distNames,departmentIds,dbHiveMatService,db1Service);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		}
		else {    //模拟数据
			//模拟数据函数
			mcsDto = SimuDataFunc();
		}		

		return mcsDto;
	}
}