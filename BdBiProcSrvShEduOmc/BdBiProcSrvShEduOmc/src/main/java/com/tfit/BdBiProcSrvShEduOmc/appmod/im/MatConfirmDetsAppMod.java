package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatConfirmDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.MatConfirmDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpMatCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//用料确认详情列表应用模型
public class MatConfirmDetsAppMod {
	private static final Logger logger = LogManager.getLogger(MatConfirmDetsAppMod.class.getName());
	
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
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 2;
	
	//数组数据初始化
	String[] matUseDate_Array = {"2018/09/03", "2018/09/03", "2018/09/03", "2018/09/03", "2018/09/03", "2018/09/03", "2018/09/03", "2018/09/03", "2018/09/03", "2018/09/03", "2018/09/03", "2018/09/03", "2018/09/03"};
	String[] matCategory_Array = {"原料", "原料", "原料", "原料", "原料", "原料", "成品菜", "原料", "原料", "原料", "原料", "原料", "原料"};
	String[] ppName_Array = {"上海市徐汇区向阳小学", "上海市徐汇区世界小学", "上海市民办盛大花园小学", "上海市徐汇区汇师小学", "上海市徐汇区龙华小学", "上海交通大学附属小学", "上海市徐汇区求知小学", "上海市徐汇区逸夫小学", "华东理工大学附属小学", "上海市徐汇区建襄小学", "上海市徐汇区徐浦小学", "上海市徐汇区爱菊小学", "上海市徐汇区华泾小学"};
	String[] distName_Array = {"徐汇区", "徐汇区", "徐汇区", "徐汇区", "徐汇区", "徐汇区", "徐汇区", "徐汇区", "徐汇区", "徐汇区", "徐汇区", "徐汇区", "徐汇区"};
	String[] schType_Array = {"小学", "小学", "小学", "小学", "小学", "小学", "小学", "小学", "小学", "小学", "小学", "小学", "小学"};
	String[] schProp_Array = {"公办", "民办", "民办", "公办", "公办", "公办", "公办", "民办", "公办", "公办", "公办", "公办", "公办"};
	String[] optMode_Array = {"外包-外包", "外包-外包", "自营", "外包-外包", "外包-外包", "外包-外包", "外包-外送", "外包-外包", "外包-外包", "外包-外包", "外包-外包", "外包-外包", "外包-外包"};
	String[] rmcName_Array = {"上海绿捷实业发展有限公司", "上海龙神餐饮有限公司", "上海市民办盛大花园小学", "上海绿捷实业发展有限公司", "上海绿捷实业发展有限公司", "上海龙神餐饮有限公司", "上海龙神餐饮有限公司", "上海绿捷实业发展有限公司", "上海龙神餐饮有限公司", "上海龙神餐饮有限公司", "上海龙神餐饮有限公司", "上海绿捷实业发展有限公司", "上海龙神餐饮有限公司"};
	int[] confirmFlag_Array = {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	int[] sendFlag_Array = {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	
	//模拟数据函数
	private MatConfirmDetsDTO SimuDataFunc() {
		MatConfirmDetsDTO mcdDto = new MatConfirmDetsDTO();
		// 设置返回数据
		mcdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		List<MatConfirmDets> matConfirmDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < matUseDate_Array.length; i++) {
			MatConfirmDets mcd = new MatConfirmDets();		
			mcd.setMatUseDate(matUseDate_Array[i]);
			mcd.setMatCategory(matCategory_Array[i]);
			mcd.setPpName(ppName_Array[i]);
			mcd.setDistName(distName_Array[i]);
			mcd.setSchType(schType_Array[i]);
			mcd.setSchProp(schProp_Array[i]);
			mcd.setOptMode(optMode_Array[i]);
			mcd.setRmcName(rmcName_Array[i]);
			mcd.setConfirmFlag(confirmFlag_Array[i]);
			mcd.setSendFlag(sendFlag_Array[i]);
			matConfirmDets.add(mcd);
		}
		//设置数据
		mcdDto.setMatConfirmDets(matConfirmDets);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = matUseDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		mcdDto.setPageInfo(pageInfo);
		//消息ID
		mcdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mcdDto;
	}
	
	//用料确认详情列表函数
	private MatConfirmDetsDTO matConfirmDets(String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, 
			Db1Service db1Service, SaasService saasService, String ppName, String rmcName, 
			int subLevel, int compDep, int schGenBraFlag, String subDistName, int fblMb, 
			int confirmFlag, int schType, int schProp, int optMode, int matType,
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			String optModes,String subDistNames,
			String departmentId,String departmentIds) {
		MatConfirmDetsDTO mcdDto = new MatConfirmDetsDTO();
		List<MatConfirmDets> matConfirmDets = new ArrayList<>();
		Map<String, String> useMaterialPlanDetailMap = new HashMap<>(), projIdToSchIdMap = new HashMap<>();
		int i, j, k, dateCount = dates.length;
		String key = null, keyVal = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
    	List<Object> schPropsList=CommonUtil.changeStringToList(schProps);
    	List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
    	List<Object> optModesList=CommonUtil.changeStringToList(optModes);
    	List<Object> subDistNamesList=CommonUtil.changeStringToList(subDistNames);
    	List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
    	
		//所有学校id
		List<TEduSchoolDo> tesDoList = new ArrayList<TEduSchoolDo>();
		if(distIdorSCName!=null && !"".equals(distIdorSCName)) {
			tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 5);
		}else {
			tesDoList = db1Service.getTEduSchoolDoListByDs1(distNamesList,1,1);
		}
		
		for(i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
			if(tesDoList.get(i).getDepartmentMasterId()!=null && tesDoList.get(i).getDepartmentSlaveId()!=null) {
			}
		}
		//项目点id和学校id
		List<TEduSchoolSupplierDo> tessDoList = saasService.getAllIdSupplierIdSchoolId(null);
		for(i = 0; i < tessDoList.size(); i++) {
			projIdToSchIdMap.put(tessDoList.get(i).getId(), tessDoList.get(i).getSchoolId());
		}
		//团餐公司id和团餐公司名称
		Map<String, String> RmcIdToNameMap = new HashMap<>();
		List<TProSupplierDo> tpsDoList = saasService.getRmcIdName();
		if(tpsDoList != null) {
			for(i = 0; i < tpsDoList.size(); i++) {
				RmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
			}
		}
    	
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_useMaterialPlan-Detail";
			useMaterialPlanDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (useMaterialPlanDetailMap != null) {
				for (String curKey : useMaterialPlanDetailMap.keySet()) {
					keyVal = useMaterialPlanDetailMap.get(curKey);
					// 用料计划列表
					String[] curKeys = curKey.split("_");
					if(curKeys.length >= 10) {
						if(distIdorSCName != null) {
							if(curKeys[1].compareTo(distIdorSCName) != 0)
								continue ;
						}else if(distNamesList!=null && distNamesList.size() >0) {
							if(!CommonUtil.isInteger(curKeys[1])) {
								continue;
							}
							if(!distNamesList.contains(curKeys[1])) {
								continue ;
							}
						}
						MatConfirmDets mcd = new MatConfirmDets();
						//学校信息（项目点）
						TEduSchoolDo tesDo = null;
						if(projIdToSchIdMap.containsKey(curKeys[7])) {
							String schId = projIdToSchIdMap.get(curKeys[7]);
							if(schIdMap.containsKey(schId)) {
								j = schIdMap.get(schId);
								tesDo = tesDoList.get(j-1);
							}
						}
						
						if(tesDo == null)
							continue;
						//用料日期
						mcd.setMatUseDate(dates[k].replaceAll("-", "/"));
						//用料类别
						if(CommonUtil.isInteger(curKeys[3])) {
							int matCategory = Integer.parseInt(curKeys[3]);								
							mcd.setMatCategory(AppModConfig.dispTypeIdToNameMap.get(matCategory));
						}else {
							mcd.setMatCategory("");
						}
						//项目点名称
						mcd.setPpName(curKeys[5]);
	    				//总校/分校
						int curSchGenBraFlag = 0;
						mcd.setSchGenBraFlag("-");
	    				if(tesDo!=null && tesDo.getIsBranchSchool() != null) {
	    					if(tesDo.getIsBranchSchool() == 1) { //分销
	    						curSchGenBraFlag = 2;
	    						mcd.setSchGenBraFlag("分校");
	    					}
	    					else {   //总校
	    						curSchGenBraFlag = 1;
	    						mcd.setSchGenBraFlag("总校");
	    					}
	    				}
	    				//分校数量
	    				int curBraCampusNum = 0;
	    				mcd.setBraCampusNum(curBraCampusNum);
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
	    						mcd.setBraCampusNum(curBraCampusNum);
	    				}
	    				//关联总校
	    				mcd.setRelGenSchName("-");
	    				if(schIdMap.containsKey(tesDo.getParentId())) {
	    					i = schIdMap.get(tesDo.getParentId())-1;
	    					mcd.setRelGenSchName(tesDoList.get(i).getSchoolName());
	    				}    		
						//管理部门
	    				mcd.setDepartmentId(tesDo.getDepartmentId()==null?"":tesDo.getDepartmentId());
	    				//所属
	    				int curSubLevel = 0;
	    				if(tesDo.getDepartmentMasterId() != null && CommonUtil.isInteger(tesDo.getDepartmentMasterId())) {
	    					curSubLevel = Integer.parseInt(tesDo.getDepartmentMasterId());
	    				}
	    				mcd.setSubLevel(AppModConfig.subLevelIdToNameMap.get(curSubLevel));
	    				//主管部门
	    				int curCompDep = 0;
	    				mcd.setCompDep("其他");
	    				if(curSubLevel == 0) {      //其他
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId()) && CommonUtil.isInteger(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					mcd.setCompDep(AppModConfig.compDepIdToNameMap0.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel ==1) {      //部级   
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId()) && CommonUtil.isInteger(tesDo.getDepartmentSlaveId()) ) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					mcd.setCompDep(AppModConfig.compDepIdToNameMap1.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel == 2) {      //市级
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId()) && CommonUtil.isInteger(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					mcd.setCompDep(AppModConfig.compDepIdToNameMap2.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel == 3) {      //区级
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						String orgName = AppModConfig.compDepIdToNameMap3bd.get(tesDo.getDepartmentSlaveId());
	    						if(orgName != null && CommonUtil.isInteger(AppModConfig.compDepNameToIdMap3.get(orgName))) {
	    							curCompDep = Integer.parseInt(AppModConfig.compDepNameToIdMap3.get(orgName));
	    						}
	    					}
	    					mcd.setCompDep(AppModConfig.compDepIdToNameMap3.get(String.valueOf(curCompDep)));
	    				}    				
	    				//所属区域名称
	    				mcd.setSubDistName("-");
	    				if(tesDo.getSchoolAreaId() != null)
	    					mcd.setSubDistName(AppModConfig.distIdToNameMap.get(tesDo.getSchoolAreaId()));
	    				//证件主体，0:学校，1:外包
	    				mcd.setFblMb("-");
	    				if(tesDo.getLicenseMainType() != null  && CommonUtil.isInteger(tesDo.getLicenseMainType()) ) {
	    					int curFblMb = Integer.parseInt(tesDo.getLicenseMainType());
	    					if(AppModConfig.fblMbIdToNameMap.containsKey(curFblMb))
	    						mcd.setFblMb(AppModConfig.fblMbIdToNameMap.get(curFblMb));
	    				}
						//区
						mcd.setDistName(curKeys[1]);
						//学校学制
						mcd.setSchType("-");
						if(tesDo != null)
							mcd.setSchType(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2()));
						//学校性质
						mcd.setSchProp("-");
						if(tesDo != null)
							mcd.setSchProp(AppModConfig.getSchProp(tesDo.getSchoolNature()));
						//供餐模式
						mcd.setOptMode("-");
						if(tesDo != null)
							mcd.setOptMode(AppModConfig.getOptModeName(tesDo.getCanteenMode(), tesDo.getLedgerType(), tesDo.getLicenseMainType(), tesDo.getLicenseMainChild()));
						//团餐公司
						mcd.setRmcName(curKeys[9]);
						//是否确认
						if(keyVal.equalsIgnoreCase("0") || keyVal.equalsIgnoreCase("1"))
							mcd.setConfirmFlag(0);
						else if(keyVal.equalsIgnoreCase("2"))
							mcd.setConfirmFlag(1);
						else
							mcd.setConfirmFlag(0);
						//发送状态
						mcd.setSendFlag(0);
						//条件判断
						boolean isAdd = true;
						int[] flIdxs = new int[12];
						//判断项目点名称（判断索引0）
						if(ppName != null) {
							if(mcd.getPpName().indexOf(ppName) == -1)
								flIdxs[0] = -1;
						}
						//判断团餐公司（判断索引1）
						if(rmcName != null) {
							if(!(mcd.getRmcName().equalsIgnoreCase(RmcIdToNameMap.get(rmcName))))
								flIdxs[1] = -1;
						}
						//判断是否确认（判断索引2）
						if(confirmFlag != -1) {
							if(mcd.getConfirmFlag() != confirmFlag)
								flIdxs[2] = -1;
						}
						//判断学校类型（学制）（判断索引3）
						if(schType != -1) {
							if(AppModConfig.schTypeNameToIdMap.containsKey(mcd.getSchType())) {
								int curSchType = AppModConfig.schTypeNameToIdMap.get(mcd.getSchType());
								if(curSchType != schType)
									flIdxs[3] = -1;
							}
							else
								flIdxs[3] = -1;
						}else if(schTypesList!=null && schTypesList.size() >0) {
							if(AppModConfig.schTypeNameToIdMap.containsKey(mcd.getSchType())) {
								int curSchType = AppModConfig.schTypeNameToIdMap.get(mcd.getSchType());
								if(!schTypesList.contains(String.valueOf(curSchType))) {
									flIdxs[2] = -1;
								}
							}else{
								flIdxs[2] = -1;
							}
						}
						//判断学校性质（判断索引4）
						if(schProp != -1) {
							int curSchProp = AppModConfig.schPropNameToIdMap.get(mcd.getSchProp());
							if(curSchProp != schProp)
								flIdxs[4] = -1;
						}else if(schPropsList!=null && schPropsList.size() >0) {
							int curSchProp = AppModConfig.schPropNameToIdMap.get(mcd.getSchProp());
							if(!schPropsList.contains(String.valueOf(curSchProp))) {
								flIdxs[4] = -1;
							}
						}	
						//判断经营模式（判断索引5）
						if(optMode != -1) {
							Integer curOptMode = AppModConfig.optModeNameToIdMap.get(mcd.getOptMode());
							if(curOptMode != null) {
								if(curOptMode.intValue() != optMode)
									flIdxs[5] = -1;
							}
							else
								flIdxs[5] = -1;
						}else if(optModesList!=null && optModesList.size() >0) {
							if(mcd.getOptMode() != null) {
								if(AppModConfig.optModeNameToIdMap.containsKey(mcd.getOptMode())) {
									int curOptMode = AppModConfig.optModeNameToIdMap.get(mcd.getOptMode());
									if(!optModesList.contains(String.valueOf(curOptMode))) {
										flIdxs[5] = -1;
									}
								}
								else {
									flIdxs[5] = -1;
								}
							}
							else {
								flIdxs[5] = -1;
							}
						}	
						//判断用料类型（判断索引6）
						if(matType != -1) {
							int curMatType = AppModConfig.dispTypeNameToIdMap.get(mcd.getMatCategory());
							if(curMatType != matType)
								flIdxs[6] = -1;
						}	
						//判断所属（判断索引7）
						if(subLevel != -1) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(mcd.getSubLevel());
							if(curSubLevel != subLevel)
								flIdxs[7] = -1;
						}else if(subLevelsList!=null && subLevelsList.size() >0) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(mcd.getSubLevel());
							if(!subLevelsList.contains(String.valueOf(curSubLevel))) {
								flIdxs[7] = -1;
							}
						}
						//判断主管部门（判断索引8）
						if(compDep != -1) {
							curCompDep = 0;
							if(subLevel == 0) {    //其他
								String strCompDepId = AppModConfig.compDepNameToIdMap0.get(mcd.getCompDep());
								if(strCompDepId != null && CommonUtil.isInteger(strCompDepId ) ) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[8] = -1;
								}
								else
									flIdxs[8] = -1;
							}
							else if(subLevel == 1) {    //部署
								String strCompDepId = AppModConfig.compDepNameToIdMap1.get(mcd.getCompDep());
								if(strCompDepId != null  && CommonUtil.isInteger(strCompDepId)) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[8] = -1;
								}
								else
									flIdxs[8] = -1;
							}
							else if(subLevel == 2) {    //市属
								String strCompDepId = AppModConfig.compDepNameToIdMap2.get(mcd.getCompDep());
								if(strCompDepId != null  && CommonUtil.isInteger(strCompDepId)) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[8] = -1;
								}
								else
									flIdxs[8] = -1;
							}
							else if(subLevel == 3) {    //区属
								String strCompDepId = AppModConfig.compDepNameToIdMap3.get(mcd.getCompDep());
								if(strCompDepId != null && CommonUtil.isInteger(strCompDepId) ) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[8] = -1;
								}
								else
									flIdxs[8] = -1;
							}
						}else if(compDepsList!=null && compDepsList.size() >0) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(mcd.getSubLevel());
							curCompDep = 0;
							if(curSubLevel == 0) {    //其他
								String strCompDepId = AppModConfig.compDepNameToIdMap0.get(mcd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[8] = -1;
									}
								}
								else
									flIdxs[8] = -1;
							}
							else if(curSubLevel == 1) {    //部署
								String strCompDepId = AppModConfig.compDepNameToIdMap1.get(mcd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[8] = -1;
									}
								}
								else
									flIdxs[8] = -1;
							}
							else if(curSubLevel == 2) {    //市属
								String strCompDepId = AppModConfig.compDepNameToIdMap2.get(mcd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[8] = -1;
									}
								}
								else
									flIdxs[8] = -1;
							}
							else if(curSubLevel == 3) {    //区属
								String strCompDepId = AppModConfig.compDepNameToIdMap3.get(mcd.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[8] = -1;
									}
								}
								else
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[8] = -1;
									}
							}
						}
						//判断总分校标识（判断索引9）
						if(schGenBraFlag != -1) {
							curSchGenBraFlag = AppModConfig.genBraSchNameToIdMap.get(mcd.getSchGenBraFlag());
							if(curSchGenBraFlag != schGenBraFlag)
								flIdxs[9] = -1;
						}
						//判断所属区域名称（判断索引10）
						if(subDistName != null) {
							if(mcd.getSubDistName() != null) {
								if(!mcd.getSubDistName().equals(subDistName))
									flIdxs[10] = -1;
							}
							else
								flIdxs[10] = -1;
						}else if(subDistNamesList!=null && subDistNamesList.size() >0) {
							if(mcd.getSubDistName() != null) {
								if(!subDistNamesList.contains(mcd.getSubDistName()))
									flIdxs[10] = -1;
							}
							else {
								flIdxs[10] = -1;
							}
						}
						
						//主管部门编号（判断索引8）
						if(departmentId != null) {
							if(mcd.getDepartmentId() != null) {
								if(!mcd.getDepartmentId().equals(departmentId))
									flIdxs[8] = -1;
							}
							else
								flIdxs[8] = -1;
							
						}
						if(departmentIdList!=null && departmentIdList.size() >0) {
							if(mcd.getDepartmentId() != null) {
								if(!departmentIdList.contains(mcd.getDepartmentId()))
									flIdxs[8] = -1;
							}
							else {
								flIdxs[8] = -1;
							}
						}
						
						//判断证件主体（判断索引11）
						if(fblMb != -1) {
							int curFblMb = AppModConfig.fblMbNameToIdMap.get(mcd.getFblMb());
							if(curFblMb != fblMb)
								flIdxs[11] = -1;
						}						
						//总体条件判断
						for(i = 0; i < flIdxs.length; i++) {
							if(flIdxs[i] == -1) {
								isAdd = false;
								break;
							}
						}
						//是否满足条件
						if(isAdd)
							matConfirmDets.add(mcd);
					}
					else
						logger.info("配货计划："+ curKey + "，格式错误！");
				}
			}
		}
		//排序
    	SortList<MatConfirmDets> sortList = new SortList<MatConfirmDets>();  
    	sortList.Sort3Level(matConfirmDets, methods, sorts, dataTypes);
		//时戳
		mcdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//分页
		PageBean<MatConfirmDets> pageBean = null;
		if(curPageNum == -1 || pageSize == -1) {
			pageBean = new PageBean<MatConfirmDets>(matConfirmDets, -1, matConfirmDets.size());
		}else {
			pageBean = new PageBean<MatConfirmDets>(matConfirmDets, curPageNum, pageSize);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		mcdDto.setPageInfo(pageInfo);
		//设置数据
		mcdDto.setMatConfirmDets(pageBean.getCurPageData());
		//消息ID
		mcdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mcdDto;
	}
	
	//用料确认详情列表函数
	private MatConfirmDetsDTO matConfirmDetsFromHive(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, 
			Db1Service db1Service, SaasService saasService, String ppName, String rmcName, 
			int subLevel, int compDep, int schGenBraFlag, String subDistName, int fblMb, 
			int confirmFlag, int schType, int schProp, int optMode, int matType,
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			String optModes,String subDistNames,String departmentIds,DbHiveMatService dbHiveMatService) {
		MatConfirmDetsDTO mcdDto = new MatConfirmDetsDTO();
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
    	List<Object> schPropsList=CommonUtil.changeStringToList(schProps);
    	List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
    	List<Object> optModesList=CommonUtil.changeStringToList(optModes);
    	List<Object> subDistNamesList=CommonUtil.changeStringToList(subDistNames);
    	List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
    	
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

		List<PpMatCommonDets> dishList = new ArrayList<>();
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
		if(subDistNamesList !=null && subDistNamesList.size()>0) {
			for(Object subDistNameObj  : subDistNamesList) {
				newSubDistNamesList.add(AppModConfig.distNameToIdMap.get(subDistNameObj.toString()));
			}
		}
		dishList = dbHiveMatService.getMatDetsList(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
				ppName, rmcName, null, subLevel, compDep, schGenBraFlag, subDistName, fblMb, 
				confirmFlag, schType, schProp, optMode, matType, distNamesList, subLevelsList, compDepsList, 
				schPropsList, schTypesList, optModesList, newSubDistNamesList,departmentId,departmentIdsList, (curPageNum-1)*pageSize, curPageNum*pageSize);
		dishList.removeAll(Collections.singleton(null));
		
		Integer pageTotalTemp = dbHiveMatService.getMatDetsCount(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
				ppName, rmcName, null, subLevel, compDep, schGenBraFlag, subDistName, fblMb, 
				confirmFlag, schType, schProp, optMode, matType, distNamesList, subLevelsList, compDepsList, 
				schPropsList, schTypesList, optModesList, newSubDistNamesList,departmentId,departmentIdsList);
		
		if(pageTotalTemp!=null) {
			pageTotal = pageTotalTemp;
		}
		
		List<MatConfirmDets> ppDishDets = new ArrayList<>();
		for(PpMatCommonDets commonDets : dishList) {
			MatConfirmDets pdd = new MatConfirmDets();
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
		mcdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//分页
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		mcdDto.setPageInfo(pageInfo);
		//设置数据
		mcdDto.setMatConfirmDets(ppDishDets);
		//消息ID
		mcdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return mcdDto;
	}

	//用料确认详情列表模型函数
	public MatConfirmDetsDTO appModFunc(String token, String startDate, String endDate, 
			String ppName, String rmcName, String distName, String prefCity, String province, 
			String subLevel, String compDep, String schGenBraFlag, String subDistName, 
			String fblMb, String confirmFlag, String schType, String schProp, 
			String optMode, String matType, 
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			String optModes,String subDistNames,
			String departmentId,String departmentIds,
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveMatService dbHiveMatService) {
		MatConfirmDetsDTO mcdDto = null;
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
			//是否确认， 0:未确认，1:已确认
			int curConfirmFlag = -1;
			if(confirmFlag != null)
				curConfirmFlag = Integer.parseInt(confirmFlag);
			//学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
			int curSchType = -1;
			if(schType != null)
				curSchType = Integer.parseInt(schType);
			//学校性质，0:公办，1:民办，2:其他
			int curSchProp = -1;
			if(schProp != null)
				curSchProp = Integer.parseInt(schProp);
			//经营模式，0:自营，1:外包-现场加工，2:外包-快餐配送
			int curOptMode = -1;
			if(optMode != null)
				curOptMode = Integer.parseInt(optMode);
			//用料类别，0:原料，1:成品菜
			int curMatType = -1;
			if(matType != null)
				curMatType = Integer.parseInt(matType);
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
					// 用料确认详情列表函数
					if(methodIndex == 1) {
						mcdDto = matConfirmDets(distIdorSCName, dates, tddList, db1Service, saasService,
								ppName, rmcName, curSubLevel, curCompDep, curSchGenBraFlag, 
								subDistName, curFblMb, curConfirmFlag, curSchType, curSchProp, curOptMode, curMatType,
								distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,departmentId,departmentIds);		
					}else if(methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						if(days >= 2) {
							mcdDto = matConfirmDetsFromHive(departmentId,distIdorSCName, dates, tddList, db1Service, saasService,
									ppName, rmcName, curSubLevel, curCompDep, curSchGenBraFlag, 
									subDistName, curFblMb, curConfirmFlag, curSchType, curSchProp, curOptMode, curMatType,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,departmentIds, 
									dbHiveMatService);
						}else {
							mcdDto = matConfirmDets(distIdorSCName, dates, tddList, db1Service, saasService,
									ppName, rmcName, curSubLevel, curCompDep, curSchGenBraFlag, 
									subDistName, curFblMb, curConfirmFlag, curSchType, curSchProp, curOptMode, curMatType,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,departmentId,departmentIds);		
						}
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
					// 用料确认详情列表函数
					if(methodIndex == 1) {
						mcdDto = matConfirmDets(distIdorSCName, dates, tddList, db1Service, saasService, ppName, rmcName, 
								curSubLevel, curCompDep, curSchGenBraFlag, subDistName, curFblMb, curConfirmFlag,
								curSchType, curSchProp, curOptMode, curMatType,
								distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,departmentId,departmentIds);
					}else if(methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						if(days >= 2) {
							mcdDto = matConfirmDetsFromHive(departmentId,distIdorSCName, dates, tddList, db1Service, saasService, ppName, rmcName, 
									curSubLevel, curCompDep, curSchGenBraFlag, subDistName, curFblMb, curConfirmFlag,
									curSchType, curSchProp, curOptMode, curMatType,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames, departmentIds,
									dbHiveMatService);
						}else {
							mcdDto = matConfirmDets(distIdorSCName, dates, tddList, db1Service, saasService, ppName, rmcName, 
									curSubLevel, curCompDep, curSchGenBraFlag, subDistName, curFblMb, curConfirmFlag,
									curSchType, curSchProp, curOptMode, curMatType,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,departmentId,departmentIds);
						}
					}
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}
		} else { // 模拟数据
			//模拟数据函数
			mcdDto = SimuDataFunc();
		}

		return mcdDto;
	}
}
