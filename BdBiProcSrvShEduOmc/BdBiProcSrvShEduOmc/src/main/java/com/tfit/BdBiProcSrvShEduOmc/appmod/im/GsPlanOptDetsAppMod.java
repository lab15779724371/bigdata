package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//配货计划操作详情列表应用模型
public class GsPlanOptDetsAppMod {
	private static final Logger logger = LogManager.getLogger(GsPlanOptDetsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//三级排序条件
	final String[] methods = {"getDistName", "getSchType", "getDistrBatNumber"};
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
	String[] distrDate_Array = {"2018/08/08", "2018/08/08"};
	String[] distrBatNumber_Array = {"2018080802055", "2018080802055"};
	String[] ppName_Array = {"上海市天山中学", "上海市天山中学"};
	String[] schType_Array = {"初级中学", "初级中学"};
	String[] distName_Array = {"长宁区", "长宁区"};
	String[] rmcName_Array = {"上海绿捷", "上海绿捷"};
	String[] dispType_Array = {"原料", "原料"};
	String[] dispMode_Array = {"统配", "统配"};
	int[] assignStatus_Array = {1, 1};
	int[] dispStatus_Array = {0, 0};
	int[] acceptStatus_Array = {0, 0};
	int[] sendFlag_Array = {0, 0};
	
	//模拟数据函数
	private GsPlanOptDetsDTO SimuDataFunc() {
		GsPlanOptDetsDTO gpodDto = new GsPlanOptDetsDTO();
		//设置返回数据
		gpodDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		List<GsPlanOptDets> matConfirmDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < distrDate_Array.length; i++) {
			GsPlanOptDets gpod = new GsPlanOptDets();
			gpod.setDistrDate(distrDate_Array[i]);
			gpod.setDistrBatNumber(distrBatNumber_Array[i]);
			gpod.setPpName(ppName_Array[i]);
			gpod.setSchType(schType_Array[i]);
			gpod.setDistName(distName_Array[i]);
			gpod.setRmcName(rmcName_Array[i]);
			gpod.setDispType(dispType_Array[i]);
			gpod.setDispMode(dispMode_Array[i]);
			gpod.setAssignStatus(assignStatus_Array[i]);
			gpod.setDispStatus(dispStatus_Array[i]);
			gpod.setAcceptStatus(acceptStatus_Array[i]);
			gpod.setSendFlag(sendFlag_Array[i]);
			matConfirmDets.add(gpod);
		}
		//设置数据
		gpodDto.setGsPlanOptDets(matConfirmDets);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = distrDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		gpodDto.setPageInfo(pageInfo);
		//消息ID
		gpodDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gpodDto;
	}
	
	// 配货计划操作详情列表函数
	GsPlanOptDetsDTO gsPlanOptDetsFromHive(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, 
			Db1Service db1Service, SaasService saasService, 
			int subLevel, int compDep, int schGenBraFlag, String subDistName, int fblMb, 
			int schProp, String ppName, String rmcName, int assignStatus, int dispStatus, 
			int acceptStatus, String distrBatNumber, int schType, int dispType, 
			int dispMode, int sendFlag,
			String distNames,String subLevels,String compDeps,String schProps,
			String schTypes,String dispStatuss,String assignStatuss,
			String departmentIds,String plastatus,
			List<String> distrBatNumberList,
			DbHiveGsService dbHiveGsService
			) {
		GsPlanOptDetsDTO gpodDto = new GsPlanOptDetsDTO();
		List<GsPlanOptDets> gsPlanOptDets = new ArrayList<>();
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
    	List<Object> schPropsList=CommonUtil.changeStringToList(schProps);
    	List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
    	List<Object> dispStatussList=CommonUtil.changeStringToList(dispStatuss);
    	List<Object> assignStatussList=CommonUtil.changeStringToList(assignStatuss);
    	List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
    	
    	
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

		List<GsPlanOptCommonDets> dishList = new ArrayList<>();
		//总校分校标识转换
		//hive库中0：分校 1：总校
		//接口入参：1：总校 2：分校
		if(schGenBraFlag == 1) {
			schGenBraFlag =0;
		}else if (schGenBraFlag == 2) {
			schGenBraFlag =1;
		}
		
		
		dishList = dbHiveGsService.getGsDetsList(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
				subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, ppName,null,
				rmcName, null, schType,sendFlag, distNamesList, 
				subLevelsList, compDepsList, schPropsList, schTypesList, 
        		distrBatNumber,assignStatus,dispStatus,acceptStatus,
    	    	dispStatussList,assignStatussList,dispType, dispMode,
    	    	departmentId,departmentIdList,plastatus,
    	    	distrBatNumberList,
    	    	0,
				 (curPageNum-1)*pageSize, curPageNum*pageSize);
		dishList.removeAll(Collections.singleton(null));
		
		Integer pageTotalTemp = dbHiveGsService.getGsDetsCount(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
				subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, ppName,null,
				rmcName, null, schType, sendFlag, distNamesList, 
				subLevelsList, compDepsList, schPropsList, schTypesList,
        		distrBatNumber,assignStatus,dispStatus,acceptStatus,
    	    	dispStatussList,assignStatussList,dispType, dispMode,
    	    	departmentId,departmentIdList,plastatus,distrBatNumberList);
		
		if(pageTotalTemp!=null) {
			pageTotal = pageTotalTemp;
		}
		
		for(GsPlanOptCommonDets commonDets : dishList) {
			GsPlanOptDets pdd = new GsPlanOptDets();
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
				
				//配送类型
				if(pdd.getDispType().equalsIgnoreCase("0"))
					pdd.setDispType("原料");
				else if(pdd.getDispType().equalsIgnoreCase("1"))
					pdd.setDispType("成品菜");
				else
					pdd.setDispType("-");
				
				//配送方式，0:统配，1:直配
				if(pdd.getDispMode().equalsIgnoreCase("0"))
					pdd.setDispMode("统配");
				else if(pdd.getDispMode().equalsIgnoreCase("1"))
					pdd.setDispMode("直配");
				else
					pdd.setDispMode("-");
				
				//所属区域名称
				pdd.setSubDistName("-");
				if(CommonUtil.isNotEmpty(pdd.getSubDistName())) {
					pdd.setSubDistName(AppModConfig.distIdToNameMap.get(pdd.getSubDistName()));
				}
				
				Integer dispPlanStatus = commonDets.getHaulStatus();
				//-2 信息不完整 -1 未指派 0 已指派（未配送） 1配送中 2 待验收（已配送）3已验收 -4已取消
				//指派状态，0:未指派，1：已指派，2:已取消
				int curAssignStatus = 0;
				if(dispPlanStatus == 0 || dispPlanStatus == 1 || dispPlanStatus == 2 || dispPlanStatus == 3)
					curAssignStatus = 1;
				else if(dispPlanStatus == -4)
					curAssignStatus = 2;
				pdd.setAssignStatus(curAssignStatus);
				//配送状态0:未派送，1:配送中，2: 已配送
				int curDispStatus = 0;
				if(dispPlanStatus == 2 || dispPlanStatus == 3)
					curDispStatus = 2;
				else if(dispPlanStatus == 1)
					curDispStatus = 1;
				pdd.setDispStatus(curDispStatus);
				//验收状态0:待验收，1:已验收
				int curAcceptStatus = 0;
				if(dispPlanStatus == 3)
					curAcceptStatus = 1;
				pdd.setAcceptStatus(curAcceptStatus);
				
				//配货单操作状态
				pdd.setPlaStatus(commonDets.getDisDealStatus());
				
				gsPlanOptDets.add(pdd);
				
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			}
		}
		
		//时戳
		gpodDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		gpodDto.setPageInfo(pageInfo);
		//设置数据
		gpodDto.setGsPlanOptDets(gsPlanOptDets);
		//消息ID
		gpodDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gpodDto;
	}
	
	
	// 配货计划操作详情列表函数
	GsPlanOptDetsDTO gsPlanOptDets(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, 
			Db1Service db1Service, SaasService saasService, 
			int subLevel, int compDep, int schGenBraFlag, String subDistName, int fblMb, 
			int schProp, String ppName, String rmcName, int assignStatus, int dispStatus, 
			int acceptStatus, String distrBatNumber, int schType, int dispType, 
			int dispMode, int sendFlag,
			String distNames,String subLevels,String compDeps,String schProps,
			String schTypes,String dispStatuss,String assignStatuss,String departmentIds,String plastatus,
			List<String> distrBatNumberList
			) {
		GsPlanOptDetsDTO gpodDto = new GsPlanOptDetsDTO();
		List<GsPlanOptDets> gsPlanOptDets = new ArrayList<>();
		Map<String, String> distributionDetailMap = new HashMap<>();
		int i, j, k, dateCount = dates.length;
		String key = null;
		String [] keyVals = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
    	List<Object> schPropsList=CommonUtil.changeStringToList(schProps);
    	List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
    	List<Object> dispStatussList=CommonUtil.changeStringToList(dispStatuss);
    	List<Object> assignStatussList=CommonUtil.changeStringToList(assignStatuss);
    	//管理部门
    	List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
    	
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
    	
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_Distribution-Detail";
			//(id_主键id_type_配送类型（1 原料，2 成品菜）_schoolid_学校id_area_区号_sourceid_团餐公司id_batchno_发货批次号_delivery_统配/直配)
			//  配送状态_deliveryDate_验收上报时间_disstatus_配送规则_purchaseDate_进货时间_deliveryReDate_验收时间（手动）
			distributionDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionDetailMap != null) {
				for (String curKey : distributionDetailMap.keySet()) {
					keyVals = distributionDetailMap.get(curKey).split("_");
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKeys.length >= 14) {
						if(distIdorSCName != null) {
							if(curKeys[7].compareTo(distIdorSCName) != 0)
								continue ;
						}else if(distNamesList!=null && distNamesList.size() >0) {
							if(StringUtils.isEmpty(curKeys[7]) || !StringUtils.isNumeric(curKeys[7])) {
								continue;
							}
							if(!distNamesList.contains(curKeys[7])) {
								continue ;
							}
						}
						GsPlanOptDets gpod = new GsPlanOptDets();
						
						//如果value值为空或者value第一个值不是数字，则不做统计
						if(keyVals.length < 1 ) {
							continue;
						}
						int dispPlanStatus = -1;
						if(CommonUtil.isNotEmpty(keyVals[0]))
							dispPlanStatus = Integer.parseInt(keyVals[0]);
						
						//学校信息（项目点）
						TEduSchoolDo tesDo = null;
						if(schIdMap.containsKey(curKeys[5])) {
							j = schIdMap.get(curKeys[5]);
							tesDo = tesDoList.get(j-1);
						}
						if(tesDo == null)
							continue;
						//配货日期
						gpod.setDistrDate(dates[k].replaceAll("-", "/"));
						//验收操作时间
						if(keyVals.length >= 3 && keyVals[2] !=null && !"null".equals(keyVals[2])) {
							gpod.setAcceptTime(keyVals[2].replaceAll("-", "/"));
						}else {
							gpod.setAcceptTime("");
						}
						
						//配货批次号
						gpod.setDistrBatNumber(curKeys[11]);
						//项目点
						gpod.setPpName(tesDo.getSchoolName());						
	    				//总校/分校
						int curSchGenBraFlag = 0;
						gpod.setSchGenBraFlag("-");
	    				if(tesDo.getIsBranchSchool() != null) {
	    					if(tesDo.getIsBranchSchool() == 1) { //分销
	    						curSchGenBraFlag = 2;
	    						gpod.setSchGenBraFlag("分校");
	    					}
	    					else {   //总校
	    						curSchGenBraFlag = 1;
	    						gpod.setSchGenBraFlag("总校");
	    					}
	    				}
	    				//分校数量
	    				int curBraCampusNum = 0;
	    				gpod.setBraCampusNum(curBraCampusNum);
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
	    						gpod.setBraCampusNum(curBraCampusNum);
	    				}
	    				//关联总校
	    				gpod.setRelGenSchName("-");
	    				if(schIdMap.containsKey(tesDo.getParentId())) {
	    					i = schIdMap.get(tesDo.getParentId())-1;
	    					gpod.setRelGenSchName(tesDoList.get(i).getSchoolName());
	    				}  
	    				
	    				//所属
	    				int curSubLevel = 0;
	    				if(tesDo.getDepartmentMasterId() != null) {
	    					curSubLevel = Integer.parseInt(tesDo.getDepartmentMasterId());
	    				}
	    				gpod.setSubLevel(AppModConfig.subLevelIdToNameMap.get(curSubLevel));
	    				//主管部门
	    				int curCompDep = 0;
	    				gpod.setCompDep("其他");
	    				if(curSubLevel == 0) {      //其他
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					gpod.setCompDep(AppModConfig.compDepIdToNameMap0.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel ==1) {      //部级   
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					gpod.setCompDep(AppModConfig.compDepIdToNameMap1.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel == 2) {      //市级
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
	    					}
	    					gpod.setCompDep(AppModConfig.compDepIdToNameMap2.get(String.valueOf(curCompDep)));
	    				}
	    				else if(curSubLevel == 3) {      //区级
	    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
	    						String orgName = AppModConfig.compDepIdToNameMap3bd.get(tesDo.getDepartmentSlaveId());
	    						if(orgName != null) {
	    							curCompDep = Integer.parseInt(AppModConfig.compDepNameToIdMap3.get(orgName));
	    						}
	    					}
	    					gpod.setCompDep(AppModConfig.compDepIdToNameMap3.get(String.valueOf(curCompDep)));
	    				}    				
	    				//所属区域名称
	    				gpod.setSubDistName("-");
	    				if(tesDo.getSchoolAreaId() != null)
	    					gpod.setSubDistName(AppModConfig.distIdToNameMap.get(tesDo.getSchoolAreaId()));
	    				//证件主体，0:学校，1:外包
	    				gpod.setFblMb("-");
	    				if(tesDo.getLicenseMainType() != null) {
	    					int curFblMb = Integer.parseInt(tesDo.getLicenseMainType());
	    					if(AppModConfig.fblMbIdToNameMap.containsKey(curFblMb))
	    						gpod.setFblMb(AppModConfig.fblMbIdToNameMap.get(curFblMb));
	    				}
	    				//学校性质
						gpod.setSchProp(AppModConfig.getSchProp(tesDo.getSchoolNature()));
						//学校学制
						gpod.setSchType("-");
						if(tesDo != null)
							gpod.setSchType(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2()));
						//区
						gpod.setDistName(curKeys[7]);
						//团餐公司
						if(RmcIdToNameMap.containsKey(curKeys[9]))
							gpod.setRmcName(RmcIdToNameMap.get(curKeys[9]));
						else
							gpod.setRmcName("-");
						//配送类型
						if(curKeys[3].equalsIgnoreCase("1"))
							gpod.setDispType("原料");
						else if(curKeys[3].equalsIgnoreCase("2"))
							gpod.setDispType("成品菜");
						else
							gpod.setDispType("-");
						//配货方式
						gpod.setDispMode(curKeys[13]);				
						//指派状态
						int curAssignStatus = 0;
						if(dispPlanStatus == 0 || dispPlanStatus == 1 || dispPlanStatus == 2 || dispPlanStatus == 3)
							curAssignStatus = 1;
						else if(dispPlanStatus == 4)
							curAssignStatus = 2;
						gpod.setAssignStatus(curAssignStatus);
						//配送状态
						int curDispStatus = 0;
						if(dispPlanStatus == 2 || dispPlanStatus == 3)
							curDispStatus = 2;
						else if(dispPlanStatus == 1)
							curDispStatus = 1;
						gpod.setDispStatus(curDispStatus);
						//验收状态
						int curAcceptStatus = 0;
						if(dispPlanStatus == 3)
							curAcceptStatus = 1;
						gpod.setAcceptStatus(curAcceptStatus);
						//发送状态
						gpod.setSendFlag(0);
						
	    				//管理部门
						gpod.setDepartmentId(tesDo.getDepartmentId()==null?"":tesDo.getDepartmentId());
						//操作状态
						if(keyVals.length >= 5 && CommonUtil.isNotEmpty(keyVals[4])) {
							gpod.setPlaStatus(keyVals[4]);
						}else {
							gpod.setPlaStatus("");
						}
						
						//条件判断
						boolean isAdd = true;
						int[] flIdxs = new int[15];
						//判断项目点名称（判断索引0）
						if(ppName != null) {
							if(!tesDo.getId().equals(ppName))
								flIdxs[0] = -1;
						}
						//判断团餐公司（判断索引1）
						if(rmcName != null && RmcIdToNameMap != null) {
							if(!(gpod.getRmcName().equalsIgnoreCase(RmcIdToNameMap.get(rmcName))))
								flIdxs[1] = -1;
						}
						//判断指派状态（判断索引2）
						if(assignStatus != -1) {
							if(gpod.getAssignStatus() != assignStatus)
								flIdxs[2] = -1;
						}else if(assignStatussList!=null && assignStatussList.size() >0) {
							if(!assignStatussList.contains(String.valueOf(gpod.getAssignStatus()))) {
								flIdxs[2] = -1;
							}
						}	
						//判断配送状态（判断索引3）
						if(dispStatus != -1) {
							if(gpod.getDispStatus() != dispStatus)
								flIdxs[3] = -1;
						}else if(dispStatussList!=null && dispStatussList.size() >0) {
							if(!dispStatussList.contains(String.valueOf(gpod.getDispStatus()))) {
								flIdxs[3] = -1;
							}
						}					
						//判断验收状态（判断索引4）
						if(acceptStatus != -1) {
							if(gpod.getAcceptStatus() != acceptStatus)
								flIdxs[4] = -1;
						}
						//判断配货批次号（判断索引5）
						if(distrBatNumber != null) {
							if(gpod.getDistrBatNumber().indexOf(distrBatNumber) == -1)
								flIdxs[5] = -1;
						}
						
						if(distrBatNumberList!=null && distrBatNumberList.size() >0) {
							if(!distrBatNumberList.contains(String.valueOf(gpod.getDistrBatNumber()))) {
								flIdxs[5] = -1;
							}
						}	
						
						//判断学校类型（学制）（判断索引6）
						if(schType != -1) {
							if(AppModConfig.schTypeNameToIdMap.containsKey(gpod.getSchType())) {
								int curSchType = AppModConfig.schTypeNameToIdMap.get(gpod.getSchType());
								if(curSchType != schType)
									flIdxs[6] = -1;
							}
							else
								flIdxs[6] = -1;
						}else if(schTypesList!=null && schTypesList.size() >0) {
							if(AppModConfig.schTypeNameToIdMap.containsKey(gpod.getSchType())) {
								int curSchType = AppModConfig.schTypeNameToIdMap.get(gpod.getSchType());
								if(!schTypesList.contains(String.valueOf(curSchType))) {
									flIdxs[6] = -1;
								}
							}else{
								flIdxs[6] = -1;
							}
						}	
						//判断配送类型（判断索引7）
						if(dispType != -1) {
							if(AppModConfig.dispTypeIdToNameMap.containsKey(dispType)) {
								if(!(gpod.getDispType().equalsIgnoreCase(AppModConfig.dispTypeIdToNameMap.get(dispType))))
									flIdxs[7] = -1;
							}
							else
								flIdxs[7] = -1;
						}
						//判断配送方式（判断索引8）
						if(dispMode != -1) {							
							if(AppModConfig.dispModeIdToNameMap.containsKey(dispMode)) {
								if(!(gpod.getDispMode().equalsIgnoreCase(AppModConfig.dispModeIdToNameMap.get(dispMode))))
									flIdxs[8] = -1;
							}
							else
								flIdxs[8] = -1;
						}
						//判断所属（判断索引9）
						if(subLevel != -1) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(gpod.getSubLevel());
							if(curSubLevel != subLevel)
								flIdxs[9] = -1;
						}else if(subLevelsList!=null && subLevelsList.size() >0) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(gpod.getSubLevel());
							if(!subLevelsList.contains(String.valueOf(curSubLevel))) {
								flIdxs[9] = -1;
							}
						}
						//判断主管部门（判断索引10）
						if(compDep != -1) {
							curCompDep = 0;
							if(subLevel == 0) {    //其他
								String strCompDepId = AppModConfig.compDepNameToIdMap0.get(gpod.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[10] = -1;
								}
								else
									flIdxs[10] = -1;
							}
							else if(subLevel == 1) {    //部署
								String strCompDepId = AppModConfig.compDepNameToIdMap1.get(gpod.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[10] = -1;
								}
								else
									flIdxs[10] = -1;
							}
							else if(subLevel == 2) {    //市属
								String strCompDepId = AppModConfig.compDepNameToIdMap2.get(gpod.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[10] = -1;
								}
								else
									flIdxs[10] = -1;
							}
							else if(subLevel == 3) {    //区属
								String strCompDepId = AppModConfig.compDepNameToIdMap3.get(gpod.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(curCompDep != compDep)
										flIdxs[10] = -1;
								}
								else
									flIdxs[10] = -1;
							}
						}else if(compDepsList!=null && compDepsList.size() >0) {
							curSubLevel = AppModConfig.subLevelNameToIdMap.get(gpod.getSubLevel());
							curCompDep = 0;
							if(curSubLevel == 0) {    //其他
								String strCompDepId = AppModConfig.compDepNameToIdMap0.get(gpod.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[10] = -1;
									}
								}
								else
									flIdxs[10] = -1;
							}
							else if(curSubLevel == 1) {    //部署
								String strCompDepId = AppModConfig.compDepNameToIdMap1.get(gpod.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[10] = -1;
									}
								}
								else
									flIdxs[10] = -1;
							}
							else if(curSubLevel == 2) {    //市属
								String strCompDepId = AppModConfig.compDepNameToIdMap2.get(gpod.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[10] = -1;
									}
								}
								else
									flIdxs[10] = -1;
							}
							else if(curSubLevel == 3) {    //区属
								String strCompDepId = AppModConfig.compDepNameToIdMap3.get(gpod.getCompDep());
								if(strCompDepId != null) {
									curCompDep = Integer.parseInt(strCompDepId);
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[10] = -1;
									}
								}
								else
									if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
										flIdxs[10] = -1;
									}
							}
							
						}
						//判断总分校标识（判断索引11）
						if(schGenBraFlag != -1) {
							curSchGenBraFlag = AppModConfig.genBraSchNameToIdMap.get(gpod.getSchGenBraFlag());
							if(curSchGenBraFlag != schGenBraFlag)
								flIdxs[11] = -1;
						}
						//判断所属区域名称（判断索引12）
						if(subDistName != null) {
							if(gpod.getSubDistName() != null) {
								if(!gpod.getSubDistName().equals(subDistName))
									flIdxs[12] = -1;
							}
							else
								flIdxs[12] = -1;
						}
						//判断证件主体（判断索引13）
						if(fblMb != -1) {
							if(AppModConfig.fblMbNameToIdMap.get(gpod.getFblMb()) == null) {
								flIdxs[13] = -1;
							}else {
								int curFblMb = AppModConfig.fblMbNameToIdMap.get(gpod.getFblMb());
								if(curFblMb != fblMb)
									flIdxs[13] = -1;
							}
						}
						//判断学校性质（判断索引14）
						if(schProp != -1) {
							int curSchProp = AppModConfig.schPropNameToIdMap.get(gpod.getSchProp());
							if(curSchProp != schProp)
								flIdxs[14] = -1;
						}else if(schPropsList!=null && schPropsList.size() >0) {
							int curSchProp = AppModConfig.schPropNameToIdMap.get(gpod.getSchProp());
							if(!schPropsList.contains(String.valueOf(curSchProp))) {
								flIdxs[14] = -1;
							}
						}
						
						//主管部门编号（判断索引8）
						if(departmentId != null) {
							if(gpod.getDepartmentId() != null) {
								if(!gpod.getDepartmentId().equals(departmentId))
									flIdxs[8] = -1;
							}
							else
								flIdxs[8] = -1;
							
						}
						if(departmentIdList!=null && departmentIdList.size() >0) {
							if(gpod.getDepartmentId() != null) {
								if(!departmentIdList.contains(gpod.getDepartmentId()))
									flIdxs[8] = -1;
							}
							else {
								flIdxs[8] = -1;
							}
						}
						
						//判断操作状态（判断索引7）
						if(CommonUtil.isNotEmpty(plastatus)) {
							if(CommonUtil.isEmpty(gpod.getPlaStatus()) || !plastatus.equals(gpod.getPlaStatus() ))
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
						if(isAdd)
							gsPlanOptDets.add(gpod);
					}
					else
						logger.info("配货计划："+ curKey + "，格式错误！");
				}
			}
		}
		//排序
    	SortList<GsPlanOptDets> sortList = new SortList<GsPlanOptDets>();  
    	sortList.Sort3Level(gsPlanOptDets, methods, sorts, dataTypes);		
		//时戳
		gpodDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<GsPlanOptDets> pageBean = null;
		if(curPageNum == -1 || pageSize == -1) {
			pageBean = new PageBean<GsPlanOptDets>(gsPlanOptDets, 1, gsPlanOptDets.size());
		}else {
			pageBean = new PageBean<GsPlanOptDets>(gsPlanOptDets, curPageNum, pageSize);
		}
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		gpodDto.setPageInfo(pageInfo);
		//设置数据
		gpodDto.setGsPlanOptDets(pageBean.getCurPageData());
		//消息ID
		gpodDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gpodDto;
	}

	//配货计划操作详情列表模型函数
	public GsPlanOptDetsDTO appModFunc(String token, String startDate, String endDate, String ppName,
			String distName, String prefCity, String province, String subLevel, String compDep, 
			String schGenBraFlag, String subDistName, String fblMb, String schProp, String rmcName, 
			String assignStatus, String dispStatus, String acceptStatus, String distrBatNumber, 
			String schType, String dispType, String dispMode, String sendFlag,
			String distNames,String subLevels,String compDeps,String schProps,
			String schTypes,String dispStatuss,String assignStatuss,
			String departmentId,String departmentIds,String plastatus,
			List<String> distrBatNumberList,
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveGsService dbHiveGsService) {
		GsPlanOptDetsDTO gpodDto = null;
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
			//学校性质，0:公办，1:民办，2:其他
			int curSchProp = -1;
			if(schProp != null)
				curSchProp = Integer.parseInt(schProp);
			//指派状态，0:未指派，1：已指派，2:已取消
			int curAssignStatus = -1;
			if(assignStatus != null)
				curAssignStatus = Integer.parseInt(assignStatus);
			//配送状态，0:未派送，1:配送中，2: 已配送
			int curDispStatus = -1;
			if(dispStatus != null)
				curDispStatus = Integer.parseInt(dispStatus);
			//验收状态，0:待验收，1:已验收
			int curAcceptStatus = -1;
			if(acceptStatus != null)
				curAcceptStatus = Integer.parseInt(acceptStatus);
			//学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，
			//10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
			int curSchType = -1;
			if(schType != null)
				curSchType = Integer.parseInt(schType);
			//配送类型，0:原料，1:成品菜
			int curDispType = -1;
			if(dispType != null)
				curDispType = Integer.parseInt(dispType);
			//配送方式，0:统配，1:直配
			int curDispMode = -1;
			if(dispMode != null)
				curDispMode = Integer.parseInt(dispMode);
			//是否发送
			int curSendFlag = -1;
			if(sendFlag != null)
				curSendFlag = Integer.parseInt(sendFlag);
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
					// 配货计划操作详情列表函数
				  	if(methodIndex == 1) {
						gpodDto = gsPlanOptDets(departmentId,distIdorSCName, dates, tddList, db1Service, saasService, curSubLevel, curCompDep, 
								curSchGenBraFlag, subDistName, curFblMb, curSchProp, ppName, rmcName, curAssignStatus, 
								curDispStatus, curAcceptStatus, distrBatNumber, curSchType, curDispType, 
								curDispMode, curSendFlag,
								distNames,subLevels,compDeps,schProps,
								schTypes,dispStatuss,assignStatuss,departmentIds,plastatus,distrBatNumberList);	
				  	}else if (methodIndex == 2) {
				  		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						if(days >= 2) {
							gpodDto = gsPlanOptDetsFromHive(departmentId,distIdorSCName, dates, tddList, db1Service, saasService, curSubLevel, curCompDep, 
									curSchGenBraFlag, subDistName, curFblMb, curSchProp, ppName, rmcName, curAssignStatus, 
									curDispStatus, curAcceptStatus, distrBatNumber, curSchType, curDispType, 
									curDispMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,
									schTypes,dispStatuss,assignStatuss,
									departmentIds,plastatus,distrBatNumberList,
									dbHiveGsService);
						}else {
							gpodDto = gsPlanOptDets(departmentId,distIdorSCName, dates, tddList, db1Service, saasService, curSubLevel, curCompDep, 
									curSchGenBraFlag, subDistName, curFblMb, curSchProp, ppName, rmcName, curAssignStatus, 
									curDispStatus, curAcceptStatus, distrBatNumber, curSchType, curDispType, 
									curDispMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,
									schTypes,dispStatuss,assignStatuss,departmentIds,plastatus,distrBatNumberList);
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
					//获取用户数据权限信息
				  	/*UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);
				  	if(curSubLevel == -1)
				  		curSubLevel = udpiDto.getSubLevelId();
				  	if(curCompDep == -1)
				  		curCompDep = udpiDto.getCompDepId();*/
					// 配货计划操作详情列表函数
				  	if(methodIndex == 1) {
						gpodDto = gsPlanOptDets(departmentId,distIdorSCName, dates, tddList, db1Service, saasService, curSubLevel, 
								curCompDep, curSchGenBraFlag, subDistName, curFblMb, curSchProp, ppName, rmcName, 
								curAssignStatus, curDispStatus, curAcceptStatus, distrBatNumber, curSchType, 
								curDispType, curDispMode, curSendFlag,
								distNames,subLevels,compDeps,schProps,
								schTypes,dispStatuss,assignStatuss,departmentIds,plastatus,distrBatNumberList);
				  	}else if (methodIndex == 2) {
				  		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						if(days >= 2) {
							gpodDto = gsPlanOptDetsFromHive(departmentId,distIdorSCName, dates, tddList, db1Service, saasService, curSubLevel, 
									curCompDep, curSchGenBraFlag, subDistName, curFblMb, curSchProp, ppName, rmcName, 
									curAssignStatus, curDispStatus, curAcceptStatus, distrBatNumber, curSchType, 
									curDispType, curDispMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,
									schTypes,dispStatuss,assignStatuss,
									departmentIds,plastatus,distrBatNumberList,
									dbHiveGsService);
						}else {
							gpodDto = gsPlanOptDets(departmentId,distIdorSCName, dates, tddList, db1Service, saasService, curSubLevel, 
									curCompDep, curSchGenBraFlag, subDistName, curFblMb, curSchProp, ppName, rmcName, 
									curAssignStatus, curDispStatus, curAcceptStatus, distrBatNumber, curSchType, 
									curDispType, curDispMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,
									schTypes,dispStatuss,assignStatuss,departmentIds,plastatus,distrBatNumberList);
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
			gpodDto = SimuDataFunc();
		}

		return gpodDto;
	}
}