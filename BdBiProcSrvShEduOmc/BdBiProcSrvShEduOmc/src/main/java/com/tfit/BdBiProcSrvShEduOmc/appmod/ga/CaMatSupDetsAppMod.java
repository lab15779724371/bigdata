package com.tfit.BdBiProcSrvShEduOmc.appmod.ga;

import java.math.BigDecimal;
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
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.SortList;

//综合分析原料供应明细列表应用模型
public class CaMatSupDetsAppMod {
	private static final Logger logger = LogManager.getLogger(CaMatSupDetsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//二级排序条件
	final String[] methods = {"getDistName", "getSchType", "getDistrBatNumber"};
	final String[] sorts = {"asc", "asc", "asc"};
	final String[] dataTypes = {"String", "String", "String"};
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 2;
	
	//数组数据初始化
	String[] matUseDate_Array = {"2018/09/03", "2018/09/03"};
	String[] distrBatNumber_Array = {"2018090302055", "2018090302055"};
	String[] schName_Array = {"上海市天山中学", "上海市天山中学"};
	String[] distName_Array = {"长宁区", "长宁区"};	
	String[] detailAddr_Array = {"天山路1222号", "天山路1222号"};	
	String[] schType_Array = {"九年一贯制学校", "九年一贯制学校"};
	String[] schProp_Array = {"公办", "公办"};
	String[] optMode_Array = {"自营", "自营"};
	String[] dispType_Array = {"原料", "原料"};
	String[] rmcName_Array = {"上海绿捷", "上海绿捷"};
	String[] matName_Array = {"土鸡蛋", "土鸡蛋"};
	String[] standardName_Array = {"鸡蛋", "鸡蛋"};
	String[] matClassify_Array = {"蛋类产品", "蛋类产品"};
	String[] quantity_Array = {"100.00", "100.00"};
	String[] cvtRel_Array = {"1箱=5kg", "1箱=5kg"};
	String[] cvtQuantity_Array = {"20箱", "20箱"};
	String[] batNumber_Array = {"201810080025", "201810080025"};
	String[] prodDate_Array = {"20181005", "20181005"};
	String[] qaGuaPeriod_Array = {"90天", "90天"};
	String[] supplierName_Array = {"上海海天", "上海海天"};
	int[] acceptStatus_Array = {1, 1};
	String[] acceptNum_Array = {"20箱", "20箱"};
	float[] acceptRate_Array = {(float)100.00, (float)100.00};
	String[] gsBillPicUrl_Array = {"/caMatSupDets/test.jpg", "/caMatSupDets/test.jpg"};
	String[] qaCertPicUrl_Array = {"/caMatSupDets/test.jpg", "/caMatSupDets/test.jpg"};
	String[] acceptDate_Array = {"2018/09/03", "2018/09/03"};
	String[] acceptPerson_Array = {"admin", "admin"};
	
	//模拟数据函数
	private CaMatSupDetsDTO SimuDataFunc() {
		CaMatSupDetsDTO cmsdDto = new CaMatSupDetsDTO();
		//时戳
		cmsdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//综合分析原料供应明细列表模拟数据
		List<CaMatSupDets> caMatSupDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < matUseDate_Array.length; i++) {
			CaMatSupDets cmsd = new CaMatSupDets();			
			cmsd.setMatUseDate(matUseDate_Array[i]);
			cmsd.setDistrBatNumber(distrBatNumber_Array[i]);
			cmsd.setSchName(schName_Array[i]);
			cmsd.setDistName(distName_Array[i]);
			cmsd.setDetailAddr(detailAddr_Array[i]);
			cmsd.setSchType(schType_Array[i]);
			cmsd.setSchProp(schProp_Array[i]);
			cmsd.setOptMode(optMode_Array[i]);
			cmsd.setDispType(dispType_Array[i]);
			cmsd.setRmcName(rmcName_Array[i]);
			cmsd.setMatName(matName_Array[i]);
			cmsd.setStandardName(standardName_Array[i]);
			cmsd.setMatClassify(matClassify_Array[i]);
			cmsd.setQuantity(quantity_Array[i]);
			cmsd.setCvtRel(cvtRel_Array[i]);
			cmsd.setCvtQuantity(cvtQuantity_Array[i]);
			cmsd.setBatNumber(batNumber_Array[i]);
			cmsd.setProdDate(prodDate_Array[i]);
			cmsd.setQaGuaPeriod(qaGuaPeriod_Array[i]);
			cmsd.setSupplierName(supplierName_Array[i]);
			cmsd.setAcceptStatus(acceptStatus_Array[i]);
			cmsd.setAcceptNum(acceptNum_Array[i]);
			cmsd.setAcceptRate(acceptRate_Array[i]);
			cmsd.setGsBillPicUrl(SpringConfig.repfile_srvdn + gsBillPicUrl_Array[i]);
			cmsd.setQaCertPicUrl(SpringConfig.repfile_srvdn + qaCertPicUrl_Array[i]);
			cmsd.setAcceptDate(acceptDate_Array[i]);
			cmsd.setAcceptPerson(acceptPerson_Array[i]);
			caMatSupDets.add(cmsd);
		}		
		//设置数据
		cmsdDto.setCaMatSupDets(caMatSupDets);
		//设置分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = matUseDate_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		cmsdDto.setPageInfo(pageInfo);
		//消息ID
		cmsdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cmsdDto;
	}
	
	//  综合分析原料供应明细列表函数
	CaMatSupDetsDTO caMatSupDets(String distIdorSCName, String[] dates, List<TEduDistrictDo> tddList, 
		Db1Service db1Service, SaasService saasService, 
		String schName, String matName, String rmcName, String supplierName, 
		String distrBatNumber, int schType, int acceptStatus, int optMode) {
		CaMatSupDetsDTO cmsdDto = new CaMatSupDetsDTO();
		List<CaMatSupDets> caMatSupDets = new ArrayList<>();
		Map<String, String> material2SupllierDetailMap = new HashMap<>();
		int i, j, k, dateCount = dates.length;
		String key = null, keyVal = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		//查询变脸索引
		int[] queryVarIdxs = new int[9];
		for(i = 0; i < queryVarIdxs.length; i++)
			queryVarIdxs[i] = -1;
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
			key = dates[k] + "_Material2SupllierDetail";
			material2SupllierDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (material2SupllierDetailMap != null) {
				for (String curKey : material2SupllierDetailMap.keySet()) {
					keyVal = material2SupllierDetailMap.get(curKey);
					// 综合分析原料供应明细列表
					String[] keyVals = keyVal.split(";");
					if(keyVals.length >= 46) {
						CaMatSupDets cmsd = new CaMatSupDets();
						//学校信息（项目点）
						TEduSchoolDo tesDo = null;
						i = AppModConfig.getVarValIndex(keyVals, "schoolid");
						if(i != -1) {
							queryVarIdxs[0] = i;
							queryVarIdxs[6] = i;
							queryVarIdxs[8] = i;
							if(schIdMap.containsKey(keyVals[i])) {
								j = schIdMap.get(keyVals[i]);
								tesDo = tesDoList.get(j-1);
							}
						}
						if(tesDo == null)
							continue;
						//用料日期
						cmsd.setMatUseDate(dates[k].replaceAll("-", "/"));
						//配货批次号
						i = AppModConfig.getVarValIndex(keyVals, "batchno");
						queryVarIdxs[5] = i;
						if(i != -1)
							cmsd.setDistrBatNumber(keyVals[i]);
						//学校
						cmsd.setSchName(tesDo.getSchoolName());
						//区
						cmsd.setDistName("-");
						i = AppModConfig.getVarValIndex(keyVals, "area");
						if(i != -1) {
							queryVarIdxs[1] = i;
							if(!keyVals[i].equalsIgnoreCase("null"))
								cmsd.setDistName(keyVals[i]);
						}
						//详细地址
						cmsd.setDetailAddr(tesDo.getAddress());
						//学校学制
						cmsd.setSchType(AppModConfig.getSchType(tesDo.getLevel(), tesDo.getLevel2()));
						//学校性质
						cmsd.setSchProp(AppModConfig.getSchProp(tesDo.getSchoolNature()));
						//经营模式（供餐模式）
						cmsd.setOptMode(AppModConfig.getOptModeName(tesDo.getCanteenMode(), tesDo.getLedgerType(), tesDo.getLicenseMainType(), tesDo.getLicenseMainChild()));
						//配送类型
						i = AppModConfig.getVarValIndex(keyVals, "ledgertype");
						if(i != -1) {
							if(keyVals[i].equalsIgnoreCase("1"))
								cmsd.setDispType("原料");
							else if(keyVals[i].equalsIgnoreCase("2"))
								cmsd.setDispType("成品菜");
							else
								cmsd.setDispType("-");
						}
						//团餐公司
						i = AppModConfig.getVarValIndex(keyVals, "suppliername");
						if(i != -1) {
							queryVarIdxs[3] = i;
							if(!keyVals[i].equalsIgnoreCase("null")) 
								cmsd.setRmcName(keyVals[i]);
							else
								cmsd.setRmcName("-");
						}
						//物料名称
						i = AppModConfig.getVarValIndex(keyVals, "suppliermaterialname");
						if(i != -1) {
							queryVarIdxs[2] = i;
							if(!keyVals[i].equalsIgnoreCase("null")) 
								cmsd.setMatName(keyVals[i]);
							else
								cmsd.setMatName("-");
						}
						//标准名称
						i = AppModConfig.getVarValIndex(keyVals, "name");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null"))
								cmsd.setStandardName(keyVals[i]);
						}
						//原料类别
						i = AppModConfig.getVarValIndex(keyVals, "warestypename");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null"))
								cmsd.setMatClassify(keyVals[i]);
						}
						//数量
						cmsd.setQuantity("-");
						i = AppModConfig.getVarValIndex(keyVals, "actualquantity");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								float quantity = Float.parseFloat(keyVals[i]);
								BigDecimal bd = new BigDecimal(quantity);
								quantity = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
								cmsd.setQuantity(quantity + " " + keyVals[i+1]);
							}
						}
						//换算关系
						cmsd.setCvtRel("-");
						i = AppModConfig.getVarValIndex(keyVals, "huansuan");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null"))
								cmsd.setCvtRel(keyVals[i]);
						}
						//换算数量
						float cvtQuantity = 0;
						cmsd.setCvtQuantity(String.valueOf(cvtQuantity));
						i = AppModConfig.getVarValIndex(keyVals, "otherquantity");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								cvtQuantity = Float.parseFloat(keyVals[i]);
								BigDecimal bd = new BigDecimal(cvtQuantity);
								cvtQuantity = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
								cmsd.setCvtQuantity(cvtQuantity + " " + keyVals[i+1]);
							}
						}
						//批号
						cmsd.setBatNumber("-");
						i = AppModConfig.getVarValIndex(keyVals, "nob");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null"))
								cmsd.setBatNumber(keyVals[i]);
						}
						//生产日期
						cmsd.setProdDate("-");
						i = AppModConfig.getVarValIndex(keyVals, "productiondate");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null"))
								cmsd.setProdDate(keyVals[i]);
						}
						//保质期
						cmsd.setQaGuaPeriod("-");
						i = AppModConfig.getVarValIndex(keyVals, "shelflife");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null"))
								cmsd.setQaGuaPeriod(keyVals[i]);
						}
						//供应商
						cmsd.setSupplierName("-");
						i = AppModConfig.getVarValIndex(keyVals, "supplyname");
						if(i != -1) {
							queryVarIdxs[4] = i;
							if(!keyVals[i].equalsIgnoreCase("null"))
								cmsd.setSupplierName(keyVals[i]);
						}
						//是否验收
						int curAcceptStatus = 0;
						cmsd.setAcceptStatus(0);
						i = AppModConfig.getVarValIndex(keyVals, "status");
						if(i != -1) {
							queryVarIdxs[7] = i;
							if(!keyVals[i].equalsIgnoreCase("null")) {
								curAcceptStatus = Integer.parseInt(keyVals[i]);
								if(curAcceptStatus == 3)
									curAcceptStatus = 1;
								else
									curAcceptStatus = 0;
								cmsd.setAcceptStatus(curAcceptStatus);
							}
						}
						//验收数量
						float acceptNum = 0;
						cmsd.setAcceptNum(String.valueOf(acceptNum));
						i = AppModConfig.getVarValIndex(keyVals, "deliverynumber");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								acceptNum = Float.parseFloat(keyVals[i]);
								BigDecimal bd = new BigDecimal(acceptNum);
								acceptNum = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
								j = AppModConfig.getVarValIndex(keyVals, "otherquantity");
								if(j != -1)
									cmsd.setAcceptNum(String.valueOf(acceptNum) + " " + keyVals[j+1]);
							}
						}
						//验收比例
						float acceptRate = 0;
						if(cvtQuantity > 0) {
							acceptRate = 100*acceptNum/cvtQuantity;
							BigDecimal bd = new BigDecimal(acceptRate);
							acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
							/*if(acceptRate > 100)
								acceptRate = 100;*/
						}
						cmsd.setAcceptRate(acceptRate);
						//配货单图片
						cmsd.setGsBillPicUrl("-");
						i = AppModConfig.getVarValIndex(keyVals, "peiimage");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								String picSrvDn = SpringConfig.ss_picfile_srvdn + "/";
								cmsd.setGsBillPicUrl(picSrvDn + keyVals[i]);
							}
						}
						//检疫证图片
						cmsd.setQaCertPicUrl("-");
						i = AppModConfig.getVarValIndex(keyVals, "jianimage");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								String picSrvDn = SpringConfig.ss_picfile_srvdn + "/";
								cmsd.setQaCertPicUrl(picSrvDn + keyVals[i]);
							}
						}
						//验收日期
						cmsd.setAcceptDate("-");
						i = AppModConfig.getVarValIndex(keyVals, "deliverydate");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								String[] acceptDates = keyVals[i].split(" ");
								cmsd.setAcceptDate(acceptDates[0].replaceAll(" ", "/"));
							}
						}
						//条件判断
						boolean isAdd = true;
						int[] flIdxs = new int[9];
						//判断学校名称（判断索引0）
						if(schName != null && queryVarIdxs[0] != -1) {
							if(!keyVals[queryVarIdxs[0]].equalsIgnoreCase(schName))
								flIdxs[0] = -1;
						}
						//判断区域（判断索引1）
						if(distIdorSCName != null && queryVarIdxs[1] != -1) {
							if(!distIdorSCName.equalsIgnoreCase(keyVals[queryVarIdxs[1]]))
								flIdxs[1] = -1;
						}
						//判断物料名称（判断索引2）
						if(matName != null && queryVarIdxs[2] != -1) {
							if(!cmsd.getMatName().equalsIgnoreCase(matName))
								flIdxs[2] = -1;
						}
						//判断团餐公司（判断索引3）
						if(rmcName != null && RmcIdToNameMap != null && queryVarIdxs[3] != -1) {
							if(RmcIdToNameMap.containsKey(rmcName)) {
								if(!(cmsd.getRmcName().equalsIgnoreCase(RmcIdToNameMap.get(rmcName))))
									flIdxs[3] = -1;
							}
							else
								flIdxs[3] = -1;
						}
						//判断供应商（判断索引4）
						if(supplierName != null && queryVarIdxs[4] != -1) {
							if(!cmsd.getSupplierName().equalsIgnoreCase(supplierName))
								flIdxs[4] = -1;
						}
						//判断配货批次号（判断索引5）
						if(distrBatNumber != null && queryVarIdxs[5] != -1) {
							if(cmsd.getDistrBatNumber().indexOf(distrBatNumber) == -1)
								flIdxs[5] = -1;
						}
						//判断学校类型（学制）（判断索引6）
						if(schType != -1 && queryVarIdxs[6] != -1) {
							if(AppModConfig.schTypeNameToIdMap.containsKey(cmsd.getSchType())) {
								int curSchType = AppModConfig.schTypeNameToIdMap.get(cmsd.getSchType());
								if(curSchType != schType)
									flIdxs[6] = -1;
							}
							else
								flIdxs[6] = -1;
						}
						//判断是否验收（判断索引7）
						if(acceptStatus != -1 && queryVarIdxs[7] != -1) {
							if(cmsd.getAcceptStatus() != acceptStatus)
								flIdxs[7] = -1;
						}
						//判断经营模型（供餐模式）（判断索引8）
						if(optMode != -1 && queryVarIdxs[8] != -1) {
							if(cmsd.getOptMode() != null) {
								if(AppModConfig.optModeNameToIdMap.containsKey(cmsd.getOptMode())) {
									int curOptMode = AppModConfig.optModeNameToIdMap.get(cmsd.getOptMode());
									if(curOptMode != optMode)
										flIdxs[8] = -1;
								}
								else
									flIdxs[8] = -1;
							}
							else
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
							caMatSupDets.add(cmsd);
					}
					else
						logger.info("学校餐厨垃圾："+ curKey + "，格式错误！");
				}
			}
		}
		//排序
    	SortList<CaMatSupDets> sortList = new SortList<CaMatSupDets>();  
    	sortList.Sort3Level(caMatSupDets, methods, sorts, dataTypes);
		//时戳
		cmsdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageBean<CaMatSupDets> pageBean = new PageBean<CaMatSupDets>(caMatSupDets, curPageNum, pageSize);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageBean.getTotalCount());
		pageInfo.setCurPageNum(curPageNum);
		cmsdDto.setPageInfo(pageInfo);
		//设置数据
		cmsdDto.setCaMatSupDets(pageBean.getCurPageData());
		//消息ID
		cmsdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cmsdDto;
	}	
	
	//  综合分析原料供应明细列表函数
	CaMatSupDetsDTO caMatSupDetsTwo(String distIdorSCName,String startDate, String endDate,List<TEduDistrictDo> tddList, 
			Db1Service db1Service, SaasService saasService,DbHiveService dbHiveService, 
			String schName, String matName,String stdMatName, String rmcName, String supplierName, 
			String distrBatNumber, int schType, int acceptStatus, int optMode) {
		CaMatSupDetsDTO cmsdDto = new CaMatSupDetsDTO();
		
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
		
		List<CaMatSupDets> caMatSupDets = new ArrayList<>();
		//分页总数
		try {
			//获取列表  
			caMatSupDets = dbHiveService.getCaMatSupDetsList(listYearMonth, startDate, endDateAddOne, schName, 
					distIdorSCName,null, matName,stdMatName, rmcName, supplierName, distrBatNumber, schType, acceptStatus, optMode, 
					(curPageNum-1)*pageSize, curPageNum*pageSize, schoolPropertyMap,supplierName,null);
			
			//移除所有未null的值
			caMatSupDets.removeAll(Collections.singleton(null));
			
			Integer pageTotalTemp = dbHiveService.getCaMatSupDetsCount(listYearMonth, startDate, endDateAddOne,
					schName, distIdorSCName, null,matName,stdMatName, rmcName, supplierName, distrBatNumber, schType, 
					acceptStatus, optMode,supplierName,null);
			logger.info("行数01********************************"+pageTotalTemp);
			if(pageTotalTemp!=null) {
				pageTotal = pageTotalTemp;
			}
		}catch(Exception e) {
			pageTotal = 1;
			logger.info("行数catch********************************"+e.getMessage());
		}
				
		//时戳
		cmsdDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		cmsdDto.setPageInfo(pageInfo);
		//设置数据
		cmsdDto.setCaMatSupDets(caMatSupDets);
		//消息ID
		cmsdDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return cmsdDto;
	}	
	
	// 综合分析原料供应明细列表模型函数
	public CaMatSupDetsDTO appModFunc(String token, String startUseDate, String endUseDate, String schName, 
			String distName, String prefCity, String province, String matName,String stdMatName, String rmcName, 
			String supplierName, String distrBatNumber, String schType, String acceptStatus, 
			String optMode, 
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,DbHiveService dbHiveService) {
		CaMatSupDetsDTO cmsdDto = null;
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
			//验收状态，0:待验收，1:已验收
			int curAcceptStatus = -1;
			if(acceptStatus != null)
				curAcceptStatus = Integer.parseInt(acceptStatus);
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
					// 综合分析原料供应明细列表函数
					if(methodIndex == 1) {
						cmsdDto = caMatSupDets(distIdorSCName, dates, tddList, db1Service, saasService, schName, matName, 
								rmcName, supplierName, distrBatNumber, curSchType, curAcceptStatus, curOptMode);	
					}else if (methodIndex == 2) {
						cmsdDto = caMatSupDetsTwo(distIdorSCName, startUseDate,endUseDate, tddList, db1Service, saasService,dbHiveService, schName, matName,stdMatName, 
								rmcName, supplierName, distrBatNumber, curSchType, curAcceptStatus, curOptMode);	
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
					// 综合分析原料供应明细列表函数
					if(methodIndex == 1) {
						cmsdDto = caMatSupDets(distIdorSCName, dates, tddList, db1Service, saasService, schName, 
								matName, rmcName, supplierName, distrBatNumber, curSchType, curAcceptStatus, curOptMode);
					}else if (methodIndex == 2) {
						cmsdDto = caMatSupDetsTwo(distIdorSCName, startUseDate,endUseDate,tddList, db1Service, 
								saasService,dbHiveService, schName, 
								matName,stdMatName, rmcName, supplierName, distrBatNumber, curSchType, curAcceptStatus, curOptMode);
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
			cmsdDto = SimuDataFunc();
		}		

		return cmsdDto;
	}
}
