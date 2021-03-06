package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.math.BigDecimal;
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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageBean;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonRecs;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwRmcRecs;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwRmcRecsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//餐厨垃圾团餐公司回收列表应用模型
public class KwRmcRecsAppMod {
	private static final Logger logger = LogManager.getLogger(KwRmcRecsAppMod.class.getName());

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
	String[] recDate_Array = {"2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04"};
	String[] distName_Array = {"11", "1", "16"};
	int[] rmcNum_Array = {50, 50, 50};
	int[] rcNum_Array = {60, 60, 60};
	
	//模拟数据函数
	private KwRmcRecsDTO SimuDataFunc() {
		KwRmcRecsDTO krrDto = new KwRmcRecsDTO();
		//时戳
		krrDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//餐厨垃圾团餐公司回收列表模拟数据
		List<KwRmcRecs> kwRmcRecs = new ArrayList<>();
		//赋值
		for (int i = 0; i < recDate_Array.length; i++) {
			KwRmcRecs krr = new KwRmcRecs();
			krr.setRecDate(recDate_Array[i]);
			krr.setDistName(distName_Array[i]);
			krr.setRmcNum(rmcNum_Array[i]);
			krr.setRcNum(rcNum_Array[i]);
			kwRmcRecs.add(krr);
		}
		//设置数据
		krrDto.setKwRmcRecs(kwRmcRecs);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = recDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		krrDto.setPageInfo(pageInfo);
		//消息ID
		krrDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return krrDto;
	}
	
	// 餐厨垃圾团餐公司回收列表函数
	private KwRmcRecsDTO kwRmcRecs(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList,
			String distNames) {
		KwRmcRecsDTO krrDto = new KwRmcRecsDTO();
		List<KwRmcRecs> kwRmcRecs = new ArrayList<>();
		KwRmcRecs krr = null;
		String key = "", keyVal = "", field = "", fieldPrefix = "";
		// 时间段内各区排菜团餐公司回收总数
		Map<String, String> supplierwastetotalMap = null;
		int distCount = tedList.size(), dateCount = dates.length;
		int[][] totalRcFreqs = new int[dateCount][distCount];
		float[][] totalRcNums = new float[dateCount][distCount];
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		
		// 时间段内各区排菜团餐公司回收数量
		for (int k = 0; k < dates.length; k++) {
			// 回收桶数
			key = dates[k] + "_supplierwastetotal";
			supplierwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(supplierwastetotalMap != null) {
				for(String curKey : supplierwastetotalMap.keySet()) {
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						String curDistId = curTdd.getId();
						//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						if(distIdorSCName != null) {
							if(!curDistId.equals(distIdorSCName))
								continue ;
						}else if(distNamesList!=null && distNamesList.size() >0) {
							if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
								continue;
							}
							if(!distNamesList.contains(curDistId)) {
								continue ;
							}
						}
						//区域回收次数
						fieldPrefix = curDistId + "_total";
						if (curKey.equalsIgnoreCase(fieldPrefix)) {
							keyVal = supplierwastetotalMap.get(curKey);
							if(keyVal != null) {
								totalRcFreqs[k][i] = Integer.parseInt(keyVal);
							}
						}
						// 区域回收垃圾桶数
						fieldPrefix = curDistId;
						if (curKey.equalsIgnoreCase(fieldPrefix)) {
							keyVal = supplierwastetotalMap.get(curKey);
							if(keyVal != null) {
								totalRcNums[k][i] = Float.parseFloat(keyVal);
							}
						}
					}
				}
			}
			// 该日期各区餐厨垃圾回收列表
			for (int i = 0; i < distCount; i++) {
				TEduDistrictDo curTdd = tedList.get(i);
				String curDistId = curTdd.getId();
				field = "area" + "_" + curDistId;
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (distIdorSCName != null) {
					if (!curDistId.equals(distIdorSCName))
						continue;
				}else if(distNamesList!=null && distNamesList.size() >0) {
					if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
						continue;
					}
					if(!distNamesList.contains(curDistId)) {
						continue ;
					}
				}
				BigDecimal bd = new BigDecimal(totalRcNums[k][i]);
				totalRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，回收次数：" + totalRcFreqs[k][i]
						+ "，回收数量：" + totalRcNums[k][i] + " 桶" + "，field = " + field);
			}
		}
		String startDate = dates[dates.length - 1].replaceAll("-", "/"), endDate = dates[0].replaceAll("-", "/");
		for (int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distIdorSCName != null) {
				if (!curDistId.equals(distIdorSCName))
					continue;
			}else if(distNamesList!=null && distNamesList.size() >0) {
				if(StringUtils.isEmpty(curDistId) || !StringUtils.isNumeric(curDistId)) {
					continue;
				}
				if(!distNamesList.contains(curDistId)) {
					continue ;
				}
			}
			krr = new KwRmcRecs();
			krr.setRecDate(startDate + "-" + endDate);
			krr.setDistName(curTdd.getId());
			int totalRcFreq = 0;
			float totalRcNum = 0;
			for (int k = 0; k < dates.length; k++) {
				totalRcFreq += totalRcFreqs[k][i];
				totalRcNum += totalRcNums[k][i];
			}

			krr.setRmcNum(totalRcFreq);
			krr.setRcNum(new BigDecimal(totalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			kwRmcRecs.add(krr);
		}
		// 设置返回数据
		krrDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<KwRmcRecs> pageBean = new PageBean<KwRmcRecs>(kwRmcRecs, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		krrDto.setPageInfo(pageInfo);
		// 设置数据
		krrDto.setKwRmcRecs(pageBean.getCurPageData());
		// 消息ID
		krrDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return krrDto;
	}	
	
	// 餐厨垃圾团餐公司回收列表函数
	private KwRmcRecsDTO kwRmcRecsFromHive(String distIdorSCName, String startDate, String endDate, List<TEduDistrictDo> tedList,
			String distNames,DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		KwRmcRecsDTO krrDto = new KwRmcRecsDTO();
		List<KwRmcRecs> kwRmcRecs = new ArrayList<>();
		KwRmcRecs ksr = null;
		// 时间段内各区餐厨垃圾学校回收总数
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
				
				/**
				 * 1.从hive库中获取汇总数据
				 */
				List<Object> distIdList=CommonUtil.changeStringToList(distNames);
				List<KwCommonRecs> warnCommonLicList = new ArrayList<>();
				warnCommonLicList = dbHiveRecyclerWasteService.getRecyclerWasteList(listYearMonth, startDate, endDateAddOne, distIdorSCName, 
						distIdList, null, null, -1,-1,null,null,2, 1,0);
				
				/**
				 * 2.合并数据(按区合并)
				 */
				Map<String, KwRmcRecs> warnTotalMap = new HashMap<String, KwRmcRecs>();
				for(KwCommonRecs kwCommonRecs : warnCommonLicList) {
					ksr =warnTotalMap.get(kwCommonRecs.getDistName());
					if(ksr == null) {
						ksr = new KwRmcRecs();
						ksr.setDistName(kwCommonRecs.getDistName());
					}
					ksr.setRmcNum(ksr.getRmcNum() + kwCommonRecs.getRecyclerTotal());
					ksr.setRcNum(ksr.getRcNum() + kwCommonRecs.getRecyclerSum());
					warnTotalMap.put(kwCommonRecs.getDistName(), ksr);
				}
				
				/**
				 * 3.计算每个区的合计和预警率
				 */
				// 时间段内各区预警统计
				String startDateTemp = startDate.replaceAll("-", "/");
				String endDateTemp = endDate.replaceAll("-", "/");
				for (TEduDistrictDo curTdd : tedList) {
					// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if (distIdorSCName != null) {
						if (!curTdd.getId().equals(distIdorSCName))
							continue;
					}
					ksr = warnTotalMap.get(curTdd.getId());
					if(ksr ==null ) {
						ksr = new KwRmcRecs();
						ksr.setDistName(curTdd.getId());
					}
					ksr.setRecDate(startDateTemp + "-" + endDateTemp);
					ksr.setRcNum(new BigDecimal(ksr.getRcNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
					
					
					kwRmcRecs.add(ksr);
				}
		// 设置返回数据
		krrDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<KwRmcRecs> pageBean = new PageBean<KwRmcRecs>(kwRmcRecs, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		krrDto.setPageInfo(pageInfo);
		// 设置数据
		krrDto.setKwRmcRecs(pageBean.getCurPageData());
		// 消息ID
		krrDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return krrDto;
	}	
	
	// 餐厨垃圾团餐公司回收列表模型函数
	public KwRmcRecsDTO appModFunc(String token, String recStartDate, String recEndDate, String distName, 
			String prefCity, String province, String distNames,
			String page, String pageSize, Db1Service db1Service, Db2Service db2Service,DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		KwRmcRecsDTO krrDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 日期
			String[] dates = null;
			if (recStartDate == null || recEndDate == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			} else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(recStartDate);
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(recEndDate);
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
					// 餐厨垃圾团餐公司回收列表函数
					if(methodIndex == 1) {
						krrDto = kwRmcRecs(distIdorSCName, dates, tedList,distNames);
					}else if(methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						
						//日期包含30天之前的数据则去hive库
						if(days >= 2) {
							krrDto = kwRmcRecsFromHive(distIdorSCName, dates[dates.length-1], dates[0], tedList, distNames, 
									dbHiveRecyclerWasteService);
						}else {
							krrDto = kwRmcRecs(distIdorSCName, dates, tedList,distNames);
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
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 餐厨垃圾团餐公司回收列表函数
					if(methodIndex == 1) {
						krrDto = kwRmcRecs(distIdorSCName, dates, tedList,distNames);
					}else if(methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						
						//日期包含30天之前的数据则去hive库
						if(days >= 2) {
							krrDto = kwRmcRecsFromHive(distIdorSCName, recStartDate, recEndDate, tedList, distNames, 
									dbHiveRecyclerWasteService);
						}else {
							krrDto = kwRmcRecs(distIdorSCName, dates, tedList,distNames);
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
			krrDto = SimuDataFunc();
		}		

		return krrDto;
	}
}
