package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchList;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.PpDishDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.PpDishUseDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.PpDishUseDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FileWRCommSys;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;



//阳光午餐使用统计列表应用模型
public class PpDishUseDetsAppMod {
	private static final Logger logger = LogManager.getLogger(PpDishUseDetsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//三级排序条件
	final String[] methods = {"getDistName", "getSchType", "getPpName"};
	final String[] sorts = {"asc", "asc", "asc"};
	final String[] dataTypes = {"String", "String", "String"};
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//真实模拟数据读取标识
	private static boolean isReadSimuData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20, actPageSize = 0, attrCount = 9;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 1;
	
	//数组数据初始化
	String[] dishDate_Array = {"2018/08/08", "2018/08/08"};
	String[] ppName_Array = {"上海市天山中学", "上海市天山中学"};	
	String[] schGenBraFlag_Array = {"总校", "总校"};
	int[] braCampusNum_Array = {1, 1};
	String[] relGenSchName_Array = {"-", "-"};
	String[] subLevel_Array = {"区属", "区属"};
	String[] compDep_Array = {"长宁区教育局", "长宁区教育局"};
	String[] subDistName_Array = {"-", "-"};
	String[] fblMb_Array = {"学校", "学校"};
	String[] distName_Array = {"长宁区", "长宁区"};
	String[] schType_Array = {"初级中学", "初级中学"};
	String[] schProp_Array = {"公办", "公办"};
	int[] mealFlag_Array = {1, 1};
	String[] optMode_Array = {"外包-外包", "外包-外送"};
	String[] rmcName_Array = {"上海绿捷", "上海神龙"};
	int[] dishFlag_Array = {1, 1};
	
	//团餐公司id到团餐公司名称
	Map<String, String> RmcIdToNameMap = new HashMap<>();
	
	//读取真实模拟数据
	private void GetRealSimuData() {
		String filePathName = SpringConfig.base_dir + "/real_simu_data/biOptAnl_v1_ppDishDets.txt";
		List<String> realSimuDataList = FileWRCommSys.ReadFileByRow(filePathName);
		List<BdSchList> warnSchLics = null;
		logger.info("真实模拟数据文件名：" + filePathName);
		if (warnSchLics == null) {
			warnSchLics = new ArrayList<>();
			// 页总条数
			pageTotal = realSimuDataList.size();
			logger.info("pageTotal = " + pageTotal);
			dishDate_Array = new String[pageSize];
			ppName_Array = new String[pageSize];
			distName_Array = new String[pageSize];
			schType_Array = new String[pageSize];
			schProp_Array = new String[pageSize];
			mealFlag_Array = new int[pageSize];
			optMode_Array = new String[pageSize];
			rmcName_Array = new String[pageSize];			
			dishFlag_Array = new int[pageSize];
			actPageSize = 0;
			// 获取当前页数据
			for (int i = (curPageNum - 1) * pageSize; i < pageTotal && i < curPageNum * pageSize; i++) {
				String curStrLine = realSimuDataList.get(i);
				String[] curStrLines = curStrLine.split(";");
				if (curStrLines.length >= attrCount) {
					dishDate_Array[actPageSize] = curStrLines[0];
					ppName_Array[actPageSize] = curStrLines[1];
					distName_Array[actPageSize] = curStrLines[2];
					schType_Array[actPageSize] = curStrLines[3];
					schProp_Array[actPageSize] = curStrLines[4];
					if (curStrLines[5].compareTo("是") == 0)
						mealFlag_Array[actPageSize] = 1;
					else
						mealFlag_Array[actPageSize] = 0;
					optMode_Array[actPageSize] = curStrLines[6];
					rmcName_Array[actPageSize] = curStrLines[7];
					if (curStrLines[8].compareTo("已排菜") == 0)
						dishFlag_Array[actPageSize] = 1;
					else
						dishFlag_Array[actPageSize] = 0;
					actPageSize++;
				}
			}
		}
	}
	
	//模拟数据函数
	private PpDishUseDetsDTO SimuDataFunc() {
		PpDishUseDetsDTO pddDto = new PpDishUseDetsDTO();
		// 设置返回数据
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		List<PpDishUseDets> ppDishDets = new ArrayList<>();
		//读取真实模拟数据
		if(isReadSimuData)
			GetRealSimuData();
		//赋值
		for (int i = 0; i < Math.max(dishDate_Array.length, actPageSize); i++) {
			PpDishUseDets pdd = new PpDishUseDets();
			pdd.setDishDate(dishDate_Array[i]);
			pdd.setPpName(ppName_Array[i]);			
			pdd.setSchGenBraFlag(schGenBraFlag_Array[i]);
			pdd.setBraCampusNum(braCampusNum_Array[i]);
			pdd.setRelGenSchName(relGenSchName_Array[i]);
			pdd.setSubLevel(subLevel_Array[i]);
			pdd.setCompDep(compDep_Array[i]);
			pdd.setSubDistName(subDistName_Array[i]);
			pdd.setFblMb(fblMb_Array[i]);			
			pdd.setDistName(distName_Array[i]);
			pdd.setSchType(schType_Array[i]);
			pdd.setSchProp(schProp_Array[i]);
			pdd.setMealFlag(mealFlag_Array[i]);
			pdd.setOptMode(optMode_Array[i]);
			pdd.setRmcName(rmcName_Array[i]);
			pdd.setDishFlag(dishFlag_Array[i]);
			ppDishDets.add(pdd);
		}
		//设置模拟数据
		pddDto.setPpDishDets(ppDishDets);
		//分页
		PageInfo pageInfo = new PageInfo();
		if(actPageSize == 0)
			pageTotal = dishDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		pddDto.setPageInfo(pageInfo);
		//消息ID
		pddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return pddDto;
	}
	
	// 项目点排菜详情列表函数（方案2）
	PpDishUseDetsDTO ppDishDetsFromRedis(String departmentId,String distIdorSCName, String[] dates, int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			List<TEduDistrictDo> tddList, Db1Service db1Service, SaasService saasService, String ppName, 
			String rmcId,String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			String optModes,String subDistNames,
			String schoolId,Integer materialStatus,Integer acceptStatus,
			Integer assignStatus,Integer dispStatus,Integer haveReserve,
			String reason,String departmentIds) {
		PpDishUseDetsDTO pddDto = new PpDishUseDetsDTO();
		// 排菜学校
		Map<String, String> redisMap = new HashMap<>();
		int dateCount = dates.length;
		String key = null;
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
    	List<Object> schPropsList=CommonUtil.changeStringToList(schProps);
    	List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
    	List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
    	List<PpDishUseDets> dishList = new ArrayList<>();
		// 时间段内各区排菜学校数量
		for(int k = 0; k < dateCount; k++) {
			//redis结构
			//Key： 2019-08-08_allUseData
			//Feild: d375b2b6-36f4-472a-a40b-00cc30d4c26a (schoolid)
			//Value:
			//school_name:上海市宝山区第二中心小学分校,area:3,level_name:7,
			//school_nature_name:0,department_master_id:0,department_slave_id_name:0,
			//have_class:0,have_platoon:0,material_status:1,haul_status:-1,
			//have_reserve:0,supplier_name:上海安友营养食品有限公司,
			//reason:null
			key = dates[k] + "_allUseData";
			redisMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (redisMap != null) {
				for (Map.Entry<String, String> entry : redisMap.entrySet()) {
					//行处理
					Map<String,String> valueMap= CommonUtil.getStringToMap(entry.getValue());
					// 排菜学校ID列表
					if(valueMap!=null && valueMap.size() > 1) {
						
						/**
						 * 判断条件
						 */
						//判断项目点名称（判断索引0）
						if(CommonUtil.isNotEmpty(ppName)) {
							if(CommonUtil.isEmpty(valueMap.get("school_name")) || valueMap.get("school_name").indexOf(ppName) == -1) {
								continue;
							}
						}
						//判断团餐公司（判断索引1）
						if(rmcName != null) {
							if(CommonUtil.isEmpty(valueMap.get("supplier_name")) || valueMap.get("supplier_name").indexOf(rmcName) == -1) {
								continue;
							}
						}
						//判断学校类型（判断索引2）
						if(schType != -1) {
							if(CommonUtil.isEmpty(valueMap.get("level_name")) ||  Integer.valueOf(valueMap.get("level_name")) != schType) {
								continue;
							}
						}else if(schTypesList!=null && schTypesList.size() >0) {
							if(CommonUtil.isEmpty(valueMap.get("level_name")) ||  !schTypesList.contains(String.valueOf(valueMap.get("level_name")))) {
								continue;
							}
						}	
						
						
						//判断是否供餐（判断索引3）
						if(mealFlag != -1) {
							if(CommonUtil.isEmpty(valueMap.get("have_class")) ||  Integer.valueOf(valueMap.get("have_class")) != mealFlag) {
								continue;
							}
						}
						//判断所属（判断索引5）
						if(subLevel != -1) {
							if(CommonUtil.isEmpty(valueMap.get("department_master_id")) ||  Integer.valueOf(valueMap.get("department_master_id")) != subLevel) {
								continue;
							}
						}
						if(subLevelsList!=null && subLevelsList.size() >0) {
							if(CommonUtil.isEmpty(valueMap.get("department_master_id")) ||  !subLevelsList.contains(valueMap.get("department_master_id"))) {
								continue;
							}
						}
						//判断主管部门（判断索引6）
						if(compDep != -1) {
							if(CommonUtil.isEmpty(valueMap.get("department_slave_id_name")) ||  Integer.valueOf(valueMap.get("department_slave_id_name")) != compDep) {
								continue;
							}
						}
						if(compDepsList!=null && compDepsList.size() >0) {
							
							if(CommonUtil.isEmpty(valueMap.get("department_slave_id_name")) ||  CommonUtil.isEmpty(valueMap.get("department_master_id"))) {
								continue;
							}
							if(!compDepsList.contains(valueMap.get("department_master_id")+"_"+valueMap.get("department_slave_id_name"))) {
								continue;
							}
							
						}
						//判断学校性质（判断索引10）
						if(schProp != -1) {
							if(CommonUtil.isEmpty(valueMap.get("school_nature_name")) ||  Integer.valueOf(valueMap.get("school_nature_name")) != schProp) {
								continue;
							}
						}
						if(schPropsList!=null && schPropsList.size() >0) {
							if(CommonUtil.isEmpty(valueMap.get("school_nature_name")) ||  !schPropsList.contains(valueMap.get("school_nature_name"))) {
								continue;
							}
						}
						
						//区
						//判断学校性质（判断索引10）
						if(CommonUtil.isNotEmpty(distIdorSCName)) {
							if(CommonUtil.isEmpty(valueMap.get("area")) ||  !valueMap.get("area").equals(distIdorSCName)) {
								continue;
							}
						}
						if(distNamesList!=null && distNamesList.size() >0) {
							if(CommonUtil.isEmpty(valueMap.get("area")) ||  !distNamesList.contains(valueMap.get("area"))) {
								continue;
							}
						}
						//学校
						//判断学校性质（判断索引10）
						if(CommonUtil.isNotEmpty(schoolId)) {
							if(CommonUtil.isEmpty(entry.getKey()) ||  !entry.getKey().equals(schoolId)) {
								continue;
							}
						}
						
						
						//排菜情况
						//判断是否供餐（判断索引3）
						if(dishFlag != -1) {
							if(CommonUtil.isEmpty(valueMap.get("have_platoon")) ||  Integer.valueOf(valueMap.get("have_platoon")) != dishFlag) {
								continue;
							}
						}
						//haul_status  -2 信息不完整 -1 未指派 0 已指派（未配送） 1配送中 2 待验收（已配送）3已验收 -4已取消
				        //验收状态
				        if(acceptStatus !=null && acceptStatus != -1) {
				        	if(CommonUtil.isEmpty(valueMap.get("haul_status"))) {
								continue;
							}
				        	
				        	if(acceptStatus == 0 ) {
				        		//未验收
				        		if(Integer.valueOf(valueMap.get("haul_status")) ==3) {
									continue;
								}
				        	}else {
				        		//已验收
				        		if(Integer.valueOf(valueMap.get("haul_status")) !=3) {
									continue;
								}
				        	}
				        }
				        
				        
				        //指派状态
				        if(assignStatus !=null && assignStatus != -1) {
				        	if(CommonUtil.isEmpty(valueMap.get("haul_status"))) {
								continue;
							}
				        	
				        	if(assignStatus == 0 ) {
				        		//未指派
				        		if(Integer.valueOf(valueMap.get("haul_status")) >=0) {
									continue;
								}
				        	}else {
				        		//已指派
				        		if(Integer.valueOf(valueMap.get("haul_status")) <0) {
									continue;
								}
				        	}
				        }
				        //配送状态
				        if(dispStatus !=null && dispStatus != -1) {
				        	if(CommonUtil.isEmpty(valueMap.get("haul_status"))) {
								continue;
							}
				        	
				        	if(dispStatus == 0 ) {
				        		//未配送
				        		if(Integer.valueOf(valueMap.get("haul_status")) ==2 || Integer.valueOf(valueMap.get("haul_status")) ==3) {
									continue;
								}
				        	}else {
				        		//已配送
				        		if(Integer.valueOf(valueMap.get("haul_status")) !=2 && Integer.valueOf(valueMap.get("haul_status")) !=3) {
									continue;
								}
				        	}
				        }
				        //留样状态
				        if(haveReserve !=null && haveReserve != -1) {
				        	if(CommonUtil.isEmpty(valueMap.get("have_reserve"))) {
								continue;
							}
				        	
				        	if(haveReserve == 0 ) {
				        		//未验收
				        		if(Integer.valueOf(valueMap.get("have_reserve")) ==1) {
									continue;
								}
				        	}else {
				        		//已验收
				        		if(Integer.valueOf(valueMap.get("have_reserve")) !=1) {
									continue;
								}
				        	}
				        }
				        //用料确认状态
				        if(materialStatus !=null && materialStatus != -1) {
				        	if(CommonUtil.isEmpty(valueMap.get("material_status"))) {
								continue;
							}
				        	if(materialStatus == 0 ) {
				        		//未确认
				        		if(Integer.valueOf(valueMap.get("material_status")) ==2) {
									continue;
								}
				        	}else {
				        		//已确认
				        		if(Integer.valueOf(valueMap.get("material_status")) !=2) {
									continue;
								}
				        	}
				        }
				        
						//不供餐原因类型
						if(CommonUtil.isNotEmpty(reason)) {
							//如果是供餐，不供餐原因作废（为了解决阳光午餐，供餐学校，存在不供餐原因的问题）
							if(CommonUtil.isNotEmpty(valueMap.get("have_class")) &&  Integer.valueOf(valueMap.get("have_class")) == 1) {
								continue;
							}
							
							if(CommonUtil.isEmpty(valueMap.get("reason"))) {
								continue;
							}
							if(AppModConfig.noDishReasonTypeTwo.contains(String.valueOf(reason))) {
								//非其他
								if(!reason.equals(valueMap.get("reason"))) {
									continue;
								}
							}else {
								//其他
								if(AppModConfig.noDishReasonTypeTwo.contains(String.valueOf(valueMap.get("reason")))) {
									continue;
								}
							}
						}
						
						//管理部门
						if(CommonUtil.isNotEmpty(departmentId)) {
							if(CommonUtil.isEmpty(valueMap.get("department_id")) ||  !valueMap.get("department_id").equals(departmentId)) {
								continue;
							}
						}
						if(departmentIdList!=null && departmentIdList.size() >0) {
							if(CommonUtil.isEmpty(valueMap.get("department_id")) ||  !departmentIdList.contains(valueMap.get("department_id"))) {
								continue;
							}
						}
						
						
						/**
						 * 组织数据
						 */
						PpDishUseDets cdsd = new PpDishUseDets();
						//排菜日期
						cdsd.setDishDate(dates[k].replaceAll("-", "/"));
						//项目点名称
						cdsd.setPpName("-");
			            if(CommonUtil.isNotEmpty(valueMap.get("school_name"))) {
			            	cdsd.setPpName(valueMap.get("school_name"));
			            }
						//所属
						cdsd.setSubLevel("-");
						if(CommonUtil.isNotEmpty(valueMap.get("department_master_id")) && !"-1".equals(valueMap.get("department_master_id"))) {
							cdsd.setSubLevel(AppModConfig.subLevelIdToNameMap.get(Integer.valueOf(valueMap.get("department_master_id"))));
						}
						//主管部门
						cdsd.setCompDep("-");
						if(CommonUtil.isNotEmpty(cdsd.getSubLevel())&& !"-1".equals(valueMap.get("department_master_id"))
								&& !"-1".equals(valueMap.get("department_slave_id_name"))) {
							if("0".equals(valueMap.get("department_master_id"))) {
								cdsd.setCompDep(AppModConfig.compDepIdToNameMap0.get(valueMap.get("department_slave_id_name")));
							}else if ("1".equals(valueMap.get("department_master_id"))) {
								cdsd.setCompDep(AppModConfig.compDepIdToNameMap1.get(valueMap.get("department_slave_id_name")));
							}else if ("2".equals(valueMap.get("department_master_id"))) {
								cdsd.setCompDep(AppModConfig.compDepIdToNameMap2.get(valueMap.get("department_slave_id_name")));
							}else if ("3".equals(valueMap.get("department_master_id"))) {
								cdsd.setCompDep(AppModConfig.compDepIdToNameMap3.get(valueMap.get("department_slave_id_name")));
							}
						}
						
						//区域
						cdsd.setDistName("-");
						if(CommonUtil.isNotEmpty(valueMap.get("area"))) {
				            	cdsd.setDistName(valueMap.get("area"));
				        }
						//学校类型（学制）
						cdsd.setSchType("-");
						if(CommonUtil.isNotEmpty(valueMap.get("level_name"))) {
							cdsd.setSchType(AppModConfig.schTypeIdToNameMap.get(Integer.valueOf(valueMap.get("level_name"))));		
						}
						//学校性质
						cdsd.setSchProp("-");
						if(CommonUtil.isNotEmpty(valueMap.get("school_nature_name")) && !"-1".equals(valueMap.get("school_nature_name"))) {
							cdsd.setSchProp(AppModConfig.getSchProp(valueMap.get("school_nature_name")));
						}
						//是否供餐
						cdsd.setMealFlag(0);
			            if(CommonUtil.isNotEmpty(valueMap.get("have_class"))) {
			            	cdsd.setMealFlag(CommonUtil.isEmpty(valueMap.get("have_class"))?0:Integer.valueOf(valueMap.get("have_class")));
			            }
						
						
						//团餐公司名称
			            cdsd.setRmcName("-");
			            if(CommonUtil.isNotEmpty(valueMap.get("supplier_name"))) {
			            	cdsd.setRmcName(valueMap.get("supplier_name"));
			            }
			            
						//是否排菜
						cdsd.setDishFlag(0);
			            if(CommonUtil.isNotEmpty(valueMap.get("have_platoon"))) {
			            	cdsd.setDishFlag(CommonUtil.isEmpty(valueMap.get("have_platoon"))?0:Integer.valueOf(valueMap.get("have_platoon")));
			            }
						//配送状态 0:未配送，1:已配送
			            cdsd.setDispPlanStatus(0);
						//指派状态 0:未派送，1:已派送
			            cdsd.setAssignPlanStatus(0);
						//验收情况 0:未验收，1:已验收
			            cdsd.setAcceptStatus(0);
						if(CommonUtil.isNotEmpty(valueMap.get("haul_status"))) {
							if( Integer.valueOf(valueMap.get("haul_status")) == 3) {
								cdsd.setAcceptStatus(1);
							}
							
							if(Integer.valueOf(valueMap.get("haul_status")) == 0 || Integer.valueOf(valueMap.get("haul_status")) == 1
									||Integer.valueOf(valueMap.get("haul_status")) == 2 || Integer.valueOf(valueMap.get("haul_status")) == 3) {
								cdsd.setAssignPlanStatus(1);
							}
							
							if(Integer.valueOf(valueMap.get("haul_status")) == 2 || Integer.valueOf(valueMap.get("haul_status")) == 3) {
								cdsd.setDispPlanStatus(1);
							}
						}
			            
			        	//是否留样
			        	cdsd.setHaveReserve(0);
			            if(CommonUtil.isNotEmpty(valueMap.get("have_reserve"))) {
			            	cdsd.setHaveReserve(CommonUtil.isEmpty(valueMap.get("have_reserve"))?0:Integer.valueOf(valueMap.get("have_reserve")));
			            }
			            
			            //用料确认情况 0:未确认，1:已确认
			        	cdsd.setMaterialStatus(0);
			            if(CommonUtil.isNotEmpty(valueMap.get("material_status"))) {
			            	cdsd.setMaterialStatus(CommonUtil.isEmpty(valueMap.get("material_status"))?0:(Integer.valueOf(valueMap.get("material_status")) !=2?0:1));
			            }
			        	//供餐备注
			        	cdsd.setReason("");
			            if(CommonUtil.isNotEmpty(valueMap.get("reason"))) {
							//如果是供餐，不供餐原因作废（为了解决阳光午餐，供餐学校，存在不供餐原因的问题）
							if(CommonUtil.isNotEmpty(valueMap.get("have_class")) &&  Integer.valueOf(valueMap.get("have_class")) == 1) {
							}else {
								cdsd.setReason(valueMap.get("reason"));
							}
			            }
			            
						//管理部门
						cdsd.setDepartmentId("-");
						if(CommonUtil.isNotEmpty(valueMap.get("department_id"))) {
				            	cdsd.setDepartmentId(valueMap.get("department_id"));
				        }
						
			            dishList.add(cdsd);
					}
				}
			}
		}
		//排序
    	SortList<PpDishUseDets> sortList = new SortList<PpDishUseDets>();  
    	sortList.Sort3Level(dishList, methods, sorts, dataTypes);
		//时戳
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<PpDishUseDets> pageBean = null;
		if(curPageNum == -1 || pageSize == -1) {
			pageBean = new PageBean<PpDishUseDets>(dishList, 1, dishList.size());
		}else {
			pageBean = new PageBean<PpDishUseDets>(dishList, curPageNum, pageSize);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		pddDto.setPageInfo(pageInfo);
		//设置数据
		pddDto.setPpDishDets(pageBean.getCurPageData());
		//消息ID
		pddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return pddDto;
	}	
	
	// 项目点排菜详情列表函数（方案2）
	PpDishUseDetsDTO ppDishDetsFromHive(String departmentId,String distIdorSCName, String[] dates, int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			List<TEduDistrictDo> tddList, Db1Service db1Service, SaasService saasService, String ppName, 
			String rmcId,String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			String optModes,String subDistNames,
			String schoolId,Integer materialStatus,Integer acceptStatus,
			Integer assignStatus,Integer dispStatus,Integer haveReserve,String reason,
			String departmentIds,
			DbHiveDishService dbHiveDishService) {
		PpDishUseDetsDTO pddDto = new PpDishUseDetsDTO();
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
    	List<Object> schPropsList=CommonUtil.changeStringToList(schProps);
    	List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
    	List<Object> optModesList=CommonUtil.changeStringToList(optModes);
    	List<Object> subDistNamesList=CommonUtil.changeStringToList(subDistNames);
    	List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
    	//学校编号集合
    	
		//所有学校id
		List<TEduSchoolDo> tesDoList = new ArrayList<TEduSchoolDo>();
		tesDoList = db1Service.getTEduSchoolDoListByDs1(null,null,null,5);
		Map<String,TEduSchoolDo> schMap = tesDoList.stream().collect(Collectors.toMap(TEduSchoolDo::getId,(b)->b));
		
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

		List<PpDishCommonDets> dishList = new ArrayList<>();
		//总校分校标识转换
		//hive库中0：分校 1：总校
		//接口入参：1：总校 2：分校
		if(schGenBraFlag == 1) {
			schGenBraFlag =0;
		}else if (schGenBraFlag == 2) {
			schGenBraFlag =1;
		}
		
		//转换所属区，前段传递名称，数据库存储编号
		List<Object> newSubDistNamesList = new ArrayList<Object>();
		if(subDistNamesList !=null ) {
			for(Object subDistNameObj  : subDistNamesList) {
				newSubDistNamesList.add(AppModConfig.distNameToIdMap.get(subDistNameObj.toString()));
			}
		}
		
		Integer startNum = (curPageNum-1)*pageSize;
		Integer endNum = curPageNum*pageSize;
		if(curPageNum == -1 || pageSize == -1) {
			startNum = -1;
			endNum = -1;
		}
		/**
		 * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据列表
		 */
		dishList = dbHiveDishService.getDishUseDetsList(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
				subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, dishFlag,schoolId, ppName,
				rmcId, rmcName, schType, mealFlag, optMode, sendFlag, distNamesList, 
				subLevelsList, compDepsList, schPropsList, schTypesList, optModesList, 
				newSubDistNamesList,acceptStatus,assignStatus,dispStatus,
				materialStatus,haveReserve,reason,
				departmentId,departmentIdsList,
				startNum,endNum );
		dishList.removeAll(Collections.singleton(null));
		
		Integer pageTotalTemp = dbHiveDishService.getDishUseDetsCount(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
				subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, dishFlag,schoolId, ppName,
				rmcId, rmcName, schType, mealFlag, optMode, sendFlag, distNamesList, 
				subLevelsList, compDepsList, schPropsList, schTypesList, optModesList, 
				newSubDistNamesList,acceptStatus,assignStatus,dispStatus,
				materialStatus,haveReserve,reason,departmentId,departmentIdsList);
		
		if(pageTotalTemp!=null) {
			pageTotal = pageTotalTemp;
		}
		
		List<PpDishUseDets> ppDishDets = new ArrayList<>();
		for(PpDishCommonDets commonDets : dishList) {
			PpDishUseDets pdd = new PpDishUseDets();
			try {
				BeanUtils.copyProperties(pdd, commonDets);
				//关联总校
				String relGenSchName = "";
				if(CommonUtil.isNotEmpty(pdd.getRelGenSchName()) && !"-".equals(pdd.getRelGenSchName())) {
					if(schMap.get(pdd.getRelGenSchName()) !=null) {
						relGenSchName = schMap.get(pdd.getRelGenSchName()).getSchoolName();
					}
				}
				
				if(CommonUtil.isNotEmpty(relGenSchName)) {
					pdd.setRelGenSchName(relGenSchName);
				}else {
					pdd.setRelGenSchName("-");
				}
				
				//配送状态 0:未配送，1:已配送
				pdd.setDispPlanStatus(0);
				//指派状态 0:未派送，1:已派送
				pdd.setAssignPlanStatus(0);
				//验收情况 0:未验收，1:已验收
				pdd.setAcceptStatus(0);
				if(commonDets.getHaulStatus() != null) {
					if( commonDets.getHaulStatus() == 3) {
						pdd.setAcceptStatus(1);
					}
					
					if(commonDets.getHaulStatus() == 0 || commonDets.getHaulStatus() == 1
							||commonDets.getHaulStatus() == 2 || commonDets.getHaulStatus() == 3) {
						pdd.setAssignPlanStatus(1);
					}
					
					if(commonDets.getHaulStatus() == 2 || commonDets.getHaulStatus() == 3) {
						pdd.setDispPlanStatus(1);
					}
				}
				
				//所属区域名称
				pdd.setSubDistName("-");
				if(CommonUtil.isNotEmpty(pdd.getSubDistName())) {
					pdd.setSubDistName(AppModConfig.distIdToNameMap.get(pdd.getSubDistName()));
				}
				ppDishDets.add(pdd);
				
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			}
		}
		
		//时戳
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		pddDto.setPageInfo(pageInfo);
		//设置数据
		pddDto.setPpDishDets(ppDishDets);
		//消息ID
		pddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return pddDto;
	}	

	//项目点排菜详情列表模型函数
	public PpDishUseDetsDTO appModFunc(String token,PpDishDets ppDishDets,Db1Service db1Service, 
			Db2Service db2Service, SaasService saasService,
			DbHiveDishService dbHiveDishService) {
		PpDishUseDetsDTO pddDto = null;
        //页号
        String page = ppDishDets.getPage();
        if (page == null) {
            page = "1";
        }
        //分页大小
        String pageSize = ppDishDets.getPageSize();
        if (pageSize == null) {
            pageSize = "20";
        }
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if (isRealData) { // 真实数据
		    //开始日期
	        String startDate = ppDishDets.getStartSubDate();
	        //结束日期
	        String endDate = ppDishDets.getEndSubDate();
	        //项目点名称
	        String ppName = ppDishDets.getPpName();
	        //区域名称
	        String distName = ppDishDets.getDistName();
	        //地级城市
	        String prefCity = ppDishDets.getPrefCity();
	        //省或直辖市
	        String province = ppDishDets.getProvince();
	        //所属，0:其他，1:部属，2:市属，3: 区属
	        String subLevel = ppDishDets.getSubLevel();
	        //主管部门，0:市教委，1:商委，2:教育部
	        String compDep = ppDishDets.getCompDep();
	        //总分校标识，0:无，1:总校，2:分校
	        String schGenBraFlag = ppDishDets.getSchGenBraFlag();
	        //所属区域名称
	        String subDistName = ppDishDets.getSubDistName();
	        //证件主体，0:学校，1:外包
	        String fblMb = ppDishDets.getFblMb();
	        //学校性质，0:公办，1:民办，2:其他
	        String schProp = ppDishDets.getSchProp();        
	        //是否排菜，0:未排菜，1:已排菜
	        String dishFlag = ppDishDets.getDishFlag();
	        //团餐公司名称
	        String rmcName = ppDishDets.getRmcName();
	        //学校类型（学制）
	        String schType = ppDishDets.getSchType();
	        //是否供餐，0:否，1:是
	        String mealFlag = ppDishDets.getMealFlag();
	        //经营模式
	        String optMode = ppDishDets.getOptMode();
	        //发送状态，0:未发送，1:已发送
	        String sendFlag = ppDishDets.getSendFlag();
	        
	        //区域名称 格式：[“1”,”2”……]
	        String distNames = ppDishDets.getDistNames();
	        //所属 格式：[“1”,”2”……]
	        String subLevels = ppDishDets.getSubLevels();
	        //主管部门 格式：[“1”,”2”……]
	        String compDeps = ppDishDets.getCompDeps();
	        //学校性质 格式：[“1”,”2”……]
	        String schProps = ppDishDets.getSchProps();
	        //学校类型 格式：[“1”,”2”……]
	        String schTypes = ppDishDets.getSchTypes();
	        //经营模式 格式：[“1”,”2”……]
	        String optModes = ppDishDets.getOptModes();
	        //所属区(sub) 格式：[“1”,”2”……]
	        String subDistNames = ppDishDets.getSubDistNames();
	        //学校编号
	        String schoolId = ppDishDets.getSchoolId();
	        
	        //用料计划确认情况，0:待确认，1:已确认
	        Integer materialStatus = ppDishDets.getMaterialStatus();
	        
	        //验收情况，0:未验收，1:已验收
	        Integer acceptStatus = ppDishDets.getAcceptStatus();
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
					logger.info("dates[" + i + "] = " + dates[i]);
				}
			}
			// 省或直辖市
			if(province == null)
				province = "上海市";
			//所属，0:其他，1:部属，2:市属，3: 区属
			int curSubLevel = -1;
			if(subLevel != null)
				curSubLevel = Integer.parseInt(subLevel);
			//主管部门，0:市教委，1:商委，2:教育部
			int curCompDep = -1;
			if(compDep != null)
				curCompDep = Integer.parseInt(compDep);
			//总分校标识，0:无，1:总校，2:分校
			int curSchGenBraFlag = -1;
			if(schGenBraFlag != null)
				curSchGenBraFlag = Integer.parseInt(schGenBraFlag);
			//证件主体，0:学校，1:外包
			int curFblMb = -1;
			if(fblMb != null)
				curFblMb = Integer.parseInt(fblMb);
			//学校性质，0:公办，1:民办，2:其他
			int curSchProp = -1;
			if(schProp != null)
				curSchProp = Integer.parseInt(schProp);			
			//是否排菜标识
			int curDishFlag = -1;
			if(dishFlag != null)
				curDishFlag = Integer.parseInt(dishFlag);
			//学校类型（学制）
			int curSchType = -1;
			if(schType != null)
				curSchType = Integer.parseInt(schType);
			//是否供餐
			int curMealFlag = -1;
			if(mealFlag != null)
				curMealFlag = Integer.parseInt(mealFlag);
			//经营模式
			int curOptMode = -1;
			if(optMode != null)
				curOptMode = Integer.parseInt(optMode);
			//是否发送
			int curSendFlag = -1;
			if(sendFlag != null)
				curSendFlag = Integer.parseInt(sendFlag);
			
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
			String departmentId = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) {    // 按区域，省或直辖市处理
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
					// 项目点排菜详情列表函数
					if(methodIndex == 1) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						if(days >= 2) {
							/**
							 * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据列表
							 */
							pddDto = ppDishDetsFromHive(departmentId,distIdorSCName, dates, curSubLevel, curCompDep, curSchGenBraFlag, subDistName, 
									curFblMb, curSchProp, curDishFlag, tddList, db1Service, saasService, ppName,ppDishDets.getRmcId(), rmcName, 
									curSchType, curMealFlag, curOptMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,
									schoolId,materialStatus,acceptStatus,ppDishDets.getAssignStatus(),ppDishDets.getDispStatus(),ppDishDets.getHaveReserve(),
									ppDishDets.getReason(),
									ppDishDets.getDepartmentIds(),
									dbHiveDishService);		
						}else {
							pddDto = ppDishDetsFromRedis(departmentId,distIdorSCName, dates, curSubLevel, curCompDep, curSchGenBraFlag, subDistName, 
									curFblMb, curSchProp, curDishFlag, tddList, db1Service, saasService, ppName,ppDishDets.getRmcId(), rmcName, 
									curSchType, curMealFlag, curOptMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,
									schoolId,materialStatus,acceptStatus,ppDishDets.getAssignStatus(),ppDishDets.getDispStatus(),ppDishDets.getHaveReserve(),
									ppDishDets.getReason(),ppDishDets.getDepartmentIds());		
						}
					}else if(methodIndex == 2) {
						
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
					// 项目点排菜详情列表函数
					if(methodIndex == 1) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						if(days >= 2) {
							pddDto = ppDishDetsFromHive(departmentId,distIdorSCName, dates, curSubLevel, curCompDep, curSchGenBraFlag,
									subDistName, curFblMb, curSchProp, curDishFlag, tddList, db1Service, saasService, 
									ppName,ppDishDets.getRmcId(), rmcName, curSchType, curMealFlag, curOptMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,
									schoolId,materialStatus,acceptStatus,ppDishDets.getAssignStatus(),
									ppDishDets.getDispStatus(),ppDishDets.getHaveReserve(),ppDishDets.getReason(),
									ppDishDets.getDepartmentIds(),
									dbHiveDishService);		
						}else {
							pddDto = ppDishDetsFromRedis(departmentId,distIdorSCName, dates, curSubLevel, curCompDep, curSchGenBraFlag,
									subDistName, curFblMb, curSchProp, curDishFlag, tddList, db1Service, saasService, 
									ppName, ppDishDets.getRmcId(),rmcName, curSchType, curMealFlag, curOptMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,
									schoolId,materialStatus,acceptStatus,ppDishDets.getAssignStatus(),
									ppDishDets.getDispStatus(),ppDishDets.getHaveReserve(),ppDishDets.getReason(),
									ppDishDets.getDepartmentIds());	
						}
					}else if (methodIndex == 2) {
						
					}
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}			
		} else { // 模拟数据
			//模拟数据函数
			pddDto = SimuDataFunc();
		}

		return pddDto;
	}
}