package com.tfit.BdBiProcSrvShEduOmc.annotation;

import java.lang.annotation.*;

/**
 * @Descritpion：访问注解-记录方法入参出参(适用于公共方法)
 * @author: tianfang_infotech
 * @date: 2019/1/3 17:00
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLog {

    String value() default "";
}
