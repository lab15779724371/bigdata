package com.tfit.BdBiProcSrvShEduOmc.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
 
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//数据库连接测试;
@RestController
@RequestMapping(value = "/dbConn")
public class DbConnTestController {
	private static final Logger logger = LogManager.getLogger(DbConnTestController.class.getName());
   
    //没有指定为主数据源.
    @Autowired
    private DataSource dataSource;
   
    @Autowired
    @Qualifier("ds1")
    private DataSource dataSource1;
   
    @Autowired
    @Qualifier("ds2")
    private DataSource dataSource2;
    
    @Autowired
    @Qualifier("dsHive")
    private DataSource dataSourceHive;
    
    
    /*@Autowired
    @Qualifier("ds3")
    private DataSource dataSource3;
   
    @Autowired
    @Qualifier("ds4")
    private DataSource dataSource4;
    
    @Autowired
    @Qualifier("ds5")
    private DataSource dataSource5;
   
    @Autowired
    @Qualifier("ds6")
    private DataSource dataSource6;
    
    @Autowired
    @Qualifier("ds7")
    private DataSource dataSource7;*/
   
    @SuppressWarnings("unused")
	private JdbcTemplate jdbcTemplate;   
   
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    	logger.info("DbConnTestController.setJdbcTemplate()");
    	jdbcTemplate.setDataSource(dataSource1);//设置dataSource
    	this.jdbcTemplate = jdbcTemplate;
    }
 
    @RequestMapping("/getDefaultDb")
    public String get() {
    	//观察控制台的打印信息.
    	logger.info(dataSource);
    	
    	return"ok.DefaultDb";
    }
   
    @RequestMapping("/getDs1Db")
    public String getDs1Db() {
    	//观察控制台的打印信息.
    	logger.info(dataSource1);
    	
    	return"ok.Ds1";
    }
    
    @RequestMapping("/getDs2Db")
    public String getDs2Db() {
    	//观察控制台的打印信息.
    	logger.info(dataSource2);
    	
    	return"ok.Ds2";
    }
    
    @RequestMapping("/getDsHiveDb")
    public String getDsHiveDb() {
    	//观察控制台的打印信息.
    	logger.info(dataSourceHive);
    	
    	return"ok.DsHive";
    }
    
    
    /*@RequestMapping("/getDs3Db")
    public String getDs3Db() {
    	//观察控制台的打印信息.
    	logger.info(dataSource3);
    	
    	return"ok.Ds3";
    }
    
    @RequestMapping("/getDs4Db")
    public String getDs4Db() {
    	//观察控制台的打印信息.
    	logger.info(dataSource4);
    	
    	return"ok.Ds4";
    }
    
    @RequestMapping("/getDs5Db")
    public String getDs5Db() {
    	//观察控制台的打印信息.
    	logger.info(dataSource5);
    	
    	return"ok.Ds5";
    }
    
    @RequestMapping("/getDs6Db")
    public String getDs6Db() {
    	//观察控制台的打印信息.
    	logger.info(dataSource6);
    	
    	return"ok.Ds6";
    }
    
    @RequestMapping("/getDs7Db")
    public String getDs7Db() {
    	//观察控制台的打印信息.
    	logger.info(dataSource7);
    	
    	return"ok.Ds7";
    }*/
   
    @RequestMapping("/getDs1Data1")
    public String getDs1Data1() {
    	//观察控制台的打印信息.
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource1);
    	logger.info(jdbcTemplate.getDataSource());
    	logger.info(jdbcTemplate);
        
    	/*
    	 * Demo1只在test1中存在，test并没有此数据库;
         * 需要自己自己进行复制，不然会报错：Table 'test1.demo1' doesn't exist
         */
    	String sql = "select id,name from t_edu_district";
    	jdbcTemplate.query(sql, new RowMapper<String>(){
    		
    		@Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
    			logger.info(rs.getString("id")+"---"+rs.getString("name"));
    			
    			return "";
            }
 
        });
      
    	return"ok.Ds1Data1";
    }
}