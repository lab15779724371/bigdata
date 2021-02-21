package com.tfit.BdBiProcSrvShEduOmc.service.impl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.WarnLevelBody;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdMenuDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdRoleDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserPermDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSuperviseUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.EduBdInterfaceColumnsDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.EduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdBriKitStoveDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdComplaintDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdEtvidLibDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperContDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdExamPaperSubjectDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdFoodSafetyGradeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMailSrvDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdMsgNoticeDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdNoticeStatusDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdQuestionBodyDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.edubd.TEduBdQuestionCandAnsDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.EduBdInterfaceColumnsMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.EduBdUserDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdAddressLableMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdBriKitStoveDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdComplaintDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdEtvidLibDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdExamPaperContDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdExamPaperDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdExamPaperSubjectDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdFoodSafetyGradeDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdMailSrvDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdMsgNoticeDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdNoticeStatusDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdQuestionBodyDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdQuestionCandAnsDoMapper;
import com.tfit.BdBiProcSrvShEduOmc.dao.mapper.edubd.TEduBdUserLableRelationMapper;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdAddressLableObj;
import com.tfit.BdBiProcSrvShEduOmc.obj.base.TEduBdUserLableRelationObj;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

@Service
public class Db2ServiceImpl implements Db2Service {
	private static final Logger logger = LogManager.getLogger(Db1ServiceImpl.class.getName());
	
	@Autowired
	EduBdUserDoMapper ebuDoMapper;
	
	@Autowired
	TEduBdMsgNoticeDoMapper tebmnDoMapper;
	
	@Autowired
	TEduBdNoticeStatusDoMapper tebnsDoMapper;
	
	@Autowired
	TEduBdMailSrvDoMapper tebmsDoMapper;
	
	@Autowired
	TEduBdBriKitStoveDoMapper tebbksDoMapper;
	
	@Autowired
	EduBdInterfaceColumnsMapper  columnsMapper;
	
	@Autowired
	TEduBdEtvidLibDoMapper tebelDoMapper;
	
	@Autowired
	TEduBdFoodSafetyGradeDoMapper tebfsgDoMapper;
	
	@Autowired
	TEduBdComplaintDoMapper tebcpDoMapper;
	
	@Autowired
	TEduBdExamPaperDoMapper tebepDoMapper;
	
	@Autowired
	TEduBdExamPaperContDoMapper tebepcDoMapper;
	
	@Autowired
	TEduBdQuestionBodyDoMapper tebqbDoMapper;
	
	@Autowired
	TEduBdQuestionCandAnsDoMapper tebqcaDoMapper;
	
	@Autowired
	TEduBdExamPaperSubjectDoMapper tebepsDoMapper;
	
	@Autowired
	TEduBdAddressLableMapper tEduBdAddressLableMapper;
	
	@Autowired
	TEduBdUserLableRelationMapper tEduBdUserLableRelationMapper;
	
	//额外数据源2
    @Autowired
    @Qualifier("ds2")
    DataSource dataSource2;
    //额外数据源2连接模板
    JdbcTemplate jdbcTemplate2= null;
    
    //初始化处理标识，true表示已处理，false表示未处理
    boolean initProcFlag = false;
    
    //初始化处理
  	@Scheduled(fixedRate = 60*60*1000)
  	public void initProc() {
  		if(initProcFlag)
  			return ;
  		initProcFlag = true;
  		logger.info("定时建立与 DataSource数据源ds2对象表示的数据源的连接，时间：" + BCDTimeUtil.convertNormalFrom(null));
  		jdbcTemplate2 = new JdbcTemplate(dataSource2);
  	}
  	
    //从数据源ds2的数据表t_edu_supervise_user中查找用户名和密码（sha1字符串）以用户名（账号）
    public TEduSuperviseUserDo getUserNamePassByUserName(String userName) {
    	if(jdbcTemplate2 == null)
    		return null;
    	TEduSuperviseUserDo tesuDo = new TEduSuperviseUserDo();
    	String sql = "select user_account,password from t_edu_supervise_user where user_account = " + "'" + userName + "'" + " and stat = 1";
        jdbcTemplate2.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		tesuDo.setUserAccount(rs.getString("user_account"));
        		tesuDo.setPassword(rs.getString("password"));
        	}
        });
    	
    	return tesuDo;
    }
    
    //更新生成的token到数据源ds2的数据表t_edu_supervise_user表中
    public boolean updateUserTokenToTEduSuperviseUser(String userName, String password, String token) {
    	if(jdbcTemplate2 == null)
    		return false;
    	String sql = "update t_edu_supervise_user set token = " + "'" + token + "'" + " where user_account = " + "'" + userName + "'" + " and password = " + "'" + password + "'";
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //从数据源ds2的数据表t_edu_supervise_user中查找授权码以当前授权码
    public String getAuthCodeByCurAuthCode(String token) {
    	if(jdbcTemplate2 == null)
    		return null;
    	String retToken = null;
    	TEduSuperviseUserDo tesuDo = new TEduSuperviseUserDo();
    	String sql = "select token from t_edu_supervise_user where token = " + "'" + token + "'" + " and stat = 1";
        jdbcTemplate2.query(sql, new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		tesuDo.setToken(token);
        	}   
        });
        if(tesuDo.getToken() != null) {
        	retToken = tesuDo.getToken();
        }
        
        return retToken;
    }
  	
    //从数据源ds2的数据表t_edu_bd_user中查找用户信息
    public TEduBdUserDo getBdUserInfoByUserName(String userName) {
    	if(jdbcTemplate2 == null)
    		return null;
    	TEduBdUserDo tebuDo = new TEduBdUserDo();
    	String sql = "select id, user_account, password, email, fix_phone, mobile_phone,"
    			+ " name, user_pic_url, is_admin, role_id, parent_id, last_login_time, "
    			+ "creator, create_time, updater, last_update_time, forbid, token,"
    			+ " stat, remarks, org_id, org_name, "
    			+ "fax,user_type,error_password_count "
    			+ "from t_edu_bd_user "
    			+ "where user_account = " + "'" + userName + "'" + " and stat = 1";
    	logger.info("执行的MySql语句：" + sql);
    	jdbcTemplate2.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		tebuDo.setId(rs.getString("id"));
        		tebuDo.setUserAccount(rs.getString("user_account"));
        		tebuDo.setPassword(rs.getString("password"));
        		tebuDo.setEmail(rs.getString("email"));
        		tebuDo.setFixPhone(rs.getString("fix_phone"));
        		tebuDo.setMobilePhone(rs.getString("mobile_phone"));
        		tebuDo.setName(rs.getString("name"));
        		tebuDo.setUserPicUrl(rs.getString("user_pic_url"));
        		tebuDo.setIsAdmin(rs.getInt("is_admin"));
        		tebuDo.setRoleId(rs.getString("role_id"));
        		tebuDo.setParentId(rs.getString("parent_id"));
        		tebuDo.setLastLoginTime(rs.getString("last_login_time"));
        		tebuDo.setCreator(rs.getString("creator"));
        		tebuDo.setCreateTime(rs.getString("create_time"));
        		tebuDo.setUpdater(rs.getString("updater"));
        		tebuDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebuDo.setForbid(rs.getInt("forbid"));
        		tebuDo.setToken(rs.getString("token"));
        		tebuDo.setStat(rs.getInt("stat"));
        		tebuDo.setRemarks(rs.getString("remarks"));
        		tebuDo.setOrgId(rs.getString("org_id"));
        		tebuDo.setOrgName(rs.getString("org_name"));
        		tebuDo.setFax(rs.getString("fax"));
        		tebuDo.setUserType(1);
        		if(CommonUtil.isNotEmpty(rs.getString("fax"))) {
        			tebuDo.setUserType(rs.getInt("user_type"));
        		}
        		tebuDo.setErrorPasswordAccount(rs.getInt("error_password_count"));
        	}
        });
    	
    	return tebuDo;
    }
    
    //从数据源ds2的数据表t_edu_bd_user中查找授权码以当前授权码
    public TEduBdUserDo getBdUserInfoByCurAuthCode(String token) {
    	if(jdbcTemplate2 == null)
    		return null;
    	TEduBdUserDo tebuDo = new TEduBdUserDo();
    	String sql = "select id, user_account, password, email, fix_phone, mobile_phone, name, user_pic_url, is_admin, role_id, parent_id, last_login_time, creator, create_time, updater, last_update_time, forbid, token, stat, remarks, org_id, org_name, fax from t_edu_bd_user where token = " + "'" + token + "'" + " and stat = 1";
    	logger.info("执行的MySql语句：" + sql);
    	jdbcTemplate2.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		tebuDo.setId(rs.getString("id"));
        		tebuDo.setUserAccount(rs.getString("user_account"));
        		tebuDo.setPassword(rs.getString("password"));
        		tebuDo.setEmail(rs.getString("email"));
        		tebuDo.setFixPhone(rs.getString("fix_phone"));
        		tebuDo.setMobilePhone(rs.getString("mobile_phone"));
        		tebuDo.setName(rs.getString("name"));
        		tebuDo.setUserPicUrl(rs.getString("user_pic_url"));
        		tebuDo.setIsAdmin(rs.getInt("is_admin"));
        		tebuDo.setRoleId(rs.getString("role_id"));
        		tebuDo.setParentId(rs.getString("parent_id"));
        		tebuDo.setLastLoginTime(rs.getString("last_login_time"));
        		tebuDo.setCreator(rs.getString("creator"));
        		tebuDo.setCreateTime(rs.getString("create_time"));
        		tebuDo.setUpdater(rs.getString("updater"));
        		tebuDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebuDo.setForbid(rs.getInt("forbid"));
        		tebuDo.setToken(rs.getString("token"));
        		tebuDo.setStat(rs.getInt("stat"));
        		tebuDo.setRemarks(rs.getString("remarks"));
        		tebuDo.setOrgId(rs.getString("org_id"));
        		tebuDo.setOrgName(rs.getString("org_name"));
        		tebuDo.setFax(rs.getString("fax"));
        	}
        });
    	
    	return tebuDo;
    }
    
    //从数据源ds2的数据表t_edu_bd_user中查找授权码以当前授权码
    public String getAuthCodeByCurAuthCode2(String token) {
    	if(jdbcTemplate2 == null)
    		return null;
    	String retToken = null;
    	TEduSuperviseUserDo tesuDo = new TEduSuperviseUserDo();
    	String sql = "select token from t_edu_bd_user where token = " + "'" + token + "'" + " and stat = 1";
        jdbcTemplate2.query(sql, new RowCallbackHandler() {   
        	public void processRow(ResultSet rs) throws SQLException {
        		tesuDo.setToken(rs.getString("token"));
        	}   
        });
        if(tesuDo.getToken() != null) {
        	retToken = tesuDo.getToken();
        }
        
        return retToken;
    }
    
    //从数据源ds2的数据表t_edu_bd_user中查找所有用户信息
    public List<TEduBdUserDo> getAllBdUserInfo() {
    	if(jdbcTemplate2 == null)
    		return null;
    	String sql = "select id, user_account, password, email, fix_phone, mobile_phone, name, user_pic_url, is_admin, role_id, parent_id, last_login_time, creator, create_time, updater, last_update_time, forbid, token, stat, remarks, org_id, org_name, fax from t_edu_bd_user where" + " stat = 1";
    	logger.info("执行的MySql语句：" + sql);       
        return (List<TEduBdUserDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdUserDo>(){

            @Override
            public TEduBdUserDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduBdUserDo tebuDo = new TEduBdUserDo();
            	tebuDo.setId(rs.getString("id"));
        		tebuDo.setUserAccount(rs.getString("user_account"));
        		tebuDo.setPassword(rs.getString("password"));
        		tebuDo.setEmail(rs.getString("email"));
        		tebuDo.setFixPhone(rs.getString("fix_phone"));
        		tebuDo.setMobilePhone(rs.getString("mobile_phone"));
        		tebuDo.setName(rs.getString("name"));
        		tebuDo.setUserPicUrl(rs.getString("user_pic_url"));
        		tebuDo.setIsAdmin(rs.getInt("is_admin"));
        		tebuDo.setRoleId(rs.getString("role_id"));
        		tebuDo.setParentId(rs.getString("parent_id"));
        		tebuDo.setLastLoginTime(rs.getString("last_login_time"));
        		tebuDo.setCreator(rs.getString("creator"));
        		tebuDo.setCreateTime(rs.getString("create_time"));
        		tebuDo.setUpdater(rs.getString("updater"));
        		tebuDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebuDo.setForbid(rs.getInt("forbid"));
        		tebuDo.setToken(rs.getString("token"));
        		tebuDo.setStat(rs.getInt("stat"));
        		tebuDo.setRemarks(rs.getString("remarks"));
        		tebuDo.setOrgId(rs.getString("org_id"));
        		tebuDo.setOrgName(rs.getString("org_name"));
        		tebuDo.setFax(rs.getString("fax"));
                return tebuDo;
            }
        });
    }
    
    //从数据源ds2的数据表t_edu_bd_user中查找所有用户信息以父账户
    public List<TEduBdUserDo> getAllBdUserInfoByParentId(String id,String parentId,Integer userType) {
    	if(jdbcTemplate2 == null)
    		return null;
    	String sql = "select id, user_account, password, email,"
    			+ " fix_phone, mobile_phone, name, user_pic_url, "
    			+ "is_admin, role_id, parent_id, last_login_time, "
    			+ "creator, create_time, updater, last_update_time, "
    			+ "forbid, token, stat, remarks, org_id, org_name, "
    			+ "fax,user_type "
    			+ "from t_edu_bd_user "
    			+ "where 1=1 ";
    			if(parentId!=null) {
    				sql+= " and parent_id = "+ "'" + parentId+ "'" ;
    			}
    			if(id != null){
    				sql+= " and id != "+ "'" + id+ "'" ;
    			}
    			sql+= " and stat = 1"
    			+ " and user_type = " + userType;
    	logger.info("执行的MySql语句：" + sql);
        return (List<TEduBdUserDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdUserDo>(){

            @Override
            public TEduBdUserDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduBdUserDo tebuDo = new TEduBdUserDo();
            	tebuDo.setId(rs.getString("id"));
        		tebuDo.setUserAccount(rs.getString("user_account"));
        		tebuDo.setPassword(rs.getString("password"));
        		tebuDo.setEmail(rs.getString("email"));
        		tebuDo.setFixPhone(rs.getString("fix_phone"));
        		tebuDo.setMobilePhone(rs.getString("mobile_phone"));
        		tebuDo.setName(rs.getString("name"));
        		tebuDo.setUserPicUrl(rs.getString("user_pic_url"));
        		tebuDo.setIsAdmin(rs.getInt("is_admin"));
        		tebuDo.setRoleId(rs.getString("role_id"));
        		tebuDo.setParentId(rs.getString("parent_id"));
        		tebuDo.setLastLoginTime(rs.getString("last_login_time"));
        		tebuDo.setCreator(rs.getString("creator"));
        		tebuDo.setCreateTime(rs.getString("create_time"));
        		tebuDo.setUpdater(rs.getString("updater"));
        		tebuDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebuDo.setForbid(rs.getInt("forbid"));
        		tebuDo.setToken(rs.getString("token"));
        		tebuDo.setStat(rs.getInt("stat"));
        		tebuDo.setRemarks(rs.getString("remarks"));
        		tebuDo.setOrgId(rs.getString("org_id"));
        		tebuDo.setOrgName(rs.getString("org_name"));
        		tebuDo.setFax(rs.getString("fax"));
                return tebuDo;
            }
        });
    }
    
    //从数据源ds2的数据表t_edu_bd_user中查找用户信息以单位ID
    public List<TEduBdUserDo> getBdUserInfoByUserOrg(String orgId,Integer userType) {
    	if(jdbcTemplate2 == null || orgId == null)
    		return null;
    	String sql = "select id, user_account, password, email, fix_phone, mobile_phone, "
    			+ "name, user_pic_url, is_admin, role_id, parent_id, last_login_time, "
    			+ "creator, create_time, updater, last_update_time, forbid, token, stat, "
    			+ "remarks, org_id, org_name, fax from t_edu_bd_user "
    			+ "where "
    			+ "org_id = "+ "'" + orgId+ "'" 
    			+ " and stat = 1"
    			+ " and user_type = " + userType;
    	logger.info("执行的MySql语句：" + sql);
        return (List<TEduBdUserDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdUserDo>(){

            @Override
            public TEduBdUserDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduBdUserDo tebuDo = new TEduBdUserDo();
            	tebuDo.setId(rs.getString("id"));
        		tebuDo.setUserAccount(rs.getString("user_account"));
        		tebuDo.setPassword(rs.getString("password"));
        		tebuDo.setEmail(rs.getString("email"));
        		tebuDo.setFixPhone(rs.getString("fix_phone"));
        		tebuDo.setMobilePhone(rs.getString("mobile_phone"));
        		tebuDo.setName(rs.getString("name"));
        		tebuDo.setUserPicUrl(rs.getString("user_pic_url"));
        		tebuDo.setIsAdmin(rs.getInt("is_admin"));
        		tebuDo.setRoleId(rs.getString("role_id"));
        		tebuDo.setParentId(rs.getString("parent_id"));
        		tebuDo.setLastLoginTime(rs.getString("last_login_time"));
        		tebuDo.setCreator(rs.getString("creator"));
        		tebuDo.setCreateTime(rs.getString("create_time"));
        		tebuDo.setUpdater(rs.getString("updater"));
        		tebuDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebuDo.setForbid(rs.getInt("forbid"));
        		tebuDo.setToken(rs.getString("token"));
        		tebuDo.setStat(rs.getInt("stat"));
        		tebuDo.setRemarks(rs.getString("remarks"));
        		tebuDo.setOrgId(rs.getString("org_id"));
        		tebuDo.setOrgName(rs.getString("org_name"));
        		tebuDo.setFax(rs.getString("fax"));
                return tebuDo;
            }
        });
    }
    
    //插入记录到数据源ds2的数据表t_edu_bd_user中
    public boolean InsertBdUserInfo(TEduBdUserDo tebuDo) {
    	if(jdbcTemplate2 == null || tebuDo == null)
    		return false;    	
    	String sql = "", sql1 = "insert into t_edu_bd_user(", sql2 = " values(";
    	//主键id
    	if(tebuDo.getId() != null) {
    		sql1 += "id";
    		sql2 += "'" + tebuDo.getId() + "'";
    	}
    	//登录账户名
    	if(tebuDo.getUserAccount() != null) {
    		sql1 += ", user_account";
    		sql2 += ", '" + tebuDo.getUserAccount() + "'";
    	}
    	//密码
    	if(tebuDo.getPassword() != null) {
    		sql1 += ", password";
    		sql2 += ", '" + tebuDo.getPassword() + "'";
    	}               
    	//邮箱
    	if(tebuDo.getEmail() != null) {
    		sql1 += ", email";
    		sql2 += ", '" + tebuDo.getEmail() + "'";
    	}               
    	//固定电话
    	if(tebuDo.getFixPhone() != null) {
    		sql1 += ", fix_phone";
    		sql2 += ", '" + tebuDo.getFixPhone() + "'";
    	}             
    	//手机号码
    	if(tebuDo.getMobilePhone() != null) {
    		sql1 += ", mobile_phone";
    		sql2 += ", '" + tebuDo.getMobilePhone() + "'";
    	}         
    	//姓名
    	if(tebuDo.getName() != null) {
    		sql1 += ", name";
    		sql2 += ", '" + tebuDo.getName() + "'";
    	}              
    	//用户图片URL   
    	if(tebuDo.getUserPicUrl() != null) {
    		sql1 += ", user_pic_url";
    		sql2 += ", '" + tebuDo.getUserPicUrl() + "'";
    	}          
    	//0是false 1是true
    	if(tebuDo.getIsAdmin() != null) {
    		sql1 += ", is_admin";
    		sql2 += ", '" + tebuDo.getIsAdmin() + "'";
    	}         
    	//t_edu_bd_role  表id
    	if(tebuDo.getRoleId() != null) {
    		sql1 += ", role_id";
    		sql2 += ", '" + tebuDo.getRoleId() + "'";
    	}            
    	//账户父ID，空表示超级管理员账户，拥有最高权限
    	if(tebuDo.getParentId() != null) {
    		sql1 += ", parent_id";
    		sql2 += ", '" + tebuDo.getParentId() + "'";
    	}                     
    	//最后登录时间
    	if(tebuDo.getLastLoginTime() != null) {
    		sql1 += ", last_login_time";
    		sql2 += ", '" + tebuDo.getLastLoginTime() + "'";
    	}           
    	//创建者
    	if(tebuDo.getCreator() != null) {
    		sql1 += ", creator";
    		sql2 += ", '" + tebuDo.getCreator() + "'";
    	}       
    	//创建时间
    	if(tebuDo.getCreateTime() != null) {
    		sql1 += ", create_time";
    		sql2 += ", '" + tebuDo.getCreateTime() + "'";
    	}      
    	//更新人
    	if(tebuDo.getUpdater() != null) {
    		sql1 += ", updater";
    		sql2 += ", '" + tebuDo.getUpdater() + "'";
    	}          
    	//最近更新时间
    	if(tebuDo.getLastUpdateTime() != null) {
    		sql1 += ", last_update_time";
    		sql2 += ", '" + tebuDo.getLastUpdateTime() + "'";
    	}      
    	//是否禁用0禁用 1启用
    	if(tebuDo.getForbid() != null) {
    		sql1 += ", forbid";
    		sql2 += ", '" + tebuDo.getForbid() + "'";
    	}       
    	//用户授权码
    	if(tebuDo.getToken() != null) {
    		sql1 += ", token";
    		sql2 += ", '" + tebuDo.getToken() + "'";
    	}       
    	//是否有效 1 有效 0 无效
    	if(tebuDo.getStat() != null) {
    		sql1 += ", stat";
    		sql2 += ", '" + tebuDo.getStat() + "'";
    	}  
    	//备注
    	if(tebuDo.getRemarks() != null) {
    		sql1 += ", remarks";
    		sql2 += ", '" + tebuDo.getRemarks() + "'";
    	}
    	//组织ID
    	if(tebuDo.getOrgId() != null) {
    		sql1 += ", org_id";
    		sql2 += ", '" + tebuDo.getOrgId() + "'";
    	}
    	
    	//用户类型
    	if(tebuDo.getOrgId() != null) {
    		sql1 += ", user_type";
    		sql2 += ", " + tebuDo.getUserType();
    	}
    	
    	//组织名称
    	if(tebuDo.getOrgName() != null) {
    		sql1 += ", org_name";
    		sql2 += ", '" + tebuDo.getOrgName() + "'";
    	}
    	//传真
    	if(tebuDo.getFax() != null) {
    		sql1 += ", fax";
    		sql2 += ", '" + tebuDo.getFax() + "'";
    	}
    	sql1 += ")";
    	sql2 += ")";
    	sql = sql1 + sql2;
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //更新记录到数据源ds2的数据表t_edu_bd_user中
    public boolean UpdateBdUserInfo(TEduBdUserDo tebuDo, String token,boolean isClearnToken) {
    	if(jdbcTemplate2 == null || tebuDo == null)
    		return false;    	
    	String sql = "", sql1 = "update t_edu_bd_user set", sql2 = " ", sql3 = " where token = " + "'" + token + "'";
        boolean headFlag = false;
    	//主键id
    	if(tebuDo.getId() != null) {
    		sql2 += "id=";
    		sql2 += "'" + tebuDo.getId() + "'";
    		headFlag = true;
    	}
    	//登录账户名
    	if(tebuDo.getUserAccount() != null) {
    		if(headFlag)
    			sql2 += ", user_account=";
    		else {
    			sql2 += "user_account=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getUserAccount() + "'";
    	}
    	
    	//密码
    	if(isClearnToken) {
    		if(headFlag)
    			sql2 += ", token='' ";
    		else {
    			sql2 += "token='' ";
    			headFlag = true;
    		}
    	}     
    	
    	//密码
    	if(tebuDo.getPassword() != null) {
    		if(headFlag)
    			sql2 += ", password=";
    		else {
    			sql2 += "password=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getPassword() + "'";
    	}     
    	//安全等级
    	if(tebuDo.getSafeGrade() != null) {
    		if(headFlag)
    			sql2 += ", safe_grade=";
    		else {
    			sql2 += "safe_grade=";
    			headFlag = true;
    		}
    		sql2 += tebuDo.getSafeGrade();
    	}
    	//邮箱
    	if(tebuDo.getEmail() != null) {
    		if(headFlag)
    			sql2 += ", email=";
    		else {
    			sql2 += "email=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getEmail() + "'";
    	}               
    	//固定电话
    	if(tebuDo.getFixPhone() != null) {
    		if(headFlag)
    			sql2 += ", fix_phone=";
    		else {
    			sql2 += "fix_phone=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getFixPhone() + "'";
    	}             
    	//手机号码
    	if(tebuDo.getMobilePhone() != null) {
    		if(headFlag)
    			sql2 += ", mobile_phone=";
    		else {
    			sql2 += "mobile_phone=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getMobilePhone() + "'";
    	}         
    	//姓名
    	if(tebuDo.getName() != null) {
    		if(headFlag)
    			sql2 += ", name=";
    		else {
    			sql2 += "name=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getName() + "'";
    	}              
    	//用户图片URL   
    	if(tebuDo.getUserPicUrl() != null) {
    		if(headFlag)
    			sql2 += ", user_pic_url=";
    		else {
    			sql2 += "user_pic_url=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getUserPicUrl() + "'";
    	}          
    	//0是false 1是true
    	if(tebuDo.getIsAdmin() != null) {
    		if(headFlag)
    			sql2 += ", is_admin=";
    		else {
    			sql2 += "is_admin=";
    			headFlag = true;
    		}
    		sql2 += tebuDo.getIsAdmin();
    	}         
    	//t_edu_bd_role  表id
    	if(tebuDo.getRoleId() != null) {
    		if(headFlag)
    			sql2 += ", role_id=";
    		else {
    			sql2 += "role_id=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getRoleId() + "'";
    	}            
    	//账户父ID，空表示超级管理员账户，拥有最高权限
    	if(tebuDo.getParentId() != null) {
    		if(headFlag)
    			sql2 += ", parent_id=";
    		else {
    			sql2 += "parent_id=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getParentId() + "'";
    	}                     
    	//最后登录时间
    	if(tebuDo.getLastLoginTime() != null) {
    		if(headFlag)
    			sql2 += ", last_login_time=";
    		else {
    			sql2 += "last_login_time=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getLastLoginTime() + "'";
    	}           
    	//创建者
    	if(tebuDo.getCreator() != null) {
    		if(headFlag)
    			sql2 += ", creator=";
    		else {
    			sql2 += "creator=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getCreator() + "'";
    	}       
    	//创建时间
    	if(tebuDo.getCreateTime() != null) {
    		if(headFlag)
    			sql2 += ", create_time=";
    		else {
    			sql2 += "create_time=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getCreateTime() + "'";
    	}      
    	//更新人
    	if(tebuDo.getUpdater() != null) {
    		if(headFlag)
    			sql2 += ", updater=";
    		else {
    			sql2 += "updater=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getUpdater() + "'";
    	}          
    	//最近更新时间
    	if(tebuDo.getLastUpdateTime() != null) {
    		if(headFlag)
    			sql2 += ", last_update_time=";
    		else {
    			sql2 += "last_update_time=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getLastUpdateTime() + "'";
    	}      
    	//是否禁用0禁用 1启用
    	if(tebuDo.getForbid() != null) {
    		if(headFlag)
    			sql2 += ", forbid=";
    		else {
    			sql2 += "forbid=";
    			headFlag = true;
    		}
    		sql2 += tebuDo.getForbid();
    	}       
    	//用户授权码
    	if(tebuDo.getToken() != null) {
    		if(headFlag)
    			sql2 += ", token=";
    		else {
    			sql2 += "token=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getToken() + "'";
    	}       
    	//是否有效 1 有效 0 无效
    	if(tebuDo.getStat() != null) {
    		if(headFlag)
    			sql2 += ", stat=";
    		else {
    			sql2 += "stat=";
    			headFlag = true;
    		}
    		sql2 += tebuDo.getStat();
    	}  
    	//备注
    	if(tebuDo.getRemarks() != null) {
    		if(headFlag)
    			sql2 += ", remarks=";
    		else {
    			sql2 += "remarks=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getRemarks() + "'";
    	}
    	//组织ID
    	if(tebuDo.getOrgId() != null) {
    		if(headFlag)
    			sql2 += ", org_id=";
    		else {
    			sql2 += "org_id=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getOrgId() + "'";
    	}
    	//组织名称
    	if(tebuDo.getOrgName() != null) {    		
    		if(headFlag)
    			sql2 += ", org_name=";
    		else {
    			sql2 += "org_name=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getOrgName() + "'";
    	}
    	//传真
    	if(tebuDo.getFax() != null) {    		
    		if(headFlag)
    			sql2 += ", fax=";
    		else {
    			sql2 += "fax=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getFax() + "'";
    	}
    	sql = sql1 + sql2 + sql3;
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //更新记录到数据源ds2的数据表t_edu_bd_user中以输入字段
    public boolean UpdateBdUserInfoByField(TEduBdUserDo tebuDo, String fieldName, String fieldVal) {
    	if(jdbcTemplate2 == null || tebuDo == null)
    		return false;    	
    	String sql = "", sql1 = "update t_edu_bd_user set", sql2 = " ", sql3 = " where " + fieldName + " = " + "'" + fieldVal + "'" + " and stat = 1";
        boolean headFlag = false;
    	//主键id
    	if(tebuDo.getId() != null) {
    		sql2 += "id=";
    		sql2 += "'" + tebuDo.getId() + "'";
    		headFlag = true;
    	}
    	//登录账户名
    	if(tebuDo.getUserAccount() != null) {
    		if(headFlag)
    			sql2 += ", user_account=";
    		else {
    			sql2 += "user_account=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getUserAccount() + "'";
    	}
    	//密码
    	if(tebuDo.getPassword() != null) {
    		if(headFlag)
    			sql2 += ", password=";
    		else {
    			sql2 += "password=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getPassword() + "'";
    	}               
    	//邮箱
    	if(tebuDo.getEmail() != null) {
    		if(headFlag)
    			sql2 += ", email=";
    		else {
    			sql2 += "email=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getEmail() + "'";
    	}               
    	//固定电话
    	if(tebuDo.getFixPhone() != null) {
    		if(headFlag)
    			sql2 += ", fix_phone=";
    		else {
    			sql2 += "fix_phone=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getFixPhone() + "'";
    	}             
    	//手机号码
    	if(tebuDo.getMobilePhone() != null) {
    		if(headFlag)
    			sql2 += ", mobile_phone=";
    		else {
    			sql2 += "mobile_phone=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getMobilePhone() + "'";
    	}         
    	//姓名
    	if(tebuDo.getName() != null) {
    		if(headFlag)
    			sql2 += ", name=";
    		else {
    			sql2 += "name=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getName() + "'";
    	}              
    	//用户图片URL   
    	if(tebuDo.getUserPicUrl() != null) {
    		if(headFlag)
    			sql2 += ", user_pic_url=";
    		else {
    			sql2 += "user_pic_url=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getUserPicUrl() + "'";
    	}          
    	//0是false 1是true
    	if(tebuDo.getIsAdmin() != null) {
    		if(headFlag)
    			sql2 += ", is_admin=";
    		else {
    			sql2 += "is_admin=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getIsAdmin() + "'";
    	}         
    	//t_edu_bd_role  表id
    	if(tebuDo.getRoleId() != null) {
    		if(headFlag)
    			sql2 += ", role_id=";
    		else {
    			sql2 += "role_id=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getRoleId() + "'";
    	}            
    	//账户父ID，空表示超级管理员账户，拥有最高权限
    	if(tebuDo.getParentId() != null) {
    		if(headFlag)
    			sql2 += ", parent_id=";
    		else {
    			sql2 += "parent_id=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getParentId() + "'";
    	}                     
    	//最后登录时间
    	if(tebuDo.getLastLoginTime() != null) {
    		if(headFlag)
    			sql2 += ", last_login_time=";
    		else {
    			sql2 += "last_login_time=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getLastLoginTime() + "'";
    	}           
    	//创建者
    	if(tebuDo.getCreator() != null) {
    		if(headFlag)
    			sql2 += ", creator=";
    		else {
    			sql2 += "creator=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getCreator() + "'";
    	}       
    	//创建时间
    	if(tebuDo.getCreateTime() != null) {
    		if(headFlag)
    			sql2 += ", create_time=";
    		else {
    			sql2 += "create_time=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getCreateTime() + "'";
    	}      
    	//更新人
    	if(tebuDo.getUpdater() != null) {
    		if(headFlag)
    			sql2 += ", updater=";
    		else {
    			sql2 += "updater=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getUpdater() + "'";
    	}          
    	//最近更新时间
    	if(tebuDo.getLastUpdateTime() != null) {
    		if(headFlag)
    			sql2 += ", last_update_time=";
    		else {
    			sql2 += "last_update_time=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getLastUpdateTime() + "'";
    	}      
    	//是否禁用0禁用 1启用
    	if(tebuDo.getForbid() != null) {
    		if(headFlag)
    			sql2 += ", forbid=";
    		else {
    			sql2 += "forbid=";
    			headFlag = true;
    		}
    		sql2 += tebuDo.getForbid();
    	}       
    	//用户授权码
    	if(tebuDo.getToken() != null) {
    		if(headFlag)
    			sql2 += ", token=";
    		else {
    			sql2 += "token=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getToken() + "'";
    	}       
    	//是否有效 1 有效 0 无效
    	if(tebuDo.getStat() != null) {
    		if(headFlag)
    			sql2 += ", stat=";
    		else {
    			sql2 += "stat=";
    			headFlag = true;
    		}
    		sql2 += tebuDo.getStat();
    	}  
    	//备注
    	if(tebuDo.getRemarks() != null) {
    		if(headFlag)
    			sql2 += ", remarks=";
    		else {
    			sql2 += "remarks=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getRemarks() + "'";
    	}
    	//组织ID
    	if(tebuDo.getOrgId() != null) {
    		if(headFlag)
    			sql2 += ", org_id=";
    		else {
    			sql2 += "org_id=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getOrgId() + "'";
    	}
    	//组织名称
    	if(tebuDo.getOrgName() != null) {    		
    		if(headFlag)
    			sql2 += ", org_name=";
    		else {
    			sql2 += "org_name=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getOrgName() + "'";
    	}
    	
    	//
    	if(tebuDo.getErrorPasswordAccount()!= null) {
    		if(headFlag)
    			sql2 += ", error_password_count=";
    		else {
    			sql2 += "error_password_count=";
    			headFlag = true;
    		}
    		sql2 += tebuDo.getErrorPasswordAccount();
    	}      
    	
    	//传真
    	if(tebuDo.getFax() != null) {    		
    		if(headFlag)
    			sql2 += ", fax=";
    		else {
    			sql2 += "fax=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebuDo.getFax() + "'";
    	}
    	sql = sql1 + sql2 + sql3;
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //删除数据源ds2的数据表t_edu_bd_user中记录以用户名
    public boolean DeleteBdUserInfoByUserName(String userName) {
    	if(jdbcTemplate2 == null || userName == null)
    		return false;    	
    	String sql = "delete from t_edu_bd_user where user_account = " + "'" + userName + "'";
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //从数据源ds2的数据表t_edu_bd_user中查找用户信息以授权码token
    public TEduBdUserDo getBdUserInfoByToken(String token) {
    	if(jdbcTemplate2 == null)
    		return null;
    	TEduBdUserDo tebuDo = new TEduBdUserDo();
    	String sql = "select id, user_account, password, safe_grade, email, fix_phone, mobile_phone, name, "
    			+ "user_pic_url, is_admin, role_id, parent_id, last_login_time, creator, create_time, "
    			+ "updater, last_update_time, forbid, token, stat, remarks, org_id, org_name, "
    			+ "fax,user_type "
    			+ "from t_edu_bd_user where token = " + "'" + token + "'" + " and stat = 1";
    	logger.info("执行的MySql语句：" + sql);
    	jdbcTemplate2.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		tebuDo.setId(rs.getString("id"));
        		tebuDo.setUserAccount(rs.getString("user_account"));
        		tebuDo.setPassword(rs.getString("password"));
        		tebuDo.setSafeGrade(rs.getInt("safe_grade"));
        		tebuDo.setEmail(rs.getString("email"));
        		tebuDo.setFixPhone(rs.getString("fix_phone"));
        		tebuDo.setMobilePhone(rs.getString("mobile_phone"));
        		tebuDo.setName(rs.getString("name"));
        		tebuDo.setUserPicUrl(rs.getString("user_pic_url"));
        		tebuDo.setIsAdmin(rs.getInt("is_admin"));
        		tebuDo.setRoleId(rs.getString("role_id"));
        		tebuDo.setParentId(rs.getString("parent_id"));
        		tebuDo.setLastLoginTime(rs.getString("last_login_time"));
        		tebuDo.setCreator(rs.getString("creator"));
        		tebuDo.setCreateTime(rs.getString("create_time"));
        		tebuDo.setUpdater(rs.getString("updater"));
        		tebuDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebuDo.setForbid(rs.getInt("forbid"));
        		tebuDo.setToken(rs.getString("token"));
        		tebuDo.setStat(rs.getInt("stat"));
        		tebuDo.setRemarks(rs.getString("remarks"));
        		tebuDo.setOrgId(rs.getString("org_id"));
        		tebuDo.setOrgName(rs.getString("org_name"));
        		tebuDo.setFax(rs.getString("fax"));
        		tebuDo.setUserType(1);
        		if(CommonUtil.isNotEmpty(rs.getString("user_type"))) {
        			tebuDo.setUserType(rs.getInt("user_type"));
        		}
        	}
        });
    	
    	return tebuDo;
    }
    
    //插入记录到数据源ds2的数据表t_edu_bd_role中
    public boolean InsertBdRoleInfo(TEduBdRoleDo tebrDo) {
    	if(jdbcTemplate2 == null || tebrDo == null)
    		return false;    	
    	String sql = "", sql1 = "insert into t_edu_bd_role(", sql2 = " values(";
    	//主键id
    	if(tebrDo.getId() != null) {
    		sql1 += "id";
    		sql2 += "'" + tebrDo.getId() + "'";
    	}
    	//角色类型，1:监管部门，2:学校	
    	if(tebrDo.getRoleType() != null) {
    		sql1 += ", role_type";
    		sql2 += ", '" + tebrDo.getRoleType() + "'";
    	}
    	//角色名称
    	if(tebrDo.getRoleName() != null) {
    		sql1 += ", role_name";
    		sql2 += ", '" + tebrDo.getRoleName() + "'";
    	}
    	//创建时间
    	if(tebrDo.getCreateTime() != null) {
    		sql1 += ", create_time";
    		sql2 += ", '" + tebrDo.getCreateTime() + "'";
    	}             
    	//最后更新时间
    	if(tebrDo.getLastUpdateTime() != null) {
    		sql1 += ", last_update_time";
    		sql2 += ", '" + tebrDo.getLastUpdateTime() + "'";
    	}         
    	//创建人
    	if(tebrDo.getCreator() != null) {
    		sql1 += ", creator";
    		sql2 += ", '" + tebrDo.getCreator() + "'";
    	}    
    	//描述
    	if(tebrDo.getDiscrip() != null) {
    		sql1 += ", discrip";
    		sql2 += ", '" + tebrDo.getDiscrip() + "'";
    	}              	
    	//是否有效 1 有效 0 无效
    	if(tebrDo.getStat() != null) {
    		sql1 += ", stat";
    		sql2 += ", '" + tebrDo.getStat() + "'";
    	}    	
    	sql1 += ")";
    	sql2 += ")";
    	sql = sql1 + sql2;
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //更新记录到数据源ds2的数据表t_edu_bd_role中以输入字段
    public boolean UpdateBdRoleInfoByField(TEduBdRoleDo tebrDo, String fieldName, String fieldVal) {
    	if(jdbcTemplate2 == null || tebrDo == null)
    		return false;    	
    	String sql = "", sql1 = "update t_edu_bd_role set", sql2 = " ", sql3 = " where " + fieldName + " = " + "'" + fieldVal + "'" + " and stat = 1";
        boolean headFlag = false;
    	//主键id
    	if(tebrDo.getId() != null) {
    		sql2 += "id=";
    		sql2 += "'" + tebrDo.getId() + "'";
    		headFlag = true;
    	}
    	//角色类型，1:监管部门，2:学校
    	if(tebrDo.getRoleType() != null) {
    		if(headFlag)
    			sql2 += ", role_type=";
    		else {
    			sql2 += "role_type=";
    			headFlag = true;
    		}
    		sql2 += tebrDo.getRoleType();
    	}    	
    	//角色名称
    	if(tebrDo.getRoleName() != null) {
    		if(headFlag)
    			sql2 += ", role_name=";
    		else {
    			sql2 += "role_name=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebrDo.getRoleName() + "'";
    	}               
    	//创建时间
    	if(tebrDo.getCreateTime() != null) {
    		if(headFlag)
    			sql2 += ", create_time=";
    		else {
    			sql2 += "create_time=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebrDo.getCreateTime() + "'";
    	}             
    	//最后更新时间
    	if(tebrDo.getLastUpdateTime() != null) {
    		if(headFlag)
    			sql2 += ", last_update_time=";
    		else {
    			sql2 += "last_update_time=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebrDo.getLastUpdateTime() + "'";
    	} 
    	//创建人
    	if(tebrDo.getCreator() != null) {
    		if(headFlag)
    			sql2 += ", creator=";
    		else {
    			sql2 += "creator=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebrDo.getCreator() + "'";
    	} 
    	//描述
    	if(tebrDo.getDiscrip() != null) {
    		if(headFlag)
    			sql2 += ", discrip=";
    		else {
    			sql2 += "discrip=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebrDo.getDiscrip() + "'";
    	}    	
    	//是否有效 1 有效 0 无效
    	if(tebrDo.getStat() != null) {
    		if(headFlag)
    			sql2 += ", stat=";
    		else {
    			sql2 += "stat=";
    			headFlag = true;
    		}
    		sql2 += tebrDo.getStat();
    	}  
    	sql = sql1 + sql2 + sql3;
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色信息以id
    public TEduBdRoleDo getBdRoleInfoByRoleId(String id) {
    	if(jdbcTemplate2 == null)
    		return null;
    	TEduBdRoleDo tebrDo = new TEduBdRoleDo();
    	String sql = "select id, role_type, role_name, create_time, last_update_time, discrip, stat from t_edu_bd_role where id = " + "'" + id + "'" + " and stat = 1";
    	logger.info("执行的MySql语句：" + sql);
    	jdbcTemplate2.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		tebrDo.setId(rs.getString("id"));
        		tebrDo.setRoleType(rs.getInt("role_type"));
        		tebrDo.setRoleName(rs.getString("role_name"));
        		tebrDo.setCreateTime(rs.getString("create_time"));
        		tebrDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebrDo.setDiscrip(rs.getString("discrip"));
        		tebrDo.setStat(rs.getInt("stat"));
        	}
        });
    	
    	return tebrDo;
    }
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色信息以角色名称
    public TEduBdRoleDo getBdRoleInfoByRoleName(String roleName) {
    	if(jdbcTemplate2 == null)
    		return null;
    	TEduBdRoleDo tebrDo = new TEduBdRoleDo();
    	String sql = "select id, role_type, role_name, create_time, last_update_time, discrip, stat from t_edu_bd_role where role_name = " + "'" + roleName + "'" + " and stat = 1";
    	logger.info("执行的MySql语句：" + sql);
    	jdbcTemplate2.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		tebrDo.setId(rs.getString("id"));
        		tebrDo.setRoleType(rs.getInt("role_type"));
        		tebrDo.setRoleName(rs.getString("role_name"));
        		tebrDo.setCreateTime(rs.getString("create_time"));
        		tebrDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebrDo.setDiscrip(rs.getString("discrip"));
        		tebrDo.setStat(rs.getInt("stat"));
        	}
        });
    	
    	return tebrDo;
    }
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色信息以角色名称
    public List<TEduBdRoleDo> getBdRoleInfoByRoleName2(String roleName) {
    	if(jdbcTemplate2 == null)
    		return null;
    	String sql = "select id, role_type, role_name, create_time, last_update_time, discrip, stat from t_edu_bd_role where role_name = " + "'" + roleName + "'" + " and stat = 1";
    	logger.info("执行的MySql语句：" + sql);
    	return (List<TEduBdRoleDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdRoleDo>(){

    		@Override
    		public TEduBdRoleDo mapRow(ResultSet rs, int rowNum) throws SQLException {
    			TEduBdRoleDo tebrDo = new TEduBdRoleDo();
    			tebrDo.setId(rs.getString("id"));
    			tebrDo.setRoleType(rs.getInt("role_type"));
    			tebrDo.setRoleName(rs.getString("role_name"));
        		tebrDo.setCreateTime(rs.getString("create_time"));
        		tebrDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebrDo.setDiscrip(rs.getString("discrip"));
        		tebrDo.setStat(rs.getInt("stat"));
        		return tebrDo;
        	}
    	});
    }
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色信息以角色名称
    public TEduBdRoleDo getBdRoleInfoByRoleName3(int roleType, String roleName) {
    	if(jdbcTemplate2 == null)
    		return null;
    	TEduBdRoleDo tebrDo = new TEduBdRoleDo();
    	String sql = "select id, role_type, role_name, create_time, last_update_time, discrip, stat from t_edu_bd_role where role_name = " + "'" + roleName + "'" + " and stat = 1" + " and role_type = " + roleType;
    	logger.info("执行的MySql语句：" + sql);
    	jdbcTemplate2.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		tebrDo.setId(rs.getString("id"));
        		tebrDo.setRoleType(rs.getInt("role_type"));
        		tebrDo.setRoleName(rs.getString("role_name"));
        		tebrDo.setCreateTime(rs.getString("create_time"));
        		tebrDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebrDo.setDiscrip(rs.getString("discrip"));
        		tebrDo.setStat(rs.getInt("stat"));
        	}
        });
    	
    	return tebrDo;
    }
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色信息以角色名称
    public List<TEduBdRoleDo> getBdRoleInfoByRoleName4(int roleType, String roleName) {
    	if(jdbcTemplate2 == null)
    		return null;
    	String sql = "select id, role_type, role_name, create_time, last_update_time, discrip, stat from t_edu_bd_role where role_name = " + "'" + roleName + "'" + " and stat = 1" + " and role_type = " + roleType;
    	logger.info("执行的MySql语句：" + sql);
    	return (List<TEduBdRoleDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdRoleDo>(){

    		@Override
    		public TEduBdRoleDo mapRow(ResultSet rs, int rowNum) throws SQLException {
    			TEduBdRoleDo tebrDo = new TEduBdRoleDo();
    			tebrDo.setId(rs.getString("id"));
    			tebrDo.setRoleType(rs.getInt("role_type"));
    			tebrDo.setRoleName(rs.getString("role_name"));
        		tebrDo.setCreateTime(rs.getString("create_time"));
        		tebrDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebrDo.setDiscrip(rs.getString("discrip"));
        		tebrDo.setStat(rs.getInt("stat"));
        		return tebrDo;
        	}
    	});
    }
    
    //从数据源ds2的数据表t_edu_bd_role中查找所有角色名称
    public List<TEduBdRoleDo> getBdRoleInfoAllRoleNames() {
    	if(jdbcTemplate2 == null)
    		return null;
        String sql = null;
        sql = "select distinct role_name from t_edu_bd_role" + " where stat = 1";        
        logger.info("sql语句：" + sql);
        return (List<TEduBdRoleDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdRoleDo>(){

            @Override
            public TEduBdRoleDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduBdRoleDo tebrDo = new TEduBdRoleDo();
            	tebrDo.setRoleName(rs.getString("role_name"));
                return tebrDo;
            }
        });
    }
    
    //从数据源ds2的数据表t_edu_bd_role中查找角色名称以角色类型
    public List<TEduBdRoleDo> getBdRoleInfoRoleNamesByRoleType(int roleType) {
    	if(jdbcTemplate2 == null)
    		return null;
        String sql = null;
        sql = "select distinct role_name from t_edu_bd_role" + " where stat = 1" + " and role_type = " + roleType;        
        logger.info("sql语句：" + sql);
        return (List<TEduBdRoleDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdRoleDo>(){

            @Override
            public TEduBdRoleDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduBdRoleDo tebrDo = new TEduBdRoleDo();
            	tebrDo.setRoleName(rs.getString("role_name"));
                return tebrDo;
            }
        });
    }
    
    //从数据源ds2的数据表t_edu_bd_role中查找所有角色信息
    public List<TEduBdRoleDo> getAllBdRoleInfo() {
    	if(jdbcTemplate2 == null)
    		return null;
    	String sql = "select id, role_type, role_name, create_time, last_update_time, creator, discrip, stat from t_edu_bd_role where" + " stat = 1";    	
    	logger.info("执行的MySql语句：" + sql);       
        return (List<TEduBdRoleDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdRoleDo>(){

            @Override
            public TEduBdRoleDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduBdRoleDo tebrDo = new TEduBdRoleDo();
            	tebrDo.setId(rs.getString("id"));
            	tebrDo.setRoleType(rs.getInt("role_type"));
            	tebrDo.setRoleName(rs.getString("role_name"));
            	tebrDo.setCreateTime(rs.getString("create_time"));
            	tebrDo.setLastUpdateTime(rs.getString("last_update_time"));
            	tebrDo.setCreator(rs.getString("creator"));
            	tebrDo.setDiscrip(rs.getString("discrip"));
            	tebrDo.setStat(rs.getInt("stat"));
                return tebrDo;
            }
        });
    }    
    
    //删除数据源ds2的数据表t_edu_bd_role中记录以角色名
    public boolean DeleteBdRoleInfoByRoleName(String roleName) {
    	if(jdbcTemplate2 == null || roleName == null)
    		return false;    	
    	String sql = "delete from t_edu_bd_role where role_name = " + "'" + roleName + "'";
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //插入记录到数据源ds2的数据表t_edu_bd_user_perm中
    public boolean InsertBdUserPermInfo(TEduBdUserPermDo tebupDo) {
    	if(jdbcTemplate2 == null || tebupDo == null)
    		return false;    	
    	String sql = "", sql1 = "insert into t_edu_bd_user_perm(", sql2 = " values(";    	
    	//角色id，表t_edu_bd_user的主键id
    	if(tebupDo.getUserId() != null) {
    		sql1 += "user_id";
    		sql2 += "'" + tebupDo.getUserId() + "'";
    	}
    	//权限id，数据权限则为表t_edu_bd_data_perm的主键id，菜单权限则为表t_edu_bd_menu的主键id
    	if(tebupDo.getPermId() != null) {
    		sql1 += ", perm_id";
    		sql2 += ", '" + tebupDo.getPermId() + "'";
    	}
    	//权限类型，1:数据权限，2:菜单权限
    	if(tebupDo.getPermType() != null) {
    		sql1 += ", perm_type";
    		sql2 += ", " + tebupDo.getPermType();
    	}               
    	//创建时间
    	if(tebupDo.getCreateTime() != null) {
    		sql1 += ", create_time";
    		sql2 += ", '" + tebupDo.getCreateTime() + "'";
    	}               
    	//更新时间
    	if(tebupDo.getUpdateTime() != null) {
    		sql1 += ", update_time";
    		sql2 += ", '" + tebupDo.getUpdateTime() + "'";
    	}
    	sql1 += ")";
    	sql2 += ")";
    	sql = sql1 + sql2;
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //插入记录到数据源ds2的数据表t_edu_bd_user_perm中
    public boolean InsertBdUserPermInfo(List<TEduBdUserPermDo> tebupDoList) {
    	if(jdbcTemplate2 == null || tebupDoList == null)
    		return false;    	
    	if(tebupDoList.size() == 0)
    		return false;
    	String sql = "", sql1 = "insert into t_edu_bd_user_perm(", sql2 = " values(";
    	TEduBdUserPermDo tebupDo = tebupDoList.get(0);
    	//角色id，表t_edu_bd_user的主键id
    	if(tebupDo.getUserId() != null) {
    		sql1 += "user_id";
    		sql2 += "'" + tebupDo.getUserId() + "'";
    	}
    	//权限id，数据权限则为表t_edu_bd_data_perm的主键id，菜单权限则为表t_edu_bd_menu的主键id
    	if(tebupDo.getPermId() != null) {
    		sql1 += ", perm_id";
    		sql2 += ", '" + tebupDo.getPermId() + "'";
    	}
    	//权限类型，1:数据权限，2:菜单权限
    	if(tebupDo.getPermType() != null) {
    		sql1 += ", perm_type";
    		sql2 += ", " + tebupDo.getPermType();
    	}               
    	//创建时间
    	if(tebupDo.getCreateTime() != null) {
    		sql1 += ", create_time";
    		sql2 += ", '" + tebupDo.getCreateTime() + "'";
    	}               
    	//更新时间
    	if(tebupDo.getUpdateTime() != null) {
    		sql1 += ", update_time";
    		sql2 += ", '" + tebupDo.getUpdateTime() + "'";
    	}
    	sql1 += ")";
    	sql2 += ")";
    	for(int i = 1; i < tebupDoList.size(); i++) {
    		tebupDo = tebupDoList.get(i);
    		sql2 += ", (";
    		sql2 += "'" + tebupDo.getUserId() + "'";
    		sql2 += ", '" + tebupDo.getPermId() + "'";
    		sql2 += ", " + tebupDo.getPermType();
    		sql2 += ", '" + tebupDo.getCreateTime() + "'";
    		sql2 += ", '" + tebupDo.getUpdateTime() + "'";
    		sql2 += ")";
    	}
    	sql = sql1 + sql2;
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //更新记录到数据源ds2的数据表t_edu_bd_user_perm中以输入字段
    public boolean UpdateBdRolePermInfoByField(TEduBdUserPermDo tebupDo, String fieldName, String fieldVal) {
    	if(jdbcTemplate2 == null || tebupDo == null)
    		return false;    	
    	String sql = "", sql1 = "update t_edu_bd_user_perm set", sql2 = " ", sql3 = " where " + fieldName + " = " + "'" + fieldVal + "'" + " and stat = 1";
        boolean headFlag = false;        
    	//主键id
    	if(tebupDo.getId() != null) {
    		sql2 += "id=";
    		sql2 += "'" + tebupDo.getId() + "'";
    		headFlag = true;
    	}
    	//角色id，表t_edu_bd_user的主键id
    	if(tebupDo.getUserId() != null) {
    		if(headFlag)
    			sql2 += ", user_id=";
    		else {
    			sql2 += "user_id=";
    			headFlag = true;
    		}
    		sql2 += tebupDo.getUserId();
    	}    	
    	//权限id，数据权限则为表t_edu_bd_data_perm的主键id，菜单权限则为表t_edu_bd_menu的主键id
    	if(tebupDo.getPermId() != null) {
    		if(headFlag)
    			sql2 += ", perm_id=";
    		else {
    			sql2 += "perm_id=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebupDo.getPermId() + "'";
    	}
    	//权限类型，1:数据权限，2:菜单权限
    	if(tebupDo.getPermType() != null) {
    		if(headFlag)
    			sql2 += ", perm_type=";
    		else {
    			sql2 += "perm_type=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebupDo.getPermType() + "'";
    	}               
    	//更新时间
    	if(tebupDo.getUpdateTime() != null) {
    		if(headFlag)
    			sql2 += ", update_time=";
    		else {
    			sql2 += "update_time=";
    			headFlag = true;
    		}
    		sql2 += "'" + tebupDo.getUpdateTime() + "'";
    	} 
    	//是否有效 1 有效 0 无效
    	if(tebupDo.getStat() != null) {
    		if(headFlag)
    			sql2 += ", stat=";
    		else {
    			sql2 += "stat=";
    			headFlag = true;
    		}
    		sql2 += tebupDo.getStat();
    	}
    	sql = sql1 + sql2 + sql3;
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //删除数据源ds2的数据表t_edu_bd_user_perm中记录以用户名
    public boolean DeleteBdUserPermInfoByUserId(String userId) {
    	if(jdbcTemplate2 == null || userId == null)
    		return false;    	
    	String sql = "delete from t_edu_bd_user_perm where user_id = " + "'" + userId + "'";
    	logger.info("执行的MySql语句：" + sql);
        jdbcTemplate2.execute(sql);
    	
    	return true;
    }
    
    //从数据源ds2的数据表t_edu_bd_user_perm中查找所有用户权限信息
    public List<TEduBdUserPermDo> getAllBdUserPermInfo(String userId, int permType) {
    	if(jdbcTemplate2 == null)
    		return null;
    	String sql = "select id, user_id, perm_id, perm_type from t_edu_bd_user_perm where" + " stat = 1" + " and user_id = " + "'" + userId + "'" + " and perm_type = " + permType;    	
    	logger.info("执行的MySql语句：" + sql);       
        return (List<TEduBdUserPermDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdUserPermDo>(){

            @Override
            public TEduBdUserPermDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduBdUserPermDo tebupDo = new TEduBdUserPermDo();
            	tebupDo.setId(rs.getLong("id"));
            	tebupDo.setUserId(rs.getString("user_id"));
            	tebupDo.setPermId(rs.getString("perm_id"));
            	tebupDo.setPermType(rs.getInt("perm_type"));
                return tebupDo;
            }
        });
    }    
    
    //从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别
    public List<TEduBdMenuDo> getBdMenuInfoByLevel(int level) {
    	if(jdbcTemplate2 == null)
    		return null;
        String sql = null;
        sql = "select distinct id, menu_name, level, parent_id, parent_name, menu_type, descript,sort_id, stat from t_edu_bd_menu" + " where stat = 1" + " and level = " + level;
        sql += " order by sort_id ";
        logger.info("sql语句：" + sql);
        return (List<TEduBdMenuDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdMenuDo>(){

            @Override
            public TEduBdMenuDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduBdMenuDo tebmpDo = new TEduBdMenuDo();
            	tebmpDo.setId(rs.getString("id"));
            	tebmpDo.setMenuName(rs.getString("menu_name"));
            	tebmpDo.setLevel(rs.getInt("level"));
            	tebmpDo.setParentId(rs.getString("parent_id"));
            	tebmpDo.setParentName(rs.getString("parent_name"));
            	tebmpDo.setMenuType(rs.getInt("menu_type"));
            	tebmpDo.setDescript(rs.getString("descript"));
            	tebmpDo.setStat(rs.getInt("stat"));
                return tebmpDo;
            }
        });
    }
    
    //从数据源ds2的数据表t_edu_bd_menu中查找菜单信息以菜单级别和父菜单ID
    public List<TEduBdMenuDo> getBdMenuInfoByLevel(int level, String parentId) {
    	if(jdbcTemplate2 == null)
    		return null;
        String sql = null;
        sql = "select distinct id, menu_name, level, parent_id, parent_name, menu_type, descript,sort_id, stat from t_edu_bd_menu" + " where stat = 1" + " and level = " + level + " and parent_id = " + "'" + parentId + "'";
        sql += " order by sort_id ";
        logger.info("sql语句：" + sql);
        return (List<TEduBdMenuDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdMenuDo>(){

            @Override
            public TEduBdMenuDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduBdMenuDo tebmpDo = new TEduBdMenuDo();
            	tebmpDo.setId(rs.getString("id"));
            	tebmpDo.setMenuName(rs.getString("menu_name"));
            	tebmpDo.setLevel(rs.getInt("level"));
            	tebmpDo.setParentId(rs.getString("parent_id"));
            	tebmpDo.setParentName(rs.getString("parent_name"));
            	tebmpDo.setMenuType(rs.getInt("menu_type"));
            	tebmpDo.setDescript(rs.getString("descript"));
            	tebmpDo.setStat(rs.getInt("stat"));
                return tebmpDo;
            }
        });
    }
    
    //插入消息通知记录
    public int insertMsgNotice(TEduBdMsgNoticeDo tebmnDo) {
  		return tebmnDoMapper.insertMsgNotice(tebmnDo);
  	}
    
    //获取所有用户名、单位ID、单位名称记录信息
    public List<EduBdUserDo> getAllUserInfos(String id,Integer userType) {
    	return ebuDoMapper.getAllUserInfos(id,userType);
    }
    
    //插入消息通知状态记录
    public int insertMsgNoticeStatus(TEduBdNoticeStatusDo tebnsDo) {
  		return tebnsDoMapper.insertMsgNoticeStatus(tebnsDo);
  	}
    
    //查询消息通知状态记录列表以接收用户名
    public List<TEduBdNoticeStatusDo> getMsgNoticeStatusByRcvUserName(String rcvUserName) {
  		return tebnsDoMapper.getMsgNoticeStatusByRcvUserName(rcvUserName);
  	}
    
    //查询消息通知记录以通知id
    public TEduBdMsgNoticeDo getMsgNoticeById(String id) {
    	return tebmnDoMapper.getMsgNoticeById(id);
    }
    
    //查询消息通知状态记录列表以接收用户名
    public List<TEduBdNoticeStatusDo> getMsgNoticeStatusBySendUserName(String sendUserName) {
  		return tebnsDoMapper.getMsgNoticeStatusBySendUserName(sendUserName);
  	}
    
    //查询消息通知状态记录列表以通知ID和发布用户名
    public List<TEduBdNoticeStatusDo> getMsgNoticeStatusBybIdSendUser(String bulletinId, String sendUserName) {
  		return tebnsDoMapper.getMsgNoticeStatusBybIdSendUser(bulletinId, sendUserName);
  	}
    
    //查询消息通知状态记录列表以通知id和接收用户名
    public TEduBdNoticeStatusDo getMsgNoticeStatusBybIdRcvUserName(String bulletinId, String rcvUserName) {
  		return tebnsDoMapper.getMsgNoticeStatusBybIdRcvUserName(bulletinId, rcvUserName);
  	}
  	
  	//更新阅读次数
    public int updateReadCountInMsgNotice(String bulletinId, String rcvUserName, int readCount) {
  		return tebnsDoMapper.updateReadCountInMsgNotice(bulletinId, rcvUserName, readCount);
  	}
    
    //更新签到标识
    public int updateSignFlagByTEduBdNoticeStatusDo(TEduBdNoticeStatusDo tebnsDo) {
    	return tebnsDoMapper.updateSignFlagByTEduBdNoticeStatusDo(tebnsDo);
    }

    //查询消息通知当前上一条记录以当前通知id
    public TEduBdMsgNoticeDo getPreMsgNoticeById(String id) {
  		return tebmnDoMapper.getPreMsgNoticeById(id);
  	}
    
    //查询消息通知当前下一条记录以当前通知id
    public TEduBdMsgNoticeDo getNextMsgNoticeById(String id) {
    	return tebmnDoMapper.getNextMsgNoticeById(id);
  	}
    
    //查询所有子用户记录信息以父用户id
    public List<EduBdUserDo> getAllSubUserInfosByParentId(String orgId,String parentId,Integer userType) {
    	return ebuDoMapper.getAllSubUserInfosByParentId(orgId,parentId,userType);
    }
    
    //查询用户记录信息以用户id
    public EduBdUserDo getUserInfoByUserId(String id) {
    	return ebuDoMapper.getUserInfoByUserId(id);
    }
    
    //查询消息通知当前上一条记录以当前通知id和接收用户名（接收用户名字串前后添加%）
  	public TEduBdMsgNoticeDo getPreMsgNoticeByIdRcvUserName(String id, String rcvUserName) {
  		return tebmnDoMapper.getPreMsgNoticeByIdRcvUserName(id, rcvUserName);
  	}
  	
  	//查询消息通知当前下一条记录以当前通知id和接收用户名（接收用户名字串前后添加%）
  	public TEduBdMsgNoticeDo getNextMsgNoticeByIdRcvUserName(String id, String rcvUserName) {
  		return tebmnDoMapper.getNextMsgNoticeByIdRcvUserName(id, rcvUserName);
  	}  	
  	
  	//查询消息通知当前上一条记录以当前通知id和接收用户名（发送用户名）
  	public TEduBdMsgNoticeDo getPreMsgNoticeByIdSendUserName(String id, String sendUserName) {
  		return tebmnDoMapper.getPreMsgNoticeByIdSendUserName(id, sendUserName);
  	}
  			
  	//查询消息通知当前下一条记录以当前通知id和接收用户名（发送用户名）
  	public TEduBdMsgNoticeDo getNextMsgNoticeByIdSendUserName(String id, String sendUserName) {
  		return tebmnDoMapper.getNextMsgNoticeByIdSendUserName(id, sendUserName);
  	}
  	
  	//查询邮件服务记录以用户名
  	public TEduBdMailSrvDo getMailSrvInfoByUserName(String userName) {
  		return tebmsDoMapper.getMailSrvInfoByUserName(userName);
  	}
  	
  	//插入邮件服务记录
  	public int insertMailSrv(TEduBdMailSrvDo tebmsDo) {
  		return tebmsDoMapper.insertMailSrv(tebmsDo);
  	}
  	
  	//更新邮件服务记录
  	public boolean updateMailSrv(TEduBdMailSrvDo tebmsDo) {
  		boolean retFlag = false;
  		if(tebmsDo.getUserName() != null) {
  			retFlag = true;
  			//更新邮件用户名
  			if(tebmsDo.getEmail() != null)
  				tebmsDoMapper.updateEmail(tebmsDo.getUserName(), tebmsDo.getEmail());
  			//更新密码以用户名
  			if(tebmsDo.getPassword() != null)
  				tebmsDoMapper.updatePassword(tebmsDo.getUserName(), tebmsDo.getPassword());  			
  			//更新接收服务器以用户名
  			if(tebmsDo.getRcvServer() != null)
  				tebmsDoMapper.updateRcvServer(tebmsDo.getUserName(), tebmsDo.getRcvServer());  			
  			//更新接收服务端口以用户名
  			if(tebmsDo.getRcvSrvPort() != null)
  				tebmsDoMapper.updateRcvSrvPort(tebmsDo.getUserName(), tebmsDo.getRcvSrvPort());  			
  			//更新接收服务端口号以用户名
  			if(tebmsDo.getRcvSrvPortNo() != null)
  				tebmsDoMapper.updateRcvSrvPortNo(tebmsDo.getUserName(), tebmsDo.getRcvSrvPortNo());  			
  			//更新发送服务器以用户名
  			if(tebmsDo.getSendServer() != null)
  				tebmsDoMapper.updateSendServer(tebmsDo.getUserName(), tebmsDo.getSendServer());  			
  			//更新发送服务端口以用户名
  			if(tebmsDo.getSendSrvPort() != null)
  				tebmsDoMapper.updateSendSrvPort(tebmsDo.getUserName(), tebmsDo.getSendSrvPort());  			
  			//更新发送服务端口号以用户名
  			if(tebmsDo.getSendSrvPortNo() != null)
  				tebmsDoMapper.updateSendSrvPortNo(tebmsDo.getUserName(), tebmsDo.getSendSrvPortNo());  			
  			//更新有效标识以用户名
  			if(tebmsDo.getStat() != null)
  				tebmsDoMapper.updateStat(tebmsDo.getUserName(), tebmsDo.getStat());
  		}
  		
  		return retFlag;
  	}
  	
  	//查询学校视频监控记录信息以学校id
    public List<TEduBdBriKitStoveDo> getSchVidSurvInfosBySchId(String schoolId) {
    	return tebbksDoMapper.getSchVidSurvInfosBySchId(schoolId);
    }
    
    //查询所有学校视频监控记录信息
    public List<TEduBdBriKitStoveDo> getAllSchVidSurvInfos() {
    	return tebbksDoMapper.getAllSchVidSurvInfos();
    }
    
    //查询学校视频监控记录信息以区域id
    public List<TEduBdBriKitStoveDo> getSchVidSurvInfosByDistId(String regionId) {
    	return tebbksDoMapper.getSchVidSurvInfosByDistId(regionId);
    }
    
    
    /**
     * 插入用户设置动态列
     * @param record
     * @return
     */
    public int addUserInterfaceColums(EduBdInterfaceColumnsDo record) {
    	return columnsMapper.insert(record);
    }

    /**
     * 根据主键修改用户设置的动态列
     * @param record
     * @return
     */
    public int updateUserInterfaceColumsByPrimaryKey(EduBdInterfaceColumnsDo record) {
    	return columnsMapper.updateByPrimaryKey(record);
    };
    
    
    /**
     * 根据接口名称查询对应的列设置
     * @param interfaceName
     * @return
     */
    public EduBdInterfaceColumnsDo getByInterfaceName(String userId,String interfaceName) {
    	return columnsMapper.selectByInterfaceName(userId,interfaceName);
    }
    
    /**
     * 根据接口名称查询对应的列设置
     * @param interfaceName
     * @return
     */
    public Integer getInterfaceColumnsMaxId() {
    	return columnsMapper.selectMaxId();
    }
    
    //插入教育视频记录
    public int insertTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo) {
    	return tebelDoMapper.insertTEduBdEtvidLibDo(tebelDo);
    }
    
    //更新教育视频记录
    public int updateTEduBdEtvidLibDo(TEduBdEtvidLibDo tebelDo) {
    	//视频名称
    	if(tebelDo.getVidName() != null && tebelDo.getId() != null) {
    		tebelDoMapper.updateVidNameByTEduBdEtvidLibDo(tebelDo);
    	}
    	//副标题	
    	if(tebelDo.getSubTitle() != null && tebelDo.getId() != null){
    		tebelDoMapper.updateSubTitleByTEduBdEtvidLibDo(tebelDo);
    	}
    	//视频分类，0:系统操作，1:食品安全，2:政策法规	
    	if(tebelDo.getVidCategory() != null && tebelDo.getId() != null){
    		tebelDoMapper.updateVidCategoryByTEduBdEtvidLibDo(tebelDo);
    	}
    	//缩略图图片URL
    	if(tebelDo.getThumbUrl() != null && tebelDo.getId() != null) {
    		tebelDoMapper.updateThumbUrlByTEduBdEtvidLibDo(tebelDo);
    	}
    	//视频URL
    	if(tebelDo.getVidUrl() != null && tebelDo.getId() != null) {
    		tebelDoMapper.updateVidUrlByTEduBdEtvidLibDo(tebelDo);
    	}
    	//视频描述内容	
    	if(tebelDo.getVidDescrCont() != null && tebelDo.getId() != null){
    		tebelDoMapper.updateVidDescrContByTEduBdEtvidLibDo(tebelDo);
    	}
    	//最后更新时间
    	if(tebelDo.getLastUpdateTime() != null && tebelDo.getId() != null) {
    		tebelDoMapper.updateLastUpdateTimeByTEduBdEtvidLibDo(tebelDo);
    	}
    	
    	return 1;
    }
    
    //获取教育视频以记录ID
    public TEduBdEtvidLibDo getTEduBdEtvidLibDoById(String id) {
  		return tebelDoMapper.getTEduBdEtvidLibDoById(id);
  	}
    
    //获取所有教育视频
    public List<TEduBdEtvidLibDo> getAllTEduBdEtvidLibDos() {
    	return tebelDoMapper.getAllTEduBdEtvidLibDos();
    }
    
    //获取教育视频以开始和结束时间
    public List<TEduBdEtvidLibDo> getTEduBdEtvidLibDosByCreateTime(String startTime, String endTime) {
    	return tebelDoMapper.getTEduBdEtvidLibDosByCreateTime(startTime, endTime);
    }
    
    //删除教育视频记录以记录ID
    public int deleteTEduBdEtvidLibDoById(String id) {
    	return tebelDoMapper.deleteTEduBdEtvidLibDoById(id);
    }
    
    //获取所有食品安全等级记录
    public List<TEduBdFoodSafetyGradeDo> getAllTEduBdFoodSafetyGradeDos() {
    	return tebfsgDoMapper.getAllTEduBdFoodSafetyGradeDos();
    }
    	
    //获取食品安全等级记录以区域名称
    public List<TEduBdFoodSafetyGradeDo> getTEduBdFoodSafetyGradeDoByDistName(String distName) {
    	return tebfsgDoMapper.getTEduBdFoodSafetyGradeDoByDistName(distName);
    }
    
    //插入投诉举报记录
    public int insertTEduBdComplaintDo(TEduBdComplaintDo tebcpDo) {
    	return tebcpDoMapper.insertTEduBdComplaintDo(tebcpDo);
    }
    	
    //获取投诉举报以记录ID
    public TEduBdComplaintDo getTEduBdComplaintDoById(String id) {
    	return tebcpDoMapper.getTEduBdComplaintDoById(id);
    }
    
    //更新投诉举报
    public int updateTEduBdComplaintDo(TEduBdComplaintDo tebcpDo) {
    	//更新投诉举报处理状态
    	if(tebcpDo.getId() != null && tebcpDo.getCpStatus() != null) {
    		tebcpDoMapper.updateCpStatusByTEduBdComplaintDo(tebcpDo);
    	}
    	//更新承办人名称
    	if(tebcpDo.getId() != null && tebcpDo.getContractor() != null) {
    		tebcpDoMapper.updateContractorByTEduBdComplaintDo(tebcpDo);
    	}
    	//更新办结反馈
    	if(tebcpDo.getId() != null && tebcpDo.getFeedBack() != null) {
    		tebcpDoMapper.updateFeedBackByTEduBdComplaintDo(tebcpDo);
    	}
    	//更新办结日期，格式：xxxx-xx-xx
    	if(tebcpDo.getId() != null && tebcpDo.getFinishDate() != null) {
    		tebcpDoMapper.updateFinishDateByTEduBdComplaintDo(tebcpDo);
    	}
    	//更新投诉举报最近更新时间
    	if(tebcpDo.getId() != null && tebcpDo.getLastUpdateTime() != null) {
    		tebcpDoMapper.updateLastUpdateTimeByTEduBdComplaintDo(tebcpDo);
    	}
    	
    	return 1;
    }
    
    //获取投诉举报以日期段，日期格式：xxxx-xx-xx
    public List<TEduBdComplaintDo> getTEduBdComplaintDosBySubDate(String startDate, String endDate) {
  		return tebcpDoMapper.getTEduBdComplaintDosBySubDate(startDate, endDate);
  	}
    
    //获取所有投诉举报
    public List<TEduBdComplaintDo> getAllTEduBdComplaintDos() {
    	return tebcpDoMapper.getAllTEduBdComplaintDos();
    }
    
    //获取所有试卷
    public List<TEduBdExamPaperDo> getAllTEduBdExamPaperDo() {
    	return tebepDoMapper.getAllTEduBdExamPaperDo();
    }
    
    //获取所有试卷信息以试卷ID
    public TEduBdExamPaperDo getTEduBdExamPaperDoById(String id) {
    	return tebepDoMapper.getTEduBdExamPaperDoById(id);
  	}
    
    //获取所有试卷内容以试卷ID
    public List<TEduBdExamPaperContDo> getTEduBdExamPaperContDosByEpId(String epId) {
    	return tebepcDoMapper.getTEduBdExamPaperContDosByEpId(epId);
    }
    
    //获取试题以试题ID
    public TEduBdQuestionBodyDo getTEduBdQuestionBodyDoById(String id) {
    	return tebqbDoMapper.getTEduBdQuestionBodyDoById(id);
    }
    
    //获取候选答案以试题ID
    public List<TEduBdQuestionCandAnsDo> getTEduBdQuestionCandAnsDoByQuestionId(String questionId) {
    	return tebqcaDoMapper.getTEduBdQuestionCandAnsDoByQuestionId(questionId);
    }
    
    //获取所有试卷大题型主题以试卷ID和试题类型
    public TEduBdExamPaperSubjectDo getTEduBdExamPaperSubjectDoByEpIdQuestionType(String epId, int questionType) {
    	return tebepsDoMapper.getTEduBdExamPaperSubjectDoByEpIdQuestionType(epId, questionType);
    }
    
	/** 任务中心 **/
	// 1.0  根据任务id查询任务信息
	public AppCommonDao getCheckTask(String id) {
		if (jdbcTemplate2 == null)
			return null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_task ");
		sql.append(" where 1=1 ");
		sql.append(" and id="+id);
		logger.info("sql语句：" + sql.toString());
		AppCommonDao appCommonDao=new AppCommonDao();
		appCommonDao=jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getString("id"));
				commonMap.put("time",  rs.getTimestamp("time")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("time", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("title", rs.getString("title"));
				commonMap.put("content", rs.getString("content"));
				commonMap.put("releaseUnit", rs.getString("release_unit"));
				commonMap.put("releaser", rs.getString("releaser"));
				commonMap.put("releaserName", rs.getString("releaser_name"));
				commonMap.put("status", rs.getString("status"));
				commonMap.put("completionFeedback", rs.getString("completion_feedback"));
				commonMap.put("completionDate", rs.getTimestamp("completion_date")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("completion_date", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("operationUnit", rs.getString("operation_unit"));
				commonMap.put("operator", rs.getString("operator"));
				commonMap.put("operatorName", rs.getString("operator_name"));
				return new AppCommonDao(commonMap);
			}
		}).get(0);
		return appCommonDao;
	}
	
	// 1.1 添加任务
	public boolean getAddTask(LinkedHashMap<String, Object> filterParamMap) {
		if (jdbcTemplate2 == null)
			return false;
		String keyStr = "";
		String valueStr="";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				keyStr +=mapKey+" , ";
				valueStr += "'"+mapValue+"' , ";
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" INSERT INTO t_edu_bd_task ");
		sql.append(" ( ");
		sql.append(keyStr.substring(0, keyStr.length()-2));
		sql.append(" ) ");
		sql.append(" VALUES( ");
		sql.append(valueStr.substring(0, valueStr.length()-2));
		sql.append(" )");
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.update(sql.toString())==1?true:false;
	}

	// 1.2 任务列表
	public List<AppCommonDao> getTaskList(LinkedHashMap<String, Object> filterParamMap) {
		if (jdbcTemplate2 == null)
			return null;
		String filterStr = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				if("startDate".equals(mapKey)) {
					String value=mapValue+" 00:00:00";
					filterStr += " and time >='" + value + "'";
				}else if("endDate".equals(mapKey)){
					String value=mapValue+" 23:59:59";
					filterStr += " and time <='" + value + "'";
				}else {
					filterStr += " and " + mapKey + "='" + mapValue + "'";
				}
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_task ");
		sql.append(" where 1=1 ");
		sql.append(filterStr);
		sql.append(" ORDER BY time desc ");
		logger.info("sql语句：" + sql.toString());
		return (List<AppCommonDao>) jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getString("id"));
				commonMap.put("time",  rs.getTimestamp("time")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("time", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("title", rs.getString("title"));
//				commonMap.put("content", rs.getString("content"));
				commonMap.put("releaseUnit", rs.getString("release_unit"));
				commonMap.put("releaser", rs.getString("releaser"));
				commonMap.put("releaserName", rs.getString("releaser_name"));
				commonMap.put("status", rs.getString("status"));
//				commonMap.put("completionFeedback", rs.getString("completion_feedback"));
				commonMap.put("completionDate", rs.getTimestamp("completion_date")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("completion_date", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("operationUnit", rs.getString("operation_unit"));
				commonMap.put("operator", rs.getString("operator"));
				commonMap.put("operatorName", rs.getString("operator_name"));
				return new AppCommonDao(commonMap);
			}
		});
	}
    //1.3 我的任务列表
    public List<AppCommonDao> getMyReleaseList(LinkedHashMap<String, Object> filterParamMap){
		if (jdbcTemplate2 == null)
			return null;
		String filterStr = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				if("startDate".equals(mapKey)) {
					String value=mapValue+" 00:00:00";
					filterStr += " and time >='" + value + "'";
				}else if("endDate".equals(mapKey)){
					String value=mapValue+" 23:59:59";
					filterStr += " and time <='" + value + "'";
				}else {
					filterStr += " and " + mapKey + "='" + mapValue + "'";
				}
				
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_task ");
		sql.append(" where 1=1 ");
		sql.append(filterStr);
		sql.append(" ORDER BY time desc ");
		logger.info("sql语句：" + sql.toString());
		return (List<AppCommonDao>) jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getString("id"));
				commonMap.put("time",  rs.getTimestamp("time")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("time", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("title", rs.getString("title"));
//				commonMap.put("content", rs.getString("content"));
				commonMap.put("releaseUnit", rs.getString("release_unit"));
				commonMap.put("releaser", rs.getString("releaser"));
				commonMap.put("releaserName", rs.getString("releaser_name"));
				commonMap.put("status", rs.getString("status"));
//				commonMap.put("completionFeedback", rs.getString("completion_feedback"));
				commonMap.put("completionDate", rs.getTimestamp("completion_date")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("completion_date", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("operationUnit", rs.getString("operation_unit"));
				commonMap.put("operator", rs.getString("operator"));
				commonMap.put("operatorName", rs.getString("operator_name"));
				return new AppCommonDao(commonMap);
			}
		});
    }
	
	// 1.3.1  修改任务
	public boolean getUpdateTask(LinkedHashMap<String, Object> filterParamMap,String id) {
		if (jdbcTemplate2 == null)
			return false;
		String filterStr = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				filterStr += mapKey + "='" + mapValue + "' ,";
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE t_edu_bd_task SET ");
		sql.append(filterStr.substring(0, filterStr.length()-2));
		sql.append(" where id="+id);
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.update(sql.toString())==1?true:false;
	}
	
    //1.3.2 删除任务
    public boolean getDeleteTask(LinkedHashMap<String, Object> filterParamMap) {
		if (jdbcTemplate2 == null)
			return false;
		String filterStr = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				filterStr += " and "+mapKey + "='" + mapValue + "' ,";
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" DELETE FROM t_edu_bd_task ");
		sql.append(" where 1=1 ");
		sql.append(filterStr.substring(0, filterStr.length()-2));
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.update(sql.toString())==1?true:false;
    }
    
    //1.4 市/区教育局下拉列表
    public List<AppCommonDao> geteduList(){
		if (jdbcTemplate2 == null)
			return null;

		StringBuffer sql = new StringBuffer();
		sql.append(" select distinct name ");
		sql.append(" from t_edu_committee ");
		sql.append(" where 1=1 ");
		logger.info("sql语句：" + sql.toString());
		return (List<AppCommonDao>) jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("code", rs.getString("name"));
				commonMap.put("name", rs.getString("name"));
				return new AppCommonDao(commonMap);
			}
		});
    }
    //1.4.1 根据市区,获取承办人列表
    public List<AppCommonDao> getoperatorList(String org_name){
		if (jdbcTemplate2 == null)
			return null;

		StringBuffer sql = new StringBuffer();
		sql.append(" select distinct user_account,name ");
		sql.append(" from t_edu_bd_user ");
		sql.append(" where 1=1 ");
		sql.append(" and org_name='"+org_name+"' ");
//		sql.append(" and org_id='"+code+"' ");
		logger.info("sql语句：" + sql.toString());
		return (List<AppCommonDao>) jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("userAccount", rs.getString("user_account"));
				commonMap.put("name", rs.getString("name"));
				return new AppCommonDao(commonMap);
			}
		});
    }
	
  	/** 事务申办 **/
    //2.0 申办详情
    public AppCommonDao getAffairDetail(String id) {
		if (jdbcTemplate2 == null)
			return null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_affair_apply ");
		sql.append(" where 1=1 ");
		sql.append(" and id="+id);
		logger.info("sql语句：" + sql.toString());
		AppCommonDao appCommonDao=new AppCommonDao();
		appCommonDao=jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getString("id"));
				commonMap.put("time",  rs.getTimestamp("time")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("time", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("type", rs.getString("type"));
				commonMap.put("title", rs.getString("title"));
				commonMap.put("content", rs.getString("content"));
				commonMap.put("schoolInfo", rs.getString("school_info"));
				commonMap.put("enclosureLink", rs.getString("enclosure_link"));
				commonMap.put("releaseUnit", rs.getString("release_unit"));
				commonMap.put("releaser", rs.getString("releaser"));
				commonMap.put("releaserName", rs.getString("releaser_name"));
				commonMap.put("status", rs.getString("status"));
				commonMap.put("completionFeedback", rs.getString("completion_feedback"));
				commonMap.put("completionDate", rs.getTimestamp("completion_date")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("completion_date", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("operationUnit", rs.getString("operation_unit"));
				commonMap.put("operator", rs.getString("operator"));
				commonMap.put("operatorName", rs.getString("operator_name"));
				commonMap.put("reviewer", rs.getString("reviewer"));
				commonMap.put("reviewerName", rs.getString("reviewer_name"));
				commonMap.put("revieweTime", rs.getString("reviewe_time"));
				commonMap.put("completer", rs.getString("completer"));
				commonMap.put("completerName", rs.getString("completer_name"));
				return new AppCommonDao(commonMap);
			}
		}).get(0);
		return appCommonDao;
    }
    
  	//2.1 事务申办
  	//2.1 事务申办
    public boolean getAddAffair(LinkedHashMap<String, Object> filterParamMap) {
		if (jdbcTemplate2 == null)
			return false;
		String keyStr = "";
		String valueStr="";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				keyStr +=mapKey+" , ";
				valueStr += "'"+mapValue+"' , ";
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" INSERT INTO t_edu_bd_affair_apply ");
		sql.append(" ( ");
		sql.append(keyStr.substring(0, keyStr.length()-2));
		sql.append(" ) ");
		sql.append(" VALUES( ");
		sql.append(valueStr.substring(0, valueStr.length()-2));
		sql.append(" )");
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.update(sql.toString())==1?true:false;
    }
    //2.2 申办列表
    public List<AppCommonDao> getAffairList(LinkedHashMap<String, Object> filterParamMap){
		if (jdbcTemplate2 == null)
			return null;
		String filterStr = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				if("startDate".equals(mapKey)) {
					String value=mapValue+" 00:00:00";
					filterStr += " and time >='" + value + "'";
				}else if("endDate".equals(mapKey)){
					String value=mapValue+" 23:59:59";
					filterStr += " and time <='" + value + "'";
				}else {
					filterStr += " and " + mapKey + "='" + mapValue + "'";
				}
				
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_affair_apply ");
		sql.append(" where 1=1 ");
		sql.append(filterStr);
		sql.append(" ORDER BY time desc ");
		logger.info("sql语句：" + sql.toString());
		return (List<AppCommonDao>) jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getString("id"));
				commonMap.put("time",  rs.getTimestamp("time")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("time", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("type", rs.getString("type"));
				commonMap.put("title", rs.getString("title"));
//				commonMap.put("content", rs.getString("content"));
				commonMap.put("schoolInfo", rs.getString("school_info"));
				commonMap.put("enclosureLink", rs.getString("enclosure_link"));
				commonMap.put("releaseUnit", rs.getString("release_unit"));
				commonMap.put("releaser", rs.getString("releaser"));
				commonMap.put("releaserName", rs.getString("releaser_name"));
				commonMap.put("status", rs.getString("status"));
//				commonMap.put("completionFeedback", rs.getString("completion_feedback"));
				commonMap.put("completionDate", rs.getTimestamp("completion_date")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("completion_date", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("operationUnit", rs.getString("operation_unit"));
				commonMap.put("operator", rs.getString("operator"));
				commonMap.put("operatorName", rs.getString("operator_name"));
				return new AppCommonDao(commonMap);
			}
		});
    }
    
    //2.4 事务办结
    public boolean getCompleteAffair(LinkedHashMap<String, Object> filterParamMap,String id){
		if (jdbcTemplate2 == null)
			return false;
		String filterStr = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				filterStr += mapKey + "='" + mapValue + "' , ";
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" update t_edu_bd_affair_apply set ");
		sql.append(filterStr.substring(0, filterStr.length()-2));
		sql.append(" where id='"+id+"' ");
		return jdbcTemplate2.update(sql.toString())>0?true:false;
    }
    
    
    //2.5 我的申办
    public List<AppCommonDao> getMyApplyListList(LinkedHashMap<String, Object> filterParamMap){
		if (jdbcTemplate2 == null)
			return null;
		String filterStr = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				if("startDate".equals(mapKey)) {
					String value=mapValue+" 00:00:00";
					filterStr += " and time >='" + value + "'";
				}else if("endDate".equals(mapKey)){
					String value=mapValue+" 23:59:59";
					filterStr += " and time <='" + value + "'";
				}else {
					filterStr += " and " + mapKey + "='" + mapValue + "'";
				}
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_affair_apply ");
		sql.append(" where 1=1 ");
		sql.append(filterStr);
		sql.append(" ORDER BY time desc ");
		logger.info("sql语句：" + sql.toString());
		return (List<AppCommonDao>) jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getString("id"));
				commonMap.put("time",  rs.getTimestamp("time")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("time", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("type", rs.getString("type"));
				commonMap.put("title", rs.getString("title"));
//				commonMap.put("content", rs.getString("content"));
				commonMap.put("schoolInfo", rs.getString("school_info"));
				commonMap.put("enclosureLink", rs.getString("enclosure_link"));
				commonMap.put("releaseUnit", rs.getString("release_unit"));
				commonMap.put("releaser", rs.getString("releaser"));
				commonMap.put("releaserName", rs.getString("releaser_name"));
				commonMap.put("status", rs.getString("status"));
//				commonMap.put("completionFeedback", rs.getString("completion_feedback"));
				commonMap.put("completionDate", rs.getTimestamp("completion_date")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("completion_date", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("operationUnit", rs.getString("operation_unit"));
				commonMap.put("operator", rs.getString("operator"));
				commonMap.put("operatorName", rs.getString("operator_name"));
				return new AppCommonDao(commonMap);
			}
		});	
    }
    
    //2.6 修改我的申办
    public boolean getUpdateMyaffair(LinkedHashMap<String, Object> filterParamMap,String id) {
		if (jdbcTemplate2 == null)
			return false;
		String filterStr = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty() && !"enclosure_link".equals(mapKey) && !"school_info".equals(mapKey)) {
				filterStr += mapKey + "='" + mapValue + "' , ";
			}
			
			if("enclosure_link".equals(mapKey) || "school_info".equals(mapKey)) {
				if(mapValue != null && !mapValue.toString().trim().isEmpty()) {
					filterStr += mapKey + "='" + mapValue + "' , ";
				}else {
					filterStr += mapKey + "=" + mapValue + " , ";
				}
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" update t_edu_bd_affair_apply set ");
		sql.append(filterStr.substring(0, filterStr.length()-2));
		sql.append(" where id='"+id+"' ");
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.update(sql.toString())>0?true:false;
    }
    //2.7 删除我的申办
    public boolean getDeleteMyaffair(String id) {
		if (jdbcTemplate2 == null)
			return false;
		String filterStr = "";
		StringBuffer sql = new StringBuffer();
		sql.append(" DELETE FROM t_edu_bd_affair_apply ");
		sql.append(" where 1=1 ");
		sql.append(" and id='"+id+"'");
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.update(sql.toString())==1?true:false;
    }
    /** 预警信息 **/ 
    //3.1 下周未排菜预警
    public AppCommonDao getLwUnscheduledAlert(String user_account,String category,String pushRecipient){
		if (jdbcTemplate2 == null)
			return null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select id,user_account,category,unscheduled,`week`,time,frequency,`interval`,pushRecipient,emailRecipient,dishesWarn,deliveryWarn ");
		sql.append(" from t_edu_bd_warn ");
		sql.append(" where 1=1 ");
		sql.append(" and user_account='"+user_account+"'");
		sql.append(" and category='"+category+"'");
		AppCommonDao sourceDao = null;
		logger.info("sql语句：" + sql.toString());
		try {
			sourceDao= jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
				@Override
				public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
					LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
					commonMap.put("id", rs.getString("id"));
					commonMap.put("userAccount", rs.getString("user_account"));
					commonMap.put("category", rs.getString("category"));
					commonMap.put("unscheduled", rs.getString("unscheduled"));
					commonMap.put("week", rs.getString("week"));
					commonMap.put("time", rs.getString("time"));
					commonMap.put("frequency", rs.getString("frequency"));
					commonMap.put("interval", rs.getString("interval"));
					commonMap.put("pushRecipient", rs.getString("pushRecipient"));
					commonMap.put("emailRecipient", rs.getString("emailRecipient").trim().isEmpty()?null:rs.getString("emailRecipient").split(","));
					commonMap.put("dishesWarn", rs.getString("dishesWarn"));
					commonMap.put("deliveryWarn", rs.getString("deliveryWarn"));
					return new AppCommonDao(commonMap);
				}
			}).get(0);
		}catch(Exception e) {
		}
		if(sourceDao !=null) {
			return sourceDao;
		}else {
			//插入默认用户数据
			String sqlInsert=" INSERT INTO t_edu_bd_warn (user_account,category,unscheduled,`week`,time,frequency,`interval`,pushRecipient,emailRecipient,dishesWarn,deliveryWarn  )  "
							+"values ('"+user_account+"','"+category+"','0','5','17:00:00','3','30','"+pushRecipient+"','','10','20')";
			logger.info("sql语句：" + sqlInsert.toString());
			jdbcTemplate2.update(sqlInsert);
			sourceDao= jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
				@Override
				public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
					LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
					commonMap.put("id", rs.getString("id"));
					commonMap.put("userAccount", rs.getString("user_account"));
					commonMap.put("category", rs.getString("category"));
					commonMap.put("unscheduled", rs.getString("unscheduled"));
					commonMap.put("week", rs.getString("week"));
					commonMap.put("time", rs.getString("time"));
					commonMap.put("frequency", rs.getString("frequency"));
					commonMap.put("interval", rs.getString("interval"));
					commonMap.put("pushRecipient", rs.getString("pushRecipient"));
					commonMap.put("emailRecipient", rs.getString("emailRecipient").trim().isEmpty()?null:rs.getString("emailRecipient").split(","));
					commonMap.put("dishesWarn", rs.getString("dishesWarn"));
					commonMap.put("deliveryWarn", rs.getString("deliveryWarn"));
					return new AppCommonDao(commonMap);
				}
			}).get(0);
			return sourceDao;
		}
    }
    public boolean getLwUnscheduledAlertUpdate(LinkedHashMap<String, Object> filterParamMap,String user_account,String category) {
		if (jdbcTemplate2 == null)
			return false;
    	String value="";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				value += "`"+mapKey+"`='"+mapValue+"' , ";
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE  `t_edu_bd_warn` set "+value.substring(0, value.length()-2));
		sql.append(" where user_account='"+user_account+"' and category='"+category+"'");
		
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.update(sql.toString())>0?true:false;
    }
    
	/** 发送信息**/
	//4.0 消息展示接口
    public List<AppCommonDao> getSysInfoDisplay(LinkedHashMap<String, Object> filterParamMap){
		if (jdbcTemplate2 == null)
			return null;
		String filterStr = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
					filterStr += " and " + mapKey + "='" + mapValue + "'";
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_sysInfo ");
		sql.append(" where 1=1 ");
		sql.append(filterStr);
		sql.append(" ORDER BY last_time desc ");
		logger.info("sql语句：" + sql.toString());
		return (List<AppCommonDao>) jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("userAccount", rs.getString("user_account"));
				commonMap.put("taskCategory", rs.getString("task_category"));
				commonMap.put("isRead", rs.getString("is_read"));
				commonMap.put("content", rs.getString("content"));
				commonMap.put("taskTime",  rs.getTimestamp("task_time")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("task_time", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
//				commonMap.put("lastTime",  rs.getTimestamp("last_time")==null?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rs.getTimestamp("last_time", Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))).getTime())));
				commonMap.put("dishesWarn", rs.getString("dishesWarn"));
				commonMap.put("deliveryWarn", rs.getString("deliveryWarn"));
				return new AppCommonDao(commonMap);
			}
		});	
    }
    //4.0.1 未读信息数
    public AppCommonDao getNoReadInfoNum(LinkedHashMap<String, Object> filterParamMap) {
		if (jdbcTemplate2 == null)
			return null;
		String filterStr = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
					filterStr += " and " + mapKey + "='" + mapValue + "'";
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select count(1) num ");
		sql.append(" from t_edu_bd_sysInfo ");
		sql.append(" where 1=1 ");
		sql.append(filterStr);
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("num", rs.getString("num"));
				return new AppCommonDao(commonMap);
			}
		}).get(0);	
    }
    //4.0.2 推送信息
    public boolean getInsertInfo(LinkedHashMap<String, Object> filterParamMap) {
		if (jdbcTemplate2 == null)
			return false;
		String key = "";
		String value = "";
		for (Entry<String, Object> entry : filterParamMap.entrySet()) {
			String mapKey = entry.getKey();
			Object mapValue = entry.getValue();
			if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
				key += mapKey+", ";
				value += "'"+mapValue+"', ";
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" insert into t_edu_bd_sysInfo( "+key.substring(0,key.length()-2)+" ) "
				+ "values( "+value.substring(0, value.length()-2)+" )");
		
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.update(sql.toString())>0?true:false;
    }

    //更新数据
    public boolean getUpdateSysInfo(String task_category,String user_account,String task_time) {
		if (jdbcTemplate2 == null)
			return false;
		StringBuffer sql = new StringBuffer();
		String last_time=DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
		sql.append(" UPDATE  t_edu_bd_sysInfo SET is_read='1',last_time='"+last_time+"' where user_account='"+user_account+"' and task_category='"+task_category+"' and task_time='"+task_time+"'");
		
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.update(sql.toString())>0?true:false;
    }
    
    //定时任务
    public List<AppCommonDao> getScheduleTask(){
		if (jdbcTemplate2 == null)
			return null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_warn ");
		sql.append(" where 1=1 ");
		logger.info("sql语句：" + sql.toString());
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getString("id"));
				commonMap.put("userAccount", rs.getString("user_account"));
				commonMap.put("category", rs.getString("category"));
				commonMap.put("unscheduled", rs.getString("unscheduled"));
				commonMap.put("week", rs.getString("week"));
				commonMap.put("time", rs.getString("time"));
				commonMap.put("frequency", rs.getString("frequency"));
				commonMap.put("interval", rs.getString("interval"));
				commonMap.put("pushRecipient", rs.getString("pushRecipient"));
				commonMap.put("emailRecipient", rs.getString("emailRecipient"));
				commonMap.put("dishesWarn", rs.getString("dishesWarn"));
				commonMap.put("deliveryWarn", rs.getString("deliveryWarn"));
				return new AppCommonDao(commonMap);
			}
		});
    }
    
    //从数据源ds2的数据表t_edu_bd_user中查找用户信息以user_account
    public TEduBdUserDo getBdUserInfoByUserAccount(String user_account) {
    	if(jdbcTemplate2 == null)
    		return null;
    	TEduBdUserDo tebuDo = new TEduBdUserDo();
    	String sql = "select id, user_account, password, safe_grade, email, fix_phone, mobile_phone, name, "
    			+ "user_pic_url, is_admin, role_id, parent_id, last_login_time, creator, create_time, "
    			+ "updater, last_update_time, forbid, token, stat, remarks, org_id, org_name, "
    			+ "fax,user_type "
    			+ "from t_edu_bd_user where user_account = " + "'" + user_account + "'" + " and stat = 1";
    	logger.info("执行的MySql语句：" + sql);
    	jdbcTemplate2.query(sql, new RowCallbackHandler(){   
        	public void processRow(ResultSet rs) throws SQLException{  
        		tebuDo.setId(rs.getString("id"));
        		tebuDo.setUserAccount(rs.getString("user_account"));
        		tebuDo.setPassword(rs.getString("password"));
        		tebuDo.setSafeGrade(rs.getInt("safe_grade"));
        		tebuDo.setEmail(rs.getString("email"));
        		tebuDo.setFixPhone(rs.getString("fix_phone"));
        		tebuDo.setMobilePhone(rs.getString("mobile_phone"));
        		tebuDo.setName(rs.getString("name"));
        		tebuDo.setUserPicUrl(rs.getString("user_pic_url"));
        		tebuDo.setIsAdmin(rs.getInt("is_admin"));
        		tebuDo.setRoleId(rs.getString("role_id"));
        		tebuDo.setParentId(rs.getString("parent_id"));
        		tebuDo.setLastLoginTime(rs.getString("last_login_time"));
        		tebuDo.setCreator(rs.getString("creator"));
        		tebuDo.setCreateTime(rs.getString("create_time"));
        		tebuDo.setUpdater(rs.getString("updater"));
        		tebuDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebuDo.setForbid(rs.getInt("forbid"));
        		tebuDo.setToken(rs.getString("token"));
        		tebuDo.setStat(rs.getInt("stat"));
        		tebuDo.setRemarks(rs.getString("remarks"));
        		tebuDo.setOrgId(rs.getString("org_id"));
        		tebuDo.setOrgName(rs.getString("org_name"));
        		tebuDo.setFax(rs.getString("fax"));
        		tebuDo.setUserType(1);
        		if(CommonUtil.isNotEmpty(rs.getString("user_type"))) {
        			tebuDo.setUserType(rs.getInt("user_type"));
        		}
        	}
        });
    	
    	return tebuDo;
    }
    
    //保存下周数据
    public boolean getInsertLwUnscheduledAlert(List<LinkedHashMap<String, Object>> dataList) {
    	if(jdbcTemplate2 == null)
    		return false;
    	 List<Object[]> objectList = new ArrayList<>();
    	for(int i=0;i<dataList.size();i++) {
    		 objectList.add(new  Object[] {
    				 dataList.get(i).get("taskTime"),
    				 dataList.get(i).get("pushUserAccount"),
    				 dataList.get(i).get("dishDate"),
    				 dataList.get(i).get("ppName"),
    				 dataList.get(i).get("schType"),
    				 dataList.get(i).get("compDep"),
    				 dataList.get(i).get("num")
    				 });
    	}
    	 String sql = " INSERT INTO t_edu_bd_lw_unscheduled (taskTime, pushUserAccount, dishDate, ppName, schType, compDep, num) " +
                 " VALUES (?, ?, ?, ?, ?, ?, ?) ";
    	 jdbcTemplate2.batchUpdate(sql, objectList);
		return initProcFlag;
    }
    //获取下周数据
    public List<AppCommonDao> getLwUnscheduledAlert(String taskTime,String pushUserAccount){
    	if(jdbcTemplate2 == null)
    		return null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_lw_unscheduled ");
		sql.append(" where 1=1 ");
		sql.append(" and taskTime ='"+taskTime+"'");
		sql.append(" and pushUserAccount ='"+pushUserAccount+"'");
		logger.info(sql);
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("dishDate", rs.getString("dishDate"));
				commonMap.put("ppName", rs.getString("ppName"));
				commonMap.put("schType", rs.getString("schType"));
				commonMap.put("compDep", rs.getString("compDep"));
				commonMap.put("num", rs.getLong("num"));
				return new AppCommonDao(commonMap);
			}
		});
    }
    //保存今天排菜数据
    public boolean getInsertTodayUnscheduledAlert(List<LinkedHashMap<String, Object>> dataList) {
    	if(jdbcTemplate2 == null)
    		return false;
    	 List<Object[]> objectList = new ArrayList<>();
    	for(int i=0;i<dataList.size();i++) {
    		 objectList.add(new  Object[] {
    				 dataList.get(i).get("taskTime"),
    				 dataList.get(i).get("pushUserAccount"),
    				 dataList.get(i).get("dishDate"),
    				 dataList.get(i).get("ppName"),
    				 dataList.get(i).get("schType"),
    				 dataList.get(i).get("compDep"),
    				 dataList.get(i).get("num")
    				 });
    	}
    	 String sql = " INSERT INTO t_edu_bd_today_unscheduled (taskTime, pushUserAccount, dishDate, ppName, schType, compDep, num) " +
                 " VALUES (?, ?, ?, ?, ?, ?, ?) ";
    	 jdbcTemplate2.batchUpdate(sql, objectList);
		return initProcFlag;
    }
    //获取今天排菜数据
    public List<AppCommonDao> getTodayUnscheduledAlert(String taskTime,String pushUserAccount){
    	if(jdbcTemplate2 == null)
    		return null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_today_unscheduled ");
		sql.append(" where 1=1 ");
		sql.append(" and taskTime ='"+taskTime+"'");
		sql.append(" and pushUserAccount ='"+pushUserAccount+"'");
		logger.info(sql);
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("dishDate", rs.getString("dishDate"));
				commonMap.put("ppName", rs.getString("ppName"));
				commonMap.put("schType", rs.getString("schType"));
				commonMap.put("compDep", rs.getString("compDep"));
				commonMap.put("num", rs.getLong("num"));
				return new AppCommonDao(commonMap);
			}
		});
    }
    //保存今天排菜数据
    public boolean getInsertUnacceptedWarning(List<LinkedHashMap<String, Object>> dataList) {
    	if(jdbcTemplate2 == null)
    		return false;
    	 List<Object[]> objectList = new ArrayList<>();
    	for(int i=0;i<dataList.size();i++) {
    		 objectList.add(new  Object[] {
    				 dataList.get(i).get("taskTime"),
    				 dataList.get(i).get("pushUserAccount"),
    				 dataList.get(i).get("dishDate"),
    				 dataList.get(i).get("ppName"),
    				 dataList.get(i).get("schType"),
    				 dataList.get(i).get("compDep"),
    				 dataList.get(i).get("num")
    				 });
    	}
    	 String sql = " INSERT INTO t_edu_bd_un_unaccepted (taskTime, pushUserAccount, dishDate, ppName, schType, compDep, num) " +
                 " VALUES (?, ?, ?, ?, ?, ?, ?) ";
    	 jdbcTemplate2.batchUpdate(sql, objectList);
		return initProcFlag;
    }
    //获取下周排菜数据
    public List<AppCommonDao> getUnacceptedWarning(String taskTime,String pushUserAccount){
    	if(jdbcTemplate2 == null)
    		return null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_un_unaccepted ");
		sql.append(" where 1=1 ");
		sql.append(" and taskTime ='"+taskTime+"'");
		sql.append(" and pushUserAccount ='"+pushUserAccount+"'");
		logger.info(sql);
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("dishDate", rs.getString("dishDate"));
				commonMap.put("ppName", rs.getString("ppName"));
				commonMap.put("schType", rs.getString("schType"));
				commonMap.put("compDep", rs.getString("compDep"));
				commonMap.put("num", rs.getLong("num"));
				return new AppCommonDao(commonMap);
			}
		});
    }
    //保存今天排菜数据
    public boolean getInsertNotSampleWarning(List<LinkedHashMap<String, Object>> dataList) {
    	if(jdbcTemplate2 == null)
    		return false;
    	 List<Object[]> objectList = new ArrayList<>();
    	for(int i=0;i<dataList.size();i++) {
    		 objectList.add(new  Object[] {
    				 dataList.get(i).get("taskTime"),
    				 dataList.get(i).get("pushUserAccount"),
    				 dataList.get(i).get("dishDate"),
    				 dataList.get(i).get("ppName"),
    				 dataList.get(i).get("schType"),
    				 dataList.get(i).get("compDep"),
    				 dataList.get(i).get("num")
    				 });
    	}
    	 String sql = " INSERT INTO t_edu_bd_not_sample (taskTime, pushUserAccount, dishDate, ppName, schType, compDep, num) " +
                 " VALUES (?, ?, ?, ?, ?, ?, ?) ";
    	 jdbcTemplate2.batchUpdate(sql, objectList);
		return initProcFlag;
    }
    //获取下周排菜数据
    public List<AppCommonDao> getNotSampleWarning(String taskTime,String pushUserAccount){
    	if(jdbcTemplate2 == null)
    		return null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_not_sample ");
		sql.append(" where 1=1 ");
		sql.append(" and taskTime ='"+taskTime+"'");
		sql.append(" and pushUserAccount ='"+pushUserAccount+"'");
		logger.info(sql);
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("dishDate", rs.getString("dishDate"));
				commonMap.put("ppName", rs.getString("ppName"));
				commonMap.put("schType", rs.getString("schType"));
				commonMap.put("compDep", rs.getString("compDep"));
				commonMap.put("num", rs.getLong("num"));
				return new AppCommonDao(commonMap);
			}
		});
    }
    
    //保存今天排菜数据
    public boolean getInsertSteakDataAnomalyWarning(List<LinkedHashMap<String, Object>> dataList) {
    	if(jdbcTemplate2 == null)
    		return false;
    	 List<Object[]> objectList = new ArrayList<>();
    	for(int i=0;i<dataList.size();i++) {
    		 objectList.add(new  Object[] {
    				 dataList.get(i).get("taskTime"),
    				 dataList.get(i).get("pushUserAccount"),
    				 dataList.get(i).get("supplyDate"),
    				 dataList.get(i).get("schoolName"),
    				 dataList.get(i).get("menuGroupName"),
    				 dataList.get(i).get("caterTypeName"),
    				 dataList.get(i).get("dishesName"),
    				 dataList.get(i).get("mealsCount"),
    				 dataList.get(i).get("dishesNumber")
    				 });
    	}
    	 String sql = " INSERT INTO t_edu_bd_steak_anomaly (taskTime, pushUserAccount, supplyDate, schoolName, menuGroupName, caterTypeName, dishesName,mealsCount,dishesNumber) " +
                 " VALUES (?, ?, ?, ?, ?, ?, ?,?,?) ";
    	 jdbcTemplate2.batchUpdate(sql, objectList);
		return initProcFlag;
    }
    //获取下周排菜数据
    public List<AppCommonDao> getSteakDataAnomalyWarning(String taskTime,String pushUserAccount){
    	if(jdbcTemplate2 == null)
    		return null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_steak_anomaly ");
		sql.append(" where 1=1 ");
		sql.append(" and taskTime ='"+taskTime+"'");
		sql.append(" and pushUserAccount ='"+pushUserAccount+"'");
		logger.info(sql);
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("supplyDate", rs.getString("supplyDate"));
				commonMap.put("schoolName", rs.getString("schoolName"));
				commonMap.put("menuGroupName", rs.getString("menuGroupName"));
				commonMap.put("caterTypeName", rs.getString("caterTypeName"));
				commonMap.put("dishesName", rs.getString("dishesName"));
				commonMap.put("mealsCount", rs.getLong("mealsCount"));
				commonMap.put("dishesNumber", rs.getLong("dishesNumber"));
				return new AppCommonDao(commonMap);
			}
		});
    }
    
    //保存今天排菜数据
    public boolean getInsertAcceptanceDataAnomalyWarning(List<LinkedHashMap<String, Object>> dataList) {
    	if(jdbcTemplate2 == null)
    		return false;
    	 List<Object[]> objectList = new ArrayList<>();
    	for(int i=0;i<dataList.size();i++) {
    		 objectList.add(new  Object[] {
    				 dataList.get(i).get("taskTime"),
    				 dataList.get(i).get("pushUserAccount"),
    				 dataList.get(i).get("useDate"),
    				 dataList.get(i).get("schoolName"),
    				 dataList.get(i).get("wareBatchNo"),
    				 dataList.get(i).get("name"),
    				 dataList.get(i).get("otherQuantity"),
    				 dataList.get(i).get("deliveryNumber"),
    				 dataList.get(i).get("ratio")
    				 });
    	}
    	 String sql = " INSERT INTO t_edu_bd_acceptance_anomaly (taskTime, pushUserAccount, useDate, schoolName, wareBatchNo, name, otherQuantity,deliveryNumber,ratio) " +
                 " VALUES (?, ?, ?, ?, ?, ?, ?,?,?) ";
    	 int[] n=jdbcTemplate2.batchUpdate(sql, objectList);
		return initProcFlag;
    }
    //获取下周排菜数据
    public List<AppCommonDao> getAcceptanceDataAnomalyWarning(String taskTime,String pushUserAccount){
    	if(jdbcTemplate2 == null)
    		return null;
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_acceptance_anomaly ");
		sql.append(" where 1=1 ");
		sql.append(" and taskTime ='"+taskTime+"'");
		sql.append(" and pushUserAccount ='"+pushUserAccount+"'");
		logger.info(sql);
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("useDate", rs.getString("useDate"));
				commonMap.put("schoolName", rs.getString("schoolName"));
				commonMap.put("wareBatchNo", rs.getString("wareBatchNo"));
				commonMap.put("name", rs.getString("name"));
				commonMap.put("otherQuantity", rs.getString("otherQuantity"));
				commonMap.put("deliveryNumber", rs.getLong("deliveryNumber"));
				commonMap.put("ratio", rs.getFloat("ratio"));
				return new AppCommonDao(commonMap);
			}
		});
    }


	/** 
	 * @Description: 查询预警规则
	 * @Param: [warnType, warnAlertType] 
	 * @return: java.util.List<com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao> 
	 * @Author: jianghy 
	 * @Date: 2020/1/14
	 * @Time: 14:19       
	 */
	@Override
	public List<AppCommonDao> getCheckWarnSetting(Integer warnType,Integer warnAlertType){
		if(jdbcTemplate2 == null){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_warn_level_rule ");
		sql.append(" where 1=1 ");
		sql.append(" and warn_type ='"+warnType+"'");
		sql.append(" and warn_alert_type ='"+warnAlertType+"'");
		logger.info(sql);
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getInt("id"));
				commonMap.put("userAccount", rs.getString("user_account"));
				commonMap.put("warnType", rs.getInt("warn_type"));
				commonMap.put("warnAlertType", rs.getInt("warn_alert_type"));
				commonMap.put("scheduledStatus", rs.getInt("scheduled_status"));
				commonMap.put("warnPushContent", rs.getString("warn_push_content"));
				commonMap.put("warnDataTime", rs.getString("warn_data_time"));
				commonMap.put("warnPushTime", rs.getString("warn_push_time"));
				commonMap.put("pushReceiverMsg", rs.getString("push_receiver_msg"));
				commonMap.put("emailStatus", rs.getInt("email_status"));
				return new AppCommonDao(commonMap);
			}
		});
	}


	/** 
	 * @Description: 新增预警规则设置 
	 * @Param: [wlb] 
	 * @return: void 
	 * @Author: jianghy 
	 * @Date: 2020/1/15
	 * @Time: 15:44       
	 */
	@Override
	public void insertWarnRuleSetting(WarnLevelBody wlb){
		if(jdbcTemplate2 == null){
			return;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" insert into ");
		sql.append(" t_edu_bd_warn_level_rule(user_account,warn_type,warn_alert_type,scheduled_status,warn_push_content,warn_data_time,warn_push_time,push_receiver_msg,email_status) ");
		sql.append(" values (");
		sql.append(" '"+wlb.getUserAccount()+"','"+wlb.getWarnType()+"','"+wlb.getWarnAlertType()+"','"+wlb.getScheduledStatus()+"','"+wlb.getWarnPushContent()+"','"+wlb.getWarnDataTime()+"','"+wlb.getWarnPushTime()+"','"+wlb.getPushReceiverMsgStr()+"','"+wlb.getEmailStatus()+"'");
		sql.append(" )");
		logger.info(sql);
		jdbcTemplate2.execute(sql.toString());
	}


	/** 
	 * @Description: 修改预警规则设置 
	 * @Param: [wlb] 
	 * @return: void 
	 * @Author: jianghy 
	 * @Date: 2020/1/15
	 * @Time: 15:44       
	 */
	@Override
	public void updateWarnRuleSetting(WarnLevelBody wlb){
		if(jdbcTemplate2 == null){
			return;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" update ");
		sql.append(" t_edu_bd_warn_level_rule");
		sql.append(" set user_account = '"+wlb.getUserAccount()+"',scheduled_status = "+wlb.getScheduledStatus()+",push_receiver_msg = '"+wlb.getPushReceiverMsgStr()+"',email_status = "+wlb.getEmailStatus());
		sql.append(" where 1=1");
		sql.append(" and id =" + wlb.getId());
		logger.info(sql);
		jdbcTemplate2.execute(sql.toString());
	}


	/** 
	 * @Description: 插入预警标题 
	 * @Param: [map] 
	 * @return: void 
	 * @Author: jianghy 
	 * @Date: 2020/1/18
	 * @Time: 14:48       
	 */
	@Override
	public void insertWarnTitle(Map<String, Object> map) {
		if(jdbcTemplate2 == null){
			return;
		}
		if (map != null){
			Integer warnType = Integer.valueOf(map.get("warnType").toString());//预警类型
			String warnTitle = String.valueOf(map.get("warnTitle"));//预警标题
			String searchEndDate = String.valueOf(map.get("searchEndDate"));//截至日期
			String pushDate = String.valueOf(map.get("pushDate"));//推送时间
			String createTime = BCDTimeUtil.convertNormalFrom(null);//当前时间
			Integer readStatus = 1;//默认未读
			String userAccount = String.valueOf(map.get("userAccount"));//用户账户
			String periodDate = String.valueOf(map.get("periodDate"));//供餐周期
			String searchDepartment = String.valueOf(map.get("searchDepartment"));//统计部门
			Integer schoolNum = Integer.valueOf(map.get("schoolNum").toString());//统计部门
			String id = String.valueOf(map.get("id"));//id
			StringBuffer sql = new StringBuffer();
			sql.append(" insert into ");
			sql.append(" t_edu_bd_push_title(id,warn_type,warn_title,search_end_date,push_date,create_time,read_status,user_account,period_date,search_department,school_num)");
			sql.append(" values('"+id+"',"+warnType+",'"+warnTitle+"','"+searchEndDate+"','"+pushDate+"','"+createTime+"',"+readStatus+",'"+userAccount+"','"+periodDate+"','"+searchDepartment+"',"+schoolNum+")");
			logger.info(sql);
			jdbcTemplate2.execute(sql.toString());
		}else{
			return;
		}
	}

	
	/** 
	 * @Description: 插入预警内容 
	 * @Param: [list] 
	 * @return: void 
	 * @Author: jianghy 
	 * @Date: 2020/1/18
	 * @Time: 14:48       
	 */
	@Override
	public void insertWarnContent(List<LinkedHashMap<String, Object>> dataList) {
		if(jdbcTemplate2 == null){
			return;
		}
		List<Object[]> objectList = new ArrayList<>();
		for (LinkedHashMap<String, Object> stringObjectMap : dataList) {
			String createTime = BCDTimeUtil.convertNormalFrom(null);
			objectList.add(new Object[]{
					stringObjectMap.get("id"),
					stringObjectMap.get("titleId"),//标题id
					stringObjectMap.get("departmentName"),//管理部门名称
					stringObjectMap.get("schTypeName"),//学制
					stringObjectMap.get("diningName"),//项目点名称
					stringObjectMap.get("warnType"),//预警类型:(1：未排菜预警 , 2：未验收预警）
					stringObjectMap.get("warnAlertType"),//预警提示类型：(1：提示，2：提醒，3：预警，4：督办，5：追责)
					stringObjectMap.get("optDate"),//通用日期(如，未排菜日期，未验收日期)
					stringObjectMap.get("searchEndDate"),//截至日期
					stringObjectMap.get("pushAccount"),//推送对象
					createTime//创建时间
			});
		}
		String sql = " INSERT INTO t_edu_bd_push_content(id, title_id, department_name, sch_type_name, dining_name, warn_type, warn_alert_type, opt_date,search_end_date,push_account,create_time) " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		jdbcTemplate2.batchUpdate(sql, objectList);
	}

	
	/** 
	 * @Description: 修改已读未读状态 
	 * @Param: [titleId] 
	 * @return: void 
	 * @Author: jianghy 
	 * @Date: 2020/1/18
	 * @Time: 14:49       
	 */
	@Override
	public void updateWarnReadSatus(String titleId) {
		if(jdbcTemplate2 == null){
			return;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" update ");
		sql.append(" t_edu_bd_push_title set read_status = 2");
		sql.append(" where 1=1");
		sql.append(" and id ='"+ titleId +"'");
		logger.info(sql);
		jdbcTemplate2.execute(sql.toString());
	}

	
	/** 
	 * @Description: 根据标题获取预警列表 
	 * @Param: [titleId] 
	 * @return: java.util.List<com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao> 
	 * @Author: jianghy 
	 * @Date: 2020/1/18
	 * @Time: 14:50       
	 */
	@Override
	public List<AppCommonDao> getWarnListByTitleId(String titleId) {
		if(jdbcTemplate2 == null){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_push_content ");
		sql.append(" where 1=1 ");
		sql.append(" and title_id ='"+titleId+"'");
		sql.append(" order by create_time desc,department_name desc");
		logger.info(sql);
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getString("id"));
				commonMap.put("titleId", rs.getString("title_id"));
				commonMap.put("departmentName", rs.getString("department_name"));
				commonMap.put("schTypeName", rs.getString("sch_type_name"));
				commonMap.put("diningName", rs.getString("dining_name"));
				commonMap.put("warnType", rs.getInt("warn_type"));
				commonMap.put("warnAlertType", rs.getInt("warn_alert_type"));
				commonMap.put("optDate", rs.getString("opt_date"));
				commonMap.put("searchEndDate", rs.getString("search_end_date"));
				commonMap.put("pushAccount", rs.getString("push_account"));
				commonMap.put("createTime", rs.getString("create_time"));
				commonMap.put("createTime", rs.getString("create_time"));
				return new AppCommonDao(commonMap);
			}
		});
	}

	
	/** 
	 * @Description: 根据预警标题状态和用户状态获取预警标题列表
	 * @Param: [readsStatus, userAccount] 
	 * @return: java.util.List<com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao> 
	 * @Author: jianghy 
	 * @Date: 2020/1/18
	 * @Time: 14:50       
	 */
	@Override
	public List<AppCommonDao> getWarnTitleByReadStatus(Integer readsStatus, String userAccount,String id) {
		if(jdbcTemplate2 == null){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select id,warn_type,warn_title,search_end_date,push_date,create_time,read_status,user_account, ");
		sql.append(" period_date,search_department,school_num,warn_content ");
		sql.append(" from t_edu_bd_push_title ");
		sql.append(" where 1=1 ");
		if(readsStatus != null) {
			sql.append(" and read_status ='"+readsStatus+"'");
		}
		if(CommonUtil.isNotEmpty(userAccount)) {
			sql.append(" and user_account ='"+userAccount+"'");
		}
		if(CommonUtil.isNotEmpty(id)) {
			sql.append(" and id ='"+id+"'");
		}
		sql.append(" order by create_time desc");
		logger.info(sql);
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getString("id"));
				commonMap.put("warnType", rs.getInt("warn_type"));
				commonMap.put("warnTitle", rs.getString("warn_title"));
				commonMap.put("searchEndDate", rs.getString("search_end_date"));
				commonMap.put("createTime", getFormatDate(rs.getString("create_time")));
				commonMap.put("readStatus", rs.getInt("read_status"));
				commonMap.put("userAccount", rs.getString("user_account"));
				commonMap.put("periodDate", rs.getString("period_date"));
				commonMap.put("searchDepartment", rs.getString("search_department"));
				commonMap.put("schoolNum", rs.getInt("school_num"));
				commonMap.put("warnContent", rs.getString("warn_content"));
				return new AppCommonDao(commonMap);
			}
		});
	}
	
	/** 
	 * @Description: 根据预警标题状态和用户状态获取预警标题列表
	 * @Param: [readsStatus, userAccount] 
	 * @return: java.util.List<com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao> 
	 * @Author: jianghy 
	 * @Date: 2020/1/18
	 * @Time: 14:50       
	 */
	@Override
	public List<AppCommonDao> getWarnTitleById(String id) {
		if(jdbcTemplate2 == null){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select * ");
		sql.append(" from t_edu_bd_push_title ");
		sql.append(" where 1=1 ");
		if(CommonUtil.isNotEmpty(id)) {
			sql.append(" and id ='"+id+"'");
		}
		sql.append(" order by create_time desc");
		logger.info(sql);
		return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
			@Override
			public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
				LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
				commonMap.put("id", rs.getString("id"));
				commonMap.put("warnType", rs.getInt("warn_type"));
				commonMap.put("warnTitle", rs.getString("warn_title"));
				commonMap.put("searchEndDate", rs.getString("search_end_date"));
				commonMap.put("createTime", getFormatDate(rs.getString("create_time")));
				commonMap.put("readStatus", rs.getInt("read_status"));
				commonMap.put("userAccount", rs.getString("user_account"));
				commonMap.put("periodDate", rs.getString("period_date"));
				commonMap.put("searchDepartment", rs.getString("search_department"));
				commonMap.put("schoolNum", rs.getInt("school_num"));
				commonMap.put("warnContent", rs.getString("warn_content"));
				commonMap.put("annCont", rs.getBytes("template_content"));
				commonMap.put("amInfo", rs.getString("am_info"));
				return new AppCommonDao(commonMap);
			}
		});
	}
	
    //未读信息数量
    @Override
    public AppCommonDao getNoReadWarnInfoNum(Integer readsStatus, String userAccount) {
        if (jdbcTemplate2 == null)
            return null;
        StringBuffer sql = new StringBuffer();
        sql.append(" select count(*) as num ");
        sql.append(" from t_edu_bd_push_title ");
        sql.append(" where 1=1 ");
        sql.append(" and read_status ='" + readsStatus + "'");
        sql.append(" and user_account ='" + userAccount + "'");

        logger.info("sql语句：" + sql.toString());
        return jdbcTemplate2.query(sql.toString(), new RowMapper<AppCommonDao>() {
            @Override
            public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
                commonMap.put("num", rs.getString("num"));
                return new AppCommonDao(commonMap);
            }
        }).get(0);
    }


	/**
	 * @Description:  日期格式化
	 * @Param: [strDate]
	 * @return: java.lang.String
	 * @Author: jianghy
	 * @Date: 2020/1/12
	 * @Time: 12:03
	 */
	public String getFormatDate(String strDate){
		// 实例化模板对象
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm") ;
		// 实例化模板对象
		java.util.Date d = null ;
		try{
			d = sdf.parse(strDate) ;   // 将给定的字符串中的日期提取出来
		}catch(Exception e){            // 如果提供的字符串格式有错误，则进行异常处理
			e.printStackTrace() ;       // 打印异常信息
		}
		return sdf2.format(d);    // 将日期变为新的格式
	}

	
	//*******************通讯录相关*********************************
    public int deleteByPrimaryKeyAddressLable(Integer id) {
    	return tEduBdAddressLableMapper.deleteByPrimaryKey(id);
    }

    public int insertAddressLable(TEduBdAddressLableObj record){
    	return tEduBdAddressLableMapper.insert(record);
    }

    public TEduBdAddressLableObj selectByPrimaryKeyAddressLable(Integer id){
    	return tEduBdAddressLableMapper.selectByPrimaryKey(id);
    }
    
    public List<TEduBdAddressLableObj> selectListAddressLable(TEduBdAddressLableObj tEduBdAddressLableObj,Integer startNum,Integer pageSize){
    	return tEduBdAddressLableMapper.selectList(tEduBdAddressLableObj,startNum,pageSize);
    }
    
    public int selectListAddressLableCount(TEduBdAddressLableObj tEduBdAddressLableObj) {
    	return tEduBdAddressLableMapper.selectListCount(tEduBdAddressLableObj);
    }

    public int updateByPrimaryKeySelectiveAddressLable(TEduBdAddressLableObj record){
    	return tEduBdAddressLableMapper.updateByPrimaryKeySelective(record);
    }
    
    /**
     * 根据接口名称查询对应的列设置
     * @param interfaceName
     * @return
     */
    public Integer getAddressLableMaxId() {
    	return tEduBdAddressLableMapper.selectMaxId();
    }
    
    public List<TEduBdAddressLableObj> selectListAndUserCount(TEduBdAddressLableObj tEduBdAddressLableObj,Integer startNum,Integer pageSize){
    	return tEduBdAddressLableMapper.selectListAndUserCount(tEduBdAddressLableObj,startNum,pageSize);
    }
    
    
    public int deleteByPrimaryKeyUserLableRelation(String userId,Integer lableId) {
    	return tEduBdUserLableRelationMapper.deleteByPrimaryKey(userId,lableId);
    }
    
    public int deleteByLableId(Integer lableId) {
    	return tEduBdUserLableRelationMapper.deleteByLableId(lableId);
    }

    public int insertUserLableRelation(TEduBdUserLableRelationObj record){
    	return tEduBdUserLableRelationMapper.insert(record);
    }

   /* public TEduBdUserLableRelationObj selectByPrimaryKeyUserLableRelation(Integer id){
    	return tEduBdUserLableRelationMapper.selectByPrimaryKey(id);
    }
*/
    public int updateByPrimaryKeySelectiveUserLableRelation(TEduBdUserLableRelationObj record){
    	return tEduBdUserLableRelationMapper.updateByPrimaryKeySelective(record);
    }
    
    /**
     * 根据接口名称查询对应的列设置
     * @param interfaceName
     * @return
     */
    public List<TEduBdUserLableRelationObj> selectListAddressLableRelation(TEduBdUserLableRelationObj tEduBdAddressLableObj,Integer startNum,Integer pageSize){
    	return tEduBdUserLableRelationMapper.selectList(tEduBdAddressLableObj,startNum,pageSize);
    }

    
    //用户
    //从数据源ds2的数据表t_edu_bd_user中查找用户信息以单位ID
    public List<TEduBdUserDo> getBdUserInfoByUserLableRelation(Integer lableId) {
    	if(jdbcTemplate2 == null)
    		return null;
    	String sql = "select u.id, u.user_account, u.password, u.email, u.fix_phone, u.mobile_phone, "
    			+ "u.name, u.user_pic_url, u.is_admin, u.role_id, u.parent_id, u.last_login_time, "
    			+ "u.creator, u.create_time, u.updater, u.last_update_time, u.forbid, u.token, u.stat, "
    			+ "u.remarks, u.org_id, u.org_name, u.fax"
    			+ " from t_edu_bd_user u "
    			+ " left join t_edu_bd_user_lable_relation r on u.id = r.user_id "
    			+ " where 1=1 ";
    	if(lableId != null) {
    		sql += " and r.lable_id =  " + lableId;
    	}
    	logger.info("执行的MySql语句：" + sql);
        return (List<TEduBdUserDo>) jdbcTemplate2.query(sql, new RowMapper<TEduBdUserDo>(){

            @Override
            public TEduBdUserDo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	TEduBdUserDo tebuDo = new TEduBdUserDo();
            	tebuDo.setId(rs.getString("id"));
        		tebuDo.setUserAccount(rs.getString("user_account"));
        		tebuDo.setPassword(rs.getString("password"));
        		tebuDo.setEmail(rs.getString("email"));
        		tebuDo.setFixPhone(rs.getString("fix_phone"));
        		tebuDo.setMobilePhone(rs.getString("mobile_phone"));
        		tebuDo.setName(rs.getString("name"));
        		tebuDo.setUserPicUrl(rs.getString("user_pic_url"));
        		tebuDo.setIsAdmin(rs.getInt("is_admin"));
        		tebuDo.setRoleId(rs.getString("role_id"));
        		tebuDo.setParentId(rs.getString("parent_id"));
        		tebuDo.setLastLoginTime(rs.getString("last_login_time"));
        		tebuDo.setCreator(rs.getString("creator"));
        		tebuDo.setCreateTime(rs.getString("create_time"));
        		tebuDo.setUpdater(rs.getString("updater"));
        		tebuDo.setLastUpdateTime(rs.getString("last_update_time"));
        		tebuDo.setForbid(rs.getInt("forbid"));
        		tebuDo.setToken(rs.getString("token"));
        		tebuDo.setStat(rs.getInt("stat"));
        		tebuDo.setRemarks(rs.getString("remarks"));
        		tebuDo.setOrgId(rs.getString("org_id"));
        		tebuDo.setOrgName(rs.getString("org_name"));
        		tebuDo.setFax(rs.getString("fax"));
                return tebuDo;
            }
        });
    }
}

