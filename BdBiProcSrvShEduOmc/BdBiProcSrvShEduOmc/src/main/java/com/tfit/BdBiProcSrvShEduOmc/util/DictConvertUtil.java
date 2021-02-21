package com.tfit.BdBiProcSrvShEduOmc.util;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @Descritpion：数据字典Key/value转换工具类(Key,value互转)
 * @author: tianfang_infotech
 * @date: 2019/1/8 16:31
 */
public class DictConvertUtil {

    /**
     * AppModConfig.schTypeIdToNameMap.get(curSchType)
     * <p>
     * AppModConfig.subLevelIdToNameMap.get(curSubLevel)
     * <p>
     * AppModConfig.fblMbIdToNameMap.get(curFblMb)
     * <p>
     * //判断学校类型（学制）（判断索引2）
     * AppModConfig.schTypeNameToIdMap.get(bsl.getSchType())
     * <p>
     * //判断学校性质（判断索引3）
     * AppModConfig.schPropNameToIdMap.get(bsl.getSchProp())
     * <p>
     * //判断经营模式（供餐模式）（判断索引5）
     * AppModConfig.optModeNameToIdMap.get(bsl.getOptMode())
     * <p>
     * //判断总校/分校（判断索引7）
     * AppModConfig.genBraSchNameToIdMap.get(bsl.getSchGenBraFlag())
     * <p>
     * //判断所属（判断索引9）
     * AppModConfig.subLevelNameToIdMap.get(bsl.getSubLevel())
     */

    public static final Map<String, Integer> wasteOilNameToIdMap = new HashMap<String, Integer>() {{
        put("-", 0);
        put("废油", 1);
        put("含油废水", 2);
    }};
    @SuppressWarnings("serial")
    public static final Map<Integer, String> wasteOilIdToNameMap = new HashMap<Integer, String>() {{
        put(0, "-");
        put(1, "废油");
        put(2, "含油废水");
    }};

    /**
     * 区域code与机构名称关系映射表
     */
    public static final Map<String, String> orgCodeToNameMap = new HashMap<String, String>() {{
        put("1", "黄浦区教育局");
        put("2", "嘉定区教育局");
        put("3", "宝山区教育局");
        put("4", "浦东新区教育局");
        put("5", "松江区教育局");
        put("6", "金山区教育局");
        put("7", "青浦区教育局");
        put("8", "奉贤区教育局");
        put("9", "崇明区教育局");
        put("10", "静安区教育局");
        put("11", "徐汇区教育局");
        put("12", "长宁区教育局");
        put("13", "普陀区教育局");
        put("14", "虹口区教育局");
        put("15", "杨浦区教育局");
        put("16", "闵行区教育局");
    }};

    /**
     * 机构名称与区域code关系映射表
     */
    public static final Map<String, String> orgNameToCodeMap = new HashMap<String, String>() {{
        put("黄浦区教育局", "1");
        put("嘉定区教育局", "2");
        put("宝山区教育局", "3");
        put("浦东新区教育局", "4");
        put("松江区教育局", "5");
        put("金山区教育局", "6");
        put("青浦区教育局", "7");
        put("奉贤区教育局", "8");
        put("崇明区教育局", "9");
        put("静安区教育局", "10");
        put("徐汇区教育局", "11");
        put("长宁区教育局", "12");
        put("普陀区教育局", "13");
        put("虹口区教育局", "14");
        put("杨浦区教育局", "15");
        put("闵行区教育局", "16");
    }};


    /**
     * 区县编号转名称
     *
     * @param distId
     * @return
     */
    public static String mapToDistName(String distId) {
        return AppModConfig.distIdToNameMap.get(distId);
    }

    /**
     * 区县名称转编号
     *
     * @param distName
     * @return
     */
    public static String mapToDistId(String distName) {
        return AppModConfig.distNameToIdMap.get(distName);
    }


    /**
     * 学校类型编号转名称
     *
     * @param schTypeId
     * @return
     */
    public static String mapToSchTypeName(Integer schTypeId) {
        return AppModConfig.schTypeIdToNameMap.get(schTypeId);
    }

    /**
     * 学校类型名称转编号
     *
     * @param schTypeName
     * @return
     */
    public static Integer mapToSchTypeId(String schTypeName) {
        return AppModConfig.schTypeNameToIdMap.get(schTypeName);
    }

    /**
     * 子等级编号转名称
     *
     * @param subLevelId
     * @return
     */
    public static String mapToSubLevelName(Integer subLevelId) {
        return AppModConfig.subLevelIdToNameMap.get(subLevelId);
    }

    /**
     * 子等级名称转编号
     *
     * @param schTypeName
     * @return
     */
    public static Integer mapToSubLevelId(String schTypeName) {
        return AppModConfig.subLevelNameToIdMap.get(schTypeName);
    }

    /**
     * 学校性质编号转名称
     *
     * @param schPropId
     * @return
     */
    public static String mapToSchPropName(Integer schPropId) {
        return AppModConfig.schPropIdToNameMap.get(schPropId);
    }

    /**
     * 学校性质名称转编号
     *
     * @param schPropName
     * @return
     */
    public static Integer mapToSchPropId(String schPropName) {
        return AppModConfig.schPropNameToIdMap.get(schPropName);
    }

    /**
     * 学校品牌性质编号转名称
     *
     * @param genBraSchId
     * @return
     */
    public static String mapToGenBraSchName(Integer genBraSchId) {
        return AppModConfig.genBraSchIdToNameMap.get(genBraSchId);
    }

    /**
     * 学校品牌性质名称转编号
     *
     * @param genBraSchName
     * @return
     */
    public static Integer mapToGenBraSchId(String genBraSchName) {
        return AppModConfig.genBraSchNameToIdMap.get(genBraSchName);
    }

    /**
     * 废弃油脂类型编号转名称
     *
     * @param wasteOilTypeId
     * @return
     */
    public static String mapToWasteOilTypeName(Integer wasteOilTypeId) {
        return wasteOilIdToNameMap.get(wasteOilTypeId);
    }

    /**
     * 废弃油脂类型名称转编号
     *
     * @param wasteOilTypeName
     * @return
     */
    public static Integer mapToWasteOilTypeId(String wasteOilTypeName) {
        return wasteOilNameToIdMap.get(wasteOilTypeName);
    }

    /**
     * 学校机构名称转机构编码
     *
     * @param orgName
     * @return
     */
    public static String mapToOrgCode(String orgName) {
        return orgNameToCodeMap.get(orgName);
    }

    /**
     * 学校机构编码转机构名称
     *
     * @param orgCode
     * @return
     */
    public static String mapToOrgName(String orgCode) {
        return orgCodeToNameMap.get(orgCode);
    }
}
