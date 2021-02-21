package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpGsPlanOpts;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpGsPlanOptsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//项目点配货计划操作列表应用模型
public class PpGsPlanOptsAppMod {
	private static final Logger logger = LogManager.getLogger(PpGsPlanOptsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	/*//二级排序条件
	final String[] methods0 = {"getSubLevel", "getCompDep"};
	final String[] sorts0 = {"desc", "desc"};
	final String[] dataTypes0 = {"String", "String"};
	
	final String[] methods1 = {"getDistName", "getDishDate"};
	final String[] sorts1 = {"asc", "asc"};
	final String[] dataTypes1 = {"String", "String"};*/
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
	//三级排序条件
	final String[] methods = {"getDistName", "getSchType"};
	final String[] sorts = {"asc", "asc"};
	final String[] dataTypes = {"String", "String"};
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 2;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	
	//模拟数据函数
	private PpGsPlanOptsDTO exampleDataFunc() {
		String data = "{\n" + 
				"   \"time\": \"2016-07-14 09:51:35\",\n" + 
				"   \"pageInfo\":\n" + 
				"   {\n" + 
				"     \"pageTotal\":2,\n" + 
				"     \"curPageNum\":1\n" + 
				"   },\n" + 
				"   \"ppGsPlanOpts\": [\n" + 
				"{\n" + 
				"  \"distrDate\":\"2018/09/04\",\n" + 
				"\"distName\":\"徐汇区\",\n" + 
				"\"schType\":\"小学\",\n" + 
				"\"ppName\":\"上海市徐汇区向阳小学\",\n" + 
				"\"detailAddr\":\"\",\n" + 
				"\"projContact\":\"\",\n" + 
				"\"pcMobilePhone\":\"\",\n" + 
				"\"distrPlanNum\":2,\n" + 
				"\"acceptStatus\":0,\n" + 
				"\"acceptPlanNum\":1,\n" + 
				"\"noAcceptPlanNum\":1,\n" + 
				"\"assignStatus\":1,\n" + 
				"\"assignPlanNum\":1,\n" + 
				"\"noAssignPlanNum\":1,\n" + 
				"\"dispStatus\":1,\n" + 
				"\"dispPlanNum\":2,\n" + 
				"\"noDispPlanNum\":0\n" + 
				"},\n" + 
				"{\n" + 
				"  \"distrDate\":\"2018/09/04\",\n" + 
				"\"distName\":\"徐汇区\",\n" + 
				"\"schType\":\"小学\",\n" + 
				"\"ppName\":\"上海市徐汇区世界小学\",\n" + 
				"\"detailAddr\":\"\",\n" + 
				"\"projContact\":\"\",\n" + 
				"\"pcMobilePhone\":\"\",\n" + 
				"\"distrPlanNum\":2,\n" + 
				"\"acceptStatus\":0,\n" + 
				"\"acceptPlanNum\":1,\n" + 
				"\"noAcceptPlanNum\":1,\n" + 
				"\"assignStatus\":1,\n" + 
				"\"assignPlanNum\":1,\n" + 
				"\"noAssignPlanNum\":1,\n" + 
				"\"dispStatus\":1,\n" + 
				"\"dispPlanNum\":2,\n" + 
				"\"noDispPlanNum\":0\n" + 
				" } ],\n" + 
				"   \"msgId\":1\n" + 
				"}\n" + 
				"";
		
		
		PpGsPlanOptsDTO drsDto = new PpGsPlanOptsDTO();
		JSONObject jsStr = JSONObject.parseObject(data); 
		drsDto = (PpGsPlanOptsDTO) JSONObject.toJavaObject(jsStr,PpGsPlanOptsDTO.class);
		
		
		//消息ID
		drsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return drsDto;
	}
	
	// 配货列表函数
	private PpGsPlanOptsDTO dishRetSamplesOne(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			String ppName,Integer acceptStatus,Integer assignStatus,
			Integer dispStatus,Integer schType,String distNames,String schTypes,
			String departmentIds,String plastatus,
			int subLevel, int compDep,String subLevels,String compDeps,
			Db1Service db1Service) {
		PpGsPlanOptsDTO drsDto = new PpGsPlanOptsDTO();
		List<PpGsPlanOpts> dishRetSamples = new ArrayList<>();
		String key = "";
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
		List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
		
		//获取学校
		List<TEduSchoolDo> schoolList = new ArrayList<TEduSchoolDo>();
		if(distIdorSCName!=null && !"".equals(distIdorSCName)) {
			schoolList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 5);
		}else {
			schoolList = db1Service.getTEduSchoolDoListByDs1(distNamesList,1,1);
		}
		
		Map<String,TEduSchoolDo> schoolMap = schoolList.stream().collect(Collectors.toMap(TEduSchoolDo::getId,(b)->b));
		
		// 当天排菜学校总数
		Map<String, String>  platoonFeedTotalMap = null;
		int  k;
		// 当天各区菜品留样数量
		for (k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k] + "_DistributionTotal_child";
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(platoonFeedTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				//platoonFeedTotalMap = AppModConfig.getHdfsDataKey(dates[k], key);
			}
			if(platoonFeedTotalMap != null) {
				for(Map.Entry<String, String> entry : platoonFeedTotalMap.entrySet()) {
					String keys[] =entry.getKey().split("_");
					String values[] =entry.getValue().split("_");
					
					
					String schoolId = keys[3];
					TEduSchoolDo eduSchool = schoolMap.get(schoolId);
					if(eduSchool==null) {
						continue;
					}
					
					//主管部门编号（判断索引8）
					if(departmentId != null) {
						if(eduSchool.getDepartmentId() != null) {
							if(!eduSchool.getDepartmentId().equals(departmentId))
								continue;
						}else {
							continue;
						}
						
					}
					if(departmentIdList!=null && departmentIdList.size() >0) {
						if(eduSchool.getDepartmentId() != null) {
							if(!departmentIdList.contains(eduSchool.getDepartmentId()))
								continue;
						}
						else {
							continue;
						}
					}
					
					//判断操作状态（判断索引7）
					if(CommonUtil.isNotEmpty(plastatus)) {
						if(CommonUtil.isEmpty(values[11]) || !plastatus.equals(values[11]))
							continue;
					}
					
					//过滤区
					if(distIdorSCName != null) {
						if(keys[1].compareTo(distIdorSCName) != 0) {
							continue ;
						}
					}else if(distNamesList!=null && distNamesList.size() >0) {
						if(StringUtils.isEmpty(keys[1]) || !StringUtils.isNumeric(keys[1])) {
							continue;
						}
						if(!distNamesList.contains(keys[1])) {
							continue ;
						}
					}
					
					//验收状态，0:待验收，1:已验收
					if(acceptStatus != null) {
						if(acceptStatus.equals(0) && (values[9].equals("3") || values[9].equals("4"))) {
							continue;
						}else if(acceptStatus.equals(1) && !values[9].equals("3")) {
							continue;
						}
					}
					//指派状态，0:未指派，1：已指派，2:已取消
					if(assignStatus != null) {
						if(assignStatus.equals(0) && !values[9].equals("-1") && !values[9].equals("-2")) {
							continue;
						}else if(assignStatus.equals(1) && !values[9].equals("0") && !values[9].equals("1") 
								&& !values[9].equals("2") && !values[9].equals("3")) {
							continue;
						}
						//已取消数据不统计（李左明确认）
						/*else if(assignStatus.equals(2) && !values[9].equals("4")) {
							continue;
						}*/
					}
					
					//过滤配送状态，0:未派送，1: 已配送 
					if(dispStatus != null) {
						if(dispStatus.equals(0) && !values[9].equals("-2") && !values[9].equals("-1") && 
								!values[9].equals("0")&& !values[9].equals("1")) {
							continue;
						}else if(dispStatus.equals(1) && !values[9].equals("2")&& !values[9].equals("3")) {
							continue;
						}
					}
					
					
					//过滤学校学制学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，
					//12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
					Integer schTypeId = AppModConfig.getSchTypeId(eduSchool.getLevel(), eduSchool.getLevel2());
					if(schType != null && schType != -1) {
						if(schTypeId==null || 
								!schTypeId.equals(schType)) {
							continue ;
						}
					}else if(schTypesList!=null && schTypesList.size() >0) {
						if(schTypeId==null || !schTypesList.contains(schTypeId.toString())) {
							continue;
						}
					}
					
					//过滤项目点
					if(ppName != null) {
						if(!eduSchool.getSchoolName().contains(ppName)) {
							continue ;
						}
					}
					
					//判断所属（判断索引9）
					if(subLevel != -1) {
						if(CommonUtil.isNotEmpty(eduSchool.getDepartmentMasterId())) {
							continue;
						}
						if(Integer.parseInt(eduSchool.getDepartmentMasterId()) != subLevel)
							continue;
					}
					
					if(subLevelsList!=null && subLevelsList.size() >0) {
						if(!subLevelsList.contains(eduSchool.getDepartmentMasterId())) {
							continue;
						}
					}
					//判断主管部门（判断索引10）
					if(compDep != -1) {
						if(subLevel == 0 || subLevel == 1 || subLevel == 2) {    //其他/部署/市属
							if(eduSchool.getDepartmentSlaveId() != null) {
								if(Integer.parseInt(eduSchool.getDepartmentSlaveId()) != compDep)
									continue;
							}
							else
								continue;
						}else if(subLevel == 3) {    //区属
							String strCompDepId = AppModConfig.compDepNameToIdMap3.get( AppModConfig.compDepIdToNameMap3bd.get(eduSchool.getDepartmentSlaveId()));
							if(strCompDepId != null) {
								if(Integer.parseInt(strCompDepId) != compDep)
									continue;
							}
							else
								continue;
						}
					}
					
					if(compDepsList!=null && compDepsList.size() >0) {
						if("0".equals(eduSchool.getDepartmentMasterId()) || 
								"1".equals(eduSchool.getDepartmentMasterId()) ||
								"2".equals(eduSchool.getDepartmentMasterId())) {    //其他
							if(eduSchool.getDepartmentMasterId() != null) {
								if(!compDepsList.contains(eduSchool.getDepartmentMasterId()+"_"+eduSchool.getDepartmentSlaveId())) {
									continue;
								}
							}
							else
								continue;
						}else if("3".equals(eduSchool.getDepartmentMasterId())) {    //区属
							String strCompDepId = AppModConfig.compDepNameToIdMap3.get( AppModConfig.compDepIdToNameMap3bd.get(eduSchool.getDepartmentSlaveId()));
							if(strCompDepId != null) {
								if(!compDepsList.contains(eduSchool.getDepartmentMasterId()+"_"+strCompDepId)) {
									continue;
								}
							}
							else
								if(!compDepsList.contains(eduSchool.getDepartmentMasterId()+"_"+strCompDepId)) {
									continue;
								}
						}
						
					}
					
					
					PpGsPlanOpts drs = new PpGsPlanOpts();
					//配货日期，格式：xxxx/xx/xx
					drs.setDistrDate(dates[k].replaceAll("-", "/"));
					//区域名称
					drs.setDistName(AppModConfig.distIdToNameMap.get(keys[1]));
					//学校类型（学制）
					drs.setSchType(AppModConfig.getSchType(eduSchool.getLevel(), eduSchool.getLevel2()));
					
					//项目点名称
					drs.setPpName(eduSchool.getSchoolName());
					//详细地址
					drs.setDetailAddr(eduSchool.getAddress()==null?"":eduSchool.getAddress());
					//项目联系人
					drs.setProjContact(eduSchool.getDepartmentHead()==null?"":eduSchool.getDepartmentHead());
					//手机
					drs.setPcMobilePhone(eduSchool.getDepartmentMobilephone()==null?"":eduSchool.getDepartmentMobilephone());
					
					//配货计划数量
					drs.setDistrPlanNum(0);
					if(StringUtils.isNumeric(values[1])) {
						drs.setDistrPlanNum(values[1]==null?0:Integer.parseInt(values[1]));
					}
					
					//验收状态，0:待验收，1:已验收
					drs.setAcceptStatus(values[9].equals("3")?1:0);
					//已验收数量
					if(StringUtils.isNumeric(values[3])) {
						drs.setAcceptPlanNum(values[3]==null?0:Integer.parseInt(values[3]));
					}
					
					//未验收数量(未验收数量 = 总数 - 已验收)
					int noAcceptPlanNum = drs.getDistrPlanNum() - drs.getAcceptPlanNum();
					if(noAcceptPlanNum < 0 ) {
						noAcceptPlanNum = 0;
					}
					drs.setNoAcceptPlanNum(noAcceptPlanNum);
					//指派状态，0:未指派，1：已指派
					int assignStatusTemp = 0;
					if(values[9].equals("0") || values[9].equals("1") || values[9].equals("2")
							|| values[9].equals("3")) {
						assignStatusTemp = 1;
					}else if (values[9].equals("-1") || values[9].equals("-2") ) {
						assignStatusTemp = 0;
					}
					/*else if (values[9].equals("4") ) {
						assignStatusTemp = 2;
					}*/
					drs.setAssignStatus(assignStatusTemp);
					//已指派数量 
					drs.setAssignPlanNum(0);
					if(StringUtils.isNumeric(values[5])) {
						drs.setAssignPlanNum(values[5]==null?0:Integer.parseInt(values[5]));
					}
					
					//未指派数量(未配送数量 = 总数 - 已配送)
					int noAssignPlanNum = drs.getDistrPlanNum() - drs.getAssignPlanNum();
					if(noAssignPlanNum < 0 ) {
						noAssignPlanNum = 0 ;
					}
					drs.setNoAssignPlanNum(noAssignPlanNum);
					//配送状态，0:未派送，1: 已配送
					int dispStatusTemp = 0;
					if(values[9].equals("0") || values[9].equals("-1") || values[9].equals("-2") || values[9].equals("1")) {
						dispStatusTemp = 0;
					}else if (values[9].equals("2") || values[9].equals("3") ) {
						dispStatusTemp = 1;
					}
					drs.setDispStatus(dispStatusTemp);
					//已配送数量
					drs.setDispPlanNum(0);
					if(StringUtils.isNumeric(values[7])) {
						drs.setDispPlanNum(values[7]==null?0:Integer.parseInt(values[7]));
					}
					
					//未配送数量(未指派数量 = 总数 -已指派)
					int noDispPlanNum = drs.getDistrPlanNum() -drs.getDispPlanNum();
					if(noDispPlanNum<0) {
						noDispPlanNum = 0;
					}
					drs.setNoDispPlanNum(noDispPlanNum);
					
    				//管理部门
					drs.setDepartmentId(eduSchool.getDepartmentId()==null?"":eduSchool.getDepartmentId());
					//操作状态
					if(values.length >= 12 && CommonUtil.isNotEmpty(values[11])) {
						drs.setPlaStatus(values[11]);
					}else {
						drs.setPlaStatus("");
					}
					
					dishRetSamples.add(drs);
				}
			}
		}
		//排序
		/*String[] methods = {"getDistName","getSchType"};
		String[] sorts = {"asc","asc"};
		String[] dataTypes = {"String","String"};*/
		
		SortList<PpGsPlanOpts> sortList = new SortList<PpGsPlanOpts>();
		sortList.Sort(dishRetSamples, methods, sorts, dataTypes);
		// 设置返回数据
		drsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<PpGsPlanOpts> pageBean = null;
		if(curPageNum == -1 || pageSize == -1) {
			pageBean = new PageBean<PpGsPlanOpts>(dishRetSamples, 1, dishRetSamples.size());
		}else {
			pageBean = new PageBean<PpGsPlanOpts>(dishRetSamples, curPageNum, pageSize);
		}
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		drsDto.setPageInfo(pageInfo);
		// 设置数据
		drsDto.setPpGsPlanOpts(pageBean.getCurPageData());
		// 消息ID
		drsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return drsDto;
	}
	
	// 配货列表函数
	private PpGsPlanOptsDTO dishRetSamplesFromHive(String departmentId,String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			String ppName,Integer acceptStatus,Integer assignStatus,
			Integer dispStatus,Integer schType,String distNames,String schTypes,
			String departmentIds,String disDealStatus,
			int subLevel, int compDep,String subLevels,String compDeps,
			Db1Service db1Service,DbHiveDishService dbHiveDishService) {
		PpGsPlanOptsDTO drsDto = new PpGsPlanOptsDTO();
		List<PpGsPlanOpts> ppGsPlanOpts = new ArrayList<>();
		try {
			List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
			List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
			List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
	    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
	    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
			
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
			//从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据列表
			dishList = dbHiveDishService.getDishDetsList(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
					-1, -1, -1, null, -1, -1, -1, ppName,
					null, null, schType==null?-1:schType, 1, -1, -1, distNamesList, 
					subLevelsList, compDepsList, null, schTypesList, null, 
					null,acceptStatus,assignStatus,dispStatus,departmentId,departmentIdsList,null,null,disDealStatus,null, 0,(curPageNum-1)*pageSize, curPageNum*pageSize);
			dishList.removeAll(Collections.singleton(null));
			
			//从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据条数
			Integer pageTotalTemp = dbHiveDishService.getDishDetsCount(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
					-1, -1, -1, null, -1, -1, -1, ppName,
					null, null, schType==null?-1:schType, 1, -1, -1, distNamesList, 
					subLevelsList, compDepsList, null, schTypesList, null, 
					null,acceptStatus,assignStatus,dispStatus,departmentId,departmentIdsList,null,null,disDealStatus,null);
			
			if(pageTotalTemp!=null) {
				pageTotal = pageTotalTemp;
			}
					
			for(PpDishCommonDets commonDets : dishList) {
				//配送的基础信息表
				PpGsPlanOpts pdd = new PpGsPlanOpts();
				try {
					
					BeanUtils.copyProperties(pdd, commonDets);
					
					//配货时间
					pdd.setDistrDate(commonDets.getDishDate());
					
					//区，接口返回的区名称
					pdd.setDistName(AppModConfig.distIdToNameMap.get(pdd.getDistName()));
					
					//未验收数量
					pdd.setNoAcceptPlanNum(pdd.getDistrPlanNum() - pdd.getAcceptPlanNum());
					
					//未指派数量
					pdd.setNoAssignPlanNum(pdd.getDistrPlanNum() - pdd.getAssignPlanNum());
					//未配送数量
					pdd.setNoDispPlanNum(pdd.getDistrPlanNum() - pdd.getDispPlanNum());
					
					//-2 信息不完整 -1 未指派 0 已指派（未配送） 1配送中 2 待验收（已配送）3已验收 -4已取消
					if(commonDets.getHaulStatus() !=null) {
						//验收状态，0:待验收，1:已验收
						if(commonDets.getHaulStatus() >= 3) {
							pdd.setAcceptStatus(1);
						}else {
							pdd.setAcceptStatus(0);
						}
						
						//指派状态，0:未指派，1：已指派
						if(commonDets.getHaulStatus() >= 0) {
							pdd.setAssignStatus(1);
						}else {
							pdd.setAssignStatus(0);
						}
						
						//配送状态，0:未配送，1: 已配送
						if(commonDets.getHaulStatus() >= 2) {
							pdd.setDispStatus(1);
						}else {
							pdd.setDispStatus(0);
						}
					}

					//配货单操作状态
					pdd.setPlaStatus(commonDets.getDisDealStatus());
					ppGsPlanOpts.add(pdd);
					
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					logger.info(e.getMessage());
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					logger.info(e.getMessage());
				}
			}
		}catch (Exception e) {
			StackTraceElement ste =e.getStackTrace()[0];
			logger.info("异常："+ste.getLineNumber()+":"+ste.getMethodName()+":"+e.getMessage());
		}
		// 设置返回数据
		drsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//时戳
		drsDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		drsDto.setPageInfo(pageInfo);
		//设置数据
		drsDto.setPpGsPlanOpts(ppGsPlanOpts);
		// 消息ID
		drsDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return drsDto;
	}
	
	// 菜品留样列表模型函数
	public PpGsPlanOptsDTO appModFunc(String token,String startDate,String endDate,String ppName,
			Integer acceptStatus,Integer assignStatus, Integer dispStatus,Integer schType, String distName, 
			String prefCity, String province,String distNames,String schTypes,
			String departmentIds,String plastatus,
			String subLevel, String compDep,String subLevels,String compDeps,
			String page, String pageSize, Db1Service db1Service,
			EduSchoolService eduSchoolService, Db2Service db2Service,DbHiveDishService dbHiveDishService) {
		PpGsPlanOptsDTO drsDto = null;
		
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		
		//所属，0:其他，1:部属，2:市属，3: 区属
		int curSubLevel = -1;
		if(subLevel != null)
			curSubLevel = Integer.parseInt(subLevel);
		//主管部门，0:市教委，1:商委，2:教育部
		int curCompDep = -1;
		if(compDep != null)
			curCompDep = Integer.parseInt(compDep);
		
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
					if(methodIndex == 0) {
						drsDto = exampleDataFunc();
					}else if (methodIndex == 1) {
						// 菜品留样列表函数
						drsDto = dishRetSamplesOne(departmentId,distIdorSCName, dates, tedList,ppName,acceptStatus,assignStatus,
								dispStatus,schType,distNames,schTypes,departmentIds,plastatus,
								curSubLevel, curCompDep,subLevels,compDeps,
								db1Service);
					}else if (methodIndex == 2) {
						// 菜品留样列表函数
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						if(days >= 2) {
							drsDto = dishRetSamplesFromHive(departmentId,distIdorSCName, dates, tedList,ppName,acceptStatus,assignStatus,
									dispStatus,schType,distNames,schTypes,
									departmentIds,plastatus,
									curSubLevel, curCompDep,subLevels,compDeps,
									db1Service,dbHiveDishService);
						}else {
							drsDto = dishRetSamplesOne(departmentId,distIdorSCName, dates, tedList,ppName,acceptStatus,assignStatus,
									dispStatus,schType,distNames,schTypes,
									departmentIds,plastatus,
									curSubLevel, curCompDep,subLevels,compDeps,
									db1Service);
						}
					}
					
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
					
					if(methodIndex == 0) {
						drsDto = exampleDataFunc();
					}else if (methodIndex == 1) {
						// 菜品留样列表函数
						drsDto = dishRetSamplesOne(departmentId,distIdorSCName, dates, tedList,ppName,acceptStatus,assignStatus,
								dispStatus,schType,distNames,schTypes,
								departmentIds,plastatus,
								curSubLevel, curCompDep,subLevels,compDeps,
								db1Service);
					}else if (methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						if(days >= 2) {
							drsDto = dishRetSamplesFromHive(departmentId,distIdorSCName, dates, tedList,ppName,acceptStatus,assignStatus,
									dispStatus,schType,distNames,schTypes,
									departmentIds,plastatus,
									curSubLevel, curCompDep,subLevels,compDeps,
									db1Service,dbHiveDishService);
						}else {
							drsDto = dishRetSamplesOne(departmentId,distIdorSCName, dates, tedList,ppName,acceptStatus,assignStatus,
									dispStatus,schType,distNames,schTypes,
									departmentIds,plastatus,
									curSubLevel, curCompDep,subLevels,compDeps,
									db1Service);
						}
					}
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}			
		}
		else {    //模拟数据
			//模拟数据函数
			drsDto = exampleDataFunc();
		}		

		return drsDto;
	}
	
	public static void main(String[] args) {
			PpGsPlanOpts pdd = new PpGsPlanOpts();
			PpDishCommonDets 	commonDets = new PpDishCommonDets();
				try {
					BeanUtils.copyProperties(pdd, commonDets);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
}
