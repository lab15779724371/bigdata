package com.tfit.BdBiProcSrvShEduOmc.appmod.im.outside;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.outside.PpDishDetsOutside;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.outside.PpDishDetsOutsideDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//项目点排菜详情列表应用模型
public class PpDishDetsOutsideAppMod {
	private static final Logger logger = LogManager.getLogger(PpDishDetsOutsideAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//三级排序条件
	final String[] methods = {"getDistName", "getSchType", "getPpName"};
	final String[] sorts = {"asc", "asc", "asc"};
	final String[] dataTypes = {"String", "String", "String"};
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	
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
	
	//获取学校名称到团餐公司映射
	Map<String, String> getSchoolNameToRmcNameMap(Db1Service db1Service, SaasService saasService) {
		Map<String, String> schNameToSupNameMap = null;
		//从数据源ds1的数据表t_edu_school中查找所有id以区域ID（空时在查询所有）和输出字段方法
	    List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(null,1,1, 0);
	    if(tesDoList != null) {
	    	schNameToSupNameMap = new HashMap<>();
	    	//学校名称到学校id映射
	    	Map<String, String> schNameToIdMap = new HashMap<>();
	    	for(int i = 0; i < tesDoList.size(); i++)
	    		schNameToIdMap.put(tesDoList.get(i).getSchoolName(), tesDoList.get(i).getId());
	    	//学校id和团餐公司id映射
	    	Map<String, String> schIdToSupplierIdMap = new HashMap<>();
	    	List<TEduSchoolSupplierDo> tessDoList = saasService.getAllSupplierIdSchoolId();
	    	if(tesDoList != null) {
	    		for(int i = 0; i < tessDoList.size(); i++)
	    			schIdToSupplierIdMap.put(tessDoList.get(i).getSchoolId(), tessDoList.get(i).getSupplierId());
	    	}
	    	//团餐公司id和团餐公司名称
	    	List<TProSupplierDo> tpsDoList = saasService.getRmcIdName();
	    	if(tpsDoList != null) {
	    		for(int i = 0; i < tpsDoList.size(); i++) {
	    			RmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
	    		}
	    	}
	    	//学校名称和团餐公司名称
	    	for(String curKey : schNameToIdMap.keySet()) {
	    		String schId = schNameToIdMap.get(curKey);
	    		if(schIdToSupplierIdMap.containsKey(schId)) {
	    			String supplierId = schIdToSupplierIdMap.get(schId);
	    			if(RmcIdToNameMap.containsKey(supplierId)) {
	    				String supplierName = RmcIdToNameMap.get(supplierId);
	    				schNameToSupNameMap.put(curKey, supplierName);
	    			}
	    		}
	    	}
	    }
		
		return schNameToSupNameMap;
	}
	
	// 项目点排菜详情列表函数（方案2）
	PpDishDetsOutsideDTO ppDishDets_Method1(String[] dates, 
			Db1Service db1Service, SaasService saasService,
			List<String> schoolList) {
		PpDishDetsOutsideDTO pddDto = new PpDishDetsOutsideDTO();
		// 排菜学校
		Map<String, String> schIdToPlatoonMap = new HashMap<>();
		int k, dateCount = dates.length;
		String key = null, keyVal = null;
		//操作状态
		@SuppressWarnings("unchecked")
		Map<String,String> [] plaStatusMap = new Map[dateCount];
		
    	
		//所有学校id（未排菜时获取所有学校id）
		for(k = 0; k < dateCount; k++) {
			plaStatusMap[k] = new HashMap<String, String>();
		}
		// 时间段内各区排菜学校数量
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_platoon-feed";
			schIdToPlatoonMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(schIdToPlatoonMap == null) {    //Redis没有该数据则从hdfs系统中获取
				schIdToPlatoonMap = AppModConfig.getHdfsDataKey(dates[k], key);
			}
			if (schIdToPlatoonMap != null) {
				for (String curKey : schIdToPlatoonMap.keySet()) {
					// 排菜学校ID列表
					String[] curKeys = curKey.split("_");
					if(curKeys.length > 1) {
						keyVal = schIdToPlatoonMap.get(curKey);
						String[] keyVals = keyVal.split("_");
						//标记已排菜学校列表
						if(schoolList!=null && schoolList.size()>0) {
							if(schoolList.contains(curKeys[1])) {
								if(keyVals.length >= 8 ) {
									plaStatusMap[k].put(curKeys[1], keyVals[7]);
								}
							}
						}else {
							if(keyVals.length >= 8 ) {
								plaStatusMap[k].put(curKeys[1], keyVals[7]);
							}
						}
					}
					else
						logger.info("排菜学校ID："+ curKey + "，格式错误！");
				}
			}
		}
		List<PpDishDetsOutside> ppDishDets = new ArrayList<>();
		for(k = 0; k < dateCount; k++) {
			for (String curKey : plaStatusMap[k].keySet()) {
				
				PpDishDetsOutside pdd = new PpDishDetsOutside();
				//排菜日期
				pdd.setDishDate(dates[k].replaceAll("-", "/"));
				//操作状态
				if(plaStatusMap[k] !=null && plaStatusMap[k].containsKey(curKey)) {
					pdd.setPlaStatus(plaStatusMap[k].get(curKey));
				}else {
					pdd.setPlaStatus("");
				}
				//学校的UUID
				pdd.setSchoolId(curKey);
				ppDishDets.add(pdd);
			}
		}
		//排序
    	SortList<PpDishDetsOutside> sortList = new SortList<PpDishDetsOutside>();  
    	sortList.Sort3Level(ppDishDets, methods, sorts, dataTypes);
		//时戳
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//设置数据
		pddDto.setPpDishDets(ppDishDets);
		//消息ID
		pddDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return pddDto;
	}	
	
	// 项目点排菜详情列表函数（方案2）
	PpDishDetsOutsideDTO ppDishDetsFromHive(String[] dates,
			Db1Service db1Service, SaasService saasService, 
			List<String> schoolList,
			DbHiveDishService dbHiveDishService) {
		PpDishDetsOutsideDTO pddDto = new PpDishDetsOutsideDTO();
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
		
		dishList = dbHiveDishService.getDishDetsList(listYearMonth, startDate, endDateAddOne, null, 
				-1, -1, -1, null, -1, -1, -1, null,
				null, null, -1, -1, -1, -1, null, 
				null, null, null, null, null, 
				null,null,null,null,null,null,null,null,null,schoolList,1, null, null);
		
		List<PpDishDetsOutside> ppDishDets = new ArrayList<>();
		for(PpDishCommonDets commonDets : dishList) {
			PpDishDetsOutside pdd = new PpDishDetsOutside();
			try {
				BeanUtils.copyProperties(pdd, commonDets);
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
	public PpDishDetsOutsideDTO appModFunc(String token, String startDate, String endDate,
			List<String> schoolList,
			Db1Service db1Service, 
			Db2Service db2Service, SaasService saasService,
			DbHiveDishService dbHiveDishService) {
		PpDishDetsOutsideDTO pddDto = null;
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
			// 参数查找标识
			DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
			DateTime currentTime = new DateTime();
			int days = Days.daysBetween(startDt, currentTime).getDays();
			if(days >= 2) {
				pddDto = ppDishDetsFromHive(dates, db1Service, saasService,schoolList,
						dbHiveDishService);		
			}else {
				pddDto = ppDishDets_Method1( dates, db1Service, saasService,schoolList);		
			}
		} else { // 模拟数据
			//模拟数据函数
		}

		return pddDto;
	}
}