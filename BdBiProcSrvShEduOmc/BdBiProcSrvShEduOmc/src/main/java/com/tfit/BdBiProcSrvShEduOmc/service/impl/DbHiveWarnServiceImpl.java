package com.tfit.BdBiProcSrvShEduOmc.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommon;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLicDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLics;
import com.tfit.BdBiProcSrvShEduOmc.obj.warn.AppTEduNoLedgerCollectD;
import com.tfit.BdBiProcSrvShEduOmc.obj.warn.AppTEduNoPlatoonCollectD;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 预警先关hive库的查询
 * @author Administrator
 *
 */
@Service
public class DbHiveWarnServiceImpl implements DbHiveWarnService {
	private static final Logger logger = LogManager.getLogger(DbHiveWarnServiceImpl.class.getName());
	
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
  	
  	//--------------------证照预警-汇总----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_warn_total中根据条件查询数据列表
  	 */
    public List<WarnCommonLics> getWarnLicList(Integer licType, List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,Integer target ) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append("select from_unixtime(unix_timestamp(warn_date,'yyyy-MM-dd'),'yyyy/MM/dd') warnDate,district_id distName,");
        sb.append("untreated_sum noProcWarnNum,rejected_sum rejectWarnNum,");
        sb.append("review_sum auditWarnNum, deal_sum elimWarnNum,nodeal_sum noProcUnitNum ");
        sb.append(" from app_t_edu_warn_total " );
        sb.append(" where  1=1 ");
        
        getWarnLicCondition(licType,listYearMonth, startDate, endDateAddOne,
        		distId,target,sb);
        logger.info("执行sql:"+sb.toString());
		return (List<WarnCommonLics>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<WarnCommonLics>() {
			@Override
			public WarnCommonLics mapRow(ResultSet rs, int rowNum) throws SQLException {
				WarnCommonLics cdsd = new WarnCommonLics();
				
				cdsd.setWarnPeriod(rs.getString("warnDate"));
				//区域名称
				cdsd.setDistName("-");
	            if(StringUtils.isNotEmpty(rs.getString("distName"))) {
	            	cdsd.setDistName(rs.getString("distName"));
	            }
				//预警总数
				//int totalWarnNum;
				
				//未处理预警数
				cdsd.setNoProcWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("noProcWarnNum")) && rs.getInt("noProcWarnNum")>0) {
	            	cdsd.setNoProcWarnNum(rs.getInt("noProcWarnNum"));
	            }
				//已驳回预警数
				cdsd.setRejectWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("rejectWarnNum")) && rs.getInt("rejectWarnNum")>0) {
	            	cdsd.setRejectWarnNum(rs.getInt("rejectWarnNum"));
	            }
				//审核中预警数
				cdsd.setAuditWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("auditWarnNum")) && rs.getInt("auditWarnNum")>0) {
	            	cdsd.setAuditWarnNum(rs.getInt("auditWarnNum"));
	            }
				//已消除预警数
				cdsd.setElimWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("elimWarnNum")) && rs.getInt("elimWarnNum")>0) {
	            	cdsd.setElimWarnNum(rs.getInt("elimWarnNum"));
	            }
				
				//已消除预警数
				cdsd.setNoProcUnitNum(0);
				if(StringUtils.isNotEmpty(rs.getString("noProcUnitNum")) && rs.getInt("noProcUnitNum")>0) {
	            	cdsd.setNoProcUnitNum(rs.getInt("noProcUnitNum"));
	            }
				
				//预警处理率
				//float warnProcRate;
				return cdsd;
			}
		});
    }

    /**
     * 预警汇总查询条件（查询列表和查询总数共用）
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
	private void getWarnLicCondition(Integer licType,List<String> listYearMonth, String startDate, String endDateAddOne,
    		String distId,Integer target,
			StringBuffer sb) {
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
		
        sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
        if(target!=null) {
        	sb.append(" and target = \"" + target+"\"");
        }
        
        //sb.append(" and  school_name is not null and school_name !=\"\" and school_name !=\"null\" ");
        //证书类型：1：学校证书 2：团餐公司证书 3：人员证书
        if(licType!=null) {
        	//1：学校证件 2:团餐公司证件 3：人员证件  4：全部
        	sb.append(" and lic_type =  " + licType);
        }
        
		//String distId, 
        //区属，所属主管部门
        if(StringUtils.isNotEmpty(distId)) {
        	sb.append(" and district_id = \"" + distId+"\"");
        }
	}
	
  	//--------------------证照预警-汇总-按学校性质0 公办   2 民办 3 "外籍人员子女学校"----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_warn_nature_total中根据条件查询数据列表
  	 */
    public List<WarnCommonLics> getWarnLicListByNature(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,Integer target ) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append(" select from_unixtime(unix_timestamp(use_date,'yyyy-MM-dd'),'yyyy/MM/dd') warnDate,district_id distName,nature,");
        sb.append(" warn_nodeal_sum noProcWarnNum,");
        sb.append(" warn_sum totalWarnNum,warn_dis_nodeal_sum noProcUnitNum ");
        sb.append(" from app_t_edu_warn_nature_total " );
        sb.append(" where  1=1 ");
        
        getWarnLicConditionByNature(listYearMonth, startDate, endDateAddOne,
        		distId,target,sb);
        logger.info("执行sql:"+sb.toString());
		return (List<WarnCommonLics>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<WarnCommonLics>() {
			@Override
			public WarnCommonLics mapRow(ResultSet rs, int rowNum) throws SQLException {
				WarnCommonLics cdsd = new WarnCommonLics();
				
				cdsd.setWarnPeriod(rs.getString("warnDate"));
				//区域名称
				cdsd.setDistName("-");
	            if(StringUtils.isNotEmpty(rs.getString("distName"))) {
	            	cdsd.setDistName(rs.getString("distName"));
	            }
	            
	            //学校学制
				cdsd.setNature("-");
	            if(StringUtils.isNotEmpty(rs.getString("nature")) && !"-1".equals(rs.getString("nature")) && 
	            		!"null".equals(rs.getString("nature"))) {
	            	cdsd.setNature(rs.getString("nature"));
	            }
	            
				//预警总数
	            cdsd.setTotalWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("totalWarnNum")) && rs.getInt("totalWarnNum")>0) {
	            	cdsd.setTotalWarnNum(rs.getInt("totalWarnNum"));
	            }
				
				//未处理预警数
				cdsd.setNoProcWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("noProcWarnNum")) && rs.getInt("noProcWarnNum")>0) {
	            	cdsd.setNoProcWarnNum(rs.getInt("noProcWarnNum"));
	            }
				//未处理预警单位
				cdsd.setNoProcUnitNum(0);
				if(StringUtils.isNotEmpty(rs.getString("noProcUnitNum")) && rs.getInt("noProcUnitNum")>0) {
	            	cdsd.setNoProcUnitNum(rs.getInt("noProcUnitNum"));
	            }
				
				//预警处理率
				//float warnProcRate;
				return cdsd;
			}
		});
    }

    /**
     * 预警汇总查询条件（查询列表和查询总数共用）
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
	private void getWarnLicConditionByNature(List<String> listYearMonth, String startDate, String endDateAddOne,
    		String distId,Integer target,
			StringBuffer sb) {
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
        
        if(target!=null) {
        	sb.append(" and target = \"" + target+"\"");
        }
        
		//String distId, 
        //区属，所属主管部门
        if(StringUtils.isNotEmpty(distId)) {
        	sb.append(" and district_id = \"" + distId+"\"");
        }
	}
	
 	//--------------------证照预警-汇总-按学校学制----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_warn_level_total中根据条件查询数据列表
  	 */
    public List<WarnCommonLics> getWarnLicListByLevel(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,Integer target ) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append(" select from_unixtime(unix_timestamp(use_date,'yyyy-MM-dd'),'yyyy/MM/dd') warnDate,district_id distName,level,");
        sb.append(" warn_nodeal_sum noProcWarnNum,");
        sb.append(" warn_sum totalWarnNum,warn_dis_nodeal_sum noProcUnitNum ");
        sb.append(" from app_t_edu_warn_level_total " );
        sb.append(" where  1=1 ");
        
        getWarnLicConditionByLevel(listYearMonth, startDate, endDateAddOne,
        		distId,target,sb);
        logger.info("执行sql:"+sb.toString());
		return (List<WarnCommonLics>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<WarnCommonLics>() {
			@Override
			public WarnCommonLics mapRow(ResultSet rs, int rowNum) throws SQLException {
				WarnCommonLics cdsd = new WarnCommonLics();
				
				cdsd.setWarnPeriod(rs.getString("warnDate"));
				//区域名称
				cdsd.setDistName("-");
	            if(StringUtils.isNotEmpty(rs.getString("distName"))) {
	            	cdsd.setDistName(rs.getString("distName"));
	            }
	            
	            //学校学制
				cdsd.setLevel("-");
	            if(StringUtils.isNotEmpty(rs.getString("level")) && !"-1".equals(rs.getString("level")) && 
	            		!"null".equals(rs.getString("level"))) {
	            	cdsd.setLevel(rs.getString("level"));
	            }
	            
				//预警总数
	            cdsd.setTotalWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("totalWarnNum")) && rs.getInt("totalWarnNum")>0) {
	            	cdsd.setTotalWarnNum(rs.getInt("totalWarnNum"));
	            }
				
				//未处理预警数
				cdsd.setNoProcWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("noProcWarnNum")) && rs.getInt("noProcWarnNum")>0) {
	            	cdsd.setNoProcWarnNum(rs.getInt("noProcWarnNum"));
	            }
				//已消除预警数
				cdsd.setNoProcUnitNum(0);
				if(StringUtils.isNotEmpty(rs.getString("noProcUnitNum")) && rs.getInt("noProcUnitNum")>0) {
	            	cdsd.setNoProcUnitNum(rs.getInt("noProcUnitNum"));
	            }
				
				return cdsd;
			}
		});
    }

    /**
     * 预警汇总查询条件（查询列表和查询总数共用）
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
	private void getWarnLicConditionByLevel(List<String> listYearMonth, String startDate, String endDateAddOne,
    		String distId,Integer target,
			StringBuffer sb) {
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
        
        if(target!=null) {
        	sb.append(" and target = \"" + target+"\"");
        }
        
		//String distId, 
        //区属，所属主管部门
        if(StringUtils.isNotEmpty(distId)) {
        	sb.append(" and district_id = \"" + distId+"\"");
        }
	}
	
  	//--------------------证照预警-详情----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询数据列表
  	 */
    public List<WarnCommonLicDets> getWarnLicDetsList(Integer warnType,Integer certificateType, List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId, String schName, int schType, int licType, int licStatus, int licAuditStatus, List<Object> licAuditStatussList,
			String startElimDate, String endElimDate, String startValidDate, String endValidDate, 
			int schProp, String licNo,String rmcName,String fullName,Integer target,
			String departmentId,List<Object> departmentIdList,
			String trigWarnUnit,String area,String warnTypeChild,String warnLevelName,
    		Integer startNum,Integer endNum,Map<Integer, String> schoolPropertyMap) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append("select from_unixtime(unix_timestamp(warn_date,'yyyy-MM-dd'),'yyyy/MM/dd') warnDate,school_name schName,district_id distName,");
        sb.append("level_name schType,school_nature_name schProp,");
        sb.append("lic_no licNo, warn_type_child warnTypeChild,");
        sb.append("from_unixtime(unix_timestamp(deal_date,'yyyy-MM-dd'),'yyyy/MM/dd') elimDate,from_unixtime(unix_timestamp(lose_time,'yyyyMMddHH'),'yyyy/MM/dd') validDate,");
        sb.append(" warn_stat licAuditStatus, ");
        sb.append(" address address,food_safety_persion foodSafetyPersion,food_safety_mobilephone foodSafetyMobilephone,area, ");
        if(warnType==3) {
        	//过保预警
        	sb.append(" material_name materialName, batch_no batchNo,");
        	sb.append(" car_code carCode, driver_name driverName, from_unixtime(unix_timestamp(batch_date,'yyyy-MM-dd'),'yyyy/MM/dd')  batchDate,");
        	sb.append(" from_unixtime(unix_timestamp(production_date,'yyyy-MM-dd'),'yyyy/MM/dd')  productionDate, from_unixtime(unix_timestamp(expiration_date,'yyyy-MM-dd'),'yyyy/MM/dd')  expirationDate,");
        }
        
        sb.append(" department_id departmentId,supplier_name rmcName,written_name fullName , ");
        sb.append(" from_unixtime(unix_timestamp(dinner_date,'yyyy-MM-dd'),'MM月dd日') dinnerDate, ");
        sb.append(" from_unixtime(unix_timestamp(delivery_date,'yyyy-MM-dd'),'MM月dd日') deliveryDate,warn_level_name warnLevelName ");
        sb.append(" from app_t_edu_warn_detail " );
        sb.append(" where  1=1 ");
        
        getWarnLicDetsListCondition(warnType,certificateType,listYearMonth, startDate, endDateAddOne,
        		distId,schName,schType,licType,licStatus,licAuditStatus, licAuditStatussList,
    			startElimDate,endElimDate,startValidDate,endValidDate, schProp,
    			licNo,rmcName,fullName,target,departmentId,departmentIdList,trigWarnUnit,area,warnTypeChild,warnLevelName, sb);
        if(startNum!=null &&startNum!=-1 && endNum!=null && endNum!=-1 ) {
        	sb.append(" limit "+endNum);
        }
        logger.info("执行sql:"+sb.toString());
		return (List<WarnCommonLicDets>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<WarnCommonLicDets>() {
			@Override
			public WarnCommonLicDets mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
				
				WarnCommonLicDets cdsd = new WarnCommonLicDets();
	            cdsd.setWarnDate(rs.getString("warnDate"));
	            cdsd.setDistName("-");
	            if(StringUtils.isNotEmpty(rs.getString("area"))) {
	            	cdsd.setDistName(rs.getString("area"));
	            }
	            
	            cdsd.setAddress("-");
	            if(StringUtils.isNotEmpty(rs.getString("address"))) {
	            	cdsd.setAddress(rs.getString("address"));
	            }
	            
	            cdsd.setFoodSafetyPersion("-");
	            if(StringUtils.isNotEmpty(rs.getString("foodSafetyPersion"))) {
	            	cdsd.setFoodSafetyPersion(rs.getString("foodSafetyPersion"));
	            }
	            
	            cdsd.setFoodSafetyMobilephone("-");
	            if(StringUtils.isNotEmpty(rs.getString("foodSafetyMobilephone"))) {
	            	cdsd.setFoodSafetyMobilephone(rs.getString("foodSafetyMobilephone"));
	            }
	            
	            if(warnType==3) {
	            	//过保预警
	            	cdsd.setBatchNo("");
		            if(rs.getString("batchNo")!=null && !"".equals(rs.getString("batchNo"))){
		            	cdsd.setBatchNo(rs.getString("batchNo"));
		            }
		            
		            cdsd.setCarCode("");
		            if(rs.getString("carCode")!=null && !"".equals(rs.getString("carCode"))){
		            	cdsd.setCarCode(rs.getString("carCode"));
		            }
		            
		            cdsd.setDriverName("");
		            if(rs.getString("driverName")!=null && !"".equals(rs.getString("driverName"))){
		            	cdsd.setDriverName(rs.getString("driverName"));
		            }
		            
		            cdsd.setBatchDate("");
		            if(rs.getString("batchDate")!=null && !"".equals(rs.getString("batchDate"))){
		            	cdsd.setBatchDate(rs.getString("batchDate"));
		            }
		            
		            cdsd.setProductionDate("");
		            if(rs.getString("productionDate")!=null && !"".equals(rs.getString("productionDate"))){
		            	cdsd.setProductionDate(rs.getString("productionDate"));
		            }
		            
		            cdsd.setExpirationDate("");
		            if(rs.getString("expirationDate")!=null && !"".equals(rs.getString("expirationDate"))){
		            	cdsd.setExpirationDate(rs.getString("expirationDate"));
		            }
		            cdsd.setMaterialName(rs.getString("materialName"));
	            }
	            
	            cdsd.setSchType("");
	            if(rs.getString("schType")!=null && !"".equals(rs.getString("schType"))){
	            	cdsd.setSchType(AppModConfig.schTypeIdToNameMap.get(Integer.parseInt(rs.getString("schType"))));
	            }
	            
	            cdsd.setSchProp("");
	            if(StringUtils.isNotEmpty(rs.getString("schProp")) && schoolPropertyMap!=null){
	            	cdsd.setSchProp(schoolPropertyMap.get(Integer.parseInt(rs.getString("schProp"))));
	            }
	            cdsd.setSchTypeId(1000);
	            if(rs.getString("schType")!=null && !"".equals(rs.getString("schType")) && !"NULL".equals(rs.getString("schType"))){
	            	cdsd.setSchTypeId(Integer.parseInt(rs.getString("schType")));
	            }
	            
	            cdsd.setSchName("-");
	            if(StringUtils.isNotEmpty(rs.getString("schName"))) {
	            	cdsd.setSchName(rs.getString("schName"));
	            }
	            
	            cdsd.setLicName("-");
	            if(StringUtils.isNotEmpty(rs.getString("warnTypeChild"))) {
	            	int curLicType = -1;
					if(!rs.getString("warnTypeChild").equalsIgnoreCase("null")) {
						if(rs.getString("warnTypeChild").equalsIgnoreCase("0"))        //餐饮服务许可证
							curLicType = 3;
						else if(rs.getString("warnTypeChild").equalsIgnoreCase("1"))   //食品经营许可证
							curLicType = 0;
						else if(rs.getString("warnTypeChild").equalsIgnoreCase("20"))  //健康证
							curLicType = 2;
						else if(rs.getString("warnTypeChild").equalsIgnoreCase("22"))  //A1
							curLicType = 4;
						else if(rs.getString("warnTypeChild").equalsIgnoreCase("23"))  //B
							curLicType = 6;
						else if(rs.getString("warnTypeChild").equalsIgnoreCase("24"))  //C
							curLicType = 7;
						else if(rs.getString("warnTypeChild").equalsIgnoreCase("25"))  //A2
							curLicType = 5;
						if(curLicType != -1)
							cdsd.setLicName(AppModConfig.licTypeIdToNameMap.get(curLicType));
					}
	            }
	            cdsd.setLicNo("-");
	            if(StringUtils.isNotEmpty(rs.getString("licNo"))) {
	            	cdsd.setLicNo(rs.getString("licNo"));
	            }
	        	//有效日期，格式：xxxx-xx-xx
	            cdsd.setValidDate("-");
	            if(StringUtils.isNotEmpty(rs.getString("validDate"))) {
	            	cdsd.setValidDate(rs.getString("validDate"));
	            }
	        	//状态
	            cdsd.setLicStatus("-");
	            if(StringUtils.isNotEmpty(rs.getString("validDate"))) {
					String curDate = BCDTimeUtil.convertNormalDate(null);						
					DateTime startDt = BCDTimeUtil.convertDateStrToDate(cdsd.getValidDate().replaceAll("/", "-"));
					DateTime endDt = BCDTimeUtil.convertDateStrToDate(curDate);
					if(curDate.compareTo(cdsd.getValidDate().replaceAll("/", "-")) > 0) {
						cdsd.setLicStatus("逾期");
					}
					else {
						int days = Math.abs(Days.daysBetween(startDt, endDt).getDays())+1;
						cdsd.setLicStatus("剩余 " + days + " 天");
					}
	            }
	        	//审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
	            cdsd.setLicAuditStatus(0);
	            if(StringUtils.isNotEmpty(rs.getString("licAuditStatus"))) {
		            if(!rs.getString("licAuditStatus").equalsIgnoreCase("null")) {
						if(rs.getString("licAuditStatus").equalsIgnoreCase("1"))
							cdsd.setLicAuditStatus(0);
						else if(rs.getString("licAuditStatus").equalsIgnoreCase("2"))
							cdsd.setLicAuditStatus(1);
						else if(rs.getString("licAuditStatus").equalsIgnoreCase("3"))
							cdsd.setLicAuditStatus(3);
						else if(rs.getString("licAuditStatus").equalsIgnoreCase("4"))
							cdsd.setLicAuditStatus(2);
					}
	            }
	        	//消除日期，格式：xxxx/xx/xx，只有状态未已消除时，取消除时间（数据库中目前无论是否消除，都会有消除时间）
	            cdsd.setElimDate("-");
	            if(StringUtils.isNotEmpty(rs.getString("elimDate")) && rs.getString("licAuditStatus").equalsIgnoreCase("4")) {
	            	cdsd.setElimDate(rs.getString("elimDate"));
	            }
	            
	            //团餐公司名称
	            cdsd.setRmcName(rs.getString("rmcName"));
	            
	            //人员名称
	            cdsd.setFullName("-");
	            if(StringUtils.isNotEmpty(rs.getString("fullName"))) {
	            	cdsd.setFullName(rs.getString("fullName"));
	            }
	            //关联学校
	            cdsd.setRelSchName("-");
	            if(StringUtils.isNotEmpty(rs.getString("schName"))) {
	            	cdsd.setRelSchName(rs.getString("schName"));
	            }
	            
	            //预警单位
	            cdsd.setTrigWarnUnit("-");
	            if(StringUtils.isNotEmpty(rs.getString("rmcName")) && !"null".equals(rs.getString("rmcName"))) {
	            	cdsd.setTrigWarnUnit(rs.getString("rmcName"));
	            }
	            
	            //排菜日期
	            cdsd.setDinnerDate("-");
	            if(StringUtils.isNotEmpty(rs.getString("dinnerDate")) && !"null".equals(rs.getString("dinnerDate"))) {
	            	cdsd.setDinnerDate(rs.getString("dinnerDate"));
	            }

				//验收日期
				cdsd.setDeliveryDate("-");
				if(StringUtils.isNotEmpty(rs.getString("deliveryDate")) && !"null".equals(rs.getString("deliveryDate"))) {
					cdsd.setDeliveryDate(rs.getString("deliveryDate"));
				}
				
				//验收日期
				cdsd.setWarnLevelName("-");
				if(StringUtils.isNotEmpty(rs.getString("warnLevelName")) && !"null".equals(rs.getString("warnLevelName"))) {
					cdsd.setWarnLevelName(rs.getString("warnLevelName"));
				}
				
	            cdsd.setDepartmentId(rs.getString("departmentId"));
				return cdsd;
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_dish_menu中根据条件查询数据条数
     */
    public Integer getWarnLicDetsCount(Integer warnType,Integer certificateType,List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId, String schName, int schType, int licType, int licStatus, int licAuditStatus, List<Object> licAuditStatussList,
			String startElimDate, String endElimDate, String startValidDate, String endValidDate, 
			int schProp, String licNo,String rmcName,String fullName,Integer target,String departmentId,List<Object> departmentIdList,
			String trigWarnUnit,String area,String warnTypeChild,String warnLevelName
			) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        //sb.append("select count(*) dataCount from ( ");
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_warn_detail" );
        sb.append(" where 1=1  ");
        
        getWarnLicDetsListCondition(warnType,certificateType,listYearMonth, startDate, endDateAddOne,
        		distId,schName,schType,licType,licStatus,licAuditStatus,licAuditStatussList, 
    			startElimDate,endElimDate,startValidDate,endValidDate, schProp,
    			licNo,rmcName,fullName,target,departmentId,departmentIdList,trigWarnUnit,area,warnTypeChild,warnLevelName,sb);
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
	private void getWarnLicDetsListCondition(Integer warnType,Integer certificateType,List<String> listYearMonth, String startDate, String endDateAddOne,
    		String distId, String schName, int schType, int licType, int licStatus, int licAuditStatus, List<Object> licAuditStatussList,
			String startElimDate, String endElimDate, String startValidDate, String endValidDate, 
			int schProp, String licNo,String rmcName, String fullName,Integer target,
			String departmentId,List<Object> departmentIdList,String trigWarnUnit,String area,String warnTypeChild,String warnLevelName,
			StringBuffer sb) {
		String [] arrYearMonth ;
		
		if(listYearMonth.size() > 0) {
			sb.append("AND (");
			for(String strYearMonth : listYearMonth) {
				arrYearMonth = strYearMonth.split("_");
				sb.append(" (year =  "+arrYearMonth[0]+"");
		        sb.append(" and month >= " +arrYearMonth[1]);
		        sb.append(" and month <= " +arrYearMonth[2]+") ");
		        
		        if(!strYearMonth.equals(listYearMonth.get(listYearMonth.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
		sb.append(" and warn_type = " + warnType);
        sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
        if(CommonUtil.isNotEmpty(warnLevelName)) {
        	sb.append(" and warn_level_name = \"" + warnLevelName+"\"");
        }
        
        if(target!=null) {
        	sb.append(" and target = \"" + target+"\"");
        }
        
        if(CommonUtil.isNotEmpty(trigWarnUnit)) {
        	sb.append(" and supplier_name like \"%" + trigWarnUnit+"%\"");
        }
        
        if(CommonUtil.isNotEmpty(departmentId)) {
        	sb.append(" and department_id = \"" + departmentId+"\"");
        }
        
        if(CommonUtil.isNotEmpty(area)) {
        	sb.append(" and area = \"" + area+"\"");
        }
        if(departmentIdList !=null && departmentIdList.size()>0) {
        	String departmentIds= departmentIdList.toString().substring(1,departmentIdList.toString().length()-1);
        	if(departmentIds.indexOf("\"") <0) {
        		departmentIds = "\""+departmentIds.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and department_id in (" +departmentIds +")");
        }
        
        //sb.append(" and  school_name is not null and school_name !=\"\" and school_name !=\"null\" ");
        //证书类型：1：学校证书 2：团餐公司证书 3：人员证书
        if(certificateType!=null) {
        	//0, "餐饮服务许可证",1, "食品经营许可证" 4, "营业执照",20, "健康证", 22, "A1", 23, "B",24, "C", 25, "A2"
        	if(certificateType == 1) {
        		sb.append(" and (warn_type_child =0 or warn_type_child =1 or warn_type_child =4) ");
        		sb.append(" and company_type = 1 ");
        	}else if(certificateType == 2) {
        		sb.append(" and (warn_type_child =0 or warn_type_child =1 or warn_type_child =4) ");
        		sb.append(" and (company_type is null or company_type != 1 ) ");
        	}else if(certificateType == 3) {
        		sb.append(" and warn_type_child !=0 and warn_type_child !=1 and warn_type_child !=4 ");
        	}else if(certificateType == 6) {
        		sb.append(" and warn_type_child !=0 and warn_type_child !=1 and warn_type_child !=4 ");
        	}
        }
        
		//String distId, 
        //区属，所属主管部门
        if(StringUtils.isNotEmpty(distId)) {
        	sb.append(" and district_id = \"" + distId+"\"");
        }
		//String schName, 
        //学校
        if(StringUtils.isNotEmpty(schName)) {
        	sb.append(" and school_id = \"" + schName+"\"");
        }
		//int schType, 
        //学校学段
        if(schType != -1) {
        	sb.append(" and level_name = " + schType);
        }
		//int licType, 
       //判断证件名称（类型）（判断索引3）
	 	if(licType != -1) {
	 		//系统证照类型和hive库中的类型转换，方便查询
	 		//系统：证件类型，0:食品经营许可证，1:营业执照，2:健康证，3:餐饮服务许可证，4:A1证，5:A2证，6:B证，7:C证
	 		//hive 0, "餐饮服务许可证",1, "食品经营许可证" 4, "营业执照",20, "健康证", 22, "A1", 23, "B",24, "C", 25, "A2"
	    	int curLicType = -1;
			if(licType==3)        //餐饮服务许可证
				curLicType = 0;
			else if(licType==0)   //食品经营许可证
				curLicType = 1;
			else if(licType==1)   //营业执照
				curLicType = 4;
			else if(licType==2)  //健康证
				curLicType = 20;
			else if(licType==4)  //A1
				curLicType = 22;
			else if(licType==6)  //B
				curLicType = 23;
			else if(licType==7)  //C
				curLicType = 24;
			else if(licType==5)  //A2
				curLicType = 25;
			if(curLicType != -1) {
				 sb.append(" and warn_type_child = " + curLicType);
			}
			
		}
		//int licStatus, 
	 	//判断证件状况，0:逾期，1:到期
		if(licStatus != -1) {
			String curDate = BCDTimeUtil.convertNormalDate(null);						
			if(licStatus == 0) {
				//逾期
				sb.append(" and  \""+curDate +" 00:00:00\" >= lose_time");
			}else if(licStatus == 1){
				sb.append(" and lose_time > \""+curDate+" 00:00:00\"");
			}
		}
		if(warnType == 1) {
			//int licAuditStatus, 
	        if(licAuditStatus != -1) {
	        	//转换hive库中的状态，和系统中的状态
	        	//hive库中的状态1待处理,2处理中,3驳回,4已处理'
	        	//系统中的状态：//审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
	        	int licAuditStatusTemp = -1;
				if(licAuditStatus == 0) {
					licAuditStatusTemp =1;
				}else if(licAuditStatus == 1) {
					licAuditStatusTemp =2;
				}else if(licAuditStatus == 3) {
					licAuditStatusTemp =3;
				}else if(licAuditStatus == 2) {
					licAuditStatusTemp =4;
				}
				
				 if(licAuditStatusTemp != -1) {
			        sb.append(" and warn_stat = " + licAuditStatusTemp);
			     }
	        }
		}else if(warnType == 3) {
			if(licAuditStatus != -1) {
				if(licAuditStatus == 0) {
					sb.append(" and warn_stat != 4 ");
				}else if(licAuditStatus == 1){
					sb.append(" and warn_stat = 4 ");
				}
			}
		}
        
        //过滤状态，多选
        if(licAuditStatussList!=null && licAuditStatussList.size() > 0) {
			sb.append(" AND (");
			for(Object licAuditStatusObj : licAuditStatussList) {
				if(licAuditStatusObj == null || !CommonUtil.isInteger(licAuditStatusObj.toString())) {
					continue;
				}
				
				int licAuditStatusTemp = -1;
				if(Integer.parseInt(licAuditStatusObj.toString()) == 0) {
					licAuditStatusTemp =1;
				}else if(Integer.parseInt(licAuditStatusObj.toString()) == 1) {
					licAuditStatusTemp =2;
				}else if(Integer.parseInt(licAuditStatusObj.toString()) == 3) {
					licAuditStatusTemp =3;
				}else if(Integer.parseInt(licAuditStatusObj.toString()) == 2) {
					licAuditStatusTemp =4;
				}
				
		        sb.append(" warn_stat = \"" +licAuditStatusTemp+"\"");
		        
		        if(!licAuditStatusObj.equals(licAuditStatussList.get(licAuditStatussList.size() - 1))) {
		        	sb.append(" or ");
		        }
			}
			
			sb.append(") ");
		}
        
        
		//消除日期 String startElimDate,String endElimDate, 
        if(startElimDate != null && endElimDate != null) {
			 sb.append(" and deal_date >= \""+startElimDate+"\"");
		     sb.append(" and \""+endElimDate +"\" >= deal_date");
		}
        
        //判断有效日期（判断索引6）
		//String startValidDate, String endValidDate, 
		if(startValidDate != null && endValidDate != null) {
			 sb.append(" and lose_time >= \""+startValidDate+" 00:00:00\"");
		     sb.append(" and \""+endValidDate +" 00:00:00\" >= lose_time");
		}
		
		//int schProp, 
		//判断学校性质
        if(schProp != -1) {
        	sb.append(" and school_nature_name = \"" + schProp+"\"");
        }
		//String licNo,
        //判断证件号码（判断索引8）
		if(StringUtils.isNotEmpty(licNo)) {
			sb.append(" and lic_no like  \"" +"%"+licNo+"%"+"\"");
		}
		
        //团餐公司
        if(StringUtils.isNotEmpty(rmcName)) {
        	sb.append(" and supplier_id = \"" + rmcName+"\"");
        }
        //姓名
        if(StringUtils.isNotEmpty(fullName)) {
			sb.append(" and written_name like  \"" +"%"+fullName+"%"+"\"");
		}
        
        //预警二级类型
        if(StringUtils.isNotEmpty(warnTypeChild)) {
        	sb.append(" and warn_type_child = \"" + warnTypeChild+"\"");
        }
	}
	
 	//--------------------预警-汇总（新表）----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_platoon_total中根据条件查询数据列表
  	 * statMode:统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
  	 */
    public List<WarnCommon> getWarnList(Integer warnType,Integer licType,Integer target, List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,List<Object> distIdList,
    		int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
    		String departmentId,List<Object> departmentIdList,
    		Integer statMode ) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append("select from_unixtime(unix_timestamp(warn_date,'yyyy-MM-dd'),'yyyy/MM/dd') warnDate,area,");
        sb.append("untreated_sum noProcWarnNum,rejected_sum rejectWarnNum,");
        sb.append("review_sum auditWarnNum, deal_sum elimWarnNum, ");
        sb.append(" level_name levelName,school_nature_name schoolNatureName, ");
        sb.append(" department_master_id departmentMasterId,department_slave_id_name departmentSlaveIdName, ");
        sb.append(" department_id departmentId ");
        sb.append(" from app_t_edu_warn_total_d " );
        sb.append(" where  1=1 ");
        if(statMode == 0) {
        	//按区统计时，其他三个属性需要为空，数据是重叠的
	        sb.append(" and (level_name is null or level_name = \"null\" or level_name = \"NULL\" )  ");
	        sb.append(" and (school_nature_name is null or school_nature_name = \"null\" or school_nature_name = \"NULL\" ) ");
	        sb.append(" and (department_master_id is null or department_master_id = \"null\" or department_master_id = \"NULL\" ) ");
	        sb.append(" and (department_slave_id_name is null or department_slave_id_name = \"null\" or department_slave_id_name = \"NULL\" ) ");
	        
	        sb.append(" and (area is not null and area != \"null\" and area != \"NULL\" )  ");
	        
	        //管理部门
            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
            	
            }else {
            	//sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
            }
        }else if (statMode == 1) {
        	sb.append(" and school_nature_name != \"NULL\"  ");
            
        	//管理部门
            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
            	
            }else {
            	//sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
            }
            
            sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
            
        }else if (statMode == 2) {
        	sb.append(" and level_name != \"NULL\"  ");
        	
        	//管理部门
            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
            	
            }else {
            	//sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
            }
           sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
           
        }else if (statMode == 3) {
        	sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
        	sb.append(" and department_master_id != \"NULL\"");
        	//sb.append(" and department_slave_id_name != \"NULL\"  ");
        	
        	sb.append(" and department_master_id != \"-1\"");
        	//sb.append(" and department_slave_id_name != \"-1\"  ");
        	
        	//管理部门
            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
            	
            }else {
            	//sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
            }
        	
        }else if(statMode == 4) {
        	//按管理部门统计时，其他四个属性需要为空，数据是重叠的
        	sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
	        sb.append(" and (level_name is null or level_name = \"null\" or level_name = \"NULL\" )  ");
	        sb.append(" and (school_nature_name is null or school_nature_name = \"null\" or school_nature_name = \"NULL\" ) ");
	        sb.append(" and (department_master_id is null or department_master_id = \"null\" or department_master_id = \"NULL\" ) ");
	        sb.append(" and (department_slave_id_name is null or department_slave_id_name = \"null\" or department_slave_id_name = \"NULL\" ) ");
        	
        }
        
        getDishCondition(warnType,licType,target,listYearMonth, startDate, endDateAddOne,
        		distId,distIdList,
        		subLevel,compDep,subLevels,compDeps,
        		departmentId,departmentIdList,
        		sb);
        logger.info("执行sql:"+sb.toString());
		return (List<WarnCommon>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<WarnCommon>() {
			@Override
			public WarnCommon mapRow(ResultSet rs, int rowNum) throws SQLException {
				WarnCommon cdsd = new WarnCommon();
				
				cdsd.setWarnPeriod(rs.getString("warnDate"));
				//区域名称
				cdsd.setDistName("-");
	            if(StringUtils.isNotEmpty(rs.getString("area"))) {
	            	cdsd.setDistName(rs.getString("area"));
	            }
				//预警总数
				//int totalWarnNum;
				
				//未处理预警数
				cdsd.setNoProcWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("noProcWarnNum")) && rs.getInt("noProcWarnNum")>0) {
	            	cdsd.setNoProcWarnNum(rs.getInt("noProcWarnNum"));
	            }
				//已驳回预警数
				cdsd.setRejectWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("rejectWarnNum")) && rs.getInt("rejectWarnNum")>0) {
	            	cdsd.setRejectWarnNum(rs.getInt("rejectWarnNum"));
	            }
				//审核中预警数
				cdsd.setAuditWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("auditWarnNum")) && rs.getInt("auditWarnNum")>0) {
	            	cdsd.setAuditWarnNum(rs.getInt("auditWarnNum"));
	            }
				//已消除预警数
				cdsd.setElimWarnNum(0);
				if(StringUtils.isNotEmpty(rs.getString("elimWarnNum")) && rs.getInt("elimWarnNum")>0) {
	            	cdsd.setElimWarnNum(rs.getInt("elimWarnNum"));
	            }
				
	            cdsd.setLevel(rs.getString("levelName"));
	            cdsd.setNature(rs.getString("schoolNatureName"));
	            cdsd.setDepartmentMasterId(rs.getString("departmentMasterId"));
	            cdsd.setDepartmentSlaveIdName(rs.getString("departmentSlaveIdName"));
	            
				
				cdsd.setDepartmentId(rs.getString("departmentId"));
				return cdsd;
			}
		});
    }

    /**
     * 预警汇总查询条件（查询列表和查询总数共用）
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
	private void getDishCondition(Integer warnType,Integer licType,Integer target,List<String> listYearMonth, String startDate, String endDateAddOne,
			String distId,List<Object> distIdList,
			int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
			String departmentId,List<Object> departmentIdList,
			StringBuffer sb) {
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
		
        sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
        if(target!=null) {
        	sb.append(" and target = \"" + target+"\"");
        }
        
        if(warnType!=null) {
        	sb.append(" and warn_type = \"" + warnType+"\"");
        }
        
        //sb.append(" and  school_name is not null and school_name !=\"\" and school_name !=\"null\" ");
        //证书类型：1：学校证书 2：团餐公司证书 3：人员证书
        if(licType!=null) {
        	//1：学校证件 2:团餐公司证件 3：人员证件  4：全部
        	sb.append(" and lic_type =  " + licType);
        }
        
        //1为教委端 2为团餐端
	    //学校区
        if(StringUtils.isNotEmpty(distId)) {
        	sb.append(" and area = \"" + distId+"\"");
        }

        if(distIdList !=null && distIdList.size()>0) {
        	String distIds= distIdList.toString().substring(1,distIdList.toString().length()-1);
        	if(distIds.indexOf("\"") <0) {
        		distIds = "\""+distIds.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and area in (" +distIds +")");
        }
        
	    //学校管理部门
        if(StringUtils.isNotEmpty(departmentId)) {
        	sb.append(" and department_id = \"" + departmentId+"\"");
        }
        
        if(departmentIdList !=null && departmentIdList.size()>0) {
        	String departmentIds= departmentIdList.toString().substring(1,departmentIdList.toString().length()-1);
        	if(departmentIds.indexOf("\"") <0) {
        		departmentIds = "\""+departmentIds.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and department_id in (" +departmentIds +")");
        }
        
        //int subLevel, 
        //所属
        if(subLevel != -1) {
        	sb.append(" and department_master_id = " + subLevel);
        }
        
		//int schType, 
        //主管部门
        if(compDep != -1) {
        	sb.append(" and department_slave_id_name = " + compDep);
        }
        
        
        //List<String> subLevels,
        //过滤状态，多选
        if(subLevels !=null && subLevels.size()>0) {
        	String supplierAreas= subLevels.toString().substring(1,subLevels.toString().length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and department_master_id in (" +supplierAreas +")");
        }
        
        //List<String> compDeps,
        //过滤状态，多选
        if(compDeps !=null && compDeps.size()>0) {
        	Map<String,String> compMap = new HashMap<String,String>();
        	//拆分compDeps格式 "["1_2","3_7"]"
        	for(Object objCompDep :compDeps) {
        		if(objCompDep !=null) {
        			String[] compArr = objCompDep.toString().split("_");
        			compMap.put(compArr[0], compMap.get(compArr[0])==null?compArr[1]:compMap.get(compArr[0])+","+compArr[1]);
        		}
        	}
        	
        	if(compMap.size() > 0) {
    			sb.append("AND (");
	        	for(Map.Entry<String,String> entry : compMap.entrySet()) {
	        		sb.append(" ( department_master_id = \"" +entry.getKey() +"\"");
	        		String supplierAreas= entry.getValue().toString();
	            	if(supplierAreas.indexOf("\"") <0) {
	            		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
	            	}
	            	sb.append(" and department_slave_id_name in (" +supplierAreas +"))");
    		        sb.append(" or ");
	        	}
	        	//去除最后一个or
	        	sb.replace(sb.length()-3, sb.length(), "");
	        	sb.append(") ");
        	}
        }
	}

	//--------------------排菜、验收未上报已经汇总----------------------------------------------
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_ledger_collect_d中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduNoLedgerCollectD> getAppTEduNoLedgerCollectDList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoLedgerCollectD inputObj,
    		Integer startNum,Integer endNum) {
        logger.info("[Enter dao method] {}-{}", "AppTEduNoLedgerCollectDDao", "getAppTEduNoLedgerCollectDList");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append("warn_date warnDate, ");
        sb.append("  department_id departmentId, school_id schoolId, school_name schoolName, warn_prompt warnPrompt, ");
        sb.append("  warn_remind warnRemind, warn_early warnEarly, warn_supervise warnSupervise, warn_accountability warnAccountability");
        sb.append("   ");
        sb.append(" from app_t_edu_no_ledger_collect_d " );
        sb.append(" where  1=1 ");
        getAppTEduNoLedgerCollectDListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+endNum);
        }        logger.info("执行sql:"+sb.toString());
		return (List<AppTEduNoLedgerCollectD>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<AppTEduNoLedgerCollectD>() {
			@Override
			public AppTEduNoLedgerCollectD mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				if(startNum!=null && endNum!=null && startNum!=-1 &&  endNum != -1 
						&& (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
								AppTEduNoLedgerCollectD obj = new AppTEduNoLedgerCollectD();

				obj.setWarnDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("warnDate"))) {
					obj.setWarnDate(rs.getString("warnDate"));
				}
				obj.setDepartmentId("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentId"))) {
					obj.setDepartmentId(rs.getString("departmentId"));
				}
				obj.setSchoolId("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolId"))) {
					obj.setSchoolId(rs.getString("schoolId"));
				}
				obj.setSchoolName("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolName"))) {
					obj.setSchoolName(rs.getString("schoolName"));
				}
				obj.setWarnPrompt(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnPrompt"))) {
					obj.setWarnPrompt(rs.getInt("warnPrompt"));
				}
				obj.setWarnRemind(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnRemind"))) {
					obj.setWarnRemind(rs.getInt("warnRemind"));
				}
				obj.setWarnEarly(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnEarly"))) {
					obj.setWarnEarly(rs.getInt("warnEarly"));
				}
				obj.setWarnSupervise(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnSupervise"))) {
					obj.setWarnSupervise(rs.getInt("warnSupervise"));
				}
				obj.setWarnAccountability(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnAccountability"))) {
					obj.setWarnAccountability(rs.getInt("warnAccountability"));
				}
				logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
				return obj;
			}
		});
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_ledger_collect_d中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduNoLedgerCollectDListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoLedgerCollectD inputObj) {
        logger.info("[Enter dao method] {}-{}", "AppTEduNoLedgerCollectDDao", "getAppTEduNoLedgerCollectDListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_no_ledger_collect_d" );
        sb.append(" where 1=1  ");
        
        getAppTEduNoLedgerCollectDListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
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
    * 从数据库app_saas_v1的数据表app_t_edu_no_ledger_collect_d中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public AppTEduNoLedgerCollectD getAppTEduNoLedgerCollectDListSum(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoLedgerCollectD inputObj) {
        logger.info("[Enter dao method] {}-{}", "AppTEduNoLedgerCollectDDao", "getAppTEduNoLedgerCollectDListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
		final AppTEduNoLedgerCollectD obj = new AppTEduNoLedgerCollectD();
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select sum(warn_prompt) warnPrompt,sum(warn_remind) warnRemind,sum(warn_early) warnEarly,"
        		+ "sum(warn_supervise) warnSupervise,sum(warn_accountability) warnAccountability ");
        sb.append(" from app_t_edu_no_ledger_collect_d" );
        sb.append(" where 1=1  ");
        
        getAppTEduNoLedgerCollectDListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
				obj.setWarnPrompt(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnPrompt"))) {
					obj.setWarnPrompt(rs.getInt("warnPrompt"));
				}
				obj.setWarnRemind(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnRemind"))) {
					obj.setWarnRemind(rs.getInt("warnRemind"));
				}
				obj.setWarnEarly(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnEarly"))) {
					obj.setWarnEarly(rs.getInt("warnEarly"));
				}
				obj.setWarnSupervise(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnSupervise"))) {
					obj.setWarnSupervise(rs.getInt("warnSupervise"));
				}
				obj.setWarnAccountability(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnAccountability"))) {
					obj.setWarnAccountability(rs.getInt("warnAccountability"));
				}
        	}   
        });
        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
        return obj;
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_ledger_collect_d中根据条件查询数据列表条件
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @param sb
    */
    public void getAppTEduNoLedgerCollectDListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoLedgerCollectD inputObj,
    StringBuffer sb) {
        logger.info("[Enter dao method] {}-{}", "AppTEduNoLedgerCollectDDao", "getAppTEduNoLedgerCollectDListCondition");
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
		
        sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
        //DepartmentMode
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentMode())) {
        	if("0".equals(inputObj.getDepartmentMode())) {
        		sb.append(" and department_id != \"20\"");
        	}else if("-1".equals(inputObj.getDepartmentMode())) {
        		
        	}else {
        		sb.append(" and department_id = \"" + inputObj.getDepartmentMode()+"\"");
        	}
        }else if("-1".equals(inputObj.getDepartmentMode())) {
        }else {
        	sb.append(" and department_id != \"20\"");
        }
        
        //warnDate
        if(CommonUtil.isNotEmpty(inputObj.getWarnDate())) {
        	sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') = \"" + inputObj.getWarnDate()+"\"");
        }

        //departmentId
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentId())) {
        	sb.append(" and department_id = \"" + inputObj.getDepartmentId()+"\"");
        }

        //schoolId
        if(CommonUtil.isNotEmpty(inputObj.getSchoolId())) {
        	sb.append(" and school_id = \"" + inputObj.getSchoolId()+"\"");
        }

        //schoolName
        if(CommonUtil.isNotEmpty(inputObj.getSchoolName())) {
        	sb.append(" and school_name = \"" + inputObj.getSchoolName()+"\"");
        }

        //warnPrompt
        if(inputObj.getWarnPrompt() !=null && inputObj.getWarnPrompt() != -1) {
        	sb.append(" and warn_prompt = \"" + inputObj.getWarnPrompt()+"\"");
        }

        //warnRemind
        if(inputObj.getWarnRemind() !=null && inputObj.getWarnRemind() != -1) {
        	sb.append(" and warn_remind = \"" + inputObj.getWarnRemind()+"\"");
        }

        //warnEarly
        if(inputObj.getWarnEarly() !=null && inputObj.getWarnEarly() != -1) {
        	sb.append(" and warn_early = \"" + inputObj.getWarnEarly()+"\"");
        }

        //warnSupervise
        if(inputObj.getWarnSupervise() !=null && inputObj.getWarnSupervise() != -1) {
        	sb.append(" and warn_supervise = \"" + inputObj.getWarnSupervise()+"\"");
        }

        //warnAccountability
        if(inputObj.getWarnAccountability() !=null && inputObj.getWarnAccountability() != -1) {
        	sb.append(" and warn_accountability = \"" + inputObj.getWarnAccountability()+"\"");
        }

        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_platoon_collect_d中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduNoPlatoonCollectD> getAppTEduNoPlatoonCollectDList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoPlatoonCollectD inputObj,
    		Integer startNum,Integer endNum) {
        logger.info("[Enter dao method] {}-{}", "AppTEduNoPlatoonCollectDDao", "getAppTEduNoPlatoonCollectDList");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append("warn_date warnDate, ");
        sb.append("  department_id departmentId, school_id schoolId, school_name schoolName, warn_prompt warnPrompt, ");
        sb.append("  warn_remind warnRemind, warn_early warnEarly, warn_supervise warnSupervise, warn_accountability warnAccountability");
        sb.append("   ");
        sb.append(" from app_t_edu_no_platoon_collect_d " );
        sb.append(" where  1=1 ");
        getAppTEduNoPlatoonCollectDListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+endNum);
        }        logger.info("执行sql:"+sb.toString());
		return (List<AppTEduNoPlatoonCollectD>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<AppTEduNoPlatoonCollectD>() {
			@Override
			public AppTEduNoPlatoonCollectD mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				if(startNum!=null && endNum!=null && startNum!=-1 &&  endNum != -1 
						&& (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
								AppTEduNoPlatoonCollectD obj = new AppTEduNoPlatoonCollectD();

				obj.setWarnDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("warnDate"))) {
					obj.setWarnDate(rs.getString("warnDate"));
				}
				obj.setDepartmentId("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentId"))) {
					obj.setDepartmentId(rs.getString("departmentId"));
				}
				obj.setSchoolId("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolId"))) {
					obj.setSchoolId(rs.getString("schoolId"));
				}
				obj.setSchoolName("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolName"))) {
					obj.setSchoolName(rs.getString("schoolName"));
				}
				obj.setWarnPrompt(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnPrompt"))) {
					obj.setWarnPrompt(rs.getInt("warnPrompt"));
				}
				obj.setWarnRemind(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnRemind"))) {
					obj.setWarnRemind(rs.getInt("warnRemind"));
				}
				obj.setWarnEarly(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnEarly"))) {
					obj.setWarnEarly(rs.getInt("warnEarly"));
				}
				obj.setWarnSupervise(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnSupervise"))) {
					obj.setWarnSupervise(rs.getInt("warnSupervise"));
				}
				obj.setWarnAccountability(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnAccountability"))) {
					obj.setWarnAccountability(rs.getInt("warnAccountability"));
				}
				logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
				return obj;
			}
		});
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_platoon_collect_d中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduNoPlatoonCollectDListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoPlatoonCollectD inputObj) {
        logger.info("[Enter dao method] {}-{}", "AppTEduNoPlatoonCollectDDao", "getAppTEduNoPlatoonCollectDListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_no_platoon_collect_d" );
        sb.append(" where 1=1  ");
        
        getAppTEduNoPlatoonCollectDListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
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
    * 从数据库app_saas_v1的数据表app_t_edu_no_platoon_collect_d中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public AppTEduNoPlatoonCollectD getAppTEduNoPlatoonCollectDListSum(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoPlatoonCollectD inputObj) {
        logger.info("[Enter dao method] {}-{}", "AppTEduNoPlatoonCollectDDao", "getAppTEduNoPlatoonCollectDListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final AppTEduNoPlatoonCollectD obj = new AppTEduNoPlatoonCollectD();
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select sum(warn_prompt) warnPrompt,sum(warn_remind) warnRemind,sum(warn_early) warnEarly,"
        		+ "sum(warn_supervise) warnSupervise,sum(warn_accountability) warnAccountability ");
        sb.append(" from app_t_edu_no_platoon_collect_d" );
        sb.append(" where 1=1  ");
        
        getAppTEduNoPlatoonCollectDListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        logger.info("执行sql:"+sb.toString());
        jdbcTemplateTemp.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		obj.setWarnPrompt(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnPrompt"))) {
					obj.setWarnPrompt(rs.getInt("warnPrompt"));
				}
				obj.setWarnRemind(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnRemind"))) {
					obj.setWarnRemind(rs.getInt("warnRemind"));
				}
				obj.setWarnEarly(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnEarly"))) {
					obj.setWarnEarly(rs.getInt("warnEarly"));
				}
				obj.setWarnSupervise(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnSupervise"))) {
					obj.setWarnSupervise(rs.getInt("warnSupervise"));
				}
				obj.setWarnAccountability(0);
				if(CommonUtil.isNotEmpty(rs.getString("warnAccountability"))) {
					obj.setWarnAccountability(rs.getInt("warnAccountability"));
				}
        	}   
        });
        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
        return obj;
    }
    

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_no_platoon_collect_d中根据条件查询数据列表条件
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @param sb
    */
    public void getAppTEduNoPlatoonCollectDListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduNoPlatoonCollectD inputObj,
    StringBuffer sb) {
        logger.info("[Enter dao method] {}-{}", "AppTEduNoPlatoonCollectDDao", "getAppTEduNoPlatoonCollectDListCondition");
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
		
        sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
        //DepartmentMode
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentMode())) {
        	if("0".equals(inputObj.getDepartmentMode())) {
        		sb.append(" and department_id != \"20\"");
        	}else if("-1".equals(inputObj.getDepartmentMode())) {
        		
        	}else {
        		sb.append(" and department_id = \"" + inputObj.getDepartmentMode()+"\"");
        	}
        }else if("-1".equals(inputObj.getDepartmentMode())) {
        }else {
        	sb.append(" and department_id != \"20\"");
        }

        
        //warnDate
        if(CommonUtil.isNotEmpty(inputObj.getWarnDate())) {
        	sb.append(" and DATE_FORMAT(warn_date,'yyyy-MM-dd') = \"" + inputObj.getWarnDate()+"\"");
        }

        //departmentId
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentId()) && !"-1".equals(inputObj.getDepartmentId())) {
        	sb.append(" and department_id = \"" + inputObj.getDepartmentId()+"\"");
        }

        //schoolId
        if(CommonUtil.isNotEmpty(inputObj.getSchoolId())) {
        	sb.append(" and school_id = \"" + inputObj.getSchoolId()+"\"");
        }

        //schoolName
        if(CommonUtil.isNotEmpty(inputObj.getSchoolName())) {
        	sb.append(" and school_name = \"" + inputObj.getSchoolName()+"\"");
        }

        //warnPrompt
        if(inputObj.getWarnPrompt() !=null && inputObj.getWarnPrompt() != -1) {
        	sb.append(" and warn_prompt = \"" + inputObj.getWarnPrompt()+"\"");
        }

        //warnRemind
        if(inputObj.getWarnRemind() !=null && inputObj.getWarnRemind() != -1) {
        	sb.append(" and warn_remind = \"" + inputObj.getWarnRemind()+"\"");
        }

        //warnEarly
        if(inputObj.getWarnEarly() !=null && inputObj.getWarnEarly() != -1) {
        	sb.append(" and warn_early = \"" + inputObj.getWarnEarly()+"\"");
        }

        //warnSupervise
        if(inputObj.getWarnSupervise() !=null && inputObj.getWarnSupervise() != -1) {
        	sb.append(" and warn_supervise = \"" + inputObj.getWarnSupervise()+"\"");
        }

        //warnAccountability
        if(inputObj.getWarnAccountability() !=null && inputObj.getWarnAccountability() != -1) {
        	sb.append(" and warn_accountability = \"" + inputObj.getWarnAccountability()+"\"");
        }

        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
    }

}
