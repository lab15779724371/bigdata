package com.tfit.BdBiProcSrvShEduOmc.service.info.manage.impl;

import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.constant.RedisKeyConstant;
import com.tfit.BdBiProcSrvShEduOmc.model.ro.SchoolBasicRO;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.info.manage.SchoolBasicService;
import com.tfit.BdBiProcSrvShEduOmc.util.RedisValueUtil;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Descritpion：学校基础信息查询服务实现类
 * @author: tianfang_infotech
 * @date: 2019/1/8 13:51
 */
@Slf4j
@Service
public class SchoolBaseServiceImpl implements SchoolBasicService {
	private static final Logger logger = LogManager.getLogger(RecoveryWasteOilsServiceImpl.class.getName());

    /**
     * 分割符号
     */
    private static final String SPLIT_SYMBOL = ";";
    /**
     * 循环迭代步长
     */
    private static final int ITERATE_STEP_SIZE = 2;
    /**
     * redis 中学校基本信息格式数据项长度
     */
    private static final int REDIS_SCHOOL_BASIC_RO_FORMAT_LENGTH = 84;

    @Autowired
    private RedisService redisService;

    @Override
    public SchoolBasicRO getSchoolBasicFromRedis(String schoolId) {

        String schoolDetail = readRedisByField(RedisKeyConstant.SCHOOL_BASE_INFO, schoolId);
        if (StringUtils.isEmpty(schoolDetail)) {
            logger.info("School=> redis：key={}, field={} 不存在。", RedisKeyConstant.SCHOOL_BASE_INFO, schoolId);
            return null;
        }
        if (schoolDetail.indexOf(SPLIT_SYMBOL) == -1) {
            logger.info("School=> redis：key={}, value={} 值格式错误, 不包含分隔符“{}”, 解析失败。", RedisKeyConstant.SCHOOL_BASE_INFO, schoolDetail, SPLIT_SYMBOL);
            return null;
        }
        String[] splitArray = schoolDetail.split(SPLIT_SYMBOL);
        if (splitArray.length < REDIS_SCHOOL_BASIC_RO_FORMAT_LENGTH) {
            logger.info("School=> redis：key={}, value={} 值格式错误, 数据项少于{}, 解析失败。", RedisKeyConstant.SCHOOL_BASE_INFO, schoolDetail, REDIS_SCHOOL_BASIC_RO_FORMAT_LENGTH);
            return null;
        }

        int index = -1;
        SchoolBasicRO schoolBasicRO = new SchoolBasicRO();

        index += ITERATE_STEP_SIZE;
        /**
         * 学校编号（表主键）
         */
        schoolBasicRO.setId(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 学校名称
         */
        schoolBasicRO.setSchoolName(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 是否分校(0：总校; 1：分校)
         */
        schoolBasicRO.setIsBranchSchool(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 关联总校Id
         */
        schoolBasicRO.setParentId(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 区号
         */
        schoolBasicRO.setArea(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 地址
         */
        schoolBasicRO.setAddress(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 统一社会信用代码
         */
        schoolBasicRO.setSocialCreditCode(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 学校学制
         */
        schoolBasicRO.setLevel(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 学校性质
         */
        schoolBasicRO.setSchoolNature(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 学校性质小类
         */
        schoolBasicRO.setNatureSub(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 所属部门(3:区级 2:市级 1:部级 0 其它;)
         */
        schoolBasicRO.setDepartmentMasterId(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 主管部门
         */
        schoolBasicRO.setDepartmentSlaveId(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 食堂经营模式
         */
        schoolBasicRO.setCanteenMode(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 配送类型
         */
        schoolBasicRO.setLedgerType(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 学生数量
         */
        schoolBasicRO.setStudentsAmount(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 教职工人数
         */
        schoolBasicRO.setStaffAmount(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 法定代表人
         */
        schoolBasicRO.setCorporation(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 电话
         */
        schoolBasicRO.setCorporationWay(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 座机
         */
        schoolBasicRO.setCorporationTelephone(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 部门负责人
         */
        schoolBasicRO.setDepartmentHead(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 手机
         */
        schoolBasicRO.setDepartmentMobilePhone(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 座机
         */
        schoolBasicRO.setDepartmentTelephone(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *传真
         */
        schoolBasicRO.setDepartmentFax(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *电子邮件
         */
        schoolBasicRO.setDepartmentEmail(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *项目联系人
         */
        schoolBasicRO.setFoodSafetyPerson(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 手机
         */
        schoolBasicRO.setFoodSafetyMobilePhone(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *座机
         */
        schoolBasicRO.setFoodSafetyTelephone(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 供餐(0:不供餐;1:供餐)
         */
        schoolBasicRO.setGongCan(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *证件类型 1 食品经营许可证
         */
        schoolBasicRO.setSlicType(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *图片
         */
        schoolBasicRO.setSlicPic(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *经营单位
         */
        schoolBasicRO.setSlicJob(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *许可证号
         */
        schoolBasicRO.setSlicNo(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *发证机关
         */
        schoolBasicRO.setSoperation(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *发证时间
         */
        schoolBasicRO.setSlicDate(RedisValueUtil.toDate(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *有效日期
         */
        schoolBasicRO.setSendDate(RedisValueUtil.toDate(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 0 餐饮服务许可证
         */
        schoolBasicRO.setClicType(RedisValueUtil.toInteger(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 图片
         */
        schoolBasicRO.setClicPic(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *经营单位
         */
        schoolBasicRO.setClicJob(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 许可证号
         */
        schoolBasicRO.setClicNo(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         *发证机关
         */
        schoolBasicRO.setCoperation(RedisValueUtil.filterString(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 发证时间
         */
        schoolBasicRO.setClicDate(RedisValueUtil.toDate(splitArray[index]));

        index += ITERATE_STEP_SIZE;
        /**
         * 有效日期
         */
        schoolBasicRO.setCendDate(RedisValueUtil.toDate(splitArray[index]));

        return schoolBasicRO;
    }

    private String readRedisByField(String key, String schoolId) {
        return redisService.getHashByKeyField(SpringConfig.RedisRunEnvIdx, SpringConfig.RedisDBIdx, key, schoolId);
    }
}
