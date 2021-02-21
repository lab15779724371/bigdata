package com.tfit.BdBiProcSrvShEduOmc.appmod.dwn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfit.BdBiProcSrvShEduOmc.appmod.assess.AssessListMod;


import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduBdUserDo;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.apiDto.AppCommonExternalModulesDto;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveDishesWarnNoticeService;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.ToolUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Description: 未排菜和未验收预警通知模型
 * @Param: $
 * @returns: $
 * @Author: weihai_zhao
 * @Date: 2020-01-15
 */
public class DishesWarnNoticeMod {
    private static final Logger logger = LogManager.getLogger(AssessListMod.class.getName());
    // 是否为真实数据标识
    private static boolean isRealData = true;


    //未排菜预警
    public String queryDishesWarnNoticeFunc(HttpServletRequest request, DbHiveDishesWarnNoticeService dbHiveDishesWarnNoticeService, Db2Service db2Service, Db1Service db1Service) {
        // 固定Dto层
        AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();
        //AppCommonData appCommonData = new AppCommonData();

        // 业务操作
        try {
            //授权码
            String token = request.getHeader("Authorization");
            //验证授权
            boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service, new int[2]);
            //boolean verTokenFlag = true;


            if (verTokenFlag) {
                // 以下业务逻辑层修改
                // 筛选参数
                //TEduBdUserDo userINfo = db2Service.getBdUserInfoByToken(token);
                List<LinkedHashMap<String, Object>> resultDao = new ArrayList<>();
                String departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);//获取用户权限区域ID
                //如果departmentId不为空 区教育局 根据departmentId查询 本区的数据
                if (!CommonUtil.isEmpty(departmentId)) {
                    LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();
                    //可选参数
                    filterParamMap.put("warnDate", request.getParameter("warnDate"));
                    filterParamMap.put("warnLevelName", request.getParameter("warnLevelName"));
                    filterParamMap.put("departmentId", departmentId);
                    //if (filterParamMap.get("warnDate") !=null && filterParamMap.get("warnLevelName") !=null) {
                    //}
                    resultDao = dbHiveDishesWarnNoticeService.getAreaDishWarnNoticeList(filterParamMap);
                } else {
                    //departmentId == null  表示市教委查询全部
                    LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();
                    //可选参数
                    filterParamMap.put("warnDate", request.getParameter("warnDate"));
                    filterParamMap.put("warnLevelName", request.getParameter("warnLevelName"));
                    //if (filterParamMap.get("warnDate") !=null && filterParamMap.get("warnLevelName") !=null) {
                    // }
                    resultDao = dbHiveDishesWarnNoticeService.getDishWarnNoticeList(filterParamMap);

                    //不为空的情况下
                    if (!resultDao.isEmpty()) {
                        //把查询出来的数据 value 放到集合中
                        List<String> resultDepartment = new ArrayList<>();
                        int maxNum = 0;
                        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : resultDao) {
                            for (Map.Entry<String, Object> entryData : stringObjectLinkedHashMap.entrySet()) {
                                String dataKey = entryData.getKey();
                                String dataValue = (String) entryData.getValue();
                                if (dataValue != null && !dataValue.toString().trim().isEmpty()) {
                                    if ("departmentId".equals(dataKey)) {
                                        resultDepartment.add(dataValue);
                                    }
                                    if ("rn".equals(dataKey)) {
                                        maxNum = Integer.parseInt(dataValue)+1;
                                    }
                                }
                            }
                        }

                        //LinkedHashMap<String, Object> originDepartment = new LinkedHashMap<String, Object>();
                        //包含所有key的集合
                        List<String> originDepartment = new ArrayList<>();

                        originDepartment.add("0");
                        originDepartment.add("1");
                        originDepartment.add("2");
                        originDepartment.add("3");
                        originDepartment.add("4");
                        originDepartment.add("5");
                        originDepartment.add("6");
                        originDepartment.add("7");
                        originDepartment.add("8");
                        originDepartment.add("9");
                        originDepartment.add("10");
                        originDepartment.add("11");
                        originDepartment.add("12");
                        originDepartment.add("13");
                        originDepartment.add("14");
                        originDepartment.add("15");
                        originDepartment.add("16");
                        originDepartment.add("20");
                        originDepartment.add("21");

                        //没有的区 排名 sort
                        String sort = String.valueOf(maxNum);
                        //判断 去除不在originDepartment集合里的key ，并赋值
                        List<LinkedHashMap<String, Object>> resultDao2 = new ArrayList<>();
                        for (String isResultContain : originDepartment) {
                            if (resultDepartment.contains(isResultContain)) {
                                continue;
                            } else {
                                LinkedHashMap<String, Object> addNotKey = new LinkedHashMap<String, Object>();
                                int i = resultDepartment.size() + 1;
                                //添加查询出来的没有的departmentId=isResultContain   noPlatoonTotal 并赋值 0
                                addNotKey.put("departmentId", isResultContain);
                                addNotKey.put("noPlatoonTotal", "0");
                                addNotKey.put("rn",sort);
                                // resultDao.add()
                                resultDao2.add(addNotKey);
                            }
                        }


                        resultDao.addAll(resultDao2);
                    }
                }
                appCommonExternalModulesDto.setData(resultDao);

                // 以上业务逻辑层修改
                // 固定返回
            } else {
                appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
                appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
            }
        } catch (Exception e) {
            appCommonExternalModulesDto.setResCode(IOTRspType.System_ERR.getCode().toString());
            appCommonExternalModulesDto.setResMsg(IOTRspType.System_ERR.getMsg().toString());
            logger.error( e.getMessage());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String strResp = null;
        try {
            strResp = objectMapper.writeValueAsString(appCommonExternalModulesDto);
            strResp = new ToolUtil().rmExternalStructure(strResp);
        } catch (Exception e) {
            strResp = new ToolUtil().getInitJson();
            logger.error( e.getMessage());
        }
        return strResp;
    }

    //未验收预警
    public String queryCheckWarnNoticeFunc(HttpServletRequest request, DbHiveDishesWarnNoticeService dbHiveDishesWarnNoticeService, Db2Service db2Service, Db1Service db1Service) {
        // 固定Dto层
        AppCommonExternalModulesDto appCommonExternalModulesDto = new AppCommonExternalModulesDto();

        // 业务操作
        try {
            //授权码
            String token = request.getHeader("Authorization");
            //验证授权
            boolean verTokenFlag = AppModConfig.verifyAuthCode2(token, db2Service, new int[2]);

            //boolean verTokenFlag = true;
            if (verTokenFlag) {
                // 筛选参数
                //TEduBdUserDo userINfo = db2Service.getBdUserInfoByToken(token);

                List<LinkedHashMap<String, Object>> resultDao = new ArrayList<>();
                String departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);//获取用户权限区域ID
                //如果departmentId不为空 区教育局 根据departmentId查询 本区的数据
                if (!CommonUtil.isEmpty(departmentId)) {
                    //区 根据departmentId，warnDate，warnLevelName 查询
                    LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();
                    //可选参数
                    filterParamMap.put("warnDate", request.getParameter("warnDate"));
                    filterParamMap.put("warnLevelName", request.getParameter("warnLevelName"));
                    filterParamMap.put("departmentId", departmentId);
                    // if (filterParamMap.get("warnDate") !=null && filterParamMap.get("warnLevelName") !=null) {
                    resultDao = dbHiveDishesWarnNoticeService.getAreaCheckWarnNoticeList(filterParamMap);
                    // }
                } else {

                    //departmentId=null 的情况 市教委账号 查全部
                    LinkedHashMap<String, Object> filterParamMap = new LinkedHashMap<String, Object>();
                    //可选参数
                    filterParamMap.put("warnDate", request.getParameter("warnDate"));
                    filterParamMap.put("warnLevelName", request.getParameter("warnLevelName"));
                    //if (filterParamMap.get("warnDate") !=null && filterParamMap.get("warnLevelName") !=null) {
                    resultDao = dbHiveDishesWarnNoticeService.getCheckWarnNoticeList(filterParamMap);
                    // }

                    //不为空的情况下
                    if (!resultDao.isEmpty()) {
                        //把查询出来的数据 value 放到集合中
                        List<String> resultDepartment = new ArrayList<>();
                        int maxNum = 0;
                        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : resultDao) {
                            for (Map.Entry<String, Object> entryData : stringObjectLinkedHashMap.entrySet()) {
                                String dataKey = entryData.getKey();
                                String dataValue = (String) entryData.getValue();
                                if (dataValue != null && !dataValue.toString().trim().isEmpty()) {
                                    if ("departmentId".equals(dataKey)) {
                                        resultDepartment.add(dataValue);
                                    }
                                    if ("rn".equals(dataKey)) {
                                        maxNum = Integer.parseInt(dataValue)+1;
                                    }
                                }
                            }
                        }

                        //LinkedHashMap<String, Object> originDepartment = new LinkedHashMap<String, Object>();
                        //包含所有key的集合
                        List<String> originDepartment = new ArrayList<>();

                        originDepartment.add("0");
                        originDepartment.add("1");
                        originDepartment.add("2");
                        originDepartment.add("3");
                        originDepartment.add("4");
                        originDepartment.add("5");
                        originDepartment.add("6");
                        originDepartment.add("7");
                        originDepartment.add("8");
                        originDepartment.add("9");
                        originDepartment.add("10");
                        originDepartment.add("11");
                        originDepartment.add("12");
                        originDepartment.add("13");
                        originDepartment.add("14");
                        originDepartment.add("15");
                        originDepartment.add("16");
                        originDepartment.add("20");
                        originDepartment.add("21");

                        //判断 去除不在originDepartment集合里的key ，并赋值
                        List<LinkedHashMap<String, Object>> resultDao2 = new ArrayList<>();

                        //没有的区 排名 sort
                        String sort = String.valueOf(maxNum);

                        for (String isResultContain : originDepartment) {
                            if (resultDepartment.contains(isResultContain)) {
                                continue;
                            } else {
                                LinkedHashMap<String, Object> addNotKey = new LinkedHashMap<String, Object>();
                                //添加查询出来的 没有的noPlatoonTotal 并赋值 0
                                addNotKey.put("departmentId", isResultContain);
                                addNotKey.put("noLedgerTotal", "0");
                                addNotKey.put("rn", sort);
                                /// resultDao.add()
                                resultDao2.add(addNotKey);
                            }
                        }
                        resultDao.addAll(resultDao2);
                    }
                }


                appCommonExternalModulesDto.setData(resultDao);

                // 以上业务逻辑层修改
                // 固定返回
            } else {
                appCommonExternalModulesDto.setResCode(IOTRspType.AUTHCODE_CHKERR.getCode().toString());
                appCommonExternalModulesDto.setResMsg(IOTRspType.AUTHCODE_CHKERR.getMsg().toString());
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
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
