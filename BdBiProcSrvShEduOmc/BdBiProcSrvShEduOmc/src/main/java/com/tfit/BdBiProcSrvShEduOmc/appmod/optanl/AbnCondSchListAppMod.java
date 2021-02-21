package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.AbnCondSchListDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

/**
 * 3.2.3.	异常情况学校列表应用模型
 * @author Administrator
 *
 */
public class AbnCondSchListAppMod {
	private static final Logger logger = LogManager.getLogger(AbnCondSchListAppMod.class.getName());
	
	/**
	 * 是否为真实数据标识
	 */
	private static boolean isRealData = false;
	/**
	 * 页号、页大小和总页数
	 */
	int curPageNum = 1;
	int pageTotal = 1;
	int pageSize = 20;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 0;
	
	/**
	 * 数组数据初始化
	 */
	String tempData ="{\r\n" + 
			"   \"time\": \"2016-07-14 09:51:35\",\r\n" + 
			"   \"pageInfo\":\r\n" + 
			"   {\r\n" + 
			"     \"pageTotal\":3,\r\n" + 
			"     \"curPageNum\":1\r\n" + 
			"   },\r\n" + 
			"   \"abnCondSchList\": [\r\n" + 
			"{\r\n" + 
			"  \"sn\":1,\r\n" + 
			"  \"schName\":\"上海市徐汇区向阳小学\",\r\n" + 
			"  \"subLevel\":\"区属\",\r\n" + 
			"  \"compDep\":\"徐汇区教育局\",\r\n" + 
			"  \"subDistName\":\"-\",\r\n" + 
			"  \"fblMb\":\"学校\",\r\n" + 
			"  \"distName\":\"徐汇区\",\r\n" + 
			"  \"schType\":\"小学\",\r\n" + 
			"  \"schProp\":\"公办\",\r\n" + 
			"  \"mealFlag\":\"1\",\r\n" + 
			"  \"optMode\":\"外包-现场加工\",\r\n" + 
			"  \"rmcName\":\"上海绿捷实业发展有限公司\",\r\n" + 
			"   \"recentStatus\":\"连续3天未排菜\"\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"sn\":2,\r\n" + 
			"  \"schName\":\"上海市徐汇区向阳小学（分校区）\",\r\n" + 
			"  \"subLevel\":\"区属\",\r\n" + 
			"  \"compDep\":\"徐汇区教育局\",\r\n" + 
			"  \"subDistName\":\"-\",\r\n" + 
			"  \"fblMb\":\"学校\",\r\n" + 
			"  \"distName\":\"徐汇区\",\r\n" + 
			"  \"schType\":\"小学\",\r\n" + 
			"  \"schProp\":\"公办\",\r\n" + 
			"  \"mealFlag\":\"1\",\r\n" + 
			"  \"optMode\":\"外包-现场加工\",\r\n" + 
			"  \"rmcName\":\"上海绿捷实业发展有限公司\",\r\n" + 
			"   \"recentStatus\":\"连续5天未排菜\"\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"sn\":3,\r\n" + 
			"  \"schName\":\"上海市徐汇区世界小学\",\r\n" + 
			"  \"subLevel\":\"区属\",\r\n" + 
			"  \"compDep\":\"徐汇区教育局\",\r\n" + 
			"  \"subDistName\":\"-\",\r\n" + 
			"  \"fblMb\":\"学校\",\r\n" + 
			"  \"distName\":\"徐汇区\",\r\n" + 
			"  \"schType\":\"小学\",\r\n" + 
			"  \"schProp\":\"民办\",\r\n" + 
			"  \"mealFlag\":\"1\",\r\n" + 
			"  \"optMode\":\"外包-现场加工\",\r\n" + 
			"  \"rmcName\":\"上海龙神餐饮有限公司\",\r\n" + 
			"   \"recentStatus\":\"连续3天未排菜\"\r\n" + 
			" } ],\r\n" + 
			"   \"msgId\":1\r\n" + 
			"}\r\n" + 
			"";
	
	/**
	 * 模拟数据函数
	 * @return
	 */
	private AbnCondSchListDTO abnCondSchListFunc() {
		AbnCondSchListDTO abnCondSchListDTO = new AbnCondSchListDTO();
		
		JSONObject jsStr = JSONObject.parseObject(tempData);
		/** 
		* json对象转换成java对象 
		*/ 
		abnCondSchListDTO = (AbnCondSchListDTO) JSONObject.toJavaObject(jsStr,AbnCondSchListDTO.class);
		if(abnCondSchListDTO==null ) {
			abnCondSchListDTO = new AbnCondSchListDTO();
		}
		
		
		//时戳
		abnCondSchListDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		abnCondSchListDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return abnCondSchListDTO;
	}
	
	/**
	 * 投诉举报详情列表模型函数
	 * @param subDate
	 * @param distName
	 * @param prefCity
	 * @param province
	 * @param schName
	 * @param contractor
	 * @param procStatus
	 * @param db1Service
	 * @return
	 */
	public AbnCondSchListDTO appModFunc(String distName, String prefCity, String province,String startDate, String endDate,Db1Service db1Service) {
		AbnCondSchListDTO abnCondSchListDTO = null;
		//真实数据
		if(isRealData) {       
			
		}
		else {    //模拟数据
			//模拟数据函数
			abnCondSchListDTO = abnCondSchListFunc();
		}		

		return abnCondSchListDTO;
	}
}
