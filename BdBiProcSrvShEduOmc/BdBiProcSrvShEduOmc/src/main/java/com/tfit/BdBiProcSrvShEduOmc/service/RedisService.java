package com.tfit.BdBiProcSrvShEduOmc.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.tfit.BdBiProcSrvShEduOmc.client.RedisRWClient;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;

//redis服务
@Service
public class RedisService {
	
    //以键值和域名获取hash表域值
    public String getHashByKeyField(int jedisIdx, int dbIndex, String key, String field) {
    	RedisRWClient jedisUtil = RedisRWClient.getInstance();
    	if(!existHashKey(jedisIdx, dbIndex, key)) {
    		return AppModConfig.getHdfsDataKeyField(key.split("_")[0], key, field);
    	}else {
    		return jedisUtil.getHashByKeyField(jedisIdx, dbIndex, key, field);
    	}
    }
    
    //以键值获取hash表所有映射值
    public Map<String, String> getHashByKey(int jedisIdx, int dbIndex, String key) {
    	Map<String, String> resultMap = null;
    	RedisRWClient jedisUtil = RedisRWClient.getInstance();
    	if(!existHashKey(jedisIdx, dbIndex, key)) {
    		resultMap =  AppModConfig.getHdfsDataKey(key.split("_")[0],key);
    	}else {
    		resultMap = jedisUtil.getHashByKey(jedisIdx, dbIndex, key);
    	}
    	return resultMap;
    }
    
    //判断hash表键值、域是否存在
    public boolean existHashKeyFied(int jedisIdx, int dbIndex, String key, String field) {
    	RedisRWClient jedisUtil = RedisRWClient.getInstance();
    	return jedisUtil.existHashKeyFied(jedisIdx, dbIndex, key, field);
    }
    
    //判断hash表键值是否存在
    public boolean existHashKey(int jedisIdx, int dbIndex, String key) {
    	RedisRWClient jedisUtil = RedisRWClient.getInstance();
    	return jedisUtil.existHashKey(jedisIdx, dbIndex, key);
    }
    
    //删除键key
    public boolean delHaskKey(int jedisIdx, int dbIndex, String key) {
    	RedisRWClient jedisUtil = RedisRWClient.getInstance();
    	return jedisUtil.delHaskKey(jedisIdx, dbIndex, key);
    }
    
    //删除键key及field
    public boolean delHaskKey(int jedisIdx, int dbIndex, String key, String field) {
    	RedisRWClient jedisUtil = RedisRWClient.getInstance();
    	return jedisUtil.delHaskKey(jedisIdx, dbIndex, key, field);
    }
    
    //设置key及域值
    public boolean setHashKeyFieldVal(int jedisIdx, int dbIndex, String key, String field, String value) {
    	RedisRWClient jedisUtil = RedisRWClient.getInstance();
    	return jedisUtil.setHashKeyFieldVal(jedisIdx, dbIndex, key, field, value);
    }
}
