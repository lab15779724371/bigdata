package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishSitStat;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishSitStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 3.2.10.	学校排菜情况统计应用模型
 * @author fengyang_xie
 *
 */
public class SchDishSitStatAppMod {
	private static final Logger logger = LogManager.getLogger(SchDishSitStatAppMod.class.getName());
	
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
			"   \"schDishSitStat\": [\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"  \"statPropName\":\"浦东新区\",\r\n" + 
			"   \"totalSchNum\":55,\r\n" + 
			"\"mealSchNum\":50,\r\n" + 
			"\"dishSchNum\":40,\r\n" + 
			"\"noDishSchNum\":10,\r\n" + 
			"   \"dishRate\":36.33\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"  \"statPropName\":\"黄浦区\",\r\n" + 
			"   \"totalSchNum\":55,\r\n" + 
			"\"mealSchNum\":50,\r\n" + 
			"\"dishSchNum\":40,\r\n" + 
			"\"noDishSchNum\":10,\r\n" + 
			"   \"dishRate\":36.33\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"\"statPropName\":\"静安区\",\r\n" + 
			"   \"totalSchNum\":55,\r\n" + 
			"\"mealSchNum\":50,\r\n" + 
			"\"dishSchNum\":40,\r\n" + 
			"\"noDishSchNum\":10,\r\n" + 
			"   \"dishRate\":36.33\r\n" + 
			" } ],\r\n" + 
			"   \"msgId\":1\r\n" + 
			"}\r\n" + 
			"";
	
	/**
	 * 汇总数据
	 * @return
	 */
	private SchDishSitStatDTO schDishSitStatFunc(String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService ) {
		
		SchDishSitStatDTO schDishSitStatDTO = new SchDishSitStatDTO();
		
		List<SchDishSitStat> schDishSitStat = new ArrayList<SchDishSitStat>();
		schDishSitStatDTO.setSchDishSitStat(schDishSitStat);
		
		JSONObject jsStr = JSONObject.parseObject(tempData); 
		schDishSitStatDTO = (SchDishSitStatDTO) JSONObject.toJavaObject(jsStr,SchDishSitStatDTO.class);
		
		
		
		
		//时戳
		schDishSitStatDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		schDishSitStatDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return schDishSitStatDTO;
	}
	
	/**
	 * 汇总数据
	 * @return
	 */
	private SchDishSitStatDTO schDishSitStatFuncOne(String departmentId,String distId,String currDistName,Integer statMode,Integer subLevel,Integer compDep,
			String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService,EduSchoolService eduSchoolService,DbHiveDishService dbHiveDishService) {
		
		SchDishSitStatDTO schDishSitStatDTO = new SchDishSitStatDTO();
		
		List<SchDishSitStat> schDishSitStatList = new ArrayList<SchDishSitStat>();
		
		Map<String,SchDishSitStat> schDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
		
		DateTime startDt = BCDTimeUtil.convertDateStrToDate(dates[dates.length-1]);
		DateTime currentTime = new DateTime();
		int days = Days.daysBetween(startDt, currentTime).getDays();
		
		
		if(statMode == 0) {
			//========0:按区统计
			if(days >= 2) {
				//监管学校数量 、排菜汇总
				getDishInfoByAreaFromHive(departmentId,distId, dates, tedList, schDishSitStatMap, redisService, dbHiveDishService);
			}else {
				/**
				 * 监管学校数量
				 */
				getSupSchNumByArea(departmentId,distId,currDistName,tedList,schDishSitStatMap);
				
				/**
				 * 排菜汇总
				 */
				getDishInfoByArea(departmentId,distId, dates, tedList,schDishSitStatMap,redisService);
			}
			
			
		}else if(statMode == 1) {
			//========1:按学校性质统计
			Map<Integer, String> schoolPropertyMap = new HashMap<Integer,String>();
			schoolPropertyMap.put(0, "公办");
			schoolPropertyMap.put(2, "民办");
			schoolPropertyMap.put(3, "外籍人员子女学校");
			schoolPropertyMap.put(4, "其他");
			
			if(days >= 2) {
				//监管学校数量、排菜汇总
				getDishInfoByNatureFromHive(departmentId,distId, dates, schoolPropertyMap, schDishSitStatMap, redisService, dbHiveDishService);
			}else {
				/**
				 * 监管学校数量
				 */
				getSupSchNumByNature(departmentId,distId,currDistName,schoolPropertyMap,schDishSitStatMap);
				
				
				/**
				 * 排菜汇总
				 */
				getDishInfoByNature(departmentId,distId, dates, schoolPropertyMap,schDishSitStatMap,redisService);
			}
			
		}else if (statMode == 2) {
			//========2:按学校学制统计
			
			if(days >= 2) {
				getDishInfoBySchoolTypeFromHive(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap, schDishSitStatMap, redisService, dbHiveDishService);
			}else {
				
				/**
				 * 监管学校数量
				 */
				getSupSchNumBySchoolType(departmentId,distId,currDistName, AppModConfig.schTypeIdToNameMap,schDishSitStatMap);
				
				
				/**
				 * 排菜汇总
				 */
				getDishInfoBySchoolType(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap,schDishSitStatMap,redisService);
			}
			//计算每种【学制分类】的数量
			Map<String,SchDishSitStat> newSchDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			String statClassName="";
			SchDishSitStat schDishSitStatSum = new SchDishSitStat();
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				
				
				
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newSchDishSitStatMap.put(statClassName+"_小计", schDishSitStatSum);
					schDishSitStatSum = new SchDishSitStat();
				}

				//计算数据
				setSchoolPropertSumData(schDishSitStatSum, entry);
				statClassName = entry.getValue().getStatClassName();
				schDishSitStatSum.setStatClassName(statClassName);
				schDishSitStatSum.setStatPropName("小计");
				setRate(schDishSitStatSum);
				
				newSchDishSitStatMap.put(entry.getKey(), entry.getValue());
				
			}
			newSchDishSitStatMap.put("其他"+"_小计", schDishSitStatSum);
			schDishSitStatMap = newSchDishSitStatMap;
			
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
		    
		    
		    if(days >= 2) {
		    	getDishInfoBySlaveHive(departmentId,distId, dates, compDepNameToSubLevelNameMap, schDishSitStatMap, redisService, dbHiveDishService);
			}else {
				/**
				 * 监管学校数量
				 */
			    getSupSchNumBySchoolSlave(departmentId,distId,currDistName, compDepNameToSubLevelNameMap,schDishSitStatMap);
				
				
				/**
				 * 排菜汇总
				 */
				getDishInfoBySlave(departmentId,distId, dates, compDepNameToSubLevelNameMap,schDishSitStatMap,redisService);
			}	
		    
		    //计算每种【学制分类】的数量
			Map<String,SchDishSitStat> newSchDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			String statClassName="";
			SchDishSitStat schDishSitStatSum = new SchDishSitStat();
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newSchDishSitStatMap.put(statClassName+"_小计", schDishSitStatSum);
					schDishSitStatSum = new SchDishSitStat();
				}

				//计算数据
				setSchoolPropertSumData(schDishSitStatSum, entry);
				statClassName = entry.getValue().getStatClassName();
				schDishSitStatSum.setStatClassName(statClassName);
				schDishSitStatSum.setStatPropName("小计");
				//计算比率：包括排菜率、验收率、留样率、预警处理率
                setRate(schDishSitStatSum);
				newSchDishSitStatMap.put(entry.getKey(), entry.getValue());
				
			}
			newSchDishSitStatMap.put("其他"+"_小计", schDishSitStatSum);
			schDishSitStatMap = newSchDishSitStatMap;
			
			//循环技术小计的百分比（包括排菜率、验收率、留样率、预警处理率、）
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				if(entry.getKey().contains("小计")) {
					schDishSitStatSum = entry.getValue();
				}
			}
			
		}else if(statMode == 4) {
			//========4:按学校管理部门统计
			
			//管理部门
			//获取用户信息，用于匹配部门的编辑人
			
			Map<Integer, String> departmentMap = new LinkedHashMap<Integer,String>();
			List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(new DepartmentObj(),null, -1, -1);	
			
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
			
			
			
			if(days >= 2) {
				getDishInfoByDepartmentFromHive(departmentId, dates, departmentMap, schOwnTypes, schDishSitStatMap, redisService, dbHiveDishService);
			}else {
				
				/**
				 * 监管学校数量(暂时不取)
				 */
				getSupSchNumByDepartment(departmentId, departmentMap, schDishSitStatMap);
				
				/**
				 * 排菜汇总
				 */
				getDishInfoByDepartment(departmentId, dates, departmentMap,schOwnTypes,schDishSitStatMap,redisService);
			}
			
			//计算每种【管理】的数量
			Map<String,SchDishSitStat> newSchDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			String statPropName="";
			SchDishSitStat schDishSitStatSum = new SchDishSitStat();
			
			SchDishSitStat preSchDishSitStatSum = new SchDishSitStat();
			int sumIndex = 0;
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				sumIndex ++ ;
				if(sumIndex == 17 || sumIndex ==18 || sumIndex == 19) {
					newSchDishSitStatMap.put(statPropName+"_小计", schDishSitStatSum);
					schDishSitStatSum = new SchDishSitStat();
					try {
						BeanUtils.copyProperties(schDishSitStatSum, preSchDishSitStatSum);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				
				//计算数据
				setSchoolPropertSumData(schDishSitStatSum, entry);
				statPropName = entry.getValue().getStatPropName();
				schDishSitStatSum.setStatPropName("小计");
				setRate(schDishSitStatSum);
				
				newSchDishSitStatMap.put(entry.getKey(), entry.getValue());
				
				
				if(sumIndex == 16 || sumIndex ==17 || sumIndex == 18) {
					try {
						preSchDishSitStatSum = new SchDishSitStat();
						BeanUtils.copyProperties(preSchDishSitStatSum, schDishSitStatSum);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				

				
				
			}
			newSchDishSitStatMap.put("小计", schDishSitStatSum);
			schDishSitStatMap = newSchDishSitStatMap;
			
			/*//循环技术小计的百分比（包括排菜率、验收率、留样率、预警处理率、）
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				if(entry.getKey().contains("小计")) {
					schDishSitStatSum = entry.getValue();
				}
			}*/
		}
		
		
		
		schDishSitStatMap.values();
		Collection<SchDishSitStat> valueCollection = schDishSitStatMap.values();
	    schDishSitStatList = new ArrayList<SchDishSitStat>(valueCollection);
		
		
		schDishSitStatDTO.setSchDishSitStat(schDishSitStatList);
		
		//时戳
		schDishSitStatDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		schDishSitStatDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return schDishSitStatDTO;
	}
	
	private void setRate(SchDishSitStat schDishSitStatSum) {
		//排菜率
		float distDishRate = 0;
		if(schDishSitStatSum.getMealSchNum() > 0) {
			distDishRate = 100 * ((float) (schDishSitStatSum.getMealSchNum() - schDishSitStatSum.getNoDishSchNum()) / (float) schDishSitStatSum.getMealSchNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		schDishSitStatSum.setDishRate(distDishRate);
		
		//学校标准验收率
		distDishRate = 0;
		if(schDishSitStatSum.getMealSchNum()> 0) {
			distDishRate = 100 * ((float) (schDishSitStatSum.getStandardNum()) / (float) schDishSitStatSum.getMealSchNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		schDishSitStatSum.setStandardRate(distDishRate);
	}

	
	/**
	 * 学习学制每个分类小计
	 * @param schDishSitStatSum
	 * @param entry
	 */
	private void setSchoolPropertSumData(SchDishSitStat schDishSitStatSum, Map.Entry<String, SchDishSitStat> entry) {
		/**
		 * 学校总数
		 */
		schDishSitStatSum.setTotalSchNum(schDishSitStatSum.getTotalSchNum()+entry.getValue().getTotalSchNum());
		
		/**
		 * 未排菜学校数
		 */
		schDishSitStatSum.setNoDishSchNum(schDishSitStatSum.getNoDishSchNum()+entry.getValue().getNoDishSchNum());
		
		/**
		 * 应排菜天数，即供餐天数(供餐学校数量)
		 */
		schDishSitStatSum.setMealSchNum(schDishSitStatSum.getMealSchNum()+entry.getValue().getMealSchNum());
		
		/**
		 * 已排菜学校
		 */
		schDishSitStatSum.setDishSchNum(schDishSitStatSum.getDishSchNum()+entry.getValue().getDishSchNum());
		
		/**
		 * 排菜率
		 */
		schDishSitStatSum.setDishRate(schDishSitStatSum.getDishRate()+entry.getValue().getDishRate());
		
		schDishSitStatSum.setStandardNum(schDishSitStatSum.getStandardNum()+entry.getValue().getStandardNum());
		schDishSitStatSum.setSupplementNum(schDishSitStatSum.getSupplementNum()+entry.getValue().getSupplementNum());
		schDishSitStatSum.setBeOverdueNum(schDishSitStatSum.getBeOverdueNum()+entry.getValue().getBeOverdueNum());
		schDishSitStatSum.setNoDataNum(schDishSitStatSum.getNoDataNum()+entry.getValue().getNoDataNum());
		schDishSitStatSum.setStandardRate(schDishSitStatSum.getStandardRate()+entry.getValue().getStandardRate());
		
		
	}
	
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getDishInfoByArea(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,SchDishSitStat> schDishSitStatMap,RedisService redisService) {
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
		

		int[][] standardNums = new int[dateCount][distCount];
		int[][] supplementNums = new int[dateCount][distCount];
		int[][] beOverdueNums = new int[dateCount][distCount];
		int[][] noDataNums = new int[dateCount][distCount];
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
						
						//操作状态对应的数量
						fieldPrefix = "area_"+curDistId + "_plastatus_";
						int standardNum = 0, supplementNum = 0, beOverdueNum = 0, noDataNum = 0;
						if (curKey.indexOf(fieldPrefix) == 0) {
							String[] curKeys = curKey.split("_");
							
							if(curKeys.length >= 4)
							{
								if(curKeys[3].equalsIgnoreCase("1")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										standardNum = Integer.parseInt(keyVal);
									}
								}else if(curKeys[3].equalsIgnoreCase("2")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										supplementNum = Integer.parseInt(keyVal);
									}
								}else if(curKeys[3].equalsIgnoreCase("3")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										beOverdueNum = Integer.parseInt(keyVal);
									}
								}else if(curKeys[3].equalsIgnoreCase("4")) {
									keyVal = platoonFeedTotalMap.get(curKey);
									if(keyVal != null) {
										noDataNum = Integer.parseInt(keyVal);
									}
								}
							}
						}
						
						standardNums[k][i] += standardNum;
						supplementNums[k][i] += supplementNum;
						beOverdueNums[k][i] += beOverdueNum;
						noDataNums[k][i] += noDataNum;
						
					}
				}
			}
		}
		
		float standardRate = 0;
		for (int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equals(distId)) {
					continue;
				}
			}
			
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(curTdd.getName());
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//区域名称
				schDishSitStat.setStatPropName(curTdd.getName());
			}
			
			int totalDistSchNum = 0;
			int distDishSchNum = 0;
			int distNoDishSchNum = 0;
			
			int standardNum = 0;
			int supplementNum = 0;
			int beOverdueNum = 0;
			int noDataNum = 0;
			
			for (int k = 0; k < dates.length; k++) {
				totalDistSchNum += totalMealSchNums[k][i];
				distDishSchNum += distDishSchNums[k][i];
				distNoDishSchNum += distNoDishSchNums[k][i];
				
				standardNum += standardNums[k][i];
				supplementNum += supplementNums[k][i];
				beOverdueNum += beOverdueNums[k][i];
				noDataNum += noDataNums[k][i];
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
			schDishSitStat.setMealSchNum(totalDistSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			schDishSitStat.setNoDishSchNum(distNoDishSchNum);
			//已排菜学校
			schDishSitStat.setDishSchNum(distDishSchNum);
			//排菜率
			schDishSitStat.setDishRate(distDishRates[i]);
			
			//1 表示规范录入
			schDishSitStat.setStandardNum(standardNum);
			//2 表示补录
			schDishSitStat.setSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishSitStat.setBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishSitStat.setNoDataNum(noDataNum);

			standardRate = 0;
			if(totalDistSchNum > 0) {
				standardRate = 100 * ((float) schDishSitStat.getStandardNum() / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			schDishSitStat.setStandardRate(standardRate);
			
			schDishSitStatMap.put(curTdd.getName(), schDishSitStat);
		}
	}
	
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getDishInfoByAreaFromHive(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,SchDishSitStat> schDishSitStatMap,RedisService redisService,DbHiveDishService dbHiveDishService) {
		// 当天排菜学校总数
		int distCount = tedList.size();
		float[] distDishRates = new float[distCount];
		
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
					
					//未供餐的数据去除
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
					
					}
				}
				
				if(schDishCommon.getPlatoonDealStatus() !=null && schDishCommon.getPlatoonDealStatus() != -1 ) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if(schDishCommon.getPlatoonDealStatus() == 1) {
						standardNumMap.put(schDishCommon.getDistId(), 
								(standardNumMap.get(schDishCommon.getDistId())==null?0:standardNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 2) {
						supplementNumMap.put(schDishCommon.getDistId(), 
								(supplementNumMap.get(schDishCommon.getDistId())==null?0:supplementNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 3) {
						beOverdueNumMap.put(schDishCommon.getDistId(), 
								(beOverdueNumMap.get(schDishCommon.getDistId())==null?0:beOverdueNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 4) {
						noDataNumMap.put(schDishCommon.getDistId(), 
								(noDataNumMap.get(schDishCommon.getDistId())==null?0:noDataNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getTotal());
					}
				}
			}
		}
		
		float standardRate = 0;
		for (int i = 0; i < distCount; i++) {
			TEduDistrictDo curTdd = tedList.get(i);
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equals(distId)) {
					continue;
				}
			}
			
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(curTdd.getName());
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//区域名称
				schDishSitStat.setStatPropName(curTdd.getName());
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
			
			//1 表示规范录入
			int standardNum = standardNumMap.get(String.valueOf(curDistId))==null?0:standardNumMap.get(String.valueOf(curDistId));
			//2 表示补录
			int supplementNum = supplementNumMap.get(String.valueOf(curDistId))==null?0:supplementNumMap.get(String.valueOf(curDistId));
			//3 表示逾期补录
			int beOverdueNum = beOverdueNumMap.get(String.valueOf(curDistId))==null?0:beOverdueNumMap.get(String.valueOf(curDistId));
			//4 表示无数据
			int noDataNum = noDataNumMap.get(String.valueOf(curDistId))==null?0:noDataNumMap.get(String.valueOf(curDistId));
			
			
			//学校总数
			schDishSitStat.setTotalSchNum(totalSchNum);
			//应排菜天数，即供餐天数
			schDishSitStat.setMealSchNum(totalDistSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			schDishSitStat.setNoDishSchNum(distNoDishSchNum);
			//已排菜学校
			schDishSitStat.setDishSchNum(distDishSchNum);
			//排菜率
			schDishSitStat.setDishRate(distDishRates[i]);
			
			//1 表示规范录入
			schDishSitStat.setStandardNum(standardNum);
			//2 表示补录
			schDishSitStat.setSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishSitStat.setBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishSitStat.setNoDataNum(noDataNum);
			
			standardRate = 0;
			if(totalDistSchNum > 0) {
				standardRate = 100 * ((float) schDishSitStat.getStandardNum() / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			schDishSitStat.setStandardRate(standardRate);
			
			schDishSitStatMap.put(curTdd.getName(), schDishSitStat);
		}
	}

	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getDishInfoByNatureFromHive(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,
			Map<String,SchDishSitStat> schDishSitStatMap,RedisService redisService,DbHiveDishService dbHiveDishService) {
		int[] schOwnTypes = { 0, 2,3, 4};
		int distCount = schoolPropertyMap.size();
		float[] distDishRates = new float[distCount];
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
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null,departmentId,null, 1);
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
				
				if(schDishCommon.getPlatoonDealStatus() !=null && schDishCommon.getPlatoonDealStatus() != -1 ) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if(schDishCommon.getPlatoonDealStatus() == 1) {
						standardNumMap.put(schDishCommon.getSchoolNatureName(), 
								(standardNumMap.get(schDishCommon.getSchoolNatureName())==null?0:standardNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 2) {
						supplementNumMap.put(schDishCommon.getSchoolNatureName(), 
								(supplementNumMap.get(schDishCommon.getSchoolNatureName())==null?0:supplementNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 3) {
						beOverdueNumMap.put(schDishCommon.getSchoolNatureName(), 
								(beOverdueNumMap.get(schDishCommon.getSchoolNatureName())==null?0:beOverdueNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 4) {
						noDataNumMap.put(schDishCommon.getSchoolNatureName(), 
								(noDataNumMap.get(schDishCommon.getSchoolNatureName())==null?0:noDataNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getTotal());
					}
				}
			}
		}
		
		float standardRate = 0;
		for (int i = 0; i < distCount; i++) {
			int curDistId = schOwnTypes[i];
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(schoolPropertyMap.get(curDistId));
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//区域名称
				schDishSitStat.setStatPropName(schoolPropertyMap.get(curDistId));
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
			
			

			//1 表示规范录入
			int standardNum = standardNumMap.get(String.valueOf(curDistId))==null?0:standardNumMap.get(String.valueOf(curDistId));
			//2 表示补录
			int supplementNum = supplementNumMap.get(String.valueOf(curDistId))==null?0:supplementNumMap.get(String.valueOf(curDistId));
			//3 表示逾期补录
			int beOverdueNum = beOverdueNumMap.get(String.valueOf(curDistId))==null?0:beOverdueNumMap.get(String.valueOf(curDistId));
			//4 表示无数据
			int noDataNum = noDataNumMap.get(String.valueOf(curDistId))==null?0:noDataNumMap.get(String.valueOf(curDistId));
			
			//学校个数
			schDishSitStat.setTotalSchNum(totalSchNum);
			//应排菜天数，即供餐天数
			schDishSitStat.setMealSchNum(totalDistSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			schDishSitStat.setNoDishSchNum(distNoDishSchNum);
			//已排菜学校
			schDishSitStat.setDishSchNum(distDishSchNum);
			//排菜率
			schDishSitStat.setDishRate(distDishRates[i]);
			
			//1 表示规范录入
			schDishSitStat.setStandardNum(standardNum);
			//2 表示补录
			schDishSitStat.setSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishSitStat.setBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishSitStat.setNoDataNum(noDataNum);
			
			standardRate = 0;
			if(totalDistSchNum > 0) {
				standardRate = 100 * ((float) schDishSitStat.getStandardNum() / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			schDishSitStat.setStandardRate(standardRate);
			
			schDishSitStatMap.put(schoolPropertyMap.get(curDistId), schDishSitStat);
		}
		
		
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getDishInfoByNature(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,
			Map<String,SchDishSitStat> schDishSitStatMap,RedisService redisService) {
		String key = "";
		String keyVal = "";
		int[] schOwnTypes = { 0, 2,3, 4};
		
		int distCount = schoolPropertyMap.size();
		int dateCount = dates.length;
		int[][]totalMealSchNums = new int[dateCount][distCount];
		int[][]distDishSchNums = new int[dateCount][distCount];
		int[][]distNoDishSchNums = new int[dateCount][distCount];
		float[] distDishRates = new float[distCount];
		
		int[][] standardNums = new int[dateCount][distCount];
		int[][] supplementNums = new int[dateCount][distCount];
		int[][] beOverdueNums = new int[dateCount][distCount];
		int[][] noDataNums = new int[dateCount][distCount];
		
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]  + DataKeyConfig.platoonfeedTotal;
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
				
				int standardNum = 0;
				int supplementNum = 0;
				int beOverdueNum = 0;
				int noDataNum = 0;
				
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
						
						
						//操作状态
						keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
								SpringConfig.RedisDBIdx, key, filedPre+ "nature_"+propertyId+"_nature-sub_"+entrySon.getKey()+"_plastatus_1");
						if(keyVal!=null && Integer.parseInt(keyVal)>0) {
							standardNum += Integer.parseInt(keyVal);
						}
						
						keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
								SpringConfig.RedisDBIdx, key, filedPre+ "nature_"+propertyId+"_nature-sub_"+entrySon.getKey()+"_plastatus_2");
						if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
							supplementNum += Integer.parseInt(keyVal);
						}
						
						keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
								SpringConfig.RedisDBIdx, key, filedPre+ "nature_"+propertyId+"_nature-sub_"+entrySon.getKey()+"_plastatus_3");
						if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
							beOverdueNum += Integer.parseInt(keyVal);
						}
						
						keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
								SpringConfig.RedisDBIdx, key, filedPre+ "nature_"+propertyId+"_nature-sub_"+entrySon.getKey()+"_plastatus_4");
						if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
							noDataNum += Integer.parseInt(keyVal);
						}
						
						if(4==propertyId) {
							//如果是4：其他，则将shanghai-nature_null_nature-sub_null数据同样归为其他
							keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
									SpringConfig.RedisDBIdx, key, filedPre+ "nature_null_nature-sub_"+entrySon.getKey()+"_plastatus_1");
							if(keyVal!=null && Integer.parseInt(keyVal)>0) {
								standardNum += Integer.parseInt(keyVal);
							}
							
							keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
									SpringConfig.RedisDBIdx, key, filedPre+ "nature_null_nature-sub_"+entrySon.getKey()+"_plastatus_2");
							if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
								supplementNum += Integer.parseInt(keyVal);
							}
							
							keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
									SpringConfig.RedisDBIdx, key, filedPre+ "nature_null_nature-sub_"+entrySon.getKey()+"_plastatus_3");
							if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
								beOverdueNum += Integer.parseInt(keyVal);
							}
							
							keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
									SpringConfig.RedisDBIdx, key, filedPre+ "nature_null_nature-sub_"+entrySon.getKey()+"_plastatus_4");
							if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
								noDataNum += Integer.parseInt(keyVal);
							}
						}
					}
					
					
				//}
				totalMealSchNums[k][i] += mealSchNum;
				distDishSchNums[k][i] += dishSchNum;
				distNoDishSchNums[k][i] += noDishSchNum;
				
				standardNums[k][i] += standardNum;
				supplementNums[k][i] += supplementNum;
				beOverdueNums[k][i] += beOverdueNum;
				noDataNums[k][i] += noDataNum;
			}
			
		}
		
		float standardRate = 0;
		for (int i = 0; i < distCount; i++) {
			int curDistId = schOwnTypes[i];
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(schoolPropertyMap.get(curDistId));
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//区域名称
				schDishSitStat.setStatPropName(schoolPropertyMap.get(curDistId));
			}
			
			int totalDistSchNum = 0;
			int distDishSchNum = 0;
			int distNoDishSchNum = 0;
			
			int standardNum = 0;
			int supplementNum = 0;
			int beOverdueNum = 0;
			int noDataNum = 0;
			
			for (int k = 0; k < dates.length; k++) {
				totalDistSchNum += totalMealSchNums[k][i];
				distDishSchNum += distDishSchNums[k][i];
				distNoDishSchNum += distNoDishSchNums[k][i];
				

				standardNum += standardNums[k][i];
				supplementNum += supplementNums[k][i];
				beOverdueNum += beOverdueNums[k][i];
				noDataNum += noDataNums[k][i];
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
			schDishSitStat.setMealSchNum(totalDistSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			schDishSitStat.setNoDishSchNum(distNoDishSchNum);
			//已排菜学校
			schDishSitStat.setDishSchNum(distDishSchNum);
			//排菜率
			schDishSitStat.setDishRate(distDishRates[i]);
			

			//1 表示规范录入
			schDishSitStat.setStandardNum(standardNum);
			//2 表示补录
			schDishSitStat.setSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishSitStat.setBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishSitStat.setNoDataNum(noDataNum);
			
			standardRate = 0;
			if(totalDistSchNum > 0) {
				standardRate = 100 * ((float) schDishSitStat.getStandardNum() / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			schDishSitStat.setStandardRate(standardRate);
			
			schDishSitStatMap.put(schoolPropertyMap.get(curDistId), schDishSitStat);
		}
		
		
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)(按学校类型统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getDishInfoBySchoolTypeFromHive(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,
			Map<String,SchDishSitStat> schDishSitStatMap,RedisService redisService,DbHiveDishService dbHiveDishService) {
		//String fieldPrefix = "";
		// 当天排菜学校总数
		//Map<String, String> platoonFeedTotalMap = null;
		//List<Integer> list = new ArrayList<Integer>();
		
		int[] schOwnTypes = { 0,1,2, 3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
		
		int distCount = schoolPropertyMap.size();
		float[] distDishRates = new float[distCount];
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
		dishList = dbHiveDishService.getDishList(DataKeyConfig.talbePlatoonTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
				-1, -1, null, null, departmentId,null,2);
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
				if(schDishCommon.getPlatoonDealStatus() !=null && schDishCommon.getPlatoonDealStatus() != -1 ) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if(schDishCommon.getPlatoonDealStatus() == 1) {
						standardNumMap.put(schDishCommon.getLevelName(), 
								(standardNumMap.get(schDishCommon.getLevelName())==null?0:standardNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 2) {
						supplementNumMap.put(schDishCommon.getLevelName(), 
								(supplementNumMap.get(schDishCommon.getLevelName())==null?0:supplementNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 3) {
						beOverdueNumMap.put(schDishCommon.getLevelName(), 
								(beOverdueNumMap.get(schDishCommon.getLevelName())==null?0:beOverdueNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 4) {
						noDataNumMap.put(schDishCommon.getLevelName(), 
								(noDataNumMap.get(schDishCommon.getLevelName())==null?0:noDataNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getTotal());
					}
				}
			}
		}
		
		float standardRate = 0;
		for (int i = 0; i < distCount; i++) {
			int curDistId = schOwnTypes[i];
			
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(schoolPropertyMap.get(curDistId));
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//学校性质名称
				schDishSitStat.setStatPropName(schoolPropertyMap.get(curDistId));
				//
				schDishSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(schDishSitStat.getStatPropName()));
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
			
			//1 表示规范录入
			int standardNum = standardNumMap.get(String.valueOf(curDistId))==null?0:standardNumMap.get(String.valueOf(curDistId));
			//2 表示补录
			int supplementNum = supplementNumMap.get(String.valueOf(curDistId))==null?0:supplementNumMap.get(String.valueOf(curDistId));
			//3 表示逾期补录
			int beOverdueNum = beOverdueNumMap.get(String.valueOf(curDistId))==null?0:beOverdueNumMap.get(String.valueOf(curDistId));
			//4 表示无数据
			int noDataNum = noDataNumMap.get(String.valueOf(curDistId))==null?0:noDataNumMap.get(String.valueOf(curDistId));
			
			//学校个数
			schDishSitStat.setTotalSchNum(totalSchNum);
			//应排菜天数，即供餐天数
			schDishSitStat.setMealSchNum(totalDistSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			schDishSitStat.setNoDishSchNum(distNoDishSchNum);
			//已排菜学校
			schDishSitStat.setDishSchNum(distDishSchNum);
			//排菜率
			schDishSitStat.setDishRate(distDishRates[i]);
			
			//1 表示规范录入
			schDishSitStat.setStandardNum(standardNum);
			//2 表示补录
			schDishSitStat.setSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishSitStat.setBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishSitStat.setNoDataNum(noDataNum);
			
			standardRate = 0;
			if(totalDistSchNum > 0) {
				standardRate = 100 * ((float) schDishSitStat.getStandardNum() / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			schDishSitStat.setStandardRate(standardRate);
			
			schDishSitStatMap.put(schoolPropertyMap.get(curDistId), schDishSitStat);
		}
		
		
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)(按学校类型统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getDishInfoBySchoolType(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,
			Map<String,SchDishSitStat> schDishSitStatMap,RedisService redisService) {
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
		
		int[][] standardNums = new int[dateCount][distCount];
		int[][] supplementNums = new int[dateCount][distCount];
		int[][] beOverdueNums = new int[dateCount][distCount];
		int[][] noDataNums = new int[dateCount][distCount];
		
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]  + DataKeyConfig.platoonfeedTotal;
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
				
				
				//操作状态
				int standardNum = 0;
				int supplementNum = 0;
				int beOverdueNum = 0;
				int noDataNum = 0;
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, filedPre+ "level_"+propertyId+"_plastatus_1");
				if(keyVal!=null && Integer.parseInt(keyVal)>0) {
					standardNum += Integer.parseInt(keyVal);
				}
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, filedPre+ "level_"+propertyId+"_plastatus_2");
				if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
					supplementNum += Integer.parseInt(keyVal);
				}
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, filedPre+ "level_"+propertyId+"_plastatus_3");
				if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
					beOverdueNum += Integer.parseInt(keyVal);
				}
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, filedPre+ "level_"+propertyId+"_plastatus_4");
				if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
					noDataNum += Integer.parseInt(keyVal);
				}
				
				standardNums[k][i] += standardNum;
				supplementNums[k][i] += supplementNum;
				beOverdueNums[k][i] += beOverdueNum;
				noDataNums[k][i] += noDataNum;
			}
			
		}
		
		float standardRate = 0;
		for (int i = 0; i < distCount; i++) {
			int curDistId = schOwnTypes[i];
			
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(schoolPropertyMap.get(curDistId));
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//学校性质名称
				schDishSitStat.setStatPropName(schoolPropertyMap.get(curDistId));
				//
				schDishSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(schDishSitStat.getStatPropName()));
			}
			
			int totalDistSchNum = 0;
			int distDishSchNum = 0;
			int distNoDishSchNum = 0;
			
			int standardNum = 0;
			int supplementNum = 0;
			int beOverdueNum = 0;
			int noDataNum = 0;
			
			for (int k = 0; k < dates.length; k++) {
				totalDistSchNum += totalMealSchNums[k][i];
				distDishSchNum += distDishSchNums[k][i];
				distNoDishSchNum += distNoDishSchNums[k][i];
				
				standardNum += standardNums[k][i];
				supplementNum += supplementNums[k][i];
				beOverdueNum += beOverdueNums[k][i];
				noDataNum += noDataNums[k][i];
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
			schDishSitStat.setMealSchNum(totalDistSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			schDishSitStat.setNoDishSchNum(distNoDishSchNum);
			//已排菜学校
			schDishSitStat.setDishSchNum(distDishSchNum);
			//排菜率
			schDishSitStat.setDishRate(distDishRates[i]);

			//1 表示规范录入
			schDishSitStat.setStandardNum(standardNum);
			//2 表示补录
			schDishSitStat.setSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishSitStat.setBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishSitStat.setNoDataNum(noDataNum);
			
			standardRate = 0;
			if(totalDistSchNum > 0) {
				standardRate = 100 * ((float) schDishSitStat.getStandardNum() / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			schDishSitStat.setStandardRate(standardRate);
			
			schDishSitStatMap.put(schoolPropertyMap.get(curDistId), schDishSitStat);
		}
		
		
	}
	
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)(按学校管理部门统计)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getDishInfoByDepartment(String departmentId, String[] dates,Map<Integer, String> schoolDepartmentMap,int [] schOwnTypes,
			Map<String,SchDishSitStat> schDishSitStatMap,RedisService redisService) {
		String key = "";
		String keyVal = "";
		//String fieldPrefix = "";
		// 当天排菜学校总数
		//Map<String, String> platoonFeedTotalMap = null;
		//List<Integer> list = new ArrayList<Integer>();
		/*for(Map.Entry<Integer, String>) {
			
		}*/
		int distCount = schoolDepartmentMap.size();
		int dateCount = dates.length;
		int[][]totalMealSchNums = new int[dateCount][distCount];
		int[][]distDishSchNums = new int[dateCount][distCount];
		int[][]distNoDishSchNums = new int[dateCount][distCount];
		float[] distDishRates = new float[distCount];
		

		int[][] standardNums = new int[dateCount][distCount];
		int[][] supplementNums = new int[dateCount][distCount];
		int[][] beOverdueNums = new int[dateCount][distCount];
		int[][] noDataNums = new int[dateCount][distCount];
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]  + DataKeyConfig.platoonfeedTotal;
			//如果是管理部门账号，则取管理部门账号的key
			if(CommonUtil.isNotEmpty(departmentId)) {
				key = dates[k] + DataKeyConfig.departmentPlatoonfeedTotal+departmentId;
			}
			
			// 供餐学校已排菜和未排菜数
			//int dishSchNumTotal = 0;
			//int noDishSchNumTotal = 0;
			for (int i = 0; i<schOwnTypes.length ; i++) {	
				int propertyId = schOwnTypes[i];
				/*String filedPre = "";
				//如果区域不为空
				if(StringUtils.isNotEmpty(distId)) {
					filedPre ="area_"+distId+"_";
				}*/
				
				Integer dishSchNum = 0;
				int mealSchNum = 0;
				int noDishSchNum = 0;
				
				//指定学校性质的已排菜学校个数
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key,"department_"+propertyId+"_供餐_已排菜");
				if(keyVal!=null && Integer.parseInt(keyVal)>0) {
					dishSchNum = Integer.parseInt(keyVal);
					mealSchNum += Integer.parseInt(keyVal);
				}
				
				//指定学校性质的未排菜学校个数
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, "department_"+propertyId+"_供餐_未排菜");
				if(keyVal!=null && Integer.parseInt(keyVal)>0) {
					noDishSchNum = Integer.parseInt(keyVal);
					mealSchNum += Integer.parseInt(keyVal);
				}
				
				totalMealSchNums[k][i] += mealSchNum;
				distDishSchNums[k][i] += dishSchNum;
				distNoDishSchNums[k][i] += noDishSchNum;
				
				
				//操作状态
				int standardNum = 0;
				int supplementNum = 0;
				int beOverdueNum = 0;
				int noDataNum = 0;
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, "department_"+propertyId+"_plastatus_1");
				if(keyVal!=null && Integer.parseInt(keyVal)>0) {
					standardNum += Integer.parseInt(keyVal);
				}
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, "department_"+propertyId+"_plastatus_2");
				if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
					supplementNum += Integer.parseInt(keyVal);
				}
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, "department_"+propertyId+"_plastatus_3");
				if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
					beOverdueNum += Integer.parseInt(keyVal);
				}
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, "department_"+propertyId+"_plastatus_4");
				if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
					noDataNum += Integer.parseInt(keyVal);
				}
				
				standardNums[k][i] += standardNum;
				supplementNums[k][i] += supplementNum;
				beOverdueNums[k][i] += beOverdueNum;
				noDataNums[k][i] += noDataNum;
			}
			
		}
		
		float standardRate = 0;
		for (int i = 0; i < distCount; i++) {
			int curDistId = schOwnTypes[i];
			
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(schoolDepartmentMap.get(curDistId));
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//学校性质名称
				schDishSitStat.setStatPropName(schoolDepartmentMap.get(curDistId));
				//
				schDishSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(schDishSitStat.getStatPropName()));
			}
			
			int totalDistSchNum = 0;
			int distDishSchNum = 0;
			int distNoDishSchNum = 0;
			
			int standardNum = 0;
			int supplementNum = 0;
			int beOverdueNum = 0;
			int noDataNum = 0;
			
			for (int k = 0; k < dates.length; k++) {
				totalDistSchNum += totalMealSchNums[k][i];
				distDishSchNum += distDishSchNums[k][i];
				distNoDishSchNum += distNoDishSchNums[k][i];
				
				standardNum += standardNums[k][i];
				supplementNum += supplementNums[k][i];
				beOverdueNum += beOverdueNums[k][i];
				noDataNum += noDataNums[k][i];
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
			schDishSitStat.setMealSchNum(totalDistSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			schDishSitStat.setNoDishSchNum(distNoDishSchNum);
			//已排菜学校
			schDishSitStat.setDishSchNum(distDishSchNum);
			//排菜率
			schDishSitStat.setDishRate(distDishRates[i]);
			
			//1 表示规范录入
			schDishSitStat.setStandardNum(standardNum);
			//2 表示补录
			schDishSitStat.setSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishSitStat.setBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishSitStat.setNoDataNum(noDataNum);
			
			standardRate = 0;
			if(totalDistSchNum > 0) {
				standardRate = 100 * ((float) schDishSitStat.getStandardNum() / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			schDishSitStat.setStandardRate(standardRate);
			
			schDishSitStatMap.put(schoolDepartmentMap.get(curDistId), schDishSitStat);
		}
		
		
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getDishInfoByDepartmentFromHive(String departmentId,String[] dates,Map<Integer, String> schoolDepartmentMap,
			int [] schOwnTypes,
			Map<String,SchDishSitStat> schDishSitStatMap,RedisService redisService,DbHiveDishService dbHiveDishService) {
		int distCount = schoolDepartmentMap.size();
		float[] distDishRates = new float[distCount];
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
					}
				}
				
				if(schDishCommon.getPlatoonDealStatus() !=null && schDishCommon.getPlatoonDealStatus() != -1 ) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if(schDishCommon.getPlatoonDealStatus() == 1) {
						standardNumMap.put(schDishCommon.getDepartmentId(), 
								(standardNumMap.get(schDishCommon.getDepartmentId())==null?0:standardNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 2) {
						supplementNumMap.put(schDishCommon.getDepartmentId(), 
								(supplementNumMap.get(schDishCommon.getDepartmentId())==null?0:supplementNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 3) {
						beOverdueNumMap.put(schDishCommon.getDepartmentId(), 
								(beOverdueNumMap.get(schDishCommon.getDepartmentId())==null?0:beOverdueNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 4) {
						noDataNumMap.put(schDishCommon.getDepartmentId(), 
								(noDataNumMap.get(schDishCommon.getDepartmentId())==null?0:noDataNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getTotal());
					}
				}
			}
		}
		
		float standardRate = 0;
		for (int i = 0; i < distCount; i++) {
			int curDistId = schOwnTypes[i];
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(schoolDepartmentMap.get(curDistId));
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//区域名称
				schDishSitStat.setStatPropName(schoolDepartmentMap.get(curDistId));
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
			
			

			//1 表示规范录入
			int standardNum = standardNumMap.get(String.valueOf(curDistId))==null?0:standardNumMap.get(String.valueOf(curDistId));
			//2 表示补录
			int supplementNum = supplementNumMap.get(String.valueOf(curDistId))==null?0:supplementNumMap.get(String.valueOf(curDistId));
			//3 表示逾期补录
			int beOverdueNum = beOverdueNumMap.get(String.valueOf(curDistId))==null?0:beOverdueNumMap.get(String.valueOf(curDistId));
			//4 表示无数据
			int noDataNum = noDataNumMap.get(String.valueOf(curDistId))==null?0:noDataNumMap.get(String.valueOf(curDistId));
			
			//学校个数
			schDishSitStat.setTotalSchNum(totalSchNum);
			//应排菜天数，即供餐天数
			schDishSitStat.setMealSchNum(totalDistSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			schDishSitStat.setNoDishSchNum(distNoDishSchNum);
			//已排菜学校
			schDishSitStat.setDishSchNum(distDishSchNum);
			//排菜率
			schDishSitStat.setDishRate(distDishRates[i]);
			
			//1 表示规范录入
			schDishSitStat.setStandardNum(standardNum);
			//2 表示补录
			schDishSitStat.setSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishSitStat.setBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishSitStat.setNoDataNum(noDataNum);
			
			standardRate = 0;
			if(totalDistSchNum > 0) {
				standardRate = 100 * ((float) schDishSitStat.getStandardNum() / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			schDishSitStat.setStandardRate(standardRate);
			
			schDishSitStatMap.put(schoolDepartmentMap.get(curDistId), schDishSitStat);
		}
		
		
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)(按学校所属主管部门)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getDishInfoBySlaveHive(String departmentId,String distId, String[] dates,Map<String, String> slaveMap,
			Map<String,SchDishSitStat> schDishSitStatMap,RedisService redisService,DbHiveDishService dbHiveDishService) {
		// 当天排菜学校总数
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
				if(schDishCommon.getPlatoonDealStatus() !=null && schDishCommon.getPlatoonDealStatus() != -1 ) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if(schDishCommon.getPlatoonDealStatus() == 1) {
						standardNumMap.put(key, 
								(standardNumMap.get(key)==null?0:standardNumMap.get(key)) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 2) {
						supplementNumMap.put(key, 
								(supplementNumMap.get(key)==null?0:supplementNumMap.get(key)) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 3) {
						beOverdueNumMap.put(key, 
								(beOverdueNumMap.get(key)==null?0:beOverdueNumMap.get(key)) 
								+ schDishCommon.getTotal());
					}else if(schDishCommon.getPlatoonDealStatus() == 4) {
						noDataNumMap.put(key, 
								(noDataNumMap.get(key)==null?0:noDataNumMap.get(key)) 
								+ schDishCommon.getTotal());
					}
				}
			}
		}
		
		float distDishRate = 0;
		float standardRate = 0;
		for (Map.Entry<String, String> entry : slaveMap.entrySet()) {
			distDishRate = 0;
			String[] keys = entry.getKey().split("_");
			String masterid = keys[0];
			String slave = keys[1];
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(entry.getKey());
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				schDishSitStat.setStatClassName(entry.getValue());
				//区域名称
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				schDishSitStat.setStatPropName(slaveName);
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
			
			//1 表示规范录入
			int standardNum = standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey);
			//2 表示补录
			int supplementNum = supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey);
			//3 表示逾期补录
			int beOverdueNum = beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey);
			//4 表示无数据
			int noDataNum = noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey);
			
			
			//应排菜天数，即供餐天数
			schDishSitStat.setTotalSchNum(totalSchNum);
			//应排菜天数，即供餐天数
			schDishSitStat.setMealSchNum(totalDistSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			schDishSitStat.setNoDishSchNum(distNoDishSchNum);
			//已排菜学校
			schDishSitStat.setDishSchNum(distDishSchNum);
			//排菜率
			schDishSitStat.setDishRate(distDishRate);
			
			//1 表示规范录入
			schDishSitStat.setStandardNum(standardNum);
			//2 表示补录
			schDishSitStat.setSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishSitStat.setBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishSitStat.setNoDataNum(noDataNum);
			
			standardRate = 0;
			if(totalDistSchNum > 0) {
				standardRate = 100 * ((float) schDishSitStat.getStandardNum() / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			schDishSitStat.setStandardRate(standardRate);
			
			schDishSitStatMap.put(entry.getKey(), schDishSitStat);
		}
		
		
	}
	
	/**
	 * 获取排菜数据(未排菜学校、应排菜天数、未排菜天数、排菜率)(按学校所属主管部门)
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @return
	 */
	public static void getDishInfoBySlave(String departmentId,String distId, String[] dates,Map<String, String> slaveMap,
			Map<String,SchDishSitStat> schDishSitStatMap,RedisService redisService) {
		String key = "";
		String keyVal = "";
		// 当天排菜学校总数
		Map<String,Integer> totalMealSchNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distDishSchNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distNoDishSchNumMap = new HashMap<String,Integer>();
		
		Map<String,Integer> standardNumMap = new HashMap<String,Integer>();
		Map<String,Integer> supplementNumMap = new HashMap<String,Integer>();
		Map<String,Integer> beOverdueNumMap = new HashMap<String,Integer>();
		Map<String,Integer> noDataNumMap = new HashMap<String,Integer>();
		// 当天各区排菜学校数量
		for (int k = 0; k < dates.length; k++) {
			//供餐学校数量
			key = dates[k]  + DataKeyConfig.platoonfeedTotal;
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
				
				
				//操作状态
				int standardNum = 0;
				int supplementNum = 0;
				int beOverdueNum = 0;
				int noDataNum = 0;
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, "masterid_"+masterid+"_slave_"+slave+"_plastatus_1");
				if(keyVal!=null && Integer.parseInt(keyVal)>0) {
					standardNum += Integer.parseInt(keyVal);
				}
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, "masterid_"+masterid+"_slave_"+slave+"_plastatus_2");
				if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
					supplementNum += Integer.parseInt(keyVal);
				}
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, "masterid_"+masterid+"_slave_"+slave+"_plastatus_3");
				if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
					beOverdueNum += Integer.parseInt(keyVal);
				}
				
				keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, 
						SpringConfig.RedisDBIdx, key, "masterid_"+masterid+"_slave_"+slave+"_plastatus_4");
				if(keyVal!=null && Integer.parseInt(keyVal) > 0) {
					noDataNum += Integer.parseInt(keyVal);
				}
				
				standardNumMap.put(entry.getKey(), (standardNumMap.get(entry.getKey())==null?0:standardNumMap.get(entry.getKey()))+standardNum);
				supplementNumMap.put(entry.getKey(), (supplementNumMap.get(entry.getKey())==null?0:supplementNumMap.get(entry.getKey()))+supplementNum);
				beOverdueNumMap.put(entry.getKey(), (beOverdueNumMap.get(entry.getKey())==null?0:beOverdueNumMap.get(entry.getKey()))+beOverdueNum);
				noDataNumMap.put(entry.getKey(), (noDataNumMap.get(entry.getKey())==null?0:noDataNumMap.get(entry.getKey()))+noDataNum);
			}
			
		}
		
		float distDishRate = 0;
		float standardRate = 0;
		for (Map.Entry<String, String> entry : slaveMap.entrySet()) {
			distDishRate = 0;
			String[] keys = entry.getKey().split("_");
			String masterid = keys[0];
			String slave = keys[1];
			
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(entry.getKey());
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				schDishSitStat.setStatClassName(entry.getValue());
				//区域名称
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				schDishSitStat.setStatPropName(slaveName);
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
			
			
			int standardNum =  standardNumMap.get(entry.getKey());
			int supplementNum = supplementNumMap.get(entry.getKey());
			int beOverdueNum = beOverdueNumMap.get(entry.getKey());
			int noDataNum = noDataNumMap.get(entry.getKey());
			
			//应排菜天数，即供餐天数
			schDishSitStat.setMealSchNum(totalDistSchNum);
			//未排菜学校数（目前和委排菜天数一样，一个需要一天只会排一次菜）
			schDishSitStat.setNoDishSchNum(distNoDishSchNum);
			//已排菜学校
			schDishSitStat.setDishSchNum(distDishSchNum);
			//排菜率
			schDishSitStat.setDishRate(distDishRate);
			
			//1 表示规范录入
			schDishSitStat.setStandardNum(standardNum);
			//2 表示补录
			schDishSitStat.setSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishSitStat.setBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishSitStat.setNoDataNum(noDataNum);
			
			standardRate = 0;
			if(totalDistSchNum > 0) {
				standardRate = 100 * ((float) schDishSitStat.getStandardNum() / (float) totalDistSchNum);
				BigDecimal bd = new BigDecimal(standardRate);
				standardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (standardRate > 100)
					standardRate = 100;
			}
			schDishSitStat.setStandardRate(standardRate);
			
			schDishSitStatMap.put(entry.getKey(), schDishSitStat);
		}
		
		
	}
	/**
	 * 获取监管学校总数
	 * @param distId
	 * @return
	 */
	private void getSupSchNumByArea(String departmentId,String distId,String currDistName, List<TEduDistrictDo> tedList,Map<String,SchDishSitStat> schDishSitStatMap) {
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
					
					SchDishSitStat schDishSitStat = schDishSitStatMap.get(curTdd.getName());
					if(schDishSitStat==null) {
						schDishSitStat = new SchDishSitStat();
						//区域名称
						schDishSitStat.setStatPropName(curTdd.getName());
					}
					//学校总数
					schDishSitStat.setTotalSchNum(supSchNum);
					//sumSupSchNum +=supSchNum;
					schDishSitStatMap.put(curTdd.getName(), schDishSitStat);
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
				SchDishSitStat schDishSitStat = schDishSitStatMap.get(currDistName);
				if(schDishSitStat==null) {
					schDishSitStat = new SchDishSitStat();
					//区域名称
					schDishSitStat.setStatPropName(currDistName);
					
				}
				
				//学校总数
				schDishSitStat.setTotalSchNum(supSchNum);
				schDishSitStatMap.put(currDistName, schDishSitStat);
			}
		}
		
		/**
		 * 合计
		 */
		/*SchDishSitStat schDishSitStat = schDishSitStatMap.get("合计");
		if(schDishSitStat==null) {
			schDishSitStat = new SchDishSitStat();
			//区域名称
			schDishSitStat.setStatPropName("合计");
		}
		//学校总数
		schDishSitStat.setTotalSchNum(sumSupSchNum);*/
	}
	
	/**
	 * 获取监管学校总数（按学校性质统计）
	 * @param distId
	 * @return
	 */
	private void getSupSchNumByNature(String departmentId,String distId,String currDistName,Map<Integer, String> schoolPropertyMap,Map<String,SchDishSitStat> schDishSitStatMap) {
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
			
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(entry.getValue());
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//区域名称
				schDishSitStat.setStatPropName(entry.getValue());
				
			}
			//学校总数
			schDishSitStat.setTotalSchNum(supSchNum);
			schDishSitStatMap.put(entry.getValue(), schDishSitStat);
		}
		
	}
	
	/**
	 * 获取监管学校总数（按学校类型统计）
	 * @param distId
	 * @return
	 */
	private void getSupSchNumBySchoolType(String departmentId,String distId,String currDistName,Map<Integer, String> schoolPropertyMap,Map<String,SchDishSitStat> schDishSitStatMap) {
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
			
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(entry.getValue());
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//学校性质名称
				schDishSitStat.setStatPropName(entry.getValue());
				//
				schDishSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				
			}
			//学校总数
			schDishSitStat.setTotalSchNum(supSchNum);
			schDishSitStatMap.put(entry.getValue(), schDishSitStat);
		}
		
		
	}
	
	/**
	 * 获取监管学校总数（按学校管理部门统计）
	 * @param distId
	 * @return
	 */
	private void getSupSchNumByDepartment(String departmentId, Map<Integer, String> departmentMap,Map<String,SchDishSitStat> schDishSitStatMap) {
		Integer supSchNum = 0;
		String key = DataKeyConfig.schoolData;
		//如果是管理部门账号，则取管理部门账号的key
		if(CommonUtil.isNotEmpty(departmentId)) {
			key = DataKeyConfig.areaSchoolData+departmentId;
		}
		
		String keyVal = null;
		for (Map.Entry<Integer, String> entry : departmentMap.entrySet()) {
			key = DataKeyConfig.areaSchoolData+entry.getKey();
		    keyVal = redisService.getHashByKeyField(SpringConfig.RedisConnPool.REDISCLUSTER1.value, SpringConfig.RedisDBIdx, key, "shanghai");
			supSchNum = 0;
			if(keyVal != null && Integer.parseInt(keyVal)>0) {
				supSchNum = Integer.parseInt(keyVal);
			}
			
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(entry.getValue());
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				//学校性质名称
				schDishSitStat.setStatPropName(entry.getValue());
				//
				//schDishSitStat.setStatClassName(entry.getValue());
				
			}
			//学校总数
			schDishSitStat.setTotalSchNum(supSchNum);
			schDishSitStatMap.put(entry.getValue(), schDishSitStat);
		}
		
		
	}
	
	/**
	 * 获取监管学校总数（按学校主管部门）
	 * @param distId
	 * @return
	 */
	private void getSupSchNumBySchoolSlave(String departmentId,String distId,String currDistName,Map<String, String> slaveMap,Map<String,SchDishSitStat> schDishSitStatMap) {
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
			
			SchDishSitStat schDishSitStat = schDishSitStatMap.get(entry.getKey());
			if(schDishSitStat==null) {
				schDishSitStat = new SchDishSitStat();
				schDishSitStat.setStatClassName(entry.getValue());
				//区域名称
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				schDishSitStat.setStatPropName(slaveName);
			}
			//学校总数
			schDishSitStat.setTotalSchNum(supSchNum);
			schDishSitStatMap.put(entry.getKey(), schDishSitStat);
		}
		
		
	}
	
	/**
	 * 投诉举报详情模型函数
	 * @param crId
	 * @param distName
	 * @param prefCity
	 * @param province
	 * @param startDate
	 * @param endDate
	 * @param db1Service
	 * @return
	 */
	public SchDishSitStatDTO appModFunc(String token, String distName, String prefCity, String province,String startDate, String endDate, Integer statMode,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,EduSchoolService eduSchoolService,DbHiveDishService dbHiveDishService) {
		
		SchDishSitStatDTO schDishSitStatDTO = null;
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
					// 餐厨垃圾学校回收列表函数
					if(methodIndex==0) {
						schDishSitStatDTO = schDishSitStatFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						schDishSitStatDTO = schDishSitStatFuncOne(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates, tedList,
								db1Service, saasService,eduSchoolService,dbHiveDishService);
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
						schDishSitStatDTO = schDishSitStatFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						schDishSitStatDTO = schDishSitStatFuncOne(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates, tedList,
								db1Service, saasService,eduSchoolService,dbHiveDishService);
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
			schDishSitStatDTO = new SchDishSitStatDTO();
		}		

		
		return schDishSitStatDTO;
	}
	
}
