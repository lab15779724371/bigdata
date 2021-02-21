package com.tfit.BdBiProcSrvShEduOmc.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SchIdNameDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SchOptModeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SchOwnershipDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.SchTypeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSuperviseUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edu.TEduDistrictDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edu.TEduDistrictV2DoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edu.TEduSchoolDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dto.search.SearchLicense;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.DepartmentObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

@Service
public class Db1ServiceImpl implements Db1Service {
	private static final Logger logger = LogManager.getLogger(Db1ServiceImpl.class.getName());
	
	@Autowired
	TEduDistrictV2DoMapper tedv2DoMapper;
	
	@Autowired
	TEduSchoolDoMapper tesDoMapper;
	
	//额外数据源1
	@Autowired
	@Qualifier("ds1")
	DataSource dataSource1;
	//额外数据源1连接模板
	JdbcTemplate jdbcTemplate1 = null;
	
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
  		logger.info("定时建立与 DataSource数据源ds1对象表示的数据源的连接，时间：" + BCDTimeUtil.convertNormalFrom(null));
  		jdbcTemplate1 = new JdbcTemplate(dataSource1);
  	}
  	
  	//从数据源ds1的数据表t_edu_district中查找id和区域名称
    public List<TEduDistrictDo> getListByDs1IdName() {
    	if(mybatisUseFlag)
    		return tedv2DoMapper.getDistIdNameListByDs1();
    	else {
    		if(jdbcTemplate1 == null)
    			return null;
    		String sql = "select distinct id,name from t_edu_district";
    		return (List<TEduDistrictDo>) jdbcTemplate1.query(sql, new RowMapper<TEduDistrictDo>() {

    			@Override
    			public TEduDistrictDo mapRow(ResultSet rs, int rowNum) throws SQLException {
    				TEduDistrictDo tdd = new TEduDistrictDo();
    				tdd.setId(rs.getString("id"));
    				tdd.setName(rs.getString("name"));
                
    				return tdd;
    			}
    		});
    	}        
    }
    
    //从数据源ds1的数据表t_edu_school中查找canteen_mode以id
    public SchOptModeDo getSchOptModeByDs1Id(String id){
    	if(jdbcTemplate1 == null)
    		return null;
    	SchOptModeDo somDo = new SchOptModeDo(); 
        String sql = "select canteen_mode from t_edu_school where id = " + "'" + id + "'" + " and stat = 1 and reviewed = 1";
        jdbcTemplate1.query(sql, new RowCallbackHandler(){
        	public void processRow(ResultSet rs) throws SQLException{
        		somDo.setCanteenMode(rs.getInt("canteen_mode"));
        	}
        });
        int curOptMode = somDo.getCanteenMode();
        if(curOptMode == 0)
        	somDo.setStrCanteenMode("Self_support");
        else if(curOptMode == 1)
        	somDo.setStrCanteenMode("outsource");
        else if(curOptMode == 2)
        	somDo.setStrCanteenMode("blend");
        else
        	somDo.setStrCanteenMode("");
        
        return somDo;
    }
    
    //从数据源ds1的数据表t_edu_school中查找school_nature以id
    public SchOwnershipDo getSchOwnByDs1Id(String id){
    	if(jdbcTemplate1 == null)
    		return null;
    	SchOwnershipDo soDo = new SchOwnershipDo(); 
    	String[] schOwnTypes = { "Domestic_public_office", "Nation_public_office", "Domestic_civilian_office", "Nation_civilian_office" };
        String sql = "select school_nature from t_edu_school where id = " + "'" + id + "'" + " and stat = 1 and reviewed = 1";
        jdbcTemplate1.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		soDo.setSchoolNature(rs.getString("school_nature"));
        	}   
        });
        String curStrOwn = soDo.getSchoolNature();
        int curOwn = 0;
        if(curStrOwn != null) {
        	if(!curStrOwn.isEmpty()) {
        		String[] curStrOwns = curStrOwn.split(",");
        		curOwn = Integer.valueOf(curStrOwns[0]);
        	}
        }
        if(curOwn == 0)
        	soDo.setStrOwnership(schOwnTypes[0]);
        else if(curOwn == 1)
        	soDo.setStrOwnership(schOwnTypes[2]);
        else if(curOwn == 2)
        	soDo.setStrOwnership(schOwnTypes[1]);
        else if(curOwn == 3)
        	soDo.setStrOwnership(schOwnTypes[3]);
        
        return soDo;
    }
    
    //从数据源ds1的数据表t_edu_school中查找level以id
    public SchTypeDo getSchTypeByDs1Id(String id){
    	if(jdbcTemplate1 == null)
    		return null;
    	SchTypeDo stDo = new SchTypeDo(); 
    	String[] schTypes = { "Nursery", "Kindergarten", "Primary_school", "middle_school", "high_school", "Vocational_school", "Other" };
        String sql = "select level from t_edu_school where id = " + "'" + id + "'" + " and stat = 1 and reviewed = 1";
        jdbcTemplate1.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		stDo.setLevel(rs.getString("level"));
        	}   
        });
        String curSchType = stDo.getLevel();
        String [] curSchTypes = curSchType.split(",");
        for(int i = 0; i < curSchTypes.length; i++) {
        	if(curSchTypes[i].compareTo("0") == 0)
        		stDo.setSchType(schTypes[1]);
        	else if(curSchTypes[i].compareTo("1") == 0)
        		stDo.setSchType(schTypes[2]);
        	else if(curSchTypes[i].compareTo("2") == 0)
        		stDo.setSchType(schTypes[3]);
        	else if(curSchTypes[i].compareTo("3") == 0)
        		stDo.setSchType(schTypes[4]);
        	else if(curSchTypes[i].compareTo("6") == 0)
        		stDo.setSchType(schTypes[5]);
        	else if(curSchTypes[i].compareTo("7") == 0)
        		stDo.setSchType(schTypes[0]);
        	else if(curSchTypes[i].compareTo("9") == 0)
        		stDo.setSchType(schTypes[6]);
        }
        
        return stDo;
    }
    
    //从数据源ds1的数据表t_edu_school中查找所有id以区域ID（空时在查询所有）
    public List<SchIdNameDo> getSchIdListByDs1(String distId) {
    	if(mybatisUseFlag) {
    		if(distId == null)
    			return tesDoMapper.getSchIdListByDs1();
    		else
    			return tesDoMapper.getSchIdListByDs1DistId(distId);
    	}
    	else {
    		if(jdbcTemplate1 == null)
    			return null;
    		String sql = null;
    		if(distId == null)
    			sql = "select distinct id, school_name from t_edu_school" + " where stat = 1 and reviewed = 1";
    		else
    			sql = "select distinct id, school_name from t_edu_school" + " where area = '" + distId + "'" + " and stat = 1 and reviewed = 1";
    		logger.info("sql语句：" + sql);
    		return (List<SchIdNameDo>) jdbcTemplate1.query(sql, new RowMapper<SchIdNameDo>(){

    			@Override
    			public SchIdNameDo mapRow(ResultSet rs, int rowNum) throws SQLException {
    				SchIdNameDo siDo = new SchIdNameDo();
    				siDo.setId(rs.getString("id"));
    				siDo.setName(rs.getString("school_name"));
    				return siDo;
    			}
    		});
    	}
    }
    
    //从数据源ds1的数据表t_edu_school中查找所有id以区域ID（空时在查询所有）和输出字段方法
    public List<TEduSchoolDo> getTEduSchoolDoListByDs1(String distId,Integer stat,Integer reviewed, int outMethod) {
    	if(jdbcTemplate1 == null)
    		return null;
        String sql = null;
        if(distId == null) {
        	if(outMethod == 0) {        //输出学校ID、学校名称（项目点名称）
        		sql = "select distinct id, school_name, area,department_id from t_edu_school" + " where 1=1 ";
        	}else if(outMethod == 1) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、学校性质、食堂经营模式、证件主体、供餐模式
        		sql = "select distinct id, school_name, area, level, school_nature, canteen_mode, ledger_type, level2, license_main_type, license_main_child,department_id from t_edu_school" + " where 1=1 ";
        	}else if(outMethod == 2) {   //输出输出学校ID、学校名称（项目点名称）、详细地址、区域ID、学校学制、学校性质、食堂经营模式、证件主体、供餐模式
        		sql = "select distinct id, school_name, address, area, level, school_nature, canteen_mode, ledger_type, level2, license_main_type, license_main_child,department_id from t_edu_school" + " where 1=1 ";
        	}else if(outMethod == 3) {   //输出输出学校ID、学校名称（项目点名称）、详细地址、区域ID、学校学制、学校性质、食堂经营模式、总校ID、食品许可证件主体的类型(1学校|2外包)、供餐模式:20181024新增:1自行加工,2食品加工商3快餐配送,4现场加工
        		sql = "select distinct id, school_name, address, area, level, school_nature, canteen_mode, ledger_type, level2, parent_id, "
        				+ "license_main_type, license_main_child, school_area_id,department_master_id, department_slave_id,department_id from t_edu_school" + " where 1=1 ";
        	}else if(outMethod == 4) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、所属区、所属级别、主管部门、证件主体、供餐模式
        		sql = "select distinct id, school_name, area, level, level2, school_area_id, department_master_id, department_slave_id, license_main_type, license_main_child,department_id from t_edu_school" + " where 1=1 ";
        	}else if(outMethod == 5) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、学校性质、所属区ID、所属级别、主管部门、是否分校、关联的总校、证件主体、供餐模式
        		sql = "select distinct id, school_name, area, level, level2, school_nature, school_area_id, department_master_id, department_slave_id,"
        				+ " is_branch_school, parent_id, license_main_type, license_main_child,department_head as departmentHead ,department_mobilephone as departmentMobilephone,address,department_id "
        				+ "from t_edu_school" + " where 1=1 ";
        		
        		if(stat !=null ) {
        			sql += " and stat = "+stat;
        		}
        		
        		if(reviewed !=null ) {
        			sql += " and reviewed = "+reviewed;
        		}
        	}else if(outMethod == 6) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、所属区、所属级别、主管部门、部门负责人、部门邮件
        		sql = "select distinct id, school_name, area, level, level2, school_area_id, department_master_id, department_slave_id, department_head, department_mobilephone, department_email,department_id from t_edu_school" + " where 1=1 ";
        	}
       }else {
        	if(outMethod == 0) {        //输出学校ID、学校名称（项目点名称）
        		sql = "select distinct id, school_name, area,department_id from t_edu_school" + " where area = '" + distId + "'" + "  ";
        	}else if(outMethod == 1) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、学校性质、食堂经营模式、证件主体、供餐模式
        		sql = "select distinct id, school_name, area, level, school_nature, canteen_mode, ledger_type, level2, license_main_type, license_main_child,department_id from t_edu_school" + " where area = '" + distId + "'" + "  ";
        	}else if(outMethod == 2) {   //输出输出学校ID、学校名称（项目点名称）、详细地址、区域ID、学校学制、学校性质、食堂经营模式、证件主体、供餐模式
        		sql = "select distinct id, school_name, address, area, level, school_nature, canteen_mode, ledger_type, level2, license_main_type, license_main_child,department_id from t_edu_school" + " where area = '" + distId + "'" + "  ";
        	}else if(outMethod == 3) {   //输出输出学校ID、学校名称（项目点名称）、详细地址、区域ID、学校学制、学校性质、食堂经营模式、总校ID、食品许可证件主体的类型(1学校|2外包)、供餐模式:20181024新增:1自行加工,2食品加工商3快餐配送,4现场加工
        		sql = "select distinct id, school_name, address, area, level, school_nature, canteen_mode, ledger_type, "
        				+ "level2, parent_id, license_main_type, license_main_child, school_area_id,department_master_id, department_slave_id,department_id from t_edu_school" + " where area = '" + distId + "'" + "  ";
        	}else if(outMethod == 4) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、所属区、所属级别、主管部门、证件主体、供餐模式
        		sql = "select distinct id, school_name, area, level, level2, school_area_id, department_master_id, department_slave_id, license_main_type, license_main_child,department_id from t_edu_school" + " where area = '" + distId + "'" + "  ";
        	}else if(outMethod == 5) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、学校性质、所属区ID、所属级别、主管部门、是否分校、关联的总校、证件主体、供餐模式
        		sql = "select distinct id, school_name, area, level, level2, school_nature, school_area_id, department_master_id, department_slave_id, "
        				+ "is_branch_school, parent_id, license_main_type, license_main_child ,department_head as departmentHead ,department_mobilephone as departmentMobilephone,address,department_id "
        				+ "from t_edu_school" + " where area = '" + distId + "'" + " ";
        	}else if(outMethod == 6) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、所属区、所属级别、主管部门、部门负责人、部门邮件
        		sql = "select distinct id, school_name, area, level, level2, school_area_id, department_master_id, department_slave_id, department_head, department_mobilephone, department_email,department_id from t_edu_school" + " where area = '" + distId + "'";
        	}
        }
        
		if(stat !=null ) {
			sql += " and stat = "+stat;
		}
		
		if(reviewed !=null ) {
			sql += " and reviewed = "+reviewed;
		}
		
        logger.info("sql语句：" + sql);
        return (List<TEduSchoolDo>) jdbcTemplate1.query(sql, new RowMapper<TEduSchoolDo>(){

            @Override
            public TEduSchoolDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduSchoolDo tesDo = new TEduSchoolDo();
            	if(outMethod == 0) {      //输出学校ID、学校名称（项目点名称）
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setDepartmentId(rs.getString("department_id"));
            	}
            	else if(outMethod == 1) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、学校性质、食堂经营模式
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setLevel(rs.getString("level"));
            		tesDo.setSchoolNature(rs.getString("school_nature"));
            		tesDo.setCanteenMode(rs.getShort("canteen_mode"));
            		tesDo.setLedgerType(rs.getString("ledger_type"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setLicenseMainType(rs.getString("license_main_type"));
            		if(rs.getObject("license_main_child") != null)
            			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));
            		tesDo.setDepartmentId(rs.getString("department_id"));
            	}
            	else if(outMethod == 2) {   //输出输出学校ID、学校名称（项目点名称）、详细地址、区域ID、学校学制、学校性质、食堂经营模式
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setAddress(rs.getString("address"));
            		tesDo.setLevel(rs.getString("level"));
            		tesDo.setSchoolNature(rs.getString("school_nature"));
            		tesDo.setCanteenMode(rs.getShort("canteen_mode"));
            		tesDo.setLedgerType(rs.getString("ledger_type"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setLicenseMainType(rs.getString("license_main_type"));
            		if(rs.getObject("license_main_child") != null)
            			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));
            		tesDo.setDepartmentId(rs.getString("department_id"));
            	}
            	else if(outMethod == 3) {   //输出输出学校ID、学校名称（项目点名称）、详细地址、区域ID、学校学制、学校性质、食堂经营模式、总校ID
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setAddress(rs.getString("address"));
            		tesDo.setLevel(rs.getString("level"));
            		tesDo.setSchoolNature(rs.getString("school_nature"));
            		tesDo.setCanteenMode(rs.getShort("canteen_mode"));
            		tesDo.setLedgerType(rs.getString("ledger_type"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setParentId(rs.getString("parent_id"));
            		tesDo.setLicenseMainType(rs.getString("license_main_type"));
            		if(rs.getObject("license_main_child") != null)
            			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));
            		tesDo.setSchoolAreaId(rs.getString("school_area_id"));
            		tesDo.setDepartmentMasterId(rs.getString("department_master_id"));
            		tesDo.setDepartmentSlaveId(rs.getString("department_slave_id"));
            		tesDo.setDepartmentId(rs.getString("department_id"));
            	}
            	else if(outMethod == 4) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、所属区、所属级别、主管部门
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setLevel(rs.getString("level"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setSchoolAreaId(rs.getString("school_area_id"));            		
            		tesDo.setDepartmentMasterId(rs.getString("department_master_id"));
            		tesDo.setDepartmentSlaveId(rs.getString("department_slave_id"));
            		tesDo.setLicenseMainType(rs.getString("license_main_type"));
            		if(rs.getObject("license_main_child") != null)
            			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));
            	}
            	else if(outMethod == 5) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、学校性质、所属区ID、所属级别、主管部门、是否分校、关联的总校、证件主体
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setLevel(rs.getString("level"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setSchoolNature(rs.getString("school_nature"));
            		tesDo.setSchoolAreaId(rs.getString("school_area_id"));            		
            		tesDo.setDepartmentMasterId(rs.getString("department_master_id"));
            		tesDo.setDepartmentSlaveId(rs.getString("department_slave_id"));
            		if(rs.getObject("is_branch_school") != null)
            			tesDo.setIsBranchSchool(rs.getInt("is_branch_school"));
            		tesDo.setParentId(rs.getString("parent_id"));
            		tesDo.setLicenseMainType(rs.getString("license_main_type"));
            		if(rs.getObject("license_main_child") != null)
            			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));            		
            		tesDo.setDepartmentHead(rs.getString("departmentHead"));
            		tesDo.setDepartmentMobilephone(rs.getString("departmentMobilephone"));
            		tesDo.setAddress(rs.getString("address"));
            		tesDo.setDepartmentId(rs.getString("department_id"));
            	}
            	else if(outMethod == 6) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、所属区、所属级别、主管部门、部门负责人、部门邮件
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setLevel(rs.getString("level"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setSchoolAreaId(rs.getString("school_area_id"));            		
            		tesDo.setDepartmentMasterId(rs.getString("department_master_id"));
            		tesDo.setDepartmentSlaveId(rs.getString("department_slave_id"));
            		tesDo.setDepartmentHead(rs.getString("department_head"));
            		tesDo.setDepartmentMobilephone(rs.getString("department_mobilephone"));
            		tesDo.setDepartmentEmail(rs.getString("department_email"));
            		tesDo.setDepartmentId(rs.getString("department_id"));
            	}
                
                return tesDo;
            }
        });
    }
    
    //从数据源ds1的数据表t_edu_school中查找学校信息以学校id
    public TEduSchoolDo getTEduSchoolDoBySchId(String SchId, int outMethod) {
    	if(jdbcTemplate1 == null)
    		return null;
        String sql = null;
        if(outMethod == 0)        //输出学校ID、学校名称（项目点名称）
        	sql = "select distinct id, school_name, area from t_edu_school" + " where id = '" + SchId + "'" + " and stat = 1 and reviewed = 1 limit 1";
        else if(outMethod == 1)   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、学校性质、食堂经营模式、证件主体、供餐模式
        	sql = "select distinct id, school_name, area, level, school_nature, canteen_mode, ledger_type, level2, license_main_type, license_main_child from t_edu_school" + " where id = '" + SchId + "'" + " and stat = 1 and reviewed = 1 limit 1";
        else if(outMethod == 2)   //输出输出学校ID、学校名称（项目点名称）、详细地址、区域ID、学校学制、学校性质、食堂经营模式、证件主体、供餐模式
        	sql = "select distinct id, school_name, address, area, level, school_nature, canteen_mode, ledger_type, level2, license_main_type, license_main_child from t_edu_school" + " where id = '" + SchId + "'" + " and stat = 1 and reviewed = 1 limit 1";
        else if(outMethod == 3)   //输出输出学校ID、学校名称（项目点名称）、详细地址、区域ID、学校学制、学校性质、食堂经营模式、总校ID、食品许可证件主体的类型(1学校|2外包)、供餐模式:20181024新增:1自行加工,2食品加工商3快餐配送,4现场加工
        	sql = "select distinct id, school_name, address, area, level, school_nature, canteen_mode, ledger_type, level2, parent_id, license_main_type, license_main_child, school_area_id from t_edu_school" + " where id = '" + SchId + "'" + " and stat = 1 and reviewed = 1 limit 1";
        else if(outMethod == 4)   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、所属区、所属级别、主管部门、证件主体、供餐模式
        	sql = "select distinct id, school_name, area, level, level2, school_area_id, department_master_id, department_slave_id, license_main_type, license_main_child from t_edu_school" + " where id = '" + SchId + "'" + " and stat = 1 and reviewed = 1 limit 1";
        else if(outMethod == 5)   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、学校性质、所属区ID、所属级别、主管部门、是否分校、关联的总校、证件主体、供餐模式
        	sql = "select distinct id, school_name, area, level, level2, school_nature, school_area_id, department_master_id, department_slave_id, "
        				+ "is_branch_school, parent_id, license_main_type, license_main_child ,department_head as departmentHead ,department_mobilephone as departmentMobilephone,address "
        				+ "from t_edu_school" + " where id = '" + SchId + "'" + " and stat = 1 and reviewed = 1 limit 1";
        else if(outMethod == 6)   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、所属区、所属级别、主管部门、部门负责人、部门邮件
        	sql = "select distinct id, school_name, area, level, level2, school_area_id, department_master_id, department_slave_id, department_head, department_mobilephone, department_email from t_edu_school" + " where id = '" + SchId + "'" + " and stat = 1 and reviewed = 1 limit 1";
        logger.info("sql语句：" + sql);
        TEduSchoolDo tesDo = new TEduSchoolDo();
        jdbcTemplate1.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		if(outMethod == 0) {      //输出学校ID、学校名称（项目点名称）
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            	}
            	else if(outMethod == 1) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、学校性质、食堂经营模式
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setLevel(rs.getString("level"));
            		tesDo.setSchoolNature(rs.getString("school_nature"));
            		tesDo.setCanteenMode(rs.getShort("canteen_mode"));
            		tesDo.setLedgerType(rs.getString("ledger_type"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setLicenseMainType(rs.getString("license_main_type"));
            		if(rs.getObject("license_main_child") != null)
            			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));
            	}
            	else if(outMethod == 2) {   //输出输出学校ID、学校名称（项目点名称）、详细地址、区域ID、学校学制、学校性质、食堂经营模式
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setAddress(rs.getString("address"));
            		tesDo.setLevel(rs.getString("level"));
            		tesDo.setSchoolNature(rs.getString("school_nature"));
            		tesDo.setCanteenMode(rs.getShort("canteen_mode"));
            		tesDo.setLedgerType(rs.getString("ledger_type"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setLicenseMainType(rs.getString("license_main_type"));
            		if(rs.getObject("license_main_child") != null)
            			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));
            	}
            	else if(outMethod == 3) {   //输出输出学校ID、学校名称（项目点名称）、详细地址、区域ID、学校学制、学校性质、食堂经营模式、总校ID
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setAddress(rs.getString("address"));
            		tesDo.setLevel(rs.getString("level"));
            		tesDo.setSchoolNature(rs.getString("school_nature"));
            		tesDo.setCanteenMode(rs.getShort("canteen_mode"));
            		tesDo.setLedgerType(rs.getString("ledger_type"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setParentId(rs.getString("parent_id"));
            		tesDo.setLicenseMainType(rs.getString("license_main_type"));
            		if(rs.getObject("license_main_child") != null)
            			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));
            		tesDo.setSchoolAreaId(rs.getString("school_area_id"));
            	}
            	else if(outMethod == 4) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、所属区、所属级别、主管部门
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setLevel(rs.getString("level"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setSchoolAreaId(rs.getString("school_area_id"));            		
            		tesDo.setDepartmentMasterId(rs.getString("department_master_id"));
            		tesDo.setDepartmentSlaveId(rs.getString("department_slave_id"));
            		tesDo.setLicenseMainType(rs.getString("license_main_type"));
            		if(rs.getObject("license_main_child") != null)
            			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));
            	}
            	else if(outMethod == 5) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、学校性质、所属区ID、所属级别、主管部门、是否分校、关联的总校、证件主体
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setLevel(rs.getString("level"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setSchoolNature(rs.getString("school_nature"));
            		tesDo.setSchoolAreaId(rs.getString("school_area_id"));            		
            		tesDo.setDepartmentMasterId(rs.getString("department_master_id"));
            		tesDo.setDepartmentSlaveId(rs.getString("department_slave_id"));
            		if(rs.getObject("is_branch_school") != null)
            			tesDo.setIsBranchSchool(rs.getInt("is_branch_school"));
            		tesDo.setParentId(rs.getString("parent_id"));
            		tesDo.setLicenseMainType(rs.getString("license_main_type"));
            		if(rs.getObject("license_main_child") != null)
            			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));            		
            		tesDo.setDepartmentHead(rs.getString("departmentHead"));
            		tesDo.setDepartmentMobilephone(rs.getString("departmentMobilephone"));
            		tesDo.setAddress(rs.getString("address"));
            	}
            	else if(outMethod == 6) {   //输出输出学校ID、学校名称（项目点名称）、区域ID、学校学制、所属区、所属级别、主管部门、部门负责人、部门邮件
            		tesDo.setId(rs.getString("id"));
            		tesDo.setSchoolName(rs.getString("school_name"));
            		tesDo.setArea(rs.getString("area"));
            		tesDo.setLevel(rs.getString("level"));
            		if(rs.getObject("level2") != null)
            			tesDo.setLevel2(rs.getInt("level2"));
            		tesDo.setSchoolAreaId(rs.getString("school_area_id"));            		
            		tesDo.setDepartmentMasterId(rs.getString("department_master_id"));
            		tesDo.setDepartmentSlaveId(rs.getString("department_slave_id"));
            		tesDo.setDepartmentHead(rs.getString("department_head"));
            		tesDo.setDepartmentMobilephone(rs.getString("department_mobilephone"));
            		tesDo.setDepartmentEmail(rs.getString("department_email"));
            	}
        	}
        });
    	
    	return tesDo;
    }
    
    //从数据源ds1的数据表t_edu_school中查找所有id以区域ID（空时在查询所有）和输出字段方法
    public List<TEduSchoolDo> getTEduSchoolDoListByDs1(List<Object> distIdList,Integer stat,Integer reviewed) {
    	if(jdbcTemplate1 == null)
    		return null;
        String sql = null;
		sql = "select distinct id, school_name, area, level, level2, school_nature, school_area_id, department_master_id, department_slave_id, "
				+ " is_branch_school, parent_id, license_main_type, license_main_child ,department_head as departmentHead ,"
				+ " department_mobilephone as departmentMobilephone,address, "
				+"  canteen_mode, ledger_type,department_id "
				+ "from t_edu_school" 
				+ " where 1=1 ";
		if(stat !=null ) {
			sql += " and stat = "+stat;
		}
		
		if(reviewed !=null ) {
			sql += " and reviewed = "+reviewed;
		}
		 if(distIdList!=null && distIdList.size()>0) {
			 sql += " and area in(";
			 for(int i =0;i<distIdList.size();i++) {
				 Object distId = distIdList.get(i);
				 if(i<(distIdList.size()-1)) {
					 sql +=  "'" + distId + "',";
				 }else {
					 sql +=  "'" + distId + "'";
				 }
			 }
			 
			 sql += ")";
		 }
        logger.info("sql语句：" + sql);
        return (List<TEduSchoolDo>) jdbcTemplate1.query(sql, new RowMapper<TEduSchoolDo>(){

            @Override
            public TEduSchoolDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduSchoolDo tesDo = new TEduSchoolDo();
        		tesDo.setId(rs.getString("id"));
        		tesDo.setSchoolName(rs.getString("school_name"));
        		tesDo.setArea(rs.getString("area"));
        		tesDo.setLevel(rs.getString("level"));
        		if(rs.getObject("level2") != null)
        			tesDo.setLevel2(rs.getInt("level2"));
        		tesDo.setSchoolNature(rs.getString("school_nature"));
        		tesDo.setSchoolAreaId(rs.getString("school_area_id"));            		
        		tesDo.setDepartmentMasterId(rs.getString("department_master_id"));
        		tesDo.setDepartmentSlaveId(rs.getString("department_slave_id"));
        		if(rs.getObject("is_branch_school") != null)
        			tesDo.setIsBranchSchool(rs.getInt("is_branch_school"));
        		tesDo.setParentId(rs.getString("parent_id"));
        		tesDo.setLicenseMainType(rs.getString("license_main_type"));
        		if(rs.getObject("license_main_child") != null)
        			tesDo.setLicenseMainChild(rs.getShort("license_main_child"));            		
        		tesDo.setDepartmentHead(rs.getString("departmentHead"));
        		tesDo.setDepartmentMobilephone(rs.getString("departmentMobilephone"));
        		tesDo.setAddress(rs.getString("address"));
        		tesDo.setCanteenMode(rs.getShort("canteen_mode"));
        		tesDo.setLedgerType(rs.getString("ledger_type"));
        		tesDo.setDepartmentId(rs.getString("department_id"));
                return tesDo;
            }
        });
    }
    
    //从数据源ds1的数据表t_edu_school中查找所有总校id以区域ID（空时在查询所有）
    public List<TEduSchoolDo> getGenSchIdNameListByDs1(String distId) {
    	if(jdbcTemplate1 == null)
    		return null;
        String sql = null;
        if(distId == null)
        	sql = "select distinct id, school_name from t_edu_school" + " where stat = 1" + " and is_branch_school = 0" + " and reviewed = 1";
        else
        	sql = "select distinct id, school_name from t_edu_school" + " where area = '" + distId + "'" + " and stat = 1" + " and is_branch_school = 0" + " and reviewed = 1";
        logger.info("sql语句：" + sql);
        return (List<TEduSchoolDo>) jdbcTemplate1.query(sql, new RowMapper<TEduSchoolDo>(){

            @Override
            public TEduSchoolDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduSchoolDo tesDo = new TEduSchoolDo();
            	tesDo.setId(rs.getString("id"));
            	tesDo.setSchoolName(rs.getString("school_name"));
                return tesDo;
            }
        });
    }
    
    //从数据源ds1的数据表t_edu_supervise_user中查找用户名和密码（sha1字符串）以用户名（账号）
    public TEduSuperviseUserDo getUserNamePassByUserName(String userName) {
    	if(jdbcTemplate1 == null)
    		return null;
    	TEduSuperviseUserDo tesuDo = new TEduSuperviseUserDo();
    	String sql = "select user_account,password from t_edu_supervise_user where user_account = " + "'" + userName + "'" + " and stat = 1";
        jdbcTemplate1.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		tesuDo.setUserAccount(rs.getString("user_account"));
        		tesuDo.setPassword(rs.getString("password"));
        	}
        });
    	
    	return tesuDo;
    }
    
    //更新生成的token到数据源ds1的数据表t_edu_supervise_user表中
    public boolean updateUserTokenToTEduSuperviseUser(String userName, String password, String token) {
    	if(jdbcTemplate1 == null)
    		return false;
    	String sql = "update t_edu_supervise_user set token = " + "'" + token + "'" + " where user_account = " + "'" + userName + "'" + " and password = " + "'" + password + "'" + " and stat = 1";
        jdbcTemplate1.execute(sql);
    	
    	return true;
    }
    
    //从数据源ds1的数据表t_edu_supervise_user中查找授权码以当前授权码
    public String getAuthCodeByCurAuthCode(String token) {
    	if(jdbcTemplate1 == null)
    		return null;
    	String retToken = null;
    	TEduSuperviseUserDo tesuDo = new TEduSuperviseUserDo();
    	String sql = "select token from t_edu_supervise_user where token = " + "'" + token + "'" + " and stat = 1";
        jdbcTemplate1.query(sql, new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		tesuDo.setToken(token);
        	}   
        });
        if(tesuDo.getToken() != null) {
        	retToken = tesuDo.getToken();
        }
        
        return retToken;
    }
    
	//-----------------证书列表（t_pro_license）---------------------------------------------------------
 	/**
  	 * 从mysql数据库saas_v1的数据表t_pro_license中根据条件查询数据列表
  	 */
    public List<SearchLicense> getLicenseList(String schName,String distName,List<String> relationIdList,List<String> supplierList,
    		List<Integer> licTypeList,Integer cerSource,List<Integer> cerSourceList,
    		Integer startNum,Integer endNum) {
    	if(jdbcTemplate1 == null)
    		return null;
		StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     id licId,lic_name licName, ");
        sb.append("     relation_id relationId, ");
        sb.append("     lic_no licNo,lic_type licType, ");
        sb.append("     DATE_FORMAT(lic_start_date, '%Y/%m/%d' ) licStartDate, ");
        sb.append("     DATE_FORMAT(lic_end_date, '%Y/%m/%d' ) licEndDate, ");
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
		return (List<SearchLicense>) jdbcTemplate1.query(sb.toString(), new RowMapper<SearchLicense>() {

			@Override
			public SearchLicense mapRow(ResultSet rs, int rowNum) throws SQLException {
				/*if(rowNum > (endNum-(startNum==0?1:startNum))) {
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
 	
 	/**
  	 * 从数据库app_saas_v1的数据表t_pro_license中根据条件查询数据列表
  	 */
    public List<SearchLicense> getEmployeeLicenseList(String schName,String distName,List<String> relationIdList,List<String> supplierList,List<String> schoolSupplierIdList,
    		List<Integer> licTypeList,Integer cerSource,List<Integer> cerSourceList,
    		Integer startNum,Integer endNum) {
    	if(jdbcTemplate1 == null)
    		return null;
		StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     l.id licId,l.lic_name licName, ");
        sb.append("     l.relation_id relationId, ");
        sb.append("     l.lic_no licNo,l.lic_type licType, ");
        sb.append("     DATE_FORMAT(l.lic_start_date, '%Y/%m/%d' ) licStartDate, ");
        sb.append("     DATE_FORMAT(l.lic_end_date, '%Y/%m/%d' ) licEndDate, ");
        sb.append("     DATE_FORMAT(l.give_lic_date, '%Y/%m/%d' ) giveLicDate, ");
        sb.append("     l.written_name writtenName,l.stat stat,e.supplier_id supplierId,e.school_supplier_id schoolSupplierId ");
        sb.append(" FROM ");
        sb.append("     t_pro_employee e ");
        sb.append("     left join t_pro_license l on e.id = l.relation_id  ");
        sb.append(" where  1=1 ");
        getEmployeeLicenseCondition(schName, distName,relationIdList,supplierList,schoolSupplierIdList,licTypeList,cerSource,cerSourceList, sb);
        
        //sb.append(" order by distName asc,schType asc");
        /*if(endNum >0) {
        	sb.append(" limit "+endNum);
        }*/
        logger.info("执行sql:"+sb.toString());
		return (List<SearchLicense>) jdbcTemplate1.query(sb.toString(), new RowMapper<SearchLicense>() {

			@Override
			public SearchLicense mapRow(ResultSet rs, int rowNum) throws SQLException {
				/*if(rowNum > (endNum-(startNum==0?1:startNum))) {
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
				if(CommonUtil.isNotEmpty(rs.getString("giveLicDate"))) {
					sch.setLicStartDate(rs.getString("giveLicDate"));
				}
				//证书有效期截止日期
				sch.setLicEndDate("-");
				if(CommonUtil.isNotEmpty(rs.getString("licEndDate"))) {
					sch.setLicEndDate(rs.getString("licEndDate"));
				}
				
				//状态是否有效:0-无效,1-有效
				sch.setStat("-");
				/*if(CommonUtil.isNotEmpty(rs.getString("stat")) && CommonUtil.isInteger("stat")) {
					Integer stat = rs.getInt("stat");
					if(stat==1) {
						sch.setStat("有效");
					}else {
						sch.setStat("无效");
					}
				}*/
				
				//证书人名称
				sch.setWrittenName("-");
				if(CommonUtil.isNotEmpty(rs.getString("writtenName"))) {
					sch.setWrittenName(rs.getString("writtenName"));
				}
				
				if(CommonUtil.isNotEmpty(rs.getString("supplierId"))) {
					sch.setSupplierId(rs.getString("supplierId"));
				}
				
				if(CommonUtil.isNotEmpty(rs.getString("schoolSupplierId"))) {
					sch.setSchoolSupplierId(rs.getString("schoolSupplierId"));
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
 	private void getEmployeeLicenseCondition(String schName, String distName,List<String> relationIdList,List<String> supplierList,List<String> schoolSupplierIdList,
 			List<Integer> licTypeList,
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
 	            sb.append("  l.relation_id in (" + relationIds + ")");
 	            
 	            if(ednIndex <relationIdList.size()-1) {
 	            	sb.append(" or ");
 	            }
 	        }
 	       sb.append(" ) ");
 		}
 		
 		//团餐公司和学校关联关系表
 		if(schoolSupplierIdList!=null && schoolSupplierIdList.size() >0) {
 			 sb.append(" and ( ");
 	        for (int i = 0; i < (schoolSupplierIdList.size() / 800 + ((schoolSupplierIdList.size() % 800) > 0 ? 1 : 0)); i++) {
 	            int startIndex = i * 800;
 	            if (startIndex >= schoolSupplierIdList.size()) {
 	                startIndex = schoolSupplierIdList.size() - 1;
 	            }
 	            int ednIndex = (i + 1) * 800;
 	            if (ednIndex >= schoolSupplierIdList.size()) {
 	                ednIndex = schoolSupplierIdList.size();
 	            }
 	            String relationIds = StringUtils.join(schoolSupplierIdList.subList(startIndex, ednIndex).toArray(), ",");
 	            relationIds="\""+relationIds.replaceAll(",", "\",\"")+"\"";
 	            sb.append("  e.school_supplier_id in (" + relationIds + ")");
 	            
 	            if(ednIndex <schoolSupplierIdList.size()-1) {
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
 	            sb.append("  e.supplier_id in (" + relationIds + ")");
 	            
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
 		        sb.append(" l.lic_type = \"" +licType+"\"");
 		        
 		        if(!licType.equals(licTypeList.get(licTypeList.size() - 1))) {
 		        	sb.append(" or ");
 		        }
 			}
 			
 			sb.append(") ");
 		}
 		//证书来源，0：供应商-，1：从业人员-雇员，2：商品，3：食堂--教委web学校的法人证
 		if(cerSource!=null && cerSource >0) {
 			sb.append(" and l.cer_source = \""+cerSource+"\"");
 		}
 		
 		if(cerSourceList!=null && cerSourceList.size() > 0) {
 			sb.append(" AND (");
 			for(Integer licType : cerSourceList) {
 				if(licType == null) {
 					continue;
 				}
 		        sb.append(" l.cer_source = \"" +licType+"\"");
 		        
 		        if(!licType.equals(cerSourceList.get(cerSourceList.size() - 1))) {
 		        	sb.append(" or ");
 		        }
 			}
 			
 			sb.append(") ");
 		}
 		
 		//是否失效
 		sb.append(" and l.stat = \"1\"");
 		//是否审核通过(暂时不作过滤)
 		//sb.append(" and reviewed = \"1\"");

 		
 	}

 	//-------------------管理部门（t_edu_bd_department）
    /**
    * 从数据库app_saas_v1的数据表t_edu_bd_department中根据条件查询数据列表
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public List<DepartmentObj> getDepartmentObjList(DepartmentObj inputObj,List<Object> departmentIdList,
    		Integer startNum,Integer endNum) {
        logger.info("[Enter dao method] {}-{}", "DepartmentObjDao", "getDepartmentObjList");
    	if(jdbcTemplate1 == null)
    		return null;
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append("department_id departmentId, ");
        sb.append("  department_name departmentName, remark remark, creator creator, create_time createTime, ");
        sb.append("  updater updater, last_update_time lastUpdateTime ");
        sb.append(" from t_edu_bd_department " );
        sb.append(" where  1=1 ");
        getDepartmentObjListCondition(inputObj,departmentIdList,sb);
        sb.append("  order by sort_id asc   ");
        if(startNum !=null && endNum !=null && startNum!=-1 &&  endNum != -1) {
        	sb.append(" limit "+endNum);
        }        logger.info("执行sql:"+sb.toString());
		return (List<DepartmentObj>) jdbcTemplate1.query(sb.toString(), new RowMapper<DepartmentObj>() {
			@Override
			public DepartmentObj mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				DepartmentObj obj = new DepartmentObj();

				obj.setDepartmentId("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentId"))) {
					obj.setDepartmentId(rs.getString("departmentId"));
				}
				obj.setDepartmentName("-");
				if(CommonUtil.isNotEmpty(rs.getString("departmentName"))) {
					obj.setDepartmentName(rs.getString("departmentName"));
				}
				obj.setRemark("-");
				if(CommonUtil.isNotEmpty(rs.getString("remark"))) {
					obj.setRemark(rs.getString("remark"));
				}
				obj.setCreator("-");
				if(CommonUtil.isNotEmpty(rs.getString("creator"))) {
					obj.setCreator(rs.getString("creator"));
				}
				obj.setCreateTime("-");
				if(CommonUtil.isNotEmpty(rs.getString("createTime"))) {
					obj.setCreateTime(rs.getString("createTime"));
				}
				obj.setUpdater("-");
				if(CommonUtil.isNotEmpty(rs.getString("updater"))) {
					obj.setUpdater(rs.getString("updater"));
				}
				obj.setLastUpdateTime("-");
				if(CommonUtil.isNotEmpty(rs.getString("lastUpdateTime"))) {
					obj.setLastUpdateTime(rs.getString("lastUpdateTime"));
				}
				return obj;
			}
		});
    }

    /**
    * 从数据库app_saas_v1的数据表t_edu_bd_department中根据条件查询数据列表个数
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @return
    */
    public Integer getDepartmentObjListCount(DepartmentObj inputObj,List<Object> departmentIdList) {
        logger.info("[Enter dao method] {}-{}", "DepartmentObjDao", "getDepartmentObjListCount");
        Long daoStartTime = System.currentTimeMillis();
        if(jdbcTemplate1 == null)
    		return null;
    	
    	final Integer[] dataCounts={0};
    	
    	StringBuffer sb = new StringBuffer();
        sb.append("select count(1) dataCount ");
        sb.append(" from t_edu_bd_department" );
        sb.append(" where 1=1  ");
        
        getDepartmentObjListCondition(inputObj,departmentIdList,sb);
        logger.info("执行sql:"+sb.toString());
        jdbcTemplate1.query(sb.toString(), new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		dataCounts[0] = rs.getInt("dataCount");
        	}   
        });
        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
        return dataCounts[0];
    }

    /**
    * 从数据库app_saas_v1的数据表t_edu_bd_department中根据条件查询数据列表条件
    * @param listYearMonth
    * @param startDate
    * @param endDateAddOne
    * @param sb
    */
    public void getDepartmentObjListCondition(DepartmentObj inputObj,List<Object> departmentIdList,
    StringBuffer sb) {
        logger.info("[Enter dao method] {}-{}", "DepartmentObjDao", "getDepartmentObjListCondition");
        Long daoStartTime = System.currentTimeMillis();
        //departmentId
        if(inputObj!=null && CommonUtil.isNotEmpty(inputObj.getDepartmentId())) {
        	sb.append(" and department_id = \"" + inputObj.getDepartmentId()+"\"");
        }

        //departmentName
        if(inputObj!=null && CommonUtil.isNotEmpty(inputObj.getDepartmentName())) {
        	sb.append(" and department_name = \"" + inputObj.getDepartmentName()+"\"");
        }

        //remark
        if(inputObj!=null && CommonUtil.isNotEmpty(inputObj.getRemark())) {
        	sb.append(" and remark = \"" + inputObj.getRemark()+"\"");
        }

        //creator
        if(inputObj!=null && CommonUtil.isNotEmpty(inputObj.getCreator())) {
        	sb.append(" and creator = \"" + inputObj.getCreator()+"\"");
        }

        //createTime
        if(inputObj!=null && CommonUtil.isNotEmpty(inputObj.getCreateTime())) {
        	sb.append(" and create_time = \"" + inputObj.getCreateTime()+"\"");
        }

        //updater
        if(inputObj!=null && CommonUtil.isNotEmpty(inputObj.getUpdater())) {
        	sb.append(" and updater = \"" + inputObj.getUpdater()+"\"");
        }

        //lastUpdateTime
        if(inputObj!=null && CommonUtil.isNotEmpty(inputObj.getLastUpdateTime())) {
        	sb.append(" and last_update_time = \"" + inputObj.getLastUpdateTime()+"\"");
        }
        
        if(departmentIdList !=null && departmentIdList.size()>0) {
        	String departmentIds= departmentIdList.toString().substring(1,departmentIdList.toString().length()-1);
        	if(departmentIds.indexOf("\"") <0) {
        		departmentIds = "\""+departmentIds.replaceAll(",", "\",\"")+"\"";
        	}
        	sb.append(" and department_id in (" +departmentIds +")");
        }

        logger.info("Exec dao has takes {} millisecond.", System.currentTimeMillis() - daoStartTime);
    }

    /**
     * 修改学校管理部门
     */
    public boolean updateSchoolDepartMent(String id, String departmentId) {
    	if(jdbcTemplate1 == null)
    		return false;
    	String sql = "update t_edu_school set department_id = " + "'" + departmentId + "'" + " where id = " + "'" + id + "'";
    	logger.info("执行sql:"+sql.toString());
        jdbcTemplate1.execute(sql);
    	
    	return true;
    }
}
