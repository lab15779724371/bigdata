package com.tfit.BdBiProcSrvShEduOmc.appmod.im;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RmcKwDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.RmcKwDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//团餐公司餐厨垃圾详情列表应用模型
public class RmcKwDetsAppMod {
	private static final Logger logger = LogManager.getLogger(RmcKwDetsAppMod.class.getName());
	
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
	String[] recDate_Array = {"2018/08/01", "2018/08/01", "2018/08/01"};
	String[] distName_Array = {"11", "11", "11"};
	String[] rmcName_Array = {"上海绿捷实业发展有限公司", "上海龙神餐饮有限公司", "上海市民办盛大花园小学"};
	float[] recNum_Array = {2, 1, 2};
	String[] recComany_Array = {"上海市环兴环境资源利用有限公司", "上海市环兴环境资源利用有限公司", "上海市环兴环境资源利用有限公司"};
	String[] recPerson_Array = {"张山", "张山", "张山"};
	int[] recBillNum_Array = {2, 1, 2};
	
	//模拟数据函数
	private RmcKwDetsDTO SimuDataFunc() {
		RmcKwDetsDTO rkdDto = new RmcKwDetsDTO();
		//设置返回数据
		rkdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//列表元素设置
		List<RmcKwDets> rmcKwDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < recDate_Array.length; i++) {
			RmcKwDets rkd = new RmcKwDets();
			rkd.setRecDate(recDate_Array[i]);
			rkd.setDistName(distName_Array[i]);
			rkd.setRmcName(rmcName_Array[i]);
			rkd.setRecNum(String.valueOf(recNum_Array[i]));
			rkd.setRecComany(recComany_Array[i]);
			rkd.setRecPerson(recPerson_Array[i]);
			rkd.setRecBillNum(recBillNum_Array[i]);
			rmcKwDets.add(rkd);
		}
		//设置数据
		rkdDto.setRmcKwDets(rmcKwDets);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = recDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		rkdDto.setPageInfo(pageInfo);
		//消息ID
		rkdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rkdDto;
	}

	// 团餐公司餐厨垃圾详情列表函数
	RmcKwDetsDTO rmcKwDets(String distIdorSCName, String[] dates, 
			List<TEduDistrictDo> tddList, Db1Service db1Service, SaasService saasService, 
			String distName, String prefCity, String province, String ppName, 
			String rmcName, String recComany, String recPerson,
			String distNames) {
		RmcKwDetsDTO rkdDto = new RmcKwDetsDTO();
		List<RmcKwDets> rmcKwDets = new ArrayList<>();
		Map<String, String> supplierwasteMap = new HashMap<>();
		int i, j, k, dateCount = dates.length;
		String key = null, keyVal = null;
		
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = new ArrayList<TEduSchoolDo>();
		if(distIdorSCName!=null && !"".equals(distIdorSCName)) {
			tesDoList = db1Service.getTEduSchoolDoListByDs1(distIdorSCName,1,1, 5);
		}else {
			tesDoList = db1Service.getTEduSchoolDoListByDs1(distNamesList,1,1);
		}
		for(i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		//团餐公司id和学校id映射
    	Map<String, String> SupplierIdToschIdMap = new HashMap<>();
    	List<TEduSchoolSupplierDo> tessDoList = saasService.getAllSupplierIdSchoolId();
    	if(tesDoList != null) {
    		for(i = 0; i < tessDoList.size(); i++)
    			SupplierIdToschIdMap.put(tessDoList.get(i).getSupplierId(), tessDoList.get(i).getSchoolId());
    	}
		//团餐公司id和团餐公司名称
		Map<String, String> RmcIdToNameMap = new HashMap<>();
    	List<TProSupplierDo> tpsDoList = saasService.getIdSupplierIdName();
    	if(tpsDoList != null) {
    		for(i = 0; i < tpsDoList.size(); i++) {
    			RmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
    		}
    	}
		// 时间段内各区团餐公司餐厨垃圾详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_supplierwaste";
			supplierwasteMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (supplierwasteMap != null) {
				for (String curKey : supplierwasteMap.keySet()) {
					keyVal = supplierwasteMap.get(curKey);
					// 团餐公司餐厨垃圾列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 12) {
						if(distIdorSCName != null) {
							if(keyVals[1].compareTo(distIdorSCName) != 0)
								continue ;
						}else if(distNamesList!=null && distNamesList.size() >0) {
							if(StringUtils.isEmpty(keyVals[1]) || !StringUtils.isNumeric(keyVals[1])) {
								continue;
							}
							if(!distNamesList.contains(keyVals[1])) {
								continue ;
							}
						}
						RmcKwDets rkd = new RmcKwDets();
						//学校信息（项目点）
						TEduSchoolDo tesDo = null;
						if(SupplierIdToschIdMap.containsKey(keyVals[3])) {
							String schId = SupplierIdToschIdMap.get(keyVals[3]);
							if(schIdMap.containsKey(schId)) {
								j = schIdMap.get(schId);
								tesDo = tesDoList.get(j-1);
							}
						}
						//就餐日期
						rkd.setRecDate(dates[k].replaceAll("-", "/"));
						//区
						rkd.setDistName("-");
						if(!keyVals[1].equalsIgnoreCase("null"))
							rkd.setDistName(keyVals[1]);				
						//团餐公司
						if(RmcIdToNameMap.containsKey(keyVals[3]))
							rkd.setRmcName(RmcIdToNameMap.get(keyVals[3]));
						else
							rkd.setRmcName("-");
						//回收数量
						if(!keyVals[5].equalsIgnoreCase("null")) {
							//float rcNum = Float.parseFloat(keyVals[5]);
							//BigDecimal bd = new BigDecimal(rcNum);
							//rcNum = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
							rkd.setRecNum(keyVals[5]);
						}
						//回收单位
						rkd.setRecComany("-");
						if(!keyVals[7].equalsIgnoreCase("null"))
							rkd.setRecComany(keyVals[7]);
						//回收人
						rkd.setRecPerson(keyVals[9]);
						//回收单据
						rkd.setRecBillNum(0);
						if(!keyVals[11].equalsIgnoreCase("null"))
							rkd.setRecBillNum(Integer.parseInt(keyVals[11]));
						//条件判断
						boolean isAdd = true;
						int[] flIdxs = new int[5];
						//判断项目点名称（判断索引0）
						if(ppName != null && tesDo != null) {
							if(tesDo.getSchoolName().indexOf(ppName) == -1)
								flIdxs[0] = -1;
						}
						//判断团餐公司（判断索引1）
						if(rmcName != null && RmcIdToNameMap != null) {
							if(!(rkd.getRmcName().equalsIgnoreCase(RmcIdToNameMap.get(rmcName))))
								flIdxs[2] = -1;
						}
						//回收单位名称（判断索引2）
						if(recComany != null) {
							if(rkd.getRecComany().indexOf(recComany) == -1)
								flIdxs[3] = -1;
						}
						//回收人（判断索引3）
						if(recPerson != null) {
							if(rkd.getRecPerson().indexOf(recPerson) == -1)
								flIdxs[4] = -1;
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
							rmcKwDets.add(rkd);
					}
					else
						logger.info("团餐公司餐厨垃圾："+ curKey + "，格式错误！");
				}
			}
		}
		//时戳
		rkdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<RmcKwDets> pageBean = new PageBean<RmcKwDets>(rmcKwDets, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		rkdDto.setPageInfo(pageInfo);
		//设置数据
		rkdDto.setRmcKwDets(pageBean.getCurPageData());
		//消息ID
		rkdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rkdDto;
	}		
	
	// 团餐公司餐厨垃圾详情列表函数
	RmcKwDetsDTO rmcKwDetsFromHive(String distIdorSCName, String[] dates, 
			List<TEduDistrictDo> tddList, SaasService saasService, 
			String distName, String prefCity, String province, String ppName, 
			String rmcId, String recComany, String recPerson,
			String distNames,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		RmcKwDetsDTO rkdDto = new RmcKwDetsDTO();
		List<RmcKwDets> rmcKwDets = new ArrayList<>();
		int i;
		List<Object> distNamesList=CommonUtil.changeStringToList(distNames);
		
		//团餐公司id和团餐公司名称
		Map<String, String> rmcIdToNameMap = new HashMap<>();
    	List<TProSupplierDo> tpsDoList = saasService.getIdSupplierIdName();
    	if(tpsDoList != null) {
    		for(i = 0; i < tpsDoList.size(); i++) {
    			rmcIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
    		}
    	}
		
		String rmcNameNew = rmcIdToNameMap.get(rmcId);
		
		// 时间段内各区餐厨垃圾学校回收总数
    	String startDate = dates[dates.length-1];
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
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<KwCommonDets> commonDets = new ArrayList<>();
		commonDets = dbHiveRecyclerWasteService.getRecyclerWasteDetsList(listYearMonth, startDate, endDateAddOne, distIdorSCName,
				ppName, -1,null,rmcNameNew, recComany, recPerson, -1, -1, -1,null, 
				distNamesList, null, null, null, null, 2, 1, (curPageNum-1)*pageSize, curPageNum*pageSize);
		//移除所有未null的值
		commonDets.removeAll(Collections.singleton(null));
		Integer pageTotalTemp = dbHiveRecyclerWasteService.getRecyclerWasteDetsCount(listYearMonth, startDate, endDateAddOne, distIdorSCName,
				ppName, -1,null, rmcNameNew, recComany, recPerson, -1, -1, -1, null,
				distNamesList, null, null, null, null, 2, 1);
		if(pageTotalTemp!=null) {
			pageTotal = pageTotalTemp;
		}
		for(KwCommonDets kwCommonDets : commonDets) {
			RmcKwDets schKwDetObj = new RmcKwDets();
			try {
				BeanUtils.copyProperties(schKwDetObj, kwCommonDets);
			} catch (IllegalAccessException e) {
				continue;
			} catch (InvocationTargetException e) {
				continue;
			}
			
			rmcKwDets.add(schKwDetObj);
		}
		
		//时戳
		rkdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<RmcKwDets> pageBean = new PageBean<RmcKwDets>(rmcKwDets, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		rkdDto.setPageInfo(pageInfo);
		//设置数据
		rkdDto.setRmcKwDets(pageBean.getCurPageData());
		//消息ID
		rkdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return rkdDto;
	}		
	
	//团餐公司餐厨垃圾详情列表模型函数
	public RmcKwDetsDTO appModFunc(String token, String recStartDate, String recEndDate, 
			String distName, String prefCity, String province, String ppName, String rmcName, 
			String recComany, String recPerson, 
			 String distNames,
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		RmcKwDetsDTO rkdDto = null;
		this.curPageNum = Integer.parseInt(page);
		this.pageSize = Integer.parseInt(pageSize);
		if (isRealData) { // 真实数据
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
					logger.info("dates[" + i + "] = " + dates[i]);
				}
			}
			// 省或直辖市
			if(province == null)
				province = "上海市";
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
					// 团餐公司餐厨垃圾详情列表函数
					if(methodIndex == 1) {
						rkdDto = rmcKwDets(distIdorSCName, dates, tddList, db1Service, saasService, distName, 
								prefCity, province, ppName, rmcName, recComany, recPerson, distNames);		
					}else if(methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						
						//日期包含30天之前的数据则去hive库
						if(days >= 2) {
							rkdDto = rmcKwDetsFromHive(distIdorSCName, dates, tddList, saasService, distName, 
									prefCity, province, ppName, rmcName, recComany, recPerson, distNames,dbHiveRecyclerWasteService);		
						}else {
							rkdDto = rmcKwDets(distIdorSCName, dates, tddList, db1Service, saasService, distName, 
									prefCity, province, ppName, rmcName, recComany, recPerson, distNames);		
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
					if(distIdorSCName == null)
						distIdorSCName = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
					// 团餐公司餐厨垃圾详情列表函数
					if(methodIndex == 1) {
						rkdDto = rmcKwDets(distIdorSCName, dates, tddList, db1Service, saasService, distName, 
							prefCity, province, ppName, rmcName, recComany, recPerson,distNames);
					}else if(methodIndex == 2) {
						DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
						DateTime currentTime = new DateTime();
						int days = Days.daysBetween(startDt, currentTime).getDays();
						
						//日期包含30天之前的数据则去hive库
						if(days >= 2) {
							rkdDto = rmcKwDetsFromHive(distIdorSCName, dates, tddList, saasService, distName, 
									prefCity, province, ppName, rmcName, recComany, recPerson,distNames,dbHiveRecyclerWasteService);
						}else {
							rkdDto = rmcKwDets(distIdorSCName, dates, tddList, db1Service, saasService, distName, 
									prefCity, province, ppName, rmcName, recComany, recPerson,distNames);
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
			rkdDto = SimuDataFunc();
		}

		return rkdDto;
	}
}
