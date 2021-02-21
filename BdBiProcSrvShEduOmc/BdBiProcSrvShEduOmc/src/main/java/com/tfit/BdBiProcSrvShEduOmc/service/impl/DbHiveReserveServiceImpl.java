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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;
import com.tfit.BdBiProcSrvShEduOmc.obj.opt.AppTEduReserveTotalD;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveReserveService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 排菜相关hive库的查询
 * @author Administrator
 *
 */
@Service
public class DbHiveReserveServiceImpl implements DbHiveReserveService {
	private static final Logger logger = LogManager.getLogger(DbHiveReserveServiceImpl.class.getName());
	
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
  	
  	//--------------------留样-汇总----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_d中根据条件查询数据列表
  	 * statMode:统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
  	 */
    public List<SchDishCommon> getDishList(String tableName,List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,List<Object> distIdList,
    		int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
    		String departmentId,List<Object> departmentIdList,
    		Integer statMode ) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
    	
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append("select from_unixtime(unix_timestamp(use_date,'yyyy-MM-dd'),'yyyy/MM/dd') useDate , ");
        sb.append(" area,total,have_platoon havePlatoon, ");
        sb.append(" level_name levelName,school_nature_name schoolNatureName, ");
        sb.append(" department_master_id departmentMasterId,department_slave_id_name departmentSlaveIdName,platoon_deal_status platoonDealStatus, ");
        sb.append(" department_id departmentId ");
        sb.append(" from "+tableName+" " );
        sb.append(" where  1=1 ");
        if(statMode == 0) {
        	//按区统计时，其他三个属性需要为空，数据是重叠的
	        sb.append(" and (level_name is null or level_name = \"null\" or level_name = \"NULL\" )  ");
	        sb.append(" and (school_nature_name is null or school_nature_name = \"null\" or school_nature_name = \"NULL\" ) ");
	        sb.append(" and (department_master_id is null or department_master_id = \"null\" or department_master_id = \"NULL\" ) ");
	        sb.append(" and (department_slave_id_name is null or department_slave_id_name = \"null\" or department_slave_id_name = \"NULL\" ) ");
	        
	        if(DataKeyConfig.talbePlatoonTotal.equals(tableName)) {
	        	
	        }else if (DataKeyConfig.talbePlatoonTotalD.equals(tableName)) {
		        //管理部门
	            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
	            	
	            }else {
	            	sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
	            }
	        }
        }else if (statMode == 1) {
        	sb.append(" and school_nature_name != \"NULL\"  ");
            
            if(DataKeyConfig.talbePlatoonTotal.equals(tableName)) {
            	//如果按区域过滤
                if(StringUtils.isNotEmpty(distId) || (distIdList !=null && distIdList.size() >0)) {
                	
                }else {
                	sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
                }
	        }else if (DataKeyConfig.talbePlatoonTotalD.equals(tableName)) {
	        	//管理部门
	            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
	            	
	            }else {
	            	sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
	            }
	            
	            sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
	        }
            
        }else if (statMode == 2) {
        	sb.append(" and level_name != \"NULL\"  ");
        	
        	 if(DataKeyConfig.talbePlatoonTotal.equals(tableName)) {
             	//如果按区域过滤
                 if(StringUtils.isNotEmpty(distId) || (distIdList !=null && distIdList.size() >0)) {
                 	
                 }else {
                 	sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
                 }
 	        }else if (DataKeyConfig.talbePlatoonTotalD.equals(tableName)) {
 	        	//管理部门
 	            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
 	            	
 	            }else {
 	            	sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
 	            }
 	           sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
 	        }
        }else if (statMode == 3) {
        	sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
        	sb.append(" and department_master_id != \"NULL\"");
        	//sb.append(" and department_slave_id_name != \"NULL\"  ");
        	
        	sb.append(" and department_master_id != \"-1\"");
        	//sb.append(" and department_slave_id_name != \"-1\"  ");
        	
        	if(DataKeyConfig.talbePlatoonTotal.equals(tableName)) {
        		
 	        }else if (DataKeyConfig.talbePlatoonTotalD.equals(tableName)) {
 	        	//管理部门
 	            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
 	            	
 	            }else {
 	            	sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
 	            }
 	        }
        	
        }else if(statMode == 4) {
        	if(DataKeyConfig.talbePlatoonTotal.equals(tableName)) {
        		
 	        }else if (DataKeyConfig.talbePlatoonTotalD.equals(tableName)) {
 	        	//按管理部门统计时，其他四个属性需要为空，数据是重叠的
 	        	sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
 		        sb.append(" and (level_name is null or level_name = \"null\" or level_name = \"NULL\" )  ");
 		        sb.append(" and (school_nature_name is null or school_nature_name = \"null\" or school_nature_name = \"NULL\" ) ");
 		        sb.append(" and (department_master_id is null or department_master_id = \"null\" or department_master_id = \"NULL\" ) ");
 		        sb.append(" and (department_slave_id_name is null or department_slave_id_name = \"null\" or department_slave_id_name = \"NULL\" ) ");
 	        }
        	
        }
        
        getDishCondition(listYearMonth, startDate, endDateAddOne,
        		distId,distIdList,
        		subLevel,compDep,subLevels,compDeps,
        		departmentId,departmentIdList,
        		sb);
        logger.info("执行sql:"+sb.toString());
		return (List<SchDishCommon>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<SchDishCommon>() {
			@Override
			public SchDishCommon mapRow(ResultSet rs, int rowNum) throws SQLException {
				SchDishCommon cdsd = new SchDishCommon();
				cdsd.setDishDate(rs.getString("useDate"));
				cdsd.setDistId(rs.getString("area"));
				//回收次数
				cdsd.setTotal(0);
				if(StringUtils.isNotEmpty(rs.getString("total")) && rs.getInt("total")>0) {
	            	cdsd.setTotal(rs.getInt("total"));
	            }
				cdsd.setHaveClass(null);
				if(CommonUtil.isNotEmpty(rs.getString("haveClass"))) {
					cdsd.setHaveClass(rs.getInt("haveClass"));
				}
				
				cdsd.setHavePlatoon(null);
				if(CommonUtil.isNotEmpty(rs.getString("havePlatoon"))) {
					cdsd.setHavePlatoon(rs.getInt("havePlatoon"));
				}
	            cdsd.setLevelName(rs.getString("levelName"));
	            cdsd.setSchoolNatureName(rs.getString("schoolNatureName"));
	            cdsd.setDepartmentMasterId(rs.getString("departmentMasterId"));
	            cdsd.setDepartmentSlaveIdName(rs.getString("departmentSlaveIdName"));
	            
	            cdsd.setPlatoonDealStatus(null);
				if(CommonUtil.isNotEmpty(rs.getString("platoonDealStatus"))) {
					cdsd.setPlatoonDealStatus(rs.getInt("platoonDealStatus"));
				}
				
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
	private void getDishCondition(List<String> listYearMonth, String startDate, String endDateAddOne,
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
		
        sb.append(" and DATE_FORMAT(use_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(use_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
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
	
  	//--------------------排菜-汇总----------------------------------------------
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_d中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduReserveTotalD> getAppTEduReserveTotalDList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduReserveTotalD inputObj,
    		Integer startNum,Integer endNum) {
        logger.info("[Enter dao method] {}-{}", "AppTEduReserveTotalDDao", "getAppTEduReserveTotalDList");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append("use_date useDate, ");
        sb.append("  area area, total total, school_total schoolTotal, have_reserve haveReserve, ");
        sb.append("  level_name levelName, school_nature_name schoolNatureName, department_master_id departmentMasterId, department_slave_id_name departmentSlaveIdName, ");
        sb.append("  department_id departmentId, reserve_deal_status reserveDealStatus ");
        sb.append(" from app_t_edu_reserve_total_d " );
        sb.append(" where  1=1 ");
        getAppTEduReserveTotalDListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+startNum+","+endNum);
        }        logger.info("执行sql:"+sb.toString());
		return (List<AppTEduReserveTotalD>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<AppTEduReserveTotalD>() {
			@Override
			public AppTEduReserveTotalD mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				if(startNum!=null && endNum!=null && startNum!=-1 &&  endNum != -1 
						&& (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
								AppTEduReserveTotalD obj = new AppTEduReserveTotalD();

				obj.setUseDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("useDate"))) {
					obj.setUseDate(rs.getString("useDate"));
				}
				obj.setArea("-");
				if(CommonUtil.isNotEmpty(rs.getString("area"))) {
					obj.setArea(rs.getString("area"));
				}
				obj.setTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("total"))) {
					obj.setTotal(rs.getInt("total"));
				}
				obj.setSchoolTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("schoolTotal"))) {
					obj.setSchoolTotal(rs.getInt("schoolTotal"));
				}
				obj.setHaveReserve(0);
				if(CommonUtil.isNotEmpty(rs.getString("haveReserve"))) {
					obj.setHaveReserve(rs.getInt("haveReserve"));
				}
				obj.setLevelName("-");
				if(CommonUtil.isNotEmpty(rs.getString("levelName"))) {
					obj.setLevelName(rs.getString("levelName"));
				}
				obj.setSchoolNatureName("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolNatureName"))) {
					obj.setSchoolNatureName(rs.getString("schoolNatureName"));
				}
				obj.setDepartmentMasterId("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentMasterId"))) {
					obj.setDepartmentMasterId(rs.getString("departmentMasterId"));
				}
				obj.setDepartmentSlaveIdName("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentSlaveIdName"))) {
					obj.setDepartmentSlaveIdName(rs.getString("departmentSlaveIdName"));
				}
				obj.setDepartmentId("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentId"))) {
					obj.setDepartmentId(rs.getString("departmentId"));
				}
				obj.setReserveDealStatus("-");
				if(CommonUtil.isNotEmpty(rs.getString("reserveDealStatus"))) {
					obj.setReserveDealStatus(rs.getString("reserveDealStatus"));
				}
				logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
				return obj;
			}
		});
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_d中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduReserveTotalDListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduReserveTotalD inputObj) {
        logger.info("[Enter dao method] {}-{}", "AppTEduReserveTotalDDao", "getAppTEduReserveTotalDListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_reserve_total_d" );
        sb.append(" where 1=1  ");
        
        getAppTEduReserveTotalDListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
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
    * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_d中根据条件查询数据列表条件
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @param sb
    */
    public void getAppTEduReserveTotalDListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduReserveTotalD inputObj,
    StringBuffer sb) {
        logger.info("[Enter dao method] {}-{}", "AppTEduReserveTotalDDao", "getAppTEduReserveTotalDListCondition");
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
		
        sb.append(" and DATE_FORMAT(expect_receive_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(expect_receive_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
        //useDate
        if(CommonUtil.isNotEmpty(inputObj.getUseDate())) {
        	sb.append(" and use_date = \"" + inputObj.getUseDate()+"\"");
        }

        //area
        if(CommonUtil.isNotEmpty(inputObj.getArea())) {
        	sb.append(" and area = \"" + inputObj.getArea()+"\"");
        }

        //total
        if(inputObj.getTotal() !=null && inputObj.getTotal() != -1) {
        	sb.append(" and total = \"" + inputObj.getTotal()+"\"");
        }

        //schoolTotal
        if(inputObj.getSchoolTotal() !=null && inputObj.getSchoolTotal() != -1) {
        	sb.append(" and school_total = \"" + inputObj.getSchoolTotal()+"\"");
        }

        //haveReserve
        if(inputObj.getHaveReserve() !=null && inputObj.getHaveReserve() != -1) {
        	sb.append(" and have_reserve = \"" + inputObj.getHaveReserve()+"\"");
        }

        //levelName
        if(CommonUtil.isNotEmpty(inputObj.getLevelName())) {
        	sb.append(" and level_name = \"" + inputObj.getLevelName()+"\"");
        }

        //schoolNatureName
        if(CommonUtil.isNotEmpty(inputObj.getSchoolNatureName())) {
        	sb.append(" and school_nature_name = \"" + inputObj.getSchoolNatureName()+"\"");
        }

        //departmentMasterId
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentMasterId())) {
        	sb.append(" and department_master_id = \"" + inputObj.getDepartmentMasterId()+"\"");
        }

        //departmentSlaveIdName
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentSlaveIdName())) {
        	sb.append(" and department_slave_id_name = \"" + inputObj.getDepartmentSlaveIdName()+"\"");
        }

        //departmentId
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentId())) {
        	sb.append(" and department_id = \"" + inputObj.getDepartmentId()+"\"");
        }

        //reserveDealStatus
        if(CommonUtil.isNotEmpty(inputObj.getReserveDealStatus())) {
        	sb.append(" and reserve_deal_status = \"" + inputObj.getReserveDealStatus()+"\"");
        }

        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
    }

}
