package com.tfit.BdBiProcSrvShEduOmc.appmod.iw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;

import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.PageInfo;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnCommonLicDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnMaterialOverProDets;
import com.tfit.BdBiProcSrvShEduOmc.dto.iw.WarnMaterialOverProDetsDTO;
import com.tfit.BdBiProcSrvShEduOmc.service.Db1Service;
import com.tfit.BdBiProcSrvShEduOmc.service.Db2Service;
import com.tfit.BdBiProcSrvShEduOmc.service.DbHiveWarnService;
import com.tfit.BdBiProcSrvShEduOmc.service.RedisService;
import com.tfit.BdBiProcSrvShEduOmc.service.SaasService;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;
import com.tfit.BdBiProcSrvShEduOmc.util.CommonUtil;

//证照预警学校证件详情列表
public class WarnMaterialOverProDetsAppMod {
	private static final Logger logger = LogManager.getLogger(WarnMaterialOverProDetsAppMod.class.getName());
	
	//Redis服务
	@Autowired
	RedisService redisService = new RedisService();
	
	//是否为真实数据标识
	private static boolean isRealData = true;
	//页号、页大小和总页数
	int curPageNum = 1, pageTotal = 1, pageSize = 20;
	
	/**
	 * 方法类型索引
	 */
	int methodIndex = 1;
	
	//数组数据初始化
	String[] warnDate_Array = {"2018/09/03", "2018/09/03"};
	String[] distName_Array = {"11", "11"};
	String[] schType_Array = {"小学", "幼儿园"};
	String[] schProp_Array = {"公办", "公办"};
	String[] schName_Array = {"上海市徐汇区徐浦小学", "上海市徐汇区东兰幼儿园"};
	String[] licName_Array = {"食品经营许可证", "食品经营许可证"};
	String[] licNo_Array = {"JY23101140041987", "JY13101050042467"};
	String[] validDate_Array = {"2018-12-23", "2018-06-03"};
	String[] licStatus_Array = {"剩余 1 天", "逾期"};
	int[] licAuditStatus_Array = {2, 2};
	String[] elimDate_Array = {"2018/09/03", "2018/09/03"};
	
	//模拟数据函数
	private WarnMaterialOverProDetsDTO SimuDataFunc() {
		WarnMaterialOverProDetsDTO wsldDto = new WarnMaterialOverProDetsDTO();
		//时戳
		wsldDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		//证照预警学校证件详情列表模拟数据
		List<WarnMaterialOverProDets> warnMaterialOverProDets = new ArrayList<>();
		//赋值
		for (int i = 0; i < warnDate_Array.length; i++) {
			WarnMaterialOverProDets wsld = new WarnMaterialOverProDets();
			wsld.setWarnDate(warnDate_Array[i]);
			wsld.setDistName(distName_Array[i]);
			wsld.setSchName(schName_Array[i]);
			wsld.setElimDate(elimDate_Array[i]);
			warnMaterialOverProDets.add(wsld);
		}
		//设置数据
		wsldDto.setWarnMaterialOverProDets(warnMaterialOverProDets);
		//分页
		PageInfo pageInfo = new PageInfo();
		pageTotal = distName_Array.length;
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		wsldDto.setPageInfo(pageInfo);
		//消息ID
		wsldDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return wsldDto;
	}
	
	// 证照预警学校证件详情列表函数
	WarnMaterialOverProDetsDTO warnMaterialOverProDets(String departmentId,String distIdorSCName, String startDate, String endDate,  
			DbHiveWarnService dbHiveWarnService,String departmentIds, String schName, String status, String trigWarnUnit,
			Integer target) {
		WarnMaterialOverProDetsDTO wsldDto = new WarnMaterialOverProDetsDTO();
		List<WarnMaterialOverProDets> warnMaterialOverProDets = new ArrayList<>();

		if(startDate==null || startDate.split("-").length < 2) {
    		startDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	if((endDate==null || endDate.split("-").length < 2)&& startDate!=null) {
    		endDate = startDate;
    	}else if (endDate==null || endDate.split("-").length < 2) {
    		endDate = BCDTimeUtil.convertNormalDate(null);
    	}
    	
    	String [] yearMonths = new String [4];
    	//根据开始日期、结束日期，获取开始日期和结束日期的年、月
    	yearMonths = CommonUtil.getYearMonthByDate(startDate, endDate);
    	String startYear = yearMonths[0];
    	String startMonth = yearMonths[1];
    	String endYear = yearMonths[2];
    	String endMonth = yearMonths[3];
    	
		//结束日期+1天，方便查询处理
		String endDateAddOne = CommonUtil.dateAddDay(endDate, 1);
		//获取开始日期、结束日期的年月集合
		List<String> listYearMonth = CommonUtil.getYearMonthList(startYear, startMonth, endYear, endMonth);
		Map<Integer, String> schoolPropertyMap = new HashMap<Integer,String>();
		schoolPropertyMap.put(0, "公办");
		schoolPropertyMap.put(2, "民办");
		schoolPropertyMap.put(3, "外籍人员子女学校");
		schoolPropertyMap.put(4, "其他");
		
		List<Object> departmentIdsList=CommonUtil.changeStringToList(departmentIds);
		
		//分页总数
		try {
			int iStatus= -1;
			if(CommonUtil.isNotEmpty(status)) {
				iStatus = Integer.valueOf(status);
			}
			int pageStart = (curPageNum-1)*pageSize;
			int pageEnd = curPageNum*pageSize;
			if(curPageNum==-1 || pageSize==-1) {
				pageStart=-1;
				pageEnd=-1;
			}
			//获取列表  
			List<WarnCommonLicDets> warnCommonLicDets = new ArrayList<>();
			warnCommonLicDets = dbHiveWarnService.getWarnLicDetsList(3,null,listYearMonth, startDate,
					endDateAddOne, null, schName, -1, -1, -1, 
					iStatus,null, null, null, null, 
					null, -1, null, trigWarnUnit,null,target,
					departmentId, departmentIdsList,
					null,distIdorSCName,null,null,
					pageStart, pageEnd, schoolPropertyMap);
			warnCommonLicDets.removeAll(Collections.singleton(null));
			
			//转换业务对应实体
			for(WarnCommonLicDets warnCommon :warnCommonLicDets) {
				WarnMaterialOverProDets warn = new WarnMaterialOverProDets();
				BeanUtils.copyProperties(warn, warnCommon);
				if(warnCommon.getLicAuditStatus()==2) {
					//审核状态，0:未处理，1:审核中，2:已消除，3:已驳回
					warn.setStatus("1");
				}else {
					warn.setStatus("0");
				}
				
				warnMaterialOverProDets.add(warn);
			}
			
			Integer pageTotalTemp = dbHiveWarnService.getWarnLicDetsCount(3,null,listYearMonth, startDate, 
					endDateAddOne, null, schName, -1, -1, -1, 
					iStatus,null, null, null, null, 
					null, -1, null,trigWarnUnit,null,target,departmentId, departmentIdsList,null,distIdorSCName,null,null);
			logger.info("行数01********************************"+pageTotalTemp);
			if(pageTotalTemp!=null) {
				pageTotal = pageTotalTemp;
			}
		}catch(Exception e) {
			pageTotal = 1;
			logger.info("行数catch********************************"+e.getMessage());
		}
		
		//时戳
		wsldDto.setTime(BCDTimeUtil.convertNormalFrom(null));
		// 分页
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageTotal(pageTotal);
		pageInfo.setCurPageNum(curPageNum);
		wsldDto.setPageInfo(pageInfo);
		//设置数据
		wsldDto.setWarnMaterialOverProDets(warnMaterialOverProDets);
		//消息ID
		wsldDto.setMsgId(AppModConfig.msgId);
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		
		return wsldDto;
	}	
	
	// 证照预警学校证件详情列表模型函数
	public WarnMaterialOverProDetsDTO appModFunc(String token, String startWarnDate, String endWarnDate, String distName,
			String prefCity, String province,String departmentId,String departmentIds,String schName, String status, 
			String trigWarnUnit,
			String page, String pageSize, 
			Db1Service db1Service, Db2Service db2Service, SaasService saasService,
			DbHiveWarnService dbHiveWarnService) {
		WarnMaterialOverProDetsDTO wsldDto = null;
		if(page != null)
			curPageNum = Integer.parseInt(page);
		if(pageSize != null)
			this.pageSize = Integer.parseInt(pageSize);
		if(isRealData) {       //真实数据
			// 日期
			String[] dates = null;
			if (startWarnDate == null || endWarnDate == null) { // 按照当天日期获取数据
				dates = new String[1];
				dates[0] = BCDTimeUtil.convertNormalDate(null);
			} else { // 按照开始日期和结束日期获取数据
				DateTime startDt = BCDTimeUtil.convertDateStrToDate(startWarnDate);
				DateTime endDt = BCDTimeUtil.convertDateStrToDate(endWarnDate);
				int days = Days.daysBetween(startDt, endDt).getDays() + 1;
				dates = new String[days];
				for (int i = 0; i < days; i++) {
					dates[i] = endDt.minusDays(i).toString("yyyy-MM-dd");
					logger.info("dates[" + i + "] = " + dates[i]);
				}
			}
			// 省或直辖市
			if(province == null)
				province = "上海市";  				
			// 参数查找标识
			// 按不同参数形式处理
			if (distName != null && prefCity == null && province != null) {    // 按区域，省或直辖市处理
				// 存在则获取数据
				if(departmentId == null)
					departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
				// 证照预警学校证件详情列表函数
				if(methodIndex == 1 ) {
					Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
					wsldDto = warnMaterialOverProDets(departmentId,distName, startWarnDate,endWarnDate, 
							dbHiveWarnService,departmentIds, schName, status,trigWarnUnit,target);
				}
			} else if (distName == null && prefCity == null && province != null) { // 按省或直辖市处理
				if(departmentId == null)
					departmentId = AppModConfig.getUserDataPermDistId(token, db1Service, db2Service);  //获取用户权限区域ID
				// 证照预警学校证件详情列表函数
				if(methodIndex == 1 ) {
					Integer target = CommonUtil.getTarget(token, db1Service, db2Service);
					wsldDto = warnMaterialOverProDets(departmentId,distName, startWarnDate,endWarnDate, 
							dbHiveWarnService,departmentIds, schName, status,trigWarnUnit,target);
				}
			} else if (distName != null && prefCity != null && province != null) { // 按区域，地级市，省或直辖市处理

			} else if (distName == null && prefCity != null && province != null) { // 地级市，省或直辖市处理

			} else {
				logger.info("访问接口参数非法！");
			}																
		}
		else {    //模拟数据
			//模拟数据函数
			wsldDto = SimuDataFunc();
		}		

		return wsldDto;
	}
}
