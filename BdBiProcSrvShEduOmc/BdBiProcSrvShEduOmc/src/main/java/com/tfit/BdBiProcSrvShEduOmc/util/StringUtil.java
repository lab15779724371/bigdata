package com.tfit.BdBiProcSrvShEduOmc.util;

import com.tfit.BdBiProcSrvShEduOmc.constant.RegexExpressConstant;

import java.util.regex.Pattern;

/**
 * @Descritpion：字符串工具类
 * @author: tianfang_infotech
 * @date: 2019/1/21 13:04
 */
public class StringUtil {

    public static String getValue(Object object) {
        return object == null ? "" : object.toString();
    }

    public static Boolean isNull(Object object) {
        return object == null ? true : false;
    }

    public static Boolean isNull(String object) {
        return (object == null || object.length() == 0) ? true : false;
    }

    public static Boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static Boolean isNotNull(String object) {
        return !isNull(object);
    }

    /**
     * 为空或数字类型
     *
     * @param text
     * @return
     */
    public static Boolean isNullOrNumber(String text) {
        return (text == null || Pattern.matches(RegexExpressConstant.REGEX_ALL_NUMBER_FORMAT, text)) ? true : false;
    }

    public static Boolean isNumber(String text) {
        return Pattern.matches(RegexExpressConstant.REGEX_ALL_NUMBER_FORMAT, text) ? true : false;
    }
}
