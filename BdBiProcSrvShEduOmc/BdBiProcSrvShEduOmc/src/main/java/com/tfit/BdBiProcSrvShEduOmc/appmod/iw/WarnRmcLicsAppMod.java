package com.tfit.BdBiProcSrvShEduOmc.appmod.iw;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnRmcLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnRmcLicsDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//证照预警团餐公司证件列表应用模型
public class WarnRmcLicsAppMod {
	private static final Logger logger = LogManager.getLogger(WarnRmcLicsAppMod.class.getName());
	
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
	int methodIndex = 3;
	
	//数组数据初始化
	String[] warnPeriod_Array = {"2018/09/03-2018/09/04", "2018/09/03-2018/09/04", "2018/09/03-2018/09/04"};
	String[] distName_Array = {"11", "1", "10"};
	int[] totalWarnNum_Array = {100, 100, 100};
	int[] noProcWarnNum_Array = {80, 80, 80};
	int[] rejectWarnNum_Array = {0, 0, 0};
	int[] auditWarnNum_Array = {10, 10, 10};
	int[] elimWarnNum_Array = {10, 10, 10};
	float[] warnProcRate_Array = {(float) 10.00, (float) 10.00, (float) 10.00};
	
	//模拟数据函数
	private WarnRmcLicsDTO SimuDataFunc() {
		WarnRmcLicsDTO wrlDto = new WarnRmcLicsDTO();
		//时戳
		wrlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//证照预警团餐公司证件列表模拟数据
		List<WarnRmcLics> warnRmcLics = new ArrayList<>();
		//赋值
		for (int i = 0; i < distName_Array.length; i++) {
			WarnRmcLics wrl = new WarnRmcLics();
			wrl.setWarnPeriod(warnPeriod_Array[i]);
			wrl.setDistName(distName_Array[i]);
			wrl.setTotalWarnNum(totalWarnNum_Array[i]);
			wrl.setNoProcWarnNum(noProcWarnNum_Array[i]);
			wrl.setRejectWarnNum(rejectWarnNum_Array[i]);
			wrl.setAuditWarnNum(auditWarnNum_Array[i]);
			wrl.setElimWarnNum(elimWarnNum_Array[i]);
			wrl.setWarnProcRate(warnProcRate_Array[i]);
			warnRmcLics.add(wrl);
		}
		//设置数据
		wrlDto.setWarnRmcLics(warnRmcLics);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = distName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		wrlDto.setPageInfo(pageInfo);
		//消息ID
		wrlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return wrlDto;
	}
	
	// 证照预警团餐公司证件列表函数
	private WarnRmcLicsDTO warnRmcLics(String distIdorSCName, String[] dates, List<TEduDistrictDo> tedList, Db1Service db1Service) {
		WarnRmcLicsDTO wrlDto = new WarnRmcLicsDTO();
		List<WarnRmcLics> warnRmcLics = new ArrayList<>();
		WarnRmcLics wrl = null;
		String key = "", keyVal = "";
		int i, j, k;
		int distCount = tedList.size(), dateCount = dates.length, curWarnNum = 0;
		int[][] totalWarnNums = new int[dateCount][distCount+1], noProcWarnNums = new int[dateCount][distCount+1], rejectWarnNums = new int[dateCount][distCount+1], auditWarnNums = new int[dateCount][distCount+1], elimWarnNums = new int[dateCount][distCount+1];
		float[] warnProcRates = new float[distCount+1];
		//区域ID到索引映射
		Map<String, Integer> distIdToIdxMap = new HashMap<>();
		for(i = 0; i < distCount; i++) {
			distIdToIdxMap.put(tedList.get(i).getId(), i);
		}
		distIdToIdxMap.put("-", i);
		// 时间段预警总数
		Map<String, String> warnTotalMap = null;
		// 时间段内各区预警统计
		for (k = 0; k < dates.length; k++) {
			// 供应数量
			key = dates[k] + "_warn-total";
			warnTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if(warnTotalMap == null) {    //Redis没有该数据则从hdfs系统中获取
				
			}
			if(warnTotalMap != null) {
				for(String curKey : warnTotalMap.keySet()) {
					String[] curKeys = curKey.split("_");
					if(curKeys.length == 5 && curKey.indexOf("supplier_") != -1) {
						i = AppModConfig.getVarValIndex(curKeys, "area");
						if(i != -1) {
							if(!curKeys[i].equalsIgnoreCase("null")) {
								if(distIdToIdxMap.containsKey(curKeys[i])) {
									int idx = distIdToIdxMap.get(curKeys[i]);
									keyVal = warnTotalMap.get(curKey);
									curWarnNum = 0;
									if(keyVal != null)
										curWarnNum = Integer.parseInt(keyVal);
									if(curWarnNum < 0)
										curWarnNum = 0;
									j = AppModConfig.getVarValIndex(curKeys, "status");
									if(j != -1) {
										if(curKeys[j].equalsIgnoreCase("1")) {         //未处理预警数
											noProcWarnNums[k][idx] += curWarnNum;
											totalWarnNums[k][idx] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("2")) {    //审核中预警数
											auditWarnNums[k][idx] += curWarnNum;
											totalWarnNums[k][idx] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("3")) {    //已驳回预警数
											rejectWarnNums[k][idx] += curWarnNum;
											totalWarnNums[k][idx] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("4")) {    //已消除预警数
											elimWarnNums[k][idx] += curWarnNum;
											totalWarnNums[k][idx] += curWarnNum;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		String startDate = dates[dates.length - 1].replaceAll("-", "/"), endDate = dates[0].replaceAll("-", "/");
		for (i = 0; i < distCount+1; i++) {
			String curDistId = "-";
			if(i < distCount) {
				TEduDistrictDo curTdd = tedList.get(i);
				curDistId = curTdd.getId();
			}
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distIdorSCName != null) {
				if (!curDistId.equals(distIdorSCName))
					continue;
			}
			wrl = new WarnRmcLics();
			wrl.setWarnPeriod(startDate + "-" + endDate);
			wrl.setDistName(curDistId);
			int totalWarnNum = 0, noProcWarnNum = 0, rejectWarnNum = 0, auditWarnNum = 0, elimWarnNum = 0;
			for (k = 0; k < dates.length; k++) {
				totalWarnNum += totalWarnNums[k][i];
				noProcWarnNum += noProcWarnNums[k][i];
				rejectWarnNum += rejectWarnNums[k][i];
				auditWarnNum += auditWarnNums[k][i];
				elimWarnNum += elimWarnNums[k][i];
			}			
			wrl.setTotalWarnNum(totalWarnNum);
			wrl.setNoProcWarnNum(noProcWarnNum);
			wrl.setRejectWarnNum(rejectWarnNum);
			wrl.setAuditWarnNum(auditWarnNum);
			wrl.setElimWarnNum(elimWarnNum);
			warnProcRates[i] = 0;
			if(totalWarnNum > 0) {
				warnProcRates[i] = 100 * ((float) elimWarnNum / (float) totalWarnNum);
				BigDecimal bd = new BigDecimal(warnProcRates[i]);
				warnProcRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRates[i] > 100)
					warnProcRates[i] = 100;
			}
			wrl.setWarnProcRate(warnProcRates[i]);
			if(i == distCount && totalWarnNum == 0)   //无区号且预警数为0的不输出
				continue;
			warnRmcLics.add(wrl);
		}
		// 设置返回数据
		wrlDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<WarnRmcLics> pageBean = new PageBean<WarnRmcLics>(warnRmcLics, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		wrlDto.setPageInfo(pageInfo);
		// 设置数据
		wrlDto.setWarnRmcLics(pageBean.getCurPageData());
		// 消息ID
		wrlDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return wrlDto;
	}		
	
	// 证照预警团餐公司证件列表函数
	private WarnRmcLicsDTO warnRmcLicsTwo(String distIdorSCName, String startDate, String endDate, List<TEduDistrictDo> tedList,Integer target,
			DbHiveWarnService dbHiveWarnService) {
		WarnRmcLicsDTO wslDto = new WarnRmcLicsDTO();
		List<WarnRmcLics> warnRmcLics = new ArrayList<>();
		WarnRmcLics wsl = null;
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
		List<WarnCommonLics> warnCommonLicList = new ArrayList<>();
		warnCommonLicList = dbHiveWarnService.getWarnLicList(2, listYearMonth, startDate, endDateAddOne, distIdorSCName,target);
		
		/**
		 * 2.合并数据(按区合并)
		 */
		Map<String, WarnRmcLics> warnTotalMap = new HashMap<String, WarnRmcLics>();
		for(WarnCommonLics warnCommonLics : warnCommonLicList) {
			WarnRmcLics warnCommonLicsTemp =warnTotalMap.get(warnCommonLics.getDistName());
			if(warnCommonLicsTemp == null) {
				warnCommonLicsTemp = new WarnRmcLics();
				warnCommonLicsTemp.setDistName(warnCommonLics.getDistName());
			}
			warnCommonLicsTemp.setTotalWarnNum(warnCommonLicsTemp.getTotalWarnNum() + warnCommonLics.getTotalWarnNum());
			warnCommonLicsTemp.setNoProcWarnNum(warnCommonLicsTemp.getNoProcWarnNum() + warnCommonLics.getNoProcWarnNum());
			warnCommonLicsTemp.setRejectWarnNum(warnCommonLicsTemp.getRejectWarnNum() + warnCommonLics.getRejectWarnNum());
			warnCommonLicsTemp.setAuditWarnNum(warnCommonLicsTemp.getAuditWarnNum() + warnCommonLics.getAuditWarnNum());
			warnCommonLicsTemp.setElimWarnNum(warnCommonLicsTemp.getElimWarnNum() + warnCommonLics.getElimWarnNum());
			
			warnTotalMap.put(warnCommonLics.getDistName(), warnCommonLicsTemp);
		}
		
		/**
		 * 3.计算每个区的合计和预警率
		 */
		// 时间段内各区预警统计
		String startDateTemp = startDate.replaceAll("-", "/");
		String endDateTemp = endDate.replaceAll("-", "/");
		float warnProcRate = 0;
		for (TEduDistrictDo curTdd : tedList) {
			warnProcRate = 0;
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distIdorSCName != null) {
				if (!curTdd.getId().equals(distIdorSCName))
					continue;
			}
			wsl = warnTotalMap.get(curTdd.getId());
			if(wsl ==null ) {
				wsl = new WarnRmcLics();
				wsl.setDistName(curTdd.getId());
			}
			wsl.setWarnPeriod(startDateTemp + "-" + endDateTemp);
			wsl.setTotalWarnNum(wsl.getNoProcWarnNum() + wsl.getRejectWarnNum() + wsl.getAuditWarnNum() + wsl.getElimWarnNum());
			warnProcRate = 0;
			if(wsl.getTotalWarnNum() > 0) {
				warnProcRate = 100 * ((float) wsl.getElimWarnNum() / (float) wsl.getTotalWarnNum());
				BigDecimal bd = new BigDecimal(warnProcRate);
				warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRate > 100)
					warnProcRate = 100;
			}
			wsl.setWarnProcRate(warnProcRate);
			warnRmcLics.add(wsl);
		}
		
		// 设置返回数据
		wslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<WarnRmcLics> pageBean = new PageBean<WarnRmcLics>(warnRmcLics, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		wslDto.setPageInfo(pageInfo);
		// 设置数据
		wslDto.setWarnRmcLics(pageBean.getCurPageData());
		// 消息ID
		wslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return wslDto;
	}		
	
	// 证照预警团餐公司证件列表函数
	private WarnRmcLicsDTO warnRmcLicsThree(String departmentId,String distIdorSCName,String distNames, String startDate, String endDate, List<TEduDistrictDo> tedList,Integer target,
			DbHiveWarnService dbHiveWarnService) {
		WarnRmcLicsDTO wslDto = new WarnRmcLicsDTO();
		List<WarnRmcLics> warnRmcLics = new ArrayList<>();
		WarnRmcLics wsl = null;
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
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(1, 2, target, listYearMonth, startDate, endDateAddOne,
				distIdorSCName, distNamesList, -1, -1, null, null, departmentId, null, 0);
		
		/**
		 * 2.合并数据(按区合并)
		 */
		Map<String, WarnRmcLics> warnTotalMap = new HashMap<String, WarnRmcLics>();
		for(WarnCommon warnCommonLics : warnCommonList) {
			WarnRmcLics warnCommonLicsTemp =warnTotalMap.get(warnCommonLics.getDistName());
			if(warnCommonLicsTemp == null) {
				warnCommonLicsTemp = new WarnRmcLics();
				warnCommonLicsTemp.setDistName(warnCommonLics.getDistName());
			}
			warnCommonLicsTemp.setTotalWarnNum(warnCommonLicsTemp.getTotalWarnNum() + warnCommonLics.getTotalWarnNum());
			warnCommonLicsTemp.setNoProcWarnNum(warnCommonLicsTemp.getNoProcWarnNum() + warnCommonLics.getNoProcWarnNum());
			warnCommonLicsTemp.setRejectWarnNum(warnCommonLicsTemp.getRejectWarnNum() + warnCommonLics.getRejectWarnNum());
			warnCommonLicsTemp.setAuditWarnNum(warnCommonLicsTemp.getAuditWarnNum() + warnCommonLics.getAuditWarnNum());
			warnCommonLicsTemp.setElimWarnNum(warnCommonLicsTemp.getElimWarnNum() + warnCommonLics.getElimWarnNum());
			
			warnTotalMap.put(warnCommonLics.getDistName(), warnCommonLicsTemp);
		}
		
		/**
		 * 3.计算每个区的合计和预警率
		 */
		// 时间段内各区预警统计
		String startDateTemp = startDate.replaceAll("-", "/");
		String endDateTemp = endDate.replaceAll("-", "/");
		float warnProcRate = 0;
		for (TEduDistrictDo curTdd : tedList) {
			warnProcRate = 0;
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distIdorSCName != null) {
				if (!curTdd.getId().equals(distIdorSCName))
					continue;
			}
			wsl = warnTotalMap.get(curTdd.getId());
			if(wsl ==null ) {
				wsl = new WarnRmcLics();
				wsl.setDistName(curTdd.getId());
			}
			wsl.setWarnPeriod(startDateTemp + "-" + endDateTemp);
			wsl.setTotalWarnNum(wsl.getNoProcWarnNum() + wsl.getRejectWarnNum() + wsl.getAuditWarnNum() + wsl.getElimWarnNum());
			warnProcRate = 0;
			if(wsl.getTotalWarnNum() > 0) {
				warnProcRate = 100 * ((float) wsl.getElimWarnNum() / (float) wsl.getTotalWarnNum());
				BigDecimal bd = new BigDecimal(warnProcRate);
				warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRate > 100)
					warnProcRate = 100;
			}
			wsl.setWarnProcRate(warnProcRate);
			warnRmcLics.add(wsl);
		}
		
		// 设置返回数据
		wslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<WarnRmcLics> pageBean = new PageBean<WarnRmcLics>(warnRmcLics, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		wslDto.setPageInfo(pageInfo);
		// 设置数据
		wslDto.setWarnRmcLics(pageBean.getCurPageData());
		// 消息ID
		wslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return wslDto;
	}		
	
	// 证照预警团餐公司证件列表函数
	private WarnRmcLicsDTO warnRmcLicsByDepartment(String departmentId,String departmentIds, String startDate, String endDate, List<DepartmentObj> departmntList,Integer target,
			DbHiveWarnService dbHiveWarnService) {
		WarnRmcLicsDTO wslDto = new WarnRmcLicsDTO();
		List<WarnRmcLics> warnRmcLics = new ArrayList<>();
		WarnRmcLics wsl = null;
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
		
		List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(1, 2, target, listYearMonth, startDate, endDateAddOne,
				null, null, -1, -1, null, null, departmentId, departmentIdsList, 4);
		
		/**
		 * 2.合并数据(按区合并)
		 */
		Map<String, WarnRmcLics> warnTotalMap = new HashMap<String, WarnRmcLics>();
		for(WarnCommon warnCommonLics : warnCommonList) {
			WarnRmcLics warnCommonLicsTemp =warnTotalMap.get(warnCommonLics.getDepartmentId());
			if(warnCommonLicsTemp == null) {
				warnCommonLicsTemp = new WarnRmcLics();
				warnCommonLicsTemp.setDepartmentId(warnCommonLics.getDepartmentId());
			}
			warnCommonLicsTemp.setTotalWarnNum(warnCommonLicsTemp.getTotalWarnNum() + warnCommonLics.getTotalWarnNum());
			warnCommonLicsTemp.setNoProcWarnNum(warnCommonLicsTemp.getNoProcWarnNum() + warnCommonLics.getNoProcWarnNum());
			warnCommonLicsTemp.setRejectWarnNum(warnCommonLicsTemp.getRejectWarnNum() + warnCommonLics.getRejectWarnNum());
			warnCommonLicsTemp.setAuditWarnNum(warnCommonLicsTemp.getAuditWarnNum() + warnCommonLics.getAuditWarnNum());
			warnCommonLicsTemp.setElimWarnNum(warnCommonLicsTemp.getElimWarnNum() + warnCommonLics.getElimWarnNum());
			
			warnTotalMap.put(warnCommonLics.getDepartmentId(), warnCommonLicsTemp);
		}
		
		/**
		 * 3.计算每个区的合计和预警率
		 */
		// 时间段内各区预警统计
		String startDateTemp = startDate.replaceAll("-", "/");
		String endDateTemp = endDate.replaceAll("-", "/");
		float warnProcRate = 0;
		for (DepartmentObj departmentObj : departmntList) {
			warnProcRate = 0;
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (departmentId != null) {
				if (!departmentObj.getDepartmentId().equals(departmentId))
					continue;
			}
			wsl = warnTotalMap.get(departmentObj.getDepartmentId());
			if(wsl ==null ) {
				wsl = new WarnRmcLics();
				wsl.setDepartmentId(departmentObj.getDepartmentId());
			}
			wsl.setDepartmentName(departmentObj.getDepartmentName());
			wsl.setWarnPeriod(startDateTemp + "-" + endDateTemp);
			wsl.setTotalWarnNum(wsl.getNoProcWarnNum() + wsl.getRejectWarnNum() + wsl.getAuditWarnNum() + wsl.getElimWarnNum());
			warnProcRate = 0;
			if(wsl.getTotalWarnNum() > 0) {
				warnProcRate = 100 * ((float) wsl.getElimWarnNum() / (float) wsl.getTotalWarnNum());
				BigDecimal bd = new BigDecimal(warnProcRate);
				warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRate > 100)
					warnProcRate = 100;
			}
			wsl.setWarnProcRate(warnProcRate);
			warnRmcLics.add(wsl);
		}
		
		// 设置返回数据
		wslDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<WarnRmcLics> pageBean = new PageBean<WarnRmcLics>(warnRmcLics, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		wslDto.setPageInfo(pageInfo);
		// 设置数据
		wslDto.setWarnRmcLics(pageBean.getCurPageData());
		// 消息ID
		wslDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();

		return wslDto;
	}		
	
	// 证照预警团餐公司证件列表模型函数
	public WarnRmcLicsDTO appModFunc(String token, String startWarnDate, String endWarnDate, String distName, String prefCity, 
			String province, String distNames,String departmentIds, String schSelMode, String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveWarnService dbHiveWarnService) {
		WarnRmcLicsDTO wrlDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(CommonUtil.isEmpty(schSelMode)) {
			schSelMode = "1";//默认按区
		}
		if(isRealData) {       //真实数据
			// 日期
			String[] dates = null;
			if (startWarnDate == null || endWarnDate == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			} else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(startWarnDate);
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(endWarnDate);
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
			String deparmentId = null;
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
					if(deparmentId == null)
						deparmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 证照预警团餐公司证件列表函数
					if(methodIndex == 1) {
						wrlDto = warnRmcLics(distIdorSCName, dates, tedList, db1Service);
					}else if (methodIndex == 2) {
						Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
						wrlDto = warnRmcLicsTwo(distIdorSCName, startWarnDate,endWarnDate, tedList,target,dbHiveWarnService);
					}else if (methodIndex == 3) {
						Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
						if("1".equals(schSelMode)) {  //按所在地
							wrlDto = warnRmcLicsThree(deparmentId,distIdorSCName, distNames,startWarnDate,endWarnDate, tedList,target,dbHiveWarnService);
						}else if("2".equals(schSelMode)) {  //按管理部门
							List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
							List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),departmentIdsList, -1, -1);
							wrlDto = warnRmcLicsByDepartment(deparmentId, departmentIds, startWarnDate, endWarnDate, deparmentList, target, dbHiveWarnService);
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
					if(deparmentId == null)
						deparmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 证照预警团餐公司证件列表函数
					if(methodIndex == 1) {
						wrlDto = warnRmcLics(distIdorSCName, dates, tedList, db1Service);
					}else if (methodIndex == 2) {
						Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
						wrlDto = warnRmcLicsTwo(distIdorSCName, startWarnDate,endWarnDate, tedList,target, dbHiveWarnService);
					}else if (methodIndex == 3) {
						Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
						if("1".equals(schSelMode)) {  //按所在地
							wrlDto = warnRmcLicsThree(deparmentId,distIdorSCName, distNames,startWarnDate,endWarnDate, tedList,target,dbHiveWarnService);
						}else if("2".equals(schSelMode)) {  //按管理部门
							List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
							List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),departmentIdsList, -1, -1);
							wrlDto = warnRmcLicsByDepartment(deparmentId, departmentIds, startWarnDate, endWarnDate, deparmentList, target, dbHiveWarnService);
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
			wrlDto = SimuDataFunc();
		}		

		return wrlDto;
	}
}
