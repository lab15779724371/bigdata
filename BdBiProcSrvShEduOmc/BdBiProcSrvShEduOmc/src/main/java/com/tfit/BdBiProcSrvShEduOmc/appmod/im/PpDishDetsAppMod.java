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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.bd.BdSchList;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.FileWRCommSys;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//项目点排菜详情列表应用模型
public class PpDishDetsAppMod {
	private static final Logger logger = LogManager.getLogger(PpDishDetsAppMod.class.getName());
	
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
	int methodIndex = 2;
	
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
	private PpDishDetsDTO SimuDataFunc() {
		PpDishDetsDTO pddDto = new PpDishDetsDTO();
		// 设置返回数据
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		List<PpDishDets> ppDishDets = new ArrayList<>();
		//读取真实模拟数据
		if(isReadSimuData)
			GetRealSimuData();
		//赋值
		for (int i = 0; i < Math.max(dishDate_Array.length, actPageSize); i++) {
			PpDishDets pdd = new PpDishDets();
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
	
	// 项目点排菜详情列表函数
	PpDishDetsDTO ppDishDets(String distIdorSCName, String[] dates, int dishFlag, List<TEduDistrictDo> tddList, 
			Db1Service db1Service, SaasService saasService, String ppName, String rmcName, int schType, 
			int mealFlag, int optMode, int sendFlag) {
		PpDishDetsDTO pddDto = new PpDishDetsDTO();
		// 排菜学校
		Map<String, String> schIdToPlatoonMap = new HashMap<>(), schNameToSupNameMap = getSchoolNameToRmcNameMap(db1Service, saasService);
		int i, j, k, dateCount = dates.length, curDishFlag = 0;
		String key = null;
		@SuppressWarnings("unchecked")
		Map<String, Integer>[] schIdMaps1 = new Map[dateCount], schIdMaps2 = new Map[dateCount];       //映射值为0表示已排菜，大于0表示未排菜
		//所有学校id（未排菜时获取所有学校id）
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 1);
		for(k = 0; k < dateCount; k++) {
			if(tesDoList != null) {
				schIdMaps1[k] = new HashMap<String, Integer>();
				schIdMaps2[k] = new HashMap<String, Integer>();
				for(i = 0; i < tesDoList.size(); i++) {
					schIdMaps1[k].put(tesDoList.get(i).getId(), i+1);
				}
			}
		}
		// 时间段内各区排菜学校数量
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_platoon";
			schIdToPlatoonMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (schIdToPlatoonMap != null) {
				for (String curKey : schIdToPlatoonMap.keySet()) {
					// 排菜学校ID列表
					String[] curKeys = curKey.split("_");
					if(curKeys.length > 1) {
						if(distIdorSCName != null) {
							if(curKeys[0].compareTo(distIdorSCName) != 0)
								continue ;
						}
						//标记已排菜学校列表
						if(schIdMaps1[k].containsKey(curKeys[1])) {
							int platoonFlag = schIdMaps1[k].get(curKeys[1]);
							schIdMaps2[k].put(curKeys[1], platoonFlag);
						}
					}
					else
						logger.info("排菜学校ID："+ curKey + "，格式错误！");
				}
			}
		}
		List<PpDishDets> ppDishDets = new ArrayList<>();
		for(k = 0; k < dateCount; k++) {
			logger.info("学校总数：" + schIdMaps1[k].size() + "，排菜学校数：" + schIdMaps2[k].size());
			for (String curKey : schIdMaps1[k].keySet()) {
				int curVal = schIdMaps1[k].get(curKey);
				TEduSchoolDo tesDo = null;
				j = 0;
				if(!schIdMaps2[k].containsKey(curKey) && dishFlag == 0) {	    //未排菜			
					j = curVal;
					curDishFlag = 0;
					tesDo = tesDoList.get(j-1);
				}
				else if(schIdMaps2[k].containsKey(curKey) && dishFlag == 1) {  //已排菜
					j = curVal;
					curDishFlag = 1;
					tesDo = tesDoList.get(j-1);
				}
				else if(dishFlag == -1) {                                      //未排菜和已排菜
					j = curVal;
					curDishFlag = 0;
					if(schIdMaps2[k].containsKey(curKey))
						curDishFlag = 1;
					tesDo = tesDoList.get(j-1);
				}
				if(j > 0 && tesDo != null) {					
					PpDishDets pdd = new PpDishDets();
					//排菜日期
					pdd.setDishDate(dates[k].replaceAll("-", "/"));
					pdd.setSchoolId(tesDo.getId());
					//项目点名称（学校名称）
					pdd.setPpName(tesDo.getSchoolName());
					//详细地址
					pdd.setDetailAddr(tesDo.getAddress()==null?"":tesDo.getAddress());
					//项目联系人
					pdd.setProjContact(tesDo.getDepartmentHead()==null?"":tesDo.getDepartmentHead());
					//手机
					pdd.setPcMobilePhone(tesDo.getDepartmentMobilephone()==null?"":tesDo.getDepartmentMobilephone());
					//区名称
					pdd.setDistName(tesDo.getArea());
					//学校类型
					pdd.setSchType(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2()));
					//学校性质
					pdd.setSchProp(AppModConfig.getSchProp(tesDo.getSchoolNature()));
					//是否供餐
					pdd.setMealFlag(1);
					//经营模式
					pdd.setOptMode(AppModConfig.getOptModeName(tesDo.getCanteenMode(), tesDo.getLedgerType(), tesDo.getLicenseMainType(), tesDo.getLicenseMainChild()));
					//团餐公司名称
					if(schNameToSupNameMap.containsKey(tesDo.getSchoolName()))
						pdd.setRmcName(schNameToSupNameMap.get(tesDo.getSchoolName()));
					else {
						if(tesDo.getId().equalsIgnoreCase("c1a8b452-eb2d-46dd-841c-1deb8bf4cac6"))
							logger.info("学校id：c1a8b452-eb2d-46dd-841c-1deb8bf4cac6" + "，没有关联团餐公司！");
						pdd.setRmcName("-");
					}
					//设置未排菜标识
					pdd.setDishFlag(curDishFlag);
					//条件判断
					boolean isAdd = true;
					int[] flIdxs = new int[6];
					//判断项目点名称（判断索引0）
					if(ppName != null) {
						if(pdd.getPpName().indexOf(ppName) == -1)
							flIdxs[0] = -1;
					}
					//判断团餐公司（判断索引1）
					if(rmcName != null && RmcIdToNameMap != null) {
						if(!(pdd.getRmcName().equalsIgnoreCase(RmcIdToNameMap.get(rmcName))))
							flIdxs[1] = -1;
					}
					//判断学校类型（判断索引2）
					if(schType != -1) {
						int curSchType = AppModConfig.schTypeNameToIdMap.get(pdd.getSchType());
						if(curSchType != schType)
							flIdxs[2] = -1;
					}
					//判断是否供餐（判断索引3）
					if(mealFlag != -1) {
						
					}
					//判断经营模式（判断索引4）
					if(optMode != -1) {
						if(pdd.getOptMode() != null) {
							if(AppModConfig.optModeNameToIdMap.containsKey(pdd.getOptMode())) {
								int curOptMode = AppModConfig.optModeNameToIdMap.get(pdd.getOptMode());
								if(curOptMode != optMode)
									flIdxs[4] = -1;
							}
							else
								flIdxs[4] = -1;
						}
						else
							flIdxs[4] = -1;
					}
					//判断发送状态（判断索引5）
					if(sendFlag != -1) {
						
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
						ppDishDets.add(pdd);
				}
			}
		}
		//时戳
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<PpDishDets> pageBean = new PageBean<PpDishDets>(ppDishDets, curPageNum, pageSize);
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
	PpDishDetsDTO ppDishDets_Method1(String departmentId,String distIdorSCName, String[] dates, int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			List<TEduDistrictDo> tddList, Db1Service db1Service, SaasService saasService, String ppName, 
			String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			String optModes,String subDistNames,String departmentIds,String plastatus,String reason,
			List<String> schoolList) {
		PpDishDetsDTO pddDto = new PpDishDetsDTO();
		// 排菜学校
		Map<String, String> schIdToPlatoonMap = new HashMap<>(), schNameToSupNameMap = getSchoolNameToRmcNameMap(db1Service, saasService);
		int i, j, k, dateCount = dates.length, curDishFlag = 0, curMealFlag = 0;
		String key = null, keyVal = null;
		@SuppressWarnings("unchecked")
		Map<String, Integer>[] schIdMaps1 = new Map[dateCount];
		@SuppressWarnings("unchecked")
		Map<String, Integer>[] schIdMaps2 = new Map[dateCount];
		//schIdMaps2为供餐模式下映射值大于0表示已排菜，小于0表示未排菜；schIdMaps3为不供餐模式下映射值大于0表示已排菜，小于0表示未排菜
		@SuppressWarnings("unchecked")
		Map<String, Integer>[] schIdMaps3 = new Map[dateCount];       
		@SuppressWarnings("unchecked")
		Map<String,String> [] schDishCreateTimeMap = new Map[dateCount];
		
		//不供餐原因
		@SuppressWarnings("unchecked")
		Map<String,String> [] reasonMap = new Map[dateCount];
		//操作状态
		@SuppressWarnings("unchecked")
		Map<String,String> [] plaStatusMap = new Map[dateCount];
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
    	List<Object> subLevelsList=CommonUtil.changeStringToList(subLevels);
    	List<Object> compDepsList=CommonUtil.changeStringToList(compDeps);
    	List<Object> schPropsList=CommonUtil.changeStringToList(schProps);
    	List<Object> schTypesList=CommonUtil.changeStringToList(schTypes);
    	List<Object> optModesList=CommonUtil.changeStringToList(optModes);
    	List<Object> subDistNamesList=CommonUtil.changeStringToList(subDistNames);
    	//管理部门
    	List<Object> departmentIdList=CommonUtil.changeStringToList(departmentIds);
    	
		//所有学校id（未排菜时获取所有学校id）
    	List<TEduSchoolDo> tesDoList = new ArrayList<TEduSchoolDo>();
		if(distIdorSCName!=null && !"".equals(distIdorSCName)) {
			tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 5);
		}else {
			tesDoList = db1Service.getTEduSchoolDoListByDs1(distNamesList,1,1);
		}
		for(k = 0; k < dateCount; k++) {
			if(tesDoList != null) {
				schIdMaps1[k] = new HashMap<String, Integer>();
				schIdMaps2[k] = new HashMap<String, Integer>();
				schIdMaps3[k] = new HashMap<String, Integer>();
				schDishCreateTimeMap[k] = new HashMap<String, String>();
				
				reasonMap[k] = new HashMap<String, String>();
				plaStatusMap[k] = new HashMap<String, String>();
				
				for(i = 0; i < tesDoList.size(); i++) {
					schIdMaps1[k].put(tesDoList.get(i).getId(), i+1);
				}
			}
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
						if(distIdorSCName != null) {
							if(curKeys[0].compareTo(distIdorSCName) != 0)
								continue ;
						}else if(distNamesList!=null && distNamesList.size() >0) {
							if(StringUtils.isEmpty(curKeys[0]) || !StringUtils.isNumeric(curKeys[0])) {
								continue;
							}
							if(!distNamesList.contains(curKeys[0])) {
								continue ;
							}
						}
						//标记已排菜学校列表
						if(schIdMaps1[k].containsKey(curKeys[1])) {
							keyVal = schIdToPlatoonMap.get(curKey);
							String[] keyVals = keyVal.split("_");
							if(keyVals.length >= 2) {
								int platoonFlag = schIdMaps1[k].get(curKeys[1]);
								if(keyVals[0].equalsIgnoreCase("供餐") && keyVals[1].equalsIgnoreCase("已排菜")) {
									schIdMaps2[k].put(curKeys[1], platoonFlag);
									if(keyVals.length>=4  && !keyVals[3].equalsIgnoreCase("null")) {
										schDishCreateTimeMap[k].put(curKeys[1], keyVals[3]);
									}
								}else if(keyVals[0].equalsIgnoreCase("供餐") && keyVals[1].equalsIgnoreCase("未排菜")) {
									schIdMaps2[k].put(curKeys[1], -platoonFlag);
								}else if(keyVals[0].equalsIgnoreCase("不供餐") && keyVals[1].equalsIgnoreCase("已排菜")) {
									schIdMaps3[k].put(curKeys[1], platoonFlag);
									if(keyVals.length>=4 && !keyVals[3].equalsIgnoreCase("null")) {
										schDishCreateTimeMap[k].put(curKeys[1], keyVals[3]);
									}
									//供餐，切未排菜，才有不供餐原因
									if(CommonUtil.isNotEmpty(keyVals[5])) {
										reasonMap[k].put(curKeys[1], keyVals[5]);
									}
								}else if(keyVals[0].equalsIgnoreCase("不供餐") && keyVals[1].equalsIgnoreCase("未排菜")) {
									schIdMaps3[k].put(curKeys[1], -platoonFlag);
									//供餐，切未排菜，才有不供餐原因
									if(CommonUtil.isNotEmpty(keyVals[5])) {
										reasonMap[k].put(curKeys[1], keyVals[5]);
									}
								}
								
								//操作状态
								if(keyVals.length >= 8 ) {
									plaStatusMap[k].put(curKeys[1], keyVals[7]);
								}
							}
						}
					}
					else
						logger.info("排菜学校ID："+ curKey + "，格式错误！");
				}
			}
		}
		List<PpDishDets> ppDishDets = new ArrayList<>();
		for(k = 0; k < dateCount; k++) {
			logger.info("日期：" + dates[k] + "，学校总数：" + schIdMaps1[k].size() + "，供餐学校数：" + schIdMaps2[k].size() + "，不供餐学校数：" + schIdMaps3[k].size());
			for (String curKey : schIdMaps1[k].keySet()) {
				TEduSchoolDo tesDo = null;
				int curVal = 0;
				j = 0;
				if(schIdMaps2[k].containsKey(curKey)) {      //供餐
					curVal = schIdMaps2[k].get(curKey);
					j = Math.abs(curVal);
					curMealFlag = 1;
					if(curVal < 0 && dishFlag == 0) {	     //供餐，未排菜
						curDishFlag = 0;
						tesDo = tesDoList.get(j-1);
					}
					else if(curVal > 0 && dishFlag == 1) {   //供餐，已排菜
						curDishFlag = 1;
						tesDo = tesDoList.get(j-1);
					}
					else if(dishFlag == -1) {                //供餐，未排菜和已排菜
						if(curVal < 0)
							curDishFlag = 0;
						else if(curVal > 0)
							curDishFlag = 1;
						tesDo = tesDoList.get(j-1);
					}
				}
				else if(schIdMaps3[k].containsKey(curKey)) {   //不供餐
					curVal = schIdMaps3[k].get(curKey);
					j = Math.abs(curVal);
					curMealFlag = 0;
					if(curVal < 0 && dishFlag == 0) {	       //不供餐，未排菜
						curDishFlag = 0;
						tesDo = tesDoList.get(j-1);
					}
					else if(curVal > 0 && dishFlag == 1) {     //不供餐，已排菜
						curDishFlag = 1;
						tesDo = tesDoList.get(j-1);
					}
					else if(dishFlag == -1) {                  //不供餐，未排菜和已排菜
						if(curVal < 0)
							curDishFlag = 0;
						else if(curVal > 0)
							curDishFlag = 1;
						tesDo = tesDoList.get(j-1);
					}
				}
				if(j > 0 && tesDo != null) {					
					PpDishDets pdd = new PpDishDets();
					//排菜日期
					pdd.setDishDate(dates[k].replaceAll("-", "/"));
					//学校的UUID
					pdd.setSchoolId(tesDo.getId());
					//项目点名称（学校名称）
					pdd.setPpName(tesDo.getSchoolName());	
					//详细地址
					pdd.setDetailAddr(tesDo.getAddress()==null?"":tesDo.getAddress());
					//项目联系人
					pdd.setProjContact(tesDo.getDepartmentHead()==null?"":tesDo.getDepartmentHead());
					//手机
					pdd.setPcMobilePhone(tesDo.getDepartmentMobilephone()==null?"":tesDo.getDepartmentMobilephone());
					
					//管理部门
					pdd.setDepartmentId(tesDo.getDepartmentId()==null?"":tesDo.getDepartmentId());
					
    				//总校/分校
					int curSchGenBraFlag = 0;
					pdd.setSchGenBraFlag("-");
    				if(tesDo.getIsBranchSchool() != null) {
    					if(tesDo.getIsBranchSchool() == 1) { //分校
    						curSchGenBraFlag = 2;
    						pdd.setSchGenBraFlag("分校");
    					}
    					else if(tesDo.getIsBranchSchool() == 0) {   //总校
    						curSchGenBraFlag = 1;
    						pdd.setSchGenBraFlag("总校");
    					}
    				}
    				//分校数量
    				int curBraCampusNum = 0;
    				pdd.setBraCampusNum(curBraCampusNum);
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
    						pdd.setBraCampusNum(curBraCampusNum);
    				}
    				//关联总校
    				pdd.setRelGenSchName("-");
    				if(schIdMaps1[k].containsKey(tesDo.getParentId())) {
    					i = schIdMaps1[k].get(tesDo.getParentId())-1;
    					pdd.setRelGenSchName(tesDoList.get(i).getSchoolName());
    				}    				
    				//所属
    				int curSubLevel = 0;
    				if(tesDo.getDepartmentMasterId() != null) {
    					curSubLevel = Integer.parseInt(tesDo.getDepartmentMasterId());
    				}
    				pdd.setSubLevel(AppModConfig.subLevelIdToNameMap.get(curSubLevel));
    				//主管部门
    				int curCompDep = 0;
    				pdd.setCompDep("其他");
    				if(curSubLevel == 0) {      //其他
    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
    					}
    					pdd.setCompDep(AppModConfig.compDepIdToNameMap0.get(String.valueOf(curCompDep)));
    				}
    				else if(curSubLevel ==1) {      //部级   
    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
    					}
    					pdd.setCompDep(AppModConfig.compDepIdToNameMap1.get(String.valueOf(curCompDep)));
    				}
    				else if(curSubLevel == 2) {      //市级
    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
    						curCompDep = Integer.parseInt(tesDo.getDepartmentSlaveId());
    					}
    					pdd.setCompDep(AppModConfig.compDepIdToNameMap2.get(String.valueOf(curCompDep)));
    				}
    				else if(curSubLevel == 3) {      //区级
    					if(CommonUtil.isNotEmpty(tesDo.getDepartmentSlaveId())) {
    						String orgName = AppModConfig.compDepIdToNameMap3bd.get(tesDo.getDepartmentSlaveId());
    						if(orgName != null) {
    							curCompDep = Integer.parseInt(AppModConfig.compDepNameToIdMap3.get(orgName));
    						}
    					}
    					pdd.setCompDep(AppModConfig.compDepIdToNameMap3.get(String.valueOf(curCompDep)));
    				}
    				//所属区域名称
    				pdd.setSubDistName("-");
    				if(tesDo.getSchoolAreaId() != null)
    					pdd.setSubDistName(AppModConfig.distIdToNameMap.get(tesDo.getSchoolAreaId()));
    				//证件主体，0:学校，1:外包
    				pdd.setFblMb("-");
    				if(tesDo.getLicenseMainType() != null) {
    					int curFblMb = Integer.parseInt(tesDo.getLicenseMainType());
    					if(AppModConfig.fblMbIdToNameMap.containsKey(curFblMb))
    						pdd.setFblMb(AppModConfig.fblMbIdToNameMap.get(curFblMb));
    				}
					//区名称
					pdd.setDistName(tesDo.getArea());
					//学校类型
					pdd.setSchType(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2()));
					//学校性质
					pdd.setSchProp(AppModConfig.getSchProp(tesDo.getSchoolNature()));
					//是否供餐
					pdd.setMealFlag(curMealFlag);
					//经营模式
					pdd.setOptMode(AppModConfig.getOptModeName(tesDo.getCanteenMode(), tesDo.getLedgerType(), tesDo.getLicenseMainType(), tesDo.getLicenseMainChild()));
					//团餐公司名称
					if(schNameToSupNameMap.containsKey(tesDo.getSchoolName()))
						pdd.setRmcName(schNameToSupNameMap.get(tesDo.getSchoolName()));
					else {
						pdd.setRmcName("-");
					}
					//设置未排菜标识
					pdd.setDishFlag(curDishFlag);
					
					//操作时间
					if(schDishCreateTimeMap[k]!=null && schDishCreateTimeMap[k].containsKey(curKey)) {
						pdd.setCreatetime(schDishCreateTimeMap[k].get(curKey).replaceAll("-", "/"));
					}else {
						pdd.setCreatetime("");
					}
					
					
					//操作状态
					if(plaStatusMap[k] !=null && plaStatusMap[k].containsKey(curKey)) {
						pdd.setPlaStatus(plaStatusMap[k].get(curKey));
					}else {
						pdd.setPlaStatus("");
					}
					
					//不供餐原因
					if(reasonMap[k]!=null && reasonMap[k].containsKey(curKey)) {
						pdd.setReason(reasonMap[k].get(curKey));
					}else {
						pdd.setReason("");
					}
					
					//条件判断
					boolean isAdd = true;
					int[] flIdxs = new int[11];
					//判断项目点名称（判断索引0）
					if(ppName != null) {
						if(pdd.getPpName().indexOf(ppName) == -1)
							flIdxs[0] = -1;
					}
					
					if(schoolList!=null && schoolList.size() >0) {
						if(pdd.getPpName() != null) {
							if(!schoolList.contains(tesDo.getId()))
								flIdxs[0] = -1;
						}
						else {
							flIdxs[0] = -1;
						}
					}
					
					//判断团餐公司（判断索引1）
					if(rmcName != null && RmcIdToNameMap != null) {
						if(!(pdd.getRmcName().equalsIgnoreCase(RmcIdToNameMap.get(rmcName))))
							flIdxs[1] = -1;
					}
					//判断学校类型（判断索引2）
					if(schType != -1) {
						int curSchType = AppModConfig.schTypeNameToIdMap.get(pdd.getSchType());
						if(curSchType != schType)
							flIdxs[2] = -1;
					}else if(schTypesList!=null && schTypesList.size() >0) {
						if(AppModConfig.schTypeNameToIdMap.containsKey(pdd.getSchType())) {
							int curSchType = AppModConfig.schTypeNameToIdMap.get(pdd.getSchType());
							if(!schTypesList.contains(String.valueOf(curSchType))) {
								flIdxs[2] = -1;
							}
						}else{
							flIdxs[2] = -1;
						}
					}	
					//判断是否供餐（判断索引3）
					if(mealFlag != -1) {
						if(curMealFlag != mealFlag)
							flIdxs[3] = -1;
					}
					//判断经营模式（判断索引4）
					if(optMode != -1) {
						if(pdd.getOptMode() != null) {
							if(AppModConfig.optModeNameToIdMap.containsKey(pdd.getOptMode())) {
								int curOptMode = AppModConfig.optModeNameToIdMap.get(pdd.getOptMode());
								if(curOptMode != optMode)
									flIdxs[4] = -1;
							}
							else
								flIdxs[4] = -1;
						}
						else
							flIdxs[4] = -1;
					}else if(optModesList!=null && optModesList.size() >0) {
						if(pdd.getOptMode() != null) {
							if(AppModConfig.optModeNameToIdMap.containsKey(pdd.getOptMode())) {
								int curOptMode = AppModConfig.optModeNameToIdMap.get(pdd.getOptMode());
								if(!optModesList.contains(String.valueOf(curOptMode))) {
									flIdxs[4] = -1;
								}
							}
							else {
								flIdxs[4] = -1;
							}
						}
						else {
							flIdxs[4] = -1;
						}
					}	
					//判断所属（判断索引5）
					if(subLevel != -1) {
						curSubLevel = AppModConfig.subLevelNameToIdMap.get(pdd.getSubLevel());
						if(curSubLevel != subLevel)
							flIdxs[5] = -1;
					}else if(subLevelsList!=null && subLevelsList.size() >0) {
						curSubLevel = AppModConfig.subLevelNameToIdMap.get(pdd.getSubLevel());
						if(!subLevelsList.contains(String.valueOf(curSubLevel))) {
							flIdxs[8] = -1;
						}
					}
					//判断主管部门（判断索引6）
					if(compDep != -1) {
						curCompDep = 0;
						if(subLevel == 0) {    //其他
							String strCompDepId = AppModConfig.compDepNameToIdMap0.get(pdd.getCompDep());
							if(strCompDepId != null) {
								curCompDep = Integer.parseInt(strCompDepId);
								if(curCompDep != compDep)
									flIdxs[6] = -1;
							}
							else
								flIdxs[6] = -1;
						}
						else if(subLevel == 1) {    //部署
							String strCompDepId = AppModConfig.compDepNameToIdMap1.get(pdd.getCompDep());
							if(strCompDepId != null) {
								curCompDep = Integer.parseInt(strCompDepId);
								if(curCompDep != compDep)
									flIdxs[6] = -1;
							}
							else
								flIdxs[6] = -1;
						}
						else if(subLevel == 2) {    //市属
							String strCompDepId = AppModConfig.compDepNameToIdMap2.get(pdd.getCompDep());
							if(strCompDepId != null) {
								curCompDep = Integer.parseInt(strCompDepId);
								if(curCompDep != compDep)
									flIdxs[6] = -1;
							}
							else
								flIdxs[6] = -1;
						}
						else if(subLevel == 3) {    //区属
							String strCompDepId = AppModConfig.compDepNameToIdMap3.get(pdd.getCompDep());
							if(strCompDepId != null) {
								curCompDep = Integer.parseInt(strCompDepId);
								if(curCompDep != compDep)
									flIdxs[6] = -1;
							}
							else
								flIdxs[6] = -1;
						}
					}else if(compDepsList!=null && compDepsList.size() >0) {
						curSubLevel = AppModConfig.subLevelNameToIdMap.get(pdd.getSubLevel());
						curCompDep = 0;
						if(curSubLevel == 0) {    //其他
							String strCompDepId = AppModConfig.compDepNameToIdMap0.get(pdd.getCompDep());
							if(strCompDepId != null) {
								curCompDep = Integer.parseInt(strCompDepId);
								if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
									flIdxs[6] = -1;
								}
							}
							else
								flIdxs[6] = -1;
						}
						else if(curSubLevel == 1) {    //部署
							String strCompDepId = AppModConfig.compDepNameToIdMap1.get(pdd.getCompDep());
							if(strCompDepId != null) {
								curCompDep = Integer.parseInt(strCompDepId);
								if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
									flIdxs[6] = -1;
								}
							}
							else
								flIdxs[6] = -1;
						}
						else if(curSubLevel == 2) {    //市属
							String strCompDepId = AppModConfig.compDepNameToIdMap2.get(pdd.getCompDep());
							if(strCompDepId != null) {
								curCompDep = Integer.parseInt(strCompDepId);
								if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
									flIdxs[6] = -1;
								}
							}
							else
								flIdxs[6] = -1;
						}
						else if(curSubLevel == 3) {    //区属
							String strCompDepId = AppModConfig.compDepNameToIdMap3.get(pdd.getCompDep());
							if(strCompDepId != null) {
								curCompDep = Integer.parseInt(strCompDepId);
								if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
									flIdxs[6] = -1;
								}
							}
							else
								if(!compDepsList.contains(curSubLevel+"_"+curCompDep)) {
									flIdxs[6] = -1;
								}
						}
						
					}
					//判断总分校标识（判断索引7）
					if(schGenBraFlag != -1) {
						curSchGenBraFlag = AppModConfig.genBraSchNameToIdMap.get(pdd.getSchGenBraFlag());
						if(curSchGenBraFlag != schGenBraFlag)
							flIdxs[7] = -1;
					}
					//判断所属区域名称（判断索引8）
					if(subDistName != null) {
						if(pdd.getSubDistName() != null) {
							if(!pdd.getSubDistName().equals(subDistName))
								flIdxs[8] = -1;
						}
						else
							flIdxs[8] = -1;
					}else if(subDistNamesList!=null && subDistNamesList.size() >0) {
						if(pdd.getSubDistName() != null) {
							if(!subDistNamesList.contains(pdd.getSubDistName()))
								flIdxs[8] = -1;
						}
						else {
							flIdxs[8] = -1;
						}
					}	
					//判断证件主体（判断索引9）
					if(fblMb != -1) {			
						if(AppModConfig.fblMbNameToIdMap.get(pdd.getFblMb()) == null) {
							flIdxs[9] = -1;
						}else {
						int curFblMb = AppModConfig.fblMbNameToIdMap.get(pdd.getFblMb());
							if(curFblMb != fblMb)
								flIdxs[9] = -1;
						}
					}
					//判断学校性质（判断索引10）
					if(schProp != -1) {
						int curSchProp = AppModConfig.schPropNameToIdMap.get(pdd.getSchProp());
						if(curSchProp != schProp)
							flIdxs[10] = -1;
					}else if(schPropsList!=null && schPropsList.size() >0) {
						int curSchProp = AppModConfig.schPropNameToIdMap.get(pdd.getSchProp());
						if(!schPropsList.contains(String.valueOf(curSchProp))) {
							flIdxs[10] = -1;
						}
					}
					
					//主管部门编号（判断索引8）
					if(departmentId != null) {
						if(pdd.getDepartmentId() != null) {
							if(!pdd.getDepartmentId().equals(departmentId))
								flIdxs[8] = -1;
						}
						else
							flIdxs[8] = -1;
						
					}
					if(departmentIdList!=null && departmentIdList.size() >0) {
						if(pdd.getDepartmentId() != null) {
							if(!departmentIdList.contains(pdd.getDepartmentId()))
								flIdxs[8] = -1;
						}
						else {
							flIdxs[8] = -1;
						}
					}
					
					//不供餐原因类型
					if(CommonUtil.isNotEmpty(reason)) {
						//如果是供餐，不供餐原因作废（为了解决阳光午餐，供餐学校，存在不供餐原因的问题）
						if(pdd.getMealFlag() == 1) {
							continue;
						}
						
						if(CommonUtil.isEmpty(pdd.getReason())) {
							continue;
						}
						if(AppModConfig.noDishReasonTypeTwo.contains(String.valueOf(reason))) {
							//非其他
							if(!reason.equals(pdd.getReason())) {
								continue;
							}
						}else {
							//其他
							if(AppModConfig.noDishReasonTypeTwo.contains(String.valueOf(pdd.getReason()))) {
								continue;
							}
						}
					}
					
					
					//判断操作状态（判断索引7）
					if(CommonUtil.isNotEmpty(plastatus)) {
						if(CommonUtil.isEmpty(pdd.getPlaStatus()) || !plastatus.equals(pdd.getPlaStatus() ))
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
						ppDishDets.add(pdd);
				}
			}
		}
		//排序
    	SortList<PpDishDets> sortList = new SortList<PpDishDets>();  
    	sortList.Sort3Level(ppDishDets, methods, sorts, dataTypes);
		//时戳
		pddDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<PpDishDets> pageBean = null;
		if(curPageNum == -1 || pageSize == -1) {
			pageBean = new PageBean<PpDishDets>(ppDishDets, 1, ppDishDets.size());
		}else {
			pageBean = new PageBean<PpDishDets>(ppDishDets, curPageNum, pageSize);
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
	PpDishDetsDTO ppDishDetsFromHive(String departmentId,String distIdorSCName, String[] dates, int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			List<TEduDistrictDo> tddList, Db1Service db1Service, SaasService saasService, String ppName, 
			String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			String optModes,String subDistNames,String departmentIds,String plastatus,String reason,
			List<String> schoolList,
			DbHiveDishService dbHiveDishService) {
		PpDishDetsDTO pddDto = new PpDishDetsDTO();
		
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
		if(subDistNamesList !=null && subDistNamesList.size()>0) {
			for(Object subDistNameObj  : subDistNamesList) {
				newSubDistNamesList.add(AppModConfig.distNameToIdMap.get(subDistNameObj.toString()));
			}
		}
		
		dishList = dbHiveDishService.getDishDetsList(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
				subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, dishFlag, ppName,
				rmcName, null, schType, mealFlag, optMode, sendFlag, distNamesList, 
				subLevelsList, compDepsList, schPropsList, schTypesList, optModesList, 
				newSubDistNamesList,null,null,null,departmentId,departmentIdList,plastatus,reason,null,schoolList,0, (curPageNum-1)*pageSize, curPageNum*pageSize);
		dishList.removeAll(Collections.singleton(null));
		
		Integer pageTotalTemp = dbHiveDishService.getDishDetsCount(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
				subLevel, compDep, schGenBraFlag, subDistName, fblMb, schProp, dishFlag, ppName,
				rmcName, null, schType, mealFlag, optMode, sendFlag, distNamesList, 
				subLevelsList, compDepsList, schPropsList, schTypesList, optModesList, 
				newSubDistNamesList,null,null,null,
				departmentId,departmentIdList,plastatus,reason,null,schoolList);
		
		if(pageTotalTemp!=null) {
			pageTotal = pageTotalTemp;
		}
		
		List<PpDishDets> ppDishDets = new ArrayList<>();
		for(PpDishCommonDets commonDets : dishList) {
			PpDishDets pdd = new PpDishDets();
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
	public PpDishDetsDTO appModFunc(String token, String startDate, String endDate, String ppName, 
			String distName, String prefCity, String province, String subLevel, String compDep, 
			String schGenBraFlag, String subDistName, String fblMb, String schProp, String dishFlag, 
			String rmcName, String schType, String mealFlag, String optMode, String sendFlag, 
			String distNames,String subLevels,String compDeps,String schProps,String schTypes,
			String optModes,String subDistNames,String departmentId,String departmentIds,
			String plastatus,String reason,
			List<String> schoolList,
			String page, String pageSize, Db1Service db1Service, 
			Db2Service db2Service, SaasService saasService,
			DbHiveDishService dbHiveDishService) {
		PpDishDetsDTO pddDto = null;
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
						pddDto = ppDishDets_Method1(departmentId,distIdorSCName, dates, curSubLevel, curCompDep, curSchGenBraFlag, subDistName, 
								curFblMb, curSchProp, curDishFlag, tddList, db1Service, saasService, ppName, rmcName, 
								curSchType, curMealFlag, curOptMode, curSendFlag,
								distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,departmentIds,plastatus,reason,schoolList);		
					}else if(methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						if(days >= 2) {
							pddDto = ppDishDetsFromHive(departmentId,distIdorSCName, dates, curSubLevel, curCompDep, curSchGenBraFlag, subDistName, 
									curFblMb, curSchProp, curDishFlag, tddList, db1Service, saasService, ppName, rmcName, 
									curSchType, curMealFlag, curOptMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,
									departmentIds,plastatus,reason,schoolList,
									dbHiveDishService);		
						}else {
							pddDto = ppDishDets_Method1(departmentId,distIdorSCName, dates, curSubLevel, curCompDep, curSchGenBraFlag, subDistName, 
									curFblMb, curSchProp, curDishFlag, tddList, db1Service, saasService, ppName, rmcName, 
									curSchType, curMealFlag, curOptMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,departmentIds,plastatus,reason,schoolList);		
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
					// 项目点排菜详情列表函数
					if(methodIndex == 1) {
						pddDto = ppDishDets_Method1(departmentId,distIdorSCName, dates, curSubLevel, curCompDep, curSchGenBraFlag,
								subDistName, curFblMb, curSchProp, curDishFlag, tddList, db1Service, saasService, 
								ppName, rmcName, curSchType, curMealFlag, curOptMode, curSendFlag,
								distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,departmentIds,plastatus,reason,schoolList);
					}else if (methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						if(days >= 2) {
							pddDto = ppDishDetsFromHive(departmentId,distIdorSCName, dates, curSubLevel, curCompDep, curSchGenBraFlag,
									subDistName, curFblMb, curSchProp, curDishFlag, tddList, db1Service, saasService, 
									ppName, rmcName, curSchType, curMealFlag, curOptMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,
									departmentIds,plastatus,reason,schoolList,
									dbHiveDishService);		
						}else {
							pddDto = ppDishDets_Method1(departmentId,distIdorSCName, dates, curSubLevel, curCompDep, curSchGenBraFlag,
									subDistName, curFblMb, curSchProp, curDishFlag, tddList, db1Service, saasService, 
									ppName, rmcName, curSchType, curMealFlag, curOptMode, curSendFlag,
									distNames,subLevels,compDeps,schProps,schTypes,optModes,subDistNames,departmentIds,plastatus,reason,schoolList);	
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
			pddDto = SimuDataFunc();
		}

		return pddDto;
	}
}