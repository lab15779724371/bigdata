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
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.im.KwCommonRecs;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRecyclerWasteService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 垃圾回收废弃油脂相关hive库的查询
 * @author Administrator
 *
 */
@Service
public class DbHiveRecyclerWasteServiceImpl implements DbHiveRecyclerWasteService {
	private static final Logger logger = LogManager.getLogger(DbHiveRecyclerWasteServiceImpl.class.getName());
	
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
  	
  	//--------------------废弃油脂-汇总----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_recycler_waste_total中根据条件查询数据列表
  	 * statMode:统计模式，0:按区统计，1:按学校性质统计，2:按学校学制统计，3:按所属主管部门统计
  	 */
    public List<KwCommonRecs> getRecyclerWasteList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distId,List<Object> distIdList,String supplierArea,List<Object> supplierAreaList,
    		int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
    		Integer platformType ,Integer type,Integer statMode ) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append("select from_unixtime(unix_timestamp(recycler_date,'yyyy-MM-dd'),'yyyy/MM/dd') recyclerDate , ");
        sb.append(" area,supplier_area supplierArea,recycler_sum recyclerSum,recycler_total recyclerTotal, ");
        sb.append(" type,platform_type platformType, ");
        sb.append(" level_name levelName,school_nature_name schoolNatureName, ");
        sb.append(" department_master_id departmentMasterId,department_slave_id_name departmentSlaveIdName ");
        sb.append(" from app_t_edu_recycler_waste_total " );
        sb.append(" where  1=1 ");
        if(statMode == 0) {
        	//按区统计时，其他三个属性需要为空，数据是重叠的
	        sb.append(" and (level_name is null or level_name = \"null\" or level_name = \"NULL\" )  ");
	        sb.append(" and (school_nature_name is null or school_nature_name = \"null\" or school_nature_name = \"NULL\" ) ");
	        sb.append(" and (department_master_id is null or department_master_id = \"null\" or department_master_id = \"NULL\" ) ");
	        sb.append(" and (department_slave_id_name is null or department_slave_id_name = \"null\" or department_slave_id_name = \"NULL\" ) ");
        }else if (statMode == 1) {
        	sb.append(" and school_nature_name != \"NULL\"  ");
        }else if (statMode == 2) {
        	sb.append(" and level_name != \"NULL\"  ");
        }else if (statMode == 3) {
        	sb.append(" and department_master_id != \"NULL\"");
        	sb.append(" and department_slave_id_name != \"NULL\"  ");
        	
        	sb.append(" and department_master_id != \"-1\"");
        	sb.append(" and department_slave_id_name != \"-1\"  ");
        }
       getRecyclerWasteCondition(listYearMonth, startDate, endDateAddOne,
        		distId,distIdList,
        		subLevel,compDep,subLevels,compDeps,
        		supplierArea,supplierAreaList,platformType,type,sb);
        logger.info("执行sql:"+sb.toString());
		return (List<KwCommonRecs>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<KwCommonRecs>() {
			@Override
			public KwCommonRecs mapRow(ResultSet rs, int rowNum) throws SQLException {
				KwCommonRecs cdsd = new KwCommonRecs();
				
				cdsd.setRecDate(rs.getString("recyclerDate"));
				
				cdsd.setPlatformType(rs.getInt("platformType"));
				cdsd.setType(rs.getInt("type"));
				//区域名称
				cdsd.setDistName("-");
				//1为教委端 2为团餐端
				if(cdsd.getPlatformType() > -1) {
					if( cdsd.getPlatformType() == 2) {//2
			            if(StringUtils.isNotEmpty(rs.getString("supplierArea"))) {
			            	cdsd.setDistName(rs.getString("supplierArea"));
			            }
					}else {//1
						 if(StringUtils.isNotEmpty(rs.getString("area"))) {
				            	cdsd.setDistName(rs.getString("area"));
				         }
					}
				}else {
					//area 和 supplierArea只会同时存在一个
					if(StringUtils.isNotEmpty(rs.getString("area")) && !"NULL".equals(rs.getString("area"))) {
		            	cdsd.setDistName(rs.getString("area"));
		            }if(StringUtils.isNotEmpty(rs.getString("supplierArea"))&& !"NULL".equals(rs.getString("supplierArea"))) {
		            	cdsd.setDistName(rs.getString("supplierArea"));
		            }
				}
				
				//回收次数
				cdsd.setRecyclerTotal(0);
				if(StringUtils.isNotEmpty(rs.getString("recyclerTotal")) && rs.getInt("recyclerTotal")>0) {
	            	cdsd.setRecyclerTotal(rs.getInt("recyclerTotal"));
	            }
				//回收数量
				cdsd.setRecyclerSum(Float.valueOf("0"));
				if(StringUtils.isNotEmpty(rs.getString("recyclerSum")) && rs.getFloat("recyclerSum")>0) {
					cdsd.setRecyclerSum(rs.getFloat("recyclerSum"));
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
	private void getRecyclerWasteCondition(List<String> listYearMonth, String startDate, String endDateAddOne,
			String distId,List<Object> distIdList,
			int subLevel, int compDep,List<Object> subLevels,List<Object> compDeps,
			String supplierArea,List<Object> supplierAreaList,Integer platformType ,Integer type,
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
		
        sb.append(" and date_format(recycler_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and date_format(recycler_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
        //1为教委端 2为团餐端
        if(platformType!=null && platformType>-1) {
        	sb.append(" and platform_type = \"" + platformType+"\"");
        	if(platformType == 2) {
       		   //团餐公司区
                if(StringUtils.isNotEmpty(distId)) {
                	sb.append(" and supplier_area = \"" + distId+"\"");
                }
                
                if(distIdList !=null && distIdList.size()>0) {
                	String distIds= distIdList.toString().substring(1,distIdList.toString().length()-1);
                	if(distIds.indexOf("\"") <0) {
                		distIds = "\""+distIds.replaceAll(",", "\",\"")+"\"";
                	}
                	sb.append(" and supplier_area in (" +distIds +")");
                }
        	}else{
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
        	}
        	
        }else {
        	
        	//团餐公司区
            if(StringUtils.isNotEmpty(distId)) {
            	sb.append(" and (supplier_area = \"" + distId+"\" or area = \""+distId+"\")"  );
            }
            
            if(distIdList !=null && distIdList.size()>0) {
            	String distIds= distIdList.toString().substring(1,distIdList.toString().length()-1);
            	if(distIds.indexOf("\"") <0) {
            		distIds = "\""+distIds.replaceAll(",", "\",\"")+"\"";
            	}
            	sb.append(" and supplier_area in (" +distIds +") or area in (" +distIds +")");
            }
        }
        
        //1餐厨垃圾，2废弃油脂
        if(type!=null && type > -1) {
        	sb.append(" and type = \"" + type+"\"");
        }
        
        
        //团餐公司区
        if(StringUtils.isNotEmpty(supplierArea)) {
        	sb.append(" and supplier_area = \"" + supplierArea+"\"");
        }
        
        if(supplierAreaList !=null && supplierAreaList.size()>0) {
        	String supplierAreas= supplierAreaList.toString().substring(1,supplierAreaList.toString().length()-1);
        	if(supplierAreas.indexOf("\"") <0) {
        		supplierAreas = "\""+supplierAreas.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and supplier_area in (" +supplierAreas +")");
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
	        		String supplierAreas= entry.getValue();
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
	
  	//--------------------废弃油脂-详情----------------------------------------------
  	/**
  	 * 从数据库app_saas_v1的数据表app_t_edu_recycler_waste中根据条件查询数据列表
  	 */
    public List<KwCommonDets> getRecyclerWasteDetsList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,String ppName, int schType, 
    		String rmcId,String rmcName, String recComany, String recPerson, int schProp,
			int subLevel, int compDep,Integer secontType,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			Integer platformType ,Integer type,
    		Integer startNum,Integer endNum) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
        sb.append(" select from_unixtime(unix_timestamp(recycler_date,'yyyy-MM-dd'),'yyyy/MM/dd') recyclerDate,is_branch_school schGenBraFlag,branch_total braCampusNum, ");
        sb.append(" parent_id relGenSchName,department_master_id subLevel,department_slave_id_name compDep,school_area_id subDistName, ");
        sb.append(" school_nature_name schProp, area area, supplier_area supplierArea, ");
        sb.append(" school_name ppName, supplier_name rmcName,level_name schType,recycler_number recNum, ");
        sb.append(" type,platform_type platformType, secont_type secontType, ");
        sb.append(" recycler_name recCompany, contact recPerson,recycler_documents recBillNum,supplier_id schSupplierId ");
        sb.append(" from app_t_edu_recycler_waste" );
        sb.append(" where  1=1 ");
        
        getRecyclerWasteDetsListCondition(listYearMonth, startDate, endDateAddOne, distIdorSCName, ppName, schType,rmcId, rmcName,
        		recComany, recPerson, schProp, subLevel, compDep,secontType, distNames, subLevels, compDeps, schProps, schTypes,platformType,type, sb);
        if(startNum !=null && endNum !=null) {
        	sb.append(" limit "+endNum);
        }
        logger.info("执行sql:"+sb.toString());
        logger.info("******************jdbcTemplateHive:"+jdbcTemplateHive);
		return (List<KwCommonDets>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<KwCommonDets>() {
			@Override
			public KwCommonDets mapRow(ResultSet rs, int rowNum) throws SQLException {
				if(startNum!=null && endNum!=null && startNum!=null && endNum!=null && startNum!=-1 && endNum!=-1 && (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
				
				KwCommonDets cdsd = new KwCommonDets();
				//回收日期，格式：xxxx/xx/xx
				cdsd.setRecDate(rs.getString("recyclerDate"));
				
				cdsd.setPlatformType(rs.getInt("platformType"));
				cdsd.setType(rs.getInt("type"));
				
				//区域名称
				cdsd.setDistName("-");
				//1为教委端 2为团餐端
				if(cdsd.getPlatformType() > -1) {
					if( cdsd.getPlatformType() == 2) {
			            if(CommonUtil.isNotEmpty(rs.getString("supplierArea"))) {
			            	cdsd.setDistName(rs.getString("supplierArea"));
			            }
					}else {
						 if(CommonUtil.isNotEmpty(rs.getString("area"))) {
				            	cdsd.setDistName(rs.getString("area"));
				         }
					}
				}else {
					//area 和 supplierArea只会同时存在一个
					if(CommonUtil.isNotEmpty(rs.getString("area")) && !"NULL".equals(rs.getString("area"))) {
		            	cdsd.setDistName(rs.getString("area"));
		            }if(CommonUtil.isNotEmpty(rs.getString("supplierArea"))&& !"NULL".equals(rs.getString("supplierArea"))) {
		            	cdsd.setDistName(rs.getString("supplierArea"));
		            }
				}
				//项目点名称
			    cdsd.setPpName("-");
	            if(CommonUtil.isNotEmpty(rs.getString("ppName"))) {
	            	cdsd.setPpName(rs.getString("ppName"));
	            }
	           
	            //团餐公司名称
	            cdsd.setRmcName("-");
	            if(CommonUtil.isNotEmpty(rs.getString("rmcName"))) {
	            	cdsd.setRmcName(rs.getString("rmcName"));
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
				if(rs.getInt("schGenBraFlag") > -1) {
					cdsd.setBraCampusNum(rs.getInt("schGenBraFlag"));
				}
				//关联总校
				cdsd.setRelGenSchName("-");
				if(CommonUtil.isNotEmpty(rs.getString("relGenSchName")) ) {
					cdsd.setRelGenSchName(rs.getString("relGenSchName"));
				}   
				//所属
				cdsd.setSubLevel("其他");
				if(CommonUtil.isNotEmpty(rs.getString("subLevel")) && !"-1".equals(rs.getString("subLevel"))) {
					cdsd.setSubLevel(AppModConfig.subLevelIdToNameMap.get(Integer.valueOf(rs.getString("subLevel"))));
				}
				//主管部门
				cdsd.setCompDep("其他");
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
					cdsd.setSubLevel(AppModConfig.distIdToNameMap.get(rs.getString("subDistName")));
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
				//团餐公司名称
				cdsd.setRmcName("-");
				if(CommonUtil.isNotEmpty(rs.getString("rmcName"))) {
					cdsd.setRmcName(rs.getString("rmcName"));
				}
				//回收数量，单位：桶
				cdsd.setRecNum("0");
				if(CommonUtil.isNotEmpty(rs.getString("recNum"))) {
					//float rcNum = rs.getFloat("recNum");
					//BigDecimal bd = new BigDecimal(rcNum);
					//rcNum = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					cdsd.setRecNum(rs.getString("recNum"));
				}
				
				//recycler_name recCompany, contact recPerson,recycler_documents recBillNum 
				//回收单位
				cdsd.setRecComany("-");
				if(CommonUtil.isNotEmpty(rs.getString("recCompany")))
					cdsd.setRecComany(rs.getString("recCompany"));
				//回收人
				cdsd.setRecPerson(rs.getString("recPerson"));
				//回收单据数
				cdsd.setRecBillNum(0);
				if(rs.getInt("recBillNum") > 0)
					cdsd.setRecBillNum(rs.getInt("recBillNum"));
	            
				//hive库中的含义：针对大类 2(1废油，2 含油废水)  默认为0(无特别含义)
				cdsd.setWoType("-");
				if(rs.getInt("secontType") >0) {
					if(rs.getInt("secontType")==1) {
						cdsd.setWoType("废油");
					}else if(rs.getInt("secontType")==2) {
						cdsd.setWoType("含油费油");
					}
				}
				
				cdsd.setSchSupplierId("");
				if(CommonUtil.isNotEmpty(rs.getString("schSupplierId")))
					cdsd.setSchSupplierId(rs.getString("schSupplierId"));
				
				return cdsd;
			}
		});
    }

    /**
     * 从数据库app_saas_v1的数据表app_t_edu_recycler_waste中根据条件查询数据条数
     */
    public Integer getRecyclerWasteDetsCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,String ppName, int schType, 
    		String rmcId,String rmcName, String recComany, String recPerson, int schProp,
			int subLevel, int compDep,Integer secontType,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			Integer platformType ,Integer type
			) {
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_recycler_waste" );
        sb.append(" where 1=1  ");
        
        getRecyclerWasteDetsListCondition(listYearMonth, startDate, endDateAddOne, distIdorSCName, ppName, schType,
        		rmcId,rmcName, recComany, recPerson, schProp, subLevel, compDep,secontType, 
        		distNames, subLevels, compDeps, schProps, schTypes,platformType,type, sb);
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
	private void getRecyclerWasteDetsListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		String distIdorSCName,String schName, int schType, 
    		String rmcId,String rmcName, String recComany, String recPerson, int schProp,
			int subLevel, int compDep,Integer secontType,
			List<Object> distNames,List<Object> subLevels,List<Object> compDeps,List<Object> schProps,List<Object> schTypes,
			Integer platformType ,Integer type,
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
		
        sb.append(" and date_format(recycler_date,'yyyy-MM-dd') >= \""+startDate+"\"");
        sb.append(" and date_format(recycler_date,'yyyy-MM-dd') < \""+endDateAddOne +"\"");
        
		//String distId, 
        //区
        //1为教委端 2为团餐端
        if(platformType!=null && platformType>-1) {
        	sb.append(" and platform_type = \"" + platformType+"\"");
        	if(platformType == 2) {
       		   //团餐公司区
                if(StringUtils.isNotEmpty(distIdorSCName)) {
                	sb.append(" and supplier_area = \"" + distIdorSCName+"\"");
                }
                
                if(distNames !=null && distNames.size()>0) {
                	String distIds= distNames.toString().substring(1,distNames.toString().length()-1);
                	if(distIds.indexOf("\"") <0) {
                		distIds = "\""+distIds.replaceAll(",", "\",\"")+"\"";
                	}
                	sb.append(" and supplier_area in (" +distIds +")");
                }
        	}else{
       		    //学校区
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
        	}
        	
        }else {
        	
        	//团餐公司区
            if(StringUtils.isNotEmpty(distIdorSCName)) {
            	sb.append(" and (supplier_area = \"" + distIdorSCName+"\" or area = \""+distIdorSCName+"\")"  );
            }
            
            if(distNames !=null && distNames.size()>0) {
            	String distIds= distNames.toString().substring(1,distNames.toString().length()-1);
            	if(distIds.indexOf("\"") <0) {
            		distIds = "\""+distIds.replaceAll(",", "\",\"")+"\"";
            	}
            	sb.append(" and supplier_area in (" +distIds +") or area in (" +distIds +")");
            }
        }
        
        //1餐厨垃圾，2废弃油脂
        if(type!=null && type > -1) {
        	sb.append(" and type = \"" + type+"\"");
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
		
        //团餐公司编号（针对学校餐厨垃圾和废弃油脂有效）
        if(StringUtils.isNotEmpty(rmcId)) {
        	sb.append(" and supplier_id = \"" + rmcId+"\"");
        }
        
        //团餐公司名称（针对团餐公司废弃油脂和餐厨垃圾时有效）
        if(StringUtils.isNotEmpty(rmcName)) {
        	sb.append(" and supplier_name = \"" + rmcName+"\"");
        }
        
        //回收单位
        if(StringUtils.isNotEmpty(recComany)) {
			sb.append(" and recycler_name like  \"%"+recComany+"%\"");
		}
        //回收人
        if(StringUtils.isNotEmpty(recPerson)) {
			sb.append(" and contact like  \"%"+recPerson+"%\"");
		}
        
        //hive库针对大类 2(1废油，2 含油废水)  默认为0(无特别含义) 
        if(secontType!=null && secontType!=-1) {
        	sb.append(" and secont_type = \"" + secontType+"\"");
        }
	}
}
