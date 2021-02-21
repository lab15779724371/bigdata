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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.GsPlanOptCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduLedgerMasterTotalWObj;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.SchGsCommon;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveGsService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 排菜相关hive库的查询
 * @author Administrator
 *
 */
@Service
public class DbHiveGsServiceImpl implements DbHiveGsService {
	private static final Logger logger = LogManager.getLogger(DbHiveGsServiceImpl.class.getName());
	
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
  	
  	//--------------------配货-汇总----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_ledger_master_total中根据条件查询数据列表
  	 * statMode:统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
  	 */
    public List<SchGsCommon> getGsList(String tableName,List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,List<Object> distIdList,
    		int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
    		String departmentId,List<Object> departmentIdList,
    		Integer statMode ) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append("select from_unixtime(unix_timestamp(action_date,'yyyy-MM-dd'),'yyyy/MM/dd') actionDate , ");
        sb.append(" area,total,school_total schoolTotal,haul_status haulStatus, ");
        sb.append(" level_name levelName,school_nature_name schoolNatureName, ");
        sb.append(" department_master_id departmentMasterId,department_slave_id_name departmentSlaveIdName,dis_deal_status disSealStatus,department_id departmentId ");
        sb.append(" from  " + tableName );
        sb.append(" where  1=1 ");
        if(statMode == 0) {
        	//按区统计时，其他三个属性需要为空，数据是重叠的
	        sb.append(" and (level_name is null or level_name = \"null\" or level_name = \"NULL\" )  ");
	        sb.append(" and (school_nature_name is null or school_nature_name = \"null\" or school_nature_name = \"NULL\" ) ");
	        sb.append(" and (department_master_id is null or department_master_id = \"null\" or department_master_id = \"NULL\" ) ");
	        sb.append(" and (department_slave_id_name is null or department_slave_id_name = \"null\" or department_slave_id_name = \"NULL\" ) ");
	        
	        if(DataKeyConfig.talbeLedgerMasterTotal.equals(tableName)) {
	        	
	        }else if (DataKeyConfig.talbeLedgerMasterTotalD.equals(tableName)) {
		        //管理部门
	            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
	            	
	            }else {
	            	sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
	            }
	        }
        }else if (statMode == 1) {
        	sb.append(" and school_nature_name != \"NULL\"  ");
            if(DataKeyConfig.talbeLedgerMasterTotal.equals(tableName)) {
            	//如果按区域过滤
                if(StringUtils.isNotEmpty(distId) || (distIdList !=null && distIdList.size() >0)) {
                	
                }else {
                	sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
                }
	        }else if (DataKeyConfig.talbeLedgerMasterTotalD.equals(tableName)) {
	        	//管理部门
	            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
	            	
	            }else {
	            	sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
	            }
	            
	            sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
	        }
            
        }else if (statMode == 2) {
        	sb.append(" and level_name != \"NULL\"  ");
            
            if(DataKeyConfig.talbeLedgerMasterTotal.equals(tableName)) {
             	//如果按区域过滤
                 if(StringUtils.isNotEmpty(distId) || (distIdList !=null && distIdList.size() >0)) {
                 	
                 }else {
                 	sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
                 }
 	        }else if (DataKeyConfig.talbeLedgerMasterTotalD.equals(tableName)) {
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
        	
        	if(DataKeyConfig.talbeLedgerMasterTotal.equals(tableName)) {
        		
 	        }else if (DataKeyConfig.talbeLedgerMasterTotalD.equals(tableName)) {
 	        	//管理部门
 	            if(StringUtils.isNotEmpty(departmentId) || (departmentIdList !=null && departmentIdList.size() >0)) {
 	            	
 	            }else {
 	            	sb.append(" and (department_id is null or department_id = \"null\" or department_id = \"NULL\"  or department_id = -1)  ");
 	            }
 	        }
        }else if(statMode == 4) {
        	if(DataKeyConfig.talbeLedgerMasterTotal.equals(tableName)) {
        		
 	        }else if (DataKeyConfig.talbeLedgerMasterTotalD.equals(tableName)) {
 	        	//按管理部门统计时，其他四个属性需要为空，数据是重叠的
 	        	sb.append(" and (area is null or area = \"null\" or area = \"NULL\" )  ");
 		        sb.append(" and (level_name is null or level_name = \"null\" or level_name = \"NULL\" )  ");
 		        sb.append(" and (school_nature_name is null or school_nature_name = \"null\" or school_nature_name = \"NULL\" ) ");
 		        sb.append(" and (department_master_id is null or department_master_id = \"null\" or department_master_id = \"NULL\" ) ");
 		        sb.append(" and (department_slave_id_name is null or department_slave_id_name = \"null\" or department_slave_id_name = \"NULL\" ) ");
 	        }
        	
        }
        
        getGsCondition(listYearMonth, startDate, endDateAddOne,
        		distId,distIdList,
        		subLevel,compDep,subLevels,compDeps,departmentId,departmentIdList,sb);
        logger.info("执行sql:"+sb.toString());
		return (List<SchGsCommon>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<SchGsCommon>() {
			@Override
			public SchGsCommon mapRow(ResultSet rs, int rowNum) throws SQLException {
				SchGsCommon cdsd = new SchGsCommon();
				cdsd.setActionDate(rs.getString("actionDate"));
				cdsd.setDistId(rs.getString("area"));
				//配货单数量
				cdsd.setTotal(0);
				if(StringUtils.isNotEmpty(rs.getString("total")) && rs.getInt("total")>0) {
	            	cdsd.setTotal(rs.getInt("total"));
	            }
				//学校数量
				cdsd.setSchoolTotal(0);
				if(StringUtils.isNotEmpty(rs.getString("schoolTotal")) && rs.getInt("schoolTotal")>0) {
	            	cdsd.setSchoolTotal(rs.getInt("schoolTotal"));
	            }
				cdsd.setHaulStatus(null);
				if(CommonUtil.isNotEmpty(rs.getString("haulStatus"))) {
					cdsd.setHaulStatus(rs.getInt("haulStatus"));
				}
	            cdsd.setLevelName(rs.getString("levelName"));
	            cdsd.setSchoolNatureName(rs.getString("schoolNatureName"));
	            cdsd.setDepartmentMasterId(rs.getString("departmentMasterId"));
	            cdsd.setDepartmentSlaveIdName(rs.getString("departmentSlaveIdName"));
	            
	            cdsd.setDisSealStatus(null);
				if(CommonUtil.isNotEmpty(rs.getString("disSealStatus"))) {
					cdsd.setDisSealStatus(rs.getString("disSealStatus"));
				}
				cdsd.setDepartmentId(rs.getString("departmentId"));
				return cdsd;
			}
		});
    }

    /**
     * 配货汇总查询条件（查询列表和查询总数共用）
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
	private void getGsCondition(List<String> listYearMonth, String startDate, String endDateAddOne,
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
		
        sb.append(" and DATE_FORMAT(action_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(action_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
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
        
        //管理部门
        if(StringUtils.isNotEmpty(departmentId)) {
        	sb.append(" and department_id = \"" + departmentId+"\"");
        }
        
        if(departmentIdList !=null && departmentIdList.size()>0) {
        	String distIds= departmentIdList.toString().substring(1,departmentIdList.toString().length()-1);
        	if(distIds.indexOf("\"") <0) {
        		distIds = "\""+distIds.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and department_id in (" +distIds +")");
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
	
  	//--------------------配货-详情----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_ledger_master_detail中根据条件查询数据列表
  	 */
    public List<GsPlanOptCommonDets> getGsDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp,
			String schoolId,String ppName,String rmcId, String rmcName, int schType, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			String distrBatNumber, int assignStatus, int dispStatus, int acceptStatus,
	    	List<Object> dispStatussList,List<Object> assignStatussList, int dispType, int dispMode,
	    	String departmentId,List<Object> departmentIdList,String plastatus,
	    	List<String> distrBatNumberList,
	    	int mode,
    		Integer startNum,Integer endNum) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append(" select from_unixtime(unix_timestamp(action_date,'yyyy-MM-dd'),'yyyy/MM/dd') actionDate,");
        if(mode==-1 || mode==0) {
	        sb.append(" parent_id relGenSchName,department_master_id subLevel,department_slave_id_name compDep,school_area_id subDistName, ");
	        sb.append(" is_branch_school schGenBraFlag,branch_total braCampusNum,school_nature_name schProp, area area, ");
	        sb.append(" school_name ppName, supplier_name rmcName,level_name schType, ");
	        sb.append(" supplier_id schSupplierId,license_main_type fblMb, ");
	        sb.append(" haul_status haulStatus,ledger_type ledgerType,delivery_attr deliveryAttr, ");
	        sb.append(" from_unixtime(unix_timestamp(delivery_date,'yyyy-MM-dd HH:mm:ss'),'yyyy/MM/dd HH:mm:ss') deliveryDate, ");
        }
        sb.append(" ware_batch_no wareBatchNo,dis_deal_status disDealStatus,department_id departmentId ");
        sb.append(" from app_t_edu_ledger_master_detail" );
        sb.append(" where  1=1 ");
        
        getGsDetsListCondition(listYearMonth, startDate, endDateAddOne, distIdorSCName, subLevel,
        		compDep, schGenBraFlag, subDistName, fblMb, schProp,schoolId, ppName, rmcId, rmcName, 
        		schType,sendFlag, distNames, subLevels, compDeps, schProps, 
        		schTypes,
        		distrBatNumber,assignStatus,dispStatus,acceptStatus,
    	    	dispStatussList,assignStatussList,dispType, dispMode,
    	    	departmentId,departmentIdList,plastatus,distrBatNumberList,
        		sb);
        if(startNum !=null && endNum !=null && startNum >=0 && endNum>=0 && endNum>=startNum) {
        	sb.append(" limit "+endNum);
        }
        logger.info("执行sql:"+sb.toString());
		return (List<GsPlanOptCommonDets>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<GsPlanOptCommonDets>() {
			@Override
			public GsPlanOptCommonDets mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && endNum!=null  && startNum >=0 && endNum>=0  && endNum>=startNum 
						&& startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
				
				GsPlanOptCommonDets cdsd = new GsPlanOptCommonDets();
				cdsd.setDistrDate(rs.getString("actionDate"));
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
					//食品经营许可证主体
					cdsd.setFblMb("-");
					if(CommonUtil.isNotEmpty(rs.getString("fblMb")) && CommonUtil.isInteger(rs.getString("fblMb"))) {
						cdsd.setFblMb(AppModConfig.fblMbIdToNameMap.get(Integer.parseInt(rs.getString("fblMb"))));
					}
					
					//团餐公司名称
		            cdsd.setRmcName("-");
		            if(CommonUtil.isNotEmpty(rs.getString("rmcName"))) {
		            	cdsd.setRmcName(rs.getString("rmcName"));
		            }
		            
		        	//验收时间
		        	cdsd.setAcceptTime("");
					if(CommonUtil.isNotEmpty(rs.getString("deliveryDate"))) {
						cdsd.setAcceptTime(rs.getString("deliveryDate"));
					}
			    	//配送类型，0:原料，1:成品菜
			    	cdsd.setDispType("0");
					if(CommonUtil.isNotEmpty(rs.getString("ledgerType"))) {
						//转换 hive数据库中：1 原料 2 成品菜
						cdsd.setDispType("1".equals(rs.getString("ledgerType"))?"0":"1");
					}
			    	//配送方式，0:统配，1:直配
			    	cdsd.setDispMode("0");
					if(CommonUtil.isNotEmpty(rs.getString("deliveryAttr"))) {
						//转换 hive数据库中：1 原料 2 成品菜
						cdsd.setDispMode(("0".equals(rs.getString("deliveryAttr")) || "2".equals(rs.getString("deliveryAttr")))?"0":"1");
					}
					
					//配送状态
					cdsd.setHaulStatus(-2);
					if(CommonUtil.isNotEmpty(rs.getString("haulStatus"))&& CommonUtil.isInteger(rs.getString("haulStatus"))) {
						cdsd.setHaulStatus(rs.getInt("haulStatus"));
					}
			    	//发送状态，0:未发送，1:已发送
			    	cdsd.setSendFlag(0);
			    	
		        	//管理部门
		        	cdsd.setDepartmentId("");
		            if(CommonUtil.isNotEmpty(rs.getString("departmentId"))) {
		            	cdsd.setDepartmentId(rs.getString("departmentId"));
		            }
				 }
				//批次号
		    	cdsd.setDistrBatNumber("-");
				if(CommonUtil.isNotEmpty(rs.getString("wareBatchNo"))) {
					cdsd.setDistrBatNumber((rs.getString("wareBatchNo")));
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
     * 从数据库app_saas_v1的数据表app_t_edu_ledger_master_detail中根据条件查询数据条数
     */
    public Integer getGsDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp,
			String schoolId,String ppName,String rmcId, String rmcName, int schType, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			String distrBatNumber, int assignStatus, int dispStatus, int acceptStatus,
	    	List<Object> dispStatussList,List<Object> assignStatussList, int dispType, int dispMode,
	    	String departmentId,List<Object> departmentIdList,String plastatus,List<String> distrBatNumberList
			) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_ledger_master_detail" );
        sb.append(" where 1=1  ");
        
        getGsDetsListCondition(listYearMonth, startDate, endDateAddOne, distIdorSCName, subLevel, compDep,
        		schGenBraFlag, subDistName, fblMb, schProp,schoolId, ppName, rmcId, rmcName, schType,
        		 sendFlag, distNames, subLevels, compDeps, schProps, schTypes,
     			distrBatNumber,assignStatus,dispStatus,acceptStatus,
    	    	dispStatussList,assignStatussList,dispType, dispMode,
    	    	departmentId,departmentIdList,plastatus,distrBatNumberList,
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
	private void getGsDetsListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,int subLevel, 
			int compDep, int schGenBraFlag, String subDistName, int fblMb, int schProp,
			String schoolId,String schName,String rmcId, String rmcName, int schType, int sendFlag,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			String distrBatNumber, int assignStatus, int dispStatus, int acceptStatus,
	    	List<Object> dispStatussList,List<Object> assignStatussList,
	    	int dispType, int dispMode,
	    	String departmentId,List<Object> departmentIdList,String plastatus,
	    	List<String> distrBatNumberList,
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
		
        sb.append(" and DATE_FORMAT(action_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and DATE_FORMAT(action_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
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
        
        //操作状态
        if(CommonUtil.isNotEmpty(plastatus)) {
        	sb.append(" and dis_deal_status = \"" + plastatus+"\"");
        }
        
        if(distNames !=null && distNames.size()>0) {
        	String distIds= distNames.toString().substring(1,distNames.toString().length()-1);
        	if(distIds.indexOf("\"") <0) {
        		distIds = "\""+distIds.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and area in (" +distIds +")");
        }
        	
		//String schoolId, 
        //学校
        if(StringUtils.isNotEmpty(schoolId)) {
        	sb.append(" and school_id = \"" + schoolId+"\"");
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
        
        //是否是总校
        if(schGenBraFlag != -1) {
        	sb.append(" and is_branch_school = \"" + schGenBraFlag+"\"");
        }
        
        //是否是总校
        if(fblMb != -1) {
        	sb.append(" and license_main_type = \"" + fblMb+"\"");
        }
        
        //配货批次号
        if(CommonUtil.isNotEmpty(distrBatNumber)) {
        	sb.append(" and ware_batch_no like \"%" + distrBatNumber+"%\"");
        }
        
        if(distrBatNumberList !=null && distrBatNumberList.size()>0) {
        	String distrBatNumbers= distrBatNumberList.toString().substring(1,distrBatNumberList.toString().length()-1);
        	if(distrBatNumbers.indexOf("\"") <0) {
        		distrBatNumbers = "\""+distrBatNumbers.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and ware_batch_no in (" +distrBatNumbers.replace(" ", "") +")");
        }
        
        //haul_status  -2 信息不完整 -1 未指派 0 已指派（未配送） 1配送中 2 待验收（已配送）3已验收 -4已取消
        //验收状态0:待验收，1:已验收
        if(acceptStatus != -1) {
        	if(acceptStatus == 0 ) {
        		//未验收
        		sb.append(" and haul_status < 3 ");
        	}else {
        		//已验收
        		sb.append(" and haul_status = 3 ");
        	}
        }
        //指派状态0:未指派，1：已指派，2:已取消
        if( assignStatus != -1) {
        	if(assignStatus == 0 ) {
        		//未指派
        		sb.append(" and haul_status < 0 ");
        	}else if(assignStatus == 1){
        		//已指派
        		sb.append(" and haul_status > -1 ");
        	}else {
        		sb.append(" and haul_status = -4 ");
        	}
        }
        
        //指派状态多选
        if(assignStatussList != null && assignStatussList.size()>0) {
        	sb.append(" and ( 1=2 ");
        	if(assignStatussList.indexOf("0") >= 0) {
        		sb.append(" or haul_status < 0 ");
        	}
        	if (assignStatussList.indexOf("1") >= 0) {
        		sb.append(" or haul_status > -1 ");
        	}
        	if (assignStatussList.indexOf("2") >= 0) {
        		sb.append(" or haul_status = -4 ");
        	}
        	
        	sb.append(" ) ");
        }
        
        //配送状态0:未派送，1:配送中，2: 已配送
        if(dispStatus != -1) {
        	if(dispStatus == 0 ) {
        		//未配送
        		sb.append(" and haul_status < 1 ");
        	}else if(dispStatus == 1 ){
        		//配送中
        		sb.append(" and haul_status = 1 ");
        	}else if(dispStatus == 2){
        		//已配送
        		sb.append(" and haul_status > 1 ");
        	}
        }
        //配送状态多选
        if(dispStatussList != null && dispStatussList.size()>0) {
        	sb.append(" and ( 1=2 ");
        	if(dispStatussList.indexOf("0") >= 0) {
        		sb.append(" or haul_status < 1 ");
        	}
        	if (dispStatussList.indexOf("1") >= 0) {
        		sb.append(" or haul_status = 1 ");
        	}
        	if (dispStatussList.indexOf("2") >= 0) {
        		sb.append(" or haul_status > 1 ");
        	}
        	
        	sb.append(" ) ");
        }
        
        
        //配送类型，0:原料，1:成品菜
        if(dispType != -1) {
        	if(dispType == 0 ) {
        		//原料
        		sb.append(" and ledger_type = 1 ");
        	}else if(dispType == 1 ) {
        		//成品菜
        		sb.append(" and ledger_type = 2 ");
        	}
        }
        
    	//配送方式，0:统配，1:直配
        if(dispMode != -1) {
        	if(dispMode == 0 ) {
        		//统配
        		sb.append(" and (delivery_attr = 0  or delivery_attr = 2 )");
        	}else if(dispMode == 1 ) {
        		//直配
        		sb.append(" and (delivery_attr = 1  or delivery_attr = 3 )");
        	}
        }
        
        
	}

	//--------------------配货-周报表----------------------------------------------

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_ledger_master_total_w中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduLedgerMasterTotalWObj> getAppTEduLedgerMasterTotalWObjList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduLedgerMasterTotalWObj inputObj,
    		Integer startNum,Integer endNum) {
        logger.info("[Enter dao method] {}-{}", "AppTEduLedgerMasterTotalWObjDao", "getAppTEduLedgerMasterTotalWObjList");
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append("start_action_date startActionDate, ");
        sb.append("  end_action_date endActionDate, school_id schoolId, school_name schoolName, department_id departmentId, ");
        sb.append("  area area, level_name levelName, school_nature_name schoolNatureName, school_nature_sub_name schoolNatureSubName, ");
        sb.append("  is_branch_school isBranchSchool,parent_id parentId,parent_name parentName,");
        sb.append("  branch_total branchTotal, department_master_id departmentMasterId, department_slave_id_name departmentSlaveIdName, school_area_id schoolAreaId, ");
        sb.append("  license_main_type licenseMainType, license_main_child licenseMainChild, ledger_day_total ledgerDayTotal, have_ledger_day_total haveLedgerDayTotal, ");
        sb.append("  have_no_ledger_day_total haveNoLedgerDayTotal, guifan_ledger_total guifanLedgerTotal, bulu_ledger_total buluLedgerTotal, yuqi_ledger_total yuqiLedgerTotal, ");
        sb.append("  no_ledger_total noLedgerTotal, address address, food_safety_persion foodSafetyPersion, food_safety_mobilephone foodSafetyMobilephone");
        sb.append("   ");
        sb.append(" from app_t_edu_ledger_master_total_w " );
        sb.append(" where  1=1 ");
        getAppTEduLedgerMasterTotalWObjListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+endNum);
        }        logger.info("执行sql:"+sb.toString());
		return (List<AppTEduLedgerMasterTotalWObj>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<AppTEduLedgerMasterTotalWObj>() {
			@Override
			public AppTEduLedgerMasterTotalWObj mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				if(startNum!=null && endNum!=null && startNum!=-1 &&  endNum != -1 
						&& (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
								AppTEduLedgerMasterTotalWObj obj = new AppTEduLedgerMasterTotalWObj();

				obj.setStartActionDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("startActionDate"))) {
					obj.setStartActionDate(rs.getString("startActionDate"));
				}
				obj.setEndActionDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("endActionDate"))) {
					obj.setEndActionDate(rs.getString("endActionDate"));
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
				obj.setLedgerDayTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("ledgerDayTotal"))) {
					obj.setLedgerDayTotal(rs.getInt("ledgerDayTotal"));
				}
				obj.setHaveLedgerDayTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("haveLedgerDayTotal"))) {
					obj.setHaveLedgerDayTotal(rs.getInt("haveLedgerDayTotal"));
				}
				obj.setHaveNoLedgerDayTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("haveNoLedgerDayTotal"))) {
					obj.setHaveNoLedgerDayTotal(rs.getInt("haveNoLedgerDayTotal"));
				}
				obj.setGuifanLedgerTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("guifanLedgerTotal"))) {
					obj.setGuifanLedgerTotal(rs.getInt("guifanLedgerTotal"));
				}
				obj.setBuluLedgerTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("buluLedgerTotal"))) {
					obj.setBuluLedgerTotal(rs.getInt("buluLedgerTotal"));
				}
				obj.setYuqiLedgerTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("yuqiLedgerTotal"))) {
					obj.setYuqiLedgerTotal(rs.getInt("yuqiLedgerTotal"));
				}
				obj.setNoLedgerTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("noLedgerTotal"))) {
					obj.setNoLedgerTotal(rs.getInt("noLedgerTotal"));
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
				
				obj.setParentId("");
				if(CommonUtil.isNotEmpty(rs.getString("parentId"))) {
					obj.setParentId(rs.getString("parentId"));
				}
				
				obj.setParentName("-");
				if(CommonUtil.isNotEmpty(rs.getString("parentName")) && !"0".equals(rs.getString("parentName")) && !"-1".equals(rs.getString("parentName"))) {
					obj.setParentName(rs.getString("parentName"));
				}
				
				obj.setIsBranchSchool(-1);
				if(CommonUtil.isNotEmpty(rs.getString("isBranchSchool"))) {
					obj.setIsBranchSchool(rs.getInt("isBranchSchool"));
				}
				
				
				
				return obj;
			}
		});
		
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_ledger_master_total_w中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduLedgerMasterTotalWObjListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduLedgerMasterTotalWObj inputObj) {
        logger.info("[Enter dao method] {}-{}", "AppTEduLedgerMasterTotalWObjDao", "getAppTEduLedgerMasterTotalWObjListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_ledger_master_total_w" );
        sb.append(" where 1=1  ");
        
        getAppTEduLedgerMasterTotalWObjListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
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
    * 从数据库app_saas_v1的数据表app_t_edu_ledger_master_total_w中根据条件查询数据列表条件
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @param sb
    */
    public void getAppTEduLedgerMasterTotalWObjListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduLedgerMasterTotalWObj inputObj,
    StringBuffer sb) {
        logger.info("[Enter dao method] {}-{}", "AppTEduLedgerMasterTotalWObjDao", "getAppTEduLedgerMasterTotalWObjListCondition");
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
		
        /*sb.append(" and start_action_date >= \""+startDate+"\"");
        sb.append(" and start_action_date < \""+endDateAddOne +"\"");*/
        
        //startActionDate
		if(CommonUtil.isNotEmpty(inputObj.getStartActionDate())) {
	        	sb.append(" and DATE_FORMAT(start_action_date,'yyyy-MM-dd') = \"" + inputObj.getStartActionDate()+"\"");
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

        //ledgerDayTotal
        if(inputObj.getLedgerDayTotal() !=null && inputObj.getLedgerDayTotal() != -1) {
        	sb.append(" and ledger_day_total = \"" + inputObj.getLedgerDayTotal()+"\"");
        }

        //haveLedgerDayTotal
        if(inputObj.getHaveLedgerDayTotal() !=null && inputObj.getHaveLedgerDayTotal() != -1) {
        	sb.append(" and have_ledger_day_total = \"" + inputObj.getHaveLedgerDayTotal()+"\"");
        }

        //haveNoLedgerDayTotal
        if(inputObj.getHaveNoLedgerDayTotal() !=null && inputObj.getHaveNoLedgerDayTotal() != -1) {
        	sb.append(" and have_no_ledger_day_total = \"" + inputObj.getHaveNoLedgerDayTotal()+"\"");
        }

        //guifanLedgerTotal
        if(inputObj.getGuifanLedgerTotal() !=null && inputObj.getGuifanLedgerTotal() != -1) {
        	sb.append(" and guifan_ledger_total = \"" + inputObj.getGuifanLedgerTotal()+"\"");
        }

        //buluLedgerTotal
        if(inputObj.getBuluLedgerTotal() !=null && inputObj.getBuluLedgerTotal() != -1) {
        	sb.append(" and bulu_ledger_total = \"" + inputObj.getBuluLedgerTotal()+"\"");
        }

        //yuqiLedgerTotal
        if(inputObj.getYuqiLedgerTotal() !=null && inputObj.getYuqiLedgerTotal() != -1) {
        	sb.append(" and yuqi_ledger_total = \"" + inputObj.getYuqiLedgerTotal()+"\"");
        }

        //noLedgerTotal
        if(inputObj.getNoLedgerTotal() !=null && inputObj.getNoLedgerTotal() != -1) {
        	sb.append(" and no_ledger_total = \"" + inputObj.getNoLedgerTotal()+"\"");
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

        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
    }

}
