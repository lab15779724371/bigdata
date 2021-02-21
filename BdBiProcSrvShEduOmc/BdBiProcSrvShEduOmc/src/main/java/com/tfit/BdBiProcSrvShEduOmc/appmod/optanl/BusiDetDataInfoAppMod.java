package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.EduSchool;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonRecs;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLics;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.BusiDetDataInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.BusiDetDataInfoDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishSitStat;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsCommon;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 3.2.2.	业务明细数据信息应用模型
 * @author Administrator
 *
 */
public class BusiDetDataInfoAppMod {
	private static final Logger logger = LogManager.getLogger(BusiDetDataInfoAppMod.class.getName());
	
	/**
	 * Redis服务
	 */
	@Autowired
	RedisService redisService = new RedisService();

	/**
	 * 是否为真实数据标识
	 */
	private static boolean isRealData = true;
	/**
	 * 页号、页大小和总页数
	 */
	int curPageNum = 1;
	int pageTotal = 1;
	int pageSize = 20;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 0;
	
	/**
	 * 数组数据初始化
	 */
	String tempData ="{\r\n" + 
			"   \"time\": \"2016-07-14 09:51:35\",\r\n" + 
			"   \"busiDetDataInfo\": [\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":\"二环\",\r\n" + 
			"  \"statPropName\":\"浦东新区\",\r\n" + 
			"   \"totalSchNum\":952,\r\n" + 
			"   \"noDishSchNum\":20,\r\n" + 
			"   \"mealSchNum\":922,\r\n" + 
			"   \"noDishDayNum\":30,\r\n" + 
			"   \"dishRate\":95.81,\r\n" + 
			"   \"noAcceptSchNum\":20,\r\n" + 
			"   \"gsPlanNum\":100,\r\n" + 
			"   \"noAcceptPlanNum\":30,\r\n" + 
			"   \"acceptRate\":95.82,\r\n" + 
			"   \"noRsSchNum\":20,\r\n" + 
			"   \"dishNum\":1000,\r\n" + 
			"   \"noRsDishNum\":30,\r\n" + 
			"   \"rsRate\":95.81,\r\n" + 
			"   \"noProcUnitNum\":20,\r\n" + 
			"   \"warnNum\":120,\r\n" + 
			"   \"noProcWarnNum\":30,\r\n" + 
			"   \"procRate\":95.85,\r\n" + 
			"   \"kwSchRecNum\":20,\r\n" + 
			"   \"kwRmcRecNum\":30,\r\n" + 
			"   \"kwTotalRecNum\":50,\r\n" + 
			"   \"woSchRecNum\":20,\r\n" + 
			"   \"woRmcRecNum\":20,\r\n" + 
			"   \"woTotalRecNum\":40\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":\"一环\",\r\n" + 
			"  \"statPropName\":\"黄浦区\",\r\n" + 
			"   \"totalSchNum\":952,\r\n" + 
			"   \"noDishSchNum\":20,\r\n" + 
			"   \"mealSchNum\":922,\r\n" + 
			"   \"noDishDayNum\":30,\r\n" + 
			"   \"dishRate\":95.81,\r\n" + 
			"   \"noAcceptSchNum\":20,\r\n" + 
			"   \"gsPlanNum\":100,\r\n" + 
			"   \"noAcceptPlanNum\":30,\r\n" + 
			"   \"acceptRate\":95.82,\r\n" + 
			"   \"noRsSchNum\":20,\r\n" + 
			"   \"dishNum\":1000,\r\n" + 
			"   \"noRsDishNum\":30,\r\n" + 
			"   \"rsRate\":95.81,\r\n" + 
			"   \"noProcUnitNum\":20,\r\n" + 
			"   \"warnNum\":120,\r\n" + 
			"   \"noProcWarnNum\":30,\r\n" + 
			"   \"procRate\":95.85,\r\n" + 
			"   \"kwSchRecNum\":20,\r\n" + 
			"   \"kwRmcRecNum\":30,\r\n" + 
			"   \"kwTotalRecNum\":50,\r\n" + 
			"   \"woSchRecNum\":20,\r\n" + 
			"   \"woRmcRecNum\":20,\r\n" + 
			"   \"woTotalRecNum\":40 },\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":\"一环\",\r\n" + 
			"\"statPropName\":\"静安区\",\r\n" + 
			"   \"totalSchNum\":952,\r\n" + 
			"   \"noDishSchNum\":20,\r\n" + 
			"   \"mealSchNum\":922,\r\n" + 
			"   \"noDishDayNum\":30,\r\n" + 
			"   \"dishRate\":95.81,\r\n" + 
			"   \"noAcceptSchNum\":20,\r\n" + 
			"   \"gsPlanNum\":100,\r\n" + 
			"   \"noAcceptPlanNum\":30,\r\n" + 
			"   \"acceptRate\":95.82,\r\n" + 
			"   \"noRsSchNum\":20,\r\n" + 
			"   \"dishNum\":1000,\r\n" + 
			"   \"noRsDishNum\":30,\r\n" + 
			"   \"rsRate\":95.81,\r\n" + 
			"   \"noProcUnitNum\":20,\r\n" + 
			"   \"warnNum\":120,\r\n" + 
			"   \"noProcWarnNum\":30,\r\n" + 
			"   \"procRate\":95.85,\r\n" + 
			"   \"kwSchRecNum\":20,\r\n" + 
			"   \"kwRmcRecNum\":30,\r\n" + 
			"   \"kwTotalRecNum\":50,\r\n" + 
			"   \"woSchRecNum\":20,\r\n" + 
			"   \"woRmcRecNum\":20,\r\n" + 
			"   \"woTotalRecNum\":40\r\n" + 
			" } ],\r\n" + 
			"   \"msgId\":1\r\n" + 
			"}\r\n" + 
			"";
	
	/**
	 * 
	 * @param distId 区域编号
	 * @param currDistName 当前区域名称
	 * @param statMode 统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
	 * @param dates 统计时间
	 * @param tedList 区域列表
	 * @param db1Service 
	 * @param saasService
	 * @return
	 */
	private BusiDetDataInfoDTO busiDetDataInfoFunc(String departmentId,String distId,String currDistName,Integer statMode,
			Integer subLevel,Integer compDep, String[] dates,Integer target,List<TEduDistrictDo> tedList,
			Db1Service db1Service,SaasService saasService,EduSchoolService eduSchoolService,DbHiveWarnService dbHiveWarnService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService,DbHiveDishService dbHiveDishService,
			DbHiveGsService dbHiveGsService) {
		
		BusiDetDataInfoDTO busiDetDataInfo= new BusiDetDataInfoDTO();
		List<BusiDetDataInfo> busiDetDataInfoList = new ArrayList<BusiDetDataInfo>();
		
		Map<String,BusiDetDataInfo> busiDetDataInfoMap= new LinkedHashMap<String,BusiDetDataInfo>();
		
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		
		if(statMode == 0) {
			//========0:按区统计
			
			/**
			 * 监管学校数量
			 */
			getSupSchNumByArea(departmentId,distId,currDistName,tedList,busiDetDataInfoMap);
			
			if(days >= 2) {
				//学校总数、排菜汇总（一天前数据，此处会覆盖学校总数）
				getDishInfoByAreaFromHive(departmentId,distId, dates, tedList, busiDetDataInfoMap, dbHiveDishService);
			}else {
				/**
				 * 排菜汇总
				 */
				getDishInfoByArea(departmentId,distId, dates, tedList,busiDetDataInfoMap);
			}
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收配送单个数、验收率
			if(days >= 2) {
				getAccDistrInfoByAreaFromHive(departmentId,distId, dates, tedList, busiDetDataInfoMap, dbHiveGsService);
			}else {
				getAccDistrInfoByArea(departmentId,distId, dates, tedList,busiDetDataInfoMap);
			}
			
			//2019-03-27注释原因：改为从Total里获取
			//获取配送信息：未验收学校
			//getAcceptNoAccSchuNumByArea(distId, dates, tedList, busiDetDataInfoMap);
			
			/**
			 * 留样汇总
			 */
			 //获取留样汇总：留样率、未留样菜品个数
			 getRsInfoRsRateByArea(departmentId,distId, dates, tedList,busiDetDataInfoMap);
			 
			//2019.03.28 注释原因：建模规则修改由detail改为total中获取
			 //获取留样汇总：未留样学校个数
			 //getRsInfoNoRsSchNumByArea(distId, dates, tedList,db1Service,busiDetDataInfoMap);
			
			/**
			 * 预警汇总
			 */
			 //2019.06.06注释，注释原因：预警信息改为从hive库中获取
			//获取预警汇总：未处理预警、处理率
			//getWarnInfoWarnProcRateByArea(distId, dates, tedList,busiDetDataInfoMap);
			//获取预警汇总：未处理单位
			//getWarnInfoNoProcUnitNumByArea(distId, dates,tedList, saasService,busiDetDataInfoMap);
			getWarnInfoWarnProcRateByAreaTwo(departmentId,null, dates, target, tedList, busiDetDataInfoMap, dbHiveWarnService);
			
			//30天之前的数据获取hive库，30天以内以及今天以后的数据获取redis数据
			if(days >= 2) {
				getRecInfoByAreaFromHive(departmentId, dates, tedList, dbHiveRecyclerWasteService, busiDetDataInfoMap);
			}else {
				/**
				 * 餐厨垃圾
				 */
				//获取学校回收垃圾数
				String keySuffix ="_schoolwastetotal";
				getRecInfoByArea(departmentId, dates, tedList, keySuffix,busiDetDataInfoMap);
				//获取团餐公司回收垃圾数
				keySuffix ="_supplierwastetotal";
				getRecInfoByArea(departmentId, dates, tedList, keySuffix,busiDetDataInfoMap);
				
				/**
				 * 排废弃油脂
				 */
				//获取学校回收废弃油脂数
			    keySuffix ="_schooloiltotal";
			    getRecInfoByArea(departmentId, dates, tedList, keySuffix,busiDetDataInfoMap);
				//获取团餐公司废弃油脂数
				keySuffix ="_supplieroiltotal";
				getRecInfoByArea(departmentId, dates, tedList, keySuffix,busiDetDataInfoMap);
			}
		}else if(statMode == 1) {
			//========1:按学校性质统计
			Map<Integer, String> schoolPropertyMap = new HashMap<Integer,String>();
			schoolPropertyMap.put(0, "公办");
			schoolPropertyMap.put(2, "民办");
			schoolPropertyMap.put(3, "外籍人员子女学校");
			schoolPropertyMap.put(4, "其他");
			
			/**
			 * 监管学校数量
			 */
			getSupSchNumByNature(departmentId,distId,currDistName,schoolPropertyMap,busiDetDataInfoMap);
			
			if(days >= 2) {
				//学校总数、排菜汇总（一天前数据，此处会覆盖学校总数）
				getDishInfoByNatureFromHive(departmentId,distId, dates, schoolPropertyMap,busiDetDataInfoMap,dbHiveDishService);
			}else {
				
				/**
				 * 排菜汇总
				 */
				getDishInfoByNature(departmentId,distId, dates, schoolPropertyMap,busiDetDataInfoMap);
				
			}
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收学校、未验收配送单个数、验收率
			//getAcceptNoAccSchuNumByNature(distId, dates, schoolPropertyMap, busiDetDataInfoMap,eduSchoolService);
			//2019-04-01 更改取数规则：改为新的建模数据
			if(days >= 2) {
				getAcceptNoAccSchuNumByNatureFromHive(departmentId,distId, dates, schoolPropertyMap, busiDetDataInfoMap, eduSchoolService, dbHiveGsService);
			}else {
				getAcceptNoAccSchuNumByNatureTwo(departmentId,distId, dates, schoolPropertyMap, busiDetDataInfoMap, eduSchoolService);
			}
			/**
			 * 留样汇总
			 */
			 //获取留样汇总：未留样学校个数、留样率、未留样菜品个数、留样率
			 //getRsInfoNoRsSchNumByNature(distId, dates, schoolPropertyMap,db1Service,busiDetDataInfoMap,eduSchoolService);
			 //2019-04-01 更改取数规则：改为新的建模数据
			 getRsInfoNoRsSchNumByNatureTwo(departmentId,distId, dates, schoolPropertyMap, db1Service, busiDetDataInfoMap, eduSchoolService);
			
			/**
			 * 预警汇总
			 */
			//获取预警汇总：未处理单位、未处理预警、处理率
			//2019.06.10注释，注释原因：预警信息改为从hive库中获取
			//getWarnInfoNoProcUnitNumByNature(distId, dates,schoolPropertyMap, saasService,busiDetDataInfoMap,eduSchoolService);
			getWarnInfoNoProcUnitNumByNatureTwo(departmentId, target, dates, schoolPropertyMap, busiDetDataInfoMap,dbHiveWarnService);
			if(days >= 2) {
				getRecInfoByNatureFromHive(departmentId, dates, schoolPropertyMap, busiDetDataInfoMap, dbHiveRecyclerWasteService);
			}else {
				/**
				 * 餐厨垃圾
				 */
				//获取学校回收垃圾数
				String keySuffix ="_schoolwaste";
				getRecInfoByNature(departmentId, dates, schoolPropertyMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
				//获取团餐公司回收垃圾数[注释原因：按照学校统计，不统计团餐公司数据]
				//keySuffix ="_supplierwaste";
				//getRecInfoByNature(distId, dates, schoolPropertyMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
				
				/**
				 * 排废弃油脂
				 */
				//获取学校回收废弃油脂数
			    keySuffix ="_schooloil";
			    getRecInfoByNature(departmentId, dates, schoolPropertyMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
				//获取团餐公司废弃油脂数[注释原因：按照学校统计，不统计团餐公司数据]
				//keySuffix ="_supplieroil";
				//getRecInfoByNature(distId, dates, schoolPropertyMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
			}
		}else if (statMode == 2) {
			//========2:按学校学制统计
			/**
			 * 监管学校数量
			 */
			getSupSchNumBySchoolType(departmentId,distId,currDistName, AppModConfig.schTypeIdToNameMap,busiDetDataInfoMap);
			if(days >= 2) {
				//学校总数、排菜汇总（一天前数据，此处会覆盖学校总数）
				getDishInfoBySchoolTypeFromHive(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap, busiDetDataInfoMap, dbHiveDishService);
			}else {
				/**
				 * 排菜汇总
				 */
				getDishInfoBySchoolType(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap,busiDetDataInfoMap);
			}
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收学校、未验收配送单个数、验收率
			//getAcceptNoAccSchuNumBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap, busiDetDataInfoMap,eduSchoolService);
			//2019-04-01 更改取数规则：改为新的建模数据
			if(days >= 2) {
				getAcceptNoAccSchuNumBySchoolTypeFromHive(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap, busiDetDataInfoMap, eduSchoolService, dbHiveGsService);
			}else {
				getAcceptNoAccSchuNumBySchoolTypeTwo(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap, busiDetDataInfoMap,eduSchoolService);
			}
			
			/**
			 * 留样汇总
			 */
			 //获取留样汇总：未留样学校个数、留样率、未留样菜品个数、留样率
			 //getRsInfoNoRsSchNumBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap,db1Service,busiDetDataInfoMap,eduSchoolService);
			 //2019-04-01 更改取数规则：改为新的建模数据
			 getRsInfoNoRsSchNumBySchoolTypeTwo(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap, db1Service, busiDetDataInfoMap, eduSchoolService);
			 
			 /**
			 * 预警汇总
			 */
			//获取预警汇总：未处理单位、未处理预警、处理率
			 //2019.06.10注释 ，注释原因：改为从hive库中获取
			//getWarnInfoNoProcUnitNumBySchoolType(distId, dates,AppModConfig.schTypeIdToNameMap, saasService,busiDetDataInfoMap,eduSchoolService);
			getWarnInfoNoProcUnitNumBySchoolTypeTwo(departmentId, dates, target, AppModConfig.schTypeIdToNameMap, busiDetDataInfoMap, dbHiveWarnService);
			 
			 if(days >= 2) {
				 getRecInfoBySchoolTypeFromHive(departmentId, dates, AppModConfig.schTypeIdToNameMap, busiDetDataInfoMap, saasService, eduSchoolService, dbHiveRecyclerWasteService);
			 }else {
				/**
				 * 餐厨垃圾
				 */
				//获取学校回收垃圾数
				String keySuffix ="_schoolwaste";
				getRecInfoBySchoolType(departmentId, dates, AppModConfig.schTypeIdToNameMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
				//获取团餐公司回收垃圾数[注释原因：按照学校统计，不统计团餐公司数据]
				//keySuffix ="_supplierwaste";
				//getRecInfoBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
				
				/**
				 * 排废弃油脂
				 */
				//获取学校回收废弃油脂数
			    keySuffix ="_schooloil";
			    getRecInfoBySchoolType(departmentId, dates, AppModConfig.schTypeIdToNameMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
				//获取团餐公司废弃油脂数[注释原因：按照学校统计，不统计团餐公司数据]
				//keySuffix ="_supplieroil";
				//getRecInfoBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
			 }
			//计算每种【学制分类】的数量
			Map<String,BusiDetDataInfo> newBusiDetDataInfoMap= new LinkedHashMap<String,BusiDetDataInfo>();
			String statClassName="";
			BusiDetDataInfo busiDetDataInfoSum = new BusiDetDataInfo();
			for(Map.Entry<String,BusiDetDataInfo> entry : busiDetDataInfoMap.entrySet()) {
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newBusiDetDataInfoMap.put(statClassName+"_小计", busiDetDataInfoSum);
					busiDetDataInfoSum = new BusiDetDataInfo();
				}
				//计算数据
				setSchoolPropertSumData(busiDetDataInfoSum, entry);
				statClassName = entry.getValue().getStatClassName();
				busiDetDataInfoSum.setStatClassName(statClassName);
				busiDetDataInfoSum.setStatPropName("小计");
				setRate(busiDetDataInfoSum);
				
				newBusiDetDataInfoMap.put(entry.getKey(), entry.getValue());
				
			}
			newBusiDetDataInfoMap.put("其他"+"_小计", busiDetDataInfoSum);
			busiDetDataInfoMap = newBusiDetDataInfoMap;
			
		}else if (statMode == 3) {
			
			//========3:按所属主管部门统计
			
			
		    //初始化区属主管部门3名称与ID映射Map（内部数据库映射）
		  	Map<String, String> compDepNameToSubLevelNameMap = new LinkedHashMap<String, String>(){{
		  		put("3_黄浦区教育局", "区属");
		  		put("3_静安区教育局", "区属");
		  		put("3_徐汇区教育局", "区属");
		  		put("3_长宁区教育局", "区属");
		  		put("3_普陀区教育局", "区属");
		  		put("3_虹口区教育局", "区属");
		  		put("3_杨浦区教育局", "区属");
		  		put("3_闵行区教育局", "区属");
		  		put("3_嘉定区教育局", "区属");
		  		put("3_宝山区教育局", "区属");
		  		put("3_浦东新区教育局", "区属");
		  		put("3_松江区教育局", "区属");
		  		put("3_金山区教育局", "区属");
		  		put("3_青浦区教育局", "区属");
		  		put("3_奉贤区教育局", "区属");
		  		put("3_崇明区教育局", "区属");  
		  		put("3_其他", "区属");//为3_null归为q
		  		put("2_7", "市属");//7  市教委
		  		put("2_6", "市属");//6  市经信委
		  		put("2_5", "市属");//5  市商务委
		  		put("2_4", "市属");//4  市科委
		  		put("2_3", "市属");//3  市交通委
		  		put("2_2", "市属");//2  市农委
		  		put("2_1", "市属");//1 市水务局（海洋局）
		  		put("2_0", "市属");//0 其他
		  		put("1_1", "部属");//1_教育部
		  		put("1_0", "部属");//1_其他
		  		put("0_0", "其他");//0_其他
		    }};
		    
		    Map<String, String> newCompDepNameToSubLevelNameMap = new LinkedHashMap<String, String>(){{
		  		put("3_黄浦区教育局", "区属");
		  		put("3_静安区教育局", "区属");
		  		put("3_徐汇区教育局", "区属");
		  		put("3_长宁区教育局", "区属");
		  		put("3_普陀区教育局", "区属");
		  		put("3_虹口区教育局", "区属");
		  		put("3_杨浦区教育局", "区属");
		  		put("3_闵行区教育局", "区属");
		  		put("3_嘉定区教育局", "区属");
		  		put("3_宝山区教育局", "区属");
		  		put("3_浦东新区教育局", "区属");
		  		put("3_松江区教育局", "区属");
		  		put("3_金山区教育局", "区属");
		  		put("3_青浦区教育局", "区属");
		  		put("3_奉贤区教育局", "区属");
		  		put("3_崇明区教育局", "区属");  
		  		put("3_其他", "区属");//为3_null归为q
		  		put("2_7", "市属");//7  市教委
		  		put("2_6", "市属");//6  市经信委
		  		put("2_5", "市属");//5  市商务委
		  		put("2_4", "市属");//4  市科委
		  		put("2_3", "市属");//3  市交通委
		  		put("2_2", "市属");//2  市农委
		  		put("2_1", "市属");//1 市水务局（海洋局）
		  		put("2_0", "市属");//0 其他
		  		put("1_1", "部属");//1_教育部
		  		put("1_0", "部属");//1_其他
		  		put("0_0", "其他");//0_其他
		    }};
		    //过滤所属部门(根据登录用户获取管辖部门)
		    //Integer subLevel,//所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
			//Integer compDep//主管部门
		    if((subLevel!=null && subLevel>=0)  || (compDep!=null && compDep>=0)) {
		    	for(Map.Entry<String, String> entry:compDepNameToSubLevelNameMap.entrySet()) {
		    		String [] keys = entry.getKey().split("_");
		    		if(subLevel!=null && subLevel>=0 && !keys[0].equals(subLevel.toString())) {
		    			newCompDepNameToSubLevelNameMap.remove(entry.getKey());
		    		}
		    		
		    		if(compDep!=null && compDep>=0) {
		    			if(subLevel!=null && subLevel==3 && "区属".equals(entry.getValue())) {
		    				if(!compDep.toString().equals(AppModConfig.compDepNameToIdMap3.get(keys[1]))) {
		    					newCompDepNameToSubLevelNameMap.remove(entry.getKey());
		    				}
		    			}else {
		    				if(!keys[1].equals(compDep.toString())) {
		    					newCompDepNameToSubLevelNameMap.remove(entry.getKey());
		    				}
		    			}
		    			
		    			
		    		}
		    	}
		    	
		    	compDepNameToSubLevelNameMap = newCompDepNameToSubLevelNameMap;
		    }
		    
			/**
			 * 监管学校数量
			 */
		    getSupSchNumBySchoolSlave(departmentId,distId,currDistName, compDepNameToSubLevelNameMap,busiDetDataInfoMap);
		    
		    if(days >= 2) {
		    	//学校总数、排菜汇总（一天前数据，此处会覆盖学校总数）
		    	getDishInfoBySlaveFromHive(departmentId,distId, dates, compDepNameToSubLevelNameMap, busiDetDataInfoMap, dbHiveDishService);
			}else {
				/**
				 * 排菜汇总
				 */
				getDishInfoBySlave(departmentId,distId, dates, compDepNameToSubLevelNameMap,busiDetDataInfoMap);
			}
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收学校、未验收配送单个数、验收率
		    if(days >= 2) {
		    	getAcceptNoAccSchuNumBySlaveFromHive(departmentId,distId, dates, compDepNameToSubLevelNameMap, busiDetDataInfoMap, eduSchoolService, dbHiveGsService);
		    }else {
		    	getAcceptNoAccSchuNumBySlave(departmentId,distId, dates, compDepNameToSubLevelNameMap, busiDetDataInfoMap,eduSchoolService);
		    }
			
			/**
			 * 留样汇总
			 */
			 //获取留样汇总：未留样学校个数、留样率、未留样菜品个数、留样率
			 getRsInfoNoRsSchNumBySlave(departmentId,distId, dates, compDepNameToSubLevelNameMap,db1Service,busiDetDataInfoMap,eduSchoolService);
			 
			 /**
			 * 预警汇总
			 */
			//2019.06.10注释,注释原因：预警改为从hive中获取
			//获取预警汇总：未处理预警、处理率
			//getWarnInfoWarnProcRateBySlave(distId, dates, compDepNameToSubLevelNameMap,busiDetDataInfoMap);
			//获取预警汇总：未处理单位
			//getWarnInfoNoProcUnitNumBySlave(distId, dates, compDepNameToSubLevelNameMap, saasService, busiDetDataInfoMap, eduSchoolService);
			 
			 //从hive中获取预警信息
			getWarnInfoWarnProcRateBySlaveTwo(departmentId, dates, target, compDepNameToSubLevelNameMap, busiDetDataInfoMap, dbHiveWarnService);
			
			 if(days >= 2) {
				 getRecInfoBySlaveFromHive(departmentId, dates, compDepNameToSubLevelNameMap, busiDetDataInfoMap, dbHiveRecyclerWasteService);
			 }else {
				/**
				 * 餐厨垃圾
				 */
				//获取学校回收垃圾数
				String keySuffix ="_schoolwastetotal";
				getRecInfoBySlave(departmentId, dates, compDepNameToSubLevelNameMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
				//获取团餐公司回收垃圾数[注释原因：按照学校统计，不统计团餐公司数据]
				//keySuffix ="_supplierwaste";
				//getRecInfoBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
				
				/**
				 * 排废弃油脂
				 */
				//获取学校回收废弃油脂数
			    keySuffix ="_schooloiltotal";
			    getRecInfoBySlave(departmentId, dates, compDepNameToSubLevelNameMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
				//获取团餐公司废弃油脂数[注释原因：按照学校统计，不统计团餐公司数据]
				//keySuffix ="_supplieroil";
				//getRecInfoBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap, keySuffix,busiDetDataInfoMap,saasService,eduSchoolService);
			 }
		    //计算每种【学制分类】的数量
			Map<String,BusiDetDataInfo> newBusiDetDataInfoMap= new LinkedHashMap<String,BusiDetDataInfo>();
			String statClassName="";
			BusiDetDataInfo busiDetDataInfoSum = new BusiDetDataInfo();
			for(Map.Entry<String,BusiDetDataInfo> entry : busiDetDataInfoMap.entrySet()) {
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newBusiDetDataInfoMap.put(statClassName+"_小计", busiDetDataInfoSum);
					busiDetDataInfoSum = new BusiDetDataInfo();
				}

				//计算数据
				setSchoolPropertSumData(busiDetDataInfoSum, entry);
				statClassName = entry.getValue().getStatClassName();
				busiDetDataInfoSum.setStatClassName(statClassName);
				busiDetDataInfoSum.setStatPropName("小计");
				//计算比率：包括排菜率、验收率、留样率、预警处理率
                setRate(busiDetDataInfoSum);
				newBusiDetDataInfoMap.put(entry.getKey(), entry.getValue());
				
			}
			newBusiDetDataInfoMap.put("其他"+"_小计", busiDetDataInfoSum);
			busiDetDataInfoMap = newBusiDetDataInfoMap;
			
			//循环技术小计的百分比（包括排菜率、验收率、留样率、预警处理率、）
			for(Map.Entry<String,BusiDetDataInfo> entry : busiDetDataInfoMap.entrySet()) {
				if(entry.getKey().contains("小计")) {
					busiDetDataInfoSum = entry.getValue();
				}
			}
			
		}else if (statMode == 4) {
			//========4:按学校管理部门统计
	    	
	    	Map<Integer, String> departmentMap = new LinkedHashMap<Integer,String>();
	    	DepartmentObj departmentObj = new DepartmentObj();
	    	departmentObj.setDepartmentId(departmentId);
			List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,null, -1, -1);	
			
			int [] schOwnTypes = new int [deparmentList.size()];
			if(CommonUtil.isNotEmpty(departmentId)) {
			     schOwnTypes = new int [1];
			}
			int index = 0;
			if(deparmentList != null) {
				for(DepartmentObj department : deparmentList) {
					if(CommonUtil.isNotEmpty(departmentId) && !departmentId.equals(department.getDepartmentId())) {
						continue;
					}
					departmentMap.put(Integer.valueOf(department.getDepartmentId()), department.getDepartmentName());
					schOwnTypes[index++] = Integer.valueOf(department.getDepartmentId());
				}
			}
			
			/**
			 * 监管学校数量
			 */
			getSupSchNumByDepartment(departmentId, departmentMap, busiDetDataInfoMap);
			
			if(days >= 2) {
				//学校总数、排菜汇总（一天前数据，此处会覆盖学校总数）
				getDishInfoByDeparmentFromHive(departmentId, dates, deparmentList, busiDetDataInfoMap, dbHiveDishService);
			}else {
				/**
				 * 排菜汇总
				 */
				getDishInfoByDepartment(departmentId, dates, departmentMap, schOwnTypes, busiDetDataInfoMap);
			}
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收配送单个数、验收率
			if(days >= 2) {
				getAccDistrInfoByDepartmentFromHive(departmentId, dates, deparmentList, busiDetDataInfoMap, dbHiveGsService);
			}else {
				getAccDistrInfoByDepartment(departmentId, dates, deparmentList, busiDetDataInfoMap);
			}
			
			//2019-03-27注释原因：改为从Total里获取
			//获取配送信息：未验收学校
			//getAcceptNoAccSchuNumByArea(distId, dates, tedList, busiDetDataInfoMap);
			
			/**
			 * 留样汇总
			 */
			 //获取留样汇总：留样率、未留样菜品个数
			 getRsInfoRsRateByDepartment(departmentId, dates, deparmentList, busiDetDataInfoMap);
			 
			//2019.03.28 注释原因：建模规则修改由detail改为total中获取
			 //获取留样汇总：未留样学校个数
			 //getRsInfoNoRsSchNumByArea(distId, dates, tedList,db1Service,busiDetDataInfoMap);
			
			/**
			 * 预警汇总
			 */
			 //2019.06.06注释，注释原因：预警信息改为从hive库中获取
			//获取预警汇总：未处理预警、处理率
			//getWarnInfoWarnProcRateByArea(distId, dates, tedList,busiDetDataInfoMap);
			//获取预警汇总：未处理单位
			//getWarnInfoNoProcUnitNumByArea(distId, dates,tedList, saasService,busiDetDataInfoMap);
			getWarnInfoWarnProcRateByDepartmentTwo(departmentId, null, dates, target, deparmentList, busiDetDataInfoMap, dbHiveWarnService);
			/*//30天之前的数据获取hive库，30天以内以及今天以后的数据获取redis数据
			if(days >= 2) {
				getRecInfoByAreaFromHive(distId, dates, tedList, dbHiveRecyclerWasteService, busiDetDataInfoMap);
			}else {
				*//**
				 * 餐厨垃圾
				 *//*
				//获取学校回收垃圾数
				String keySuffix ="_schoolwastetotal";
				getRecInfoByArea(distId, dates, tedList, keySuffix,busiDetDataInfoMap);
				//获取团餐公司回收垃圾数
				keySuffix ="_supplierwastetotal";
				getRecInfoByArea(distId, dates, tedList, keySuffix,busiDetDataInfoMap);
				
				*//**
				 * 排废弃油脂
				 *//*
				//获取学校回收废弃油脂数
			    keySuffix ="_schooloiltotal";
			    getRecInfoByArea(distId, dates, tedList, keySuffix,busiDetDataInfoMap);
				//获取团餐公司废弃油脂数
				keySuffix ="_supplieroiltotal";
				getRecInfoByArea(distId, dates, tedList, keySuffix,busiDetDataInfoMap);
			}*/
			 
				//计算每种【管理】的数量
				Map<String,SchDishSitStat> newSchDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
				String statPropName="";
				SchDishSitStat schDishSitStatSum = new SchDishSitStat();
				boolean otherFist = true;
				/*for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
					if(!"".equals(statPropName) && statPropName.indexOf("区教育局") < 0 && otherFist) {
						otherFist = false;
						newSchDishSitStatMap.put(statPropName+"_小计", schDishSitStatSum);
						schDishSitStatSum = new SchDishSitStat();
					}

					//计算数据
					setSchoolPropertSumData(schDishSitStatSum, entry);
					statPropName = entry.getValue().getStatPropName();
					schDishSitStatSum.setStatPropName("小计");
					setRate(schDishSitStatSum);
					
					newSchDishSitStatMap.put(entry.getKey(), entry.getValue());
					
				}*/
				newSchDishSitStatMap.put("小计", schDishSitStatSum);
				//schDishSitStatMap = newSchDishSitStatMap;
			
		}
		
		
		
		busiDetDataInfoMap.values();
		Collection<BusiDetDataInfo> valueCollection = busiDetDataInfoMap.values();
	    busiDetDataInfoList = new ArrayList<BusiDetDataInfo>(valueCollection);

		
		busiDetDataInfo.setBusiDetDataInfo(busiDetDataInfoList);
		//时戳
		busiDetDataInfo.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		busiDetDataInfo.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		
		
		return busiDetDataInfo;
	}

	private void setRate(BusiDetDataInfo busiDetDataInfoSum) {
		//排菜率
		float distDishRate = 0;
		if(busiDetDataInfoSum.getMealSchNum() > 0) {
			distDishRate = 100 * ((float) (busiDetDataInfoSum.getMealSchNum() - busiDetDataInfoSum.getNoDishSchNum()) / (float) busiDetDataInfoSum.getMealSchNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		busiDetDataInfoSum.setDishRate(distDishRate);
		
		//验收率
		distDishRate = 0;
		if(busiDetDataInfoSum.getGsPlanNum() > 0) {
			distDishRate = 100 * ((float) (busiDetDataInfoSum.getGsPlanNum() - busiDetDataInfoSum.getNoAcceptPlanNum()) / (float) busiDetDataInfoSum.getGsPlanNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			/*if (distDishRate > 100) {
				distDishRate = 100;
			}*/
		}
		busiDetDataInfoSum.setAcceptRate(distDishRate);
		
		//学校验收率
		distDishRate = 0;
		if(busiDetDataInfoSum.getShouldAcceptSchNum()> 0) {
			distDishRate = 100 * ((float) (busiDetDataInfoSum.getShouldAcceptSchNum() - busiDetDataInfoSum.getNoAcceptSchNum()) / (float) busiDetDataInfoSum.getShouldAcceptSchNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		busiDetDataInfoSum.setSchAcceptRate(distDishRate);
		
		//留样率
		distDishRate = 0;
		if(busiDetDataInfoSum.getDishNum() > 0) {
			distDishRate = 100 * ((float) (busiDetDataInfoSum.getDishNum() - busiDetDataInfoSum.getNoRsDishNum()) / (float) busiDetDataInfoSum.getDishNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		busiDetDataInfoSum.setRsRate(distDishRate);
		
		//学校留样率
		distDishRate = 0;
		if(busiDetDataInfoSum.getShouldRsSchNum() > 0) {
			distDishRate = 100 * ((float) (busiDetDataInfoSum.getShouldRsSchNum() - busiDetDataInfoSum.getNoRsSchNum()) / (float) busiDetDataInfoSum.getShouldRsSchNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		busiDetDataInfoSum.setSchRsRate(distDishRate);
		
		//预警处理率
		distDishRate = 0;
		if(busiDetDataInfoSum.getWarnNum() > 0) {
			distDishRate = 100 * ((float) (busiDetDataInfoSum.getWarnNum() - busiDetDataInfoSum.getNoProcWarnNum()) / (float) busiDetDataInfoSum.getWarnNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		busiDetDataInfoSum.setProcRate(distDishRate);
	}

	/**
	 * 学习学制每个分类小计
	 * @param busiDetDataInfoSum
	 * @param entry
	 */
	private void setSchoolPropertSumData(BusiDetDataInfo busiDetDataInfoSum, Map.Entry<String, BusiDetDataInfo> entry) {
		/**
		 * 学校总数
		 */
		busiDetDataInfoSum.setTotalSchNum(busiDetDataInfoSum.getTotalSchNum()+entry.getValue().getTotalSchNum());
		/**
		 * 已排菜学校数
		 */
		busiDetDataInfoSum.setDishSchNum(busiDetDataInfoSum.getDishSchNum()+entry.getValue().getDishSchNum());
		
		/**
		 * 未排菜学校数
		 */
		busiDetDataInfoSum.setNoDishSchNum(busiDetDataInfoSum.getNoDishSchNum()+entry.getValue().getNoDishSchNum());
		
		/**
		 * 应排菜天数，即供餐天数
		 */
		busiDetDataInfoSum.setMealSchNum(busiDetDataInfoSum.getMealSchNum()+entry.getValue().getMealSchNum());
		
		/**
		 * 未排菜天数
		 */
		busiDetDataInfoSum.setNoDishDayNum(busiDetDataInfoSum.getNoDishDayNum()+entry.getValue().getNoDishDayNum());
		
		/**
		 * 排菜率
		 */
		busiDetDataInfoSum.setDishRate(busiDetDataInfoSum.getDishRate()+entry.getValue().getDishRate());
		
		/**
		 * 应验收学校数
		 */
		busiDetDataInfoSum.setShouldAcceptSchNum(busiDetDataInfoSum.getShouldAcceptSchNum()+entry.getValue().getShouldAcceptSchNum());
		/**
		 * 已验收学校数
		 */
		busiDetDataInfoSum.setAcceptSchNum(busiDetDataInfoSum.getAcceptSchNum()+entry.getValue().getAcceptSchNum());
		
		/**
		 * 未验收学校数
		 */
		busiDetDataInfoSum.setNoAcceptSchNum(busiDetDataInfoSum.getNoAcceptSchNum()+entry.getValue().getNoAcceptSchNum());
		/**
		 * 配货计划数
		 */
		busiDetDataInfoSum.setGsPlanNum(busiDetDataInfoSum.getGsPlanNum()+entry.getValue().getGsPlanNum());
		/**
		 * 已验收计划数
		 */
		busiDetDataInfoSum.setAcceptPlanNum(busiDetDataInfoSum.getAcceptPlanNum()+entry.getValue().getAcceptPlanNum());
		/**
		 * 未验收计划数
		 */
		busiDetDataInfoSum.setNoAcceptPlanNum(busiDetDataInfoSum.getNoAcceptPlanNum()+entry.getValue().getNoAcceptPlanNum());
		/**
		 * 验收率
		 */
		busiDetDataInfoSum.setAcceptRate(busiDetDataInfoSum.getAcceptRate()+entry.getValue().getAcceptRate());
		/**
		 * 应留样学校数
		 */
		busiDetDataInfoSum.setShouldRsSchNum(busiDetDataInfoSum.getShouldRsSchNum()+entry.getValue().getShouldRsSchNum());
		/**
		 * 已留样学校数
		 */
		busiDetDataInfoSum.setRsSchNum(busiDetDataInfoSum.getRsSchNum()+entry.getValue().getRsSchNum());
		/**
		 * 未留样学校数
		 */
		busiDetDataInfoSum.setNoRsSchNum(busiDetDataInfoSum.getNoRsSchNum()+entry.getValue().getNoRsSchNum());
		/**
		 * 菜品数量
		 */
		busiDetDataInfoSum.setDishNum(busiDetDataInfoSum.getDishNum()+entry.getValue().getDishNum());
		/**
		 * 已留样菜品
		 */
		busiDetDataInfoSum.setRsDishNum(busiDetDataInfoSum.getRsDishNum()+entry.getValue().getRsDishNum());
		/**
		 * 未留样菜品
		 */
		busiDetDataInfoSum.setNoRsDishNum(busiDetDataInfoSum.getNoRsDishNum()+entry.getValue().getNoRsDishNum());
		/**
		 * 留样率
		 */
		busiDetDataInfoSum.setRsRate(busiDetDataInfoSum.getRsRate()+entry.getValue().getRsRate());
		/**
		 * 未处理数
		 */
		busiDetDataInfoSum.setElimWarnNum(busiDetDataInfoSum.getElimWarnNum()+entry.getValue().getElimWarnNum());
		/**
		 * 预警数
		 */
		busiDetDataInfoSum.setWarnNum(busiDetDataInfoSum.getWarnNum()+entry.getValue().getWarnNum());
		/**
		 * 未处理预警数
		 */
		busiDetDataInfoSum.setNoProcWarnNum(busiDetDataInfoSum.getNoProcWarnNum()+entry.getValue().getNoProcWarnNum());
		/**
		 * 处理率
		 */
		busiDetDataInfoSum.setProcRate(busiDetDataInfoSum.getProcRate()+entry.getValue().getProcRate());
		/**
		 * 餐厨垃圾学校回收桶数
		 */
		busiDetDataInfoSum.setKwSchRecNum(busiDetDataInfoSum.getKwSchRecNum()+entry.getValue().getKwSchRecNum());
		/**
		 * 餐厨垃圾团餐公司回收桶数
		 */
		busiDetDataInfoSum.setKwRmcRecNum(busiDetDataInfoSum.getKwRmcRecNum()+entry.getValue().getKwRmcRecNum());
		/**
		 * 餐厨垃圾回收合计
		 */
		busiDetDataInfoSum.setKwTotalRecNum(busiDetDataInfoSum.getKwTotalRecNum()+entry.getValue().getKwTotalRecNum());
		busiDetDataInfoSum.setKwTotalRecNum(new BigDecimal(busiDetDataInfoSum.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		/**
		 * 废弃油脂学校回收桶数
		 */
		busiDetDataInfoSum.setWoSchRecNum(busiDetDataInfoSum.getWoSchRecNum()+entry.getValue().getWoSchRecNum());
		/**
		 * 废弃油脂团餐公司回收桶数
		 */
		busiDetDataInfoSum.setWoRmcRecNum(busiDetDataInfoSum.getWoRmcRecNum()+entry.getValue().getWoRmcRecNum());
		/**
		 * 废弃油脂回收合计
		 */
		busiDetDataInfoSum.setWoTotalRecNum(busiDetDataInfoSum.getWoTotalRecNum()+entry.getValue().getWoTotalRecNum());
		busiDetDataInfoSum.setWoTotalRecNum(new BigDecimal(busiDetDataInfoSum.getWoTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
	}
	
	/**
	 * 获取废弃油脂、餐厨垃圾相关数据（按区统计）
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param keySuffix
	 * @return
	 */
	private void getRecInfoByArea(String distId, String[] dates, List<TEduDistrictDo> tedList, String keySuffix,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		String field = "";
		String fieldPrefix = "";
		// 时间段内各区餐厨垃圾学校回收总数
		Map<String, String> schoolwastetotalMap = null;
		int distCount = tedList.size();
		int dateCount = dates.length;
		int[][] totalRcFreqs = new int[dateCount][distCount];
		float[][] totalRcNums = new float[dateCount][distCount];
		// 时间段内各区餐厨垃圾学校回收数量
		for (int k = 0; k < dates.length; k++) {
			// 回收桶数
			key = dates[k] + keySuffix;
			schoolwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(schoolwastetotalMap == null) {    
				
			}
			if(schoolwastetotalMap != null) {
				for(String curKey : schoolwastetotalMap.keySet()) {
					for (int i = 0; i < tedList.size(); i++) {
						totalRcFreqs[k][i]=0;
						TEduDistrictDo curTdd = tedList.get(i);
						String curDistId = curTdd.getId();
						//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						if(distId != null) {
							if(curDistId.compareTo(distId) != 0) {
								continue ;
							}
						}
						//区域回收次数
						fieldPrefix = curDistId + "_total";
						if (curKey.equalsIgnoreCase(fieldPrefix)) {
							keyVal = schoolwastetotalMap.get(curKey);
							if(keyVal != null) {
								totalRcFreqs[k][i] += Integer.parseInt(keyVal);
							}
						}
						// 区域回收垃圾桶数
						fieldPrefix = curDistId;
						if (curKey.equalsIgnoreCase(fieldPrefix)) {
							keyVal = schoolwastetotalMap.get(curKey);
							if(keyVal != null) {
								totalRcNums[k][i] += Float.parseFloat(keyVal);
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
				if (distId != null) {
					if (curDistId.compareTo(distId) != 0) {
						continue;
					}
				}
				BigDecimal bd = new BigDecimal(totalRcNums[k][i]);
				totalRcNums[k][i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，回收次数：" + totalRcFreqs[k]
						+ "，回收数量：" + totalRcNums[k] + " 桶" + "，field = " + field);
			}
		}
		
		for (int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equals(distId)) {
					continue;
				}
			}
			float totalRcNum = 0;
			for (int k = 0; k < dates.length; k++) {
				totalRcNum += totalRcNums[k][i];
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(curTdd.getName());
			}
			
			if("_schoolwastetotal".equals(keySuffix)) {
				/**
				 * 餐厨垃圾学校回收桶数
				 */
				busiDetDataInfo.setKwSchRecNum(totalRcNum);
				
				/**
				 * 餐厨垃圾回收合计
				 */
				busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
						(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
				busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				
			}else if("_supplierwastetotal".equals(keySuffix)) {
				/**
				 * 餐厨垃圾团餐公司回收桶数
				 */
				busiDetDataInfo.setKwRmcRecNum(totalRcNum);
				
				/**
				 * 餐厨垃圾回收合计
				 */
				busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
						(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
				busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				
			}else if("_schooloiltotal".equals(keySuffix)) {
				
				/**
				 * 废弃油脂学校回收桶数
				 */
				busiDetDataInfo.setWoSchRecNum(totalRcNum);
				/**
				 * 废弃油脂回收合计
				 */
				busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
						(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
				busiDetDataInfo.setWoTotalRecNum(new BigDecimal(busiDetDataInfo.getWoTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				
			}else if("_supplieroiltotal".equals(keySuffix)) {
				/**
				 * 废弃油脂团餐公司回收桶数
				 */
				busiDetDataInfo.setWoRmcRecNum(totalRcNum);
				/**
				 * 废弃油脂回收合计
				 */
				busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
						(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
				busiDetDataInfo.setWoTotalRecNum(new BigDecimal(busiDetDataInfo.getWoTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			}
			
			busiDetDataInfoMap.put(curTdd.getName(), busiDetDataInfo);
			
		}
	}
	
	/**
	 * 获取废弃油脂、餐厨垃圾相关数据（按区统计）从hive库中获取
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param keySuffix
	 * @return
	 */
	private void getRecInfoByAreaFromHive(String distId, String[] dates, List<TEduDistrictDo> tedList,DbHiveRecyclerWasteService dbHiveRecyclerWasteService,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		// 时间段内各区餐厨垃圾学校回收总数
		int distCount = tedList.size();
		//学校餐厨垃圾集合 key:区号 value ：回收数量
		Map<String,Float> totalSchRcNumMap = new HashMap<String,Float>();
		//团餐公司餐厨垃圾集合 key:区号 value ：回收数量
		Map<String,Float> totalRmCRcNumMap = new HashMap<String,Float>();
		//学校餐废弃油脂集合 key:区号 value ：回收数量
		Map<String,Float> totalSchOilRcNumMap = new HashMap<String,Float>();
		//团餐公司废弃油脂集合 key:区号 value ：回收数量
		Map<String,Float> totalRmCOilRcNumMap = new HashMap<String,Float>();
		
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
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<KwCommonRecs> warnCommonLicList = new ArrayList<>();
		warnCommonLicList = dbHiveRecyclerWasteService.getRecyclerWasteList(listYearMonth, startDate, endDateAddOne, distId, 
				null, null, null, -1,-1,null,null, null, null,0);
		float totalRcNum = 0;
		for(KwCommonRecs warnCommonLics : warnCommonLicList) {
			if(CommonUtil.isEmpty(warnCommonLics.getDistName())) {
				continue;
			}
			//PlatformType:1为教委端 2为团餐端 type:1餐厨垃圾，2废弃油脂
			if(warnCommonLics.getPlatformType() == 1) {
				//学校
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalSchRcNumMap.get(warnCommonLics.getDistName())==null?0:totalSchRcNumMap.get(warnCommonLics.getDistName());
					totalRcNum = totalRcNum+ warnCommonLics.getRecyclerSum();
					totalSchRcNumMap.put(warnCommonLics.getDistName(), totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalSchOilRcNumMap.get(warnCommonLics.getDistName())==null?0:totalSchOilRcNumMap.get(warnCommonLics.getDistName());
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalSchOilRcNumMap.put(warnCommonLics.getDistName(), totalRcNum);
				}
			}else if(warnCommonLics.getPlatformType() == 2) {
				//团餐公司
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalRmCRcNumMap.get(warnCommonLics.getDistName())==null?0:totalRmCRcNumMap.get(warnCommonLics.getDistName());
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalRmCRcNumMap.put(warnCommonLics.getDistName(), totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalRmCOilRcNumMap.get(warnCommonLics.getDistName())==null?0:totalRmCOilRcNumMap.get(warnCommonLics.getDistName());
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalRmCOilRcNumMap.put(warnCommonLics.getDistName(), totalRcNum);
				}
			}
		}
		
		for (int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equals(distId)) {
					continue;
				}
			}
			totalRcNum = totalSchRcNumMap.get(curDistId)==null?0:totalSchRcNumMap.get(curDistId);
			float totalRmCRcNum = totalRmCRcNumMap.get(curDistId)==null?0:totalRmCRcNumMap.get(curDistId);
			float totalSchOilRcNum = totalSchOilRcNumMap.get(curDistId)==null?0:totalSchOilRcNumMap.get(curDistId);
			float totalRmCOilRcNum = totalRmCOilRcNumMap.get(curDistId)==null?0:totalRmCOilRcNumMap.get(curDistId);
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(curTdd.getName());
			}
			
			/**
			 * 餐厨垃圾学校回收桶数
			 */
			
			busiDetDataInfo.setKwSchRecNum(new BigDecimal(totalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 餐厨垃圾团餐公司回收桶数
			 */
			busiDetDataInfo.setKwRmcRecNum(new BigDecimal(totalRmCRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 餐厨垃圾回收合计
			 */
			busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
					(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
			busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 废弃油脂学校回收桶数
			 */
			busiDetDataInfo.setWoSchRecNum(new BigDecimal(totalSchOilRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 废弃油脂团餐公司回收桶数
			 */
			busiDetDataInfo.setWoRmcRecNum(new BigDecimal(totalRmCOilRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			/**
			 * 废弃油脂回收合计
			 */
			busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
					(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
			busiDetDataInfo.setWoTotalRecNum(new BigDecimal(busiDetDataInfo.getWoTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			busiDetDataInfoMap.put(curTdd.getName(), busiDetDataInfo);
			
		}
	}
	
	/**
	 * 获取废弃油脂、餐厨垃圾相关数据(按学校性质统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param keySuffix
	 * @return
	 */
	private void getRecInfoByNature(String distId, String[] dates,Map<Integer, String> schoolPropertyMap, String keySuffix,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,SaasService saasService,EduSchoolService eduSchoolService) {
		String key = "";
		String keyVal = "";
		// 时间段内各区餐厨垃圾学校回收总数
		Map<String, String> schoolwastetotalMap = null;
		Map<String,Float> totalRcNumMap = new HashMap<String,Float>();
		
		List<TEduSchoolSupplierDo> tEduSchoolSupplierDoList = saasService.getAllIdSupplierIdSchoolId(null);
    	//Key:SupplierId value:shcooolId
    	Map<String,TEduSchoolSupplierDo> schoolSupplierMap = tEduSchoolSupplierDoList.stream().collect(Collectors.toMap(TEduSchoolSupplierDo::getId,Function.identity(),(oldData,newData)->newData));
		
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		
		String [] keyVals;//redis中filed对应的value值（以"_"分割的数组）
		String schoolId;
		String supplierId;
		String nature = "";
		float rcNum = 0;
		// 时间段内各区餐厨垃圾学校回收数量
		for (int k = 0; k < dates.length; k++) {
			// 回收桶数
			key = dates[k] + keySuffix;
			schoolwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(schoolwastetotalMap == null) {    
				
			}
			if(schoolwastetotalMap != null) {
				for(String curKey : schoolwastetotalMap.keySet()) {
					
					keyVal = schoolwastetotalMap.get(curKey);
					keyVals = keyVal.split("_");
					if(distId != null) {
						if(distId.compareTo(keyVals[1]) != 0) {
							continue ;
						}
					}
					
					
					if("_supplierwaste".equals(keySuffix) || "_supplieroil".equals(keySuffix)) {
						//团餐公司餐厨垃圾和废弃油脂
						supplierId = keyVals[3];
						if(schoolSupplierMap.get(supplierId)==null) {
							continue;
						}
						schoolId = schoolSupplierMap.get(supplierId).getSchoolId();
					}else {
						//学校餐厨垃圾和废弃油脂
						schoolId = keyVals[3];
					}
					if(schoolMap.get(schoolId) == null) {
						continue;
					}
					nature = schoolMap.get(schoolId).getSchoolNature();
					rcNum = ((keyVals[5]==null || !CommonUtil.isNumeric(keyVals[5]))?0:Float.parseFloat(keyVals[5]));
					totalRcNumMap.put(nature, totalRcNumMap.get(nature)==null?0:totalRcNumMap.get(nature)+rcNum);
					
				}
			}
		}
		
		for (Map.Entry<Integer, String> entry :schoolPropertyMap.entrySet()) {
			float totalRcNum =( totalRcNumMap.get(entry.getKey().toString())==null||"null".equals(totalRcNumMap.get(entry.getKey().toString())))?0:totalRcNumMap.get(entry.getKey().toString());
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(entry.getValue());
			}
			
			if("_schoolwaste".equals(keySuffix)) {
				/**
				 * 餐厨垃圾学校回收桶数
				 */
				busiDetDataInfo.setKwSchRecNum(totalRcNum);
				
				/**
				 * 餐厨垃圾回收合计
				 */
				busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
						(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
				busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				
			}else if("_supplierwaste".equals(keySuffix)) {
				/**
				 * 餐厨垃圾团餐公司回收桶数
				 */
				busiDetDataInfo.setKwRmcRecNum(totalRcNum);
				
				/**
				 * 餐厨垃圾回收合计
				 */
				busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
						(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
				
			}else if("_schooloil".equals(keySuffix)) {
				
				/**
				 * 废弃油脂学校回收桶数
				 */
				busiDetDataInfo.setWoSchRecNum(totalRcNum);
				/**
				 * 废弃油脂回收合计
				 */
				busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
						(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
				busiDetDataInfo.setWoTotalRecNum(new BigDecimal(busiDetDataInfo.getWoTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				
				
			}else if("_supplieroil".equals(keySuffix)) {
				/**
				 * 废弃油脂团餐公司回收桶数
				 */
				busiDetDataInfo.setWoRmcRecNum(totalRcNum);
				/**
				 * 废弃油脂回收合计
				 */
				busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
						(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
			}
			
			busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
			
		}
	}
	
	/**
	 * 获取废弃油脂、餐厨垃圾相关数据(按学校性质统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param keySuffix
	 * @return
	 */
	private void getRecInfoByNatureFromHive(String distId, String[] dates,Map<Integer, String> schoolPropertyMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		
		//学校餐厨垃圾集合 key:学校性质编号 value ：回收数量
		Map<String,Float> totalSchRcNumMap = new HashMap<String,Float>();
		//团餐公司餐厨垃圾集合 key:学校性质编号 value ：回收数量
		Map<String,Float> totalRmCRcNumMap = new HashMap<String,Float>();
		//学校餐废弃油脂集合 key:学校性质编号 value ：回收数量
		Map<String,Float> totalSchOilRcNumMap = new HashMap<String,Float>();
		//团餐公司废弃油脂集合 key:学校性质编号 value ：回收数量
		Map<String,Float> totalRmCOilRcNumMap = new HashMap<String,Float>();
		
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
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<KwCommonRecs> warnCommonLicList = new ArrayList<>();
		warnCommonLicList = dbHiveRecyclerWasteService.getRecyclerWasteList(listYearMonth, startDate, endDateAddOne, distId, 
				null, null, null, -1,-1,null,null, null, null,1);
		float totalRcNum = 0;
		for(KwCommonRecs warnCommonLics : warnCommonLicList) {
			//PlatformType:1为教委端 2为团餐端 type:1餐厨垃圾，2废弃油脂
			if(warnCommonLics.getPlatformType() == 1) {
				//学校
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalSchRcNumMap.get(warnCommonLics.getSchoolNatureName())==null?0:totalSchRcNumMap.get(warnCommonLics.getSchoolNatureName());
					totalRcNum = totalRcNum+ warnCommonLics.getRecyclerSum();
					totalSchRcNumMap.put(warnCommonLics.getSchoolNatureName(), totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalSchOilRcNumMap.get(warnCommonLics.getSchoolNatureName())==null?0:totalSchOilRcNumMap.get(warnCommonLics.getSchoolNatureName());
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalSchOilRcNumMap.put(warnCommonLics.getSchoolNatureName(), totalRcNum);
				}
			}else if(warnCommonLics.getPlatformType() == 2) {
				//团餐公司
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalRmCRcNumMap.get(warnCommonLics.getSchoolNatureName())==null?0:totalRmCRcNumMap.get(warnCommonLics.getSchoolNatureName());
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalRmCRcNumMap.put(warnCommonLics.getSchoolNatureName(), totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalRmCOilRcNumMap.get(warnCommonLics.getSchoolNatureName())==null?0:totalRmCOilRcNumMap.get(warnCommonLics.getSchoolNatureName());
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalRmCOilRcNumMap.put(warnCommonLics.getSchoolNatureName(), totalRcNum);
				}
			}
		}
		
		for (Map.Entry<Integer, String> entry :schoolPropertyMap.entrySet()) {
			totalRcNum = totalSchRcNumMap.get(entry.getKey().toString())==null?0:totalSchRcNumMap.get(entry.getKey().toString());
			float totalRmCRcNum = totalRmCRcNumMap.get(entry.getKey().toString())==null?0:totalRmCRcNumMap.get(entry.getKey().toString());
			float totalSchOilRcNum = totalSchOilRcNumMap.get(entry.getKey().toString())==null?0:totalSchOilRcNumMap.get(entry.getKey().toString());
			float totalRmCOilRcNum = totalRmCOilRcNumMap.get(entry.getKey().toString())==null?0:totalRmCOilRcNumMap.get(entry.getKey().toString());
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(entry.getValue());
			}
			
			/**
			 * 餐厨垃圾学校回收桶数
			 */
			busiDetDataInfo.setKwSchRecNum(new BigDecimal(totalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			
			/**
			 * 餐厨垃圾团餐公司回收桶数
			 */
			busiDetDataInfo.setKwRmcRecNum(new BigDecimal(totalRmCRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 餐厨垃圾回收合计
			 */
			busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
					(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
			busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 废弃油脂学校回收桶数
			 */
			busiDetDataInfo.setWoSchRecNum(new BigDecimal(totalSchOilRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 废弃油脂团餐公司回收桶数
			 */
			busiDetDataInfo.setWoRmcRecNum(new BigDecimal(totalRmCOilRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			/**
			 * 废弃油脂回收合计
			 */
			busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
					(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
			
			busiDetDataInfo.setWoTotalRecNum(new BigDecimal(busiDetDataInfo.getWoTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
			
		}
	}
	
	/**
	 * 获取废弃油脂、餐厨垃圾相关数据(按学校类型统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param keySuffix
	 * @return
	 */
	private void getRecInfoBySchoolType(String distId, String[] dates,Map<Integer, String> schoolPropertyMap, String keySuffix,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,SaasService saasService,EduSchoolService eduSchoolService) {
		String key = "";
		String keyVal = "";
		// 时间段内各区餐厨垃圾学校回收总数
		Map<String, String> schoolwastetotalMap = null;
		Map<String,Float> totalRcNumMap = new HashMap<String,Float>();
		
		List<TEduSchoolSupplierDo> tEduSchoolSupplierDoList = saasService.getAllIdSupplierIdSchoolId(null);
    	//Key:SupplierId value:shcooolId
    	Map<String,TEduSchoolSupplierDo> schoolSupplierMap = tEduSchoolSupplierDoList.stream().collect(Collectors.toMap(TEduSchoolSupplierDo::getId,Function.identity(),(oldData,newData)->newData));
		
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		
		String [] keyVals;//redis中filed对应的value值（以"_"分割的数组）
		String schoolId;
		String supplierId;
		String level = "";
		float rcNum = 0;
		// 时间段内各区餐厨垃圾学校回收数量
		for (int k = 0; k < dates.length; k++) {
			// 回收桶数
			key = dates[k] + keySuffix;
			schoolwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(schoolwastetotalMap == null) {    
				
			}
			if(schoolwastetotalMap != null) {
				for(String curKey : schoolwastetotalMap.keySet()) {
					
					keyVal = schoolwastetotalMap.get(curKey);
					keyVals = keyVal.split("_");
					if(distId != null) {
						if(distId.compareTo(keyVals[1]) != 0) {
							continue ;
						}
					}
					
					
					if("_supplierwaste".equals(keySuffix) || "_supplieroil".equals(keySuffix)) {
						//团餐公司餐厨垃圾和废弃油脂
						supplierId = keyVals[3];
						if(schoolSupplierMap.get(supplierId)==null) {
							continue;
						}
						schoolId = schoolSupplierMap.get(supplierId).getSchoolId();
					}else {
						//学校餐厨垃圾和废弃油脂
						schoolId = keyVals[3];
					}
					if(schoolMap.get(schoolId)==null) {
						continue;
					}
					level = schoolMap.get(schoolId).getLEVEL();
					rcNum = ((keyVals[5] ==null|| !CommonUtil.isNumeric(keyVals[5]))?0:Float.parseFloat(keyVals[5]));
					totalRcNumMap.put(level, totalRcNumMap.get(level)==null?0:totalRcNumMap.get(level)+rcNum);
					
				}
			}
		}
		
		for (Map.Entry<Integer, String> entry :schoolPropertyMap.entrySet()) {
			float totalRcNum = totalRcNumMap.get(entry.getKey().toString())==null?0:totalRcNumMap.get(entry.getKey().toString());
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(entry.getValue());
				busiDetDataInfo.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
			}
			
			if("_schoolwaste".equals(keySuffix)) {
				/**
				 * 餐厨垃圾学校回收桶数
				 */
				busiDetDataInfo.setKwSchRecNum(totalRcNum);
				
				/**
				 * 餐厨垃圾回收合计
				 */
				busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
						(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
				busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			}else if("_supplierwaste".equals(keySuffix)) {
				/**
				 * 餐厨垃圾团餐公司回收桶数
				 */
				busiDetDataInfo.setKwRmcRecNum(totalRcNum);
				
				/**
				 * 餐厨垃圾回收合计
				 */
				busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
						(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
				busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				
			}else if("_schooloil".equals(keySuffix)) {
				
				/**
				 * 废弃油脂学校回收桶数
				 */
				busiDetDataInfo.setWoSchRecNum(totalRcNum);
				/**
				 * 废弃油脂回收合计
				 */
				busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
						(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
				
			}else if("_supplieroil".equals(keySuffix)) {
				/**
				 * 废弃油脂团餐公司回收桶数
				 */
				busiDetDataInfo.setWoRmcRecNum(totalRcNum);
				/**
				 * 废弃油脂回收合计
				 */
				busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
						(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
			}
			
			busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
			
		}
	}

	/**
	 * 获取废弃油脂、餐厨垃圾相关数据(按学校类型统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param keySuffix
	 * @return
	 */
	private void getRecInfoBySchoolTypeFromHive(String distId, String[] dates,Map<Integer, String> schoolPropertyMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,SaasService saasService,EduSchoolService eduSchoolService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		
		//学校餐厨垃圾集合 key:区号 value ：回收数量
		Map<String,Float> totalSchRcNumMap = new HashMap<String,Float>();
		//团餐公司餐厨垃圾集合 key:区号 value ：回收数量
		Map<String,Float> totalRmCRcNumMap = new HashMap<String,Float>();
		//学校餐废弃油脂集合 key:区号 value ：回收数量
		Map<String,Float> totalSchOilRcNumMap = new HashMap<String,Float>();
		//团餐公司废弃油脂集合 key:区号 value ：回收数量
		Map<String,Float> totalRmCOilRcNumMap = new HashMap<String,Float>();
		
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
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<KwCommonRecs> warnCommonLicList = new ArrayList<>();
		warnCommonLicList = dbHiveRecyclerWasteService.getRecyclerWasteList(listYearMonth, startDate, endDateAddOne, distId, 
				null, null, null, -1,-1,null,null, null, null,2);
		float totalRcNum = 0;
		for(KwCommonRecs warnCommonLics : warnCommonLicList) {
			//PlatformType:1为教委端 2为团餐端 type:1餐厨垃圾，2废弃油脂
			if(warnCommonLics.getPlatformType() == 1) {
				//学校
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalSchRcNumMap.get(warnCommonLics.getLevelName())==null?0:totalSchRcNumMap.get(warnCommonLics.getLevelName());
					totalRcNum = totalRcNum+ warnCommonLics.getRecyclerSum();
					totalSchRcNumMap.put(warnCommonLics.getLevelName(), totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalSchOilRcNumMap.get(warnCommonLics.getLevelName())==null?0:totalSchOilRcNumMap.get(warnCommonLics.getLevelName());
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalSchOilRcNumMap.put(warnCommonLics.getLevelName(), totalRcNum);
				}
			}else if(warnCommonLics.getPlatformType() == 2) {
				//团餐公司
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalRmCRcNumMap.get(warnCommonLics.getLevelName())==null?0:totalRmCRcNumMap.get(warnCommonLics.getLevelName());
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalRmCRcNumMap.put(warnCommonLics.getLevelName(), totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalRmCOilRcNumMap.get(warnCommonLics.getLevelName())==null?0:totalRmCOilRcNumMap.get(warnCommonLics.getLevelName());
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalRmCOilRcNumMap.put(warnCommonLics.getLevelName(), totalRcNum);
				}
			}
		}
		
		for (Map.Entry<Integer, String> entry :schoolPropertyMap.entrySet()) {
			
			totalRcNum = totalSchRcNumMap.get(entry.getKey().toString())==null?0:totalSchRcNumMap.get(entry.getKey().toString());
			float totalRmCRcNum = totalRmCRcNumMap.get(entry.getKey().toString())==null?0:totalRmCRcNumMap.get(entry.getKey().toString());
			float totalSchOilRcNum = totalSchOilRcNumMap.get(entry.getKey().toString())==null?0:totalSchOilRcNumMap.get(entry.getKey().toString());
			float totalRmCOilRcNum = totalRmCOilRcNumMap.get(entry.getKey().toString())==null?0:totalRmCOilRcNumMap.get(entry.getKey().toString());
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(entry.getValue());
				busiDetDataInfo.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
			}
			
			/**
			 * 餐厨垃圾学校回收桶数
			 */
			busiDetDataInfo.setKwSchRecNum( new BigDecimal(totalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			
			/**
			 * 餐厨垃圾团餐公司回收桶数
			 */
			busiDetDataInfo.setKwRmcRecNum( new BigDecimal(totalRmCRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 餐厨垃圾回收合计
			 */
			busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
					(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
			busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			
			/**
			 * 废弃油脂学校回收桶数
			 */
			busiDetDataInfo.setWoSchRecNum( new BigDecimal(totalSchOilRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 废弃油脂团餐公司回收桶数
			 */
			busiDetDataInfo.setWoRmcRecNum( new BigDecimal(totalRmCOilRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			/**
			 * 废弃油脂回收合计
			 */
			busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
					(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
			busiDetDataInfo.setWoTotalRecNum(new BigDecimal(busiDetDataInfo.getWoTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
			
		}
	}
	
	/**
	 * 获取废弃油脂、餐厨垃圾相关数据(按所属主管部门统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param keySuffix
	 * @return
	 */
	private void getRecInfoBySlave(String distId, String[] dates,Map<String, String> slaveMap, String keySuffix,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,SaasService saasService,EduSchoolService eduSchoolService) {
		String key = "";
		String keyVal = "";
		// 时间段内各区餐厨垃圾学校回收总数
		Map<String, String> schoolwastetotalMap = null;
		Map<String,Float> totalRcNumMap = new HashMap<String,Float>();
		
		float rcNum = 0;
		// 时间段内各区餐厨垃圾学校回收数量
		for (int k = 0; k < dates.length; k++) {
			// 回收桶数
			key = dates[k] + keySuffix;
			schoolwastetotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(schoolwastetotalMap == null) {    
				
			}
			if(schoolwastetotalMap != null) {
				for(String curKey : schoolwastetotalMap.keySet()) {
					String[] curKeys= curKey.split("_");
					if(curKey.indexOf("masterid_")==0 &&  curKeys.length >= 5) {
						if("3".equals(curKeys[1]) && curKeys[3]==null) {
							curKeys[3] = "其他";
						}
						keyVal = schoolwastetotalMap.get(curKey);
						//直属统一处理
						/*if(distId != null) {
							if(distId.compareTo(keyVals[1]) != 0) {
								continue ;
							}
						}*/
						
						rcNum = (keyVal==null?0:Float.parseFloat(keyVal));
						totalRcNumMap.put(curKeys[1]+"_"+curKeys[3], (totalRcNumMap.get(curKeys[1]+"_"+curKeys[3])==null?0:totalRcNumMap.get(curKeys[1]+"_"+curKeys[3]))+rcNum);
					}
				}
			}
		}
		
		for (Map.Entry<String, String> entry :slaveMap.entrySet()) {
			float totalRcNum = totalRcNumMap.get(entry.getKey().toString())==null?0:totalRcNumMap.get(entry.getKey().toString());
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
			if(busiDetDataInfo==null) {
				String[] keys = entry.getKey().split("_");
				String masterid = keys[0];
				String slave = keys[1];
				busiDetDataInfo = new BusiDetDataInfo();
				busiDetDataInfo.setStatClassName(entry.getKey());
				//所属主管部门
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				busiDetDataInfo.setStatPropName(slaveName);
			}
			
			if("_schoolwastetotal".equals(keySuffix)) {
				/**
				 * 餐厨垃圾学校回收桶数
				 */
				busiDetDataInfo.setKwSchRecNum(totalRcNum);
				
				/**
				 * 餐厨垃圾回收合计
				 */
				busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+ 
						(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
				busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			}else if("_supplierwaste".equals(keySuffix)) {
				/**
				 * 餐厨垃圾团餐公司回收桶数
				 */
				busiDetDataInfo.setKwRmcRecNum(totalRcNum);
				
				/**
				 * 餐厨垃圾回收合计
				 */
				busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
						(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
				busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			}else if("_schooloiltotal".equals(keySuffix)) {
				
				/**
				 * 废弃油脂学校回收桶数
				 */
				busiDetDataInfo.setWoSchRecNum(totalRcNum);
				/**
				 * 废弃油脂回收合计
				 */
				busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
						(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
				busiDetDataInfo.setWoTotalRecNum(new BigDecimal(busiDetDataInfo.getWoTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			}else if("_supplieroil".equals(keySuffix)) {
				/**
				 * 废弃油脂团餐公司回收桶数
				 */
				busiDetDataInfo.setWoRmcRecNum(totalRcNum);
				/**
				 * 废弃油脂回收合计
				 */
				busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
						(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
				busiDetDataInfo.setWoTotalRecNum(new BigDecimal(busiDetDataInfo.getWoTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			}
			
			busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
			
		}
	}
	
	/**
	 * 获取废弃油脂、餐厨垃圾相关数据(按所属主管部门统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param keySuffix
	 * @return
	 */
	private void getRecInfoBySlaveFromHive(String distId, String[] dates,Map<String, String> slaveMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService) {
		
		//学校餐厨垃圾集合 key:所属+主管部门 value ：回收数量
		Map<String,Float> totalSchRcNumMap = new HashMap<String,Float>();
		//团餐公司餐厨垃圾集合 key:所属+主管部门  value ：回收数量
		Map<String,Float> totalRmCRcNumMap = new HashMap<String,Float>();
		//学校餐废弃油脂集合 key:所属+主管部门  value ：回收数量
		Map<String,Float> totalSchOilRcNumMap = new HashMap<String,Float>();
		//团餐公司废弃油脂集合 key:所属+主管部门  value ：回收数量
		Map<String,Float> totalRmCOilRcNumMap = new HashMap<String,Float>();
		
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
		
		/**
		 * 1.从hive库中获取汇总数据
		 */
		List<KwCommonRecs> warnCommonLicList = new ArrayList<>();
		warnCommonLicList = dbHiveRecyclerWasteService.getRecyclerWasteList(listYearMonth, startDate, endDateAddOne, distId, 
				null, null, null, -1,-1,null,null, null, null,3);
		float totalRcNum = 0;
		for(KwCommonRecs warnCommonLics : warnCommonLicList) {
			
			String departmentSlaveId = warnCommonLics.getDepartmentSlaveIdName();
			if(warnCommonLics.getDepartmentMasterId() !=null && "3".equals(warnCommonLics.getDepartmentMasterId())) {
				departmentSlaveId = AppModConfig.compDepIdToNameMap3.get(warnCommonLics.getDepartmentSlaveIdName());
			}
			
			//PlatformType:1为教委端 2为团餐端 type:1餐厨垃圾，2废弃油脂
			if(warnCommonLics.getPlatformType() == 1) {
				//学校
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalSchRcNumMap.get(warnCommonLics.getDepartmentMasterId() +"_"+departmentSlaveId)==null?0:totalSchRcNumMap.get(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId);
					totalRcNum = totalRcNum+ warnCommonLics.getRecyclerSum();
					totalSchRcNumMap.put(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId, totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalSchOilRcNumMap.get(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId)==null?0:totalSchOilRcNumMap.get(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId);
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalSchOilRcNumMap.put(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId, totalRcNum);
				}
			}else if(warnCommonLics.getPlatformType() == 2) {
				//团餐公司
				if(warnCommonLics.getType() == 1) {
					//餐厨垃圾
					totalRcNum = totalRmCRcNumMap.get(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId)==null?0:totalRmCRcNumMap.get(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId);
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalRmCRcNumMap.put(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId, totalRcNum);
				}else if (warnCommonLics.getType() == 2) {
					//废弃油脂
					totalRcNum = totalRmCOilRcNumMap.get(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId)==null?0:totalRmCOilRcNumMap.get(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId);
					totalRcNum += warnCommonLics.getRecyclerSum();
					totalRmCOilRcNumMap.put(warnCommonLics.getDepartmentMasterId() +"_"+ departmentSlaveId, totalRcNum);
				}
			}
		}
				
		for (Map.Entry<String, String> entry :slaveMap.entrySet()) {
			
			totalRcNum = totalSchRcNumMap.get(entry.getKey().toString())==null?0:totalSchRcNumMap.get(entry.getKey().toString());
			float totalRmCRcNum = totalRmCRcNumMap.get(entry.getKey().toString())==null?0:totalRmCRcNumMap.get(entry.getKey().toString());
			float totalSchOilRcNum = totalSchOilRcNumMap.get(entry.getKey().toString())==null?0:totalSchOilRcNumMap.get(entry.getKey().toString());
			float totalRmCOilRcNum = totalRmCOilRcNumMap.get(entry.getKey().toString())==null?0:totalRmCOilRcNumMap.get(entry.getKey().toString());
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
			if(busiDetDataInfo==null) {
				String[] keys = entry.getKey().split("_");
				String masterid = keys[0];
				String slave = keys[1];
				busiDetDataInfo = new BusiDetDataInfo();
				busiDetDataInfo.setStatClassName(entry.getKey());
				//所属主管部门
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				busiDetDataInfo.setStatPropName(slaveName);
			}
			
			/**
			 * 餐厨垃圾学校回收桶数
			 */
			busiDetDataInfo.setKwSchRecNum(  new BigDecimal(totalRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 餐厨垃圾团餐公司回收桶数
			 */
			busiDetDataInfo.setKwRmcRecNum(  new BigDecimal(totalRmCRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 餐厨垃圾回收合计
			 */
			busiDetDataInfo.setKwTotalRecNum((busiDetDataInfo.getKwSchRecNum()==null?0:busiDetDataInfo.getKwSchRecNum())+
					(busiDetDataInfo.getKwRmcRecNum()==null?0:busiDetDataInfo.getKwRmcRecNum()));
			busiDetDataInfo.setKwTotalRecNum(new BigDecimal(busiDetDataInfo.getKwTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			/**
			 * 废弃油脂学校回收桶数
			 */
			busiDetDataInfo.setWoSchRecNum(  new BigDecimal(totalSchOilRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			/**
			 * 废弃油脂团餐公司回收桶数
			 */
			busiDetDataInfo.setWoRmcRecNum(  new BigDecimal(totalRmCOilRcNum).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			/**
			 * 废弃油脂回收合计
			 */
			busiDetDataInfo.setWoTotalRecNum((busiDetDataInfo.getWoSchRecNum()==null?0F:busiDetDataInfo.getWoSchRecNum())+
					(busiDetDataInfo.getWoRmcRecNum()==null?0:busiDetDataInfo.getWoRmcRecNum()));
			busiDetDataInfo.setWoTotalRecNum(new BigDecimal(busiDetDataInfo.getWoTotalRecNum()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			
			busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
			
		}
	}
	
	/**
	 * 获取预警汇总：未处理单位
	 * @param distId
	 * @param dates
	 * @param saasService
	 * @return
	 */
	private void getWarnInfoNoProcUnitNumByArea(String distId, String[] dates,List<TEduDistrictDo> tedList, SaasService saasService,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		
		Map<String,Set<String>> warnUnitMap = new HashMap<String,Set<String>>();
		//所有单位
		Set<String> warnUnitSet = new HashSet<String>();
		//人员证照：key：supplierId vlaue:schoolId
		Map<String,String> peopleWarnMap = new HashMap<String,String>();
		Map<String, String> warnDetailMap = new HashMap<>();
		Map<String,TEduDistrictDo> tedMap = tedList.stream().collect(Collectors.toMap(TEduDistrictDo::getId,(b)->b));
		int i;
		int k;
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		//供应商id和名称
		Map<String, String> supIdToNameMap = new HashMap<>();
    	List<TProSupplierDo> tpsDoList = saasService.getIdSupplierIdName();
    	if(tpsDoList != null) {
    		for(i = 0; i < tpsDoList.size(); i++) {
    			supIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
    		}
    	}
    	// 时间段内各区学校餐厨垃圾详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_warnDetail";
			warnDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (warnDetailMap != null) {
				for (String curKey : warnDetailMap.keySet()) {
					keyVal = warnDetailMap.get(curKey);
					// 证照预警全部证件详情列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 16) {
						//区
						i = AppModConfig.getVarValIndex(keyVals, "area");
						//判断区域（判断索引0）
						if(distId != null) {
							String curDistName = keyVals[i];
							if(!curDistName.equalsIgnoreCase(distId)) {
								continue;
							}
								
						}
						
						//触发预警单位
						String trigWarnUnit = "";
						i = AppModConfig.getVarValIndex(keyVals, "supplierid");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								if(supIdToNameMap.containsKey(keyVals[i])) {
									trigWarnUnit = supIdToNameMap.get(keyVals[i]);
								}
							}
						}
						
						//状态
						i = AppModConfig.getVarValIndex(keyVals, "status");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								if(!keyVals[i].equalsIgnoreCase("4")) {
									warnUnitSet=warnUnitMap.get(keyVals[1]);
									if(warnUnitSet == null) {
										warnUnitSet = new HashSet<String>();
									}
									
									if(keyVal.indexOf("people_")==0) {
										//学校名称
										String schoolId="";
										i = AppModConfig.getVarValIndex(keyVals, "schoolid");
										if(i != -1) {
											if(!keyVals[i].equalsIgnoreCase("null")) {
												schoolId = keyVals[i];
											}
										}
										peopleWarnMap.put(trigWarnUnit, schoolId);
									}else {
										warnUnitSet.add(trigWarnUnit);
									}
									
									warnUnitMap.put(keyVals[1], warnUnitSet);
									
								}else {
									continue;
								}
							}
						}
						
						   
					}
					else {
						logger.info("菜品供应明细："+ curKey + "，格式错误！");
					}
				}
			}
			//整合学校证书、团餐公司证书、人员证书。学校证书拿团餐公司统计，团餐公司证书拿团餐公司统计，人员证书拿团餐公司和学校
			for(Map.Entry<String, String> entry : peopleWarnMap.entrySet()) {
				//如果人员对应的团餐公司不包含在学校和团餐公司预计中，则将人员预警对应的学习加入统计中
				if(!warnUnitSet.contains(entry.getKey())) {
					warnUnitSet.add(entry.getValue());
				}
			}
			
			
			for(Map.Entry<String, Set<String>> entry : warnUnitMap.entrySet() ) {
				TEduDistrictDo tEduDistrictDo =  tedMap.get(entry.getKey());
				if(tEduDistrictDo!=null && StringUtils.isNotEmpty(tEduDistrictDo.getName())) {
					BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(tEduDistrictDo.getName());
					if(busiDetDataInfo==null) {
						busiDetDataInfo = new BusiDetDataInfo();
						//区域名称
						busiDetDataInfo.setStatPropName(tEduDistrictDo.getName());
					}
					/**
					 * 未处理单位数
					 */
					busiDetDataInfo.setNoProcUnitNum(entry.getValue()==null?0:entry.getValue().size());
					busiDetDataInfoMap.put(tEduDistrictDo.getName(), busiDetDataInfo);
				}
			}
		}
	}
	
	/**
	 * 获取预警汇总：预警数量、未处理预警、处理率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getWarnInfoWarnProcRateByArea(String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		int i;
		int k;
		int j;
		int distCount = tedList.size();
		int dateCount = dates.length;
		int curWarnNum = 0;
		int[][] totalWarnNums = new int[dateCount][distCount];
		int[][] noProcWarnNums = new int[dateCount][distCount];
		int[][] elimWarnNums = new int[dateCount][distCount];
		float[] warnProcRates = new float[distCount];
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
			//Redis没有该数据则从hdfs系统中获取
			if(warnTotalMap == null) {    
				
			}
			if(warnTotalMap != null) {
				for(String curKey : warnTotalMap.keySet()) {
					String[] curKeys = curKey.split("_");
					if(curKeys.length == 4) {
						i = AppModConfig.getVarValIndex(curKeys, "area");
						if(i != -1) {
							if(!curKeys[i].equalsIgnoreCase("null")) {
								if(distIdToIdxMap.containsKey(curKeys[i])) {
									int idx = distIdToIdxMap.get(curKeys[i]);
									// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
									if (distId != null) {
										if (!curKeys[i].equalsIgnoreCase(distId)) {
											continue;
										}
									}
									
									keyVal = warnTotalMap.get(curKey);
									curWarnNum = 0;
									if(keyVal != null) {
										curWarnNum = Integer.parseInt(keyVal);
									}
									if(curWarnNum < 0) {
										curWarnNum = 0;
									}
									j = AppModConfig.getVarValIndex(curKeys, "status");
									if(j != -1) {
										
										if(curKeys[j].equalsIgnoreCase("1")) {   
											 //未处理预警数
											noProcWarnNums[k][idx] += curWarnNum;
											totalWarnNums[k][idx] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("2")) {   
											 //审核中预警数
											noProcWarnNums[k][idx] += curWarnNum;
											totalWarnNums[k][idx] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("3")) {   
											 //已驳回预警数
											noProcWarnNums[k][idx] += curWarnNum;
											totalWarnNums[k][idx] += curWarnNum;
										}
										else if(curKeys[j].equalsIgnoreCase("4")) {    
											elimWarnNums[k][idx] += curWarnNum;
											//已消除预警数
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
		
		for (i = 0; i < distCount; i++) {
			String curDistId = "-";
			TEduDistrictDo curTdd = new TEduDistrictDo();
			if(i < distCount) {
				curTdd = tedList.get(i);
				curDistId = curTdd.getId();
			}
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equalsIgnoreCase(distId)) {
					continue;
				}
			}
			int totalWarnNum = 0;
			int noProcWarnNum = 0;
			int elimWarnNum = 0;
			for (k = 0; k < dates.length; k++) {
				totalWarnNum += totalWarnNums[k][i];
				noProcWarnNum += noProcWarnNums[k][i];
				elimWarnNum += elimWarnNums[k][i];
			}			
			warnProcRates[i] = 0;
			if(totalWarnNum > 0) {
				warnProcRates[i] = 100 * ((float) elimWarnNum / (float) totalWarnNum);
				BigDecimal bd = new BigDecimal(warnProcRates[i]);
				warnProcRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRates[i] > 100) {
					warnProcRates[i] = 100;
				}
			}
			//无区号且预警数为0的不输出
			if(i == distCount && totalWarnNum == 0) {  
				continue;
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(curTdd.getName());
			}
			/**
			 * 预警数
			 */
			busiDetDataInfo.setWarnNum(totalWarnNum);
			/**
			 * 未处理预警数
			 */
			busiDetDataInfo.setNoProcWarnNum(noProcWarnNum);
			/**
			 * 处理率
			 */
			busiDetDataInfo.setProcRate(warnProcRates[i]);
			
			busiDetDataInfoMap.put(curTdd.getName(), busiDetDataInfo);
		}
	}
	
	/**
	 * 获取预警汇总：预警数量、未处理预警、处理率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getWarnInfoWarnProcRateByAreaTwo(String departmentId,String distId, String[] dates, Integer target,List<TEduDistrictDo> tedList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveWarnService dbHiveWarnService) {
		
		
		String startDate = dates[dates.length-1];
		String endDate = dates[0];
		/**
		 * 1.从hive库中获取汇总数据
		 */
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
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(1, 4, target, listYearMonth, startDate, endDateAddOne,
				distId, null, -1, -1, null, null, departmentId, null, 0);
		
		int distCount = tedList.size();
		int[] totalWarnNums = new int[distCount];
		int[] noProcWarnNums = new int[distCount];
		int[] elimWarnNums = new int[distCount];
		float[] warnProcRates = new float[distCount];
		
		//区域ID到索引映射
		Map<String, Integer> distIdToIdxMap = new HashMap<>();
		for(int i = 0; i < distCount; i++) {
			distIdToIdxMap.put(tedList.get(i).getId(), i);
		}
		
		for(WarnCommon warnCommonLics : warnCommonList) {
			if(distIdToIdxMap.containsKey(warnCommonLics.getDistName())) {
				int idx = distIdToIdxMap.get(warnCommonLics.getDistName());
				noProcWarnNums[idx] += warnCommonLics.getNoProcWarnNum();
				elimWarnNums[idx] += warnCommonLics.getElimWarnNum();
				totalWarnNums[idx] += (warnCommonLics.getNoProcWarnNum() + warnCommonLics.getRejectWarnNum() + warnCommonLics.getAuditWarnNum() + warnCommonLics.getElimWarnNum());
			}
		}
		
		for (int i = 0; i < distCount; i++) {
			String curDistId = "-";
			TEduDistrictDo curTdd = new TEduDistrictDo();
			if(i < distCount) {
				curTdd = tedList.get(i);
				curDistId = curTdd.getId();
			}
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equalsIgnoreCase(distId)) {
					continue;
				}
			}
			int totalWarnNum = 0;
			int noProcWarnNum = 0;
			int elimWarnNum = 0;
			int noProcUnitNum = 0;
			totalWarnNum += totalWarnNums[i];
			noProcWarnNum += noProcWarnNums[i];
			elimWarnNum += elimWarnNums[i];
			
			warnProcRates[i] = 0;
			if(totalWarnNum > 0) {
				warnProcRates[i] = 100 * ((float) elimWarnNum / (float) totalWarnNum);
				BigDecimal bd = new BigDecimal(warnProcRates[i]);
				warnProcRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRates[i] > 100) {
					warnProcRates[i] = 100;
				}
			}
			//无区号且预警数为0的不输出
			if(i == distCount && totalWarnNum == 0) {  
				continue;
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(curTdd.getName());
			}
			/**
			 * 预警数
			 */
			busiDetDataInfo.setWarnNum(totalWarnNum);
			/**
			 * 未处理预警数
			 */
			busiDetDataInfo.setNoProcWarnNum(totalWarnNum - elimWarnNum);
			/**
			 * 处理率
			 */
			busiDetDataInfo.setProcRate(warnProcRates[i]);
			busiDetDataInfo.setElimWarnNum(elimWarnNum);
			
			//未处理预警单位
			busiDetDataInfo.setNoProcUnitNum(noProcUnitNum);
			
			busiDetDataInfoMap.put(curTdd.getName(), busiDetDataInfo);
		}
	}
	
	/**
	 * 获取预警汇总：预警数量、未处理预警、处理率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getWarnInfoWarnProcRateByDepartmentTwo(String departmentId,String distId, String[] dates, Integer target,List<DepartmentObj> deparmentList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveWarnService dbHiveWarnService) {
		
		String startDate = dates[dates.length-1];
		String endDate = dates[0];
		/**
		 * 1.从hive库中获取汇总数据
		 */
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
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(1, 4, target, listYearMonth, startDate, endDateAddOne,
				distId, null, -1, -1, null, null, departmentId, null, 4);
		
		int distCount = deparmentList.size();
		int[] totalWarnNums = new int[distCount];
		int[] noProcWarnNums = new int[distCount];
		int[] elimWarnNums = new int[distCount];
		float[] warnProcRates = new float[distCount];
		
		//区域ID到索引映射
		Map<String, Integer> distIdToIdxMap = new HashMap<>();
		for(int i = 0; i < distCount; i++) {
			distIdToIdxMap.put(deparmentList.get(i).getDepartmentId(), i);
		}
		
		for(WarnCommon warnCommonLics : warnCommonList) {
			if(distIdToIdxMap.containsKey(warnCommonLics.getDepartmentId())) {
				int idx = distIdToIdxMap.get(warnCommonLics.getDepartmentId());
				noProcWarnNums[idx] += warnCommonLics.getNoProcWarnNum();
				elimWarnNums[idx] += warnCommonLics.getElimWarnNum();
				totalWarnNums[idx] += (warnCommonLics.getNoProcWarnNum() + warnCommonLics.getRejectWarnNum() + warnCommonLics.getAuditWarnNum() + warnCommonLics.getElimWarnNum());
			}
		}
		
		for (int i = 0; i < distCount; i++) {
			String curDistId = "-";
			DepartmentObj curTdd = new DepartmentObj();
			if(i < distCount) {
				curTdd = deparmentList.get(i);
				curDistId = curTdd.getDepartmentId();
			}
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equalsIgnoreCase(distId)) {
					continue;
				}
			}
			int totalWarnNum = 0;
			int noProcWarnNum = 0;
			int elimWarnNum = 0;
			int noProcUnitNum = 0;
			totalWarnNum += totalWarnNums[i];
			noProcWarnNum += noProcWarnNums[i];
			elimWarnNum += elimWarnNums[i];
			
			warnProcRates[i] = 0;
			if(totalWarnNum > 0) {
				warnProcRates[i] = 100 * ((float) elimWarnNum / (float) totalWarnNum);
				BigDecimal bd = new BigDecimal(warnProcRates[i]);
				warnProcRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRates[i] > 100) {
					warnProcRates[i] = 100;
				}
			}
			//无区号且预警数为0的不输出
			if(i == distCount && totalWarnNum == 0) {  
				continue;
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getDepartmentName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(curTdd.getDepartmentName());
			}
			/**
			 * 预警数
			 */
			busiDetDataInfo.setWarnNum(totalWarnNum);
			/**
			 * 未处理预警数
			 */
			busiDetDataInfo.setNoProcWarnNum(totalWarnNum - elimWarnNum);
			/**
			 * 处理率
			 */
			busiDetDataInfo.setProcRate(warnProcRates[i]);
			busiDetDataInfo.setElimWarnNum(elimWarnNum);
			
			//未处理预警单位
			busiDetDataInfo.setNoProcUnitNum(noProcUnitNum);
			
			busiDetDataInfoMap.put(curTdd.getDepartmentName(), busiDetDataInfo);
		}
	}
	
	/**
	 * 获取预警汇总：未处理单位、预警数量、未处理预警、处理率（只获取学校证书预警（已和产品确认））
	 * @param distId
	 * @param dates
	 * @param saasService
	 * @return
	 */
	private void getWarnInfoNoProcUnitNumByNature(String distId, String[] dates,Map<Integer, String> schoolPropertyMap, SaasService saasService,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,EduSchoolService eduSchoolService) {
		
		Map<String,Set<String>> warnUnitMap = new HashMap<String,Set<String>>();
		//所有单位
		Set<String> warnUnitSet = new HashSet<String>();
		Map<String, String> warnDetailMap = new HashMap<>();
		int i;
		int k;
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		//供应商id和名称
		Map<String, String> supIdToNameMap = new HashMap<>();
    	List<TProSupplierDo> tpsDoList = saasService.getIdSupplierIdName();
    	if(tpsDoList != null) {
    		for(i = 0; i < tpsDoList.size(); i++) {
    			supIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
    		}
    	}
    	
    	List<TEduSchoolSupplierDo> tEduSchoolSupplierDoList = saasService.getAllIdSupplierIdSchoolId(null);
    	//Key:SupplierId value:shcooolId
    	Map<String,TEduSchoolSupplierDo> schoolSupplierMap = tEduSchoolSupplierDoList.stream().collect(Collectors.toMap(TEduSchoolSupplierDo::getId,Function.identity(),(oldData,newData)->newData));
    	
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		
		Map<String,Integer> totalWarnNumMap = new HashMap<String,Integer>();//总预警数
		Map<String,Integer> noProcWarnNumMap = new HashMap<String,Integer>();//未处理预警数
		Map<String,Integer> elimWarnNumMap = new HashMap<String,Integer>();//已处理预警总数
		
		String supplierId ="";
		String schoolId = "";
		String nature = "";
    	// 时间段内各区学校餐厨垃圾详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_warnDetail";
			warnDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (warnDetailMap != null) {
				for (String curKey : warnDetailMap.keySet()) {
					//只获取学校的证书预警
					if(curKey.indexOf("school")!=0) {
						continue;
					}
					keyVal = warnDetailMap.get(curKey);
					// 证照预警全部证件详情列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 16) {
						//区
						i = AppModConfig.getVarValIndex(keyVals, "area");
						//判断区域（判断索引0）
						if(distId != null) {
							String curDistName = keyVals[i];
							if(!curDistName.equalsIgnoreCase(distId)) {
								continue;
							}
						}
						
						//触发预警单位
						i = AppModConfig.getVarValIndex(keyVals, "supplierid");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								if(supIdToNameMap.containsKey(keyVals[i])) {
									supplierId = keyVals[i];
								}
							}
						}
						 
						//状态
						i = AppModConfig.getVarValIndex(keyVals, "status");
						
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								if(schoolSupplierMap.get(supplierId) == null) {
									continue;
								}
								supplierId =keyVals[3];
								schoolId = schoolSupplierMap.get(supplierId).getSchoolId();
								nature = schoolMap.get(schoolId).getSchoolNature();
								
								
								if(keyVals[i].equalsIgnoreCase("1")) {   
									 //未处理预警数
									noProcWarnNumMap.put(nature, noProcWarnNumMap.get(nature)+1);
									totalWarnNumMap.put(nature, totalWarnNumMap.get(nature)+1);
									
								}
								else if(keyVals[i].equalsIgnoreCase("2")) {   
									 //审核中预警数
									noProcWarnNumMap.put(nature, noProcWarnNumMap.get(nature)+1);
									totalWarnNumMap.put(nature, totalWarnNumMap.get(nature)+1);
								}
								else if(keyVals[i].equalsIgnoreCase("3")) {   
									 //已驳回预警数
									noProcWarnNumMap.put(nature, noProcWarnNumMap.get(nature)+1);
									totalWarnNumMap.put(nature, totalWarnNumMap.get(nature)+1);
								}
								else if(keyVals[i].equalsIgnoreCase("4")) {    
									elimWarnNumMap.put(nature, elimWarnNumMap.get(nature)+1);
									//已消除预警数
									totalWarnNumMap.put(nature, totalWarnNumMap.get(nature)+1);
								}
								
								if(!keyVals[i].equalsIgnoreCase("4")) {
									warnUnitSet=warnUnitMap.get(nature);
									if(warnUnitSet == null) {
										warnUnitSet = new HashSet<String>();
									}
									
									warnUnitSet.add(schoolId);
									
									warnUnitMap.put(nature, warnUnitSet);
									
								}else {
									continue;
								}
							}
						}
						
						   
					}
					else {
						logger.info("菜品供应明细："+ curKey + "，格式错误！");
					}
				}
			}
			
			float warnProcRate = 0;
			for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				warnProcRate = 0;
				if(totalWarnNumMap.get(entry.getKey().toString()) !=null &&  totalWarnNumMap.get(entry.getKey().toString())> 0) {
					warnProcRate = 100 * ((float) elimWarnNumMap.get(entry.getKey().toString()) / (float) totalWarnNumMap.get(entry.getKey().toString()));
					BigDecimal bd = new BigDecimal(warnProcRate);
					warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (warnProcRate > 100) {
						warnProcRate = 100;
					}
				}
				
				
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
				}
				
				/**
				 * 未处理单位数
				 */
				busiDetDataInfo.setNoProcUnitNum(warnUnitMap.get(entry.getKey().toString())==null?0:warnUnitMap.get(entry.getKey().toString()).size());
				
				/**
				 * 预警数
				 */
				busiDetDataInfo.setWarnNum(totalWarnNumMap.get(entry.getKey().toString())==null?0:totalWarnNumMap.get(entry.getKey().toString()));
				/**
				 * 未处理预警数
				 */
				busiDetDataInfo.setNoProcWarnNum(noProcWarnNumMap.get(entry.getKey().toString())==null?0:noProcWarnNumMap.get(entry.getKey().toString()));
				/**
				 * 处理率
				 */
				busiDetDataInfo.setProcRate(warnProcRate);
				
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
				
			}
		}
	}
	
	/**
	 * 获取预警汇总：未处理单位、预警数量、未处理预警、处理率，从hive中获取
	 * @param distId
	 * @param dates
	 * @param saasService
	 * @return
	 */
	private void getWarnInfoNoProcUnitNumByNatureTwo(String departmentId,Integer target, String[] dates,Map<Integer, String> schoolPropertyMap, 
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveWarnService dbHiveWarnService) {
		
		
		String startDate = dates[0];
		String endDate = dates[dates.length - 1];
		/**
		 * 1.从hive库中获取汇总数据
		 */
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
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(1, 4, target, listYearMonth, startDate, endDateAddOne,
				null, null, -1, -1, null, null, departmentId, null, 1);
		
		Map<String,Integer> totalWarnNumMap = new HashMap<String,Integer>();//总预警数
		Map<String,Integer> noProcWarnNumMap = new HashMap<String,Integer>();//未处理预警数
		Map<String,Integer> elimWarnNumMap = new HashMap<String,Integer>();//已处理预警总数
		
		
		Integer curWarnNum = 0;
		Integer count = 0;
		
		if(warnCommonList != null && warnCommonList.size()>0) {
			for(WarnCommon warnCommon : warnCommonList) {
				if(warnCommon !=null) {
					if(StringUtils.isNotEmpty(warnCommon.getNature()) && CommonUtil.isInteger(warnCommon.getNature())) {
						String nature = warnCommon.getNature();
						//如果学校性质为空或者-1，学校性质归为其他
						if("-".equals(nature)) {
							nature = "4";
						}
						//if(StringUtils.isEmpty(nature)) {
						//	continue;
						//}
						
						//未处理预警数
						curWarnNum = warnCommon.getNoProcWarnNum();
						if(noProcWarnNumMap.get(nature)==null) {
							count = curWarnNum;
						}else {
							count = noProcWarnNumMap.get(nature)+curWarnNum;
						}
						noProcWarnNumMap.put(nature, count);
						
						//预警总数
						curWarnNum = warnCommon.getNoProcWarnNum() + warnCommon.getRejectWarnNum() + warnCommon.getAuditWarnNum() + warnCommon.getElimWarnNum();
						if(totalWarnNumMap.get(nature)==null) {
							count = curWarnNum;
						}else {
							count = totalWarnNumMap.get(nature)+curWarnNum;
						}
						totalWarnNumMap.put(nature, count);
						
					}
				}
			}
		}
			
		float warnProcRate = 0;
		Integer totalWarnNum = 0;
		Integer elimWarnNum = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			warnProcRate = 0;
			
			//预警总数
			totalWarnNum = totalWarnNumMap.get(entry.getKey().toString())==null?0:totalWarnNumMap.get(entry.getKey().toString());
			//已消除预警数
			elimWarnNum = elimWarnNumMap.get(entry.getKey().toString())==null?0:elimWarnNumMap.get(entry.getKey().toString());
			
			if(totalWarnNumMap.get(entry.getKey().toString()) !=null &&  totalWarnNumMap.get(entry.getKey().toString())> 0) {
				warnProcRate = 100 * ((float) elimWarnNum / (float) totalWarnNum);
				BigDecimal bd = new BigDecimal(warnProcRate);
				warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRate > 100) {
					warnProcRate = 100;
				}
			}
			
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//学校性质名称
				busiDetDataInfo.setStatPropName(entry.getValue());
			}
			
			/**
			 * 未处理数
			 */
			busiDetDataInfo.setElimWarnNum(elimWarnNum);
			
			/**
			 * 预警数
			 */
			busiDetDataInfo.setWarnNum(totalWarnNum);
			/**
			 * 未处理预警数
			 */
			busiDetDataInfo.setNoProcWarnNum(totalWarnNum - elimWarnNum);
			/**
			 * 处理率
			 */
			busiDetDataInfo.setProcRate(warnProcRate);
			
			busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
			
		}
	}

	/**
	 * 获取预警汇总：未处理单位、预警数量、未处理预警、处理率（只获取学校证书预警（已和产品确认））
	 * @param distId
	 * @param dates
	 * @param saasService
	 * @return
	 */
	private void getWarnInfoNoProcUnitNumBySchoolType(String distId, String[] dates,Map<Integer, String> schoolPropertyMap, SaasService saasService,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,EduSchoolService eduSchoolService) {
		
		Map<String,Set<String>> warnUnitMap = new HashMap<String,Set<String>>();
		//所有单位
		Set<String> warnUnitSet = new HashSet<String>();
		Map<String, String> warnDetailMap = new HashMap<>();
		int i;
		int k;
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		//供应商id和名称
		Map<String, String> supIdToNameMap = new HashMap<>();
    	List<TProSupplierDo> tpsDoList = saasService.getIdSupplierIdName();
    	if(tpsDoList != null) {
    		for(i = 0; i < tpsDoList.size(); i++) {
    			supIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
    		}
    	}
    	
    	List<TEduSchoolSupplierDo> tEduSchoolSupplierDoList = saasService.getAllIdSupplierIdSchoolId(null);
    	//Key:SupplierId value:shcooolId
    	Map<String,TEduSchoolSupplierDo> schoolSupplierMap = tEduSchoolSupplierDoList.stream().collect(Collectors.toMap(TEduSchoolSupplierDo::getId,Function.identity(),(oldData,newData)->newData));
    	
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		
		Map<String,Integer> totalWarnNumMap = new HashMap<String,Integer>();//总预警数
		Map<String,Integer> noProcWarnNumMap = new HashMap<String,Integer>();//未处理预警数
		Map<String,Integer> elimWarnNumMap = new HashMap<String,Integer>();//已处理预警总数
		
		String supplierId ="";
		String schoolId = "";
		String level = "";
    	// 时间段内各区学校餐厨垃圾详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_warnDetail";
			warnDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (warnDetailMap != null) {
				for (String curKey : warnDetailMap.keySet()) {
					//只获取学校的证书预警
					if(curKey.indexOf("school")!=0) {
						continue;
					}
					keyVal = warnDetailMap.get(curKey);
					// 证照预警全部证件详情列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 16) {
						//区
						i = AppModConfig.getVarValIndex(keyVals, "area");
						//判断区域（判断索引0）
						if(distId != null) {
							String curDistName = keyVals[i];
							if(!curDistName.equalsIgnoreCase(distId)) {
								continue;
							}
						}
						
						//触发预警单位
						String trigWarnUnit = "";
						i = AppModConfig.getVarValIndex(keyVals, "supplierid");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								if(supIdToNameMap.containsKey(keyVals[i])) {
									trigWarnUnit = supIdToNameMap.get(keyVals[i]);
								}
							}
						}
						 
						//状态
						i = AppModConfig.getVarValIndex(keyVals, "status");
						
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								supplierId =keyVals[3];
								if(schoolSupplierMap.get(supplierId)==null) {
									continue;
								}
								schoolId = schoolSupplierMap.get(supplierId).getSchoolId();
								level = schoolMap.get(schoolId).getLEVEL();
								
								
								if(keyVals[i].equalsIgnoreCase("1")) {   
									 //未处理预警数
									noProcWarnNumMap.put(level, noProcWarnNumMap.get(level)+1);
									totalWarnNumMap.put(level, totalWarnNumMap.get(level)+1);
									
								}
								else if(keyVals[i].equalsIgnoreCase("2")) {   
									 //审核中预警数
									noProcWarnNumMap.put(level, noProcWarnNumMap.get(level)+1);
									totalWarnNumMap.put(level, totalWarnNumMap.get(level)+1);
								}
								else if(keyVals[i].equalsIgnoreCase("3")) {   
									 //已驳回预警数
									noProcWarnNumMap.put(level, noProcWarnNumMap.get(level)+1);
									totalWarnNumMap.put(level, totalWarnNumMap.get(level)+1);
								}
								else if(keyVals[i].equalsIgnoreCase("4")) {    
									elimWarnNumMap.put(level, elimWarnNumMap.get(level)+1);
									//已消除预警数
									totalWarnNumMap.put(level, totalWarnNumMap.get(level)+1);
								}
								
								if(!keyVals[i].equalsIgnoreCase("4")) {
									warnUnitSet=warnUnitMap.get(level);
									if(warnUnitSet == null) {
										warnUnitSet = new HashSet<String>();
									}
									
									warnUnitSet.add(schoolId);
									
									warnUnitMap.put(level, warnUnitSet);
									
								}else {
									continue;
								}
							}
						}
						
						   
					}
					else {
						logger.info("菜品供应明细："+ curKey + "，格式错误！");
					}
				}
			}
			
			float warnProcRate = 0;
			for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				warnProcRate = 0;
				if(totalWarnNumMap.get(entry.getKey().toString())!=null && totalWarnNumMap.get(entry.getKey().toString()) > 0) {
					warnProcRate = 100 * ((float) elimWarnNumMap.get(entry.getKey().toString()) / (float) totalWarnNumMap.get(entry.getKey().toString()));
					BigDecimal bd = new BigDecimal(warnProcRate);
					warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (warnProcRate > 100) {
						warnProcRate = 100;
					}
				}
				
				
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
					busiDetDataInfo.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				
				/**
				 * 未处理单位数
				 */
				busiDetDataInfo.setNoProcUnitNum(warnUnitMap.get(entry.getKey().toString())==null?0:warnUnitMap.get(entry.getKey().toString()).size());
				
				/**
				 * 预警数
				 */
				busiDetDataInfo.setWarnNum(totalWarnNumMap.get(entry.getKey().toString())==null?0:totalWarnNumMap.get(entry.getKey().toString()));
				/**
				 * 未处理预警数
				 */
				busiDetDataInfo.setNoProcWarnNum(noProcWarnNumMap.get(entry.getKey().toString())==null?0:noProcWarnNumMap.get(entry.getKey().toString()));
				/**
				 * 处理率
				 */
				busiDetDataInfo.setProcRate(warnProcRate);
				
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
				
			}
		}
	}
	
	/**
	 * 获取预警汇总：未处理单位、预警数量、未处理预警、处理率（只获取学校证书预警（已和产品确认））
	 * @param distId
	 * @param dates
	 * @param saasService
	 * @return
	 */
	private void getWarnInfoNoProcUnitNumBySchoolTypeTwo(String departmentId, String[] dates,Integer target,Map<Integer, String> schoolPropertyMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveWarnService dbHiveWarnService) {
		
		
		String startDate = dates[0];
		String endDate = dates[dates.length - 1];
		/**
		 * 1.从hive库中获取汇总数据
		 */
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
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(1, 4, target, listYearMonth, startDate, endDateAddOne,
				null, null, -1, -1, null, null, departmentId, null, 2);
		
		Map<String,Integer> totalWarnNumMap = new HashMap<String,Integer>();//总预警数
		Map<String,Integer> noProcWarnNumMap = new HashMap<String,Integer>();//未处理预警数
		Map<String,Integer> elimWarnNumMap = new HashMap<String,Integer>();//已处理预警总数
		
		
		
		Integer curWarnNum = 0;
		Integer count = 0;
		
		if(warnCommonList != null && warnCommonList.size()>0) {
			for(WarnCommon warnCommon : warnCommonList) {
				if(warnCommon !=null) {
					if(StringUtils.isNotEmpty(warnCommon.getLevel()) && CommonUtil.isInteger(warnCommon.getLevel())) {
						String level = warnCommon.getLevel();
						//如果学校性质为空或者-1，学校性质归为其他
						if("-".equals(level)) {
							level = "17";
						}
						//if(StringUtils.isEmpty(level)) {
						//	continue;
						//}
						
						//未处理预警数
						curWarnNum = warnCommon.getNoProcWarnNum();
						if(noProcWarnNumMap.get(level)==null) {
							count = curWarnNum;
						}else {
							count = noProcWarnNumMap.get(level)+curWarnNum;
						}
						noProcWarnNumMap.put(level, count);
						
                        //预警总数		
						curWarnNum = warnCommon.getNoProcWarnNum() + warnCommon.getRejectWarnNum() + warnCommon.getAuditWarnNum() + warnCommon.getElimWarnNum();
						if(totalWarnNumMap.get(level)==null) {
							count = curWarnNum;
						}else {
							count = totalWarnNumMap.get(level)+curWarnNum;
						}
						totalWarnNumMap.put(level, count);
						
					}
				}
			}
		}
		
		float warnProcRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			warnProcRate = 0;
			Integer totalWarnNum =totalWarnNumMap.get(entry.getKey().toString())==null?0:totalWarnNumMap.get(entry.getKey().toString());
			Integer elimWarnNum =elimWarnNumMap.get(entry.getKey().toString())==null?0:elimWarnNumMap.get(entry.getKey().toString());
			
			if(totalWarnNumMap.get(entry.getKey().toString())!=null && totalWarnNumMap.get(entry.getKey().toString()) > 0) {
				warnProcRate = 100 * ((float) elimWarnNum / (float) totalWarnNum);
				BigDecimal bd = new BigDecimal(warnProcRate);
				warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRate > 100) {
					warnProcRate = 100;
				}
			}
			
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//学校性质名称
				busiDetDataInfo.setStatPropName(entry.getValue());
				busiDetDataInfo.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
			}
			
			/**
			 * 未处理数
			 */
			busiDetDataInfo.setElimWarnNum(elimWarnNum);
			
			/**
			 * 预警数
			 */
			busiDetDataInfo.setWarnNum(totalWarnNum);
			/**
			 * 未处理预警数
			 */
			busiDetDataInfo.setNoProcWarnNum(totalWarnNum - elimWarnNum);
			/**
			 * 处理率
			 */
			busiDetDataInfo.setProcRate(warnProcRate);
			
			busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
			
		}
	}
	
	/**
	 * 获取预警汇总：未处理单位（按所属主管部门）
	 * （只获取学校和人员证书预警（已和产品确认））
	 * @param distId
	 * @param dates
	 * @param saasService
	 * @return
	 */
	private void getWarnInfoNoProcUnitNumBySlave(String distId, String[] dates,Map<String, String> slaveMap, SaasService saasService,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,EduSchoolService eduSchoolService) {
		
		Map<String,Set<String>> warnUnitMap = new HashMap<String,Set<String>>();
		//所有单位
		Set<String> warnUnitSet = new HashSet<String>();
		Map<String, String> warnDetailMap = new HashMap<>();
		int i;
		int k;
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		//供应商id和名称
		Map<String, String> supIdToNameMap = new HashMap<>();
    	List<TProSupplierDo> tpsDoList = saasService.getIdSupplierIdName();
    	if(tpsDoList != null) {
    		for(i = 0; i < tpsDoList.size(); i++) {
    			supIdToNameMap.put(tpsDoList.get(i).getId(), tpsDoList.get(i).getSupplierName());
    		}
    	}
    	
    	List<TEduSchoolSupplierDo> tEduSchoolSupplierDoList = saasService.getAllIdSupplierIdSchoolId(null);
    	//Key:SupplierId value:shcooolId
    	Map<String,TEduSchoolSupplierDo> schoolSupplierMap = tEduSchoolSupplierDoList.stream().collect(Collectors.toMap(TEduSchoolSupplierDo::getId,Function.identity(),(oldData,newData)->newData));
    	
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		
		String supplierId ="";
		String schoolId = "";
		String masterId = "";
		String slaveId = "";
		String slave = "";
		String resultKey = "";//结果集的可以（masterid_slaveId(如果masterId是3，则为区域名称)）
    	// 时间段内各区学校餐厨垃圾详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_warnDetail";
			warnDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (warnDetailMap != null) {
				for (String curKey : warnDetailMap.keySet()) {
					//school Filed格式：area_14_supplierid_15f4866d-0925-4f95-9586-be1f20b3ee0f_warntypechild_1_licno_JY23101090003184_losetime_2019-01-3000: 00: 00_remaintime_1_status_1_dealtime_null
					//people Filed格式：area_13_supplierid_4f5de487-8913-40c8-bcce-7a538be4ec29_warntypechild_20_licno_449818011508211110342380458_losetime_2019-01-1500: 00: 00_remaintime_1_status_2_dealtime_null_schoolid_10c86441-472d-46c5-b15e-5ab47eb88dbb_schoolname_上海市普陀区康泰幼儿园_writtenname_刘万勤
					//只获取学校的证书预警
					if(curKey.indexOf("school")!=0 && curKey.indexOf("people")!=0) {
						continue;
					}
					keyVal = warnDetailMap.get(curKey);
					// 证照预警全部证件详情列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 16) {
						//区
						/*i = AppModConfig.getVarValIndex(keyVals, "area");
						//判断区域（判断索引0）
						if(distId != null) {
							String curDistName = keyVals[i];
							if(!curDistName.equalsIgnoreCase(distId)) {
								continue;
							}
						}*/
						
						//触发预警单位
						i = AppModConfig.getVarValIndex(keyVals, "supplierid");
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								if(supIdToNameMap.containsKey(keyVals[i])) {
									supplierId = keyVals[i];
								}
							}
						}
						 
						//状态
						i = AppModConfig.getVarValIndex(keyVals, "status");
						
						if(i != -1) {
							if(!keyVals[i].equalsIgnoreCase("null")) {
								if(schoolSupplierMap.get(supplierId) == null) {
									continue;
								}
								supplierId =keyVals[3];
								schoolId = schoolSupplierMap.get(supplierId).getSchoolId();
								if(schoolMap.get(schoolId)==null) {
									continue;
								}
								
								masterId = schoolMap.get(schoolId).getDepartmentMasterId();
								slaveId = schoolMap.get(schoolId).getDepartmentSlaveId();
								//slave = schoolMap.get(schoolId).getDepartmentSlave();
								
								resultKey =masterId+"_"+slaveId;
								if("3".equals(masterId)) {
									resultKey = masterId+"_"+AppModConfig.compDepIdToNameMap3bd.get(slaveId);
								}
								
								if(!keyVals[i].equalsIgnoreCase("4")) {
									warnUnitSet=warnUnitMap.get(resultKey);
									if(warnUnitSet == null) {
										warnUnitSet = new HashSet<String>();
									}
									
									warnUnitSet.add(schoolId);
									
									warnUnitMap.put(resultKey, warnUnitSet);
									
								}else {
									continue;
								}
							}
						}
						
						   
					}
					else {
						logger.info("菜品供应明细："+ curKey + "，格式错误！");
					}
				}
			}
			
			for(Map.Entry<String, String> entry : slaveMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
				if(busiDetDataInfo==null) {
					String[] keys = entry.getKey().split("_");
					String masterid = keys[0];
					slave = keys[1];
					busiDetDataInfo = new BusiDetDataInfo();
					busiDetDataInfo.setStatClassName(entry.getValue());
					//所属主管部门
					String slaveName=slave;
					
					if("0".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
					}else if ("1".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
					}else if ("2".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
					}
					
					busiDetDataInfo.setStatPropName(slaveName);
				}
				
				/**
				 * 未处理单位数
				 */
				busiDetDataInfo.setNoProcUnitNum(warnUnitMap.get(entry.getKey().toString())==null?0:warnUnitMap.get(entry.getKey().toString()).size());
				busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
				
			}
		}
	}
	
	/**
	 * 获取预警汇总：预警数量、未处理预警、处理率（按所属主管部门）
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getWarnInfoWarnProcRateBySlave(String distId, String[] dates, Map<String, String> slaveMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		int k;
		int j;
		int curWarnNum = 0;
		Map<String,Integer> totalWarnNumMap = new HashMap<String,Integer>();
		Map<String,Integer> noProcWarnNumMap = new HashMap<String,Integer>();
		Map<String,Integer> elimWarnNumMap = new HashMap<String,Integer>();
		int count = 0;
		
		// 时间段预警总数
		Map<String, String> warnTotalMap = null;
		// 时间段内各区预警统计
		for (k = 0; k < dates.length; k++) {
			// 供应数量
			key = dates[k] + "_warn-total";
			warnTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(warnTotalMap == null) {    
				
			}
			if(warnTotalMap != null) {
				for(String curKey : warnTotalMap.keySet()) {
					String[] curKeys = curKey.split("_");
					if((curKey.indexOf("school_masterid_")==0 ||curKey.indexOf("people_masterid_")==0 ) &&  curKeys.length >= 7) {
						
						if("3".equals(curKeys[2]) && curKeys[4]==null) {
                        	curKeys[4] ="其他";
                        }
					    //统一判断
						// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						/*if (distId != null) {
							if (!curKeys[i].equalsIgnoreCase(distId)) {
								continue;
							}
						}*/
						
						keyVal = warnTotalMap.get(curKey);
						curWarnNum = 0;
						if(keyVal != null) {
							curWarnNum = Integer.parseInt(keyVal);
						}
						if(curWarnNum < 0) {
							curWarnNum = 0;
						}
						j = AppModConfig.getVarValIndex(curKeys, "status");
						if(j != -1) {
							
						  if(curKeys[j].equalsIgnoreCase("1") || curKeys[j].equalsIgnoreCase("2") || curKeys[j].equalsIgnoreCase("3")) {   
								 //审核中预警数
								if(noProcWarnNumMap.get(curKeys[2]+"_"+curKeys[4])==null) {
									count = curWarnNum;
								}else {
									count = noProcWarnNumMap.get(curKeys[2]+"_"+curKeys[4])+curWarnNum;
								}
								noProcWarnNumMap.put(curKeys[2]+"_"+curKeys[4], count);
								
								if(totalWarnNumMap.get(curKeys[2]+"_"+curKeys[4])==null) {
									count = curWarnNum;
								}else {
									count = totalWarnNumMap.get(curKeys[2]+"_"+curKeys[4])+curWarnNum;
								}
								totalWarnNumMap.put(curKeys[2]+"_"+curKeys[4], count);
							}
							else if(curKeys[j].equalsIgnoreCase("4")) {    
								//已消除预警数
								if(elimWarnNumMap.get(curKeys[2]+"_"+curKeys[4])==null) {
									count = curWarnNum;
								}else {
									count = elimWarnNumMap.get(curKeys[2]+"_"+curKeys[4])+curWarnNum;
								}
								elimWarnNumMap.put(curKeys[2]+"_"+curKeys[4], count);
								
								if(totalWarnNumMap.get(curKeys[2]+"_"+curKeys[4])==null) {
									count = curWarnNum;
								}else {
									count = totalWarnNumMap.get(curKeys[2]+"_"+curKeys[4])+curWarnNum;
								}
								totalWarnNumMap.put(curKeys[2]+"_"+curKeys[4], count);
							}
						}
					}
				}
			}
		}
		
		float warnProcRate = 0;
		for(Map.Entry<String, String> entry : slaveMap.entrySet() ) {
			int totalWarnNum = totalWarnNumMap.get(entry.getKey())==null?0:totalWarnNumMap.get(entry.getKey());
			int noProcWarnNum = noProcWarnNumMap.get(entry.getKey())==null?0:noProcWarnNumMap.get(entry.getKey());
			int elimWarnNum = elimWarnNumMap.get(entry.getKey())==null?0:elimWarnNumMap.get(entry.getKey());
			warnProcRate = 0;
			if(totalWarnNum > 0) {
				warnProcRate = 100 * ((float) elimWarnNum / (float) totalWarnNum);
				BigDecimal bd = new BigDecimal(warnProcRate);
				warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRate > 100) {
					warnProcRate = 100;
				}
			}
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
			if(busiDetDataInfo==null) {
				String[] keys = entry.getKey().split("_");
				String masterid = keys[0];
				String slave = keys[1];
				busiDetDataInfo = new BusiDetDataInfo();
				busiDetDataInfo.setStatClassName(entry.getValue());
				//所属主管部门
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				busiDetDataInfo.setStatPropName(slaveName);
			}
			/**
			 * 预警数
			 */
			busiDetDataInfo.setWarnNum(totalWarnNum);
			/**
			 * 未处理预警数
			 */
			busiDetDataInfo.setNoProcWarnNum(noProcWarnNum);
			/**
			 * 处理率
			 */
			busiDetDataInfo.setProcRate(warnProcRate);
			
			busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
		}
	}
	
	/**
	 * 获取预警汇总：预警单、预警数量、未处理预警、处理率（按所属主管部门）（由redis改为从hive中获取）
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getWarnInfoWarnProcRateBySlaveTwo(String departmentId, String[] dates,Integer target, Map<String, String> slaveMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveWarnService dbHiveWarnService) {
		
		
		String startDate = dates[dates.length-1];
		String endDate = dates[0];
		/**
		 * 1.从hive库中获取汇总数据
		 */
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
		List<WarnCommon> warnCommonList = new ArrayList<>();
		warnCommonList = dbHiveWarnService.getWarnList(1, 4, target, listYearMonth, startDate, endDateAddOne,
				null, null, -1, -1, null, null, departmentId, null, 3);
		
		Map<String,Integer> totalWarnNumMap = new HashMap<String,Integer>();
		Map<String,Integer> noProcWarnNumMap = new HashMap<String,Integer>();
		Map<String,Integer> elimWarnNumMap = new HashMap<String,Integer>();
		int count = 0;
		int curWarnNum = 0;
		
		if(warnCommonList != null && warnCommonList.size()>0) {
			for(WarnCommon warnCommon : warnCommonList) {
				count = 0;
				if(warnCommon !=null) {
					if(StringUtils.isNotEmpty(warnCommon.getDepartmentSlaveIdName()) && CommonUtil.isInteger(warnCommon.getDepartmentSlaveIdName())) {
						
						if(CommonUtil.isEmpty(warnCommon.getDepartmentMasterId()) ) {
							continue;
						}
						String departmentSlaveId = warnCommon.getDepartmentSlaveIdName();
						if(CommonUtil.isNotEmpty(warnCommon.getDepartmentMasterId()) && "3".equals(warnCommon.getDepartmentMasterId())) {
							departmentSlaveId = AppModConfig.compDepIdToNameMap3.get(warnCommon.getDepartmentSlaveIdName());
							if(StringUtils.isEmpty(departmentSlaveId)) {
								continue;
							}
						}
						
						//未处理预警数
						curWarnNum = warnCommon.getNoProcWarnNum();
						if(noProcWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)==null) {
							count = curWarnNum;
						}else {
							count = noProcWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)+curWarnNum;
						}
						noProcWarnNumMap.put(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId, count);
						
						if(totalWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)==null) {
							count = curWarnNum;
						}else {
							count = totalWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)+curWarnNum;
						}
						totalWarnNumMap.put(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId, count);
						
						//审核中预警数
						curWarnNum = warnCommon.getAuditWarnNum();
						if(totalWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)==null) {
							count = curWarnNum;
						}else {
							count = totalWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)+curWarnNum;
						}
						totalWarnNumMap.put(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId, count);
						
						//已驳回预警数
						curWarnNum = warnCommon.getRejectWarnNum();
						if(totalWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)==null) {
							count = curWarnNum;
						}else {
							count = totalWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)+curWarnNum;
						}
						totalWarnNumMap.put(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId, count);
						
						//已消除预警数
						curWarnNum = warnCommon.getElimWarnNum();
						if(noProcWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)==null) {
							count = curWarnNum;
						}else {
							count = noProcWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)+curWarnNum;
						}
						noProcWarnNumMap.put(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId, count);
						
						if(totalWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)==null) {
							count = curWarnNum;
						}else {
							count = totalWarnNumMap.get(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId)+curWarnNum;
						}
						totalWarnNumMap.put(warnCommon.getDepartmentMasterId()+"_"+departmentSlaveId, count);
					}
				}
			}
		}
		
		float warnProcRate = 0;
		for(Map.Entry<String, String> entry : slaveMap.entrySet() ) {
			int totalWarnNum = totalWarnNumMap.get(entry.getKey())==null?0:totalWarnNumMap.get(entry.getKey());
			int noProcWarnNum = noProcWarnNumMap.get(entry.getKey())==null?0:noProcWarnNumMap.get(entry.getKey());
			int elimWarnNum = elimWarnNumMap.get(entry.getKey())==null?0:elimWarnNumMap.get(entry.getKey());
			warnProcRate = 0;
			if(totalWarnNum > 0) {
				warnProcRate = 100 * ((float) elimWarnNum / (float) totalWarnNum);
				BigDecimal bd = new BigDecimal(warnProcRate);
				warnProcRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (warnProcRate > 100) {
					warnProcRate = 100;
				}
			}
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
			if(busiDetDataInfo==null) {
				String[] keys = entry.getKey().split("_");
				String masterid = keys[0];
				String slave = keys[1];
				busiDetDataInfo = new BusiDetDataInfo();
				busiDetDataInfo.setStatClassName(entry.getValue());
				//所属主管部门
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				busiDetDataInfo.setStatPropName(slaveName);
			}
			/**
			 * 预警数
			 */
			busiDetDataInfo.setWarnNum(totalWarnNum);
			/**
			 * 未处理预警数
			 */
			busiDetDataInfo.setNoProcWarnNum(totalWarnNum - elimWarnNum);
			/**
			 * 处理率
			 */
			busiDetDataInfo.setProcRate(warnProcRate);
			
			/**
			 * 未处理预警数
			 */
			busiDetDataInfo.setElimWarnNum(elimWarnNum);
			
			busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
		}
	}
	/**
	 * 获取留样汇总：未留样学校个数
	 * @param distId
	 * @param dates
	 * @param db1Service
	 * @return
	 */
	private void getRsInfoNoRsSchNumByArea(String distId, String[] dates,List<TEduDistrictDo> tedList, Db1Service db1Service,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		Map<String,Set<String>> schoolMap = new HashMap<String,Set<String>>();
		Set<String> schoolSet = new HashSet<String>();
		Map<String, String> gcRetentiondishMap = new HashMap<String, String>();
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		int j;
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distId,1,1, 1);
		for(int i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		Map<String,TEduDistrictDo> tedMap = tedList.stream().collect(Collectors.toMap(TEduDistrictDo::getId,(b)->b));
		
		// 时间段内各区菜品留样详情
		for(int k = 0; k < dateCount; k++) {
			key = dates[k] + "_gc-retentiondish";
			gcRetentiondishMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (gcRetentiondishMap != null) {
				for (String curKey : gcRetentiondishMap.keySet()) {
					keyVal = gcRetentiondishMap.get(curKey);
					// 菜品留样列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 23) {
						if(!"未留样".equals(keyVals[14])) {
							continue;
						}
						if(distId != null) {
							if(keyVals[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						//学校信息（项目点）
						TEduSchoolDo tesDo = null;
						if(schIdMap.containsKey(keyVals[5])) {
							j = schIdMap.get(keyVals[5]);
							tesDo = tesDoList.get(j-1);
						}
						if(tesDo == null) {
							continue;
						}
						
						
						schoolSet=schoolMap.get(keyVals[1]);
						if(schoolSet == null) {
							schoolSet = new HashSet<String>();
						}
						schoolSet.add(keyVals[5]);
						
						schoolMap.put(keyVals[1], schoolSet);
					}
					else {
						logger.info("菜品留样："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
		for(Map.Entry<String, Set<String>> entry : schoolMap.entrySet() ) {
			TEduDistrictDo tEduDistrictDo =  tedMap.get(entry.getKey());
			if(tEduDistrictDo!=null && StringUtils.isNotEmpty(tEduDistrictDo.getName())) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(tEduDistrictDo.getName());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//区域名称
					busiDetDataInfo.setStatPropName(tEduDistrictDo.getName());
				}
				/**
				 * 未留样学校数
				 */
				busiDetDataInfo.setNoRsSchNum(entry.getValue()==null?0:entry.getValue().size());
				busiDetDataInfoMap.put(tEduDistrictDo.getName(), busiDetDataInfo);
			}
			
			
		}
		
	}

	/**
	 * 获取留样汇总：菜品数量、留样率、未留样菜品
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param rsInfo
	 */
	private void getRsInfoRsRateByArea(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		String field = "";
		String fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> gcRetentiondishtotalMap = null;
		int distCount = tedList.size();
		int dateCount = dates.length;
		int[][] totalDishNums = new int[dateCount][distCount];
		int[][] distRsDishNums = new int[dateCount][distCount];
		int[][] distNoRsDishNums = new int[dateCount][distCount];
		float[] distRsRate = new float[distCount];
		
		int[][] distRsSchNums = new int[dateCount][distCount];
		int[][] distNoRsSchNums = new int[dateCount][distCount];
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
			gcRetentiondishtotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(gcRetentiondishtotalMap == null) {    
				
			}
			if(gcRetentiondishtotalMap != null) {
				for(String curKey : gcRetentiondishtotalMap.keySet()) {
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						String curDistId = curTdd.getId();
						//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						if(distId != null) {
							if(curDistId.compareTo(distId) != 0) {
								continue ;
							}
						}
						// 区域菜品留样和未留样数
						fieldPrefix = curDistId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 2)
							{
								if(curKeys[1].equalsIgnoreCase("已留样")) {     
									//区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distRsDishNums[k][i] += Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[1].equalsIgnoreCase("未留样")) {    
									 //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsDishNums[k][i] += Integer.parseInt(keyVal);
									}
								}
							}
						}
						if(curKey.equalsIgnoreCase(curDistId)) {      
							//区域菜品总数
							keyVal = gcRetentiondishtotalMap.get(curKey);
							if(keyVal != null) {
								totalDishNums[k][i] += Integer.parseInt(keyVal);
							}
						}
						
						// 区域学校留样和未留样数
						fieldPrefix = "school-area" + "_" + curDistId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 3)
							{
								if(curKeys[2].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distRsSchNums[k][i] = Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[2].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsSchNums[k][i] = Integer.parseInt(keyVal);
									}
								}
							}
						}
					}
				}
			}
			// 该日期各区留样率
			for (int i = 0; i < distCount; i++) {
				TEduDistrictDo curTdd = tedList.get(i);
				String curDistId = curTdd.getId();
				field = "area" + "_" + curDistId;
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (distId != null) {
					if (curDistId.compareTo(distId) != 0) {
						continue;
					}
				}
				// 区域留样率
				if (totalDishNums[k][i] != 0) {
					distRsRate[i] = 100 * ((float) distRsDishNums[k][i] / (float) totalDishNums[k][i]);
					BigDecimal bd = new BigDecimal(distRsRate[i]);
					distRsRate[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRate[i] > 100) {
						distRsRate[i] = 100;
						distRsDishNums[k][i] = totalDishNums[k][i];
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + curTdd.getName() + "，菜品数量：" + totalDishNums[k]
						+ "，已留样菜品数：" + distRsDishNums[k] + "，未留样菜品数：" + distNoRsDishNums[k] + "，排菜率：" + distRsRate + "，field = "
						+ field);
			}
		}
		
		
		float schRsRate = 0;
		for (int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equals(distId)) {
					continue;
				}
			}
			int totalDishNum = 0;
			int distRsDishNum = 0;
			int distNoRsDishNum = 0;
			int totalDistRsSchNum = 0;
			int totalDistNoRsSchNum = 0;
			for (int k = 0; k < dates.length; k++) {
				totalDishNum += totalDishNums[k][i];
				distRsDishNum += distRsDishNums[k][i];
				distNoRsDishNum += distNoRsDishNums[k][i];
				totalDistRsSchNum += distRsSchNums[k][i];
				totalDistNoRsSchNum += distNoRsSchNums[k][i];
			}
			distRsRate[i] = 0;
			if(totalDishNum > 0) {
				distRsRate[i] = 100 * ((float) distRsDishNum / (float) totalDishNum);
				BigDecimal bd = new BigDecimal(distRsRate[i]);
				distRsRate[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distRsRate[i] > 100) {
					distRsRate[i] = 100;
				}
			}
			
			//学校留样率
			int shouldRsSchNum = totalDistNoRsSchNum + totalDistRsSchNum;
			schRsRate = 0;
			if(shouldRsSchNum > 0) {
				schRsRate = 100 * ((float) totalDistRsSchNum / (float) shouldRsSchNum);
				BigDecimal bd = new BigDecimal(schRsRate);
				schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schRsRate > 100) {
					schRsRate = 100;
				}
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(curTdd.getName());
			}
			/**
			 * 菜品数量
			 */
			busiDetDataInfo.setDishNum(totalDishNum);
			/**
			 * 已留样菜品
			 */
			busiDetDataInfo.setRsDishNum(distRsDishNum);
			/**
			 * 未留样菜品
			 */
			busiDetDataInfo.setNoRsDishNum(distNoRsDishNum);
			/**
			 * 留样率
			 */
			busiDetDataInfo.setRsRate(distRsRate[i]);
			
			/**
			 * 应留样学校数
			 */
			busiDetDataInfo.setShouldRsSchNum(shouldRsSchNum);
			/**
			 * 已留样学校数
			 */
			busiDetDataInfo.setRsSchNum(totalDistRsSchNum);
			
			/**
			 * 未留样学校数
			 */
			busiDetDataInfo.setNoRsSchNum(totalDistNoRsSchNum);
			
            /**
			 * 学校留样率
			 */
			busiDetDataInfo.setSchRsRate(schRsRate);
			
			busiDetDataInfoMap.put(curTdd.getName(), busiDetDataInfo);
		}
	   
	    
		
	}
	
	/**
	 * 获取留样汇总：菜品数量、留样率、未留样菜品
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param rsInfo
	 */
	private void getRsInfoRsRateByDepartment(String departmentId,String[] dates, List<DepartmentObj> departmentList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		String field = "";
		String fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> gcRetentiondishtotalMap = null;
		int distCount = departmentList.size();
		int dateCount = dates.length;
		int[][] totalDishNums = new int[dateCount][distCount];
		int[][] distRsDishNums = new int[dateCount][distCount];
		int[][] distNoRsDishNums = new int[dateCount][distCount];
		float[] distRsRate = new float[distCount];
		
		int[][] distRsSchNums = new int[dateCount][distCount];
		int[][] distNoRsSchNums = new int[dateCount][distCount];
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
			gcRetentiondishtotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(gcRetentiondishtotalMap == null) {    
				
			}
			if(gcRetentiondishtotalMap != null) {
				for(String curKey : gcRetentiondishtotalMap.keySet()) {
					for (int i = 0; i < departmentList.size(); i++) {
						DepartmentObj departmentObj = departmentList.get(i);
						String curDepartmentId = departmentObj.getDepartmentId();
						//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						if(departmentId != null) {
							if(curDepartmentId.compareTo(departmentId) != 0) {
								continue ;
							}
						}
						// 区域菜品留样和未留样数
						fieldPrefix = "department_"+curDepartmentId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 2)
							{
								if(curKeys[2].equalsIgnoreCase("已留样")) {     
									//区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distRsDishNums[k][i] += Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[2].equalsIgnoreCase("未留样")) {    
									 //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsDishNums[k][i] += Integer.parseInt(keyVal);
									}
								}
							}
						}
						if(curKey.equalsIgnoreCase(curDepartmentId)) {      
							//区域菜品总数
							keyVal = gcRetentiondishtotalMap.get(curKey);
							if(keyVal != null) {
								totalDishNums[k][i] += Integer.parseInt(keyVal);
							}
						}
						
						// 区域学校留样和未留样数
						fieldPrefix = "school-department" + "_" + curDepartmentId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 3)
							{
								if(curKeys[2].equalsIgnoreCase("已留样")) {     //区域留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distRsSchNums[k][i] = Integer.parseInt(keyVal);
									}
								}
								else if(curKeys[2].equalsIgnoreCase("未留样")) {     //区域未留样菜品总数
									keyVal = gcRetentiondishtotalMap.get(curKey);
									if(keyVal != null) {
										distNoRsSchNums[k][i] = Integer.parseInt(keyVal);
									}
								}
							}
						}
					}
				}
			}
			// 该日期各区留样率
			for (int i = 0; i < distCount; i++) {
				DepartmentObj departmentObj = departmentList.get(i);
				String curDepartmentId = departmentObj.getDepartmentId();
				field = "department" + "_" + curDepartmentId;
				// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
				if (departmentId != null) {
					if (curDepartmentId.compareTo(departmentId) != 0) {
						continue;
					}
				}
				// 区域留样率
				if (totalDishNums[k][i] != 0) {
					distRsRate[i] = 100 * ((float) distRsDishNums[k][i] / (float) totalDishNums[k][i]);
					BigDecimal bd = new BigDecimal(distRsRate[i]);
					distRsRate[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRate[i] > 100) {
						distRsRate[i] = 100;
						distRsDishNums[k][i] = totalDishNums[k][i];
					}
				}
				logger.info("日期：" + dates[k] + "，辖区名称：" + departmentObj.getDepartmentName() + "，菜品数量：" + totalDishNums[k]
						+ "，已留样菜品数：" + distRsDishNums[k] + "，未留样菜品数：" + distNoRsDishNums[k] + "，排菜率：" + distRsRate + "，field = "
						+ field);
			}
		}
		
		
		float schRsRate = 0;
		for (int i = 0; i < distCount; i++) {
			DepartmentObj departmentObj = departmentList.get(i);
			String curDepartmentId = departmentObj.getDepartmentId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (departmentId != null) {
				if (!curDepartmentId.equals(departmentId)) {
					continue;
				}
			}
			int totalDishNum = 0;
			int distRsDishNum = 0;
			int distNoRsDishNum = 0;
			int totalDistRsSchNum = 0;
			int totalDistNoRsSchNum = 0;
			for (int k = 0; k < dates.length; k++) {
				totalDishNum += totalDishNums[k][i];
				distRsDishNum += distRsDishNums[k][i];
				distNoRsDishNum += distNoRsDishNums[k][i];
				totalDistRsSchNum += distRsSchNums[k][i];
				totalDistNoRsSchNum += distNoRsSchNums[k][i];
			}
			distRsRate[i] = 0;
			if(totalDishNum > 0) {
				distRsRate[i] = 100 * ((float) distRsDishNum / (float) totalDishNum);
				BigDecimal bd = new BigDecimal(distRsRate[i]);
				distRsRate[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distRsRate[i] > 100) {
					distRsRate[i] = 100;
				}
			}
			
			//学校留样率
			int shouldRsSchNum = totalDistNoRsSchNum + totalDistRsSchNum;
			schRsRate = 0;
			if(shouldRsSchNum > 0) {
				schRsRate = 100 * ((float) totalDistRsSchNum / (float) shouldRsSchNum);
				BigDecimal bd = new BigDecimal(schRsRate);
				schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schRsRate > 100) {
					schRsRate = 100;
				}
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(departmentObj.getDepartmentName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(departmentObj.getDepartmentName());
			}
			/**
			 * 菜品数量
			 */
			busiDetDataInfo.setDishNum(totalDishNum);
			/**
			 * 已留样菜品
			 */
			busiDetDataInfo.setRsDishNum(distRsDishNum);
			/**
			 * 未留样菜品
			 */
			busiDetDataInfo.setNoRsDishNum(distNoRsDishNum);
			/**
			 * 留样率
			 */
			busiDetDataInfo.setRsRate(distRsRate[i]);
			
			/**
			 * 应留样学校数
			 */
			busiDetDataInfo.setShouldRsSchNum(shouldRsSchNum);
			/**
			 * 已留样学校数
			 */
			busiDetDataInfo.setRsSchNum(totalDistRsSchNum);
			
			/**
			 * 未留样学校数
			 */
			busiDetDataInfo.setNoRsSchNum(totalDistNoRsSchNum);
			
            /**
			 * 学校留样率
			 */
			busiDetDataInfo.setSchRsRate(schRsRate);
			
			busiDetDataInfoMap.put(departmentObj.getDepartmentName(), busiDetDataInfo);
		}
	   
	    
		
	}
	
	/**
	 * 获取留样汇总：未留样学校个数、菜品数量、留样率、未留样菜品
	 * @param distId
	 * @param dates
	 * @param db1Service
	 * @return
	 */
	private void getRsInfoNoRsSchNumByNature(String distId, String[] dates,Map<Integer, String> schoolPropertyMap, Db1Service db1Service,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,EduSchoolService eduSchoolService) {
		Map<String,Set<String>> schoolCountMap = new HashMap<String,Set<String>>();
		Set<String> schoolSet = new HashSet<String>();
		Map<String, String> gcRetentiondishMap = new HashMap<String, String>();
		
		//key：学校编号+餐别+菜单+菜品+日期+状态,value :对应个数
		Map<String,Float> schoolDetailMap = new HashMap<String,Float>();
		
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distId,1,1, 1);
		for(int i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		
		// 时间段内各区菜品留样详情
		float schoolDetailTotal = 0;
		for(int k = 0; k < dateCount; k++) {
			key = dates[k] + "_gc-retentiondish";
			gcRetentiondishMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (gcRetentiondishMap != null) {
				for (String curKey : gcRetentiondishMap.keySet()) {
					keyVal = gcRetentiondishMap.get(curKey);
					// 菜品留样列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 23) {
						if(distId != null) {
							if(keyVals[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						
						String schoolDetailMapKey = keyVals[5]+"_"+keyVals[13]+"_"+keyVals[7]+"_"+keyVals[9]+"_"+dates[k]+"_"+keyVals[14];
						//学校信息（项目点）
						schoolDetailTotal=schoolDetailMap.get(schoolDetailMapKey)==null?0:schoolDetailMap.get(schoolDetailMapKey);
						schoolDetailMap.put(schoolDetailMapKey, (keyVals[20]==null || "null".equals(keyVals[20]))?0:Float.parseFloat(keyVals[20])+schoolDetailTotal);
					}
					else {
						logger.info("菜品留样："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
		Map<String,Integer> totalDishNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distRsDishNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distNoRsDishNumMap = new HashMap<String,Integer>();
		
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		int count = 0;
		for(Map.Entry<String, Float> entry : schoolDetailMap.entrySet() ) {
			String keys[]=entry.getKey().split("_");
			EduSchool eduSchool =  schoolMap.get(keys[0]);
			if(eduSchool!=null && StringUtils.isNotEmpty(eduSchool.getSchoolNature())) {
				
				//已验收数量
				if(totalDishNumMap.get(eduSchool.getSchoolNature())==null) {
					count = 1;
				}else {
					count = totalDishNumMap.get(eduSchool.getSchoolNature())+1;
				}
				
				totalDishNumMap.put(eduSchool.getSchoolNature(), count);
				
				
				if(keys[5] !=null && keys[5].equals("已留样")) {
					//已验收数量
					if(distRsDishNumMap.get(eduSchool.getSchoolNature())==null) {
						count = 1;
					}else {
						count = distRsDishNumMap.get(eduSchool.getSchoolNature())+1;
					}
					
					distRsDishNumMap.put(eduSchool.getSchoolNature(), count);
				}else{
					//未验收数量
					if(distNoRsDishNumMap.get(eduSchool.getSchoolNature())==null) {
						count = 1;
					}else {
						count = distNoRsDishNumMap.get(eduSchool.getSchoolNature())+1;
					}
					
					distNoRsDishNumMap.put(eduSchool.getSchoolNature(), count);
					
					//未验收学校数
					schoolSet=schoolCountMap.get(eduSchool.getSchoolNature());
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					schoolCountMap.put(eduSchool.getSchoolNature(), schoolSet);
				}
				
			}
			
			
		}
		
		//计算留样率和数据封装
		float distRsRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
				}
				/**
				 * 未留样学校数
				 */
				busiDetDataInfo.setNoRsSchNum(schoolCountMap.get(entry.getKey().toString())==null?0:schoolCountMap.get(entry.getKey().toString()).size());
				
				
				int totalDishNum = totalDishNumMap.get(entry.getKey().toString())==null?0:totalDishNumMap.get(entry.getKey().toString());
				int distRsDishNum = distRsDishNumMap.get(entry.getKey().toString())==null?0:distRsDishNumMap.get(entry.getKey().toString());
				int distNoRsDishNum = distNoRsDishNumMap.get(entry.getKey().toString())==null?0:distNoRsDishNumMap.get(entry.getKey().toString());
				
				distRsRate = 0;
				if(totalDishNum > 0) {
					distRsRate = 100 * ((float) distRsDishNum / (float) totalDishNum);
					BigDecimal bd = new BigDecimal(distRsRate);
					distRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRate > 100) {
						distRsRate = 100;
					}
				}
				/**
				 * 菜品数量
				 */
				busiDetDataInfo.setDishNum(totalDishNum);
				/**
				 * 未留样菜品
				 */
				busiDetDataInfo.setNoRsDishNum(distNoRsDishNum);
				/**
				 * 留样率
				 */
				busiDetDataInfo.setRsRate(distRsRate);
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取留样汇总：未留样学校个数、菜品数量、留样率、未留样菜品
	 * @param distId
	 * @param dates
	 * @param db1Service
	 * @return
	 */
	private void getRsInfoNoRsSchNumByNatureTwo(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap, Db1Service db1Service,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,EduSchoolService eduSchoolService) {
		Integer schoolNum = 0;//未留样学校个数

		Map<String, String> distributionTotalMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		
		//菜品数量
		Map<String,Integer> totalDishNumMap = new HashMap<String,Integer>();
		//已留样数量
		Map<String,Integer> distRsDishNumMap = new HashMap<String,Integer>();
		//未留样数量
		Map<String,Integer> noDistRsDishNumMap = new HashMap<String,Integer>();
		//未留样学校个数
		Map<String,Integer> noRsSchNumMap = new HashMap<String,Integer>();
		//已留样学校个数
		Map<String,Integer> rsSchNumMap = new HashMap<String,Integer>();
		Integer count =0;
		Integer valueCount =0;
		
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionTotalMap != null) {
				for (String curKey : distributionTotalMap.keySet()) {
					valueCount = distributionTotalMap.get(curKey)==null?0:Integer.parseInt(distributionTotalMap.get(curKey));
					
					count = valueCount;
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKey.indexOf("nat-area_")==0 && curKeys.length >= 7) {
						//过滤区域
						if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						
						//菜品数量
						if(totalDishNumMap.get(curKeys[3])!=null) {
							count = totalDishNumMap.get(curKeys[3])+valueCount;
						}
						
						totalDishNumMap.put(curKeys[3], count);
						
						
						count = valueCount;
						if(curKeys[6].equalsIgnoreCase("已留样")) {
							//已留样数量
							if(distRsDishNumMap.get(curKeys[3])!=null) {
								count = distRsDishNumMap.get(curKeys[3])+valueCount;
							}
							
							distRsDishNumMap.put(curKeys[3], count);
						}else if(curKeys[6].equalsIgnoreCase("未留样")) { 
							//未留样数量
							if(noDistRsDishNumMap.get(curKeys[3])!=null) {
								count = noDistRsDishNumMap.get(curKeys[3])+valueCount;
							}
							
							noDistRsDishNumMap.put(curKeys[3], count);
						}
					}
					
					//未留样学校数
					if(curKey.indexOf("school-nat-area_")==0 && curKeys.length >= 5) {
						//过滤区域
						if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						
						if(curKeys[6].equalsIgnoreCase("未留样")) { 
							//未留样学校数
							schoolNum=noRsSchNumMap.get(curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							noRsSchNumMap.put(curKeys[3], schoolNum);
						}else if(curKeys[6].equalsIgnoreCase("已留样")) {
							//未留样学校数
							schoolNum=rsSchNumMap.get(curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							rsSchNumMap.put(curKeys[3], schoolNum);
						}
					}
				}
			}
		}
		
		//计算留样率和数据封装
		float distRsRate = 0;
		//学校留样率
		float schRsRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
				}
				/**
				 * 未留样学校数
				 */
				busiDetDataInfo.setNoRsSchNum(noRsSchNumMap.get(entry.getKey().toString())==null?0:noRsSchNumMap.get(entry.getKey().toString()));
				
				/**
				 * 已留样学校数
				 */
				int rsSchNum = rsSchNumMap.get(entry.getKey().toString())==null?0:rsSchNumMap.get(entry.getKey().toString());
				busiDetDataInfo.setRsSchNum(rsSchNum);
				schRsRate = 0;
				int shouldRsSchNum = rsSchNum + busiDetDataInfo.getNoRsSchNum();
				if(shouldRsSchNum > 0) {
					schRsRate = 100 * ((float) rsSchNum / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schRsRate);
					schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schRsRate > 100) {
						schRsRate = 100;
					}
				}
				
				/**
				 * 学校留样率
				 */
				busiDetDataInfo.setSchRsRate(schRsRate);
				
				/**
				 * 应留样学校数
				 */
				busiDetDataInfo.setShouldRsSchNum(shouldRsSchNum);
				
				int totalDishNum = totalDishNumMap.get(entry.getKey().toString())==null?0:totalDishNumMap.get(entry.getKey().toString());
				int distRsDishNum = distRsDishNumMap.get(entry.getKey().toString())==null?0:distRsDishNumMap.get(entry.getKey().toString());
				int distNoRsDishNum = noDistRsDishNumMap.get(entry.getKey().toString())==null?0:noDistRsDishNumMap.get(entry.getKey().toString());
				
				distRsRate = 0;
				if(totalDishNum > 0) {
					distRsRate = 100 * ((float) distRsDishNum / (float) totalDishNum);
					BigDecimal bd = new BigDecimal(distRsRate);
					distRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRate > 100) {
						distRsRate = 100;
					}
				}
				/**
				 * 菜品数量
				 */
				busiDetDataInfo.setDishNum(totalDishNum);
				/**
				 * 已留样菜品
				 */
				busiDetDataInfo.setRsDishNum(distRsDishNum);
				/**
				 * 未留样菜品
				 */
				busiDetDataInfo.setNoRsDishNum(distNoRsDishNum);
				/**
				 * 留样率
				 */
				busiDetDataInfo.setRsRate(distRsRate);
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取留样汇总：未留样学校个数、菜品数量、留样率、未留样菜品
	 * @param distId
	 * @param dates
	 * @param db1Service
	 * @return
	 */
	private void getRsInfoNoRsSchNumBySchoolType(String distId, String[] dates,Map<Integer, String> schoolPropertyMap, Db1Service db1Service,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,EduSchoolService eduSchoolService) {
		Map<String,Set<String>> schoolCountMap = new HashMap<String,Set<String>>();
		Set<String> schoolSet = new HashSet<String>();
		Map<String, String> gcRetentiondishMap = new HashMap<String, String>();
		
		//key：学校编号+餐别+菜单+菜品+日期+状态,value :对应个数
		Map<String,Float> schoolDetailMap = new HashMap<String,Float>();
		
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distId,1,1, 1);
		for(int i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		
		// 时间段内各区菜品留样详情
		float schoolDetailTotal = 0;
		for(int k = 0; k < dateCount; k++) {
			key = dates[k] + "_gc-retentiondish";
			gcRetentiondishMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (gcRetentiondishMap != null) {
				for (String curKey : gcRetentiondishMap.keySet()) {
					keyVal = gcRetentiondishMap.get(curKey);
					// 菜品留样列表
					String[] keyVals = keyVal.split("_");
					if(keyVals.length >= 23) {
						if(distId != null) {
							if(keyVals[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						
						String schoolDetailMapKey = keyVals[5]+"_"+keyVals[13]+"_"+keyVals[7]+"_"+keyVals[9]+"_"+dates[k]+"_"+keyVals[14];
						//学校信息（项目点）
						schoolDetailTotal=schoolDetailMap.get(schoolDetailMapKey)==null?0:schoolDetailMap.get(schoolDetailMapKey);
						schoolDetailMap.put(schoolDetailMapKey, (keyVals[20]==null || "null".equals(keyVals[20]))?0:Float.parseFloat(keyVals[20])+schoolDetailTotal);
					}
					else {
						logger.info("菜品留样："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
		Map<String,Integer> totalDishNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distRsDishNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distNoRsDishNumMap = new HashMap<String,Integer>();
		
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		int count = 0;
		for(Map.Entry<String, Float> entry : schoolDetailMap.entrySet() ) {
			String []keys=entry.getKey().split("_");
			EduSchool eduSchool =  schoolMap.get(keys[0]);
			if(eduSchool!=null ) {
				String schTypeId = AppModConfig.getSchTypeId(eduSchool.getLEVEL(), eduSchool.getLevel2())==null?null:AppModConfig.getSchTypeId(eduSchool.getLEVEL(), eduSchool.getLevel2()).toString();
				if(schTypeId == null || "-1".equals(schTypeId)) {
					continue;
				}
				
				//已验收数量
				if(totalDishNumMap.get(schTypeId)==null) {
					count = 1;
				}else {
					count = totalDishNumMap.get(schTypeId)+1;
				}
				
				totalDishNumMap.put(schTypeId, count);
				
				
				if(keys[5] !=null && keys[5].equals("已留样")) {
					//已验收数量
					if(distRsDishNumMap.get(schTypeId)==null) {
						count = 1;
					}else {
						count = distRsDishNumMap.get(schTypeId)+1;
					}
					
					distRsDishNumMap.put(schTypeId, count);
				}else{
					//未验收数量
					if(distNoRsDishNumMap.get(schTypeId)==null) {
						count = 1;
					}else {
						count = distNoRsDishNumMap.get(schTypeId)+1;
					}
					
					distNoRsDishNumMap.put(schTypeId, count);
					
					//未验收学校数
					schoolSet=schoolCountMap.get(schTypeId);
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					schoolCountMap.put(schTypeId, schoolSet);
				}
				
			}
			
			
		}
		
		//计算留样率和数据封装
		float distRsRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
					//
					busiDetDataInfo.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				/**
				 * 未留样学校数
				 */
				busiDetDataInfo.setNoRsSchNum(schoolCountMap.get(entry.getKey().toString())==null?0:schoolCountMap.get(entry.getKey().toString()).size());
				
				
				int totalDishNum = totalDishNumMap.get(entry.getKey().toString())==null?0:totalDishNumMap.get(entry.getKey().toString());
				int distRsDishNum = distRsDishNumMap.get(entry.getKey().toString())==null?0:distRsDishNumMap.get(entry.getKey().toString());
				int distNoRsDishNum = distNoRsDishNumMap.get(entry.getKey().toString())==null?0:distNoRsDishNumMap.get(entry.getKey().toString());
				
				distRsRate = 0;
				if(totalDishNum > 0) {
					distRsRate = 100 * ((float) distRsDishNum / (float) totalDishNum);
					BigDecimal bd = new BigDecimal(distRsRate);
					distRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRate > 100) {
						distRsRate = 100;
					}
				}
				/**
				 * 菜品数量
				 */
				busiDetDataInfo.setDishNum(totalDishNum);
				/**
				 * 未留样菜品
				 */
				busiDetDataInfo.setNoRsDishNum(distNoRsDishNum);
				/**
				 * 留样率
				 */
				busiDetDataInfo.setRsRate(distRsRate);
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取留样汇总：未留样学校个数、菜品数量、留样率、未留样菜品
	 * @param distId
	 * @param dates
	 * @param db1Service
	 * @return
	 */
	private void getRsInfoNoRsSchNumBySchoolTypeTwo(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap, Db1Service db1Service,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,EduSchoolService eduSchoolService) {
		Integer schoolNum = 0;//未留样学校个数

		Map<String, String> distributionTotalMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		
		//菜品数量
		Map<String,Integer> totalDishNumMap = new HashMap<String,Integer>();
		//已留样数量
		Map<String,Integer> distRsDishNumMap = new HashMap<String,Integer>();
		//未留样数量
		Map<String,Integer> noDistRsDishNumMap = new HashMap<String,Integer>();
		//未留样学校个数
		Map<String,Integer> noRsSchNumMap = new HashMap<String,Integer>();
		//已留样学校个数
		Map<String,Integer> rsSchNumMap = new HashMap<String,Integer>();
		Integer count =0;
		Integer valueCount =0;
		
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionTotalMap != null) {
				for (String curKey : distributionTotalMap.keySet()) {
					valueCount = distributionTotalMap.get(curKey)==null?0:Integer.parseInt(distributionTotalMap.get(curKey));
					count = valueCount;
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKey.indexOf("lev-area_")==0 && curKeys.length >= 5) {
						//过滤区域
						if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						
						//菜品数量
						if(totalDishNumMap.get(curKeys[3])!=null) {
							count = totalDishNumMap.get(curKeys[3])+valueCount;
						}
						
						totalDishNumMap.put(curKeys[3], count);
						
						
						count = valueCount;
						if(curKeys[4].equalsIgnoreCase("已留样")) {
							//已留样数量
							if(distRsDishNumMap.get(curKeys[3])!=null) {
								count = distRsDishNumMap.get(curKeys[3])+valueCount;
							}
							
							distRsDishNumMap.put(curKeys[3], count);
						}else if(curKeys[4].equalsIgnoreCase("未留样")) { 
							//未留样数量
							if(noDistRsDishNumMap.get(curKeys[3])!=null) {
								count = noDistRsDishNumMap.get(curKeys[3])+valueCount;
							}
							
							noDistRsDishNumMap.put(curKeys[3], count);
						}
					}
					
					//未留样学校数
					if(curKey.indexOf("school-lev-area_")==0 && curKeys.length >= 5) {
						//过滤区域
						if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						
						if(curKeys[4].equalsIgnoreCase("未留样")) { 
							//未留样学校数
							schoolNum=noRsSchNumMap.get(curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							noRsSchNumMap.put(curKeys[3], schoolNum);
						}else if(curKeys[4].equalsIgnoreCase("已留样")) {
							//未留样学校数
							schoolNum=rsSchNumMap.get(curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							rsSchNumMap.put(curKeys[3], schoolNum);
						}
					}
				}
			}
		}
		
		//计算留样率和数据封装
		float distRsRate = 0;
		//学校留样率
		float schRsRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
					//
					busiDetDataInfo.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				/**
				 * 未留样学校数
				 */
				busiDetDataInfo.setNoRsSchNum(noRsSchNumMap.get(entry.getKey().toString())==null?0:noRsSchNumMap.get(entry.getKey().toString()));
				
				/**
				 * 已留样学校数
				 */
				int rsSchNum = rsSchNumMap.get(entry.getKey().toString())==null?0:rsSchNumMap.get(entry.getKey().toString());
				busiDetDataInfo.setRsSchNum(rsSchNum);
				
				schRsRate = 0;
				int shouldRsSchNum = rsSchNum + busiDetDataInfo.getNoRsSchNum();
				if(shouldRsSchNum > 0) {
					schRsRate = 100 * ((float) rsSchNum / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schRsRate);
					schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schRsRate > 100) {
						schRsRate = 100;
					}
				}
				
				/**
				 * 学校留样率
				 */
				busiDetDataInfo.setSchRsRate(schRsRate);
				//应留样学校
				busiDetDataInfo.setShouldRsSchNum(shouldRsSchNum);
				
				int totalDishNum = totalDishNumMap.get(entry.getKey().toString())==null?0:totalDishNumMap.get(entry.getKey().toString());
				int distRsDishNum = distRsDishNumMap.get(entry.getKey().toString())==null?0:distRsDishNumMap.get(entry.getKey().toString());
				int distNoRsDishNum = noDistRsDishNumMap.get(entry.getKey().toString())==null?0:noDistRsDishNumMap.get(entry.getKey().toString());
				
				distRsRate = 0;
				if(totalDishNum > 0) {
					distRsRate = 100 * ((float) distRsDishNum / (float) totalDishNum);
					BigDecimal bd = new BigDecimal(distRsRate);
					distRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRate > 100) {
						distRsRate = 100;
					}
				}
				/**
				 * 菜品数量
				 */
				busiDetDataInfo.setDishNum(totalDishNum);
				/**
				 * 已留样菜品
				 */
				busiDetDataInfo.setRsDishNum(distRsDishNum);
				/**
				 * 未留样菜品
				 */
				busiDetDataInfo.setNoRsDishNum(distNoRsDishNum);
				/**
				 * 留样率
				 */
				busiDetDataInfo.setRsRate(distRsRate);
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取留样汇总：未留样学校个数、菜品数量、留样率、未留样菜品
	 * @param distId
	 * @param dates
	 * @param db1Service
	 * @return
	 */
	private void getRsInfoNoRsSchNumBySlave(String departmentId,String distId, String[] dates,Map<String, String> schoolPropertyMap, Db1Service db1Service,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,EduSchoolService eduSchoolService) {
		Map<String,Integer> noRsSchNumSchoolMap = new HashMap<String,Integer>();
		Map<String,Integer> rsSchNumSchoolMap = new HashMap<String,Integer>();
		Map<String, String> gcRetentiondishMap = new HashMap<String, String>();
		
		int dateCount = dates.length;
		String key = null;
		
		Map<String,Integer> totalDishNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distRsDishNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distNoRsDishNumMap = new HashMap<String,Integer>();
		// 时间段内各区菜品留样详情
		int count = 0;
		Integer valueCount = 0;
		Integer schoolNum = 0;
		for(int k = 0; k < dateCount; k++) {
			key = dates[k]   + DataKeyConfig.gcRetentiondishtotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentGcRetentiondishtotal+departmentId;
			}
			gcRetentiondishMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (gcRetentiondishMap != null) {
				for (String curKey : gcRetentiondishMap.keySet()) {
					count =0;
					valueCount = gcRetentiondishMap.get(curKey)==null?0:Integer.parseInt(gcRetentiondishMap.get(curKey));
					// 菜品留样列表
					String[] curKeys = curKey.split("_");
					if(curKey.indexOf("masterid_")==0 &&  curKeys.length >= 5) {
						if("3".equals(curKeys[1]) && curKeys[3]==null) {
							curKeys[3] = "其他";
						}
						//已验收数量
						if(totalDishNumMap.get(curKeys[1]+"_"+curKeys[3])==null) {
							count = valueCount;
						}else {
							count = totalDishNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
						}
						
						totalDishNumMap.put(curKeys[1]+"_"+curKeys[3], count);
						
						
						if(curKeys[4] !=null && curKeys[4].equals("已留样")) {
							//已验收数量
							if(distRsDishNumMap.get(curKeys[1]+"_"+curKeys[3])==null) {
								count = valueCount;
							}else {
								count = distRsDishNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
							}
							
							distRsDishNumMap.put(curKeys[1]+"_"+curKeys[3], count);
						}else{
							//未验收数量
							if(distNoRsDishNumMap.get(curKeys[1]+"_"+curKeys[3])==null) {
								count = valueCount;
							}else {
								count = distNoRsDishNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
							}
							
							distNoRsDishNumMap.put(curKeys[1]+"_"+curKeys[3], count);
							
							/*//未验收学校数
							schoolSet=schoolCountMap.get(curKeys[1]+"_"+curKeys[3]);
							if(schoolSet == null) {
								schoolSet = new HashSet<String>();
							}
							schoolSet.add(curKeys[0]);*/
							//schoolCountMap.put(curKeys[1]+"_"+curKeys[3], schoolSet);
						}
					}
					
					//未留样学校数
					if(curKey.indexOf("school-masterid")==0 && curKeys.length >= 5) {
						if(curKeys[4] !=null && curKeys[4].equals("未留样")) {
							//未留样学校数
							schoolNum=noRsSchNumSchoolMap.get(curKeys[1]+"_"+curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							noRsSchNumSchoolMap.put(curKeys[1]+"_"+curKeys[3], schoolNum);
						}else if(curKeys[4] !=null && curKeys[4].equals("已留样")) {
							//未留样学校数
							schoolNum=rsSchNumSchoolMap.get(curKeys[1]+"_"+curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							rsSchNumSchoolMap.put(curKeys[1]+"_"+curKeys[3], schoolNum);
						}
					}
				}
			}
		}
		
		//计算留样率和数据封装
		float distRsRate = 0;
		//学校留样率
		float schRsRate = 0;
		for(Map.Entry<String, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
				if(busiDetDataInfo==null) {
					String[] keys = entry.getKey().split("_");
					String masterid = keys[0];
					String slave = keys[1];
					busiDetDataInfo = new BusiDetDataInfo();
					busiDetDataInfo.setStatClassName(entry.getValue());
					//所属主管部门
					String slaveName=slave;
					
					if("0".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
					}else if ("1".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
					}else if ("2".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
					}
					
					busiDetDataInfo.setStatPropName(slaveName);
				}
				/**
				 * 未留样学校数
				 */
				busiDetDataInfo.setNoRsSchNum(noRsSchNumSchoolMap.get(entry.getKey().toString())==null?0:noRsSchNumSchoolMap.get(entry.getKey().toString()));
				
				/**
				 * 已留样学校数
				 */
				int rsSchNum = rsSchNumSchoolMap.get(entry.getKey().toString())==null?0:rsSchNumSchoolMap.get(entry.getKey().toString());
				busiDetDataInfo.setRsSchNum(rsSchNum);
				
				schRsRate = 0;
				int shouldRsSchNum = rsSchNum + busiDetDataInfo.getNoRsSchNum();
				if(shouldRsSchNum > 0) {
					schRsRate = 100 * ((float) rsSchNum / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schRsRate);
					schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schRsRate > 100) {
						schRsRate = 100;
					}
				}
				
				/**
				 * 学校留样率
				 */
				busiDetDataInfo.setSchRsRate(schRsRate);
				
				/**
				 * 应留样学校数
				 */
				busiDetDataInfo.setShouldRsSchNum(shouldRsSchNum);
				
				int totalDishNum = totalDishNumMap.get(entry.getKey().toString())==null?0:totalDishNumMap.get(entry.getKey().toString());
				int distRsDishNum = distRsDishNumMap.get(entry.getKey().toString())==null?0:distRsDishNumMap.get(entry.getKey().toString());
				int distNoRsDishNum = distNoRsDishNumMap.get(entry.getKey().toString())==null?0:distNoRsDishNumMap.get(entry.getKey().toString());
				
				distRsRate = 0;
				if(totalDishNum > 0) {
					distRsRate = 100 * ((float) distRsDishNum / (float) totalDishNum);
					BigDecimal bd = new BigDecimal(distRsRate);
					distRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (distRsRate > 100) {
						distRsRate = 100;
					}
				}
				/**
				 * 菜品数量
				 */
				busiDetDataInfo.setDishNum(totalDishNum);
				/**
				 * 已留样菜品
				 */
				busiDetDataInfo.setRsDishNum(distRsDishNum);
				/**
				 * 未留样菜品
				 */
				busiDetDataInfo.setNoRsDishNum(distNoRsDishNum);
				/**
				 * 留样率
				 */
				busiDetDataInfo.setRsRate(distRsRate);
				busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
		}
		
	}

	/**
	 * 获取配送信息：未验收学校
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumByArea(String distId, String[] dates, List<TEduDistrictDo> tedList,Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		Set<String> schoolSet = new HashSet<String>();
		Map<String,Set<String>> schoolMap = new HashMap<String,Set<String>>();
		Map<String, String> distributionDetailMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		String[] keyVals = null;
		Map<String,TEduDistrictDo> tedMap = tedList.stream().collect(Collectors.toMap(TEduDistrictDo::getId,(b)->b));
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
						if(keyVals.length < 1 ) {
							continue;
						}
						int dispPlanStatus = Integer.parseInt(keyVals[0]);
						//去除已验收和已取消的订单
						if(dispPlanStatus!=3 && dispPlanStatus!=4) {
							schoolSet=schoolMap.get(curKeys[7]);
							if(schoolSet == null) {
								schoolSet = new HashSet<String>();
							}
							schoolSet.add(curKeys[5]);
							schoolMap.put(curKeys[7], schoolSet);
							
						}
					}
					else {
						logger.info("配货计划："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
		for(Map.Entry<String, Set<String>> entry : schoolMap.entrySet() ) {
			TEduDistrictDo tEduDistrictDo =  tedMap.get(entry.getKey());
			if(tEduDistrictDo!=null && StringUtils.isNotEmpty(tEduDistrictDo.getName())) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(tEduDistrictDo.getName());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//区域名称
					busiDetDataInfo.setStatPropName(tEduDistrictDo.getName());
				}
				/**
				 * 未验收学校数
				 */
				busiDetDataInfo.setNoAcceptSchNum(entry.getValue()==null?0:entry.getValue().size());
				busiDetDataInfoMap.put(tEduDistrictDo.getName(), busiDetDataInfo);
			}
			
			
		}
		
	}
	
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumByNature(String distId, String[] dates,Map<Integer, String> schoolPropertyMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap,
			EduSchoolService eduSchoolService) {
		Set<String> schoolSet = new HashSet<String>();
		
		Map<String,Set<String>> schoolCountMap = new HashMap<String,Set<String>>();
		//key：学校编号+状态,value :对应个数
		Map<String,Integer> schoolDetailMap = new HashMap<String,Integer>();
		Map<String, String> distributionDetailMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		String[] keyVals = null;
		Integer schoolDetailTotal = 0;
		List<String> schoolIdList = new ArrayList<String>();
		
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_Distribution-Detail";
			distributionDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionDetailMap != null) {
				for (String curKey : distributionDetailMap.keySet()) {
					keyVals = distributionDetailMap.get(curKey).split("_");
					//如果value值为空或者value第一个值不是数字，则不做统计
					if(keyVals.length < 1) {
						continue;
					}
					int dispPlanStatus = Integer.parseInt(keyVals[0]);
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKeys.length >= 14) {
						if(distId != null) {
							if(curKeys[7].compareTo(distId) != 0) {
								continue ;
							}
						}
						schoolDetailTotal=schoolDetailMap.get(curKeys[5]+"_"+dispPlanStatus);
						if(schoolDetailTotal == null) {
							schoolDetailTotal = 0;
						}
						schoolDetailMap.put(curKeys[5]+"_"+dispPlanStatus, schoolDetailTotal);
						schoolIdList.add(curKeys[5]);
					}
					else {
						logger.info("配货计划："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();//配送计划数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();//已验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();//未验收数量
		Integer count =0;
		for(Map.Entry<String, Integer> entry : schoolDetailMap.entrySet() ) {
			String [] keys = entry.getKey().split("_");
			EduSchool eduSchool =  schoolMap.get(keys[0]);
			if(eduSchool!=null && StringUtils.isNotEmpty(eduSchool.getSchoolNature())) {
				count = 0;
				//配送计划数量(排除status_4    已取消)
				if(keys[1] !=null && !keys[1].equals("4")) {
					
					if(totalGsPlanNumMap.get(eduSchool.getSchoolNature())==null) {
						count = 1;
					}else {
						count = totalGsPlanNumMap.get(eduSchool.getSchoolNature())+1;
					}
					
					totalGsPlanNumMap.put(eduSchool.getSchoolNature(), count);
				}
				
				if(keys[1] !=null && keys[1].equals("3")) {
					//已验收数量
					
					if(acceptGsPlanNumMap.get(eduSchool.getSchoolNature())==null) {
						count = 1;
					}else {
						count = acceptGsPlanNumMap.get(eduSchool.getSchoolNature())+1;
					}
					
					acceptGsPlanNumMap.put(eduSchool.getSchoolNature(), count);
				}else if(keys[1] !=null && !keys[1].equals("4") && !keys[1].equals("3")) {
					//未验收数量
					if(noAcceptGsPlanNumMap.get(eduSchool.getSchoolNature())==null) {
						count = 1;
					}else {
						count = noAcceptGsPlanNumMap.get(eduSchool.getSchoolNature())+1;
					}
					
					noAcceptGsPlanNumMap.put(eduSchool.getSchoolNature(), count);
					
					//未验收学校数
					schoolSet=schoolCountMap.get(eduSchool.getSchoolNature());
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					schoolCountMap.put(eduSchool.getSchoolNature(), schoolSet);
				}
			}
			
			
		}
		
		float acceptRate = 0F;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
				}
				/**
				 * 未验收学校数
				 */
				busiDetDataInfo.setNoAcceptSchNum(schoolCountMap.get(entry.getKey().toString())==null?0:schoolCountMap.get(entry.getKey().toString()).size());
				
				int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
				int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
				acceptRate = 0F;
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划数
				 */
				busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
				/**
				 * 未验收计划数
				 */
				busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
				/**
				 * 验收率
				 */
				busiDetDataInfo.setAcceptRate(acceptRate);
				
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率(修改取数规则（更换后台建模）)
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumByNatureTwo(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap,
			EduSchoolService eduSchoolService) {
		Integer schoolNum = 0;//未验收学校个数

		Map<String, String> distributionTotalMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		
		//配送计划数量
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();
		//已验收数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		Integer count =0;
		Integer valueCount =0;
		
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionTotalMap != null) {
				for (String curKey : distributionTotalMap.keySet()) {
					valueCount = distributionTotalMap.get(curKey)==null?0:Integer.parseInt(distributionTotalMap.get(curKey));
					count = valueCount;
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKey.indexOf("nature_")==0 && curKeys.length >= 6) {
						//过滤区域
						/*if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}*/
						
						//配送计划数量(排除status_4    已取消)
						if(curKeys[5] !=null && !curKeys[5].equals("4")) {
							
							if(totalGsPlanNumMap.get(curKeys[1])!=null) {
								count = totalGsPlanNumMap.get(curKeys[1])+valueCount;
							}
							
							totalGsPlanNumMap.put(curKeys[1], count);
						}
						
						count = valueCount;
						if(curKeys[5] !=null && curKeys[5].equals("3")) {
							//已验收数量
							if(acceptGsPlanNumMap.get(curKeys[1])!=null) {
								count = acceptGsPlanNumMap.get(curKeys[1])+valueCount;
							}
							
							acceptGsPlanNumMap.put(curKeys[1], count);
						}else if(curKeys[5] !=null && !curKeys[5].equals("4") && !curKeys[5].equals("3")) {
							//未验收数量
							if(noAcceptGsPlanNumMap.get(curKeys[1])!=null) {
								count = noAcceptGsPlanNumMap.get(curKeys[1])+valueCount;
							}
							
							noAcceptGsPlanNumMap.put(curKeys[1], count);
						}
					}
					
					//未验收学校数
					if(curKey.indexOf("school-nature")==0 && curKeys.length >= 6) {
						//过滤区域
						if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						if(curKeys[5] !=null && !curKeys[5].equals("4") && !curKeys[5].equals("3")) {
							count = 0;
							//未验收学校数
							schoolNum=noAcceptSchNumsMap.get(curKeys[1]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							noAcceptSchNumsMap.put(curKeys[1], schoolNum);
						}else if (curKeys[5] !=null && curKeys[5].equals("3")) {
							count = 0;
							//未验收学校数
							schoolNum=acceptSchNumsMap.get(curKeys[1]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							acceptSchNumsMap.put(curKeys[1], schoolNum);
						}
					}
				}
			}
		}		
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
				}
				/**
				 * 未验收学校数
				 */
				busiDetDataInfo.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()));
				
				/**
				 * 已验收学校数
				 */
				int acceptSchNum = acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString());
				busiDetDataInfo.setAcceptSchNum(acceptSchNum);
				
				int shouldAccSchNum = busiDetDataInfo.getNoAcceptSchNum() + acceptSchNum;
				schAcceptRate = 0;
				if(shouldAccSchNum > 0) {
					schAcceptRate = 100 * ((float) acceptSchNum / (float) shouldAccSchNum);
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100) {
						schAcceptRate = 100;
					}
				}
				
				/**
				 * 应验收学校数
				 */
				busiDetDataInfo.setShouldAcceptSchNum(shouldAccSchNum);
				
				
				int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
				int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
				acceptRate = 0F;
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划数
				 */
				busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
				/**
				 * 已验收计划数
				 */
				busiDetDataInfo.setAcceptPlanNum(acceptGsPlanNum);
				/**
				 * 未验收计划数
				 */
				busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
				/**
				 * 验收率
				 */
				busiDetDataInfo.setAcceptRate(acceptRate);
				/**
				 * 学校验收率，保留小数点有效数字两位
				 */
				busiDetDataInfo.setSchAcceptRate(schAcceptRate);
				
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率(修改取数规则（更换后台建模）)
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumByNatureFromHive(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap,
			EduSchoolService eduSchoolService,DbHiveGsService dbHiveGsService) {
		
		//配送计划数量
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();
		//已验收数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		
		// 时间段内各区配货计划详情
		float acceptRate = 0F;
		
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
				-1, -1, null, null,departmentId,null, 1);
		if(dishList !=null && dishList.size() > 0) {
			for(SchGsCommon schDishCommon: dishList) {
				if(schDishCommon.getDisSealStatus() ==null || "-1".equals(schDishCommon.getDisSealStatus())) {
					if(schDishCommon.getHaulStatus() ==null) {
						totalGsPlanNumMap.put(schDishCommon.getSchoolNatureName(), 
								(totalGsPlanNumMap.get(schDishCommon.getSchoolNatureName())==null?0:totalGsPlanNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getTotal());
						
					}else if(schDishCommon.getHaulStatus() == 3) {
						//已验收
						acceptSchNumsMap.put(schDishCommon.getSchoolNatureName(), 
								(acceptSchNumsMap.get(schDishCommon.getSchoolNatureName())==null?0:acceptSchNumsMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getSchoolTotal());
						
						acceptGsPlanNumMap.put(schDishCommon.getSchoolNatureName(), 
								(acceptGsPlanNumMap.get(schDishCommon.getSchoolNatureName())==null?0:acceptGsPlanNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getTotal());
					}else {
						//未验收：haul_status = -4 or haul_status = -2 or haul_status = -1 or haul_status = 0 or haul_status = 1 or haul_status = 2
						noAcceptSchNumsMap.put(schDishCommon.getSchoolNatureName(), 
								(noAcceptSchNumsMap.get(schDishCommon.getSchoolNatureName())==null?0:noAcceptSchNumsMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getSchoolTotal());
						
						noAcceptGsPlanNumMap.put(schDishCommon.getSchoolNatureName(), 
								(noAcceptGsPlanNumMap.get(schDishCommon.getSchoolNatureName())==null?0:noAcceptGsPlanNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getTotal());
					}
				}
			}
		}
		
		float schAcceptRate = 0F;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
				}
				/**
				 * 未验收学校数
				 */
				busiDetDataInfo.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()));
				
				/**
				 * 已验收学校数
				 */
				int acceptSchNum = acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString());
				busiDetDataInfo.setAcceptSchNum(acceptSchNum);
				
				int shouldAccSchNum = busiDetDataInfo.getNoAcceptSchNum() + acceptSchNum;
				schAcceptRate = 0;
				if(shouldAccSchNum > 0) {
					schAcceptRate = 100 * ((float) acceptSchNum / (float) shouldAccSchNum);
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100) {
						schAcceptRate = 100;
					}
				}
				
				/**
				 * 应验收学校数
				 */
				busiDetDataInfo.setShouldAcceptSchNum(shouldAccSchNum);
				
				
				int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
				int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
				acceptRate = 0F;
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划数
				 */
				busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
				/**
				 * 已验收计划数
				 */
				busiDetDataInfo.setAcceptPlanNum(acceptGsPlanNum);
				/**
				 * 未验收计划数
				 */
				busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
				/**
				 * 验收率
				 */
				busiDetDataInfo.setAcceptRate(acceptRate);
				/**
				 * 学校验收率，保留小数点有效数字两位
				 */
				busiDetDataInfo.setSchAcceptRate(schAcceptRate);
				
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率（按学校类型分类）
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumBySchoolType(String distId, String[] dates,Map<Integer, String> schoolPropertyMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap,
			EduSchoolService eduSchoolService) {
		Set<String> schoolSet = new HashSet<String>();
		
		Map<String,Set<String>> schoolCountMap = new HashMap<String,Set<String>>();
		//key：学校编号+状态,value :对应个数
		Map<String,Integer> schoolDetailMap = new HashMap<String,Integer>();
		Map<String, String> distributionDetailMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		String[] keyVals = null;
		Integer schoolDetailTotal = 0;
		List<String> schoolIdList = new ArrayList<String>();
		
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + "_Distribution-Detail";
			distributionDetailMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionDetailMap != null) {
				for (String curKey : distributionDetailMap.keySet()) {
					keyVals = distributionDetailMap.get(curKey).split("_");
					//如果value值为空或者value第一个值不是数字，则不做统计
					if(keyVals.length < 1) {
						continue;
					}
					int dispPlanStatus = Integer.parseInt(keyVals[0]);
					
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKeys.length >= 14) {
						if(distId != null) {
							if(curKeys[7].compareTo(distId) != 0) {
								continue ;
							}
						}
						schoolDetailTotal=schoolDetailMap.get(curKeys[5]+"_"+dispPlanStatus);
						if(schoolDetailTotal == null) {
							schoolDetailTotal = 0;
						}else {
							schoolDetailTotal = schoolDetailTotal+1;
						}
						schoolDetailMap.put(curKeys[5]+"_"+dispPlanStatus, schoolDetailTotal);
						schoolIdList.add(curKeys[5]);
					}
					else {
						logger.info("配货计划："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();//配送计划数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();//已验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();//未验收数量
		Integer count =0;
		for(Map.Entry<String, Integer> entry : schoolDetailMap.entrySet() ) {
			String [] keys = entry.getKey().split("_");
			EduSchool eduSchool =  schoolMap.get(keys[0]);
			if(eduSchool!=null) {
				String schTypeId = AppModConfig.getSchTypeId(eduSchool.getLEVEL(), eduSchool.getLevel2())==null?null:AppModConfig.getSchTypeId(eduSchool.getLEVEL(), eduSchool.getLevel2()).toString();
				if(schTypeId == null || "-1".equals(schTypeId)) {
					continue;
				}
				
				count = 0;
				//配送计划数量(排除status_4    已取消)
				if(keys[1] !=null && !keys[1].equals("4")) {
					
					if(totalGsPlanNumMap.get(schTypeId)==null) {
						count = 1;
					}else {
						count = totalGsPlanNumMap.get(schTypeId)+1;
					}
					
					totalGsPlanNumMap.put(schTypeId, count);
				}
				
				if(keys[1] !=null && keys[1].equals("3")) {
					//已验收数量
					if(acceptGsPlanNumMap.get(schTypeId)==null) {
						count = 1;
					}else {
						count = acceptGsPlanNumMap.get(schTypeId)+1;
					}
					
					acceptGsPlanNumMap.put(schTypeId, count);
				}else if(keys[1] !=null && !keys[1].equals("4") && !keys[1].equals("3")) {
					//未验收数量
					if(noAcceptGsPlanNumMap.get(schTypeId)==null) {
						count = 1;
					}else {
						count = noAcceptGsPlanNumMap.get(schTypeId)+1;
					}
					
					noAcceptGsPlanNumMap.put(schTypeId, count);
					
					//未验收学校数
					schoolSet=schoolCountMap.get(schTypeId);
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					schoolCountMap.put(schTypeId, schoolSet);
				}
			}
			
			
		}
		
		float acceptRate = 0F;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
					//
					busiDetDataInfo.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				/**
				 * 未验收学校数
				 */
				busiDetDataInfo.setNoAcceptSchNum(schoolCountMap.get(entry.getKey().toString())==null?0:schoolCountMap.get(entry.getKey().toString()).size());
				
				int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
				int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
				acceptRate = 0F;
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划数
				 */
				busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
				/**
				 * 未验收计划数
				 */
				busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
				/**
				 * 验收率
				 */
				busiDetDataInfo.setAcceptRate(acceptRate);
				
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率（按学校类型分类）
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumBySchoolTypeTwo(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap,
			EduSchoolService eduSchoolService) {
		Integer schoolNum = 0;//未验收学校个数

		Map<String, String> distributionTotalMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		
		//配送计划数量
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();
		//已验收数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		Integer count =0;
		Integer valueCount =0;
		
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionTotalMap != null) {
				for (String curKey : distributionTotalMap.keySet()) {
					valueCount = distributionTotalMap.get(curKey)==null?0:Integer.parseInt(distributionTotalMap.get(curKey));
					count = valueCount;
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKey.indexOf("level_")==0 && curKeys.length >= 4) {
						//过滤区域
						/*if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}*/
						
						//配送计划数量(排除status_4    已取消)
						if(curKeys[3] !=null && !curKeys[3].equals("4")) {
							
							if(totalGsPlanNumMap.get(curKeys[1])!=null) {
								count = totalGsPlanNumMap.get(curKeys[1])+valueCount;
							}
							
							totalGsPlanNumMap.put(curKeys[1], count);
						}
						
						
						count = valueCount;
						if(curKeys[3] !=null && curKeys[3].equals("3")) {
							//已验收数量
							if(acceptGsPlanNumMap.get(curKeys[1])!=null) {
								count = acceptGsPlanNumMap.get(curKeys[1])+valueCount;
							}
							
							acceptGsPlanNumMap.put(curKeys[1], count);
						}else if(curKeys[3] !=null && !curKeys[3].equals("4") && !curKeys[3].equals("3")) {
							//未验收数量
							if(noAcceptGsPlanNumMap.get(curKeys[1])!=null) {
								count = noAcceptGsPlanNumMap.get(curKeys[1])+valueCount;
							}
							
							noAcceptGsPlanNumMap.put(curKeys[1], count);
						}
					}
					
					//未验收学校数
					if(curKey.indexOf("school-level_")==0 && curKeys.length >= 4) {
						//过滤区域
						if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						if(curKeys[3] !=null && !curKeys[3].equals("4") && !curKeys[3].equals("3")) {
							//未验收学校数
							schoolNum=noAcceptSchNumsMap.get(curKeys[1]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							noAcceptSchNumsMap.put(curKeys[1], schoolNum);
						}else if (curKeys[3] !=null && curKeys[3].equals("3")) {
							//未验收学校数
							schoolNum=acceptSchNumsMap.get(curKeys[1]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							acceptSchNumsMap.put(curKeys[1], schoolNum);
						}
					}
				}
			}
		}
		
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
					//
					busiDetDataInfo.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				/**
				 * 未验收学校数
				 */
				busiDetDataInfo.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()));
				
				/**
				 * 已验收学校数
				 */
				int acceptSchNum = acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString());
				busiDetDataInfo.setAcceptSchNum(acceptSchNum);
				
				int shouldAccSchNum = busiDetDataInfo.getNoAcceptSchNum() + acceptSchNum;
				schAcceptRate = 0;
				if(shouldAccSchNum > 0) {
					schAcceptRate = 100 * ((float) acceptSchNum / (float) shouldAccSchNum);
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100) {
						schAcceptRate = 100;
					}
				}
				
				/**
				 * 应验收学校数
				 */
				busiDetDataInfo.setShouldAcceptSchNum(shouldAccSchNum);
				
				int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
				int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
				acceptRate = 0F;
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划数
				 */
				busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
				/**
				 * 已验收计划数
				 */
				busiDetDataInfo.setAcceptPlanNum(acceptGsPlanNum);
				/**
				 * 未验收计划数
				 */
				busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
				/**
				 * 验收率
				 */
				busiDetDataInfo.setAcceptRate(acceptRate);

	             /**
				 * 学校验收率，保留小数点有效数字两位
				 */
			    busiDetDataInfo.setSchAcceptRate(schAcceptRate);
				
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}

	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率（按学校类型分类）
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumBySchoolTypeFromHive(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap,
			EduSchoolService eduSchoolService,DbHiveGsService dbHiveGsService) {
		//配送计划数量
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();
		//已验收数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		
		// 时间段内各区配货计划详情
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
				-1, -1, null, null,departmentId,null, 2);
		if(dishList !=null && dishList.size() > 0) {
			for(SchGsCommon schDishCommon: dishList) {
				
				if(schDishCommon.getDisSealStatus() ==null || "-1".equals(schDishCommon.getDisSealStatus())) {
					if(schDishCommon.getHaulStatus() ==null) {
						totalGsPlanNumMap.put(schDishCommon.getLevelName(), 
								(totalGsPlanNumMap.get(schDishCommon.getLevelName())==null?0:totalGsPlanNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getTotal());
						
					}else if(schDishCommon.getHaulStatus() == 3) {
						//已验收
						acceptSchNumsMap.put(schDishCommon.getLevelName(), 
								(acceptSchNumsMap.get(schDishCommon.getLevelName())==null?0:acceptSchNumsMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getSchoolTotal());
						
						acceptGsPlanNumMap.put(schDishCommon.getLevelName(), 
								(acceptGsPlanNumMap.get(schDishCommon.getLevelName())==null?0:acceptGsPlanNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getTotal());
					}else {
						//未验收：haul_status = -4 or haul_status = -2 or haul_status = -1 or haul_status = 0 or haul_status = 1 or haul_status = 2
						noAcceptSchNumsMap.put(schDishCommon.getLevelName(), 
								(noAcceptSchNumsMap.get(schDishCommon.getLevelName())==null?0:noAcceptSchNumsMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getSchoolTotal());
						
						noAcceptGsPlanNumMap.put(schDishCommon.getLevelName(), 
								(noAcceptGsPlanNumMap.get(schDishCommon.getLevelName())==null?0:noAcceptGsPlanNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getTotal());
					}
				}
			}
		}
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//学校性质名称
					busiDetDataInfo.setStatPropName(entry.getValue());
					//
					busiDetDataInfo.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				/**
				 * 未验收学校数
				 */
				busiDetDataInfo.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()));
				
				/**
				 * 已验收学校数
				 */
				int acceptSchNum = acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString());
				busiDetDataInfo.setAcceptSchNum(acceptSchNum);
				
				int shouldAccSchNum = busiDetDataInfo.getNoAcceptSchNum() + acceptSchNum;
				schAcceptRate = 0;
				if(shouldAccSchNum > 0) {
					schAcceptRate = 100 * ((float) acceptSchNum / (float) shouldAccSchNum);
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100) {
						schAcceptRate = 100;
					}
				}
				
				/**
				 * 应验收学校数
				 */
				busiDetDataInfo.setShouldAcceptSchNum(shouldAccSchNum);
				
				int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
				int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
				acceptRate = 0F;
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划数
				 */
				busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
				/**
				 * 已验收计划数
				 */
				busiDetDataInfo.setAcceptPlanNum(acceptGsPlanNum);
				/**
				 * 未验收计划数
				 */
				busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
				/**
				 * 验收率
				 */
				busiDetDataInfo.setAcceptRate(acceptRate);

	             /**
				 * 学校验收率，保留小数点有效数字两位
				 */
			    busiDetDataInfo.setSchAcceptRate(schAcceptRate);
				
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率（按学校所属主管部门）
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumBySlave(String departmentId,String distId, String[] dates,Map<String, String> slaveMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap,
			EduSchoolService eduSchoolService) {

		Integer schoolNum = 0;//未验收学校个数
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		Map<String, String> distributionTotalMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();//配送计划数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();//已验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();//未验收数量
		Integer count =0;
		Integer valueCount =0;
		
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionTotalMap != null) {
				for (String curKey : distributionTotalMap.keySet()) {
					count =0;
					valueCount = distributionTotalMap.get(curKey)==null?0:Integer.parseInt(distributionTotalMap.get(curKey));
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKey.indexOf("masterid")==0 && curKeys.length >= 6) {
						if("3".equals(curKeys[1]) && curKeys[3]==null) {
							curKeys[3] = "其他";
						}
						//配送计划数量(排除status_4    已取消)
						if(curKeys[5] !=null && !curKeys[5].equals("4")) {
							
							if(totalGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])!=null) {
								count = totalGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
							}else {
								count = valueCount;
							}
							
							totalGsPlanNumMap.put(curKeys[1]+"_"+curKeys[3], count);
						}
						
						if(curKeys[5] !=null && curKeys[5].equals("3")) {
							//已验收数量
							if(acceptGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])!=null) {
								count = acceptGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
							}else {
								count = valueCount;
							}
							
							acceptGsPlanNumMap.put(curKeys[1]+"_"+curKeys[3], count);
						}else if(curKeys[5] !=null && !curKeys[5].equals("4") && !curKeys[5].equals("3")) {
							//未验收数量
							if(noAcceptGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])!=null) {
								count = noAcceptGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
							}else {
								count = valueCount;
							}
							
							noAcceptGsPlanNumMap.put(curKeys[1]+"_"+curKeys[3], count);
						}
					}
					
					//未验收学校数
					if(curKey.indexOf("school-masterid")==0 && curKeys.length >= 6) {
						if(curKeys[5] !=null && !curKeys[5].equals("4") && !curKeys[5].equals("3")) {
							//未验收学校数
							schoolNum=noAcceptSchNumsMap.get(curKeys[1]+"_"+curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							noAcceptSchNumsMap.put(curKeys[1]+"_"+curKeys[3], schoolNum);
						}else if (curKeys[5] !=null && curKeys[5].equals("3")) {
							//未验收学校数
							schoolNum=acceptSchNumsMap.get(curKeys[1]+"_"+curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							acceptSchNumsMap.put(curKeys[1]+"_"+curKeys[3], schoolNum);
						}
					}
				}
			}
		}
		
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		for(Map.Entry<String, String> entry : slaveMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
				if(busiDetDataInfo==null) {
					String[] keys = entry.getKey().split("_");
					String masterid = keys[0];
					String slave = keys[1];
					busiDetDataInfo = new BusiDetDataInfo();
					busiDetDataInfo.setStatClassName(entry.getValue());
					//区域名称
					String slaveName=slave;
					
					if("0".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
					}else if ("1".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
					}else if ("2".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
					}
					
					busiDetDataInfo.setStatPropName(slaveName);
				}
				/**
				 * 未验收学校数
				 */
				busiDetDataInfo.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()));
				/**
				 * 已验收学校数
				 */
				int acceptSchNum = acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString());
				/**
				 * 已验收学校数
				 */
				busiDetDataInfo.setAcceptSchNum(acceptSchNum);
				
				int shouldAccSchNum = busiDetDataInfo.getNoAcceptSchNum() + acceptSchNum;
				schAcceptRate = 0;
				if(shouldAccSchNum > 0) {
					schAcceptRate = 100 * ((float) acceptSchNum / (float) shouldAccSchNum);
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100) {
						schAcceptRate = 100;
					}
				}
				
				/**
				 * 应验收学校数
				 */
				busiDetDataInfo.setShouldAcceptSchNum(shouldAccSchNum);
				
				int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
				int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
				acceptRate = 0F;
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划数
				 */
				busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
				/**
				 * 已验收计划数
				 */
				busiDetDataInfo.setAcceptPlanNum(acceptGsPlanNum);
				/**
				 * 未验收计划数
				 */
				busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
				/**
				 * 验收率
				 */
				busiDetDataInfo.setAcceptRate(acceptRate);
				/**
				 * 学校验收率，保留小数点有效数字两位
				 */
				busiDetDataInfo.setSchAcceptRate(schAcceptRate);
				
				busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率（按学校所属主管部门）
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumBySlaveFromHive(String departmentId,String distId, String[] dates,Map<String, String> slaveMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap,
			EduSchoolService eduSchoolService,DbHiveGsService dbHiveGsService) {

		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();//配送计划数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();//已验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();//未验收数量
		
		// 时间段内各区配货计划详情
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
				-1, -1, null, null,departmentId,null, 3);
		if(dishList !=null && dishList.size() > 0) {
			for(SchGsCommon schDishCommon: dishList) {
				String key = schDishCommon.getDepartmentMasterId() + "_" +schDishCommon.getDepartmentSlaveIdName();
				
				if(schDishCommon.getDisSealStatus() ==null || "-1".equals(schDishCommon.getDisSealStatus())) {
					if(schDishCommon.getHaulStatus() ==null) {
						totalGsPlanNumMap.put(key, 
								(totalGsPlanNumMap.get(key)==null?0:totalGsPlanNumMap.get(key)) 
								+ schDishCommon.getTotal());
						
					}else if(schDishCommon.getHaulStatus() == 3) {
						//已验收
						acceptSchNumsMap.put(key, 
								(acceptSchNumsMap.get(key)==null?0:acceptSchNumsMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
						
						acceptGsPlanNumMap.put(key, 
								(acceptGsPlanNumMap.get(key)==null?0:acceptGsPlanNumMap.get(key)) 
								+ schDishCommon.getTotal());
					}else {
						//未验收：haul_status = -4 or haul_status = -2 or haul_status = -1 or haul_status = 0 or haul_status = 1 or haul_status = 2
						noAcceptSchNumsMap.put(key, 
								(noAcceptSchNumsMap.get(key)==null?0:noAcceptSchNumsMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
						
						noAcceptGsPlanNumMap.put(key, 
								(noAcceptGsPlanNumMap.get(key)==null?0:noAcceptGsPlanNumMap.get(key)) 
								+ schDishCommon.getTotal());
					}
				}
			}
		}
		
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		for(Map.Entry<String, String> entry : slaveMap.entrySet() ) {
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
				
				String[] keys = entry.getKey().split("_");
				String masterid = keys[0];
				String slave = keys[1];
				
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					busiDetDataInfo.setStatClassName(entry.getValue());
					//区域名称
					String slaveName=slave;
					
					if("0".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
					}else if ("1".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
					}else if ("2".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
					}
					
					busiDetDataInfo.setStatPropName(slaveName);
				}
				
				//转换，hive库中 区属的子项是数字，slaveMap中为中文
				String dataKey = masterid+"_";
				if("3".equals(masterid)) {
					dataKey += AppModConfig.compDepNameToIdMap3.get(slave);
				}else {
					dataKey += slave;
				}
				
				/**
				 * 未验收学校数
				 */
				busiDetDataInfo.setNoAcceptSchNum(noAcceptSchNumsMap.get(dataKey)==null?0:noAcceptSchNumsMap.get(dataKey));
				/**
				 * 已验收学校数
				 */
				int acceptSchNum = acceptSchNumsMap.get(dataKey)==null?0:acceptSchNumsMap.get(dataKey);
				/**
				 * 已验收学校数
				 */
				busiDetDataInfo.setAcceptSchNum(acceptSchNum);
				
				int shouldAccSchNum = busiDetDataInfo.getNoAcceptSchNum() + acceptSchNum;
				schAcceptRate = 0;
				if(shouldAccSchNum > 0) {
					schAcceptRate = 100 * ((float) acceptSchNum / (float) shouldAccSchNum);
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100) {
						schAcceptRate = 100;
					}
				}
				
				/**
				 * 应验收学校数
				 */
				busiDetDataInfo.setShouldAcceptSchNum(shouldAccSchNum);
				
				int totalGsPlanNum = totalGsPlanNumMap.get(dataKey)==null?0:totalGsPlanNumMap.get(dataKey);
				int acceptGsPlanNum = acceptGsPlanNumMap.get(dataKey)==null?0:acceptGsPlanNumMap.get(dataKey);
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(dataKey)==null?0:noAcceptGsPlanNumMap.get(dataKey);
				acceptRate = 0F;
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划数
				 */
				busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
				/**
				 * 已验收计划数
				 */
				busiDetDataInfo.setAcceptPlanNum(acceptGsPlanNum);
				/**
				 * 未验收计划数
				 */
				busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
				/**
				 * 验收率
				 */
				busiDetDataInfo.setAcceptRate(acceptRate);
				/**
				 * 学校验收率，保留小数点有效数字两位
				 */
				busiDetDataInfo.setSchAcceptRate(schAcceptRate);
				
				busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取配送信息：配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAccDistrInfoByArea(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		String field = "";
		// 当天排菜学校总数
		Map<String, String> schIdToPlatoonMap = new HashMap<>();
		int dateCount = dates.length;
		int distCount = tedList.size();
		int[][]totalGsPlanNums = new int[dateCount][distCount];//配送计划数量
		int [][]acceptGsPlanNums = new int[dateCount][distCount];//已验收数量
		int [][] noAcceptGsPlanNums = new int[dateCount][distCount];//未验收数量
		int [][]noAcceptSchNums = new int[dateCount][distCount];//未验收学校
		int [][]acceptSchNums = new int[dateCount][distCount];//已验收学校
		int [][]conSchNums = new int[dateCount][distCount];//确认学校
		
		float acceptRate = 0;
		float schAcceptRate = 0;
		// 当天各区配货计划总数量
		for(int k = 0; k < dates.length; k++) {
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			schIdToPlatoonMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (schIdToPlatoonMap != null) {
				for (int i = 0; i < tedList.size(); i++) {
					
					totalGsPlanNums[k][i] = 0;
					noAcceptGsPlanNums[k][i] = 0;
					acceptGsPlanNums[k][i] = 0;
					
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
						totalGsPlanNums[k][i] = Integer.parseInt(keyVal);
						if(totalGsPlanNums[k][i] < 0) {
							totalGsPlanNums[k][i] = 0;
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
							noAcceptGsPlanNums[k][i] += noAcceptGsPlanNum;
						}
					}
					
					// 已验收数
					field = "area" + "_" + curDistId + "_" + "status" + "_3";
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						acceptGsPlanNums[k][i] = Integer.parseInt(keyVal);
						if(acceptGsPlanNums[k][i] < 0) {
							acceptGsPlanNums[k][i] = 0;
						}
					}
					
					//已确认学校
					conSchNums[k][i] = 0;
					for(int j = 2; j < 4; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							conSchNums[k][i] += curConSchNum;
						}
					}
					//已验收学校
					field = "school-area" + "_" + curDistId + "_" + "status" + "_3";
					acceptSchNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						acceptSchNums[k][i] = Integer.parseInt(keyVal);
						if(acceptSchNums[k][i] < 0)
							acceptSchNums[k][i] = 0;
					}
					
					// 未验收学校
					for(int j = -2; j <= 2; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int noAcceptSchNum = Integer.parseInt(keyVal);
							if(noAcceptSchNum < 0) {
								noAcceptSchNum = 0;
							}
							noAcceptSchNums[k][i] += noAcceptSchNum;
						}
					}
				}
			}
		}
		
		for(int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			//判断是否按区域获取配货计划数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if(distId != null) {
				if(!curDistId.equalsIgnoreCase(distId)) {
					continue ;
				}
			}
			int totalGsPlanNum = 0;
			int acceptGsPlanNum = 0;
			int noAcceptGsPlanNum =0;
			int noAcceptSchNum =0;
			int acceptSchNum =0;
			for(int k = 0; k < dates.length; k++) {
				totalGsPlanNum += totalGsPlanNums[k][i];
				acceptGsPlanNum += acceptGsPlanNums[k][i];
				noAcceptGsPlanNum +=noAcceptGsPlanNums[k][i];
				noAcceptSchNum +=noAcceptSchNums[k][i];
				acceptSchNum +=acceptSchNums[k][i];
			}
			
			acceptRate = 0F;
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			
			schAcceptRate = 0F;
			int totalSchAcceptNum = noAcceptSchNum +acceptSchNum ;
			if(totalSchAcceptNum > 0) {
				schAcceptRate = 100 * ((float) acceptSchNum / (float) totalSchAcceptNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(curTdd.getName());
			}
			/**
			 * 配货计划数
			 */
			busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
			/**
			 * 已验收计划数
			 */
			busiDetDataInfo.setAcceptPlanNum(acceptGsPlanNum);
			/**
			 * 未验收计划数
			 */
			busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
			/**
			 * 验收率
			 */
			busiDetDataInfo.setAcceptRate(acceptRate);
			
			/**
			 * 应验收学校数
			 */
			busiDetDataInfo.setShouldAcceptSchNum(totalSchAcceptNum);
			/**
			 * 已验收学校数
			 */
			busiDetDataInfo.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 未验收学校数
			 */
			busiDetDataInfo.setNoAcceptSchNum(noAcceptSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			busiDetDataInfo.setSchAcceptRate(schAcceptRate);
			
			busiDetDataInfoMap.put(curTdd.getName(), busiDetDataInfo);
		}
	}

	/**
	 * 获取配送信息：配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAccDistrInfoByDepartment(String departmentId,String[] dates, List<DepartmentObj> deparmentList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		String field = "";
		// 当天排菜学校总数
		Map<String, String> schIdToPlatoonMap = new HashMap<>();
		int dateCount = dates.length;
		int distCount = deparmentList.size();
		int[][]totalGsPlanNums = new int[dateCount][distCount];//配送计划数量
		int [][]acceptGsPlanNums = new int[dateCount][distCount];//已验收数量
		int [][] noAcceptGsPlanNums = new int[dateCount][distCount];//未验收数量
		int [][]noAcceptSchNums = new int[dateCount][distCount];//未验收学校
		int [][]acceptSchNums = new int[dateCount][distCount];//已验收学校
		int [][]conSchNums = new int[dateCount][distCount];//确认学校
		
		float acceptRate = 0;
		float schAcceptRate = 0;
		// 当天各区配货计划总数量
		for(int k = 0; k < dates.length; k++) {
			key = dates[k]   + DataKeyConfig.distributionTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentDistributionTotal+departmentId;
			}
			schIdToPlatoonMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (schIdToPlatoonMap != null) {
				for (int i = 0; i < deparmentList.size(); i++) {
					
					totalGsPlanNums[k][i] = 0;
					noAcceptGsPlanNums[k][i] = 0;
					acceptGsPlanNums[k][i] = 0;
					
					DepartmentObj departmentObj = deparmentList.get(i);
					String curDepartmentId = departmentObj.getDepartmentId();
					//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
					if(departmentId != null) {
						if(!curDepartmentId.equalsIgnoreCase(departmentId)) {
							continue ;
						}
					}
					// 区域配货计划总数
					for(int l = -2; l < 4; l++) {
						field = "department" + "_" + curDepartmentId + "_" + "status" + "_" + l;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							totalGsPlanNums[k][i] += curConSchNum;
						}
					}
					
					// 未验收数
					for(int j = -2; j <= 2; j++) {
						field = "department" + "_" + curDepartmentId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int noAcceptGsPlanNum = Integer.parseInt(keyVal);
							if(noAcceptGsPlanNum < 0) {
								noAcceptGsPlanNum = 0;
							}
							noAcceptGsPlanNums[k][i] += noAcceptGsPlanNum;
						}
					}
					
					// 已验收数
					field = "department" + "_" + curDepartmentId + "_" + "status" + "_3";
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						acceptGsPlanNums[k][i] = Integer.parseInt(keyVal);
						if(acceptGsPlanNums[k][i] < 0) {
							acceptGsPlanNums[k][i] = 0;
						}
					}
					
					//已确认学校
					conSchNums[k][i] = 0;
					for(int j = 2; j < 4; j++) {
						field = "school-department" + "_" + curDepartmentId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							conSchNums[k][i] += curConSchNum;
						}
					}
					//已验收学校
					field = "school-department" + "_" + curDepartmentId + "_" + "status" + "_3";
					acceptSchNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						acceptSchNums[k][i] = Integer.parseInt(keyVal);
						if(acceptSchNums[k][i] < 0)
							acceptSchNums[k][i] = 0;
					}
					
					// 未验收学校
					for(int j = -2; j <= 2; j++) {
						field = "school-department" + "_" + curDepartmentId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int noAcceptSchNum = Integer.parseInt(keyVal);
							if(noAcceptSchNum < 0) {
								noAcceptSchNum = 0;
							}
							noAcceptSchNums[k][i] += noAcceptSchNum;
						}
					}
				}
			}
		}
		
		for(int i = 0; i < distCount; i++) {
			DepartmentObj departmentObj = deparmentList.get(i);
			String curDepartmentId = departmentObj.getDepartmentId();
			//判断是否按区域获取配货计划数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if(departmentId != null) {
				if(!curDepartmentId.equalsIgnoreCase(departmentId)) {
					continue ;
				}
			}
			int totalGsPlanNum = 0;
			int acceptGsPlanNum = 0;
			int noAcceptGsPlanNum =0;
			int noAcceptSchNum =0;
			int acceptSchNum =0;
			for(int k = 0; k < dates.length; k++) {
				totalGsPlanNum += totalGsPlanNums[k][i];
				acceptGsPlanNum += acceptGsPlanNums[k][i];
				noAcceptGsPlanNum +=noAcceptGsPlanNums[k][i];
				noAcceptSchNum +=noAcceptSchNums[k][i];
				acceptSchNum +=acceptSchNums[k][i];
			}
			
			acceptRate = 0F;
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			
			schAcceptRate = 0F;
			int totalSchAcceptNum = noAcceptSchNum +acceptSchNum ;
			if(totalSchAcceptNum > 0) {
				schAcceptRate = 100 * ((float) acceptSchNum / (float) totalSchAcceptNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(departmentObj.getDepartmentName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(departmentObj.getDepartmentName());
			}
			/**
			 * 配货计划数
			 */
			busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
			/**
			 * 已验收计划数
			 */
			busiDetDataInfo.setAcceptPlanNum(acceptGsPlanNum);
			/**
			 * 未验收计划数
			 */
			busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
			/**
			 * 验收率
			 */
			busiDetDataInfo.setAcceptRate(acceptRate);
			
			/**
			 * 应验收学校数
			 */
			busiDetDataInfo.setShouldAcceptSchNum(totalSchAcceptNum);
			/**
			 * 已验收学校数
			 */
			busiDetDataInfo.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 未验收学校数
			 */
			busiDetDataInfo.setNoAcceptSchNum(noAcceptSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			busiDetDataInfo.setSchAcceptRate(schAcceptRate);
			
			busiDetDataInfoMap.put(departmentObj.getDepartmentName(), busiDetDataInfo);
		}
	}
	
	/**
	 * 获取配送信息：配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAccDistrInfoByAreaFromHive(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveGsService dbHiveGsService) {
		int dateCount = dates.length;
		int distCount = tedList.size();
		
		float acceptRate = 0;
		float schAcceptRate = 0;
		// 当天各区配货计划总数量
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
		//配送计划数量
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();
		//已验收数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		
		
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
						totalGsPlanNumMap.put(schDishCommon.getDistId(), 
								(totalGsPlanNumMap.get(schDishCommon.getDistId())==null?0:totalGsPlanNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getTotal());
						
					}else if(schDishCommon.getHaulStatus() == 3) {
						//已验收
						acceptSchNumsMap.put(schDishCommon.getDistId(), 
								(acceptSchNumsMap.get(schDishCommon.getDistId())==null?0:acceptSchNumsMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getSchoolTotal());
						
						acceptGsPlanNumMap.put(schDishCommon.getDistId(), 
								(acceptGsPlanNumMap.get(schDishCommon.getDistId())==null?0:acceptGsPlanNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getTotal());
					}else {
						//未验收：haul_status = -4 or haul_status = -2 or haul_status = -1 or haul_status = 0 or haul_status = 1 or haul_status = 2
						noAcceptSchNumsMap.put(schDishCommon.getDistId(), 
								(noAcceptSchNumsMap.get(schDishCommon.getDistId())==null?0:noAcceptSchNumsMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getSchoolTotal());
						
						noAcceptGsPlanNumMap.put(schDishCommon.getDistId(), 
								(noAcceptGsPlanNumMap.get(schDishCommon.getDistId())==null?0:noAcceptGsPlanNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getTotal());
					}
				}
			}
		}
		
		for(int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			//判断是否按区域获取配货计划数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if(distId != null) {
				if(!curDistId.equalsIgnoreCase(distId)) {
					continue ;
				}
			}
			
			int noAcceptSchNum = noAcceptSchNumsMap.get(String.valueOf(curDistId))==null?0:noAcceptSchNumsMap.get(String.valueOf(curDistId));
			int acceptSchNum = acceptSchNumsMap.get(String.valueOf(curDistId))==null?0:acceptSchNumsMap.get(String.valueOf(curDistId));
			int totalGsPlanNum = totalGsPlanNumMap.get(String.valueOf(curDistId))==null?0:totalGsPlanNumMap.get(String.valueOf(curDistId));
			int acceptGsPlanNum = acceptGsPlanNumMap.get(String.valueOf(curDistId))==null?0:acceptGsPlanNumMap.get(String.valueOf(curDistId));
			int  noAcceptGsPlanNum= noAcceptGsPlanNumMap.get(String.valueOf(curDistId))==null?0:noAcceptGsPlanNumMap.get(String.valueOf(curDistId));
			
			acceptRate = 0F;
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			
			schAcceptRate = 0F;
			int totalSchAcceptNum = noAcceptSchNum +acceptSchNum ;
			if(totalSchAcceptNum > 0) {
				schAcceptRate = 100 * ((float) acceptSchNum / (float) totalSchAcceptNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(curTdd.getName());
			}
			/**
			 * 配货计划数
			 */
			busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
			/**
			 * 已验收计划数
			 */
			busiDetDataInfo.setAcceptPlanNum(acceptGsPlanNum);
			/**
			 * 未验收计划数
			 */
			busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
			/**
			 * 验收率
			 */
			busiDetDataInfo.setAcceptRate(acceptRate);
			
			/**
			 * 应验收学校数
			 */
			busiDetDataInfo.setShouldAcceptSchNum(totalSchAcceptNum);
			/**
			 * 已验收学校数
			 */
			busiDetDataInfo.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 未验收学校数
			 */
			busiDetDataInfo.setNoAcceptSchNum(noAcceptSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			busiDetDataInfo.setSchAcceptRate(schAcceptRate);
			
			busiDetDataInfoMap.put(curTdd.getName(), busiDetDataInfo);
		}
	}
	
	/**
	 * 获取配送信息：配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAccDistrInfoByDepartmentFromHive(String departmentId,String[] dates, List<DepartmentObj> deparmentList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveGsService dbHiveGsService) {
		int dateCount = dates.length;
		int distCount = deparmentList.size();
		
		float acceptRate = 0;
		float schAcceptRate = 0;
		// 当天各区配货计划总数量
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
		//配送计划数量
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();
		//已验收数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		
		
		List<SchGsCommon> dishList = new ArrayList<>();
		dishList = dbHiveGsService.getGsList(DataKeyConfig.talbeLedgerMasterTotalD,listYearMonth, startDate, endDateAddOne, null, null, 
				-1, -1, null, null,departmentId,null, 4);
		if(dishList !=null && dishList.size() > 0) {
			for(SchGsCommon schDishCommon: dishList) {
				
				//区域为空，代表全市数据，此处去除
				if(CommonUtil.isEmpty(schDishCommon.getDepartmentId())) {
					continue;
				}
				
				if(schDishCommon.getDisSealStatus() ==null || "-1".equals(schDishCommon.getDisSealStatus())) {
					if(schDishCommon.getHaulStatus() ==null) {
						totalGsPlanNumMap.put(schDishCommon.getDepartmentId(), 
								(totalGsPlanNumMap.get(schDishCommon.getDepartmentId())==null?0:totalGsPlanNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getTotal());
						
					}else if(schDishCommon.getHaulStatus() == 3) {
						//已验收
						acceptSchNumsMap.put(schDishCommon.getDepartmentId(), 
								(acceptSchNumsMap.get(schDishCommon.getDepartmentId())==null?0:acceptSchNumsMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getSchoolTotal());
						
						acceptGsPlanNumMap.put(schDishCommon.getDepartmentId(), 
								(acceptGsPlanNumMap.get(schDishCommon.getDepartmentId())==null?0:acceptGsPlanNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getTotal());
					}else {
						//未验收：haul_status = -4 or haul_status = -2 or haul_status = -1 or haul_status = 0 or haul_status = 1 or haul_status = 2
						noAcceptSchNumsMap.put(schDishCommon.getDepartmentId(), 
								(noAcceptSchNumsMap.get(schDishCommon.getDepartmentId())==null?0:noAcceptSchNumsMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getSchoolTotal());
						
						noAcceptGsPlanNumMap.put(schDishCommon.getDepartmentId(), 
								(noAcceptGsPlanNumMap.get(schDishCommon.getDepartmentId())==null?0:noAcceptGsPlanNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getTotal());
					}
				}
			}
		}
		
		for(int i = 0; i < distCount; i++) {
			DepartmentObj departmentObj = deparmentList.get(i);
			String curDepartmentId = departmentObj.getDepartmentId();
			//判断是否按区域获取配货计划数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if(departmentId != null) {
				if(!curDepartmentId.equalsIgnoreCase(departmentId)) {
					continue ;
				}
			}
			
			int noAcceptSchNum = noAcceptSchNumsMap.get(String.valueOf(curDepartmentId))==null?0:noAcceptSchNumsMap.get(String.valueOf(curDepartmentId));
			int acceptSchNum = acceptSchNumsMap.get(String.valueOf(curDepartmentId))==null?0:acceptSchNumsMap.get(String.valueOf(curDepartmentId));
			int totalGsPlanNum = totalGsPlanNumMap.get(String.valueOf(curDepartmentId))==null?0:totalGsPlanNumMap.get(String.valueOf(curDepartmentId));
			int acceptGsPlanNum = acceptGsPlanNumMap.get(String.valueOf(curDepartmentId))==null?0:acceptGsPlanNumMap.get(String.valueOf(curDepartmentId));
			int  noAcceptGsPlanNum= noAcceptGsPlanNumMap.get(String.valueOf(curDepartmentId))==null?0:noAcceptGsPlanNumMap.get(String.valueOf(curDepartmentId));
			
			acceptRate = 0F;
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			
			schAcceptRate = 0F;
			int totalSchAcceptNum = noAcceptSchNum +acceptSchNum ;
			if(totalSchAcceptNum > 0) {
				schAcceptRate = 100 * ((float) acceptSchNum / (float) totalSchAcceptNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(departmentObj.getDepartmentName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(departmentObj.getDepartmentName());
			}
			/**
			 * 配货计划数
			 */
			busiDetDataInfo.setGsPlanNum(totalGsPlanNum);
			/**
			 * 已验收计划数
			 */
			busiDetDataInfo.setAcceptPlanNum(acceptGsPlanNum);
			/**
			 * 未验收计划数
			 */
			busiDetDataInfo.setNoAcceptPlanNum(noAcceptGsPlanNum);
			/**
			 * 验收率
			 */
			busiDetDataInfo.setAcceptRate(acceptRate);
			
			/**
			 * 应验收学校数
			 */
			busiDetDataInfo.setShouldAcceptSchNum(totalSchAcceptNum);
			/**
			 * 已验收学校数
			 */
			busiDetDataInfo.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 未验收学校数
			 */
			busiDetDataInfo.setNoAcceptSchNum(noAcceptSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			busiDetDataInfo.setSchAcceptRate(schAcceptRate);
			
			busiDetDataInfoMap.put(departmentObj.getDepartmentName(), busiDetDataInfo);
		}
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoByArea(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		String fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> platoonFeedTotalMap = null;
		int distCount = tedList.size();
		int dateCount = dates.length;
		int[][]totalMealSchNums = new int[dateCount][distCount];
		int[][]distDishSchNums = new int[dateCount][distCount];
		int[][]distNoDishSchNums = new int[dateCount][distCount];
		float[] distDishRates = new float[distCount];
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]   + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(platoonFeedTotalMap == null) {   
				
			}else{
				for(String curKey : platoonFeedTotalMap.keySet()) {
					for (int i = 0; i < tedList.size(); i++) {
						TEduDistrictDo curTdd = tedList.get(i);
						String curDistId = curTdd.getId();
						//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						if(distId != null && curDistId.compareTo(distId) != 0) {
							continue ;
						}
						// 区域排菜学校供餐数
						fieldPrefix = curDistId + "_";
						int mealSchNum = 0;
						int dishSchNum = 0;
						int noDishSchNum = 0;
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 3)
							{
								if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("已排菜")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										mealSchNum = Integer.parseInt(keyVal);
										dishSchNum = mealSchNum;
									}
								}
								else if(curKeys[1].equalsIgnoreCase("供餐") && curKeys[2].equalsIgnoreCase("未排菜")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										mealSchNum = Integer.parseInt(keyVal);
										noDishSchNum = mealSchNum;
									}
								}
							}
						}
						totalMealSchNums[k][i] += mealSchNum;
						distDishSchNums[k][i] += dishSchNum;
						distNoDishSchNums[k][i] += noDishSchNum;
					}
				}
			}
		}
		
		for (int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equals(distId)) {
					continue;
				}
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(curTdd.getName());
			}
			
			int totalDistSchNum = 0;
			int distDishSchNum = 0;
			int distNoDishSchNum = 0;
			for (int k = 0; k < dates.length; k++) {
				totalDistSchNum += totalMealSchNums[k][i];
				distDishSchNum += distDishSchNums[k][i];
				distNoDishSchNum += distNoDishSchNums[k][i];
			}
			distDishRates[i] = 0;
			if(totalDistSchNum > 0) {
				distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(distDishRates[i]);
				distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distDishRates[i] > 100) {
					distDishRates[i] = 100;
				}
			}
			
			//应排菜天数，即供餐天数
			busiDetDataInfo.setMealSchNum(totalDistSchNum);
			//已排菜学校数
			busiDetDataInfo.setDishSchNum(distDishSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			busiDetDataInfo.setNoDishSchNum(distNoDishSchNum);
			//未排菜天数
			busiDetDataInfo.setNoDishDayNum(distNoDishSchNum);
			//排菜率
			busiDetDataInfo.setDishRate(distDishRates[i]);
			
			busiDetDataInfoMap.put(curTdd.getName(), busiDetDataInfo);
		}
	}
	
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoByDepartment(String departmentId, String[] dates,Map<Integer, String> schoolDepartmentMap,
			int [] schOwnTypes,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		String fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> platoonFeedTotalMap = null;
		int departmentCount = schoolDepartmentMap.size();
		int dateCount = dates.length;
		int[][]totalMealSchNums = new int[dateCount][departmentCount];
		int[][]distDishSchNums = new int[dateCount][departmentCount];
		int[][]distNoDishSchNums = new int[dateCount][departmentCount];
		float[] distDishRates = new float[departmentCount];
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]   + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(platoonFeedTotalMap == null) {   
				
			}else{
				for(String curKey : platoonFeedTotalMap.keySet()) {
					for (int i = 0; i < schOwnTypes.length; i++) {
						int departmentIdTemp = schOwnTypes[i];
						// 区域排菜学校供餐数
						fieldPrefix = "department_"+departmentIdTemp + "_";
						int mealSchNum = 0;
						int dishSchNum = 0;
						int noDishSchNum = 0;
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 3)
							{
								if(curKeys[2].equalsIgnoreCase("供餐") && curKeys[3].equalsIgnoreCase("已排菜")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										mealSchNum = Integer.parseInt(keyVal);
										dishSchNum = mealSchNum;
									}
								}
								else if(curKeys[2].equalsIgnoreCase("供餐") && curKeys[3].equalsIgnoreCase("未排菜")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										mealSchNum = Integer.parseInt(keyVal);
										noDishSchNum = mealSchNum;
									}
								}
							}
						}
						totalMealSchNums[k][i] += mealSchNum;
						distDishSchNums[k][i] += dishSchNum;
						distNoDishSchNums[k][i] += noDishSchNum;
					}
				}
			}
		}
		
		for (int i = 0; i < departmentCount; i++) {
			int departmentIdTemp = schOwnTypes[i];
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(schoolDepartmentMap.get(departmentIdTemp));
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(schoolDepartmentMap.get(departmentIdTemp));
			}
			
			int totalDistSchNum = 0;
			int distDishSchNum = 0;
			int distNoDishSchNum = 0;
			for (int k = 0; k < dates.length; k++) {
				totalDistSchNum += totalMealSchNums[k][i];
				distDishSchNum += distDishSchNums[k][i];
				distNoDishSchNum += distNoDishSchNums[k][i];
			}
			distDishRates[i] = 0;
			if(totalDistSchNum > 0) {
				distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(distDishRates[i]);
				distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distDishRates[i] > 100) {
					distDishRates[i] = 100;
				}
			}
			
			//应排菜天数，即供餐天数
			busiDetDataInfo.setMealSchNum(totalDistSchNum);
			//已排菜学校数
			busiDetDataInfo.setDishSchNum(distDishSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			busiDetDataInfo.setNoDishSchNum(distNoDishSchNum);
			//未排菜天数
			busiDetDataInfo.setNoDishDayNum(distNoDishSchNum);
			//排菜率
			busiDetDataInfo.setDishRate(distDishRates[i]);
			
			busiDetDataInfoMap.put(schoolDepartmentMap.get(departmentIdTemp), busiDetDataInfo);
		}
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoByAreaFromHive(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveDishService dbHiveDishService) {
		// 当天排菜学校总数
		int distCount = tedList.size();
		float[] distDishRates = new float[distCount];
		// 当天各区排菜学校数量
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
		//学校总数
		Map<String,Integer> totalSchNumMap = new HashMap<>();
		//应排菜学校
		Map<String,Integer> totalMealSchNumMap = new HashMap<>();
		//已排菜学校
		Map<String,Integer> distDishSchNumMap = new HashMap<>();
		//未排菜学校
		Map<String,Integer> distNoDishSchNumMap = new HashMap<>();
		
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,departmentId,null, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNumMap.put(schDishCommon.getDistId(), 
									(totalSchNumMap.get(schDishCommon.getDistId())==null?0:totalSchNumMap.get(schDishCommon.getDistId())) 
									+ schDishCommon.getTotal());
						}
					}
				}else {
					if(schDishCommon.getHaveClass() != 1) {
						continue;
					}
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//应排菜数量的统计数据，
							//判断area,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1
							totalMealSchNumMap.put(schDishCommon.getDistId(), 
									(totalMealSchNumMap.get(schDishCommon.getDistId())==null?0:totalMealSchNumMap.get(schDishCommon.getDistId())) 
									+ schDishCommon.getTotal());
						}else{
							if(schDishCommon.getHavePlatoon() == 1) {
								//已排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =1
								distDishSchNumMap.put(schDishCommon.getDistId(), 
										(distDishSchNumMap.get(schDishCommon.getDistId())==null?0:distDishSchNumMap.get(schDishCommon.getDistId())) 
										+ schDishCommon.getTotal());
							}else if(schDishCommon.getHavePlatoon() == 0) {
								//未排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =0
								distNoDishSchNumMap.put(schDishCommon.getDistId(), 
										(distNoDishSchNumMap.get(schDishCommon.getDistId())==null?0:distNoDishSchNumMap.get(schDishCommon.getDistId())) 
										+ schDishCommon.getTotal());
							}
						}
					}else{
					}
				}
			}
		}
		
		for (int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equals(distId)) {
					continue;
				}
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(curTdd.getName());
			}
			
			int totalSchNum = totalSchNumMap.get(String.valueOf(curDistId))==null?0:totalSchNumMap.get(String.valueOf(curDistId));
			int totalDistSchNum = totalMealSchNumMap.get(String.valueOf(curDistId))==null?0:totalMealSchNumMap.get(String.valueOf(curDistId));
			int distDishSchNum = distDishSchNumMap.get(String.valueOf(curDistId))==null?0:distDishSchNumMap.get(String.valueOf(curDistId));
			int distNoDishSchNum = distNoDishSchNumMap.get(String.valueOf(curDistId))==null?0:distNoDishSchNumMap.get(String.valueOf(curDistId));
			
			distDishRates[i] = 0;
			if(totalDistSchNum > 0) {
				distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(distDishRates[i]);
				distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distDishRates[i] > 100) {
					distDishRates[i] = 100;
				}
			}
			
			//学校总数
			busiDetDataInfo.setTotalSchNum(totalSchNum);
			//应排菜天数，即供餐天数
			busiDetDataInfo.setMealSchNum(totalDistSchNum);
			//已排菜学校数
			busiDetDataInfo.setDishSchNum(distDishSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			busiDetDataInfo.setNoDishSchNum(distNoDishSchNum);
			//未排菜天数
			busiDetDataInfo.setNoDishDayNum(distNoDishSchNum);
			//排菜率
			busiDetDataInfo.setDishRate(distDishRates[i]);
			
			busiDetDataInfoMap.put(curTdd.getName(), busiDetDataInfo);
		}
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoByDeparmentFromHive(String departmentId,String[] dates, List<DepartmentObj> deparmentList,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveDishService dbHiveDishService) {
		// 当天排菜学校总数
		int distCount = deparmentList.size();
		float[] distDishRates = new float[distCount];
		// 当天各区排菜学校数量
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
		//学校总数
		Map<String,Integer> totalSchNumMap = new HashMap<>();
		//应排菜学校
		Map<String,Integer> totalMealSchNumMap = new HashMap<>();
		//已排菜学校
		Map<String,Integer> distDishSchNumMap = new HashMap<>();
		//未排菜学校
		Map<String,Integer> distNoDishSchNumMap = new HashMap<>();
		
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, null, null, 
				-1, -1, null, null,departmentId,null, 4);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNumMap.put(schDishCommon.getDepartmentId(), 
									(totalSchNumMap.get(schDishCommon.getDepartmentId())==null?0:totalSchNumMap.get(schDishCommon.getDepartmentId())) 
									+ schDishCommon.getTotal());
						}
					}
				}else {
					if(schDishCommon.getHaveClass() != 1) {
						continue;
					}
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//应排菜数量的统计数据，
							//判断area,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1
							totalMealSchNumMap.put(schDishCommon.getDepartmentId(), 
									(totalMealSchNumMap.get(schDishCommon.getDepartmentId())==null?0:totalMealSchNumMap.get(schDishCommon.getDepartmentId())) 
									+ schDishCommon.getTotal());
						}else{
							if(schDishCommon.getHavePlatoon() == 1) {
								//已排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =1
								distDishSchNumMap.put(schDishCommon.getDepartmentId(), 
										(distDishSchNumMap.get(schDishCommon.getDepartmentId())==null?0:distDishSchNumMap.get(schDishCommon.getDepartmentId())) 
										+ schDishCommon.getTotal());
							}else if(schDishCommon.getHavePlatoon() == 0) {
								//未排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =0
								distNoDishSchNumMap.put(schDishCommon.getDepartmentId(), 
										(distNoDishSchNumMap.get(schDishCommon.getDepartmentId())==null?0:distNoDishSchNumMap.get(schDishCommon.getDepartmentId())) 
										+ schDishCommon.getTotal());
							}
						}
					}else{
					}
				}
			}
		}
		
		for (int i = 0; i < distCount; i++) {
			DepartmentObj departmentObj = deparmentList.get(i);
			String curDepartmentId = departmentObj.getDepartmentId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (departmentId != null) {
				if (!curDepartmentId.equals(departmentId)) {
					continue;
				}
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(departmentObj.getDepartmentName());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(departmentObj.getDepartmentName());
			}
			
			int totalSchNum = totalSchNumMap.get(String.valueOf(curDepartmentId))==null?0:totalSchNumMap.get(String.valueOf(curDepartmentId));
			int totalDistSchNum = totalMealSchNumMap.get(String.valueOf(curDepartmentId))==null?0:totalMealSchNumMap.get(String.valueOf(curDepartmentId));
			int distDishSchNum = distDishSchNumMap.get(String.valueOf(curDepartmentId))==null?0:distDishSchNumMap.get(String.valueOf(curDepartmentId));
			int distNoDishSchNum = distNoDishSchNumMap.get(String.valueOf(curDepartmentId))==null?0:distNoDishSchNumMap.get(String.valueOf(curDepartmentId));
			
			distDishRates[i] = 0;
			if(totalDistSchNum > 0) {
				distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(distDishRates[i]);
				distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distDishRates[i] > 100) {
					distDishRates[i] = 100;
				}
			}
			
			//学校总数
			busiDetDataInfo.setTotalSchNum(totalSchNum);
			//应排菜天数，即供餐天数
			busiDetDataInfo.setMealSchNum(totalDistSchNum);
			//已排菜学校数
			busiDetDataInfo.setDishSchNum(distDishSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			busiDetDataInfo.setNoDishSchNum(distNoDishSchNum);
			//未排菜天数
			busiDetDataInfo.setNoDishDayNum(distNoDishSchNum);
			//排菜率
			busiDetDataInfo.setDishRate(distDishRates[i]);
			
			busiDetDataInfoMap.put(departmentObj.getDepartmentName(), busiDetDataInfo);
		}
	}

	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoByNature(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		int[] schOwnTypes = { 0, 2,3, 4};
		
		int distCount = schoolPropertyMap.size();
		int dateCount = dates.length;
		int[][]totalMealSchNums = new int[dateCount][distCount];
		int[][]distDishSchNums = new int[dateCount][distCount];
		int[][]distNoDishSchNums = new int[dateCount][distCount];
		float[] distDishRates = new float[distCount];
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]   + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			
			// 供餐学校已排菜和未排菜数
			for (int i = 0; i<schOwnTypes.length ; i++) {	
				int propertyId = schOwnTypes[i];
				String filedPre = "";
				//如果区域不为空
				if(StringUtils.isNotEmpty(distId)) {
					filedPre ="area_"+distId+"_";
				}
				
				Integer dishSchNum = 0;
				int mealSchNum = 0;
				int noDishSchNum = 0;
				/*if(propertyId!=4) {
					//指定学校性质的已排菜学校个数
					keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key,filedPre+ "nature_"+propertyId+"_nature-sub_null_供餐_已排菜");
					if(keyVal!=null && Integer.parseInt(keyVal)>0) {
						mealSchNum += Integer.parseInt(keyVal);
						dishSchNum = Integer.parseInt(keyVal);
					}
					
					//指定学校性质的未排菜学校个数
					keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, filedPre+ "nature_"+propertyId+"_nature-sub_null_供餐_未排菜");
					if(keyVal!=null && Integer.parseInt(keyVal)>0) {
						mealSchNum += Integer.parseInt(keyVal);
						noDishSchNum = Integer.parseInt(keyVal);
					}
					
				}else {*/
					//其他性质的学习，分子类型
					Map<Integer, String> natureSubTypeMap = new HashMap<Integer,String>();
					natureSubTypeMap.put(1, "集体办");
					natureSubTypeMap.put(2, "部队办");
					natureSubTypeMap.put(3, "企事业办");
					natureSubTypeMap.put(4, "企业合作");
					natureSubTypeMap.put(5, "国际办");
					natureSubTypeMap.put(9, "其它");
					natureSubTypeMap.put(null, "非正常数据");
					for (Map.Entry<Integer,String> entrySon : natureSubTypeMap.entrySet()) {
						keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
								SpringConfig.RedisDBIdx, key, filedPre+ "nature_"+propertyId+"_nature-sub_"+entrySon.getKey()+"_供餐_已排菜");
						if(keyVal!=null && Integer.parseInt(keyVal)>0) {
							mealSchNum += Integer.parseInt(keyVal);
							dishSchNum += Integer.parseInt(keyVal);
						}
						
						keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
								SpringConfig.RedisDBIdx, key, filedPre+ "nature_"+propertyId+"_nature-sub_"+entrySon.getKey()+"_供餐_未排菜");
						if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
							mealSchNum += Integer.parseInt(keyVal);
							noDishSchNum += Integer.parseInt(keyVal);
						}
						
						if(4==propertyId) {
							//如果是4：其他，则将shanghai-nature_null_nature-sub_null数据同样归为其他
							keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
									SpringConfig.RedisDBIdx, key, filedPre+ "nature_null_nature-sub_"+entrySon.getKey()+"_供餐_已排菜");
							if(keyVal!=null && Integer.parseInt(keyVal)>0) {
								mealSchNum += Integer.parseInt(keyVal);
								dishSchNum += Integer.parseInt(keyVal);
							}
							
							keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
									SpringConfig.RedisDBIdx, key, filedPre+ "nature_null_nature-sub_"+entrySon.getKey()+"_供餐_未排菜");
							if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
								mealSchNum += Integer.parseInt(keyVal);
								noDishSchNum += Integer.parseInt(keyVal);
							}
						}
					}
					
					
				//}
				totalMealSchNums[k][i] += mealSchNum;
				distDishSchNums[k][i] += dishSchNum;
				distNoDishSchNums[k][i] += noDishSchNum;
			}
			
		}
		
		for (int i = 0; i < distCount; i++) {
			int curDistId = schOwnTypes[i];
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(schoolPropertyMap.get(curDistId));
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(schoolPropertyMap.get(curDistId));
			}
			
			int totalDistSchNum = 0;
			int distDishSchNum = 0;
			int distNoDishSchNum = 0;
			for (int k = 0; k < dates.length; k++) {
				totalDistSchNum += totalMealSchNums[k][i];
				distDishSchNum += distDishSchNums[k][i];
				distNoDishSchNum += distNoDishSchNums[k][i];
			}
			distDishRates[i] = 0;
			if(totalDistSchNum > 0) {
				distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(distDishRates[i]);
				distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distDishRates[i] > 100) {
					distDishRates[i] = 100;
				}
			}
			
			//应排菜天数，即供餐天数
			busiDetDataInfo.setMealSchNum(totalDistSchNum);
			//已排菜学校数
			busiDetDataInfo.setDishSchNum(distDishSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			busiDetDataInfo.setNoDishSchNum(distNoDishSchNum);
			//未排菜天数
			busiDetDataInfo.setNoDishDayNum(distNoDishSchNum);
			//排菜率
			busiDetDataInfo.setDishRate(distDishRates[i]);
			
			busiDetDataInfoMap.put(schoolPropertyMap.get(curDistId), busiDetDataInfo);
		}
		
		
	}
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoByNatureFromHive(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveDishService dbHiveDishService) {
		int[] schOwnTypes = { 0, 2,3, 4};
		
		int distCount = schoolPropertyMap.size();
		int dateCount = dates.length;
		float[] distDishRates = new float[distCount];
		// 当天各区排菜学校数量
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
		//学校总数
		Map<String,Integer> totalSchNumMap = new HashMap<>();
		//应排菜学校
		Map<String,Integer> totalMealSchNumMap = new HashMap<>();
		//已排菜学校
		Map<String,Integer> distDishSchNumMap = new HashMap<>();
		//未排菜学校
		Map<String,Integer> distNoDishSchNumMap = new HashMap<>();
		
		
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,departmentId, null,1);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNumMap.put(schDishCommon.getSchoolNatureName(), 
									(totalSchNumMap.get(schDishCommon.getSchoolNatureName())==null?0:totalSchNumMap.get(schDishCommon.getSchoolNatureName())) 
									+ schDishCommon.getTotal());
						}
					}
					
				}else {
					if(schDishCommon.getHaveClass() != 1) {
						continue;
					}
					

					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//应排菜数量的统计数据，
							//判断area,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1
							totalMealSchNumMap.put(schDishCommon.getSchoolNatureName(), 
									(totalMealSchNumMap.get(schDishCommon.getSchoolNatureName())==null?0:totalMealSchNumMap.get(schDishCommon.getSchoolNatureName())) 
									+ schDishCommon.getTotal());
						}else{
							if(schDishCommon.getHavePlatoon() == 1) {
								//已排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =1
								distDishSchNumMap.put(schDishCommon.getSchoolNatureName(), 
										(distDishSchNumMap.get(schDishCommon.getSchoolNatureName())==null?0:distDishSchNumMap.get(schDishCommon.getSchoolNatureName())) 
										+ schDishCommon.getTotal());
							}else if(schDishCommon.getHavePlatoon() == 0) {
								//未排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =0
								distNoDishSchNumMap.put(schDishCommon.getSchoolNatureName(), 
										(distNoDishSchNumMap.get(schDishCommon.getSchoolNatureName())==null?0:distNoDishSchNumMap.get(schDishCommon.getSchoolNatureName())) 
										+ schDishCommon.getTotal());
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < distCount; i++) {
			int curDistId = schOwnTypes[i];
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(schoolPropertyMap.get(curDistId));
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(schoolPropertyMap.get(curDistId));
			}
			
			int totalSchNum = totalSchNumMap.get(String.valueOf(curDistId))==null?0:totalSchNumMap.get(String.valueOf(curDistId));
			int totalDistSchNum = totalMealSchNumMap.get(String.valueOf(curDistId))==null?0:totalMealSchNumMap.get(String.valueOf(curDistId));
			int distDishSchNum = distDishSchNumMap.get(String.valueOf(curDistId))==null?0:distDishSchNumMap.get(String.valueOf(curDistId));
			int distNoDishSchNum = distNoDishSchNumMap.get(String.valueOf(curDistId))==null?0:distNoDishSchNumMap.get(String.valueOf(curDistId));
			
			distDishRates[i] = 0;
			if(totalDistSchNum > 0) {
				distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(distDishRates[i]);
				distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distDishRates[i] > 100) {
					distDishRates[i] = 100;
				}
			}
			//学校总数
			busiDetDataInfo.setTotalSchNum(totalSchNum);
			//应排菜天数，即供餐天数
			busiDetDataInfo.setMealSchNum(totalDistSchNum);
			//已排菜学校数
			busiDetDataInfo.setDishSchNum(distDishSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			busiDetDataInfo.setNoDishSchNum(distNoDishSchNum);
			//未排菜天数
			busiDetDataInfo.setNoDishDayNum(distNoDishSchNum);
			//排菜率
			busiDetDataInfo.setDishRate(distDishRates[i]);
			
			busiDetDataInfoMap.put(schoolPropertyMap.get(curDistId), busiDetDataInfo);
		}
		
		
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)(按学校类型统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoBySchoolType(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		//String fieldPrefix = "";
		// 当天排菜学校总数
		//Map<String, String> platoonFeedTotalMap = null;
		//List<Integer> list = new ArrayList<Integer>();
		
		int[] schOwnTypes = { 0,1,2, 3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
		
		int distCount = schoolPropertyMap.size();
		int dateCount = dates.length;
		int[][]totalMealSchNums = new int[dateCount][distCount];
		int[][]distDishSchNums = new int[dateCount][distCount];
		int[][]distNoDishSchNums = new int[dateCount][distCount];
		float[] distDishRates = new float[distCount];
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]   + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			
			// 供餐学校已排菜和未排菜数
			//int dishSchNumTotal = 0;
			//int noDishSchNumTotal = 0;
			for (int i = 0; i<schOwnTypes.length ; i++) {	
				int propertyId = schOwnTypes[i];
				String filedPre = "";
				//如果区域不为空
				if(StringUtils.isNotEmpty(distId)) {
					filedPre ="area_"+distId+"_";
				}
				
				Integer dishSchNum = 0;
				int mealSchNum = 0;
				int noDishSchNum = 0;
				
				//指定学校性质的已排菜学校个数
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key,filedPre+ "level_"+propertyId+"_供餐_已排菜");
				if(keyVal!=null && Integer.parseInt(keyVal)>0) {
					dishSchNum = Integer.parseInt(keyVal);
					mealSchNum += Integer.parseInt(keyVal);
				}
				
				//指定学校性质的未排菜学校个数
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, filedPre+ "level_"+propertyId+"_供餐_未排菜");
				if(keyVal!=null && Integer.parseInt(keyVal)>0) {
					noDishSchNum = Integer.parseInt(keyVal);
					mealSchNum += Integer.parseInt(keyVal);
				}
				
				totalMealSchNums[k][i] += mealSchNum;
				distDishSchNums[k][i] += dishSchNum;
				distNoDishSchNums[k][i] += noDishSchNum;
			}
			
		}
		
		for (int i = 0; i < distCount; i++) {
			int curDistId = schOwnTypes[i];
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(schoolPropertyMap.get(curDistId));
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(schoolPropertyMap.get(curDistId));
			}
			
			int totalDistSchNum = 0;
			int distDishSchNum = 0;
			int distNoDishSchNum = 0;
			for (int k = 0; k < dates.length; k++) {
				totalDistSchNum += totalMealSchNums[k][i];
				distDishSchNum += distDishSchNums[k][i];
				distNoDishSchNum += distNoDishSchNums[k][i];
			}
			distDishRates[i] = 0;
			if(totalDistSchNum > 0) {
				distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(distDishRates[i]);
				distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distDishRates[i] > 100) {
					distDishRates[i] = 100;
				}
			}
			
			//应排菜天数，即供餐天数
			busiDetDataInfo.setMealSchNum(totalDistSchNum);
			//已排菜学校数
			busiDetDataInfo.setDishSchNum(distDishSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			busiDetDataInfo.setNoDishSchNum(distNoDishSchNum);
			//未排菜天数
			busiDetDataInfo.setNoDishDayNum(distNoDishSchNum);
			//排菜率
			busiDetDataInfo.setDishRate(distDishRates[i]);
			
			busiDetDataInfoMap.put(schoolPropertyMap.get(curDistId), busiDetDataInfo);
		}
		
		
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)(按学校类型统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoBySchoolTypeFromHive(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveDishService dbHiveDishService) {
		//String fieldPrefix = "";
		// 当天排菜学校总数
		//Map<String, String> platoonFeedTotalMap = null;
		//List<Integer> list = new ArrayList<Integer>();
		
		int[] schOwnTypes = { 0,1,2, 3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
		
		int distCount = schoolPropertyMap.size();
		float[] distDishRates = new float[distCount];
		// 当天各区排菜学校数量
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
		//学校总数
		Map<String,Integer> totalSchNumMap = new HashMap<>();
		//应排菜学校
		Map<String,Integer> totalMealSchNumMap = new HashMap<>();
		//已排菜学校
		Map<String,Integer> distDishSchNumMap = new HashMap<>();
		//未排菜学校
		Map<String,Integer> distNoDishSchNumMap = new HashMap<>();
		
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,departmentId,null, 2);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNumMap.put(schDishCommon.getLevelName(), 
									(totalSchNumMap.get(schDishCommon.getLevelName())==null?0:totalSchNumMap.get(schDishCommon.getLevelName())) 
									+ schDishCommon.getTotal());
						}
					}
				}else {
					if(schDishCommon.getHaveClass() != 1) {
						continue;
					}
					
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//应排菜数量的统计数据，
							//判断area,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1
							totalMealSchNumMap.put(schDishCommon.getLevelName(), 
									(totalMealSchNumMap.get(schDishCommon.getLevelName())==null?0:totalMealSchNumMap.get(schDishCommon.getLevelName())) 
									+ schDishCommon.getTotal());
						}else{
							if(schDishCommon.getHavePlatoon() == 1) {
								//已排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =1
								distDishSchNumMap.put(schDishCommon.getLevelName(), 
										(distDishSchNumMap.get(schDishCommon.getLevelName())==null?0:distDishSchNumMap.get(schDishCommon.getLevelName())) 
										+ schDishCommon.getTotal());
							}else if(schDishCommon.getHavePlatoon() == 0) {
								//未排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =0
								distNoDishSchNumMap.put(schDishCommon.getLevelName(), 
										(distNoDishSchNumMap.get(schDishCommon.getLevelName())==null?0:distNoDishSchNumMap.get(schDishCommon.getLevelName())) 
										+ schDishCommon.getTotal());
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < distCount; i++) {
			int curDistId = schOwnTypes[i];
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(schoolPropertyMap.get(curDistId));
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(schoolPropertyMap.get(curDistId));
			}
			
			int totalSchNum = totalSchNumMap.get(String.valueOf(curDistId))==null?0:totalSchNumMap.get(String.valueOf(curDistId));
			int totalDistSchNum = totalMealSchNumMap.get(String.valueOf(curDistId))==null?0:totalMealSchNumMap.get(String.valueOf(curDistId));
			int distDishSchNum = distDishSchNumMap.get(String.valueOf(curDistId))==null?0:distDishSchNumMap.get(String.valueOf(curDistId));
			int distNoDishSchNum = distNoDishSchNumMap.get(String.valueOf(curDistId))==null?0:distNoDishSchNumMap.get(String.valueOf(curDistId));
			
			distDishRates[i] = 0;
			if(totalDistSchNum > 0) {
				distDishRates[i] = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(distDishRates[i]);
				distDishRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distDishRates[i] > 100) {
					distDishRates[i] = 100;
				}
			}
			
			//学校总数
			busiDetDataInfo.setTotalSchNum(totalSchNum);
			//应排菜天数，即供餐天数
			busiDetDataInfo.setMealSchNum(totalDistSchNum);
			//已排菜学校数
			busiDetDataInfo.setDishSchNum(distDishSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			busiDetDataInfo.setNoDishSchNum(distNoDishSchNum);
			//未排菜天数
			busiDetDataInfo.setNoDishDayNum(distNoDishSchNum);
			//排菜率
			busiDetDataInfo.setDishRate(distDishRates[i]);
			
			busiDetDataInfoMap.put(schoolPropertyMap.get(curDistId), busiDetDataInfo);
		}
		
		
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)(按学校所属主管部门)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoBySlave(String departmentId,String distId, String[] dates,Map<String, String> slaveMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		String key = "";
		String keyVal = "";
		// 当天排菜学校总数
		Map<String,Integer> totalMealSchNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distDishSchNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distNoDishSchNumMap = new HashMap<String,Integer>();
		
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]   + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			
			// 供餐学校已排菜和未排菜数
			for (Map.Entry<String, String> entry : slaveMap.entrySet()) {	
				String[] keys = entry.getKey().split("_");
				String masterid = keys[0];
				String slave = keys[1];
				if("3".equals(masterid) && CommonUtil.isEmpty(slave)) {
					slave = "其他";
				}
				
				//String filedPre = "";
				//如果区域不为空(区域过滤在组织所属部门时统一过滤)
				/*if(StringUtils.isNotEmpty(distId)) {
					filedPre ="area_"+distId+"_";
				}*/
				
				Integer dishSchNum = 0;
				int mealSchNum = 0;
				int noDishSchNum = 0;
				
				//指定学校性质的已排菜学校个数
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key,"masterid_"+masterid+"_slave_"+slave+"_供餐_已排菜");
				if(keyVal!=null && Integer.parseInt(keyVal)>0) {
					dishSchNum = Integer.parseInt(keyVal);
					mealSchNum += Integer.parseInt(keyVal);
				}
				
				//指定学校性质的未排菜学校个数
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key,"masterid_"+masterid+"_slave_"+slave+"_供餐_未排菜");
				if(keyVal!=null && Integer.parseInt(keyVal)>0) {
					noDishSchNum = Integer.parseInt(keyVal);
					mealSchNum += Integer.parseInt(keyVal);
				}
				
				totalMealSchNumMap.put(entry.getKey(), (totalMealSchNumMap.get(entry.getKey())==null?0:totalMealSchNumMap.get(entry.getKey()))+mealSchNum);
				distDishSchNumMap.put(entry.getKey(), (distDishSchNumMap.get(entry.getKey())==null?0:distDishSchNumMap.get(entry.getKey()))+dishSchNum);
				distNoDishSchNumMap.put(entry.getKey(), (distNoDishSchNumMap.get(entry.getKey())==null?0:distNoDishSchNumMap.get(entry.getKey()))+noDishSchNum);
			}
			
		}
		
		float distDishRate = 0;
		for (Map.Entry<String, String> entry : slaveMap.entrySet()) {
			String[] keys = entry.getKey().split("_");
			String masterid = keys[0];
			String slave = keys[1];
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				busiDetDataInfo.setStatClassName(entry.getValue());
				//区域名称
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				busiDetDataInfo.setStatPropName(slaveName);
			}
			
			int totalDistSchNum = totalMealSchNumMap.get(entry.getKey());
			int distDishSchNum = distDishSchNumMap.get(entry.getKey());
			int distNoDishSchNum = distNoDishSchNumMap.get(entry.getKey());
			distDishRate = 0;
			if(totalDistSchNum > 0) {
				distDishRate = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(distDishRate);
				distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distDishRate > 100) {
					distDishRate = 100;
				}
			}
			
			//应排菜天数，即供餐天数
			busiDetDataInfo.setMealSchNum(totalDistSchNum);
			//已排菜学校数
			busiDetDataInfo.setDishSchNum(distDishSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			busiDetDataInfo.setNoDishSchNum(distNoDishSchNum);
			//未排菜天数
			busiDetDataInfo.setNoDishDayNum(distNoDishSchNum);
			//排菜率
			busiDetDataInfo.setDishRate(distDishRate);
			
			busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
		}
		
		
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)(按学校所属主管部门)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	private void getDishInfoBySlaveFromHive(String departmentId,String distId, String[] dates,Map<String, String> slaveMap,
			Map<String,BusiDetDataInfo> busiDetDataInfoMap,DbHiveDishService dbHiveDishService) {
		// 当天各区排菜学校数量
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
		//学校总数
		Map<String,Integer> totalSchNumMap = new HashMap<>();
		//应排菜学校
		Map<String,Integer> totalMealSchNumMap = new HashMap<>();
		//已排菜学校
		Map<String,Integer> distDishSchNumMap = new HashMap<>();
		//未排菜学校
		Map<String,Integer> distNoDishSchNumMap = new HashMap<>();
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
		List<SchDishCommon> dishList = new ArrayList<>();
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, null, null, 
				-1, -1, null, null,departmentId,null, 3);
		if(dishList !=null && dishList.size() > 0) {
			for(SchDishCommon schDishCommon: dishList) {
				String key = schDishCommon.getDepartmentMasterId() + "_" +schDishCommon.getDepartmentSlaveIdName();
				if(schDishCommon.getHaveClass() ==null || schDishCommon.getHaveClass() == -1) {
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//各区学校总数
							//判断area,have_class,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null
							totalSchNumMap.put(key, 
									(totalSchNumMap.get(key )==null?0:totalSchNumMap.get(key)) 
									+ schDishCommon.getTotal());
						}
					}
				}else {
					if(schDishCommon.getHaveClass() != 1) {
						continue;
					}
					if(schDishCommon.getPlatoonDealStatus() ==null || schDishCommon.getPlatoonDealStatus() == -1 ) {
						if(schDishCommon.getHavePlatoon() ==null || schDishCommon.getHavePlatoon() == -1) {
							//应排菜数量的统计数据，
							//判断area,have_platoon,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1
							totalMealSchNumMap.put(key, 
									(totalMealSchNumMap.get(key)==null?0:totalMealSchNumMap.get(key)) 
									+ schDishCommon.getTotal());
						}else{
							if(schDishCommon.getHavePlatoon() == 1) {
								//已排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =1
								distDishSchNumMap.put(key, 
										(distDishSchNumMap.get(key)==null?0:distDishSchNumMap.get(key)) 
										+ schDishCommon.getTotal());
							}else if(schDishCommon.getHavePlatoon() == 0) {
								//未排菜数量的统计数据，
								//判断area,level_name,school_nature_name,department_master_id,department_slave_id_name is null 和  have_class=1 和 have_platoon =0
								distNoDishSchNumMap.put(key, 
										(distNoDishSchNumMap.get(key)==null?0:distNoDishSchNumMap.get(key)) 
										+ schDishCommon.getTotal());
							}
						}
					}
				}
			}
		}
		
		float distDishRate = 0;
		for (Map.Entry<String, String> entry : slaveMap.entrySet()) {
			String[] keys = entry.getKey().split("_");
			String masterid = keys[0];
			String slave = keys[1];
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				busiDetDataInfo.setStatClassName(entry.getValue());
				//区域名称
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				busiDetDataInfo.setStatPropName(slaveName);
			}
			
			//转换，hive库中 区属的子项是数字，slaveMap中为中文
			String dataKey = masterid+"_";
			if("3".equals(masterid)) {
				dataKey += AppModConfig.compDepNameToIdMap3.get(slave);
			}else {
				dataKey += slave;
			}
			
			int totalSchNum = totalSchNumMap.get(dataKey)==null?0:totalSchNumMap.get(dataKey);
			int totalDistSchNum = totalMealSchNumMap.get(dataKey)==null?0:totalMealSchNumMap.get(dataKey);
			int distDishSchNum = distDishSchNumMap.get(dataKey)==null?0:distDishSchNumMap.get(dataKey);
			int distNoDishSchNum = distNoDishSchNumMap.get(dataKey)==null?0:distNoDishSchNumMap.get(dataKey);
			
			distDishRate = 0;
			if(totalDistSchNum > 0) {
				distDishRate = 100 * ((float) distDishSchNum / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(distDishRate);
				distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (distDishRate > 100) {
					distDishRate = 100;
				}
			}
			
			//学校总数
			busiDetDataInfo.setTotalSchNum(totalSchNum);
			//应排菜天数，即供餐天数
			busiDetDataInfo.setMealSchNum(totalDistSchNum);
			//已排菜学校数
			busiDetDataInfo.setDishSchNum(distDishSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			busiDetDataInfo.setNoDishSchNum(distNoDishSchNum);
			//未排菜天数
			busiDetDataInfo.setNoDishDayNum(distNoDishSchNum);
			//排菜率
			busiDetDataInfo.setDishRate(distDishRate);
			
			busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
		}
		
		
	}
	
	
	/**
	 * 获取监管学校总数
	 * @param distId
	 * @return
	 */
	private void getSupSchNumByArea(String departmentId,String distId,String currDistName, List<TEduDistrictDo> tedList,Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		//Integer sumSupSchNum =0;
		Integer supSchNum = 0;
		String key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		String keyVal = null;
		if(StringUtils.isEmpty(distId)) {
			for (int i = 0; i < tedList.size(); i++) {
				TEduDistrictDo curTdd = tedList.get(i);
				String curDistId = curTdd.getId();
			    keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, "area_"+curDistId);
			    if(keyVal != null) {
					supSchNum = Integer.parseInt(keyVal);
					
					BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(curTdd.getName());
					if(busiDetDataInfo==null) {
						busiDetDataInfo = new BusiDetDataInfo();
						//区域名称
						busiDetDataInfo.setStatPropName(curTdd.getName());
					}
					//学校总数
					busiDetDataInfo.setTotalSchNum(supSchNum);
					//sumSupSchNum +=supSchNum;
					busiDetDataInfoMap.put(curTdd.getName(), busiDetDataInfo);
				}
			}
		}else {
			//如果区域不为空，则去除区域学校总数
			keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, "area_"+distId);
			if(keyVal != null) {
				supSchNum = Integer.parseInt(keyVal);
				currDistName = AppModConfig.compDepIdToNameMap3.get(distId);
				if(currDistName!=null && currDistName.indexOf("教育局") >=0) {
					currDistName = currDistName.replace("教育局", "");
				}
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(currDistName);
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//区域名称
					busiDetDataInfo.setStatPropName(currDistName);
					
				}
				
				//学校总数
				busiDetDataInfo.setTotalSchNum(supSchNum);
				busiDetDataInfoMap.put(currDistName, busiDetDataInfo);
			}
		}
		
		/**
		 * 合计
		 */
		/*BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get("合计");
		if(busiDetDataInfo==null) {
			busiDetDataInfo = new BusiDetDataInfo();
			//区域名称
			busiDetDataInfo.setStatPropName("合计");
		}
		//学校总数
		busiDetDataInfo.setTotalSchNum(sumSupSchNum);*/
	}
	
	/**
	 * 获取监管学校总数
	 * @param distId
	 * @return
	 */
	private void getSupSchNumByDepartment(String departmentId, Map<Integer, String> departmentMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap) { //Integer sumSupSchNum =0;
		Integer supSchNum = 0;
		String key = DataKeyConfig.schoolData;
		String keyVal = null;
		for (Map.Entry<Integer, String> entry : departmentMap.entrySet()) {
			key = DataKeyConfig.areaSchoolData+entry.getKey();
		    keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, "shanghai");
		    if(keyVal != null) {
				supSchNum = Integer.parseInt(keyVal);
				
				BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
				if(busiDetDataInfo==null) {
					busiDetDataInfo = new BusiDetDataInfo();
					//区域名称
					busiDetDataInfo.setStatPropName(entry.getValue());
				}
				//学校总数
				busiDetDataInfo.setTotalSchNum(supSchNum);
				//sumSupSchNum +=supSchNum;
				busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
			}
		}
		
		/**
		 * 合计
		 */
		/*BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get("合计");
		if(busiDetDataInfo==null) {
			busiDetDataInfo = new BusiDetDataInfo();
			//区域名称
			busiDetDataInfo.setStatPropName("合计");
		}
		//学校总数
		busiDetDataInfo.setTotalSchNum(sumSupSchNum);*/
	}
	
	/**
	 * 获取监管学校总数（按学校性质统计）
	 * @param distId
	 * @return
	 */
	private void getSupSchNumByNature(String departmentId,String distId,String currDistName,Map<Integer, String> schoolPropertyMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		Integer supSchNum = 0;
		String key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		String keyVal = null;
		//redis的类型结构
		//shanghai-nature_0_nature-sub_null
		String preFiled="shanghai-";
		if(StringUtils.isNotEmpty(distId)) {
			//area_1_nature_0_nature-sub_null
			preFiled="area_"+distId+"_";
		}
		for (Map.Entry<Integer,String> entry : schoolPropertyMap.entrySet()) {
			
			supSchNum = 0;
			/*if(entry.getKey()!=4) {
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, preFiled+"nature_"+entry.getKey()+"_nature-sub_null");
				if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
					supSchNum = Integer.parseInt(keyVal);
				}
			}else {*/
				//其他性质的学习，分子类型
				Map<Integer, String> natureSubTypeMap = new HashMap<Integer,String>();
				natureSubTypeMap.put(1, "集体办");
				natureSubTypeMap.put(2, "部队办");
				natureSubTypeMap.put(3, "企事业办");
				natureSubTypeMap.put(4, "企业合作");
				natureSubTypeMap.put(5, "国际办");
				natureSubTypeMap.put(9, "其它");
				natureSubTypeMap.put(null, "非正常数据");
				for (Map.Entry<Integer,String> entrySon : natureSubTypeMap.entrySet()) {
					keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, preFiled+"nature_"+entry.getKey()+"_nature-sub_"+entrySon.getKey());
					if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
						supSchNum += Integer.parseInt(keyVal);
					}
					
					if(4==entry.getKey()) {
						//如果是4：其他，则将shanghai-nature_null_nature-sub_null数据同样归为其他
						keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, preFiled+"nature_null_nature-sub_"+entrySon.getKey());
						if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
							supSchNum += Integer.parseInt(keyVal);
						}
					}
				}
			//}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//区域名称
				busiDetDataInfo.setStatPropName(entry.getValue());
				
			}
			//学校总数
			busiDetDataInfo.setTotalSchNum(supSchNum);
			busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
	}
	
	/**
	 * 获取监管学校总数（按学校类型统计）
	 * @param distId
	 * @return
	 */
	private void getSupSchNumBySchoolType(String departmentId,String distId,String currDistName,Map<Integer, String> schoolPropertyMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		Integer supSchNum = 0;
		String key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		String keyVal = null;
		//redis的类型结构
		//shanghai-nature_0_nature-sub_null
		String preFiled="shanghai-";
		if(StringUtils.isNotEmpty(distId)) {
			//area_1_nature_0_nature-sub_null
			preFiled="area_"+distId+"_";
		}
		
		
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet()) {
			
			keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, preFiled+"level_"+entry.getKey());
			supSchNum = 0;
			if(keyVal != null && Integer.parseInt(keyVal)>0) {
				supSchNum = Integer.parseInt(keyVal);
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getValue());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				//学校性质名称
				busiDetDataInfo.setStatPropName(entry.getValue());
				//
				busiDetDataInfo.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				
			}
			//学校总数
			busiDetDataInfo.setTotalSchNum(supSchNum);
			busiDetDataInfoMap.put(entry.getValue(), busiDetDataInfo);
		}
		
		
	}
	
	/**
	 * 获取监管学校总数（按学校主管部门）
	 * @param distId
	 * @return
	 */
	private void getSupSchNumBySchoolSlave(String departmentId,String distId,String currDistName,Map<String, String> slaveMap,Map<String,BusiDetDataInfo> busiDetDataInfoMap) {
		Integer supSchNum = 0;
		String key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		String keyVal = null;
		//redis的类型结构
		//masterid_3_slave_长宁区教育局 "
		for (Map.Entry<String, String> entry : slaveMap.entrySet()) {
			
			String[] keys = entry.getKey().split("_");
			String masterid = keys[0];
			String slave = keys[1];
			
			if("3".equals(masterid) && CommonUtil.isEmpty(slave)) {
				slave = "其他";
			}
			
			keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key,"masterid_"+masterid+"_slave_"+slave);
			supSchNum = 0;
			if(keyVal != null && Integer.parseInt(keyVal)>0) {
				supSchNum = Integer.parseInt(keyVal);
			}
			
			BusiDetDataInfo busiDetDataInfo = busiDetDataInfoMap.get(entry.getKey());
			if(busiDetDataInfo==null) {
				busiDetDataInfo = new BusiDetDataInfo();
				busiDetDataInfo.setStatClassName(entry.getValue());
				//区域名称
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				busiDetDataInfo.setStatPropName(slaveName);
			}
			//学校总数
			busiDetDataInfo.setTotalSchNum(supSchNum);
			busiDetDataInfoMap.put(entry.getKey(), busiDetDataInfo);
		}
		
		
	}
	
	/**
	 * 投诉举报详情列表模型函数
	 * @param token
	 * @param distName
	 * @param prefCity
	 * @param province
	 * @param startDate
	 * @param endDate
	 * @param statMode 统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
	 * @param db1Service
	 * @param db2Service
	 * @param saasService
	 * @return
	 */
	public BusiDetDataInfoDTO appModFunc(String token, String distName, String prefCity, String province,String startDate, String endDate, Integer statMode,
			String subLevel,String compDep,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,EduSchoolService eduSchoolService,DbHiveWarnService dbHiveWarnService,
			DbHiveRecyclerWasteService dbHiveRecyclerWasteService,DbHiveDishService dbHiveDishService,DbHiveGsService dbHiveGsService) {
		BusiDetDataInfoDTO busiDetDataInfoDTO = null;
		try {
			//真实数据
			if(isRealData) {       
				// 日期
				String[] dates = null;
				
				//开始时间和结束时间有一个为空，一个不为空，则开始时间和结束时间一致
				if((startDate==null || "".equals(startDate)) && (endDate!=null && !"".equals(endDate))) {
					startDate = endDate;
				}else if((endDate==null || "".equals(endDate)) && (startDate!=null && !"".equals(startDate))) {
					endDate = startDate;
				}
				
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
				String currDistName = null;
				
				int curSubLevel = -1;
				//所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效
				if(subLevel != null)
					curSubLevel = Integer.parseInt(subLevel);
				//主管部门，按主管部门有效
				int curCompDep = -1;
				if(compDep != null)
					curCompDep = Integer.parseInt(compDep);	
					
				//获取用户数据权限信息
			  	/*UserDataPermInfoDTO udpiDto = AppModConfig.getUserDataPermInfo(token, db1Service, db2Service);
			  	if(curSubLevel == -1)
			  		curSubLevel = udpiDto.getSubLevelId();
			  	if(curCompDep == -1)
			  		curCompDep = udpiDto.getCompDepId();*/
			  	
			  	Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
			  	
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
								currDistName = curTdd.getName();
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
						// 餐厨垃圾学校回收列表函数
						busiDetDataInfoDTO = busiDetDataInfoFunc(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates,target, tedList,
								db1Service, saasService,eduSchoolService,dbHiveWarnService,dbHiveRecyclerWasteService,dbHiveDishService,
								dbHiveGsService);
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
						// 餐厨垃圾学校回收列表函数
						busiDetDataInfoDTO = busiDetDataInfoFunc(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep, dates,target,tedList,
								db1Service, saasService,eduSchoolService,dbHiveWarnService,dbHiveRecyclerWasteService,dbHiveDishService,dbHiveGsService);
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
				busiDetDataInfoDTO = new BusiDetDataInfoDTO();
			}		
		}catch(Exception e) {
			logger.info("异常："+e.getMessage());
		}
		return busiDetDataInfoDTO;
	}
}
