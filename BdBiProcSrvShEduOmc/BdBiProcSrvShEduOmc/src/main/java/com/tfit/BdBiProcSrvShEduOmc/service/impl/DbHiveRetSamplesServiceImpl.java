package com.tfit.BdBiProcSrvShEduOmc.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tfit.BdBiProcSrvShEduOmc.dto.im.week.AppTEduReserveTotalW;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveRetSamplesService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

/**
 * 排菜相关hive库的查询
 * @author Administrator
 *
 */
@Service
public class DbHiveRetSamplesServiceImpl implements DbHiveRetSamplesService {
	private static final Logger logger = LogManager.getLogger(DbHiveRetSamplesServiceImpl.class.getName());
	
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
  	

	//--------------------使用情况-周报表----------------------------------------------
    /**
    * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_w中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<AppTEduReserveTotalW> getAppTEduReserveTotalWList(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduReserveTotalW inputObj,
    		Integer startNum,Integer endNum) {
        logger.info("[Enter dao method] {}-{}", "AppTEduReserveTotalWDao", "getAppTEduReserveTotalWList");
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
        sb.append("  license_main_type licenseMainType, license_main_child licenseMainChild, reserve_day_total reserveDayTotal, have_reserve_day_total haveReserveDayTotal, ");
        sb.append("  have_no_reserve_day_total haveNoReserveDayTotal, guifan_reserve_total guifanReserveTotal, bulu_reserve_total buluReserveTotal, yuqi_reserve_total yuqiReserveTotal, ");
        sb.append("  no_reserve_total noReserveTotal, address address, food_safety_persion foodSafetyPersion, food_safety_mobilephone foodSafetyMobilephone, ");
        sb.append("  is_branch_school isBranchSchool, parent_id parentId, parent_name parentName ");
        sb.append(" from app_t_edu_reserve_total_w " );
        sb.append(" where  1=1 ");
        getAppTEduReserveTotalWListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+endNum);
        }        logger.info("执行sql:"+sb.toString());
		return (List<AppTEduReserveTotalW>) jdbcTemplateTemp.query(sb.toString(), new RowMapper<AppTEduReserveTotalW>() {
			@Override
			public AppTEduReserveTotalW mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				if(startNum!=null && endNum!=null && startNum!=-1 &&  endNum != -1 
						&& (rowNum <startNum || rowNum >= endNum)) {
					return null;
				}
								AppTEduReserveTotalW obj = new AppTEduReserveTotalW();

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
				obj.setReserveDayTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("reserveDayTotal"))) {
					obj.setReserveDayTotal(rs.getInt("reserveDayTotal"));
				}
				obj.setHaveReserveDayTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("haveReserveDayTotal"))) {
					obj.setHaveReserveDayTotal(rs.getInt("haveReserveDayTotal"));
				}
				obj.setHaveNoReserveDayTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("haveNoReserveDayTotal"))) {
					obj.setHaveNoReserveDayTotal(rs.getInt("haveNoReserveDayTotal"));
				}
				obj.setGuifanReserveTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("guifanReserveTotal"))) {
					obj.setGuifanReserveTotal(rs.getInt("guifanReserveTotal"));
				}
				obj.setBuluReserveTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("buluReserveTotal"))) {
					obj.setBuluReserveTotal(rs.getInt("buluReserveTotal"));
				}
				obj.setYuqiReserveTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("yuqiReserveTotal"))) {
					obj.setYuqiReserveTotal(rs.getInt("yuqiReserveTotal"));
				}
				obj.setNoReserveTotal(0);
				if(CommonUtil.isNotEmpty(rs.getString("noReserveTotal"))) {
					obj.setNoReserveTotal(rs.getInt("noReserveTotal"));
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
				obj.setIsBranchSchool(0);
				if(CommonUtil.isNotEmpty(rs.getString("isBranchSchool"))) {
					obj.setIsBranchSchool(rs.getInt("isBranchSchool"));
				}
				obj.setParentId("-");
				if(CommonUtil.isNotEmpty(rs.getString("parentId"))) {
					obj.setParentId(rs.getString("parentId"));
				}
				obj.setParentName("-");
				if(CommonUtil.isNotEmpty(rs.getString("parentName"))) {
					obj.setParentName(rs.getString("parentName"));
				}
				logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
				return obj;
			}
		});
    }

    /**
    * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_w中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getAppTEduReserveTotalWListCount(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduReserveTotalW inputObj) {
        logger.info("[Enter dao method] {}-{}", "AppTEduReserveTotalWDao", "getAppTEduReserveTotalWListCount");
        Long daoStartTime = System.currentTimeMillis();
    	JdbcTemplate jdbcTemplateTemp  =DbHiveServiceImpl.getJdbcTemplateHive(jdbcTemplateHive, jdbcTemplateHive2, dataSourceHive);
		if(jdbcTemplateTemp == null) {
			return null;
		}
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from app_t_edu_reserve_total_w" );
        sb.append(" where 1=1  ");
        
        getAppTEduReserveTotalWListCondition(listYearMonth, startDate, endDateAddOne,inputObj,sb);
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
    * 从数据库app_saas_v1的数据表app_t_edu_reserve_total_w中根据条件查询数据列表条件
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @param sb
    */
    public void getAppTEduReserveTotalWListCondition(List<String> listYearMonth, String startDate,String endDateAddOne,
    		AppTEduReserveTotalW inputObj,
    StringBuffer sb) {
        logger.info("[Enter dao method] {}-{}", "AppTEduReserveTotalWDao", "getAppTEduReserveTotalWListCondition");
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
		
       /* sb.append(" and expect_receive_date >= \""+startDate+"\"");
        sb.append(" and expect_receive_date < \""+endDateAddOne +"\"");
        */
        //startUseDate
        if(CommonUtil.isNotEmpty(inputObj.getStartUseDate())) {
        	sb.append(" and DATE_FORMAT(start_use_date,'yyyy-MM-dd') = \"" + inputObj.getStartUseDate()+"\"");
        }

       /* //endUseDate
        if(CommonUtil.isNotEmpty(inputObj.getEndUseDate())) {
        	sb.append(" and end_use_date = \"" + inputObj.getEndUseDate()+"\"");
        }
*/
        //schoolId
        if(CommonUtil.isNotEmpty(inputObj.getSchoolId())) {
        	sb.append(" and school_id = \"" + inputObj.getSchoolId()+"\"");
        }

        //schoolName
        if(CommonUtil.isNotEmpty(inputObj.getSchoolName())) {
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

        /*//licenseMainChild
        if(inputObj.getLicenseMainChild() !=null && inputObj.getLicenseMainChild() != -1) {
        	sb.append(" and license_main_child = \"" + inputObj.getLicenseMainChild()+"\"");
        }*/

        //reserveDayTotal
        if(inputObj.getReserveDayTotal() !=null && inputObj.getReserveDayTotal() != -1) {
        	sb.append(" and reserve_day_total = \"" + inputObj.getReserveDayTotal()+"\"");
        }

        //haveReserveDayTotal
        if(inputObj.getHaveReserveDayTotal() !=null && inputObj.getHaveReserveDayTotal() != -1) {
        	sb.append(" and have_reserve_day_total = \"" + inputObj.getHaveReserveDayTotal()+"\"");
        }

        //haveNoReserveDayTotal
        if(inputObj.getHaveNoReserveDayTotal() !=null && inputObj.getHaveNoReserveDayTotal() != -1) {
        	sb.append(" and have_no_reserve_day_total = \"" + inputObj.getHaveNoReserveDayTotal()+"\"");
        }

        //guifanReserveTotal
        if(inputObj.getGuifanReserveTotal() !=null && inputObj.getGuifanReserveTotal() != -1) {
        	sb.append(" and guifan_reserve_total = \"" + inputObj.getGuifanReserveTotal()+"\"");
        }

        //buluReserveTotal
        if(inputObj.getBuluReserveTotal() !=null && inputObj.getBuluReserveTotal() != -1) {
        	sb.append(" and bulu_reserve_total = \"" + inputObj.getBuluReserveTotal()+"\"");
        }

        //yuqiReserveTotal
        if(inputObj.getYuqiReserveTotal() !=null && inputObj.getYuqiReserveTotal() != -1) {
        	sb.append(" and yuqi_reserve_total = \"" + inputObj.getYuqiReserveTotal()+"\"");
        }

        //noReserveTotal
        if(inputObj.getNoReserveTotal() !=null && inputObj.getNoReserveTotal() != -1) {
        	sb.append(" and no_reserve_total = \"" + inputObj.getNoReserveTotal()+"\"");
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

        //isBranchSchool
        if(inputObj.getIsBranchSchool() !=null && inputObj.getIsBranchSchool() != -1) {
        	sb.append(" and is_branch_school = \"" + inputObj.getIsBranchSchool()+"\"");
        }

        //parentId
        if(CommonUtil.isNotEmpty(inputObj.getParentId())) {
        	sb.append(" and parent_id = \"" + inputObj.getParentId()+"\"");
        }

        //parentName
        if(CommonUtil.isNotEmpty(inputObj.getParentName())) {
        	sb.append(" and parent_name = \"" + inputObj.getParentName()+"\"");
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
        
        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
    }

}
