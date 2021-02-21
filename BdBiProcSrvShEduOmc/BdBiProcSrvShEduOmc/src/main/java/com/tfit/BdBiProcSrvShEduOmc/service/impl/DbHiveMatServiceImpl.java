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

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.config.DataKeyConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.PpMatCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduMaterialTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchMatCommon;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveMatService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 用料相关hive库的查询
 * @author Administrator
 *
 */
@Service
public class DbHiveMatServiceImpl implements DbHiveMatService {
	private static final Logger logger = LogManager.getLogger(DbHiveMatServiceImpl.class.getName());
	
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
  	
  	//--------------------用料-汇总----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_material_total中根据条件查询数据列表
  	 * statMode:统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
  	 */
    public List<SchMatCommon> getMatList(String tableName,List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,List<Object> distIdList,
    		int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,String departmentId,List<Object> departmentIdList,
    		Integer statMode ) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append("select from_unixtime(unix_timestamp(use_date,'yyyy-MM-dd'),'yyyy/MM/dd') useDate , ");
        sb.append(" area,total, ");
        if("app_t_edu_platoon_total_d".equals(tableName)){
        	sb.append(" total schoolTotal,platoon_deal_status status ,");
        }else {
        	sb.append(" school_total schoolTotal,status ,");
        }
        
        sb.append(" level_name levelName,school_nature_name schoolNatureName, ");
        sb.append(" department_master_id departmentMasterId,department_slave_id_name departmentSlaveIdName ");
        sb.append(" from " + tableName );
        sb.append(" where  1=1 ");
        if(statMode == 0) {
        	//按区统计时，其他三个属性需要为空，数据是重叠的
	        sb.append(" and (level_name is null or level_name = \"null\" or level_name = \"NULL\" )  ");
	        sb.append(" and (school_nature_name is null or school_nature_name = \"null\" or school_nature_name = \"NULL\" ) ");
	        sb.append(" and (department_master_id is null or department_master_id = \"null\" or department_master_id = \"NULL\" ) ");
	        sb.append(" and (department_slave_id_name is null or department_slave_id_name = \"null\" or department_slave_id_name = \"NULL\" ) ");
	        
	        if(DataKeyConfig.talbePlatoonTotal.equals(tableName)) {
	        	
	        }else if (DataKeyConfig.talbeMaterialTotalD.equals(tableName)) {
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
	        }else if (DataKeyConfig.talbeMaterialTotalD.equals(tableName)) {
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
 	        }else if (DataKeyConfig.talbeMaterialTotalD.equals(tableName)) {
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
        		
 	        }else if (DataKeyConfig.talbeMaterialTotalD.equals(tableName)) {
 	        	//管理部门
 	            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
 	            	
 	            }else {
 	            	sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
 	            }
 	        }
        }else if(statMode == 4) {
        	if(DataKeyConfig.talbePlatoonTotal.equals(tableName)) {
        		
 	        }else if (DataKeyConfig.talbeMaterialTotalD.equals(tableName)) {
 	        	//按管理部门统计时，其他四个属性需要为空，数据是重叠的
 	        	sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
 		        sb.append(" and (level_name is null or level_name = \"null\" or level_name = \"NULL\" )  ");
 		        sb.append(" and (school_nature_name is null or school_nature_name = \"null\" or school_nature_name = \"NULL\" ) ");
 		        sb.append(" and (department_master_id is null or department_master_id = \"null\" or department_master_id = \"NULL\" ) ");
 		        sb.append(" and (department_slave_id_name is null or department_slave_id_name = \"null\" or department_slave_id_name = \"NULL\" ) ");
 	        }
        }
        
        getMatCondition(listYearMonth, startDate, endDateAddOne,
        		distId,distIdList,
        		subLevel,compDep,subLevels,compDeps,
        		departmentId,departmentIdList,
        		sb);
        logger.info("执行sql:"+sb.toString());
		return (List<SchMatCommon>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<SchMatCommon>() {
			@Override
			public SchMatCommon mapRow(ResultSet rs, int rowNum) throws SQLException {
				SchMatCommon cdsd = new SchMatCommon();
				cdsd.setMatDate(rs.getString("useDate"));
				cdsd.setDistId(rs.getString("area"));
				//用料条数
				cdsd.setTotal(0);
				if(StringUtils.isNotEmpty(rs.getString("total")) && rs.getInt("total")>0) {
	            	cdsd.setTotal(rs.getInt("total"));
	            }
				//学校数量
				cdsd.setSchoolTotal(0);
				if(StringUtils.isNotEmpty(rs.getString("schoolTotal")) && rs.getInt("schoolTotal")>0) {
	            	cdsd.setSchoolTotal(rs.getInt("schoolTotal"));
	            }
				
				cdsd.setStatus(null);
				if(CommonUtil.isNotEmpty(rs.getString("status"))) {
					cdsd.setStatus(rs.getInt("status"));
				}
				
	            cdsd.setLevelName(rs.getString("levelName"));
	            cdsd.setSchoolNatureName(rs.getString("schoolNatureName"));
	            cdsd.setDepartmentMasterId(rs.getString("departmentMasterId"));
	            cdsd.setDepartmentSlaveIdName(rs.getString("departmentSlaveIdName"));
				
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
     * @param MatName
     * @param rmcName
     * @param distName
     * @param caterType
     * @param schType
     * @param schProp
     * @param optMode
     * @param menuName
     * @param sb
     */
	private void getMatCondition(List<String> listYearMonth, String startDate, String endDateAddOne,
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
	
  	//--------------------用料-详情----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_material_detail中根据条件查询数据列表
  	 */
    public List<PpMatCommonDets> getMatDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,String ppName, String rmcId,String rmcName, 
			int subLevel, int compDep, int schGenBraFlag, String subDistName, int fblMb, 
			int confirmFlag, int schType, int schProp, int optMode, int matType,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			String departmentId,List<Object> deparmentIds,
    		Integer startNum,Integer endNum) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append(" select from_unixtime(unix_timestamp(use_date,'yyyy-MM-dd'),'yyyy/MM/dd') useDate,is_branch_school schGenBraFlag,branch_total braCampusNum, ");
        sb.append(" parent_id relGenSchName,department_master_id subLevel,department_slave_id_name compDep,school_area_id subDistName, ");
        sb.append(" school_nature_name schProp, area area, ");
        sb.append(" school_name ppName, supplier_name rmcName,level_name schType, ");
        sb.append(" type matCategory,status ,");
        sb.append(" supplier_id schSupplierId,license_main_type fblMb, license_main_child optMode,department_id departmentId ");
        sb.append(" from app_t_edu_material_detail" );
        sb.append(" where  1=1 ");
        
        getMatDetsListCondition(listYearMonth,startDate,endDateAddOne,
        		distIdorSCName,ppName,rmcId,rmcName, 
    			subLevel,compDep,schGenBraFlag,subDistName,fblMb, 
    			confirmFlag,schType,schProp,optMode,matType,
    			distNames,subLevels,compDeps,schProps,schTypes,
    			optModesList,subDistNamesList,
    			departmentId,deparmentIds,
    			sb);
        if(startNum !=null && endNum !=null) {
        	sb.append(" limit "+endNum);
        }
        logger.info("执行sql:"+sb.toString());
        logger.info("******************jdbcTemplateHive:"+jdbcTemplateHive);
		return (List<PpMatCommonDets>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<PpMatCommonDets>() {
			@Override
			public PpMatCommonDets mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && endNum!=null && startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
				
				PpMatCommonDets cdsd = new PpMatCommonDets();
				cdsd.setSendFlag(0);
				cdsd.setMatUseDate(rs.getString("useDate"));
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
				//用料类别：原料、成品菜
				cdsd.setMatCategory("-");
	            if(CommonUtil.isNotEmpty(rs.getString("matCategory"))) {
	            	cdsd.setMatCategory(AppModConfig.dispTypeIdToNameMap.get(rs.getInt("matCategory")));
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
			  			}
			  		}else {
		  				cdsd.setOptMode("");
		  			}
				}
				
				//团餐公司名称
	            cdsd.setRmcName("-");
	            if(CommonUtil.isNotEmpty(rs.getString("rmcName"))) {
	            	cdsd.setRmcName(rs.getString("rmcName"));
	            }
	            
				//是否用料确认：0：未确认 1：已确认（界面展示）
				cdsd.setConfirmFlag(0);
	            if(CommonUtil.isNotEmpty(rs.getString("status"))) {
	            	cdsd.setConfirmFlag(rs.getString("status").equals("2")?1:0);
	            }
	            
				//区域
				cdsd.setDepartmentId("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentId"))) {
		            	cdsd.setDepartmentId(rs.getString("departmentId"));
		        }
				return cdsd;
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_material_detail中根据条件查询数据条数
     */
    public Integer getMatDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,String schName,String rmcId,String rmcName, 
			int subLevel, int compDep, int schGenBraFlag, String subDistName, int fblMb, 
			int confirmFlag, int schType, int schProp, int optMode, int matType,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
			String departmentId,List<Object> deparmentIds
			) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_material_detail" );
        sb.append(" where 1=1  ");
        
        getMatDetsListCondition(listYearMonth,startDate,endDateAddOne,
        		distIdorSCName,schName,rmcId,rmcName, 
    			subLevel,compDep,schGenBraFlag,subDistName,fblMb, 
    			confirmFlag,schType,schProp,optMode,matType,
    			distNames,subLevels,compDeps,schProps,schTypes,
    			optModesList,subDistNamesList,
    			departmentId,deparmentIds,
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
	private void getMatDetsListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,String schName,String rmcId,String rmcName, 
			int subLevel, int compDep, int schGenBraFlag, String subDistName, int fblMb, 
			int confirmFlag, int schType, int schProp, int optMode, int matType,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			List<Object> optModesList,List<Object> subDistNamesList,
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
        	
		//String schName, 
        //学校
        if(StringUtils.isNotEmpty(schName)) {
        	sb.append(" and school_name like \"%" + schName+"%\"");
        }
		//int schType, 
        //学校学段
        if(schType != -1) {
        	sb.append(" and level_name = " + schType);
        }
        
		//int subLevel, 
        //用料类别： 0 原料  1 成品菜
        if(matType != -1) {
        	sb.append(" and type = " + matType);
        }
        
        //confirmFlag 0:未确认 1：已确认 
        //数据库中status：0 信息不完整 1待确认  2 已确认'
        if(confirmFlag != -1) {
        	if(confirmFlag == 0) {
        		sb.append(" and (status = 0 or status = 1)  ");
        	}else if (confirmFlag == 1) {
        		sb.append(" and status = 2 ");
        	}
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
        
	}

	//--------------------用料-周报表----------------------------------------------

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_material_total_w中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduMaterialTotalWObj> getAppTEduMaterialTotalWObjList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduMaterialTotalWObj inputObj,
    		Integer startNum,Integer endNum) {
        logger.info("[Enter dao method] {}-{}", "AppTEduMaterialTotalWObjDao", "getAppTEduMaterialTotalWObjList");
        Long daoStartTime = System.currentTimeMillis();
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
        sb.append("  license_main_type licenseMainType, license_main_child licenseMainChild, material_day_total materialDayTotal, have_material_day_total haveMaterialDayTotal, ");
        sb.append("  no_material_day_total noMaterialDayTotal, material_total materialTotal, have_material_total haveMaterialTotal, no_material_total noMaterialTotal, ");
        sb.append("  address address, food_safety_persion foodSafetyPersion, food_safety_mobilephone foodSafetyMobilephone ");
        sb.append(" from app_t_edu_material_total_w " );
        sb.append(" where  1=1 ");
        getAppTEduMaterialTotalWObjListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+endNum);
        }        logger.info("执行sql:"+sb.toString());
		return (List<AppTEduMaterialTotalWObj>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<AppTEduMaterialTotalWObj>() {
			@Override
			public AppTEduMaterialTotalWObj mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				if(startNum!=null && endNum!=null && startNum!=-1 &&  endNum != -1 
						&& (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
								AppTEduMaterialTotalWObj obj = new AppTEduMaterialTotalWObj();

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
				obj.setMaterialDayTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("materialDayTotal"))) {
					obj.setMaterialDayTotal(rs.getInt("materialDayTotal"));
				}
				obj.setHaveMaterialDayTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("haveMaterialDayTotal"))) {
					obj.setHaveMaterialDayTotal(rs.getInt("haveMaterialDayTotal"));
				}
				obj.setNoMaterialDayTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("noMaterialDayTotal"))) {
					obj.setNoMaterialDayTotal(rs.getInt("noMaterialDayTotal"));
				}
				obj.setMaterialTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("materialTotal"))) {
					obj.setMaterialTotal(rs.getInt("materialTotal"));
				}
				obj.setHaveMaterialTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("haveMaterialTotal"))) {
					obj.setHaveMaterialTotal(rs.getInt("haveMaterialTotal"));
				}
				obj.setNoMaterialTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("noMaterialTotal"))) {
					obj.setNoMaterialTotal(rs.getInt("noMaterialTotal"));
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
				logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
				return obj;
			}
		});
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_material_total_w中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduMaterialTotalWObjListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduMaterialTotalWObj inputObj) {
        logger.info("[Enter dao method] {}-{}", "AppTEduMaterialTotalWObjDao", "getAppTEduMaterialTotalWObjListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_material_total_w" );
        sb.append(" where 1=1  ");
        
        getAppTEduMaterialTotalWObjListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
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
    * 从数据库app_saas_v1的数据表app_t_edu_material_total_w中根据条件查询数据列表条件
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @param sb
    */
    public void getAppTEduMaterialTotalWObjListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduMaterialTotalWObj inputObj,
    StringBuffer sb) {
        logger.info("[Enter dao method] {}-{}", "AppTEduMaterialTotalWObjDao", "getAppTEduMaterialTotalWObjListCondition");
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
		
 /*       sb.append(" and expect_receive_date >= \""+startDate+"\"");
        sb.append(" and expect_receive_date < \""+endDateAddOne +"\"");
        
        //startUseDate
        if(CommonUtil.isNotEmpty(inputObj.getStartUseDate())) {
        	sb.append(" and start_use_date = \"" + inputObj.getStartUseDate()+"\"");
        }

        //endUseDate
        if(CommonUtil.isNotEmpty(inputObj.getEndUseDate())) {
        	sb.append(" and end_use_date = \"" + inputObj.getEndUseDate()+"\"");
        }*/
		
		if(CommonUtil.isNotEmpty(inputObj.getStartUseDate())) {
        	sb.append(" and DATE_FORMAT(start_use_date,'yyyy-MM-dd') = \"" + inputObj.getStartUseDate()+"\"");
		}

        //schoolId
        if(CommonUtil.isNotEmpty(inputObj.getSchoolId())) {
        	sb.append(" and school_id = \"" + inputObj.getSchoolId()+"\"");
        }

      //schoolName
        if(StringUtils.isNotEmpty(inputObj.getSchoolName())) {
        	sb.append(" and school_name like \"%" + inputObj.getSchoolName()+"%\"");
        }

        //departmentId
        if(CommonUtil.isNotEmpty(inputObj.getDepartmentId())) {
        	sb.append(" and department_id = \"" + inputObj.getDepartmentId()+"\"");
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

        //licenseMainType
        if(CommonUtil.isNotEmpty(inputObj.getLicenseMainType())) {
        	sb.append(" and license_main_type = \"" + inputObj.getLicenseMainType()+"\"");
        }

        //licenseMainChild
        /*if(inputObj.getLicenseMainChild() !=null && inputObj.getLicenseMainChild() != -1) {
        	sb.append(" and license_main_child = \"" + inputObj.getLicenseMainChild()+"\"");
        }*/

        //materialDayTotal
        if(inputObj.getMaterialDayTotal() !=null && inputObj.getMaterialDayTotal() != -1) {
        	sb.append(" and material_day_total = \"" + inputObj.getMaterialDayTotal()+"\"");
        }

        //haveMaterialDayTotal
        if(inputObj.getHaveMaterialDayTotal() !=null && inputObj.getHaveMaterialDayTotal() != -1) {
        	sb.append(" and have_material_day_total = \"" + inputObj.getHaveMaterialDayTotal()+"\"");
        }

        //noMaterialDayTotal
        if(inputObj.getNoMaterialDayTotal() !=null && inputObj.getNoMaterialDayTotal() != -1) {
        	sb.append(" and no_material_day_total = \"" + inputObj.getNoMaterialDayTotal()+"\"");
        }

        //materialTotal
        if(inputObj.getMaterialTotal() !=null && inputObj.getMaterialTotal() != -1) {
        	sb.append(" and material_total = \"" + inputObj.getMaterialTotal()+"\"");
        }

        //haveMaterialTotal
        if(inputObj.getHaveMaterialTotal() !=null && inputObj.getHaveMaterialTotal() != -1) {
        	sb.append(" and have_material_total = \"" + inputObj.getHaveMaterialTotal()+"\"");
        }

        //noMaterialTotal
        if(inputObj.getNoMaterialTotal() !=null && inputObj.getNoMaterialTotal() != -1) {
        	sb.append(" and no_material_total = \"" + inputObj.getNoMaterialTotal()+"\"");
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
        
        List<Object> distNamesList=CommonUtil.changeStringToList(inputObj.getDistNames());
    	List<Object> subLevelsList=CommonUtil.changeStringToList(inputObj.getSubLevels());
    	List<Object> compDepsList=CommonUtil.changeStringToList(inputObj.getCompDeps());
    	List<Object> schPropsList=CommonUtil.changeStringToList(inputObj.getSchProps());
    	List<Object> schTypesList=CommonUtil.changeStringToList(inputObj.getSchTypes());
    	List<Object> optModesList=CommonUtil.changeStringToList(inputObj.getOptModesList());
    	List<Object> subDistNamesList=CommonUtil.changeStringToList(inputObj.getSubDistNamesList());
    	List<Object> departmentIdList=CommonUtil.changeStringToList(inputObj.getDepartmentIdList());
    	
        //是否是总校
        if(inputObj.getSchGenBraFlag()!=null &&  inputObj.getSchGenBraFlag()!= -1) {
        	sb.append(" and is_branch_school = \"" + inputObj.getSchGenBraFlag()+"\"");
        }
        
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

    }

}
