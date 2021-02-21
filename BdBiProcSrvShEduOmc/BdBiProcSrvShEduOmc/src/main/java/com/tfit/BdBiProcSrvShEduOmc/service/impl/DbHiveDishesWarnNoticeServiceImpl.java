package com.tfit.BdBiProcSrvShEduOmc.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishesWarnNoticeService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;


/**
 * @Description: 未排菜预警通知
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-15
 */
@Service
public class DbHiveDishesWarnNoticeServiceImpl implements DbHiveDishesWarnNoticeService {
    private static final Logger logger = LogManager.getLogger(DbHiveWarnServiceImpl.class.getName());

    //额外数据源Hive
  /*  @Autowired
    @Qualifier("dsHive")
    DataSource dataSourceHive;*/

    @Autowired
    @Qualifier("dsHive2")
    DataSource dataSourceHive2;

    //额外数据源Hive连接模板
    //JdbcTemplate jdbcTemplateHive = null;

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
        // jdbcTemplateHive = new JdbcTemplate(dataSourceHive);

        jdbcTemplateHive2 = new JdbcTemplate(dataSourceHive2);
    }
    // 未排菜预警通知 市教委  获取全部
    @Override
    public List<LinkedHashMap<String, Object>> getDishWarnNoticeList(LinkedHashMap<String, Object>  filterParamMap) {
        if (dataSourceHive2 == null)
            return null;
        String filterStr = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
                if ("warnDate".equals(mapKey)) {
                    String value = (String) mapValue;
                    //分区处理
            		if (value!=null && value.split("/").length == 3) {
                	    filterStr += " and year =" + Integer.parseInt(value.substring(0,value.indexOf("/")));
                	    filterStr += " and month =" + Integer.parseInt(value.substring(value.indexOf("/")+1,value.lastIndexOf("/"))) ;
            		}
            		
                      filterStr += " and date_format(warn_date,'yyyy/MM/dd') ='" + value + "'";
                    //filterStr += " and date_format(warn_date,'yyyy-MM-dd') ='" + value + "'";
                    logger.info("日期*****"+filterStr);
                }else {
                    if ("1".equals(mapValue) ) {
                        mapValue = "提示";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    } else if ("2".equals(mapValue)) {
                        mapValue = "提醒";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }else if ("3".equals(mapValue)) {
                        mapValue = "预警";
                        filterStr += " and warn_level_name" +  "='" + mapValue + "'";
                    }else if ("4".equals(mapValue)) {
                        mapValue = "警告";
                        filterStr += " and warn_level_name" +  "='" + mapValue + "'";
                    } else if ("5".equals(mapValue)) {
                        mapValue = "追责";
                        filterStr += " and warn_level_name" +  "='" + mapValue + "'";
                    }else {
                         mapValue = " ";
                        filterStr += " and warn_level_name" +  "='" + mapValue + "'";
                    }

                }

            }
        }
        StringBuffer sql = new StringBuffer();
        //sql.append("set hive.groupby.skewindata=true;set io.sort.mb=10;set mapreduce.map.java.opts=-Xmx7168m -XX:+UseConcMarkSweepGC;set hive.optimize.skewjoin=true;set hive.map.aggr=false;");
        sql.append("set mapreduce.map.java.opts=-Xmx7168m -XX:+UseConcMarkSweepGC;");
        sql.append("select department_id,no_platoon_total,rn from app_t_edu_no_platoon_total_d where 1=1 ");
        sql.append(filterStr);
        //sql.append(" order by no_platoon_total desc");

        logger.info("sql语句：" + sql.toString());
        List<LinkedHashMap<String, Object>> queryDishesList = jdbcTemplateHive2.query(sql.toString(), new RowMapper<LinkedHashMap<String, Object>>() {
            @Override
            public LinkedHashMap<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
                //commonMap.put("department_name", rs.getString("department_id"));
                commonMap.put("departmentId", rs.getString("department_id"));
                commonMap.put("noPlatoonTotal", rs.getString("no_platoon_total"));
                commonMap.put("rn", rs.getString("rn"));
                //commonMap.put("maxNum", rs.getString("maxNum"));
                return commonMap;
            }
        });
        return queryDishesList;
    }

    // 未排菜预警通知 区  根据departmentId ,warnDate,warnLevelName 查询
    @Override
    public List<LinkedHashMap<String, Object>> getAreaDishWarnNoticeList(LinkedHashMap<String, Object> filterParamMap) {
        if (dataSourceHive2 == null)
            return null;
        String filterStr = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
                if ("warnDate".equals(mapKey)) {
                	String value = (String) mapValue;
                	//分区处理
            		if (value!=null && value.split("/").length == 3) {
                	    filterStr += " and year =" + Integer.parseInt(value.substring(0,value.indexOf("/")));
                	    filterStr += " and month =" + Integer.parseInt(value.substring(value.indexOf("/")+1,value.lastIndexOf("/"))) ;
            		}
                    
                    filterStr += " and date_format(warn_date,'yyyy/MM/dd') ='" + value + "'";
                    //filterStr += " and date_format(warn_date,'yyyy-MM-dd') ='" + value + "'";
                    logger.info("日期*****"+filterStr);
                }else if("warnLevelName".equals(mapKey)){
                    if ("1".equals(mapValue) ) {
                        mapValue = "提示";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    } else if ("2".equals(mapValue)) {
                        mapValue = "提醒";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }else if ("3".equals(mapValue)) {
                        mapValue = "预警";
                        filterStr += " and warn_level_name" +  "='" + mapValue + "'";
                    }else if ("4".equals(mapValue)) {
                        mapValue = "警告";
                        filterStr += " and warn_level_name" +  "='" + mapValue + "'";
                    } else if ("5".equals(mapValue)) {
                        mapValue = "追责";
                        filterStr += " and warn_level_name" +  "='" + mapValue + "'";
                    }else {
                        mapValue = " ";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }

                }else {
                    String value = (String) mapValue;
                    filterStr += " and department_id ='" + value + "'";
                }

            }
        }
        StringBuffer sql = new StringBuffer();

        sql.append("select department_id,no_platoon_total,rn from app_t_edu_no_platoon_total_d where 1=1 ");
        sql.append(filterStr);
        //sql.append(" order by no_platoon_total desc");

        logger.info("sql语句：" + sql.toString());
        List<LinkedHashMap<String, Object>> queryAreaDishes = jdbcTemplateHive2.query(sql.toString(), new RowMapper<LinkedHashMap<String, Object>>() {
            @Override
            public LinkedHashMap<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
                //commonMap.put("department_name", rs.getString("department_id"));
                commonMap.put("departmentId", rs.getString("department_id"));
                commonMap.put("noPlatoonTotal", rs.getString("no_platoon_total"));
                commonMap.put("rn", rs.getString("rn"));
                return commonMap;
            }
        });
        return queryAreaDishes;
    }

    // 未验收预警通知  市教委账号查询
    @Override
    public List<LinkedHashMap<String, Object>> getCheckWarnNoticeList(LinkedHashMap<String, Object>  filterParamMap) {
        if (dataSourceHive2 == null)
            return null;
        String filterStr = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
                if ("warnDate".equals(mapKey)) {
                    String value =(String) mapValue;
                    
                    //分区处理
            		if (value!=null && value.split("/").length == 3) {
                	    filterStr += " and year =" + Integer.parseInt(value.substring(0,value.indexOf("/")));
                	    filterStr += " and month =" + Integer.parseInt(value.substring(value.indexOf("/")+1,value.lastIndexOf("/"))) ;
            		}
            		
                    filterStr += " and date_format(warn_date,'yyyy/MM/dd') ='" + value + "'";
                    //filterStr += " and date_format(warn_date,'yyyy-MM-dd') ='" + value + "'";
                    //filterStr += " and warn_date.substring(0,10) ='" + value + "'";
                    logger.info("日期*****"+filterStr);
                }else  {
                    if ("1".equals(mapValue) ) {
                        mapValue = "提示";
                        //filterStr += " and " + mapKey + "='" + mapValue + "'";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    } else if ("2".equals(mapValue)) {
                        mapValue = "提醒";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }else if ("3".equals(mapValue)) {
                        mapValue = "预警";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }else if ("4".equals(mapValue)) {
                        mapValue = "警告";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    } else if ("5".equals(mapValue)) {
                        mapValue = "追责";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }else {
                        mapValue = " ";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }
                }
            }
        }
        StringBuffer sql = new StringBuffer();

        sql.append("select department_id,no_ledger_total,rn from app_t_edu_no_ledger_total_d where 1=1 ");
        sql.append(filterStr);
        //sql.append(" order by no_ledger_total desc");

        logger.info("sql语句：" + sql.toString());
        List<LinkedHashMap<String, Object>> queryCheckList = jdbcTemplateHive2.query(sql.toString(), new RowMapper<LinkedHashMap<String, Object>>() {
            @Override
            public LinkedHashMap<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
                //commonMap.put("department_name", rs.getString("department_id"));
                commonMap.put("departmentId", rs.getString("department_id"));
                commonMap.put("noLedgerTotal", rs.getString("no_ledger_total"));
                commonMap.put("rn", rs.getString("rn"));
                return commonMap;
            }
        });
        return queryCheckList;
    }

    //未验收预警通知 区账号  根据departmentId ,warnDate,warnLevelName 查询
    @Override
    public List<LinkedHashMap<String, Object>> getAreaCheckWarnNoticeList(LinkedHashMap<String, Object> filterParamMap) {
        if (dataSourceHive2 == null)
            return null;
        String filterStr = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
                if ("warnDate".equals(mapKey)) {
                    String value =(String) mapValue;
                    
                    //分区处理
            		if (value!=null && value.split("/").length == 3) {
                	    filterStr += " and year =" + Integer.parseInt(value.substring(0,value.indexOf("/")));
                	    filterStr += " and month =" + Integer.parseInt(value.substring(value.indexOf("/")+1,value.lastIndexOf("/"))) ;
            		}
            		
                    filterStr += " and date_format(warn_date,'yyyy/MM/dd') ='" + value + "'";
                    logger.info("日期*****"+filterStr);
                }else if("warnLevelName".equals(mapKey)){
                    if ("1".equals(mapValue) ) {
                        mapValue = "提示";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    } else if ("2".equals(mapValue)) {
                        mapValue = "提醒";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }else if ("3".equals(mapValue)) {
                        mapValue = "预警";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }else if ("4".equals(mapValue)) {
                        mapValue = "警告";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    } else if ("5".equals(mapValue)) {
                        mapValue = "追责";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }else {
                        mapValue = " ";
                        filterStr += " and warn_level_name" + "='" + mapValue + "'";
                    }

                } else {
                    String value = (String) mapValue;
                    filterStr += " and department_id ='" + value + "'";
                }

            }
        }
        StringBuffer sql = new StringBuffer();

        sql.append("select department_id,no_ledger_total,rn from app_t_edu_no_ledger_total_d where 1=1 ");
        sql.append(filterStr);
        //sql.append(" order by no_ledger_total desc");

        logger.info("sql语句：" + sql.toString());
        List<LinkedHashMap<String, Object>> queryAreaCheck = jdbcTemplateHive2.query(sql.toString(), new RowMapper<LinkedHashMap<String, Object>>() {
            @Override
            public LinkedHashMap<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
                //commonMap.put("department_name", rs.getString("department_id"));
                commonMap.put("departmentId", rs.getString("department_id"));
                commonMap.put("noLedgerTotal", rs.getString("no_ledger_total"));
                commonMap.put("rn", rs.getString("rn"));
                return commonMap;
            }
        });
        return queryAreaCheck;
    }
    
}
