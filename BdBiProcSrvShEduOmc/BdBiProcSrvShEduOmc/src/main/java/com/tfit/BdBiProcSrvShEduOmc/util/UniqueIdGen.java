package com.tfit.BdBiProcSrvShEduOmc.util;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.UUID;

//封装各种生成唯一性ID算法的工具类.
@Service
@Lazy(false)
public class UniqueIdGen implements SessionIdGenerator {
	private static SecureRandom random = new SecureRandom();

    //封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    //封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
    public static String uuidInterSeg() {
        return UUID.randomUUID().toString();
    }

    //使用SecureRandom随机生成Long.
    public static long randomLong() {
        return Math.abs(random.nextLong());
    }
    
    @Override
    public Serializable generateId(Session session) {
    	return UniqueIdGen.uuid();
    }
}
