package com.tfit.BdBiProcSrvShEduOmc.config;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
/*
 * 获得数据源DataSource,临时用,不需要启动容器
 */
public class DataSourceConn extends BasicDataSource {

	 public DataSource getDataSource(String ip) {
		  DataSource ds = null;
		  super.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
		  //数据库是testdb1 
		  super.setUrl("jdbc:hive2://"+ip+":10000/app_saas_v1?autoReconnect=true&useSSL=false&useJDBCCompliantTimezoneShift=true"
		  		+ "&useLegacyDatetimeCode=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8");
		  super.setUsername("");
		  super.setPassword("");
		  try {
			  ds = super.createDataSource();
		  } catch (SQLException e) {
			  e.printStackTrace();
		  }
		  return ds;
	 }
}
