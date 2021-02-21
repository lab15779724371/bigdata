package com.tfit.BdBiProcSrvShEduOmc.service.impl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpDishCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduPlatoonTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchDishCommon;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;

/**
 * 排菜相关hive库的查询
 * @author Administrator
 *
 */
@Service
public class DbHiveDishServiceImpl implements DbHiveDishService {
	private static final Logger logger = LogManager.getLogger(DbHiveDishServiceImpl.class.getName());
	
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
  	
  	//--------------------排菜-汇总----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_platoon_total中根据条件查询数据列表
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
        sb.append(" area,total,have_class haveClass,have_platoon havePlatoon, ");
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
	
  	//--------------------排菜-详情----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据列表
  	 */
    public List<PpDishCommonDets> getDishDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			String ppName,String rmcId, String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			Integer acceptStatus,Integer assignStatus,Integer dispStatus,
			String departmentId,List<Object> departmentIdList,String plastatus,String reason,
			String disDealStatus,List<String> schoolList,
			int mode,
    		Integer startNum,Integer endNum) {
    	
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
        sb.append(" select from_unixtime(unix_timestamp(use_date,'yyyy-MM-dd'),'yyyy/MM/dd') useDate, ");
        if(mode==-1 || mode==0) {
	        sb.append(" parent_id relGenSchName,department_master_id subLevel,department_slave_id_name compDep,school_area_id subDistName, ");
	        sb.append(" is_branch_school schGenBraFlag,branch_total braCampusNum,school_nature_name schProp, area area, ");
	        sb.append(" school_name ppName, supplier_name rmcName,level_name schType,address detailAddr, ");
	        sb.append(" have_class mealFlag,have_platoon dishFlag,");
	        sb.append(" food_safety_persion projContact,food_safety_mobilephone pcMobilePhone, ");
	        sb.append(" supplier_id schSupplierId,license_main_type fblMb, license_main_child optMode, ");
	        sb.append(" from_unixtime(unix_timestamp(platoon_create_time,'yyyy-MM-dd  HH:mm:dd'),'yyyy/MM/dd  HH:mm:dd') platoonCreateTime, ");
	        sb.append(" material_status materialStatus,ledger_total ledgerTotal, ");
	        sb.append(" ledger_accept_total ledgerAcceptTotal,ledger_assign_total ledgerAssignTotal, ");
	        sb.append(" ledger_shipp_total ledgerShippTotal,haul_status haulStatus, ");
	        sb.append(" reserve_total reserveTotal,no_reserve_total noReserveTotal, ");
	        sb.append(" have_reserve_total haveReserveTotal,have_reserve haveReserve, reason,department_id departmentId, ");
        }
        
        sb.append(" platoon_deal_status plastatus,dis_deal_status disDealStatus,school_id schoolId ");
        
        sb.append(" from app_t_edu_platoon_detail" );
        sb.append(" where  1=1 ");
        getDishDetsListCondition(listYearMonth, startDate, endDateAddOne, distIdorSCName, subLevel,
        		compDep, schGenBraFlag, subDistName, fblMb, schProp, dishFlag, ppName, rmcId, rmcName, 
        		schType, mealFlag, optMode, sendFlag, distNames, subLevels, compDeps, schProps, 
        		schTypes, optModesList, subDistNamesList,acceptStatus,assignStatus,dispStatus,
        		departmentId,departmentIdList,plastatus,reason,disDealStatus,schoolList,
        		sb);
        
        if(startNum !=null && endNum !=null && startNum >=0 && endNum>=0  && endNum>=startNum) {
        	sb.append(" limit "+endNum);
        }
        logger.info("执行sql:"+sb.toString());
        logger.info("******************jdbcTemplateHive:"+jdbcTemplateHive);
		return (List<PpDishCommonDets>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<PpDishCommonDets>() {
			@Override
			public PpDishCommonDets mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && endNum!=null && startNum >=0 && endNum>=0  && endNum>=startNum
						&& startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
				
				PpDishCommonDets cdsd = new PpDishCommonDets();
				cdsd.setDishDate(rs.getString("useDate"));
				//项目点UUID
				cdsd.setSchoolId("-");
	            if(CommonUtil.isNotEmpty(rs.getString("schoolId"))) {
	            	cdsd.setSchoolId(rs.getString("schoolId"));
	            }
	            
	            if(mode==-1 || mode==0) {
					//项目点名称
					cdsd.setPpName("-");
		            if(CommonUtil.isNotEmpty(rs.getString("ppName"))) {
		            	cdsd.setPpName(rs.getString("ppName"));
		            }
					//总分校标识
					cdsd.setSchGenBraFlag("-");
					if(rs.getInt("schGenBraFlag") > -1) {
						if(rs.getInt("schGenBraFlag") == 1) { //分校
							cdsd.setSchGenBraFlag("分校");
						}
						else {   //总校
							cdsd.setSchGenBraFlag("总校");
						}
					}
					//分校数量
					cdsd.setBraCampusNum(0);
					if(rs.getInt("braCampusNum") > -1) {
						cdsd.setBraCampusNum(rs.getInt("braCampusNum"));
					}
					//关联总校
					cdsd.setRelGenSchName("-");
					if(CommonUtil.isNotEmpty(rs.getString("relGenSchName")) ) {
						cdsd.setRelGenSchName(rs.getString("relGenSchName"));
					}
					//所属
					cdsd.setSubLevel("-");
					if(CommonUtil.isNotEmpty(rs.getString("subLevel")) && !"-1".equals(rs.getString("subLevel"))) {
						cdsd.setSubLevel(AppModConfig.subLevelIdToNameMap.get(Integer.valueOf(rs.getString("subLevel"))));
					}
					//主管部门
					cdsd.setCompDep("-");
					if(CommonUtil.isNotEmpty(cdsd.getSubLevel())&& !"-1".equals(rs.getString("subLevel"))
							&& !"-1".equals(rs.getString("compDep"))) {
						if("0".equals(rs.getString("subLevel"))) {
							cdsd.setCompDep(AppModConfig.compDepIdToNameMap0.get(rs.getString("compDep")));
						}else if ("1".equals(rs.getString("subLevel"))) {
							cdsd.setCompDep(AppModConfig.compDepIdToNameMap1.get(rs.getString("compDep")));
						}else if ("2".equals(rs.getString("subLevel"))) {
							cdsd.setCompDep(AppModConfig.compDepIdToNameMap2.get(rs.getString("compDep")));
						}else if ("3".equals(rs.getString("subLevel"))) {
							cdsd.setCompDep(AppModConfig.compDepIdToNameMap3.get(rs.getString("compDep")));
						}
					}
					
					//所属区域名称
					cdsd.setSubDistName("-");
					if(CommonUtil.isNotEmpty(rs.getString("subDistName"))) {
						cdsd.setSubDistName(AppModConfig.distIdToNameMap.get(rs.getString("subDistName")));
					}
					
					//区域
					cdsd.setDistName("-");
					if(CommonUtil.isNotEmpty(rs.getString("area"))) {
			            	cdsd.setDistName(rs.getString("area"));
			        }
					//学校类型（学制）
					cdsd.setSchType("-");
					if(CommonUtil.isNotEmpty(rs.getString("schType"))) {
						cdsd.setSchType(AppModConfig.schTypeIdToNameMap.get(Integer.valueOf(rs.getString("schType"))));		
					}
					//学校性质
					cdsd.setSchProp("-");
					if(CommonUtil.isNotEmpty(rs.getString("schProp")) && !"-1".equals(rs.getString("schProp"))) {
						cdsd.setSchProp(AppModConfig.getSchProp(rs.getString("schProp")));
					}
					//是否供餐
					cdsd.setMealFlag(0);
		            if(CommonUtil.isNotEmpty(rs.getString("mealFlag"))) {
		            	cdsd.setMealFlag(rs.getInt("mealFlag"));
		            }
					//食品经营许可证主体
					cdsd.setFblMb("-");
					if(CommonUtil.isNotEmpty(rs.getString("fblMb")) && CommonUtil.isInteger(rs.getString("fblMb"))) {
						cdsd.setFblMb(AppModConfig.fblMbIdToNameMap.get(Integer.parseInt(rs.getString("fblMb"))));
					}
					
					//供餐模式
					cdsd.setOptMode("-");
					if(CommonUtil.isNotEmpty(rs.getString("optMode")) && CommonUtil.isInteger(rs.getString("optMode"))) {
						cdsd.setOptMode(rs.getString("optMode"));
						
						String licenseMainType = rs.getString("fblMb");
						Integer licenseMainChild = Integer.parseInt(rs.getString("optMode"));
						//新的经营模式判断
				  		if(licenseMainType != null) {
				  			if(licenseMainType.equals("0")) {    //学校
				  				if(licenseMainChild != null) {
				  					if(licenseMainChild == 0)
				  						cdsd.setOptMode("自营");
				  					else if(licenseMainChild == 1)
				  						cdsd.setOptMode("自营");
				  				}
				  				cdsd.setOptMode("自营");
				  			}
				  			else if(licenseMainType.equals("1")) {    //外包
				  				if(licenseMainChild != null) {
				  					if(licenseMainChild == 0)
				  						cdsd.setOptMode("托管");
				  					else if(licenseMainChild == 1)
				  						cdsd.setOptMode("外送");
				  				}
				  			}else {
				  				cdsd.setOptMode("");
				  			}
				  		}
					}
					
					//团餐公司名称
		            cdsd.setRmcName("-");
		            if(CommonUtil.isNotEmpty(rs.getString("rmcName"))) {
		            	cdsd.setRmcName(rs.getString("rmcName"));
		            }
		            
					//是否排菜
					cdsd.setDishFlag(0);
		            if(CommonUtil.isNotEmpty(rs.getString("dishFlag"))) {
		            	cdsd.setDishFlag(rs.getInt("dishFlag"));
		            }
		            
					//详细地址
					cdsd.setDetailAddr("-");
			        if(CommonUtil.isNotEmpty(rs.getString("detailAddr"))) {
			            cdsd.setDetailAddr(rs.getString("detailAddr"));
			        }
					//联系人
					cdsd.setProjContact("-");
					if(CommonUtil.isNotEmpty(rs.getString("projContact"))) {
						cdsd.setProjContact(rs.getString("projContact"));
					}
					//联系电话
					cdsd.setPcMobilePhone("-");
					if(CommonUtil.isNotEmpty(rs.getString("pcMobilePhone"))) {
						cdsd.setPcMobilePhone(rs.getString("pcMobilePhone"));
					}
					
					//操作时间
					cdsd.setCreatetime("-");
			        if(CommonUtil.isNotEmpty(rs.getString("platoonCreateTime"))) {
			            cdsd.setCreatetime(rs.getString("platoonCreateTime"));
			        }
		        	//配货计划数量
		        	cdsd.setDistrPlanNum(0);
		            if(CommonUtil.isNotEmpty(rs.getString("ledgerTotal"))) {
		            	cdsd.setDistrPlanNum(rs.getInt("ledgerTotal"));
		            }
		        	//已验收数量
		        	cdsd.setAcceptPlanNum(0);
		            if(CommonUtil.isNotEmpty(rs.getString("ledgerAcceptTotal"))) {
		            	cdsd.setAcceptPlanNum(rs.getInt("ledgerAcceptTotal"));
		            }
		        	//已指派数量
		        	cdsd.setAssignPlanNum(0);
		            if(CommonUtil.isNotEmpty(rs.getString("ledgerAssignTotal"))) {
		            	cdsd.setAssignPlanNum(rs.getInt("ledgerAssignTotal"));
		            }
		        	//已配送数量
		        	cdsd.setDispPlanNum(0);
		            if(CommonUtil.isNotEmpty(rs.getString("ledgerShippTotal"))) {
		            	cdsd.setDispPlanNum(rs.getInt("ledgerShippTotal"));
		            }
		            //配送状态
		        	cdsd.setHaulStatus(null);
		            if(CommonUtil.isNotEmpty(rs.getString("haulStatus"))) {
		            	cdsd.setHaulStatus(rs.getInt("haulStatus"));
		            }
		        	
		        	//留样总数
		        	cdsd.setReserveTotal(0);
		            if(CommonUtil.isNotEmpty(rs.getString("reserveTotal"))) {
		            	cdsd.setReserveTotal(rs.getInt("reserveTotal"));
		            }
		        	//未留样数量
		        	cdsd.setNoreserveTotal(0);
		            if(CommonUtil.isNotEmpty(rs.getString("noreserveTotal"))) {
		            	cdsd.setNoreserveTotal(rs.getInt("noreserveTotal"));
		            }
		        	//已留样数量
		        	cdsd.setHaveReserveTotal(0);
		            if(CommonUtil.isNotEmpty(rs.getString("haveReserveTotal"))) {
		            	cdsd.setHaveReserveTotal(rs.getInt("haveReserveTotal"));
		            }
		        	//是否留样
		        	cdsd.setHaveReserve(0);
		            if(CommonUtil.isNotEmpty(rs.getString("haveReserve"))) {
		            	cdsd.setHaveReserve(rs.getInt("haveReserve"));
		            }
		        	//供餐备注
		        	cdsd.setReason("");
		            if(CommonUtil.isNotEmpty(rs.getString("reason"))) {
		            	cdsd.setReason(rs.getString("reason"));
		            }
		            
		        	//管理部门
		        	cdsd.setDepartmentId("");
		            if(CommonUtil.isNotEmpty(rs.getString("departmentId"))) {
		            	cdsd.setDepartmentId(rs.getString("departmentId"));
		            }
	            }
	        	//操作状态
	        	cdsd.setPlaStatus("");
	            if(CommonUtil.isNotEmpty(rs.getString("plastatus"))) {
	            	cdsd.setPlaStatus(rs.getString("plastatus"));
	            }
	            
	          //操作状态
	        	cdsd.setDisDealStatus("");
	            if(CommonUtil.isNotEmpty(rs.getString("disDealStatus"))) {
	            	cdsd.setDisDealStatus(rs.getString("disDealStatus"));
	            }
				return cdsd;
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据条数
     */
    public Integer getDishDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			String ppName,String rmcId, String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			Integer acceptStatus,Integer assignStatus,Integer dispStatus,
			String departmentId,List<Object> departmentIdList,String plastatus,String reason,
			String disDealStatus,List<String> schoolList
			) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_platoon_detail" );
        sb.append(" where 1=1  ");
        
        getDishDetsListCondition(listYearMonth, startDate, endDateAddOne, distIdorSCName, subLevel, compDep,
        		schGenBraFlag, subDistName, fblMb, schProp, dishFlag, ppName, rmcId, rmcName, schType,
        		mealFlag, optMode, sendFlag, distNames, subLevels, compDeps, schProps, schTypes,
        		optModesList, subDistNamesList,acceptStatus,assignStatus,dispStatus,
        		departmentId,departmentIdList,plastatus,reason,disDealStatus,schoolList,
        		sb);
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
	private void getDishDetsListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			String schName,String rmcId, String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			Integer acceptStatus,Integer assignStatus,Integer dispStatus,
			String departmentId,List<Object> departmentIdList,String plastatus,String reason,
			String disDealStatus,List<String> schoolList,
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
		//String distId, 
        //区
        if(StringUtils.isNotEmpty(distIdorSCName)) {
        	sb.append(" and area = \"" + distIdorSCName+"\"");
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
        
        if(distNames !=null && distNames.size()>0) {
        	String distIds= distNames.toString().substring(1,distNames.toString().length()-1);
        	if(distIds.indexOf("\"") <0) {
        		distIds = "\""+distIds.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and area in (" +distIds +")");
        }
        	
		//String schName, 
        //学校
        if(StringUtils.isNotEmpty(schName)) {
        	sb.append(" and school_name like \"%" + schName+"%\"");
        }
        
        if(schoolList !=null && schoolList.size()>0) {
        	String schoolIds= schoolList.toString().substring(1,schoolList.toString().length()-1);
        	if(schoolIds.indexOf("\"") <0) {
        		schoolIds = "\""+schoolIds.replaceAll(",", "\",\"")+"\"";
        		schoolIds=schoolIds.replaceAll(" ", "");
        	}
        	sb.append(" and school_id in (" +schoolIds +")");
        }
        
		//int schType, 
        //学校学段
        if(schType != -1) {
        	sb.append(" and level_name = " + schType);
        }
		//int subLevel, 
        //所属
        if(dishFlag != -1) {
        	sb.append(" and have_platoon = " + dishFlag);
        }
        
      //所属
        if(mealFlag != -1) {
        	sb.append(" and have_class = " + mealFlag);
        }
        
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
        if(subDistNamesList !=null && subDistNamesList.size()>0) {
        	String supplierAreas= subDistNamesList.toString().replace(" ", "");
        	supplierAreas = supplierAreas.substring(1,supplierAreas.length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and school_area_id in (" +supplierAreas +")");
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
        			
        			compMap.put(compArr[0], compArr[0]==null?compArr[1]:(compArr[1]));
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
        //List<String> schProps,
        //过滤状态，多选
        if(schProps !=null && schProps.size()>0) {
        	String supplierAreas= schProps.toString().substring(1,schProps.toString().length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and school_nature_name in (" +supplierAreas +")");
        }
		
        //List<String> schTypes,
        //过滤状态，多选
        if(schTypes !=null && schTypes.size()>0) {
        	String supplierAreas= schTypes.toString().substring(1,schTypes.toString().length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and level_name in (" +supplierAreas +")");
        }
        
		//int schProp, 
		//判断学校性质
        if(schProp != -1) {
        	sb.append(" and school_nature_name = \"" + schProp+"\"");
        }
		
        //
        if(StringUtils.isNotEmpty(subDistName)) {
        	sb.append(" and school_area_id = \"" + subDistName+"\"");
        }
        
        //团餐公司编号（针对学校餐厨垃圾和废弃油脂有效）
        if(StringUtils.isNotEmpty(rmcId)) {
        	sb.append(" and supplier_id = \"" + rmcId+"\"");
        }
        //团餐公司名称（针对团餐公司废弃油脂和餐厨垃圾时有效）
        if(StringUtils.isNotEmpty(rmcName)) {
        	sb.append(" and supplier_name = \"" + rmcName+"\"");
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
        
        //经营模式：多选
        if(optModesList!=null && optModesList.size() > 0) {
			sb.append(" AND (");
        	for(Object  optModeTemp : optModesList) {
		        int iOptMode = Integer.valueOf(optModeTemp.toString());
		        if(iOptMode==0 || iOptMode == 1) {
	        		sb.append("  license_main_type = \"0\" ");
	        		/*if(iOptMode ==0) {
	        			sb.append(" and license_main_child = 0 ) ");
	        		}else if (iOptMode ==1) {
	        			sb.append(" and license_main_child = 1 ) ");
	        		}else {
	        			sb.append("  ) ");
	        		}*/
	        	}else if (iOptMode==2 || iOptMode == 3) {
	        		sb.append(" (  license_main_type = \"1\" ");
	        		if(iOptMode ==2) {
	        			sb.append(" and license_main_child = 0 ) ");
	        		}else if (iOptMode ==3) {
	        			sb.append(" and license_main_child = 1 ) ");
	        		}else {
	        			sb.append("  ) ");
	        		}
	        	}
		        sb.append(" or ");
        	}
        	//去除最后一个or
        	sb.replace(sb.length()-3, sb.length(), "");
        	sb.append(") ");
    	}
        
        //是否是总校
        if(schGenBraFlag != -1) {
        	sb.append(" and is_branch_school = \"" + schGenBraFlag+"\"");
        }
        
        //是否是总校
        if(fblMb != -1) {
        	sb.append(" and license_main_type = \"" + fblMb+"\"");
        }
        
        
        //haul_status  -2 信息不完整 -1 未指派 0 已指派（未配送） 1配送中 2 待验收（已配送）3已验收 -4已取消
        //验收状态
        if(acceptStatus !=null && acceptStatus != -1) {
        	if(acceptStatus == 0 ) {
        		//未验收
        		sb.append(" and haul_status < 3 ");
        	}else {
        		//已验收
        		sb.append(" and haul_status = 3 ");
        	}
        }
        //指派状态
        if(assignStatus !=null && assignStatus != -1) {
        	if(assignStatus == 0 ) {
        		//未指派
        		sb.append(" and haul_status < 0 ");
        	}else {
        		//已指派
        		sb.append(" and haul_status > -1 ");
        	}
        }
        
        //配送状态
        if(dispStatus !=null && dispStatus != -1) {
        	if(dispStatus == 0 ) {
        		//未配送
        		sb.append(" and haul_status < 1 ");
        	}else {
        		//已配送
        		sb.append(" and haul_status > 0 ");
        	}
        }
        
        //操作状态
        if(CommonUtil.isNotEmpty(plastatus)) {
        	sb.append(" and platoon_deal_status = \"" + plastatus+"\"");
        }
        
        //验收操作规则规则
        if(CommonUtil.isNotEmpty(disDealStatus)) {
        	sb.append(" and dis_deal_status = \"" + disDealStatus+"\"");
        }
        
        //不供餐原因
        if(CommonUtil.isNotEmpty(reason)) {
        	
        	if(CommonUtil.isNotEmpty(reason)) {
				if(AppModConfig.noDishReasonTypeTwo.contains(String.valueOf(reason))) {
					//非其他
					sb.append(" and reason = \"" + reason+"\"");
				}else {
					//其他
					String supplierAreas= AppModConfig.noDishReasonTypeTwo.toString().replace(" ", "");
	            	supplierAreas = supplierAreas.substring(1,supplierAreas.length()-1);
	            	if(supplierAreas.indexOf("\"") <0) {
	            		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
	            	}
	            	sb.append(" and reason not in (" +supplierAreas +")");
				}
			}
        }
        
        
	}

	//--------------------使用情况-详情----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据列表
  	 */
    public List<PpDishCommonDets> getDishUseDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			String schoolId,String ppName,String rmcId, String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			Integer acceptStatus,Integer assignStatus,Integer dispStatus,
			Integer materialStatus,Integer haveReserve,String reason,
			String departmentId,List<Object> departmentIdList,
    		Integer startNum,Integer endNum) {
    	
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
        sb.append(" select from_unixtime(unix_timestamp(use_date,'yyyy-MM-dd'),'yyyy/MM/dd') useDate,is_branch_school schGenBraFlag,branch_total braCampusNum, ");
        sb.append(" parent_id relGenSchName,department_master_id subLevel,department_slave_id_name compDep,school_area_id subDistName, ");
        sb.append(" school_nature_name schProp, area area, ");
        sb.append(" school_name ppName, supplier_name rmcName,level_name schType,address detailAddr, ");
        sb.append(" have_class mealFlag,have_platoon dishFlag,");
        sb.append(" food_safety_persion projContact,food_safety_mobilephone pcMobilePhone, ");
        sb.append(" supplier_id schSupplierId,license_main_type fblMb, license_main_child optMode, ");
        sb.append(" from_unixtime(unix_timestamp(platoon_create_time,'yyyy-MM-dd'),'yyyy/MM/dd') platoonCreateTime, ");
        
        sb.append(" material_status materialStatus,ledger_total ledgerTotal, ");
        sb.append(" ledger_accept_total ledgerAcceptTotal,ledger_assign_total ledgerAssignTotal, ");
        sb.append(" ledger_shipp_total ledgerShippTotal,haul_status haulStatus, ");
        sb.append(" reserve_total reserveTotal,no_reserve_total noReserveTotal, ");
        sb.append(" have_reserve_total haveReserveTotal,have_reserve haveReserve, reason,department_id departmentId ");

        sb.append(" from app_t_edu_platoon_detail" );
        sb.append(" where  1=1 ");
        getDishUseDetsListCondition(listYearMonth, startDate, endDateAddOne, distIdorSCName, subLevel,
        		compDep, schGenBraFlag, subDistName, fblMb, schProp, dishFlag, schoolId,ppName, rmcId, rmcName, 
        		schType, mealFlag, optMode, sendFlag, distNames, subLevels, compDeps, schProps, 
        		schTypes, optModesList, subDistNamesList,acceptStatus,assignStatus,dispStatus,
        		materialStatus,haveReserve,reason,
        		departmentId,departmentIdList,
        		sb);
        
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+endNum);
        }
        logger.info("执行sql:"+sb.toString());
		return (List<PpDishCommonDets>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<PpDishCommonDets>() {
			@Override
			public PpDishCommonDets mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				if(startNum!=null && endNum!=null && startNum!=-1 &&  endNum != -1 
						&& startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
				
				PpDishCommonDets cdsd = new PpDishCommonDets();
				cdsd.setDishDate(rs.getString("useDate"));
				//项目点名称
				cdsd.setPpName("-");
	            if(CommonUtil.isNotEmpty(rs.getString("ppName"))) {
	            	cdsd.setPpName(rs.getString("ppName"));
	            }
				//总分校标识
				cdsd.setSchGenBraFlag("-");
				if(rs.getInt("schGenBraFlag") > -1) {
					if(rs.getInt("schGenBraFlag") == 1) { //分校
						cdsd.setSchGenBraFlag("分校");
					}
					else {   //总校
						cdsd.setSchGenBraFlag("总校");
					}
				}
				//分校数量
				cdsd.setBraCampusNum(0);
				if(rs.getInt("braCampusNum") > -1) {
					cdsd.setBraCampusNum(rs.getInt("braCampusNum"));
				}
				//关联总校
				cdsd.setRelGenSchName("-");
				if(CommonUtil.isNotEmpty(rs.getString("relGenSchName")) ) {
					cdsd.setRelGenSchName(rs.getString("relGenSchName"));
				}
				//所属
				cdsd.setSubLevel("-");
				if(CommonUtil.isNotEmpty(rs.getString("subLevel")) && !"-1".equals(rs.getString("subLevel"))) {
					cdsd.setSubLevel(AppModConfig.subLevelIdToNameMap.get(Integer.valueOf(rs.getString("subLevel"))));
				}
				//主管部门
				cdsd.setCompDep("-");
				if(CommonUtil.isNotEmpty(cdsd.getSubLevel())&& !"-1".equals(rs.getString("subLevel"))
						&& !"-1".equals(rs.getString("compDep"))) {
					if("0".equals(rs.getString("subLevel"))) {
						cdsd.setCompDep(AppModConfig.compDepIdToNameMap0.get(rs.getString("compDep")));
					}else if ("1".equals(rs.getString("subLevel"))) {
						cdsd.setCompDep(AppModConfig.compDepIdToNameMap1.get(rs.getString("compDep")));
					}else if ("2".equals(rs.getString("subLevel"))) {
						cdsd.setCompDep(AppModConfig.compDepIdToNameMap2.get(rs.getString("compDep")));
					}else if ("3".equals(rs.getString("subLevel"))) {
						cdsd.setCompDep(AppModConfig.compDepIdToNameMap3.get(rs.getString("compDep")));
					}
				}
				
				//所属区域名称
				cdsd.setSubDistName("-");
				if(CommonUtil.isNotEmpty(rs.getString("subDistName"))) {
					cdsd.setSubDistName(AppModConfig.distIdToNameMap.get(rs.getString("subDistName")));
				}
				
				//区域
				cdsd.setDistName("-");
				if(CommonUtil.isNotEmpty(rs.getString("area"))) {
		            	cdsd.setDistName(rs.getString("area"));
		        }
				//学校类型（学制）
				cdsd.setSchType("-");
				if(CommonUtil.isNotEmpty(rs.getString("schType"))) {
					cdsd.setSchType(AppModConfig.schTypeIdToNameMap.get(Integer.valueOf(rs.getString("schType"))));		
				}
				//学校性质
				cdsd.setSchProp("-");
				if(CommonUtil.isNotEmpty(rs.getString("schProp")) && !"-1".equals(rs.getString("schProp"))) {
					cdsd.setSchProp(AppModConfig.getSchProp(rs.getString("schProp")));
				}
				//是否供餐
				cdsd.setMealFlag(0);
	            if(CommonUtil.isNotEmpty(rs.getString("mealFlag"))) {
	            	cdsd.setMealFlag(rs.getInt("mealFlag"));
	            }
				//食品经营许可证主体
				cdsd.setFblMb("-");
				if(CommonUtil.isNotEmpty(rs.getString("fblMb")) && CommonUtil.isInteger(rs.getString("fblMb"))) {
					cdsd.setFblMb(AppModConfig.fblMbIdToNameMap.get(Integer.parseInt(rs.getString("fblMb"))));
				}
				
				//供餐模式
				cdsd.setOptMode("-");
				if(CommonUtil.isNotEmpty(rs.getString("optMode")) && CommonUtil.isInteger(rs.getString("optMode"))) {
					cdsd.setOptMode(rs.getString("optMode"));
					
					String licenseMainType = rs.getString("fblMb");
					Integer licenseMainChild = Integer.parseInt(rs.getString("optMode"));
					//新的经营模式判断
			  		if(licenseMainType != null) {
			  			if(licenseMainType.equals("0")) {    //学校
			  				if(licenseMainChild != null) {
			  					if(licenseMainChild == 0)
			  						cdsd.setOptMode("自营");
			  					else if(licenseMainChild == 1)
			  						cdsd.setOptMode("自营");
			  				}
			  				cdsd.setOptMode("自营");
			  			}
			  			else if(licenseMainType.equals("1")) {    //外包
			  				if(licenseMainChild != null) {
			  					if(licenseMainChild == 0)
			  						cdsd.setOptMode("托管");
			  					else if(licenseMainChild == 1)
			  						cdsd.setOptMode("外送");
			  				}
			  			}else {
			  				cdsd.setOptMode("");
			  			}
			  		}
				}
				
				//团餐公司名称
	            cdsd.setRmcName("-");
	            if(CommonUtil.isNotEmpty(rs.getString("rmcName"))) {
	            	cdsd.setRmcName(rs.getString("rmcName"));
	            }
	            
				//是否排菜
				cdsd.setDishFlag(0);
	            if(CommonUtil.isNotEmpty(rs.getString("dishFlag"))) {
	            	cdsd.setDishFlag(rs.getInt("dishFlag"));
	            }
	            
				//详细地址
				cdsd.setDetailAddr("-");
		        if(CommonUtil.isNotEmpty(rs.getString("detailAddr"))) {
		            cdsd.setDetailAddr(rs.getString("detailAddr"));
		        }
				//联系人
				cdsd.setProjContact("-");
				if(CommonUtil.isNotEmpty(rs.getString("projContact"))) {
					cdsd.setProjContact(rs.getString("projContact"));
				}
				//联系电话
				cdsd.setPcMobilePhone("-");
				if(CommonUtil.isNotEmpty(rs.getString("pcMobilePhone"))) {
					cdsd.setPcMobilePhone(rs.getString("pcMobilePhone"));
				}
				
				//操作时间
				cdsd.setCreatetime("-");
		        if(CommonUtil.isNotEmpty(rs.getString("platoonCreateTime"))) {
		            cdsd.setCreatetime(rs.getString("platoonCreateTime"));
		        }
	        	//配货计划数量
	        	cdsd.setDistrPlanNum(0);
	            if(CommonUtil.isNotEmpty(rs.getString("ledgerTotal"))) {
	            	cdsd.setDistrPlanNum(rs.getInt("ledgerTotal"));
	            }
	        	//已验收数量
	        	cdsd.setAcceptPlanNum(0);
	            if(CommonUtil.isNotEmpty(rs.getString("ledgerAcceptTotal"))) {
	            	cdsd.setAcceptPlanNum(rs.getInt("ledgerAcceptTotal"));
	            }
	        	//已指派数量
	        	cdsd.setAssignPlanNum(0);
	            if(CommonUtil.isNotEmpty(rs.getString("ledgerAssignTotal"))) {
	            	cdsd.setAssignPlanNum(rs.getInt("ledgerAssignTotal"));
	            }
	        	//已配送数量
	        	cdsd.setDispPlanNum(0);
	            if(CommonUtil.isNotEmpty(rs.getString("ledgerShippTotal"))) {
	            	cdsd.setDispPlanNum(rs.getInt("ledgerShippTotal"));
	            }
	            //配送状态
	        	cdsd.setHaulStatus(null);
	            if(CommonUtil.isNotEmpty(rs.getString("haulStatus"))) {
	            	cdsd.setHaulStatus(rs.getInt("haulStatus"));
	            }
	        	
	        	//留样总数
	        	cdsd.setReserveTotal(0);
	            if(CommonUtil.isNotEmpty(rs.getString("reserveTotal"))) {
	            	cdsd.setReserveTotal(rs.getInt("reserveTotal"));
	            }
	        	//未留样数量
	        	cdsd.setNoreserveTotal(0);
	            if(CommonUtil.isNotEmpty(rs.getString("noreserveTotal"))) {
	            	cdsd.setNoreserveTotal(rs.getInt("noreserveTotal"));
	            }
	        	//已留样数量
	        	cdsd.setHaveReserveTotal(0);
	            if(CommonUtil.isNotEmpty(rs.getString("haveReserveTotal"))) {
	            	cdsd.setHaveReserveTotal(rs.getInt("haveReserveTotal"));
	            }
	        	//是否留样
	        	cdsd.setHaveReserve(0);
	            if(CommonUtil.isNotEmpty(rs.getString("haveReserve"))) {
	            	cdsd.setHaveReserve(rs.getInt("haveReserve"));
	            }
	            
	            
	            //用料确认情况 0:未确认，1:已确认
	        	cdsd.setMaterialStatus(0);
	            if(CommonUtil.isNotEmpty(rs.getString("materialStatus"))) {
	            	cdsd.setMaterialStatus(rs.getInt("materialStatus") !=2?0:1);
	            }
	        	//供餐备注
	        	cdsd.setReason("");
	            if(CommonUtil.isNotEmpty(rs.getString("reason"))) {
	            	cdsd.setReason(rs.getString("reason"));
	            }
	            
	            //管理部门
				cdsd.setDepartmentId("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentId"))) {
		            	cdsd.setDepartmentId(rs.getString("departmentId"));
		        }
				return cdsd;
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_platoon_detail中根据条件查询数据条数
     */
    public Integer getDishUseDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			String schoolId,String ppName,String rmcId, String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			Integer acceptStatus,Integer assignStatus,Integer dispStatus,
			Integer materialStatus,Integer haveReserve,String reason,
			String departmentId,List<Object> departmentIdList
			) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_platoon_detail" );
        sb.append(" where 1=1  ");
        
        getDishUseDetsListCondition(listYearMonth, startDate, endDateAddOne, distIdorSCName, subLevel, compDep,
        		schGenBraFlag, subDistName, fblMb, schProp, dishFlag,schoolId, ppName, rmcId, rmcName, schType,
        		mealFlag, optMode, sendFlag, distNames, subLevels, compDeps, schProps, schTypes,
        		optModesList, subDistNamesList,acceptStatus,assignStatus,dispStatus,
        		materialStatus,haveReserve,reason,
        		departmentId,departmentIdList,
        		sb);
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
	private void getDishUseDetsListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp, int dishFlag,
			String schoolId,String schName,String rmcId, String rmcName, int schType, int mealFlag, int optMode, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			Integer acceptStatus,Integer assignStatus,Integer dispStatus,
			Integer materialStatus,Integer haveReserve,String reason,
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
        
		//String distId, 
        //区
        if(StringUtils.isNotEmpty(distIdorSCName)) {
        	sb.append(" and area = \"" + distIdorSCName+"\"");
        }
        
        if(distNames !=null && distNames.size()>0) {
        	String distIds= distNames.toString().substring(1,distNames.toString().length()-1);
        	if(distIds.indexOf("\"") <0) {
        		distIds = "\""+distIds.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and area in (" +distIds +")");
        }
        	
		//String schName, 
        //学校
        if(StringUtils.isNotEmpty(schName)) {
        	sb.append(" and school_name like \"%" + schName+"%\"");
        }
        
        //学校编号
        if(StringUtils.isNotEmpty(schoolId)) {
        	sb.append(" and school_id = \"" + schoolId+"\"");
        }
        
		//int schType, 
        //学校学段
        if(schType != -1) {
        	sb.append(" and level_name = " + schType);
        }
		//int subLevel, 
        //所属
        if(dishFlag != -1) {
        	sb.append(" and have_platoon = " + dishFlag);
        }
        
      //所属
        if(mealFlag != -1) {
        	sb.append(" and have_class = " + mealFlag);
        }
        
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
        if(subDistNamesList !=null && subDistNamesList.size()>0) {
        	String supplierAreas= subDistNamesList.toString().replace(" ", "");
        	supplierAreas = supplierAreas.substring(1,supplierAreas.length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and school_area_id in (" +supplierAreas +")");
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
        			
        			compMap.put(compArr[0], compMap.get(compArr[0])==null?"":compMap.get(compArr[0])+","+compArr[1]);
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
        //List<String> schProps,
        //过滤状态，多选
        if(schProps !=null && schProps.size()>0) {
        	String supplierAreas= schProps.toString().substring(1,schProps.toString().length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and school_nature_name in (" +supplierAreas +")");
        }
		
        //List<String> schTypes,
        //过滤状态，多选
        if(schTypes !=null && schTypes.size()>0) {
        	String supplierAreas= schTypes.toString().substring(1,schTypes.toString().length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and level_name in (" +supplierAreas +")");
        }
        
		//int schProp, 
		//判断学校性质
        if(schProp != -1) {
        	sb.append(" and school_nature_name = \"" + schProp+"\"");
        }
		
        //
        if(StringUtils.isNotEmpty(subDistName)) {
        	sb.append(" and school_area_id = \"" + subDistName+"\"");
        }
        
        //团餐公司编号（针对学校餐厨垃圾和废弃油脂有效）
        if(StringUtils.isNotEmpty(rmcId)) {
        	sb.append(" and supplier_id = \"" + rmcId+"\"");
        }
        //团餐公司名称（针对团餐公司废弃油脂和餐厨垃圾时有效）
        if(StringUtils.isNotEmpty(rmcName)) {
        	sb.append(" and supplier_name like \"%" + rmcName+"%\"");
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
        
        //经营模式：多选
        if(optModesList!=null && optModesList.size() > 0) {
			sb.append(" AND (");
        	for(Object  optModeTemp : optModesList) {
		        int iOptMode = Integer.valueOf(optModeTemp.toString());
		        if(iOptMode==0 || iOptMode == 1) {
	        		sb.append("   license_main_type = \"0\" ");
	        		/*if(iOptMode ==0) {
	        			sb.append(" and license_main_child = 0 ) ");
	        		}else if (iOptMode ==1) {
	        			sb.append(" and license_main_child = 1 ) ");
	        		}else {
	        			sb.append("  ) ");
	        		}*/
	        	}else if (iOptMode==2 || iOptMode == 3) {
	        		sb.append(" (  license_main_type = \"1\" ");
	        		if(iOptMode ==2) {
	        			sb.append(" and license_main_child = 0 ) ");
	        		}else if (iOptMode ==3) {
	        			sb.append(" and license_main_child = 1 ) ");
	        		}else {
	        			sb.append("  ) ");
	        		}
	        	}
		        sb.append(" or ");
        	}
        	//去除最后一个or
        	sb.replace(sb.length()-3, sb.length(), "");
        	sb.append(") ");
    	}
        
        //是否是总校
        if(schGenBraFlag != -1) {
        	sb.append(" and is_branch_school = \"" + schGenBraFlag+"\"");
        }
        
        //是否是总校
        if(fblMb != -1) {
        	sb.append(" and license_main_type = \"" + fblMb+"\"");
        }
        
        
        //haul_status  -2 信息不完整 -1 未指派 0 已指派（未配送） 1配送中 2 待验收（已配送）3已验收 -4已取消
        //验收状态
        if(acceptStatus !=null && acceptStatus != -1) {
        	if(acceptStatus == 0 ) {
        		//未验收
        		sb.append(" and haul_status < 3 ");
        	}else {
        		//已验收
        		sb.append(" and haul_status = 3 ");
        	}
        }
        //指派状态
        if(assignStatus !=null && assignStatus != -1) {
        	if(assignStatus == 0 ) {
        		//未指派
        		sb.append(" and haul_status < 0 ");
        	}else {
        		//已指派
        		sb.append(" and haul_status > -1 ");
        	}
        }
        
        //配送状态
        if(dispStatus !=null && dispStatus != -1) {
        	if(dispStatus == 0 ) {
        		//未配送
        		sb.append(" and haul_status < 1 ");
        	}else {
        		//已配送
        		sb.append(" and haul_status > 0 ");
        	}
        }
        
        //用料确认状态
        if(materialStatus !=null && materialStatus != -1) {
        	if(materialStatus == 0 ) {
        		//未确认
        		sb.append(" and material_status < 2 ");
        	}else {
        		//已确认
        		sb.append(" and material_status = 2 ");
        	}
        }
        
        //留样状态
        if(haveReserve !=null && haveReserve != -1) {
        	if(dispStatus == 0 ) {
        		//未留样
        		sb.append(" and have_reserve < 1 ");
        	}else {
        		//已留样
        		sb.append(" and haul_status > 0 ");
        	}
        }
        
        //不供餐原因
        if(CommonUtil.isNotEmpty(reason)) {
        	
        	if(CommonUtil.isNotEmpty(reason)) {
				if(AppModConfig.noDishReasonTypeTwo.contains(String.valueOf(reason))) {
					//非其他
					sb.append(" and reason = \"" + reason+"\"");
				}else {
					//其他
					String supplierAreas= AppModConfig.noDishReasonTypeTwo.toString().replace(" ", "");
	            	supplierAreas = supplierAreas.substring(1,supplierAreas.length()-1);
	            	if(supplierAreas.indexOf("\"") <0) {
	            		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
	            	}
	            	sb.append(" and reason not in (" +supplierAreas +")");
				}
			}
        }
        
	}

	//--------------------使用情况-周报表----------------------------------------------
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_platoon_total_w中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduPlatoonTotalWObj> getAppTEduPlatoonTotalWObjList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduPlatoonTotalWObj inputObj,
    		Integer startNum,Integer endNum) {
        logger.info("[Enter dao method] {}-{}", "AppTEduPlatoonTotalWObjDao", "getAppTEduPlatoonTotalWObjList");
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append("start_use_date startUseDate, ");
        sb.append("  end_use_date endUseDate, school_id schoolId, school_name schoolName, department_id departmentId, ");
        sb.append("  area area, level_name levelName, school_nature_name schoolNatureName, school_nature_sub_name schoolNatureSubName, ");
        sb.append("  branch_total branchTotal, department_master_id departmentMasterId, department_slave_id_name departmentSlaveIdName, school_area_id schoolAreaId, ");
        sb.append("  license_main_type licenseMainType, license_main_child licenseMainChild, have_class_total haveClassTotal, have_platoon_total havePlatoonTotal, ");
        sb.append("  have_no_platoon_total haveNoPlatoonTotal, guifan_platoon_total guifanPlatoonTotal, bulu_platoon_total buluPlatoonTotal, yuqi_platoon_total yuqiPlatoonTotal, ");
        sb.append("  no_platoon_total noPlatoonTotal, address address, food_safety_persion foodSafetyPersion, food_safety_mobilephone foodSafetyMobilephone");
        sb.append("   ");
        sb.append(" from app_t_edu_platoon_total_w " );
        sb.append(" where  1=1 ");
        getAppTEduPlatoonTotalWObjListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+endNum);
        }        logger.info("执行sql:"+sb.toString());
		return (List<AppTEduPlatoonTotalWObj>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<AppTEduPlatoonTotalWObj>() {
			@Override
			public AppTEduPlatoonTotalWObj mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				if(startNum!=null && endNum!=null && startNum!=-1 &&  endNum != -1 
						&& (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
				AppTEduPlatoonTotalWObj obj = new AppTEduPlatoonTotalWObj();

				obj.setStartUseDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("startUseDate"))) {
					obj.setStartUseDate(rs.getString("startUseDate"));
				}
				obj.setEndUseDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("endUseDate"))) {
					obj.setEndUseDate(rs.getString("endUseDate"));
				}
				obj.setSchoolId("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolId"))) {
					obj.setSchoolId(rs.getString("schoolId"));
				}
				obj.setSchoolName("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolName"))) {
					obj.setSchoolName(rs.getString("schoolName"));
				}
				obj.setDepartmentId("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentId"))) {
					obj.setDepartmentId(rs.getString("departmentId"));
				}
				obj.setArea("-");
				if(CommonUtil.isNotEmpty(rs.getString("area"))) {
					obj.setArea(rs.getString("area"));
				}
				obj.setLevelName("-");
				if(CommonUtil.isNotEmpty(rs.getString("levelName"))) {
					obj.setLevelName(rs.getString("levelName"));
				}
				obj.setSchoolNatureName("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolNatureName"))) {
					obj.setSchoolNatureName(rs.getString("schoolNatureName"));
				}
				obj.setSchoolNatureSubName("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolNatureSubName"))) {
					obj.setSchoolNatureSubName(rs.getString("schoolNatureSubName"));
				}
				obj.setBranchTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("branchTotal"))) {
					obj.setBranchTotal(rs.getInt("branchTotal"));
				}
				obj.setDepartmentMasterId("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentMasterId"))) {
					obj.setDepartmentMasterId(rs.getString("departmentMasterId"));
				}
				obj.setDepartmentSlaveIdName("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentSlaveIdName"))) {
					obj.setDepartmentSlaveIdName(rs.getString("departmentSlaveIdName"));
				}
				obj.setSchoolAreaId("-");
				if(CommonUtil.isNotEmpty(rs.getString("schoolAreaId"))) {
					obj.setSchoolAreaId(rs.getString("schoolAreaId"));
				}
				obj.setLicenseMainType("-");
				if(CommonUtil.isNotEmpty(rs.getString("licenseMainType"))) {
					obj.setLicenseMainType(rs.getString("licenseMainType"));
				}
				obj.setLicenseMainChild(0);
				if(CommonUtil.isNotEmpty(rs.getString("licenseMainChild"))) {
					obj.setLicenseMainChild(rs.getInt("licenseMainChild"));
				}
				obj.setHaveClassTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("haveClassTotal"))) {
					obj.setHaveClassTotal(rs.getInt("haveClassTotal"));
				}
				obj.setHavePlatoonTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("havePlatoonTotal"))) {
					obj.setHavePlatoonTotal(rs.getInt("havePlatoonTotal"));
				}
				obj.setHaveNoPlatoonTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("haveNoPlatoonTotal"))) {
					obj.setHaveNoPlatoonTotal(rs.getInt("haveNoPlatoonTotal"));
				}
				obj.setGuifanPlatoonTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("guifanPlatoonTotal"))) {
					obj.setGuifanPlatoonTotal(rs.getInt("guifanPlatoonTotal"));
				}
				obj.setBuluPlatoonTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("buluPlatoonTotal"))) {
					obj.setBuluPlatoonTotal(rs.getInt("buluPlatoonTotal"));
				}
				obj.setYuqiPlatoonTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("yuqiPlatoonTotal"))) {
					obj.setYuqiPlatoonTotal(rs.getInt("yuqiPlatoonTotal"));
				}
				obj.setNoPlatoonTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("noPlatoonTotal"))) {
					obj.setNoPlatoonTotal(rs.getInt("noPlatoonTotal"));
				}
				obj.setAddress("-");
				if(CommonUtil.isNotEmpty(rs.getString("address"))) {
					obj.setAddress(rs.getString("address"));
				}
				obj.setFoodSafetyPersion("-");
				if(CommonUtil.isNotEmpty(rs.getString("foodSafetyPersion"))) {
					obj.setFoodSafetyPersion(rs.getString("foodSafetyPersion"));
				}
				obj.setFoodSafetyMobilephone("-");
				if(CommonUtil.isNotEmpty(rs.getString("foodSafetyMobilephone"))) {
					obj.setFoodSafetyMobilephone(rs.getString("foodSafetyMobilephone"));
				}
				return obj;
			}
		});
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_platoon_total_w中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduPlatoonTotalWObjListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduPlatoonTotalWObj inputObj) {
        logger.info("[Enter dao method] {}-{}", "AppTEduPlatoonTotalWObjDao", "getAppTEduPlatoonTotalWObjListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_platoon_total_w" );
        sb.append(" where 1=1  ");
        
        getAppTEduPlatoonTotalWObjListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
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
    * 从数据库app_saas_v1的数据表app_t_edu_platoon_total_w中根据条件查询数据列表条件
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @param sb
    */
    public void getAppTEduPlatoonTotalWObjListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduPlatoonTotalWObj inputObj,
    StringBuffer sb) {
        logger.info("[Enter dao method] {}-{}", "AppTEduPlatoonTotalWObjDao", "getAppTEduPlatoonTotalWObjListCondition");
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
		
        //sb.append(" and start_use_date >= \""+startDate+"\"");
        //sb.append(" and start_use_date < \""+endDateAddOne +"\"");
        
        //startUseDate
       if(CommonUtil.isNotEmpty(inputObj.getStartUseDate())) {
        	sb.append(" and DATE_FORMAT(start_use_date,'yyyy-MM-dd') = \"" + inputObj.getStartUseDate()+"\"");
        }

        //endUseDate
        /*if(CommonUtil.isNotEmpty(inputObj.getEndUseDate())) {
        	sb.append(" and end_use_date = \"" + inputObj.getEndUseDate()+"\"");
        }*/

        //schoolId
        if(CommonUtil.isNotEmpty(inputObj.getSchoolId())) {
        	sb.append(" and school_id = \"" + inputObj.getSchoolId()+"\"");
        }

		//String schName, 
        //学校
        if(StringUtils.isNotEmpty(inputObj.getSchoolName())) {
        	sb.append(" and school_name like \"%" + inputObj.getSchoolName()+"%\"");
        }

        //departmentId
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentId())) {
        	sb.append(" and department_id = \"" + inputObj.getDepartmentId()+"\"");
        }
        
        //是否是总校
        if(inputObj.getSchGenBraFlag()!=null &&  inputObj.getSchGenBraFlag()!= -1) {
        	sb.append(" and is_branch_school = \"" + inputObj.getSchGenBraFlag()+"\"");
        }
        

		List<Object> distNamesList=CommonUtil.changeStringToList(inputObj.getDistNames());
    	List<Object> subLevelsList=CommonUtil.changeStringToList(inputObj.getSubLevels());
    	List<Object> compDepsList=CommonUtil.changeStringToList(inputObj.getCompDeps());
    	List<Object> schPropsList=CommonUtil.changeStringToList(inputObj.getSchProps());
    	List<Object> schTypesList=CommonUtil.changeStringToList(inputObj.getSchTypes());
    	List<Object> optModesList=CommonUtil.changeStringToList(inputObj.getOptModesList());
    	List<Object> subDistNamesList=CommonUtil.changeStringToList(inputObj.getSubDistNamesList());
    	List<Object> departmentIdList=CommonUtil.changeStringToList(inputObj.getDepartmentIdList());
    	
        if(departmentIdList !=null && departmentIdList.size()>0) {
        	String departmentIds= departmentIdList.toString().substring(1,departmentIdList.toString().length()-1);
        	if(departmentIds.indexOf("\"") <0) {
        		departmentIds = "\""+departmentIds.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and department_id in (" +departmentIds +")");
        }
        
    	
        if(distNamesList !=null && distNamesList.size()>0) {
        	String distIds= distNamesList.toString().substring(1,distNamesList.toString().length()-1);
        	if(distIds.indexOf("\"") <0) {
        		distIds = "\""+distIds.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and area in (" +distIds +")");
        }

        //area
        if(CommonUtil.isNotEmpty(inputObj.getArea())) {
        	sb.append(" and area = \"" + inputObj.getArea()+"\"");
        }

        //levelName
        if(CommonUtil.isNotEmpty(inputObj.getLevelId())) {
        	sb.append(" and level_name = \"" + inputObj.getLevelId()+"\"");
        }

        //schoolNatureName
        if(CommonUtil.isNotEmpty(inputObj.getSchoolNatureId())) {
        	sb.append(" and school_nature_name = \"" + inputObj.getSchoolNatureId()+"\"");
        }

        //schoolNatureSubName
        if(CommonUtil.isNotEmpty(inputObj.getSchoolNatureSubId())) {
        	sb.append(" and school_nature_sub_name = \"" + inputObj.getSchoolNatureSubId()+"\"");
        }
        
        //List<String> schProps,
        //过滤状态，多选
        if(schPropsList !=null && schPropsList.size()>0) {
        	String supplierAreas= schPropsList .toString().substring(1,schPropsList .toString().length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and school_nature_name in (" +supplierAreas +")");
        }
		
        //List<String> schTypes,
        //过滤状态，多选
        if(schTypesList !=null && schTypesList.size()>0) {
        	String supplierAreas= schTypesList.toString().substring(1,schTypesList.toString().length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and level_name in (" +supplierAreas +")");
        }

        //branchTotal
        if(inputObj.getBranchTotal() !=null && inputObj.getBranchTotal() != -1) {
        	sb.append(" and branch_total = \"" + inputObj.getBranchTotal()+"\"");
        }

        //departmentMasterId
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentMasterId())) {
        	sb.append(" and department_master_id = \"" + inputObj.getDepartmentMasterId()+"\"");
        }

        //departmentSlaveIdName
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentSlaveId())) {
        	sb.append(" and department_slave_id_name = \"" + inputObj.getDepartmentSlaveId()+"\"");
        }

        //schoolAreaId
        if(CommonUtil.isNotEmpty(inputObj.getSchoolAreaId())) {
        	sb.append(" and school_area_id = \"" + inputObj.getSchoolAreaId()+"\"");
        }
        
        //List<String> subLevels,
        //过滤状态，多选
        if(subLevelsList !=null && subLevelsList.size()>0) {
        	String supplierAreas= subLevelsList.toString().substring(1,subLevelsList.toString().length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and department_master_id in (" +supplierAreas +")");
        }
        
        //List<String> compDeps,
        //过滤状态，多选
        if(compDepsList !=null && compDepsList.size()>0) {
        	Map<String,String> compMap = new HashMap<String,String>();
        	//拆分compDeps格式 "["1_2","3_7"]"
        	for(Object objCompDep :compDepsList) {
        		if(objCompDep !=null) {
        			String[] compArr = objCompDep.toString().split("_");
        			
        			compMap.put(compArr[0], compArr[0]==null?compArr[1]:(compArr[1]));
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


        //List<String> subLevels,
        //过滤状态，多选
        if(subDistNamesList !=null && subDistNamesList.size()>0) {
        	String supplierAreas= subDistNamesList.toString().replace(" ", "");
        	supplierAreas = supplierAreas.substring(1,supplierAreas.length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and school_area_id in (" +supplierAreas +")");
        }
        
        //licenseMainType
        if(CommonUtil.isNotEmpty(inputObj.getLicenseMainType())) {
        	sb.append(" and license_main_type = \"" + inputObj.getLicenseMainType()+"\"");
        }

        
        //经营模式：经营模式（供餐模式，0：学校-自行加工  1：学校-食品加工商 2：外包-现场加工 3：外包-快餐配送 
        if(inputObj.getLicenseMainChild() !=null && inputObj.getLicenseMainChild() != -1) {
        	if(inputObj.getLicenseMainChild()==0 || inputObj.getLicenseMainChild() == 1) {
        		sb.append(" and license_main_type = \"0\" ");
        		/*if(inputObj.getLicenseMainChild() ==0) {
        			sb.append(" and license_main_child = 0 ");
        		}else if (inputObj.getLicenseMainChild() ==1) {
        			sb.append(" and license_main_child = 1 ");
        		}*/
        	}else if (inputObj.getLicenseMainChild()==2 || inputObj.getLicenseMainChild() == 3) {
        		sb.append(" and license_main_type = \"1\" ");
        		if(inputObj.getLicenseMainChild() ==2) {
        			sb.append(" and license_main_child = 0 ");
        		}else if (inputObj.getLicenseMainChild() ==3) {
        			sb.append(" and license_main_child = 1 ");
        		}
        	}
        	
        }
        
        //经营模式：多选
        if(optModesList!=null && optModesList.size() > 0) {
			sb.append(" AND (");
        	for(Object  optModeTemp : optModesList) {
		        int iOptMode = Integer.valueOf(optModeTemp.toString());
		        if(iOptMode==0 || iOptMode == 1) {
	        		sb.append("  license_main_type = \"0\" ");
	        		/*if(iOptMode ==0) {
	        			sb.append(" and license_main_child = 0 ) ");
	        		}else if (iOptMode ==1) {
	        			sb.append(" and license_main_child = 1 ) ");
	        		}else {
	        			sb.append("  ) ");
	        		}*/
	        	}else if (iOptMode==2 || iOptMode == 3) {
	        		sb.append(" (  license_main_type = \"1\" ");
	        		if(iOptMode ==2) {
	        			sb.append(" and license_main_child = 0 ) ");
	        		}else if (iOptMode ==3) {
	        			sb.append(" and license_main_child = 1 ) ");
	        		}else {
	        			sb.append("  ) ");
	        		}
	        	}
		        sb.append(" or ");
        	}
        	//去除最后一个or
        	sb.replace(sb.length()-3, sb.length(), "");
        	sb.append(") ");
    	}

        //haveClassTotal
        if(inputObj.getHaveClassTotal() !=null && inputObj.getHaveClassTotal() != -1) {
        	sb.append(" and have_class_total = \"" + inputObj.getHaveClassTotal()+"\"");
        }

        //havePlatoonTotal
        if(inputObj.getHavePlatoonTotal() !=null && inputObj.getHavePlatoonTotal() != -1) {
        	sb.append(" and have_platoon_total = \"" + inputObj.getHavePlatoonTotal()+"\"");
        }

        //haveNoPlatoonTotal
        if(inputObj.getHaveNoPlatoonTotal() !=null && inputObj.getHaveNoPlatoonTotal() != -1) {
        	sb.append(" and have_no_platoon_total = \"" + inputObj.getHaveNoPlatoonTotal()+"\"");
        }

        //guifanPlatoonTotal
        if(inputObj.getGuifanPlatoonTotal() !=null && inputObj.getGuifanPlatoonTotal() != -1) {
        	sb.append(" and guifan_platoon_total = \"" + inputObj.getGuifanPlatoonTotal()+"\"");
        }

        //buluPlatoonTotal
        if(inputObj.getBuluPlatoonTotal() !=null && inputObj.getBuluPlatoonTotal() != -1) {
        	sb.append(" and bulu_platoon_total = \"" + inputObj.getBuluPlatoonTotal()+"\"");
        }

        //yuqiPlatoonTotal
        if(inputObj.getYuqiPlatoonTotal() !=null && inputObj.getYuqiPlatoonTotal() != -1) {
        	sb.append(" and yuqi_platoon_total = \"" + inputObj.getYuqiPlatoonTotal()+"\"");
        }

        //noPlatoonTotal
        if(inputObj.getNoPlatoonTotal() !=null && inputObj.getNoPlatoonTotal() != -1) {
        	sb.append(" and no_platoon_total = \"" + inputObj.getNoPlatoonTotal()+"\"");
        }

        //address
        if(CommonUtil.isNotEmpty(inputObj.getAddress())) {
        	sb.append(" and address = \"" + inputObj.getAddress()+"\"");
        }

        //foodSafetyPersion
        if(CommonUtil.isNotEmpty(inputObj.getFoodSafetyPersion())) {
        	sb.append(" and food_safety_persion = \"" + inputObj.getFoodSafetyPersion()+"\"");
        }

        //foodSafetyMobilephone
        if(CommonUtil.isNotEmpty(inputObj.getFoodSafetyMobilephone())) {
        	sb.append(" and food_safety_mobilephone = \"" + inputObj.getFoodSafetyMobilephone()+"\"");
        }

        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
    }

	
    //查询排菜异常数据
    public List<AppCommonDao> getSendSteakDataAnomalyWarning(String startDate,String endDate,float offset,String supply_date,String departmentId){
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	StringBuffer sb = new StringBuffer();
        sb.append(" select  to_date(supply_date) supply_date,school_name,department_slave_id_name,menu_group_name,cater_type_name,dishes_name,meals_count,dishes_number ");
        sb.append(" from app_t_edu_dish_menu ");
        sb.append(" where 1=1 ");
        sb.append(" and supply_date='"+supply_date+"' ");
        if(departmentId !=null && !departmentId.trim().isEmpty()) {
        sb.append(" and department_slave_id_name='"+departmentId+"' ");
        }
        List<String> listYearMonth = CommonUtil.getYearMonthList(startDate, endDate);
        new ToolUtil().yearMonth(listYearMonth,sb);
        sb.append(" and (dishes_number/meals_count < "+(1-offset)+" OR dishes_number/meals_count >"+(1+offset)+")");
        logger.info(sb);
		return jdbcTemplateTemp.query(sb.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("supplyDate", rs.getString("supply_date"));
				commonMap.put("schoolName", rs.getString("school_name"));
				commonMap.put("departmentSlaveIdName", rs.getString("department_slave_id_name"));
				commonMap.put("menuGroupName", rs.getString("menu_group_name"));
				commonMap.put("caterTypeName", rs.getString("cater_type_name"));
				commonMap.put("dishesName", rs.getString("dishes_name"));
				commonMap.put("mealsCount", rs.getLong("meals_count"));
				commonMap.put("dishesNumber", rs.getLong("dishes_number"));
				return new AppCommonDao(commonMap);
			}
		});
    }
    //查询验收异常数据
    public List<AppCommonDao> getSendAcceptanceDataAnomalyWarning(String startDate,String endDate,String deliveryWarn,String use_date,String departmentId){

    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	//获取开始日期、结束日期的年月集合
    	List<String> listYearMonth = CommonUtil.getYearMonthList(startDate, endDate);
		
    	StringBuffer sb = new StringBuffer();
        sb.append(" select  to_date(use_date) use_date,school_name,ware_batch_no,name,other_quantity,delivery_number,ledger_type,haul_status,supplier_material_units ");
        sb.append(" from app_t_edu_ledege_detail ");
        sb.append(" where 1=1 ");
        sb.append(" and to_date(use_date)='"+use_date+"' ");
        if(departmentId !=null && !departmentId.trim().isEmpty()) {
        	sb.append(" and department_slave_id_name='"+departmentId+"' ");
        }
        
//        sb.append(" and haul_status !=3 ");
        new ToolUtil().yearMonth(listYearMonth,sb);
//        sb.append(" and (other_quantity/delivery_number <0.8 OR other_quantity/delivery_number >1.2)");
        logger.info(sb);
		return jdbcTemplateTemp.query(sb.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("useDate", rs.getString("use_date"));
				commonMap.put("schoolName", rs.getString("school_name"));
				commonMap.put("wareBatchNo", rs.getString("ware_batch_no"));
				commonMap.put("name", rs.getString("name"));
				commonMap.put("otherQuantity", rs.getBigDecimal("other_quantity"));
				commonMap.put("deliveryNumber", rs.getBigDecimal("delivery_number"));
				commonMap.put("ledgerType", rs.getLong("ledger_type"));
				commonMap.put("haul_status", rs.getLong("haul_status"));
				commonMap.put("supplier_material_units", rs.getString("supplier_material_units"));
				return new AppCommonDao(commonMap);
			}
		});
    	
    }
}
