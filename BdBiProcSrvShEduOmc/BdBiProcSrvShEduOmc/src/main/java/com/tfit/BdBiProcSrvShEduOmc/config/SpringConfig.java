package com.tfit.BdBiProcSrvShEduOmc.config;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.MultipartConfigElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource({"classpath:application.properties"})
public class SpringConfig {
	private static final Logger logger = LogManager.getLogger(SpringConfig.class.getName());
	
	//阳光午餐数据主题
	public static final String shinelunch_que = "maxwellTopic1";
	
	//阳光午餐数据demo1主题
	public static final String shinelunch_demo1_que = "maxwellDemo1";
	
	//环境参数，即sit，uat或prod
    public static String spring_profiles_active = "";  //spring.profiles.active=@env@
    //基础目录
    public static String base_dir = "/opt/data/BdBiProcSrvShEduOmc";
//	BdBiProcSrvShEduOmcpublic static String base_dir = "e:/BdBiProcSrvShEduOmc/";
    //临时文件目录
    public static String tmp_file_dir = "/opt/data/BdBiProcSrvShEduOmc/tmp";
    
    //网络配置
    public static String tomcatIpPort = "";
    public static String server_port = "";
    //文件服务目录集合
  	public static String[] tomcatSrvDirs = {"http://172.18.14.19/opt/data/tomcat/tomcat_video/webapps/ROOT", "http://172.18.14.19/opt/apps/tomcat/tomcat_repfile/webapps/ROOT"};
    //视频图片文件服务域名
    public static String video_srvdn = "";
    public static String pic = "/opt/apps/B2bFileSrv01/webapps/ROOT";
    //视频文件服务目录集合
    public static String[] videoFileSrvDirs = {"/amSaveUserInfo", "/etVidLib", "/biOptMain", "/ecBriKitStove", "/briKitStoveRtVids", "/briKitStovePbVids", "/etVidThumbAtt",
    		                                   "/etVidAttachment"};
    //报表文件服务域名
    public static String repfile_srvdn = "";
    //报表文件服务目录集合
    public static String[] repFileSrvDirs = {"/expBdSchList", "/expKwSchRecs", "/expSchWasteOilDets", "/expCaDishSupDets", "/expMatConfirmDets", "/expSumDataDets", 
    		                                 "/expCaDishSupStats", "/expMatConfirms", "/expWarnAllLicDets", "/expCaMatSupDets", "/expPpDishDets", "/expWarnAllLics", 
    		                                 "/expCaMatSupStats", "/expPpDishList", "/expWarnRmcLicDets", "/expDishRetSamples", "/expRmcKwDets", "/expWarnRmcLics", 
    		                                 "/biOptMain", "/expDishRsDets", "/expRmcRecWasteOils", "/expWarnSchLicDets", "/expGsPlanOptDets", "/expRmcWasteOilDets", 
    		                                 "/expWarnSchLics", "/expAmRoleManage", "/expGsPlanOpts", "/expSchKwDets", "/expWarnStaffLicDets", "/expAmUserManage", 
    		                                 "/expKwRmcRecs", "/expSchRecWasteOils", "/expWarnStaffLics", "/expBdRmcList", "/expGsPlanOptDets", "/rbUlAttachment", 
    		                                 "/expSchRecWasteOils", "/expSchWasteOilDets", "/expRmcRecWasteOils", "/expRmcWasteOilDets","/expPpGsPlanOpts","/expPpRetSamples",
    		                                 "/expDishSumInfo", "/expBdSupList", "/expSearchSchList", "/expWeeklyOptReps","/expSearchMaterialList","/expSearchMaterialListPic",
    		                                 "/expWarnMaterialOverPro","/expWarnMaterialOverProDets",
    		                                 "/expAcceptWarnSumListReps","/expAcceptWarnListReps","/expDishWarnListReps","/expDishWarnSumListReps",
    		                                 "/expPpGsPlanWeek","/expPpRetSamplesWeek","/expPpDishWeek","/expPpMatConfirmWeek"};
    //报表文件格式
    public static String[] repFileFormats = {".xls", ".xlsx"};
    //报表文件格式索引
    public static int curRepFileFrmtIdx = 1;
    //阳光午餐图片文件服务域名
    public static String ss_picfile_srvdn = "";
    //HDFS文件目录
    public static String hdfscluster0_dir_default = "";  //报表文件格式索引

    //是否使用测试邮件账号
    public static boolean isUseTestMail = true;
    
    //Redis数据清理时间长度（单位：天），即清理该时间长度以前的redis数据
    public static int redis_clearagodays = 40;
    
    //Redis连接池对象枚举
    public static enum RedisConnPool {  
    	REDISCLUSTER0(-2), REDISCLUSTER1(-1), MASTERREDIS(0), SLAVEREDIS1(1), SLAVEREDIS2(2), SLAVEREDIS3(3), SLAVEREDIS4(4), SLAVEREDIS5(5), SLAVEREDIS6(6), SLAVEREDIS7(7), SLAVEREDIS8(8);    	
    	public final int value;
        private RedisConnPool(int value) {
            this.value = value;
        }
    }  
    //Redis数据库索引枚举
    public static enum RedisDBIndex {
    	DB0(0), DB1(1), DB2(2), DB3(3), DB4(4), DB5(5), DB6(6), DB7(7), DB8(8), DB9(9), DB10(10), DB11(11), DB12(12), DB13(13), DB14(14), DB15(15);
    	public final int value;
        private RedisDBIndex(int value) {
            this.value = value;
        }
    }
    //Redis运行环境
    public static int RedisRunEnvIdx = 0;               //运行环境索引，-1表示redis集群，非负数表示对应redis单机节点
    public static int RedisDBIdx = 0;                   //运行数据库索引，范围：0～15
    //Redis运行环境索引，-1表示集群模式，否则为单机运行模式（取值范围：0～8）
    public static String redis_runenvindex = "";
	public static String redis_dbindex= "";
    //Redis通用配置参数
    public static String redis_maxIdle = "";             //redis.maxIdle=300
    public static String redis_maxTotal = "";            //redis.maxTotal=600
    public static String redis_maxWaitMillis = "";       //redis.maxWaitMillis=1000
    public static String redis_testOnBorrow = "";        //redis.testOnBorrow=false
    //Redis配置0参数
    public static String redis0_host = "";               //Redis的IP地址
    public static String redis0_port = "";               //端口
    public static String redis0_pass = "";               //密码
    public static String redis0_timeout = "";            //超时，单位：ms
    //Redis配置1参数
    public static String redis1_host = "";
    public static String redis1_port = "";
    public static String redis1_pass = "";
    public static String redis1_timeout = "";
    //Redis配置2参数
    public static String redis2_host = "";
    public static String redis2_port = "";
    public static String redis2_pass = "";
    public static String redis2_timeout = "";
    //Redis配置3参数
    public static String redis3_host = "";
    public static String redis3_port = "";
    public static String redis3_pass = "";
    public static String redis3_timeout = "";
    //Redis配置4参数
    public static String redis4_host = "";
    public static String redis4_port = "";
    public static String redis4_pass = "";
    public static String redis4_timeout = "";
    //Redis配置5参数
    public static String redis5_host = "";
    public static String redis5_port = "";
    public static String redis5_pass = "";
    public static String redis5_timeout = "";
    //Redis配置6参数
    public static String redis6_host = "";
    public static String redis6_port = "";
    public static String redis6_pass = "";
    public static String redis6_timeout = "";
    //Redis配置7参数
    public static String redis7_host = "";
    public static String redis7_port = "";
    public static String redis7_pass = "";
    public static String redis7_timeout = "";
    //Redis配置8参数
    public static String redis8_host = "";
    public static String redis8_port = "";
    public static String redis8_pass = "";
    public static String redis8_timeout = "";
    
    //Redis集群配置0参数
    public static String rediscluster0_host = "";               //Redis集群的IP地址
    public static String rediscluster0_port = "";               //端口
    public static String rediscluster0_pass = "";               //密码
    public static String rediscluster0_timeout = "";            //超时，单位：ms
    //Redis集群配置1参数
    public static String rediscluster1_host = "";
    public static String rediscluster1_port = "";
    public static String rediscluster1_pass = "";
    public static String rediscluster1_timeout = "";
    
    //Hdfs配置
    public static String hdfscluster0_url= "";
    public static String hdfscluster1_url= "";
    //hdfs 默认目录
	public static String hdfscluster1_dir_default= "";
    
    //创建目录
   	public static void CreateDir(String strDirName) 
   	{
   		String strCurDirName = strDirName;
   		File dir = new File(strCurDirName);
   		if(dir.exists()) 
   		{
   			// 判断目录是否存在
   			logger.info("目录 " + strCurDirName + " 已存在！");
   		}
   		else
   		{
   			if(dir.mkdirs()) 
   				logger.info("目录 " + strCurDirName + " 创建成功！");	 
   			else 
   				logger.info("创建目录 " + strCurDirName + " 失败！");
   		}
   	}
   	
   	//初始化
   	public static void init() {
   		Environment env = ApplicationUtil.getBean(Environment.class);
   		//环境参数，即sit，uat或prod
   		spring_profiles_active = env.getProperty("spring.profiles.active");
   		logger.info("spring_profiles_active =  " + spring_profiles_active);
   		server_port = env.getProperty("server.port");
   		logger.info("server_port =  " + server_port);
   		//视频文件服务域名
   		video_srvdn = env.getProperty("spring.video.srvdn");
   		logger.info("video_srvdn =  " + video_srvdn);
   		//报表文件服务域名
   	    repfile_srvdn = env.getProperty("spring.repfile.srvdn");
   	    //阳光午餐图片文件服务域名
   	    ss_picfile_srvdn = env.getProperty("sunshinelunch.picfile.srvdn");
   		//Redis初始化
   	   	initRedis(env);
   	   	//Hdfs配置
   	    hdfscluster0_url= env.getProperty("hdfscluster0.url");
   	    hdfscluster1_url= env.getProperty("hdfscluster1.url");
		hdfscluster0_dir_default= env.getProperty("hdfscluster0.dir");
		hdfscluster1_dir_default= env.getProperty("hdfscluster1.dir");
		//是否使用测试邮件账号
		String strIsUseTestMail = env.getProperty("spring.isUseTestMail");
		if(strIsUseTestMail != null) {
			isUseTestMail = Boolean.parseBoolean(strIsUseTestMail);
		}
		logger.info("是否使用学校测试邮件账号：" + isUseTestMail);
   	}
   	
    //设置Redis运行环境
   	public static void setRedisRunEnv() {
   		//运行环境索引，-1表示redis集群，非负数表示对应redis单机节点
   		if(redis_runenvindex != null) {
   			RedisRunEnvIdx = Integer.parseInt(redis_runenvindex);
   			if(RedisRunEnvIdx < SpringConfig.RedisConnPool.REDISCLUSTER0.value || RedisRunEnvIdx > SpringConfig.RedisConnPool.SLAVEREDIS8.value)
   				RedisRunEnvIdx = SpringConfig.RedisConnPool.MASTERREDIS.value;
   		}
   		else
   			RedisRunEnvIdx = SpringConfig.RedisConnPool.MASTERREDIS.value;
        if(RedisRunEnvIdx < 0) {
        	if(RedisRunEnvIdx == -2)
        		logger.info("当前Redis运行环境为集群0，RedisRunEnvIdx = " + RedisRunEnvIdx);
        	else if(RedisRunEnvIdx == -1)
        		logger.info("当前Redis运行环境为集群1，RedisRunEnvIdx = " + RedisRunEnvIdx);
        }
        else {
        	logger.info("当前Redis运行环境为单机连接，RedisRunEnvIdx = " + RedisRunEnvIdx);
        }

        if(redis_dbindex != null){
			RedisDBIdx = Integer.parseInt(redis_dbindex);
			if(RedisDBIdx < RedisDBIndex.DB0.value || RedisDBIdx > RedisDBIndex.DB15.value){
				/**
				 * uat环境 redis 配置信息，兼容uta配置
				 */
				RedisDBIdx = SpringConfig.RedisDBIndex.DB8.value;
			}
		}
        //运行数据库索引，范围：0～15
        //RedisDBIdx = SpringConfig.RedisDBIndex.DB8.value;
        logger.info("当前Redis数据库索引，RedisDBIdx = DB" + RedisDBIdx);
   	}
   	
   	//Redis初始化
   	public static void initRedis(Environment env) {
   		//Redis运行环境索引，-1表示集群模式，否则为单机运行模式（取值范围：0～8）
   		redis_runenvindex = env.getProperty("spring.redis.runenvindex");
		logger.info("redis_runenvindex =  " + redis_runenvindex);
		redis_dbindex = env.getProperty("spring.redis.dbindex");
		logger.info("redis_dbindex =  " + redis_dbindex);
   		//Redis通用配置参数
	    redis_maxIdle = env.getProperty("redis.maxIdle");
	    logger.info("redis_maxIdle =  " + redis_maxIdle);
	    redis_maxTotal = env.getProperty("redis.maxTotal");
	    logger.info("redis_maxTotal =  " + redis_maxTotal);
	    redis_maxWaitMillis = env.getProperty("redis.maxWaitMillis");
	    logger.info("redis_maxWaitMillis =  " + redis_maxWaitMillis);
	    redis_testOnBorrow = env.getProperty("redis.testOnBorrow");
	    logger.info("redis_testOnBorrow =  " + redis_testOnBorrow);
	    //Redis配置0参数
   		redis0_host = env.getProperty("redis0.host");
   		logger.info("redis0_host =  " + redis0_host);
   		redis0_port = env.getProperty("redis0.port");
   		logger.info("redis0_port =  " + redis0_port);
   	    redis0_pass = env.getProperty("redis0.pass");
   	    logger.info("redis0_pass =  " + redis0_pass);
   	    redis0_timeout = env.getProperty("redis0.timeout");
	    logger.info("redis0_timeout =  " + redis0_timeout);
   		//Redis配置1参数
   		redis1_host = env.getProperty("redis1.host");
   		logger.info("redis1_host =  " + redis1_host);
   		redis1_port = env.getProperty("redis1.port");
   		logger.info("redis1_port =  " + redis1_port);
   	    redis1_pass = env.getProperty("redis1.pass");
   	    logger.info("redis1_pass =  " + redis1_pass);
   	    redis1_timeout = env.getProperty("redis1.timeout");
	    logger.info("redis1_timeout =  " + redis1_timeout);
	    //Redis配置2参数
	    redis2_host = env.getProperty("redis2.host");
   		logger.info("redis2_host =  " + redis2_host);
   		redis2_port = env.getProperty("redis2.port");
   		logger.info("redis2_port =  " + redis2_port);
   	    redis2_pass = env.getProperty("redis2.pass");
   	    logger.info("redis2_pass =  " + redis2_pass);
   	    redis2_timeout = env.getProperty("redis2.timeout");
	    logger.info("redis2_timeout =  " + redis2_timeout);	    
	    //Redis配置3参数
   		redis3_host = env.getProperty("redis3.host");
   		logger.info("redis3_host =  " + redis3_host);
   		redis3_port = env.getProperty("redis3.port");
   		logger.info("redis3_port =  " + redis3_port);
   	    redis3_pass = env.getProperty("redis3.pass");
   	    logger.info("redis3_pass =  " + redis3_pass);
   	    redis3_timeout = env.getProperty("redis3.timeout");
	    logger.info("redis3_timeout =  " + redis3_timeout);
   		//Redis配置4参数
   		redis4_host = env.getProperty("redis4.host");
   		logger.info("redis4_host =  " + redis4_host);
   		redis4_port = env.getProperty("redis4.port");
   		logger.info("redis4_port =  " + redis4_port);
   	    redis4_pass = env.getProperty("redis4.pass");
   	    logger.info("redis4_pass =  " + redis4_pass);
   	    redis4_timeout = env.getProperty("redis4.timeout");
	    logger.info("redis4_timeout =  " + redis4_timeout);
	    //Redis配置5参数
	    redis5_host = env.getProperty("redis5.host");
   		logger.info("redis5_host =  " + redis5_host);
   		redis5_port = env.getProperty("redis5.port");
   		logger.info("redis5_port =  " + redis5_port);
   	    redis5_pass = env.getProperty("redis5.pass");
   	    logger.info("redis5_pass =  " + redis5_pass);
   	    redis5_timeout = env.getProperty("redis5.timeout");
	    logger.info("redis5_timeout =  " + redis5_timeout);	    
	    //Redis配置6参数
   		redis6_host = env.getProperty("redis6.host");
   		logger.info("redis6_host =  " + redis6_host);
   		redis6_port = env.getProperty("redis6.port");
   		logger.info("redis6_port =  " + redis6_port);
   	    redis6_pass = env.getProperty("redis6.pass");
   	    logger.info("redis6_pass =  " + redis6_pass);
   	    redis6_timeout = env.getProperty("redis6.timeout");
	    logger.info("redis6_timeout =  " + redis6_timeout);
   		//Redis配置7参数
   		redis7_host = env.getProperty("redis7.host");
   		logger.info("redis7_host =  " + redis7_host);
   		redis7_port = env.getProperty("redis7.port");
   		logger.info("redis7_port =  " + redis7_port);
   	    redis7_pass = env.getProperty("redis7.pass");
   	    logger.info("redis7_pass =  " + redis7_pass);
   	    redis7_timeout = env.getProperty("redis7.timeout");
	    logger.info("redis7_timeout =  " + redis7_timeout);
	    //Redis配置8参数
	    redis8_host = env.getProperty("redis8.host");
   		logger.info("redis8_host =  " + redis8_host);
   		redis8_port = env.getProperty("redis8.port");
   		logger.info("redis8_port =  " + redis8_port);
   	    redis8_pass = env.getProperty("redis8.pass");
   	    logger.info("redis8_pass =  " + redis8_pass);
   	    redis8_timeout = env.getProperty("redis8.timeout");
	    logger.info("redis8_timeout =  " + redis8_timeout);
	    
	    //Redis集群配置0参数
	    rediscluster0_host = env.getProperty("rediscluster0.host");                  //Redis集群的IP地址
	    rediscluster0_port = env.getProperty("rediscluster0.port");                  //端口
	    rediscluster0_pass = env.getProperty("rediscluster0.pass");                  //密码
	    rediscluster0_timeout = env.getProperty("rediscluster0.timeout");            //超时，单位：ms
	    //Redis集群配置1参数
	    rediscluster1_host = env.getProperty("rediscluster1.host");
	    rediscluster1_port = env.getProperty("rediscluster1.port");
	    rediscluster1_pass = env.getProperty("rediscluster1.pass");
	    rediscluster1_timeout = env.getProperty("rediscluster1.timeout");
	    
	    //文件服务服务目录
	    String fileSrvDir = env.getProperty("spring.tomcat.filedirs");
	    if(fileSrvDir != null) {
	    	String[] fileSrvDirs = fileSrvDir.split(",");
	    	tomcatSrvDirs = new String[fileSrvDirs.length];
	    	for(int i = 0; i < fileSrvDirs.length; i++) {
	    		tomcatSrvDirs[i] = fileSrvDirs[i];
	    		logger.info("tomcatSrvDirs[" + i + "]=" + tomcatSrvDirs[i]);
	    	}
	    }
	    
	    //连接池枚举
        logger.info("SpringConfig.RedisConnPool.MASTERREDIS = " + SpringConfig.RedisConnPool.MASTERREDIS.value);
        logger.info("SpringConfig.RedisConnPool.SLAVEREDIS1 = " + SpringConfig.RedisConnPool.SLAVEREDIS1.value);
        logger.info("SpringConfig.RedisConnPool.SLAVEREDIS2 = " + SpringConfig.RedisConnPool.SLAVEREDIS2.value);
        logger.info("SpringConfig.RedisDBIndex.DB0 = " + SpringConfig.RedisDBIndex.DB0.value);
        logger.info("SpringConfig.RedisDBIndex.DB1 = " + SpringConfig.RedisDBIndex.DB1.value);
        logger.info("SpringConfig.RedisDBIndex.DB2 = " + SpringConfig.RedisDBIndex.DB2.value);
        logger.info("SpringConfig.RedisDBIndex.DB3 = " + SpringConfig.RedisDBIndex.DB3.value);
        logger.info("SpringConfig.RedisDBIndex.DB4 = " + SpringConfig.RedisDBIndex.DB4.value);
        logger.info("SpringConfig.RedisDBIndex.DB5 = " + SpringConfig.RedisDBIndex.DB5.value);
        logger.info("SpringConfig.RedisDBIndex.DB6 = " + SpringConfig.RedisDBIndex.DB6.value);
        logger.info("SpringConfig.RedisDBIndex.DB7 = " + SpringConfig.RedisDBIndex.DB7.value);
        logger.info("SpringConfig.RedisDBIndex.DB8 = " + SpringConfig.RedisDBIndex.DB8.value);
        logger.info("SpringConfig.RedisDBIndex.DB9 = " + SpringConfig.RedisDBIndex.DB9.value);
        logger.info("SpringConfig.RedisDBIndex.DB10 = " + SpringConfig.RedisDBIndex.DB10.value);
        logger.info("SpringConfig.RedisDBIndex.DB11 = " + SpringConfig.RedisDBIndex.DB11.value);
        logger.info("SpringConfig.RedisDBIndex.DB12 = " + SpringConfig.RedisDBIndex.DB12.value);
        logger.info("SpringConfig.RedisDBIndex.DB13 = " + SpringConfig.RedisDBIndex.DB13.value);
        logger.info("SpringConfig.RedisDBIndex.DB14 = " + SpringConfig.RedisDBIndex.DB14.value);
        logger.info("SpringConfig.RedisDBIndex.DB15 = " + SpringConfig.RedisDBIndex.DB15.value);
        
        //设置Redis运行环境
       	setRedisRunEnv();
   	}

   	//目录配置
    public static void DirConfig() {
    	String strCurDir = "";
    	//创建分析结果目录
    	strCurDir = base_dir + "/anlresult";
    	CreateDir(strCurDir);
    	//创建kafka消息目录
    	strCurDir = base_dir + "/kafka";
    	CreateDir(strCurDir);
    	strCurDir = base_dir + "/kafka/" + shinelunch_que;
    	CreateDir(strCurDir);
    	strCurDir = base_dir + "/kafka/" + shinelunch_demo1_que;
    	CreateDir(strCurDir);
    	for(int i = 0; i < tomcatSrvDirs.length; i++) {
    		if(i == 0) {    //视频文件服务目录
    			for(int j = 0; j < videoFileSrvDirs.length; j++) {
    				CreateDir(tomcatSrvDirs[i]+videoFileSrvDirs[j]);
    				CreateDir(base_dir+videoFileSrvDirs[j]);
    			}
    		}
    		else if(i == 1) {  //报表文件服务目录
    			for(int j = 0; j < repFileSrvDirs.length; j++) {
    				CreateDir(tomcatSrvDirs[i]+repFileSrvDirs[j]);
    				CreateDir(base_dir+repFileSrvDirs[j]);
    			}
    		}
    	}
    }
    
    //网络配置
    public static void NetConfig() {
    	InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		if(addr != null) {
			String ip = addr.getHostAddress().toString();         //获取本机ip
			tomcatIpPort = ip + ":" + server_port;
		}
		logger.info("tomcatIpPort = " + tomcatIpPort);
    }
    
    //运行配置
    public static void RunConfig() {
    	//初始化
    	init();
    	//目录配置
    	DirConfig();
    	//网络配置
        NetConfig();
    }
    
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize("196MB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("196MB");
        return factory.createMultipartConfig();
    }
}
