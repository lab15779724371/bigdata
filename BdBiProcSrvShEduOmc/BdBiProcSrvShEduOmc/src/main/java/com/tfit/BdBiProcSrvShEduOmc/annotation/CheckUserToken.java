package com.tfit.BdBiProcSrvShEduOmc.annotation;

import java.lang.annotation.*;

/**
 * @Descritpion：验证用户token(验证身份信息)
 * @author: tianfang_infotech
 * @date: 2019/1/21 11:44
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckUserToken {

    String value() default "";
}
