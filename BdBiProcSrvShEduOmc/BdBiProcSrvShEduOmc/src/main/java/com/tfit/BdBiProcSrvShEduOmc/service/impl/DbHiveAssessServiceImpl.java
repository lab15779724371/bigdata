package com.tfit.BdBiProcSrvShEduOmc.service.impl;

import java.util.*;

import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dto.assess.AssessData;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveAssessService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description: 考核评价相关
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-13
 */
@Service
public class DbHiveAssessServiceImpl implements DbHiveAssessService {
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
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void initProc() {
        if (initProcFlag)
            return;
        initProcFlag = true;
        logger.info("定时建立与 DataSource数据源dsHive对象表示的数据源的连接，时间：" + BCDTimeUtil.convertNormalFrom(null));
        jdbcTemplateHive2 = new JdbcTemplate(dataSourceHive2);
    }

    /**
     * 考核评价查询列表 市教委账号
     *
     * @param filterParamMap
     * @return
     */
    @Override
    public List<AppCommonDao> getAssessList(LinkedHashMap<String, Object> filterParamMap) {
        if (dataSourceHive2 == null)
            return null;
        String filterStr = getWhereCondition(filterParamMap).toString();

        StringBuffer sql = new StringBuffer();
//        sql.append("select start_use_date,school_id,evaluate_period,school_name,area,department_id,basic_information_score ");
        sql.append("select start_use_date,end_use_date,school_id,school_name,area,department_id,synthesis_assessment_results ");

        //本地测试 对应的表
        //sql.append("from app_saas_v1.t_edu_school_review_details_w where 1=1 and (");
        //生产环境 对应的表
        if (StringUtils.isNotEmpty(filterStr)) {
            sql.append(" from app_saas_v1.app_t_edu_school_appraisals_w where 1=1 and (");
            if (filterStr.trim().startsWith("and")) {
                filterStr = filterStr.trim().substring(3);
            }
            sql.append(filterStr).append(")");
        } else {
            sql.append(" from app_saas_v1.app_t_edu_school_appraisals_w where 1=1");
        }

        /**
         * sql写法：右括号
         * select evaluate_period,school_name,area,management_department,comprehensive_evaluation_results from t_edu_school_review_details_w
         * where 1=1 and (start_use_date='2019-12-02' or start_use_date='2019-12-09' or start_use_date='2019-12-16') and area = '徐汇区' and school_name='上海市徐汇区向阳小学'
         */

        logger.info("sql语句：" + sql.toString());
        List<AppCommonDao> queryAssessList = jdbcTemplateHive2.query(sql.toString(), new RowMapper<AppCommonDao>() {
            @Override
            public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
//                commonMap.put("startDate", rs.getString("start_date"));
                commonMap.put("startDate", rs.getString("start_use_date"));
                commonMap.put("schoolId", rs.getString("school_id"));

//                commonMap.put("evaluatePeriod", rs.getString("evaluate_period"));
                DateTime startDt = BCDTimeUtil.convertDateStrToDate(rs.getString("start_use_date"));
                DateTime endDt = BCDTimeUtil.convertDateStrToDate(rs.getString("end_use_date"));
                commonMap.put("evaluatePeriod", startDt.toString("yyyy/MM/dd") + "-" +  endDt.toString("yyyy/MM/dd"));

                commonMap.put("schoolName", rs.getString("school_name"));
                commonMap.put("area", rs.getString("area"));
//                commonMap.put("managementDepartment", rs.getString("management_department"));
                commonMap.put("managementDepartment", rs.getString("department_id"));

                commonMap.put("comprehensiveEvaluationResults", rs.getString("synthesis_assessment_results"));
                return new AppCommonDao(commonMap);
            }
        });
        return queryAssessList;
    }

    /**
     * 考核评价查询列表 区账号
     *
     * @param filterParamMap
     * @return
     */
    @Override
    public List<AppCommonDao> getAssessAreaList(LinkedHashMap<String, Object> filterParamMap) {
        if (dataSourceHive2 == null)
            return null;

        StringBuilder whereCondition = getWhereCondition(filterParamMap);

        StringBuffer sql = new StringBuffer();
//        sql.append("select start_use_date,school_id,evaluate_period,school_name,area,management_department,comprehensive_evaluation_results ");
        sql.append("select start_use_date,end_use_date,school_id,school_name,area,department_id,synthesis_assessment_results ");
        //本地测试 对应的表
        //sql.append("from app_saas_v1.t_edu_school_review_details_w where 1=1 and ");
        //生产环境 对应的表
        sql.append(" from app_saas_v1.app_t_edu_school_appraisals_w where 1=1 and ");
        sql.append(whereCondition.toString());
        /**
         * sql写法：右括号
         * select evaluate_period,school_name,area,management_department,comprehensive_evaluation_results from t_edu_school_review_details_w
         * where 1=1 and management_department = '11' and (start_use_date='2019-12-02' or start_use_date='2019-12-09' or start_use_date='2019-12-16') and area = '11' and school_name='上海市徐汇区向阳小学'
         */
        logger.info("sql语句：" + sql.toString());
        List<AppCommonDao> queryAssessList = jdbcTemplateHive2.query(sql.toString(), new RowMapper<AppCommonDao>() {
            @Override
            public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
//                commonMap.put("startDate", rs.getString("start_date"));
                commonMap.put("startDate", rs.getString("start_use_date"));
                commonMap.put("schoolId", rs.getString("school_id"));

//                commonMap.put("evaluatePeriod", rs.getString("evaluate_period"));
                DateTime startDt = BCDTimeUtil.convertDateStrToDate(rs.getString("start_use_date"));
                DateTime endDt = BCDTimeUtil.convertDateStrToDate(rs.getString("end_use_date"));
                commonMap.put("evaluatePeriod", startDt.toString("yyyy/MM/dd") + "-" +  endDt.toString("yyyy/MM/dd"));

                commonMap.put("schoolName", rs.getString("school_name"));
                commonMap.put("area", rs.getString("area"));
//                commonMap.put("managementDepartment", rs.getString("management_department"));
//                commonMap.put("comprehensiveEvaluationResults", rs.getString("comprehensive_evaluation_results"));

                commonMap.put("managementDepartment", rs.getString("department_id"));
                commonMap.put("comprehensiveEvaluationResults", rs.getString("synthesis_assessment_results"));

                return new AppCommonDao(commonMap);
            }
        });
        return queryAssessList;
    }

    private StringBuilder getWhereCondition(LinkedHashMap<String, Object> paramMap) {
        List<String> filterItemList = new ArrayList<>();
        List<String> dateFilterItemList = new ArrayList<>();

        //如果departmentId  key存在 表示区账号查询 加上departmentId条件查询
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if ("school_name".equals(mapKey)) {
                String temp = (String) mapValue;
                filterItemList.add(" school_name like '%" + temp + "%'");
            } else if ("searchDates".equals(mapKey)) {
                List<String> searchDates = (List<String>) mapValue;
                for (String searchDate : searchDates) {
                    //把集合的日期 转化 为当前周的周一
                    //select date_add(next_day('2019-02-12','MO'),-7); 表示当前周 的周一
                    //表中start_use_date全部表示周一日期
                    dateFilterItemList.add(" start_use_date = date_add(next_day('" + searchDate + "','MO'),-7)");
                }
            } else {
                filterItemList.add(mapKey + "='" + mapValue + "'");
            }
        }
        StringBuilder whereCondition = new StringBuilder();

        whereCondition.append(StringUtils.join(filterItemList, " and "));
        if (dateFilterItemList.size() == 1) {
            whereCondition.append(" and ").append(dateFilterItemList.get(0));
        } else if (dateFilterItemList.size() > 1) {
            whereCondition.append(" and ")
                    .append("(")
                    .append(StringUtils.join(dateFilterItemList, " or "))
                    .append(")");
        }
        return whereCondition;
    }

    /**
     * 传入的时间周期为null时  考核评价查询列表 按时间倒序
     * 市教委 和 区账号
     *
     * @param filterParamMap
     * @return
     */
    @Override
    public List<AppCommonDao> getAssessTimeIsNullAreaList(LinkedHashMap<String, Object> filterParamMap) {
        if (dataSourceHive2 == null)
            return null;
        String filterStr = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if ("school_name".equals(mapKey)) {
                filterStr += " and school_name like '%" + mapValue + "%'";
            } else {
                filterStr += " and " + mapKey + "='" + mapValue + "'";
            }
        }

        StringBuffer sql = new StringBuffer();
//        sql.append("select start_use_date,school_id,evaluate_period,school_name,area,management_department,comprehensive_evaluation_results ");
        sql.append("select start_use_date,end_use_date,school_id,school_name,area,department_id,synthesis_assessment_results ");

        //sql.append("from app_saas_v1.app_t_edu_school_appraisals_w where 1=1 ");
        sql.append(" from app_saas_v1.app_t_edu_school_appraisals_w where 1=1 ");
        sql.append(filterStr);
        sql.append(" order by start_use_date desc ");
        logger.info("sql语句：" + sql.toString());

        List<AppCommonDao> queryAssessList = jdbcTemplateHive2.query(sql.toString(), new RowMapper<AppCommonDao>() {
            @Override
            public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
//                commonMap.put("startDate", rs.getString("start_date"));
                commonMap.put("startDate", rs.getString("start_use_date"));
                commonMap.put("schoolId", rs.getString("school_id"));
//                commonMap.put("evaluatePeriod", rs.getString("evaluate_period"));

                DateTime startDt = BCDTimeUtil.convertDateStrToDate(rs.getString("start_use_date"));
                DateTime endDt = BCDTimeUtil.convertDateStrToDate(rs.getString("end_use_date"));
                commonMap.put("evaluatePeriod", startDt.toString("yyyy/MM/dd") + "-" +  endDt.toString("yyyy/MM/dd"));

                commonMap.put("schoolName", rs.getString("school_name"));
                commonMap.put("area", rs.getString("area"));
//                commonMap.put("managementDepartment", rs.getString("management_department"));
//                commonMap.put("comprehensiveEvaluationResults", rs.getString("comprehensive_evaluation_results"));

                commonMap.put("managementDepartment", rs.getString("department_id"));
                commonMap.put("comprehensiveEvaluationResults", rs.getString("synthesis_assessment_results"));
                return new AppCommonDao(commonMap);
            }
        });
        return queryAssessList;
    }

    //考核评价详情
    @Override
    public List<AppCommonDao> getAssessDetailList(LinkedHashMap<String, Object> filterParamMap) {
        if (dataSourceHive2 == null)
            return null;

        Map<String, String> mapLevelName = new HashMap<>();
        mapLevelName.put("-1", "");
        mapLevelName.put("0", "托儿所");
        mapLevelName.put("1", "托幼园");
        mapLevelName.put("2", "托幼小");
        mapLevelName.put("3", "幼儿园");
        mapLevelName.put("4", "幼小");
        mapLevelName.put("5", "幼小初");
        mapLevelName.put("6", "幼小初高");
        mapLevelName.put("7", "小学");
        mapLevelName.put("8", "初级中学");
        mapLevelName.put("9", "高级中学");
        mapLevelName.put("10", "完全中学");
        mapLevelName.put("11", "九年一贯制学校");
        mapLevelName.put("12", "十二年一贯制学校");
        mapLevelName.put("13", "职业初中");
        mapLevelName.put("14", "中等职业学校");
        mapLevelName.put("15", "工读学校");
        mapLevelName.put("16", "特殊教育学校");
        mapLevelName.put("17", "其他");

        Map<String, String>  mapAreaName= new HashMap<>();
        mapAreaName.put("1", "黄浦区");
        mapAreaName.put("10", "静安区");
        mapAreaName.put("11", "徐汇区");
        mapAreaName.put("12", "长宁区");
        mapAreaName.put("13", "普陀区");
        mapAreaName.put("14", "虹口区");
        mapAreaName.put("15", "杨浦区");
        mapAreaName.put("16", "闵行区");
        mapAreaName.put("2", "嘉定区");
        mapAreaName.put("3", "宝山区");
        mapAreaName.put("4", "浦东新区");
        mapAreaName.put("5", "松江区");
        mapAreaName.put("6", "金山区");
        mapAreaName.put("7", "青浦区");
        mapAreaName.put("8", "奉贤区");
        mapAreaName.put("9", "崇明区");

        Map<String, String> mapSchoolNature = new HashMap<>();
        mapSchoolNature.put("-1", "");
        mapSchoolNature.put("0", "公办");
        mapSchoolNature.put("2", "民办");
        mapSchoolNature.put("3", "外籍人员子女学校");
        mapSchoolNature.put("4", "其他");

        Map<String, String> mapLicMainType = new HashMap<>();
        mapLicMainType.put("0", "自营");
        mapLicMainType.put("1", "外包");

        Map<String, String> mapLicMainChild0 = new HashMap<>();
        mapLicMainChild0.put("0", "自营");
        mapLicMainChild0.put("1", "自营");

        Map<String, String> mapLicMainChild1 = new HashMap<>();
        mapLicMainChild1.put("0", "托管");
        mapLicMainChild1.put("1", "外送");

        List<AppCommonDao> queryAssessList = new ArrayList<>();
        String filterStr = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            filterStr += " and " + mapKey + "='" + mapValue + "'";
        }


        StringBuffer sql = new StringBuffer();
//        sql.append("select school_name,address,school_nature_name,level_name,license_main_type,evaluate_period,comprehensive_evaluation_results,business_operations_evaluate,regulate_dishes_rate,have_class_total,guifan_platoon_total,regulate_check_rate,ledger_day_total,guifan_ledger_total,regulate_reserved_rate,reserve_day_total,guifan_reserve_total,warn_deal,warn_total,have_warn_total,edu_basic_data_evaluate,have_basic_informaton,edu_contact_information_evaluate,have_contact_information,external_evaluation,canteen_safety_level ");

        sql.append("select school_name,address,school_nature_name,level_name,license_main_type,license_main_child," +
                "start_use_date,end_use_date," +
                "basic_information_score,outer_score,business_operations_score,synthesis_assessment_results," +
                "have_class_total,guifan_platoon_total,platoon_lv," +
                "ledger_day_total,guifan_ledger_total,ledger_lv," +
                "reserve_day_total,guifan_reserve_total,reserve_lv," +
                "warn_total,have_warn_total,warn_lv," +
                "school_basic_information_total,have_school_basic_information_total,school_basic_information_lv," +
                "school_contact_information_total,have_school_contact_information_total,school_contact_information_lv ");

        //business_operations_evaluate,regulate_dishes_rate,regulate_check_rate,regulate_reserved_rate,warn_deal,
        //edu_basic_data_evaluate,have_basic_informaton,edu_contact_information_evaluate,have_contact_information,
        //external_evaluation,canteen_safety_level
        //sql.append("from app_saas_v1.t_edu_school_review_details_w where 1=1 and");
        sql.append(" from app_saas_v1.app_t_edu_school_appraisals_w where 1=1 ");
        sql.append(filterStr);
        logger.info("sql语句：" + sql.toString());
        queryAssessList = jdbcTemplateHive2.query(sql.toString(), new RowMapper<AppCommonDao>() {
            @Override
            public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
                commonMap.put("schoolName", rs.getString("school_name"));
                commonMap.put("address", rs.getString("address"));
                commonMap.put("schoolNatureName", mapSchoolNature.get(rs.getString("school_nature_name")));
                commonMap.put("levelName", mapLevelName.get(rs.getString("level_name")));
                commonMap.put("licenseMainType", mapLicMainType.get(rs.getString("license_main_type")));

                String licenseMainChild = "";
                if("0".equals(rs.getString("license_main_type"))){
                    licenseMainChild = mapLicMainChild0.get(rs.getString("license_main_child"));
                    licenseMainChild = "自营";
                }else if ("1".equals(rs.getString("license_main_type"))){
                    licenseMainChild = mapLicMainChild1.get(rs.getString("license_main_child"));
                }

                commonMap.put("licenseMainChild", licenseMainChild);

                DateTime startDt = BCDTimeUtil.convertDateStrToDate(rs.getString("start_use_date"));
                DateTime endDt = BCDTimeUtil.convertDateStrToDate(rs.getString("end_use_date"));
                commonMap.put("evaluatePeriod", startDt.toString("yyyy/MM/dd") + "-" +  endDt.toString("yyyy/MM/dd"));

                commonMap.put("comprehensiveEvaluationResults", rs.getString("synthesis_assessment_results"));
                commonMap.put("basicInformationScore", rs.getString("basic_information_score"));
                commonMap.put("businessOperationsScore", rs.getString("business_operations_score"));
                commonMap.put("synthesisAssessmentResults", rs.getString("synthesis_assessment_results"));


                commonMap.put("haveClassTotal", rs.getString("have_class_total"));
                commonMap.put("guifanPlatoonTotal", rs.getString("guifan_platoon_total"));
                commonMap.put("platoonLv", rs.getString("platoon_lv"));

                commonMap.put("ledgerDayTotal", rs.getString("ledger_day_total"));
                commonMap.put("guifanLedgerTotal", rs.getString("guifan_ledger_total"));
                commonMap.put("ledgerLv", rs.getString("ledger_lv"));

                commonMap.put("reserveDayTotal", rs.getString("reserve_day_total"));
                commonMap.put("guifanReserveTotal", rs.getString("guifan_reserve_total"));
                commonMap.put("reserveLv", rs.getString("reserve_lv"));

                commonMap.put("warnTotal", rs.getString("warn_total"));
                commonMap.put("haveWarnTotal", rs.getString("have_warn_total"));
                commonMap.put("warnLv", rs.getString("warn_lv"));

                commonMap.put("schoolBasicInformationTotal", rs.getString("school_basic_information_total"));
                commonMap.put("haveSchoolBasicInformationTotal", rs.getString("have_school_basic_information_total"));
                commonMap.put("schoolBasicInformationLv", rs.getString("school_basic_information_lv"));

                commonMap.put("schoolContactInformationTotal", rs.getString("school_contact_information_total"));
                commonMap.put("haveSchoolContactInformationTotal", rs.getString("have_school_contact_information_total"));
                commonMap.put("schoolContactInformationLv", rs.getString("school_contact_information_lv"));

                commonMap.put("outerScore", rs.getString("outer_score"));

                float outerScore = rs.getFloat("outer_score");
                String canteenSecurityLevel = "良好";
                if(outerScore > 1 && outerScore <= 3){
                    canteenSecurityLevel = "一般";
                }else if(outerScore <= 1){
                    canteenSecurityLevel = "差";
                }
                commonMap.put("canteenSecurityLevel", canteenSecurityLevel);

                return new AppCommonDao(commonMap);
            }
        });


        if(queryAssessList.size() == 0)
            return queryAssessList;

        AppCommonDao appCommonDao = queryAssessList.get(0);

        //趋势分析的数据 第一周。。。第五周  表join的话查询比较慢 所以 一周一周的数据单独查询出来
        /**
         * 第一周
         */
        String filterStr1 = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if ("start_use_date".equals(mapKey)) {
                String wk = (String) mapValue;

                //第1周 星期一 转换得到如 2019-12-02
//                String wkMonday = wk.substring(0, 10).replaceAll("/", "-");
//                filterStr1 += " and start_use_date = date_add('" + wkMonday + "',-14)";
                DateTime startDt = BCDTimeUtil.convertDateStrToDate(wk).minusWeeks(4);
                filterStr1 += " and start_use_date='"+ startDt.toString("yyyy-MM-dd") +"' ";
            } else {
                filterStr1 += " and " + mapKey + "='" + mapValue + "'";
            }
        }

        StringBuffer sql1 = new StringBuffer();

        sql1.append("select synthesis_assessment_results ");
        sql1.append("from app_saas_v1.app_t_edu_school_appraisals_w where 1=1");
        sql1.append(filterStr1);
        logger.info("sql语句1：" + sql1.toString());

        List<AppCommonDao> queryWkMon1 = jdbcTemplateHive2.query(sql1.toString(), new RowMapper<AppCommonDao>() {
            @Override
            public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
//                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
                LinkedHashMap<String, Object> commonMap = appCommonDao.getCommonMap();
                commonMap.put("comprehensiveEvaluationResults1", rs.getString("synthesis_assessment_results"));
                return new AppCommonDao(commonMap);
            }
        });

//        queryAssessList.addAll(queryWkMon1);

        /**
         * 第二周
         */
        String filterStr2 = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if ("start_use_date".equals(mapKey)) {
                String wk = (String) mapValue;
                //第1周 星期一 转换得到如 2019-12-02
//                String wkMonday = wk.substring(0, 10).replaceAll("/", "-");
//                filterStr2 += " and start_use_date = date_add('" + wkMonday + "',-7)";

                DateTime startDt = BCDTimeUtil.convertDateStrToDate(wk).minusWeeks(3);
                filterStr2 += " and start_use_date='"+ startDt.toString("yyyy-MM-dd") +"' ";
            } else {
                filterStr2 += " and " + mapKey + "='" + mapValue + "'";
            }
        }

        StringBuffer sql2 = new StringBuffer();

        sql2.append("select synthesis_assessment_results ");
        sql2.append("from app_saas_v1.app_t_edu_school_appraisals_w where 1=1");
        sql2.append(filterStr2);
        logger.info("sql语句2：" + sql2.toString());

        List<AppCommonDao> queryWkMon2 = jdbcTemplateHive2.query(sql2.toString(), new RowMapper<AppCommonDao>() {
            @Override
            public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
//                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
                LinkedHashMap<String, Object> commonMap = appCommonDao.getCommonMap();
                commonMap.put("comprehensiveEvaluationResults2", rs.getString("synthesis_assessment_results"));
                return new AppCommonDao(commonMap);
            }
        });

//        queryAssessList.addAll(queryWkMon2);

        /**
         * 第三周
         *
         */
        String filterStr3 = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if ("start_use_date".equals(mapKey)) {
                String wk = (String) mapValue;
                //第1周 星期一 转换得到如 2019-12-02
//                String wkMonday = wk.substring(0, 10).replaceAll("/", "-");
//                filterStr5 += " and start_use_date = date_add('" + wkMonday + "',-7)";
                DateTime startDt = BCDTimeUtil.convertDateStrToDate(wk).minusWeeks(2);
                filterStr3 += " and start_use_date='"+ startDt.toString("yyyy-MM-dd") +"' ";
            } else {
                filterStr3 += " and " + mapKey + "='" + mapValue + "'";
            }
        }

        StringBuffer sql3 = new StringBuffer();

        sql3.append("select synthesis_assessment_results ");
        sql3.append("from app_saas_v1.app_t_edu_school_appraisals_w where 1=1");
        sql3.append(filterStr3);
        logger.info("sql语句3：" + sql3.toString());

        List<AppCommonDao> queryWkMon3 = jdbcTemplateHive2.query(sql3.toString(), new RowMapper<AppCommonDao>() {
            @Override
            public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
//                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
                LinkedHashMap<String, Object> commonMap = appCommonDao.getCommonMap();
                commonMap.put("comprehensiveEvaluationResults3", rs.getString("synthesis_assessment_results"));
                return new AppCommonDao(commonMap);
            }
        });
//        queryAssessList.addAll(queryWkMon3);

        /**
         * 第四周
         */
        String filterStr4 = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if ("start_use_date".equals(mapKey)) {
                String wk = (String) mapValue;
                //第1周 星期一 转换得到如 2019-12-02
//                String wkMonday = wk.substring(0, 10).replaceAll("/", "-");
//                filterStr4 += " and start_use_date = date_add('" + wkMonday + "',-7)";

                DateTime startDt = BCDTimeUtil.convertDateStrToDate(wk).minusWeeks(1);
                filterStr4 += " and start_use_date='"+ startDt.toString("yyyy-MM-dd") +"' ";
            } else {
                filterStr4 += " and " + mapKey + "='" + mapValue + "'";
            }
        }

        StringBuffer sql4 = new StringBuffer();

        sql4.append("select synthesis_assessment_results ");
        //sql.append("from app_saas_v1.t_edu_school_review_details_w where 1=1 and");
        sql4.append("from app_saas_v1.app_t_edu_school_appraisals_w where 1=1");
        sql4.append(filterStr4);
        logger.info("sql语句4：" + sql4.toString());

        List<AppCommonDao> queryWkMon4 = jdbcTemplateHive2.query(sql4.toString(), new RowMapper<AppCommonDao>() {
            @Override
            public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
//                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
                LinkedHashMap<String, Object> commonMap = appCommonDao.getCommonMap();
                commonMap.put("comprehensiveEvaluationResults4", rs.getString("synthesis_assessment_results"));
                return new AppCommonDao(commonMap);
            }
        });

//        queryAssessList.addAll(queryWkMon4);

        /**
         * 第五周
         */
        LinkedHashMap<String, Object> commonMap = appCommonDao.getCommonMap();
        commonMap.put("comprehensiveEvaluationResults5", commonMap.get("comprehensiveEvaluationResults"));

//        String filterStr5 = "";
//        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
//            String mapKey = entry.getKey();
//            Object mapValue = entry.getValue();
//            if ("start_use_date".equals(mapKey)) {
//                String wk = (String) mapValue;
//                //第1周 星期一 转换得到如 2019-12-02
////                String wkMonday = wk.substring(0, 10).replaceAll("/", "-");
////                filterStr5 += " and start_use_date = date_add('" + wkMonday + "',-7)";
//                DateTime startDt = BCDTimeUtil.convertDateStrToDate(wk).minusWeeks(1);
//                filterStr5 += " and start_use_date='"+ startDt.toString("yyyy-MM-dd") +"' ";
//            } else {
//                filterStr5 += " and " + mapKey + "='" + mapValue + "'";
//            }
//        }
//
//        StringBuffer sql5 = new StringBuffer();
//
//        sql5.append("select synthesis_assessment_results ");
//        //sql.append("from app_saas_v1.t_edu_school_review_details_w where 1=1 and");
//        sql5.append("from app_saas_v1.app_t_edu_school_appraisals_w where 1=1");
//        sql5.append(filterStr5);
//        logger.info("sql语句5：" + sql5.toString());
//
//        List<AppCommonDao> queryWkMon5 = jdbcTemplateHive2.query(sql5.toString(), new RowMapper<AppCommonDao>() {
//            @Override
//            public AppCommonDao mapRow(ResultSet rs, int rowNum) throws SQLException {
////                LinkedHashMap<String, Object> commonMap = new LinkedHashMap<String, Object>();
//                LinkedHashMap<String, Object> commonMap = appCommonDao.getCommonMap();
//                commonMap.put("comprehensiveEvaluationResults5", rs.getString("synthesis_assessment_results"));
//                return new AppCommonDao(commonMap);
//            }
//        });
//        queryAssessList.addAll(queryWkMon5);

        return queryAssessList;
    }

    //导出考核评价
    @Override
    public List<AssessData> getExpAssessList(LinkedHashMap<String, Object> filterParamMap) {
        if (dataSourceHive2 == null)
            return null;
        String filterStr = "";
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            if("start_use_date".equals(mapKey) && mapValue!= null){
                List<String> dates = (List<String>) mapValue;
                if(dates.size() > 0){
                    filterStr += " and start_use_date in(";
                    for(int i =0;i<dates.size();i++){
                        filterStr += "'" + dates.get(i) + "'";
                        if(i < dates.size()-1){
                            filterStr +=",";
                        }
                    }
                    filterStr += ") ";

                }
            }else if("school_name".equals(mapKey) && mapValue != null){
                filterStr += " and " + mapKey + " like '%" + mapValue + "%' ";
            }else{
                if (mapValue != null && !mapValue.toString().trim().isEmpty()) {
                    filterStr += " and " + mapKey + "='" + mapValue + "'";
                }
            }

        }
        StringBuffer sql = new StringBuffer();
        sql.append("select start_use_date,end_use_date,school_name,area,department_id,synthesis_assessment_results ");
//        sql.append("from app_saas_v1.t_edu_school_review_details_w where 1=1");
        sql.append("from app_saas_v1.app_t_edu_school_appraisals_w where 1=1 ");

        //sql.append(" where 1=1");
        sql.append(filterStr);
        //sql.append(" ORDER BY time desc ");
        logger.info("sql语句：" + sql.toString());
        List<AssessData> expAssessList = jdbcTemplateHive2.query(sql.toString(), new RowMapper<AssessData>() {
            @Override
            public AssessData mapRow(ResultSet rs, int rowNum) throws SQLException {
                AssessData assessData = new AssessData();
//                assessData.setEvaluatePeriod(rs.getString("evaluate_period"));
                DateTime startDt = BCDTimeUtil.convertDateStrToDate(rs.getString("start_use_date"));
                DateTime endDt = BCDTimeUtil.convertDateStrToDate(rs.getString("end_use_date"));
                assessData.setEvaluatePeriod(startDt.toString("yyyy/MM/dd") + "-" +  endDt.toString("yyyy/MM/dd"));

                assessData.setSchoolName(rs.getString("school_name"));
                assessData.setArea(rs.getString("area"));
                assessData.setManagementDepartment(rs.getString("department_id"));
                assessData.setComprehensiveEvaluationResults(rs.getString("synthesis_assessment_results"));
                return assessData;
            }
        });
        return expAssessList;
    }
}
