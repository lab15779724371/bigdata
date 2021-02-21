package com.tfit.BdBiProcSrvShEduOmc.appmod.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//综合分析菜品供应明细列表应用模型
public class CaDishSupDetsAppMod {
	private static final Logger logger = LogManager.getLogger(CaDishSupDetsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//二级排序条件
	final String[] methods = {"getDistName", "getSchType", "getMenuName"};
	final String[] sorts = {"asc", "asc", "asc"};
	final String[] dataTypes = {"String", "String", "String"};
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 2;
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	//数组数据初始化
	String[] repastDate_Array = {"2018/09/03", "2018/09/03"};
	String[] schName_Array = {"上海市天山中学", "上海市天山中学"};
	String[] distName_Array = {"长宁区", "长宁区"};
	String[] detailAddr_Array = {"上海市长宁区天山北路256号", "上海市长宁区天山北路256号"};
	String[] schType_Array = {"九年一贯制学校", "九年一贯制学校"};
	String[] schProp_Array = {"公办", "公办"};
	String[] optMode_Array = {"自营", "自营"};
	String[] dispType_Array = {"原料", "原料"};
	String[] caterType_Array = {"午餐", "午餐"};
	String[] dishName_Array = {"番茄炒蛋", "土豆肉丝"};
	String[] dishType_Array = {"素菜", "大荤"};
	int[] supNum_Array = {110, 112};
	String[] menuName_Array = {"国内班", "国内班"};
	String[] rmcName_Array = {"上海绿捷", "上海绿捷"};
	
	//模拟数据函数
	private CaDishSupDetsDTO SimuDataFunc() {
		CaDishSupDetsDTO cdsdDto = new CaDishSupDetsDTO();
		//时戳
		cdsdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//综合分析菜品供应明细列表模拟数据
		List<CaDishSupDets> caDishSupDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < repastDate_Array.length; i++) {
			CaDishSupDets cdsd = new CaDishSupDets();			
			cdsd.setRepastDate(repastDate_Array[i]);
			cdsd.setSchName(schName_Array[i]);
			cdsd.setDistName(distName_Array[i]);
			cdsd.setDetailAddr(detailAddr_Array[i]);
			cdsd.setSchType(schType_Array[i]);
			cdsd.setSchProp(schProp_Array[i]);
			cdsd.setOptMode(optMode_Array[i]);
			cdsd.setDispType(dispType_Array[i]);
			cdsd.setCaterType(caterType_Array[i]);
			cdsd.setDishName(dishName_Array[i]);
			cdsd.setDishType(dishType_Array[i]);
			cdsd.setSupNum(supNum_Array[i]);
			cdsd.setMenuName(menuName_Array[i]);
			cdsd.setRmcName(rmcName_Array[i]);			
			caDishSupDets.add(cdsd);
		}		
		//设置数据
		cdsdDto.setCaDishSupDets(caDishSupDets);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = repastDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		cdsdDto.setPageInfo(pageInfo);
		//消息ID
		cdsdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cdsdDto;
	}
	
	// 综合分析菜品供应明细列表函数
	CaDishSupDetsDTO caDishSupDets(String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, Db1Service db1Service, 
	           SaasService saasService, String distName, String prefCity, String province, String schName, String dishName, String rmcName,
	           String caterType, int schType, int schProp, int optMode, String menuName) {
			CaDishSupDetsDTO cdsdDto = new CaDishSupDetsDTO();
		List<CaDishSupDets> caDishSupDets = new ArrayList<>();
		Map<String, String> dishmenuMap = new HashMap<>();
		int i, j, k, dateCount = dates.length;
		String key = null, keyVal = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 2);
		for(i = 0; i < tesDoList.size(); i++) {
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
    	// 时间段内各区学校餐厨垃圾详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_dish-menu";
			dishmenuMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (dishmenuMap != null) {
				for (String curKey : dishmenuMap.keySet()) {
					keyVal = dishmenuMap.get(curKey);
					// 综合分析菜品供应明细列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 18) {
						CaDishSupDets cdsd = new CaDishSupDets();
						//学校信息（项目点）
						TEduSchoolDo tesDo = null;
						if(schIdMap.containsKey(keyVals[3])) {
							j = schIdMap.get(keyVals[3]);
							tesDo = tesDoList.get(j-1);
						}
						if(tesDo == null)
							continue;
						//用餐日期
						cdsd.setRepastDate(dates[k].replaceAll("-", "/"));
						//学校
						cdsd.setSchName(tesDo.getSchoolName());
						//区
						if(!keyVals[17].equalsIgnoreCase("null"))
							cdsd.setDistName(keyVals[17]);
						else
							cdsd.setDistName("-");
						//详细地址
						cdsd.setDetailAddr(tesDo.getAddress());
						//学校学制
						cdsd.setSchType(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2()));
						//学校性质
						cdsd.setSchProp(AppModConfig.getSchProp(tesDo.getSchoolNature()));
						//经营模式（供餐模式）
						cdsd.setOptMode(AppModConfig.getOptModeName(tesDo.getCanteenMode(), tesDo.getLedgerType(), tesDo.getLicenseMainType(), tesDo.getLicenseMainChild()));
						//配送类型
						if(keyVals[13].equalsIgnoreCase("1"))
							cdsd.setDispType("原料");
						else if(keyVals[13].equalsIgnoreCase("2"))
							cdsd.setDispType("成品菜");
						else
							cdsd.setDispType("-");
						//餐别
						cdsd.setCaterType(keyVals[11]);
						//菜品名称
						cdsd.setDishName(keyVals[7]);
						//菜品类别
						cdsd.setDishType(keyVals[15]);
						//供应份数
						if(AppModConfig.isInteger(keyVals[9]))
							cdsd.setSupNum(Integer.parseInt(keyVals[9]));
						else
							cdsd.setSupNum(0);
						//菜单名称
						if(!keyVals[5].equalsIgnoreCase("null"))
							cdsd.setMenuName(keyVals[5]);
						else
							cdsd.setMenuName("-");						
						//团餐公司
						if(RmcIdToNameMap.containsKey(keyVals[1]))
							cdsd.setRmcName(RmcIdToNameMap.get(keyVals[1]));
						else
							cdsd.setRmcName("-");
						//条件判断
						boolean isAdd = true;
						int[] flIdxs = new int[9];
						//判断学校名称（判断索引0）
						if(schName != null) {
							if(!keyVals[3].equalsIgnoreCase(schName))
								flIdxs[0] = -1;
						}
						//判断区域（判断索引1）
						if(distIdorSCName != null) {
							if(!keyVals[17].equalsIgnoreCase(distIdorSCName))
								flIdxs[1] = -1;
						}
						//判断菜品名称（判断索引2）
						if(dishName != null) {
							if(keyVals[7].indexOf(dishName) == -1)
								flIdxs[2] = -1;
						}
						//判断团餐公司（判断索引3）
						if(rmcName != null && RmcIdToNameMap != null) {
							if(!(cdsd.getRmcName().equalsIgnoreCase(RmcIdToNameMap.get(rmcName))))
								flIdxs[3] = -1;
						}
						//判断餐别（判断索引4）
						if(caterType != null) {
							if(!keyVals[11].equalsIgnoreCase(caterType))
								flIdxs[4] = -1;
						}
						//判断学校类型（学制）（判断索引5）
						if(schType != -1) {
							if(AppModConfig.schTypeNameToIdMap.containsKey(cdsd.getSchType())) {
								int curSchType = AppModConfig.schTypeNameToIdMap.get(cdsd.getSchType());
								if(curSchType != schType)
									flIdxs[5] = -1;
							}
							else
								flIdxs[5] = -1;
						}
						//判断学校性质（判断索引6）
						if(schProp != -1) {
							if(AppModConfig.schPropNameToIdMap.containsKey(cdsd.getSchProp())) {
								int curSchProp = AppModConfig.schPropNameToIdMap.get(cdsd.getSchProp());
								if(curSchProp != schProp)
									flIdxs[6] = -1;
							}
							else
								flIdxs[6] = -1;
						}
						//判断经营模型（供餐模式）（判断索引7）
						if(optMode != -1) {
							if(cdsd.getOptMode() != null) {
								if(AppModConfig.optModeNameToIdMap.containsKey(cdsd.getOptMode())) {
									int curOptMode = AppModConfig.optModeNameToIdMap.get(cdsd.getOptMode());
									if(curOptMode != optMode)
										flIdxs[7] = -1;
								}
								else
									flIdxs[7] = -1;
							}
							else
								flIdxs[7] = -1;
						}
						//判断菜单名称（判断索引8）
						if(menuName != null) {
							if(!keyVals[5].equalsIgnoreCase(menuName))
								flIdxs[8] = -1;
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
							caDishSupDets.add(cdsd);
					}
					else
						logger.info("菜品供应明细："+ curKey + "，格式错误！");
				}
			}
		}
		//排序
    	SortList<CaDishSupDets> sortList = new SortList<CaDishSupDets>();  
    	sortList.Sort3Level(caDishSupDets, methods, sorts, dataTypes);
		//时戳
		cdsdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<CaDishSupDets> pageBean = new PageBean<CaDishSupDets>(caDishSupDets, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		cdsdDto.setPageInfo(pageInfo);
		//设置数据
		cdsdDto.setCaDishSupDets(pageBean.getCurPageData());
		//消息ID
		cdsdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cdsdDto;
	}	
	
	// 综合分析菜品供应明细列表函数
	CaDishSupDetsDTO caDishSupDetsTwo(String distIdorSCName, String startDate, String endDate, List<TEduDistrictDo> tddList, 
			Db1Service db1Service, SaasService saasService,DbHiveService dbHiveService,String prefCity, String province, 
			String schName, String dishName, String rmcName, String caterType, int schType, int schProp, 
			int optMode, String menuName) {
		CaDishSupDetsDTO cdsdDto = new CaDishSupDetsDTO();
		
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
    	
		Map<Integer, String> schoolPropertyMap = new HashMap<Integer,String>();
		schoolPropertyMap.put(0, "公办");
		schoolPropertyMap.put(2, "民办");
		schoolPropertyMap.put(3, "外籍人员子女学校");
		schoolPropertyMap.put(4, "其他");
		
		
		List<CaDishSupDets> caDishSupDets = new ArrayList<>();
		//分页总数
		try {
			//获取列表  
			caDishSupDets = dbHiveService.getCaDishSupDetsList(listYearMonth, startDate, endDateAddOne, 
					   schName,null, dishName, rmcName, distIdorSCName,
					   caterType,schType,schProp,optMode,menuName,
					   (curPageNum-1)*pageSize, curPageNum*pageSize, schoolPropertyMap);
			caDishSupDets.removeAll(Collections.singleton(null));
			Integer pageTotalTemp = dbHiveService.getCaDishSupDetsCount(listYearMonth,  startDate, endDateAddOne, 
					   schName,null, dishName, rmcName, distIdorSCName,
					   caterType,schType,schProp,optMode,menuName);
			logger.info("行数01********************************"+pageTotalTemp);
			if(pageTotalTemp!=null) {
				pageTotal = pageTotalTemp;
			}
		}catch(Exception e) {
			pageTotal = 1;
			logger.info("行数catch********************************"+e.getMessage());
		}
	    
		//时戳
		cdsdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		
		
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		cdsdDto.setPageInfo(pageInfo);
		//设置数据
		cdsdDto.setCaDishSupDets(caDishSupDets);
		//消息ID
		cdsdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cdsdDto;
	}

	// 综合分析菜品供应明细列表模型函数
	public CaDishSupDetsDTO appModFunc(String token, String repastStartDate, String repasEndDate, String schName, String distName, 
			String prefCity, String province, String dishName, String rmcName, String caterType, String schType, String schProp, 
			String optMode, String menuName, String page, String pageSize, Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveService dbHiveService) {
		CaDishSupDetsDTO cdsdDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
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
			//学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
			int curSchType = -1;
			if(schType != null)
				curSchType = Integer.parseInt(schType);
			//学校性质，0:公办，1:民办，2:其他
			int curSchProp = -1;
			if(schProp != null)
				curSchProp = Integer.parseInt(schProp);
			//经营模式（供餐模式，1:外包-现场加工，2:外包-快餐配送
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
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 综合分析菜品供应明细列表函数
					if(methodIndex==1) {
						// 综合分析菜品供应明细列表函数
						cdsdDto = caDishSupDets(distIdorSCName, dates, tddList, db1Service, saasService, distName, prefCity, 
								province, schName, dishName, rmcName, caterType, curSchType, curSchProp, curOptMode, menuName);	
					}else if (methodIndex==2) {
						// 综合分析菜品供应明细列表函数
						cdsdDto = caDishSupDetsTwo(distIdorSCName,repastStartDate, repasEndDate, tddList, db1Service, saasService, dbHiveService, prefCity, 
								province, schName, dishName, rmcName, caterType, curSchType, curSchProp, curOptMode, menuName);	
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
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					if(methodIndex==1) {
						// 综合分析菜品供应明细列表函数
						cdsdDto = caDishSupDets(distIdorSCName, dates, tddList, db1Service, saasService, distName, prefCity,
								province, schName, dishName, rmcName, caterType, curSchType, curSchProp, curOptMode, menuName);
					}else if (methodIndex==2) {
						// 综合分析菜品供应明细列表函数
						cdsdDto = caDishSupDetsTwo(distIdorSCName, repastStartDate, repasEndDate, tddList, db1Service, saasService,dbHiveService, prefCity,
								province, schName, dishName, rmcName, caterType, curSchType, curSchProp, curOptMode, menuName);
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
			cdsdDto = SimuDataFunc();
		}		

		return cdsdDto;
	}
}