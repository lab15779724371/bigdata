package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.GsAcceptSumInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.GsAcceptSumInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.MatSumInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsCommon;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 3.2.32.	配货验收汇总信息应用模型
 * @author fengyang_xie
 *
 */
public class GsAcceptSumInfoAppMod {
	private static final Logger logger = LogManager.getLogger(GsAcceptSumInfoAppMod.class.getName());
	
	/**
	 * Redis服务
	 */
	@Autowired
	RedisService redisService = new RedisService();
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 2;
	/**
	 * 是否为真实数据标识
	 */
	private static boolean isRealData = true;
	
	/**
	 * 数组数据初始化
	 */
	String tempData ="{\r\n" + 
			"   \"time\": \"2016-07-14 09:51:35\",\r\n" + 
			"   \"gsAcceptSumInfo\": \r\n" + 
			"{\r\n" + 
			"  \"dishSchNum\":500,\r\n" + 
			"\"conMatSchNum\":450,\r\n" + 
			"\"acceptSchNum\":400,\r\n" + 
			"\"noAcceptSchNum\":50,\r\n" + 
			"\"totalGsPlanNum\":3567,\r\n" + 
			"\"acceptGsNum\":3000,\r\n" + 
			"\"noAcceptGsNum\":567,\r\n" + 
			"\"acceptRate\":84.11\r\n" + 
			"},\r\n" + 
			"   \"msgId\":1\r\n" + 
			"}\r\n" + 
			"";
	
	/**
	 * 汇总数据
	 * @return
	 */
	private GsAcceptSumInfoDTO gsAcceptSumInfoFunc(String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService ) {
		
		GsAcceptSumInfoDTO gsAcceptSumInfoDTO = new GsAcceptSumInfoDTO();
		
		GsAcceptSumInfo gsAcceptSumInfo = new GsAcceptSumInfo();
		gsAcceptSumInfoDTO.setGsAcceptSumInfo(gsAcceptSumInfo);
		
		JSONObject jsStr = JSONObject.parseObject(tempData); 
		gsAcceptSumInfoDTO = (GsAcceptSumInfoDTO) JSONObject.toJavaObject(jsStr,GsAcceptSumInfoDTO.class);
		
		//时戳
		gsAcceptSumInfoDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		gsAcceptSumInfoDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gsAcceptSumInfoDTO;
	}
	
	/**
	 * 汇总数据(未验收学校、已验收学校由_DistributionDetail 改为_DistributionTotal)
	 * @return
	 */
	private GsAcceptSumInfoDTO gsAcceptSumInfoFuncOne(String departmentId,String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService ) {
		
        GsAcceptSumInfoDTO gsAcceptSumInfoDTO = new GsAcceptSumInfoDTO();
		
		GsAcceptSumInfo gsAcceptSumInfo = new GsAcceptSumInfo();
		
		//已排菜学校
		Integer  distDishSchNum =MatSumInfoAppMod.getDishInfo(departmentId,distId, dates, tedList,redisService);
		gsAcceptSumInfo.setDishSchNum(distDishSchNum);
		
		//		conMatSchNum	必选	INT	已确认用料学校
		MatSumInfo matSumInfo = new MatSumInfo();
		MatSumInfoAppMod.getAccDistrInfo(departmentId,distId, dates, tedList, matSumInfo,redisService);
		gsAcceptSumInfo.setConMatSchNum(matSumInfo.getConMatSchNum());
		
		//		totalGsPlanNum	必选	INT	配货计划总数
		//		acceptGsNum	必选	INT	已验收配货单
		//		noAcceptGsNum	必选	INT	未验收配货单
		//		acceptRate	必选	FLOAT	验收率，保留小数点有效数字两位
		getAccDistrInfo(departmentId,distId, dates, tedList, gsAcceptSumInfo);
		
		//		acceptSchNum	必选	INT	已验收学校
		//		noAcceptSchNum	必选	INT	未验收学校
		getAcceptNoAccSchuNum(distId, dates, gsAcceptSumInfo);
		
		gsAcceptSumInfoDTO.setGsAcceptSumInfo(gsAcceptSumInfo);
		
		//时戳
		gsAcceptSumInfoDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		gsAcceptSumInfoDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gsAcceptSumInfoDTO;
	}
	
	/**
	 * 汇总数据
	 * @return
	 */
	private GsAcceptSumInfoDTO gsAcceptSumInfoFuncTwo(String departmentId,String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService ,DbHiveGsService dbHiveGsService) {
		
        GsAcceptSumInfoDTO gsAcceptSumInfoDTO = new GsAcceptSumInfoDTO();
		
		GsAcceptSumInfo gsAcceptSumInfo = new GsAcceptSumInfo();
		
		//2019-08-01注释，注释原因：目前界面不需要展示排菜和用料信息，且排菜和用料改为读取hive库后速度变慢
		//已排菜学校
		//Integer  distDishSchNum =MatSumInfoAppMod.getDishInfo(distId, dates, tedList,redisService);
		//gsAcceptSumInfo.setDishSchNum(distDishSchNum);
		
		//		conMatSchNum	必选	INT	已确认用料学校
		//MatSumInfo matSumInfo = new MatSumInfo();
		//MatSumInfoAppMod.getAccDistrInfo(distId, dates, tedList, matSumInfo,redisService);
		//gsAcceptSumInfo.setConMatSchNum(matSumInfo.getConMatSchNum());
		
		//		totalGsPlanNum	必选	INT	配货计划总数
		//		acceptGsNum	必选	INT	已验收配货单
		//		noAcceptGsNum	必选	INT	未验收配货单
		//		acceptRate	必选	FLOAT	验收率，保留小数点有效数字两位
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		if(days >= 2) {
			getAccDistrInfoFromHive(departmentId,distId, dates, tedList, gsAcceptSumInfo, dbHiveGsService);
		}else {
			getAccDistrInfo(departmentId,distId, dates, tedList, gsAcceptSumInfo);
		}
		gsAcceptSumInfoDTO.setGsAcceptSumInfo(gsAcceptSumInfo);
		
		//时戳
		gsAcceptSumInfoDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		gsAcceptSumInfoDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return gsAcceptSumInfoDTO;
	}
	
	/**
	 * 获取配送信息：未验收配送单个数、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param gsAcceptSumInfo
	 */
	private void getAccDistrInfo(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			GsAcceptSumInfo gsAcceptSumInfo) {
		String key = "";
		String keyVal = "";
		String field = "";
		// 当天排菜学校总数
		Map<String, String> schIdToPlatoonMap = new HashMap<>();
		int dateCount = dates.length;
		int[]totalGsPlanNums = new int[dateCount];
		int []acceptGsPlanNums = new int[dateCount];
		int []noAcceptSchNums = new int[dateCount];
		int []acceptSchNums = new int[dateCount];
		int []conSchNums = new int[dateCount];
		
		
		float acceptRate = 0;
		int []noAcceptGsPlanNums = new int[dateCount];
		// 当天各区配货计划总数量
		for(int k = 0; k < dates.length; k++) {
			totalGsPlanNums[k] = 0;
			noAcceptGsPlanNums[k] = 0;
			acceptGsPlanNums[k] = 0;
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			schIdToPlatoonMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (schIdToPlatoonMap != null) {
				for (int i = 0; i < tedList.size(); i++) {
					TEduDistrictDo curTdd = tedList.get(i);
					String curDistId = curTdd.getId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(distId != null) {
						if(!curDistId.equalsIgnoreCase(distId)) {
							continue ;
						}
					}
					// 区域配货计划总数
					field = "area" + "_" + curDistId;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						totalGsPlanNums[k] += Integer.parseInt(keyVal);
						if(totalGsPlanNums[k] < 0) {
							totalGsPlanNums[k] = 0;
						}
					}
					// 未验收数
					for(int j = -2; j <= 2; j++) {
						field = "area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int noAcceptGsPlanNum = Integer.parseInt(keyVal);
							if(noAcceptGsPlanNum < 0) {
								noAcceptGsPlanNum = 0;
							}
							noAcceptGsPlanNums[k] += noAcceptGsPlanNum;
						}
					}
					
					// 已验收数
					field = "area" + "_" + curDistId + "_" + "status" + "_3";
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						acceptGsPlanNums[k] += Integer.parseInt(keyVal);
						if(acceptGsPlanNums[k] < 0) {
							acceptGsPlanNums[k] = 0;
						}
					}
					
					//已确认学校
					for(int j = 2; j < 4; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							conSchNums[k] += curConSchNum;
						}
					}
					
					//已验收学校
					field = "school-area" + "_" + curDistId + "_" + "status" + "_3";
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						int acceptSchNum = Integer.parseInt(keyVal);
						if(acceptSchNum < 0)
							acceptSchNum = 0;
						acceptSchNums[k] += acceptSchNum;
					}
					
					//未验收学校
					for(int j = -2; j <= 2; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							noAcceptSchNums[k] += curConSchNum;
						}
					}
				}
			}
		}
		
		int totalGsPlanNum = 0;
		int acceptGsPlanNum = 0;
		int noAcceptGsPlanNum = 0;
		int acceptSchNum = 0;
		int noAcceptSchNum = 0;
		for(int k = 0; k < dates.length; k++) {
			totalGsPlanNum += totalGsPlanNums[k];
			acceptGsPlanNum += acceptGsPlanNums[k];
			noAcceptGsPlanNum +=noAcceptGsPlanNums[k];
			acceptSchNum += acceptSchNums[k];
			noAcceptSchNum +=noAcceptSchNums[k];
		}
		//验收数量及验收率
		acceptRate = 0;
		if(totalGsPlanNum > 0) {
			acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
			BigDecimal bd = new BigDecimal(acceptRate);
			acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			/*if (acceptRate > 100) {
				acceptRate = 100;
			}*/
		}
		
		//验收率的计算改为：已验收的学校数量/应验收学校的数量*100%。应验收学校的数量=未验收学校数据+已验收学校的数量
		float schAcceptRate = 0;
		int totalSchNum = acceptSchNum + noAcceptSchNum;
		if(totalGsPlanNum > 0) {
			schAcceptRate = 100 * ((float) acceptSchNum / (float) totalSchNum);
			BigDecimal bd = new BigDecimal(schAcceptRate);
			schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (schAcceptRate > 100) {
				schAcceptRate = 100;
			}
		}
				
		
		/**
		 * 配货单总数
		 */
		gsAcceptSumInfo.setTotalGsPlanNum(totalGsPlanNum);
		/**
		 * 已验收配货单
		 */
		gsAcceptSumInfo.setAcceptGsNum(acceptGsPlanNum);
		
		/**
		 * 未验收配货单
		 */
		gsAcceptSumInfo.setNoAcceptGsNum(noAcceptGsPlanNum);
		
		/**
		 * 验收率，保留小数点有效数字两位
		 */
		gsAcceptSumInfo.setAcceptRate(acceptRate);
		
		/**
		 * 应验收学校
		 */
		gsAcceptSumInfo.setShouldAccSchNum(totalSchNum);
		
		/**
		 * 已验收学校
		 */
		gsAcceptSumInfo.setAcceptSchNum(acceptSchNum);
		
		/**
		 * 未验收学校
		 */
		gsAcceptSumInfo.setNoAcceptSchNum(noAcceptSchNum);
		
		/**
		 * 学校验收率
		 */
		gsAcceptSumInfo.setSchAcceptRate(schAcceptRate);
		
	}
	
	/**
	 * 获取配送信息：未验收配送单个数、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAccDistrInfoFromHive(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			GsAcceptSumInfo gsAcceptSumInfo,DbHiveGsService dbHiveGsService) {
		//配货单总数
		int totalGsPlanNum = 0;
		//已验收配货单
		int acceptGsPlanNum = 0;
		//未验收配货单
		int noAcceptGsPlanNum = 0;
		//已确认学校
		int acceptSchNum = 0;
		//未确认学校
		int noAcceptSchNum = 0;
		//验收率
		float acceptRate = 0;

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
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<SchGsCommon> dishList = new ArrayList<>();
		dishList = dbHiveGsService.getGsList(DataKeyConfig.talbeLedgerMasterTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,departmentId,null, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchGsCommon schDishCommon: dishList) {
				//区域为空，代表全市数据，此处去除
				if(CommonUtil.isEmpty(schDishCommon.getDistId())) {
					continue;
				}
				if(schDishCommon.getDisSealStatus() ==null || "-1".equals(schDishCommon.getDisSealStatus())) {
					if(schDishCommon.getHaulStatus() ==null) {
						totalGsPlanNum += schDishCommon.getTotal();
						
					}else if(schDishCommon.getHaulStatus() == 3) {
						//已验收
						acceptSchNum +=schDishCommon.getSchoolTotal();
						acceptGsPlanNum += schDishCommon.getTotal();
					}else {
						//未验收：haul_status = -4 or haul_status = -2 or haul_status = -1 or haul_status = 0 or haul_status = 1 or haul_status = 2
						noAcceptSchNum +=schDishCommon.getSchoolTotal();
						noAcceptGsPlanNum += schDishCommon.getTotal();
					}
				}
			}
		}
		
		//验收数量及验收率
		acceptRate = 0;
		if(totalGsPlanNum > 0) {
			acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
			BigDecimal bd = new BigDecimal(acceptRate);
			acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			/*if (acceptRate > 100) {
				acceptRate = 100;
			}*/
		}
		
		//验收率的计算改为：已验收的学校数量/应验收学校的数量*100%。应验收学校的数量=未验收学校数据+已验收学校的数量
		float schAcceptRate = 0;
		int totalSchNum = acceptSchNum + noAcceptSchNum;
		if(totalGsPlanNum > 0) {
			schAcceptRate = 100 * ((float) acceptSchNum / (float) totalSchNum);
			BigDecimal bd = new BigDecimal(schAcceptRate);
			schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (schAcceptRate > 100) {
				schAcceptRate = 100;
			}
		}
				
		
		/**
		 * 配货单总数
		 */
		gsAcceptSumInfo.setTotalGsPlanNum(totalGsPlanNum);
		/**
		 * 已验收配货单
		 */
		gsAcceptSumInfo.setAcceptGsNum(acceptGsPlanNum);
		
		/**
		 * 未验收配货单
		 */
		gsAcceptSumInfo.setNoAcceptGsNum(noAcceptGsPlanNum);
		
		/**
		 * 验收率，保留小数点有效数字两位
		 */
		gsAcceptSumInfo.setAcceptRate(acceptRate);
		
		/**
		 * 应验收学校
		 */
		gsAcceptSumInfo.setShouldAccSchNum(totalSchNum);
		
		/**
		 * 已验收学校
		 */
		gsAcceptSumInfo.setAcceptSchNum(acceptSchNum);
		
		/**
		 * 未验收学校
		 */
		gsAcceptSumInfo.setNoAcceptSchNum(noAcceptSchNum);
		
		/**
		 * 学校验收率
		 */
		gsAcceptSumInfo.setSchAcceptRate(schAcceptRate);
		
	}
	
	/**
	 * 获取配送信息：已验收学校、未验收学校
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNum(String distId, String[] dates,GsAcceptSumInfo gsAcceptSumInfo) {
		//未验收学校
		Set<String> noAcceptSchNumSet = new HashSet<String>();
		//已验收学校
		Set<String> acceptSchNumSet = new HashSet<String>();
		Map<String, String> distributionDetailMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		String[] keyVals = null;
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_Distribution-Detail";
			distributionDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionDetailMap != null) {
				for (String curKey : distributionDetailMap.keySet()) {
					keyVals = distributionDetailMap.get(curKey).split("_");
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKeys.length >= 14) {
						if(distId != null) {
							if(curKeys[7].compareTo(distId) != 0) {
								continue ;
							}
						}
						//如果value值为空或者value第一个值不是数字，则不做统计
						if(keyVals.length < 1) {
							continue;
						}
						int dispPlanStatus = Integer.parseInt(keyVals[0]);
						//去除已验收和已取消的订单
						if(dispPlanStatus!=3 && dispPlanStatus!=4) {
							noAcceptSchNumSet.add(curKeys[5]);
						}else if (dispPlanStatus==3) {
							acceptSchNumSet.add(curKeys[5]);
						}
					}
					else {
						logger.info("配货计划："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
		/**
		 * 已验收学校
		 */
		gsAcceptSumInfo.setAcceptSchNum(acceptSchNumSet.size());
		
		/**
		 * 未验收学校
		 */
		gsAcceptSumInfo.setNoAcceptSchNum(noAcceptSchNumSet.size());
		
		
	}
	
	/**
	 * 投诉举报详情模型函数
	 * @param token
	 * @param distName
	 * @param prefCity
	 * @param province
	 * @param startDate
	 * @param endDate
	 * @param db1Service
	 * @return
	 */
	public GsAcceptSumInfoDTO appModFunc(String token, String distName, String prefCity, String province,String startDate, String endDate, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService , DbHiveGsService dbHiveGsService) {
		
		GsAcceptSumInfoDTO gsAcceptSumInfoDTO = null;
		//真实数据
		if(isRealData) {       
			// 日期
			String[] dates = null;
			// 按照当天日期获取数据
			if (startDate == null || endDate == null) { 
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
			if(province == null) {
				province = "上海市";
			}
			// 参数查找标识
			boolean bfind = false;
			String distId = null;
			String departmentId = null;
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) { 
				// 按区域，省或直辖市处理
				List<TEduDistrictDo> tedList = db1Service.getListByDs1IdName();
				if(tedList != null) {
					// 查找是否存在该区域和省市
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						if (curTdd.getId().compareTo(distName) == 0) {
							bfind = true;
							distId = curTdd.getId();
							break;
						}
					}
				}
				// 存在则获取数据
				if (bfind) {
					if(departmentId == null) {
						//获取用户权限区域ID
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  
					}
					
					if(methodIndex==0) {
						// 餐厨垃圾学校回收列表函数
						gsAcceptSumInfoDTO = gsAcceptSumInfoFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						// 餐厨垃圾学校回收列表函数
						gsAcceptSumInfoDTO = gsAcceptSumInfoFuncOne(departmentId,distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==2) {
						// 餐厨垃圾学校回收列表函数
						gsAcceptSumInfoDTO = gsAcceptSumInfoFuncTwo(departmentId,distId, dates, tedList, db1Service, saasService,dbHiveGsService);
					}
				}
			} else if (distName == null && prefCity == null && province != null) { 
				// 按省或直辖市处理
				List<TEduDistrictDo> tedList = null;
				if (province.compareTo("上海市") == 0) {
					tedList = db1Service.getListByDs1IdName();
					if(tedList != null) {
						bfind = true;
					}
					distId = null;
				}
				if (bfind) {
					if(departmentId == null) {
						//获取用户权限区域ID
						departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  
					}
					if(methodIndex==0) {
						// 餐厨垃圾学校回收列表函数
						gsAcceptSumInfoDTO = gsAcceptSumInfoFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						// 餐厨垃圾学校回收列表函数
						gsAcceptSumInfoDTO = gsAcceptSumInfoFuncOne(departmentId,distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==2) {
						// 餐厨垃圾学校回收列表函数
						gsAcceptSumInfoDTO = gsAcceptSumInfoFuncTwo(departmentId,distId, dates, tedList, db1Service, saasService,dbHiveGsService);
					}
				}
			} else if (distName != null && prefCity != null && province != null) { 
				// 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { 
				// 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}			
		}
		else {    //模拟数据
			//模拟数据函数
			gsAcceptSumInfoDTO = new GsAcceptSumInfoDTO();
		}		

		
		return gsAcceptSumInfoDTO;
	}
}
