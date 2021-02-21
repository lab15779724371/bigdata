package com.tfit.BdBiProcSrvShEduOmc.client;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//Redis读写客户端
public class RedisRWClient {
	private static final Logger logger = LogManager.getLogger(RedisRWClient.class.getName());
	//Redis配置
    private static JedisPoolConfig config = null;
	//Redis连接池个数
	private static final int JedisPoolCount = 9;
	//Redis连接池实例数组
    private static JedisPool[] jedisPools = null;
    // Redis集群的节点集合
    private static Set<HostAndPort> jedisClusterNodes0 = null, jedisClusterNodes1 = null;
    //Redis集群连接池个数
	private static final int JedisClusterCount = 2;
	//Redis集群密码
	private static String[] redisClusterPass = new String[JedisClusterCount];
    //Redis集群连接池实例
    private static JedisCluster[] jedisCluster = null;
    //Redis实例
    private static RedisRWClient jedisUtil = null;

    //初始化
    public RedisRWClient() {
    	init();
    }
    
    //初始化函数
    private void init() {
    	//Redis通用配置
        String maxIdle = SpringConfig.redis_maxIdle;
        String maxTotal = SpringConfig.redis_maxTotal;
        String maxWaitMillis = SpringConfig.redis_maxWaitMillis;
        String testOnBorrow = SpringConfig.redis_testOnBorrow;
        logger.info("maxIdle = " + maxIdle + ", maxTotal = " + maxTotal + ", maxWaitMillis = " + maxWaitMillis + ", testOnBorrow = " + testOnBorrow);
        //Redis配置0
        String host0 = SpringConfig.redis0_host;
        String port0 = SpringConfig.redis0_port;
        String pass0 = SpringConfig.redis0_pass;
        String timeout0 = SpringConfig.redis0_timeout;
		//Redis配置1
        String host1 = SpringConfig.redis1_host;
        String port1 = SpringConfig.redis1_port;
        String pass1 = SpringConfig.redis1_pass;
        String timeout1 = SpringConfig.redis1_timeout;
        //Redis配置2
        String host2 = SpringConfig.redis2_host;
        String port2 = SpringConfig.redis2_port;
        String pass2 = SpringConfig.redis2_pass;
        String timeout2 = SpringConfig.redis2_timeout;
        //Redis配置3
        String host3 = SpringConfig.redis3_host;
        String port3 = SpringConfig.redis3_port;
        String pass3 = SpringConfig.redis3_pass;
        String timeout3 = SpringConfig.redis3_timeout;
        //Redis配置4
        String host4 = SpringConfig.redis4_host;
        String port4 = SpringConfig.redis4_port;
        String pass4 = SpringConfig.redis4_pass;
        String timeout4 = SpringConfig.redis4_timeout;
        //Redis配置5
        String host5 = SpringConfig.redis5_host;
        String port5 = SpringConfig.redis5_port;
        String pass5 = SpringConfig.redis5_pass;
        String timeout5 = SpringConfig.redis5_timeout;
        //Redis配置6
        String host6 = SpringConfig.redis6_host;
        String port6 = SpringConfig.redis6_port;
        String pass6 = SpringConfig.redis6_pass;
        String timeout6 = SpringConfig.redis6_timeout;
        //Redis配置7
        String host7 = SpringConfig.redis7_host;
        String port7 = SpringConfig.redis7_port;
        String pass7 = SpringConfig.redis7_pass;
        String timeout7 = SpringConfig.redis7_timeout;
        //Redis配置8
        String host8 = SpringConfig.redis8_host;
        String port8 = SpringConfig.redis8_port;
        String pass8 = SpringConfig.redis8_pass;
        String timeout8 = SpringConfig.redis8_timeout;
        
        //Redis集群配置0
        String[] clusterhost0 = null;        //IP地址
        if(SpringConfig.rediscluster0_host != null) {
        	clusterhost0 = SpringConfig.rediscluster0_host.split(",");
        }
        String[] clusterport0 = null;        //端口
        if(SpringConfig.rediscluster0_port != null) {
        	clusterport0 = SpringConfig.rediscluster0_port.split(",");
        }
        String[] clusterpass0 = null;        //密码
        if(SpringConfig.rediscluster0_pass != null) {
        	clusterpass0 = SpringConfig.rediscluster0_pass.split(",");
        	redisClusterPass[0] = clusterpass0[0];
        }
        
        //Redis集群配置1
        String[] clusterhost1 = null;        //IP地址
        if(SpringConfig.rediscluster1_host != null) {
        	clusterhost1 = SpringConfig.rediscluster1_host.split(",");
        }
        String[] clusterport1 = null;        //端口
        if(SpringConfig.rediscluster1_port != null) {
        	clusterport1 = SpringConfig.rediscluster1_port.split(",");
        }
        String[] clusterpass1 = null;        //密码
        if(SpringConfig.rediscluster1_pass != null) {
        	clusterpass1 = SpringConfig.rediscluster1_pass.split(",");
        	redisClusterPass[1] = clusterpass1[0];
        }
        
        if(config == null) {
        	config = new JedisPoolConfig();
        	//控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        	//如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
        	config.setMaxTotal(Integer.parseInt(maxTotal));
        	//控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        	config.setMaxIdle(Integer.parseInt(maxIdle));
        	//表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        	config.setMaxWaitMillis(Long.parseLong(maxWaitMillis));
        	//在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        	config.setTestOnBorrow(Boolean.valueOf(testOnBorrow));
        	config.setLifo(true);
        	config.setFairness(true);
        	config.setMinIdle(2);
        }

    	//连接池数组
    	if(jedisPools == null) {
    		jedisPools = new JedisPool[JedisPoolCount];
    		//redis配置0参数
            logger.info("host = " + host0 + ", port =" + port0 + ", pass =" + pass0 + ", timeout =" + timeout0 + ", maxIdle =" + maxIdle + ", maxTotal =" + maxTotal + ", maxWaitMillis =" + maxWaitMillis + ", testOnBorrow =" + testOnBorrow);
            //生成连接池0
            if(host0 != null && port0 != null)
            	jedisPools[SpringConfig.RedisConnPool.MASTERREDIS.value] = new JedisPool(config, host0, Integer.parseInt(port0), Integer.parseInt(timeout0), pass0);
            else
            	jedisPools[SpringConfig.RedisConnPool.MASTERREDIS.value] = null;
            //redis配置1参数
            logger.info("host = " + host1 + ", port =" + port1 + ", pass =" + pass1 + ", timeout =" + timeout1 + ", maxIdle =" + maxIdle + ", maxTotal =" + maxTotal + ", maxWaitMillis =" + maxWaitMillis + ", testOnBorrow =" + testOnBorrow);
            //生成连接池1
            if(host1 != null && port1 != null)
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS1.value] = new JedisPool(config, host1, Integer.parseInt(port1), Integer.parseInt(timeout1), pass1);
            else
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS1.value] = null;
            //redis配置2参数
            logger.info("host = " + host2 + ", port =" + port2 + ", pass =" + pass2 + ", timeout =" + timeout2 + ", maxIdle =" + maxIdle + ", maxTotal =" + maxTotal + ", maxWaitMillis =" + maxWaitMillis + ", testOnBorrow =" + testOnBorrow);
            //生成连接池2
            if(host2 != null && port2 != null)
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS2.value] = new JedisPool(config, host2, Integer.parseInt(port2), Integer.parseInt(timeout2), pass2);
            else
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS2.value] = null;
            //redis配置3参数
            logger.info("host = " + host3 + ", port =" + port3 + ", pass =" + pass3 + ", timeout =" + timeout3 + ", maxIdle =" + maxIdle + ", maxTotal =" + maxTotal + ", maxWaitMillis =" + maxWaitMillis + ", testOnBorrow =" + testOnBorrow);
            //生成连接池3
            if(host3 != null && port3 != null)
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS3.value] = new JedisPool(config, host3, Integer.parseInt(port3), Integer.parseInt(timeout3), pass3);
            else
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS3.value] = null;
            //redis配置4参数
            logger.info("host = " + host4 + ", port =" + port4 + ", pass =" + pass4 + ", timeout =" + timeout4 + ", maxIdle =" + maxIdle + ", maxTotal =" + maxTotal + ", maxWaitMillis =" + maxWaitMillis + ", testOnBorrow =" + testOnBorrow);
            //生成连接池4
            if(host4 != null && port4 != null)
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS4.value] = new JedisPool(config, host4, Integer.parseInt(port4), Integer.parseInt(timeout4), pass4);
            else
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS4.value] = null;
            //redis配置5参数
            logger.info("host = " + host5 + ", port =" + port5 + ", pass =" + pass5 + ", timeout =" + timeout5 + ", maxIdle =" + maxIdle + ", maxTotal =" + maxTotal + ", maxWaitMillis =" + maxWaitMillis + ", testOnBorrow =" + testOnBorrow);
            //生成连接池5
            if(host5 != null && port5 != null)
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS5.value] = new JedisPool(config, host5, Integer.parseInt(port5), Integer.parseInt(timeout5), pass5);
            else
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS5.value] = null;
            //redis配置6参数
            logger.info("host = " + host6 + ", port =" + port6 + ", pass =" + pass6 + ", timeout =" + timeout6 + ", maxIdle =" + maxIdle + ", maxTotal =" + maxTotal + ", maxWaitMillis =" + maxWaitMillis + ", testOnBorrow =" + testOnBorrow);
            //生成连接池6
            if(host6 != null && port6 != null)
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS6.value] = new JedisPool(config, host6, Integer.parseInt(port6), Integer.parseInt(timeout6), pass6);
            else
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS6.value] = null;
            //redis配置7参数
            logger.info("host = " + host7 + ", port =" + port7 + ", pass =" + pass7 + ", timeout =" + timeout7 + ", maxIdle =" + maxIdle + ", maxTotal =" + maxTotal + ", maxWaitMillis =" + maxWaitMillis + ", testOnBorrow =" + testOnBorrow);
            //生成连接池7
            if(host7 != null && port7 != null)
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS7.value] = new JedisPool(config, host7, Integer.parseInt(port7), Integer.parseInt(timeout7), pass7);
            else
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS7.value] = null;
            //redis配置8参数
            logger.info("host = " + host8 + ", port =" + port8 + ", pass =" + pass8 + ", timeout =" + timeout8 + ", maxIdle =" + maxIdle + ", maxTotal =" + maxTotal + ", maxWaitMillis =" + maxWaitMillis + ", testOnBorrow =" + testOnBorrow);
            //生成连接池8
            if(host8 != null && port8 != null)
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS8.value] = new JedisPool(config, host8, Integer.parseInt(port8), Integer.parseInt(timeout8), pass8);
            else
            	jedisPools[SpringConfig.RedisConnPool.SLAVEREDIS8.value] = null;
    	}
    	//连接集群0
    	if(jedisClusterNodes0 == null) {
    		// Redis集群0的节点集合
    		jedisClusterNodes0 = new HashSet<HostAndPort>();
    		if(clusterhost0 != null && clusterport0 != null) {
    			logger.info("Redis集群配置0信息：");
    			for(int i = 0; i < clusterhost0.length; i++) {
    				jedisClusterNodes0.add(new HostAndPort(clusterhost0[i], Integer.parseInt(clusterport0[i])));
    				logger.info("IP: " + clusterhost0[i] + ", PORT: " + clusterport0[i]);
    			}
    		}
    	}
    	//连接集群1
    	if(jedisClusterNodes1 == null) {    
    		// Redis集群1的节点集合
    		jedisClusterNodes1 = new HashSet<HostAndPort>();
    		if(clusterhost1 != null && clusterport1 != null) {
    			logger.info("Redis集群配置1信息：");
    			for(int i = 0; i < clusterhost1.length; i++) {
    				jedisClusterNodes1.add(new HostAndPort(clusterhost1[i], Integer.parseInt(clusterport1[i])));
    				logger.info("IP: " + clusterhost1[i] + ", PORT: " + clusterport1[i]);
    			}
    		}    		
    	}
    }

    //从jedis连接池中获取获取jedis对象
    //@return
    private Jedis getJedis(int jedisIdx) {
        return jedisPools[jedisIdx].getResource();
    }
    
    //从jedis集群连接池中获取获取jedis集群对象，jedisClusterIdx负数，如-1、-2等
    //@return
    private JedisCluster getJedisCluster(int jedisClusterIdx) {
    	// Redis集群连接池实例
    	if(jedisCluster == null) { 
    		jedisCluster = new JedisCluster[JedisClusterCount];
    		jedisCluster[0] = new JedisCluster(jedisClusterNodes0, config);     //UAT集群未设置密码
    		jedisCluster[1] = new JedisCluster(jedisClusterNodes1, 10000, 5000, 5, redisClusterPass[1], config);
    	}
    	int curIdx = jedisClusterIdx + JedisClusterCount;
    	if(curIdx < 0 || curIdx >= JedisClusterCount)
    		curIdx = 0;
        return jedisCluster[curIdx];
    }

    //获取RedisRWClient实例
    //@return
    public static RedisRWClient getInstance() {
    	if(jedisUtil == null)
    		jedisUtil = new RedisRWClient();
        return jedisUtil;
    }
    
    //以键值和域名获取hash表域值
    public String getHashByKeyField(int jedisIdx, int dbIndex, String key, String field) {
    	String ret = null;
    	if(jedisIdx >= 0) {
    		Jedis jedis = getJedis(jedisIdx);
    		jedis.select(dbIndex);
    		ret = jedis.hget(key, field);
    		jedis.close();
    	}
    	else {
    		JedisCluster jedisCluster = getJedisCluster(jedisIdx);
    		ret = jedisCluster.hget(key, field);
    	}
    	return ret;
    }
    
    //以键值获取hash表所有映射值
    public Map<String, String> getHashByKey(int jedisIdx, int dbIndex, String key) {
    	Map<String, String> curMap = null;
    	if(jedisIdx >= 0) {
    		Jedis jedis = getJedis(jedisIdx);
    		jedis.select(dbIndex);
    		curMap = jedis.hgetAll(key);
    		jedis.close();
    	}
    	else {
    		JedisCluster jedisCluster = getJedisCluster(jedisIdx);
    		curMap = jedisCluster.hgetAll(key);
    	}
    	return curMap;
    }
    
    //判断hash表键值、域是否存在
    public boolean existHashKeyFied(int jedisIdx, int dbIndex, String key, String field) {
    	boolean flag = true;
    	if(jedisIdx >= 0) {
    		Jedis jedis = getJedis(jedisIdx);
    		jedis.select(dbIndex);
    		flag = jedis.hexists(key, field);
    		jedis.close();
    	}
    	else {
    		JedisCluster jedisCluster = getJedisCluster(jedisIdx);
    		flag = jedisCluster.hexists(key, field);
    	}
    	return flag;
    }
    
    //判断hash表键值是否存在
    public boolean existHashKey(int jedisIdx, int dbIndex, String key) {
    	boolean flag = true;
    	if(jedisIdx >= 0) {
    		Jedis jedis = getJedis(jedisIdx);
    		jedis.select(dbIndex);
    		flag = jedis.exists(key);
    		jedis.close();
    	}
    	else {
    		JedisCluster jedisCluster = getJedisCluster(jedisIdx);
    		flag = jedisCluster.exists(key);
    	}
    	return flag;
    }
    
    //删除键key
    public boolean delHaskKey(int jedisIdx, int dbIndex, String key) {
    	boolean flag = true;
    	if(jedisIdx != -1) {
    		Jedis jedis = getJedis(jedisIdx);
    		if(dbIndex < 0)
    			dbIndex = 0;
    		jedis.select(dbIndex);
    		jedis.del(key);
    		if(jedis.exists(key))
    			flag = false;
    		else
    			flag = true;
    		jedis.close();
    	}
    	else {
    		JedisCluster jedisCluster = getJedisCluster(jedisIdx);
    		jedisCluster.del(key);
    		if(jedisCluster.exists(key))
    			flag = false;
    		else
    			flag = true;
    	}
    	return flag;
    }
    
    //删除键key及field
    public boolean delHaskKey(int jedisIdx, int dbIndex, String key, String field) {
    	boolean flag = true;
    	if(jedisIdx != -1) {
    		Jedis jedis = getJedis(jedisIdx);
    		if(dbIndex < 0)
    			dbIndex = 0;
    		jedis.select(dbIndex);
    		jedis.hdel(key, field);
    		if(jedis.hexists(key, field))
    			flag = false;
    		else
    			flag = true;
    		jedis.close();
    	}
    	else {
    		JedisCluster jedisCluster = getJedisCluster(jedisIdx);
    		jedisCluster.hdel(key, field);
    		if(jedisCluster.hexists(key, field))
    			flag = false;
    		else
    			flag = true;
    	}
    	return flag;
    }
    
    //设置key及域值
    public boolean setHashKeyFieldVal(int jedisIdx, int dbIndex, String key, String field, String value) {
    	boolean flag = false;
    	if(jedisIdx != -1) {
    		Jedis jedis = getJedis(jedisIdx);
    		if(dbIndex < 0)
    			dbIndex = 0;
    		jedis.select(dbIndex);
    		long setRet = jedis.hset(key, field, value);
    		if(setRet == 0 || setRet == 1)
    			flag = true;
    		jedis.close();
    	}
    	else {
    		JedisCluster jedisCluster = getJedisCluster(jedisIdx);
    		long setRet = jedisCluster.hset(key, field, value);
    		if(setRet == 0 || setRet == 1)
    			flag = true;
    	}    	
    	return flag;
    }
    
    //测试
    public static void test(String[] args) {
    	RedisRWClient jedisUtil = RedisRWClient.getInstance();
    	JedisCluster jedisCluster = jedisUtil.getJedisCluster(-2);
    	if(jedisCluster != null) {
    		Map<String, String> map1 = jedisCluster.hgetAll("shanghai");
    		if(map1 != null) {
    			for(String key:map1.keySet()) {
    				logger.info(key + ", " + map1.get(key));
    			}
    		}
    		else
    			logger.info("map1 = null");
    		boolean flag = jedisCluster.exists("shanghai");
    		if(flag)
    			logger.info("键shanghai存在！");
    		else
    			logger.info("键shanghai不存在！");
    	}
    	else 
    		logger.info("jedisCluster = null");
    }
}
