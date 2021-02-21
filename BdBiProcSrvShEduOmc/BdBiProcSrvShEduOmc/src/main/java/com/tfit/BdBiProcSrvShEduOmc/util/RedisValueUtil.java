package com.tfit.BdBiProcSrvShEduOmc.util;

import com.tfit.BdBiProcSrvShEduOmc.constant.GlobalConstant;
import com.tfit.BdBiProcSrvShEduOmc.constant.RegexExpressConstant;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @Descritpion：字符串过滤功能
 * @author: tianfang_infotech
 * @date: 2019/1/11 10:57
 */
public class RedisValueUtil {

    private static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd";

    /**
     * redis 字符串值过滤
     *
     * @param redisValue
     * @return
     */
    public static String filterString(String redisValue) {
        if (StringUtils.isBlank(redisValue)) {
            return "";
        }

        if (GlobalConstant.REDIS_NULL_VALUE_STRING_DEFAULT.equalsIgnoreCase(redisValue)) {
            return "";
        }

        return redisValue;
    }


    /**
     * redis Integer值过滤方法
     *
     * @param redisValue
     * @return
     */
    public static String redisIntegerFilter(String redisValue) {
        if (StringUtils.isBlank(filterString(redisValue))) {
            return null;
        }

        if (GlobalConstant.REDIS_NULL_VALUE_STRING_DEFAULT.equalsIgnoreCase(redisValue)) {
            return null;
        }

        return redisValue;
    }

    /**
     * 转字符串
     *
     * @param value
     * @return
     */
    public static Integer toInteger(String value) {

        String val = redisIntegerFilter(value);

        if (StringUtils.isEmpty(val) || !Pattern.matches(RegexExpressConstant.REGEX_ALL_NUMBER_FORMAT, val)) {
            return 0;
        }

        return Integer.valueOf(val);
    }

    /**
     * 转字符串
     *
     * @param object
     * @return
     */
    public static Integer toInteger(Object object) {

        if (Objects.isNull(object)) {
            return null;
        }
        String val = redisIntegerFilter(object.toString());

        if (StringUtils.isEmpty(val) || !Pattern.matches(RegexExpressConstant.REGEX_ALL_NUMBER_FORMAT, val)) {
            return 0;
        }

        return Integer.valueOf(val);
    }

    /**
     * 转Date
     *
     * @param value
     * @return
     */
    public static Date toDate(String value) {

        String val = filterString(value);

        try {
            return StringUtils.isBlank(val) ? null : new SimpleDateFormat(DATE_FORMAT_DEFAULT).parse(val);
        } catch (ParseException e) {
            return null;
        }
    }
}
