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
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.EduSchool;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishRsSitStat;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishRsSitStatDTO;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.service.edu.EduSchoolService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 3.2.44.	学校菜品留样情况统计应用模型
 * @author fengyang_xie
 *
 */
public class SchDishRsSitStatAppMod {
	private static final Logger logger = LogManager.getLogger(SchDishRsSitStatAppMod.class.getName());
	
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
			"   \"schDishRsSitStat\": [\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"  \"statPropName\":\"浦东新区\",\r\n" + 
			"   \"dishSchNum\":55,\r\n" + 
			"   \"noRsSchNum\":10,\r\n" + 
			"\"rsSchNum\":45,\r\n" + 
			"\"totalDishNum\":30,\r\n" + 
			"\"rsDishNum\":25,\r\n" + 
			"\"noRsDishNum\":5,\r\n" + 
			"\"rsRate\":36.33\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"  \"statPropName\":\"黄浦区\",\r\n" + 
			"   \"dishSchNum\":55,\r\n" + 
			"   \"noRsSchNum\":10,\r\n" + 
			"\"rsSchNum\":45,\r\n" + 
			"\"totalDishNum\":30,\r\n" + 
			"\"rsDishNum\":25,\r\n" + 
			"\"noRsDishNum\":5,\r\n" + 
			"\"rsRate\":36.33\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"statClassName\":null,\r\n" + 
			"\"statPropName\":\"静安区\",\r\n" + 
			"   \"dishSchNum\":55,\r\n" + 
			"   \"noRsSchNum\":10,\r\n" + 
			"\"rsSchNum\":45,\r\n" + 
			"\"totalDishNum\":30,\r\n" + 
			"\"rsDishNum\":25,\r\n" + 
			"\"noRsDishNum\":5,\r\n" + 
			"\"rsRate\":36.33\r\n" + 
			" } ],\r\n" + 
			"   \"msgId\":1\r\n" + 
			"}\r\n" + 
			"";
	
	/**
	 * 汇总数据
	 * @return
	 */
	private SchDishRsSitStatDTO schDishRsSitStatFunc(String distId,String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService ) {
		
		SchDishRsSitStatDTO schDishRsSitStatDTO = new SchDishRsSitStatDTO();
		
		List<SchDishRsSitStat> schDishRsSitStat = new ArrayList<SchDishRsSitStat>();
		schDishRsSitStatDTO.setSchDishRsSitStat(schDishRsSitStat);
		
		JSONObject jsStr = JSONObject.parseObject(tempData); 
		schDishRsSitStatDTO = (SchDishRsSitStatDTO) JSONObject.toJavaObject(jsStr,SchDishRsSitStatDTO.class);
		
		
		
		
		//时戳
		schDishRsSitStatDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		schDishRsSitStatDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return schDishRsSitStatDTO;
	}
	
	/**
	 * 汇总数据
	 * @return
	 */
	private SchDishRsSitStatDTO schDishRsSitStatFuncOne(String departmentId,String distId,String currDistName,Integer statMode,Integer subLevel,Integer compDep, String[] dates, List<TEduDistrictDo> tedList,Db1Service db1Service
			, SaasService saasService,EduSchoolService eduSchoolService ) {
		
		SchDishRsSitStatDTO schDishRsSitStatDTO = new SchDishRsSitStatDTO();
		
		List<SchDishRsSitStat> schDishRsSitStatList = new ArrayList<SchDishRsSitStat>();
		
		Map<String,SchDishRsSitStat> schDishRsSitStatMap= new LinkedHashMap<String,SchDishRsSitStat>();
		
		
		if(statMode == 0) {
			//========0:按区统计
			
			/**
			 * 排菜汇总(已排菜学校)
			 */
			//2019.07.19注释，注释原因，目前界面不显示排菜相关数据，且排菜改为从hive库中获取后，查询速度回慢
			/*Map<String,SchDishSitStat> schDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			SchDishSitStatAppMod.getDishInfoByArea(distId, dates, tedList,schDishSitStatMap,redisService);
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				SchDishRsSitStat schDishRsSitStat = new SchDishRsSitStat();
				schDishRsSitStat.setStatClassName(entry.getValue().getStatClassName());
				schDishRsSitStat.setStatPropName(entry.getValue().getStatPropName());
				schDishRsSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schDishRsSitStatMap.put(entry.getKey(), schDishRsSitStat);
			}*/
			
			/**
			 * 留样汇总
			 */
			 //获取留样汇总：留样率、未留样菜品个数
			 getRsInfoRsRateByArea(departmentId,distId, dates, tedList,schDishRsSitStatMap);
			 
			 //2019.03.28 注释原因：建模规则修改由detail改为total中获取
			 //获取留样汇总：未留样学校个数
			 //getRsInfoNoRsSchNumByArea(distId, dates, tedList,db1Service,schDishRsSitStatMap);
			
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
				SchDishRsSitStat schDishRsSitStat = new SchDishRsSitStat();
				schDishRsSitStat.setStatClassName(entry.getValue().getStatClassName());
				schDishRsSitStat.setStatPropName(entry.getValue().getStatPropName());
				schDishRsSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schDishRsSitStatMap.put(entry.getKey(), schDishRsSitStat);
			}*/
			
			/**
			 * 留样汇总
			 */
			 //获取留样汇总：未留样学校个数、留样率、未留样菜品个数、留样率
			 //getRsInfoNoRsSchNumByNature(distId, dates, schoolPropertyMap,db1Service,schDishRsSitStatMap,eduSchoolService);
			 //2019-04-01 更改取数规则：改为新的建模数据
			 getRsInfoNoRsSchNumByNatureTwo(departmentId,distId, dates, schoolPropertyMap, db1Service, schDishRsSitStatMap, eduSchoolService);
			
		}else if (statMode == 2) {
			//========2:按学校学制统计
			
			/**
			 * 排菜汇总(已排菜学校)
			 */
			//2019.07.19注释，注释原因，目前界面不显示排菜相关数据，且排菜改为从hive库中获取后，查询速度回慢
			/*Map<String,SchDishSitStat> schDishSitStatMap= new LinkedHashMap<String,SchDishSitStat>();
			SchDishSitStatAppMod.getDishInfoBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap,schDishSitStatMap,redisService);
			for(Map.Entry<String,SchDishSitStat> entry : schDishSitStatMap.entrySet()) {
				SchDishRsSitStat schDishRsSitStat = new SchDishRsSitStat();
				schDishRsSitStat.setStatClassName(entry.getValue().getStatClassName());
				schDishRsSitStat.setStatPropName(entry.getValue().getStatPropName());
				schDishRsSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schDishRsSitStatMap.put(entry.getKey(), schDishRsSitStat);
			}*/
			
			/**
			 * 留样汇总
			 */
			 //获取留样汇总：未留样学校个数、留样率、未留样菜品个数、留样率
			 //getRsInfoNoRsSchNumBySchoolType(distId, dates, AppModConfig.schTypeIdToNameMap,db1Service,schDishRsSitStatMap,eduSchoolService);
			 ////2019-04-01 更改取数规则：改为新的建模数据
			 getRsInfoNoRsSchNumBySchoolTypeTwo(departmentId,distId, dates, AppModConfig.schTypeIdToNameMap, db1Service, schDishRsSitStatMap, eduSchoolService);
			//计算每种【学制分类】的数量
			Map<String,SchDishRsSitStat> newSchDishRsSitStatMap= new LinkedHashMap<String,SchDishRsSitStat>();
			String statClassName="";
			SchDishRsSitStat schDishRsSitStatSum = new SchDishRsSitStat();
			for(Map.Entry<String,SchDishRsSitStat> entry : schDishRsSitStatMap.entrySet()) {
				
				
				
				if(!"".equals(statClassName) && statClassName!=null && !statClassName.equals(entry.getValue().getStatClassName())) {
					newSchDishRsSitStatMap.put(statClassName+"_小计", schDishRsSitStatSum);
					schDishRsSitStatSum = new SchDishRsSitStat();
				}

				//计算数据
				setSchoolPropertSumData(schDishRsSitStatSum, entry);
				statClassName = entry.getValue().getStatClassName();
				schDishRsSitStatSum.setStatClassName(statClassName);
				schDishRsSitStatSum.setStatPropName("小计");
				setRate(schDishRsSitStatSum);
				
				newSchDishRsSitStatMap.put(entry.getKey(), entry.getValue());
				
			}
			newSchDishRsSitStatMap.put("其他"+"_小计", schDishRsSitStatSum);
			schDishRsSitStatMap = newSchDishRsSitStatMap;
			
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
				SchDishRsSitStat schDishRsSitStat = new SchDishRsSitStat();
				schDishRsSitStat.setStatClassName(entry.getValue().getStatClassName());
				schDishRsSitStat.setStatPropName(entry.getValue().getStatPropName());
				schDishRsSitStat.setDishSchNum(entry.getValue().getDishSchNum());
				schDishRsSitStatMap.put(entry.getKey(), schDishRsSitStat);
			}*/
			
			/**
			 * 留样汇总
			 */
			 //获取留样汇总：未留样学校个数、留样率、未留样菜品个数、留样率
			 getRsInfoNoRsSchNumBySlave(departmentId,distId, dates, compDepNameToSubLevelNameMap,db1Service,schDishRsSitStatMap,eduSchoolService);
		    
		    //计算每种【学制分类】的数量
			Map<String,SchDishRsSitStat> newSchDishRsSitStatMap= new LinkedHashMap<String,SchDishRsSitStat>();
			String statClassName="";
			SchDishRsSitStat schDishRsSitStatSum = new SchDishRsSitStat();
			for(Map.Entry<String,SchDishRsSitStat> entry : schDishRsSitStatMap.entrySet()) {
				if(!"".equals(statClassName) && !statClassName.equals(entry.getValue().getStatClassName())) {
					newSchDishRsSitStatMap.put(statClassName+"_小计", schDishRsSitStatSum);
					schDishRsSitStatSum = new SchDishRsSitStat();
				}

				//计算数据
				setSchoolPropertSumData(schDishRsSitStatSum, entry);
				statClassName = entry.getValue().getStatClassName();
				schDishRsSitStatSum.setStatClassName(statClassName);
				schDishRsSitStatSum.setStatPropName("小计");
				//计算比率：包括排菜率、留样率、留样率、预警处理率
                setRate(schDishRsSitStatSum);
				newSchDishRsSitStatMap.put(entry.getKey(), entry.getValue());
				
			}
			newSchDishRsSitStatMap.put("其他"+"_小计", schDishRsSitStatSum);
			schDishRsSitStatMap = newSchDishRsSitStatMap;
			
			//循环技术小计的百分比（包括排菜率、留样率、留样率、预警处理率、）
			for(Map.Entry<String,SchDishRsSitStat> entry : schDishRsSitStatMap.entrySet()) {
				if(entry.getKey().contains("小计")) {
					schDishRsSitStatSum = entry.getValue();
				}
			}
			
		}else if(statMode == 4) {
			//========4:按管理部门
			
			/**
			 * 留样汇总
			 */
			//管理部门
			//获取用户信息，用于匹配部门的编辑人
			DepartmentObj departmentObj = new DepartmentObj();
			departmentObj.setDepartmentId(departmentId);
			List<DepartmentObj> deparmentList =  db1Service.getDepartmentObjList(departmentObj,null, -1, -1);	
			
			 //获取留样汇总：留样率、未留样菜品个数
			getRsInfoRsRateByDepartment(departmentId, dates, deparmentList, schDishRsSitStatMap);
			
			//计算每种【管理】的数量
			Map<String,SchDishRsSitStat> newSchDishRsSitStatMap= new LinkedHashMap<String,SchDishRsSitStat>();
			String statPropName="";
			SchDishRsSitStat schDishSitStatSum = new SchDishRsSitStat();
			SchDishRsSitStat preSchDishSitStatSum = new SchDishRsSitStat();
			int sumIndex = 0;
			for(Map.Entry<String,SchDishRsSitStat> entry : schDishRsSitStatMap.entrySet()) {
				sumIndex ++ ;
				if(sumIndex == 17 || sumIndex ==18 || sumIndex == 19) {
					newSchDishRsSitStatMap.put(statPropName+"_小计", schDishSitStatSum);
					schDishSitStatSum = new SchDishRsSitStat();
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
				
				newSchDishRsSitStatMap.put(entry.getKey(), entry.getValue());
				
				if(sumIndex == 16 || sumIndex ==17 || sumIndex == 18) {
					try {
						preSchDishSitStatSum = new SchDishRsSitStat();
						BeanUtils.copyProperties(preSchDishSitStatSum, schDishSitStatSum);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				
			}
			newSchDishRsSitStatMap.put("小计", schDishSitStatSum);
			schDishRsSitStatMap = newSchDishRsSitStatMap;
			
		}
		
		
		
		schDishRsSitStatMap.values();
		Collection<SchDishRsSitStat> valueCollection = schDishRsSitStatMap.values();
	    schDishRsSitStatList = new ArrayList<SchDishRsSitStat>(valueCollection);
		
		schDishRsSitStatDTO.setSchDishRsSitStat(schDishRsSitStatList);
		//时戳
		schDishRsSitStatDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		schDishRsSitStatDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return schDishRsSitStatDTO;
	}

	/**
	 * 学习学制每个分类小计
	 * @param schDishRsSitStatSum
	 * @param entry
	 */
	private void setSchoolPropertSumData(SchDishRsSitStat schDishRsSitStatSum, Map.Entry<String, SchDishRsSitStat> entry) {
		
		/**
		 * 已排菜学校数量
		 */
		schDishRsSitStatSum.setDishSchNum(schDishRsSitStatSum.getDishSchNum()+entry.getValue().getDishSchNum());
		
		/**
		 * 应留样学校数
		 */
		schDishRsSitStatSum.setShouldRsSchNum(schDishRsSitStatSum.getShouldRsSchNum()+entry.getValue().getShouldRsSchNum());
		
		/**
		 * 未留样学校数
		 */
		schDishRsSitStatSum.setNoRsSchNum(schDishRsSitStatSum.getNoRsSchNum()+entry.getValue().getNoRsSchNum());
		
		/**
		 * 已留样学校
		 */
		schDishRsSitStatSum.setRsSchNum(schDishRsSitStatSum.getRsSchNum()+entry.getValue().getRsSchNum());
		
		/**
		 * 菜品数量
		 */
		schDishRsSitStatSum.setTotalDishNum(schDishRsSitStatSum.getTotalDishNum()+entry.getValue().getTotalDishNum());
		
		/**
		 * 已留样菜品
		 */
		schDishRsSitStatSum.setRsDishNum(schDishRsSitStatSum.getRsDishNum()+entry.getValue().getRsDishNum());
		
		/**
		 * 未留样菜品
		 */
		schDishRsSitStatSum.setNoRsDishNum(schDishRsSitStatSum.getNoRsDishNum()+entry.getValue().getNoRsDishNum());
		
		schDishRsSitStatSum.setSchStandardNum(schDishRsSitStatSum.getSchStandardNum()+entry.getValue().getSchStandardNum());
		schDishRsSitStatSum.setSchSupplementNum(schDishRsSitStatSum.getSchSupplementNum()+entry.getValue().getSchSupplementNum());
		schDishRsSitStatSum.setSchBeOverdueNum(schDishRsSitStatSum.getSchBeOverdueNum()+entry.getValue().getSchBeOverdueNum());
		schDishRsSitStatSum.setSchNoDataNum(schDishRsSitStatSum.getSchNoDataNum()+entry.getValue().getSchNoDataNum());
		schDishRsSitStatSum.setSchStandardRate(schDishRsSitStatSum.getSchStandardRate()+entry.getValue().getSchStandardRate());
		
	}
	
	private void setRate(SchDishRsSitStat schDishRsSitStatSum) {
		//留样率
		float distDishRate = 0;
		if(schDishRsSitStatSum.getTotalDishNum() > 0) {
			distDishRate = 100 * ((float) schDishRsSitStatSum.getRsDishNum()/ (float) schDishRsSitStatSum.getTotalDishNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		schDishRsSitStatSum.setRsRate(distDishRate);
		
		//学校留样率
		distDishRate = 0;
		if(schDishRsSitStatSum.getShouldRsSchNum() > 0) {
			distDishRate = 100 * ((float) (schDishRsSitStatSum.getShouldRsSchNum() - schDishRsSitStatSum.getNoRsSchNum()) / (float) schDishRsSitStatSum.getShouldRsSchNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		schDishRsSitStatSum.setSchRsRate(distDishRate);
		
		//学校标准验收率
		distDishRate = 0;
		if(schDishRsSitStatSum.getShouldRsSchNum()> 0) {
			distDishRate = 100 * ((float) (schDishRsSitStatSum.getSchStandardNum()) / (float) schDishRsSitStatSum.getShouldRsSchNum());
			BigDecimal bd = new BigDecimal(distDishRate);
			distDishRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (distDishRate > 100) {
				distDishRate = 100;
			}
		}
		schDishRsSitStatSum.setSchStandardRate(distDishRate);
	}
	/**
	 * 获取留样汇总：未留样学校个数、已留样学校个数
	 * @param distId
	 * @param dates
	 * @param db1Service
	 * @return
	 */
	private void getRsInfoNoRsSchNumByArea(String distId, String[] dates,List<TEduDistrictDo> tedList, Db1Service db1Service,
			Map<String,SchDishRsSitStat> schDishRsSitStatMap) {
		//未留样学校个数
		Map<String,Set<String>> noRsSchNumSchoolMap = new HashMap<String,Set<String>>();
		//已留样学校个数
		Map<String,Set<String>> rsSchNumSchoolMap = new HashMap<String,Set<String>>();
		
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
						
						if("未留样".equals(keyVals[14])) {
							schoolSet=noRsSchNumSchoolMap.get(keyVals[1]);
							if(schoolSet == null) {
								schoolSet = new HashSet<String>();
							}
							schoolSet.add(keyVals[5]);
							
							noRsSchNumSchoolMap.put(keyVals[1], schoolSet);
							
						}else if("已留样".equals(keyVals[14])){
							schoolSet=rsSchNumSchoolMap.get(keyVals[1]);
							if(schoolSet == null) {
								schoolSet = new HashSet<String>();
							}
							schoolSet.add(keyVals[5]);
							
							rsSchNumSchoolMap.put(keyVals[1], schoolSet);
						}
						
						
					}
					else {
						logger.info("菜品留样："+ curKey + "，格式错误！");
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
			SchDishRsSitStat schDishRsSitStat = schDishRsSitStatMap.get(curTdd.getName());
			if(schDishRsSitStat==null) {
				schDishRsSitStat = new SchDishRsSitStat();
				//区域名称
				schDishRsSitStat.setStatPropName(curTdd.getName());
			}
			/**
			 * 未留样学校数
			 */
			schDishRsSitStat.setNoRsSchNum(noRsSchNumSchoolMap.get(curDistId)==null?0:noRsSchNumSchoolMap.get(curDistId).size());
			
			/**
			 * 已留样学校数
			 */
			schDishRsSitStat.setRsSchNum(rsSchNumSchoolMap.get(curDistId)==null?0:rsSchNumSchoolMap.get(curDistId).size());
			
			schDishRsSitStatMap.put(curTdd.getName(), schDishRsSitStat);
			
			
			
		}
		
	}

	/**
	 * 获取留样汇总：菜品数量、留样率、未留样菜品、已留样菜品
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param rsInfo
	 */
	private void getRsInfoRsRateByArea(String departmentId,String distId, String[] dates, List<TEduDistrictDo> tedList,
			Map<String,SchDishRsSitStat> schDishRsSitStatMap) {
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
		
		int[][] distRsSchNums = new int[dateCount][distCount]; //留样学校
		int[][] distNoRsSchNums = new int[dateCount][distCount];//未留样学校
		
		int[][] standardNums = new int[dateCount][distCount]; 
		int[][] supplementNums  = new int[dateCount][distCount]; 
		int[][] beOverdueNums = new int[dateCount][distCount]; 
		int[][] noDataNums = new int[dateCount][distCount];
		
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
					String[] curKeys = curKey.split("_");
					
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
						
						//操作状态对应的数量
						fieldPrefix = "re-school-area" + "_" + curDistId + "_reservestatus_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							if("1".equals(curKeys[3])) {
								standardNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									standardNums[k][i] = Integer.parseInt(keyVal);
									if(standardNums[k][i] < 0)
										standardNums[k][i] = 0;
								}

							}else if ("2".equals(curKeys[3])) {
								supplementNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									supplementNums[k][i] = Integer.parseInt(keyVal);
									if(supplementNums[k][i] < 0)
										supplementNums[k][i] = 0;
								}
							}else if ("3".equals(curKeys[3])) {
								beOverdueNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									beOverdueNums[k][i] = Integer.parseInt(keyVal);
									if(beOverdueNums[k][i] < 0)
										beOverdueNums[k][i] = 0;
								}
							}else if ("4".equals(curKeys[3])) {
								noDataNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									noDataNums[k][i] = Integer.parseInt(keyVal);
									if(noDataNums[k][i] < 0)
										noDataNums[k][i] = 0;
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
		
		
		float schRsRate = 0F;
		float schStandardRate = 0F;
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
			
			int standardNum = 0;
			int supplementNum = 0;
			int beOverdueNum = 0;
			int noDataNum = 0;
			
			for (int k = 0; k < dates.length; k++) {
				totalDishNum += totalDishNums[k][i];
				distRsDishNum += distRsDishNums[k][i];
				distNoRsDishNum += distNoRsDishNums[k][i];
				
				totalDistRsSchNum += distRsSchNums[k][i];
				totalDistNoRsSchNum += distNoRsSchNums[k][i];
				
				standardNum += standardNums[k][i];
				supplementNum += supplementNums[k][i];
				beOverdueNum += beOverdueNums[k][i];
				noDataNum += noDataNums[k][i];
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
			
			SchDishRsSitStat schDishRsSitStat = schDishRsSitStatMap.get(curTdd.getName());
			if(schDishRsSitStat==null) {
				schDishRsSitStat = new SchDishRsSitStat();
				//区域名称
				schDishRsSitStat.setStatPropName(curTdd.getName());
			}
			/**
			 * 菜品数量
			 */
			schDishRsSitStat.setTotalDishNum(totalDishNum);
			/**
			 * 未留样菜品
			 */
			schDishRsSitStat.setNoRsDishNum(distNoRsDishNum);
			
			/**
			 * 已留样菜品
			 */
			schDishRsSitStat.setRsDishNum(distRsDishNum);
			/**
			 * 留样率
			 */
			schDishRsSitStat.setRsRate(distRsRate[i]);
			
			/**
			 * 应留样学校数
			 */
			schDishRsSitStat.setShouldRsSchNum(shouldRsSchNum);
			
			/**
			 * 未留样学校数
			 */
			schDishRsSitStat.setNoRsSchNum(totalDistNoRsSchNum);
			
			/**
			 * 已留样学校数
			 */
			schDishRsSitStat.setRsSchNum(totalDistRsSchNum);
			
			/**
			 * 学校留样率
			 */
			schDishRsSitStat.setSchRsRate(schRsRate);
			
			//1 表示规范录入
			schDishRsSitStat.setSchStandardNum(standardNum);
			//2 表示补录
			schDishRsSitStat.setSchSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishRsSitStat.setSchBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishRsSitStat.setSchNoDataNum(noDataNum);
			
			//标准验收率
			schStandardRate = 0;
			if(shouldRsSchNum > 0) {
				schStandardRate = 100 * ((float) schDishRsSitStat.getSchStandardNum() / (float) shouldRsSchNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schDishRsSitStat.setSchStandardRate(schStandardRate);
			
			schDishRsSitStatMap.put(curTdd.getName(), schDishRsSitStat);
		}
	   
	    
		
	}
	
	/**
	 * 获取留样汇总：菜品数量、留样率、未留样菜品、已留样菜品
	 * @param distId
	 * @param dates
	 * @param tedList
	 * @param rsInfo
	 */
	private void getRsInfoRsRateByDepartment(String departmentId,String[] dates, List<DepartmentObj> departmentList,
			Map<String,SchDishRsSitStat> schDishRsSitStatMap) {
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
		
		int[][] distRsSchNums = new int[dateCount][distCount]; //留样学校
		int[][] distNoRsSchNums = new int[dateCount][distCount];//未留样学校
		
		int[][] standardNums = new int[dateCount][distCount]; 
		int[][] supplementNums  = new int[dateCount][distCount]; 
		int[][] beOverdueNums = new int[dateCount][distCount]; 
		int[][] noDataNums = new int[dateCount][distCount];
		
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
					String[] curKeys = curKey.split("_");
					
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
								
								//区域菜品总数
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									totalDishNums[k][i] += Integer.parseInt(keyVal);
								}
							}
						}
						
						// 区域学校留样和未留样数
						fieldPrefix = "school-department" + "_" + curDepartmentId + "_";
						if (curKey.indexOf(fieldPrefix) == 0) {
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
						
						//操作状态对应的数量
						fieldPrefix = "re-school-department" + "_" + curDepartmentId + "_reservestatus_";
						if (curKey.indexOf(fieldPrefix) == 0) {
							if("1".equals(curKeys[3])) {
								standardNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									standardNums[k][i] = Integer.parseInt(keyVal);
									if(standardNums[k][i] < 0)
										standardNums[k][i] = 0;
								}

							}else if ("2".equals(curKeys[3])) {
								supplementNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									supplementNums[k][i] = Integer.parseInt(keyVal);
									if(supplementNums[k][i] < 0)
										supplementNums[k][i] = 0;
								}
							}else if ("3".equals(curKeys[3])) {
								beOverdueNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									beOverdueNums[k][i] = Integer.parseInt(keyVal);
									if(beOverdueNums[k][i] < 0)
										beOverdueNums[k][i] = 0;
								}
							}else if ("4".equals(curKeys[3])) {
								noDataNums[k][i] = 0;
								keyVal = gcRetentiondishtotalMap.get(curKey);
								if(keyVal != null) {
									noDataNums[k][i] = Integer.parseInt(keyVal);
									if(noDataNums[k][i] < 0)
										noDataNums[k][i] = 0;
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
				field = "deparment" + "_" + curDepartmentId;
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
		
		
		float schRsRate = 0F;
		float schStandardRate = 0F;
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
			
			int standardNum = 0;
			int supplementNum = 0;
			int beOverdueNum = 0;
			int noDataNum = 0;
			
			for (int k = 0; k < dates.length; k++) {
				totalDishNum += totalDishNums[k][i];
				distRsDishNum += distRsDishNums[k][i];
				distNoRsDishNum += distNoRsDishNums[k][i];
				
				totalDistRsSchNum += distRsSchNums[k][i];
				totalDistNoRsSchNum += distNoRsSchNums[k][i];
				
				standardNum += standardNums[k][i];
				supplementNum += supplementNums[k][i];
				beOverdueNum += beOverdueNums[k][i];
				noDataNum += noDataNums[k][i];
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
			
			SchDishRsSitStat schDishRsSitStat = schDishRsSitStatMap.get(departmentObj.getDepartmentName());
			if(schDishRsSitStat==null) {
				schDishRsSitStat = new SchDishRsSitStat();
				//区域名称
				schDishRsSitStat.setStatPropName(departmentObj.getDepartmentName());
			}
			/**
			 * 菜品数量
			 */
			schDishRsSitStat.setTotalDishNum(totalDishNum);
			/**
			 * 未留样菜品
			 */
			schDishRsSitStat.setNoRsDishNum(distNoRsDishNum);
			
			/**
			 * 已留样菜品
			 */
			schDishRsSitStat.setRsDishNum(distRsDishNum);
			/**
			 * 留样率
			 */
			schDishRsSitStat.setRsRate(distRsRate[i]);
			
			/**
			 * 应留样学校数
			 */
			schDishRsSitStat.setShouldRsSchNum(shouldRsSchNum);
			
			/**
			 * 未留样学校数
			 */
			schDishRsSitStat.setNoRsSchNum(totalDistNoRsSchNum);
			
			/**
			 * 已留样学校数
			 */
			schDishRsSitStat.setRsSchNum(totalDistRsSchNum);
			
			/**
			 * 学校留样率
			 */
			schDishRsSitStat.setSchRsRate(schRsRate);
			
			//1 表示规范录入
			schDishRsSitStat.setSchStandardNum(standardNum);
			//2 表示补录
			schDishRsSitStat.setSchSupplementNum(supplementNum);
			//3 表示逾期补录
			schDishRsSitStat.setSchBeOverdueNum(beOverdueNum);
			//4 表示无数据
			schDishRsSitStat.setSchNoDataNum(noDataNum);
			
			//标准验收率
			schStandardRate = 0;
			if(shouldRsSchNum > 0) {
				schStandardRate = 100 * ((float) schDishRsSitStat.getSchStandardNum() / (float) shouldRsSchNum);
				BigDecimal bd = new BigDecimal(schStandardRate);
				schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				if (schStandardRate > 100)
					schStandardRate = 100;
			}
			schDishRsSitStat.setSchStandardRate(schStandardRate);
			
			schDishRsSitStatMap.put(departmentObj.getDepartmentName(), schDishRsSitStat);
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
			Map<String,SchDishRsSitStat> schDishRsSitStatMap,EduSchoolService eduSchoolService) {
		Map<String,Set<String>> noRsSchNumMap = new HashMap<String,Set<String>>();
		Map<String,Set<String>> rsSchNumMap = new HashMap<String,Set<String>>();
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
				
				//已留样数量
				if(totalDishNumMap.get(eduSchool.getSchoolNature())==null) {
					count = 1;
				}else {
					count = totalDishNumMap.get(eduSchool.getSchoolNature())+1;
				}
				
				totalDishNumMap.put(eduSchool.getSchoolNature(), count);
				
				
				if(keys[5] !=null && keys[5].equals("已留样")) {
					//已留样数量
					if(distRsDishNumMap.get(eduSchool.getSchoolNature())==null) {
						count = 1;
					}else {
						count = distRsDishNumMap.get(eduSchool.getSchoolNature())+1;
					}
					
					distRsDishNumMap.put(eduSchool.getSchoolNature(), count);
					
					//已留样学校数
					schoolSet=rsSchNumMap.get(eduSchool.getSchoolNature());
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					rsSchNumMap.put(eduSchool.getSchoolNature(), schoolSet);
				}else{
					//未留样数量
					if(distNoRsDishNumMap.get(eduSchool.getSchoolNature())==null) {
						count = 1;
					}else {
						count = distNoRsDishNumMap.get(eduSchool.getSchoolNature())+1;
					}
					
					distNoRsDishNumMap.put(eduSchool.getSchoolNature(), count);
					
					//未留样学校数
					schoolSet=noRsSchNumMap.get(eduSchool.getSchoolNature());
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					noRsSchNumMap.put(eduSchool.getSchoolNature(), schoolSet);
				}
				
			}
			
			
		}
		
		//计算留样率和数据封装
		float distRsRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			distRsRate = 0;
				SchDishRsSitStat schDishRsSitStat = schDishRsSitStatMap.get(entry.getValue());
				if(schDishRsSitStat==null) {
					schDishRsSitStat = new SchDishRsSitStat();
					//学校性质名称
					schDishRsSitStat.setStatPropName(entry.getValue());
				}
				/**
				 * 未留样学校数
				 */
				schDishRsSitStat.setNoRsSchNum(noRsSchNumMap.get(entry.getKey().toString())==null?0:noRsSchNumMap.get(entry.getKey().toString()).size());
				
				/**
				 * 已留样学校数
				 */
				schDishRsSitStat.setRsSchNum(rsSchNumMap.get(entry.getKey().toString())==null?0:rsSchNumMap.get(entry.getKey().toString()).size());
				
				
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
				schDishRsSitStat.setTotalDishNum(totalDishNum);
				/**
				 * 未留样菜品
				 */
				schDishRsSitStat.setNoRsDishNum(distNoRsDishNum);
				
				/**
				 * 已留样菜品
				 */
				schDishRsSitStat.setRsDishNum(distRsDishNum);
				
				/**
				 * 留样率
				 */
				schDishRsSitStat.setRsRate(distRsRate);
				schDishRsSitStatMap.put(entry.getValue(), schDishRsSitStat);
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
			Map<String,SchDishRsSitStat> schDishRsSitStatMap,EduSchoolService eduSchoolService) {
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

		Map<String,Integer> schStandardNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schSupplementNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schBeOverdueNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schNoDataNumMap = new HashMap<String,Integer>();
		
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
					if(curKey.indexOf("school-nat-area_")==0 && curKeys.length >= 7) {
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
					
					//操作状态对应的数量
					if (curKey.indexOf("re-school-nature_") == 0) {
						if(curKeys.length >= 4)
						{
							if(curKeys[5].equalsIgnoreCase("1")) {
								if(schStandardNumMap.get(curKeys[1]) == null) {
									schStandardNumMap.put(curKeys[1], valueCount);
								}else {
									schStandardNumMap.put(curKeys[1], schoolNum + valueCount);
								}
							}else if(curKeys[5].equalsIgnoreCase("2")) {
									if(schSupplementNumMap.get(curKeys[1]) == null) {
										schSupplementNumMap.put(curKeys[1], valueCount);
									}else {
										schSupplementNumMap.put(curKeys[1], schoolNum + valueCount);
									}
							}else if(curKeys[5].equalsIgnoreCase("3")) {
									if(schBeOverdueNumMap.get(curKeys[1]) == null) {
										schBeOverdueNumMap.put(curKeys[1], valueCount);
									}else {
										schBeOverdueNumMap.put(curKeys[1], schoolNum + valueCount);
									}
							}else if(curKeys[5].equalsIgnoreCase("4")) {
									if(schNoDataNumMap.get(curKeys[1]) == null) {
										schNoDataNumMap.put(curKeys[1], valueCount);
									}else {
										schNoDataNumMap.put(curKeys[1], schoolNum + valueCount);
									}
							}
						}
					}
				}
			}
		}
		
		
		//计算留样率和数据封装
		float distRsRate = 0;
		float schRsRate = 0;
		float schStandardRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			distRsRate = 0;
				SchDishRsSitStat schDishRsSitStat = schDishRsSitStatMap.get(entry.getValue());
				if(schDishRsSitStat==null) {
					schDishRsSitStat = new SchDishRsSitStat();
					//学校性质名称
					schDishRsSitStat.setStatPropName(entry.getValue());
				}
				
				
				/**
				 * 未留样学校数
				 */
				schDishRsSitStat.setNoRsSchNum(noRsSchNumMap.get(entry.getKey().toString())==null?0:noRsSchNumMap.get(entry.getKey().toString()));
				
				/**
				 * 已留样学校数
				 */
				schDishRsSitStat.setRsSchNum(rsSchNumMap.get(entry.getKey().toString())==null?0:rsSchNumMap.get(entry.getKey().toString()));
				
				schRsRate = 0;
				int shouldRsSchNum = schDishRsSitStat.getRsSchNum() + schDishRsSitStat.getNoRsSchNum();
				if(shouldRsSchNum > 0) {
					schRsRate = 100 * ((float) schDishRsSitStat.getRsSchNum() / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schRsRate);
					schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schRsRate > 100) {
						schRsRate = 100;
					}
				}
				
				
				/**
				 * 应留样学校数
				 */
				schDishRsSitStat.setShouldRsSchNum(shouldRsSchNum);
				/**
				 * 学校留样率
				 */
				schDishRsSitStat.setSchRsRate(schRsRate);
				
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
				schDishRsSitStat.setTotalDishNum(totalDishNum);
				/**
				 * 未留样菜品
				 */
				schDishRsSitStat.setNoRsDishNum(distNoRsDishNum);
				
				/**
				 * 已留样菜品
				 */
				schDishRsSitStat.setRsDishNum(distRsDishNum);
				
				/**
				 * 留样率
				 */
				schDishRsSitStat.setRsRate(distRsRate);
				
				//1 表示规范录入
				schDishRsSitStat.setSchStandardNum(schStandardNumMap.get(entry.getKey().toString())==null?0:schStandardNumMap.get(entry.getKey().toString()));
				//2 表示补录
				schDishRsSitStat.setSchSupplementNum( schSupplementNumMap.get(entry.getKey().toString())==null?0:schSupplementNumMap.get(entry.getKey().toString()));
				//3 表示逾期补录
				schDishRsSitStat.setSchBeOverdueNum(schBeOverdueNumMap.get(entry.getKey().toString())==null?0:schBeOverdueNumMap.get(entry.getKey().toString()));
				//4 表示无数据
				schDishRsSitStat.setSchNoDataNum(schNoDataNumMap.get(entry.getKey().toString())==null?0:schNoDataNumMap.get(entry.getKey().toString()));
				
				//标准验收率
				schStandardRate = 0;
				if(shouldRsSchNum > 0) {
					schStandardRate = 100 * ((float) schDishRsSitStat.getSchStandardNum() / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schStandardRate);
					schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schStandardRate > 100)
						schStandardRate = 100;
				}
				schDishRsSitStat.setSchStandardRate(schStandardRate);
				
				schDishRsSitStatMap.put(entry.getValue(), schDishRsSitStat);
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
			Map<String,SchDishRsSitStat> schDishRsSitStatMap,EduSchoolService eduSchoolService) {
		Map<String,Set<String>> noRsSchNumschoolMap = new HashMap<String,Set<String>>();
		Map<String,Set<String>> rsSchNumSchoolMap = new HashMap<String,Set<String>>();
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
				
				//已留样数量
				if(totalDishNumMap.get(schTypeId)==null) {
					count = 1;
				}else {
					count = totalDishNumMap.get(schTypeId)+1;
				}
				
				totalDishNumMap.put(schTypeId, count);
				
				
				if(keys[5] !=null && keys[5].equals("已留样")) {
					//已留样数量
					if(distRsDishNumMap.get(schTypeId)==null) {
						count = 1;
					}else {
						count = distRsDishNumMap.get(schTypeId)+1;
					}
					
					distRsDishNumMap.put(schTypeId, count);
					
					//已留样学校数
					schoolSet=rsSchNumSchoolMap.get(schTypeId);
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					rsSchNumSchoolMap.put(schTypeId, schoolSet);
					
				}else{
					//未留样数量
					if(distNoRsDishNumMap.get(schTypeId)==null) {
						count = 1;
					}else {
						count = distNoRsDishNumMap.get(schTypeId)+1;
					}
					
					distNoRsDishNumMap.put(schTypeId, count);
					
					//未留样学校数
					schoolSet=noRsSchNumschoolMap.get(schTypeId);
					if(schoolSet == null) {
						schoolSet = new HashSet<String>();
					}
					schoolSet.add(keys[0]);
					noRsSchNumschoolMap.put(schTypeId, schoolSet);
				}
				
			}
			
			
		}
		
		//计算留样率和数据封装
		float distRsRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			distRsRate = 0;
				SchDishRsSitStat schDishRsSitStat = schDishRsSitStatMap.get(entry.getValue());
				if(schDishRsSitStat==null) {
					schDishRsSitStat = new SchDishRsSitStat();
					//学校性质名称
					schDishRsSitStat.setStatPropName(entry.getValue());
					//
					schDishRsSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				/**
				 * 未留样学校数
				 */
				schDishRsSitStat.setNoRsSchNum(noRsSchNumschoolMap.get(entry.getKey().toString())==null?0:noRsSchNumschoolMap.get(entry.getKey().toString()).size());
				
				/**
				 * 已留样学校数
				 */
				schDishRsSitStat.setRsSchNum(rsSchNumSchoolMap.get(entry.getKey().toString())==null?0:rsSchNumSchoolMap.get(entry.getKey().toString()).size());
				
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
				schDishRsSitStat.setTotalDishNum(totalDishNum);
				/**
				 * 未留样菜品
				 */
				schDishRsSitStat.setNoRsDishNum(distNoRsDishNum);
				
				/**
				 * 已留样菜品
				 */
				schDishRsSitStat.setRsDishNum(distRsDishNum);
				
				/**
				 * 留样率
				 */
				schDishRsSitStat.setRsRate(distRsRate);
				schDishRsSitStatMap.put(entry.getValue(), schDishRsSitStat);
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
			Map<String,SchDishRsSitStat> schDishRsSitStatMap,EduSchoolService eduSchoolService) {
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
		
		Map<String,Integer> schStandardNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schSupplementNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schBeOverdueNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schNoDataNumMap = new HashMap<String,Integer>();
		
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
							count = 0;
							//未留样学校数
							schoolNum=noRsSchNumMap.get(curKeys[3]);
							if(schoolNum == null) {
								schoolNum = valueCount;
							}else {
								schoolNum = schoolNum + valueCount;
							}
							noRsSchNumMap.put(curKeys[3], schoolNum);
						}else if(curKeys[4].equalsIgnoreCase("已留样")) {
							count = 0;
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
					
					//操作状态对应的数量
					if (curKey.indexOf("re-school-level_") == 0) {
						if(curKeys.length >= 4)
						{
							if(curKeys[3].equalsIgnoreCase("1")) {
								if(schStandardNumMap.get(curKeys[1]) == null) {
									schStandardNumMap.put(curKeys[1], valueCount);
								}else {
									schStandardNumMap.put(curKeys[1], schoolNum + valueCount);
								}
							}else if(curKeys[3].equalsIgnoreCase("2")) {
									if(schSupplementNumMap.get(curKeys[1]) == null) {
										schSupplementNumMap.put(curKeys[1], valueCount);
									}else {
										schSupplementNumMap.put(curKeys[1], schoolNum + valueCount);
									}
							}else if(curKeys[3].equalsIgnoreCase("3")) {
									if(schBeOverdueNumMap.get(curKeys[1]) == null) {
										schBeOverdueNumMap.put(curKeys[1], valueCount);
									}else {
										schBeOverdueNumMap.put(curKeys[1], schoolNum + valueCount);
									}
							}else if(curKeys[3].equalsIgnoreCase("4")) {
									if(schNoDataNumMap.get(curKeys[1]) == null) {
										schNoDataNumMap.put(curKeys[1], valueCount);
									}else {
										schNoDataNumMap.put(curKeys[1], schoolNum + valueCount);
									}
							}
						}
					}
				}
			}
		}
		
		//计算留样率和数据封装
		float distRsRate = 0;
		float schRsRate = 0;
		float schStandardRate = 0;
		for(Map.Entry<Integer, String> entry : schoolPropertyMap.entrySet() ) {
			distRsRate = 0;
				SchDishRsSitStat schDishRsSitStat = schDishRsSitStatMap.get(entry.getValue());
				if(schDishRsSitStat==null) {
					schDishRsSitStat = new SchDishRsSitStat();
					//学校性质名称
					schDishRsSitStat.setStatPropName(entry.getValue());
					//
					schDishRsSitStat.setStatClassName(AppModConfig.schTypeNameToParentTypeNameMap.get(entry.getValue()));
				}
				/**
				 * 未留样学校数
				 */
				schDishRsSitStat.setNoRsSchNum(noRsSchNumMap.get(entry.getKey().toString())==null?0:noRsSchNumMap.get(entry.getKey().toString()));
				
				/**
				 * 已留样学校数
				 */
				schDishRsSitStat.setRsSchNum(rsSchNumMap.get(entry.getKey().toString())==null?0:rsSchNumMap.get(entry.getKey().toString()));
				
				schRsRate = 0;
				int shouldRsSchNum = schDishRsSitStat.getRsSchNum() + schDishRsSitStat.getNoRsSchNum();
				if(shouldRsSchNum > 0) {
					schRsRate = 100 * ((float) schDishRsSitStat.getRsSchNum() / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schRsRate);
					schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schRsRate > 100) {
						schRsRate = 100;
					}
				}
				
				
				/**
				 * 应留样学校数
				 */
				schDishRsSitStat.setShouldRsSchNum(shouldRsSchNum);
				/**
				 * 学校留样率
				 */
				schDishRsSitStat.setSchRsRate(schRsRate);
				
				
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
				schDishRsSitStat.setTotalDishNum(totalDishNum);
				/**
				 * 未留样菜品
				 */
				schDishRsSitStat.setNoRsDishNum(distNoRsDishNum);
				
				/**
				 * 已留样菜品
				 */
				schDishRsSitStat.setRsDishNum(distRsDishNum);
				
				/**
				 * 留样率
				 */
				schDishRsSitStat.setRsRate(distRsRate);
				
				//1 表示规范录入
				schDishRsSitStat.setSchStandardNum(schStandardNumMap.get(entry.getKey().toString())==null?0:schStandardNumMap.get(entry.getKey().toString()));
				//2 表示补录
				schDishRsSitStat.setSchSupplementNum( schSupplementNumMap.get(entry.getKey().toString())==null?0:schSupplementNumMap.get(entry.getKey().toString()));
				//3 表示逾期补录
				schDishRsSitStat.setSchBeOverdueNum(schBeOverdueNumMap.get(entry.getKey().toString())==null?0:schBeOverdueNumMap.get(entry.getKey().toString()));
				//4 表示无数据
				schDishRsSitStat.setSchNoDataNum(schNoDataNumMap.get(entry.getKey().toString())==null?0:schNoDataNumMap.get(entry.getKey().toString()));
				
				//标准验收率
				schStandardRate = 0;
				if(shouldRsSchNum > 0) {
					schStandardRate = 100 * ((float) schDishRsSitStat.getSchStandardNum() / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schStandardRate);
					schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schStandardRate > 100)
						schStandardRate = 100;
				}
				schDishRsSitStat.setSchStandardRate(schStandardRate);
				
				schDishRsSitStatMap.put(entry.getValue(), schDishRsSitStat);
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
			Map<String,SchDishRsSitStat> schDishRsSitStatMap,EduSchoolService eduSchoolService) {
		Map<String,Integer> noRsSchNumSchoolMap = new HashMap<String,Integer>();
		Map<String,Integer> rsSchNumSchoolMap = new HashMap<String,Integer>();
		Map<String, String> gcRetentiondishMap = new HashMap<String, String>();
		
		int dateCount = dates.length;
		String key = null;
		
		Map<String,Integer> totalDishNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distRsDishNumMap = new HashMap<String,Integer>();
		Map<String,Integer> distNoRsDishNumMap = new HashMap<String,Integer>();
		
		Map<String,Integer> schStandardNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schSupplementNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schBeOverdueNumMap = new HashMap<String,Integer>();
		Map<String,Integer> schNoDataNumMap = new HashMap<String,Integer>();
		
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
						//已留样数量
						if(totalDishNumMap.get(curKeys[1]+"_"+curKeys[3])==null) {
							count = valueCount;
						}else {
							count = totalDishNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
						}
						
						totalDishNumMap.put(curKeys[1]+"_"+curKeys[3], count);
						
						
						if(curKeys[4] !=null && curKeys[4].equals("已留样")) {
							//已留样数量
							if(distRsDishNumMap.get(curKeys[1]+"_"+curKeys[3])==null) {
								count = valueCount;
							}else {
								count = distRsDishNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
							}
							
							distRsDishNumMap.put(curKeys[1]+"_"+curKeys[3], count);
						}else{
							//未留样数量
							if(distNoRsDishNumMap.get(curKeys[1]+"_"+curKeys[3])==null) {
								count = valueCount;
							}else {
								count = distNoRsDishNumMap.get(curKeys[1]+"_"+curKeys[3])+valueCount;
							}
							
							distNoRsDishNumMap.put(curKeys[1]+"_"+curKeys[3], count);
							
							/*//未留样学校数
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
					
					//操作状态对应的数量
					if (curKey.indexOf("re-school-masterid_") == 0) {
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
		
		//计算留样率和数据封装
		float distRsRate = 0;
		//学校留样率
		float schRsRate = 0;
		float schStandardRate = 0;
		for(Map.Entry<String, String> entry : schoolPropertyMap.entrySet() ) {
			distRsRate = 0;
				SchDishRsSitStat schDishRsSitStat = schDishRsSitStatMap.get(entry.getKey());
				if(schDishRsSitStat==null) {
					String[] keys = entry.getKey().split("_");
					String masterid = keys[0];
					String slave = keys[1];
					schDishRsSitStat = new SchDishRsSitStat();
					schDishRsSitStat.setStatClassName(entry.getValue());
					//所属主管部门
					String slaveName=slave;
					
					if("0".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap0.get(slave);
					}else if ("1".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap1.get(slave);
					}else if ("2".equals(masterid)) {
						slaveName = AppModConfig.compDepIdToNameMap2.get(slave);
					}
					
					schDishRsSitStat.setStatPropName(slaveName);
				}
				/**
				 * 未留样学校数
				 */
				schDishRsSitStat.setNoRsSchNum(noRsSchNumSchoolMap.get(entry.getKey().toString())==null?0:noRsSchNumSchoolMap.get(entry.getKey().toString()));
				
				/**
				 * 已留样学校数
				 */
				schDishRsSitStat.setRsSchNum(rsSchNumSchoolMap.get(entry.getKey().toString())==null?0:rsSchNumSchoolMap.get(entry.getKey().toString()));
				
				schRsRate = 0;
				int shouldRsSchNum = schDishRsSitStat.getRsSchNum() + schDishRsSitStat.getNoRsSchNum();
				if(shouldRsSchNum > 0) {
					schRsRate = 100 * ((float) schDishRsSitStat.getRsSchNum() / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schRsRate);
					schRsRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schRsRate > 100) {
						schRsRate = 100;
					}
				}
				
				
				/**
				 * 应留样学校数
				 */
				schDishRsSitStat.setShouldRsSchNum(shouldRsSchNum);
				/**
				 * 学校留样率
				 */
				schDishRsSitStat.setSchRsRate(schRsRate);
				
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
				schDishRsSitStat.setTotalDishNum(totalDishNum);
				/**
				 * 未留样菜品
				 */
				schDishRsSitStat.setNoRsDishNum(distNoRsDishNum);
				
				/**
				 * 已留样菜品
				 */
				schDishRsSitStat.setRsDishNum(distRsDishNum);
				
				/**
				 * 留样率
				 */
				schDishRsSitStat.setRsRate(distRsRate);
				
				//1 表示规范录入
				schDishRsSitStat.setSchStandardNum(schStandardNumMap.get(entry.getKey().toString())==null?0:schStandardNumMap.get(entry.getKey().toString()));
				//2 表示补录
				schDishRsSitStat.setSchSupplementNum( schSupplementNumMap.get(entry.getKey().toString())==null?0:schSupplementNumMap.get(entry.getKey().toString()));
				//3 表示逾期补录
				schDishRsSitStat.setSchBeOverdueNum(schBeOverdueNumMap.get(entry.getKey().toString())==null?0:schBeOverdueNumMap.get(entry.getKey().toString()));
				//4 表示无数据
				schDishRsSitStat.setSchNoDataNum(schNoDataNumMap.get(entry.getKey().toString())==null?0:schNoDataNumMap.get(entry.getKey().toString()));
				
				//标准验收率
				schStandardRate = 0;
				if(shouldRsSchNum > 0) {
					schStandardRate = 100 * ((float) schDishRsSitStat.getSchStandardNum() / (float) shouldRsSchNum);
					BigDecimal bd = new BigDecimal(schStandardRate);
					schStandardRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					if (schStandardRate > 100)
						schStandardRate = 100;
				}
				schDishRsSitStat.setSchStandardRate(schStandardRate);
				
				schDishRsSitStatMap.put(entry.getKey(), schDishRsSitStat);
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
	public SchDishRsSitStatDTO appModFunc(String token, String distName, String prefCity, String province,String startDate, String endDate, Integer statMode,
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,EduSchoolService eduSchoolService ) {
		
		SchDishRsSitStatDTO schDishRsSitStatDTO = null;
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
						schDishRsSitStatDTO = schDishRsSitStatFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						schDishRsSitStatDTO = schDishRsSitStatFuncOne(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates, tedList, db1Service, saasService,eduSchoolService);
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
						schDishRsSitStatDTO = schDishRsSitStatFunc(distId, dates, tedList, db1Service, saasService);
					}else if (methodIndex==1) {
						schDishRsSitStatDTO = schDishRsSitStatFuncOne(departmentId,distId,currDistName,statMode,curSubLevel,curCompDep,dates, tedList, db1Service, saasService,eduSchoolService);
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
			schDishRsSitStatDTO = new SchDishRsSitStatDTO();
		}		

		
		return schDishRsSitStatDTO;
	}
}
