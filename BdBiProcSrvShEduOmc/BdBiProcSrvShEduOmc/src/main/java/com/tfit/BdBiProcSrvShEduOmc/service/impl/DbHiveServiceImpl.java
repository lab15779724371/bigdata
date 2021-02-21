package com.tfit.BdBiProcSrvShEduOmc.service.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataSourceConn;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaDishSupStats;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.ga.CaMatSupStats;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.EduPackage;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchLicense;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSch;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchSupplier;
import com.tfit.BdBiProcSrvShEduOmc.obj.search.AppTEduMaterialDishD;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 菜品、原料以及基础信息先关hive库的查询
 * @author Administrator
 *
 */
@Service
public class DbHiveServiceImpl implements DbHiveService {
	private static final Logger logger = LogManager.getLogger(DbHiveServiceImpl.class.getName());
	
	//额外数据源Hive
	@Autowired
	@Qualifier("dsHive")
	DataSource dataSourceHive;
	
	@Autowired
	@Qualifier("dsHive2")
	DataSource dataSourceHive2;
	
	//额外数据源Hive连接模板
	JdbcTemplate jdbcTemplateHive = null;
	
	//额外数据源Hive连接模板
	JdbcTemplate jdbcTemplateHive2 = null;
	
	//初始化处理标识，true表示已处理，false表示未处理
    boolean initProcFlag = false;
    
    //是否使用mybatis中间件
    boolean mybatisUseFlag = true;
    
    //初始化处理
  	@Scheduled(fixedRate = 60*60*1000)
  	public void initProc() {
  		if(initProcFlag)
  			return ;
  		initProcFlag = true;
  		logger.info("定时建立与 DataSource数据源dsHive对象表示的数据源的连接，时间：" + BCDTimeUtil.convertNormalFrom(null));
  		jdbcTemplateHive = new JdbcTemplate(dataSourceHive);
  		
  		jdbcTemplateHive2 = new JdbcTemplate(dataSourceHive2);
  	}
  	
  	//--------------------综合分析-菜品----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询数据列表
  	 */
    public List<CaDishSupDets> getCaDishSupDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schId, List<String> shIdList,String dishName, String rmcName,String distName,
    		String caterType, int schType, int schProp, int optMode, String menuName,
    		Integer startNum,Integer endNum,Map<Integer, String> schoolPropertyMap) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append("select from_unixtime(unix_timestamp(supply_date,'yyyyMMddHH'),'yyyy/MM/dd') repastDate,school_name schName,area distName,address detailAddr,");
        sb.append("level_name schType,school_nature_name schProp,license_main_type licenseMainType,license_main_child licenseMainChild,");
        sb.append("ledger_type dispType, cater_type_name caterType,");
        sb.append(" dishes_name dishName,category dishType,dishes_number supNum,menu_group_name menuName,supplier_name rmcName,supplier_id supplierId, ");
        sb.append(" school_id schoolId,school_supplier_id schoolSupplierId ");
        sb.append(" from app_t_edu_dish_menu" );
        sb.append(" where  1=1 ");
        getCaDishSupDetsListCondition(listYearMonth, startDate, endDateAddOne, schId,shIdList, dishName, rmcName, distName,
				caterType, schType, schProp, optMode, menuName, sb);
        
        //sb.append(" order by distName asc,schType asc,menuName asc ");
        //sb.append(" order by distName asc ");
        if(startNum!=null) {
        	sb.append(" limit "+endNum);
        }
        logger.info("执行sql:"+sb.toString());
        String[] optModeNames= {""};
		return (List<CaDishSupDets>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<CaDishSupDets>() {
			@Override
			public CaDishSupDets mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
				
				CaDishSupDets cdsd = new CaDishSupDets();
	            cdsd.setRepastDate(rs.getString("repastDate"));
	            if(rs.getString("schName")!=null){
	            	cdsd.setSchName(rs.getString("schName"));
	            }
	            
	            cdsd.setDistName(rs.getString("distName"));
	            cdsd.setDetailAddr(rs.getString("detailAddr"));
	            
	            if(rs.getString("schType")!=null && !"".equals(rs.getString("schType"))){
	            	cdsd.setSchType(AppModConfig.schTypeIdToNameMap.get(Integer.parseInt(rs.getString("schType"))));
	            }
	            
	            if(rs.getString("schProp")!=null && !"".equals(rs.getString("schProp"))){
	            	if(schoolPropertyMap!=null) {
	            		cdsd.setSchProp(schoolPropertyMap.get(Integer.parseInt(rs.getString("schProp"))));
	            	}
	            }
	            
	            if(rs.getString("licenseMainType")!=null && rs.getString("licenseMainChild")!=null){
	            	optModeNames[0]="";
	            	optModeNames[0] = AppModConfig.getOptModeName(Short.parseShort("-1"), rs.getString("dispType"), rs.getString("licenseMainType"), rs.getShort("licenseMainChild"));
	            	cdsd.setOptMode(optModeNames[0]);
	            }
	            
	            if(rs.getString("dispType")!=null){
	            	cdsd.setDispType(AppModConfig.dispTypeIdToNameForDsihMap.get((rs.getString("dispType")==null || 
	            			"".equals(rs.getString("dispType")))?-1:Integer.parseInt(rs.getString("dispType"))));
	            }
	            cdsd.setCaterType(rs.getString("caterType"));
	            cdsd.setDishName(rs.getString("dishName"));
	            cdsd.setDishType(rs.getString("dishType"));
	            cdsd.setSupNum(rs.getInt("supNum"));
	            cdsd.setMenuName(rs.getString("menuName"));
	            cdsd.setRmcName(rs.getString("rmcName"));
	            cdsd.setSupplierId(rs.getString("supplierId"));
	            cdsd.setSchoolId(rs.getString("schoolId"));
	            cdsd.setSchoolSupplierId(rs.getString("schoolSupplierId"));
				return cdsd;
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询数据条数
     */
    public Integer getCaDishSupDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schId,List<String> shIdList, String dishName, String rmcName,String distName,
    		String caterType, int schType, int schProp, int optMode, String menuName) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        //sb.append("select count(*) dataCount from ( ");
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_dish_menu" );
        sb.append(" where 1=1  ");
        
        getCaDishSupDetsListCondition(listYearMonth, startDate, endDateAddOne, schId,shIdList, dishName, rmcName, distName,
				caterType, schType, schProp, optMode, menuName, sb);
        //sb.append(" ) totalTable ");
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		dataCounts[0] = rs.getInt("dataCount");
        	}   
        });
        return dataCounts[0];
    }
   
    /**
     * 菜品供应明细查询条件（查询列表和查询总数共用）
     * @param startYear
     * @param startMonth
     * @param startDate
     * @param endDateAddOne
     * @param schName
     * @param dishName
     * @param rmcName
     * @param distName
     * @param caterType
     * @param schType
     * @param schProp
     * @param optMode
     * @param menuName
     * @param sb
     */
	private void getCaDishSupDetsListCondition(List<String> listYearMonth, String startDate, String endDateAddOne,
			String schId,List<String> shIdList, String dishName, String rmcName, String distName, String caterType, int schType,
			int schProp, int optMode, String menuName, StringBuffer sb) {
		String [] arrYearMonth ;
		
		if(listYearMonth.size() > 0) {
			sb.append("AND (");
			for(String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb.append(" (year =  "+arrYearMonth[0]);
		        sb.append(" and month >= " +arrYearMonth[1]);
		        sb.append(" and month <= " +arrYearMonth[2]+") ");
		        
		        if(!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
        
		if(startDate !=null && !"".equals(startDate)) {
			sb.append(" and DATE_FORMAT(supply_date,'yyyy-MM-dd HH:mm:ss') >= \""+startDate+" 00:00:00\"");
		}
		if(endDateAddOne!=null && !"".equals(endDateAddOne)) {
			sb.append(" and DATE_FORMAT(supply_date,'yyyy-MM-dd HH:mm:ss') < \""+endDateAddOne +" 00:00:00\"");
		}
        sb.append(" and  school_name is not null and school_name !=\"\" and school_name !=\"null\" ");
        //由于阳光午餐删子表，不删主表加上后台数据特殊处理，会导致菜品为空的数据存在，故做
        sb.append(" and  dishes_name is not null and dishes_name !=\"\" and dishes_name !=\"NULL\" ");
      		
        //学校
        if(CommonUtil.isNotEmpty(schId)) {
        	sb.append(" and school_id = \"" + schId+"\"");
        }
       //学校编号集合
		if(shIdList!=null && shIdList.size() >0) {
			 sb.append(" and ( ");
	        for (int i = 0; i < (shIdList.size() / 800 + ((shIdList.size() % 800) > 0 ? 1 : 0)); i++) {
	            int startIndex = i * 800;
	            if (startIndex >= shIdList.size()) {
	                startIndex = shIdList.size() - 1;
	            }
	            int ednIndex = (i + 1) * 800;
	            if (ednIndex >= shIdList.size()) {
	                ednIndex = shIdList.size();
	            }
	            String relationIds = StringUtils.join(shIdList.subList(startIndex, ednIndex).toArray(), ",");
	            //加引号
	            relationIds="\""+relationIds.replaceAll(",", "\",\"")+"\"";
	            sb.append("  school_id in (" + relationIds + ")");
	            
	            if(ednIndex <shIdList.size()-1) {
	            	sb.append(" or ");
	            }
	            
	        }
	        sb.append(" ) ");
		}
        //团餐公司
        if(CommonUtil.isNotEmpty(rmcName)) {
        	sb.append(" and supplier_id = \"" + rmcName+"\"");
        }
        //区
        if(CommonUtil.isNotEmpty(distName)) {
        	sb.append(" and area = \"" + distName+"\"");
        }
        //菜品名称
        if(CommonUtil.isNotEmpty(dishName)) {
        	sb.append(" and dishes_name like  \"" +"%"+dishName+"%"+"\"");
        }
        
        //餐别
        if(CommonUtil.isNotEmpty(caterType)) {
        	sb.append(" and cater_type_name = \"" + caterType+"\"");
        }
        
        //菜单名称
        if(CommonUtil.isNotEmpty(menuName)) {
        	sb.append(" and menu_group_name = \"" + menuName+"\"");
        }
        
        //学校学段
        if(schType != -1) {
        	sb.append(" and level_name = " + schType);
        }
        
        //学校学制
        if(schProp != -1) {
        	sb.append(" and school_nature_name = \"" + schProp+"\"");
        }
        
        //经营模式：经营模式（供餐模式，0：学校-自行加工  1：学校-食品加工商 2：外包-现场加工 3：外包-快餐配送 
        if(optMode != -1) {
        	if(optMode==0 || optMode == 1) {
        		sb.append(" and license_main_type = \"0\" ");
        		/*if(optMode ==0) {
        			sb.append(" and license_main_child = 0 ");
        		}else if (optMode ==1) {
        			sb.append(" and license_main_child = 1 ");
        		}*/
        	}else if (optMode==2 || optMode == 3) {
        		sb.append(" and license_main_type = \"1\" ");
        		if(optMode ==2) {
        			sb.append(" and license_main_child = 0 ");
        		}else if (optMode ==3) {
        			sb.append(" and license_main_child = 1 ");
        		}
        	}
        	
        }
	}
	
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询菜品汇总数据列表
  	 */
    public List<CaDishSupStats> getCaDishSupStatsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schName, String dishName,String distName,String dishType,String caterType,
    		Integer startNum,Integer endNum) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
        sb.append("select dishes_name dishName,category dishType ,sum( dishes_number ) supNum ");
        sb.append(" from app_t_edu_dish_menu" );
        sb.append(" where  1=1 ");
        
        //拼接查询条件
        getCaDishSupStatsListCondition(listYearMonth, startDate, endDateAddOne, schName, dishName, distName, dishType, caterType, sb);
        
        sb.append(" group by dishes_name,category ");
        sb.append(" order by supNum desc ");
        sb.append(" limit "+endNum);
        logger.info("执行sql:"+sb.toString());
        
		return (List<CaDishSupStats>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<CaDishSupStats>(){
			@Override
			public CaDishSupStats mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}else {
					CaDishSupStats cdss = new CaDishSupStats();
		            cdss.setDishName(rs.getString("dishName"));
		            cdss.setDishType(rs.getString("dishType"));
		            cdss.setSupNum(rs.getInt("supNum"));
					return cdss;
				}
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询菜品汇总数据条数
     */
    public Integer getCaDishSupStatsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schName, String dishName,String distName,String dishType,String caterType) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(DISTINCT(dishes_name,category)) dataCount ");
        sb.append(" from app_t_edu_dish_menu" );
        sb.append(" where 1=1  ");
        getCaDishSupStatsListCondition(listYearMonth, startDate, endDateAddOne, schName, dishName, distName, dishType, 
        		caterType, sb);
        
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		dataCounts[0] = rs.getInt("dataCount");
        	}   
        });
        return dataCounts[0];
    }
   
    /**
     * 菜品供应明细查询条件（查询列表和查询总数共用）
     * @param startYear
     * @param startMonth
     * @param startDate
     * @param endDateAddOne
     * @param schName
     * @param dishName
     * @param rmcName
     * @param distName
     * @param caterType
     * @param schType
     * @param schProp
     * @param optMode
     * @param menuName
     * @param sb
     */
	private void getCaDishSupStatsListCondition(List<String> listYearMonth, String startDate, String endDateAddOne,
			String schName, String dishName,String distName,String dishType,String caterType,StringBuffer sb) {
		String [] arrYearMonth ;
		
		if(listYearMonth.size() > 0) {
			sb.append("AND (");
			for(String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb.append(" (year =  "+arrYearMonth[0]);
		        sb.append(" and month >= " +arrYearMonth[1]);
		        sb.append(" and month <= " +arrYearMonth[2]+") ");
		        
		        if(!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
        
        sb.append(" and DATE_FORMAT(supply_date,'yyyy-MM-dd HH:mm:ss') >= \""+startDate+" 00:00:00\"");
        sb.append(" and DATE_FORMAT(supply_date,'yyyy-MM-dd HH:mm:ss') < \""+endDateAddOne +" 00:00:00\"");
        sb.append(" and  school_name is not null and school_name !=\"\" and school_name !=\"null\" ");
        
        //学校
        if(CommonUtil.isNotEmpty(schName)) {
        	sb.append(" and school_id = \"" + schName+"\"");
        }
        //区
        if(CommonUtil.isNotEmpty(distName)) {
        	sb.append(" and area = \"" + distName+"\"");
        }
        //菜品名称
        if(CommonUtil.isNotEmpty(dishName)) {
        	sb.append(" and dishes_name like  \"" +"%"+dishName+"%"+"\"");
        }
        
        //餐别
        if(CommonUtil.isNotEmpty(caterType)) {
        	sb.append(" and cater_type_name = \"" + caterType+"\"");
        }
        
        //类别
        if(CommonUtil.isNotEmpty(dishType)) {
        	sb.append(" and category = \"" + dishType+"\"");
        }
	}
	
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_dish_total中根据条件查询菜品汇总数据列表
  	 */
    public List<CaDishSupStats> getCaDishSupStatsListFromTotal(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String dishName,String distName,String dishType,String caterType,
    		Integer startNum,Integer endNum) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select dishes_name dishName,category dishType ,sum( dishes_number ) supNum ");
        sb.append(" from app_t_edu_dish_total" );
        sb.append(" where  1=1 ");
        
        //拼接查询条件
        getCaDishSupStatsListConditionFromTotal(listYearMonth, startDate, endDateAddOne, dishName, distName, dishType, caterType, sb);
        sb.append(" group by dishes_name,category ");
        sb.append(" order by supNum desc ");
        sb.append(" limit "+endNum);
        logger.info("执行sql:"+sb.toString());
        
		return (List<CaDishSupStats>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<CaDishSupStats>(){
			@Override
			public CaDishSupStats mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}else {
					CaDishSupStats cdss = new CaDishSupStats();
		            cdss.setDishName(rs.getString("dishName"));
		            cdss.setDishType(rs.getString("dishType"));
		            cdss.setSupNum(rs.getInt("supNum"));
					return cdss;
				}
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询菜品汇总数据条数
     */
    public Integer getCaDishSupStatsCountFromTotal(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String dishName,String distName,String dishType,String caterType) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
    	
    	//注释原因：DISTINCT多个字段速度太慢（达到60s，原料是ok的，菜品存在此问题）
       /* sb.append("select count(DISTINCT(dishes_name,category)) dataCount ");
        sb.append(" from app_t_edu_dish_total" );
        sb.append(" where 1=1  ");*/
        
        sb.append(" select count(1) dataCount from (select count(1)  from app_t_edu_dish_total ");
        sb.append(" where 1=1  ");
        getCaDishSupStatsListConditionFromTotal(listYearMonth, startDate, endDateAddOne, dishName, distName, dishType, 
        		caterType, sb);
        		
        sb.append(" group by dishes_name,category) tempTable ");
        
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		dataCounts[0] = rs.getInt("dataCount");
        	}   
        });
        return dataCounts[0];
    }
   
    /**
     * 菜品供应明细查询条件（查询列表和查询总数共用）
     * @param startYear
     * @param startMonth
     * @param startDate
     * @param endDateAddOne
     * @param schName
     * @param dishName
     * @param rmcName
     * @param distName
     * @param caterType
     * @param schType
     * @param schProp
     * @param optMode
     * @param menuName
     * @param sb
     */
	private void getCaDishSupStatsListConditionFromTotal(List<String> listYearMonth, String startDate, String endDateAddOne,
			String dishName,String distName,String dishType,String caterType,StringBuffer sb) {
		String [] arrYearMonth ;
		
		if(listYearMonth.size() > 0) {
			sb.append("AND (");
			for(String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb.append(" (year =  "+arrYearMonth[0]);
		        sb.append(" and month >= " +arrYearMonth[1]);
		        sb.append(" and month <= " +arrYearMonth[2]+") ");
		        
		        if(!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
        
        sb.append(" and DATE_FORMAT(supply_date,'yyyy-MM-dd HH:mm:ss') >= \""+startDate+" 00:00:00\"");
        sb.append(" and DATE_FORMAT(supply_date,'yyyy-MM-dd HH:mm:ss') < \""+endDateAddOne +" 00:00:00\"");
        //区
        if(CommonUtil.isNotEmpty(distName)) {
        	sb.append(" and area = \"" + distName+"\"");
        }
        //菜品名称
        if(CommonUtil.isNotEmpty(dishName)) {
        	sb.append(" and dishes_name like  \"" +"%"+dishName+"%"+"\"");
        }
        
        //餐别
        if(CommonUtil.isNotEmpty(caterType)) {
        	sb.append(" and cater_type_name = \"" + caterType+"\"");
        }
        
        //类别
        if(CommonUtil.isNotEmpty(dishType)) {
        	sb.append(" and category = \"" + dishType+"\"");
        }
	}
	
	//-----------------综合分析-原料---------------------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_ledege_detail中根据条件查询数据列表
  	 */
    public List<CaMatSupDets> getCaMatSupDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schName,String distName,List<String> schoolIds, String matName,String stdMatName, String rmcName, String supplierName, 
			String distrBatNumber, int schType, int acceptStatus, int optMode,
    		Integer startNum,Integer endNum,Map<Integer, String> schoolPropertyMap,String supplierNameLike,String supplierNameAll) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     ledger_master_id ledgerMasterId,from_unixtime(unix_timestamp(use_date,'yyyyMMddHH'),'yyyy/MM/dd') matUseDate, ");
        sb.append("     ware_batch_no distrBatNumber,school_name schName, ");
        sb.append("     area distName,address detailAddr, ");
        sb.append("     level_name schType,school_nature_name schProp,school_nature_sub_name, ");
        sb.append("     license_main_type licenseMainType,license_main_child licenseMainChild, ");
        sb.append("     ledger_type dispType,supplier_name rmcName,supplier_material_name matName, ");
        sb.append("     NAME standardName,wares_type_name matClassify,actual_quantity actualQuantity,quantity quantity,amount_unit amountUnit, ");
        sb.append("     first_num firstNum,supplier_material_units supplierMaterialUnits, ");
        sb.append("     second_num secondNum,other_quantity otherQuantity,batch_no batNumber, ");
        sb.append("     production_date prodDate,shelf_life qaGuaPeriod,supply_name supplierName,haul_status acceptStatus, ");
        sb.append("     delivery_number acceptNum, ");
        sb.append("     images, ");
        sb.append("     from_unixtime(unix_timestamp(delivery_date,'yyyyMMddHH'),'yyyy/MM/dd') acceptDate,school_id schoolId,supplier_id supplierId,supply_id supplyId ");
        sb.append(" FROM ");
        sb.append("     app_t_edu_ledege_detail ");
        sb.append(" where  1=1 ");
        getCaMatSupDetsListCondition(listYearMonth, startDate, endDateAddOne,schName,distName,schoolIds,matName,stdMatName,rmcName,
        		supplierName,distrBatNumber,schType,acceptStatus,optMode,supplierNameLike,supplierNameAll,sb);
        
        //sb.append(" order by distName asc,schType asc,menuName asc "); ---由于数据量大，排序执行速度相当慢，暂时不做排序
        //sb.append(" order by distName asc ");
        if(startNum!=null) {
        	sb.append(" limit "+endNum);
        }
        logger.info("执行sql:"+sb.toString());
        String[] optModeNames= {""};
		return (List<CaMatSupDets>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<CaMatSupDets>() {

			@Override
			public CaMatSupDets mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
				
				CaMatSupDets cdsd = new CaMatSupDets();
				cdsd.setLedgerMasterId(rs.getString("ledgerMasterId"));
				cdsd.setGsBillPicUrl("-");
				cdsd.setQaCertPicUrl("-");
				cdsd.setMatUseDate(rs.getString("matUseDate"));
				cdsd.setDistrBatNumber(rs.getString("distrBatNumber"));
				cdsd.setSchName(rs.getString("schName"));
				cdsd.setDistName("-");
				if(CommonUtil.isNotEmpty(rs.getString("distName"))) {
					if(!rs.getString("distName").equalsIgnoreCase("null"))
						cdsd.setDistName(rs.getString("distName"));
				}
				cdsd.setDetailAddr(rs.getString("detailAddr"));
				
				if(rs.getString("schType")!=null && !"".equals(rs.getString("schType"))){
	            	cdsd.setSchType(AppModConfig.schTypeIdToNameMap.get(Integer.parseInt(rs.getString("schType"))));
	            }
				if(rs.getString("schProp")!=null && !"".equals(rs.getString("schProp")) && schoolPropertyMap!=null){
	            	cdsd.setSchProp(schoolPropertyMap.get(Integer.parseInt(rs.getString("schProp"))));
	            }
	            
	            if(rs.getString("licenseMainType")!=null && rs.getString("licenseMainChild")!=null){
	            	optModeNames[0]="";
	            	optModeNames[0] = AppModConfig.getOptModeName(Short.parseShort("-1"), rs.getString("dispType"), 
	            			rs.getString("licenseMainType"), rs.getShort("licenseMainChild"));
	            	cdsd.setOptMode(optModeNames[0]);
	            }
	            
	            //配送类型
	            if(rs.getString("dispType")!=null){
	            	if(rs.getString("dispType").equalsIgnoreCase("1"))
	            		cdsd.setDispType("原料");
					else if(rs.getString("dispType").equalsIgnoreCase("2"))
						cdsd.setDispType("成品菜");
					else
						cdsd.setDispType("-");
	            }
				//团餐公司名称
				cdsd.setRmcName(CommonUtil.isEmpty(rs.getString("rmcName"))?"-":rs.getString("rmcName"));
				//物料名称
				cdsd.setMatName(CommonUtil.isEmpty(rs.getString("matName"))?"-":rs.getString("matName"));
				cdsd.setStandardName(rs.getString("standardName"));
				cdsd.setMatClassify(rs.getString("matClassify"));
				
				String amountUnit = "";
				if(CommonUtil.isNotEmpty(rs.getString("amountUnit")) && !"null".equalsIgnoreCase(rs.getString("amountUnit"))) {
					amountUnit = rs.getString("amountUnit");
				}
				
				//数量=actual_quantity，单位是amount_unit
				cdsd.setQuantity("-");
				if(CommonUtil.isNotEmpty(rs.getString("quantity"))) {
					if(!rs.getString("quantity").equalsIgnoreCase("null")) {
						float quantity = rs.getFloat("quantity");
						BigDecimal bd = new BigDecimal(quantity);
						quantity = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						cdsd.setQuantity(quantity + " " + amountUnit);
					}
				}
				
				//换算关系： first_num + supplier_material_units = second_num + amount_unit
			    String firstNum = "0.00";
				if(CommonUtil.isNotEmpty(rs.getString("firstNum")) && !"null".equalsIgnoreCase(rs.getString("firstNum"))) {
					firstNum = rs.getString("firstNum");
				}
				
				String secondNum = "0.00";
				if(CommonUtil.isNotEmpty(rs.getString("secondNum")) && !"null".equalsIgnoreCase(rs.getString("secondNum"))) {
					secondNum = rs.getString("secondNum");
				}
				
				String supplierMaterialUnits = "";
				if(CommonUtil.isNotEmpty(rs.getString("supplierMaterialUnits")) && !"null".equalsIgnoreCase(rs.getString("supplierMaterialUnits"))) {
					supplierMaterialUnits = rs.getString("supplierMaterialUnits");
				}
				
				
				cdsd.setCvtRel(firstNum+supplierMaterialUnits+ "=" +
						secondNum+amountUnit);
				
				//换算数量 ： other_quantity 单位是： supplier_material_units
				float cvtQuantity = 0; 
				logger.info("********rs.getString(\"dispType\")："+rs.getString("dispType")+"******rs.getString(\"quantity\")："+rs.getString("quantity")+"rs.getString(\"otherQuantity\")"+rs.getString("otherQuantity"));
				cdsd.setCvtQuantity(String.valueOf(cvtQuantity));
				if("2".equals(rs.getString("dispType"))) {
					//成品菜
					supplierMaterialUnits = "";
					if(CommonUtil.isNotEmpty(rs.getString("amountUnit")) && !"null".equalsIgnoreCase(rs.getString("amountUnit"))) {
						supplierMaterialUnits = rs.getString("amountUnit");
					}
					
					cdsd.setCvtQuantity("0" + " " +supplierMaterialUnits);
					if(rs.getString("quantity") !=null) {
						if(!rs.getString("quantity").equalsIgnoreCase("null")) {
							cvtQuantity = rs.getFloat("quantity");
							BigDecimal bd = new BigDecimal(cvtQuantity);
							cvtQuantity = bd.intValue();
							//配送数量，单位：公斤
							cdsd.setCvtQuantity(String.valueOf(cvtQuantity) + " " + supplierMaterialUnits);
	
						}
					}
				}else {
					cdsd.setCvtQuantity(String.valueOf(cvtQuantity)+ " " + supplierMaterialUnits);
					//原料
					if(rs.getString("otherQuantity") !=null) {
						if(!rs.getString("otherQuantity").equalsIgnoreCase("null")) {
							cvtQuantity = rs.getFloat("otherQuantity");
							BigDecimal bd = new BigDecimal(cvtQuantity);
							cvtQuantity = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
							if(rs.getString("otherQuantity") !=null)
								cdsd.setCvtQuantity(String.valueOf(cvtQuantity) + " " + supplierMaterialUnits);
						}
					}
				}
				
				////批号
				cdsd.setBatNumber("-");
				if(CommonUtil.isNotEmpty(rs.getString("batNumber"))) {
					if(!rs.getString("batNumber").equalsIgnoreCase("null"))
						cdsd.setBatNumber(rs.getString("batNumber"));
				}
				
				//生产日期
				cdsd.setProdDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("prodDate"))) {
					if(!rs.getString("prodDate").equalsIgnoreCase("null"))
						cdsd.setProdDate(rs.getString("prodDate"));
				}
				//保质期
				cdsd.setQaGuaPeriod("-");
				if(CommonUtil.isNotEmpty(rs.getString("qaGuaPeriod"))) {
					if(!rs.getString("qaGuaPeriod").equalsIgnoreCase("null"))
						cdsd.setQaGuaPeriod(rs.getString("qaGuaPeriod"));
				}
				//供应商
				cdsd.setSupplierName("-");
				if(CommonUtil.isNotEmpty(rs.getString("supplierName"))) {
					if(!rs.getString("supplierName").equalsIgnoreCase("null"))
						cdsd.setSupplierName(rs.getString("supplierName"));
				}
				
				//是否验收0：未验收、1已验收
				cdsd.setAcceptStatus(rs.getInt("acceptStatus"));
				int curAcceptStatus = 0;
				cdsd.setAcceptStatus(0);
				if(CommonUtil.isNotEmpty(rs.getString("acceptStatus"))) {
					curAcceptStatus = rs.getInt("acceptStatus");
					if(curAcceptStatus == 3)
						curAcceptStatus = 1;
					else
						curAcceptStatus = 0;
					cdsd.setAcceptStatus(curAcceptStatus);
				}
				float acceptNum = 0;
				cdsd.setAcceptNum(String.valueOf(acceptNum));
				cdsd.setSupplyId(rs.getString("supplyId"));
				cdsd.setSchoolId(rs.getString("schoolId"));
				
				if("2".equals(rs.getString("dispType"))) {
					acceptNum = cvtQuantity;
					cdsd.setAcceptNum(cdsd.getCvtQuantity());
				}else {
					cdsd.setAcceptNum(String.valueOf(acceptNum) + " " + supplierMaterialUnits);
					if(rs.getString("acceptNum") !=null ) {
						if(!rs.getString("acceptNum").equalsIgnoreCase("null")) {
							acceptNum = rs.getFloat("acceptNum");
							BigDecimal bd = new BigDecimal(acceptNum);
							acceptNum = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
							if(rs.getString("otherQuantity") !=null)
								cdsd.setAcceptNum(String.valueOf(acceptNum) + " " + supplierMaterialUnits);
						}
					}
				}
				
				//验收比例
				float acceptRate = 0;
				logger.info("********acceptNum："+acceptNum+"******："+cvtQuantity);
				if(cvtQuantity > 0) {
					acceptRate = 100*acceptNum/cvtQuantity;
					BigDecimal bd = new BigDecimal(acceptRate);
					acceptRate = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					/*if(acceptRate > 100)
						acceptRate = 100;*/
				}
				cdsd.setAcceptRate(acceptRate);
				
				if(CommonUtil.isNotEmpty(rs.getString("images") )) {
					//验收图片集合
					List<String> gsBillPicUrlList = new ArrayList<String>();
					//检疫图片集合
					List<String> qaCertPicUrlList = new ArrayList<String>();
					List<Object> imageList=CommonUtil.changeStringToList(rs.getString("images"));
					
					for(Object image : imageList) {
						if(image == null) {
							continue;
						}
						String[] imageSon = image.toString().split(":");
						
						logger.info("********"+imageSon[0]+"******"+imageSon[1]);
						if(imageSon.length>=2) {
							if(imageSon[0]!=null && "1".equals(imageSon[0])) {
								//验收图片
								if(imageSon[1].indexOf("http") >=0) {
									gsBillPicUrlList.add(imageSon[1]+":"+imageSon[2]);
								}else {
									gsBillPicUrlList.add(SpringConfig.ss_picfile_srvdn + "/"+imageSon[1]);
								}
								
							}else if (imageSon[0]!=null && "2".equals(imageSon[0])) {
								//检疫图片
								if(imageSon[1].indexOf("http") >=0) {
									qaCertPicUrlList.add(imageSon[1]+":"+imageSon[2]);
								}else {
									qaCertPicUrlList.add(SpringConfig.ss_picfile_srvdn + "/"+imageSon[1]);
								}
							}
						}
						
					}
					cdsd.setGsBillPicUrls(gsBillPicUrlList);
					cdsd.setQaCertPicUrls(qaCertPicUrlList);
				}
				
				cdsd.setAcceptDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("acceptDate"))) {
					if(!rs.getString("acceptDate").equalsIgnoreCase("null")) {
						cdsd.setAcceptDate(rs.getString("acceptDate"));
					}
				}
				
				return cdsd;
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_ledege_detail中根据条件查询数据条数
     */
    public Integer getCaMatSupDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String schName,String distName,List<String> schoolIds, String matName,String stdMatName, String rmcName, String supplierName, 
			String distrBatNumber, int schType, int acceptStatus, int optMode,String supplierNameLike,String supplierNameAll) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        //sb.append("select count(*) dataCount from ( ");
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_ledege_detail" );
        sb.append(" where 1=1  ");
		
        getCaMatSupDetsListCondition(listYearMonth, startDate, endDateAddOne,schName,distName,schoolIds,matName,stdMatName,
        		rmcName,supplierName,distrBatNumber,schType,acceptStatus,optMode,supplierNameLike,supplierNameAll, sb);
        //sb.append(" ) totalTable ");
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		dataCounts[0] = rs.getInt("dataCount");
        	}   
        });
        return dataCounts[0];
    }
    
    /**
     * 原料供应明细查询条件（查询列表和查询总数共用）
     * @param startYear
     * @param startMonth
     * @param startDate
     * @param endDateAddOne
     * @param schName
     * @param dishName
     * @param rmcName
     * @param distName
     * @param caterType
     * @param schType
     * @param schProp
     * @param optMode
     * @param menuName
     * @param sb
     */
	private void getCaMatSupDetsListCondition(List<String> listYearMonth, String startDate, String endDateAddOne,
    		String schName, String distName,List<String> shIdList,String matName,String stdMatName,String rmcName, String supplierName, 
			String distrBatNumber, int schType, int acceptStatus, int optMode,String supplierNameLike,String supplierNameAll,StringBuffer sb) {
		String [] arrYearMonth ;
		
		if(listYearMonth.size() > 0) {
			sb.append("AND (");
			for(String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb.append(" (year =  "+arrYearMonth[0]);
		        sb.append(" and month >= " +arrYearMonth[1]);
		        sb.append(" and month <= " +arrYearMonth[2]+") ");
		        
		        if(!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
        
        sb.append(" and DATE_FORMAT(use_date,'yyyy-MM-dd HH:mm:ss') >= \""+startDate+" 00:00:00\"");
        sb.append(" and DATE_FORMAT(use_date,'yyyy-MM-dd HH:mm:ss') < \""+endDateAddOne +" 00:00:00\"");
        sb.append(" and  school_name is not null and school_name !=\"\" and school_name !=\"null\" ");
        
        //学校
        if(CommonUtil.isNotEmpty(schName)) {
        	sb.append(" and school_id = \"" + schName+"\"");
        }
        
        //学校编号集合
		if(shIdList!=null && shIdList.size() >0) {
			 sb.append(" and ( ");
	        for (int i = 0; i < (shIdList.size() / 800 + ((shIdList.size() % 800) > 0 ? 1 : 0)); i++) {
	            int startIndex = i * 800;
	            if (startIndex >= shIdList.size()) {
	                startIndex = shIdList.size() - 1;
	            }
	            int ednIndex = (i + 1) * 800;
	            if (ednIndex >= shIdList.size()) {
	                ednIndex = shIdList.size();
	            }
	            String relationIds = StringUtils.join(shIdList.subList(startIndex, ednIndex).toArray(), ",");
	            relationIds="\""+relationIds.replaceAll(",", "\",\"")+"\"";
	            sb.append("  school_id in (" + relationIds + ")");
	            
	            if(ednIndex <shIdList.size()-1) {
	            	sb.append(" or ");
	            }
	        }
	        sb.append(" ) ");
		}
		
        //团餐公司
        if(CommonUtil.isNotEmpty(rmcName)) {
        	sb.append(" and supplier_id = \"" + rmcName+"\"");
        }
        //区
        if(CommonUtil.isNotEmpty(distName)) {
        	sb.append(" and area = \"" + distName+"\"");
        }
        //物料名称
        if(CommonUtil.isNotEmpty(matName)) {
        	sb.append(" and supplier_material_name like  \"%" +matName+"%\"");
        }
        
        //标准物料名称，按原料查询有效
        if(CommonUtil.isNotEmpty(stdMatName)) {
        	sb.append(" and name like  \"%" +stdMatName+"%\"");
        }
        
        //标准物料名称，按原料查询有效
        if(CommonUtil.isNotEmpty(stdMatName)) {
        	sb.append(" and name like  \"%" +stdMatName+"%\"");
        }
        
        //供应商名称
        if(CommonUtil.isNotEmpty(supplierNameAll)) {
        	sb.append(" and name = \"" + supplierNameAll+"\"");
        }
        
        //供应商名称(模糊查询)
        if(CommonUtil.isNotEmpty(supplierNameLike)) {
        	sb.append(" and supply_name like \"%" + supplierNameLike+"%\"");
        }
        
        //学校学段
        if(schType != -1) {
        	sb.append(" and level_name = " + schType);
        }
        
        
        //状态：0待验收 1已验收
        if(acceptStatus == 0 ) {
        	sb.append(" and haul_status != 3 ");
        }else if(acceptStatus == 1 ) {
        	sb.append(" and haul_status = 3 ");
        }
        //配货批次号
        if(CommonUtil.isNotEmpty(distrBatNumber)) {
        	sb.append(" and ware_batch_no = \"" + distrBatNumber+"\"");
        }
        
        //经营模式：经营模式（供餐模式，0：学校-自行加工  1：学校-食品加工商 2：外包-现场加工 3：外包-快餐配送 
        if(optMode != -1) {
        	if(optMode==0 || optMode == 1) {
        		sb.append(" and license_main_type = \"0\" ");
        		/*if(optMode ==0) {
        			sb.append(" and license_main_child = 0 ");
        		}else if (optMode ==1) {
        			sb.append(" and license_main_child = 1 ");
        		}*/
        	}else if (optMode==2 || optMode == 3) {
        		sb.append(" and license_main_type = \"1\" ");
        		if(optMode ==2) {
        			sb.append(" and license_main_child = 0 ");
        		}else if (optMode ==3) {
        			sb.append(" and license_main_child = 1 ");
        		}
        	}
        	
        }
	}

  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_ledege_detail中根据条件查询原料汇总数据列表
  	 */
    public List<CaMatSupStats> getCaMatSupStatsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distName,int schType, String schName,String matClassify, int matCategory, String matStdName,
    		Integer startNum,Integer endNum) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		//,max(material_type) matCategory 
        sb.append("select name standardName,wares_type_name matClassify,sum( actual_quantity ) actualQuan ");
        sb.append(" from app_t_edu_ledege_detail" );
        sb.append(" where  1=1 ");
        
        //拼接查询条件
        getCaMatSupStatsListCondition(listYearMonth, startDate, endDateAddOne, distName,schType,
        		schName,matClassify,matCategory,matStdName, sb);
        
        sb.append(" group by name,wares_type_name");
        sb.append(" order by actualQuan desc ");
        sb.append(" limit "+endNum);
        logger.info("执行sql:"+sb.toString());
        
		return (List<CaMatSupStats>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<CaMatSupStats>(){
			@Override
			public CaMatSupStats mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}else {
					CaMatSupStats cdss = new CaMatSupStats();
		        	cdss.setStandardName(rs.getString("standardName"));
		        	//cdss.setMatCategory(rs.getInt("matCategory"));
		        	cdss.setMatClassify(rs.getString("matClassify"));
		        	cdss.setActualQuan(rs.getFloat("actualQuan"));
					return cdss;
				}
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_ledege_detail中根据条件查询原料汇总数据条数
     */
    public Integer getCaMatSupStatsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distName,int schType, String schName,String matClassify, int matCategory, String matStdName) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(DISTINCT(name,wares_type_name)) dataCount ");
        sb.append(" from app_t_edu_ledege_detail" );
        sb.append(" where 1=1  ");
        //拼接查询条件
        getCaMatSupStatsListCondition(listYearMonth, startDate, endDateAddOne, distName,schType,
        		schName,matClassify,matCategory,matStdName, sb);
        
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		dataCounts[0] = rs.getInt("dataCount");
        	}   
        });
        return dataCounts[0];
    }
   
    /**
     * 菜品供应明细查询条件（查询列表和查询总数共用）
     * @param startYear
     * @param startMonth
     * @param startDate
     * @param endDateAddOne
     * @param schName
     * @param dishName
     * @param rmcName
     * @param distName
     * @param caterType
     * @param schType
     * @param schProp
     * @param optMode
     * @param menuName
     * @param sb
     */
	private void getCaMatSupStatsListCondition(List<String> listYearMonth, String startDate, String endDateAddOne,
			String distName,int schType, String schName,String matClassify, int matCategory, String matStdName,StringBuffer sb) {
		String [] arrYearMonth ;
		
		if(listYearMonth.size() > 0) {
			sb.append("AND (");
			for(String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb.append(" (year =  "+arrYearMonth[0]);
		        sb.append(" and month >= " +arrYearMonth[1]);
		        sb.append(" and month <= " +arrYearMonth[2]+") ");
		        
		        if(!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
        
        sb.append(" and DATE_FORMAT(use_date,'yyyy-MM-dd HH:mm:ss')  >= \""+startDate+" 00:00:00\"");
        sb.append(" and DATE_FORMAT(use_date,'yyyy-MM-dd HH:mm:ss')  < \""+endDateAddOne +" 00:00:00\"");
        sb.append(" and  school_name is not null and school_name !=\"\" and school_name !=\"null\" ");
        //学校
        if(CommonUtil.isNotEmpty(schName)) {
        	sb.append(" and school_id = \"" + schName+"\"");
        }
        //学校类型
        if(schType>=0) {
        	sb.append(" and level_name = \"" + schType+"\"");
        }
        
        //区
        if(CommonUtil.isNotEmpty(distName)) {
        	sb.append(" and area = \"" + distName+"\"");
        }
        //物料标准名称
        if(CommonUtil.isNotEmpty(matStdName)) {
        	sb.append(" and dishes_name like  \"" +"%"+matStdName+"%"+"\"");
        }
        
        //分类
        if(CommonUtil.isNotEmpty(matClassify)) {
        	sb.append(" and wares_type_name = \"" + matClassify+"\"");
        }
        
        //原料类别
        if(matCategory>=0) {
        	sb.append(" and material_type = \"" + matCategory+"\"");
        }
	}
	
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_ledege_total中根据条件查询原料汇总数据列表
  	 */
    public List<CaMatSupStats> getCaMatSupStatsListFromTotal(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distName,String matClassify, int matCategory, String matStdName,
    		Integer startNum,Integer endNum) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("select name standardName,max(material_type) matCategory ,wares_type_name matClassify,sum( actual_quantity ) actualQuan ");
        sb.append(" from app_t_edu_ledege_total" );
        sb.append(" where  1=1 ");

        //拼接查询条件
        getCaMatSupStatsListConditionFromTotal(listYearMonth, startDate, endDateAddOne,distName,matClassify,matCategory,matStdName, sb);
        sb.append(" group by name,wares_type_name ");
        sb.append(" order by actualQuan desc ");
        sb.append(" limit "+endNum);
        logger.info("执行sql:"+sb.toString());
        
		return (List<CaMatSupStats>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<CaMatSupStats>(){
			@Override
			public CaMatSupStats mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}else {
					CaMatSupStats cdss = new CaMatSupStats();
		        	cdss.setStandardName(rs.getString("standardName"));
		        	cdss.setMatCategory(rs.getInt("matCategory"));
		        	cdss.setMatClassify(rs.getString("matClassify"));
		        	cdss.setActualQuan(rs.getFloat("actualQuan"));
					return cdss;
				}
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_ledege_total中根据条件查询原料汇总数据条数
     */
    public Integer getCaMatSupStatsCountFromTotal(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distName,String matClassify, int matCategory, String matStdName) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	final Integer[] dataCounts={0};
    	
    	/*StringBuffer sb = new StringBuffer();
    	//wares_type_name
        sb.append("select count(name) dataCount ");
        sb.append(" from app_t_edu_ledege_total" );*/
        
    	StringBuffer sb = new StringBuffer();
    	sb.append(" select count(1) dataCount from (");
		sb.append("select name standardName,max(material_type) matCategory ");
        sb.append(" from app_t_edu_ledege_total" );
        
        sb.append(" where 1=1  ");
        getCaMatSupStatsListConditionFromTotal(listYearMonth, startDate, endDateAddOne, distName,matClassify,
        		matCategory,matStdName, sb);
        sb.append(" group by name,wares_type_name ");
        
        sb.append(") t ");
        
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		dataCounts[0] = rs.getInt("dataCount");
        	}   
        });
        return dataCounts[0];
    }
   
    /**
     * 菜品供应明细查询条件（查询列表和查询总数共用）
     * @param startYear
     * @param startMonth
     * @param startDate
     * @param endDateAddOne
     * @param schName
     * @param dishName
     * @param rmcName
     * @param distName
     * @param caterType
     * @param schType
     * @param schProp
     * @param optMode
     * @param menuName
     * @param sb
     */
	private void getCaMatSupStatsListConditionFromTotal(List<String> listYearMonth, String startDate, String endDateAddOne,
			String distName,String matClassify, int matCategory, String matStdName,StringBuffer sb) {
		String [] arrYearMonth ;
		
		if(listYearMonth.size() > 0) {
			sb.append("AND (");
			for(String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb.append(" (year =  "+arrYearMonth[0]);
		        sb.append(" and month >= " +arrYearMonth[1]);
		        sb.append(" and month <= " +arrYearMonth[2]+") ");
		        
		        if(!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
        
        sb.append(" and DATE_FORMAT(use_date,'yyyy-MM-dd HH:mm:ss') >= \""+startDate+" 00:00:00\"");
        sb.append(" and DATE_FORMAT(use_date,'yyyy-MM-dd HH:mm:ss') < \""+endDateAddOne +" 00:00:00\"");
        //区
        if(CommonUtil.isNotEmpty(distName)) {
        	sb.append(" and area = \"" + distName+"\"");
        }
        //物料标准名称
        if(CommonUtil.isNotEmpty(matStdName)) {
        	sb.append(" and name like  \"" +"%"+matStdName+"%"+"\"");
        }
        
        //分类
        if(CommonUtil.isNotEmpty(matClassify)) {
        	sb.append(" and wares_type_name = \"" + matClassify+"\"");
        }
        
        //原料类别
        if(matCategory>=0) {
        	sb.append(" and material_type = \"" + matCategory+"\"");
        }
	}

	//-----------------一键查询---------------------------------------------------------
	//-----------------学校列表（t_edu_school_new）---------------------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表t_edu_school_new中根据条件查询数据列表
  	 */
    public List<SearchSch> getSchList(String schName,String distName,List<String> shIdList,List<String> idList,
    		Integer startNum,Integer endNum) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     id schoolId,school_name schName, ");
        sb.append("     school_name schName, ");
        sb.append("     area distName,address detailAddr, ");
        sb.append("     social_credit_code uscc,address detailAddr, ");
        sb.append("     level_name schType,school_nature_name schProp,school_nature_sub_name, ");
        sb.append("     department_master_id subLevel,department_slave_id_name compDep,license_main_type fblMb, ");
        sb.append("     license_main_child optMode,corporation legalRep,food_safety_persion projContact, ");
        sb.append("     food_safety_mobilephone pcMobilePhone ");
        sb.append(" FROM ");
        sb.append("     t_edu_school_new ");
        sb.append(" where  1=1 ");
        getSchListCondition(schName, distName,shIdList,idList, sb);
        
        sb.append(" order by distName asc,schType asc");
        /*if(endNum >0) {
        	sb.append(" limit "+endNum);
        }*/
        logger.info("执行sql:"+sb.toString());
		return (List<SearchSch>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<SearchSch>() {

			@Override
			public SearchSch mapRow(ResultSet rs, int rowNum) throws SQLException {
				/*if(startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}*/
				/*if(startNum !=null && (rowNum > endNum || rowNum < startNum)) {
					return null;
				}*/
				SearchSch sch = new SearchSch();
				//学校编号
				sch.setSchoolId(rs.getString("schoolId"));
				//学校名称
				sch.setSchName("-");
				if(CommonUtil.isNotEmpty(rs.getString("schName"))) {
					sch.setSchName(rs.getString("schName"));
				}
				//区域名称
				sch.setDistName("-");
				if(CommonUtil.isNotEmpty(rs.getString("distName"))) {
					sch.setDistName(rs.getString("distName"));
				}
				//详细地址
				sch.setDetailAddr("-");
				if(CommonUtil.isNotEmpty(rs.getString("detailAddr"))) {
					sch.setDetailAddr(rs.getString("detailAddr"));
				}
				//统一社会信用代码证
				sch.setUscc("-");
				if(CommonUtil.isNotEmpty(rs.getString("uscc"))) {
					sch.setUscc(rs.getString("uscc"));
				}
				//学制
				sch.setSchType("-");
				if(CommonUtil.isNotEmpty(rs.getString("schType")) && !"-1".equals(rs.getString("schType")) &&
						CommonUtil.isInteger(rs.getString("schType"))) {
					sch.setSchType(AppModConfig.schTypeIdToNameMap.get(Integer.parseInt(rs.getString("schType"))));
				}
				//性质
				sch.setSchProp("-");
				if(CommonUtil.isNotEmpty(rs.getString("schProp"))) {
					sch.setSchProp(AppModConfig.getSchProp(rs.getString("schProp")));
				}
				//主管部门
				sch.setSubLevel("-");
				if(CommonUtil.isNotEmpty(rs.getString("subLevel"))) {
					sch.setSubLevel(rs.getString("subLevel"));
				}
				sch.setCompDep("其他");
				if(CommonUtil.isNotEmpty(rs.getString("compDep"))) {
					if("0".equals(sch.getSubLevel())) {      //其他     							
						sch.setCompDep(AppModConfig.compDepIdToNameMap0.get(rs.getString("compDep")));
					}
					else if("1".equals(sch.getSubLevel())) {      //部级   
						sch.setCompDep(AppModConfig.compDepIdToNameMap1.get(rs.getString("compDep")));
					}
					else if("2".equals(sch.getSubLevel())) {      //市级
						sch.setCompDep(AppModConfig.compDepIdToNameMap2.get(rs.getString("compDep")));
					}
					else if("3".equals(sch.getSubLevel())) {      //区级
						sch.setCompDep(AppModConfig.compDepIdToNameMap3.get(rs.getString("compDep")));
					}
					
				}
			    //管理部门(暂时不取)
				sch.setManagerDep("-");
				//食品经营许可证主体
				sch.setFblMb("-");
				if(CommonUtil.isNotEmpty(rs.getString("fblMb")) && CommonUtil.isInteger(rs.getString("fblMb"))) {
					sch.setFblMb(AppModConfig.fblMbIdToNameMap.get(Integer.parseInt(rs.getString("fblMb"))));
				}
				//供餐模式
				sch.setOptMode("-");
				if(CommonUtil.isNotEmpty(rs.getString("optMode")) && CommonUtil.isInteger(rs.getString("optMode"))) {
					sch.setOptMode(rs.getString("optMode"));
					
					String licenseMainType = rs.getString("fblMb");
					Integer licenseMainChild = Integer.parseInt(rs.getString("optMode"));
					//新的经营模式判断
			  		if(licenseMainType != null) {
			  			if(licenseMainType.equals("0")) {    //学校
			  				if(licenseMainChild != null) {
			  					if(licenseMainChild == 0)
			  						sch.setOptMode("自营");
			  					else if(licenseMainChild == 1)
			  						sch.setOptMode("自营");
			  				}
			  				sch.setOptMode("自营");
			  			}
			  			else if(licenseMainType.equals("1")) {    //外包
			  				if(licenseMainChild != null) {
			  					if(licenseMainChild == 0)
			  						sch.setOptMode("托管");
			  					else if(licenseMainChild == 1)
			  						sch.setOptMode("外送");
			  				}
			  			}else {
			  				sch.setOptMode("");
			  			}
			  		}
					
				}
				//法人代表
				sch.setLegalRep("-");
				if(CommonUtil.isNotEmpty(rs.getString("legalRep"))) {
					sch.setLegalRep(rs.getString("legalRep"));
				}
				//联系人
				sch.setProjContact("-");
				if(CommonUtil.isNotEmpty(rs.getString("projContact"))) {
					sch.setProjContact(rs.getString("projContact"));
				}
				//联系电话
				sch.setPcMobilePhone("-");
				if(CommonUtil.isNotEmpty(rs.getString("pcMobilePhone"))) {
					sch.setPcMobilePhone(rs.getString("pcMobilePhone"));
				}
				//食品经营许可证
				sch.setLicNo("-");
				/*if(CommonUtil.isNotEmpty(rs.getString("licNo"))) {
					sch.setSchName(rs.getString("licNo"));
				}*/
				
				return sch;
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表t_edu_school_new中根据条件查询数据条数
     */
    public Integer getSchCount(String schName,String distName,List<String> shIdList,List<String> idList) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	final Integer[] dataCounts={0};
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(DISTINCT(area)) dataCount ");
        sb.append(" from t_edu_school_new" );
        sb.append(" where 1=1  ");
        getSchListCondition(schName, distName,shIdList,idList, sb);
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		dataCounts[0] = rs.getInt("dataCount");
        	}   
        });
        return dataCounts[0];
    }
    
    /**
     * 学校查询条件（查询列表和查询总数共用）
     * @param schName
     * @param distName
     * @param sb
     */
	private void getSchListCondition(String schName, String distName,List<String> shIdList,List<String> idList,StringBuffer sb) {
		
		//是否失效
		sb.append(" and stat = \"1\"");
		//是否审核通过
		sb.append(" and reviewed = \"1\"");
		
		//区
        if(CommonUtil.isNotEmpty(distName)) {
        	sb.append(" and area = \"" + distName+"\"");
        }
        
        //学校名称
        if(CommonUtil.isNotEmpty(schName)) {
        	sb.append(" and school_name like \"%" + schName+"%\"");
        }
        
      //学校编号集合
  		if(shIdList!=null && shIdList.size() >0) {
  			 sb.append(" and ( ");
  	        for (int i = 0; i < (shIdList.size() / 800 + ((shIdList.size() % 800) > 0 ? 1 : 0)); i++) {
  	            int startIndex = i * 800;
  	            if (startIndex >= shIdList.size()) {
  	                startIndex = shIdList.size() - 1;
  	            }
  	            int ednIndex = (i + 1) * 800;
  	            if (ednIndex >= shIdList.size()) {
  	                ednIndex = shIdList.size();
  	            }
  	            String relationIds = StringUtils.join(shIdList.subList(startIndex, ednIndex).toArray(), ",");
  	            //加引号
  	            relationIds="\""+relationIds.replaceAll(",", "\",\"")+"\"";
  	            sb.append("  school_id in (" + relationIds + ")");
  	            
  	            if(ednIndex <shIdList.size()-1) {
  	            	sb.append(" or ");
  	            }
  	            
  	        }
  	        sb.append(" ) ");
  		}
  		
  	//学校id集合
  		if(idList!=null && idList.size() >0) {
  			 sb.append(" and ( ");
  	        for (int i = 0; i < (idList.size() / 800 + ((idList.size() % 800) > 0 ? 1 : 0)); i++) {
  	            int startIndex = i * 800;
  	            if (startIndex >= idList.size()) {
  	                startIndex = idList.size() - 1;
  	            }
  	            int ednIndex = (i + 1) * 800;
  	            if (ednIndex >= idList.size()) {
  	                ednIndex = idList.size();
  	            }
  	            String relationIds = StringUtils.join(idList.subList(startIndex, ednIndex).toArray(), ",");
  	            //加引号
  	            relationIds="\""+relationIds.replaceAll(",", "\",\"")+"\"";
  	            sb.append("  id in (" + relationIds + ")");
  	            
  	            if(ednIndex <idList.size()-1) {
  	            	sb.append(" or ");
  	            }
  	            
  	        }
  	        sb.append(" ) ");
  		}
        
	}
	
	//-----------------证书列表（t_pro_license）---------------------------------------------------------
 	/**
  	 * 从数据库app_saas_v1的数据表t_pro_license中根据条件查询数据列表
  	 */
    public List<SearchLicense> getLicenseList(String schName,String distName,List<String> relationIdList,List<String> supplierList,
    		List<Integer> licTypeList,Integer cerSource,List<Integer> cerSourceList,
    		Integer startNum,Integer endNum) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     id licId,lic_name licName, ");
        sb.append("     relation_id relationId, ");
        sb.append("     lic_no licNo,lic_type licType, ");
        sb.append("     lic_start_date licStartDate,lic_end_date licEndDate, ");
        sb.append("     written_name writtenName,stat stat,supplier_id supplierId ");
        sb.append(" FROM ");
        sb.append("     t_pro_license ");
        sb.append(" where  1=1 ");
        getLicenseCondition(schName, distName,relationIdList,supplierList,licTypeList,cerSource,cerSourceList, sb);
        
        //sb.append(" order by distName asc,schType asc");
        /*if(endNum >0) {
        	sb.append(" limit "+endNum);
        }*/
        logger.info("执行sql:"+sb.toString());
		return (List<SearchLicense>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<SearchLicense>() {

			@Override
			public SearchLicense mapRow(ResultSet rs, int rowNum) throws SQLException {
				/*if(startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}*/
				/*if(rowNum > endNum || rowNum < startNum) {
					return null;
				}*/
				SearchLicense sch = new SearchLicense();
				//证书编号
				sch.setLicId(rs.getString("licId"));
				//证书名称
				sch.setLicName("-");
				if(CommonUtil.isNotEmpty(rs.getString("licName"))) {
					sch.setLicName(rs.getString("licName"));
				}
				//关联编号
				sch.setRelationId("-");
				if(CommonUtil.isNotEmpty(rs.getString("relationId"))) {
					sch.setRelationId(rs.getString("relationId"));
				}
				//证书编码
				sch.setLicNo("-");
				if(CommonUtil.isNotEmpty(rs.getString("licNo"))) {
					sch.setLicNo(rs.getString("licNo"));
				}
				//整数类型
				if(CommonUtil.isNotEmpty(rs.getString("licType")) && CommonUtil.isInteger(rs.getString("licType"))) {
					sch.setLicType(rs.getInt("licType"));
				}
				//证书有效期开始日期
				sch.setLicStartDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("licStartDate"))) {
					sch.setLicStartDate(rs.getString("licStartDate"));
				}
				//证书有效期截止日期
				sch.setLicEndDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("licEndDate"))) {
					sch.setLicEndDate(rs.getString("licEndDate"));
				}
				
				//状态是否有效:0-无效,1-有效
				sch.setStat("-");
				if(CommonUtil.isNotEmpty(rs.getString("stat")) && CommonUtil.isInteger("stat")) {
					Integer stat = rs.getInt("stat");
					if(stat==1) {
						sch.setStat("有效");
					}else {
						sch.setStat("无效");
					}
				}
				
				//证书人名称
				sch.setWrittenName("-");
				if(CommonUtil.isNotEmpty(rs.getString("writtenName"))) {
					sch.setWrittenName(rs.getString("writtenName"));
				}
				
				if(CommonUtil.isNotEmpty(rs.getString("supplierId"))) {
					sch.setSupplierId(rs.getString("supplierId"));
				}
				
				return sch;
			}
		});
    }
    
   /**
    * 证书（查询列表和查询总数共用）
    * @param schName
    * @param distName
    * @param relationIdList
    * @param sb
    */
	private void getLicenseCondition(String schName, String distName,List<String> relationIdList,List<String> supplierList,List<Integer> licTypeList,
			Integer cerSource,List<Integer> cerSourceList,StringBuffer sb) {
		
		//证书实体编号
		if(relationIdList!=null && relationIdList.size() >0) {
			 sb.append(" and ( ");
	        for (int i = 0; i < (relationIdList.size() / 800 + ((relationIdList.size() % 800) > 0 ? 1 : 0)); i++) {
	            int startIndex = i * 800;
	            if (startIndex >= relationIdList.size()) {
	                startIndex = relationIdList.size() - 1;
	            }
	            int ednIndex = (i + 1) * 800;
	            if (ednIndex >= relationIdList.size()) {
	                ednIndex = relationIdList.size();
	            }
	            String relationIds = StringUtils.join(relationIdList.subList(startIndex, ednIndex).toArray(), ",");
	            relationIds="\""+relationIds.replaceAll(",", "\",\"")+"\"";
	            sb.append("  relation_id in (" + relationIds + ")");
	            
	            if(ednIndex <relationIdList.size()-1) {
	            	sb.append(" or ");
	            }
	        }
	        sb.append(" ) ");
		}
		
		//证书实体编号
		if(supplierList!=null && supplierList.size() >0) {
			 sb.append(" and ( ");
	        for (int i = 0; i < (supplierList.size() / 800 + ((supplierList.size() % 800) > 0 ? 1 : 0)); i++) {
	            int startIndex = i * 800;
	            if (startIndex >= supplierList.size()) {
	                startIndex = supplierList.size() - 1;
	            }
	            int ednIndex = (i + 1) * 800;
	            if (ednIndex >= supplierList.size()) {
	                ednIndex = supplierList.size();
	            }
	            String relationIds = StringUtils.join(supplierList.subList(startIndex, ednIndex).toArray(), ",");
	            relationIds="\""+relationIds.replaceAll(",", "\",\"")+"\"";
	            sb.append("  supplier_id in (" + relationIds + ")");
	            
	            if(ednIndex <supplierList.size()-1) {
	            	sb.append(" or ");
	            }
	        }
	        sb.append(" ) ");
		}
				
		//0:餐饮服务许可证 1:食品经营许可证 2:食品流通许可证 3:食品生产许可证 4:营业执照(事业单位法人证书) 5：组织机构代码(办学许可证) 6：税务登记证
		//7:检验检疫合格证；8：ISO认证证书；9：身份证 10：港澳居民来往内地通行证 11：台湾居民往来内地通行证 12：其他; 13:食品卫生许可证 
		//14:运输许可证 15:其他证件类型A 16:其他证件类型B 17:军官证 20:员工健康证；21：护照  22:A1证  23:B证  24:C证 25:A2证   
		if(licTypeList!=null && licTypeList.size() > 0) {
			sb.append(" AND (");
			for(Integer licType : licTypeList) {
				if(licType == null) {
					continue;
				}
		        sb.append(" lic_type = \"" +licType+"\"");
		        
		        if(!licType.equals(licTypeList.get(licTypeList.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
		//证书来源，0：供应商-，1：从业人员-雇员，2：商品，3：食堂--教委web学校的法人证
		if(cerSource!=null && cerSource >0) {
			sb.append(" and cer_source = \""+cerSource+"\"");
		}
		
		if(cerSourceList!=null && cerSourceList.size() > 0) {
			sb.append(" AND (");
			for(Integer licType : cerSourceList) {
				if(licType == null) {
					continue;
				}
		        sb.append(" cer_source = \"" +licType+"\"");
		        
		        if(!licType.equals(cerSourceList.get(cerSourceList.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
		
		//是否失效
		sb.append(" and stat = \"1\"");
		//是否审核通过(暂时不作过滤)
		//sb.append(" and reviewed = \"1\"");

		
	}
	
	//-----------------团餐公司和供应商列表（t_pro_supplier）---------------------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表t_pro_supplier中根据条件查询数据列表
  	 */
    public List<SearchSupplier> getSupplierList(String distName,List<String> supplierIdList,
    		Integer startNum,Integer endNum) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     id supplierId ,supplier_name supplierName, ");
        sb.append("     area distName,address detailAddr, ");
        sb.append("     address detailAddr, ");
        sb.append("     corporation legalRep,contacts contact, ");
        sb.append("     contact_way mobilePhone ");
        sb.append(" FROM ");
        sb.append("     t_pro_supplier ");
        sb.append(" where  1=1 ");
        getSupplierListCondition(distName,supplierIdList, sb);
        
        //sb.append(" order by distName asc,schType asc");
        /*if(endNum >0) {
        	sb.append(" limit "+endNum);
        }*/
        logger.info("执行sql:"+sb.toString());
		return (List<SearchSupplier>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<SearchSupplier>() {

			@Override
			public SearchSupplier mapRow(ResultSet rs, int rowNum) throws SQLException {
				/*if(startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}*/
				/*if(startNum !=null && (rowNum > endNum || rowNum < startNum)) {
					return null;
				}*/
				SearchSupplier sch = new SearchSupplier();
				//团餐公司（或供应商）编号
				sch.setSupplierId(rs.getString("supplierId"));
				//学校名称
				sch.setSupplierName("-");
				if(CommonUtil.isNotEmpty(rs.getString("supplierName"))) {
					sch.setSupplierName(rs.getString("supplierName"));
				}
				//区域名称
				sch.setDistName("-");
				if(CommonUtil.isNotEmpty(rs.getString("distName"))) {
					sch.setDistName(rs.getString("distName"));
				}
				//详细地址
				sch.setDetailAddr("-");
				if(CommonUtil.isNotEmpty(rs.getString("detailAddr"))) {
					sch.setDetailAddr(rs.getString("detailAddr"));
				}
				//统一社会信用代码证
				sch.setUscc("-");
				//法人代表
				sch.setLegalRep("-");
				if(CommonUtil.isNotEmpty(rs.getString("legalRep"))) {
					sch.setLegalRep(rs.getString("legalRep"));
				}
				//联系人
				sch.setContact("-");
				if(CommonUtil.isNotEmpty(rs.getString("contact"))) {
					sch.setContact(rs.getString("contact"));
				}
				//联系电话
				sch.setMobilePhone("-");
				if(CommonUtil.isNotEmpty(rs.getString("mobilePhone"))) {
					sch.setMobilePhone(rs.getString("mobilePhone"));
				}
				//食品经营许可证
				sch.setFblNo("-");
				//服务起止时间
				sch.setServiceDate("-");
				//食品经营许可证有效日期
				sch.setFblExpireDate("-");
				
				return sch;
			}
		});
    }
    
    /**
     * 团餐公司（或供应商）查询条件（查询列表和查询总数共用）
     * @param schName
     * @param distName
     * @param sb
     */
	private void getSupplierListCondition(String distName,List<String> supplierIdList,StringBuffer sb) {
		//证书实体编号
		if(supplierIdList!=null && supplierIdList.size() >0) {
			 sb.append(" and ( ");
	        for (int i = 0; i < (supplierIdList.size() / 800 + ((supplierIdList.size() % 800) > 0 ? 1 : 0)); i++) {
	            int startIndex = i * 800;
	            if (startIndex >= supplierIdList.size()) {
	                startIndex = supplierIdList.size() - 1;
	            }
	            int ednIndex = (i + 1) * 800;
	            if (ednIndex >= supplierIdList.size()) {
	                ednIndex = supplierIdList.size();
	            }
	            String relationIds = StringUtils.join(supplierIdList.subList(startIndex, ednIndex).toArray(), ",");
	            relationIds="\""+relationIds.replaceAll(",", "\",\"")+"\"";
	            sb.append("  id in (" + relationIds + ")");
	
	            if(ednIndex <supplierIdList.size()-1) {
	            	sb.append(" or ");
	            }
	        }
	        sb.append(" ) ");
		}
		//是否失效
		//sb.append(" and stat = \"1\"");
		//是否审核通过
		//sb.append(" and reviewed = \"1\"");
		
		//区
        if(CommonUtil.isNotEmpty(distName)) {
        	sb.append(" and area = \"" + distName+"\"");
        }
        
	}
	
	//-----------------供餐相关（app_t_edu_package）---------------------------------------------------------
 	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_calendar中根据条件查询数据列表
  	 */
    public List<EduPackage> getPackageList(List<String> listYearMonth, String startDate, String endDateAddOne,String schName,
    		String distName,List<String> schoolIdList) {
    	JdbcTemplate jdbcTemplateTemp  =getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     use_date useDate,school_id schoolId, ");
        sb.append("     have_class haveClass ");
        sb.append(" FROM ");
        sb.append("     app_t_edu_calendar ");
        sb.append(" where  1=1 ");
        getPackageCondition(listYearMonth,startDate,endDateAddOne,schName, distName,schoolIdList, sb);
        
        //sb.append(" order by distName asc,schType asc");
        /*if(endNum >0) {
        	sb.append(" limit "+endNum);
        }*/
        logger.info("执行sql:"+sb.toString());
		return (List<EduPackage>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<EduPackage>() {

			@Override
			public EduPackage mapRow(ResultSet rs, int rowNum) throws SQLException {
				/*if(rowNum > (endNum-(startNum==0?1:startNum))) {
					return null;
				}*/
				/*if(rowNum > endNum || rowNum < startNum) {
					return null;
				}*/
				EduPackage obj = new EduPackage();
				
				//供餐日期
				obj.setUseDate(rs.getString("useDate"));
				//学校编号
				obj.setSchoolId(rs.getString("schoolId"));
				//是否供餐
				obj.setHaveClass(0);
				if(CommonUtil.isNotEmpty(rs.getString("haveClass")) && CommonUtil.isInteger(rs.getString("haveClass"))) {
					obj.setHaveClass(rs.getInt("haveClass"));
				}
				
				return obj;
			}
		});
    }
    
   /**
    * 学校供餐信息（查询列表和查询总数共用）
    * @param schName
    * @param distName
    * @param relationIdList
    * @param sb
    */
	private void getPackageCondition(List<String> listYearMonth, String startDate, String endDateAddOne,
			String schName, String distName,List<String> schoolIdList,StringBuffer sb) {
		
		String [] arrYearMonth ;
		if(listYearMonth.size() > 0) {
			sb.append("AND (");
			for(String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb.append(" (year =  "+arrYearMonth[0]);
		        sb.append(" and month >= " +arrYearMonth[1]);
		        sb.append(" and month <= " +arrYearMonth[2]+") ");
		        
		        if(!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
        
        sb.append(" and DATE_FORMAT(use_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(use_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
		
		//证书实体编号
		if(schoolIdList!=null && schoolIdList.size() >0) {
			sb.append(" and ( ");
	        for (int i = 0; i < (schoolIdList.size() / 800 + ((schoolIdList.size() % 800) > 0 ? 1 : 0)); i++) {
	            int startIndex = i * 800;
	            if (startIndex >= schoolIdList.size()) {
	                startIndex = schoolIdList.size() - 1;
	            }
	            int ednIndex = (i + 1) * 800;
	            if (ednIndex >= schoolIdList.size()) {
	                ednIndex = schoolIdList.size();
	            }
	            String relationIds = StringUtils.join(schoolIdList.subList(startIndex, ednIndex).toArray(), ",");
	            relationIds="\""+relationIds.replaceAll(",", "\",\"")+"\"";
	            sb.append("  school_id in (" + relationIds + ")");
	            
	            if(ednIndex <schoolIdList.size()-1) {
	            	sb.append(" or ");
	            }
	        }
	        sb.append(" ) ");
		}
		
	}
	
	//-----------------一键排查（app_t_edu_material_dish）---------------------------------------------------------
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_material_dish中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduMaterialDishD> getAppTEduMaterialDishDList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduMaterialDishD inputObj,
    		Integer startNum,Integer endNum) {
        logger.info("[Enter dao method] {}-{}", "AppTEduMaterialDishDDao", "getAppTEduMaterialDishDList");
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append("id id, ");
        sb.append("  supply_date supplyDate, school_id schoolId, school_name schoolName, area area, ");
        sb.append("  address address, corporation corporation, food_safety_persion foodSafetyPersion, food_safety_mobilephone foodSafetyMobilephone, ");
        sb.append("  material_id materialId, material_name materialName, supply_id supplyId, supply_name supplyName, ");
        sb.append("  dishes dishes, proj_id projId, supplier_id supplierId, supplier_name supplierName, ");
        sb.append("  level_name levelName, ware_batch_no wareBatchNo ");
        sb.append(" from app_t_edu_material_dish_d " );
        sb.append(" where  1=1 ");
        getAppTEduMaterialDishDListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+endNum);
        }        logger.info("执行sql:"+sb.toString());
		return (List<AppTEduMaterialDishD>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<AppTEduMaterialDishD>() {
			@Override
			public AppTEduMaterialDishD mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				if(startNum!=null && endNum!=null && startNum!=-1 &&  endNum != -1 
						&& startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
								AppTEduMaterialDishD obj = new AppTEduMaterialDishD();

				obj.setId("-");
				if(CommonUtil.isNotEmpty(rs.getString("id"))) {
					obj.setId(rs.getString("id"));
				}
				obj.setSupplyDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("supplyDate"))) {
					obj.setSupplyDate(rs.getString("supplyDate"));
				}
				obj.setSchoolId("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolId"))) {
					obj.setSchoolId(rs.getString("schoolId"));
				}
				obj.setSchoolName("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolName"))) {
					obj.setSchoolName(rs.getString("schoolName"));
				}
				obj.setArea("-");
				if(CommonUtil.isNotEmpty(rs.getString("area"))) {
					obj.setArea(rs.getString("area"));
				}
				obj.setAddress("-");
				if(CommonUtil.isNotEmpty(rs.getString("address"))) {
					obj.setAddress(rs.getString("address"));
				}
				obj.setCorporation("-");
				if(CommonUtil.isNotEmpty(rs.getString("corporation"))) {
					obj.setCorporation(rs.getString("corporation"));
				}
				obj.setFoodSafetyPersion("-");
				if(CommonUtil.isNotEmpty(rs.getString("foodSafetyPersion"))) {
					obj.setFoodSafetyPersion(rs.getString("foodSafetyPersion"));
				}
				obj.setFoodSafetyMobilephone("-");
				if(CommonUtil.isNotEmpty(rs.getString("foodSafetyMobilephone"))) {
					obj.setFoodSafetyMobilephone(rs.getString("foodSafetyMobilephone"));
				}
				obj.setMaterialId("-");
				if(CommonUtil.isNotEmpty(rs.getString("materialId"))) {
					obj.setMaterialId(rs.getString("materialId"));
				}
				obj.setMaterialName("-");
				if(CommonUtil.isNotEmpty(rs.getString("materialName"))) {
					obj.setMaterialName(rs.getString("materialName"));
				}
				obj.setSupplyId("-");
				if(CommonUtil.isNotEmpty(rs.getString("supplyId"))) {
					obj.setSupplyId(rs.getString("supplyId"));
				}
				obj.setSupplyName("-");
				if(CommonUtil.isNotEmpty(rs.getString("supplyName"))) {
					obj.setSupplyName(rs.getString("supplyName"));
				}
				obj.setDishes("-");
				if(CommonUtil.isNotEmpty(rs.getString("dishes"))) {
					obj.setDishes(rs.getString("dishes"));
				}
				obj.setProjId("-");
				if(CommonUtil.isNotEmpty(rs.getString("projId"))) {
					obj.setProjId(rs.getString("projId"));
				}
				obj.setSupplierId("-");
				if(CommonUtil.isNotEmpty(rs.getString("supplierId"))) {
					obj.setSupplierId(rs.getString("supplierId"));
				}
				obj.setSupplierName("-");
				if(CommonUtil.isNotEmpty(rs.getString("supplierName"))) {
					obj.setSupplierName(rs.getString("supplierName"));
				}
				obj.setLevelName("-");
				if(CommonUtil.isNotEmpty(rs.getString("levelName"))) {
					obj.setLevelName(rs.getString("levelName"));
				}
				obj.setWareBatchNo("-");
				if(CommonUtil.isNotEmpty(rs.getString("wareBatchNo"))) {
					obj.setWareBatchNo(rs.getString("wareBatchNo"));
				}
				return obj;
			}
		});
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_material_dish中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduMaterialDishDListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduMaterialDishD inputObj) {
        logger.info("[Enter dao method] {}-{}", "AppTEduMaterialDishDDao", "getAppTEduMaterialDishDListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_material_dish_d " );
        sb.append(" where 1=1  ");
        
        getAppTEduMaterialDishDListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		dataCounts[0] = rs.getInt("dataCount");
        	}   
        });
        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
        return dataCounts[0];
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_material_dish中根据条件查询数据列表条件
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @param sb
    */
    public void getAppTEduMaterialDishDListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduMaterialDishD inputObj,
    StringBuffer sb) {
        logger.info("[Enter dao method] {}-{}", "AppTEduMaterialDishDDao", "getAppTEduMaterialDishDListCondition");
        Long daoStartTime = System.currentTimeMillis();
		String [] arrYearMonth ;
		
		if(listYearMonth.size() > 0) {
			sb.append("AND (");
			for(String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb.append(" (year =  "+arrYearMonth[0]);
		        sb.append(" and month >= " +arrYearMonth[1]);
		        sb.append(" and month <= " +arrYearMonth[2]+")");
		        
		        if(!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
		
        sb.append(" and DATE_FORMAT(supply_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(supply_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
        //id
        if(CommonUtil.isNotEmpty(inputObj.getId())) {
        	sb.append(" and id = \"" + inputObj.getId()+"\"");
        }

        //schoolId
        if(CommonUtil.isNotEmpty(inputObj.getSchoolId())) {
        	sb.append(" and school_id = \"" + inputObj.getSchoolId()+"\"");
        }

        //schoolName
        if(CommonUtil.isNotEmpty(inputObj.getSchoolName())) {
        	sb.append(" and school_name = \"" + inputObj.getSchoolName()+"\"");
        }

        //area
        if(CommonUtil.isNotEmpty(inputObj.getArea())) {
        	sb.append(" and area = \"" + inputObj.getArea()+"\"");
        }

        //address
        if(CommonUtil.isNotEmpty(inputObj.getAddress())) {
        	sb.append(" and address = \"" + inputObj.getAddress()+"\"");
        }

        //corporation
        if(CommonUtil.isNotEmpty(inputObj.getCorporation())) {
        	sb.append(" and corporation = \"" + inputObj.getCorporation()+"\"");
        }

        //foodSafetyPersion
        if(CommonUtil.isNotEmpty(inputObj.getFoodSafetyPersion())) {
        	sb.append(" and food_safety_persion = \"" + inputObj.getFoodSafetyPersion()+"\"");
        }

        //foodSafetyMobilephone
        if(CommonUtil.isNotEmpty(inputObj.getFoodSafetyMobilephone())) {
        	sb.append(" and food_safety_mobilephone = \"" + inputObj.getFoodSafetyMobilephone()+"\"");
        }

        //materialId
        if(CommonUtil.isNotEmpty(inputObj.getMaterialId())) {
        	sb.append(" and material_id = \"" + inputObj.getMaterialId()+"\"");
        }

        //materialName
        if(CommonUtil.isNotEmpty(inputObj.getMaterialName())) {
        	sb.append(" and material_name = \"" + inputObj.getMaterialName()+"\"");
        }

        //supplyId
        if(CommonUtil.isNotEmpty(inputObj.getSupplyId())) {
        	sb.append(" and supply_id = \"" + inputObj.getSupplyId()+"\"");
        }

        //supplyName
        if(CommonUtil.isNotEmpty(inputObj.getSupplyName())) {
        	sb.append(" and supply_name like \"%" + inputObj.getSupplyName()+"%\"");
        }

        //dishes
        if(CommonUtil.isNotEmpty(inputObj.getDishes())) {
        	sb.append(" and dishes = \"" + inputObj.getDishes()+"\"");
        }

        //projId
        if(CommonUtil.isNotEmpty(inputObj.getProjId())) {
        	sb.append(" and proj_id = \"" + inputObj.getProjId()+"\"");
        }

        //supplierId
        if(CommonUtil.isNotEmpty(inputObj.getSupplierId())) {
        	sb.append(" and supplier_id = \"" + inputObj.getSupplierId()+"\"");
        }

        //supplierName
        if(CommonUtil.isNotEmpty(inputObj.getSupplierName())) {
        	sb.append(" and supplier_name like \"%" + inputObj.getSupplierName()+"%\"");
        }

        //levelName
        if(CommonUtil.isNotEmpty(inputObj.getLevelName())) {
        	sb.append(" and level_name = \"" + inputObj.getLevelName()+"\"");
        }

        //wareBatchNo
        if(CommonUtil.isNotEmpty(inputObj.getWareBatchNo())) {
        	sb.append(" and ware_batch_no = \"" + inputObj.getWareBatchNo()+"\"");
        }

        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
    }

	//----------公共方法---------------------------------------------------------------------------
	public static JdbcTemplate getJdbcTemplateHive(JdbcTemplate jdbcTemplateHive,JdbcTemplate jdbcTemplateHive2,DataSource dataSourceHive) {
		
		JdbcTemplate jdbcTemplateHiveTemp = jdbcTemplateHive;
		logger.info("***********************************************************************");
		if(jdbcTemplateHiveTemp == null) {
			if(jdbcTemplateHive2!=null) {
				jdbcTemplateHiveTemp = jdbcTemplateHive2;
				logger.info("***********************************************************************01 jdbcTemplateHive2");
			}else {
				jdbcTemplateHiveTemp = null;
			}
		}
		boolean hive1IsBreak = false;
		if(jdbcTemplateHiveTemp !=null) {
			String sql = "select 1 from t_edu_school_new limit 0";
			try {
				jdbcTemplateHiveTemp.queryForMap(sql);
			}catch(Exception ex) {
				if(ex.getMessage().contains("java.net.SocketException")) {
			    	logger.info("-------------------------------------------------------------------新线程启动hive");
					jdbcTemplateHiveTemp = jdbcTemplateHive2;
					logger.info("***********************************************************************catch jdbcTemplateHive2"+ex.getMessage());
				}
				hive1IsBreak= true;
				logger.info("*********************************"+ex.getMessage());
			}
		}
		
		if(hive1IsBreak) {
			//如果hive1连接有问题，测试hive2
			if(jdbcTemplateHiveTemp !=null) {
				String sql = "select 1 from t_edu_school_new limit 0";
				try {
					jdbcTemplateHiveTemp.queryForMap(sql);
				}catch(Exception ex) {
					if(ex.getMessage().contains("java.net.SocketException")) {
						
						logger.info("***********************************************************************catch jdbcTemplateHive2"+ex.getMessage());
						//重新加载hive1
						String url = "172.18.14.31";
						String uri = "";
						try {
							uri = jdbcTemplateHiveTemp.getDataSource().getConnection().getClientInfo().getProperty("Uri");
							logger.info("uri:"+uri);
						} catch (SQLException e) {
							e.printStackTrace();
						}
						if(CommonUtil.isNotEmpty(uri) && uri.indexOf("172.18.14.35") >=0) {
							url = "172.18.14.31";
						}
						dataSourceHive = new DataSourceConn().getDataSource(url);
						jdbcTemplateHiveTemp = new JdbcTemplate(dataSourceHive);
					}
					hive1IsBreak= true;
					logger.info("*********************************"+ex.getMessage());
				}
			}else {
				
			}
		}
		logger.info("***********************************************************************"+jdbcTemplateHiveTemp.getDataSource().toString());
		
		return jdbcTemplateHiveTemp;
	}

	
	
}
