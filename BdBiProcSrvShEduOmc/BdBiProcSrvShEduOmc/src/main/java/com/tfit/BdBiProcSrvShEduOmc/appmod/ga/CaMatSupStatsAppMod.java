package com.tfit.BdBiProcSrvShEduOmc.appmod.ga;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TBaseMaterialTypeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupStats;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupStatsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//综合分析原料供应统计列表应用模型
public class CaMatSupStatsAppMod {
	private static final Logger logger = LogManager.getLogger(CaMatSupStatsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 2;
	
	//数组数据初始化
	int[] sn_Array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
	String[] standardName_Array = {"鸡蛋", "西红柿", "猪肉", "鸡胸肉", "桂鱼", "大白菜", "虾仁", "胡萝卜", "带鱼", "牛肉", "玉米", "五花肉", "花菜", "鸡腿"};
	int[] matCategory_Array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	String[] matClassify_Array = {"蛋类产品", "蔬菜（木本植物）", "兽类、禽类和爬行类动物肉产品", "兽类、禽类和爬行类动物肉产品", "水产动物", "蔬菜（木本植物）", "水产品制品", "蔬菜（木本植物）", "水产动物", "兽类、禽类和爬行类动物肉产品", "蔬菜（木本植物）", "兽类、禽类和爬行类动物肉产品", "蔬菜（木本植物）", "兽类、禽类和爬行类动物肉产品"};
	float[] actualQuan_Array = {(float)2555.0, (float)2332.0, (float)2222.0, (float)2220.0, (float)2105.0, (float)2102.0, (float)2020.0, (float)2002.0, (float)2002.0, (float)2000.0, (float)2000.0, (float)2000.0, (float)1990.0, (float)1980.0};
	
	//模拟数据函数
	private CaMatSupStatsDTO SimuDataFunc() {
		CaMatSupStatsDTO cmssDto = new CaMatSupStatsDTO();
		//时戳
		cmssDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//综合分析原料供应统计列表模拟数据
		List<CaMatSupStats> caMatSupStats = new ArrayList<>();
		//赋值
		for (int i = 0; i < sn_Array.length; i++) {
			CaMatSupStats cmss = new CaMatSupStats();
			cmss.setSn(sn_Array[i]);
			cmss.setStandardName(standardName_Array[i]);
			cmss.setMatCategory(matCategory_Array[i]);
			cmss.setMatClassify(matClassify_Array[i]);
			cmss.setActualQuan(actualQuan_Array[i]);
			caMatSupStats.add(cmss);
		}		
		//设置数据
		cmssDto.setCaMatSupStats(caMatSupStats);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = sn_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		cmssDto.setPageInfo(pageInfo);
		//消息ID
		cmssDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cmssDto;
	}
	
	// 综合分析原料供应统计列表函数
	private CaMatSupStatsDTO caMatSupStats(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, int schType, String schName, String matClassify, int matCategory, String matStdName, Db1Service db1Service, SaasService saasService) {
		CaMatSupStatsDTO cmssDto = new CaMatSupStatsDTO();
		List<CaMatSupStats> caMatSupStats = new ArrayList<>();
		CaMatSupStats cmss = null;
		String key = "", keyVal = "";
		int i, j;
		//所有学校id
		Map<String, Integer> schIdMap = new HashMap<>();
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 1);
		for(i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		//所有原料分类id
		Map<String, String> matClassifyIdToNameMap = new HashMap<>();
		List<TBaseMaterialTypeDo> tbmtList = saasService.getAllMatClassifyIdName();
		if(tbmtList != null) {
			for(i = 0; i < tbmtList.size(); i++) {
				matClassifyIdToNameMap.put(String.valueOf(tbmtList.get(i).getId()), tbmtList.get(i).getName());
			}
		}
		// 时间段原料供应总数
		Map<String, String> material2supplierMap = null;
		Map<String, Float> matSupStdNmtmcToQuaMap = new HashMap<>();
		// 时间段内各区原料供应统计
		for (int k = 0; k < dates.length; k++) {
			// 供应数量
			key = dates[k] + "_Material2supplier";
			material2supplierMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(material2supplierMap == null) {    //Redis没有该数据则从hdfs系统中获取
				
			}
			if(material2supplierMap != null) {
				for(String curKey : material2supplierMap.keySet()) {
					String[] curKeys = curKey.split("_");
					if(curKeys.length >= 11) {
						key = curKeys[2] + "_" + curKeys[0] + "_" + curKeys[4];
						keyVal = material2supplierMap.get(curKey);
						//学校信息（项目点）
						TEduSchoolDo tesDo = null;
						if(schIdMap.containsKey(curKeys[10])) {
							j = schIdMap.get(curKeys[10]);
							tesDo = tesDoList.get(j-1);
						}
						if(tesDo == null)
							continue;
						//条件判断
						boolean isAdd = true;
						int[] flIdxs = new int[6];
						//判断区域（判断索引0）
						if(distIdorSCName != null) {
							if(!distIdorSCName.equalsIgnoreCase(curKeys[8]))
								flIdxs[0] = -1;
						}
						//判断学校学制（判断索引1）
						if(schType != -1) {
							if(tesDo != null) {
								String schTypeName = AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2());
								int curSchType = AppModConfig.schTypeNameToIdMap.get(schTypeName);
								if(curSchType != schType)
									flIdxs[1] = -1;
							}
						}
						//判断学校名称（判断索引2）
						if(schName != null) {
							if(!curKeys[10].equalsIgnoreCase(schName))
								flIdxs[2] = -1;
						}
						//判断分类（物料）（判断索引3）
						if(matClassify != null) {
							if(matClassifyIdToNameMap.containsKey(matClassify)) {
								if(!curKeys[4].equalsIgnoreCase(matClassifyIdToNameMap.get(matClassify)))
									flIdxs[3] = -1;
							}
						}
						//判断原料类别，0:主料，1:辅料（判断索引4）
						if(matCategory != -1) {
							if(AppModConfig.matCategoryNameToIdMap.containsKey(curKeys[0])) {
								int curMatCategory = AppModConfig.matCategoryNameToIdMap.get(curKeys[0]);
								if(curMatCategory != matCategory)
									flIdxs[4] = -1;
							}
						}
						//判断原料标准名称（判断索引5）
						if(matStdName != null) {
							if(curKeys[2].indexOf(matStdName) == -1)
								flIdxs[5] = -1;
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
								Float curSupNum = Float.parseFloat(keyVal);
								if(matSupStdNmtmcToQuaMap.containsKey(key)) {
									Float supNum = matSupStdNmtmcToQuaMap.get(key);
									Float sumSupNum = curSupNum + supNum;
									if(curSupNum > 0)
										matSupStdNmtmcToQuaMap.put(key, sumSupNum);
								}
								else {
									if(curSupNum > 0)
										matSupStdNmtmcToQuaMap.put(key, curSupNum);
								}
							}
						}
					}
				}
			}
		}
		for(String curKey : matSupStdNmtmcToQuaMap.keySet()) {
			String[] curKeys = curKey.split("_");
			cmss = new CaMatSupStats();
			//标准名称
			cmss.setStandardName(curKeys[0]);
			//原料类别，0:主料，1:辅料
			cmss.setMatCategory(0);
			if(AppModConfig.matCategoryNameToIdMap.containsKey(curKeys[1]))
				cmss.setMatCategory(AppModConfig.matCategoryNameToIdMap.get(curKeys[1]));
			//原料分类
			cmss.setMatClassify(curKeys[2]);
			//实际数量
			float actualQuan = matSupStdNmtmcToQuaMap.get(curKey);
			BigDecimal bd = new BigDecimal(actualQuan);
			actualQuan = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			cmss.setActualQuan(actualQuan);
			caMatSupStats.add(cmss);
		}
		//排序
		SortList<CaMatSupStats> sortList = new SortList<CaMatSupStats>();
		sortList.Sort(caMatSupStats, "getActualQuan", "desc", "Float");
		for(i = 0; i < caMatSupStats.size(); i++)
			caMatSupStats.get(i).setSn(i+1);
		// 设置返回数据
		cmssDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<CaMatSupStats> pageBean = new PageBean<CaMatSupStats>(caMatSupStats, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		cmssDto.setPageInfo(pageInfo);
		// 设置数据
		cmssDto.setCaMatSupStats(pageBean.getCurPageData());
		// 消息ID
		cmssDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return cmssDto;
	}		
	
	// 综合分析原料供应统计列表函数
	private CaMatSupStatsDTO caMatSupStatsTwo(String distIdorSCName,String startDate, String endDate, 
			List<TEduDistrictDo> tedList, int schType, String schName, String matClassify, int matCategory, String matStdName, 
			Db1Service db1Service, SaasService saasService,DbHiveService dbHiveService) {
		CaMatSupStatsDTO cmssDto = new CaMatSupStatsDTO();
		List<CaMatSupStats> caMatSupStats = new ArrayList<>();
		
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
    			
		//分页总数
		try {
			//获取列表  
			//开始条数
			int startNum =(curPageNum-1)*pageSize;
			Integer pageTotalTemp = 0;
			
			//hive库中1 主料 2 辅料，接口定义中原料类别，0:主料，1:辅料
			if(matCategory >=0) {
				if(matCategory==0) {
					matCategory =1;
				}else if (matCategory==1) {
					matCategory =2;
				}
			}
			if(StringUtils.isNotEmpty(schName) || schType>=0) {
				//如果根据学校查询查询，则从app_t_edu_dish_menu表中获取数据
				caMatSupStats = dbHiveService.getCaMatSupStatsList(listYearMonth, startDate, endDateAddOne, distIdorSCName, schType, 
						schName, matClassify, matCategory, matStdName, startNum, curPageNum*pageSize);
				caMatSupStats.removeAll(Collections.singleton(null));
				//添加序号
				startNum++;
				for(int i = 0; i < caMatSupStats.size(); i++) {
					caMatSupStats.get(i).setSn(startNum++);
					//转换主料、辅料
					//hive库中1 主料 2 辅料，接口定义中原料类别，0:主料，1:辅料
					if(caMatSupStats.get(i).getMatCategory() == 1) {
						caMatSupStats.get(i).setMatCategory(0);
					}else if (caMatSupStats.get(i).getMatCategory() == 2) {
						caMatSupStats.get(i).setMatCategory(1);
					}
					
					Float actualQuan = Float.valueOf("0.00");
		        	BigDecimal bd = new BigDecimal(caMatSupStats.get(i).getActualQuan());
					actualQuan = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					caMatSupStats.get(i).setActualQuan(actualQuan);
			    }
				pageTotalTemp = dbHiveService.getCaMatSupStatsCount(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
						schType, schName, matClassify, matCategory, matStdName);
			}else {
				//如果不需要根据学校查询，则从app_t_edu_dish_total表中获取数据
				caMatSupStats = dbHiveService.getCaMatSupStatsListFromTotal(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
						matClassify, matCategory, matStdName, startNum, curPageNum*pageSize);
				caMatSupStats.removeAll(Collections.singleton(null));
				//添加序号
				startNum++;
				for(int i = 0; i < caMatSupStats.size(); i++) {
					caMatSupStats.get(i).setSn(startNum++);
					//转换主料、辅料
					//hive库中1 主料 2 辅料，接口定义中原料类别，0:主料，1:辅料
					if(caMatSupStats.get(i).getMatCategory() == 1) {
						caMatSupStats.get(i).setMatCategory(0);
					}else if (caMatSupStats.get(i).getMatCategory() == 2) {
						caMatSupStats.get(i).setMatCategory(1);
					}
					
					Float actualQuan = Float.valueOf("0.00");
		        	BigDecimal bd = new BigDecimal(caMatSupStats.get(i).getActualQuan());
					actualQuan = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					caMatSupStats.get(i).setActualQuan(actualQuan);
			    }
				pageTotalTemp = dbHiveService.getCaMatSupStatsCountFromTotal(listYearMonth, startDate, endDateAddOne, 
						distIdorSCName, matClassify, matCategory, matStdName);
			}
			logger.info("行数01********************************"+pageTotalTemp);
			if(pageTotalTemp!=null) {
				pageTotal = pageTotalTemp;
			}
		}catch(Exception e) {
			pageTotal = 1;
			logger.info("行数catch********************************"+e.getMessage());
		}
				
		// 设置返回数据
		cmssDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		cmssDto.setPageInfo(pageInfo);
		// 设置数据
		cmssDto.setCaMatSupStats(caMatSupStats);
		// 消息ID
		cmssDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return cmssDto;
	}		
	
	// 综合分析原料供应统计列表模型函数
	public CaMatSupStatsDTO appModFunc(String token, String startUseDate, String endUseDate, String distName, 
			String prefCity, String province, String schType, String schName, String matClassify, 
			String matCategory, String matStdName, 
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveService dbHiveService) {
		CaMatSupStatsDTO cmssDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 日期
			String[] dates = null;
			if (startUseDate == null || endUseDate == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			} else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(startUseDate);
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(endUseDate);
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
			//学校类型（学制），0:托儿所，1:托幼园，2:托幼小，3:幼儿园，4:幼小，5:幼小初，6:幼小初高，7:小学，8:初级中学，9:高级中学，10:完全中学，11:九年一贯制学校，12:十二年一贯制学校，13:职业初中，14:中等职业学校，15:工读学校，16:特殊教育学校，17:其他
	  		int curSchType = -1;
	  		if(schType != null)
	  			curSchType = Integer.parseInt(schType);
			//原料类别，0:主料，1:辅料
			int curMatCategory = -1;
			if(matCategory != null)
				curMatCategory = Integer.parseInt(matCategory);
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
					// 综合分析原料供应统计列表函数
					if(methodIndex == 1) {
						cmssDto = caMatSupStats(distIdorSCName, dates, tedList, curSchType, schName, matClassify, curMatCategory, matStdName, db1Service, saasService);
					}else if (methodIndex == 2) {
						cmssDto = caMatSupStatsTwo(distIdorSCName, startUseDate,endUseDate, tedList, curSchType, schName, matClassify, curMatCategory,
								matStdName, db1Service, saasService,dbHiveService);
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
					// 综合分析原料供应统计列表函数
					if(methodIndex == 1) {
						cmssDto = caMatSupStats(distIdorSCName, dates, tedList, curSchType, schName, matClassify, curMatCategory, matStdName, 
								db1Service, saasService);
					}else if (methodIndex == 2) {
						cmssDto = caMatSupStatsTwo(distIdorSCName, startUseDate,endUseDate, tedList, curSchType, schName, matClassify, curMatCategory, matStdName,
								db1Service, saasService,dbHiveService);
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
			cmssDto = SimuDataFunc();
		}		

		return cmssDto;
	}
}
