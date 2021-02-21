package com.tfit.BdBiProcSrvShEduOmc.appmod.assess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;
import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonData;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.dto.assess.AssessData;
import com.tfit.BdBiProcSrvShEduOmc.dto.assess.AssessListDataDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveAssessService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Description: 考核评价数据应用模型
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-09
 */
public class AssessListMod {
    private static final Logger logger = LogManager.getLogger(AssessListMod.class.getName());
    // 是否为真实数据标识
    private static boolean isRealData = true;

    // 页号、页大小和总页数
    int curPageNum = 1, pageTotal = 1, pageSize = 20;

    //考核评价查询
    public String queryAssessModFunc(HttpServletRequest request, DbHiveAssessService dbHiveAssessService, Db1Service db1Service, Db2Service db2Service) {
        // 固定Dto层
        AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
        AppCommonData appCommonData = new AppCommonData();
        List<AppCommonDao> sourceDao = null;
        AppCommonDao pageTotal = null;
        List<LinkedHashMap<String, Object>> dataList = new ArrayList();

        // 业务操作
        try {
            //授权码
            String token = request.getHeader("Authorization");
            //验证授权
//            boolean verTokenFlag = true;
            boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service, new int[2]);

            if (verTokenFlag) {
                // 以下业务逻辑层修改

                //分页参数
                if (request.getParameter("page") != null && !request.getParameter("page").toString().isEmpty()) {
                    this.curPageNum = Integer.parseInt(request.getParameter("page").toString());
                }
                if (request.getParameter("pageSize") != null && !request.getParameter("pageSize").toString().isEmpty()) {
                    this.pageSize = Integer.parseInt(request.getParameter("pageSize").toString());
                }
                Integer startNum = (curPageNum - 1) * pageSize;

                // 筛选参数
                LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();

                String startDateAssess = request.getParameter("startDateAssess");
                String endDateAssess = request.getParameter("endDateAssess");

                //获取用户权限区域ID
                String departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);
                //如果departmentId不为空  属于区账号 查询加departmentId条件
                if (!CommonUtil.isEmpty(departmentId)) {
                    //添加departmentId作为查询条件 getAssessAreaList
//                    filterParamMap.put("departmentId", departmentId);
                    //filterParamMap.put("searchDates", searchDates);
                    filterParamMap.put("department_id", departmentId);

                    /**
                     * 注意：
                     * 分时间参数为空 和 不为空 两种情况
                     */
                    if (CommonUtil.isEmpty(startDateAssess) || CommonUtil.isEmpty(endDateAssess)) {
                        //时间周期为null  根据其他查询条件查询 全部 按时间倒序 排列
                        if (request.getParameter("area") != null) {
                            filterParamMap.put("area", request.getParameter("area"));
                        }

//                        if (request.getParameter("departmentId") != null) {
//                            filterParamMap.put("department_id", request.getParameter("departmentId"));
//                        }

                        if (request.getParameter("managementDepartment") != null) {
                            filterParamMap.put("department_id", request.getParameter("managementDepartment"));
                        }
                        if (request.getParameter("schoolName") != null) {
                            filterParamMap.put("school_name", request.getParameter("schoolName"));
                        }
                        if (request.getParameter("schoolId") != null) {
                            filterParamMap.put("school_id", request.getParameter("schoolId"));
                        }

                        sourceDao = dbHiveAssessService.getAssessTimeIsNullAreaList(filterParamMap);

                    } else { // 按照开始日期和结束日期获取数据

                        // 日期  如2019-12-02,2019-12-03......2019-12-08  周一到周日 7 天的日期
                        List<String> searchDates = new ArrayList<>();
                        DateTime startDt = BCDTimeUtil.convertDateStrToDate(startDateAssess);
                        DateTime endDt = BCDTimeUtil.convertDateStrToDate(endDateAssess);
                        int days = Days.daysBetween(startDt, endDt).getDays() + 1;
                        for (int i = 0; i < days; i++) {
                            searchDates.add(endDt.minusDays(i).toString("yyyy-MM-dd"));
                        }

                        if (searchDates.size() > 0) {
                            filterParamMap.put("searchDates", searchDates);
                        }

                        if (request.getParameter("area") != null) {
                            filterParamMap.put("area", request.getParameter("area"));
                        }

//                        if (request.getParameter("departmentId") != null) {
//                            filterParamMap.put("department_id", request.getParameter("departmentId"));
//                        }

                        if (request.getParameter("managementDepartment") != null) {
                            filterParamMap.put("department_id", request.getParameter("managementDepartment"));
                        }
                        if (request.getParameter("schoolName") != null) {
                            filterParamMap.put("school_name", request.getParameter("schoolName"));
                        }
                        if (request.getParameter("schoolId") != null) {
                            filterParamMap.put("school_id", request.getParameter("schoolId"));
                        }
                        sourceDao = dbHiveAssessService.getAssessAreaList(filterParamMap);
                    }

                } else {
                    //departmentId为空 表示市教委账号
                    // 日期  如2019-12-02,2019-12-03......2019-12-08  周一到周日 7 天的日期
                    List<String> searchDates = new ArrayList<>();

                    if (CommonUtil.isEmpty(startDateAssess) || CommonUtil.isEmpty(endDateAssess)) {

                        //时间周期为null  根据其他查询条件查询 全部 按时间倒序 排列
                        if (request.getParameter("area") != null) {
                            filterParamMap.put("area", request.getParameter("area"));
                        }

//                        if (request.getParameter("departmentId") != null) {
//                            filterParamMap.put("department_id", request.getParameter("departmentId"));
//                        }

                        if (request.getParameter("managementDepartment") != null) {
                            filterParamMap.put("department_id", request.getParameter("managementDepartment"));
                        }
                        if (request.getParameter("schoolName") != null) {
                            filterParamMap.put("school_name", request.getParameter("schoolName"));
                        }
                        if (request.getParameter("schoolId") != null) {
                            filterParamMap.put("school_id", request.getParameter("schoolId"));
                        }
//                        sourceDao = dbHiveAssessService.getAssessTimeIsNullAreaList(filterParamMap);

                    } else { // 按照开始日期和结束日期获取数据
                        DateTime startDt = BCDTimeUtil.convertDateStrToDate(startDateAssess);
                        DateTime endDt = BCDTimeUtil.convertDateStrToDate(endDateAssess);
                        int days = Days.daysBetween(startDt, endDt).getDays() + 1;
                        for (int i = 0; i < days; i++) {
                            searchDates.add(endDt.minusDays(i).toString("yyyy-MM-dd"));
                        }
                    }
                    if (searchDates.size() > 0) {
                        filterParamMap.put("searchDates", searchDates);
                    }

                    if (request.getParameter("area") != null) {
                        filterParamMap.put("area", request.getParameter("area"));
                    }

//                    if (request.getParameter("departmentId") != null) {
//                        filterParamMap.put("department_id", request.getParameter("departmentId"));
//                    }

                    if (request.getParameter("managementDepartment") != null) {
                        filterParamMap.put("department_id", request.getParameter("managementDepartment"));
                    }
                    if (request.getParameter("schoolName") != null) {
                        filterParamMap.put("school_name", request.getParameter("schoolName"));
                    }
                    if (request.getParameter("schoolId") != null) {
                        filterParamMap.put("school_id", request.getParameter("schoolId"));
                    }
                    sourceDao = dbHiveAssessService.getAssessList(filterParamMap);
                }

                List<AppCommonDao> resultDao = sourceDao.subList(startNum, sourceDao.size() > (startNum + pageSize) ? (startNum + pageSize) : sourceDao.size());
                // 获取列表数据
                int sortId = startNum;
                for (int i = 0; i < resultDao.size(); i++) {
                    sortId += 1;
                    LinkedHashMap<String, Object> commonMap = resultDao.get(i).getCommonMap();
                    commonMap.put("sortId", sortId);
                    dataList.add(commonMap);
                }
                LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
                data.put("curPageNum", curPageNum);
                data.put("pageTotal", sourceDao.size());
                appCommonData.setData(data);
                appCommonData.setDataList(dataList);

                appCommonExternalModulesDto.setData(appCommonData);

                // 以上业务逻辑层修改
                // 固定返回
            } else {
                appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
                appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
            }
        } catch (Exception e) {

            appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
            appCommonExternalModulesDto.setResMsg(IOTRspType.System_ERR.getMsg().toString());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String strResp = null;
        try {
            strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
            strResp = new ToolUtil().rmExternalStructure(strResp);
        } catch (Exception e) {
            strResp = new ToolUtil().getInitJson();
        }
        return strResp;
    }


    //考核评价 导出数据模型
    public AssessListDataDTO expAssessModFunc(HttpServletRequest request, DbHiveAssessService dbHiveAssessService, Db2Service db2Service) {
        // 固定Dto层
        AssessListDataDTO assessListDataDTO = new AssessListDataDTO();

        List<AssessData> resultDao = null;

        // 业务操作
        try {
            //授权码
            String token = request.getHeader("Authorization");
            //验证授权
            //boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service, new int[2]);
            boolean verTokenFlag = true;
            if (verTokenFlag) {
                // 以下业务逻辑层修改

                //分页
         /*       if (request.getParameter("page") != null && !request.getParameter("page").toString().isEmpty()) {
                    this.curPageNum = Integer.parseInt(request.getParameter("page").toString());
                }
                if (request.getParameter("pageSize") != null && !request.getParameter("pageSize").toString().isEmpty()) {
                    this.pageSize = Integer.parseInt(request.getParameter("pageSize").toString());
                }
                Integer startNum = (curPageNum - 1) * pageSize;*/
                // 筛选参数
                TEduBdUserDo userINfo = db2Service.getBdUserInfoByToken(token);
                LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();

                //可选参数

                String startDateAssess = request.getParameter("startDateAssess");
                String endDateAssess = request.getParameter("endDateAssess");

                DateTime startDt = BCDTimeUtil.convertDateStrToDate(startDateAssess).withDayOfWeek(1);
                DateTime endDt = BCDTimeUtil.convertDateStrToDate(endDateAssess);
                Period period = new Period(startDt, endDt, PeriodType.weeks());
                int weeks = period.getWeeks();
                List<String> searchDates = new ArrayList<>();
                for(int i = 0;i<=weeks;i++){
                    String sdate = startDt.plusWeeks(i).toString("yyyy-MM-dd");
                    searchDates.add(sdate);
                }

                filterParamMap.put("start_use_date", searchDates);
//                filterParamMap.put("end_use_date", request.getParameter("endDateAssess"));
                filterParamMap.put("area", request.getParameter("area"));
                filterParamMap.put("department_id", request.getParameter("managementDepartment"));
                if(request.getParameter("schoolName") != null) {
                    filterParamMap.put("school_name", request.getParameter("schoolName"));
                }
                if(request.getParameter("schoolId") != null) {
                    filterParamMap.put("school_id", request.getParameter("schoolId"));
                }

                //sourceDao = dbHiveAssessService.getAssessList(filterParamMap);
                resultDao = dbHiveAssessService.getExpAssessList(filterParamMap);
                //resultDao = sourceDao.subList(startNum, sourceDao.size() > (startNum + pageSize) ? (startNum + pageSize) : sourceDao.size());

                if (resultDao != null) {
                    assessListDataDTO.setAssessListData(resultDao);
                }

                // 获取列表数据
                // 以上业务逻辑层修改
                // 固定返回
            } else {
                assessListDataDTO.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
                assessListDataDTO.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
            }
        } catch (Exception e) {
            assessListDataDTO.setResCode(IOTRspType.System_ERR.getCode().toString());
            assessListDataDTO.setResMsg(IOTRspType.System_ERR.getMsg().toString());
        }

        return assessListDataDTO;
    }

    //考核详情
    public String queryAssessDetailsModFunc(HttpServletRequest request, DbHiveAssessService dbHiveAssessService, Db2Service db2Service) {
        // 固定Dto层
        AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
        List<AppCommonDao> sourceDao = null;
        // 业务操作
        try {
            //授权码
            String token = request.getHeader("Authorization");
            //验证授权
            boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service, new int[2]);
//            boolean verTokenFlag = true;
            if (verTokenFlag) {
                // 以下业务逻辑层修改

                // 筛选参数
                LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();

                //参数
                String startDateAssess = request.getParameter("evaluatePeriod").split("-")[0];
                DateTime startDt = BCDTimeUtil.convertDateStrToDate(startDateAssess, "yyyy/MM/dd");

                filterParamMap.put("start_use_date", startDt.toString("yyyy-MM-dd"));

//                filterParamMap.put("evaluate_period ", request.getParameter("evaluatePeriod"));
                if (request.getParameter("schoolName") != null) {
                    filterParamMap.put("school_name", request.getParameter("schoolName"));
                }
                if (request.getParameter("schoolId") != null) {
                    filterParamMap.put("school_id", request.getParameter("schoolId"));
                }

                sourceDao = dbHiveAssessService.getAssessDetailList(filterParamMap);

                appCommonExternalModulesDto.setData(sourceDao);

                // 以上业务逻辑层修改
                // 固定返回
            } else {
                appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
                appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
            appCommonExternalModulesDto.setResMsg(IOTRspType.System_ERR.getMsg().toString());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String strResp = null;
        try {
            strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
            strResp = new ToolUtil().rmExternalStructure(strResp);
        } catch (Exception e) {
            strResp = new ToolUtil().getInitJson();
        }
        return strResp;
    }
}
