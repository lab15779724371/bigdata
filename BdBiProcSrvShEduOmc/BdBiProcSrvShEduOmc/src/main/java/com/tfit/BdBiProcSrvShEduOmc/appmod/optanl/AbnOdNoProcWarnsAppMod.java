package com.tfit.BdBiProcSrvShEduOmc.appmod.optanl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.optanl.AbnOdNoProcWarnsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

/**
 * 3.2.4.	逾期未处理预警列表应用模型
 * @author Administrator
 *
 */
public class AbnOdNoProcWarnsAppMod {
	private static final Logger logger = LogManager.getLogger(AbnOdNoProcWarnsAppMod.class.getName());
	
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
			"   \"abnOdNoProcWarns\": [\r\n" + 
			"{\r\n" + 
			"  \"sn\":1,\r\n" + 
			"  \"warnDate\":\"2018/09/03\",\r\n" + 
			"   \"distName\":\"徐汇区\",\r\n" + 
			"   \"schName\":\"上海市徐汇区徐浦小学\",\r\n" + 
			"   \"trigWarnUnit\":\"上海市徐汇区徐浦小学\",\r\n" + 
			"   \"licName\":\"食品经营许可证\",\r\n" + 
			"   \"licNo\":\"JY23101140041987\",\r\n" + 
			"   \"validDate\":\"2018-12-23\",\r\n" + 
			"   \"licStatus\":\"逾期\",\r\n" + 
			"   \"licAuditStatus\":\"未处理\"\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"sn\":2,\r\n" + 
			"  \"warnDate\":\"2018/09/03\",\r\n" + 
			"   \"distName\":\"徐汇区\",\r\n" + 
			"   \"schName\":\"上海市徐汇区东兰幼儿园\",\r\n" + 
			"   \"trigWarnUnit\":\"上海市徐汇区东兰幼儿园\",\r\n" + 
			"   \"licName\":\"食品经营许可证\",\r\n" + 
			"   \"licNo\":\"JY13101050042467\",\r\n" + 
			"   \"validDate\":\"2018-06-03\",\r\n" + 
			"   \"licStatus\":\"逾期\",\r\n" + 
			"   \"licAuditStatus\":\"未处理\"\r\n" + 
			" },\r\n" + 
			"{\r\n" + 
			"  \"sn\":3,\r\n" + 
			"  \"warnDate\":\"2018/09/03\",\r\n" + 
			"   \"distName\":\"徐汇区\",\r\n" + 
			"   \"schName\":\"上海市宛平中学\",\r\n" + 
			"   \"trigWarnUnit\":\"上海市宛平中学\",\r\n" + 
			"   \"licName\":\"食品经营许可证\",\r\n" + 
			"   \"licNo\":\"JY13101170104193\",\r\n" + 
			"   \"validDate\":\"2018-05-29\",\r\n" + 
			"   \"licStatus\":\"逾期\",\r\n" + 
			"   \"licAuditStatus\":\"未处理\"\r\n" + 
			" } ],\r\n" + 
			"   \"msgId\":1\r\n" + 
			"}\r\n" + 
			"";
	
	/**
	 * 模拟数据函数
	 * @return
	 */
	private AbnOdNoProcWarnsDTO abnOdNoProcWarnsFunc() {
		AbnOdNoProcWarnsDTO abnOdNoProcWarnsDTO = new AbnOdNoProcWarnsDTO();
		
		JSONObject jsStr = JSONObject.parseObject(tempData);
		/** 
		* json对象转换成java对象 
		*/ 
		abnOdNoProcWarnsDTO = (AbnOdNoProcWarnsDTO) JSONObject.toJavaObject(jsStr,AbnOdNoProcWarnsDTO.class);
		if(abnOdNoProcWarnsDTO==null ) {
			abnOdNoProcWarnsDTO = new AbnOdNoProcWarnsDTO();
		}
		
		
		//时戳
		abnOdNoProcWarnsDTO.setTime(BCDTimeUtil.convertNormalFrom(null));
		//消息ID
		abnOdNoProcWarnsDTO.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return abnOdNoProcWarnsDTO;
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
	public AbnOdNoProcWarnsDTO appModFunc(String distName, String prefCity, String province,String startDate, String endDate,Db1Service db1Service) {
		AbnOdNoProcWarnsDTO abnOdNoProcWarnsDTO = null;
		//真实数据
		if(isRealData) {       
			
		}
		else {    //模拟数据
			//模拟数据函数
			abnOdNoProcWarnsDTO = abnOdNoProcWarnsFunc();
		}		

		return abnOdNoProcWarnsDTO;
	}
}
