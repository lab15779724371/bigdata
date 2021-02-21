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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.EduSchool;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchConMatSitStat;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsAcceptSitStat;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsAcceptSitStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsCommon;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 3.2.33.	学校配货验收情况统计用模型
 * @author fengyang_xie
 *
 */
public class SchGsAcceptSitStatAppMod {
	private static final Logger logger = LogManager.getLogger(SchGsAcceptSitStatAppMod.class.getName());
	
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
			"   \"schGsAcceptSitStat\": [\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"  \"statPropName\":\"浦东新区\",\r\n" + 
			"   \"dishSchNum\":55,\r\n" + 
			"   \"conMatSchNum\":55,\r\n" + 
			"\"noAcceptSchNum\":5,\r\n" + 
			"\"acceptSchNum\":50,\r\n" + 
			"\"totalGsPlanNum\":55,\r\n" + 
			"\"acceptGsNum\":30,\r\n" + 
			"\"noAcceptGsNum\":25,\r\n" + 
			"\"acceptRate\":36.33\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"  \"statPropName\":\"黄浦区\",\r\n" + 
			"   \"dishSchNum\":55,\r\n" + 
			"   \"conMatSchNum\":55,\r\n" + 
			"\"noAcceptSchNum\":5,\r\n" + 
			"\"acceptSchNum\":50,\r\n" + 
			"\"totalGsPlanNum\":55,\r\n" + 
			"\"acceptGsNum\":30,\r\n" + 
			"\"noAcceptGsNum\":25,\r\n" + 
			"\"acceptRate\":36.33\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"\"statPropName\":\"静安区\",\r\n" + 
			"   \"dishSchNum\":55,\r\n" + 
			"   \"conMatSchNum\":55,\r\n" + 
			"\"noAcceptSchNum\":5,\r\n" + 
			"\"acceptSchNum\":50,\r\n" + 
			"\"totalGsPlanNum\":55,\r\n" + 
			"\"acceptGsNum\":30,\r\n" + 
			"\"noAcceptGsNum\":25,\r\n" + 
			"\"acceptRate\":36.33\r\n" + 
			" } ],\r\n" + 
			"   \"msgId\":1\r\n" + 
			"}\r\n" + 
			"";
	
	/**
	 * 汇总数据
	 * @return
	 */
	private SchGsAcceptSitStatDTO schGsAcceptSitStatFunc(String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService ) {
		
		SchGsAcceptSitStatDTO schGsAcceptSitStatDTO = new SchGsAcceptSitStatDTO();
		
		List<SchGsAcceptSitStat> schGsAcceptSitStat = new ArrayList<SchGsAcceptSitStat>();
		schGsAcceptSitStatDTO.setSchGsAcceptSitStat(schGsAcceptSitStat);
		
		JSONObject jsStr = JSONObject.parseObject(tempData); 
		schGsAcceptSitStatDTO = (SchGsAcceptSitStatDTO) JSONObject.toJavaObject(jsStr,SchGsAcceptSitStatDTO.class);
		
		
		
		
		//时戳
		schGsAcceptSitStatDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		schGsAcceptSitStatDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return schGsAcceptSitStatDTO;
	}
	
	/**
	 * 汇总数据
	 * @return
	 */
	private SchGsAcceptSitStatDTO schGsAcceptSitStatFuncOne(String departmentId,String distId,String currDistName,Integer statMode,Integer subLevel,Integer compDep, 
			String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService,EduSchoolService eduSchoolService ) {
		
		SchGsAcceptSitStatDTO schGsAcceptSitStatDTO = new SchGsAcceptSitStatDTO();
		
		List<SchGsAcceptSitStat> schGsAcceptSitStatList = new ArrayList<SchGsAcceptSitStat>();
		
		
		Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap= new LinkedHashMap<String,SchGsAcceptSitStat>();
		
		
		if(statMode == 0) {
			//========0:按区统计
			
			/**
			 * 排菜汇总(已排菜学校)
			 */
			//2019.07.19注释，注释原因，目前界面不显示排菜相关数据，且排菜改为从hive库中获取后，查询速度回慢
			/*Map<String,SchDishSitStat> schDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			SchDishSitStatAppMod.getDishInfoByArea(distId, dates, tedList,schDishSitStatMap,redisService);
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = new SchGsAcceptSitStat();
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 用料汇总(已确认用料学校)
			 */
			Map<String,SchConMatSitStat> schConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			SchConMatSitStatAppMod.getConMatSitByArea(departmentId,distId, dates, tedList,schConMatSitStatMap,redisService);
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getKey());
				if(schGsAcceptSitStat==null) {
					schGsAcceptSitStat = new SchGsAcceptSitStat();
				}
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setConMatSchNum(entry.getValue().getConMatSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收配送单个数、验收率
			getAccDistrInfoByArea(departmentId,distId, dates, tedList,schGsAcceptSitStatMap);
			//获取配送信息：未验收学校
			getAcceptNoAccSchuNumByArea(distId, dates, tedList, schGsAcceptSitStatMap);
			
			
		}else if(statMode == 1) {
			//========1:按学校性质统计
			Map<Integer, String> schoolPropertyMap = new LinkedHashMap<Integer,String>();
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
				SchGsAcceptSitStat schGsAcceptSitStat = new SchGsAcceptSitStat();
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 用料汇总(已确认用料学校)
			 */
			Map<String,SchConMatSitStat> schConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			SchConMatSitStatAppMod.getConMatSitByNature(distId, dates, schoolPropertyMap, db1Service,saasService, schConMatSitStatMap, eduSchoolService,redisService);
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getKey());
				if(schGsAcceptSitStat==null) {
					schGsAcceptSitStat = new SchGsAcceptSitStat();
				}
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setConMatSchNum(entry.getValue().getConMatSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收学校、未验收配送单个数、验收率
			getAcceptNoAccSchuNumByNature(distId, dates, schoolPropertyMap, schGsAcceptSitStatMap,eduSchoolService);
		}else if (statMode == 2) {
			//========2:按学校学制统计
			
			/**
			 * 排菜汇总(已排菜学校)
			 */
			//2019.07.19注释，注释原因，目前界面不显示排菜相关数据，且排菜改为从hive库中获取后，查询速度回慢
			/*Map<String,SchDishSitStat> schDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			SchDishSitStatAppMod.getDishInfoBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap,schDishSitStatMap,redisService);
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = new SchGsAcceptSitStat();
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 用料汇总(已确认用料学校)
			 */
			Map<String,SchConMatSitStat> schConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			SchConMatSitStatAppMod.getConMatSitBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap, db1Service, saasService,schConMatSitStatMap, eduSchoolService,redisService);
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getKey());
				if(schGsAcceptSitStat==null) {
					schGsAcceptSitStat = new SchGsAcceptSitStat();
				}
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setConMatSchNum(entry.getValue().getConMatSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收学校、未验收配送单个数、验收率
			getAcceptNoAccSchuNumBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap, schGsAcceptSitStatMap,eduSchoolService);
			
			//计算每种【学制分类】的数量
			Map<String,SchGsAcceptSitStat> newSchGsAcceptSitStatMap= new LinkedHashMap<String,SchGsAcceptSitStat>();
			String statClassName="";
			SchGsAcceptSitStat schGsAcceptSitStatSum = new SchGsAcceptSitStat();
			for(Map.Entry<String,SchGsAcceptSitStat> entry : schGsAcceptSitStatMap.entrySet()) {
				
				
				
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newSchGsAcceptSitStatMap.put(statClassName+"_小计", schGsAcceptSitStatSum);
					schGsAcceptSitStatSum = new SchGsAcceptSitStat();
				}

				//计算数据
				setSchoolPropertSumData(schGsAcceptSitStatSum, entry);
				statClassName = entry.getValue().getStatClassName();
				schGsAcceptSitStatSum.setStatClassName(statClassName);
				schGsAcceptSitStatSum.setStatPropName("小计");
				setRate(schGsAcceptSitStatSum);
				
				newSchGsAcceptSitStatMap.put(entry.getKey(), entry.getValue());
				
			}
			newSchGsAcceptSitStatMap.put("其他"+"_小计", schGsAcceptSitStatSum);
			schGsAcceptSitStatMap = newSchGsAcceptSitStatMap;
			
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
				SchGsAcceptSitStat schGsAcceptSitStat = new SchGsAcceptSitStat();
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 用料汇总(已确认用料学校)
			 */
			Map<String,SchConMatSitStat> schConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			SchConMatSitStatAppMod.getConMatSitBySlave(departmentId,distId, dates, compDepNameToSubLevelNameMap,schConMatSitStatMap,redisService);
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getKey());
				if(schGsAcceptSitStat==null) {
					schGsAcceptSitStat = new SchGsAcceptSitStat();
				}
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setConMatSchNum(entry.getValue().getConMatSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收学校、未验收配送单个数、验收率
			getAcceptNoAccSchuNumBySlave(departmentId,distId, dates, compDepNameToSubLevelNameMap, schGsAcceptSitStatMap,eduSchoolService);
			
		    //计算每种【学制分类】的数量
			Map<String,SchGsAcceptSitStat> newSchGsAcceptSitStatMap= new LinkedHashMap<String,SchGsAcceptSitStat>();
			String statClassName="";
			SchGsAcceptSitStat schGsAcceptSitStatSum = new SchGsAcceptSitStat();
			for(Map.Entry<String,SchGsAcceptSitStat> entry : schGsAcceptSitStatMap.entrySet()) {
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newSchGsAcceptSitStatMap.put(statClassName+"_小计", schGsAcceptSitStatSum);
					schGsAcceptSitStatSum = new SchGsAcceptSitStat();
				}

				//计算数据
				setSchoolPropertSumData(schGsAcceptSitStatSum, entry);
				statClassName = entry.getValue().getStatClassName();	
				schGsAcceptSitStatSum.setStatClassName(statClassName);
				schGsAcceptSitStatSum.setStatPropName("小计");
				//计算比率：包括排菜率、验收率、留样率、预警处理率
                setRate(schGsAcceptSitStatSum);
				newSchGsAcceptSitStatMap.put(entry.getKey(), entry.getValue());
				
			}
			newSchGsAcceptSitStatMap.put("其他"+"_小计", schGsAcceptSitStatSum);
			schGsAcceptSitStatMap = newSchGsAcceptSitStatMap;
			
			//循环技术小计的百分比（包括排菜率、验收率、留样率、预警处理率、）
			for(Map.Entry<String,SchGsAcceptSitStat> entry : schGsAcceptSitStatMap.entrySet()) {
				if(entry.getKey().contains("小计")) {
					schGsAcceptSitStatSum = entry.getValue();
				}
			}
			
		}
		
		
		
		schGsAcceptSitStatMap.values();
		Collection<SchGsAcceptSitStat> valueCollection = schGsAcceptSitStatMap.values();
	    schGsAcceptSitStatList = new ArrayList<SchGsAcceptSitStat>(valueCollection);
		
		
		schGsAcceptSitStatDTO.setSchGsAcceptSitStat(schGsAcceptSitStatList);
		
		//时戳
		schGsAcceptSitStatDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		schGsAcceptSitStatDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return schGsAcceptSitStatDTO;
	}
	
	/**
	 * 汇总数据(有关学校部分获取Total)
	 * @return
	 */
	private SchGsAcceptSitStatDTO schGsAcceptSitStatFuncTwo(String departmentId,String distId,String currDistName,Integer statMode,Integer subLevel,Integer compDep, 
			String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService,EduSchoolService eduSchoolService,DbHiveGsService dbHiveGsService ) {
		
		SchGsAcceptSitStatDTO schGsAcceptSitStatDTO = new SchGsAcceptSitStatDTO();
		
		List<SchGsAcceptSitStat> schGsAcceptSitStatList = new ArrayList<SchGsAcceptSitStat>();
		
		
		Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap= new LinkedHashMap<String,SchGsAcceptSitStat>();
		
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
			SchDishSitStatAppMod.getDishInfoByArea(distId, dates, tedList,schDishSitStatMap,redisService);
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = new SchGsAcceptSitStat();
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 用料汇总(已确认用料学校)
			 */
			//2019-08-05注释，注释原因：目前界面不展示用料信息另外用料改为hive库读取，速度相对较慢，故暂时去除
			/*Map<String,SchConMatSitStat> schConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			SchConMatSitStatAppMod.getConMatSitByArea(distId, dates, tedList,schConMatSitStatMap,redisService);
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getKey());
				if(schGsAcceptSitStat==null) {
					schGsAcceptSitStat = new SchGsAcceptSitStat();
				}
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setConMatSchNum(entry.getValue().getConMatSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收配送单个数、验收率
			if(days >= 2) {
				getAccDistrInfoByAreaFromHive(departmentId,distId, dates, tedList, schGsAcceptSitStatMap, dbHiveGsService);
			}else {
				getAccDistrInfoByArea(departmentId,distId, dates, tedList,schGsAcceptSitStatMap);
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
				SchGsAcceptSitStat schGsAcceptSitStat = new SchGsAcceptSitStat();
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 用料汇总(已确认用料学校)
			 */
			//2019-08-05注释，注释原因：目前界面不展示用料信息另外用料改为hive库读取，速度相对较慢，故暂时去除
			/*Map<String,SchConMatSitStat> schConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			SchConMatSitStatAppMod.getConMatSitByNature(distId, dates, schoolPropertyMap, db1Service,saasService, schConMatSitStatMap, eduSchoolService,redisService);
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getKey());
				if(schGsAcceptSitStat==null) {
					schGsAcceptSitStat = new SchGsAcceptSitStat();
				}
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setConMatSchNum(entry.getValue().getConMatSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收学校、未验收配送单个数、验收率
			if(days >= 2) {
				getAcceptNoAccSchuNumByNatureFromHive(departmentId,distId, dates, schoolPropertyMap, schGsAcceptSitStatMap, dbHiveGsService);
			}else {
				getAcceptNoAccSchuNumByNatureTwo(departmentId,distId, dates, schoolPropertyMap, schGsAcceptSitStatMap);
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
				SchGsAcceptSitStat schGsAcceptSitStat = new SchGsAcceptSitStat();
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 用料汇总(已确认用料学校)
			 */
			//2019-08-05注释，注释原因：目前界面不展示用料信息另外用料改为hive库读取，速度相对较慢，故暂时去除
			/*Map<String,SchConMatSitStat> schConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			SchConMatSitStatAppMod.getConMatSitBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap, db1Service, saasService,schConMatSitStatMap, eduSchoolService,redisService);
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getKey());
				if(schGsAcceptSitStat==null) {
					schGsAcceptSitStat = new SchGsAcceptSitStat();
				}
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setConMatSchNum(entry.getValue().getConMatSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收学校、未验收配送单个数、验收率
			if(days >= 2) {
				getAcceptNoAccSchuNumBySchoolTypeFromHive(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap, schGsAcceptSitStatMap, eduSchoolService, dbHiveGsService);
			}else {
				getAcceptNoAccSchuNumBySchoolTypeTwo(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap, schGsAcceptSitStatMap, eduSchoolService);
			}
			//计算每种【学制分类】的数量
			Map<String,SchGsAcceptSitStat> newSchGsAcceptSitStatMap= new LinkedHashMap<String,SchGsAcceptSitStat>();
			String statClassName="";
			SchGsAcceptSitStat schGsAcceptSitStatSum = new SchGsAcceptSitStat();
			for(Map.Entry<String,SchGsAcceptSitStat> entry : schGsAcceptSitStatMap.entrySet()) {
				
				
				
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newSchGsAcceptSitStatMap.put(statClassName+"_小计", schGsAcceptSitStatSum);
					schGsAcceptSitStatSum = new SchGsAcceptSitStat();
				}

				//计算数据
				setSchoolPropertSumData(schGsAcceptSitStatSum, entry);
				statClassName = entry.getValue().getStatClassName();
				schGsAcceptSitStatSum.setStatClassName(statClassName);
				schGsAcceptSitStatSum.setStatPropName("小计");
				setRate(schGsAcceptSitStatSum);
				
				newSchGsAcceptSitStatMap.put(entry.getKey(), entry.getValue());
				
			}
			newSchGsAcceptSitStatMap.put("其他"+"_小计", schGsAcceptSitStatSum);
			schGsAcceptSitStatMap = newSchGsAcceptSitStatMap;
			
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
				SchGsAcceptSitStat schGsAcceptSitStat = new SchGsAcceptSitStat();
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 用料汇总(已确认用料学校)
			 */
		  //2019-08-05注释，注释原因：目前界面不展示用料信息另外用料改为hive库读取，速度相对较慢，故暂时去除
			/*Map<String,SchConMatSitStat> schConMatSitStatMap= new LinkedHashMap<String,SchConMatSitStat>();
			SchConMatSitStatAppMod.getConMatSitBySlave(distId, dates, compDepNameToSubLevelNameMap,schConMatSitStatMap,redisService);
			for(Map.Entry<String,SchConMatSitStat> entry : schConMatSitStatMap.entrySet()) {
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getKey());
				if(schGsAcceptSitStat==null) {
					schGsAcceptSitStat = new SchGsAcceptSitStat();
				}
				schGsAcceptSitStat.setStatClassName(entry.getValue().getStatClassName());
				schGsAcceptSitStat.setStatPropName(entry.getValue().getStatPropName());
				schGsAcceptSitStat.setConMatSchNum(entry.getValue().getConMatSchNum());
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
			}*/
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收学校、未验收配送单个数、验收率
		    if(days >= 2) {
		    	getAcceptNoAccSchuNumBySlaveFromHive(departmentId,distId, dates, compDepNameToSubLevelNameMap, schGsAcceptSitStatMap, eduSchoolService, dbHiveGsService);
		    }else {
		    	getAcceptNoAccSchuNumBySlave(departmentId,distId, dates, compDepNameToSubLevelNameMap, schGsAcceptSitStatMap,eduSchoolService);
		    }
		    //计算每种【学制分类】的数量
			Map<String,SchGsAcceptSitStat> newSchGsAcceptSitStatMap= new LinkedHashMap<String,SchGsAcceptSitStat>();
			String statClassName="";
			SchGsAcceptSitStat schGsAcceptSitStatSum = new SchGsAcceptSitStat();
			for(Map.Entry<String,SchGsAcceptSitStat> entry : schGsAcceptSitStatMap.entrySet()) {
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newSchGsAcceptSitStatMap.put(statClassName+"_小计", schGsAcceptSitStatSum);
					schGsAcceptSitStatSum = new SchGsAcceptSitStat();
				}

				//计算数据
				setSchoolPropertSumData(schGsAcceptSitStatSum, entry);
				statClassName = entry.getValue().getStatClassName();	
				schGsAcceptSitStatSum.setStatClassName(statClassName);
				schGsAcceptSitStatSum.setStatPropName("小计");
				//计算比率：包括排菜率、验收率、留样率、预警处理率
                setRate(schGsAcceptSitStatSum);
				newSchGsAcceptSitStatMap.put(entry.getKey(), entry.getValue());
				
			}
			newSchGsAcceptSitStatMap.put("其他"+"_小计", schGsAcceptSitStatSum);
			schGsAcceptSitStatMap = newSchGsAcceptSitStatMap;
			
			//循环技术小计的百分比（包括排菜率、验收率、留样率、预警处理率、）
			for(Map.Entry<String,SchGsAcceptSitStat> entry : schGsAcceptSitStatMap.entrySet()) {
				if(entry.getKey().contains("小计")) {
					schGsAcceptSitStatSum = entry.getValue();
				}
			}
			
		}else if(statMode == 4) {
			//========0:按管理部门统计
			
			DepartmentObj departmentObj = new DepartmentObj();
			if(CommonUtil.isNotEmpty(departmentId)) {
				departmentObj.setDepartmentId(departmentId);
			}
			List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,null, -1, -1);
			
			/**
			 * 验收汇总
			 */
			//获取配送信息：未验收配送单个数、验收率
			if(days >= 2) {
				getAccDistrInfoByDepartmentFromHive(departmentId, distId, dates, deparmentList, schGsAcceptSitStatMap, dbHiveGsService);
			}else {
				getAccDistrInfoByDeaprtment(departmentId, dates, deparmentList, schGsAcceptSitStatMap);
			}
			
			//计算每种【管理】的数量
			Map<String,SchGsAcceptSitStat> newSchGsAcceptSitStatMap= new LinkedHashMap<String,SchGsAcceptSitStat>();
			String statPropName="";
			SchGsAcceptSitStat schDishSitStatSum = new SchGsAcceptSitStat();
			SchGsAcceptSitStat preSchDishSitStatSum = new SchGsAcceptSitStat();
			int sumIndex = 0;
			for(Map.Entry<String,SchGsAcceptSitStat> entry : schGsAcceptSitStatMap.entrySet()) {
				sumIndex ++ ;
				if(sumIndex == 17 || sumIndex ==18 || sumIndex == 19) {
					newSchGsAcceptSitStatMap.put(statPropName+"_小计", schDishSitStatSum);
					schDishSitStatSum = new SchGsAcceptSitStat();
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
				
				newSchGsAcceptSitStatMap.put(entry.getKey(), entry.getValue());
				
				if(sumIndex == 16 || sumIndex ==17 || sumIndex == 18) {
					try {
						preSchDishSitStatSum = new SchGsAcceptSitStat();
						BeanUtils.copyProperties(preSchDishSitStatSum, schDishSitStatSum);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			newSchGsAcceptSitStatMap.put("小计", schDishSitStatSum);
			schGsAcceptSitStatMap = newSchGsAcceptSitStatMap;
			
		}
		
		
		
		schGsAcceptSitStatMap.values();
		Collection<SchGsAcceptSitStat> valueCollection = schGsAcceptSitStatMap.values();
	    schGsAcceptSitStatList = new ArrayList<SchGsAcceptSitStat>(valueCollection);
		
		
		schGsAcceptSitStatDTO.setSchGsAcceptSitStat(schGsAcceptSitStatList);
		
		//时戳
		schGsAcceptSitStatDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		schGsAcceptSitStatDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return schGsAcceptSitStatDTO;
	}
	
	private void setRate(SchGsAcceptSitStat schGsAcceptSitStatSum) {
		
		//验收率
		float distDishRate = 0;
		if(schGsAcceptSitStatSum.getTotalGsPlanNum() > 0) {
			distDishRate = 100 * ((float) (schGsAcceptSitStatSum.getAcceptGsNum()) / (float) schGsAcceptSitStatSum.getTotalGsPlanNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			/*if (distDishRate > 100) {
				distDishRate = 100;
			}*/
		}
		schGsAcceptSitStatSum.setAcceptRate(distDishRate);
		
		
		//学校验收率
		distDishRate = 0;
		if(schGsAcceptSitStatSum.getShouldAccSchNum()> 0) {
			distDishRate = 100 * ((float) (schGsAcceptSitStatSum.getShouldAccSchNum() - schGsAcceptSitStatSum.getNoAcceptSchNum()) / (float) schGsAcceptSitStatSum.getShouldAccSchNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		schGsAcceptSitStatSum.setSchAcceptRate(distDishRate);
		
		//学校标准验收率
		distDishRate = 0;
		if(schGsAcceptSitStatSum.getShouldAccSchNum()> 0) {
			distDishRate = 100 * ((float) (schGsAcceptSitStatSum.getSchStandardNum()) / (float) schGsAcceptSitStatSum.getShouldAccSchNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		schGsAcceptSitStatSum.setSchStandardRate(distDishRate);
	}

	/**
	 * 学习学制每个分类小计
	 * @param schGsAcceptSitStatSum
	 * @param entry
	 */
	private void setSchoolPropertSumData(SchGsAcceptSitStat schGsAcceptSitStatSum, Map.Entry<String, SchGsAcceptSitStat> entry) {
		
		/**
		 *  已排菜学校数量
		 */
		schGsAcceptSitStatSum.setDishSchNum(schGsAcceptSitStatSum.getDishSchNum()+entry.getValue().getDishSchNum());
		
		/**
		 * 已确认用料学校
		 */
		schGsAcceptSitStatSum.setConMatSchNum(schGsAcceptSitStatSum.getConMatSchNum()+entry.getValue().getConMatSchNum());
		
		/**
		 * 应验收学校数
		 */
		schGsAcceptSitStatSum.setShouldAccSchNum(schGsAcceptSitStatSum.getShouldAccSchNum()+entry.getValue().getShouldAccSchNum());
		
		/**
		 * 已验收学校数
		 */
		schGsAcceptSitStatSum.setAcceptSchNum(schGsAcceptSitStatSum.getAcceptSchNum()+entry.getValue().getAcceptSchNum());
		
		/**
		 * 未验收学校数
		 */
		schGsAcceptSitStatSum.setNoAcceptSchNum(schGsAcceptSitStatSum.getNoAcceptSchNum()+entry.getValue().getNoAcceptSchNum());
		/**
		 * 配货计划数
		 */
		schGsAcceptSitStatSum.setTotalGsPlanNum(schGsAcceptSitStatSum.getTotalGsPlanNum()+entry.getValue().getTotalGsPlanNum());
		/**
		 * 已验收配货单
		 */
		schGsAcceptSitStatSum.setAcceptGsNum(schGsAcceptSitStatSum.getAcceptGsNum()+entry.getValue().getAcceptGsNum());
		
		/**
		 * 未验收配货单
		 */
		schGsAcceptSitStatSum.setNoAcceptGsNum(schGsAcceptSitStatSum.getNoAcceptGsNum()+entry.getValue().getNoAcceptGsNum());
		/**
		 * 验收率
		 */
		schGsAcceptSitStatSum.setAcceptRate(schGsAcceptSitStatSum.getAcceptRate()+entry.getValue().getAcceptRate());
		
		schGsAcceptSitStatSum.setSchStandardNum(schGsAcceptSitStatSum.getSchStandardNum()+entry.getValue().getSchStandardNum());
		schGsAcceptSitStatSum.setSchSupplementNum(schGsAcceptSitStatSum.getSchSupplementNum()+entry.getValue().getSchSupplementNum());
		schGsAcceptSitStatSum.setSchBeOverdueNum(schGsAcceptSitStatSum.getSchBeOverdueNum()+entry.getValue().getSchBeOverdueNum());
		schGsAcceptSitStatSum.setSchNoDataNum(schGsAcceptSitStatSum.getSchNoDataNum()+entry.getValue().getSchNoDataNum());
		schGsAcceptSitStatSum.setSchStandardRate(schGsAcceptSitStatSum.getSchStandardRate()+entry.getValue().getSchStandardRate());
	}
	
	/**
	 * 获取配送信息：未验收学校
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumByArea(String distId, String[] dates, List<TEduDistrictDo> tedList,Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap) {
		//未配送学校个数
		Map<String,Set<String>> noAcceptSchNumMap = new HashMap<String,Set<String>>();
		//已配送学校个数
		Map<String,Set<String>> acceptSchNumMap = new HashMap<String,Set<String>>();
		//未配送学校个数
		Set<String> noAcceptSchNumSet = new HashSet<String>();
		//已配送学校个数
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
							noAcceptSchNumSet=noAcceptSchNumMap.get(curKeys[7]);
							if(noAcceptSchNumSet == null) {
								noAcceptSchNumSet = new HashSet<String>();
							}
							noAcceptSchNumSet.add(curKeys[5]);
							noAcceptSchNumMap.put(curKeys[7], noAcceptSchNumSet);
							
						}else if (dispPlanStatus==3) {
							acceptSchNumSet=acceptSchNumMap.get(curKeys[7]);
							if(acceptSchNumSet == null) {
								acceptSchNumSet = new HashSet<String>();
							}
							acceptSchNumSet.add(curKeys[5]);
							acceptSchNumMap.put(curKeys[7], acceptSchNumSet);
						}
					}
					else {
						logger.info("配货计划："+ curKey + "，格式错误！");
					}
				}
			}
		}
		
		
		for (TEduDistrictDo curTdd : tedList) {
			String curDistId = curTdd.getId();
			// 判断是否按区域获取排菜数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if (distId != null) {
				if (!curDistId.equals(distId)) {
					continue;
				}
			}
			
			
			//未留样
			SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(curTdd.getName());
			if(schGsAcceptSitStat==null) {
				schGsAcceptSitStat = new SchGsAcceptSitStat();
				//区域名称
				schGsAcceptSitStat.setStatPropName(curTdd.getName());
			}
			/**
			 * 未验收学校数
			 */
			schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNumMap.get(curDistId)==null?0:noAcceptSchNumMap.get(curDistId).size());
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNumMap.get(curDistId)==null?0:acceptSchNumMap.get(curDistId).size());
			
			schGsAcceptSitStatMap.put(curTdd.getName(), schGsAcceptSitStat);
			
		}
		
		
	}
	
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumByNature(String distId, String[] dates,Map<Integer, String> schoolPropertyMap,Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap,
			EduSchoolService eduSchoolService) {
		Set<String> schoolSet = new HashSet<String>();
		//未验收学校个数
		Map<String,Set<String>> noAcceptSchNumsMap = new HashMap<String,Set<String>>();
		//已验收学校个数
		Map<String,Set<String>> acceptSchNumsMap = new HashMap<String,Set<String>>();
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
					if(keyVals.length < 1 ) {
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
					
					//已验收学校数
					schoolSet=acceptSchNumsMap.get(eduSchool.getSchoolNature());
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					acceptSchNumsMap.put(eduSchool.getSchoolNature(), schoolSet);
					
				}else if(keys[1] !=null && !keys[1].equals("4") && !keys[1].equals("3")) {
					//未验收数量
					if(noAcceptGsPlanNumMap.get(eduSchool.getSchoolNature())==null) {
						count = 1;
					}else {
						count = noAcceptGsPlanNumMap.get(eduSchool.getSchoolNature())+1;
					}
					
					noAcceptGsPlanNumMap.put(eduSchool.getSchoolNature(), count);
					
					//未验收学校数
					schoolSet=noAcceptSchNumsMap.get(eduSchool.getSchoolNature());
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					noAcceptSchNumsMap.put(eduSchool.getSchoolNature(), schoolSet);
				}
			}
			
			
		}
		
		float acceptRate = 0F;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			acceptRate=0;
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getValue());
				if(schGsAcceptSitStat==null) {
					schGsAcceptSitStat = new SchGsAcceptSitStat();
					//学校性质名称
					schGsAcceptSitStat.setStatPropName(entry.getValue());
				}
				/**
				 * 未验收学校数
				 */
				schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()).size());
				
				/**
				 * 已验收学校数
				 */
				schGsAcceptSitStat.setAcceptSchNum(acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString()).size());
				
				int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
				int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
				
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划总数
				 */
				schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
				
				/**
				 * 已验收配货单
				 */
				schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
				
				/**
				 * 未验收配货单
				 */
				schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
				
				/**
				 * 验收率，保留小数点有效数字两位
				 */
				schGsAcceptSitStat.setAcceptRate(acceptRate);
				
				
		}
		
	}
	
	/**
	 * 获取配送信息：配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAcceptNoAccSchuNumByNatureTwo(String departmentId,String distId, String[] dates, Map<Integer, String> schoolPropertyMap,
			Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap) {
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
		
		Map<String,Integer> schStandardNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schSupplementNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schBeOverdueNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schNoDataNumMap = new HashMap<String,Integer>();
		
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
						if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						
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
						if (curKeys[5] !=null && curKeys[5].equals("3")) {
							//未验收学校数
							schoolNum=acceptSchNumsMap.get(curKeys[1]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							acceptSchNumsMap.put(curKeys[1], schoolNum);
						}else {
							//未验收学校
							for(int j = -2; j <= 2; j++) {
								if(curKeys[5] !=null && curKeys[5].equals(String.valueOf(j))) {
									//未验收学校数
									schoolNum=noAcceptSchNumsMap.get(curKeys[1]);
									if(schoolNum == null) {
										schoolNum = valueCount;
									}else {
										schoolNum = schoolNum + valueCount;
									}
									noAcceptSchNumsMap.put(curKeys[1], schoolNum);
								}
							}
						}
					}
					
					//操作状态对应的数量
					if (curKey.indexOf("dis-school-nature_") == 0) {
						if(curKeys.length >= 4)
						{
							if(curKeys[5].equalsIgnoreCase("1")) {
								if(schStandardNumMap.get(curKeys[1]) == null) {
									schStandardNumMap.put(curKeys[1], valueCount);
								}else {
									schStandardNumMap.put(curKeys[1], schStandardNumMap.get(curKeys[1]) + valueCount);
								}
							}else if(curKeys[5].equalsIgnoreCase("2")) {
									if(schSupplementNumMap.get(curKeys[1]) == null) {
										schSupplementNumMap.put(curKeys[1], valueCount);
									}else {
										schSupplementNumMap.put(curKeys[1], schSupplementNumMap.get(curKeys[1]) + valueCount);
									}
							}else if(curKeys[5].equalsIgnoreCase("3")) {
									if(schBeOverdueNumMap.get(curKeys[1]) == null) {
										schBeOverdueNumMap.put(curKeys[1], valueCount);
									}else {
										schBeOverdueNumMap.put(curKeys[1], schBeOverdueNumMap.get(curKeys[1]) + valueCount);
									}
							}else if(curKeys[5].equalsIgnoreCase("4")) {
									if(schNoDataNumMap.get(curKeys[1]) == null) {
										schNoDataNumMap.put(curKeys[1], valueCount);
									}else {
										schNoDataNumMap.put(curKeys[1], schNoDataNumMap.get(curKeys[1]) + valueCount);
									}
							}
						}
					}
				}
			}
		}
		
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		float schStandardRate = 0F;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			acceptRate=0;
			schAcceptRate=0;
			schStandardRate = 0F;
			SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getValue());
			if(schGsAcceptSitStat==null) {
				schGsAcceptSitStat = new SchGsAcceptSitStat();
				//学校性质名称
				schGsAcceptSitStat.setStatPropName(entry.getValue());
			}
			
			
			/**
			 * 未验收学校数
			 */
			schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()));
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString()));
			
			
			int shouldAccSchNum = schGsAcceptSitStat.getNoAcceptSchNum() + schGsAcceptSitStat.getAcceptSchNum();
			if(shouldAccSchNum > 0) {
				schAcceptRate = 100 * ((float) schGsAcceptSitStat.getAcceptSchNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			
			/**
			 * 应验收学校数
			 */
			schGsAcceptSitStat.setShouldAccSchNum(shouldAccSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setSchAcceptRate(schAcceptRate);
			
			
			int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
			int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
			int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
			
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			/**
			 * 配货计划总数
			 */
			schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
			
			/**
			 * 已验收配货单
			 */
			schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
			
			/**
			 * 未验收配货单
			 */
			schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
			
			/**
			 * 验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setAcceptRate(acceptRate);
			
			//1 表示规范录入
			schGsAcceptSitStat.setSchStandardNum(schStandardNumMap.get(entry.getKey().toString())==null?0:schStandardNumMap.get(entry.getKey().toString()));
			//2 表示补录
			schGsAcceptSitStat.setSchSupplementNum( schSupplementNumMap.get(entry.getKey().toString())==null?0:schSupplementNumMap.get(entry.getKey().toString()));
			//3 表示逾期补录
			schGsAcceptSitStat.setSchBeOverdueNum(schBeOverdueNumMap.get(entry.getKey().toString())==null?0:schBeOverdueNumMap.get(entry.getKey().toString()));
			//4 表示无数据
			schGsAcceptSitStat.setSchNoDataNum(schNoDataNumMap.get(entry.getKey().toString())==null?0:schNoDataNumMap.get(entry.getKey().toString()));
			
			//标准验收率
			schStandardRate = 0;
			if(shouldAccSchNum > 0) {
				schStandardRate = 100 * ((float) schGsAcceptSitStat.getSchStandardNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schGsAcceptSitStat.setSchStandardRate(schStandardRate);
			
			schGsAcceptSitStatMap.put(entry.getValue(), schGsAcceptSitStat);
		}
	}

	
	/**
	 * 获取配送信息：配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAcceptNoAccSchuNumByNatureFromHive(String departmentId,String distId, String[] dates, Map<Integer, String> schoolPropertyMap,
			Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap,DbHiveGsService dbHiveGsService) {

		
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
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
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
				
				if(schDishCommon.getDisSealStatus() !=null && !"-1".equals(schDishCommon.getDisSealStatus())) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if("1".equals(schDishCommon.getDisSealStatus())) {
						standardNumMap.put(schDishCommon.getSchoolNatureName(), 
								(standardNumMap.get(schDishCommon.getSchoolNatureName())==null?0:standardNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getSchoolTotal());
					}else if("2".equals(schDishCommon.getDisSealStatus())) {
						supplementNumMap.put(schDishCommon.getSchoolNatureName(), 
								(supplementNumMap.get(schDishCommon.getSchoolNatureName())==null?0:supplementNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getSchoolTotal());
					}else if("3".equals(schDishCommon.getDisSealStatus())) {
						beOverdueNumMap.put(schDishCommon.getSchoolNatureName(), 
								(beOverdueNumMap.get(schDishCommon.getSchoolNatureName())==null?0:beOverdueNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getSchoolTotal());
					}else if("4".equals(schDishCommon.getDisSealStatus())) {
						noDataNumMap.put(schDishCommon.getSchoolNatureName(), 
								(noDataNumMap.get(schDishCommon.getSchoolNatureName())==null?0:noDataNumMap.get(schDishCommon.getSchoolNatureName())) 
								+ schDishCommon.getSchoolTotal());
					}
				}
			}
		}
		
		
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		//标准验收率
		float schStandardRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			acceptRate=0;
			schAcceptRate=0;
			SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getValue());
			if(schGsAcceptSitStat==null) {
				schGsAcceptSitStat = new SchGsAcceptSitStat();
				//学校性质名称
				schGsAcceptSitStat.setStatPropName(entry.getValue());
			}
			
			
			/**
			 * 未验收学校数
			 */
			schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()));
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString()));
			
			
			int shouldAccSchNum = schGsAcceptSitStat.getNoAcceptSchNum() + schGsAcceptSitStat.getAcceptSchNum();
			if(shouldAccSchNum > 0) {
				schAcceptRate = 100 * ((float) schGsAcceptSitStat.getAcceptSchNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			
			/**
			 * 应验收学校数
			 */
			schGsAcceptSitStat.setShouldAccSchNum(shouldAccSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setSchAcceptRate(schAcceptRate);
			
			
			int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
			int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
			int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
			
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			/**
			 * 配货计划总数
			 */
			schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
			
			/**
			 * 已验收配货单
			 */
			schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
			
			/**
			 * 未验收配货单
			 */
			schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
			
			/**
			 * 验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setAcceptRate(acceptRate);
			
			//1 表示规范录入
			schGsAcceptSitStat.setSchStandardNum(standardNumMap.get(entry.getKey().toString())==null?0:standardNumMap.get(entry.getKey().toString()));
			//2 表示补录
			schGsAcceptSitStat.setSchSupplementNum(supplementNumMap.get(entry.getKey().toString())==null?0:supplementNumMap.get(entry.getKey().toString()));
			//3 表示逾期补录
			schGsAcceptSitStat.setSchBeOverdueNum(beOverdueNumMap.get(entry.getKey().toString())==null?0:beOverdueNumMap.get(entry.getKey().toString()));
			//4 表示无数据
			schGsAcceptSitStat.setSchNoDataNum(noDataNumMap.get(entry.getKey().toString())==null?0:noDataNumMap.get(entry.getKey().toString()));
			
			//标准验收率
			schStandardRate = 0;
			if(totalGsPlanNum > 0) {
				schStandardRate = 100 * ((float) schGsAcceptSitStat.getSchStandardNum() / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schGsAcceptSitStat.setSchStandardRate(schStandardRate);
			
			schGsAcceptSitStatMap.put(entry.getValue(), schGsAcceptSitStat);
		}
	}
	
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率（按学校类型分类）
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumBySchoolType(String distId, String[] dates,Map<Integer, String> schoolPropertyMap,Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap,
			EduSchoolService eduSchoolService) {
		Set<String> schoolSet = new HashSet<String>();
		
		//未验收学校个数
		Map<String,Set<String>> noAcceptSchNumsMap = new HashMap<String,Set<String>>();
		//已验收学校个数
		Map<String,Set<String>> acceptSchNumsMap = new HashMap<String,Set<String>>();
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
					if(keyVals.length < 1 ) {
						continue;
					}
					// 配货计划列表
					String[] curKeys = curKey.split("_");
					if(curKeys.length >= 14) {
						if(distId != null) {
							if(curKeys[7].compareTo(distId) != 0) {
								continue ;
							}
						}
						schoolDetailTotal=schoolDetailMap.get(curKeys[5]+"_"+keyVals[0]);
						if(schoolDetailTotal == null) {
							schoolDetailTotal = 0;
						}else {
							schoolDetailTotal = schoolDetailTotal+1;
						}
						schoolDetailMap.put(curKeys[5]+"_"+keyVals[0], schoolDetailTotal);
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
					
					//已验收学校数
					schoolSet=acceptSchNumsMap.get(schTypeId);
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					acceptSchNumsMap.put(schTypeId, schoolSet);
					
				}else  {
					
					//未验收数量
					if(noAcceptGsPlanNumMap.get(schTypeId)==null) {
						count = 1;
					}else {
						count = noAcceptGsPlanNumMap.get(schTypeId)+1;
					}
					
					noAcceptGsPlanNumMap.put(schTypeId, count);
					
					//未验收学校数
					schoolSet=noAcceptSchNumsMap.get(schTypeId);
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					noAcceptSchNumsMap.put(schTypeId, schoolSet);
				}
			}
			
			
		}
		
		float acceptRate = 0F;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			acceptRate=0;
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getValue());
				if(schGsAcceptSitStat==null) {
					schGsAcceptSitStat = new SchGsAcceptSitStat();
					//学校性质名称
					schGsAcceptSitStat.setStatPropName(entry.getValue());
					//
					schGsAcceptSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				
				/**
				 * 未验收学校数
				 */
				schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()).size());
				
				/**
				 * 已验收学校数
				 */
				schGsAcceptSitStat.setAcceptSchNum(acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString()).size());
				
				
				int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
				int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
				
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划总数
				 */
				schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
				
				/**
				 * 已验收配货单
				 */
				schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
				
				/**
				 * 未验收配货单
				 */
				schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
				
				/**
				 * 验收率，保留小数点有效数字两位
				 */
				schGsAcceptSitStat.setAcceptRate(acceptRate);
				
				schGsAcceptSitStatMap.put(entry.getValue(), schGsAcceptSitStat);
		}
		
	}

	
	/**
	 * 获取配送信息：配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAcceptNoAccSchuNumBySchoolTypeTwo(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap,
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
		
		
		Map<String,Integer> schStandardNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schSupplementNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schBeOverdueNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schNoDataNumMap = new HashMap<String,Integer>();
		
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
						if(distId != null) {
							if(curKeys[1].compareTo(distId) != 0) {
								continue ;
							}
						}
						
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
						if (curKeys[3] !=null && curKeys[3].equals("3")) {
							count = 0;
							//未验收学校数
							schoolNum=acceptSchNumsMap.get(curKeys[1]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							acceptSchNumsMap.put(curKeys[1], schoolNum);
						}else {
							//未验收学校
							for(int j = -2; j <= 2; j++) {
								if(curKeys[3] !=null && curKeys[3].equals(String.valueOf(j))) {
									count = 0;
									//未验收学校数
									schoolNum=noAcceptSchNumsMap.get(curKeys[1]);
									if(schoolNum == null) {
										schoolNum = valueCount;
									}else {
										schoolNum = schoolNum + valueCount;
									}
									noAcceptSchNumsMap.put(curKeys[1], schoolNum);
								}
							}
						}
					}
					
					//操作状态对应的数量
					if (curKey.indexOf("dis-school-level_") == 0) {
						if(curKeys.length >= 4)
						{
							if(curKeys[3].equalsIgnoreCase("1")) {
								if(schStandardNumMap.get(curKeys[1]) == null) {
									schStandardNumMap.put(curKeys[1], valueCount);
								}else {
									schStandardNumMap.put(curKeys[1], schStandardNumMap.get(curKeys[1]) + valueCount);
								}
							}else if(curKeys[3].equalsIgnoreCase("2")) {
									if(schSupplementNumMap.get(curKeys[1]) == null) {
										schSupplementNumMap.put(curKeys[1], valueCount);
									}else {
										schSupplementNumMap.put(curKeys[1], schSupplementNumMap.get(curKeys[1]) + valueCount);
									}
							}else if(curKeys[3].equalsIgnoreCase("3")) {
									if(schBeOverdueNumMap.get(curKeys[1]) == null) {
										schBeOverdueNumMap.put(curKeys[1], valueCount);
									}else {
										schBeOverdueNumMap.put(curKeys[1], schBeOverdueNumMap.get(curKeys[1]) + valueCount);
									}
							}else if(curKeys[3].equalsIgnoreCase("4")) {
									if(schNoDataNumMap.get(curKeys[1]) == null) {
										schNoDataNumMap.put(curKeys[1], valueCount);
									}else {
										schNoDataNumMap.put(curKeys[1], schNoDataNumMap.get(curKeys[1]) + valueCount);
									}
							}
						}
					}
				}
			}
		}
		
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		float schStandardRate = 0F;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			acceptRate=0;
			schAcceptRate=0;
			schStandardRate = 0F;
			SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getValue());
			if(schGsAcceptSitStat==null) {
				schGsAcceptSitStat = new SchGsAcceptSitStat();
				//学校性质名称
				schGsAcceptSitStat.setStatPropName(entry.getValue());
				//
				schGsAcceptSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
			}
			/**
			 * 未验收学校数
			 */
			schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()));
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString()));
			
			int shouldAccSchNum = schGsAcceptSitStat.getNoAcceptSchNum() + schGsAcceptSitStat.getAcceptSchNum();
			if(shouldAccSchNum > 0) {
				schAcceptRate = 100 * ((float) schGsAcceptSitStat.getAcceptSchNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			
			/**
			 * 应验收学校数
			 */
			schGsAcceptSitStat.setShouldAccSchNum(shouldAccSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setSchAcceptRate(schAcceptRate);
			
			int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
			int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
			int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
			
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			/**
			 * 配货计划总数
			 */
			schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
			
			/**
			 * 已验收配货单
			 */
			schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
			
			/**
			 * 未验收配货单
			 */
			schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
			
			/**
			 * 验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setAcceptRate(acceptRate);
			

			//1 表示规范录入
			schGsAcceptSitStat.setSchStandardNum(schStandardNumMap.get(entry.getKey().toString())==null?0:schStandardNumMap.get(entry.getKey().toString()));
			//2 表示补录
			schGsAcceptSitStat.setSchSupplementNum( schSupplementNumMap.get(entry.getKey().toString())==null?0:schSupplementNumMap.get(entry.getKey().toString()));
			//3 表示逾期补录
			schGsAcceptSitStat.setSchBeOverdueNum(schBeOverdueNumMap.get(entry.getKey().toString())==null?0:schBeOverdueNumMap.get(entry.getKey().toString()));
			//4 表示无数据
			schGsAcceptSitStat.setSchNoDataNum(schNoDataNumMap.get(entry.getKey().toString())==null?0:schNoDataNumMap.get(entry.getKey().toString()));
			
			//标准验收率
			schStandardRate = 0;
			if(shouldAccSchNum > 0) {
				schStandardRate = 100 * ((float) schGsAcceptSitStat.getSchStandardNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schGsAcceptSitStat.setSchStandardRate(schStandardRate);
			
			schGsAcceptSitStatMap.put(entry.getValue(), schGsAcceptSitStat);
		}
	}

	/**
	 * 获取配送信息：配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAcceptNoAccSchuNumBySchoolTypeFromHive(String departmentId,String distId, String[] dates,Map<Integer, String> schoolPropertyMap,Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap,
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
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
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
				-1, -1, null, null,departmentId,null,  2);
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
				if(schDishCommon.getDisSealStatus() !=null && !"-1".equals(schDishCommon.getDisSealStatus())) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if("1".equals(schDishCommon.getDisSealStatus())) {
						standardNumMap.put(schDishCommon.getLevelName(), 
								(standardNumMap.get(schDishCommon.getLevelName())==null?0:standardNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getSchoolTotal());
					}else if("2".equals(schDishCommon.getDisSealStatus())) {
						supplementNumMap.put(schDishCommon.getLevelName(), 
								(supplementNumMap.get(schDishCommon.getLevelName())==null?0:supplementNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getSchoolTotal());
					}else if("3".equals(schDishCommon.getDisSealStatus())) {
						beOverdueNumMap.put(schDishCommon.getLevelName(), 
								(beOverdueNumMap.get(schDishCommon.getLevelName())==null?0:beOverdueNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getSchoolTotal());
					}else if("4".equals(schDishCommon.getDisSealStatus())) {
						noDataNumMap.put(schDishCommon.getLevelName(), 
								(noDataNumMap.get(schDishCommon.getLevelName())==null?0:noDataNumMap.get(schDishCommon.getLevelName())) 
								+ schDishCommon.getSchoolTotal());
					}
				}

			}
		}
		
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		//标准验收率
		float schStandardRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			acceptRate=0;
			schAcceptRate=0;
			SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getValue());
			if(schGsAcceptSitStat==null) {
				schGsAcceptSitStat = new SchGsAcceptSitStat();
				//学校性质名称
				schGsAcceptSitStat.setStatPropName(entry.getValue());
				//
				schGsAcceptSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
			}
			/**
			 * 未验收学校数
			 */
			schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()));
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString()));
			
			int shouldAccSchNum = schGsAcceptSitStat.getNoAcceptSchNum() + schGsAcceptSitStat.getAcceptSchNum();
			if(shouldAccSchNum > 0) {
				schAcceptRate = 100 * ((float) schGsAcceptSitStat.getAcceptSchNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			
			/**
			 * 应验收学校数
			 */
			schGsAcceptSitStat.setShouldAccSchNum(shouldAccSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setSchAcceptRate(schAcceptRate);
			
			int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
			int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
			int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
			
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			/**
			 * 配货计划总数
			 */
			schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
			
			/**
			 * 已验收配货单
			 */
			schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
			
			/**
			 * 未验收配货单
			 */
			schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
			
			/**
			 * 验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setAcceptRate(acceptRate);
			
			//1 表示规范录入
			schGsAcceptSitStat.setSchStandardNum(standardNumMap.get(entry.getKey().toString())==null?0:standardNumMap.get(entry.getKey().toString()));
			//2 表示补录
			schGsAcceptSitStat.setSchSupplementNum(supplementNumMap.get(entry.getKey().toString())==null?0:supplementNumMap.get(entry.getKey().toString()));
			//3 表示逾期补录
			schGsAcceptSitStat.setSchBeOverdueNum(beOverdueNumMap.get(entry.getKey().toString())==null?0:beOverdueNumMap.get(entry.getKey().toString()));
			//4 表示无数据
			schGsAcceptSitStat.setSchNoDataNum(noDataNumMap.get(entry.getKey().toString())==null?0:noDataNumMap.get(entry.getKey().toString()));
			
			//标准验收率
			schStandardRate = 0;
			if(totalGsPlanNum > 0) {
				schStandardRate = 100 * ((float) schGsAcceptSitStat.getSchStandardNum() / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schGsAcceptSitStat.setSchStandardRate(schStandardRate);
			
			schGsAcceptSitStatMap.put(entry.getValue(), schGsAcceptSitStat);
		}
	}
	
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率（按学校所属主管部门）
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumBySlave(String departmentId,String distId, String[] dates,Map<String, String> slaveMap,Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap,
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
		
		Map<String,Integer> schStandardNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schSupplementNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schBeOverdueNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schNoDataNumMap = new HashMap<String,Integer>();
		
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
					if(curKey.indexOf("masterid")==0 && curKeys.length >= 6) {
						if("3".equals(curKeys[1]) && curKeys[3]==null) {
							curKeys[3] = "其他";
						}
						//配送计划数量(排除status_4    已取消)
						if(curKeys[5] !=null && !curKeys[5].equals("4")) {
							
							if(totalGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])!=null) {
								count = totalGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
							}
							
							totalGsPlanNumMap.put(curKeys[1]+"_"+curKeys[3], count);
						}
						
						count = valueCount;
						if(curKeys[5] !=null && curKeys[5].equals("3")) {
							//已验收数量
							if(acceptGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])!=null) {
								count = acceptGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
							}
							
							acceptGsPlanNumMap.put(curKeys[1]+"_"+curKeys[3], count);
						}else if(curKeys[5] !=null && !curKeys[5].equals("4") && !curKeys[5].equals("3")) {
							//未验收数量
							if(noAcceptGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])!=null) {
								count = noAcceptGsPlanNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
							}
							
							noAcceptGsPlanNumMap.put(curKeys[1]+"_"+curKeys[3], count);
						}
					}
					
					//未验收学校数
					if(curKey.indexOf("school-masterid")==0 && curKeys.length >= 6) {
						if (curKeys[5] !=null && curKeys[5].equals("3")) {
							//未验收学校数
							schoolNum=acceptSchNumsMap.get(curKeys[1]+"_"+curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							acceptSchNumsMap.put(curKeys[1]+"_"+curKeys[3], schoolNum);
						}else {
							//未验收学校
							for(int j = -2; j <= 2; j++) {
								if(curKeys[5] !=null && curKeys[5].equals(String.valueOf(j))) {
									//未验收学校数
									schoolNum=noAcceptSchNumsMap.get(curKeys[1]+"_"+curKeys[3]);
									if(schoolNum == null) {
										schoolNum = valueCount;
									}else {
										schoolNum = schoolNum + valueCount;
									}
									noAcceptSchNumsMap.put(curKeys[1]+"_"+curKeys[3], schoolNum);
								}
							}
						}
					}
					

					//操作状态对应的数量
					if (curKey.indexOf("dis-school-masterid_") == 0) {
						if(curKeys.length >= 4)
						{
							if(curKeys[5].equalsIgnoreCase("1")) {
								if(schStandardNumMap.get(curKeys[1]+"_"+curKeys[3]) == null) {
									schStandardNumMap.put(curKeys[1]+"_"+curKeys[3], valueCount);
								}else {
									schStandardNumMap.put(curKeys[1]+"_"+curKeys[3], schoolNum + valueCount);
								}
							}else if(curKeys[5].equalsIgnoreCase("2")) {
									if(schSupplementNumMap.get(curKeys[1]+"_"+curKeys[3]) == null) {
										schSupplementNumMap.put(curKeys[1]+"_"+curKeys[3], valueCount);
									}else {
										schSupplementNumMap.put(curKeys[1]+"_"+curKeys[3], schoolNum + valueCount);
									}
							}else if(curKeys[5].equalsIgnoreCase("3")) {
									if(schBeOverdueNumMap.get(curKeys[1]+"_"+curKeys[3]) == null) {
										schBeOverdueNumMap.put(curKeys[1]+"_"+curKeys[3], valueCount);
									}else {
										schBeOverdueNumMap.put(curKeys[1]+"_"+curKeys[3], schoolNum + valueCount);
									}
							}else if(curKeys[5].equalsIgnoreCase("4")) {
									if(schNoDataNumMap.get(curKeys[1]+"_"+curKeys[3]) == null) {
										schNoDataNumMap.put(curKeys[1]+"_"+curKeys[3], valueCount);
									}else {
										schNoDataNumMap.put(curKeys[1]+"_"+curKeys[3], schoolNum + valueCount);
									}
							}
						}
					}
				}
			}
		}
		
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		float schStandardRate = 0F;
		for(Map.Entry<String, String> entry : slaveMap.entrySet() ) {
			acceptRate=0;
			schAcceptRate=0;
			schStandardRate = 0;
				SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getKey());
				if(schGsAcceptSitStat==null) {
					String[] keys = entry.getKey().split("_");
					String masterid = keys[0];
					String slave = keys[1];
					schGsAcceptSitStat = new SchGsAcceptSitStat();
					schGsAcceptSitStat.setStatClassName(entry.getValue());
					//区域名称
					String slaveName=slave;
					
					if("0".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
					}else if ("1".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
					}else if ("2".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
					}
					
					schGsAcceptSitStat.setStatPropName(slaveName);
				}
				/**
				 * 未验收学校数
				 */
				schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNumsMap.get(entry.getKey().toString())==null?0:noAcceptSchNumsMap.get(entry.getKey().toString()));
				
				/**
				 * 已验收学校数
				 */
				schGsAcceptSitStat.setAcceptSchNum(acceptSchNumsMap.get(entry.getKey().toString())==null?0:acceptSchNumsMap.get(entry.getKey().toString()));
				
				int shouldAccSchNum = schGsAcceptSitStat.getNoAcceptSchNum() + schGsAcceptSitStat.getAcceptSchNum();
				if(shouldAccSchNum > 0) {
					schAcceptRate = 100 * ((float) schGsAcceptSitStat.getAcceptSchNum() / (float) shouldAccSchNum);
					BigDecimal bd = new BigDecimal(schAcceptRate);
					schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schAcceptRate > 100) {
						schAcceptRate = 100;
					}
				}
				
				
				/**
				 * 应验收学校数
				 */
				schGsAcceptSitStat.setShouldAccSchNum(shouldAccSchNum);
				
				/**
				 * 学校验收率，保留小数点有效数字两位
				 */
				schGsAcceptSitStat.setSchAcceptRate(schAcceptRate);
				
				int totalGsPlanNum = totalGsPlanNumMap.get(entry.getKey().toString())==null?0:totalGsPlanNumMap.get(entry.getKey().toString());
				int acceptGsPlanNum = acceptGsPlanNumMap.get(entry.getKey().toString())==null?0:acceptGsPlanNumMap.get(entry.getKey().toString());
				int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(entry.getKey().toString())==null?0:noAcceptGsPlanNumMap.get(entry.getKey().toString());
				
				if(totalGsPlanNum > 0) {
					acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if (acceptRate > 100) {
						acceptRate = 100;
					}*/
				}
				
				/**
				 * 配货计划总数
				 */
				schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
				
				/**
				 * 已验收配货单
				 */
				schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
				
				/**
				 * 未验收配货单
				 */
				schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
				
				/**
				 * 验收率，保留小数点有效数字两位
				 */
				schGsAcceptSitStat.setAcceptRate(acceptRate);
				
				//1 表示规范录入
				schGsAcceptSitStat.setSchStandardNum(schStandardNumMap.get(entry.getKey().toString())==null?0:schStandardNumMap.get(entry.getKey().toString()));
				//2 表示补录
				schGsAcceptSitStat.setSchSupplementNum( schSupplementNumMap.get(entry.getKey().toString())==null?0:schSupplementNumMap.get(entry.getKey().toString()));
				//3 表示逾期补录
				schGsAcceptSitStat.setSchBeOverdueNum(schBeOverdueNumMap.get(entry.getKey().toString())==null?0:schBeOverdueNumMap.get(entry.getKey().toString()));
				//4 表示无数据
				schGsAcceptSitStat.setSchNoDataNum(schNoDataNumMap.get(entry.getKey().toString())==null?0:schNoDataNumMap.get(entry.getKey().toString()));
				
				//标准验收率
				schStandardRate = 0;
				if(shouldAccSchNum > 0) {
					schStandardRate = 100 * ((float) schGsAcceptSitStat.getSchStandardNum() / (float) shouldAccSchNum);
					BigDecimal bd = new BigDecimal(schStandardRate);
					schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schStandardRate > 100)
						schStandardRate = 100;
				}
				schGsAcceptSitStat.setSchStandardRate(schStandardRate);
				
				schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
		}
		
	}
	
	/**
	 * 获取配送信息：未验收学校、配货计划数量、未确验收计划数量、验收率（按学校所属主管部门）
	 * @param distId
	 * @param dates
	 * @return
	 */
	private void getAcceptNoAccSchuNumBySlaveFromHive(String deparmentId,String distId, String[] dates,Map<String, String> slaveMap,Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap,
			EduSchoolService eduSchoolService,DbHiveGsService dbHiveGsService) {
		//未验收学校个数
		Map<String,Integer> noAcceptSchNumsMap = new HashMap<String,Integer>();
		//已验收学校个数
		Map<String,Integer> acceptSchNumsMap = new HashMap<String,Integer>();
		
		Map<String,Integer> totalGsPlanNumMap = new HashMap<String,Integer>();//配送计划数量
		Map<String,Integer> acceptGsPlanNumMap = new HashMap<String,Integer>();//已验收数量
		Map<String,Integer> noAcceptGsPlanNumMap = new HashMap<String,Integer>();//未验收数量
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
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
				-1, -1, null, null,deparmentId,null, 3);
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
				
				if(schDishCommon.getDisSealStatus() !=null && !"-1".equals(schDishCommon.getDisSealStatus())) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if("1".equals(schDishCommon.getDisSealStatus())) {
						standardNumMap.put(key, 
								(standardNumMap.get(key)==null?0:standardNumMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
					}else if("2".equals(schDishCommon.getDisSealStatus())) {
						supplementNumMap.put(key, 
								(supplementNumMap.get(key)==null?0:supplementNumMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
					}else if("3".equals(schDishCommon.getDisSealStatus())) {
						beOverdueNumMap.put(key, 
								(beOverdueNumMap.get(key)==null?0:beOverdueNumMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
					}else if("4".equals(schDishCommon.getDisSealStatus())) {
						noDataNumMap.put(key, 
								(noDataNumMap.get(key)==null?0:noDataNumMap.get(key)) 
								+ schDishCommon.getSchoolTotal());
					}
				}
			}
		}
		float acceptRate = 0F;
		float schAcceptRate = 0F;
		//标准验收率
		float schStandardRate = 0;
		for(Map.Entry<String, String> entry : slaveMap.entrySet() ) {
			acceptRate=0;
			schAcceptRate=0;
			SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(entry.getKey());
			
			String[] keys = entry.getKey().split("_");
			String masterid = keys[0];
			String slave = keys[1];
			
			if(schGsAcceptSitStat==null) {
				schGsAcceptSitStat = new SchGsAcceptSitStat();
				schGsAcceptSitStat.setStatClassName(entry.getValue());
				//区域名称
				String slaveName=slave;
				
				if("0".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
				}else if ("1".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
				}else if ("2".equals(masterid)) {
					slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
				}
				
				schGsAcceptSitStat.setStatPropName(slaveName);
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
			schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNumsMap.get(dataKey)==null?0:noAcceptSchNumsMap.get(dataKey));
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNumsMap.get(dataKey)==null?0:acceptSchNumsMap.get(dataKey));
			
			int shouldAccSchNum = schGsAcceptSitStat.getNoAcceptSchNum() + schGsAcceptSitStat.getAcceptSchNum();
			if(shouldAccSchNum > 0) {
				schAcceptRate = 100 * ((float) schGsAcceptSitStat.getAcceptSchNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			
			/**
			 * 应验收学校数
			 */
			schGsAcceptSitStat.setShouldAccSchNum(shouldAccSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setSchAcceptRate(schAcceptRate);
			
			int totalGsPlanNum = totalGsPlanNumMap.get(dataKey)==null?0:totalGsPlanNumMap.get(dataKey);
			int acceptGsPlanNum = acceptGsPlanNumMap.get(dataKey)==null?0:acceptGsPlanNumMap.get(dataKey);
			int noAcceptGsPlanNum =noAcceptGsPlanNumMap.get(dataKey)==null?0:noAcceptGsPlanNumMap.get(dataKey);
			
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			/**
			 * 配货计划总数
			 */
			schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
			
			/**
			 * 已验收配货单
			 */
			schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
			
			/**
			 * 未验收配货单
			 */
			schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
			
			/**
			 * 验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setAcceptRate(acceptRate);
			
			//1 表示规范录入
			schGsAcceptSitStat.setSchStandardNum(standardNumMap.get(dataKey)==null?0:standardNumMap.get(dataKey));
			//2 表示补录
			schGsAcceptSitStat.setSchSupplementNum(supplementNumMap.get(dataKey)==null?0:supplementNumMap.get(dataKey));
			//3 表示逾期补录
			schGsAcceptSitStat.setSchBeOverdueNum(beOverdueNumMap.get(dataKey)==null?0:beOverdueNumMap.get(dataKey));
			//4 表示无数据
			schGsAcceptSitStat.setSchNoDataNum(noDataNumMap.get(dataKey)==null?0:noDataNumMap.get(dataKey));
			
			//标准验收率
			schStandardRate = 0;
			if(totalGsPlanNum > 0) {
				schStandardRate = 100 * ((float) schGsAcceptSitStat.getSchStandardNum() / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schGsAcceptSitStat.setSchStandardRate(schStandardRate);
			
			schGsAcceptSitStatMap.put(entry.getKey(), schGsAcceptSitStat);
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
			Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap) {
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
		
		int[][] standardNums = new int[dateCount][distCount]; 
		int[][] supplementNums  = new int[dateCount][distCount]; 
		int[][] beOverdueNums = new int[dateCount][distCount]; 
		int[][] noDataNums = new int[dateCount][distCount];
		
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
					//未验收学校
					for(int j = -2; j <= 2; j++) {
						field = "school-area" + "_" + curDistId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							noAcceptSchNums[k][i] += curConSchNum;
						}
					}
					
					// 区域配货计划总数
					field = "area" + "_" + curDistId;
					totalGsPlanNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						totalGsPlanNums[k][i] = Integer.parseInt(keyVal);
						if(totalGsPlanNums[k][i] < 0)
							totalGsPlanNums[k][i] = 0;
					}
					
					
					//操作状态对应的数量
					field = "dis-school-area" + "_" + curDistId + "_disstatus_"+"1";
					standardNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						standardNums[k][i] = Integer.parseInt(keyVal);
						if(standardNums[k][i] < 0)
							standardNums[k][i] = 0;
					}
					
					
					field = "dis-school-area" + "_" + curDistId + "_disstatus_"+"2";
					supplementNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						supplementNums[k][i] = Integer.parseInt(keyVal);
						if(supplementNums[k][i] < 0)
							supplementNums[k][i] = 0;
					}
					
					field = "dis-school-area" + "_" + curDistId + "_disstatus_"+"3";
					beOverdueNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						beOverdueNums[k][i] = Integer.parseInt(keyVal);
						if(beOverdueNums[k][i] < 0)
							beOverdueNums[k][i] = 0;
					}
					
					field = "dis-school-area" + "_" + curDistId + "_disstatus_"+"4";
					noDataNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						noDataNums[k][i] = Integer.parseInt(keyVal);
						if(noDataNums[k][i] < 0)
							noDataNums[k][i] = 0;
					}
				}
			}
		}
		
		for(int i = 0; i < distCount; i++) {
			acceptRate=0;
			//学校验收率
			schAcceptRate=0;
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
			int acceptSchNum = 0;
			int noAcceptSchNum =0;
			
			int standardNum = 0;
			int supplementNum = 0;
			int beOverdueNum = 0;
			int noDataNum = 0;
			float schStandardRate = 0;
			for(int k = 0; k < dates.length; k++) {
				totalGsPlanNum += totalGsPlanNums[k][i];
				acceptGsPlanNum += acceptGsPlanNums[k][i];
				noAcceptGsPlanNum +=noAcceptGsPlanNums[k][i];
				acceptSchNum += acceptSchNums[k][i];
				noAcceptSchNum +=noAcceptSchNums[k][i];
				
				standardNum += standardNums[k][i];
				supplementNum += supplementNums[k][i];
				beOverdueNum += beOverdueNums[k][i];
				noDataNum += noDataNums[k][i];
			}
			
			//验收率
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			int shouldAccSchNum = acceptSchNum + noAcceptSchNum;
			//学校验收率
			if(shouldAccSchNum > 0) {
				schAcceptRate = 100 * ((float) acceptSchNum / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			
			SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(curTdd.getName());
			if(schGsAcceptSitStat==null) {
				schGsAcceptSitStat = new SchGsAcceptSitStat();
				//区域名称
				schGsAcceptSitStat.setStatPropName(curTdd.getName());
			}
			/**
			 * 配货计划总数
			 */
			schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
			
			/**
			 * 已验收配货单
			 */
			schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
			
			/**
			 * 未验收配货单
			 */
			schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
			
			/**
			 * 验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setAcceptRate(acceptRate);
			
			/**
			 * 应验收学校数
			 */
			schGsAcceptSitStat.setShouldAccSchNum(shouldAccSchNum);
			
			/**
			 * 未验收学校数
			 */
			schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNum);
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setSchAcceptRate(schAcceptRate);
			
			//1 表示规范录入
			schGsAcceptSitStat.setSchStandardNum(standardNum);
			//2 表示补录
			schGsAcceptSitStat.setSchSupplementNum(supplementNum);
			//3 表示逾期补录
			schGsAcceptSitStat.setSchBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schGsAcceptSitStat.setSchNoDataNum(noDataNum);
			
			//标准验收率
			schStandardRate = 0;
			if(shouldAccSchNum > 0) {
				schStandardRate = 100 * ((float) schGsAcceptSitStat.getSchStandardNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schGsAcceptSitStat.setSchStandardRate(schStandardRate);
			
			
			schGsAcceptSitStatMap.put(curTdd.getName(), schGsAcceptSitStat);
		}
	}
	
	
	/**
	 * 获取配送信息：配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAccDistrInfoByDeaprtment(String departmentId,String[] dates,List<DepartmentObj> deparmentList,
			Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap) {
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
		

		int[][] standardNums = new int[dateCount][distCount]; 
		int[][] supplementNums  = new int[dateCount][distCount]; 
		int[][] beOverdueNums = new int[dateCount][distCount]; 
		int[][] noDataNums = new int[dateCount][distCount];
		
		float acceptRate = 0;
		float schAcceptRate = 0;
		float schStandardRate = 0F;
		
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
					//未验收学校
					for(int j = -2; j <= 2; j++) {
						field = "school-department" + "_" + curDepartmentId + "_" + "status" + "_" + j;
						keyVal = schIdToPlatoonMap.get(field);
						if(keyVal != null) {
							int curConSchNum = Integer.parseInt(keyVal);
							if(curConSchNum < 0)
								curConSchNum = 0;
							noAcceptSchNums[k][i] += curConSchNum;
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
					
					
					//操作状态对应的数量
					field = "dis-school-department" + "_" + curDepartmentId + "_disstatus_"+"1";
					standardNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						standardNums[k][i] = Integer.parseInt(keyVal);
						if(standardNums[k][i] < 0)
							standardNums[k][i] = 0;
					}
					
					
					field = "dis-school-department" + "_" + curDepartmentId + "_disstatus_"+"2";
					supplementNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						supplementNums[k][i] = Integer.parseInt(keyVal);
						if(supplementNums[k][i] < 0)
							supplementNums[k][i] = 0;
					}
					
					field = "dis-school-department" + "_" + curDepartmentId + "_disstatus_"+"3";
					beOverdueNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						beOverdueNums[k][i] = Integer.parseInt(keyVal);
						if(beOverdueNums[k][i] < 0)
							beOverdueNums[k][i] = 0;
					}
					
					field = "dis-school-department" + "_" + curDepartmentId + "_disstatus_"+"4";
					noDataNums[k][i] = 0;
					keyVal = schIdToPlatoonMap.get(field);
					if(keyVal != null) {
						noDataNums[k][i] = Integer.parseInt(keyVal);
						if(noDataNums[k][i] < 0)
							noDataNums[k][i] = 0;
					}
					
				}
			}
		}
		
		for(int i = 0; i < distCount; i++) {
			acceptRate=0;
			//学校验收率
			schAcceptRate=0;
			schStandardRate = 0F;
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
			int acceptSchNum = 0;
			int noAcceptSchNum =0;

			int standardNum = 0;
			int supplementNum = 0;
			int beOverdueNum = 0;
			int noDataNum = 0;
			
			for(int k = 0; k < dates.length; k++) {
				totalGsPlanNum += totalGsPlanNums[k][i];
				acceptGsPlanNum += acceptGsPlanNums[k][i];
				noAcceptGsPlanNum +=noAcceptGsPlanNums[k][i];
				acceptSchNum += acceptSchNums[k][i];
				noAcceptSchNum +=noAcceptSchNums[k][i];
				
				standardNum += standardNums[k][i];
				supplementNum += supplementNums[k][i];
				beOverdueNum += beOverdueNums[k][i];
				noDataNum += noDataNums[k][i];
			}
			
			//验收率
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			int shouldAccSchNum = acceptSchNum + noAcceptSchNum;
			//学校验收率
			if(shouldAccSchNum > 0) {
				schAcceptRate = 100 * ((float) acceptSchNum / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			
			SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(departmentObj.getDepartmentName());
			if(schGsAcceptSitStat==null) {
				schGsAcceptSitStat = new SchGsAcceptSitStat();
				//区域名称
				schGsAcceptSitStat.setStatPropName(departmentObj.getDepartmentName());
			}
			/**
			 * 配货计划总数
			 */
			schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
			
			/**
			 * 已验收配货单
			 */
			schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
			
			/**
			 * 未验收配货单
			 */
			schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
			
			/**
			 * 验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setAcceptRate(acceptRate);
			
			/**
			 * 应验收学校数
			 */
			schGsAcceptSitStat.setShouldAccSchNum(shouldAccSchNum);
			
			/**
			 * 未验收学校数
			 */
			schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNum);
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setSchAcceptRate(schAcceptRate);
			

			//1 表示规范录入
			schGsAcceptSitStat.setSchStandardNum(standardNum);
			//2 表示补录
			schGsAcceptSitStat.setSchSupplementNum(supplementNum);
			//3 表示逾期补录
			schGsAcceptSitStat.setSchBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schGsAcceptSitStat.setSchNoDataNum(noDataNum);
			
			//标准验收率
			schStandardRate = 0;
			if(shouldAccSchNum > 0) {
				schStandardRate = 100 * ((float) schGsAcceptSitStat.getSchStandardNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schGsAcceptSitStat.setSchStandardRate(schStandardRate);
			
			
			schGsAcceptSitStatMap.put(departmentObj.getDepartmentName(), schGsAcceptSitStat);
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
			Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap,DbHiveGsService dbHiveGsService) {
		// 当天排菜学校总数
		int distCount = tedList.size();
		
		float acceptRate = 0;
		float schAcceptRate = 0;
		
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
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
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
				
				if(schDishCommon.getDisSealStatus() !=null && !"-1".equals(schDishCommon.getDisSealStatus())) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if("1".equals(schDishCommon.getDisSealStatus())) {
						standardNumMap.put(schDishCommon.getDistId(), 
								(standardNumMap.get(schDishCommon.getDistId())==null?0:standardNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getSchoolTotal());
					}else if("2".equals(schDishCommon.getDisSealStatus())) {
						supplementNumMap.put(schDishCommon.getDistId(), 
								(supplementNumMap.get(schDishCommon.getDistId())==null?0:supplementNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getSchoolTotal());
					}else if("3".equals(schDishCommon.getDisSealStatus())) {
						beOverdueNumMap.put(schDishCommon.getDistId(), 
								(beOverdueNumMap.get(schDishCommon.getDistId())==null?0:beOverdueNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getSchoolTotal());
					}else if("4".equals(schDishCommon.getDisSealStatus())) {
						noDataNumMap.put(schDishCommon.getDistId(), 
								(noDataNumMap.get(schDishCommon.getDistId())==null?0:noDataNumMap.get(schDishCommon.getDistId())) 
								+ schDishCommon.getSchoolTotal());
					}
				}
			}
		}
		
		//标准验收率
		float schStandardRate = 0;
		for(int i = 0; i < distCount; i++) {
			acceptRate=0;
			//学校验收率
			schAcceptRate=0;
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
			
			//验收率
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			int shouldAccSchNum = acceptSchNum + noAcceptSchNum;
			//学校验收率
			if(shouldAccSchNum > 0) {
				schAcceptRate = 100 * ((float) acceptSchNum / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			
			SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(curTdd.getName());
			if(schGsAcceptSitStat==null) {
				schGsAcceptSitStat = new SchGsAcceptSitStat();
				//区域名称
				schGsAcceptSitStat.setStatPropName(curTdd.getName());
			}
			/**
			 * 配货计划总数
			 */
			schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
			
			/**
			 * 已验收配货单
			 */
			schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
			
			/**
			 * 未验收配货单
			 */
			schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
			
			/**
			 * 验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setAcceptRate(acceptRate);
			
			/**
			 * 应验收学校数
			 */
			schGsAcceptSitStat.setShouldAccSchNum(shouldAccSchNum);
			
			/**
			 * 未验收学校数
			 */
			schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNum);
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setSchAcceptRate(schAcceptRate);
			

			//1 表示规范录入
			schGsAcceptSitStat.setSchStandardNum(standardNumMap.get(String.valueOf(curDistId))==null?0:standardNumMap.get(String.valueOf(curDistId)));
			//2 表示补录
			schGsAcceptSitStat.setSchSupplementNum(supplementNumMap.get(String.valueOf(curDistId))==null?0:supplementNumMap.get(String.valueOf(curDistId)));
			//3 表示逾期补录
			schGsAcceptSitStat.setSchBeOverdueNum(beOverdueNumMap.get(String.valueOf(curDistId))==null?0:beOverdueNumMap.get(String.valueOf(curDistId)));
			//4 表示无数据
			schGsAcceptSitStat.setSchNoDataNum(noDataNumMap.get(String.valueOf(curDistId))==null?0:noDataNumMap.get(String.valueOf(curDistId)));
			
			//标准验收率
			schStandardRate = 0;
			if(shouldAccSchNum > 0) {
				schStandardRate = 100 * ((float) schGsAcceptSitStat.getSchStandardNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schGsAcceptSitStat.setSchStandardRate(schStandardRate);
			
			
			schGsAcceptSitStatMap.put(curTdd.getName(), schGsAcceptSitStat);
		}
	}
	
	/**
	 * 获取配送信息：配货计划数量、未确验收计划数量、验收率
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param acceptInfo
	 */
	private void getAccDistrInfoByDepartmentFromHive(String departmentId,String distId, String[] dates, List<DepartmentObj> departmentList,
			Map<String,SchGsAcceptSitStat> schGsAcceptSitStatMap,DbHiveGsService dbHiveGsService) {
		// 当天排菜学校总数
		int distCount = departmentList.size();
		
		float acceptRate = 0;
		float schAcceptRate = 0;
		
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
		
		//1 表示规范录入
		Map<String,Integer> standardNumMap = new HashMap<>();
		//2 表示补录
		Map<String,Integer> supplementNumMap = new HashMap<>();
		//3 表示逾期补录
		Map<String,Integer> beOverdueNumMap = new HashMap<>();
		//4 表示无数据
		Map<String,Integer> noDataNumMap = new HashMap<>();
		
		List<SchGsCommon> dishList = new ArrayList<>();
		dishList = dbHiveGsService.getGsList(DataKeyConfig.talbeLedgerMasterTotalD,listYearMonth, startDate, endDateAddOne, distId, null, 
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
				if(schDishCommon.getDisSealStatus() !=null && !"-1".equals(schDishCommon.getDisSealStatus())) {
					//1 表示规范录入
					//2 表示补录
					//3 表示逾期补录
					//4 表示无数据
					if("1".equals(schDishCommon.getDisSealStatus())) {
						standardNumMap.put(schDishCommon.getDepartmentId(), 
								(standardNumMap.get(schDishCommon.getDepartmentId())==null?0:standardNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getSchoolTotal());
					}else if("2".equals(schDishCommon.getDisSealStatus())) {
						supplementNumMap.put(schDishCommon.getDepartmentId(), 
								(supplementNumMap.get(schDishCommon.getDepartmentId())==null?0:supplementNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getSchoolTotal());
					}else if("3".equals(schDishCommon.getDisSealStatus())) {
						beOverdueNumMap.put(schDishCommon.getDepartmentId(), 
								(beOverdueNumMap.get(schDishCommon.getDepartmentId())==null?0:beOverdueNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getSchoolTotal());
					}else if("4".equals(schDishCommon.getDisSealStatus())) {
						noDataNumMap.put(schDishCommon.getDepartmentId(), 
								(noDataNumMap.get(schDishCommon.getDepartmentId())==null?0:noDataNumMap.get(schDishCommon.getDepartmentId())) 
								+ schDishCommon.getSchoolTotal());
					}
				}
			}
		}
		
		//标准验收率
		float schStandardRate = 0;
		for(int i = 0; i < distCount; i++) {
			acceptRate=0;
			//学校验收率
			schAcceptRate=0;
			DepartmentObj departmentObj = departmentList.get(i);
			String curDepartmentId = departmentObj.getDepartmentId();
			//判断是否按区域获取配货计划数据（distIdorSCName为空表示按省或直辖市级别获取数据）
			if(distId != null) {
				if(!curDepartmentId.equalsIgnoreCase(distId)) {
					continue ;
				}
			}
			
			int noAcceptSchNum = noAcceptSchNumsMap.get(curDepartmentId)==null?0:noAcceptSchNumsMap.get(curDepartmentId);
			int acceptSchNum = acceptSchNumsMap.get(curDepartmentId)==null?0:acceptSchNumsMap.get(curDepartmentId);
			int totalGsPlanNum = totalGsPlanNumMap.get(curDepartmentId)==null?0:totalGsPlanNumMap.get(curDepartmentId);
			int acceptGsPlanNum = acceptGsPlanNumMap.get(curDepartmentId)==null?0:acceptGsPlanNumMap.get(curDepartmentId);
			int  noAcceptGsPlanNum= noAcceptGsPlanNumMap.get(curDepartmentId)==null?0:noAcceptGsPlanNumMap.get(curDepartmentId);
			
			//验收率
			if(totalGsPlanNum > 0) {
				acceptRate = 100 * ((float) acceptGsPlanNum / (float) totalGsPlanNum);
				BigDecimal bd = new BigDecimal(acceptRate);
				acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				/*if (acceptRate > 100) {
					acceptRate = 100;
				}*/
			}
			
			int shouldAccSchNum = acceptSchNum + noAcceptSchNum;
			//学校验收率
			if(shouldAccSchNum > 0) {
				schAcceptRate = 100 * ((float) acceptSchNum / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schAcceptRate);
				schAcceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schAcceptRate > 100) {
					schAcceptRate = 100;
				}
			}
			
			
			SchGsAcceptSitStat schGsAcceptSitStat = schGsAcceptSitStatMap.get(departmentObj.getDepartmentName());
			if(schGsAcceptSitStat==null) {
				schGsAcceptSitStat = new SchGsAcceptSitStat();
				//区域名称
				schGsAcceptSitStat.setStatPropName(departmentObj.getDepartmentName());
			}
			/**
			 * 配货计划总数
			 */
			schGsAcceptSitStat.setTotalGsPlanNum(totalGsPlanNum);
			
			/**
			 * 已验收配货单
			 */
			schGsAcceptSitStat.setAcceptGsNum(acceptGsPlanNum);
			
			/**
			 * 未验收配货单
			 */
			schGsAcceptSitStat.setNoAcceptGsNum(noAcceptGsPlanNum);
			
			/**
			 * 验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setAcceptRate(acceptRate);
			
			/**
			 * 应验收学校数
			 */
			schGsAcceptSitStat.setShouldAccSchNum(shouldAccSchNum);
			
			/**
			 * 未验收学校数
			 */
			schGsAcceptSitStat.setNoAcceptSchNum(noAcceptSchNum);
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 已验收学校数
			 */
			schGsAcceptSitStat.setAcceptSchNum(acceptSchNum);
			
			/**
			 * 学校验收率，保留小数点有效数字两位
			 */
			schGsAcceptSitStat.setSchAcceptRate(schAcceptRate);
			

			//1 表示规范录入
			schGsAcceptSitStat.setSchStandardNum(standardNumMap.get(curDepartmentId)==null?0:standardNumMap.get(curDepartmentId));
			//2 表示补录
			schGsAcceptSitStat.setSchSupplementNum(supplementNumMap.get(curDepartmentId)==null?0:supplementNumMap.get(curDepartmentId));
			//3 表示逾期补录
			schGsAcceptSitStat.setSchBeOverdueNum(beOverdueNumMap.get(curDepartmentId)==null?0:beOverdueNumMap.get(curDepartmentId));
			//4 表示无数据
			schGsAcceptSitStat.setSchNoDataNum(noDataNumMap.get(curDepartmentId)==null?0:noDataNumMap.get(curDepartmentId));
			
			//标准验收率
			schStandardRate = 0;
			if(shouldAccSchNum > 0) {
				schStandardRate = 100 * ((float) schGsAcceptSitStat.getSchStandardNum() / (float) shouldAccSchNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schGsAcceptSitStat.setSchStandardRate(schStandardRate);
			
			
			schGsAcceptSitStatMap.put(departmentObj.getDepartmentName(), schGsAcceptSitStat);
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
	public SchGsAcceptSitStatDTO appModFunc(String token, String distName, String prefCity, String province,String startDate, String endDate, Integer statMode,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,EduSchoolService eduSchoolService,DbHiveGsService  dbHiveGsService) {
		
		SchGsAcceptSitStatDTO schGsAcceptSitStatDTO = null;
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
						schGsAcceptSitStatDTO = schGsAcceptSitStatFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						schGsAcceptSitStatDTO = schGsAcceptSitStatFuncOne(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates, tedList, db1Service, saasService,eduSchoolService);
					}else if (methodIndex==2) {
						schGsAcceptSitStatDTO = schGsAcceptSitStatFuncTwo(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates, tedList, 
								db1Service, saasService,eduSchoolService,dbHiveGsService);
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
						schGsAcceptSitStatDTO = schGsAcceptSitStatFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						schGsAcceptSitStatDTO = schGsAcceptSitStatFuncOne(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates, tedList, db1Service, saasService,eduSchoolService);
					}else if (methodIndex==2) {
						schGsAcceptSitStatDTO = schGsAcceptSitStatFuncTwo(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates, tedList, 
								db1Service, saasService,eduSchoolService,dbHiveGsService);
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
			schGsAcceptSitStatDTO = new SchGsAcceptSitStatDTO();
		}		

		
		return schGsAcceptSitStatDTO;
	}
}
