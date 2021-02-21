package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.lang.reflect.InvocationTargetException;
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.EduSchool;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchConMatSitStat;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchConMatSitStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchMatCommon;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 3.2.26.	学校确认用料计划情况统计应用模型
 * @author fengyang_xie
 *
 */
public class SchConMatSitStatAppMod {
	private static final Logger logger = LogManager.getLogger(SchConMatSitStatAppMod.class.getName());
	
	/**
	 * Redis服务
	 */
	@Autowired
	RedisService redisService = new RedisService();
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 1;
	/**
	 * 是否为真实数据标识
	 */
	private static boolean isRealData = true;
	
	/**
	 * 数组数据初始化
	 */
	String tempData ="{\r\n" + 
			"   \"time\": \"2016-07-14 09:51:35\",\r\n" + 
			"   \"schConMatSitStat\": [\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"  \"statPropName\":\"浦东新区\",\r\n" + 
			"   \"dishSchNum\":55,\r\n" + 
			"   \"noConMatSchNum\":5,\r\n" + 
			"\"conMatSchNum\":50,\r\n" + 
			"\"totalMatPlanNum\":30,\r\n" + 
			"\"conMatPlanNum\":25,\r\n" + 
			"\"noConMatPlanNum\":5,\r\n" + 
			"\"matConRate\":36.33\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"  \"statPropName\":\"黄浦区\",\r\n" + 
			"   \"dishSchNum\":55,\r\n" + 
			"   \"noConMatSchNum\":5,\r\n" + 
			"\"conMatSchNum\":50,\r\n" + 
			"\"totalMatPlanNum\":30,\r\n" + 
			"\"conMatPlanNum\":25,\r\n" + 
			"\"noConMatPlanNum\":5,\r\n" + 
			"\"matConRate\":36.33\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"\"statPropName\":\"静安区\",\r\n" + 
			"   \"dishSchNum\":55,\r\n" + 
			"   \"noConMatSchNum\":5,\r\n" + 
			"\"conMatSchNum\":50,\r\n" + 
			"\"totalMatPlanNum\":30,\r\n" + 
			"\"conMatPlanNum\":25,\r\n" + 
			"\"noConMatPlanNum\":5,\r\n" + 
			"\"matConRate\":36.33\r\n" + 
			" } ],\r\n" + 
			"   \"msgId\":1\r\n" + 
			"}\r\n" + 
			"";
	
	/**
	 * 汇总数据
	 * @return
	 */
	private SchConMatSitStatDTO schConMatSitStatFunc(String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService ) {
		
		SchConMatSitStatDTO schConMatSitStatDTO = new SchConMatSitStatDTO();
		
		List<SchConMatSitStat> schConMatSitStat = new ArrayList<SchConMatSitStat>();
		schConMatSitStatDTO.setSchConMatSitStat(schConMatSitStat);
		
		JSONObject jsStr = JSONObject.parseObject(tempData); 
		schConMatSitStatDTO = (SchConMatSitStatDTO) JSONObject.toJavaObject(jsStr,SchConMatSitStatDTO.class);
		
		
		
		
		//时戳
		schConMatSitStatDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		schConMatSitStatDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return schConMatSitStatDTO;
	}
	
	/**
	 * 汇总数据
	 * @return
	 */
	private SchConMatSitStatDTO schConMatSitStatFuncOne(String departmentId,String distId,String currDistName,Integer statMode,
			Integer subLevel,Integer compDep, String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService,EduSchoolService eduSchoolService,DbHiveMatService dbHiveMatService ) {
		
		SchConMatSitStatDTO schConMatSitStatDTO = new SchConMatSitStatDTO();
		
		List<SchConMatSitStat> schConMatSitStatList = new ArrayList<SchConMatSitStat>();
		
		
		Map<String,SchConMatSitStat> schConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
		
		
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		
		if(statMode == 0) {
			//========0:按区统计
			
			/**
			 * 排菜汇总(已排菜学校)
			 */
			//2019.07.19注释，注释原因，目前界面不显示排菜相关数据，且排菜改为从hive库中获取后，查询速度回慢
			/*Map<String,SchDishSitStat> schDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			//排菜信息暂时去除
			SchDishSitStatAppMod.getDishInfoByArea(distId, dates, tedList,schDishSitStatMap,redisService);
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				SchConMatSitStat schConMatSitStat = new SchConMatSitStat();
				schConMatSitStat.setStatClassName(entry.getValue().getStatClassName());
				schConMatSitStat.setStatPropName(entry.getValue().getStatPropName());
				//改为：未确认用料+已确认用料
				schConMatSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schConMatSitStatMap.put(entry.getKey(), schConMatSitStat);
			}*/
			
			/**
			 * 用料汇总
			 */
			if(days >= 2) {
				getConMatSitByAreaFromHive(departmentId,distId, dates, tedList, schConMatSitStatMap, dbHiveMatService);
			}else {
				getConMatSitByArea(departmentId,distId, dates, tedList,schConMatSitStatMap,redisService);
			}
			
			
		}else if(statMode == 1) {
			//========1:按学校性质统计
			Map<Integer, String> schoolPropertyMap = new HashMap<Integer,String>();
			schoolPropertyMap.put(0, "公办");
			schoolPropertyMap.put(2, "民办");
			schoolPropertyMap.put(3, "外籍人员子女学校");
			schoolPropertyMap.put(4, "其他");
			
			
			/**
			 * 排菜汇总(已排菜学校)
			 */
			//2019.07.19注释，注释原因，目前界面不显示排菜相关数据，且排菜改为从hive库中获取后，查询速度回慢
			/*Map<String,SchDishSitStat> schDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			SchDishSitStatAppMod.getDishInfoByNature(distId, dates, schoolPropertyMap,schDishSitStatMap,redisService);
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				SchConMatSitStat schConMatSitStat = new SchConMatSitStat();
				schConMatSitStat.setStatClassName(entry.getValue().getStatClassName());
				schConMatSitStat.setStatPropName(entry.getValue().getStatPropName());
				schConMatSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schConMatSitStatMap.put(entry.getKey(), schConMatSitStat);
			}*/
			
			/**
			 * 用料汇总
			 */
			//获取配送信息：未用料学校、未用料配送单个数、用料率
			if(days >= 2) {
				getConMatSitByNatureFromHive(departmentId,distId, dates, schoolPropertyMap, db1Service, saasService, schConMatSitStatMap, eduSchoolService, dbHiveMatService);
			}else {
				//getConMatSitByNature(distId, dates, schoolPropertyMap, db1Service,saasService, schConMatSitStatMap, eduSchoolService,redisService);
				getConMatSitByNatureTwo(departmentId, distId, dates, schoolPropertyMap, db1Service, saasService, schConMatSitStatMap, eduSchoolService, redisService);
			}
		}else if (statMode == 2) {
			//========2:按学校学制统计
			
			/**
			 * 排菜汇总(已排菜学校)
			 */
			//2019.07.19注释，注释原因，目前界面不显示排菜相关数据，且排菜改为从hive库中获取后，查询速度回慢
			/*Map<String,SchDishSitStat> schDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			SchDishSitStatAppMod.getDishInfoBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap,schDishSitStatMap,redisService);
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				SchConMatSitStat schConMatSitStat = new SchConMatSitStat();
				schConMatSitStat.setStatClassName(entry.getValue().getStatClassName());
				schConMatSitStat.setStatPropName(entry.getValue().getStatPropName());
				schConMatSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schConMatSitStatMap.put(entry.getKey(), schConMatSitStat);
			}*/
			
			/**
			 * 用料汇总
			 */
			//获取配送信息：未用料学校、未用料配送单个数、用料率
			if(days >= 2) {
				getConMatSitBySchoolTypeFromHive(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap, db1Service, saasService, schConMatSitStatMap, eduSchoolService, dbHiveMatService);
			}else {
				//getConMatSitBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap, db1Service,saasService, schConMatSitStatMap, eduSchoolService,redisService);
				getConMatSitBySchoolTypeTwo(departmentId, distId, dates, AppModConfig.schTypeIdToNameMap, db1Service, saasService, schConMatSitStatMap, eduSchoolService, redisService);
			}
			//计算每种【学制分类】的数量
			Map<String,SchConMatSitStat> newSchConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			String statClassName="";
			SchConMatSitStat schConMatSitStatSum = new SchConMatSitStat();
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				
				
				
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newSchConMatSitStatMap.put(statClassName+"_小计", schConMatSitStatSum);
					schConMatSitStatSum = new SchConMatSitStat();
				}

				//计算数据
				setSchoolPropertSumData(schConMatSitStatSum, entry);
				statClassName = entry.getValue().getStatClassName();
				schConMatSitStatSum.setStatClassName(statClassName);
				schConMatSitStatSum.setStatPropName("小计");
				setRate(schConMatSitStatSum);
				
				newSchConMatSitStatMap.put(entry.getKey(), entry.getValue());
				
			}
			newSchConMatSitStatMap.put("其他"+"_小计", schConMatSitStatSum);
			schConMatSitStatMap = newSchConMatSitStatMap;
			
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
			 * 排菜汇总(已排菜学校)
			 */
		    //2019.07.19注释，注释原因，目前界面不显示排菜相关数据，且排菜改为从hive库中获取后，查询速度回慢
			/*Map<String,SchDishSitStat> schDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			SchDishSitStatAppMod.getDishInfoBySlave(distId, dates, compDepNameToSubLevelNameMap,schDishSitStatMap,redisService);
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				SchConMatSitStat schConMatSitStat = new SchConMatSitStat();
				schConMatSitStat.setStatClassName(entry.getValue().getStatClassName());
				schConMatSitStat.setStatPropName(entry.getValue().getStatPropName());
				schConMatSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schConMatSitStatMap.put(entry.getKey(), schConMatSitStat);
			}*/
			
			/**
			 * 用料汇总
			 */
			//获取配送信息：未用料学校、未用料配送单个数、用料率
		    if(days >= 2) {
		    	getConMatSitBySlaveFromHive(departmentId,distId, dates, compDepNameToSubLevelNameMap, schConMatSitStatMap, dbHiveMatService);
		    }else {
		    	getConMatSitBySlave(departmentId,distId, dates, compDepNameToSubLevelNameMap,schConMatSitStatMap,redisService);
		    }
			
		    //计算每种【学制分类】的数量
			Map<String,SchConMatSitStat> newSchConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			String statClassName="";
			SchConMatSitStat schConMatSitStatSum = new SchConMatSitStat();
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newSchConMatSitStatMap.put(statClassName+"_小计", schConMatSitStatSum);
					schConMatSitStatSum = new SchConMatSitStat();
				}

				//计算数据
				setSchoolPropertSumData(schConMatSitStatSum, entry);
				statClassName = entry.getValue().getStatClassName();	
				schConMatSitStatSum.setStatClassName(statClassName);
				schConMatSitStatSum.setStatPropName("小计");
				//计算比率：包括用料计划确认率、用料率、用料计划确认率、预警处理率
                setRate(schConMatSitStatSum);
				newSchConMatSitStatMap.put(entry.getKey(), entry.getValue());
				
			}
			newSchConMatSitStatMap.put("其他"+"_小计", schConMatSitStatSum);
			schConMatSitStatMap = newSchConMatSitStatMap;
			
			//循环技术小计的百分比（包括用料计划确认率、用料率、用料计划确认率、预警处理率、）
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				if(entry.getKey().contains("小计")) {
					schConMatSitStatSum = entry.getValue();
				}
			}
			
		}else if(statMode == 4) {
			//========0:按区统计
			
			/**
			 * 排菜汇总(已排菜学校)
			 */
			//2019.07.19注释，注释原因，目前界面不显示排菜相关数据，且排菜改为从hive库中获取后，查询速度回慢
			/*Map<String,SchDishSitStat> schDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			//排菜信息暂时去除
			SchDishSitStatAppMod.getDishInfoByArea(distId, dates, tedList,schDishSitStatMap,redisService);
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				SchConMatSitStat schConMatSitStat = new SchConMatSitStat();
				schConMatSitStat.setStatClassName(entry.getValue().getStatClassName());
				schConMatSitStat.setStatPropName(entry.getValue().getStatPropName());
				//改为：未确认用料+已确认用料
				schConMatSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schConMatSitStatMap.put(entry.getKey(), schConMatSitStat);
			}*/
			DepartmentObj departmentObj = new DepartmentObj();
			if(CommonUtil.isNotEmpty(departmentId)) {
				departmentObj.setDepartmentId(departmentId);
			}
			List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,null, -1, -1);
			
			/**
			 * 用料汇总
			 */
			if(days >= 2) {
				getConMatSitByDepartmentFromHive(departmentId, dates, deparmentList, schConMatSitStatMap, dbHiveMatService);
			}else {
				getConMatSitByDepartment(departmentId, dates, deparmentList, schConMatSitStatMap, redisService);
			}
			
			//计算每种【管理】的数量
			Map<String,SchConMatSitStat> newSchConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			String statPropName="";
			SchConMatSitStat schConMatSitStatSum = new SchConMatSitStat();
			SchConMatSitStat preSchDishSitStatSum = new SchConMatSitStat();
			int sumIndex = 0;
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				sumIndex ++ ;
				if(sumIndex == 17 || sumIndex ==18 || sumIndex == 19) {
					newSchConMatSitStatMap.put(statPropName+"_小计", schConMatSitStatSum);
					schConMatSitStatSum = new SchConMatSitStat();
					try {
						BeanUtils.copyProperties(schConMatSitStatSum, preSchDishSitStatSum);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}

				//计算数据
				setSchoolPropertSumData(schConMatSitStatSum, entry);
				statPropName = entry.getValue().getStatPropName();
				schConMatSitStatSum.setStatPropName("小计");
				setRate(schConMatSitStatSum);
				
				newSchConMatSitStatMap.put(entry.getKey(), entry.getValue());
				
				if(sumIndex == 16 || sumIndex ==17 || sumIndex == 18) {
					try {
						preSchDishSitStatSum = new SchConMatSitStat();
						BeanUtils.copyProperties(preSchDishSitStatSum, schConMatSitStatSum);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			newSchConMatSitStatMap.put("小计", schConMatSitStatSum);
			schConMatSitStatMap = newSchConMatSitStatMap;
		}
		
		
		
		schConMatSitStatMap.values();
		Collection<SchConMatSitStat> valueCollection = schConMatSitStatMap.values();
	    schConMatSitStatList = new ArrayList<SchConMatSitStat>(valueCollection);
		
		
		schConMatSitStatDTO.setSchConMatSitStat(schConMatSitStatList);
		//时戳
		schConMatSitStatDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		schConMatSitStatDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return schConMatSitStatDTO;
	}
	
	private void setRate(SchConMatSitStat schConMatSitStatSum) {
		//用料计划确认率
		float matConRate = 0;
		if(schConMatSitStatSum.getTotalMatPlanNum() > 0) {
			matConRate = 100 * ((float) schConMatSitStatSum.getConMatPlanNum() / (float) schConMatSitStatSum.getTotalMatPlanNum());
			BigDecimal bd = new BigDecimal(matConRate);
			matConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (matConRate > 100) {
				matConRate = 100;
			}
		}
		schConMatSitStatSum.setMatConRate(matConRate);
	}

	
	/**
	 * 学习学制每个分类小计
	 * @param schConMatSitStatSum
	 * @param entry
	 */
	private void setSchoolPropertSumData(SchConMatSitStat schConMatSitStatSum, Map.Entry<String, SchConMatSitStat> entry) {
		
		
		/**
		 * 已排菜学校
		 */
		schConMatSitStatSum.setDishSchNum(schConMatSitStatSum.getDishSchNum()+entry.getValue().getDishSchNum());
		
		/**
		 * 未确认用料学校
		 */
		schConMatSitStatSum.setNoConMatSchNum(schConMatSitStatSum.getNoConMatSchNum()+entry.getValue().getNoConMatSchNum());
		
		
		/**
		 * 已确认用料学校
		 */
		schConMatSitStatSum.setConMatSchNum(schConMatSitStatSum.getConMatSchNum()+entry.getValue().getConMatSchNum());
		
		//应确认用料学校
		schConMatSitStatSum.setShouldAccSchNum(schConMatSitStatSum.getNoConMatSchNum() + schConMatSitStatSum.getConMatSchNum());
		
		/**
		 * 用料计划总数
		 */
		schConMatSitStatSum.setTotalMatPlanNum(schConMatSitStatSum.getTotalMatPlanNum()+entry.getValue().getTotalMatPlanNum());
		

		/**
		 * 已确认用料计划数
		 */
		schConMatSitStatSum.setConMatPlanNum(schConMatSitStatSum.getConMatPlanNum()+entry.getValue().getConMatPlanNum());
		

		/**
		 * 未确认用料计划数
		 */
		schConMatSitStatSum.setNoConMatPlanNum(schConMatSitStatSum.getNoConMatPlanNum()+entry.getValue().getNoConMatPlanNum());
		
		/**
		 * 确认率，保留小数点有效数字两位
		 */
		schConMatSitStatSum.setMatConRate(schConMatSitStatSum.getMatConRate()+entry.getValue().getMatConRate());
		
	}
	
	
	/**
	 * 获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按区域)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getConMatSitByArea(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,SchConMatSitStat> schConMatSitStatMap,RedisService redisService) {
		String key = "";
		String keyVal = "";
		String fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> platoonFeedTotalMap = null;
		int distCount = tedList.size();
		int dateCount = dates.length;
		//用料计划总数
		int[][]totalMatPlanNums = new int[dateCount][distCount];
		//已确认用料计划数
		int [][]conMatPlanNums = new int[dateCount][distCount];
		//未确认用料计划数
		int [][]noConMatPlanNums = new int[dateCount][distCount];
		//未确认用料计划学校数
		int [][]conMatSchNums = new int[dateCount][distCount];
		//未确认用料计划学校数
		int [][]noConMatSchNums = new int[dateCount][distCount];
		float[] matConRates = new float[distCount];
		
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量   date_useMaterialPlanTotal
			key = dates[k] + DataKeyConfig.useMaterialPlanTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentUseMaterialPlanTotal+departmentId;
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
						//用料计划
						fieldPrefix = "area_"+curDistId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 4)
							{
								if(curKeys[3].equalsIgnoreCase("2") ) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										totalMatPlanNums[k][i] +=Integer.parseInt(keyVal);
										conMatPlanNums[k][i] +=Integer.parseInt(keyVal);
									}
								}else {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										totalMatPlanNums[k][i] +=Integer.parseInt(keyVal);
										noConMatPlanNums[k][i] +=Integer.parseInt(keyVal);
									}
								}
							}
						}
						
						//用料计划学校：已确认用料计划学校、未确认用料计划学校数
						fieldPrefix = "school-area_"+curDistId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 4)
							{
								if(curKeys[3].equalsIgnoreCase("2") ) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										conMatSchNums[k][i] +=Integer.parseInt(keyVal);
									}
								}else {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										noConMatSchNums[k][i] +=Integer.parseInt(keyVal);
									}
								}
							}
						}
						
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
			
			SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(curTdd.getName());
			if(schConMatSitStat==null) {
				schConMatSitStat = new SchConMatSitStat();
				//区域名称
				schConMatSitStat.setStatPropName(curTdd.getName());
			}
			
			int noConMatSchNum = 0;
			int conMatSchNum = 0;
			int totalMatPlanNum = 0;
			int conMatPlanNum = 0;
			int noConMatPlanNum = 0;
			for (int k = 0; k < dates.length; k++) {
				noConMatSchNum += noConMatSchNums[k][i];
				conMatSchNum += conMatSchNums[k][i];
				totalMatPlanNum += totalMatPlanNums[k][i];
				conMatPlanNum += conMatPlanNums[k][i];
				noConMatPlanNum += noConMatPlanNums[k][i];
			}
			matConRates[i] = 0;
			if(totalMatPlanNum > 0) {
				matConRates[i] = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(matConRates[i]);
				matConRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (matConRates[i] > 100) {
					matConRates[i] = 100;
				}
			}
			
			//未确认用料学校
			schConMatSitStat.setShouldAccSchNum(noConMatSchNum + conMatSchNum);
			//未确认用料学校
			schConMatSitStat.setNoConMatSchNum(noConMatSchNum);
			//已确认用料学校
			schConMatSitStat.setConMatSchNum(conMatSchNum);
			//用料计划总数
			schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
			// 已确认用料计划数
			schConMatSitStat.setConMatPlanNum(conMatPlanNum);
			//未确认用料计划数
			schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
			//确认率，保留小数点有效数字两位
			schConMatSitStat.setMatConRate(matConRates[i]);
			
			schConMatSitStatMap.put(curTdd.getName(), schConMatSitStat);
		}
	}
	
	/**
	 * 获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按区域)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getConMatSitByAreaFromHive(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,SchConMatSitStat> schConMatSitStatMap,DbHiveMatService dbHiveMatService) {
		// 当天排菜学校总数
		int distCount = tedList.size();
		float[] matConRates = new float[distCount];
		
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
		//用料计划总数
		Map<String,Integer> totalMatPlanNumMap = new HashMap<>();
		//已确认用料计划数
		Map<String,Integer> conMatPlanNumMap = new HashMap<>();
		//未确认用料计划数
		Map<String,Integer> noConMatPlanNumMap = new HashMap<>();
		//未确认用料计划学校数
		Map<String,Integer> conMatSchNumMap = new HashMap<>();
		//未确认用料计划学校数
		Map<String,Integer> noConMatSchNumMap = new HashMap<>();
		
		
		List<SchMatCommon> dishList = new ArrayList<>();
		dishList = dbHiveMatService.getMatList(DataKeyConfig.talbeMaterialTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,departmentId,null, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchMatCommon schDishCommon: dishList) {
				
				//区域为空，代表全市数据，此处去除
				if(CommonUtil.isEmpty(schDishCommon.getDistId())) {
					continue;
				}
				
				if(schDishCommon.getStatus() ==null) {
					totalMatPlanNumMap.put(schDishCommon.getDistId(), 
							(totalMatPlanNumMap.get(schDishCommon.getDistId())==null?0:totalMatPlanNumMap.get(schDishCommon.getDistId())) 
							+ schDishCommon.getTotal());
				}else if (schDishCommon.getStatus() == 0 || schDishCommon.getStatus() == 1) {
					//信息不完整和待确认都属于未确认状态
					
					noConMatSchNumMap.put(schDishCommon.getDistId(), 
							(noConMatSchNumMap.get(schDishCommon.getDistId())==null?0:noConMatSchNumMap.get(schDishCommon.getDistId())) 
							+ schDishCommon.getSchoolTotal());
					
					noConMatPlanNumMap.put(schDishCommon.getDistId(), 
							(noConMatPlanNumMap.get(schDishCommon.getDistId())==null?0:noConMatPlanNumMap.get(schDishCommon.getDistId())) 
							+ schDishCommon.getTotal());
					
				}else if (schDishCommon.getStatus() == 2) {
					
					conMatSchNumMap.put(schDishCommon.getDistId(), 
							(conMatSchNumMap.get(schDishCommon.getDistId())==null?0:conMatSchNumMap.get(schDishCommon.getDistId())) 
							+ schDishCommon.getSchoolTotal());
					
					conMatPlanNumMap.put(schDishCommon.getDistId(), 
							(conMatPlanNumMap.get(schDishCommon.getDistId())==null?0:conMatPlanNumMap.get(schDishCommon.getDistId())) 
							+ schDishCommon.getTotal());
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
			
			SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(curTdd.getName());
			if(schConMatSitStat==null) {
				schConMatSitStat = new SchConMatSitStat();
				//区域名称
				schConMatSitStat.setStatPropName(curTdd.getName());
			}
			
			int noConMatSchNum = noConMatSchNumMap.get(String.valueOf(curDistId))==null?0:noConMatSchNumMap.get(String.valueOf(curDistId));
			int conMatSchNum = conMatSchNumMap.get(String.valueOf(curDistId))==null?0:conMatSchNumMap.get(String.valueOf(curDistId));
			int totalMatPlanNum = totalMatPlanNumMap.get(String.valueOf(curDistId))==null?0:totalMatPlanNumMap.get(String.valueOf(curDistId));
			int conMatPlanNum = conMatPlanNumMap.get(String.valueOf(curDistId))==null?0:conMatPlanNumMap.get(String.valueOf(curDistId));
			int noConMatPlanNum = noConMatPlanNumMap.get(String.valueOf(curDistId))==null?0:noConMatPlanNumMap.get(String.valueOf(curDistId));
			
			matConRates[i] = 0;
			if(totalMatPlanNum > 0) {
				matConRates[i] = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(matConRates[i]);
				matConRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (matConRates[i] > 100) {
					matConRates[i] = 100;
				}
			}
			
			//未确认用料学校
			schConMatSitStat.setShouldAccSchNum(noConMatSchNum + conMatSchNum);
			//未确认用料学校
			schConMatSitStat.setNoConMatSchNum(noConMatSchNum);
			//已确认用料学校
			schConMatSitStat.setConMatSchNum(conMatSchNum);
			//用料计划总数
			schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
			// 已确认用料计划数
			schConMatSitStat.setConMatPlanNum(conMatPlanNum);
			//未确认用料计划数
			schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
			//确认率，保留小数点有效数字两位
			schConMatSitStat.setMatConRate(matConRates[i]);
			
			schConMatSitStatMap.put(curTdd.getName(), schConMatSitStat);
		}
	}

	
	/**
	 * 获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按区域)
	 * @param departmentId
	 * @param dates
	 * @param deparmentList
	 * @return
	 */
	public static void getConMatSitByDepartmentFromHive(String departmentId,String[] dates, List<DepartmentObj> deparmentList,
			Map<String,SchConMatSitStat> schConMatSitStatMap,DbHiveMatService dbHiveMatService) {
		// 当天排菜学校总数
		int distCount = deparmentList.size();
		float[] matConRates = new float[distCount];
		
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
		//用料计划总数
		Map<String,Integer> totalMatPlanNumMap = new HashMap<>();
		//已确认用料计划数
		Map<String,Integer> conMatPlanNumMap = new HashMap<>();
		//未确认用料计划数
		Map<String,Integer> noConMatPlanNumMap = new HashMap<>();
		//未确认用料计划学校数
		Map<String,Integer> conMatSchNumMap = new HashMap<>();
		//未确认用料计划学校数
		Map<String,Integer> noConMatSchNumMap = new HashMap<>();
		
		
		List<SchMatCommon> dishList = new ArrayList<>();
		dishList = dbHiveMatService.getMatList(DataKeyConfig.talbeMaterialTotalD,listYearMonth, startDate, endDateAddOne, null, null, 
				-1, -1, null, null,departmentId,null, 0);
		if(dishList !=null && dishList.size() > 0) {
			for(SchMatCommon schDishCommon: dishList) {
				
				//区域为空，代表全市数据，此处去除
				if(CommonUtil.isEmpty(schDishCommon.getDistId())) {
					continue;
				}
				
				if(schDishCommon.getStatus() ==null) {
					totalMatPlanNumMap.put(schDishCommon.getDistId(), 
							(totalMatPlanNumMap.get(schDishCommon.getDistId())==null?0:totalMatPlanNumMap.get(schDishCommon.getDistId())) 
							+ schDishCommon.getTotal());
				}else if (schDishCommon.getStatus() == 0 || schDishCommon.getStatus() == 1) {
					//信息不完整和待确认都属于未确认状态
					
					noConMatSchNumMap.put(schDishCommon.getDistId(), 
							(noConMatSchNumMap.get(schDishCommon.getDistId())==null?0:noConMatSchNumMap.get(schDishCommon.getDistId())) 
							+ schDishCommon.getSchoolTotal());
					
					noConMatPlanNumMap.put(schDishCommon.getDistId(), 
							(noConMatPlanNumMap.get(schDishCommon.getDistId())==null?0:noConMatPlanNumMap.get(schDishCommon.getDistId())) 
							+ schDishCommon.getTotal());
					
				}else if (schDishCommon.getStatus() == 2) {
					
					conMatSchNumMap.put(schDishCommon.getDistId(), 
							(conMatSchNumMap.get(schDishCommon.getDistId())==null?0:conMatSchNumMap.get(schDishCommon.getDistId())) 
							+ schDishCommon.getSchoolTotal());
					
					conMatPlanNumMap.put(schDishCommon.getDistId(), 
							(conMatPlanNumMap.get(schDishCommon.getDistId())==null?0:conMatPlanNumMap.get(schDishCommon.getDistId())) 
							+ schDishCommon.getTotal());
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
			
			SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(departmentObj.getDepartmentName());
			if(schConMatSitStat==null) {
				schConMatSitStat = new SchConMatSitStat();
				//区域名称
				schConMatSitStat.setStatPropName(departmentObj.getDepartmentName());
			}
			
			int noConMatSchNum = noConMatSchNumMap.get(String.valueOf(curDepartmentId))==null?0:noConMatSchNumMap.get(String.valueOf(curDepartmentId));
			int conMatSchNum = conMatSchNumMap.get(String.valueOf(curDepartmentId))==null?0:conMatSchNumMap.get(String.valueOf(curDepartmentId));
			int totalMatPlanNum = totalMatPlanNumMap.get(String.valueOf(curDepartmentId))==null?0:totalMatPlanNumMap.get(String.valueOf(curDepartmentId));
			int conMatPlanNum = conMatPlanNumMap.get(String.valueOf(curDepartmentId))==null?0:conMatPlanNumMap.get(String.valueOf(curDepartmentId));
			int noConMatPlanNum = noConMatPlanNumMap.get(String.valueOf(curDepartmentId))==null?0:noConMatPlanNumMap.get(String.valueOf(curDepartmentId));
			
			matConRates[i] = 0;
			if(totalMatPlanNum > 0) {
				matConRates[i] = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(matConRates[i]);
				matConRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (matConRates[i] > 100) {
					matConRates[i] = 100;
				}
			}
			
			//未确认用料学校
			schConMatSitStat.setShouldAccSchNum(noConMatSchNum + conMatSchNum);
			//未确认用料学校
			schConMatSitStat.setNoConMatSchNum(noConMatSchNum);
			//已确认用料学校
			schConMatSitStat.setConMatSchNum(conMatSchNum);
			//用料计划总数
			schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
			// 已确认用料计划数
			schConMatSitStat.setConMatPlanNum(conMatPlanNum);
			//未确认用料计划数
			schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
			//确认率，保留小数点有效数字两位
			schConMatSitStat.setMatConRate(matConRates[i]);
			
			schConMatSitStatMap.put(departmentObj.getDepartmentName(), schConMatSitStat);
		}
	}
	
	/**
	 * 获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按区域)
	 * @param
	 * @param dates
	 * @param
	 * @return
	 */
	public static void getConMatSitByDepartment(String departmentId,String[] dates, List<DepartmentObj> deparmentList,
			Map<String,SchConMatSitStat> schConMatSitStatMap,RedisService redisService) {
		String key = "";
		String keyVal = "";
		String fieldPrefix = "";
		// 当天排菜学校总数
		Map<String, String> platoonFeedTotalMap = null;
		int distCount = deparmentList.size();
		int dateCount = dates.length;
		//用料计划总数
		int[][]totalMatPlanNums = new int[dateCount][distCount];
		//已确认用料计划数
		int [][]conMatPlanNums = new int[dateCount][distCount];
		//未确认用料计划数
		int [][]noConMatPlanNums = new int[dateCount][distCount];
		//未确认用料计划学校数
		int [][]conMatSchNums = new int[dateCount][distCount];
		//未确认用料计划学校数
		int [][]noConMatSchNums = new int[dateCount][distCount];
		float[] matConRates = new float[distCount];
		
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k] + DataKeyConfig.useMaterialPlanTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentUseMaterialPlanTotal+departmentId;
			}
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(platoonFeedTotalMap == null) {   
				
			}else{
				for(String curKey : platoonFeedTotalMap.keySet()) {
					for (int i = 0; i < deparmentList.size(); i++) {
						DepartmentObj departmentObj = deparmentList.get(i);
						String curDepartmentId = departmentObj.getDepartmentId();
						//判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
						if(departmentId != null && curDepartmentId.compareTo(departmentId) != 0) {
							continue ;
						}
						//用料计划
						fieldPrefix = "department_"+curDepartmentId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 4)
							{
								if(curKeys[3].equalsIgnoreCase("2") ) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										totalMatPlanNums[k][i] +=Integer.parseInt(keyVal);
										conMatPlanNums[k][i] +=Integer.parseInt(keyVal);
									}
								}else {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										totalMatPlanNums[k][i] +=Integer.parseInt(keyVal);
										noConMatPlanNums[k][i] +=Integer.parseInt(keyVal);
									}
								}
							}
						}
						
						//用料计划学校：已确认用料计划学校、未确认用料计划学校数
						fieldPrefix = "school-department_"+curDepartmentId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							if(curKeys.length >= 4)
							{
								if(curKeys[3].equalsIgnoreCase("2") ) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										conMatSchNums[k][i] +=Integer.parseInt(keyVal);
									}
								}else {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										noConMatSchNums[k][i] +=Integer.parseInt(keyVal);
									}
								}
							}
						}
						
					}
				}
			}
		}
		
		for (int i = 0; i < distCount; i++) {
			DepartmentObj departmentObj = deparmentList.get(i);
			String curDistId = departmentObj.getDepartmentId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (departmentId != null) {
				if (!curDistId.equals(departmentId)) {
					continue;
				}
			}
			
			SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(departmentObj.getDepartmentName());
			if(schConMatSitStat==null) {
				schConMatSitStat = new SchConMatSitStat();
				//区域名称
				schConMatSitStat.setStatPropName(departmentObj.getDepartmentName());
			}
			
			int noConMatSchNum = 0;
			int conMatSchNum = 0;
			int totalMatPlanNum = 0;
			int conMatPlanNum = 0;
			int noConMatPlanNum = 0;
			for (int k = 0; k < dates.length; k++) {
				noConMatSchNum += noConMatSchNums[k][i];
				conMatSchNum += conMatSchNums[k][i];
				totalMatPlanNum += totalMatPlanNums[k][i];
				conMatPlanNum += conMatPlanNums[k][i];
				noConMatPlanNum += noConMatPlanNums[k][i];
			}
			matConRates[i] = 0;
			if(totalMatPlanNum > 0) {
				matConRates[i] = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(matConRates[i]);
				matConRates[i] = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (matConRates[i] > 100) {
					matConRates[i] = 100;
				}
			}
			
			//未确认用料学校
			schConMatSitStat.setShouldAccSchNum(noConMatSchNum + conMatSchNum);
			//未确认用料学校
			schConMatSitStat.setNoConMatSchNum(noConMatSchNum);
			//已确认用料学校
			schConMatSitStat.setConMatSchNum(conMatSchNum);
			//用料计划总数
			schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
			// 已确认用料计划数
			schConMatSitStat.setConMatPlanNum(conMatPlanNum);
			//未确认用料计划数
			schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
			//确认率，保留小数点有效数字两位
			schConMatSitStat.setMatConRate(matConRates[i]);
			
			schConMatSitStatMap.put(departmentObj.getDepartmentName(), schConMatSitStat);
		}
	}
	
	/**
	 *获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按学校性质)
	 * @param distId
	 * @param dates
	 * @param
	 * @return
	 */
	public static void getConMatSitByNature(String distId, String[] dates,Map<Integer, String> schoolPropertyMap, Db1Service db1Service,SaasService saasService,
			Map<String,SchConMatSitStat> schConMatSitStatMap,EduSchoolService eduSchoolService,RedisService redisService) {
		Set<String> schoolSet = new HashSet<String>();
		Map<String, String> gcRetentiondishMap = new HashMap<String, String>();
		
		//key：学校编号+餐别+菜单+菜品+日期+状态,value :对应个数
		Map<String,Integer> schoolDetailMap = new HashMap<String,Integer>();
		
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distId,1,1, 1);
		for(int i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		
		List<TEduSchoolSupplierDo> tEduSchoolSupplierDoList = saasService.getAllIdSupplierIdSchoolId(null);
    	//Key:SupplierId value:shcooolId
    	//Map<String,TEduSchoolSupplierDo> schoolSupplierMap = tEduSchoolSupplierDoList.stream().collect(Collectors.toMap(TEduSchoolSupplierDo::getId,Function.identity(),(oldData,newData)->newData));
    	Map<String,TEduSchoolSupplierDo> schoolSupplierMap = tEduSchoolSupplierDoList.stream().collect(Collectors.toMap(TEduSchoolSupplierDo::getId,Function.identity(),(oldData,newData)->newData));
    	
		// 时间段内各区菜品用料计划确认详情
		Integer schoolDetailTotal = 0;
		String schoolId = "";
		for(int k = 0; k < dateCount; k++) {
			key = dates[k] + "_useMaterialPlan-Detail";
			gcRetentiondishMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (gcRetentiondishMap != null) {
				for (String curKey : gcRetentiondishMap.keySet()) {
					keyVal = gcRetentiondishMap.get(curKey);
					// 菜品用料计划确认列表
					String[] keyVals = curKey.split("_");
					if(keyVals.length >= 10) {
						if(distId != null) {
							if(keyVals[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						
						if(schoolSupplierMap.get(keyVals[7])==null) {
							continue;
						}
						schoolId = schoolSupplierMap.get(keyVals[7]).getSchoolId();
						
						String schoolDetailMapKey = schoolId+"_"+dates[k]+"_"+gcRetentiondishMap.get(curKey);
						//学校信息（项目点）
						schoolDetailTotal=schoolDetailMap.get(schoolDetailMapKey)==null?0:schoolDetailMap.get(schoolDetailMapKey);
						schoolDetailMap.put(schoolDetailMapKey, schoolDetailTotal+1);
					}
					else {
						logger.info("菜品用料计划确认："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
		//用料计划总数
		Map<String,Integer> totalMatPlanNumMap = new HashMap<String,Integer>();
		//已确认用料计划数
		Map<String,Integer> conMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划数
		Map<String,Integer> noConMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划学校数
		Map<String,Set<String>> conMatSchNumMap = new HashMap<String,Set<String>>();
		//未确认用料计划学校数
		Map<String,Set<String>> noConMatSchNumMap = new HashMap<String,Set<String>>();
		
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		int count = 0;
		for(Map.Entry<String, Integer> entry : schoolDetailMap.entrySet() ) {
			String keys[]=entry.getKey().split("_");
			EduSchool eduSchool =  schoolMap.get(keys[0]);
			if(eduSchool!=null && StringUtils.isNotEmpty(eduSchool.getSchoolNature())) {
				
				//已验收数量
				if(totalMatPlanNumMap.get(eduSchool.getSchoolNature())==null) {
					count = entry.getValue();
				}else {
					count = totalMatPlanNumMap.get(eduSchool.getSchoolNature())+entry.getValue();
				}
				
				totalMatPlanNumMap.put(eduSchool.getSchoolNature(), count);
				
				
				if(keys[2] !=null && keys[2].equals("2")) {
					//已验收数量
					if(conMatPlanNumMap.get(eduSchool.getSchoolNature())==null) {
						count = entry.getValue();
					}else {
						count = conMatPlanNumMap.get(eduSchool.getSchoolNature())+entry.getValue();
					}
					
					conMatPlanNumMap.put(eduSchool.getSchoolNature(), count);
					
					//已用料计划确认学校数
					schoolSet=conMatSchNumMap.get(eduSchool.getSchoolNature());
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					conMatSchNumMap.put(eduSchool.getSchoolNature(), schoolSet);
				}else{
					//未验收数量
					if(noConMatPlanNumMap.get(eduSchool.getSchoolNature())==null) {
						count = entry.getValue();
					}else {
						count = noConMatPlanNumMap.get(eduSchool.getSchoolNature())+entry.getValue();
					}
					
					noConMatPlanNumMap.put(eduSchool.getSchoolNature(), count);
					
					//未验收学校数
					schoolSet=noConMatSchNumMap.get(eduSchool.getSchoolNature());
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					noConMatSchNumMap.put(eduSchool.getSchoolNature(), schoolSet);
				}
				
			}
			
			
		}
		
		//计算用料计划确认率和数据封装
		float matConRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(entry.getValue());
				if(schConMatSitStat==null) {
					schConMatSitStat = new SchConMatSitStat();
					//学校性质名称
					schConMatSitStat.setStatPropName(entry.getValue());
				}
				//未确认用料学校
				schConMatSitStat.setNoConMatSchNum(noConMatSchNumMap.get(entry.getKey().toString())==null?0:noConMatSchNumMap.get(entry.getKey().toString()).size());
				//已确认用料学校
				schConMatSitStat.setConMatSchNum(conMatSchNumMap.get(entry.getKey().toString())==null?0:conMatSchNumMap.get(entry.getKey().toString()).size());
				
				//应确认用料学校
				schConMatSitStat.setShouldAccSchNum(schConMatSitStat.getNoConMatSchNum() + schConMatSitStat.getConMatSchNum());
				
				int totalMatPlanNum = totalMatPlanNumMap.get(entry.getKey().toString())==null?0:totalMatPlanNumMap.get(entry.getKey().toString());
				int conMatPlanNum = conMatPlanNumMap.get(entry.getKey().toString())==null?0:conMatPlanNumMap.get(entry.getKey().toString());
				int noConMatPlanNum = noConMatPlanNumMap.get(entry.getKey().toString())==null?0:noConMatPlanNumMap.get(entry.getKey().toString());
				
				matConRate = 0;
				if(totalMatPlanNum > 0) {
					matConRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
					BigDecimal bd = new BigDecimal(matConRate);
					matConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (matConRate > 100) {
						matConRate = 100;
					}
				}
				//用料计划总数
				schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
				// 已确认用料计划数
				schConMatSitStat.setConMatPlanNum(conMatPlanNum);
				//未确认用料计划数
				schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
				//确认率，保留小数点有效数字两位
				schConMatSitStat.setMatConRate(matConRate);
				schConMatSitStatMap.put(entry.getValue(), schConMatSitStat);
		}
		
	}
	
	/**
	 *获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按学校性质)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getConMatSitByNatureFromHive(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap, Db1Service db1Service,SaasService saasService,
			Map<String,SchConMatSitStat> schConMatSitStatMap,EduSchoolService eduSchoolService,DbHiveMatService dbHiveMatService) {
		
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distId,1,1, 1);
		for(int i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		
		
		//用料计划总数
		Map<String,Integer> totalMatPlanNumMap = new HashMap<String,Integer>();
		//已确认用料计划数
		Map<String,Integer> conMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划数
		Map<String,Integer> noConMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划学校数
		Map<String,Integer> conMatSchNumMap = new HashMap<String,Integer>();
		//未确认用料计划学校数
		Map<String,Integer> noConMatSchNumMap = new HashMap<String,Integer>();
		
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
		
		List<SchMatCommon> dishList = new ArrayList<>();
		dishList = dbHiveMatService.getMatList(DataKeyConfig.talbeMaterialTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,departmentId,null,1);
		if(dishList !=null && dishList.size() > 0) {
			for(SchMatCommon schDishCommon: dishList) {
				
				if(schDishCommon.getStatus() ==null) {
					totalMatPlanNumMap.put(schDishCommon.getSchoolNatureName(), 
							(totalMatPlanNumMap.get(schDishCommon.getSchoolNatureName())==null?0:totalMatPlanNumMap.get(schDishCommon.getSchoolNatureName())) 
							+ schDishCommon.getTotal());
				}else if (schDishCommon.getStatus() == 0 || schDishCommon.getStatus() == 1) {
					//信息不完整和待确认都属于未确认状态
					
					noConMatSchNumMap.put(schDishCommon.getSchoolNatureName(), 
							(noConMatSchNumMap.get(schDishCommon.getSchoolNatureName())==null?0:noConMatSchNumMap.get(schDishCommon.getSchoolNatureName())) 
							+ schDishCommon.getSchoolTotal());
					
					noConMatPlanNumMap.put(schDishCommon.getSchoolNatureName(), 
							(noConMatPlanNumMap.get(schDishCommon.getSchoolNatureName())==null?0:noConMatPlanNumMap.get(schDishCommon.getSchoolNatureName())) 
							+ schDishCommon.getTotal());
					
				}else if (schDishCommon.getStatus() == 2) {
					
					conMatSchNumMap.put(schDishCommon.getSchoolNatureName(), 
							(conMatSchNumMap.get(schDishCommon.getSchoolNatureName())==null?0:conMatSchNumMap.get(schDishCommon.getSchoolNatureName())) 
							+ schDishCommon.getSchoolTotal());
					
					conMatPlanNumMap.put(schDishCommon.getSchoolNatureName(), 
							(conMatPlanNumMap.get(schDishCommon.getSchoolNatureName())==null?0:conMatPlanNumMap.get(schDishCommon.getSchoolNatureName())) 
							+ schDishCommon.getTotal());
				}
			}
		}
		
		//计算用料计划确认率和数据封装
		float matConRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(entry.getValue());
				if(schConMatSitStat==null) {
					schConMatSitStat = new SchConMatSitStat();
					//学校性质名称
					schConMatSitStat.setStatPropName(entry.getValue());
				}
				//未确认用料学校
				schConMatSitStat.setNoConMatSchNum(noConMatSchNumMap.get(entry.getKey().toString())==null?0:noConMatSchNumMap.get(entry.getKey().toString()));
				//已确认用料学校
				schConMatSitStat.setConMatSchNum(conMatSchNumMap.get(entry.getKey().toString())==null?0:conMatSchNumMap.get(entry.getKey().toString()));
				
				//应确认用料学校
				schConMatSitStat.setShouldAccSchNum(schConMatSitStat.getNoConMatSchNum() + schConMatSitStat.getConMatSchNum());
				
				int totalMatPlanNum = totalMatPlanNumMap.get(entry.getKey().toString())==null?0:totalMatPlanNumMap.get(entry.getKey().toString());
				int conMatPlanNum = conMatPlanNumMap.get(entry.getKey().toString())==null?0:conMatPlanNumMap.get(entry.getKey().toString());
				int noConMatPlanNum = noConMatPlanNumMap.get(entry.getKey().toString())==null?0:noConMatPlanNumMap.get(entry.getKey().toString());
				
				matConRate = 0;
				if(totalMatPlanNum > 0) {
					matConRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
					BigDecimal bd = new BigDecimal(matConRate);
					matConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (matConRate > 100) {
						matConRate = 100;
					}
				}
				//用料计划总数
				schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
				// 已确认用料计划数
				schConMatSitStat.setConMatPlanNum(conMatPlanNum);
				//未确认用料计划数
				schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
				//确认率，保留小数点有效数字两位
				schConMatSitStat.setMatConRate(matConRate);
				schConMatSitStatMap.put(entry.getValue(), schConMatSitStat);
		}
		
	}
	
	/**
	 *获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按学校性质)(修改新的建模数据)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getConMatSitByNatureTwo(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap, Db1Service db1Service,SaasService saasService, Map<String,SchConMatSitStat> schConMatSitStatMap,EduSchoolService eduSchoolService,RedisService redisService) {
		Integer schoolNum = 0;//未用料计划确认学校个数

		Map<String, String> distributionTotalMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		
		//菜品数量
		Map<String,Integer> totalMatPlanNumMap = new HashMap<String,Integer>();
		//已用料计划确认数量
		Map<String,Integer> conMatPlanNumMap = new HashMap<String,Integer>();
		//未用料计划确认数量
		Map<String,Integer> noConMatPlanNumMap = new HashMap<String,Integer>();
		//未用料计划确认学校个数
		Map<String,Integer> noConMatSchNumMap = new HashMap<String,Integer>();
		//已用料计划确认学校个数
		Map<String,Integer> conMatSchNumMap = new HashMap<String,Integer>();
		Integer count =0;
		Integer valueCount =0;
		
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + DataKeyConfig.useMaterialPlanTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentUseMaterialPlanTotal+departmentId;
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
						if(totalMatPlanNumMap.get(curKeys[3])!=null) {
							count = totalMatPlanNumMap.get(curKeys[3])+valueCount;
						}
						
						totalMatPlanNumMap.put(curKeys[3], count);
						
						
						count = valueCount;
						if(curKeys[7].equalsIgnoreCase("2")) {
							//已用料计划确认数量
							if(conMatPlanNumMap.get(curKeys[3])!=null) {
								count = conMatPlanNumMap.get(curKeys[3])+valueCount;
							}
							
							conMatPlanNumMap.put(curKeys[3], count);
						}else{ 
							//未用料计划确认数量
							if(noConMatPlanNumMap.get(curKeys[3])!=null) {
								count = noConMatPlanNumMap.get(curKeys[3])+valueCount;
							}
							
							noConMatPlanNumMap.put(curKeys[3], count);
						}
					}
					
					//未用料计划确认学校数
					if(curKey.indexOf("school-nat-area_")==0 && curKeys.length >= 7) {
						//过滤区域
						if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						if(curKeys[7].equalsIgnoreCase("2")) { 
							//未用料计划确认学校数
							schoolNum=conMatSchNumMap.get(curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							conMatSchNumMap.put(curKeys[3], schoolNum);
							
						}else {
							//未用料计划确认学校数
							schoolNum=noConMatSchNumMap.get(curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							noConMatSchNumMap.put(curKeys[3], schoolNum);
						}
					}
				}
			}
		}
		
		//计算用料计划确认率和数据封装
		float matConRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(entry.getValue());
			if(schConMatSitStat==null) {
				schConMatSitStat = new SchConMatSitStat();
				//学校性质名称
				schConMatSitStat.setStatPropName(entry.getValue());
			}
			//未确认用料学校
			schConMatSitStat.setNoConMatSchNum(noConMatSchNumMap.get(entry.getKey().toString())==null?0:noConMatSchNumMap.get(entry.getKey().toString()));
			//已确认用料学校
			schConMatSitStat.setConMatSchNum(conMatSchNumMap.get(entry.getKey().toString())==null?0:conMatSchNumMap.get(entry.getKey().toString()));
			
			//应确认用料学校
			schConMatSitStat.setShouldAccSchNum(schConMatSitStat.getNoConMatSchNum() + schConMatSitStat.getConMatSchNum());
			
			int totalMatPlanNum = totalMatPlanNumMap.get(entry.getKey().toString())==null?0:totalMatPlanNumMap.get(entry.getKey().toString());
			int conMatPlanNum = conMatPlanNumMap.get(entry.getKey().toString())==null?0:conMatPlanNumMap.get(entry.getKey().toString());
			int noConMatPlanNum = noConMatPlanNumMap.get(entry.getKey().toString())==null?0:noConMatPlanNumMap.get(entry.getKey().toString());
			
			matConRate = 0;
			if(totalMatPlanNum > 0) {
				matConRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(matConRate);
				matConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (matConRate > 100) {
					matConRate = 100;
				}
			}
			//用料计划总数
			schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
			// 已确认用料计划数
			schConMatSitStat.setConMatPlanNum(conMatPlanNum);
			//未确认用料计划数
			schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
			//确认率，保留小数点有效数字两位
			schConMatSitStat.setMatConRate(matConRate);
			schConMatSitStatMap.put(entry.getValue(), schConMatSitStat);
		}
		
	}
	
	/**
	 * 获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按学校类型统计)
	 * @param distId
	 * @param dates
	 * @param
	 * @return
	 */
	public static void getConMatSitBySchoolType(String distId, String[] dates,Map<Integer, String> schoolPropertyMap, Db1Service db1Service,SaasService saasService,
			Map<String,SchConMatSitStat> schConMatSitStatMap,EduSchoolService eduSchoolService,RedisService redisService) {
		Set<String> schoolSet = new HashSet<String>();
		Map<String, String> gcRetentiondishMap = new HashMap<String, String>();
		
		//key：学校编号+餐别+菜单+菜品+日期+状态,value :对应个数
		Map<String,Integer> schoolDetailMap = new HashMap<String,Integer>();
		
		int dateCount = dates.length;
		String key = null;
		String keyVal = null;
		Map<String, Integer> schIdMap = new HashMap<>();
		//所有学校id
		List<TEduSchoolDo> tesDoList = db1Service.getTEduSchoolDoListByDs1(distId,1,1, 1);
		for(int i = 0; i < tesDoList.size(); i++) {
			schIdMap.put(tesDoList.get(i).getId(), i+1);
		}
		
		List<TEduSchoolSupplierDo> tEduSchoolSupplierDoList = saasService.getAllIdSupplierIdSchoolId(null);
    	//Key:SupplierId value:shcooolId
    	Map<String,TEduSchoolSupplierDo> schoolSupplierMap = tEduSchoolSupplierDoList.stream().collect(Collectors.toMap(TEduSchoolSupplierDo::getId,Function.identity(),(oldData,newData)->newData));
		
		// 时间段内各区菜品用料计划确认详情
		Integer schoolDetailTotal = 0;
		String schoolId="";
		for(int k = 0; k < dateCount; k++) {
			key = dates[k] + "_useMaterialPlan-Detail";
			gcRetentiondishMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (gcRetentiondishMap != null) {
				for (String curKey : gcRetentiondishMap.keySet()) {
					keyVal = gcRetentiondishMap.get(curKey);
					// 菜品用料计划确认列表
					String[] keyVals = curKey.split("_");
					if(keyVals.length >= 10) {
						if(distId != null) {
							if(keyVals[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						if(schoolSupplierMap.get(keyVals[7])==null) {
							continue;
						}
						schoolId = schoolSupplierMap.get(keyVals[7]).getSchoolId();
						
						String schoolDetailMapKey = schoolId+"_"+dates[k]+"_"+gcRetentiondishMap.get(curKey);
						//学校信息（项目点）
						schoolDetailTotal=schoolDetailMap.get(schoolDetailMapKey)==null?0:schoolDetailMap.get(schoolDetailMapKey);
						schoolDetailMap.put(schoolDetailMapKey, schoolDetailTotal+1);
					}
					else {
						logger.info("菜品用料计划确认："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
		//用料计划总数
		Map<String,Integer> totalMatPlanNumMap = new HashMap<String,Integer>();
		//已确认用料计划数
		Map<String,Integer> conMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划数
		Map<String,Integer> noConMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划学校数
		Map<String,Set<String>> conMatSchNumMap = new HashMap<String,Set<String>>();
		//未确认用料计划学校数
		Map<String,Set<String>> noConMatSchNumMap = new HashMap<String,Set<String>>();
		
		//获取学校
		List<EduSchool> schoolList = eduSchoolService.getEduSchools();
		Map<String,EduSchool> schoolMap = schoolList.stream().collect(Collectors.toMap(EduSchool::getId,(b)->b));
		int count = 0;
		for(Map.Entry<String, Integer> entry : schoolDetailMap.entrySet() ) {
			String []keys=entry.getKey().split("_");
			EduSchool eduSchool =  schoolMap.get(keys[0]);
			if(eduSchool!=null ) {
				
				String schTypeId = AppModConfig.getSchTypeId(eduSchool.getLEVEL(), eduSchool.getLevel2())==null?null:AppModConfig.getSchTypeId(eduSchool.getLEVEL(), eduSchool.getLevel2()).toString();
				if(schTypeId == null || "-1".equals(schTypeId)) {
					continue;
				}
				
				//已验收数量
				if(totalMatPlanNumMap.get(schTypeId)==null) {
					count = entry.getValue();
				}else {
					count = totalMatPlanNumMap.get(schTypeId)+entry.getValue();
				}
				
				totalMatPlanNumMap.put(schTypeId, count);
				
				
				if(keys[2] !=null && keys[2].equals("2")) {
					//已验收数量
					if(conMatPlanNumMap.get(schTypeId)==null) {
						count = entry.getValue();
					}else {
						count = conMatPlanNumMap.get(schTypeId)+entry.getValue();
					}
					
					conMatPlanNumMap.put(schTypeId, count);
					
					//已用料计划确认学校数
					schoolSet=conMatSchNumMap.get(schTypeId);
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					conMatSchNumMap.put(schTypeId, schoolSet);
					
				}else{
					//未验收数量
					if(noConMatPlanNumMap.get(schTypeId)==null) {
						count = entry.getValue();
					}else {
						count = noConMatPlanNumMap.get(schTypeId)+entry.getValue();
					}
					
					noConMatPlanNumMap.put(schTypeId, count);
					
					//未验收学校数
					schoolSet=noConMatSchNumMap.get(schTypeId);
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					noConMatSchNumMap.put(schTypeId, schoolSet);
				}
				
			}
			
			
		}
		
		//计算用料计划确认率和数据封装
		float matConRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(entry.getValue());
				if(schConMatSitStat==null) {
					schConMatSitStat = new SchConMatSitStat();
					//学校性质名称
					schConMatSitStat.setStatPropName(entry.getValue());
					//
					schConMatSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				
				//未确认用料学校
				schConMatSitStat.setNoConMatSchNum(noConMatSchNumMap.get(entry.getKey().toString())==null?0:noConMatSchNumMap.get(entry.getKey().toString()).size());
				//已确认用料学校
				schConMatSitStat.setConMatSchNum(conMatSchNumMap.get(entry.getKey().toString())==null?0:conMatSchNumMap.get(entry.getKey().toString()).size());
				
				
				//应确认用料学校
				schConMatSitStat.setShouldAccSchNum(schConMatSitStat.getNoConMatSchNum() + schConMatSitStat.getConMatSchNum());
				
				
				int totalMatPlanNum = totalMatPlanNumMap.get(entry.getKey().toString())==null?0:totalMatPlanNumMap.get(entry.getKey().toString());
				int conMatPlanNum = conMatPlanNumMap.get(entry.getKey().toString())==null?0:conMatPlanNumMap.get(entry.getKey().toString());
				int noConMatPlanNum = noConMatPlanNumMap.get(entry.getKey().toString())==null?0:noConMatPlanNumMap.get(entry.getKey().toString());
				
				matConRate = 0;
				if(totalMatPlanNum > 0) {
					matConRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
					BigDecimal bd = new BigDecimal(matConRate);
					matConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (matConRate > 100) {
						matConRate = 100;
					}
				}
				//用料计划总数
				schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
				// 已确认用料计划数
				schConMatSitStat.setConMatPlanNum(conMatPlanNum);
				//未确认用料计划数
				schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
				//确认率，保留小数点有效数字两位
				schConMatSitStat.setMatConRate(matConRate);
				schConMatSitStatMap.put(entry.getValue(), schConMatSitStat);
				schConMatSitStatMap.put(entry.getValue(), schConMatSitStat);
		}
		
	}
	/**
	 * 获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按学校类型统计)
	 * @param distId
	 * @param dates
	 * @param
	 * @return
	 */
	public static void getConMatSitBySchoolTypeFromHive(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap, Db1Service db1Service,SaasService saasService,
			Map<String,SchConMatSitStat> schConMatSitStatMap,EduSchoolService eduSchoolService,DbHiveMatService dbHiveMatService) {
		
		//用料计划总数
		Map<String,Integer> totalMatPlanNumMap = new HashMap<String,Integer>();
		//已确认用料计划数
		Map<String,Integer> conMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划数
		Map<String,Integer> noConMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划学校数
		Map<String,Integer> conMatSchNumMap = new HashMap<String,Integer>();
		//未确认用料计划学校数
		Map<String,Integer> noConMatSchNumMap = new HashMap<String,Integer>();
		
		
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
		
		List<SchMatCommon> dishList = new ArrayList<>();
		dishList = dbHiveMatService.getMatList(DataKeyConfig.talbeMaterialTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,departmentId,null, 2);
		if(dishList !=null && dishList.size() > 0) {
			for(SchMatCommon schDishCommon: dishList) {
				
				if(schDishCommon.getStatus() ==null) {
					totalMatPlanNumMap.put(schDishCommon.getLevelName(), 
							(totalMatPlanNumMap.get(schDishCommon.getLevelName())==null?0:totalMatPlanNumMap.get(schDishCommon.getLevelName())) 
							+ schDishCommon.getTotal());
				}else if (schDishCommon.getStatus() == 0 || schDishCommon.getStatus() == 1) {
					//信息不完整和待确认都属于未确认状态
					
					noConMatSchNumMap.put(schDishCommon.getLevelName(), 
							(noConMatSchNumMap.get(schDishCommon.getLevelName())==null?0:noConMatSchNumMap.get(schDishCommon.getLevelName())) 
							+ schDishCommon.getSchoolTotal());
					
					noConMatPlanNumMap.put(schDishCommon.getLevelName(), 
							(noConMatPlanNumMap.get(schDishCommon.getLevelName())==null?0:noConMatPlanNumMap.get(schDishCommon.getLevelName())) 
							+ schDishCommon.getTotal());
					
				}else if (schDishCommon.getStatus() == 2) {
					
					conMatSchNumMap.put(schDishCommon.getLevelName(), 
							(conMatSchNumMap.get(schDishCommon.getLevelName())==null?0:conMatSchNumMap.get(schDishCommon.getLevelName())) 
							+ schDishCommon.getSchoolTotal());
					
					conMatPlanNumMap.put(schDishCommon.getLevelName(), 
							(conMatPlanNumMap.get(schDishCommon.getLevelName())==null?0:conMatPlanNumMap.get(schDishCommon.getLevelName())) 
							+ schDishCommon.getTotal());
				}
			}
		}
		
		//计算用料计划确认率和数据封装
		float matConRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(entry.getValue());
				if(schConMatSitStat==null) {
					schConMatSitStat = new SchConMatSitStat();
					//学校性质名称
					schConMatSitStat.setStatPropName(entry.getValue());
					//
					schConMatSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				
				//未确认用料学校
				schConMatSitStat.setNoConMatSchNum(noConMatSchNumMap.get(entry.getKey().toString())==null?0:noConMatSchNumMap.get(entry.getKey().toString()));
				//已确认用料学校
				schConMatSitStat.setConMatSchNum(conMatSchNumMap.get(entry.getKey().toString())==null?0:conMatSchNumMap.get(entry.getKey().toString()));
				
				
				//应确认用料学校
				schConMatSitStat.setShouldAccSchNum(schConMatSitStat.getNoConMatSchNum() + schConMatSitStat.getConMatSchNum());
				
				
				int totalMatPlanNum = totalMatPlanNumMap.get(entry.getKey().toString())==null?0:totalMatPlanNumMap.get(entry.getKey().toString());
				int conMatPlanNum = conMatPlanNumMap.get(entry.getKey().toString())==null?0:conMatPlanNumMap.get(entry.getKey().toString());
				int noConMatPlanNum = noConMatPlanNumMap.get(entry.getKey().toString())==null?0:noConMatPlanNumMap.get(entry.getKey().toString());
				
				matConRate = 0;
				if(totalMatPlanNum > 0) {
					matConRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
					BigDecimal bd = new BigDecimal(matConRate);
					matConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (matConRate > 100) {
						matConRate = 100;
					}
				}
				//用料计划总数
				schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
				// 已确认用料计划数
				schConMatSitStat.setConMatPlanNum(conMatPlanNum);
				//未确认用料计划数
				schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
				//确认率，保留小数点有效数字两位
				schConMatSitStat.setMatConRate(matConRate);
				schConMatSitStatMap.put(entry.getValue(), schConMatSitStat);
				schConMatSitStatMap.put(entry.getValue(), schConMatSitStat);
		}
		
	}
	
	/**
	 * 获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按学校类型统计)(改为新建模数据)
	 * @param distId
	 * @param dates
	 * @param
	 * @return
	 */
	public static void getConMatSitBySchoolTypeTwo(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap, Db1Service db1Service,SaasService saasService,
			Map<String,SchConMatSitStat> schConMatSitStatMap,EduSchoolService eduSchoolService,RedisService redisService) {
		Integer schoolNum = 0;//未用料确认学校个数

		Map<String, String> distributionTotalMap = new HashMap<>();
		int k;
		int dateCount = dates.length;
		String key = null;
		
		//菜品数量
		Map<String,Integer> totalMatPlanNumMap = new HashMap<String,Integer>();
		//已用料确认数量
		Map<String,Integer> conMatPlanNumMap = new HashMap<String,Integer>();
		//未用料确认数量
		Map<String,Integer> noConMatPlanNumMap = new HashMap<String,Integer>();
		//未用料确认学校个数
		Map<String,Integer> noConMatSchNumMap = new HashMap<String,Integer>();
		//已用料确认学校个数
		Map<String,Integer> conMatSchNumMap = new HashMap<String,Integer>();
		Integer count =0;
		Integer valueCount =0;
		
		// 时间段内各区配货计划详情
		for(k = 0; k < dateCount; k++) {
			key = dates[k] + DataKeyConfig.useMaterialPlanTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentUseMaterialPlanTotal+departmentId;
			}
			distributionTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			if (distributionTotalMap != null) {
				for (String curKey : distributionTotalMap.keySet()) {
					valueCount = distributionTotalMap.get(curKey)==null?0:Integer.parseInt(distributionTotalMap.get(curKey));
					count=valueCount;
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
						if(totalMatPlanNumMap.get(curKeys[3])!=null) {
							count = totalMatPlanNumMap.get(curKeys[3])+valueCount;
						}
						
						totalMatPlanNumMap.put(curKeys[3], count);
						
						
						count = valueCount;
						if(curKeys[5].equalsIgnoreCase("2")) {
							//已用料确认数量
							if(conMatPlanNumMap.get(curKeys[3])!=null) {
								count = conMatPlanNumMap.get(curKeys[3])+valueCount;
							}
							
							conMatPlanNumMap.put(curKeys[3], count);
						}else { 
							//未用料确认数量
							if(noConMatPlanNumMap.get(curKeys[3])!=null) {
								count = noConMatPlanNumMap.get(curKeys[3])+valueCount;
							}
							
							noConMatPlanNumMap.put(curKeys[3], count);
						}
					}
					
					//未用料确认学校数
					if(curKey.indexOf("school-lev-area_")==0 && curKeys.length >= 5) {
						//过滤区域
						if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						if(curKeys[5].equalsIgnoreCase("2")) { 
							//已用料确认学校数
							schoolNum=conMatSchNumMap.get(curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							conMatSchNumMap.put(curKeys[3], schoolNum);
							
						}else {
							schoolNum=noConMatSchNumMap.get(curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							noConMatSchNumMap.put(curKeys[3], schoolNum);
						}
					}
				}
			}
		}
		
		//计算用料计划确认率和数据封装
		float matConRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
				SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(entry.getValue());
				if(schConMatSitStat==null) {
					schConMatSitStat = new SchConMatSitStat();
					//学校性质名称
					schConMatSitStat.setStatPropName(entry.getValue());
					//
					schConMatSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				
				//未确认用料学校
				schConMatSitStat.setNoConMatSchNum(noConMatSchNumMap.get(entry.getKey().toString())==null?0:noConMatSchNumMap.get(entry.getKey().toString()));
				//已确认用料学校
				schConMatSitStat.setConMatSchNum(conMatSchNumMap.get(entry.getKey().toString())==null?0:conMatSchNumMap.get(entry.getKey().toString()));
				
				//应确认用料学校
				schConMatSitStat.setShouldAccSchNum(schConMatSitStat.getNoConMatSchNum() + schConMatSitStat.getConMatSchNum());
				
				int totalMatPlanNum = totalMatPlanNumMap.get(entry.getKey().toString())==null?0:totalMatPlanNumMap.get(entry.getKey().toString());
				int conMatPlanNum = conMatPlanNumMap.get(entry.getKey().toString())==null?0:conMatPlanNumMap.get(entry.getKey().toString());
				int noConMatPlanNum = noConMatPlanNumMap.get(entry.getKey().toString())==null?0:noConMatPlanNumMap.get(entry.getKey().toString());
				
				matConRate = 0;
				if(totalMatPlanNum > 0) {
					matConRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
					BigDecimal bd = new BigDecimal(matConRate);
					matConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (matConRate > 100) {
						matConRate = 100;
					}
				}
				//用料计划总数
				schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
				// 已确认用料计划数
				schConMatSitStat.setConMatPlanNum(conMatPlanNum);
				//未确认用料计划数
				schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
				//确认率，保留小数点有效数字两位
				schConMatSitStat.setMatConRate(matConRate);
				schConMatSitStatMap.put(entry.getValue(), schConMatSitStat);
				schConMatSitStatMap.put(entry.getValue(), schConMatSitStat);
		}
		
	}
	
	/**
	 * 获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按学校所属主管部门)
	 * @param distId
	 * @param dates
	 * @param
	 * @return
	 */
	public static void getConMatSitBySlave(String departmentId,String distId, String[] dates,Map<String, String> slaveMap,
			Map<String,SchConMatSitStat> schConMatSitStatMap,RedisService redisService) {
		String key = "";
		String keyVal = "";
		
		Map<String, String> platoonFeedTotalMap = null;
		
		//用料计划总数
		Map<String,Integer> totalMatPlanNumMap = new HashMap<String,Integer>();
		//已确认用料计划数
		Map<String,Integer> conMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划数
		Map<String,Integer> noConMatPlanNumMap = new HashMap<String,Integer>();
		//已确认用料计划学校数
		Map<String,Integer> conMatSchNumMap = new HashMap<String,Integer>();
		//未确认用料计划学校数
		Map<String,Integer> noConMatSchNumMap = new HashMap<String,Integer>();
		
		float matConRate = 0;
		String fieldPrefix="";
		
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k] + DataKeyConfig.useMaterialPlanTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentUseMaterialPlanTotal+departmentId;
			}
			
			platoonFeedTotalMap = redisService.getHashByKey(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key);
			//Redis没有该数据则从hdfs系统中获取
			if(platoonFeedTotalMap == null) {   
				
			}else{
				for(String curKey : platoonFeedTotalMap.keySet()) {
					//用料计划
					fieldPrefix = "masterid_";
					if (curKey.indexOf(fieldPrefix) == 0) {
						String[] curKeys = curKey.split("_");
						if(curKeys.length >= 6)
						{
							if("3".equals(curKeys[1]) && curKeys[3]==null) {
								curKeys[3] = "其他";
							}
							if(curKeys[5].equalsIgnoreCase("2") ) {
								keyVal = platoonFeedTotalMap.get(curKey);
								if(keyVal != null) {
									totalMatPlanNumMap.put(curKeys[1]+"_"+curKeys[3], (totalMatPlanNumMap.get(curKeys[1]+"_"+curKeys[3])==null?0:totalMatPlanNumMap.get(curKeys[1]+"_"+curKeys[3]))+Integer.parseInt(keyVal));
									conMatPlanNumMap.put(curKeys[1]+"_"+curKeys[3], (conMatPlanNumMap.get(curKeys[1]+"_"+curKeys[3])==null?0:conMatPlanNumMap.get(curKeys[1]+"_"+curKeys[3]))+Integer.parseInt(keyVal));
								}
							}else {
								keyVal = platoonFeedTotalMap.get(curKey);
								if(keyVal != null) {
									totalMatPlanNumMap.put(curKeys[1]+"_"+curKeys[3], (totalMatPlanNumMap.get(curKeys[1]+"_"+curKeys[3])==null?0:totalMatPlanNumMap.get(curKeys[1]+"_"+curKeys[3]))+Integer.parseInt(keyVal));
									noConMatPlanNumMap.put(curKeys[1]+"_"+curKeys[3], (noConMatPlanNumMap.get(curKeys[1]+"_"+curKeys[3])==null?0:noConMatPlanNumMap.get(curKeys[1]+"_"+curKeys[3]))+Integer.parseInt(keyVal));
								}
							}
						}
					}
					
					//用料计划学校：已确认用料计划学校、未确认用料计划学校数
					fieldPrefix = "school-masterid_";
					if (curKey.indexOf(fieldPrefix) == 0) {
						String[] curKeys = curKey.split("_");
						
						if(curKeys.length >= 4)
						{
							if("3".equals(curKeys[1]) && curKeys[3]==null) {
								curKeys[3] = "其他";
							}
							if(curKeys[5].equalsIgnoreCase("2") ) {
								keyVal = platoonFeedTotalMap.get(curKey);
								if(keyVal != null) {
									conMatSchNumMap.put(curKeys[1]+"_"+curKeys[3], (conMatSchNumMap.get(curKeys[1]+"_"+curKeys[3])==null?0:conMatSchNumMap.get(curKeys[1]+"_"+curKeys[3]))+Integer.parseInt(keyVal));
								}
							}else {
								keyVal = platoonFeedTotalMap.get(curKey);
								if(keyVal != null) {
									noConMatSchNumMap.put(curKeys[1]+"_"+curKeys[3], (noConMatSchNumMap.get(curKeys[1]+"_"+curKeys[3])==null?0:noConMatSchNumMap.get(curKeys[1]+"_"+curKeys[3]))+Integer.parseInt(keyVal));
								}
							}
						}
					}
				}
			}
		}
		for (Map.Entry<String, String> entry : slaveMap.entrySet()) {
			String[] keys = entry.getKey().split("_");
			String masterid = keys[0];
			String slave = keys[1];
			
			SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(entry.getKey());
			if(schConMatSitStat==null) {
				schConMatSitStat = new SchConMatSitStat();
				schConMatSitStat.setStatClassName(entry.getValue());
				//区域名称
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				schConMatSitStat.setStatPropName(slaveName);
			}
			
			
			int noConMatSchNum = noConMatSchNumMap.get(entry.getKey())==null?0:noConMatSchNumMap.get(entry.getKey());
			int conMatSchNum = conMatSchNumMap.get(entry.getKey())==null?0:conMatSchNumMap.get(entry.getKey());
			int totalMatPlanNum = totalMatPlanNumMap.get(entry.getKey())==null?0:totalMatPlanNumMap.get(entry.getKey());
			int conMatPlanNum = conMatPlanNumMap.get(entry.getKey())==null?0:conMatPlanNumMap.get(entry.getKey());
			int noConMatPlanNum = noConMatPlanNumMap.get(entry.getKey())==null?0:noConMatPlanNumMap.get(entry.getKey());
			
			matConRate = 0;
			if(totalMatPlanNum > 0) {
				matConRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(matConRate);
				matConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (matConRate > 100) {
					matConRate = 100;
				}
			}
			
			//未确认用料学校
			schConMatSitStat.setNoConMatSchNum(noConMatSchNum);
			//已确认用料学校
			schConMatSitStat.setConMatSchNum(conMatSchNum);
			
			//应确认用料学校
			schConMatSitStat.setShouldAccSchNum(schConMatSitStat.getNoConMatSchNum() + schConMatSitStat.getConMatSchNum());
			
			//用料计划总数
			schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
			// 已确认用料计划数
			schConMatSitStat.setConMatPlanNum(conMatPlanNum);
			//未确认用料计划数
			schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
			//确认率，保留小数点有效数字两位
			schConMatSitStat.setMatConRate(matConRate);
			
			schConMatSitStatMap.put(entry.getKey(), schConMatSitStat);
		}
		
		
	}
	/**
	 * 获取用料计划数据(未确认用料学校、已确认用料学校、用料计划总数、已确认用料计划数、未确认用料计划数、确认率)(按学校所属主管部门)
	 * @param distId
	 * @param dates
	 * @param
	 * @return
	 */
	public static void getConMatSitBySlaveFromHive(String departmentId,String distId, String[] dates,Map<String, String> slaveMap,
			Map<String,SchConMatSitStat> schConMatSitStatMap,DbHiveMatService dbHiveMatService) {
		
		
		//用料计划总数
		Map<String,Integer> totalMatPlanNumMap = new HashMap<String,Integer>();
		//已确认用料计划数
		Map<String,Integer> conMatPlanNumMap = new HashMap<String,Integer>();
		//未确认用料计划数
		Map<String,Integer> noConMatPlanNumMap = new HashMap<String,Integer>();
		//已确认用料计划学校数
		Map<String,Integer> conMatSchNumMap = new HashMap<String,Integer>();
		//未确认用料计划学校数
		Map<String,Integer> noConMatSchNumMap = new HashMap<String,Integer>();
		
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
		
		List<SchMatCommon> dishList = new ArrayList<>();
		dishList = dbHiveMatService.getMatList(DataKeyConfig.talbeMaterialTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null, departmentId,null,3);
		if(dishList !=null && dishList.size() > 0) {
			for(SchMatCommon schDishCommon: dishList) {
				String key = schDishCommon.getDepartmentMasterId() + "_" +schDishCommon.getDepartmentSlaveIdName();
				
				if(schDishCommon.getStatus() ==null) {
					totalMatPlanNumMap.put(key, 
							(totalMatPlanNumMap.get(key)==null?0:totalMatPlanNumMap.get(key))+ schDishCommon.getTotal());
				}else if (schDishCommon.getStatus() == 0 || schDishCommon.getStatus() == 1) {
					//信息不完整和待确认都属于未确认状态
					
					noConMatSchNumMap.put(key, 
							(noConMatSchNumMap.get(key)==null?0:noConMatSchNumMap.get(key))+ schDishCommon.getSchoolTotal());
					
					noConMatPlanNumMap.put(key, 
							(noConMatPlanNumMap.get(key)==null?0:noConMatPlanNumMap.get(key))+ schDishCommon.getTotal());
					
				}else if (schDishCommon.getStatus() == 2) {
					
					conMatSchNumMap.put(key, 
							(conMatSchNumMap.get(key)==null?0:conMatSchNumMap.get(key)) + schDishCommon.getSchoolTotal());
					
					conMatPlanNumMap.put(key, 
							(conMatPlanNumMap.get(key)==null?0:conMatPlanNumMap.get(key)) + schDishCommon.getTotal());
				}
			}
		}
		
		float matConRate = 0;
		for (Map.Entry<String, String> entry : slaveMap.entrySet()) {
			String[] keys = entry.getKey().split("_");
			String masterid = keys[0];
			String slave = keys[1];
			
			SchConMatSitStat schConMatSitStat = schConMatSitStatMap.get(entry.getKey());
			if(schConMatSitStat==null) {
				schConMatSitStat = new SchConMatSitStat();
				schConMatSitStat.setStatClassName(entry.getValue());
				//区域名称
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				schConMatSitStat.setStatPropName(slaveName);
			}
			
			//转换，hive库中 区属的子项是数字，slaveMap中为中文
			String dataKey = masterid+"_";
			if("3".equals(masterid)) {
				dataKey += AppModConfig.compDepNameToIdMap3.get(slave);
			}else {
				dataKey += slave;
			}
			
			
			int noConMatSchNum = noConMatSchNumMap.get(dataKey)==null?0:noConMatSchNumMap.get(dataKey);
			int conMatSchNum = conMatSchNumMap.get(dataKey)==null?0:conMatSchNumMap.get(dataKey);
			int totalMatPlanNum = totalMatPlanNumMap.get(dataKey)==null?0:totalMatPlanNumMap.get(dataKey);
			int conMatPlanNum = conMatPlanNumMap.get(dataKey)==null?0:conMatPlanNumMap.get(dataKey);
			int noConMatPlanNum = noConMatPlanNumMap.get(dataKey)==null?0:noConMatPlanNumMap.get(dataKey);
			
			matConRate = 0;
			if(totalMatPlanNum > 0) {
				matConRate = 100 * ((float) conMatPlanNum / (float) totalMatPlanNum);
				BigDecimal bd = new BigDecimal(matConRate);
				matConRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (matConRate > 100) {
					matConRate = 100;
				}
			}
			
			//未确认用料学校
			schConMatSitStat.setNoConMatSchNum(noConMatSchNum);
			//已确认用料学校
			schConMatSitStat.setConMatSchNum(conMatSchNum);
			
			//应确认用料学校
			schConMatSitStat.setShouldAccSchNum(schConMatSitStat.getNoConMatSchNum() + schConMatSitStat.getConMatSchNum());
			
			//用料计划总数
			schConMatSitStat.setTotalMatPlanNum(totalMatPlanNum);
			// 已确认用料计划数
			schConMatSitStat.setConMatPlanNum(conMatPlanNum);
			//未确认用料计划数
			schConMatSitStat.setNoConMatPlanNum(noConMatPlanNum);
			//确认率，保留小数点有效数字两位
			schConMatSitStat.setMatConRate(matConRate);
			
			schConMatSitStatMap.put(entry.getKey(), schConMatSitStat);
		}
		
		
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
	public SchConMatSitStatDTO appModFunc(String token, String distName, String prefCity, String province,String startDate, String endDate, Integer statMode,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,EduSchoolService eduSchoolService,DbHiveMatService dbHiveMatService ) {
		
		SchConMatSitStatDTO schConMatSitStatDTO = null;
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
			String currDistName = null;
			
			String departmentId = null;
			
			int curSubLevel = -1;
			String subLevel=null;//所属，0:其他，1:部属，2:市属，3: 区属，按主管部门有效【预留】
			String compDep=null;//主管部门【预留】
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
					// 餐厨垃圾学校回收列表函数
					if(methodIndex==0) {
						schConMatSitStatDTO = schConMatSitStatFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						schConMatSitStatDTO = schConMatSitStatFuncOne(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates, 
								tedList, 
								db1Service, saasService,eduSchoolService,dbHiveMatService);
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
					// 餐厨垃圾学校回收列表函数
					if(methodIndex==0) {
						schConMatSitStatDTO = schConMatSitStatFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						schConMatSitStatDTO = schConMatSitStatFuncOne(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates, tedList, 
								db1Service, saasService,eduSchoolService,dbHiveMatService);
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
			schConMatSitStatDTO = new SchConMatSitStatDTO();
		}		

		
		return schConMatSitStatDTO;
	}
}
