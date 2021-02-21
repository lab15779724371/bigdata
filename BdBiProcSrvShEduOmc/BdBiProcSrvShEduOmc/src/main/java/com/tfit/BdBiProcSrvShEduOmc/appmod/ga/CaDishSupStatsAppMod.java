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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupStats;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupStatsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//综合分析菜品供应统计列表应用模型
public class CaDishSupStatsAppMod {
	private static final Logger logger = LogManager.getLogger(CaDishSupStatsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	int methodIndex = 2;
	
	//数组数据初始化
	int[] sn_Array = {1, 2};
	String[] dishName_Array = {"番茄炒蛋", "土豆肉丝"};
	String[] dishType_Array = {"素菜", "大荤"};
	int[] supNum_Array = {12253, 122205};	
	
	//模拟数据函数
	private CaDishSupStatsDTO SimuDataFunc() {
		CaDishSupStatsDTO cdssDto = new CaDishSupStatsDTO();
		//时戳
		cdssDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//综合分析菜品供应统计列表模拟数据
		List<CaDishSupStats> caDishSupStats = new ArrayList<>();
		//赋值
		for (int i = 0; i < sn_Array.length; i++) {
			CaDishSupStats cdss = new CaDishSupStats();
			cdss.setSn(sn_Array[i]);
			cdss.setDishName(dishName_Array[i]);
			cdss.setDishType(dishType_Array[i]);
			cdss.setSupNum(supNum_Array[i]);
			caDishSupStats.add(cdss);
		}		
		//设置数据
		cdssDto.setCaDishSupStats(caDishSupStats);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = sn_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		cdssDto.setPageInfo(pageInfo);
		//消息ID
		cdssDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cdssDto;
	}
	
	// 综合分析菜品供应统计列表函数
	private CaDishSupStatsDTO caDishSupStats(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, 
		String schName, String dishType, String caterType, String dishName, 
		Db1Service db1Service, SaasService saasService) {
		CaDishSupStatsDTO cdssDto = new CaDishSupStatsDTO();
		List<CaDishSupStats> caDishSupStats = new ArrayList<>();
		CaDishSupStats cdss = null;
		String key = "", keyVal = "";
		int i, j;
		// 时间段原料供应总数
		Map<String, String> dishmenutotalMap = null;
		Map<String, Integer> dishSupDntToNumMap = new HashMap<>();
		// 时间段内各区原料供应统计
		for (int k = 0; k < dates.length; k++) {
			// 供应数量
			key = dates[k] + "_dish-menu-total";
			dishmenutotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(dishmenutotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				
			}
			if(dishmenutotalMap != null) {
				for(String curKey : dishmenutotalMap.keySet()) {
					String[] curKeys = curKey.split("_");
					if(curKeys.length >= 10) {
						key = curKeys[1] + "_" + curKeys[3];
						keyVal = dishmenutotalMap.get(curKey);
						//条件判断
						boolean isAdd = true, isFind = false;
						int[] flIdxs = new int[5];
						j = 0;
						//判断区域（判断索引0）
						if(distIdorSCName != null) {
							isFind = false;
							for(i = 0; i < curKeys.length; i++) {
								if(curKeys[i].equalsIgnoreCase("area")) {
									isFind = true;
									j = i+1;
									break;
								}
							}
							if(isFind) {
								if(!curKeys[j].equalsIgnoreCase(distIdorSCName))
									flIdxs[0] = -1;
							}
							else
								flIdxs[0] = -1;
						}
						//判断学校名称（判断索引1）
						if(schName != null) {
							isFind = false;
							for(i = 0; i < curKeys.length; i++) {
								if(curKeys[i].equalsIgnoreCase("schoolid")) {
									isFind = true;
									j = i+1;
									break;
								}
							}
							if(isFind) {
								if(!curKeys[j].equalsIgnoreCase(schName))
									flIdxs[1] = -1;
							}
							else
								flIdxs[1] = -1;
						}
						//判断菜品类别（判断索引2）
						if(dishType != null) {
							isFind = false;
							for(i = 0; i < curKeys.length; i++) {
								if(curKeys[i].equalsIgnoreCase("category")) {
									isFind = true;
									j = i+1;
									break;
								}
							}
							if(isFind) {
								if(!curKeys[j].equalsIgnoreCase(dishType))
									flIdxs[2] = -1;
							}
							else
								flIdxs[2] = -1;
						}
						//判断餐别（判断索引3）
						if(caterType != null) {
							isFind = false;
							for(i = 0; i < curKeys.length; i++) {
								if(curKeys[i].equalsIgnoreCase("catertypename")) {
									isFind = true;
									j = i+1;
									break;
								}
							}
							if(isFind) {
								if(!curKeys[j].equalsIgnoreCase(caterType))
									flIdxs[3] = -1;
							}
							else
								flIdxs[3] = -1;
						}
						//判断菜品名称（判断索引4）
						if(dishName != null) {
							i = AppModConfig.getVarValIndex(curKeys, "dishesname");
							if(i != -1) {
								if(curKeys[i].indexOf(dishName) == -1)
									flIdxs[4] = -1;
							}
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
							if(keyVal != null) {
								Integer curSupNum = Integer.parseInt(keyVal);
								if(dishSupDntToNumMap.containsKey(key)) {
									Integer supNum = dishSupDntToNumMap.get(key);
									Integer sumSupNum = curSupNum + supNum;
									if(curSupNum > 0)
										dishSupDntToNumMap.put(key, sumSupNum);
								}
								else {
									if(curSupNum.intValue() > 0)
										dishSupDntToNumMap.put(key, curSupNum);
								}
							}
						}
					}
				}
			}
		}
		for(String curKey : dishSupDntToNumMap.keySet()) {
			String[] curKeys = curKey.split("_");
			if(curKeys.length > 1) {
				cdss = new CaDishSupStats();
				//菜品名称
				cdss.setDishName(curKeys[0]);
				//菜品类别
				cdss.setDishType(curKeys[1]);
				//供应份数
				int supNum = dishSupDntToNumMap.get(curKey);
				cdss.setSupNum(supNum);
				caDishSupStats.add(cdss);
			}
		}
		//排序
		SortList<CaDishSupStats> sortList = new SortList<CaDishSupStats>();
		sortList.Sort(caDishSupStats, "getSupNum", "desc", "Integer");
		for(i = 0; i < caDishSupStats.size(); i++)
			caDishSupStats.get(i).setSn(i+1);
		// 设置返回数据
		cdssDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<CaDishSupStats> pageBean = new PageBean<CaDishSupStats>(caDishSupStats, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		cdssDto.setPageInfo(pageInfo);
		// 设置数据
		cdssDto.setCaDishSupStats(pageBean.getCurPageData());
		// 消息ID
		cdssDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return cdssDto;
	}		
	
	// 综合分析菜品供应统计列表函数
	private CaDishSupStatsDTO caDishSupStatsTwo(String distName,  String startDate, String endDate, List<TEduDistrictDo> tedList, 
			String schName, String dishType, String caterType, String dishName,
			Db1Service db1Service, SaasService saasService,DbHiveService dbHiveService) {
		CaDishSupStatsDTO cdssDto = new CaDishSupStatsDTO();

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
		
		//****************
		List<CaDishSupStats> caDishSupStats = new ArrayList<CaDishSupStats>();
		//分页总数
		try {
			//获取列表  
			//开始条数
			int startNum =(curPageNum-1)*pageSize;
			Integer pageTotalTemp = 0;
			if(schName!=null && !"".equals(schName)) {
				//如果根据学校查询查询，则从app_t_edu_dish_menu表中获取数据
				caDishSupStats = dbHiveService.getCaDishSupStatsList(listYearMonth, startDate, endDateAddOne, schName, dishName, 
						distName, dishType, caterType, startNum, curPageNum*pageSize);
				caDishSupStats.removeAll(Collections.singleton(null));
				//添加序号
				startNum++;
				for(int i = 0; i < caDishSupStats.size(); i++) {
					caDishSupStats.get(i).setSn(startNum++);
			    }
				pageTotalTemp = dbHiveService.getCaDishSupStatsCount(listYearMonth, startDate, endDateAddOne, schName, dishName, 
						distName, dishType, caterType);
			}else {
				//如果不需要根据学校查询，则从app_t_edu_dish_total表中获取数据
				caDishSupStats = dbHiveService.getCaDishSupStatsListFromTotal(listYearMonth, startDate, endDateAddOne, dishName, 
						distName, dishType, caterType, startNum, curPageNum*pageSize);
				caDishSupStats.removeAll(Collections.singleton(null));
				//添加序号
				startNum++;
				for(int i = 0; i < caDishSupStats.size(); i++) {
					caDishSupStats.get(i).setSn(startNum++);
			    }
				pageTotalTemp = dbHiveService.getCaDishSupStatsCountFromTotal(listYearMonth, startDate, endDateAddOne, dishName, 
						distName, dishType, caterType);
			}
			logger.info("行数01********************************"+pageTotalTemp);
			if(pageTotalTemp!=null) {
				pageTotal = pageTotalTemp;
			}
		}catch(Exception e) {
			pageTotal = 1;
			logger.info("行数catch********************************"+e.getMessage());
		}
		//*****************
		// 设置返回数据
		cdssDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		cdssDto.setPageInfo(pageInfo);
		// 设置数据
		cdssDto.setCaDishSupStats(caDishSupStats);
		// 消息ID
		cdssDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return cdssDto;
	}		
	
	// 综合分析菜品供应统计列表模型函数
	public CaDishSupStatsDTO appModFunc(String token, String repastStartDate, String repasEndDate, String distName,
			String prefCity, String province, String schName, String dishType, String caterType, String dishName,
			String page, String pageSize,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveService dbHiveService) {
		CaDishSupStatsDTO cdssDto = null;
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
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 综合分析菜品供应统计列表函数
					if(methodIndex==1) {
						cdssDto = caDishSupStats(distIdorSCName, dates, tedList, schName, dishType, caterType, dishName, db1Service, saasService);
					}else if (methodIndex==2) {
						cdssDto = caDishSupStatsTwo(distIdorSCName, repastStartDate,repasEndDate, tedList, schName, dishType, caterType, dishName, db1Service, saasService,dbHiveService);
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
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 综合分析菜品供应统计列表函数
					if(methodIndex==1) {
						cdssDto = caDishSupStats(distIdorSCName, dates, tedList, schName, dishType, caterType, dishName, db1Service, saasService);
					}else if (methodIndex==2) {
						cdssDto = caDishSupStatsTwo(distIdorSCName, repastStartDate,repasEndDate, tedList, schName, dishType, caterType, dishName, db1Service, saasService,dbHiveService);
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
			cdssDto = SimuDataFunc();
		}		

		return cdssDto;
	}
}