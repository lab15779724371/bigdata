package com.tfit.BdBiProcSrvShEduOmc.appmod.im;


//import static org.assertj.core.api.Assertions.entry;

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
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRsDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.DishRsDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//菜品留样详情列表应用模型
public class DishRsDetsAppMod {
	private static final Logger logger = LogManager.getLogger(DishRsDetsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//三级排序条件
	final String[] methods = {"getDistName", "getSchType", "getPpName"};
	final String[] sorts = {"asc", "asc", "asc"};
	final String[] dataTypes = {"String", "String", "String"};
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化	
	String[] repastDate_Array = {"2018/09/03", "2018/09/03"};
	String[] ppName_Array = {"上海市徐汇区向阳小学", "上海市徐汇区向阳小学"};
	String[] distName_Array = {"徐汇区", "徐汇区"};
	String[] schType_Array = {"小学", "小学"};
	String[] schProp_Array = {"公办", "民办"};
	String[] optMode_Array = {"自营", "自营"};
	String[] rmcName_Array = {"上海绿捷实业发展有限公司", "上海龙神餐饮有限公司"};
	String[] caterType_Array = {"午餐", "午餐"};
	String[] menuName_Array = {"国内班", "国内班"};
	String[] dishName_Array = {"番茄炒蛋", "青椒肉丝"};
	int[] dishNum_Array = {88, 88};
	int[] rsFlag_Array = {1, 1};
	String[] rsNum_Array = {"250g", "250g"};
	String[] rsTime_Array = {"2018/09/03 11:11", "2018/09/03 11:05"};
	String[] rsExplain_Array = {"", ""};
	String[] rsUnit_Array = {"上海市徐汇区向阳小学", "上海市徐汇区向阳小学"};
	String[] rsPerson_Array = {"admin", "admin"};
	
	//模拟数据函数
	private DishRsDetsDTO SimuDataFunc() {
		DishRsDetsDTO drdDto = new DishRsDetsDTO();
		//设置返回数据
		drdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		List<DishRsDets> dishRsDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < repastDate_Array.length; i++) {
			DishRsDets drd = new DishRsDets();			
			drd.setRepastDate(repastDate_Array[i]);
			drd.setPpName(ppName_Array[i]);
			drd.setDistName(distName_Array[i]);
			drd.setSchType(schType_Array[i]);
			drd.setSchProp(schProp_Array[i]);
			drd.setOptMode(optMode_Array[i]);
			drd.setRmcName(rmcName_Array[i]);
			drd.setCaterType(caterType_Array[i]);
			drd.setMenuName(menuName_Array[i]);
			drd.setDishName(dishName_Array[i]);
			drd.setDishNum(dishNum_Array[i]);
			drd.setRsFlag(rsFlag_Array[i]);
			drd.setRsNum(rsNum_Array[i]);
			drd.setRsTime(rsTime_Array[i]);
			drd.setRsExplain(rsExplain_Array[i]);
			drd.setRsUnit(rsUnit_Array[i]);
			drd.setRsPerson(rsPerson_Array[i]);
			dishRsDets.add(drd);
		}
		//设置数据
		drdDto.setDishRsDets(dishRsDets);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = repastDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		drdDto.setPageInfo(pageInfo);
		//消息ID
		drdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return drdDto;
	}
	
	// 菜品留样详情列表函数
	DishRsDetsDTO dishRsDets(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, 
			Db1Service db1Service, SaasService saasService, 
			int subLevel, int compDep, int schGenBraFlag, String subDistName, 
			int fblMb, int schProp, String ppName, String rmcName, int rsFlag,
			String caterType, int schType, String menuName, int optMode, String rsUnit,
			String distNames,String subLevels,String compDeps,String schProps,String caterTypes,
			String schTypes,String menuNames,String optModes,
			String departmentIds,String plastatus,Map<String,String> menuCaterTypeMap
			) {
		DishRsDetsDTO drdDto = new DishRsDetsDTO();
		List<DishRsDets> dishRsDets = new ArrayList<>();
		Map<String, String> gcRetentiondishMap = new HashMap<>();
		int i, j, k, dateCount = dates.length;
		String key = null, keyVal = null;
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
    	List<Object> schPropsList=CommonUtil.changeStringToList(schProps);
    	List<Object> caterTypesList=CommonUtil.changeStringToList(caterTypes);
    	List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
    	List<Object> menuNamesList=CommonUtil.changeStringToList(menuNames);
    	List<Object> optModesList=CommonUtil.changeStringToList(optModes);
    	//管理部门
    	List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
    	
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = new ArrayList<TEduSchoolDo>();
		if(distIdorSCName!=null && !"".equals(distIdorSCName)) {
			tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 5);
		}else {
			tesDoList = db1Service.getTEduSchoolDoListByDs1(distNamesList,1,1);
		}
		int tesDoListLen = tesDoList.size();
		for(i = 0; i < tesDoListLen; i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		//团餐公司id和团餐公司名称
		Map<String, String> RmcIdToNameMap = new HashMap<>();
    	List<TProSupplierDo> tpsDoList = saasService.getRmcIdName();
    	if(tpsDoList != null) {
    		for(i = 0; i < tpsDoList.size(); i++) {
    			RmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
    		}
    	}
		// 时间段内各区菜品留样详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_gc-retentiondish";
			gcRetentiondishMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (gcRetentiondishMap != null) {
				for (String curKey : gcRetentiondishMap.keySet()) {
					keyVal = gcRetentiondishMap.get(curKey);
					// 菜品留样列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 25) {
						if(distIdorSCName != null) {
							if(keyVals[1].compareTo(distIdorSCName) != 0)
								continue ;
						}else if(distNamesList!=null && distNamesList.size() >0) {
							if(StringUtils.isEmpty(keyVals[1]) || !StringUtils.isNumeric(keyVals[1])) {
								continue;
							}
							if(!distNamesList.contains(keyVals[1])) {
								continue ;
							}
						}
						DishRsDets drd = new DishRsDets();
						//学校信息（项目点）
						TEduSchoolDo tesDo = null;
						if(schIdMap.containsKey(keyVals[5])) {
							j = schIdMap.get(keyVals[5]);
							tesDo = tesDoList.get(j-1);
						}
						if(tesDo == null)
							continue;
						//就餐日期
						drd.setRepastDate(dates[k].replaceAll("-", "/"));
	    				//总校/分校
						int curSchGenBraFlag = 0;
						drd.setSchGenBraFlag("-");
	    				if(tesDo.getIsBranchSchool() != null) {
	    					if(tesDo.getIsBranchSchool() == 1) { //分销
	    						curSchGenBraFlag = 2;
	    						drd.setSchGenBraFlag("分校");
	    					}
	    					else {   //总校
	    						curSchGenBraFlag = 1;
	    						drd.setSchGenBraFlag("总校");
	    					}
	    				}
	    				//分校数量
	    				int curBraCampusNum = 0;
	    				drd.setBraCampusNum(curBraCampusNum);
	    				if(curSchGenBraFlag == 1) {
	    					String curSchId = tesDo.getId();
	    					if(curSchId != null) {
	    						for(i = 0; i < tesDoList.size(); i++) {
	    							if(tesDoList.get(i).getParentId() != null) {
	    								if(curSchId.equalsIgnoreCase(tesDoList.get(i).getParentId())) {
	    									curBraCampusNum++;
	    								}
	    							}
	    						}
	    					}
	    					if(curBraCampusNum > 0)
	    						drd.setBraCampusNum(curBraCampusNum);
	    				}
	    				//关联总校
	    				drd.setRelGenSchName("-");
	    				if(schIdMap.containsKey(tesDo.getParentId())) {
	    					i = schIdMap.get(tesDo.getParentId())-1;
	    					drd.setRelGenSchName(tesDoList.get(i).getSchoolName());
	    				}    				
	    				//所属
	    				int curSubLevel = 0;
	    				if(tesDo.getDepartmentMasterId() != null) {
	    					curSubLevel = Integer.parseInt(tesDo.getDepartmentMasterId());
	    				}
	    				drd.setSubLevel(AppModConfig.subLevelIdToNameMap.get(curSubLevel));
	    				//主管部门
	    				int curCompDep = 0;
	    				drd.setCompDep("其他");
	    				if(curSubLevel == 0) {      //其他
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					drd.setCompDep(AppModConfig.compDepIdToNameMap0.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel ==1) {      //部级   
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					drd.setCompDep(AppModConfig.compDepIdToNameMap1.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel == 2) {      //市级
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					drd.setCompDep(AppModConfig.compDepIdToNameMap2.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel == 3) {      //区级
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						String orgName = AppModConfig.compDepIdToNameMap3bd.get(tesDo.getDepartmentSlaveId());
	    						if(orgName != null) {
	    							curCompDep = Integer.parseInt(AppModConfig.compDepNameToIdMap3.get(orgName));
	    						}
	    					}
	    					drd.setCompDep(AppModConfig.compDepIdToNameMap3.get(String.valueOf(curCompDep)));
	    				}    				
	    				//所属区域名称
	    				drd.setSubDistName("-");
	    				if(tesDo.getSchoolAreaId() != null)
	    					drd.setSubDistName(AppModConfig.distIdToNameMap.get(tesDo.getSchoolAreaId()));
	    				//证件主体，0:学校，1:外包
	    				drd.setFblMb("-");
	    				if(tesDo.getLicenseMainType() != null) {
	    					int curFblMb = Integer.parseInt(tesDo.getLicenseMainType());
	    					if(AppModConfig.fblMbIdToNameMap.containsKey(curFblMb))
	    						drd.setFblMb(AppModConfig.fblMbIdToNameMap.get(curFblMb));
	    				}
						//项目点
						drd.setPpName(tesDo.getSchoolName());						
						//区
						drd.setDistName(keyVals[1]);
						//学校学制
						drd.setSchType(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2()));						
						//学校性质
						drd.setSchProp(AppModConfig.getSchProp(tesDo.getSchoolNature()));						
						//经营模式（供餐类型）
						drd.setOptMode(AppModConfig.getOptModeName(tesDo.getCanteenMode(), tesDo.getLedgerType(), tesDo.getLicenseMainType(), tesDo.getLicenseMainChild()));						
						//团餐公司
						if(RmcIdToNameMap.containsKey(keyVals[3]))
							drd.setRmcName(RmcIdToNameMap.get(keyVals[3]));
						else
							drd.setRmcName("-");
						//餐别
						drd.setCaterType(keyVals[13]);
						//菜单名称
						drd.setMenuName(keyVals[7]);
						//菜品名称
						drd.setDishName(keyVals[9]);
						//菜单份数
						if(keyVals[11] !=null && !"".equals(keyVals[11]) && !"null".equals(keyVals[11])) {
						  drd.setDishNum(Integer.parseInt(keyVals[11]));
						}else {
						  drd.setDishNum(0);
						}
						//是否留样
						if(keyVals[14].equalsIgnoreCase("未留样"))
							drd.setRsFlag(0);
						else if(keyVals[14].equalsIgnoreCase("已留样"))
							drd.setRsFlag(1);
						//留样数量
						if(keyVals[20].equalsIgnoreCase("null"))
							drd.setRsNum("-");
						else
							drd.setRsNum(keyVals[20] + " g");
						//留样时间
						if(keyVals[16].equalsIgnoreCase("null"))
							drd.setCreatetime("-");
						else {
							//2019/11/13 00:00:00 11:00:00
							if(keyVals[16].length() > 19) {
								drd.setCreatetime((keyVals[16].replace("00:00:00 ", "").replace("00:00:0 ", "")).replaceAll("-", "/"));	
							}else {
								drd.setCreatetime(keyVals[16].replaceAll("-", "/"));	
							}
								
						}
						//留样说明
						if(keyVals[22].equalsIgnoreCase("null"))
							drd.setRsExplain("-");
						else
							drd.setRsExplain(keyVals[22]);
						//留样单位
						drd.setRsUnit("-");
						if(tesDo != null)
							drd.setRsUnit(tesDo.getSchoolName());
						//留样人
						drd.setRsPerson("-");
						
						//留样操作时间
						if(keyVals.length >= 25 && !keyVals[24].equalsIgnoreCase("null")) {
							if(keyVals[24].length() > 19) {
								drd.setRsTime((keyVals[24].replace("00:00:00 ", "").replace("00:00:0 ", "")).replaceAll("-", "/"));	
							}else {
								drd.setRsTime(keyVals[24].replaceAll("-", "/"));
							}
						}else {
							drd.setRsTime("");
						}
						
	    				//管理部门
						drd.setDepartmentId(tesDo.getDepartmentId()==null?"":tesDo.getDepartmentId());
						//操作状态
						if(keyVals.length >= 5 && CommonUtil.isNotEmpty(keyVals[26])) {
							drd.setReserveStatus(keyVals[26]);
						}else {
							drd.setReserveStatus("");
						}
						
						//条件判断
						boolean isAdd = true;
						int[] flIdxs = new int[14];
						//判断项目点名称（判断索引0）
						if(ppName != null) {
							if(!tesDo.getId().equals(ppName))
								flIdxs[0] = -1;
						}
						//判断团餐公司（判断索引1）
						if(rmcName != null && RmcIdToNameMap != null) {
							if(!(drd.getRmcName().equalsIgnoreCase(RmcIdToNameMap.get(rmcName))))
								flIdxs[1] = -1;
						}
						//判断是否留样（判断索引2）
						if(rsFlag != -1) {
							if(drd.getRsFlag() != rsFlag)
								flIdxs[2] = -1;
						}
						//判断餐别（判断索引3）
						if(caterType != null) {
							if(!drd.getCaterType().equalsIgnoreCase(caterType)) {
								flIdxs[3] = -1;
							}
						}else if(caterTypesList!=null && caterTypesList.size() >0) {
							if(!caterTypesList.contains(drd.getCaterType())) {
								flIdxs[3] = -1;
							}
						}						
						//判断学校类型（学制）（判断索引4）
						if(schType != -1) {
							if(AppModConfig.schTypeNameToIdMap.containsKey(drd.getSchType())) {
								int curSchType = AppModConfig.schTypeNameToIdMap.get(drd.getSchType());
								if(curSchType != schType)
									flIdxs[4] = -1;
							}
							else
								flIdxs[4] = -1;
						}else if(schTypesList!=null && schTypesList.size() >0) {
							if(AppModConfig.schTypeNameToIdMap.containsKey(drd.getSchType())) {
								int curSchType = AppModConfig.schTypeNameToIdMap.get(drd.getSchType());
								if(!schTypesList.contains(String.valueOf(curSchType))) {
									flIdxs[4] = -1;
								}
							}else{
								flIdxs[4] = -1;
							}
						}	
						//判断菜单名称（判断索引5）
						if(menuName != null) {
							if(drd.getMenuName().indexOf(menuName) == -1)
								flIdxs[5] = -1;
						}else if(menuNamesList!=null && menuNamesList.size() >0) {
							if(!menuNamesList.contains(drd.getMenuName())) {
								flIdxs[5] = -1;
							}
						}	
						//判断供餐类型（判断索引6）
						if(optMode != -1) {
							if(drd.getOptMode() != null) {
								if(AppModConfig.optModeNameToIdMap.containsKey(drd.getOptMode())) {
									int curOptMode = AppModConfig.optModeNameToIdMap.get(drd.getOptMode());
									if(curOptMode != optMode)
										flIdxs[6] = -1;
								}
								else
									flIdxs[6] = -1;
							}
							else
								flIdxs[6] = -1;
						}else if(optModesList!=null && optModesList.size() >0) {
							if(drd.getOptMode() != null) {
								if(AppModConfig.optModeNameToIdMap.containsKey(drd.getOptMode())) {
									int curOptMode = AppModConfig.optModeNameToIdMap.get(drd.getOptMode());
									if(!optModesList.contains(String.valueOf(curOptMode))) {
										flIdxs[6] = -1;
									}
								}
								else {
									flIdxs[6] = -1;
								}
							}
							else {
								flIdxs[6] = -1;
							}
						}	
						
						//菜单-餐别
						//判断菜单名称（判断索引5）
						if(menuCaterTypeMap != null && menuCaterTypeMap.size() >0) {
							if(CommonUtil.isEmpty(drd.getMenuName()) || 
									CommonUtil.isEmpty(menuCaterTypeMap.get(drd.getMenuName()))) {
								flIdxs[5] = -1;
							}else{
								if(CommonUtil.isEmpty(drd.getCaterType()) || 
										!drd.getCaterType().equals(menuCaterTypeMap.get(drd.getMenuName()))) {
									flIdxs[5] = -1;
								}
							}
							
							
						}
						//判断留样单位（判断索引7）
						if(rsUnit != null) {
							String curRsUnit = tesDo.getId();
							if(!curRsUnit.equalsIgnoreCase(rsUnit))
								flIdxs[7] = -1;							
						}
						//判断所属（判断索引8）
						if(subLevel != -1) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(drd.getSubLevel());
							if(curSubLevel != subLevel)
								flIdxs[8] = -1;
						}else if(subLevelsList!=null && subLevelsList.size() >0) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(drd.getSubLevel());
							if(!subLevelsList.contains(String.valueOf(curSubLevel))) {
								flIdxs[8] = -1;
							}
						}
						//判断主管部门（判断索引9）
						if(compDep != -1) {
							curCompDep = 0;
							if(subLevel == 0) {    //其他
								String strCompDepId = AppModConfig.compDepNameToIdMap0.get(drd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[9] = -1;
								}
								else
									flIdxs[9] = -1;
							}
							else if(subLevel == 1) {    //部署
								String strCompDepId = AppModConfig.compDepNameToIdMap1.get(drd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[9] = -1;
								}
								else
									flIdxs[9] = -1;
							}
							else if(subLevel == 2) {    //市属
								String strCompDepId = AppModConfig.compDepNameToIdMap2.get(drd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[9] = -1;
								}
								else
									flIdxs[9] = -1;
							}
							else if(subLevel == 3) {    //区属
								String strCompDepId = AppModConfig.compDepNameToIdMap3.get(drd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[9] = -1;
								}
								else
									flIdxs[9] = -1;
							}
						}else if(compDepsList!=null && compDepsList.size() >0) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(drd.getSubLevel());
							curCompDep = 0;
							if(curSubLevel == 0) {    //其他
								String strCompDepId = AppModConfig.compDepNameToIdMap0.get(drd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[9] = -1;
									}
								}
								else
									flIdxs[9] = -1;
							}
							else if(curSubLevel == 1) {    //部署
								String strCompDepId = AppModConfig.compDepNameToIdMap1.get(drd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[9] = -1;
									}
								}
								else
									flIdxs[9] = -1;
							}
							else if(curSubLevel == 2) {    //市属
								String strCompDepId = AppModConfig.compDepNameToIdMap2.get(drd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[9] = -1;
									}
								}
								else
									flIdxs[9] = -1;
							}
							else if(curSubLevel == 3) {    //区属
								String strCompDepId = AppModConfig.compDepNameToIdMap3.get(drd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[9] = -1;
									}
								}
								else
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[9] = -1;
									}
							}
							
						}
						//判断总分校标识（判断索引10）
						if(schGenBraFlag != -1) {
							curSchGenBraFlag = AppModConfig.genBraSchNameToIdMap.get(drd.getSchGenBraFlag());
							if(curSchGenBraFlag != schGenBraFlag)
								flIdxs[10] = -1;
						}
						//判断所属区域名称（判断索引11）
						if(subDistName != null) {
							if(drd.getSubDistName() != null) {
								if(!drd.getSubDistName().equals(subDistName))
									flIdxs[11] = -1;
							}
							else
								flIdxs[11] = -1;
						}
						//判断证件主体（判断索引12）
						if(fblMb != -1) {
							if(AppModConfig.fblMbNameToIdMap.get(drd.getFblMb()) == null) {
								flIdxs[12] = -1;
							}else {
								int curFblMb = AppModConfig.fblMbNameToIdMap.get(drd.getFblMb());
								if(curFblMb != fblMb)
									flIdxs[12] = -1;
								}
						}
						//判断学校性质（判断索引13）
						if(schProp != -1) {
							int curSchProp = AppModConfig.schPropNameToIdMap.get(drd.getSchProp());
							if(curSchProp != schProp)
								flIdxs[13] = -1;
						}else if(schPropsList!=null && schPropsList.size() >0) {
							int curSchProp = AppModConfig.schPropNameToIdMap.get(drd.getSchProp());
							if(!schPropsList.contains(String.valueOf(curSchProp))) {
								flIdxs[13] = -1;
							}
						}	
						
						//主管部门编号（判断索引8）
						if(departmentId != null) {
							if(drd.getDepartmentId() != null) {
								if(!drd.getDepartmentId().equals(departmentId))
									flIdxs[8] = -1;
							}
							else
								flIdxs[8] = -1;
							
						}
						if(departmentIdList!=null && departmentIdList.size() >0) {
							if(drd.getDepartmentId() != null) {
								if(!departmentIdList.contains(drd.getDepartmentId()))
									flIdxs[8] = -1;
							}
							else {
								flIdxs[8] = -1;
							}
						}
						
						//判断操作状态（判断索引7）
						if(CommonUtil.isNotEmpty(plastatus)) {
							if(CommonUtil.isEmpty(drd.getReserveStatus()) || !plastatus.equals(drd.getReserveStatus() ))
								flIdxs[7] = -1;
						}
						
						//总体条件判断
						for(i = 0; i < flIdxs.length; i++) {
							if(flIdxs[i] == -1) {
								isAdd = false;
								break;
							}
						}
						//是否满足条件
						if(isAdd) {
							dishRsDets.add(drd);
							logger.info("key:"+curKey + "    keyVal"+keyVal);
						}
					}
					else
						logger.info("菜品留样："+ curKey + "，格式错误！");
				}
			}
		}
		//排序
    	SortList<DishRsDets> sortList = new SortList<DishRsDets>();  
    	sortList.Sort3Level(dishRsDets, methods, sorts, dataTypes);
		//时戳
		drdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
	/*	// 分页
		PageBean<DishRsDets> pageBean = new PageBean<DishRsDets>(dishRsDets, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		drdDto.setPageInfo(pageInfo);
		//设置数据
		drdDto.setDishRsDets(pageBean.getCurPageData());*/
		
		PageBean<DishRsDets> pageBean = null;
		if(curPageNum == -1 || pageSize == -1) {
			pageBean = new PageBean<DishRsDets>(dishRsDets, 1, dishRsDets.size());
		}else {
			pageBean = new PageBean<DishRsDets>(dishRsDets, curPageNum, pageSize);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		drdDto.setPageInfo(pageInfo);
		//设置数据
		drdDto.setDishRsDets(pageBean.getCurPageData());
		
		//消息ID
		drdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return drdDto;
	}

	//菜品留样详情列表模型函数
	public DishRsDetsDTO appModFunc(String token, String repastStartDate, String repasEndDate, 
			String distName, String prefCity, String province, String subLevel, 
			String compDep, String schGenBraFlag, String subDistName, String fblMb, 
			String schProp, String ppName, String rmcName, String rsFlag, 
			String caterType, String schType, String menuName, String optMode, 
			String rsUnit, 
			String distNames,String subLevels,String compDeps,String schProps,String caterTypes,
			String schTypes,String menuNames,String optModes,
			String departmentId,String departmentIds,String reservestatus,
			Map<String,String> menuCaterTypeMap,
			String page, String pageSize,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService) {
		DishRsDetsDTO drdDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if (isRealData) { // 真实数据
			// 日期
			String[] dates = null;
			if (repastStartDate == null || repasEndDate == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			} else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(repastStartDate);
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(repasEndDate);
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
	  		//是否留样标识，0:未留样，1:已留样
			int curRsFlag = -1;
			if(rsFlag != null)
				curRsFlag = Integer.parseInt(rsFlag);
			//学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，
			//11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
			int curSchType = -1;
			if(schType != null)
				curSchType = Integer.parseInt(schType);
			//经营模式（供餐类型），0:自营，1:外包-现场加工，2:外包-快餐配送
			int curOptMode = -1;
			if(optMode != null)
				curOptMode = Integer.parseInt(optMode);
			// 参数查找标识
			boolean bfind = false;
			String distIdorSCName = null;
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
					//获取用户数据权限信息
				  	/*UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);
				  	if(curSubLevel == -1)
				  		curSubLevel = udpiDto.getSubLevelId();
				  	if(curCompDep == -1)
				  		curCompDep = udpiDto.getCompDepId();*/
					// 菜品留样详情列表函数
					drdDto = dishRsDets(departmentId,distIdorSCName, dates, tddList, db1Service, saasService, curSubLevel, 
							curCompDep, curSchGenBraFlag, subDistName, curFblMb, curSchProp, ppName, 
							rmcName, curRsFlag, caterType, curSchType, menuName, curOptMode, rsUnit,
							distNames,subLevels,compDeps,schProps,caterTypes,schTypes,menuNames,optModes,
							departmentIds,reservestatus,menuCaterTypeMap);		
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
					// 菜品留样详情列表函数
					drdDto = dishRsDets(departmentId,distIdorSCName, dates, tddList, db1Service, saasService, 
							curSubLevel, curCompDep, curSchGenBraFlag, subDistName, curFblMb, curSchProp, 
							ppName, rmcName, curRsFlag, caterType, curSchType, menuName, curOptMode, rsUnit,
							distNames,subLevels,compDeps,schProps,caterTypes,schTypes,menuNames,optModes,
							departmentIds,reservestatus,menuCaterTypeMap);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}									
		} else { // 模拟数据
			//模拟数据函数
			drdDto = SimuDataFunc();
		}

		return drdDto;
	}
}