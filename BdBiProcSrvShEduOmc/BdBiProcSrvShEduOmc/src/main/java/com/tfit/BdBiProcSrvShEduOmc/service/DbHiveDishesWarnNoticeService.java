package com.tfit.BdBiProcSrvShEduOmc.service;

import com.tfit.BdBiProcSrvShEduOmc.dao.AppCommonDao;

import java.util.LinkedHashMap;
import java.util.List;

public interface DbHiveDishesWarnNoticeService {
    // 未排菜预警通知 市教委 departmentId == null
    List<LinkedHashMap<String, Object>> getDishWarnNoticeList(LinkedHashMap<String, Object>  filterParamMap);
    // 未排菜预警通知 区 departmentId 不为空
    List<LinkedHashMap<String, Object>> getAreaDishWarnNoticeList(LinkedHashMap<String, Object>  filterParamMap);

    // 未验收预警通知 市教委 departmentId == null
    List<LinkedHashMap<String, Object>> getCheckWarnNoticeList(LinkedHashMap<String, Object>  filterParamMap);
    // 未验收预警通知 市教委 departmentId == null
    List<LinkedHashMap<String, Object>> getAreaCheckWarnNoticeList(LinkedHashMap<String, Object>  filterParamMap);
}
