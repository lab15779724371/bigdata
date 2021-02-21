package com.tfit.BdBiProcSrvShEduOmc.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.config.SpringConfig;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TEduSchoolSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.dao.domain.TProSupplierDo;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.JsonFormatTool;

//Kafka消费者
//使用@KafkaListener注解,可以指定:主题,分区,消费组
@Component
public class KafkaConsumerClient {
	private static final Logger logger = LogManager.getLogger(KafkaConsumerClient.class.getName());
	
	@Autowired
	SaasService saasService;
	
	//kafka消息处理标识，false表示没有处理，true表示已处理
	private boolean kafkaMsgProcFlag = true; //false;
	
	//获取全量数据标志
	private boolean fullQuantityFlag = true; //false;
	
	//数据库表名列表
	private List<String> dbTabNameList = new ArrayList<>();
	
	//接收阳光午餐数据（MySQL的binlog数据）
	//@KafkaListener(topics = SpringConfig.shinelunch_que)
	public void receiveShinelunch(String message) {
		logger.info("主题：" + SpringConfig.shinelunch_que + "，接收消息:" + message);
		String strCurTime = BCDTimeUtil.convertBCDFrom(null);
		String strFileName = SpringConfig.base_dir + "/kafka/" + SpringConfig.shinelunch_que + "/" + strCurTime;
		logger.info("Kafka消息数据文件：" + strFileName);
		WriteBinaryFile(null, message, strFileName);
	}
    
    //接收阳光午餐数据demo1（MySQL的binlog数据）
  	//@KafkaListener(topics = SpringConfig.shinelunch_demo1_que)
  	public void receiveShinelunchDemo1(String message) {
  		logger.info("主题：" + SpringConfig.shinelunch_demo1_que + "，接收消息:" + message);
  		String strCurTime = BCDTimeUtil.convertBCDFrom(null);
  		String strFileName = SpringConfig.base_dir + "/kafka/" + SpringConfig.shinelunch_demo1_que + "/" + strCurTime;
  		logger.info("Kafka消息数据文件：" + strFileName);
  		WriteBinaryFile(null, message, strFileName);
  		//解析数据流
  		JSONObject binLogJObj = JSONObject.parseObject(message);
  		if(binLogJObj == null)
  			return ;
  		//获取数据库操作信息
  		String database = binLogJObj.getString("database");    //数据库名
  		String table = binLogJObj.getString("table");          //数据表名
  		String type = binLogJObj.getString("type");            //操作类型
  		long ts = binLogJObj.getLong("ts");                    //时间
  		long xid = binLogJObj.getLong("xid");                  //操作ID
  		//获取当前数据
  		JSONObject curDataJObj = binLogJObj.getJSONObject("data");
  		//获取上次数据
  		JSONObject oldDataJobj = binLogJObj.getJSONObject("old");
  		String curOptTime = BCDTimeUtil.convertNormalFromLong(ts);
  		logger.info("数据库：" + database + "表名：" + table + "接收到一个MySQL更新操作，操作时间：" + curOptTime + "，操作类型：" + type + "，操作id：" + xid);
  		int optType = -1;
  		//数据操作类型
  		if(type.compareTo("insert") == 0)
  			optType = 0;
  		else if(type.compareTo("update") == 0)
  			optType = 1;
  		else if(type.compareTo("delete") == 0)
  			optType = 2;
  		//按数据库及其表名进行解析操作
  		if(database.compareTo("test_saas") == 0)
  		{
  			//按数据表解析
  			if(table.compareTo("t_saas_week_dishes_tmp") == 0) {
  				//数据表解析处理
  				test_saas_t_saas_week_dishes_tmp(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_saas_package") == 0) {
  			    //数据表解析处理
  				test_saas_t_saas_package(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_pro_ledger") == 0) {
  			    //数据表解析处理
  				test_saas_t_pro_ledger(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_pro_supplier") == 0)	{
  			    //数据表解析处理
  				test_saas_t_pro_supplier(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_pro_warning_global_master") == 0) {
  			    //数据表解析处理
  				test_saas_t_pro_warning_global_master(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_saas_package_dishes") == 0) {
  			    //数据表解析处理
  				test_saas_t_saas_package_dishes(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_saas_package_dishes_ware") == 0) {
  			    //数据表解析处理
  				test_saas_t_saas_package_dishes_ware(curDataJObj, oldDataJobj, optType);
  			}
  		}
  		else if(database.compareTo("test_edu") == 0)
  		{
  		    if(table.compareTo("t_edu_school") == 0) {
  		        //数据表解析处理
  		    	test_edu_t_edu_school(curDataJObj, oldDataJobj, optType);
			}
  		}
  		else if(database.compareTo("bi_test") == 0) {
  			if(table.compareTo("t_edu_school") == 0) {  		
  			    //数据表解析处理
  				bi_test_t_edu_school(curDataJObj, oldDataJobj, optType);
  			}
  		}
  	}
  	
  	//处理本地kafka消息数据
  	@Scheduled(fixedRate = 60*60*1000)
  	public void ProcLocalKafkaMsg() {
  		if(kafkaMsgProcFlag)
  			return ;
  		kafkaMsgProcFlag = true;
  	    //获取指定路径下的所有文件列表
  	  	String strDir = SpringConfig.base_dir + "/kafka", strFileName = "";
  	  	List<String> fileList = getFileList(strDir);
  	  	int i, fileCount = fileList.size();
  		logger.info("文件总数：" + fileCount);
  		//分析文件
  		for(i = 0; i < fileCount; i++) {
  			strFileName = fileList.get(i);
  			logger.info("第" + (i+1) + "个文件：" + strFileName);
  			//读取文本文件
  		    String message = ReadTxtFile(fileList.get(i));
  		    //处理本地数据文件
  		  	AnlLocalMsg(message);
  		}
  		if(dbTabNameList.size() > 0) {
  			for(i = 0; i < dbTabNameList.size(); i++)
  				logger.info(dbTabNameList.get(i));
  		}
  	}
  	
  	//获取全量数据
  	@Scheduled(fixedRate = 60*60*1000)
  	public void StartFullQuantityInfo() {
  		if(fullQuantityFlag)
  			return ;
  		fullQuantityFlag = true;
  		int i;
  		List<TProSupplierDo> tpsList = saasService.getIdSupplierIdName();
  		for(i = 0; i < tpsList.size(); i++) {
  			TProSupplierDo tps = tpsList.get(i);
  			//logger.info("供应商id：" + tps.getId() + "，供应商名称：" + tps.getSupplier_name());
  		    //供应商供应学校分析
  		    SupplierToSchCountModAnl(tps.getId(), tps.getSupplierName(), null, 0);
  		}
  		List<TEduSchoolSupplierDo> tessList = saasService.getSupplierIdSchoolName();
  		for(i = 0; i < tessList.size(); i++) {
  			TEduSchoolSupplierDo tess = tessList.get(i);
  			//logger.info("供应商id：" + tess.getSupplier_id() + "，学校名称：" + tess.getSchool_name());
  		    //供应商供应学校分析
  		    SupplierToSchCountModAnl(tess.getSupplierId(), null, tess.getSchoolName(), 1);
  		}
  	}
  	
  	//处理本地数据文件
  	private void AnlLocalMsg(String message) {
  		logger.info("本地消息:" + message);
  		//解析数据流
  		JSONObject binLogJObj = JSONObject.parseObject(message);
  		if(binLogJObj == null)
  			return ;
  		//获取数据库操作信息
  		String database = binLogJObj.getString("database");    //数据库名
  		String table = binLogJObj.getString("table");          //数据表名
  		String type = binLogJObj.getString("type");            //操作类型
  		long ts = binLogJObj.getLong("ts");                    //时间
  		long xid = binLogJObj.getLong("xid");                  //操作ID
  		//获取当前数据
  		JSONObject curDataJObj = binLogJObj.getJSONObject("data");
  		//获取上次数据
  		JSONObject oldDataJobj = binLogJObj.getJSONObject("old");
  		String curOptTime = BCDTimeUtil.convertNormalFromLong(ts);
  		logger.info("数据库：" + database + "表名：" + table + "接收到一个MySQL更新操作，操作时间：" + curOptTime + "，操作类型：" + type + "，操作id：" + xid);
  		//获取接收的数据库表名
  		String rcvDbTabName = "数据库：" + database + "，表名：" + table + "，操作：" + type;
  		boolean isExist = false;
  		for(int i = 0; i < dbTabNameList.size(); i++) {
  			String curDbTabName = dbTabNameList.get(i);
  			if(curDbTabName.compareTo(rcvDbTabName) == 0)
  				isExist = true;
  		}
  		if(!isExist) {
  			dbTabNameList.add(rcvDbTabName);
  			logger.info("总数：" + dbTabNameList.size());
  		}
  		int optType = -1;
  		//数据操作类型
  		if(type.compareTo("insert") == 0)
  			optType = 0;
  		else if(type.compareTo("update") == 0)
  			optType = 1;
  		else if(type.compareTo("delete") == 0)
  			optType = 2;
  		//按数据库及其表名进行解析操作
  		if(database.compareTo("test_saas") == 0)
  		{
  			//按数据表解析
  			if(table.compareTo("t_saas_week_dishes_tmp") == 0) {
  				//数据表解析处理
  				test_saas_t_saas_week_dishes_tmp(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_saas_package") == 0) {
  			    //数据表解析处理
  				test_saas_t_saas_package(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_pro_ledger") == 0) {
  			    //数据表解析处理
  				test_saas_t_pro_ledger(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_pro_supplier") == 0)	{
  			    //数据表解析处理
  				test_saas_t_pro_supplier(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_pro_warning_global_master") == 0) {
  			    //数据表解析处理
  				test_saas_t_pro_warning_global_master(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_saas_package_dishes") == 0) {
  			    //数据表解析处理
  				test_saas_t_saas_package_dishes(curDataJObj, oldDataJobj, optType);
  			}
  			else if(table.compareTo("t_saas_package_dishes_ware") == 0) {
  			    //数据表解析处理
  				test_saas_t_saas_package_dishes_ware(curDataJObj, oldDataJobj, optType);
  			}
  		}
  		else if(database.compareTo("test_edu") == 0)
  		{
  		    if(table.compareTo("t_edu_school") == 0) {
  		        //数据表解析处理
  		    	test_edu_t_edu_school(curDataJObj, oldDataJobj, optType);
			}
  		}
  		else if(database.compareTo("bi_test") == 0) {
  			if(table.compareTo("t_edu_school") == 0) {  
  			    //数据表解析处理
  				bi_test_t_edu_school(curDataJObj, oldDataJobj, optType);
  			}
  		}
  	}
  	
  	//获取指定路径下的所有文件列表
  	//@param dir 要查找的目录
  	public static List<String> getFileList(String dir) {
  	    List<String> listFile = new ArrayList<>();
  	    File dirFile = new File(dir);
  	    //如果不是目录文件，则直接返回
  	    if (dirFile.isDirectory()) {
  	        //获得文件夹下的文件列表，然后根据文件类型分别处理
  	        File[] files = dirFile.listFiles();
  	        if (null != files && files.length > 0) {
  	            //根据时间排序
  	            Arrays.sort(files, new Comparator<File>() {
  	            	public int compare(File f1, File f2) {
  	                    return (int) (f1.lastModified() - f2.lastModified());
  	                }
  	 
  	                public boolean equals(Object obj) {
  	                    return true;
  	                }
  	            });
  	            for (File file : files) {
  	                //如果不是目录，直接添加
  	                if (!file.isDirectory()) {
  	                    listFile.add(file.getAbsolutePath());
  	                } else {
  	                    //对于目录文件，递归调用
  	                    listFile.addAll(getFileList(file.getAbsolutePath()));
  	                }
  	            }
  	        }
  	    }
  	    return listFile;
  	}
  	
    //解析数据库bi_test中表t_edu_school
  	private void bi_test_t_edu_school(JSONObject curDataJObj, JSONObject oldDataJobj, int optType) {
  		String id = curDataJObj.getString("id");                                     //:"35",
  		long school_id = curDataJObj.getLongValue("school_id");                      //:14536,
  		String committee_id = curDataJObj.getString("committee_id");                 //:null,
  		String school_name = curDataJObj.getString("school_name");                   //:"",
  		String school_thum = curDataJObj.getString("school_thum");                   //:null,
  		String corporation = curDataJObj.getString("corporation");                   //:null,
  		String corporation_way = curDataJObj.getString("corporation_way");           //:null,
  		String mobile_no = curDataJObj.getString("mobile_no");                       //:"",
  		String contacts = curDataJObj.getString("contacts");                         //:"",
  		String address = curDataJObj.getString("address");                           //:"",
  		String longitude = curDataJObj.getString("longitude");                       //:null,
  		String latitude = curDataJObj.getString("latitude");                         //:null,
  		String level = curDataJObj.getString("level");                               //:"",
  		String supplier_id = curDataJObj.getString("supplier_id");                   //:null,
  		int reviewed = curDataJObj.getIntValue("reviewed");                          //:0,
  		String online_payment = curDataJObj.getString("online_payment");             //:null,
  		String pj_no = curDataJObj.getString("pj_no");                               //:null,
  		String canteen_mode = curDataJObj.getString("canteen_mode");                 //:null,
  		String purchase_mode = curDataJObj.getString("purchase_mode");               //:null,
  		String school_nature = curDataJObj.getString("school_nature");               //:"",
  		String ledger_type = curDataJObj.getString("ledger_type");                   //:"",
  		String school_pic = curDataJObj.getString("school_pic");                     //:null,
  		String school_logo = curDataJObj.getString("school_logo");                   //:null,
  		String provinces = curDataJObj.getString("provinces");                       //:"",
  		String city = curDataJObj.getString("city");                                 //:null,
  		String area = curDataJObj.getString("area");                                 //:null,
  		String school_num = curDataJObj.getString("school_num");                     //:null,
  		String email = curDataJObj.getString("email");                               //:null,
  		String property = curDataJObj.getString("property");                         //:null,
  		String creator = curDataJObj.getString("creator");                           //:null,
  		String create_time = curDataJObj.getString("create_time");                   //:null,
  		String updater = curDataJObj.getString("updater");                           //:null,
  		String last_update_time = curDataJObj.getString("last_update_time");         //:null,
  		int stat = curDataJObj.getIntValue("stat");                                  //:0
  	    //操作
  		if(optType == 1) {     //更新操作
  		    
  		}
  		else if(optType == 2) {    //删除操作
  			
  		}
  	}
  	
  	//解析数据库test_saas中表t_saas_week_dishes_tmp
  	//@param，当前数据对象 curDataJObj，最近一次数据对象 oldDataJobj。操作类型 optType（0表示insert，1表示update，2表示delete）
  	private void test_saas_t_saas_week_dishes_tmp(JSONObject curDataJObj, JSONObject oldDataJobj, int optType) {
  		//判断是否是有效操作
  		if(optType == -1)
  			return ;
  		//当前数据
  		long id = curDataJObj.getLong("id");                                       //:239964,
	    String supplier_id = curDataJObj.getString("supplier_id");                 //:"1d864238-44b7-44d9-be92-b2eadbd94400",
	    String proj_name = curDataJObj.getString("proj_name");                     //:"上海市天山中学",
	    String use_date = curDataJObj.getString("use_date");                       //:"2018/5/04",
	    String menu_group_name = curDataJObj.getString("menu_group_name");         //:"小学学生",
	    String cater_type_name = curDataJObj.getString("cater_type_name");         //:"早点",
	    String dishes_name = curDataJObj.getString("dishes_name");                 //:"丝瓜炒蛋A",
	    int dishes_number = curDataJObj.getIntValue("dishes_number");              //:0,
	    int stat = curDataJObj.getIntValue("stat");                                //:4,
	    int is_history = curDataJObj.getIntValue("is_history");                    //:1,
	    String creator = curDataJObj.getString("creator");                         //:"36b1f40a-53a8-4909-988f-481b9213cd44",
	    String create_time = curDataJObj.getString("create_time");                 //:"2018-05-02 13:40:48",
	    String updater = curDataJObj.getString("updater");                         //:"36b1f40a-53a8-4909-988f-481b9213cd44",
	    String last_update_time = curDataJObj.getString("last_update_time");       //:"2018-05-02 13:52:36"
	    logger.info("id " + id + ", 供应商id " + supplier_id + ", 项目点 " + proj_name + ", 使用日期 " + use_date + ", 团餐名称" + menu_group_name + ", 团餐类型 " + cater_type_name + ", 菜品名 " + dishes_name + ", 菜品数量 " + dishes_number + ", 状态值 " + stat + ", 是否为过去类型 " + is_history + ", 创建者 " + creator + ", 创建时间 " + create_time + ", 更新者 " + updater + ", 最后更新时间 " + last_update_time);                                    
	    //操作
  		if(optType == 1) {     //更新操作
  		    //上次数据
        	int stat_old = oldDataJobj.getIntValue("stat");                 	       //:3,
	        String last_update_time_old = oldDataJobj.getString("last_update_time");   //:"2018-05-02 13:50:58"
	        logger.info("上次状态值 " + stat_old + ", 上次更新时间 " + last_update_time_old + "\n");
  		}
  		else if(optType == 2) {    //删除操作
  			
  		}
	    //排菜使用分析
	    DishUseRtCountModAnl(last_update_time);
  	}
  	
    //解析数据库test_saas中表t_saas_package
    //@param，当前数据对象 curDataJObj，最近一次数据对象 oldDataJobj。操作类型 optType（0表示insert，1表示update，2表示delete）
  	private void test_saas_t_saas_package(JSONObject curDataJObj, JSONObject oldDataJobj, int optType) {
  	    //判断是否是有效操作
  		if(optType == -1)
  			return ;
  		String id = curDataJObj.getString("id");                                              //:"89154b8d-13e6-400c-9a16-4e886ef46511",
        String menu_id = curDataJObj.getString("menu_id");                                    //:"6780045c-2a56-4c50-80ca-7ab6baf48b0820180504",
        String temp_id = curDataJObj.getString("temp_id");                                    //:null,
        String temp_name = curDataJObj.getString("temp_name");                                //:null,
        String menu_group_id = curDataJObj.getString("menu_group_id");                        //:"1c526b81-c731-40c0-92ad-2f3e5b4e3315",
        String menu_group_name = curDataJObj.getString("menu_group_name");                    //:"国内班",
        String cater_type_id = curDataJObj.getString("cater_type_id");                        //:"6f03fb3d-acb9-4a7a-aa44-02c7d377b50d",
        String cater_type_name = curDataJObj.getString("cater_type_name");                    //:"午餐",
        int ledger_type = curDataJObj.getIntValue("ledger_type");                             //:1,
        int meal_type = curDataJObj.getIntValue("meal_type");                                 //:1,
        String cater_package_id = curDataJObj.getString("cater_package_id");                  //:null,
        String package_name = curDataJObj.getString("package_name");                          //:"套餐1",
        String supplier_id = curDataJObj.getString("supplier_id");                            //:"1d864238-44b7-44d9-be92-b2eadbd94400",
        String supply_date = curDataJObj.getString("supply_date");                            //:"2018-05-04 00:00:00",
        String proj_group_id = curDataJObj.getString("proj_group_id");                        //:null,
        String proj_group_name = curDataJObj.getString("proj_group_name");                    //:null,
        String school_supplier_id = curDataJObj.getString("school_supplier_id");              //:"6780045c-2a56-4c50-80ca-7ab6baf48b08",
        String school_id = curDataJObj.getString("school_id");                                //:"7a79b372-9569-4b57-8b88-5b219f6f8a2a",
        String school_name = curDataJObj.getString("school_name");                            //:"上海“儿童世界”基金会长宁幼儿园分园（食堂）",
        int menu_type = curDataJObj.getIntValue("menu_type");                                 //:1,
        double cost = curDataJObj.getDoubleValue("cost");                                     //:null,
        int is_publish = curDataJObj.getIntValue("is_publish");                               //:1,
        String creator = curDataJObj.getString("creator");                                    //:null,
        String create_time = curDataJObj.getString("create_time");                            //:"2018-05-02 13:48:55",
        String updater = curDataJObj.getString("updater");                                    //:null,
        String last_update_time = curDataJObj.getString("last_update_time");                  //:null,
        int stat = curDataJObj.getIntValue("stat");                                           //:1,
        int reserved = curDataJObj.getIntValue("reserved");                                   //:0
        //操作
  		if(optType == 1) {     //更新操作
  		    
  		}
  		else if(optType == 2) {    //删除操作
  			
  		}
  	}
  	
    //解析数据库test_saas中表t_pro_ledger
    //@param，当前数据对象 curDataJObj，最近一次数据对象 oldDataJobj。操作类型 optType（0表示insert，1表示update，2表示delete）
  	private void test_saas_t_pro_ledger(JSONObject curDataJObj, JSONObject oldDataJobj, int optType) {
  	    //判断是否是有效操作
  		if(optType == -1)
  			return ;
  		//当前数据
		String id = curDataJObj.getString("id");                                                   //:"9188da31-7910-4561-a394-5221412f4fe2",
	    String master_id = curDataJObj.getString("master_id");                                     //:"da84124b-20b4-4c96-a78c-6121f7d66faa",
	    String wares_id = curDataJObj.getString("wares_id");                                       //:null,
	    String receiver_id = curDataJObj.getString("receiver_id");                                 //:"8e3da94a-ef40-40f4-977e-1182f3614211",
	    String use_date = curDataJObj.getString("use_date");                                       //:"2018-05-03 00:00:00",
	    String menu_id = curDataJObj.getString("menu_id");                                         //:"e4f14108-1de6-4e2d-933a-0fdff3be300220180503",
	    int delivery_attr = curDataJObj.getIntValue("delivery_attr");                              //:2,
	    int early_days = curDataJObj.getIntValue("early_days");                                    //:0,
	    String material_id = curDataJObj.getString("material_id");                                 //:"532bb4db-315a-11e7-962d-00155d01ef04",
	    String supplier_material_id = curDataJObj.getString("supplier_material_id");               //:"54f60972-30dd-4180-90f6-2b3705554cdf",
	    String supplier_material_name = curDataJObj.getString("supplier_material_name");           //:"爱森大排",
	    double first_num = curDataJObj.getDoubleValue("first_num");                                //:1.00,
	    double second_num = curDataJObj.getDoubleValue("second_num");                              //:1.00,
	    int wares_type = curDataJObj.getIntValue("wares_type");                                    //:48,
	    String wares_type_name = curDataJObj.getString("wares_type_name");                         //:"兽类、禽类和爬行类动物肉产品",
	    String name = curDataJObj.getString("name");                                               //:"猪肉",
	    int shelf_life = curDataJObj.getIntValue("shelf_life");                                    //:null,
	    String unit = curDataJObj.getString("unit");                                               //:null,
	    String supply_id = curDataJObj.getString("supply_id");                                     //:"92f91dbd-bb1f-4373-a6fa-c246a7b39d77",
	    String supply_name = curDataJObj.getString("supply_name");                                 //:"上海爱森猪肉食品有限公司",
	    String supplier_code = curDataJObj.getString("supplier_code");                             //:"1794",
	    String supplier_id = curDataJObj.getString("supplier_id");                                 //:"1d864238-44b7-44d9-be92-b2eadbd94400",
	    String supplier_name = curDataJObj.getString("supplier_name");                             //:"上海港茸餐饮管理有限公司1",
	    double quantity = curDataJObj.getDoubleValue("quantity");                                  //:1.5300,
	    String amount_unit = curDataJObj.getString("amount_unit");                                 //:"公斤",
	    double actual_quantity = curDataJObj.getDoubleValue("actual_quantity");                    //:1.5300,
	    String supplier_material_units_id = curDataJObj.getString("supplier_material_units_id");   //:"fed16815-8971-4609-a0e5-1d0cca029d18",
	    String supplier_material_units = curDataJObj.getString("supplier_material_units");         //:"块",
	    String spce = curDataJObj.getString("spce");                                               //:"0.1",
	    double other_quantity = curDataJObj.getDoubleValue("other_quantity");                      //:2.0000,
	    String production_date = curDataJObj.getString("production_date");                         //:null,
	    String production_name = curDataJObj.getString("production_name");                         //:null,
	    String batch_no = curDataJObj.getString("batch_no");                                       //:null,
	    String trace_code = curDataJObj.getString("trace_code");                                   //:null,
	    int paste_flag = curDataJObj.getIntValue("paste_flag");                                    //:0,
	    String creator = curDataJObj.getString("creator");                                         //:"系统",
	    String create_time = curDataJObj.getString("create_time");                                 //:"2018-05-01 21:09:36",
	    String updater = curDataJObj.getString("updater");                                         //:null,
	    String last_update_time = curDataJObj.getString("last_update_time");                       //:null,
	    int stat = curDataJObj.getIntValue("stat");                                                //:1,
	    String expiration_date = curDataJObj.getString("expiration_date");                         //:null,
	    int warn_flag = curDataJObj.getIntValue("warn_flag");                                      //:1,
	    double delivery_number = curDataJObj.getDoubleValue("delivery_number");                    //:12.0000
	    logger.info("id " + id + ", 主id " + master_id + ", 商品id " + wares_id + ", 接收者id " + receiver_id + ", 使用日期" + use_date + ", 菜单id " + menu_id + ", 快递属性 " + delivery_attr + ", 早起天数 " + early_days + ", 物料id " + material_id + ", 供应商物料id " + supplier_material_id + ", 供应商物料名 " + supplier_material_name + 
	        		", 首次数量 " + first_num + ", 其次数量 " + second_num + ", 商品类型 " + wares_type + ", 商品类型名称 " + wares_type_name + ", 商品名称 " + name + ", 保存周期 " + shelf_life + ", 单元 " + unit + ", 供应id " + supply_id + ", 供应名称 " + supply_name + ", 供应商代码 " + supplier_code + ", 供应商名称 " + supplier_id + 
	        		", 供应商名称 " + supplier_name + ", 数量 " + quantity + ", 单位数量 " + amount_unit + ", 激活数量 " + actual_quantity + ", 供应闪物料单位id " + supplier_material_units_id + ", 供应商物料单位 " + supplier_material_units + ", 空间描述 " + spce + ", 其他数量 " + other_quantity + ", 生产日期 " + production_date + ", 生产名称 " + production_name + ", 批号 " + batch_no + 
	        		", 交易码 " + trace_code + ", 粘性标识 " + paste_flag + ", 创建者 " + creator + ", 创建时间 " + create_time + ", 更新者 " + updater + ", 最后更新时间 " + last_update_time + ", 状态值 " + stat + ", 过期日期 " + expiration_date + ", 警告标识 " + warn_flag + ", 运输数量 " + delivery_number);                           		        
	    //操作
  		if(optType == 1) {     //更新操作
  		    //上次数据
        	Double delivery_number_old = oldDataJobj.getDouble("delivery_number");             //:2.0000
        	if(delivery_number_old != null)
        		logger.info("上次运输数量 " + delivery_number_old.doubleValue() + "\n");
  		}
  		else if(optType == 2) {    //删除操作
  			
  		}
	    //排菜使用分析
	    DishUseRtCountModAnl(last_update_time);    
  	}
  	
  	//解析数据库test_saas中表t_pro_supplier
    //@param，当前数据对象 curDataJObj，最近一次数据对象 oldDataJobj。操作类型 optType（0表示insert，1表示update，2表示delete）
  	private void test_saas_t_pro_supplier(JSONObject curDataJObj, JSONObject oldDataJobj, int optType) {
  	    //判断是否是有效操作
  		if(optType == -1)
  			return ;
  		String id = curDataJObj.getString("id");                                                         //:"a2ebc144-ef9e-4d4e-a99a-32e8b9e375fb",
		String supplier_name = curDataJObj.getString("supplier_name");                                   //:"上海**酱菜调味品厂",
	    int company_type = curDataJObj.getIntValue("company_type");                                      //:null,
	    String scope_id = curDataJObj.getString("scope_id");                                             //:null,
	    String scope_name = curDataJObj.getString("scope_name");                                         //:null,
	    String address = curDataJObj.getString("address");                                               //:"上海市金山区**村高桥2158号",
	    String provinces = curDataJObj.getString("provinces");                                           //:null,
	    String city = curDataJObj.getString("city");                                                     //:null,
	    String area = curDataJObj.getString("area");                                                     //:null,
	    String district_id = curDataJObj.getString("district_id");                                       //:null,
	    int supplier_classify = curDataJObj.getIntValue("supplier_classify");                            //:1,
	    int supplier_type = curDataJObj.getIntValue("supplier_type");                                    //:2,
	    String business_license = curDataJObj.getString("business_license");                             //:"310115000369898",
	    String organization_code = curDataJObj.getString("organization_code");                           //:null,
	    String food_service_code = curDataJObj.getString("food_service_code");                           //:null,
	    String food_service_code_date = curDataJObj.getString("food_service_code_date");                 //:null,
	    String food_business_code = curDataJObj.getString("food_business_code");                         //:null,
	    String food_business_code_date = curDataJObj.getString("food_business_code_date");               //:null,
	    String food_circulation_code = curDataJObj.getString("food_circulation_code");                   //:null,
	    String food_circulation_code_date = curDataJObj.getString("food_circulation_code_date");         //:"2018-01-29",
	    String food_produce_code = curDataJObj.getString("food_produce_code");                           //:"QS311603070286",
	    String food_produce_code_date = curDataJObj.getString("food_produce_code_date");                 //:null,
	    String corporation = curDataJObj.getString("corporation");                                       //:null,
	    String corporation_way = curDataJObj.getString("corporation_way");                               //:null,
	    String contacts = curDataJObj.getString("contacts");                                             //:"朱建平",
	    String contact_way = curDataJObj.getString("contact_way");                                       //:"13111111111",
	    String reg_time = curDataJObj.getString("reg_time");                                             //:null,
	    String reg_address = curDataJObj.getString("reg_address");                                       //:null,
	    String id_card = curDataJObj.getString("id_card");                                               //:null,
	    String id_type = curDataJObj.getString("id_type");                                               //:null,
	    String longitude = curDataJObj.getString("longitude");                                           //:null,
	    String latitude = curDataJObj.getString("latitude");                                             //:null,
	    int reviewed = curDataJObj.getIntValue("reviewed");                                              //:1,
	    String refuse_reason = curDataJObj.getString("refuse_reason");                                   //:null,
	    String qa_person = curDataJObj.getString("qa_person");                                           //:null,
	    String qa_way = curDataJObj.getString("qa_way");                                                 //:null,
	    String reg_capital = curDataJObj.getString("reg_capital");                                       //:null,
	    String annual_sales = curDataJObj.getString("annual_sales");                                     //:null,
	    String company_image = curDataJObj.getString("company_image");                                   //:null,
	    String note = curDataJObj.getString("note");                                                     //:null,
	    String creator = curDataJObj.getString("creator");                                               //:null,
	    String create_time = curDataJObj.getString("create_time");                                       //:"2017-06-15 11:11:52",
	    String updater = curDataJObj.getString("updater");                                               //:"b9e8af47-df4f-4808-b5c4-51cf51fdf12e",
	    String last_update_time = curDataJObj.getString("last_update_time");                             //:"2017-06-15 11:11:52",
	    int stat = curDataJObj.getIntValue("stat");                                                      //:1,
	    String yidian_code = curDataJObj.getString("yidian_code");                                       //:null,
	    int is_new = curDataJObj.getIntValue("is_new");                                                  //:0
	    logger.info("id " + id + ", 供应商名称 " + supplier_name + ", 公司类型 " + company_type + ", 经营范围id " + scope_id + ", 经营范围名称 " + scope_name + ", 地址 " + address + ", 省市 " + provinces + ", 城市 " + city + ", 区 " + area + ", 区域id " + district_id + ", 供应商分类 " + supplier_classify + 
    		        ", 供应商类型 " + supplier_type + ", 商务授权 " + business_license + ", 组织代码 " + organization_code + ", 食品服务代码 " + food_service_code + ", 食品服务代码日期 " + food_service_code_date + ", 食品商务代码 " + food_business_code + ", 食品商务代码日期 " + food_business_code_date + ", 食品回收代码 " + food_circulation_code + ", 食品代码回收日期 " + food_circulation_code_date + ", 食品生产代码 " + food_produce_code + ", 食品生产代码日期 " + food_produce_code_date + 
    		        ", 公司名称 " + corporation + ", 公司性质 " + corporation_way + ", 联系人 " + contacts + ", 联系方式 " + contact_way + ", 注册时间 " + reg_time + ", 注册地址 " + reg_address + ", 身份 " + id_card + ", 身份类型 " + id_type + ", 经度 " + longitude + ", 纬度 " + latitude + ", 检验标识 " + reviewed + 
    		        ", 拒绝原因 " + refuse_reason + ", 质检人员 " + qa_person + ", 质检方式 " + qa_way + ", 注册资本 " + reg_capital + ", 年销售额 " + annual_sales + ", 公司图片 " + company_image + ", 备注 " + note + ", 创始人 " + creator + ", 创建时间 " + create_time + ", 更新者 " + updater + 
    		        ", 最后更新时间 " + last_update_time + ", 状态值 " + stat + ", 一店代码 " + yidian_code + ", 拒绝原因 " + refuse_reason + ", 更新标识 " + is_new);                           		        
	    //操作
  		if(optType == 1) {     //更新操作
  			//上次数据
        	double yidian_code_old = oldDataJobj.getDoubleValue("yidian_code");                //:2.0000
        	logger.info("上次一店代码 " + yidian_code_old + "\n");
  		}
  		else if(optType == 2) {    //删除操作
  			
  		}
	    //供应商供应学校分析
	    SupplierToSchCountModAnl(id, supplier_name, null, 0);
  	}
  	
    //解析数据库test_edu中表t_edu_school
  	//@param，当前数据对象 curDataJObj，最近一次数据对象 oldDataJobj。操作类型 optType（0表示insert，1表示update，2表示delete）
  	private void test_edu_t_edu_school(JSONObject curDataJObj, JSONObject oldDataJobj, int optType) {
  	    //判断是否是有效操作
  		if(optType == -1)
  			return ;
  		String id = curDataJObj.getString("id");                                                 //:"003463bb-93f9-4c33-9154-e1576ab2d8a3",
  		long school_id = curDataJObj.getLongValue("school_id");                                  //:1619,
  		String committee_id = curDataJObj.getString("committee_id");                             //:"e6ee4fa4-2c5b-11e6-b1e8-005056a5ed30",
  		String school_name = curDataJObj.getString("school_name");                               //:"松江区佘山镇中心幼儿园",
  		String school_thum = curDataJObj.getString("school_thum");                               //:null,
  		String corporation = curDataJObj.getString("corporation");                               //:"何梅",
  		String corporation_way = curDataJObj.getString("corporation_way");                       //:"15256953441",
  		String mobile_no = curDataJObj.getString("mobile_no");                                   //:"15256953441",
  		String contacts = curDataJObj.getString("contacts");                                     //:"马静莲",
  		String address = curDataJObj.getString("address");                                       //:"上海市松江区佘山镇松苑路",
  		String longitude = curDataJObj.getString("longitude");                                   //:null,
  		String latitude = curDataJObj.getString("latitude");                                     //:null,
  		String level = curDataJObj.getString("level");                                           //:"0",
  		String supplier_id = curDataJObj.getString("supplier_id");                               //:"fbc041c8-82af-46f6-bdca-8e11401cab29",
  		int reviewed = curDataJObj.getIntValue("reviewed");                                      //:1,
  		int online_payment = curDataJObj.getIntValue("online_payment");                          //:1,
  		String pj_no = curDataJObj.getString("pj_no");                                           //:"0511",
  		int canteen_mode = curDataJObj.getIntValue("canteen_mode");                              //:0,
  		int purchase_mode = curDataJObj.getIntValue("purchase_mode");                            //:0,
  		String school_nature = curDataJObj.getString("school_nature");                           //:"",
  		String ledger_type = curDataJObj.getString("ledger_type");                               //:"",
  		String school_pic = curDataJObj.getString("school_pic");                                 //:null,
  		String school_logo = curDataJObj.getString("school_logo");                               //:null,
  		String provinces = curDataJObj.getString("provinces");                                   //:"",
  		String city = curDataJObj.getString("city");                                             //:null,
  		String area = curDataJObj.getString("area");                                             //:null,
  		String school_num = curDataJObj.getString("school_num");                                  //:null,
  		String email = curDataJObj.getString("email");                                           //:null,
  		String property = curDataJObj.getString("property");                                     //:null,
  		String creator = curDataJObj.getString("creator");                                       //:"abad1335-ad2f-4eb4-ae58-c6aab69ce7bc",
  		String create_time = curDataJObj.getString("create_time");                               //:"2017-06-22 09:26:37",
  		String updater = curDataJObj.getString("updater");                                       //:"abad1335-ad2f-4eb4-ae58-c6aab69ce7bc",
  		String last_update_time = curDataJObj.getString("last_update_time");                     //:"2017-08-23 08:18:46",
  		int stat = curDataJObj.getIntValue("stat");                                              //:1
  		//操作
  		if(optType == 1) {     //更新操作
  		    //上次数据
        	long school_id_old = oldDataJobj.getLongValue("school_id");                              //:9451
        	logger.info("学校id " + school_id_old + "\n");
  		}
  		else if(optType == 2) {    //删除操作
  			
  		}
  	}
  	
    //解析数据库test_saas中表t_pro_warning_global_master
  	private void test_saas_t_pro_warning_global_master(JSONObject curDataJObj, JSONObject oldDataJobj, int optType) {
  	    //判断是否是有效操作
  		if(optType == -1)
  			return ;
  		String id = curDataJObj.getString("id");                                                 //:"00094ce1e07f4cf0a34144e6b45136ad",
  		String title = curDataJObj.getString("title");                                           //:"在2018年03月08日上海市航华中学的团餐配送验收时，配送批次号为406720180308002，存在验收差异请尽快核实。",
  		String level_id = curDataJObj.getString("level_id");                                     //:"528f820a-3b99-4eca-803c-1cd87b2faf79",
  		int warn_stat = curDataJObj.getIntValue("warn_stat");                                    //:4,
  		int warn_type = curDataJObj.getIntValue("warn_type");                                    //:4,
  		int warn_type_child = curDataJObj.getIntValue("warn_type_child");                        //:1,
  		String supplier_id = curDataJObj.getString("supplier_id");                               //:"b0702dd1-4908-4465-b704-3692e5811098",
  		String creator = curDataJObj.getString("creator");                                       //:"admin",
  		String create_time = curDataJObj.getString("create_time");                               //:"2018-03-07 11:07:21",
  		String updater = curDataJObj.getString("updater");                                       //:null,
  		String last_update_time = curDataJObj.getString("last_update_time");                     //:"2018-03-07 12:12:42",
  		int stat = curDataJObj.getIntValue("stat");                                              //:1,
  		String warn_rule_id = curDataJObj.getString("warn_rule_id");                             //:"ea3db1cc-4e3d-4c7a-8574-dd7b22f3d6e0"
  	}
  	
    //解析数据库test_saas中表t_saas_package_dishes
  	private void test_saas_t_saas_package_dishes(JSONObject curDataJObj, JSONObject oldDataJobj, int optType) {
  	    //判断是否是有效操作
  		if(optType == -1)
  			return ;
  		String id = curDataJObj.getString("id");                                                //:"53a87be3-226e-4f71-ba8a-ff5b23a14853",
  		String package_id = curDataJObj.getString("package_id");                                //:"a28ecb7e-9f55-4d3b-971a-1d527864491d",
  		String cater_type_id = curDataJObj.getString("cater_type_id");                          //:"5a42f3f3-48d7-4404-a5af-e4305de545c8",
  		String cater_type_name = curDataJObj.getString("cater_type_name");                      //:"午点",
  		String category = curDataJObj.getString("category");                                    //:"大荤",
  		String dishes_id = curDataJObj.getString("dishes_id");                                  //:"1c78f5e8-e4b5-4cfd-830f-2a063fc4b41f",
  		String dishes_name = curDataJObj.getString("dishes_name");                              //:"胡萝卜黑木耳炒蛋",
  		double cost = curDataJObj.getDoubleValue("cost");                                       //:null,
  		int dishes_number = curDataJObj.getIntValue("dishes_number");                           //:107,
  		int food_quantity = curDataJObj.getIntValue("food_quantity");                           //:100,
  		String supplier_id = curDataJObj.getString("supplier_id");                              //:"23c590d0-9929-4b9e-b1cb-1c48e9b063ff",
  		String dishes_image = curDataJObj.getString("dishes_image");                            //:null,
  		String creator = curDataJObj.getString("creator");                                      //:null,
  		String create_time = curDataJObj.getString("create_time");                              //:"2018-05-07 15:26:44",
  		String updater = curDataJObj.getString("updater");                                      //:null,
  		String last_update_time = curDataJObj.getString("last_update_time");                    //:null,
  		int stat = curDataJObj.getIntValue("stat");                                             //:1
  	}
  	
  	//解析数据库test_saas中表t_saas_package_dishes_ware
  	private void test_saas_t_saas_package_dishes_ware(JSONObject curDataJObj, JSONObject oldDataJobj, int optType) {
  	    //判断是否是有效操作
  		if(optType == -1)
  			return ;
  		String id = curDataJObj.getString("id");                                                //:"ff2b42c0-51c7-11e8-9c32-00155d01ef04",
  		String package_dishes_id = curDataJObj.getString("package_dishes_id");                  //:"53a87be3-226e-4f71-ba8a-ff5b23a14853",
  		String material_id = curDataJObj.getString("material_id");                              //:"532c0962-315a-11e7-962d-00155d01ef04",
  		String material_name = curDataJObj.getString("material_name");                          //:"干黑木耳",
  		int material_type = curDataJObj.getIntValue("material_type");                           //:1,
  		String supplier_material_id = curDataJObj.getString("supplier_material_id");            //:"208fe9ac-92e5-45d2-9862-b84018200713",
  		String supplier_material_name = curDataJObj.getString("supplier_material_name");        //:"黑木耳",
  		String supplier_id = curDataJObj.getString("supplier_id");                              //:null,
  		double ware_weight = curDataJObj.getDoubleValue("ware_weight");                         //:0.50,
  		String creator = curDataJObj.getString("creator");                                      //:"系统",
  		String create_time = curDataJObj.getString("create_time");                              //:"2018-05-07 15:26:45",
  		String updater = curDataJObj.getString("updater");                                      //:null,
  		String last_update_time = curDataJObj.getString("last_update_time");                    //:null,
  		int stat = curDataJObj.getIntValue("stat");        
  	    //原料使用排名分析
	    RawMatUseRankModAnl(material_name);
  	}
  	
  	//写二进制文件
    private void WriteBinaryFile(byte[] FileCont, String strFileInfo, String strFileName)
    {
    	String fileName = strFileName, strNmJson = "";  
        try  
        {  
        	if(FileCont != null) {
        		System.out.println("日志文件长度: " + FileCont.length + " 字节");
        		//保存日志文件
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));  
                out.write(FileCont);
                out.close();
        	}
            //保存日志文件信息
            int idx = fileName.lastIndexOf(".");
            String strLogFileInfoName = "";
            if(idx != -1)
            	strLogFileInfoName = fileName.substring(0, idx) + ".json";
            else
            	strLogFileInfoName = fileName + ".json";
            DataOutputStream outfileinfo = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(strLogFileInfoName)));  
            strNmJson = JsonFormatTool.formatJson(strFileInfo);
            outfileinfo.write(strNmJson.getBytes());
            outfileinfo.close();
        } 
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
    }
    
    //读取文本文件
    private String ReadTxtFile(String strFileName)
    {
    	File file = new File(strFileName);
        StringBuilder result = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine()) != null)
            {
            	//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result.toString();
    }
    
    //排菜使用分析
    private void DishUseRtCountModAnl(String last_update_time)
    {
    	if(last_update_time != null) {
	    	String [] strUpdateTimes = last_update_time.split(" ");
		    logger.info("更新日期：" + strUpdateTimes[0] + "，更新时间：" + strUpdateTimes[1]);
		    //durcMod.AnlProc(strUpdateTimes[1]);
	    }
    }
    
    //原料使用排名分析
    private void RawMatUseRankModAnl(String material_name) {
    	if(material_name != null) {
    		//rmurMod.AnlProc(material_name);
    	}
    }
    
    //供应商供应学校分析
    private void SupplierToSchCountModAnl(String supId, String supName, String schName, int optType) {
    	if(optType == 0) {        //供应商id和供应商名加载操作
    		//if(supId != null && supName != null)
    		//	stscMod.loadSup(supId, supName);;
    	}
    	else if(optType == 1) {  //供应商id供应学校分析
    		//if(supId != null && schName != null)
    		//	stscMod.AnlProc(supId, schName);
    	}
    }
}
