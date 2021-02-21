package com.tfit.BdBiProcSrvShEduOmc.constant;

/**
 * @Descritpion：全局常量信息
 * @author: tianfang_infotech
 * @date: 2019/1/8 17:20
 */
public interface GlobalConstant {

    /**
     * 空字符串默认显示
     */
    String STRING_EMPTY_DISPLAY_DEFAULT = "-";

    /**
     * redis空值字符默认值
     */
    String REDIS_NULL_VALUE_STRING_DEFAULT = "null";

    /**
     * 路径分隔符
     */
    String PATH_SPLIT_SYMBOL = "/";

    /**
     * 当前用户编号
     */
    String CONTEXT_KEY_USER_ID = "currentUserId";

    /**
     * 当前用户名
     */
    String CONTEXT_KEY_USER_NAME = "currentUserName";

    /**
     * 当前用户token
     */
    String CONTEXT_KEY_USER_TOKEN = "currentUserToken";

    /**
     * 当前用户区/县信息码
     */
    String CONTEXT_KEY_USER_DISTRICT_CODE = "currentUserDistrictCode";

    /**
     * 当前用户是否为管理员
     */
    String CONTEXT_KEY_USER_ISADMIN = "currentUserIsAdmin";

}
