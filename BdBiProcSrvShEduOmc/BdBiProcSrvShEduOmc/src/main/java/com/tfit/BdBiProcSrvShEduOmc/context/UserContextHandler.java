package com.tfit.BdBiProcSrvShEduOmc.context;

import com.tfit.BdBiProcSrvShEduOmc.constant.GlobalConstant;
import com.tfit.BdBiProcSrvShEduOmc.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Descritpion：用户信息处理器
 * @author: tianfang_infotech
 * @date: 2019/1/21 12:03
 */
public class UserContextHandler {
    /**
     * 线程间数据共享
     */
    public static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();

    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }
        map.put(key, value);
    }

    public static Object get(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }
        return map.get(key);
    }

    public static Integer getUserId() {

        String value = StringUtil.getValue(get(GlobalConstant.CONTEXT_KEY_USER_ID));

        if(StringUtils.isNotBlank(value)) {
            return Integer.valueOf(value);
        }

        return null;
    }

    public static String getUsername() {

        Object value = get(GlobalConstant.CONTEXT_KEY_USER_NAME);

        return StringUtil.getValue(value);
    }

    /**
     * 获取区/县码
     * @return
     */
    public static String getDistrictCode() {

        Object value = get(GlobalConstant.CONTEXT_KEY_USER_DISTRICT_CODE);

        return StringUtil.getValue(value);
    }

    public static String getName() {

        Object value = get(GlobalConstant.CONTEXT_KEY_USER_NAME);

        return StringUtil.getValue(value);
    }

    public static String getToken() {

        Object value = get(GlobalConstant.CONTEXT_KEY_USER_TOKEN);

        return StringUtil.getValue(value);
    }

    public static void setToken(String token) {
        set(GlobalConstant.CONTEXT_KEY_USER_TOKEN, token);
    }

    public static void setName(String name) {
        set(GlobalConstant.CONTEXT_KEY_USER_NAME, name);
    }

    public static void setUserId(String userId) {
        set(GlobalConstant.CONTEXT_KEY_USER_ID, userId);
    }

    public static void setUsername(String username) {
        set(GlobalConstant.CONTEXT_KEY_USER_NAME, username);
    }

    public static void setDistrictCode(String districtCode) {
        set(GlobalConstant.CONTEXT_KEY_USER_DISTRICT_CODE, districtCode);
    }

    public static void remove() {
        threadLocal.remove();
    }
}
