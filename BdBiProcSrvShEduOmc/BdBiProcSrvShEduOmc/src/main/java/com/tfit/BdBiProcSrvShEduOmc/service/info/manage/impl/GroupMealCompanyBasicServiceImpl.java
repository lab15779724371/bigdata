package com.tfit.BdBiProcSrvShEduOmc.service.info.manage.impl;

import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.constant.RedisKeyConstant;
import com.tfit.BdBiProcSrvShEduOmc.model.ro.GroupMealCompanyBasicRO;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.info.manage.GroupMealCompanyBasicService;
import com.tfit.BdBiProcSrvShEduOmc.util.RedisValueUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Descritpion：团餐公司基础信息查询服务实现类
 * @author: tianfang_infotech
 * @date: 2019/1/8 13:51
 */
@Slf4j
@Service
public class GroupMealCompanyBasicServiceImpl implements GroupMealCompanyBasicService {

    /**
     * 分割符号
     */
    private static final String SPLIT_SYMBOL = ";";
    /**
     * 循环迭代步长
     */
    private static final int ITERATE_STEP_SIZE = 2;
    /**
     * redis 中供应商基本信息格式数据项长度
     */
    private static final int REDIS_SCHOOL_BASIC_RO_FORMAT_LENGTH = 20;

    @Autowired
    private RedisService redisService;


    private String readRedisByField(String key, String schoolId) {
        return redisService.getHashByKeyField(SpringConfig.RedisRunEnvIdx, SpringConfig.RedisDBIdx, key, schoolId);
    }

    @Override
    public GroupMealCompanyBasicRO getGroupMealCompanyBasicFromRedis(String schoolId) {
        String schoolDetail = readRedisByField(RedisKeyConstant.RMC_BASE_INFO, schoolId);
        if (StringUtils.isEmpty(schoolDetail)) {
            log.info("GroupMealCompany=> redis：key={}, field={} 不存在。", RedisKeyConstant.RMC_BASE_INFO, schoolId);
            return null;
        }
        if (schoolDetail.indexOf(SPLIT_SYMBOL) == -1) {
            log.info("GroupMealCompany=> redis：key={}, value={} 值格式错误, 不包含分隔符“{}”, 解析失败。", RedisKeyConstant.RMC_BASE_INFO, schoolDetail, SPLIT_SYMBOL);
            return null;
        }
        String[] splitArray = schoolDetail.split(SPLIT_SYMBOL);
        if (splitArray.length < REDIS_SCHOOL_BASIC_RO_FORMAT_LENGTH) {
            log.info("GroupMealCompany=> redis：key={}, value={} 值格式错误, 数据项少于{}, 解析失败。", RedisKeyConstant.RMC_BASE_INFO, schoolDetail, REDIS_SCHOOL_BASIC_RO_FORMAT_LENGTH);
            return null;
        }

        int index = -1;
        GroupMealCompanyBasicRO mealCompanyBasicRO = new GroupMealCompanyBasicRO();

        index += ITERATE_STEP_SIZE;
        /**
         * 团餐公司编号（表主键）
         */
        mealCompanyBasicRO.setId(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 团餐公司名称
         */
        mealCompanyBasicRO.setSupplierName(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 区号
         */
        mealCompanyBasicRO.setArea(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 地址
         */
        mealCompanyBasicRO.setAddress(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 联系人
         */
        mealCompanyBasicRO.setContact(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 手机号
         */
        mealCompanyBasicRO.setContactWay(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 质量负责人
         */
        mealCompanyBasicRO.setQaPerson(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 联系电话
         */
        mealCompanyBasicRO.setQaWay(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 注册资金
         */
        mealCompanyBasicRO.setRegCapital(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 法人
         */
        mealCompanyBasicRO.setCorporation(RedisValueUtil.filterString(splitArray[index]));

        return mealCompanyBasicRO;
    }
}
